/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public abstract class EntityRenderer<T extends Entity> {
    protected final EntityRenderDispatcher entityRenderDispatcher;
    protected float shadowRadius;
    protected float shadowStrength = 1.0f;

    protected EntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        this.entityRenderDispatcher = entityRenderDispatcher;
    }

    public final int getPackedLightCoords(T t, float f) {
        BlockPos blockPos = new BlockPos(((Entity)t).getLightProbePosition(f));
        return LightTexture.pack(this.getBlockLightLevel(t, blockPos), this.getSkyLightLevel(t, blockPos));
    }

    protected int getSkyLightLevel(T t, BlockPos blockPos) {
        return ((Entity)t).level.getBrightness(LightLayer.SKY, blockPos);
    }

    protected int getBlockLightLevel(T t, BlockPos blockPos) {
        if (((Entity)t).isOnFire()) {
            return 15;
        }
        return ((Entity)t).level.getBrightness(LightLayer.BLOCK, blockPos);
    }

    public boolean shouldRender(T t, Frustum frustum, double d, double d2, double d3) {
        if (!((Entity)t).shouldRender(d, d2, d3)) {
            return false;
        }
        if (((Entity)t).noCulling) {
            return true;
        }
        AABB aABB = ((Entity)t).getBoundingBoxForCulling().inflate(0.5);
        if (aABB.hasNaN() || aABB.getSize() == 0.0) {
            aABB = new AABB(((Entity)t).getX() - 2.0, ((Entity)t).getY() - 2.0, ((Entity)t).getZ() - 2.0, ((Entity)t).getX() + 2.0, ((Entity)t).getY() + 2.0, ((Entity)t).getZ() + 2.0);
        }
        return frustum.isVisible(aABB);
    }

    public Vec3 getRenderOffset(T t, float f) {
        return Vec3.ZERO;
    }

    public void render(T t, float f, float f2, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
        if (!this.shouldShowName(t)) {
            return;
        }
        this.renderNameTag(t, ((Entity)t).getDisplayName(), poseStack, multiBufferSource, n);
    }

    protected boolean shouldShowName(T t) {
        return ((Entity)t).shouldShowName() && ((Entity)t).hasCustomName();
    }

    public abstract ResourceLocation getTextureLocation(T var1);

    public Font getFont() {
        return this.entityRenderDispatcher.getFont();
    }

    protected void renderNameTag(T t, Component component, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
        double d = this.entityRenderDispatcher.distanceToSqr((Entity)t);
        if (d > 4096.0) {
            return;
        }
        boolean bl = !((Entity)t).isDiscrete();
        float f = ((Entity)t).getBbHeight() + 0.5f;
        int n2 = "deadmau5".equals(component.getString()) ? -10 : 0;
        poseStack.pushPose();
        poseStack.translate(0.0, f, 0.0);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.scale(-0.025f, -0.025f, 0.025f);
        Matrix4f matrix4f = poseStack.last().pose();
        float f2 = Minecraft.getInstance().options.getBackgroundOpacity(0.25f);
        int n3 = (int)(f2 * 255.0f) << 24;
        Font font = this.getFont();
        float f3 = -font.width(component) / 2;
        font.drawInBatch(component, f3, (float)n2, 553648127, false, matrix4f, multiBufferSource, bl, n3, n);
        if (bl) {
            font.drawInBatch(component, f3, (float)n2, -1, false, matrix4f, multiBufferSource, false, 0, n);
        }
        poseStack.popPose();
    }

    public EntityRenderDispatcher getDispatcher() {
        return this.entityRenderDispatcher;
    }
}

