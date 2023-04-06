/*
 * Decompiled with CFR 0.146.
 */
package com.mojang.math;

import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.util.Mth;

public class Vector4f {
    private float x;
    private float y;
    private float z;
    private float w;

    public Vector4f() {
    }

    public Vector4f(float f, float f2, float f3, float f4) {
        this.x = f;
        this.y = f2;
        this.z = f3;
        this.w = f4;
    }

    public Vector4f(Vector3f vector3f) {
        this(vector3f.x(), vector3f.y(), vector3f.z(), 1.0f);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        Vector4f vector4f = (Vector4f)object;
        if (Float.compare(vector4f.x, this.x) != 0) {
            return false;
        }
        if (Float.compare(vector4f.y, this.y) != 0) {
            return false;
        }
        if (Float.compare(vector4f.z, this.z) != 0) {
            return false;
        }
        return Float.compare(vector4f.w, this.w) == 0;
    }

    public int hashCode() {
        int n = Float.floatToIntBits(this.x);
        n = 31 * n + Float.floatToIntBits(this.y);
        n = 31 * n + Float.floatToIntBits(this.z);
        n = 31 * n + Float.floatToIntBits(this.w);
        return n;
    }

    public float x() {
        return this.x;
    }

    public float y() {
        return this.y;
    }

    public float z() {
        return this.z;
    }

    public float w() {
        return this.w;
    }

    public void mul(Vector3f vector3f) {
        this.x *= vector3f.x();
        this.y *= vector3f.y();
        this.z *= vector3f.z();
    }

    public void set(float f, float f2, float f3, float f4) {
        this.x = f;
        this.y = f2;
        this.z = f3;
        this.w = f4;
    }

    public float dot(Vector4f vector4f) {
        return this.x * vector4f.x + this.y * vector4f.y + this.z * vector4f.z + this.w * vector4f.w;
    }

    public boolean normalize() {
        float f = this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
        if ((double)f < 1.0E-5) {
            return false;
        }
        float f2 = Mth.fastInvSqrt(f);
        this.x *= f2;
        this.y *= f2;
        this.z *= f2;
        this.w *= f2;
        return true;
    }

    public void transform(Matrix4f matrix4f) {
        float f = this.x;
        float f2 = this.y;
        float f3 = this.z;
        float f4 = this.w;
        this.x = matrix4f.m00 * f + matrix4f.m01 * f2 + matrix4f.m02 * f3 + matrix4f.m03 * f4;
        this.y = matrix4f.m10 * f + matrix4f.m11 * f2 + matrix4f.m12 * f3 + matrix4f.m13 * f4;
        this.z = matrix4f.m20 * f + matrix4f.m21 * f2 + matrix4f.m22 * f3 + matrix4f.m23 * f4;
        this.w = matrix4f.m30 * f + matrix4f.m31 * f2 + matrix4f.m32 * f3 + matrix4f.m33 * f4;
    }

    public void transform(Quaternion quaternion) {
        Quaternion quaternion2 = new Quaternion(quaternion);
        quaternion2.mul(new Quaternion(this.x(), this.y(), this.z(), 0.0f));
        Quaternion quaternion3 = new Quaternion(quaternion);
        quaternion3.conj();
        quaternion2.mul(quaternion3);
        this.set(quaternion2.i(), quaternion2.j(), quaternion2.k(), this.w());
    }

    public void perspectiveDivide() {
        this.x /= this.w;
        this.y /= this.w;
        this.z /= this.w;
        this.w = 1.0f;
    }

    public String toString() {
        return "[" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + "]";
    }
}

