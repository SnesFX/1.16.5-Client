/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import java.util.List;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BeaconRenderer
extends BlockEntityRenderer<BeaconBlockEntity> {
    public static final ResourceLocation BEAM_LOCATION = new ResourceLocation("textures/entity/beacon_beam.png");

    public BeaconRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
    }

    @Override
    public void render(BeaconBlockEntity beaconBlockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int n, int n2) {
        long l = beaconBlockEntity.getLevel().getGameTime();
        List<BeaconBlockEntity.BeaconBeamSection> list = beaconBlockEntity.getBeamSections();
        int n3 = 0;
        for (int i = 0; i < list.size(); ++i) {
            BeaconBlockEntity.BeaconBeamSection beaconBeamSection = list.get(i);
            BeaconRenderer.renderBeaconBeam(poseStack, multiBufferSource, f, l, n3, i == list.size() - 1 ? 1024 : beaconBeamSection.getHeight(), beaconBeamSection.getColor());
            n3 += beaconBeamSection.getHeight();
        }
    }

    private static void renderBeaconBeam(PoseStack poseStack, MultiBufferSource multiBufferSource, float f, long l, int n, int n2, float[] arrf) {
        BeaconRenderer.renderBeaconBeam(poseStack, multiBufferSource, BEAM_LOCATION, f, 1.0f, l, n, n2, arrf, 0.2f, 0.25f);
    }

    public static void renderBeaconBeam(PoseStack poseStack, MultiBufferSource multiBufferSource, ResourceLocation resourceLocation, float f, float f2, long l, int n, int n2, float[] arrf, float f3, float f4) {
        int n3 = n + n2;
        poseStack.pushPose();
        poseStack.translate(0.5, 0.0, 0.5);
        float f5 = (float)Math.floorMod(l, 40L) + f;
        float f6 = n2 < 0 ? f5 : -f5;
        float f7 = Mth.frac(f6 * 0.2f - (float)Mth.floor(f6 * 0.1f));
        float f8 = arrf[0];
        float f9 = arrf[1];
        float f10 = arrf[2];
        poseStack.pushPose();
        poseStack.mulPose(Vector3f.YP.rotationDegrees(f5 * 2.25f - 45.0f));
        float f11 = 0.0f;
        float f12 = f3;
        float f13 = f3;
        float f14 = 0.0f;
        float f15 = -f3;
        float f16 = 0.0f;
        float f17 = 0.0f;
        float f18 = -f3;
        float f19 = 0.0f;
        float f20 = 1.0f;
        float f21 = -1.0f + f7;
        float f22 = (float)n2 * f2 * (0.5f / f3) + f21;
        BeaconRenderer.renderPart(poseStack, multiBufferSource.getBuffer(RenderType.beaconBeam(resourceLocation, false)), f8, f9, f10, 1.0f, n, n3, 0.0f, f12, f13, 0.0f, f15, 0.0f, 0.0f, f18, 0.0f, 1.0f, f22, f21);
        poseStack.popPose();
        f11 = -f4;
        f12 = -f4;
        f13 = f4;
        f14 = -f4;
        f15 = -f4;
        f16 = f4;
        f17 = f4;
        f18 = f4;
        f19 = 0.0f;
        f20 = 1.0f;
        f21 = -1.0f + f7;
        f22 = (float)n2 * f2 + f21;
        BeaconRenderer.renderPart(poseStack, multiBufferSource.getBuffer(RenderType.beaconBeam(resourceLocation, true)), f8, f9, f10, 0.125f, n, n3, f11, f12, f13, f14, f15, f16, f17, f18, 0.0f, 1.0f, f22, f21);
        poseStack.popPose();
    }

    private static void renderPart(PoseStack poseStack, VertexConsumer vertexConsumer, float f, float f2, float f3, float f4, int n, int n2, float f5, float f6, float f7, float f8, float f9, float f10, float f11, float f12, float f13, float f14, float f15, float f16) {
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();
        BeaconRenderer.renderQuad(matrix4f, matrix3f, vertexConsumer, f, f2, f3, f4, n, n2, f5, f6, f7, f8, f13, f14, f15, f16);
        BeaconRenderer.renderQuad(matrix4f, matrix3f, vertexConsumer, f, f2, f3, f4, n, n2, f11, f12, f9, f10, f13, f14, f15, f16);
        BeaconRenderer.renderQuad(matrix4f, matrix3f, vertexConsumer, f, f2, f3, f4, n, n2, f7, f8, f11, f12, f13, f14, f15, f16);
        BeaconRenderer.renderQuad(matrix4f, matrix3f, vertexConsumer, f, f2, f3, f4, n, n2, f9, f10, f5, f6, f13, f14, f15, f16);
    }

    private static void renderQuad(Matrix4f matrix4f, Matrix3f matrix3f, VertexConsumer vertexConsumer, float f, float f2, float f3, float f4, int n, int n2, float f5, float f6, float f7, float f8, float f9, float f10, float f11, float f12) {
        BeaconRenderer.addVertex(matrix4f, matrix3f, vertexConsumer, f, f2, f3, f4, n2, f5, f6, f10, f11);
        BeaconRenderer.addVertex(matrix4f, matrix3f, vertexConsumer, f, f2, f3, f4, n, f5, f6, f10, f12);
        BeaconRenderer.addVertex(matrix4f, matrix3f, vertexConsumer, f, f2, f3, f4, n, f7, f8, f9, f12);
        BeaconRenderer.addVertex(matrix4f, matrix3f, vertexConsumer, f, f2, f3, f4, n2, f7, f8, f9, f11);
    }

    private static void addVertex(Matrix4f matrix4f, Matrix3f matrix3f, VertexConsumer vertexConsumer, float f, float f2, float f3, float f4, int n, float f5, float f6, float f7, float f8) {
        vertexConsumer.vertex(matrix4f, f5, n, f6).color(f, f2, f3, f4).uv(f7, f8).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(matrix3f, 0.0f, 1.0f, 0.0f).endVertex();
    }

    @Override
    public boolean shouldRenderOffScreen(BeaconBlockEntity beaconBlockEntity) {
        return true;
    }
}

