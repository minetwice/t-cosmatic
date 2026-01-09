package com.tcosmetic.mixin;

import com.tcosmetic.client.CapeManager;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void addCustomCape(EntityRendererFactory.Context ctx, boolean slim, CallbackInfo ci) {
        PlayerEntityRenderer renderer = (PlayerEntityRenderer) (Object) this;
        renderer.addFeature(new CustomCapeFeatureRenderer(renderer));
    }

    static class CustomCapeFeatureRenderer extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {
        public CustomCapeFeatureRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context) {
            super(context);
        }

        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, PlayerEntityRenderState state, float limbAngle, float limbDistance) {
            if (state.invisible) return;
            
            Identifier capeTexture = CapeManager.getPlayerCape(state.uuid);
            if (capeTexture == null) return;

            matrices.push();
            matrices.translate(0.0F, 0.0F, 0.125F);
            
            // Simplified cape physics for the custom renderer
            float n = state.capePitch;
            float o = state.prevCapePitch + (state.capePitch - state.prevCapePitch) * 1.0f; // Simplified
            
            matrices.multiply(RotationAxis.POSITIVE_X.getDegreesQuaternion(6.0F + o / 2.0F + n));
            matrices.multiply(RotationAxis.POSITIVE_Z.getDegreesQuaternion(state.prevCapeYaw / 2.0F));
            matrices.multiply(RotationAxis.POSITIVE_Y.getDegreesQuaternion(180.0F - state.prevCapeYaw / 2.0F));
            
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(capeTexture));
            this.getContextModel().renderCape(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
            matrices.pop();
        }
    }
}
