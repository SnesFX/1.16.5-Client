/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.resources.model;

import com.mojang.math.OctahedralGroup;
import com.mojang.math.Quaternion;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.util.Mth;

public enum BlockModelRotation implements ModelState
{
    X0_Y0(0, 0),
    X0_Y90(0, 90),
    X0_Y180(0, 180),
    X0_Y270(0, 270),
    X90_Y0(90, 0),
    X90_Y90(90, 90),
    X90_Y180(90, 180),
    X90_Y270(90, 270),
    X180_Y0(180, 0),
    X180_Y90(180, 90),
    X180_Y180(180, 180),
    X180_Y270(180, 270),
    X270_Y0(270, 0),
    X270_Y90(270, 90),
    X270_Y180(270, 180),
    X270_Y270(270, 270);
    
    private static final Map<Integer, BlockModelRotation> BY_INDEX;
    private final Transformation transformation;
    private final OctahedralGroup actualRotation;
    private final int index;

    private static int getIndex(int n, int n2) {
        return n * 360 + n2;
    }

    private BlockModelRotation(int n2, int n3) {
        int n4;
        this.index = BlockModelRotation.getIndex(n2, n3);
        Quaternion quaternion = new Quaternion(new Vector3f(0.0f, 1.0f, 0.0f), -n3, true);
        quaternion.mul(new Quaternion(new Vector3f(1.0f, 0.0f, 0.0f), -n2, true));
        OctahedralGroup octahedralGroup = OctahedralGroup.IDENTITY;
        for (n4 = 0; n4 < n3; n4 += 90) {
            octahedralGroup = octahedralGroup.compose(OctahedralGroup.ROT_90_Y_NEG);
        }
        for (n4 = 0; n4 < n2; n4 += 90) {
            octahedralGroup = octahedralGroup.compose(OctahedralGroup.ROT_90_X_NEG);
        }
        this.transformation = new Transformation(null, quaternion, null, null);
        this.actualRotation = octahedralGroup;
    }

    @Override
    public Transformation getRotation() {
        return this.transformation;
    }

    public static BlockModelRotation by(int n, int n2) {
        return BY_INDEX.get(BlockModelRotation.getIndex(Mth.positiveModulo(n, 360), Mth.positiveModulo(n2, 360)));
    }

    static {
        BY_INDEX = Arrays.stream(BlockModelRotation.values()).collect(Collectors.toMap(blockModelRotation -> blockModelRotation.index, blockModelRotation -> blockModelRotation));
    }
}

