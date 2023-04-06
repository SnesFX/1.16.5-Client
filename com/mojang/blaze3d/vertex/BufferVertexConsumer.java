/*
 * Decompiled with CFR 0.146.
 */
package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.util.Mth;

public interface BufferVertexConsumer
extends VertexConsumer {
    public VertexFormatElement currentElement();

    public void nextElement();

    public void putByte(int var1, byte var2);

    public void putShort(int var1, short var2);

    public void putFloat(int var1, float var2);

    @Override
    default public VertexConsumer vertex(double d, double d2, double d3) {
        if (this.currentElement().getType() != VertexFormatElement.Type.FLOAT) {
            throw new IllegalStateException();
        }
        this.putFloat(0, (float)d);
        this.putFloat(4, (float)d2);
        this.putFloat(8, (float)d3);
        this.nextElement();
        return this;
    }

    @Override
    default public VertexConsumer color(int n, int n2, int n3, int n4) {
        VertexFormatElement vertexFormatElement = this.currentElement();
        if (vertexFormatElement.getUsage() != VertexFormatElement.Usage.COLOR) {
            return this;
        }
        if (vertexFormatElement.getType() != VertexFormatElement.Type.UBYTE) {
            throw new IllegalStateException();
        }
        this.putByte(0, (byte)n);
        this.putByte(1, (byte)n2);
        this.putByte(2, (byte)n3);
        this.putByte(3, (byte)n4);
        this.nextElement();
        return this;
    }

    @Override
    default public VertexConsumer uv(float f, float f2) {
        VertexFormatElement vertexFormatElement = this.currentElement();
        if (vertexFormatElement.getUsage() != VertexFormatElement.Usage.UV || vertexFormatElement.getIndex() != 0) {
            return this;
        }
        if (vertexFormatElement.getType() != VertexFormatElement.Type.FLOAT) {
            throw new IllegalStateException();
        }
        this.putFloat(0, f);
        this.putFloat(4, f2);
        this.nextElement();
        return this;
    }

    @Override
    default public VertexConsumer overlayCoords(int n, int n2) {
        return this.uvShort((short)n, (short)n2, 1);
    }

    @Override
    default public VertexConsumer uv2(int n, int n2) {
        return this.uvShort((short)n, (short)n2, 2);
    }

    default public VertexConsumer uvShort(short s, short s2, int n) {
        VertexFormatElement vertexFormatElement = this.currentElement();
        if (vertexFormatElement.getUsage() != VertexFormatElement.Usage.UV || vertexFormatElement.getIndex() != n) {
            return this;
        }
        if (vertexFormatElement.getType() != VertexFormatElement.Type.SHORT) {
            throw new IllegalStateException();
        }
        this.putShort(0, s);
        this.putShort(2, s2);
        this.nextElement();
        return this;
    }

    @Override
    default public VertexConsumer normal(float f, float f2, float f3) {
        VertexFormatElement vertexFormatElement = this.currentElement();
        if (vertexFormatElement.getUsage() != VertexFormatElement.Usage.NORMAL) {
            return this;
        }
        if (vertexFormatElement.getType() != VertexFormatElement.Type.BYTE) {
            throw new IllegalStateException();
        }
        this.putByte(0, BufferVertexConsumer.normalIntValue(f));
        this.putByte(1, BufferVertexConsumer.normalIntValue(f2));
        this.putByte(2, BufferVertexConsumer.normalIntValue(f3));
        this.nextElement();
        return this;
    }

    public static byte normalIntValue(float f) {
        return (byte)((int)(Mth.clamp(f, -1.0f, 1.0f) * 127.0f) & 0xFF);
    }
}

