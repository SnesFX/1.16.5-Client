/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.core;

public class Cursor3D {
    private int originX;
    private int originY;
    private int originZ;
    private int width;
    private int height;
    private int depth;
    private int end;
    private int index;
    private int x;
    private int y;
    private int z;

    public Cursor3D(int n, int n2, int n3, int n4, int n5, int n6) {
        this.originX = n;
        this.originY = n2;
        this.originZ = n3;
        this.width = n4 - n + 1;
        this.height = n5 - n2 + 1;
        this.depth = n6 - n3 + 1;
        this.end = this.width * this.height * this.depth;
    }

    public boolean advance() {
        if (this.index == this.end) {
            return false;
        }
        this.x = this.index % this.width;
        int n = this.index / this.width;
        this.y = n % this.height;
        this.z = n / this.height;
        ++this.index;
        return true;
    }

    public int nextX() {
        return this.originX + this.x;
    }

    public int nextY() {
        return this.originY + this.y;
    }

    public int nextZ() {
        return this.originZ + this.z;
    }

    public int getNextType() {
        int n = 0;
        if (this.x == 0 || this.x == this.width - 1) {
            ++n;
        }
        if (this.y == 0 || this.y == this.height - 1) {
            ++n;
        }
        if (this.z == 0 || this.z == this.depth - 1) {
            ++n;
        }
        return n;
    }
}

