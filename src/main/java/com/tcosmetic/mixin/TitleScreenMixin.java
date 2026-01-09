package com.tcosmetic.mixin;

import com.tcosmetic.client.gui.CosmeticScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {

    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(at = @At("RETURN"), method = "init")
    private void init(CallbackInfo ci) {
        int l = this.height / 4 + 48;
        
        // Add TCosmetic Button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("TCosmetic"), (button) -> {
            this.client.setScreen(new CosmeticScreen(this));
        }).dimensions(this.width / 2 + 104, l + 72 + 12, 98, 20).build());
    }
}
