/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.DragonFireball;

public class DragonFireballRenderer
extends EntityRenderer<DragonFireball> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/enderdragon/dragon_fireball.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(TEXTURE_LOCATION);

    public DragonFireballRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    @Override
    protected int getBlockLightLevel(DragonFireball dragonFireball, BlockPos blockPos) {
        return 15;
    }

    @Override
    public void render(DragonFireball dragonFireball, float f, float f2, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
        poseStack.pushPose();
        poseStack.scale(2.0f, 2.0f, 2.0f);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0f));
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RENDER_TYPE);
        DragonFireballRenderer.vertex(vertexConsumer, matrix4f, matrix3f, n, 0.0f, 0, 0, 1);
        DragonFireballRenderer.vertex(vertexConsumer, matrix4f, matrix3f, n, 1.0f, 0, 1, 1);
        DragonFireballRenderer.vertex(vertexConsumer, matrix4f, matrix3f, n, 1.0f, 1, 1, 0);
        DragonFireballRenderer.vertex(vertexConsumer, matrix4f, matrix3f, n, 0.0f, 1, 0, 0);
        poseStack.popPose();
        super.render(dragonFireball, f, f2, poseStack, multiBufferSource, n);
    }

    private static void vertex(VertexConsumer vertexConsumer, Matrix4f matrix4f, Matrix3f matrix3f, int n, float f, int n2, int n3, int n4) {
        vertexConsumer.vertex(matrix4f, f - 0.5f, (float)n2 - 0.25f, 0.0f).color(255, 255, 255, 255).uv(n3, n4).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n).normal(matrix3f, 0.0f, 1.0f, 0.0f).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(DragonFireball dragonFireball) {
        return TEXTURE_LOCATION;
    }
}

