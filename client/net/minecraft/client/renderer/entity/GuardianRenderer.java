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
import net.minecraft.client.model.GuardianModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class GuardianRenderer
extends MobRenderer<Guardian, GuardianModel> {
    private static final ResourceLocation GUARDIAN_LOCATION = new ResourceLocation("textures/entity/guardian.png");
    private static final ResourceLocation GUARDIAN_BEAM_LOCATION = new ResourceLocation("textures/entity/guardian_beam.png");
    private static final RenderType BEAM_RENDER_TYPE = RenderType.entityCutoutNoCull(GUARDIAN_BEAM_LOCATION);

    public GuardianRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        this(entityRenderDispatcher, 0.5f);
    }

    protected GuardianRenderer(EntityRenderDispatcher entityRenderDispatcher, float f) {
        super(entityRenderDispatcher, new GuardianModel(), f);
    }

    @Override
    public boolean shouldRender(Guardian guardian, Frustum frustum, double d, double d2, double d3) {
        LivingEntity livingEntity;
        if (super.shouldRender(guardian, frustum, d, d2, d3)) {
            return true;
        }
        if (guardian.hasActiveAttackTarget() && (livingEntity = guardian.getActiveAttackTarget()) != null) {
            Vec3 vec3 = this.getPosition(livingEntity, (double)livingEntity.getBbHeight() * 0.5, 1.0f);
            Vec3 vec32 = this.getPosition(guardian, guardian.getEyeHeight(), 1.0f);
            return frustum.isVisible(new AABB(vec32.x, vec32.y, vec32.z, vec3.x, vec3.y, vec3.z));
        }
        return false;
    }

    private Vec3 getPosition(LivingEntity livingEntity, double d, float f) {
        double d2 = Mth.lerp((double)f, livingEntity.xOld, livingEntity.getX());
        double d3 = Mth.lerp((double)f, livingEntity.yOld, livingEntity.getY()) + d;
        double d4 = Mth.lerp((double)f, livingEntity.zOld, livingEntity.getZ());
        return new Vec3(d2, d3, d4);
    }

    @Override
    public void render(Guardian guardian, float f, float f2, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
        super.render(guardian, f, f2, poseStack, multiBufferSource, n);
        LivingEntity livingEntity = guardian.getActiveAttackTarget();
        if (livingEntity != null) {
            float f3 = guardian.getAttackAnimationScale(f2);
            float f4 = (float)guardian.level.getGameTime() + f2;
            float f5 = f4 * 0.5f % 1.0f;
            float f6 = guardian.getEyeHeight();
            poseStack.pushPose();
            poseStack.translate(0.0, f6, 0.0);
            Vec3 vec3 = this.getPosition(livingEntity, (double)livingEntity.getBbHeight() * 0.5, f2);
            Vec3 vec32 = this.getPosition(guardian, f6, f2);
            Vec3 vec33 = vec3.subtract(vec32);
            float f7 = (float)(vec33.length() + 1.0);
            vec33 = vec33.normalize();
            float f8 = (float)Math.acos(vec33.y);
            float f9 = (float)Math.atan2(vec33.z, vec33.x);
            poseStack.mulPose(Vector3f.YP.rotationDegrees((1.5707964f - f9) * 57.295776f));
            poseStack.mulPose(Vector3f.XP.rotationDegrees(f8 * 57.295776f));
            boolean bl = true;
            float f10 = f4 * 0.05f * -1.5f;
            float f11 = f3 * f3;
            int n2 = 64 + (int)(f11 * 191.0f);
            int n3 = 32 + (int)(f11 * 191.0f);
            int n4 = 128 - (int)(f11 * 64.0f);
            float f12 = 0.2f;
            float f13 = 0.282f;
            float f14 = Mth.cos(f10 + 2.3561945f) * 0.282f;
            float f15 = Mth.sin(f10 + 2.3561945f) * 0.282f;
            float f16 = Mth.cos(f10 + 0.7853982f) * 0.282f;
            float f17 = Mth.sin(f10 + 0.7853982f) * 0.282f;
            float f18 = Mth.cos(f10 + 3.926991f) * 0.282f;
            float f19 = Mth.sin(f10 + 3.926991f) * 0.282f;
            float f20 = Mth.cos(f10 + 5.4977875f) * 0.282f;
            float f21 = Mth.sin(f10 + 5.4977875f) * 0.282f;
            float f22 = Mth.cos(f10 + 3.1415927f) * 0.2f;
            float f23 = Mth.sin(f10 + 3.1415927f) * 0.2f;
            float f24 = Mth.cos(f10 + 0.0f) * 0.2f;
            float f25 = Mth.sin(f10 + 0.0f) * 0.2f;
            float f26 = Mth.cos(f10 + 1.5707964f) * 0.2f;
            float f27 = Mth.sin(f10 + 1.5707964f) * 0.2f;
            float f28 = Mth.cos(f10 + 4.712389f) * 0.2f;
            float f29 = Mth.sin(f10 + 4.712389f) * 0.2f;
            float f30 = f7;
            float f31 = 0.0f;
            float f32 = 0.4999f;
            float f33 = -1.0f + f5;
            float f34 = f7 * 2.5f + f33;
            VertexConsumer vertexConsumer = multiBufferSource.getBuffer(BEAM_RENDER_TYPE);
            PoseStack.Pose pose = poseStack.last();
            Matrix4f matrix4f = pose.pose();
            Matrix3f matrix3f = pose.normal();
            GuardianRenderer.vertex(vertexConsumer, matrix4f, matrix3f, f22, f30, f23, n2, n3, n4, 0.4999f, f34);
            GuardianRenderer.vertex(vertexConsumer, matrix4f, matrix3f, f22, 0.0f, f23, n2, n3, n4, 0.4999f, f33);
            GuardianRenderer.vertex(vertexConsumer, matrix4f, matrix3f, f24, 0.0f, f25, n2, n3, n4, 0.0f, f33);
            GuardianRenderer.vertex(vertexConsumer, matrix4f, matrix3f, f24, f30, f25, n2, n3, n4, 0.0f, f34);
            GuardianRenderer.vertex(vertexConsumer, matrix4f, matrix3f, f26, f30, f27, n2, n3, n4, 0.4999f, f34);
            GuardianRenderer.vertex(vertexConsumer, matrix4f, matrix3f, f26, 0.0f, f27, n2, n3, n4, 0.4999f, f33);
            GuardianRenderer.vertex(vertexConsumer, matrix4f, matrix3f, f28, 0.0f, f29, n2, n3, n4, 0.0f, f33);
            GuardianRenderer.vertex(vertexConsumer, matrix4f, matrix3f, f28, f30, f29, n2, n3, n4, 0.0f, f34);
            float f35 = 0.0f;
            if (guardian.tickCount % 2 == 0) {
                f35 = 0.5f;
            }
            GuardianRenderer.vertex(vertexConsumer, matrix4f, matrix3f, f14, f30, f15, n2, n3, n4, 0.5f, f35 + 0.5f);
            GuardianRenderer.vertex(vertexConsumer, matrix4f, matrix3f, f16, f30, f17, n2, n3, n4, 1.0f, f35 + 0.5f);
            GuardianRenderer.vertex(vertexConsumer, matrix4f, matrix3f, f20, f30, f21, n2, n3, n4, 1.0f, f35);
            GuardianRenderer.vertex(vertexConsumer, matrix4f, matrix3f, f18, f30, f19, n2, n3, n4, 0.5f, f35);
            poseStack.popPose();
        }
    }

    private static void vertex(VertexConsumer vertexConsumer, Matrix4f matrix4f, Matrix3f matrix3f, float f, float f2, float f3, int n, int n2, int n3, float f4, float f5) {
        vertexConsumer.vertex(matrix4f, f, f2, f3).color(n, n2, n3, 255).uv(f4, f5).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(matrix3f, 0.0f, 1.0f, 0.0f).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(Guardian guardian) {
        return GUARDIAN_LOCATION;
    }
}

