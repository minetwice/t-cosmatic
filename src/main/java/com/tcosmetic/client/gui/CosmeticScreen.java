package com.tcosmetic.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tcosmetic.client.CapeManager;
import com.tcosmetic.network.CapeSyncPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CosmeticScreen extends Screen {
    private final Screen parent;
    private String tempSelectedCape = CapeManager.selectedCape;

    public CosmeticScreen(Screen parent) {
        super(Text.literal("TCosmetic"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int listWidth = 100;
        int itemHeight = 20;
        int yStart = 40;

        Map<String, Identifier> capes = CapeManager.getLoadedCapes();
        List<String> capeNames = new ArrayList<>(capes.keySet());

        // Cape List
        for (int i = 0; i < capeNames.size(); i++) {
            String name = capeNames.get(i);
            int yPos = yStart + (i * itemHeight);
            
            this.addDrawableChild(ButtonWidget.builder(Text.literal(name), (button) -> {
                this.tempSelectedCape = name;
            }).dimensions(10, yPos, listWidth, 20).build());
        }

        // Save and Quit
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Save & Quit"), (button) -> {
            CapeManager.selectedCape = this.tempSelectedCape;
            // Send packet to server if connected
            if (this.client.player != null) {
                 ClientPlayNetworking.send(new CapeSyncPayload(this.client.player.getUuid(), this.tempSelectedCape));
            }
            this.client.setScreen(this.parent);
        }).dimensions(this.width / 2 - 50, this.height - 30, 100, 20).build());
        
        // Refresh Capes Button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Reload Capes"), (button) -> {
            CapeManager.reloadCapes();
            this.client.setScreen(new CosmeticScreen(this.parent)); // Re-init screen
        }).dimensions(10, 10, 100, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);

        // Draw Player Preview (Doll)
        int dollX = this.width / 2 + 50;
        int dollY = this.height / 2 + 50;
        
        if (this.client.player != null) {
            // Use InventoryScreen's static method if available, or just render nothing if null
            // In 1.21, InventoryScreen.drawEntity exists.
            InventoryScreen.drawEntity(context, dollX, dollY, 30, (float)(dollX) - mouseX, (float)(dollY - 50) - mouseY, this.client.player);
        }
        
        // Draw Cape Preview Icon if selected
        if (tempSelectedCape != null) {
            Identifier capeId = CapeManager.getLoadedCapes().get(tempSelectedCape);
            if (capeId != null) {
                // Draw texture 64x32
                context.drawTexture(capeId, this.width / 2 + 10, 50, 0, 0, 64, 32, 64, 32);
                context.drawText(this.textRenderer, "Selected: " + tempSelectedCape, this.width / 2 + 10, 90, 0xFFFFFF, true);
            }
        }
    }
}
