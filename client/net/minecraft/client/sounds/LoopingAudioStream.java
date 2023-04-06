/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.sounds;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import javax.sound.sampled.AudioFormat;
import net.minecraft.client.sounds.AudioStream;

public class LoopingAudioStream
implements AudioStream {
    private final AudioStreamProvider provider;
    private AudioStream stream;
    private final BufferedInputStream bufferedInputStream;

    public LoopingAudioStream(AudioStreamProvider audioStreamProvider, InputStream inputStream) throws IOException {
        this.provider = audioStreamProvider;
        this.bufferedInputStream = new BufferedInputStream(inputStream);
        this.bufferedInputStream.mark(Integer.MAX_VALUE);
        this.stream = audioStreamProvider.create(new NoCloseBuffer(this.bufferedInputStream));
    }

    @Override
    public AudioFormat getFormat() {
        return this.stream.getFormat();
    }

    @Override
    public ByteBuffer read(int n) throws IOException {
        ByteBuffer byteBuffer = this.stream.read(n);
        if (!byteBuffer.hasRemaining()) {
            this.stream.close();
            this.bufferedInputStream.reset();
            this.stream = this.provider.create(new NoCloseBuffer(this.bufferedInputStream));
            byteBuffer = this.stream.read(n);
        }
        return byteBuffer;
    }

    @Override
    public void close() throws IOException {
        this.stream.close();
        this.bufferedInputStream.close();
    }

    @FunctionalInterface
    public static interface AudioStreamProvider {
        public AudioStream create(InputStream var1) throws IOException;
    }

    static class NoCloseBuffer
    extends FilterInputStream {
        private NoCloseBuffer(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public void close() {
        }
    }

}

