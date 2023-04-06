/*
 * Decompiled with CFR 0.146.
 */
package com.mojang.math;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import java.nio.FloatBuffer;

public final class Matrix4f {
    protected float m00;
    protected float m01;
    protected float m02;
    protected float m03;
    protected float m10;
    protected float m11;
    protected float m12;
    protected float m13;
    protected float m20;
    protected float m21;
    protected float m22;
    protected float m23;
    protected float m30;
    protected float m31;
    protected float m32;
    protected float m33;

    public Matrix4f() {
    }

    public Matrix4f(Matrix4f matrix4f) {
        this.m00 = matrix4f.m00;
        this.m01 = matrix4f.m01;
        this.m02 = matrix4f.m02;
        this.m03 = matrix4f.m03;
        this.m10 = matrix4f.m10;
        this.m11 = matrix4f.m11;
        this.m12 = matrix4f.m12;
        this.m13 = matrix4f.m13;
        this.m20 = matrix4f.m20;
        this.m21 = matrix4f.m21;
        this.m22 = matrix4f.m22;
        this.m23 = matrix4f.m23;
        this.m30 = matrix4f.m30;
        this.m31 = matrix4f.m31;
        this.m32 = matrix4f.m32;
        this.m33 = matrix4f.m33;
    }

    public Matrix4f(Quaternion quaternion) {
        float f = quaternion.i();
        float f2 = quaternion.j();
        float f3 = quaternion.k();
        float f4 = quaternion.r();
        float f5 = 2.0f * f * f;
        float f6 = 2.0f * f2 * f2;
        float f7 = 2.0f * f3 * f3;
        this.m00 = 1.0f - f6 - f7;
        this.m11 = 1.0f - f7 - f5;
        this.m22 = 1.0f - f5 - f6;
        this.m33 = 1.0f;
        float f8 = f * f2;
        float f9 = f2 * f3;
        float f10 = f3 * f;
        float f11 = f * f4;
        float f12 = f2 * f4;
        float f13 = f3 * f4;
        this.m10 = 2.0f * (f8 + f13);
        this.m01 = 2.0f * (f8 - f13);
        this.m20 = 2.0f * (f10 - f12);
        this.m02 = 2.0f * (f10 + f12);
        this.m21 = 2.0f * (f9 + f11);
        this.m12 = 2.0f * (f9 - f11);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        Matrix4f matrix4f = (Matrix4f)object;
        return Float.compare(matrix4f.m00, this.m00) == 0 && Float.compare(matrix4f.m01, this.m01) == 0 && Float.compare(matrix4f.m02, this.m02) == 0 && Float.compare(matrix4f.m03, this.m03) == 0 && Float.compare(matrix4f.m10, this.m10) == 0 && Float.compare(matrix4f.m11, this.m11) == 0 && Float.compare(matrix4f.m12, this.m12) == 0 && Float.compare(matrix4f.m13, this.m13) == 0 && Float.compare(matrix4f.m20, this.m20) == 0 && Float.compare(matrix4f.m21, this.m21) == 0 && Float.compare(matrix4f.m22, this.m22) == 0 && Float.compare(matrix4f.m23, this.m23) == 0 && Float.compare(matrix4f.m30, this.m30) == 0 && Float.compare(matrix4f.m31, this.m31) == 0 && Float.compare(matrix4f.m32, this.m32) == 0 && Float.compare(matrix4f.m33, this.m33) == 0;
    }

    public int hashCode() {
        int n = this.m00 != 0.0f ? Float.floatToIntBits(this.m00) : 0;
        n = 31 * n + (this.m01 != 0.0f ? Float.floatToIntBits(this.m01) : 0);
        n = 31 * n + (this.m02 != 0.0f ? Float.floatToIntBits(this.m02) : 0);
        n = 31 * n + (this.m03 != 0.0f ? Float.floatToIntBits(this.m03) : 0);
        n = 31 * n + (this.m10 != 0.0f ? Float.floatToIntBits(this.m10) : 0);
        n = 31 * n + (this.m11 != 0.0f ? Float.floatToIntBits(this.m11) : 0);
        n = 31 * n + (this.m12 != 0.0f ? Float.floatToIntBits(this.m12) : 0);
        n = 31 * n + (this.m13 != 0.0f ? Float.floatToIntBits(this.m13) : 0);
        n = 31 * n + (this.m20 != 0.0f ? Float.floatToIntBits(this.m20) : 0);
        n = 31 * n + (this.m21 != 0.0f ? Float.floatToIntBits(this.m21) : 0);
        n = 31 * n + (this.m22 != 0.0f ? Float.floatToIntBits(this.m22) : 0);
        n = 31 * n + (this.m23 != 0.0f ? Float.floatToIntBits(this.m23) : 0);
        n = 31 * n + (this.m30 != 0.0f ? Float.floatToIntBits(this.m30) : 0);
        n = 31 * n + (this.m31 != 0.0f ? Float.floatToIntBits(this.m31) : 0);
        n = 31 * n + (this.m32 != 0.0f ? Float.floatToIntBits(this.m32) : 0);
        n = 31 * n + (this.m33 != 0.0f ? Float.floatToIntBits(this.m33) : 0);
        return n;
    }

    private static int bufferIndex(int n, int n2) {
        return n2 * 4 + n;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Matrix4f:\n");
        stringBuilder.append(this.m00);
        stringBuilder.append(" ");
        stringBuilder.append(this.m01);
        stringBuilder.append(" ");
        stringBuilder.append(this.m02);
        stringBuilder.append(" ");
        stringBuilder.append(this.m03);
        stringBuilder.append("\n");
        stringBuilder.append(this.m10);
        stringBuilder.append(" ");
        stringBuilder.append(this.m11);
        stringBuilder.append(" ");
        stringBuilder.append(this.m12);
        stringBuilder.append(" ");
        stringBuilder.append(this.m13);
        stringBuilder.append("\n");
        stringBuilder.append(this.m20);
        stringBuilder.append(" ");
        stringBuilder.append(this.m21);
        stringBuilder.append(" ");
        stringBuilder.append(this.m22);
        stringBuilder.append(" ");
        stringBuilder.append(this.m23);
        stringBuilder.append("\n");
        stringBuilder.append(this.m30);
        stringBuilder.append(" ");
        stringBuilder.append(this.m31);
        stringBuilder.append(" ");
        stringBuilder.append(this.m32);
        stringBuilder.append(" ");
        stringBuilder.append(this.m33);
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }

    public void store(FloatBuffer floatBuffer) {
        floatBuffer.put(Matrix4f.bufferIndex(0, 0), this.m00);
        floatBuffer.put(Matrix4f.bufferIndex(0, 1), this.m01);
        floatBuffer.put(Matrix4f.bufferIndex(0, 2), this.m02);
        floatBuffer.put(Matrix4f.bufferIndex(0, 3), this.m03);
        floatBuffer.put(Matrix4f.bufferIndex(1, 0), this.m10);
        floatBuffer.put(Matrix4f.bufferIndex(1, 1), this.m11);
        floatBuffer.put(Matrix4f.bufferIndex(1, 2), this.m12);
        floatBuffer.put(Matrix4f.bufferIndex(1, 3), this.m13);
        floatBuffer.put(Matrix4f.bufferIndex(2, 0), this.m20);
        floatBuffer.put(Matrix4f.bufferIndex(2, 1), this.m21);
        floatBuffer.put(Matrix4f.bufferIndex(2, 2), this.m22);
        floatBuffer.put(Matrix4f.bufferIndex(2, 3), this.m23);
        floatBuffer.put(Matrix4f.bufferIndex(3, 0), this.m30);
        floatBuffer.put(Matrix4f.bufferIndex(3, 1), this.m31);
        floatBuffer.put(Matrix4f.bufferIndex(3, 2), this.m32);
        floatBuffer.put(Matrix4f.bufferIndex(3, 3), this.m33);
    }

    public void setIdentity() {
        this.m00 = 1.0f;
        this.m01 = 0.0f;
        this.m02 = 0.0f;
        this.m03 = 0.0f;
        this.m10 = 0.0f;
        this.m11 = 1.0f;
        this.m12 = 0.0f;
        this.m13 = 0.0f;
        this.m20 = 0.0f;
        this.m21 = 0.0f;
        this.m22 = 1.0f;
        this.m23 = 0.0f;
        this.m30 = 0.0f;
        this.m31 = 0.0f;
        this.m32 = 0.0f;
        this.m33 = 1.0f;
    }

    public float adjugateAndDet() {
        float f = this.m00 * this.m11 - this.m01 * this.m10;
        float f2 = this.m00 * this.m12 - this.m02 * this.m10;
        float f3 = this.m00 * this.m13 - this.m03 * this.m10;
        float f4 = this.m01 * this.m12 - this.m02 * this.m11;
        float f5 = this.m01 * this.m13 - this.m03 * this.m11;
        float f6 = this.m02 * this.m13 - this.m03 * this.m12;
        float f7 = this.m20 * this.m31 - this.m21 * this.m30;
        float f8 = this.m20 * this.m32 - this.m22 * this.m30;
        float f9 = this.m20 * this.m33 - this.m23 * this.m30;
        float f10 = this.m21 * this.m32 - this.m22 * this.m31;
        float f11 = this.m21 * this.m33 - this.m23 * this.m31;
        float f12 = this.m22 * this.m33 - this.m23 * this.m32;
        float f13 = this.m11 * f12 - this.m12 * f11 + this.m13 * f10;
        float f14 = -this.m10 * f12 + this.m12 * f9 - this.m13 * f8;
        float f15 = this.m10 * f11 - this.m11 * f9 + this.m13 * f7;
        float f16 = -this.m10 * f10 + this.m11 * f8 - this.m12 * f7;
        float f17 = -this.m01 * f12 + this.m02 * f11 - this.m03 * f10;
        float f18 = this.m00 * f12 - this.m02 * f9 + this.m03 * f8;
        float f19 = -this.m00 * f11 + this.m01 * f9 - this.m03 * f7;
        float f20 = this.m00 * f10 - this.m01 * f8 + this.m02 * f7;
        float f21 = this.m31 * f6 - this.m32 * f5 + this.m33 * f4;
        float f22 = -this.m30 * f6 + this.m32 * f3 - this.m33 * f2;
        float f23 = this.m30 * f5 - this.m31 * f3 + this.m33 * f;
        float f24 = -this.m30 * f4 + this.m31 * f2 - this.m32 * f;
        float f25 = -this.m21 * f6 + this.m22 * f5 - this.m23 * f4;
        float f26 = this.m20 * f6 - this.m22 * f3 + this.m23 * f2;
        float f27 = -this.m20 * f5 + this.m21 * f3 - this.m23 * f;
        float f28 = this.m20 * f4 - this.m21 * f2 + this.m22 * f;
        this.m00 = f13;
        this.m10 = f14;
        this.m20 = f15;
        this.m30 = f16;
        this.m01 = f17;
        this.m11 = f18;
        this.m21 = f19;
        this.m31 = f20;
        this.m02 = f21;
        this.m12 = f22;
        this.m22 = f23;
        this.m32 = f24;
        this.m03 = f25;
        this.m13 = f26;
        this.m23 = f27;
        this.m33 = f28;
        return f * f12 - f2 * f11 + f3 * f10 + f4 * f9 - f5 * f8 + f6 * f7;
    }

    public void transpose() {
        float f = this.m10;
        this.m10 = this.m01;
        this.m01 = f;
        f = this.m20;
        this.m20 = this.m02;
        this.m02 = f;
        f = this.m21;
        this.m21 = this.m12;
        this.m12 = f;
        f = this.m30;
        this.m30 = this.m03;
        this.m03 = f;
        f = this.m31;
        this.m31 = this.m13;
        this.m13 = f;
        f = this.m32;
        this.m32 = this.m23;
        this.m23 = f;
    }

    public boolean invert() {
        float f = this.adjugateAndDet();
        if (Math.abs(f) > 1.0E-6f) {
            this.multiply(f);
            return true;
        }
        return false;
    }

    public void multiply(Matrix4f matrix4f) {
        float f = this.m00 * matrix4f.m00 + this.m01 * matrix4f.m10 + this.m02 * matrix4f.m20 + this.m03 * matrix4f.m30;
        float f2 = this.m00 * matrix4f.m01 + this.m01 * matrix4f.m11 + this.m02 * matrix4f.m21 + this.m03 * matrix4f.m31;
        float f3 = this.m00 * matrix4f.m02 + this.m01 * matrix4f.m12 + this.m02 * matrix4f.m22 + this.m03 * matrix4f.m32;
        float f4 = this.m00 * matrix4f.m03 + this.m01 * matrix4f.m13 + this.m02 * matrix4f.m23 + this.m03 * matrix4f.m33;
        float f5 = this.m10 * matrix4f.m00 + this.m11 * matrix4f.m10 + this.m12 * matrix4f.m20 + this.m13 * matrix4f.m30;
        float f6 = this.m10 * matrix4f.m01 + this.m11 * matrix4f.m11 + this.m12 * matrix4f.m21 + this.m13 * matrix4f.m31;
        float f7 = this.m10 * matrix4f.m02 + this.m11 * matrix4f.m12 + this.m12 * matrix4f.m22 + this.m13 * matrix4f.m32;
        float f8 = this.m10 * matrix4f.m03 + this.m11 * matrix4f.m13 + this.m12 * matrix4f.m23 + this.m13 * matrix4f.m33;
        float f9 = this.m20 * matrix4f.m00 + this.m21 * matrix4f.m10 + this.m22 * matrix4f.m20 + this.m23 * matrix4f.m30;
        float f10 = this.m20 * matrix4f.m01 + this.m21 * matrix4f.m11 + this.m22 * matrix4f.m21 + this.m23 * matrix4f.m31;
        float f11 = this.m20 * matrix4f.m02 + this.m21 * matrix4f.m12 + this.m22 * matrix4f.m22 + this.m23 * matrix4f.m32;
        float f12 = this.m20 * matrix4f.m03 + this.m21 * matrix4f.m13 + this.m22 * matrix4f.m23 + this.m23 * matrix4f.m33;
        float f13 = this.m30 * matrix4f.m00 + this.m31 * matrix4f.m10 + this.m32 * matrix4f.m20 + this.m33 * matrix4f.m30;
        float f14 = this.m30 * matrix4f.m01 + this.m31 * matrix4f.m11 + this.m32 * matrix4f.m21 + this.m33 * matrix4f.m31;
        float f15 = this.m30 * matrix4f.m02 + this.m31 * matrix4f.m12 + this.m32 * matrix4f.m22 + this.m33 * matrix4f.m32;
        float f16 = this.m30 * matrix4f.m03 + this.m31 * matrix4f.m13 + this.m32 * matrix4f.m23 + this.m33 * matrix4f.m33;
        this.m00 = f;
        this.m01 = f2;
        this.m02 = f3;
        this.m03 = f4;
        this.m10 = f5;
        this.m11 = f6;
        this.m12 = f7;
        this.m13 = f8;
        this.m20 = f9;
        this.m21 = f10;
        this.m22 = f11;
        this.m23 = f12;
        this.m30 = f13;
        this.m31 = f14;
        this.m32 = f15;
        this.m33 = f16;
    }

    public void multiply(Quaternion quaternion) {
        this.multiply(new Matrix4f(quaternion));
    }

    public void multiply(float f) {
        this.m00 *= f;
        this.m01 *= f;
        this.m02 *= f;
        this.m03 *= f;
        this.m10 *= f;
        this.m11 *= f;
        this.m12 *= f;
        this.m13 *= f;
        this.m20 *= f;
        this.m21 *= f;
        this.m22 *= f;
        this.m23 *= f;
        this.m30 *= f;
        this.m31 *= f;
        this.m32 *= f;
        this.m33 *= f;
    }

    public static Matrix4f perspective(double d, float f, float f2, float f3) {
        float f4 = (float)(1.0 / Math.tan(d * 0.01745329238474369 / 2.0));
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.m00 = f4 / f;
        matrix4f.m11 = f4;
        matrix4f.m22 = (f3 + f2) / (f2 - f3);
        matrix4f.m32 = -1.0f;
        matrix4f.m23 = 2.0f * f3 * f2 / (f2 - f3);
        return matrix4f;
    }

    public static Matrix4f orthographic(float f, float f2, float f3, float f4) {
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.m00 = 2.0f / f;
        matrix4f.m11 = 2.0f / f2;
        float f5 = f4 - f3;
        matrix4f.m22 = -2.0f / f5;
        matrix4f.m33 = 1.0f;
        matrix4f.m03 = -1.0f;
        matrix4f.m13 = -1.0f;
        matrix4f.m23 = -(f4 + f3) / f5;
        return matrix4f;
    }

    public void translate(Vector3f vector3f) {
        this.m03 += vector3f.x();
        this.m13 += vector3f.y();
        this.m23 += vector3f.z();
    }

    public Matrix4f copy() {
        return new Matrix4f(this);
    }

    public static Matrix4f createScaleMatrix(float f, float f2, float f3) {
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.m00 = f;
        matrix4f.m11 = f2;
        matrix4f.m22 = f3;
        matrix4f.m33 = 1.0f;
        return matrix4f;
    }

    public static Matrix4f createTranslateMatrix(float f, float f2, float f3) {
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.m00 = 1.0f;
        matrix4f.m11 = 1.0f;
        matrix4f.m22 = 1.0f;
        matrix4f.m33 = 1.0f;
        matrix4f.m03 = f;
        matrix4f.m13 = f2;
        matrix4f.m23 = f3;
        return matrix4f;
    }
}

