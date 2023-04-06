/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.StuckInBodyLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class BeeStingerLayer<T extends LivingEntity, M extends PlayerModel<T>>
extends StuckInBodyLayer<T, M> {
    private static final ResourceLocation BEE_STINGER_LOCATION = new ResourceLocation("textures/entity/bee/bee_stinger.png");

    public BeeStingerLayer(LivingEntityRenderer<T, M> livingEntityRenderer) {
        super(livingEntityRenderer);
    }

    @Override
    protected int numStuck(T t) {
        return ((LivingEntity)t).getStingerCount();
    }

    @Override
    protected void renderStuckItem(PoseStack poseStack, MultiBufferSource multiBufferSource, int n, Entity entity, float f, float f2, float f3, float f4) {
        float f5 = Mth.sqrt(f * f + f3 * f3);
        float f6 = (float)(Math.atan2(f, f3) * 57.2957763671875);
        float f7 = (float)(Math.atan2(f2, f5) * 57.2957763671875);
        poseStack.translate(0.0, 0.0, 0.0);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(f6 - 90.0f));
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(f7));
        float f8 = 0.0f;
        float f9 = 0.125f;
        float f10 = 0.0f;
        float f11 = 0.0625f;
        float f12 = 0.03125f;
        poseStack.mulPose(Vector3f.XP.rotationDegrees(45.0f));
        poseStack.scale(0.03125f, 0.03125f, 0.03125f);
        poseStack.translate(2.5, 0.0, 0.0);
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.entityCutoutNoCull(BEE_STINGER_LOCATION));
        for (int i = 0; i < 4; ++i) {
            poseStack.mulPose(Vector3f.XP.rotationDegrees(90.0f));
            PoseStack.Pose pose = poseStack.last();
            Matrix4f matrix4f = pose.pose();
            Matrix3f matrix3f = pose.normal();
            BeeStingerLayer.vertex(vertexConsumer, matrix4f, matrix3f, -4.5f, -1, 0.0f, 0.0f, n);
            BeeStingerLayer.vertex(vertexConsumer, matrix4f, matrix3f, 4.5f, -1, 0.125f, 0.0f, n);
            BeeStingerLayer.vertex(vertexConsumer, matrix4f, matrix3f, 4.5f, 1, 0.125f, 0.0625f, n);
            BeeStingerLayer.vertex(vertexConsumer, matrix4f, matrix3f, -4.5f, 1, 0.0f, 0.0625f, n);
        }
    }

    private static void vertex(VertexConsumer vertexConsumer, Matrix4f matrix4f, Matrix3f matrix3f, float f, int n, float f2, float f3, int n2) {
        vertexConsumer.vertex(matrix4f, f, n, 0.0f).color(255, 255, 255, 255).uv(f2, f3).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2).normal(matrix3f, 0.0f, 1.0f, 0.0f).endVertex();
    }
}

