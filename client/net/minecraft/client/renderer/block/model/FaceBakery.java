/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer.block.model;

import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.FaceInfo;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockElementRotation;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.BlockMath;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class FaceBakery {
    private static final float RESCALE_22_5 = 1.0f / (float)Math.cos(0.39269909262657166) - 1.0f;
    private static final float RESCALE_45 = 1.0f / (float)Math.cos(0.7853981852531433) - 1.0f;

    public BakedQuad bakeQuad(Vector3f vector3f, Vector3f vector3f2, BlockElementFace blockElementFace, TextureAtlasSprite textureAtlasSprite, Direction direction, ModelState modelState, @Nullable BlockElementRotation blockElementRotation, boolean bl, ResourceLocation resourceLocation) {
        BlockFaceUV blockFaceUV = blockElementFace.uv;
        if (modelState.isUvLocked()) {
            blockFaceUV = FaceBakery.recomputeUVs(blockElementFace.uv, direction, modelState.getRotation(), resourceLocation);
        }
        float[] arrf = new float[blockFaceUV.uvs.length];
        System.arraycopy(blockFaceUV.uvs, 0, arrf, 0, arrf.length);
        float f = textureAtlasSprite.uvShrinkRatio();
        float f2 = (blockFaceUV.uvs[0] + blockFaceUV.uvs[0] + blockFaceUV.uvs[2] + blockFaceUV.uvs[2]) / 4.0f;
        float f3 = (blockFaceUV.uvs[1] + blockFaceUV.uvs[1] + blockFaceUV.uvs[3] + blockFaceUV.uvs[3]) / 4.0f;
        blockFaceUV.uvs[0] = Mth.lerp(f, blockFaceUV.uvs[0], f2);
        blockFaceUV.uvs[2] = Mth.lerp(f, blockFaceUV.uvs[2], f2);
        blockFaceUV.uvs[1] = Mth.lerp(f, blockFaceUV.uvs[1], f3);
        blockFaceUV.uvs[3] = Mth.lerp(f, blockFaceUV.uvs[3], f3);
        int[] arrn = this.makeVertices(blockFaceUV, textureAtlasSprite, direction, this.setupShape(vector3f, vector3f2), modelState.getRotation(), blockElementRotation, bl);
        Direction direction2 = FaceBakery.calculateFacing(arrn);
        System.arraycopy(arrf, 0, blockFaceUV.uvs, 0, arrf.length);
        if (blockElementRotation == null) {
            this.recalculateWinding(arrn, direction2);
        }
        return new BakedQuad(arrn, blockElementFace.tintIndex, direction2, textureAtlasSprite, bl);
    }

    public static BlockFaceUV recomputeUVs(BlockFaceUV blockFaceUV, Direction direction, Transformation transformation, ResourceLocation resourceLocation) {
        float f;
        float f2;
        float f3;
        float f4;
        Matrix4f matrix4f = BlockMath.getUVLockTransform(transformation, direction, () -> "Unable to resolve UVLock for model: " + resourceLocation).getMatrix();
        float f5 = blockFaceUV.getU(blockFaceUV.getReverseIndex(0));
        float f6 = blockFaceUV.getV(blockFaceUV.getReverseIndex(0));
        Vector4f vector4f = new Vector4f(f5 / 16.0f, f6 / 16.0f, 0.0f, 1.0f);
        vector4f.transform(matrix4f);
        float f7 = 16.0f * vector4f.x();
        float f8 = 16.0f * vector4f.y();
        float f9 = blockFaceUV.getU(blockFaceUV.getReverseIndex(2));
        float f10 = blockFaceUV.getV(blockFaceUV.getReverseIndex(2));
        Vector4f vector4f2 = new Vector4f(f9 / 16.0f, f10 / 16.0f, 0.0f, 1.0f);
        vector4f2.transform(matrix4f);
        float f11 = 16.0f * vector4f2.x();
        float f12 = 16.0f * vector4f2.y();
        if (Math.signum(f9 - f5) == Math.signum(f11 - f7)) {
            f = f7;
            f4 = f11;
        } else {
            f = f11;
            f4 = f7;
        }
        if (Math.signum(f10 - f6) == Math.signum(f12 - f8)) {
            f3 = f8;
            f2 = f12;
        } else {
            f3 = f12;
            f2 = f8;
        }
        float f13 = (float)Math.toRadians(blockFaceUV.rotation);
        Vector3f vector3f = new Vector3f(Mth.cos(f13), Mth.sin(f13), 0.0f);
        Matrix3f matrix3f = new Matrix3f(matrix4f);
        vector3f.transform(matrix3f);
        int n = Math.floorMod(-((int)Math.round(Math.toDegrees(Math.atan2(vector3f.y(), vector3f.x())) / 90.0)) * 90, 360);
        return new BlockFaceUV(new float[]{f, f3, f4, f2}, n);
    }

    private int[] makeVertices(BlockFaceUV blockFaceUV, TextureAtlasSprite textureAtlasSprite, Direction direction, float[] arrf, Transformation transformation, @Nullable BlockElementRotation blockElementRotation, boolean bl) {
        int[] arrn = new int[32];
        for (int i = 0; i < 4; ++i) {
            this.bakeVertex(arrn, i, direction, blockFaceUV, arrf, textureAtlasSprite, transformation, blockElementRotation, bl);
        }
        return arrn;
    }

    private float[] setupShape(Vector3f vector3f, Vector3f vector3f2) {
        float[] arrf = new float[Direction.values().length];
        arrf[FaceInfo.Constants.MIN_X] = vector3f.x() / 16.0f;
        arrf[FaceInfo.Constants.MIN_Y] = vector3f.y() / 16.0f;
        arrf[FaceInfo.Constants.MIN_Z] = vector3f.z() / 16.0f;
        arrf[FaceInfo.Constants.MAX_X] = vector3f2.x() / 16.0f;
        arrf[FaceInfo.Constants.MAX_Y] = vector3f2.y() / 16.0f;
        arrf[FaceInfo.Constants.MAX_Z] = vector3f2.z() / 16.0f;
        return arrf;
    }

    private void bakeVertex(int[] arrn, int n, Direction direction, BlockFaceUV blockFaceUV, float[] arrf, TextureAtlasSprite textureAtlasSprite, Transformation transformation, @Nullable BlockElementRotation blockElementRotation, boolean bl) {
        FaceInfo.VertexInfo vertexInfo = FaceInfo.fromFacing(direction).getVertexInfo(n);
        Vector3f vector3f = new Vector3f(arrf[vertexInfo.xFace], arrf[vertexInfo.yFace], arrf[vertexInfo.zFace]);
        this.applyElementRotation(vector3f, blockElementRotation);
        this.applyModelRotation(vector3f, transformation);
        this.fillVertex(arrn, n, vector3f, textureAtlasSprite, blockFaceUV);
    }

    private void fillVertex(int[] arrn, int n, Vector3f vector3f, TextureAtlasSprite textureAtlasSprite, BlockFaceUV blockFaceUV) {
        int n2 = n * 8;
        arrn[n2] = Float.floatToRawIntBits(vector3f.x());
        arrn[n2 + 1] = Float.floatToRawIntBits(vector3f.y());
        arrn[n2 + 2] = Float.floatToRawIntBits(vector3f.z());
        arrn[n2 + 3] = -1;
        arrn[n2 + 4] = Float.floatToRawIntBits(textureAtlasSprite.getU(blockFaceUV.getU(n)));
        arrn[n2 + 4 + 1] = Float.floatToRawIntBits(textureAtlasSprite.getV(blockFaceUV.getV(n)));
    }

    private void applyElementRotation(Vector3f vector3f, @Nullable BlockElementRotation blockElementRotation) {
        Vector3f vector3f2;
        Vector3f vector3f3;
        if (blockElementRotation == null) {
            return;
        }
        switch (blockElementRotation.axis) {
            case X: {
                vector3f3 = new Vector3f(1.0f, 0.0f, 0.0f);
                vector3f2 = new Vector3f(0.0f, 1.0f, 1.0f);
                break;
            }
            case Y: {
                vector3f3 = new Vector3f(0.0f, 1.0f, 0.0f);
                vector3f2 = new Vector3f(1.0f, 0.0f, 1.0f);
                break;
            }
            case Z: {
                vector3f3 = new Vector3f(0.0f, 0.0f, 1.0f);
                vector3f2 = new Vector3f(1.0f, 1.0f, 0.0f);
                break;
            }
            default: {
                throw new IllegalArgumentException("There are only 3 axes");
            }
        }
        Quaternion quaternion = new Quaternion(vector3f3, blockElementRotation.angle, true);
        if (blockElementRotation.rescale) {
            if (Math.abs(blockElementRotation.angle) == 22.5f) {
                vector3f2.mul(RESCALE_22_5);
            } else {
                vector3f2.mul(RESCALE_45);
            }
            vector3f2.add(1.0f, 1.0f, 1.0f);
        } else {
            vector3f2.set(1.0f, 1.0f, 1.0f);
        }
        this.rotateVertexBy(vector3f, blockElementRotation.origin.copy(), new Matrix4f(quaternion), vector3f2);
    }

    public void applyModelRotation(Vector3f vector3f, Transformation transformation) {
        if (transformation == Transformation.identity()) {
            return;
        }
        this.rotateVertexBy(vector3f, new Vector3f(0.5f, 0.5f, 0.5f), transformation.getMatrix(), new Vector3f(1.0f, 1.0f, 1.0f));
    }

    private void rotateVertexBy(Vector3f vector3f, Vector3f vector3f2, Matrix4f matrix4f, Vector3f vector3f3) {
        Vector4f vector4f = new Vector4f(vector3f.x() - vector3f2.x(), vector3f.y() - vector3f2.y(), vector3f.z() - vector3f2.z(), 1.0f);
        vector4f.transform(matrix4f);
        vector4f.mul(vector3f3);
        vector3f.set(vector4f.x() + vector3f2.x(), vector4f.y() + vector3f2.y(), vector4f.z() + vector3f2.z());
    }

    public static Direction calculateFacing(int[] arrn) {
        Vector3f vector3f = new Vector3f(Float.intBitsToFloat(arrn[0]), Float.intBitsToFloat(arrn[1]), Float.intBitsToFloat(arrn[2]));
        Vector3f vector3f2 = new Vector3f(Float.intBitsToFloat(arrn[8]), Float.intBitsToFloat(arrn[9]), Float.intBitsToFloat(arrn[10]));
        Vector3f vector3f3 = new Vector3f(Float.intBitsToFloat(arrn[16]), Float.intBitsToFloat(arrn[17]), Float.intBitsToFloat(arrn[18]));
        Vector3f vector3f4 = vector3f.copy();
        vector3f4.sub(vector3f2);
        Vector3f vector3f5 = vector3f3.copy();
        vector3f5.sub(vector3f2);
        Vector3f vector3f6 = vector3f5.copy();
        vector3f6.cross(vector3f4);
        vector3f6.normalize();
        Direction direction = null;
        float f = 0.0f;
        for (Direction direction2 : Direction.values()) {
            Vec3i vec3i = direction2.getNormal();
            Vector3f vector3f7 = new Vector3f(vec3i.getX(), vec3i.getY(), vec3i.getZ());
            float f2 = vector3f6.dot(vector3f7);
            if (!(f2 >= 0.0f) || !(f2 > f)) continue;
            f = f2;
            direction = direction2;
        }
        if (direction == null) {
            return Direction.UP;
        }
        return direction;
    }

    private void recalculateWinding(int[] arrn, Direction direction) {
        int n;
        float f;
        int[] arrn2 = new int[arrn.length];
        System.arraycopy(arrn, 0, arrn2, 0, arrn.length);
        float[] arrf = new float[Direction.values().length];
        arrf[FaceInfo.Constants.MIN_X] = 999.0f;
        arrf[FaceInfo.Constants.MIN_Y] = 999.0f;
        arrf[FaceInfo.Constants.MIN_Z] = 999.0f;
        arrf[FaceInfo.Constants.MAX_X] = -999.0f;
        arrf[FaceInfo.Constants.MAX_Y] = -999.0f;
        arrf[FaceInfo.Constants.MAX_Z] = -999.0f;
        for (int i = 0; i < 4; ++i) {
            n = 8 * i;
            float f2 = Float.intBitsToFloat(arrn2[n]);
            float f3 = Float.intBitsToFloat(arrn2[n + 1]);
            f = Float.intBitsToFloat(arrn2[n + 2]);
            if (f2 < arrf[FaceInfo.Constants.MIN_X]) {
                arrf[FaceInfo.Constants.MIN_X] = f2;
            }
            if (f3 < arrf[FaceInfo.Constants.MIN_Y]) {
                arrf[FaceInfo.Constants.MIN_Y] = f3;
            }
            if (f < arrf[FaceInfo.Constants.MIN_Z]) {
                arrf[FaceInfo.Constants.MIN_Z] = f;
            }
            if (f2 > arrf[FaceInfo.Constants.MAX_X]) {
                arrf[FaceInfo.Constants.MAX_X] = f2;
            }
            if (f3 > arrf[FaceInfo.Constants.MAX_Y]) {
                arrf[FaceInfo.Constants.MAX_Y] = f3;
            }
            if (!(f > arrf[FaceInfo.Constants.MAX_Z])) continue;
            arrf[FaceInfo.Constants.MAX_Z] = f;
        }
        FaceInfo faceInfo = FaceInfo.fromFacing(direction);
        for (n = 0; n < 4; ++n) {
            int n2 = 8 * n;
            FaceInfo.VertexInfo vertexInfo = faceInfo.getVertexInfo(n);
            f = arrf[vertexInfo.xFace];
            float f4 = arrf[vertexInfo.yFace];
            float f5 = arrf[vertexInfo.zFace];
            arrn[n2] = Float.floatToRawIntBits(f);
            arrn[n2 + 1] = Float.floatToRawIntBits(f4);
            arrn[n2 + 2] = Float.floatToRawIntBits(f5);
            for (int i = 0; i < 4; ++i) {
                int n3 = 8 * i;
                float f6 = Float.intBitsToFloat(arrn2[n3]);
                float f7 = Float.intBitsToFloat(arrn2[n3 + 1]);
                float f8 = Float.intBitsToFloat(arrn2[n3 + 2]);
                if (!Mth.equal(f, f6) || !Mth.equal(f4, f7) || !Mth.equal(f5, f8)) continue;
                arrn[n2 + 4] = arrn2[n3 + 4];
                arrn[n2 + 4 + 1] = arrn2[n3 + 4 + 1];
            }
        }
    }

}

