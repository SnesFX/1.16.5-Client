/*
 * Decompiled with CFR 0.146.
 */
package com.mojang.math;

import com.mojang.math.Vector3f;
import net.minecraft.util.Mth;

public final class Quaternion {
    public static final Quaternion ONE = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
    private float i;
    private float j;
    private float k;
    private float r;

    public Quaternion(float f, float f2, float f3, float f4) {
        this.i = f;
        this.j = f2;
        this.k = f3;
        this.r = f4;
    }

    public Quaternion(Vector3f vector3f, float f, boolean bl) {
        if (bl) {
            f *= 0.017453292f;
        }
        float f2 = Quaternion.sin(f / 2.0f);
        this.i = vector3f.x() * f2;
        this.j = vector3f.y() * f2;
        this.k = vector3f.z() * f2;
        this.r = Quaternion.cos(f / 2.0f);
    }

    public Quaternion(float f, float f2, float f3, boolean bl) {
        if (bl) {
            f *= 0.017453292f;
            f2 *= 0.017453292f;
            f3 *= 0.017453292f;
        }
        float f4 = Quaternion.sin(0.5f * f);
        float f5 = Quaternion.cos(0.5f * f);
        float f6 = Quaternion.sin(0.5f * f2);
        float f7 = Quaternion.cos(0.5f * f2);
        float f8 = Quaternion.sin(0.5f * f3);
        float f9 = Quaternion.cos(0.5f * f3);
        this.i = f4 * f7 * f9 + f5 * f6 * f8;
        this.j = f5 * f6 * f9 - f4 * f7 * f8;
        this.k = f4 * f6 * f9 + f5 * f7 * f8;
        this.r = f5 * f7 * f9 - f4 * f6 * f8;
    }

    public Quaternion(Quaternion quaternion) {
        this.i = quaternion.i;
        this.j = quaternion.j;
        this.k = quaternion.k;
        this.r = quaternion.r;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        Quaternion quaternion = (Quaternion)object;
        if (Float.compare(quaternion.i, this.i) != 0) {
            return false;
        }
        if (Float.compare(quaternion.j, this.j) != 0) {
            return false;
        }
        if (Float.compare(quaternion.k, this.k) != 0) {
            return false;
        }
        return Float.compare(quaternion.r, this.r) == 0;
    }

    public int hashCode() {
        int n = Float.floatToIntBits(this.i);
        n = 31 * n + Float.floatToIntBits(this.j);
        n = 31 * n + Float.floatToIntBits(this.k);
        n = 31 * n + Float.floatToIntBits(this.r);
        return n;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Quaternion[").append(this.r()).append(" + ");
        stringBuilder.append(this.i()).append("i + ");
        stringBuilder.append(this.j()).append("j + ");
        stringBuilder.append(this.k()).append("k]");
        return stringBuilder.toString();
    }

    public float i() {
        return this.i;
    }

    public float j() {
        return this.j;
    }

    public float k() {
        return this.k;
    }

    public float r() {
        return this.r;
    }

    public void mul(Quaternion quaternion) {
        float f = this.i();
        float f2 = this.j();
        float f3 = this.k();
        float f4 = this.r();
        float f5 = quaternion.i();
        float f6 = quaternion.j();
        float f7 = quaternion.k();
        float f8 = quaternion.r();
        this.i = f4 * f5 + f * f8 + f2 * f7 - f3 * f6;
        this.j = f4 * f6 - f * f7 + f2 * f8 + f3 * f5;
        this.k = f4 * f7 + f * f6 - f2 * f5 + f3 * f8;
        this.r = f4 * f8 - f * f5 - f2 * f6 - f3 * f7;
    }

    public void mul(float f) {
        this.i *= f;
        this.j *= f;
        this.k *= f;
        this.r *= f;
    }

    public void conj() {
        this.i = -this.i;
        this.j = -this.j;
        this.k = -this.k;
    }

    public void set(float f, float f2, float f3, float f4) {
        this.i = f;
        this.j = f2;
        this.k = f3;
        this.r = f4;
    }

    private static float cos(float f) {
        return (float)Math.cos(f);
    }

    private static float sin(float f) {
        return (float)Math.sin(f);
    }

    public void normalize() {
        float f = this.i() * this.i() + this.j() * this.j() + this.k() * this.k() + this.r() * this.r();
        if (f > 1.0E-6f) {
            float f2 = Mth.fastInvSqrt(f);
            this.i *= f2;
            this.j *= f2;
            this.k *= f2;
            this.r *= f2;
        } else {
            this.i = 0.0f;
            this.j = 0.0f;
            this.k = 0.0f;
            this.r = 0.0f;
        }
    }

    public Quaternion copy() {
        return new Quaternion(this);
    }
}

