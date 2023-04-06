/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.ibm.icu.text.ArabicShaping
 *  com.ibm.icu.text.ArabicShapingException
 *  com.ibm.icu.text.Bidi
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.font.glyphs.EmptyGlyph;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.FormattedCharSink;
import net.minecraft.util.Mth;
import net.minecraft.util.StringDecomposer;

public class Font {
    private static final Vector3f SHADOW_OFFSET = new Vector3f(0.0f, 0.0f, 0.03f);
    public final int lineHeight = 9;
    public final Random random = new Random();
    private final Function<ResourceLocation, FontSet> fonts;
    private final StringSplitter splitter;

    public Font(Function<ResourceLocation, FontSet> function) {
        this.fonts = function;
        this.splitter = new StringSplitter((n, style) -> this.getFontSet(style.getFont()).getGlyphInfo(n).getAdvance(style.isBold()));
    }

    private FontSet getFontSet(ResourceLocation resourceLocation) {
        return this.fonts.apply(resourceLocation);
    }

    public int drawShadow(PoseStack poseStack, String string, float f, float f2, int n) {
        return this.drawInternal(string, f, f2, n, poseStack.last().pose(), true, this.isBidirectional());
    }

    public int drawShadow(PoseStack poseStack, String string, float f, float f2, int n, boolean bl) {
        RenderSystem.enableAlphaTest();
        return this.drawInternal(string, f, f2, n, poseStack.last().pose(), true, bl);
    }

    public int draw(PoseStack poseStack, String string, float f, float f2, int n) {
        RenderSystem.enableAlphaTest();
        return this.drawInternal(string, f, f2, n, poseStack.last().pose(), false, this.isBidirectional());
    }

    public int drawShadow(PoseStack poseStack, FormattedCharSequence formattedCharSequence, float f, float f2, int n) {
        RenderSystem.enableAlphaTest();
        return this.drawInternal(formattedCharSequence, f, f2, n, poseStack.last().pose(), true);
    }

    public int drawShadow(PoseStack poseStack, Component component, float f, float f2, int n) {
        RenderSystem.enableAlphaTest();
        return this.drawInternal(component.getVisualOrderText(), f, f2, n, poseStack.last().pose(), true);
    }

    public int draw(PoseStack poseStack, FormattedCharSequence formattedCharSequence, float f, float f2, int n) {
        RenderSystem.enableAlphaTest();
        return this.drawInternal(formattedCharSequence, f, f2, n, poseStack.last().pose(), false);
    }

    public int draw(PoseStack poseStack, Component component, float f, float f2, int n) {
        RenderSystem.enableAlphaTest();
        return this.drawInternal(component.getVisualOrderText(), f, f2, n, poseStack.last().pose(), false);
    }

    public String bidirectionalShaping(String string) {
        try {
            Bidi bidi = new Bidi(new ArabicShaping(8).shape(string), 127);
            bidi.setReorderingMode(0);
            return bidi.writeReordered(2);
        }
        catch (ArabicShapingException arabicShapingException) {
            return string;
        }
    }

    private int drawInternal(String string, float f, float f2, int n, Matrix4f matrix4f, boolean bl, boolean bl2) {
        if (string == null) {
            return 0;
        }
        MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        int n2 = this.drawInBatch(string, f, f2, n, bl, matrix4f, bufferSource, false, 0, 15728880, bl2);
        bufferSource.endBatch();
        return n2;
    }

    private int drawInternal(FormattedCharSequence formattedCharSequence, float f, float f2, int n, Matrix4f matrix4f, boolean bl) {
        MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        int n2 = this.drawInBatch(formattedCharSequence, f, f2, n, bl, matrix4f, (MultiBufferSource)bufferSource, false, 0, 15728880);
        bufferSource.endBatch();
        return n2;
    }

    public int drawInBatch(String string, float f, float f2, int n, boolean bl, Matrix4f matrix4f, MultiBufferSource multiBufferSource, boolean bl2, int n2, int n3) {
        return this.drawInBatch(string, f, f2, n, bl, matrix4f, multiBufferSource, bl2, n2, n3, this.isBidirectional());
    }

    public int drawInBatch(String string, float f, float f2, int n, boolean bl, Matrix4f matrix4f, MultiBufferSource multiBufferSource, boolean bl2, int n2, int n3, boolean bl3) {
        return this.drawInternal(string, f, f2, n, bl, matrix4f, multiBufferSource, bl2, n2, n3, bl3);
    }

    public int drawInBatch(Component component, float f, float f2, int n, boolean bl, Matrix4f matrix4f, MultiBufferSource multiBufferSource, boolean bl2, int n2, int n3) {
        return this.drawInBatch(component.getVisualOrderText(), f, f2, n, bl, matrix4f, multiBufferSource, bl2, n2, n3);
    }

    public int drawInBatch(FormattedCharSequence formattedCharSequence, float f, float f2, int n, boolean bl, Matrix4f matrix4f, MultiBufferSource multiBufferSource, boolean bl2, int n2, int n3) {
        return this.drawInternal(formattedCharSequence, f, f2, n, bl, matrix4f, multiBufferSource, bl2, n2, n3);
    }

    private static int adjustColor(int n) {
        if ((n & 0xFC000000) == 0) {
            return n | 0xFF000000;
        }
        return n;
    }

    private int drawInternal(String string, float f, float f2, int n, boolean bl, Matrix4f matrix4f, MultiBufferSource multiBufferSource, boolean bl2, int n2, int n3, boolean bl3) {
        if (bl3) {
            string = this.bidirectionalShaping(string);
        }
        n = Font.adjustColor(n);
        Matrix4f matrix4f2 = matrix4f.copy();
        if (bl) {
            this.renderText(string, f, f2, n, true, matrix4f, multiBufferSource, bl2, n2, n3);
            matrix4f2.translate(SHADOW_OFFSET);
        }
        f = this.renderText(string, f, f2, n, false, matrix4f2, multiBufferSource, bl2, n2, n3);
        return (int)f + (bl ? 1 : 0);
    }

    private int drawInternal(FormattedCharSequence formattedCharSequence, float f, float f2, int n, boolean bl, Matrix4f matrix4f, MultiBufferSource multiBufferSource, boolean bl2, int n2, int n3) {
        n = Font.adjustColor(n);
        Matrix4f matrix4f2 = matrix4f.copy();
        if (bl) {
            this.renderText(formattedCharSequence, f, f2, n, true, matrix4f, multiBufferSource, bl2, n2, n3);
            matrix4f2.translate(SHADOW_OFFSET);
        }
        f = this.renderText(formattedCharSequence, f, f2, n, false, matrix4f2, multiBufferSource, bl2, n2, n3);
        return (int)f + (bl ? 1 : 0);
    }

    private float renderText(String string, float f, float f2, int n, boolean bl, Matrix4f matrix4f, MultiBufferSource multiBufferSource, boolean bl2, int n2, int n3) {
        StringRenderOutput stringRenderOutput = new StringRenderOutput(multiBufferSource, f, f2, n, bl, matrix4f, bl2, n3);
        StringDecomposer.iterateFormatted(string, Style.EMPTY, (FormattedCharSink)stringRenderOutput);
        return stringRenderOutput.finish(n2, f);
    }

    private float renderText(FormattedCharSequence formattedCharSequence, float f, float f2, int n, boolean bl, Matrix4f matrix4f, MultiBufferSource multiBufferSource, boolean bl2, int n2, int n3) {
        StringRenderOutput stringRenderOutput = new StringRenderOutput(multiBufferSource, f, f2, n, bl, matrix4f, bl2, n3);
        formattedCharSequence.accept(stringRenderOutput);
        return stringRenderOutput.finish(n2, f);
    }

    private void renderChar(BakedGlyph bakedGlyph, boolean bl, boolean bl2, float f, float f2, float f3, Matrix4f matrix4f, VertexConsumer vertexConsumer, float f4, float f5, float f6, float f7, int n) {
        bakedGlyph.render(bl2, f2, f3, matrix4f, vertexConsumer, f4, f5, f6, f7, n);
        if (bl) {
            bakedGlyph.render(bl2, f2 + f, f3, matrix4f, vertexConsumer, f4, f5, f6, f7, n);
        }
    }

    public int width(String string) {
        return Mth.ceil(this.splitter.stringWidth(string));
    }

    public int width(FormattedText formattedText) {
        return Mth.ceil(this.splitter.stringWidth(formattedText));
    }

    public int width(FormattedCharSequence formattedCharSequence) {
        return Mth.ceil(this.splitter.stringWidth(formattedCharSequence));
    }

    public String plainSubstrByWidth(String string, int n, boolean bl) {
        return bl ? this.splitter.plainTailByWidth(string, n, Style.EMPTY) : this.splitter.plainHeadByWidth(string, n, Style.EMPTY);
    }

    public String plainSubstrByWidth(String string, int n) {
        return this.splitter.plainHeadByWidth(string, n, Style.EMPTY);
    }

    public FormattedText substrByWidth(FormattedText formattedText, int n) {
        return this.splitter.headByWidth(formattedText, n, Style.EMPTY);
    }

    public void drawWordWrap(FormattedText formattedText, int n, int n2, int n3, int n4) {
        Matrix4f matrix4f = Transformation.identity().getMatrix();
        for (FormattedCharSequence formattedCharSequence : this.split(formattedText, n3)) {
            this.drawInternal(formattedCharSequence, n, n2, n4, matrix4f, false);
            n2 += 9;
        }
    }

    public int wordWrapHeight(String string, int n) {
        return 9 * this.splitter.splitLines(string, n, Style.EMPTY).size();
    }

    public List<FormattedCharSequence> split(FormattedText formattedText, int n) {
        return Language.getInstance().getVisualOrder(this.splitter.splitLines(formattedText, n, Style.EMPTY));
    }

    public boolean isBidirectional() {
        return Language.getInstance().isDefaultRightToLeft();
    }

    public StringSplitter getSplitter() {
        return this.splitter;
    }

    class StringRenderOutput
    implements FormattedCharSink {
        final MultiBufferSource bufferSource;
        private final boolean dropShadow;
        private final float dimFactor;
        private final float r;
        private final float g;
        private final float b;
        private final float a;
        private final Matrix4f pose;
        private final boolean seeThrough;
        private final int packedLightCoords;
        private float x;
        private float y;
        @Nullable
        private List<BakedGlyph.Effect> effects;

        private void addEffect(BakedGlyph.Effect effect) {
            if (this.effects == null) {
                this.effects = Lists.newArrayList();
            }
            this.effects.add(effect);
        }

        public StringRenderOutput(MultiBufferSource multiBufferSource, float f, float f2, int n, boolean bl, Matrix4f matrix4f, boolean bl2, int n2) {
            this.bufferSource = multiBufferSource;
            this.x = f;
            this.y = f2;
            this.dropShadow = bl;
            this.dimFactor = bl ? 0.25f : 1.0f;
            this.r = (float)(n >> 16 & 0xFF) / 255.0f * this.dimFactor;
            this.g = (float)(n >> 8 & 0xFF) / 255.0f * this.dimFactor;
            this.b = (float)(n & 0xFF) / 255.0f * this.dimFactor;
            this.a = (float)(n >> 24 & 0xFF) / 255.0f;
            this.pose = matrix4f;
            this.seeThrough = bl2;
            this.packedLightCoords = n2;
        }

        @Override
        public boolean accept(int n, Style style, int n2) {
            float f;
            float f2;
            float f3;
            float f4;
            FontSet fontSet = Font.this.getFontSet(style.getFont());
            GlyphInfo glyphInfo = fontSet.getGlyphInfo(n2);
            BakedGlyph bakedGlyph = style.isObfuscated() && n2 != 32 ? fontSet.getRandomGlyph(glyphInfo) : fontSet.getGlyph(n2);
            boolean bl = style.isBold();
            float f5 = this.a;
            TextColor textColor = style.getColor();
            if (textColor != null) {
                int n3 = textColor.getValue();
                f2 = (float)(n3 >> 16 & 0xFF) / 255.0f * this.dimFactor;
                f3 = (float)(n3 >> 8 & 0xFF) / 255.0f * this.dimFactor;
                f = (float)(n3 & 0xFF) / 255.0f * this.dimFactor;
            } else {
                f2 = this.r;
                f3 = this.g;
                f = this.b;
            }
            if (!(bakedGlyph instanceof EmptyGlyph)) {
                float f6 = bl ? glyphInfo.getBoldOffset() : 0.0f;
                f4 = this.dropShadow ? glyphInfo.getShadowOffset() : 0.0f;
                VertexConsumer vertexConsumer = this.bufferSource.getBuffer(bakedGlyph.renderType(this.seeThrough));
                Font.this.renderChar(bakedGlyph, bl, style.isItalic(), f6, this.x + f4, this.y + f4, this.pose, vertexConsumer, f2, f3, f, f5, this.packedLightCoords);
            }
            float f7 = glyphInfo.getAdvance(bl);
            float f8 = f4 = this.dropShadow ? 1.0f : 0.0f;
            if (style.isStrikethrough()) {
                this.addEffect(new BakedGlyph.Effect(this.x + f4 - 1.0f, this.y + f4 + 4.5f, this.x + f4 + f7, this.y + f4 + 4.5f - 1.0f, 0.01f, f2, f3, f, f5));
            }
            if (style.isUnderlined()) {
                this.addEffect(new BakedGlyph.Effect(this.x + f4 - 1.0f, this.y + f4 + 9.0f, this.x + f4 + f7, this.y + f4 + 9.0f - 1.0f, 0.01f, f2, f3, f, f5));
            }
            this.x += f7;
            return true;
        }

        public float finish(int n, float f) {
            if (n != 0) {
                float f2 = (float)(n >> 24 & 0xFF) / 255.0f;
                float f3 = (float)(n >> 16 & 0xFF) / 255.0f;
                float f4 = (float)(n >> 8 & 0xFF) / 255.0f;
                float f5 = (float)(n & 0xFF) / 255.0f;
                this.addEffect(new BakedGlyph.Effect(f - 1.0f, this.y + 9.0f, this.x + 1.0f, this.y - 1.0f, 0.01f, f3, f4, f5, f2));
            }
            if (this.effects != null) {
                BakedGlyph bakedGlyph = Font.this.getFontSet(Style.DEFAULT_FONT).whiteGlyph();
                VertexConsumer vertexConsumer = this.bufferSource.getBuffer(bakedGlyph.renderType(this.seeThrough));
                for (BakedGlyph.Effect effect : this.effects) {
                    bakedGlyph.renderEffect(effect, this.pose, vertexConsumer, this.packedLightCoords);
                }
            }
            return this.x;
        }
    }

}

