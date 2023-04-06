/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public abstract class MobRenderer<T extends Mob, M extends EntityModel<T>>
extends LivingEntityRenderer<T, M> {
    public MobRenderer(EntityRenderDispatcher entityRenderDispatcher, M m, float f) {
        super(entityRenderDispatcher, m, f);
    }

    @Override
    protected boolean shouldShowName(T t) {
        return super.shouldShowName(t) && (((LivingEntity)t).shouldShowName() || ((Entity)t).hasCustomName() && t == this.entityRenderDispatcher.crosshairPickEntity);
    }

    @Override
    public boolean shouldRender(T t, Frustum frustum, double d, double d2, double d3) {
        if (super.shouldRender(t, frustum, d, d2, d3)) {
            return true;
        }
        Entity entity = ((Mob)t).getLeashHolder();
        if (entity != null) {
            return frustum.isVisible(entity.getBoundingBoxForCulling());
        }
        return false;
    }

    @Override
    public void render(T t, float f, float f2, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
        super.render(t, f, f2, poseStack, multiBufferSource, n);
        Entity entity = ((Mob)t).getLeashHolder();
        if (entity == null) {
            return;
        }
        this.renderLeash(t, f2, poseStack, multiBufferSource, entity);
    }

    private <E extends Entity> void renderLeash(T t, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, E e) {
        poseStack.pushPose();
        Vec3 vec3 = ((Entity)e).getRopeHoldPosition(f);
        double d = (double)(Mth.lerp(f, ((Mob)t).yBodyRot, ((Mob)t).yBodyRotO) * 0.017453292f) + 1.5707963267948966;
        Vec3 vec32 = ((Entity)t).getLeashOffset();
        double d2 = Math.cos(d) * vec32.z + Math.sin(d) * vec32.x;
        double d3 = Math.sin(d) * vec32.z - Math.cos(d) * vec32.x;
        double d4 = Mth.lerp((double)f, ((Mob)t).xo, ((Entity)t).getX()) + d2;
        double d5 = Mth.lerp((double)f, ((Mob)t).yo, ((Entity)t).getY()) + vec32.y;
        double d6 = Mth.lerp((double)f, ((Mob)t).zo, ((Entity)t).getZ()) + d3;
        poseStack.translate(d2, vec32.y, d3);
        float f2 = (float)(vec3.x - d4);
        float f3 = (float)(vec3.y - d5);
        float f4 = (float)(vec3.z - d6);
        float f5 = 0.025f;
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.leash());
        Matrix4f matrix4f = poseStack.last().pose();
        float f6 = Mth.fastInvSqrt(f2 * f2 + f4 * f4) * 0.025f / 2.0f;
        float f7 = f4 * f6;
        float f8 = f2 * f6;
        BlockPos blockPos = new BlockPos(((Entity)t).getEyePosition(f));
        BlockPos blockPos2 = new BlockPos(((Entity)e).getEyePosition(f));
        int n = this.getBlockLightLevel(t, blockPos);
        int n2 = this.entityRenderDispatcher.getRenderer(e).getBlockLightLevel(e, blockPos2);
        int n3 = ((Mob)t).level.getBrightness(LightLayer.SKY, blockPos);
        int n4 = ((Mob)t).level.getBrightness(LightLayer.SKY, blockPos2);
        MobRenderer.renderSide(vertexConsumer, matrix4f, f2, f3, f4, n, n2, n3, n4, 0.025f, 0.025f, f7, f8);
        MobRenderer.renderSide(vertexConsumer, matrix4f, f2, f3, f4, n, n2, n3, n4, 0.025f, 0.0f, f7, f8);
        poseStack.popPose();
    }

    public static void renderSide(VertexConsumer vertexConsumer, Matrix4f matrix4f, float f, float f2, float f3, int n, int n2, int n3, int n4, float f4, float f5, float f6, float f7) {
        int n5 = 24;
        for (int i = 0; i < 24; ++i) {
            float f8 = (float)i / 23.0f;
            int n6 = (int)Mth.lerp(f8, n, n2);
            int n7 = (int)Mth.lerp(f8, n3, n4);
            int n8 = LightTexture.pack(n6, n7);
            MobRenderer.addVertexPair(vertexConsumer, matrix4f, n8, f, f2, f3, f4, f5, 24, i, false, f6, f7);
            MobRenderer.addVertexPair(vertexConsumer, matrix4f, n8, f, f2, f3, f4, f5, 24, i + 1, true, f6, f7);
        }
    }

    public static void addVertexPair(VertexConsumer vertexConsumer, Matrix4f matrix4f, int n, float f, float f2, float f3, float f4, float f5, int n2, int n3, boolean bl, float f6, float f7) {
        float f8 = 0.5f;
        float f9 = 0.4f;
        float f10 = 0.3f;
        if (n3 % 2 == 0) {
            f8 *= 0.7f;
            f9 *= 0.7f;
            f10 *= 0.7f;
        }
        float f11 = (float)n3 / (float)n2;
        float f12 = f * f11;
        float f13 = f2 > 0.0f ? f2 * f11 * f11 : f2 - f2 * (1.0f - f11) * (1.0f - f11);
        float f14 = f3 * f11;
        if (!bl) {
            vertexConsumer.vertex(matrix4f, f12 + f6, f13 + f4 - f5, f14 - f7).color(f8, f9, f10, 1.0f).uv2(n).endVertex();
        }
        vertexConsumer.vertex(matrix4f, f12 - f6, f13 + f5, f14 + f7).color(f8, f9, f10, 1.0f).uv2(n).endVertex();
        if (bl) {
            vertexConsumer.vertex(matrix4f, f12 + f6, f13 + f4 - f5, f14 - f7).color(f8, f9, f10, 1.0f).uv2(n).endVertex();
        }
    }
}

