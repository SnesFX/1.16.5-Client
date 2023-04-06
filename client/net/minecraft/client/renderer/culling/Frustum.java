/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.culling;

import com.mojang.math.Matrix4f;
import com.mojang.math.Vector4f;
import net.minecraft.world.phys.AABB;

public class Frustum {
    private final Vector4f[] frustumData = new Vector4f[6];
    private double camX;
    private double camY;
    private double camZ;

    public Frustum(Matrix4f matrix4f, Matrix4f matrix4f2) {
        this.calculateFrustum(matrix4f, matrix4f2);
    }

    public void prepare(double d, double d2, double d3) {
        this.camX = d;
        this.camY = d2;
        this.camZ = d3;
    }

    private void calculateFrustum(Matrix4f matrix4f, Matrix4f matrix4f2) {
        Matrix4f matrix4f3 = matrix4f2.copy();
        matrix4f3.multiply(matrix4f);
        matrix4f3.transpose();
        this.getPlane(matrix4f3, -1, 0, 0, 0);
        this.getPlane(matrix4f3, 1, 0, 0, 1);
        this.getPlane(matrix4f3, 0, -1, 0, 2);
        this.getPlane(matrix4f3, 0, 1, 0, 3);
        this.getPlane(matrix4f3, 0, 0, -1, 4);
        this.getPlane(matrix4f3, 0, 0, 1, 5);
    }

    private void getPlane(Matrix4f matrix4f, int n, int n2, int n3, int n4) {
        Vector4f vector4f = new Vector4f(n, n2, n3, 1.0f);
        vector4f.transform(matrix4f);
        vector4f.normalize();
        this.frustumData[n4] = vector4f;
    }

    public boolean isVisible(AABB aABB) {
        return this.cubeInFrustum(aABB.minX, aABB.minY, aABB.minZ, aABB.maxX, aABB.maxY, aABB.maxZ);
    }

    private boolean cubeInFrustum(double d, double d2, double d3, double d4, double d5, double d6) {
        float f = (float)(d - this.camX);
        float f2 = (float)(d2 - this.camY);
        float f3 = (float)(d3 - this.camZ);
        float f4 = (float)(d4 - this.camX);
        float f5 = (float)(d5 - this.camY);
        float f6 = (float)(d6 - this.camZ);
        return this.cubeInFrustum(f, f2, f3, f4, f5, f6);
    }

    private boolean cubeInFrustum(float f, float f2, float f3, float f4, float f5, float f6) {
        for (int i = 0; i < 6; ++i) {
            Vector4f vector4f = this.frustumData[i];
            if (vector4f.dot(new Vector4f(f, f2, f3, 1.0f)) > 0.0f) continue;
            if (vector4f.dot(new Vector4f(f4, f2, f3, 1.0f)) > 0.0f) continue;
            if (vector4f.dot(new Vector4f(f, f5, f3, 1.0f)) > 0.0f) continue;
            if (vector4f.dot(new Vector4f(f4, f5, f3, 1.0f)) > 0.0f) continue;
            if (vector4f.dot(new Vector4f(f, f2, f6, 1.0f)) > 0.0f) continue;
            if (vector4f.dot(new Vector4f(f4, f2, f6, 1.0f)) > 0.0f) continue;
            if (vector4f.dot(new Vector4f(f, f5, f6, 1.0f)) > 0.0f) continue;
            if (vector4f.dot(new Vector4f(f4, f5, f6, 1.0f)) > 0.0f) continue;
            return false;
        }
        return true;
    }
}

