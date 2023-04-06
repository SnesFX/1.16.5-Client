/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterators
 *  com.mojang.serialization.Codec
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  javax.annotation.Nullable
 */
package net.minecraft.core;

import com.google.common.collect.Iterators;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;

public enum Direction implements StringRepresentable
{
    DOWN(0, 1, -1, "down", AxisDirection.NEGATIVE, Axis.Y, new Vec3i(0, -1, 0)),
    UP(1, 0, -1, "up", AxisDirection.POSITIVE, Axis.Y, new Vec3i(0, 1, 0)),
    NORTH(2, 3, 2, "north", AxisDirection.NEGATIVE, Axis.Z, new Vec3i(0, 0, -1)),
    SOUTH(3, 2, 0, "south", AxisDirection.POSITIVE, Axis.Z, new Vec3i(0, 0, 1)),
    WEST(4, 5, 1, "west", AxisDirection.NEGATIVE, Axis.X, new Vec3i(-1, 0, 0)),
    EAST(5, 4, 3, "east", AxisDirection.POSITIVE, Axis.X, new Vec3i(1, 0, 0));
    
    private final int data3d;
    private final int oppositeIndex;
    private final int data2d;
    private final String name;
    private final Axis axis;
    private final AxisDirection axisDirection;
    private final Vec3i normal;
    private static final Direction[] VALUES;
    private static final Map<String, Direction> BY_NAME;
    private static final Direction[] BY_3D_DATA;
    private static final Direction[] BY_2D_DATA;
    private static final Long2ObjectMap<Direction> BY_NORMAL;

    private Direction(int n2, int n3, int n4, String string2, AxisDirection axisDirection, Axis axis, Vec3i vec3i) {
        this.data3d = n2;
        this.data2d = n4;
        this.oppositeIndex = n3;
        this.name = string2;
        this.axis = axis;
        this.axisDirection = axisDirection;
        this.normal = vec3i;
    }

    public static Direction[] orderedByNearest(Entity entity) {
        Direction direction;
        float f = entity.getViewXRot(1.0f) * 0.017453292f;
        float f2 = -entity.getViewYRot(1.0f) * 0.017453292f;
        float f3 = Mth.sin(f);
        float f4 = Mth.cos(f);
        float f5 = Mth.sin(f2);
        float f6 = Mth.cos(f2);
        boolean bl = f5 > 0.0f;
        boolean bl2 = f3 < 0.0f;
        boolean bl3 = f6 > 0.0f;
        float f7 = bl ? f5 : -f5;
        float f8 = bl2 ? -f3 : f3;
        float f9 = bl3 ? f6 : -f6;
        float f10 = f7 * f4;
        float f11 = f9 * f4;
        Direction direction2 = bl ? EAST : WEST;
        Direction direction3 = bl2 ? UP : DOWN;
        Direction direction4 = direction = bl3 ? SOUTH : NORTH;
        if (f7 > f9) {
            if (f8 > f10) {
                return Direction.makeDirectionArray(direction3, direction2, direction);
            }
            if (f11 > f8) {
                return Direction.makeDirectionArray(direction2, direction, direction3);
            }
            return Direction.makeDirectionArray(direction2, direction3, direction);
        }
        if (f8 > f11) {
            return Direction.makeDirectionArray(direction3, direction, direction2);
        }
        if (f10 > f8) {
            return Direction.makeDirectionArray(direction, direction2, direction3);
        }
        return Direction.makeDirectionArray(direction, direction3, direction2);
    }

    private static Direction[] makeDirectionArray(Direction direction, Direction direction2, Direction direction3) {
        return new Direction[]{direction, direction2, direction3, direction3.getOpposite(), direction2.getOpposite(), direction.getOpposite()};
    }

    public static Direction rotate(Matrix4f matrix4f, Direction direction) {
        Vec3i vec3i = direction.getNormal();
        Vector4f vector4f = new Vector4f(vec3i.getX(), vec3i.getY(), vec3i.getZ(), 0.0f);
        vector4f.transform(matrix4f);
        return Direction.getNearest(vector4f.x(), vector4f.y(), vector4f.z());
    }

    public Quaternion getRotation() {
        Quaternion quaternion = Vector3f.XP.rotationDegrees(90.0f);
        switch (this) {
            case DOWN: {
                return Vector3f.XP.rotationDegrees(180.0f);
            }
            case UP: {
                return Quaternion.ONE.copy();
            }
            case NORTH: {
                quaternion.mul(Vector3f.ZP.rotationDegrees(180.0f));
                return quaternion;
            }
            case SOUTH: {
                return quaternion;
            }
            case WEST: {
                quaternion.mul(Vector3f.ZP.rotationDegrees(90.0f));
                return quaternion;
            }
        }
        quaternion.mul(Vector3f.ZP.rotationDegrees(-90.0f));
        return quaternion;
    }

    public int get3DDataValue() {
        return this.data3d;
    }

    public int get2DDataValue() {
        return this.data2d;
    }

    public AxisDirection getAxisDirection() {
        return this.axisDirection;
    }

    public Direction getOpposite() {
        return Direction.from3DDataValue(this.oppositeIndex);
    }

    public Direction getClockWise() {
        switch (this) {
            case NORTH: {
                return EAST;
            }
            case EAST: {
                return SOUTH;
            }
            case SOUTH: {
                return WEST;
            }
            case WEST: {
                return NORTH;
            }
        }
        throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
    }

    public Direction getCounterClockWise() {
        switch (this) {
            case NORTH: {
                return WEST;
            }
            case EAST: {
                return NORTH;
            }
            case SOUTH: {
                return EAST;
            }
            case WEST: {
                return SOUTH;
            }
        }
        throw new IllegalStateException("Unable to get CCW facing of " + this);
    }

    public int getStepX() {
        return this.normal.getX();
    }

    public int getStepY() {
        return this.normal.getY();
    }

    public int getStepZ() {
        return this.normal.getZ();
    }

    public Vector3f step() {
        return new Vector3f(this.getStepX(), this.getStepY(), this.getStepZ());
    }

    public String getName() {
        return this.name;
    }

    public Axis getAxis() {
        return this.axis;
    }

    @Nullable
    public static Direction byName(@Nullable String string) {
        if (string == null) {
            return null;
        }
        return BY_NAME.get(string.toLowerCase(Locale.ROOT));
    }

    public static Direction from3DDataValue(int n) {
        return BY_3D_DATA[Mth.abs(n % BY_3D_DATA.length)];
    }

    public static Direction from2DDataValue(int n) {
        return BY_2D_DATA[Mth.abs(n % BY_2D_DATA.length)];
    }

    @Nullable
    public static Direction fromNormal(int n, int n2, int n3) {
        return (Direction)BY_NORMAL.get(BlockPos.asLong(n, n2, n3));
    }

    public static Direction fromYRot(double d) {
        return Direction.from2DDataValue(Mth.floor(d / 90.0 + 0.5) & 3);
    }

    public static Direction fromAxisAndDirection(Axis axis, AxisDirection axisDirection) {
        switch (axis) {
            case X: {
                return axisDirection == AxisDirection.POSITIVE ? EAST : WEST;
            }
            case Y: {
                return axisDirection == AxisDirection.POSITIVE ? UP : DOWN;
            }
        }
        return axisDirection == AxisDirection.POSITIVE ? SOUTH : NORTH;
    }

    public float toYRot() {
        return (this.data2d & 3) * 90;
    }

    public static Direction getRandom(Random random) {
        return Util.getRandom(VALUES, random);
    }

    public static Direction getNearest(double d, double d2, double d3) {
        return Direction.getNearest((float)d, (float)d2, (float)d3);
    }

    public static Direction getNearest(float f, float f2, float f3) {
        Direction direction = NORTH;
        float f4 = Float.MIN_VALUE;
        for (Direction direction2 : VALUES) {
            float f5 = f * (float)direction2.normal.getX() + f2 * (float)direction2.normal.getY() + f3 * (float)direction2.normal.getZ();
            if (!(f5 > f4)) continue;
            f4 = f5;
            direction = direction2;
        }
        return direction;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public static Direction get(AxisDirection axisDirection, Axis axis) {
        for (Direction direction : VALUES) {
            if (direction.getAxisDirection() != axisDirection || direction.getAxis() != axis) continue;
            return direction;
        }
        throw new IllegalArgumentException("No such direction: " + (Object)((Object)axisDirection) + " " + axis);
    }

    public Vec3i getNormal() {
        return this.normal;
    }

    public boolean isFacingAngle(float f) {
        float f2 = f * 0.017453292f;
        float f3 = -Mth.sin(f2);
        float f4 = Mth.cos(f2);
        return (float)this.normal.getX() * f3 + (float)this.normal.getZ() * f4 > 0.0f;
    }

    static {
        VALUES = Direction.values();
        BY_NAME = Arrays.stream(VALUES).collect(Collectors.toMap(Direction::getName, direction -> direction));
        BY_3D_DATA = (Direction[])Arrays.stream(VALUES).sorted(Comparator.comparingInt(direction -> direction.data3d)).toArray(n -> new Direction[n]);
        BY_2D_DATA = (Direction[])Arrays.stream(VALUES).filter(direction -> direction.getAxis().isHorizontal()).sorted(Comparator.comparingInt(direction -> direction.data2d)).toArray(n -> new Direction[n]);
        BY_NORMAL = (Long2ObjectMap)Arrays.stream(VALUES).collect(Collectors.toMap(direction -> new BlockPos(direction.getNormal()).asLong(), direction -> direction, (direction, direction2) -> {
            throw new IllegalArgumentException("Duplicate keys");
        }, Long2ObjectOpenHashMap::new));
    }

    public static enum Plane implements Iterable<Direction>,
    Predicate<Direction>
    {
        HORIZONTAL(new Direction[]{NORTH, EAST, SOUTH, WEST}, new Axis[]{Axis.X, Axis.Z}),
        VERTICAL(new Direction[]{UP, DOWN}, new Axis[]{Axis.Y});
        
        private final Direction[] faces;
        private final Axis[] axis;

        private Plane(Direction[] arrdirection, Axis[] arraxis) {
            this.faces = arrdirection;
            this.axis = arraxis;
        }

        public Direction getRandomDirection(Random random) {
            return Util.getRandom(this.faces, random);
        }

        public Axis getRandomAxis(Random random) {
            return Util.getRandom(this.axis, random);
        }

        @Override
        public boolean test(@Nullable Direction direction) {
            return direction != null && direction.getAxis().getPlane() == this;
        }

        @Override
        public Iterator<Direction> iterator() {
            return Iterators.forArray((Object[])this.faces);
        }

        public Stream<Direction> stream() {
            return Arrays.stream(this.faces);
        }

        @Override
        public /* synthetic */ boolean test(@Nullable Object object) {
            return this.test((Direction)object);
        }
    }

    public static enum AxisDirection {
        POSITIVE(1, "Towards positive"),
        NEGATIVE(-1, "Towards negative");
        
        private final int step;
        private final String name;

        private AxisDirection(int n2, String string2) {
            this.step = n2;
            this.name = string2;
        }

        public int getStep() {
            return this.step;
        }

        public String toString() {
            return this.name;
        }

        public AxisDirection opposite() {
            return this == POSITIVE ? NEGATIVE : POSITIVE;
        }
    }

    public static enum Axis implements StringRepresentable,
    Predicate<Direction>
    {
        X("x"){

            @Override
            public int choose(int n, int n2, int n3) {
                return n;
            }

            @Override
            public double choose(double d, double d2, double d3) {
                return d;
            }

            @Override
            public /* synthetic */ boolean test(@Nullable Object object) {
                return super.test((Direction)object);
            }
        }
        ,
        Y("y"){

            @Override
            public int choose(int n, int n2, int n3) {
                return n2;
            }

            @Override
            public double choose(double d, double d2, double d3) {
                return d2;
            }

            @Override
            public /* synthetic */ boolean test(@Nullable Object object) {
                return super.test((Direction)object);
            }
        }
        ,
        Z("z"){

            @Override
            public int choose(int n, int n2, int n3) {
                return n3;
            }

            @Override
            public double choose(double d, double d2, double d3) {
                return d3;
            }

            @Override
            public /* synthetic */ boolean test(@Nullable Object object) {
                return super.test((Direction)object);
            }
        };
        
        private static final Axis[] VALUES;
        public static final Codec<Axis> CODEC;
        private static final Map<String, Axis> BY_NAME;
        private final String name;

        private Axis(String string2) {
            this.name = string2;
        }

        @Nullable
        public static Axis byName(String string) {
            return BY_NAME.get(string.toLowerCase(Locale.ROOT));
        }

        public String getName() {
            return this.name;
        }

        public boolean isVertical() {
            return this == Y;
        }

        public boolean isHorizontal() {
            return this == X || this == Z;
        }

        public String toString() {
            return this.name;
        }

        public static Axis getRandom(Random random) {
            return Util.getRandom(VALUES, random);
        }

        @Override
        public boolean test(@Nullable Direction direction) {
            return direction != null && direction.getAxis() == this;
        }

        public Plane getPlane() {
            switch (this) {
                case X: 
                case Z: {
                    return Plane.HORIZONTAL;
                }
                case Y: {
                    return Plane.VERTICAL;
                }
            }
            throw new Error("Someone's been tampering with the universe!");
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public abstract int choose(int var1, int var2, int var3);

        public abstract double choose(double var1, double var3, double var5);

        @Override
        public /* synthetic */ boolean test(@Nullable Object object) {
            return this.test((Direction)object);
        }

        static {
            VALUES = Axis.values();
            CODEC = StringRepresentable.fromEnum(Axis::values, Axis::byName);
            BY_NAME = Arrays.stream(VALUES).collect(Collectors.toMap(Axis::getName, axis -> axis));
        }

    }

}

