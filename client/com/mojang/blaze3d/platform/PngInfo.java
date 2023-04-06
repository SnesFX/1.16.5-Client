/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  org.lwjgl.stb.STBIEOFCallback
 *  org.lwjgl.stb.STBIEOFCallbackI
 *  org.lwjgl.stb.STBIIOCallbacks
 *  org.lwjgl.stb.STBIReadCallback
 *  org.lwjgl.stb.STBIReadCallbackI
 *  org.lwjgl.stb.STBISkipCallback
 *  org.lwjgl.stb.STBISkipCallbackI
 *  org.lwjgl.stb.STBImage
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 */
package com.mojang.blaze3d.platform;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import org.lwjgl.stb.STBIEOFCallback;
import org.lwjgl.stb.STBIEOFCallbackI;
import org.lwjgl.stb.STBIIOCallbacks;
import org.lwjgl.stb.STBIReadCallback;
import org.lwjgl.stb.STBIReadCallbackI;
import org.lwjgl.stb.STBISkipCallback;
import org.lwjgl.stb.STBISkipCallbackI;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class PngInfo {
    public final int width;
    public final int height;

    public PngInfo(String string, InputStream inputStream) throws IOException {
        try (MemoryStack memoryStack = MemoryStack.stackPush();
             StbReader stbReader = PngInfo.createCallbacks(inputStream);
             STBIReadCallback sTBIReadCallback = STBIReadCallback.create((arg_0, arg_1, arg_2) -> stbReader.read(arg_0, arg_1, arg_2));
             STBISkipCallback sTBISkipCallback = STBISkipCallback.create((arg_0, arg_1) -> stbReader.skip(arg_0, arg_1));
             STBIEOFCallback sTBIEOFCallback = STBIEOFCallback.create(stbReader::eof);){
            STBIIOCallbacks sTBIIOCallbacks = STBIIOCallbacks.mallocStack((MemoryStack)memoryStack);
            sTBIIOCallbacks.read((STBIReadCallbackI)sTBIReadCallback);
            sTBIIOCallbacks.skip((STBISkipCallbackI)sTBISkipCallback);
            sTBIIOCallbacks.eof((STBIEOFCallbackI)sTBIEOFCallback);
            IntBuffer intBuffer = memoryStack.mallocInt(1);
            IntBuffer intBuffer2 = memoryStack.mallocInt(1);
            IntBuffer intBuffer3 = memoryStack.mallocInt(1);
            if (!STBImage.stbi_info_from_callbacks((STBIIOCallbacks)sTBIIOCallbacks, (long)0L, (IntBuffer)intBuffer, (IntBuffer)intBuffer2, (IntBuffer)intBuffer3)) {
                throw new IOException("Could not read info from the PNG file " + string + " " + STBImage.stbi_failure_reason());
            }
            this.width = intBuffer.get(0);
            this.height = intBuffer2.get(0);
        }
    }

    private static StbReader createCallbacks(InputStream inputStream) {
        if (inputStream instanceof FileInputStream) {
            return new StbReaderSeekableByteChannel(((FileInputStream)inputStream).getChannel());
        }
        return new StbReaderBufferedChannel(Channels.newChannel(inputStream));
    }

    static class StbReaderBufferedChannel
    extends StbReader {
        private final ReadableByteChannel channel;
        private long readBufferAddress = MemoryUtil.nmemAlloc((long)128L);
        private int bufferSize = 128;
        private int read;
        private int consumed;

        private StbReaderBufferedChannel(ReadableByteChannel readableByteChannel) {
            this.channel = readableByteChannel;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void fillReadBuffer(int n) throws IOException {
            ByteBuffer byteBuffer = MemoryUtil.memByteBuffer((long)this.readBufferAddress, (int)this.bufferSize);
            if (n + this.consumed > this.bufferSize) {
                this.bufferSize = n + this.consumed;
                byteBuffer = MemoryUtil.memRealloc((ByteBuffer)byteBuffer, (int)this.bufferSize);
                this.readBufferAddress = MemoryUtil.memAddress((ByteBuffer)byteBuffer);
            }
            byteBuffer.position(this.read);
            while (n + this.consumed > this.read) {
                try {
                    int n2 = this.channel.read(byteBuffer);
                    if (n2 != -1) continue;
                    break;
                }
                finally {
                    this.read = byteBuffer.position();
                }
            }
        }

        @Override
        public int read(long l, int n) throws IOException {
            this.fillReadBuffer(n);
            if (n + this.consumed > this.read) {
                n = this.read - this.consumed;
            }
            MemoryUtil.memCopy((long)(this.readBufferAddress + (long)this.consumed), (long)l, (long)n);
            this.consumed += n;
            return n;
        }

        @Override
        public void skip(int n) throws IOException {
            if (n > 0) {
                this.fillReadBuffer(n);
                if (n + this.consumed > this.read) {
                    throw new EOFException("Can't skip past the EOF.");
                }
            }
            if (this.consumed + n < 0) {
                throw new IOException("Can't seek before the beginning: " + (this.consumed + n));
            }
            this.consumed += n;
        }

        @Override
        public void close() throws IOException {
            MemoryUtil.nmemFree((long)this.readBufferAddress);
            this.channel.close();
        }
    }

    static class StbReaderSeekableByteChannel
    extends StbReader {
        private final SeekableByteChannel channel;

        private StbReaderSeekableByteChannel(SeekableByteChannel seekableByteChannel) {
            this.channel = seekableByteChannel;
        }

        @Override
        public int read(long l, int n) throws IOException {
            ByteBuffer byteBuffer = MemoryUtil.memByteBuffer((long)l, (int)n);
            return this.channel.read(byteBuffer);
        }

        @Override
        public void skip(int n) throws IOException {
            this.channel.position(this.channel.position() + (long)n);
        }

        @Override
        public int eof(long l) {
            return super.eof(l) != 0 && this.channel.isOpen() ? 1 : 0;
        }

        @Override
        public void close() throws IOException {
            this.channel.close();
        }
    }

    static abstract class StbReader
    implements AutoCloseable {
        protected boolean closed;

        private StbReader() {
        }

        int read(long l, long l2, int n) {
            try {
                return this.read(l2, n);
            }
            catch (IOException iOException) {
                this.closed = true;
                return 0;
            }
        }

        void skip(long l, int n) {
            try {
                this.skip(n);
            }
            catch (IOException iOException) {
                this.closed = true;
            }
        }

        int eof(long l) {
            return this.closed ? 1 : 0;
        }

        protected abstract int read(long var1, int var3) throws IOException;

        protected abstract void skip(int var1) throws IOException;

        @Override
        public abstract void close() throws IOException;
    }

}

