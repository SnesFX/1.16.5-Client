/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import java.util.function.BiConsumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

public abstract class GuiComponent {
    public static final ResourceLocation BACKGROUND_LOCATION = new ResourceLocation("textures/gui/options_background.png");
    public static final ResourceLocation STATS_ICON_LOCATION = new ResourceLocation("textures/gui/container/stats_icons.png");
    public static final ResourceLocation GUI_ICONS_LOCATION = new ResourceLocation("textures/gui/icons.png");
    private int blitOffset;

    protected void hLine(PoseStack poseStack, int n, int n2, int n3, int n4) {
        if (n2 < n) {
            int n5 = n;
            n = n2;
            n2 = n5;
        }
        GuiComponent.fill(poseStack, n, n3, n2 + 1, n3 + 1, n4);
    }

    protected void vLine(PoseStack poseStack, int n, int n2, int n3, int n4) {
        if (n3 < n2) {
            int n5 = n2;
            n2 = n3;
            n3 = n5;
        }
        GuiComponent.fill(poseStack, n, n2 + 1, n + 1, n3, n4);
    }

    public static void fill(PoseStack poseStack, int n, int n2, int n3, int n4, int n5) {
        GuiComponent.innerFill(poseStack.last().pose(), n, n2, n3, n4, n5);
    }

    private static void innerFill(Matrix4f matrix4f, int n, int n2, int n3, int n4, int n5) {
        int n6;
        if (n < n3) {
            n6 = n;
            n = n3;
            n3 = n6;
        }
        if (n2 < n4) {
            n6 = n2;
            n2 = n4;
            n4 = n6;
        }
        float f = (float)(n5 >> 24 & 0xFF) / 255.0f;
        float f2 = (float)(n5 >> 16 & 0xFF) / 255.0f;
        float f3 = (float)(n5 >> 8 & 0xFF) / 255.0f;
        float f4 = (float)(n5 & 0xFF) / 255.0f;
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        bufferBuilder.begin(7, DefaultVertexFormat.POSITION_COLOR);
        bufferBuilder.vertex(matrix4f, n, n4, 0.0f).color(f2, f3, f4, f).endVertex();
        bufferBuilder.vertex(matrix4f, n3, n4, 0.0f).color(f2, f3, f4, f).endVertex();
        bufferBuilder.vertex(matrix4f, n3, n2, 0.0f).color(f2, f3, f4, f).endVertex();
        bufferBuilder.vertex(matrix4f, n, n2, 0.0f).color(f2, f3, f4, f).endVertex();
        bufferBuilder.end();
        BufferUploader.end(bufferBuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    protected void fillGradient(PoseStack poseStack, int n, int n2, int n3, int n4, int n5, int n6) {
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(7425);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        bufferBuilder.begin(7, DefaultVertexFormat.POSITION_COLOR);
        GuiComponent.fillGradient(poseStack.last().pose(), bufferBuilder, n, n2, n3, n4, this.blitOffset, n5, n6);
        tesselator.end();
        RenderSystem.shadeModel(7424);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
    }

    protected static void fillGradient(Matrix4f matrix4f, BufferBuilder bufferBuilder, int n, int n2, int n3, int n4, int n5, int n6, int n7) {
        float f = (float)(n6 >> 24 & 0xFF) / 255.0f;
        float f2 = (float)(n6 >> 16 & 0xFF) / 255.0f;
        float f3 = (float)(n6 >> 8 & 0xFF) / 255.0f;
        float f4 = (float)(n6 & 0xFF) / 255.0f;
        float f5 = (float)(n7 >> 24 & 0xFF) / 255.0f;
        float f6 = (float)(n7 >> 16 & 0xFF) / 255.0f;
        float f7 = (float)(n7 >> 8 & 0xFF) / 255.0f;
        float f8 = (float)(n7 & 0xFF) / 255.0f;
        bufferBuilder.vertex(matrix4f, n3, n2, n5).color(f2, f3, f4, f).endVertex();
        bufferBuilder.vertex(matrix4f, n, n2, n5).color(f2, f3, f4, f).endVertex();
        bufferBuilder.vertex(matrix4f, n, n4, n5).color(f6, f7, f8, f5).endVertex();
        bufferBuilder.vertex(matrix4f, n3, n4, n5).color(f6, f7, f8, f5).endVertex();
    }

    public static void drawCenteredString(PoseStack poseStack, Font font, String string, int n, int n2, int n3) {
        font.drawShadow(poseStack, string, (float)(n - font.width(string) / 2), (float)n2, n3);
    }

    public static void drawCenteredString(PoseStack poseStack, Font font, Component component, int n, int n2, int n3) {
        FormattedCharSequence formattedCharSequence = component.getVisualOrderText();
        font.drawShadow(poseStack, formattedCharSequence, (float)(n - font.width(formattedCharSequence) / 2), (float)n2, n3);
    }

    public static void drawString(PoseStack poseStack, Font font, String string, int n, int n2, int n3) {
        font.drawShadow(poseStack, string, (float)n, (float)n2, n3);
    }

    public static void drawString(PoseStack poseStack, Font font, Component component, int n, int n2, int n3) {
        font.drawShadow(poseStack, component, (float)n, (float)n2, n3);
    }

    public void blitOutlineBlack(int n, int n2, BiConsumer<Integer, Integer> biConsumer) {
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        biConsumer.accept(n + 1, n2);
        biConsumer.accept(n - 1, n2);
        biConsumer.accept(n, n2 + 1);
        biConsumer.accept(n, n2 - 1);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        biConsumer.accept(n, n2);
    }

    public static void blit(PoseStack poseStack, int n, int n2, int n3, int n4, int n5, TextureAtlasSprite textureAtlasSprite) {
        GuiComponent.innerBlit(poseStack.last().pose(), n, n + n4, n2, n2 + n5, n3, textureAtlasSprite.getU0(), textureAtlasSprite.getU1(), textureAtlasSprite.getV0(), textureAtlasSprite.getV1());
    }

    public void blit(PoseStack poseStack, int n, int n2, int n3, int n4, int n5, int n6) {
        GuiComponent.blit(poseStack, n, n2, this.blitOffset, n3, n4, n5, n6, 256, 256);
    }

    public static void blit(PoseStack poseStack, int n, int n2, int n3, float f, float f2, int n4, int n5, int n6, int n7) {
        GuiComponent.innerBlit(poseStack, n, n + n4, n2, n2 + n5, n3, n4, n5, f, f2, n7, n6);
    }

    public static void blit(PoseStack poseStack, int n, int n2, int n3, int n4, float f, float f2, int n5, int n6, int n7, int n8) {
        GuiComponent.innerBlit(poseStack, n, n + n3, n2, n2 + n4, 0, n5, n6, f, f2, n7, n8);
    }

    public static void blit(PoseStack poseStack, int n, int n2, float f, float f2, int n3, int n4, int n5, int n6) {
        GuiComponent.blit(poseStack, n, n2, n3, n4, f, f2, n3, n4, n5, n6);
    }

    private static void innerBlit(PoseStack poseStack, int n, int n2, int n3, int n4, int n5, int n6, int n7, float f, float f2, int n8, int n9) {
        GuiComponent.innerBlit(poseStack.last().pose(), n, n2, n3, n4, n5, (f + 0.0f) / (float)n8, (f + (float)n6) / (float)n8, (f2 + 0.0f) / (float)n9, (f2 + (float)n7) / (float)n9);
    }

    private static void innerBlit(Matrix4f matrix4f, int n, int n2, int n3, int n4, int n5, float f, float f2, float f3, float f4) {
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        bufferBuilder.begin(7, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.vertex(matrix4f, n, n4, n5).uv(f, f4).endVertex();
        bufferBuilder.vertex(matrix4f, n2, n4, n5).uv(f2, f4).endVertex();
        bufferBuilder.vertex(matrix4f, n2, n3, n5).uv(f2, f3).endVertex();
        bufferBuilder.vertex(matrix4f, n, n3, n5).uv(f, f3).endVertex();
        bufferBuilder.end();
        RenderSystem.enableAlphaTest();
        BufferUploader.end(bufferBuilder);
    }

    public int getBlitOffset() {
        return this.blitOffset;
    }

    public void setBlitOffset(int n) {
        this.blitOffset = n;
    }
}

