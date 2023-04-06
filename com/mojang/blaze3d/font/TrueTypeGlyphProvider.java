/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntArraySet
 *  it.unimi.dsi.fastutil.ints.IntCollection
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  javax.annotation.Nullable
 *  org.lwjgl.stb.STBTTFontinfo
 *  org.lwjgl.stb.STBTruetype
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 */
package com.mojang.blaze3d.font;

import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.RawGlyph;
import com.mojang.blaze3d.platform.NativeImage;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.function.BiConsumer;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;
import java.util.function.ObjIntConsumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class TrueTypeGlyphProvider
implements GlyphProvider {
    private final ByteBuffer fontMemory;
    private final STBTTFontinfo font;
    private final float oversample;
    private final IntSet skip = new IntArraySet();
    private final float shiftX;
    private final float shiftY;
    private final float pointScale;
    private final float ascent;

    public TrueTypeGlyphProvider(ByteBuffer byteBuffer, STBTTFontinfo sTBTTFontinfo, float f, float f2, float f3, float f4, String string) {
        this.fontMemory = byteBuffer;
        this.font = sTBTTFontinfo;
        this.oversample = f2;
        string.codePoints().forEach(((IntSet)this.skip)::add);
        this.shiftX = f3 * f2;
        this.shiftY = f4 * f2;
        this.pointScale = STBTruetype.stbtt_ScaleForPixelHeight((STBTTFontinfo)sTBTTFontinfo, (float)(f * f2));
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            IntBuffer intBuffer = memoryStack.mallocInt(1);
            IntBuffer intBuffer2 = memoryStack.mallocInt(1);
            IntBuffer intBuffer3 = memoryStack.mallocInt(1);
            STBTruetype.stbtt_GetFontVMetrics((STBTTFontinfo)sTBTTFontinfo, (IntBuffer)intBuffer, (IntBuffer)intBuffer2, (IntBuffer)intBuffer3);
            this.ascent = (float)intBuffer.get(0) * this.pointScale;
        }
    }

    @Nullable
    @Override
    public Glyph getGlyph(int n) {
        if (this.skip.contains(n)) {
            return null;
        }
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            IntBuffer intBuffer = memoryStack.mallocInt(1);
            IntBuffer intBuffer2 = memoryStack.mallocInt(1);
            IntBuffer intBuffer3 = memoryStack.mallocInt(1);
            IntBuffer intBuffer4 = memoryStack.mallocInt(1);
            int n2 = STBTruetype.stbtt_FindGlyphIndex((STBTTFontinfo)this.font, (int)n);
            if (n2 == 0) {
                Glyph glyph = null;
                return glyph;
            }
            STBTruetype.stbtt_GetGlyphBitmapBoxSubpixel((STBTTFontinfo)this.font, (int)n2, (float)this.pointScale, (float)this.pointScale, (float)this.shiftX, (float)this.shiftY, (IntBuffer)intBuffer, (IntBuffer)intBuffer2, (IntBuffer)intBuffer3, (IntBuffer)intBuffer4);
            int n3 = intBuffer3.get(0) - intBuffer.get(0);
            int n4 = intBuffer4.get(0) - intBuffer2.get(0);
            if (n3 == 0 || n4 == 0) {
                Glyph glyph = null;
                return glyph;
            }
            IntBuffer intBuffer5 = memoryStack.mallocInt(1);
            IntBuffer intBuffer6 = memoryStack.mallocInt(1);
            STBTruetype.stbtt_GetGlyphHMetrics((STBTTFontinfo)this.font, (int)n2, (IntBuffer)intBuffer5, (IntBuffer)intBuffer6);
            Glyph glyph = new Glyph(intBuffer.get(0), intBuffer3.get(0), -intBuffer2.get(0), -intBuffer4.get(0), (float)intBuffer5.get(0) * this.pointScale, (float)intBuffer6.get(0) * this.pointScale, n2);
            return glyph;
        }
    }

    @Override
    public void close() {
        this.font.free();
        MemoryUtil.memFree((Buffer)this.fontMemory);
    }

    @Override
    public IntSet getSupportedGlyphs() {
        return (IntSet)IntStream.range(0, 65535).filter(n -> !this.skip.contains(n)).collect(IntOpenHashSet::new, IntCollection::add, IntCollection::addAll);
    }

    @Nullable
    @Override
    public /* synthetic */ RawGlyph getGlyph(int n) {
        return this.getGlyph(n);
    }

    class Glyph
    implements RawGlyph {
        private final int width;
        private final int height;
        private final float bearingX;
        private final float bearingY;
        private final float advance;
        private final int index;

        private Glyph(int n, int n2, int n3, int n4, float f, float f2, int n5) {
            this.width = n2 - n;
            this.height = n3 - n4;
            this.advance = f / TrueTypeGlyphProvider.this.oversample;
            this.bearingX = (f2 + (float)n + TrueTypeGlyphProvider.this.shiftX) / TrueTypeGlyphProvider.this.oversample;
            this.bearingY = (TrueTypeGlyphProvider.this.ascent - (float)n3 + TrueTypeGlyphProvider.this.shiftY) / TrueTypeGlyphProvider.this.oversample;
            this.index = n5;
        }

        @Override
        public int getPixelWidth() {
            return this.width;
        }

        @Override
        public int getPixelHeight() {
            return this.height;
        }

        @Override
        public float getOversample() {
            return TrueTypeGlyphProvider.this.oversample;
        }

        @Override
        public float getAdvance() {
            return this.advance;
        }

        @Override
        public float getBearingX() {
            return this.bearingX;
        }

        @Override
        public float getBearingY() {
            return this.bearingY;
        }

        @Override
        public void upload(int n, int n2) {
            NativeImage nativeImage = new NativeImage(NativeImage.Format.LUMINANCE, this.width, this.height, false);
            nativeImage.copyFromFont(TrueTypeGlyphProvider.this.font, this.index, this.width, this.height, TrueTypeGlyphProvider.this.pointScale, TrueTypeGlyphProvider.this.pointScale, TrueTypeGlyphProvider.this.shiftX, TrueTypeGlyphProvider.this.shiftY, 0, 0);
            nativeImage.upload(0, n, n2, 0, 0, this.width, this.height, false, true);
        }

        @Override
        public boolean isColored() {
            return false;
        }
    }

}

