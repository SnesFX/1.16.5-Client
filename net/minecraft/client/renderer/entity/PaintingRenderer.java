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
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.PaintingTextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.Motive;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.level.Level;

public class PaintingRenderer
extends EntityRenderer<Painting> {
    public PaintingRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    @Override
    public void render(Painting painting, float f, float f2, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
        poseStack.pushPose();
        poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0f - f));
        Motive motive = painting.motive;
        float f3 = 0.0625f;
        poseStack.scale(0.0625f, 0.0625f, 0.0625f);
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.entitySolid(this.getTextureLocation(painting)));
        PaintingTextureManager paintingTextureManager = Minecraft.getInstance().getPaintingTextures();
        this.renderPainting(poseStack, vertexConsumer, painting, motive.getWidth(), motive.getHeight(), paintingTextureManager.get(motive), paintingTextureManager.getBackSprite());
        poseStack.popPose();
        super.render(painting, f, f2, poseStack, multiBufferSource, n);
    }

    @Override
    public ResourceLocation getTextureLocation(Painting painting) {
        return Minecraft.getInstance().getPaintingTextures().getBackSprite().atlas().location();
    }

    private void renderPainting(PoseStack poseStack, VertexConsumer vertexConsumer, Painting painting, int n, int n2, TextureAtlasSprite textureAtlasSprite, TextureAtlasSprite textureAtlasSprite2) {
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();
        float f = (float)(-n) / 2.0f;
        float f2 = (float)(-n2) / 2.0f;
        float f3 = 0.5f;
        float f4 = textureAtlasSprite2.getU0();
        float f5 = textureAtlasSprite2.getU1();
        float f6 = textureAtlasSprite2.getV0();
        float f7 = textureAtlasSprite2.getV1();
        float f8 = textureAtlasSprite2.getU0();
        float f9 = textureAtlasSprite2.getU1();
        float f10 = textureAtlasSprite2.getV0();
        float f11 = textureAtlasSprite2.getV(1.0);
        float f12 = textureAtlasSprite2.getU0();
        float f13 = textureAtlasSprite2.getU(1.0);
        float f14 = textureAtlasSprite2.getV0();
        float f15 = textureAtlasSprite2.getV1();
        int n3 = n / 16;
        int n4 = n2 / 16;
        double d = 16.0 / (double)n3;
        double d2 = 16.0 / (double)n4;
        for (int i = 0; i < n3; ++i) {
            for (int j = 0; j < n4; ++j) {
                float f16 = f + (float)((i + 1) * 16);
                float f17 = f + (float)(i * 16);
                float f18 = f2 + (float)((j + 1) * 16);
                float f19 = f2 + (float)(j * 16);
                int n5 = Mth.floor(painting.getX());
                int n6 = Mth.floor(painting.getY() + (double)((f18 + f19) / 2.0f / 16.0f));
                int n7 = Mth.floor(painting.getZ());
                Direction direction = painting.getDirection();
                if (direction == Direction.NORTH) {
                    n5 = Mth.floor(painting.getX() + (double)((f16 + f17) / 2.0f / 16.0f));
                }
                if (direction == Direction.WEST) {
                    n7 = Mth.floor(painting.getZ() - (double)((f16 + f17) / 2.0f / 16.0f));
                }
                if (direction == Direction.SOUTH) {
                    n5 = Mth.floor(painting.getX() - (double)((f16 + f17) / 2.0f / 16.0f));
                }
                if (direction == Direction.EAST) {
                    n7 = Mth.floor(painting.getZ() + (double)((f16 + f17) / 2.0f / 16.0f));
                }
                int n8 = LevelRenderer.getLightColor(painting.level, new BlockPos(n5, n6, n7));
                float f20 = textureAtlasSprite.getU(d * (double)(n3 - i));
                float f21 = textureAtlasSprite.getU(d * (double)(n3 - (i + 1)));
                float f22 = textureAtlasSprite.getV(d2 * (double)(n4 - j));
                float f23 = textureAtlasSprite.getV(d2 * (double)(n4 - (j + 1)));
                this.vertex(matrix4f, matrix3f, vertexConsumer, f16, f19, f21, f22, -0.5f, 0, 0, -1, n8);
                this.vertex(matrix4f, matrix3f, vertexConsumer, f17, f19, f20, f22, -0.5f, 0, 0, -1, n8);
                this.vertex(matrix4f, matrix3f, vertexConsumer, f17, f18, f20, f23, -0.5f, 0, 0, -1, n8);
                this.vertex(matrix4f, matrix3f, vertexConsumer, f16, f18, f21, f23, -0.5f, 0, 0, -1, n8);
                this.vertex(matrix4f, matrix3f, vertexConsumer, f16, f18, f4, f6, 0.5f, 0, 0, 1, n8);
                this.vertex(matrix4f, matrix3f, vertexConsumer, f17, f18, f5, f6, 0.5f, 0, 0, 1, n8);
                this.vertex(matrix4f, matrix3f, vertexConsumer, f17, f19, f5, f7, 0.5f, 0, 0, 1, n8);
                this.vertex(matrix4f, matrix3f, vertexConsumer, f16, f19, f4, f7, 0.5f, 0, 0, 1, n8);
                this.vertex(matrix4f, matrix3f, vertexConsumer, f16, f18, f8, f10, -0.5f, 0, 1, 0, n8);
                this.vertex(matrix4f, matrix3f, vertexConsumer, f17, f18, f9, f10, -0.5f, 0, 1, 0, n8);
                this.vertex(matrix4f, matrix3f, vertexConsumer, f17, f18, f9, f11, 0.5f, 0, 1, 0, n8);
                this.vertex(matrix4f, matrix3f, vertexConsumer, f16, f18, f8, f11, 0.5f, 0, 1, 0, n8);
                this.vertex(matrix4f, matrix3f, vertexConsumer, f16, f19, f8, f10, 0.5f, 0, -1, 0, n8);
                this.vertex(matrix4f, matrix3f, vertexConsumer, f17, f19, f9, f10, 0.5f, 0, -1, 0, n8);
                this.vertex(matrix4f, matrix3f, vertexConsumer, f17, f19, f9, f11, -0.5f, 0, -1, 0, n8);
                this.vertex(matrix4f, matrix3f, vertexConsumer, f16, f19, f8, f11, -0.5f, 0, -1, 0, n8);
                this.vertex(matrix4f, matrix3f, vertexConsumer, f16, f18, f13, f14, 0.5f, -1, 0, 0, n8);
                this.vertex(matrix4f, matrix3f, vertexConsumer, f16, f19, f13, f15, 0.5f, -1, 0, 0, n8);
                this.vertex(matrix4f, matrix3f, vertexConsumer, f16, f19, f12, f15, -0.5f, -1, 0, 0, n8);
                this.vertex(matrix4f, matrix3f, vertexConsumer, f16, f18, f12, f14, -0.5f, -1, 0, 0, n8);
                this.vertex(matrix4f, matrix3f, vertexConsumer, f17, f18, f13, f14, -0.5f, 1, 0, 0, n8);
                this.vertex(matrix4f, matrix3f, vertexConsumer, f17, f19, f13, f15, -0.5f, 1, 0, 0, n8);
                this.vertex(matrix4f, matrix3f, vertexConsumer, f17, f19, f12, f15, 0.5f, 1, 0, 0, n8);
                this.vertex(matrix4f, matrix3f, vertexConsumer, f17, f18, f12, f14, 0.5f, 1, 0, 0, n8);
            }
        }
    }

    private void vertex(Matrix4f matrix4f, Matrix3f matrix3f, VertexConsumer vertexConsumer, float f, float f2, float f3, float f4, float f5, int n, int n2, int n3, int n4) {
        vertexConsumer.vertex(matrix4f, f, f2, f5).color(255, 255, 255, 255).uv(f3, f4).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n4).normal(matrix3f, n, n2, n3).endVertex();
    }
}

