/*
 * Decompiled with CFR 0.146.
 */
package com.mojang.blaze3d.platform;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class MemoryTracker {
    public static synchronized ByteBuffer createByteBuffer(int n) {
        return ByteBuffer.allocateDirect(n).order(ByteOrder.nativeOrder());
    }

    public static FloatBuffer createFloatBuffer(int n) {
        return MemoryTracker.createByteBuffer(n << 2).asFloatBuffer();
    }
}

