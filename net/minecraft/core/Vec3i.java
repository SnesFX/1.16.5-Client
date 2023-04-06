/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.MoreObjects$ToStringHelper
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.PrimitiveCodec
 *  javax.annotation.concurrent.Immutable
 */
package net.minecraft.core;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.PrimitiveCodec;
import java.util.function.Function;
import java.util.stream.IntStream;
import javax.annotation.concurrent.Immutable;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.util.Mth;

@Immutable
public class Vec3i
implements Comparable<Vec3i> {
    public static final Codec<Vec3i> CODEC = Codec.INT_STREAM.comapFlatMap(intStream -> Util.fixedSize(intStream, 3).map(arrn -> new Vec3i(arrn[0], arrn[1], arrn[2])), vec3i -> IntStream.of(vec3i.getX(), vec3i.getY(), vec3i.getZ()));
    public static final Vec3i ZERO = new Vec3i(0, 0, 0);
    private int x;
    private int y;
    private int z;

    public Vec3i(int n, int n2, int n3) {
        this.x = n;
        this.y = n2;
        this.z = n3;
    }

    public Vec3i(double d, double d2, double d3) {
        this(Mth.floor(d), Mth.floor(d2), Mth.floor(d3));
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Vec3i)) {
            return false;
        }
        Vec3i vec3i = (Vec3i)object;
        if (this.getX() != vec3i.getX()) {
            return false;
        }
        if (this.getY() != vec3i.getY()) {
            return false;
        }
        return this.getZ() == vec3i.getZ();
    }

    public int hashCode() {
        return (this.getY() + this.getZ() * 31) * 31 + this.getX();
    }

    @Override
    public int compareTo(Vec3i vec3i) {
        if (this.getY() == vec3i.getY()) {
            if (this.getZ() == vec3i.getZ()) {
                return this.getX() - vec3i.getX();
            }
            return this.getZ() - vec3i.getZ();
        }
        return this.getY() - vec3i.getY();
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    protected void setX(int n) {
        this.x = n;
    }

    protected void setY(int n) {
        this.y = n;
    }

    protected void setZ(int n) {
        this.z = n;
    }

    public Vec3i above() {
        return this.above(1);
    }

    public Vec3i above(int n) {
        return this.relative(Direction.UP, n);
    }

    public Vec3i below() {
        return this.below(1);
    }

    public Vec3i below(int n) {
        return this.relative(Direction.DOWN, n);
    }

    public Vec3i relative(Direction direction, int n) {
        if (n == 0) {
            return this;
        }
        return new Vec3i(this.getX() + direction.getStepX() * n, this.getY() + direction.getStepY() * n, this.getZ() + direction.getStepZ() * n);
    }

    public Vec3i cross(Vec3i vec3i) {
        return new Vec3i(this.getY() * vec3i.getZ() - this.getZ() * vec3i.getY(), this.getZ() * vec3i.getX() - this.getX() * vec3i.getZ(), this.getX() * vec3i.getY() - this.getY() * vec3i.getX());
    }

    public boolean closerThan(Vec3i vec3i, double d) {
        return this.distSqr(vec3i.getX(), vec3i.getY(), vec3i.getZ(), false) < d * d;
    }

    public boolean closerThan(Position position, double d) {
        return this.distSqr(position.x(), position.y(), position.z(), true) < d * d;
    }

    public double distSqr(Vec3i vec3i) {
        return this.distSqr(vec3i.getX(), vec3i.getY(), vec3i.getZ(), true);
    }

    public double distSqr(Position position, boolean bl) {
        return this.distSqr(position.x(), position.y(), position.z(), bl);
    }

    public double distSqr(double d, double d2, double d3, boolean bl) {
        double d4 = bl ? 0.5 : 0.0;
        double d5 = (double)this.getX() + d4 - d;
        double d6 = (double)this.getY() + d4 - d2;
        double d7 = (double)this.getZ() + d4 - d3;
        return d5 * d5 + d6 * d6 + d7 * d7;
    }

    public int distManhattan(Vec3i vec3i) {
        float f = Math.abs(vec3i.getX() - this.getX());
        float f2 = Math.abs(vec3i.getY() - this.getY());
        float f3 = Math.abs(vec3i.getZ() - this.getZ());
        return (int)(f + f2 + f3);
    }

    public int get(Direction.Axis axis) {
        return axis.choose(this.x, this.y, this.z);
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("x", this.getX()).add("y", this.getY()).add("z", this.getZ()).toString();
    }

    public String toShortString() {
        return "" + this.getX() + ", " + this.getY() + ", " + this.getZ();
    }

    @Override
    public /* synthetic */ int compareTo(Object object) {
        return this.compareTo((Vec3i)object);
    }
}

