/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.block;

import com.mojang.math.OctahedralGroup;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;

public enum Mirror {
    NONE(OctahedralGroup.IDENTITY),
    LEFT_RIGHT(OctahedralGroup.INVERT_Z),
    FRONT_BACK(OctahedralGroup.INVERT_X);
    
    private final OctahedralGroup rotation;

    private Mirror(OctahedralGroup octahedralGroup) {
        this.rotation = octahedralGroup;
    }

    public int mirror(int n, int n2) {
        int n3 = n2 / 2;
        int n4 = n > n3 ? n - n2 : n;
        switch (this) {
            case FRONT_BACK: {
                return (n2 - n4) % n2;
            }
            case LEFT_RIGHT: {
                return (n3 - n4 + n2) % n2;
            }
        }
        return n;
    }

    public Rotation getRotation(Direction direction) {
        Direction.Axis axis = direction.getAxis();
        return this == LEFT_RIGHT && axis == Direction.Axis.Z || this == FRONT_BACK && axis == Direction.Axis.X ? Rotation.CLOCKWISE_180 : Rotation.NONE;
    }

    public Direction mirror(Direction direction) {
        if (this == FRONT_BACK && direction.getAxis() == Direction.Axis.X) {
            return direction.getOpposite();
        }
        if (this == LEFT_RIGHT && direction.getAxis() == Direction.Axis.Z) {
            return direction.getOpposite();
        }
        return direction;
    }

    public OctahedralGroup rotation() {
        return this.rotation;
    }

}

