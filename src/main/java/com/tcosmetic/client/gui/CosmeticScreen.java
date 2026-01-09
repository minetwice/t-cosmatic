package com.tcosmetic.client.gui;

import com.tcosmetic.client.CapeManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public class CosmeticScreen extends Screen {
    private final Screen parent;
    private int currentIndex = 0;
    private final List<String> capes = List.of("cape1", "cape2", "cape3");

    public CosmeticScreen(Screen parent) {
        super(Text.literal("Cosmetic Menu"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.addDrawableChild(ButtonWidget.builder(Text.literal("<"), button -> {
            currentIndex = (currentIndex - 1 + capes.size()) % capes.size();
        }).dimensions(this.width / 2 - 100, 150, 20, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal(">"), button -> {
            currentIndex = (currentIndex + 1) % capes.size();
        }).dimensions(this.width / 2 + 80, 150, 20, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Equip"), button -> {
            CapeManager.setLocalCape(capes.get(currentIndex));
        }).dimensions(this.width / 2 - 40, 180, 80, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Back"), button -> {
            this.client.setScreen(parent);
        }).dimensions(this.width / 2 - 40, 210, 80, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);

        String currentCape = capes.get(currentIndex);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Selected: " + currentCape), this.width / 2, 130, 0xFFFFFF);

        // Draw player preview
        if (this.client != null && this.client.player != null) {
            int dollX = this.width / 2 - 50;
            int dollY = 100;
            
            // Fixed drawEntity call for 1.21
            InventoryScreen.drawEntity(context, (float)dollX, (float)dollY, 30f, new Vector3f(), new Quaternionf().rotationY((float) Math.toRadians(180)), null, this.client.player);
        }

        // Draw cape preview
        Identifier capeId = Identifier.of("tcosmetic", "textures/capes/" + currentCape + ".png");
        context.drawTexture(capeId, this.width / 2 + 10, 50, 0, 0, 64, 32, 64, 32);
    }

    @Override
    public void close() {
        this.client.setScreen(parent);
    }
}
