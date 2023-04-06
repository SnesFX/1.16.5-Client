/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  javax.annotation.Nullable
 *  org.apache.commons.io.IOUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.lwjgl.stb.STBIWriteCallback
 *  org.lwjgl.stb.STBImage
 *  org.lwjgl.stb.STBImageResize
 *  org.lwjgl.stb.STBImageWrite
 *  org.lwjgl.stb.STBTTFontinfo
 *  org.lwjgl.stb.STBTruetype
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 */
package com.mojang.blaze3d.platform;

import com.google.common.base.Charsets;
import com.mojang.blaze3d.platform.DebugMemoryUntracker;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Base64;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.stb.STBIWriteCallback;
import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBImageResize;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public final class NativeImage
implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Set<StandardOpenOption> OPEN_OPTIONS = EnumSet.of(StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    private final Format format;
    private final int width;
    private final int height;
    private final boolean useStbFree;
    private long pixels;
    private final long size;

    public NativeImage(int n, int n2, boolean bl) {
        this(Format.RGBA, n, n2, bl);
    }

    public NativeImage(Format format, int n, int n2, boolean bl) {
        this.format = format;
        this.width = n;
        this.height = n2;
        this.size = (long)n * (long)n2 * (long)format.components();
        this.useStbFree = false;
        this.pixels = bl ? MemoryUtil.nmemCalloc((long)1L, (long)this.size) : MemoryUtil.nmemAlloc((long)this.size);
    }

    private NativeImage(Format format, int n, int n2, boolean bl, long l) {
        this.format = format;
        this.width = n;
        this.height = n2;
        this.useStbFree = bl;
        this.pixels = l;
        this.size = n * n2 * format.components();
    }

    public String toString() {
        return "NativeImage[" + (Object)((Object)this.format) + " " + this.width + "x" + this.height + "@" + this.pixels + (this.useStbFree ? "S" : "N") + "]";
    }

    public static NativeImage read(InputStream inputStream) throws IOException {
        return NativeImage.read(Format.RGBA, inputStream);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static NativeImage read(@Nullable Format format, InputStream inputStream) throws IOException {
        ByteBuffer byteBuffer = null;
        try {
            byteBuffer = TextureUtil.readResource(inputStream);
            byteBuffer.rewind();
            NativeImage nativeImage = NativeImage.read(format, byteBuffer);
            return nativeImage;
        }
        finally {
            MemoryUtil.memFree((Buffer)byteBuffer);
            IOUtils.closeQuietly((InputStream)inputStream);
        }
    }

    public static NativeImage read(ByteBuffer byteBuffer) throws IOException {
        return NativeImage.read(Format.RGBA, byteBuffer);
    }

    public static NativeImage read(@Nullable Format format, ByteBuffer byteBuffer) throws IOException {
        if (format != null && !format.supportedByStb()) {
            throw new UnsupportedOperationException("Don't know how to read format " + (Object)((Object)format));
        }
        if (MemoryUtil.memAddress((ByteBuffer)byteBuffer) == 0L) {
            throw new IllegalArgumentException("Invalid buffer");
        }
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            IntBuffer intBuffer = memoryStack.mallocInt(1);
            IntBuffer intBuffer2 = memoryStack.mallocInt(1);
            IntBuffer intBuffer3 = memoryStack.mallocInt(1);
            ByteBuffer byteBuffer2 = STBImage.stbi_load_from_memory((ByteBuffer)byteBuffer, (IntBuffer)intBuffer, (IntBuffer)intBuffer2, (IntBuffer)intBuffer3, (int)(format == null ? 0 : format.components));
            if (byteBuffer2 == null) {
                throw new IOException("Could not load image: " + STBImage.stbi_failure_reason());
            }
            NativeImage nativeImage = new NativeImage(format == null ? Format.getStbFormat(intBuffer3.get(0)) : format, intBuffer.get(0), intBuffer2.get(0), true, MemoryUtil.memAddress((ByteBuffer)byteBuffer2));
            return nativeImage;
        }
    }

    private static void setClamp(boolean bl) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        if (bl) {
            GlStateManager._texParameter(3553, 10242, 10496);
            GlStateManager._texParameter(3553, 10243, 10496);
        } else {
            GlStateManager._texParameter(3553, 10242, 10497);
            GlStateManager._texParameter(3553, 10243, 10497);
        }
    }

    private static void setFilter(boolean bl, boolean bl2) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        if (bl) {
            GlStateManager._texParameter(3553, 10241, bl2 ? 9987 : 9729);
            GlStateManager._texParameter(3553, 10240, 9729);
        } else {
            GlStateManager._texParameter(3553, 10241, bl2 ? 9986 : 9728);
            GlStateManager._texParameter(3553, 10240, 9728);
        }
    }

    private void checkAllocated() {
        if (this.pixels == 0L) {
            throw new IllegalStateException("Image is not allocated.");
        }
    }

    @Override
    public void close() {
        if (this.pixels != 0L) {
            if (this.useStbFree) {
                STBImage.nstbi_image_free((long)this.pixels);
            } else {
                MemoryUtil.nmemFree((long)this.pixels);
            }
        }
        this.pixels = 0L;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public Format format() {
        return this.format;
    }

    public int getPixelRGBA(int n, int n2) {
        if (this.format != Format.RGBA) {
            throw new IllegalArgumentException(String.format("getPixelRGBA only works on RGBA images; have %s", new Object[]{this.format}));
        }
        if (n > this.width || n2 > this.height) {
            throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", n, n2, this.width, this.height));
        }
        this.checkAllocated();
        long l = (n + n2 * this.width) * 4;
        return MemoryUtil.memGetInt((long)(this.pixels + l));
    }

    public void setPixelRGBA(int n, int n2, int n3) {
        if (this.format != Format.RGBA) {
            throw new IllegalArgumentException(String.format("getPixelRGBA only works on RGBA images; have %s", new Object[]{this.format}));
        }
        if (n > this.width || n2 > this.height) {
            throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", n, n2, this.width, this.height));
        }
        this.checkAllocated();
        long l = (n + n2 * this.width) * 4;
        MemoryUtil.memPutInt((long)(this.pixels + l), (int)n3);
    }

    public byte getLuminanceOrAlpha(int n, int n2) {
        if (!this.format.hasLuminanceOrAlpha()) {
            throw new IllegalArgumentException(String.format("no luminance or alpha in %s", new Object[]{this.format}));
        }
        if (n > this.width || n2 > this.height) {
            throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", n, n2, this.width, this.height));
        }
        int n3 = (n + n2 * this.width) * this.format.components() + this.format.luminanceOrAlphaOffset() / 8;
        return MemoryUtil.memGetByte((long)(this.pixels + (long)n3));
    }

    @Deprecated
    public int[] makePixelArray() {
        if (this.format != Format.RGBA) {
            throw new UnsupportedOperationException("can only call makePixelArray for RGBA images.");
        }
        this.checkAllocated();
        int[] arrn = new int[this.getWidth() * this.getHeight()];
        for (int i = 0; i < this.getHeight(); ++i) {
            for (int j = 0; j < this.getWidth(); ++j) {
                int n;
                int n2 = this.getPixelRGBA(j, i);
                int n3 = NativeImage.getA(n2);
                int n4 = NativeImage.getB(n2);
                int n5 = NativeImage.getG(n2);
                int n6 = NativeImage.getR(n2);
                arrn[j + i * this.getWidth()] = n = n3 << 24 | n6 << 16 | n5 << 8 | n4;
            }
        }
        return arrn;
    }

    public void upload(int n, int n2, int n3, boolean bl) {
        this.upload(n, n2, n3, 0, 0, this.width, this.height, false, bl);
    }

    public void upload(int n, int n2, int n3, int n4, int n5, int n6, int n7, boolean bl, boolean bl2) {
        this.upload(n, n2, n3, n4, n5, n6, n7, false, false, bl, bl2);
    }

    public void upload(int n, int n2, int n3, int n4, int n5, int n6, int n7, boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(() -> this._upload(n, n2, n3, n4, n5, n6, n7, bl, bl2, bl3, bl4));
        } else {
            this._upload(n, n2, n3, n4, n5, n6, n7, bl, bl2, bl3, bl4);
        }
    }

    private void _upload(int n, int n2, int n3, int n4, int n5, int n6, int n7, boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        this.checkAllocated();
        NativeImage.setFilter(bl, bl3);
        NativeImage.setClamp(bl2);
        if (n6 == this.getWidth()) {
            GlStateManager._pixelStore(3314, 0);
        } else {
            GlStateManager._pixelStore(3314, this.getWidth());
        }
        GlStateManager._pixelStore(3316, n4);
        GlStateManager._pixelStore(3315, n5);
        this.format.setUnpackPixelStoreState();
        GlStateManager._texSubImage2D(3553, n, n2, n3, n6, n7, this.format.glFormat(), 5121, this.pixels);
        if (bl4) {
            this.close();
        }
    }

    public void downloadTexture(int n, boolean bl) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        this.checkAllocated();
        this.format.setPackPixelStoreState();
        GlStateManager._getTexImage(3553, n, this.format.glFormat(), 5121, this.pixels);
        if (bl && this.format.hasAlpha()) {
            for (int i = 0; i < this.getHeight(); ++i) {
                for (int j = 0; j < this.getWidth(); ++j) {
                    this.setPixelRGBA(j, i, this.getPixelRGBA(j, i) | 255 << this.format.alphaOffset());
                }
            }
        }
    }

    public void writeToFile(File file) throws IOException {
        this.writeToFile(file.toPath());
    }

    public void copyFromFont(STBTTFontinfo sTBTTFontinfo, int n, int n2, int n3, float f, float f2, float f3, float f4, int n4, int n5) {
        if (n4 < 0 || n4 + n2 > this.getWidth() || n5 < 0 || n5 + n3 > this.getHeight()) {
            throw new IllegalArgumentException(String.format("Out of bounds: start: (%s, %s) (size: %sx%s); size: %sx%s", n4, n5, n2, n3, this.getWidth(), this.getHeight()));
        }
        if (this.format.components() != 1) {
            throw new IllegalArgumentException("Can only write fonts into 1-component images.");
        }
        STBTruetype.nstbtt_MakeGlyphBitmapSubpixel((long)sTBTTFontinfo.address(), (long)(this.pixels + (long)n4 + (long)(n5 * this.getWidth())), (int)n2, (int)n3, (int)this.getWidth(), (float)f, (float)f2, (float)f3, (float)f4, (int)n);
    }

    public void writeToFile(Path path) throws IOException {
        if (!this.format.supportedByStb()) {
            throw new UnsupportedOperationException("Don't know how to write format " + (Object)((Object)this.format));
        }
        this.checkAllocated();
        try (SeekableByteChannel seekableByteChannel = Files.newByteChannel(path, OPEN_OPTIONS, new FileAttribute[0]);){
            if (!this.writeToChannel(seekableByteChannel)) {
                throw new IOException("Could not write image to the PNG file \"" + path.toAbsolutePath() + "\": " + STBImage.stbi_failure_reason());
            }
        }
    }

    /*
     * Exception decompiling
     */
    public byte[] asByteArray() throws IOException {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [0[TRYBLOCK]], but top level block is 4[TRYBLOCK]
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:427)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:479)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:619)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:699)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:188)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:133)
        // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:96)
        // org.benf.cfr.reader.entities.Method.analyse(Method.java:397)
        // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:906)
        // org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:797)
        // org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:225)
        // org.benf.cfr.reader.Driver.doJar(Driver.java:109)
        // org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:65)
        // org.benf.cfr.reader.Main.main(Main.java:48)
        throw new IllegalStateException("Decompilation failed");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean writeToChannel(WritableByteChannel writableByteChannel) throws IOException {
        WriteCallback writeCallback = new WriteCallback(writableByteChannel);
        try {
            int n = Math.min(this.getHeight(), Integer.MAX_VALUE / this.getWidth() / this.format.components());
            if (n < this.getHeight()) {
                LOGGER.warn("Dropping image height from {} to {} to fit the size into 32-bit signed int", (Object)this.getHeight(), (Object)n);
            }
            if (STBImageWrite.nstbi_write_png_to_func((long)writeCallback.address(), (long)0L, (int)this.getWidth(), (int)n, (int)this.format.components(), (long)this.pixels, (int)0) == 0) {
                boolean bl = false;
                return bl;
            }
            writeCallback.throwIfException();
            boolean bl = true;
            return bl;
        }
        finally {
            writeCallback.free();
        }
    }

    public void copyFrom(NativeImage nativeImage) {
        if (nativeImage.format() != this.format) {
            throw new UnsupportedOperationException("Image formats don't match.");
        }
        int n = this.format.components();
        this.checkAllocated();
        nativeImage.checkAllocated();
        if (this.width == nativeImage.width) {
            MemoryUtil.memCopy((long)nativeImage.pixels, (long)this.pixels, (long)Math.min(this.size, nativeImage.size));
        } else {
            int n2 = Math.min(this.getWidth(), nativeImage.getWidth());
            int n3 = Math.min(this.getHeight(), nativeImage.getHeight());
            for (int i = 0; i < n3; ++i) {
                int n4 = i * nativeImage.getWidth() * n;
                int n5 = i * this.getWidth() * n;
                MemoryUtil.memCopy((long)(nativeImage.pixels + (long)n4), (long)(this.pixels + (long)n5), (long)n2);
            }
        }
    }

    public void fillRect(int n, int n2, int n3, int n4, int n5) {
        for (int i = n2; i < n2 + n4; ++i) {
            for (int j = n; j < n + n3; ++j) {
                this.setPixelRGBA(j, i, n5);
            }
        }
    }

    public void copyRect(int n, int n2, int n3, int n4, int n5, int n6, boolean bl, boolean bl2) {
        for (int i = 0; i < n6; ++i) {
            for (int j = 0; j < n5; ++j) {
                int n7 = bl ? n5 - 1 - j : j;
                int n8 = bl2 ? n6 - 1 - i : i;
                int n9 = this.getPixelRGBA(n + j, n2 + i);
                this.setPixelRGBA(n + n3 + n7, n2 + n4 + n8, n9);
            }
        }
    }

    public void flipY() {
        this.checkAllocated();
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            int n = this.format.components();
            int n2 = this.getWidth() * n;
            long l = memoryStack.nmalloc(n2);
            for (int i = 0; i < this.getHeight() / 2; ++i) {
                int n3 = i * this.getWidth() * n;
                int n4 = (this.getHeight() - 1 - i) * this.getWidth() * n;
                MemoryUtil.memCopy((long)(this.pixels + (long)n3), (long)l, (long)n2);
                MemoryUtil.memCopy((long)(this.pixels + (long)n4), (long)(this.pixels + (long)n3), (long)n2);
                MemoryUtil.memCopy((long)l, (long)(this.pixels + (long)n4), (long)n2);
            }
        }
    }

    public void resizeSubRectTo(int n, int n2, int n3, int n4, NativeImage nativeImage) {
        this.checkAllocated();
        if (nativeImage.format() != this.format) {
            throw new UnsupportedOperationException("resizeSubRectTo only works for images of the same format.");
        }
        int n5 = this.format.components();
        STBImageResize.nstbir_resize_uint8((long)(this.pixels + (long)((n + n2 * this.getWidth()) * n5)), (int)n3, (int)n4, (int)(this.getWidth() * n5), (long)nativeImage.pixels, (int)nativeImage.getWidth(), (int)nativeImage.getHeight(), (int)0, (int)n5);
    }

    public void untrack() {
        DebugMemoryUntracker.untrack(this.pixels);
    }

    public static NativeImage fromBase64(String string) throws IOException {
        byte[] arrby = Base64.getDecoder().decode(string.replaceAll("\n", "").getBytes(Charsets.UTF_8));
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            ByteBuffer byteBuffer = memoryStack.malloc(arrby.length);
            byteBuffer.put(arrby);
            byteBuffer.rewind();
            NativeImage nativeImage = NativeImage.read(byteBuffer);
            return nativeImage;
        }
    }

    public static int getA(int n) {
        return n >> 24 & 0xFF;
    }

    public static int getR(int n) {
        return n >> 0 & 0xFF;
    }

    public static int getG(int n) {
        return n >> 8 & 0xFF;
    }

    public static int getB(int n) {
        return n >> 16 & 0xFF;
    }

    public static int combine(int n, int n2, int n3, int n4) {
        return (n & 0xFF) << 24 | (n2 & 0xFF) << 16 | (n3 & 0xFF) << 8 | (n4 & 0xFF) << 0;
    }

    public static enum Format {
        RGBA(4, 6408, true, true, true, false, true, 0, 8, 16, 255, 24, true),
        RGB(3, 6407, true, true, true, false, false, 0, 8, 16, 255, 255, true),
        LUMINANCE_ALPHA(2, 6410, false, false, false, true, true, 255, 255, 255, 0, 8, true),
        LUMINANCE(1, 6409, false, false, false, true, false, 0, 0, 0, 0, 255, true);
        
        private final int components;
        private final int glFormat;
        private final boolean hasRed;
        private final boolean hasGreen;
        private final boolean hasBlue;
        private final boolean hasLuminance;
        private final boolean hasAlpha;
        private final int redOffset;
        private final int greenOffset;
        private final int blueOffset;
        private final int luminanceOffset;
        private final int alphaOffset;
        private final boolean supportedByStb;

        private Format(int n2, int n3, boolean bl, boolean bl2, boolean bl3, boolean bl4, boolean bl5, int n4, int n5, int n6, int n7, int n8, boolean bl6) {
            this.components = n2;
            this.glFormat = n3;
            this.hasRed = bl;
            this.hasGreen = bl2;
            this.hasBlue = bl3;
            this.hasLuminance = bl4;
            this.hasAlpha = bl5;
            this.redOffset = n4;
            this.greenOffset = n5;
            this.blueOffset = n6;
            this.luminanceOffset = n7;
            this.alphaOffset = n8;
            this.supportedByStb = bl6;
        }

        public int components() {
            return this.components;
        }

        public void setPackPixelStoreState() {
            RenderSystem.assertThread(RenderSystem::isOnRenderThread);
            GlStateManager._pixelStore(3333, this.components());
        }

        public void setUnpackPixelStoreState() {
            RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
            GlStateManager._pixelStore(3317, this.components());
        }

        public int glFormat() {
            return this.glFormat;
        }

        public boolean hasAlpha() {
            return this.hasAlpha;
        }

        public int alphaOffset() {
            return this.alphaOffset;
        }

        public boolean hasLuminanceOrAlpha() {
            return this.hasLuminance || this.hasAlpha;
        }

        public int luminanceOrAlphaOffset() {
            return this.hasLuminance ? this.luminanceOffset : this.alphaOffset;
        }

        public boolean supportedByStb() {
            return this.supportedByStb;
        }

        private static Format getStbFormat(int n) {
            switch (n) {
                case 1: {
                    return LUMINANCE;
                }
                case 2: {
                    return LUMINANCE_ALPHA;
                }
                case 3: {
                    return RGB;
                }
            }
            return RGBA;
        }
    }

    public static enum InternalGlFormat {
        RGBA(6408),
        RGB(6407),
        LUMINANCE_ALPHA(6410),
        LUMINANCE(6409),
        INTENSITY(32841);
        
        private final int glFormat;

        private InternalGlFormat(int n2) {
            this.glFormat = n2;
        }

        int glFormat() {
            return this.glFormat;
        }
    }

    static class WriteCallback
    extends STBIWriteCallback {
        private final WritableByteChannel output;
        @Nullable
        private IOException exception;

        private WriteCallback(WritableByteChannel writableByteChannel) {
            this.output = writableByteChannel;
        }

        public void invoke(long l, long l2, int n) {
            ByteBuffer byteBuffer = WriteCallback.getData((long)l2, (int)n);
            try {
                this.output.write(byteBuffer);
            }
            catch (IOException iOException) {
                this.exception = iOException;
            }
        }

        public void throwIfException() throws IOException {
            if (this.exception != null) {
                throw this.exception;
            }
        }
    }

}

