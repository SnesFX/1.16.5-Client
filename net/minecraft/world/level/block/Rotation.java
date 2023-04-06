/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.world.level.block;

import com.google.common.collect.Lists;
import com.mojang.math.OctahedralGroup;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.Util;
import net.minecraft.core.Direction;

public enum Rotation {
    NONE(OctahedralGroup.IDENTITY),
    CLOCKWISE_90(OctahedralGroup.ROT_90_Y_NEG),
    CLOCKWISE_180(OctahedralGroup.ROT_180_FACE_XZ),
    COUNTERCLOCKWISE_90(OctahedralGroup.ROT_90_Y_POS);
    
    private final OctahedralGroup rotation;

    private Rotation(OctahedralGroup octahedralGroup) {
        this.rotation = octahedralGroup;
    }

    public Rotation getRotated(Rotation rotation) {
        switch (rotation) {
            case CLOCKWISE_180: {
                switch (this) {
                    case NONE: {
                        return CLOCKWISE_180;
                    }
                    case CLOCKWISE_90: {
                        return COUNTERCLOCKWISE_90;
                    }
                    case CLOCKWISE_180: {
                        return NONE;
                    }
                    case COUNTERCLOCKWISE_90: {
                        return CLOCKWISE_90;
                    }
                }
            }
            case COUNTERCLOCKWISE_90: {
                switch (this) {
                    case NONE: {
                        return COUNTERCLOCKWISE_90;
                    }
                    case CLOCKWISE_90: {
                        return NONE;
                    }
                    case CLOCKWISE_180: {
                        return CLOCKWISE_90;
                    }
                    case COUNTERCLOCKWISE_90: {
                        return CLOCKWISE_180;
                    }
                }
            }
            case CLOCKWISE_90: {
                switch (this) {
                    case NONE: {
                        return CLOCKWISE_90;
                    }
                    case CLOCKWISE_90: {
                        return CLOCKWISE_180;
                    }
                    case CLOCKWISE_180: {
                        return COUNTERCLOCKWISE_90;
                    }
                    case COUNTERCLOCKWISE_90: {
                        return NONE;
                    }
                }
            }
        }
        return this;
    }

    public OctahedralGroup rotation() {
        return this.rotation;
    }

    public Direction rotate(Direction direction) {
        if (direction.getAxis() == Direction.Axis.Y) {
            return direction;
        }
        switch (this) {
            case CLOCKWISE_180: {
                return direction.getOpposite();
            }
            case COUNTERCLOCKWISE_90: {
                return direction.getCounterClockWise();
            }
            case CLOCKWISE_90: {
                return direction.getClockWise();
            }
        }
        return direction;
    }

    public int rotate(int n, int n2) {
        switch (this) {
            case CLOCKWISE_180: {
                return (n + n2 / 2) % n2;
            }
            case COUNTERCLOCKWISE_90: {
                return (n + n2 * 3 / 4) % n2;
            }
            case CLOCKWISE_90: {
                return (n + n2 / 4) % n2;
            }
        }
        return n;
    }

    public static Rotation getRandom(Random random) {
        return Util.getRandom(Rotation.values(), random);
    }

    public static List<Rotation> getShuffled(Random random) {
        ArrayList arrayList = Lists.newArrayList((Object[])Rotation.values());
        Collections.shuffle(arrayList, random);
        return arrayList;
    }

}

