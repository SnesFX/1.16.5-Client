/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.ShulkerBulletModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ShulkerBullet;

public class ShulkerBulletRenderer
extends EntityRenderer<ShulkerBullet> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/shulker/spark.png");
    private static final RenderType RENDER_TYPE = RenderType.entityTranslucent(TEXTURE_LOCATION);
    private final ShulkerBulletModel<ShulkerBullet> model = new ShulkerBulletModel();

    public ShulkerBulletRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    @Override
    protected int getBlockLightLevel(ShulkerBullet shulkerBullet, BlockPos blockPos) {
        return 15;
    }

    @Override
    public void render(ShulkerBullet shulkerBullet, float f, float f2, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
        poseStack.pushPose();
        float f3 = Mth.rotlerp(shulkerBullet.yRotO, shulkerBullet.yRot, f2);
        float f4 = Mth.lerp(f2, shulkerBullet.xRotO, shulkerBullet.xRot);
        float f5 = (float)shulkerBullet.tickCount + f2;
        poseStack.translate(0.0, 0.15000000596046448, 0.0);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(Mth.sin(f5 * 0.1f) * 180.0f));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(Mth.cos(f5 * 0.1f) * 180.0f));
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(Mth.sin(f5 * 0.15f) * 360.0f));
        poseStack.scale(-0.5f, -0.5f, 0.5f);
        this.model.setupAnim(shulkerBullet, 0.0f, 0.0f, 0.0f, f3, f4);
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(this.model.renderType(TEXTURE_LOCATION));
        this.model.renderToBuffer(poseStack, vertexConsumer, n, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
        poseStack.scale(1.5f, 1.5f, 1.5f);
        VertexConsumer vertexConsumer2 = multiBufferSource.getBuffer(RENDER_TYPE);
        this.model.renderToBuffer(poseStack, vertexConsumer2, n, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 0.15f);
        poseStack.popPose();
        super.render(shulkerBullet, f, f2, poseStack, multiBufferSource, n);
    }

    @Override
    public ResourceLocation getTextureLocation(ShulkerBullet shulkerBullet) {
        return TEXTURE_LOCATION;
    }
}

