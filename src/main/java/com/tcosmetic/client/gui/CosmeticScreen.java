package com.tcosmetic.client.gui;

import com.tcosmetic.client.CapeManager;
import com.tcosmetic.network.CapeSyncPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class CosmeticScreen extends Screen {
    private final List<String> capes = new ArrayList<>();
    private int currentIndex = 0;

    public CosmeticScreen() {
        super(Text.literal("Cosmetic Menu"));
        // Example capes
        capes.add("none");
        capes.add("migrator");
        capes.add("minecon2011");
        capes.add("minecon2012");
        capes.add("minecon2013");
        capes.add("minecon2015");
        capes.add("minecon2016");
    }

    @Override
    protected void init() {
        int buttonWidth = 100;
        int x = this.width / 2 - buttonWidth / 2;
        int y = this.height / 2;

        this.addDrawableChild(ButtonWidget.builder(Text.literal("<"), button -> {
            currentIndex = (currentIndex - 1 + capes.size()) % capes.size();
        }).dimensions(x - 30, y, 20, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal(">"), button -> {
            currentIndex = (currentIndex + 1) % capes.size();
        }).dimensions(x + buttonWidth + 10, y, 20, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Equip"), button -> {
            String selected = capes.get(currentIndex);
            ClientPlayNetworking.send(new CapeSyncPayload(this.client.player.getUuid(), selected));
            CapeManager.setPlayerCape(this.client.player.getUuid(), selected);
        }).dimensions(x, y + 30, buttonWidth, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        
        String currentCape = capes.get(currentIndex);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Selected: " + currentCape), this.width / 2, 40, 0xAAAAAA);

        if (this.client.player != null) {
            int dollX = this.width / 2 - 50;
            int dollY = this.height / 2 - 10;
            InventoryScreen.drawEntity(context, (float)dollX, (float)dollY, 30f, new Vector3f(), new Quaternionf().rotateY((float) Math.PI), null, this.client.player);
        }

        if (!currentCape.equals("none")) {
            Identifier capeId = Identifier.of("tcosmetic", "textures/capes/" + currentCape + ".png");
            context.drawTexture(RenderLayer::getGuiTextured, capeId, this.width / 2 + 10, 50, 0, 0, 64, 32, 64, 32);
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
