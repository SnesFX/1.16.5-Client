/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.world.entity.Entity;

public class ChunkBorderRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;

    public ChunkBorderRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, double d, double d2, double d3) {
        int n;
        RenderSystem.enableDepthTest();
        RenderSystem.shadeModel(7425);
        RenderSystem.enableAlphaTest();
        RenderSystem.defaultAlphaFunc();
        Entity entity = this.minecraft.gameRenderer.getMainCamera().getEntity();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        double d4 = 0.0 - d2;
        double d5 = 256.0 - d2;
        RenderSystem.disableTexture();
        RenderSystem.disableBlend();
        double d6 = (double)(entity.xChunk << 4) - d;
        double d7 = (double)(entity.zChunk << 4) - d3;
        RenderSystem.lineWidth(1.0f);
        bufferBuilder.begin(3, DefaultVertexFormat.POSITION_COLOR);
        for (n = -16; n <= 32; n += 16) {
            for (int i = -16; i <= 32; i += 16) {
                bufferBuilder.vertex(d6 + (double)n, d4, d7 + (double)i).color(1.0f, 0.0f, 0.0f, 0.0f).endVertex();
                bufferBuilder.vertex(d6 + (double)n, d4, d7 + (double)i).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                bufferBuilder.vertex(d6 + (double)n, d5, d7 + (double)i).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                bufferBuilder.vertex(d6 + (double)n, d5, d7 + (double)i).color(1.0f, 0.0f, 0.0f, 0.0f).endVertex();
            }
        }
        for (n = 2; n < 16; n += 2) {
            bufferBuilder.vertex(d6 + (double)n, d4, d7).color(1.0f, 1.0f, 0.0f, 0.0f).endVertex();
            bufferBuilder.vertex(d6 + (double)n, d4, d7).color(1.0f, 1.0f, 0.0f, 1.0f).endVertex();
            bufferBuilder.vertex(d6 + (double)n, d5, d7).color(1.0f, 1.0f, 0.0f, 1.0f).endVertex();
            bufferBuilder.vertex(d6 + (double)n, d5, d7).color(1.0f, 1.0f, 0.0f, 0.0f).endVertex();
            bufferBuilder.vertex(d6 + (double)n, d4, d7 + 16.0).color(1.0f, 1.0f, 0.0f, 0.0f).endVertex();
            bufferBuilder.vertex(d6 + (double)n, d4, d7 + 16.0).color(1.0f, 1.0f, 0.0f, 1.0f).endVertex();
            bufferBuilder.vertex(d6 + (double)n, d5, d7 + 16.0).color(1.0f, 1.0f, 0.0f, 1.0f).endVertex();
            bufferBuilder.vertex(d6 + (double)n, d5, d7 + 16.0).color(1.0f, 1.0f, 0.0f, 0.0f).endVertex();
        }
        for (n = 2; n < 16; n += 2) {
            bufferBuilder.vertex(d6, d4, d7 + (double)n).color(1.0f, 1.0f, 0.0f, 0.0f).endVertex();
            bufferBuilder.vertex(d6, d4, d7 + (double)n).color(1.0f, 1.0f, 0.0f, 1.0f).endVertex();
            bufferBuilder.vertex(d6, d5, d7 + (double)n).color(1.0f, 1.0f, 0.0f, 1.0f).endVertex();
            bufferBuilder.vertex(d6, d5, d7 + (double)n).color(1.0f, 1.0f, 0.0f, 0.0f).endVertex();
            bufferBuilder.vertex(d6 + 16.0, d4, d7 + (double)n).color(1.0f, 1.0f, 0.0f, 0.0f).endVertex();
            bufferBuilder.vertex(d6 + 16.0, d4, d7 + (double)n).color(1.0f, 1.0f, 0.0f, 1.0f).endVertex();
            bufferBuilder.vertex(d6 + 16.0, d5, d7 + (double)n).color(1.0f, 1.0f, 0.0f, 1.0f).endVertex();
            bufferBuilder.vertex(d6 + 16.0, d5, d7 + (double)n).color(1.0f, 1.0f, 0.0f, 0.0f).endVertex();
        }
        for (n = 0; n <= 256; n += 2) {
            double d8 = (double)n - d2;
            bufferBuilder.vertex(d6, d8, d7).color(1.0f, 1.0f, 0.0f, 0.0f).endVertex();
            bufferBuilder.vertex(d6, d8, d7).color(1.0f, 1.0f, 0.0f, 1.0f).endVertex();
            bufferBuilder.vertex(d6, d8, d7 + 16.0).color(1.0f, 1.0f, 0.0f, 1.0f).endVertex();
            bufferBuilder.vertex(d6 + 16.0, d8, d7 + 16.0).color(1.0f, 1.0f, 0.0f, 1.0f).endVertex();
            bufferBuilder.vertex(d6 + 16.0, d8, d7).color(1.0f, 1.0f, 0.0f, 1.0f).endVertex();
            bufferBuilder.vertex(d6, d8, d7).color(1.0f, 1.0f, 0.0f, 1.0f).endVertex();
            bufferBuilder.vertex(d6, d8, d7).color(1.0f, 1.0f, 0.0f, 0.0f).endVertex();
        }
        tesselator.end();
        RenderSystem.lineWidth(2.0f);
        bufferBuilder.begin(3, DefaultVertexFormat.POSITION_COLOR);
        for (n = 0; n <= 16; n += 16) {
            for (int i = 0; i <= 16; i += 16) {
                bufferBuilder.vertex(d6 + (double)n, d4, d7 + (double)i).color(0.25f, 0.25f, 1.0f, 0.0f).endVertex();
                bufferBuilder.vertex(d6 + (double)n, d4, d7 + (double)i).color(0.25f, 0.25f, 1.0f, 1.0f).endVertex();
                bufferBuilder.vertex(d6 + (double)n, d5, d7 + (double)i).color(0.25f, 0.25f, 1.0f, 1.0f).endVertex();
                bufferBuilder.vertex(d6 + (double)n, d5, d7 + (double)i).color(0.25f, 0.25f, 1.0f, 0.0f).endVertex();
            }
        }
        for (n = 0; n <= 256; n += 16) {
            double d9 = (double)n - d2;
            bufferBuilder.vertex(d6, d9, d7).color(0.25f, 0.25f, 1.0f, 0.0f).endVertex();
            bufferBuilder.vertex(d6, d9, d7).color(0.25f, 0.25f, 1.0f, 1.0f).endVertex();
            bufferBuilder.vertex(d6, d9, d7 + 16.0).color(0.25f, 0.25f, 1.0f, 1.0f).endVertex();
            bufferBuilder.vertex(d6 + 16.0, d9, d7 + 16.0).color(0.25f, 0.25f, 1.0f, 1.0f).endVertex();
            bufferBuilder.vertex(d6 + 16.0, d9, d7).color(0.25f, 0.25f, 1.0f, 1.0f).endVertex();
            bufferBuilder.vertex(d6, d9, d7).color(0.25f, 0.25f, 1.0f, 1.0f).endVertex();
            bufferBuilder.vertex(d6, d9, d7).color(0.25f, 0.25f, 1.0f, 0.0f).endVertex();
        }
        tesselator.end();
        RenderSystem.lineWidth(1.0f);
        RenderSystem.enableBlend();
        RenderSystem.enableTexture();
        RenderSystem.shadeModel(7424);
    }
}

