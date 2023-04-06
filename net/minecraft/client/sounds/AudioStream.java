/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.sounds;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.sound.sampled.AudioFormat;

public interface AudioStream
extends Closeable {
    public AudioFormat getFormat();

    public ByteBuffer read(int var1) throws IOException;
}

