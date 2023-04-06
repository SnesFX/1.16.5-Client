/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.lwjgl.system.MemoryStack
 */
package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.MemoryStack;

public interface VertexConsumer {
    public static final Logger LOGGER = LogManager.getLogger();

    public VertexConsumer vertex(double var1, double var3, double var5);

    public VertexConsumer color(int var1, int var2, int var3, int var4);

    public VertexConsumer uv(float var1, float var2);

    public VertexConsumer overlayCoords(int var1, int var2);

    public VertexConsumer uv2(int var1, int var2);

    public VertexConsumer normal(float var1, float var2, float var3);

    public void endVertex();

    default public void vertex(float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, int n, int n2, float f10, float f11, float f12) {
        this.vertex(f, f2, f3);
        this.color(f4, f5, f6, f7);
        this.uv(f8, f9);
        this.overlayCoords(n);
        this.uv2(n2);
        this.normal(f10, f11, f12);
        this.endVertex();
    }

    default public VertexConsumer color(float f, float f2, float f3, float f4) {
        return this.color((int)(f * 255.0f), (int)(f2 * 255.0f), (int)(f3 * 255.0f), (int)(f4 * 255.0f));
    }

    default public VertexConsumer uv2(int n) {
        return this.uv2(n & 0xFFFF, n >> 16 & 0xFFFF);
    }

    default public VertexConsumer overlayCoords(int n) {
        return this.overlayCoords(n & 0xFFFF, n >> 16 & 0xFFFF);
    }

    default public void putBulkData(PoseStack.Pose pose, BakedQuad bakedQuad, float f, float f2, float f3, int n, int n2) {
        this.putBulkData(pose, bakedQuad, new float[]{1.0f, 1.0f, 1.0f, 1.0f}, f, f2, f3, new int[]{n, n, n, n}, n2, false);
    }

    default public void putBulkData(PoseStack.Pose pose, BakedQuad bakedQuad, float[] arrf, float f, float f2, float f3, int[] arrn, int n, boolean bl) {
        int[] arrn2 = bakedQuad.getVertices();
        Vec3i vec3i = bakedQuad.getDirection().getNormal();
        Vector3f vector3f = new Vector3f(vec3i.getX(), vec3i.getY(), vec3i.getZ());
        Matrix4f matrix4f = pose.pose();
        vector3f.transform(pose.normal());
        int n2 = 8;
        int n3 = arrn2.length / 8;
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            ByteBuffer byteBuffer = memoryStack.malloc(DefaultVertexFormat.BLOCK.getVertexSize());
            IntBuffer intBuffer = byteBuffer.asIntBuffer();
            for (int i = 0; i < n3; ++i) {
                float f4;
                float f5;
                float f6;
                float f7;
                float f8;
                intBuffer.clear();
                intBuffer.put(arrn2, i * 8, 8);
                float f9 = byteBuffer.getFloat(0);
                float f10 = byteBuffer.getFloat(4);
                float f11 = byteBuffer.getFloat(8);
                if (bl) {
                    float f12 = (float)(byteBuffer.get(12) & 0xFF) / 255.0f;
                    f8 = (float)(byteBuffer.get(13) & 0xFF) / 255.0f;
                    f7 = (float)(byteBuffer.get(14) & 0xFF) / 255.0f;
                    f6 = f12 * arrf[i] * f;
                    f5 = f8 * arrf[i] * f2;
                    f4 = f7 * arrf[i] * f3;
                } else {
                    f6 = arrf[i] * f;
                    f5 = arrf[i] * f2;
                    f4 = arrf[i] * f3;
                }
                int n4 = arrn[i];
                f8 = byteBuffer.getFloat(16);
                f7 = byteBuffer.getFloat(20);
                Vector4f vector4f = new Vector4f(f9, f10, f11, 1.0f);
                vector4f.transform(matrix4f);
                this.vertex(vector4f.x(), vector4f.y(), vector4f.z(), f6, f5, f4, 1.0f, f8, f7, n, n4, vector3f.x(), vector3f.y(), vector3f.z());
            }
        }
    }

    default public VertexConsumer vertex(Matrix4f matrix4f, float f, float f2, float f3) {
        Vector4f vector4f = new Vector4f(f, f2, f3, 1.0f);
        vector4f.transform(matrix4f);
        return this.vertex(vector4f.x(), vector4f.y(), vector4f.z());
    }

    default public VertexConsumer normal(Matrix3f matrix3f, float f, float f2, float f3) {
        Vector3f vector3f = new Vector3f(f, f2, f3);
        vector3f.transform(matrix3f);
        return this.normal(vector3f.x(), vector3f.y(), vector3f.z());
    }
}

