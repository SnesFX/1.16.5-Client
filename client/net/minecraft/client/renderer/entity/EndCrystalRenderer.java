/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;

public class EndCrystalRenderer
extends EntityRenderer<EndCrystal> {
    private static final ResourceLocation END_CRYSTAL_LOCATION = new ResourceLocation("textures/entity/end_crystal/end_crystal.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(END_CRYSTAL_LOCATION);
    private static final float SIN_45 = (float)Math.sin(0.7853981633974483);
    private final ModelPart cube;
    private final ModelPart glass;
    private final ModelPart base;

    public EndCrystalRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.shadowRadius = 0.5f;
        this.glass = new ModelPart(64, 32, 0, 0);
        this.glass.addBox(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f);
        this.cube = new ModelPart(64, 32, 32, 0);
        this.cube.addBox(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f);
        this.base = new ModelPart(64, 32, 0, 16);
        this.base.addBox(-6.0f, 0.0f, -6.0f, 12.0f, 4.0f, 12.0f);
    }

    @Override
    public void render(EndCrystal endCrystal, float f, float f2, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
        poseStack.pushPose();
        float f3 = EndCrystalRenderer.getY(endCrystal, f2);
        float f4 = ((float)endCrystal.time + f2) * 3.0f;
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RENDER_TYPE);
        poseStack.pushPose();
        poseStack.scale(2.0f, 2.0f, 2.0f);
        poseStack.translate(0.0, -0.5, 0.0);
        int n2 = OverlayTexture.NO_OVERLAY;
        if (endCrystal.showsBottom()) {
            this.base.render(poseStack, vertexConsumer, n, n2);
        }
        poseStack.mulPose(Vector3f.YP.rotationDegrees(f4));
        poseStack.translate(0.0, 1.5f + f3 / 2.0f, 0.0);
        poseStack.mulPose(new Quaternion(new Vector3f(SIN_45, 0.0f, SIN_45), 60.0f, true));
        this.glass.render(poseStack, vertexConsumer, n, n2);
        float f5 = 0.875f;
        poseStack.scale(0.875f, 0.875f, 0.875f);
        poseStack.mulPose(new Quaternion(new Vector3f(SIN_45, 0.0f, SIN_45), 60.0f, true));
        poseStack.mulPose(Vector3f.YP.rotationDegrees(f4));
        this.glass.render(poseStack, vertexConsumer, n, n2);
        poseStack.scale(0.875f, 0.875f, 0.875f);
        poseStack.mulPose(new Quaternion(new Vector3f(SIN_45, 0.0f, SIN_45), 60.0f, true));
        poseStack.mulPose(Vector3f.YP.rotationDegrees(f4));
        this.cube.render(poseStack, vertexConsumer, n, n2);
        poseStack.popPose();
        poseStack.popPose();
        BlockPos blockPos = endCrystal.getBeamTarget();
        if (blockPos != null) {
            float f6 = (float)blockPos.getX() + 0.5f;
            float f7 = (float)blockPos.getY() + 0.5f;
            float f8 = (float)blockPos.getZ() + 0.5f;
            float f9 = (float)((double)f6 - endCrystal.getX());
            float f10 = (float)((double)f7 - endCrystal.getY());
            float f11 = (float)((double)f8 - endCrystal.getZ());
            poseStack.translate(f9, f10, f11);
            EnderDragonRenderer.renderCrystalBeams(-f9, -f10 + f3, -f11, f2, endCrystal.time, poseStack, multiBufferSource, n);
        }
        super.render(endCrystal, f, f2, poseStack, multiBufferSource, n);
    }

    public static float getY(EndCrystal endCrystal, float f) {
        float f2 = (float)endCrystal.time + f;
        float f3 = Mth.sin(f2 * 0.2f) / 2.0f + 0.5f;
        f3 = (f3 * f3 + f3) * 0.4f;
        return f3 - 1.4f;
    }

    @Override
    public ResourceLocation getTextureLocation(EndCrystal endCrystal) {
        return END_CRYSTAL_LOCATION;
    }

    @Override
    public boolean shouldRender(EndCrystal endCrystal, Frustum frustum, double d, double d2, double d3) {
        return super.shouldRender(endCrystal, frustum, d, d2, d3) || endCrystal.getBeamTarget() != null;
    }
}

