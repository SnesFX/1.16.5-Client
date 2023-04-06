/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.MinecartModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class MinecartRenderer<T extends AbstractMinecart>
extends EntityRenderer<T> {
    private static final ResourceLocation MINECART_LOCATION = new ResourceLocation("textures/entity/minecart.png");
    protected final EntityModel<T> model = new MinecartModel();

    public MinecartRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.shadowRadius = 0.7f;
    }

    @Override
    public void render(T t, float f, float f2, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
        super.render(t, f, f2, poseStack, multiBufferSource, n);
        poseStack.pushPose();
        long l = (long)((Entity)t).getId() * 493286711L;
        l = l * l * 4392167121L + l * 98761L;
        float f3 = (((float)(l >> 16 & 7L) + 0.5f) / 8.0f - 0.5f) * 0.004f;
        float f4 = (((float)(l >> 20 & 7L) + 0.5f) / 8.0f - 0.5f) * 0.004f;
        float f5 = (((float)(l >> 24 & 7L) + 0.5f) / 8.0f - 0.5f) * 0.004f;
        poseStack.translate(f3, f4, f5);
        double d = Mth.lerp((double)f2, ((AbstractMinecart)t).xOld, ((Entity)t).getX());
        double d2 = Mth.lerp((double)f2, ((AbstractMinecart)t).yOld, ((Entity)t).getY());
        double d3 = Mth.lerp((double)f2, ((AbstractMinecart)t).zOld, ((Entity)t).getZ());
        double d4 = 0.30000001192092896;
        Vec3 vec3 = ((AbstractMinecart)t).getPos(d, d2, d3);
        float f6 = Mth.lerp(f2, ((AbstractMinecart)t).xRotO, ((AbstractMinecart)t).xRot);
        if (vec3 != null) {
            Vec3 vec32 = ((AbstractMinecart)t).getPosOffs(d, d2, d3, 0.30000001192092896);
            Vec3 vec33 = ((AbstractMinecart)t).getPosOffs(d, d2, d3, -0.30000001192092896);
            if (vec32 == null) {
                vec32 = vec3;
            }
            if (vec33 == null) {
                vec33 = vec3;
            }
            poseStack.translate(vec3.x - d, (vec32.y + vec33.y) / 2.0 - d2, vec3.z - d3);
            Vec3 vec34 = vec33.add(-vec32.x, -vec32.y, -vec32.z);
            if (vec34.length() != 0.0) {
                vec34 = vec34.normalize();
                f = (float)(Math.atan2(vec34.z, vec34.x) * 180.0 / 3.141592653589793);
                f6 = (float)(Math.atan(vec34.y) * 73.0);
            }
        }
        poseStack.translate(0.0, 0.375, 0.0);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0f - f));
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(-f6));
        float f7 = (float)((AbstractMinecart)t).getHurtTime() - f2;
        float f8 = ((AbstractMinecart)t).getDamage() - f2;
        if (f8 < 0.0f) {
            f8 = 0.0f;
        }
        if (f7 > 0.0f) {
            poseStack.mulPose(Vector3f.XP.rotationDegrees(Mth.sin(f7) * f7 * f8 / 10.0f * (float)((AbstractMinecart)t).getHurtDir()));
        }
        int n2 = ((AbstractMinecart)t).getDisplayOffset();
        BlockState blockState = ((AbstractMinecart)t).getDisplayBlockState();
        if (blockState.getRenderShape() != RenderShape.INVISIBLE) {
            poseStack.pushPose();
            float f9 = 0.75f;
            poseStack.scale(0.75f, 0.75f, 0.75f);
            poseStack.translate(-0.5, (float)(n2 - 8) / 16.0f, 0.5);
            poseStack.mulPose(Vector3f.YP.rotationDegrees(90.0f));
            this.renderMinecartContents(t, f2, blockState, poseStack, multiBufferSource, n);
            poseStack.popPose();
        }
        poseStack.scale(-1.0f, -1.0f, 1.0f);
        this.model.setupAnim(t, 0.0f, 0.0f, -0.1f, 0.0f, 0.0f);
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(this.model.renderType(this.getTextureLocation(t)));
        this.model.renderToBuffer(poseStack, vertexConsumer, n, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(T t) {
        return MINECART_LOCATION;
    }

    protected void renderMinecartContents(T t, float f, BlockState blockState, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(blockState, poseStack, multiBufferSource, n, OverlayTexture.NO_OVERLAY);
    }
}

