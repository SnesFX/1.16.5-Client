/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import java.util.Random;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;

public class LightningBoltRenderer
extends EntityRenderer<LightningBolt> {
    public LightningBoltRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    @Override
    public void render(LightningBolt lightningBolt, float f, float f2, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
        float[] arrf = new float[8];
        float[] arrf2 = new float[8];
        float f3 = 0.0f;
        float f4 = 0.0f;
        Object object = new Random(lightningBolt.seed);
        for (int i = 7; i >= 0; --i) {
            arrf[i] = f3;
            arrf2[i] = f4;
            f3 += (float)(((Random)object).nextInt(11) - 5);
            f4 += (float)(((Random)object).nextInt(11) - 5);
        }
        object = multiBufferSource.getBuffer(RenderType.lightning());
        Matrix4f matrix4f = poseStack.last().pose();
        for (int i = 0; i < 4; ++i) {
            Random random = new Random(lightningBolt.seed);
            for (int j = 0; j < 3; ++j) {
                int n2 = 7;
                int n3 = 0;
                if (j > 0) {
                    n2 = 7 - j;
                }
                if (j > 0) {
                    n3 = n2 - 2;
                }
                float f5 = arrf[n2] - f3;
                float f6 = arrf2[n2] - f4;
                for (int k = n2; k >= n3; --k) {
                    float f7 = f5;
                    float f8 = f6;
                    if (j == 0) {
                        f5 += (float)(random.nextInt(11) - 5);
                        f6 += (float)(random.nextInt(11) - 5);
                    } else {
                        f5 += (float)(random.nextInt(31) - 15);
                        f6 += (float)(random.nextInt(31) - 15);
                    }
                    float f9 = 0.5f;
                    float f10 = 0.45f;
                    float f11 = 0.45f;
                    float f12 = 0.5f;
                    float f13 = 0.1f + (float)i * 0.2f;
                    if (j == 0) {
                        f13 = (float)((double)f13 * ((double)k * 0.1 + 1.0));
                    }
                    float f14 = 0.1f + (float)i * 0.2f;
                    if (j == 0) {
                        f14 *= (float)(k - 1) * 0.1f + 1.0f;
                    }
                    LightningBoltRenderer.quad(matrix4f, (VertexConsumer)object, f5, f6, k, f7, f8, 0.45f, 0.45f, 0.5f, f13, f14, false, false, true, false);
                    LightningBoltRenderer.quad(matrix4f, (VertexConsumer)object, f5, f6, k, f7, f8, 0.45f, 0.45f, 0.5f, f13, f14, true, false, true, true);
                    LightningBoltRenderer.quad(matrix4f, (VertexConsumer)object, f5, f6, k, f7, f8, 0.45f, 0.45f, 0.5f, f13, f14, true, true, false, true);
                    LightningBoltRenderer.quad(matrix4f, (VertexConsumer)object, f5, f6, k, f7, f8, 0.45f, 0.45f, 0.5f, f13, f14, false, true, false, false);
                }
            }
        }
    }

    private static void quad(Matrix4f matrix4f, VertexConsumer vertexConsumer, float f, float f2, int n, float f3, float f4, float f5, float f6, float f7, float f8, float f9, boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        vertexConsumer.vertex(matrix4f, f + (bl ? f9 : -f9), n * 16, f2 + (bl2 ? f9 : -f9)).color(f5, f6, f7, 0.3f).endVertex();
        vertexConsumer.vertex(matrix4f, f3 + (bl ? f8 : -f8), (n + 1) * 16, f4 + (bl2 ? f8 : -f8)).color(f5, f6, f7, 0.3f).endVertex();
        vertexConsumer.vertex(matrix4f, f3 + (bl3 ? f8 : -f8), (n + 1) * 16, f4 + (bl4 ? f8 : -f8)).color(f5, f6, f7, 0.3f).endVertex();
        vertexConsumer.vertex(matrix4f, f + (bl3 ? f9 : -f9), n * 16, f2 + (bl4 ? f9 : -f9)).color(f5, f6, f7, 0.3f).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(LightningBolt lightningBolt) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}

