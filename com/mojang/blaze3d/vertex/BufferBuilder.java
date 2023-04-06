/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.primitives.Floats
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.ints.IntArrays
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.mojang.blaze3d.vertex;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.primitives.Floats;
import com.mojang.blaze3d.platform.MemoryTracker;
import com.mojang.blaze3d.vertex.BufferVertexConsumer;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.DefaultedVertexConsumer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.IntArrays;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.BitSet;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BufferBuilder
extends DefaultedVertexConsumer
implements BufferVertexConsumer {
    private static final Logger LOGGER = LogManager.getLogger();
    private ByteBuffer buffer;
    private final List<DrawState> vertexCounts = Lists.newArrayList();
    private int lastRenderedCountIndex = 0;
    private int totalRenderedBytes = 0;
    private int nextElementByte = 0;
    private int totalUploadedBytes = 0;
    private int vertices;
    @Nullable
    private VertexFormatElement currentElement;
    private int elementIndex;
    private int mode;
    private VertexFormat format;
    private boolean fastFormat;
    private boolean fullFormat;
    private boolean building;

    public BufferBuilder(int n) {
        this.buffer = MemoryTracker.createByteBuffer(n * 4);
    }

    protected void ensureVertexCapacity() {
        this.ensureCapacity(this.format.getVertexSize());
    }

    private void ensureCapacity(int n) {
        if (this.nextElementByte + n <= this.buffer.capacity()) {
            return;
        }
        int n2 = this.buffer.capacity();
        int n3 = n2 + BufferBuilder.roundUp(n);
        LOGGER.debug("Needed to grow BufferBuilder buffer: Old size {} bytes, new size {} bytes.", (Object)n2, (Object)n3);
        ByteBuffer byteBuffer = MemoryTracker.createByteBuffer(n3);
        this.buffer.position(0);
        byteBuffer.put(this.buffer);
        byteBuffer.rewind();
        this.buffer = byteBuffer;
    }

    private static int roundUp(int n) {
        int n2;
        int n3 = 2097152;
        if (n == 0) {
            return n3;
        }
        if (n < 0) {
            n3 *= -1;
        }
        if ((n2 = n % n3) == 0) {
            return n;
        }
        return n + n3 - n2;
    }

    public void sortQuads(float f, float f2, float f3) {
        this.buffer.clear();
        FloatBuffer floatBuffer = this.buffer.asFloatBuffer();
        int n3 = this.vertices / 4;
        float[] arrf = new float[n3];
        for (int i = 0; i < n3; ++i) {
            arrf[i] = BufferBuilder.getQuadDistanceFromPlayer(floatBuffer, f, f2, f3, this.format.getIntegerSize(), this.totalRenderedBytes / 4 + i * this.format.getVertexSize());
        }
        int[] arrn = new int[n3];
        for (int i = 0; i < arrn.length; ++i) {
            arrn[i] = i;
        }
        IntArrays.mergeSort((int[])arrn, (n, n2) -> Floats.compare((float)arrf[n2], (float)arrf[n]));
        BitSet bitSet = new BitSet();
        FloatBuffer floatBuffer2 = MemoryTracker.createFloatBuffer(this.format.getIntegerSize() * 4);
        int n4 = bitSet.nextClearBit(0);
        while (n4 < arrn.length) {
            int n5 = arrn[n4];
            if (n5 != n4) {
                this.limitToVertex(floatBuffer, n5);
                floatBuffer2.clear();
                floatBuffer2.put(floatBuffer);
                int n6 = n5;
                int n7 = arrn[n6];
                while (n6 != n4) {
                    this.limitToVertex(floatBuffer, n7);
                    FloatBuffer floatBuffer3 = floatBuffer.slice();
                    this.limitToVertex(floatBuffer, n6);
                    floatBuffer.put(floatBuffer3);
                    bitSet.set(n6);
                    n6 = n7;
                    n7 = arrn[n6];
                }
                this.limitToVertex(floatBuffer, n4);
                floatBuffer2.flip();
                floatBuffer.put(floatBuffer2);
            }
            bitSet.set(n4);
            n4 = bitSet.nextClearBit(n4 + 1);
        }
    }

    private void limitToVertex(FloatBuffer floatBuffer, int n) {
        int n2 = this.format.getIntegerSize() * 4;
        floatBuffer.limit(this.totalRenderedBytes / 4 + (n + 1) * n2);
        floatBuffer.position(this.totalRenderedBytes / 4 + n * n2);
    }

    public State getState() {
        this.buffer.limit(this.nextElementByte);
        this.buffer.position(this.totalRenderedBytes);
        ByteBuffer byteBuffer = ByteBuffer.allocate(this.vertices * this.format.getVertexSize());
        byteBuffer.put(this.buffer);
        this.buffer.clear();
        return new State(byteBuffer, this.format);
    }

    private static float getQuadDistanceFromPlayer(FloatBuffer floatBuffer, float f, float f2, float f3, int n, int n2) {
        float f4 = floatBuffer.get(n2 + n * 0 + 0);
        float f5 = floatBuffer.get(n2 + n * 0 + 1);
        float f6 = floatBuffer.get(n2 + n * 0 + 2);
        float f7 = floatBuffer.get(n2 + n * 1 + 0);
        float f8 = floatBuffer.get(n2 + n * 1 + 1);
        float f9 = floatBuffer.get(n2 + n * 1 + 2);
        float f10 = floatBuffer.get(n2 + n * 2 + 0);
        float f11 = floatBuffer.get(n2 + n * 2 + 1);
        float f12 = floatBuffer.get(n2 + n * 2 + 2);
        float f13 = floatBuffer.get(n2 + n * 3 + 0);
        float f14 = floatBuffer.get(n2 + n * 3 + 1);
        float f15 = floatBuffer.get(n2 + n * 3 + 2);
        float f16 = (f4 + f7 + f10 + f13) * 0.25f - f;
        float f17 = (f5 + f8 + f11 + f14) * 0.25f - f2;
        float f18 = (f6 + f9 + f12 + f15) * 0.25f - f3;
        return f16 * f16 + f17 * f17 + f18 * f18;
    }

    public void restoreState(State state) {
        state.data.clear();
        int n = state.data.capacity();
        this.ensureCapacity(n);
        this.buffer.limit(this.buffer.capacity());
        this.buffer.position(this.totalRenderedBytes);
        this.buffer.put(state.data);
        this.buffer.clear();
        VertexFormat vertexFormat = state.format;
        this.switchFormat(vertexFormat);
        this.vertices = n / vertexFormat.getVertexSize();
        this.nextElementByte = this.totalRenderedBytes + this.vertices * vertexFormat.getVertexSize();
    }

    public void begin(int n, VertexFormat vertexFormat) {
        if (this.building) {
            throw new IllegalStateException("Already building!");
        }
        this.building = true;
        this.mode = n;
        this.switchFormat(vertexFormat);
        this.currentElement = (VertexFormatElement)vertexFormat.getElements().get(0);
        this.elementIndex = 0;
        this.buffer.clear();
    }

    private void switchFormat(VertexFormat vertexFormat) {
        if (this.format == vertexFormat) {
            return;
        }
        this.format = vertexFormat;
        boolean bl = vertexFormat == DefaultVertexFormat.NEW_ENTITY;
        boolean bl2 = vertexFormat == DefaultVertexFormat.BLOCK;
        this.fastFormat = bl || bl2;
        this.fullFormat = bl;
    }

    public void end() {
        if (!this.building) {
            throw new IllegalStateException("Not building!");
        }
        this.building = false;
        this.vertexCounts.add(new DrawState(this.format, this.vertices, this.mode));
        this.totalRenderedBytes += this.vertices * this.format.getVertexSize();
        this.vertices = 0;
        this.currentElement = null;
        this.elementIndex = 0;
    }

    @Override
    public void putByte(int n, byte by) {
        this.buffer.put(this.nextElementByte + n, by);
    }

    @Override
    public void putShort(int n, short s) {
        this.buffer.putShort(this.nextElementByte + n, s);
    }

    @Override
    public void putFloat(int n, float f) {
        this.buffer.putFloat(this.nextElementByte + n, f);
    }

    @Override
    public void endVertex() {
        if (this.elementIndex != 0) {
            throw new IllegalStateException("Not filled all elements of the vertex");
        }
        ++this.vertices;
        this.ensureVertexCapacity();
    }

    @Override
    public void nextElement() {
        VertexFormatElement vertexFormatElement;
        ImmutableList<VertexFormatElement> immutableList = this.format.getElements();
        this.elementIndex = (this.elementIndex + 1) % immutableList.size();
        this.nextElementByte += this.currentElement.getByteSize();
        this.currentElement = vertexFormatElement = (VertexFormatElement)immutableList.get(this.elementIndex);
        if (vertexFormatElement.getUsage() == VertexFormatElement.Usage.PADDING) {
            this.nextElement();
        }
        if (this.defaultColorSet && this.currentElement.getUsage() == VertexFormatElement.Usage.COLOR) {
            BufferVertexConsumer.super.color(this.defaultR, this.defaultG, this.defaultB, this.defaultA);
        }
    }

    @Override
    public VertexConsumer color(int n, int n2, int n3, int n4) {
        if (this.defaultColorSet) {
            throw new IllegalStateException();
        }
        return BufferVertexConsumer.super.color(n, n2, n3, n4);
    }

    @Override
    public void vertex(float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, int n, int n2, float f10, float f11, float f12) {
        if (this.defaultColorSet) {
            throw new IllegalStateException();
        }
        if (this.fastFormat) {
            int n3;
            this.putFloat(0, f);
            this.putFloat(4, f2);
            this.putFloat(8, f3);
            this.putByte(12, (byte)(f4 * 255.0f));
            this.putByte(13, (byte)(f5 * 255.0f));
            this.putByte(14, (byte)(f6 * 255.0f));
            this.putByte(15, (byte)(f7 * 255.0f));
            this.putFloat(16, f8);
            this.putFloat(20, f9);
            if (this.fullFormat) {
                this.putShort(24, (short)(n & 0xFFFF));
                this.putShort(26, (short)(n >> 16 & 0xFFFF));
                n3 = 28;
            } else {
                n3 = 24;
            }
            this.putShort(n3 + 0, (short)(n2 & 0xFFFF));
            this.putShort(n3 + 2, (short)(n2 >> 16 & 0xFFFF));
            this.putByte(n3 + 4, BufferVertexConsumer.normalIntValue(f10));
            this.putByte(n3 + 5, BufferVertexConsumer.normalIntValue(f11));
            this.putByte(n3 + 6, BufferVertexConsumer.normalIntValue(f12));
            this.nextElementByte += n3 + 8;
            this.endVertex();
            return;
        }
        super.vertex(f, f2, f3, f4, f5, f6, f7, f8, f9, n, n2, f10, f11, f12);
    }

    public Pair<DrawState, ByteBuffer> popNextBuffer() {
        DrawState drawState = this.vertexCounts.get(this.lastRenderedCountIndex++);
        this.buffer.position(this.totalUploadedBytes);
        this.totalUploadedBytes += drawState.vertexCount() * drawState.format().getVertexSize();
        this.buffer.limit(this.totalUploadedBytes);
        if (this.lastRenderedCountIndex == this.vertexCounts.size() && this.vertices == 0) {
            this.clear();
        }
        ByteBuffer byteBuffer = this.buffer.slice();
        this.buffer.clear();
        return Pair.of((Object)drawState, (Object)byteBuffer);
    }

    public void clear() {
        if (this.totalRenderedBytes != this.totalUploadedBytes) {
            LOGGER.warn("Bytes mismatch " + this.totalRenderedBytes + " " + this.totalUploadedBytes);
        }
        this.discard();
    }

    public void discard() {
        this.totalRenderedBytes = 0;
        this.totalUploadedBytes = 0;
        this.nextElementByte = 0;
        this.vertexCounts.clear();
        this.lastRenderedCountIndex = 0;
    }

    @Override
    public VertexFormatElement currentElement() {
        if (this.currentElement == null) {
            throw new IllegalStateException("BufferBuilder not started");
        }
        return this.currentElement;
    }

    public boolean building() {
        return this.building;
    }

    public static final class DrawState {
        private final VertexFormat format;
        private final int vertexCount;
        private final int mode;

        private DrawState(VertexFormat vertexFormat, int n, int n2) {
            this.format = vertexFormat;
            this.vertexCount = n;
            this.mode = n2;
        }

        public VertexFormat format() {
            return this.format;
        }

        public int vertexCount() {
            return this.vertexCount;
        }

        public int mode() {
            return this.mode;
        }
    }

    public static class State {
        private final ByteBuffer data;
        private final VertexFormat format;

        private State(ByteBuffer byteBuffer, VertexFormat vertexFormat) {
            this.data = byteBuffer;
            this.format = vertexFormat;
        }
    }

}

