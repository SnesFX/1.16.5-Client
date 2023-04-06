/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.lwjgl.openal.AL10
 */
package com.mojang.blaze3d.audio;

import com.mojang.blaze3d.audio.OpenAlUtil;
import java.nio.ByteBuffer;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;
import org.lwjgl.openal.AL10;

public class SoundBuffer {
    @Nullable
    private ByteBuffer data;
    private final AudioFormat format;
    private boolean hasAlBuffer;
    private int alBuffer;

    public SoundBuffer(ByteBuffer byteBuffer, AudioFormat audioFormat) {
        this.data = byteBuffer;
        this.format = audioFormat;
    }

    OptionalInt getAlBuffer() {
        if (!this.hasAlBuffer) {
            if (this.data == null) {
                return OptionalInt.empty();
            }
            int n = OpenAlUtil.audioFormatToOpenAl(this.format);
            int[] arrn = new int[1];
            AL10.alGenBuffers((int[])arrn);
            if (OpenAlUtil.checkALError("Creating buffer")) {
                return OptionalInt.empty();
            }
            AL10.alBufferData((int)arrn[0], (int)n, (ByteBuffer)this.data, (int)((int)this.format.getSampleRate()));
            if (OpenAlUtil.checkALError("Assigning buffer data")) {
                return OptionalInt.empty();
            }
            this.alBuffer = arrn[0];
            this.hasAlBuffer = true;
            this.data = null;
        }
        return OptionalInt.of(this.alBuffer);
    }

    public void discardAlBuffer() {
        if (this.hasAlBuffer) {
            AL10.alDeleteBuffers((int[])new int[]{this.alBuffer});
            if (OpenAlUtil.checkALError("Deleting stream buffers")) {
                return;
            }
        }
        this.hasAlBuffer = false;
    }

    public OptionalInt releaseAlBuffer() {
        OptionalInt optionalInt = this.getAlBuffer();
        this.hasAlBuffer = false;
        return optionalInt;
    }
}

