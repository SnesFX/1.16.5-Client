/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.MoreObjects$ToStringHelper
 */
package net.minecraft.world.level.levelgen.structure;

import com.google.common.base.MoreObjects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.IntArrayTag;

public class BoundingBox {
    public int x0;
    public int y0;
    public int z0;
    public int x1;
    public int y1;
    public int z1;

    public BoundingBox() {
    }

    public BoundingBox(int[] arrn) {
        if (arrn.length == 6) {
            this.x0 = arrn[0];
            this.y0 = arrn[1];
            this.z0 = arrn[2];
            this.x1 = arrn[3];
            this.y1 = arrn[4];
            this.z1 = arrn[5];
        }
    }

    public static BoundingBox getUnknownBox() {
        return new BoundingBox(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    public static BoundingBox infinite() {
        return new BoundingBox(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public static BoundingBox orientBox(int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8, int n9, Direction direction) {
        switch (direction) {
            default: {
                return new BoundingBox(n + n4, n2 + n5, n3 + n6, n + n7 - 1 + n4, n2 + n8 - 1 + n5, n3 + n9 - 1 + n6);
            }
            case NORTH: {
                return new BoundingBox(n + n4, n2 + n5, n3 - n9 + 1 + n6, n + n7 - 1 + n4, n2 + n8 - 1 + n5, n3 + n6);
            }
            case SOUTH: {
                return new BoundingBox(n + n4, n2 + n5, n3 + n6, n + n7 - 1 + n4, n2 + n8 - 1 + n5, n3 + n9 - 1 + n6);
            }
            case WEST: {
                return new BoundingBox(n - n9 + 1 + n6, n2 + n5, n3 + n4, n + n6, n2 + n8 - 1 + n5, n3 + n7 - 1 + n4);
            }
            case EAST: 
        }
        return new BoundingBox(n + n6, n2 + n5, n3 + n4, n + n9 - 1 + n6, n2 + n8 - 1 + n5, n3 + n7 - 1 + n4);
    }

    public static BoundingBox createProper(int n, int n2, int n3, int n4, int n5, int n6) {
        return new BoundingBox(Math.min(n, n4), Math.min(n2, n5), Math.min(n3, n6), Math.max(n, n4), Math.max(n2, n5), Math.max(n3, n6));
    }

    public BoundingBox(BoundingBox boundingBox) {
        this.x0 = boundingBox.x0;
        this.y0 = boundingBox.y0;
        this.z0 = boundingBox.z0;
        this.x1 = boundingBox.x1;
        this.y1 = boundingBox.y1;
        this.z1 = boundingBox.z1;
    }

    public BoundingBox(int n, int n2, int n3, int n4, int n5, int n6) {
        this.x0 = n;
        this.y0 = n2;
        this.z0 = n3;
        this.x1 = n4;
        this.y1 = n5;
        this.z1 = n6;
    }

    public BoundingBox(Vec3i vec3i, Vec3i vec3i2) {
        this.x0 = Math.min(vec3i.getX(), vec3i2.getX());
        this.y0 = Math.min(vec3i.getY(), vec3i2.getY());
        this.z0 = Math.min(vec3i.getZ(), vec3i2.getZ());
        this.x1 = Math.max(vec3i.getX(), vec3i2.getX());
        this.y1 = Math.max(vec3i.getY(), vec3i2.getY());
        this.z1 = Math.max(vec3i.getZ(), vec3i2.getZ());
    }

    public BoundingBox(int n, int n2, int n3, int n4) {
        this.x0 = n;
        this.z0 = n2;
        this.x1 = n3;
        this.z1 = n4;
        this.y0 = 1;
        this.y1 = 512;
    }

    public boolean intersects(BoundingBox boundingBox) {
        return this.x1 >= boundingBox.x0 && this.x0 <= boundingBox.x1 && this.z1 >= boundingBox.z0 && this.z0 <= boundingBox.z1 && this.y1 >= boundingBox.y0 && this.y0 <= boundingBox.y1;
    }

    public boolean intersects(int n, int n2, int n3, int n4) {
        return this.x1 >= n && this.x0 <= n3 && this.z1 >= n2 && this.z0 <= n4;
    }

    public void expand(BoundingBox boundingBox) {
        this.x0 = Math.min(this.x0, boundingBox.x0);
        this.y0 = Math.min(this.y0, boundingBox.y0);
        this.z0 = Math.min(this.z0, boundingBox.z0);
        this.x1 = Math.max(this.x1, boundingBox.x1);
        this.y1 = Math.max(this.y1, boundingBox.y1);
        this.z1 = Math.max(this.z1, boundingBox.z1);
    }

    public void move(int n, int n2, int n3) {
        this.x0 += n;
        this.y0 += n2;
        this.z0 += n3;
        this.x1 += n;
        this.y1 += n2;
        this.z1 += n3;
    }

    public BoundingBox moved(int n, int n2, int n3) {
        return new BoundingBox(this.x0 + n, this.y0 + n2, this.z0 + n3, this.x1 + n, this.y1 + n2, this.z1 + n3);
    }

    public void move(Vec3i vec3i) {
        this.move(vec3i.getX(), vec3i.getY(), vec3i.getZ());
    }

    public boolean isInside(Vec3i vec3i) {
        return vec3i.getX() >= this.x0 && vec3i.getX() <= this.x1 && vec3i.getZ() >= this.z0 && vec3i.getZ() <= this.z1 && vec3i.getY() >= this.y0 && vec3i.getY() <= this.y1;
    }

    public Vec3i getLength() {
        return new Vec3i(this.x1 - this.x0, this.y1 - this.y0, this.z1 - this.z0);
    }

    public int getXSpan() {
        return this.x1 - this.x0 + 1;
    }

    public int getYSpan() {
        return this.y1 - this.y0 + 1;
    }

    public int getZSpan() {
        return this.z1 - this.z0 + 1;
    }

    public Vec3i getCenter() {
        return new BlockPos(this.x0 + (this.x1 - this.x0 + 1) / 2, this.y0 + (this.y1 - this.y0 + 1) / 2, this.z0 + (this.z1 - this.z0 + 1) / 2);
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("x0", this.x0).add("y0", this.y0).add("z0", this.z0).add("x1", this.x1).add("y1", this.y1).add("z1", this.z1).toString();
    }

    public IntArrayTag createTag() {
        return new IntArrayTag(new int[]{this.x0, this.y0, this.z0, this.x1, this.y1, this.z1});
    }

}

