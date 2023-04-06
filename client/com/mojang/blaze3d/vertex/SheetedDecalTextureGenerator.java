/*
 * Decompiled with CFR 0.146.
 */
package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.vertex.DefaultedVertexConsumer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.core.Direction;

public class SheetedDecalTextureGenerator
extends DefaultedVertexConsumer {
    private final VertexConsumer delegate;
    private final Matrix4f cameraInversePose;
    private final Matrix3f normalInversePose;
    private float x;
    private float y;
    private float z;
    private int overlayU;
    private int overlayV;
    private int lightCoords;
    private float nx;
    private float ny;
    private float nz;

    public SheetedDecalTextureGenerator(VertexConsumer vertexConsumer, Matrix4f matrix4f, Matrix3f matrix3f) {
        this.delegate = vertexConsumer;
        this.cameraInversePose = matrix4f.copy();
        this.cameraInversePose.invert();
        this.normalInversePose = matrix3f.copy();
        this.normalInversePose.invert();
        this.resetState();
    }

    private void resetState() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
        this.overlayU = 0;
        this.overlayV = 10;
        this.lightCoords = 15728880;
        this.nx = 0.0f;
        this.ny = 1.0f;
        this.nz = 0.0f;
    }

    @Override
    public void endVertex() {
        Vector3f vector3f = new Vector3f(this.nx, this.ny, this.nz);
        vector3f.transform(this.normalInversePose);
        Direction direction = Direction.getNearest(vector3f.x(), vector3f.y(), vector3f.z());
        Vector4f vector4f = new Vector4f(this.x, this.y, this.z, 1.0f);
        vector4f.transform(this.cameraInversePose);
        vector4f.transform(Vector3f.YP.rotationDegrees(180.0f));
        vector4f.transform(Vector3f.XP.rotationDegrees(-90.0f));
        vector4f.transform(direction.getRotation());
        float f = -vector4f.x();
        float f2 = -vector4f.y();
        this.delegate.vertex(this.x, this.y, this.z).color(1.0f, 1.0f, 1.0f, 1.0f).uv(f, f2).overlayCoords(this.overlayU, this.overlayV).uv2(this.lightCoords).normal(this.nx, this.ny, this.nz).endVertex();
        this.resetState();
    }

    @Override
    public VertexConsumer vertex(double d, double d2, double d3) {
        this.x = (float)d;
        this.y = (float)d2;
        this.z = (float)d3;
        return this;
    }

    @Override
    public VertexConsumer color(int n, int n2, int n3, int n4) {
        return this;
    }

    @Override
    public VertexConsumer uv(float f, float f2) {
        return this;
    }

    @Override
    public VertexConsumer overlayCoords(int n, int n2) {
        this.overlayU = n;
        this.overlayV = n2;
        return this;
    }

    @Override
    public VertexConsumer uv2(int n, int n2) {
        this.lightCoords = n | n2 << 16;
        return this;
    }

    @Override
    public VertexConsumer normal(float f, float f2, float f3) {
        this.nx = f;
        this.ny = f2;
        this.nz = f3;
        return this;
    }
}

