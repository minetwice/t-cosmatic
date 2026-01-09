package com.tcosmetic.mixin;

import com.tcosmetic.client.CapeManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {
    
    // We can't easily inject a FeatureRenderer via Mixin without an Accessor or complex logic.
    // However, we can inject into the render method of the PlayerEntityRenderer or add a layer in the constructor.
    // A simpler way for a standalone mod is to Inject into the constructor to add a custom Layer.
    
    @Inject(method = "<init>", at = @At("RETURN"))
    private void addCapeLayer(net.minecraft.client.render.entity.EntityRendererFactory.Context ctx, boolean slim, CallbackInfo ci) {
        PlayerEntityRenderer renderer = (PlayerEntityRenderer) (Object) this;
        renderer.addFeature(new CustomCapeFeatureRenderer(renderer));
    }
    
    // Inner class for the Cape Renderer
    static class CustomCapeFeatureRenderer extends FeatureRenderer<net.minecraft.client.network.AbstractClientPlayerEntity, PlayerEntityModel<net.minecraft.client.network.AbstractClientPlayerEntity>> {
        
        public CustomCapeFeatureRenderer(FeatureRendererContext<net.minecraft.client.network.AbstractClientPlayerEntity, PlayerEntityModel<net.minecraft.client.network.AbstractClientPlayerEntity>> context) {
            super(context);
        }

        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, net.minecraft.client.network.AbstractClientPlayerEntity player, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
            if (player.isInvisible() || !player.isPartVisible(net.minecraft.entity.player.PlayerModelPart.CAPE)) {
                return;
            }

            Identifier capeTexture = CapeManager.getPlayerCape(player.getUuid());
            if (capeTexture == null) return;

            matrices.push();
            matrices.translate(0.0F, 0.0F, 0.125F);
            double d = net.minecraft.util.math.MathHelper.lerp((double)tickDelta, player.prevCapeX, player.capeX) - net.minecraft.util.math.MathHelper.lerp((double)tickDelta, player.prevX, player.getX());
            double e = net.minecraft.util.math.MathHelper.lerp((double)tickDelta, player.prevCapeY, player.capeY) - net.minecraft.util.math.MathHelper.lerp((double)tickDelta, player.prevY, player.getY());
            double f = net.minecraft.util.math.MathHelper.lerp((double)tickDelta, player.prevCapeZ, player.capeZ) - net.minecraft.util.math.MathHelper.lerp((double)tickDelta, player.prevZ, player.getZ());
            float g = player.prevBodyYaw + (player.bodyYaw - player.prevBodyYaw);
            double h = (double)net.minecraft.util.math.MathHelper.sin(g * 0.017453292F);
            double i = (double)(-net.minecraft.util.math.MathHelper.cos(g * 0.017453292F));
            float j = (float)e * 10.0F;
            j = net.minecraft.util.math.MathHelper.clamp(j, -6.0F, 32.0F);
            float k = (float)(d * h + f * i) * 100.0F;
            k = net.minecraft.util.math.MathHelper.clamp(k, 0.0F, 150.0F);
            float l = (float)(d * i - f * h) * 100.0F;
            l = net.minecraft.util.math.MathHelper.clamp(l, -20.0F, 20.0F);
            if (k < 0.0F) {
                k = 0.0F;
            }

            float m = net.minecraft.util.math.MathHelper.lerp(tickDelta, player.prevStrideDistance, player.strideDistance);
            j += net.minecraft.util.math.MathHelper.sin(net.minecraft.util.math.MathHelper.lerp(tickDelta, player.prevHorizontalSpeed, player.horizontalSpeed) * 6.0F) * 32.0F * m;
            if (player.isInSneakingPose()) {
                j += 25.0F;
            }

            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(6.0F + k / 2.0F + j));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(l / 2.0F));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - l / 2.0F));
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(capeTexture));
            ((PlayerEntityModel)this.getContextModel()).renderCape(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
            matrices.pop();
        }
    }
}
