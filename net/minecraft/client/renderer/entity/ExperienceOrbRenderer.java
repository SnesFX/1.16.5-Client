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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;

public class ExperienceOrbRenderer
extends EntityRenderer<ExperienceOrb> {
    private static final ResourceLocation EXPERIENCE_ORB_LOCATION = new ResourceLocation("textures/entity/experience_orb.png");
    private static final RenderType RENDER_TYPE = RenderType.itemEntityTranslucentCull(EXPERIENCE_ORB_LOCATION);

    public ExperienceOrbRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.shadowRadius = 0.15f;
        this.shadowStrength = 0.75f;
    }

    @Override
    protected int getBlockLightLevel(ExperienceOrb experienceOrb, BlockPos blockPos) {
        return Mth.clamp(super.getBlockLightLevel(experienceOrb, blockPos) + 7, 0, 15);
    }

    @Override
    public void render(ExperienceOrb experienceOrb, float f, float f2, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
        poseStack.pushPose();
        int n2 = experienceOrb.getIcon();
        float f3 = (float)(n2 % 4 * 16 + 0) / 64.0f;
        float f4 = (float)(n2 % 4 * 16 + 16) / 64.0f;
        float f5 = (float)(n2 / 4 * 16 + 0) / 64.0f;
        float f6 = (float)(n2 / 4 * 16 + 16) / 64.0f;
        float f7 = 1.0f;
        float f8 = 0.5f;
        float f9 = 0.25f;
        float f10 = 255.0f;
        float f11 = ((float)experienceOrb.tickCount + f2) / 2.0f;
        int n3 = (int)((Mth.sin(f11 + 0.0f) + 1.0f) * 0.5f * 255.0f);
        int n4 = 255;
        int n5 = (int)((Mth.sin(f11 + 4.1887903f) + 1.0f) * 0.1f * 255.0f);
        poseStack.translate(0.0, 0.10000000149011612, 0.0);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0f));
        float f12 = 0.3f;
        poseStack.scale(0.3f, 0.3f, 0.3f);
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RENDER_TYPE);
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();
        ExperienceOrbRenderer.vertex(vertexConsumer, matrix4f, matrix3f, -0.5f, -0.25f, n3, 255, n5, f3, f6, n);
        ExperienceOrbRenderer.vertex(vertexConsumer, matrix4f, matrix3f, 0.5f, -0.25f, n3, 255, n5, f4, f6, n);
        ExperienceOrbRenderer.vertex(vertexConsumer, matrix4f, matrix3f, 0.5f, 0.75f, n3, 255, n5, f4, f5, n);
        ExperienceOrbRenderer.vertex(vertexConsumer, matrix4f, matrix3f, -0.5f, 0.75f, n3, 255, n5, f3, f5, n);
        poseStack.popPose();
        super.render(experienceOrb, f, f2, poseStack, multiBufferSource, n);
    }

    private static void vertex(VertexConsumer vertexConsumer, Matrix4f matrix4f, Matrix3f matrix3f, float f, float f2, int n, int n2, int n3, float f3, float f4, int n4) {
        vertexConsumer.vertex(matrix4f, f, f2, 0.0f).color(n, n2, n3, 128).uv(f3, f4).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n4).normal(matrix3f, 0.0f, 1.0f, 0.0f).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(ExperienceOrb experienceOrb) {
        return EXPERIENCE_ORB_LOCATION;
    }
}

