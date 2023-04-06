/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Queues
 */
package com.mojang.blaze3d.vertex;

import com.google.common.collect.Queues;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.Util;
import net.minecraft.util.Mth;

public class PoseStack {
    private final Deque<Pose> poseStack = Util.make(Queues.newArrayDeque(), arrayDeque -> {
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.setIdentity();
        Matrix3f matrix3f = new Matrix3f();
        matrix3f.setIdentity();
        arrayDeque.add(new Pose(matrix4f, matrix3f));
    });

    public void translate(double d, double d2, double d3) {
        Pose pose = this.poseStack.getLast();
        pose.pose.multiply(Matrix4f.createTranslateMatrix((float)d, (float)d2, (float)d3));
    }

    public void scale(float f, float f2, float f3) {
        Pose pose = this.poseStack.getLast();
        pose.pose.multiply(Matrix4f.createScaleMatrix(f, f2, f3));
        if (f == f2 && f2 == f3) {
            if (f > 0.0f) {
                return;
            }
            pose.normal.mul(-1.0f);
        }
        float f4 = 1.0f / f;
        float f5 = 1.0f / f2;
        float f6 = 1.0f / f3;
        float f7 = Mth.fastInvCubeRoot(f4 * f5 * f6);
        pose.normal.mul(Matrix3f.createScaleMatrix(f7 * f4, f7 * f5, f7 * f6));
    }

    public void mulPose(Quaternion quaternion) {
        Pose pose = this.poseStack.getLast();
        pose.pose.multiply(quaternion);
        pose.normal.mul(quaternion);
    }

    public void pushPose() {
        Pose pose = this.poseStack.getLast();
        this.poseStack.addLast(new Pose(pose.pose.copy(), pose.normal.copy()));
    }

    public void popPose() {
        this.poseStack.removeLast();
    }

    public Pose last() {
        return this.poseStack.getLast();
    }

    public boolean clear() {
        return this.poseStack.size() == 1;
    }

    public static final class Pose {
        private final Matrix4f pose;
        private final Matrix3f normal;

        private Pose(Matrix4f matrix4f, Matrix3f matrix3f) {
            this.pose = matrix4f;
            this.normal = matrix3f;
        }

        public Matrix4f pose() {
            return this.pose;
        }

        public Matrix3f normal() {
            return this.normal;
        }
    }

}

