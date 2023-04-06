/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

public class CubeMap {
    private final ResourceLocation[] images = new ResourceLocation[6];

    public CubeMap(ResourceLocation resourceLocation) {
        for (int i = 0; i < 6; ++i) {
            this.images[i] = new ResourceLocation(resourceLocation.getNamespace(), resourceLocation.getPath() + '_' + i + ".png");
        }
    }

    public void render(Minecraft minecraft, float f, float f2, float f3) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        RenderSystem.matrixMode(5889);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        RenderSystem.multMatrix(Matrix4f.perspective(85.0, (float)minecraft.getWindow().getWidth() / (float)minecraft.getWindow().getHeight(), 0.05f, 10.0f));
        RenderSystem.matrixMode(5888);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.rotatef(180.0f, 1.0f, 0.0f, 0.0f);
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        RenderSystem.defaultBlendFunc();
        int n = 2;
        for (int i = 0; i < 4; ++i) {
            RenderSystem.pushMatrix();
            float f4 = ((float)(i % 2) / 2.0f - 0.5f) / 256.0f;
            float f5 = ((float)(i / 2) / 2.0f - 0.5f) / 256.0f;
            float f6 = 0.0f;
            RenderSystem.translatef(f4, f5, 0.0f);
            RenderSystem.rotatef(f, 1.0f, 0.0f, 0.0f);
            RenderSystem.rotatef(f2, 0.0f, 1.0f, 0.0f);
            for (int j = 0; j < 6; ++j) {
                minecraft.getTextureManager().bind(this.images[j]);
                bufferBuilder.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
                int n2 = Math.round(255.0f * f3) / (i + 1);
                if (j == 0) {
                    bufferBuilder.vertex(-1.0, -1.0, 1.0).uv(0.0f, 0.0f).color(255, 255, 255, n2).endVertex();
                    bufferBuilder.vertex(-1.0, 1.0, 1.0).uv(0.0f, 1.0f).color(255, 255, 255, n2).endVertex();
                    bufferBuilder.vertex(1.0, 1.0, 1.0).uv(1.0f, 1.0f).color(255, 255, 255, n2).endVertex();
                    bufferBuilder.vertex(1.0, -1.0, 1.0).uv(1.0f, 0.0f).color(255, 255, 255, n2).endVertex();
                }
                if (j == 1) {
                    bufferBuilder.vertex(1.0, -1.0, 1.0).uv(0.0f, 0.0f).color(255, 255, 255, n2).endVertex();
                    bufferBuilder.vertex(1.0, 1.0, 1.0).uv(0.0f, 1.0f).color(255, 255, 255, n2).endVertex();
                    bufferBuilder.vertex(1.0, 1.0, -1.0).uv(1.0f, 1.0f).color(255, 255, 255, n2).endVertex();
                    bufferBuilder.vertex(1.0, -1.0, -1.0).uv(1.0f, 0.0f).color(255, 255, 255, n2).endVertex();
                }
                if (j == 2) {
                    bufferBuilder.vertex(1.0, -1.0, -1.0).uv(0.0f, 0.0f).color(255, 255, 255, n2).endVertex();
                    bufferBuilder.vertex(1.0, 1.0, -1.0).uv(0.0f, 1.0f).color(255, 255, 255, n2).endVertex();
                    bufferBuilder.vertex(-1.0, 1.0, -1.0).uv(1.0f, 1.0f).color(255, 255, 255, n2).endVertex();
                    bufferBuilder.vertex(-1.0, -1.0, -1.0).uv(1.0f, 0.0f).color(255, 255, 255, n2).endVertex();
                }
                if (j == 3) {
                    bufferBuilder.vertex(-1.0, -1.0, -1.0).uv(0.0f, 0.0f).color(255, 255, 255, n2).endVertex();
                    bufferBuilder.vertex(-1.0, 1.0, -1.0).uv(0.0f, 1.0f).color(255, 255, 255, n2).endVertex();
                    bufferBuilder.vertex(-1.0, 1.0, 1.0).uv(1.0f, 1.0f).color(255, 255, 255, n2).endVertex();
                    bufferBuilder.vertex(-1.0, -1.0, 1.0).uv(1.0f, 0.0f).color(255, 255, 255, n2).endVertex();
                }
                if (j == 4) {
                    bufferBuilder.vertex(-1.0, -1.0, -1.0).uv(0.0f, 0.0f).color(255, 255, 255, n2).endVertex();
                    bufferBuilder.vertex(-1.0, -1.0, 1.0).uv(0.0f, 1.0f).color(255, 255, 255, n2).endVertex();
                    bufferBuilder.vertex(1.0, -1.0, 1.0).uv(1.0f, 1.0f).color(255, 255, 255, n2).endVertex();
                    bufferBuilder.vertex(1.0, -1.0, -1.0).uv(1.0f, 0.0f).color(255, 255, 255, n2).endVertex();
                }
                if (j == 5) {
                    bufferBuilder.vertex(-1.0, 1.0, 1.0).uv(0.0f, 0.0f).color(255, 255, 255, n2).endVertex();
                    bufferBuilder.vertex(-1.0, 1.0, -1.0).uv(0.0f, 1.0f).color(255, 255, 255, n2).endVertex();
                    bufferBuilder.vertex(1.0, 1.0, -1.0).uv(1.0f, 1.0f).color(255, 255, 255, n2).endVertex();
                    bufferBuilder.vertex(1.0, 1.0, 1.0).uv(1.0f, 0.0f).color(255, 255, 255, n2).endVertex();
                }
                tesselator.end();
            }
            RenderSystem.popMatrix();
            RenderSystem.colorMask(true, true, true, false);
        }
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.matrixMode(5889);
        RenderSystem.popMatrix();
        RenderSystem.matrixMode(5888);
        RenderSystem.popMatrix();
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
    }

    public CompletableFuture<Void> preload(TextureManager textureManager, Executor executor) {
        CompletableFuture[] arrcompletableFuture = new CompletableFuture[6];
        for (int i = 0; i < arrcompletableFuture.length; ++i) {
            arrcompletableFuture[i] = textureManager.preload(this.images[i], executor);
        }
        return CompletableFuture.allOf(arrcompletableFuture);
    }
}

