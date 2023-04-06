/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.phys.shapes;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;

public final class SubShape
extends DiscreteVoxelShape {
    private final DiscreteVoxelShape parent;
    private final int startX;
    private final int startY;
    private final int startZ;
    private final int endX;
    private final int endY;
    private final int endZ;

    protected SubShape(DiscreteVoxelShape discreteVoxelShape, int n, int n2, int n3, int n4, int n5, int n6) {
        super(n4 - n, n5 - n2, n6 - n3);
        this.parent = discreteVoxelShape;
        this.startX = n;
        this.startY = n2;
        this.startZ = n3;
        this.endX = n4;
        this.endY = n5;
        this.endZ = n6;
    }

    @Override
    public boolean isFull(int n, int n2, int n3) {
        return this.parent.isFull(this.startX + n, this.startY + n2, this.startZ + n3);
    }

    @Override
    public void setFull(int n, int n2, int n3, boolean bl, boolean bl2) {
        this.parent.setFull(this.startX + n, this.startY + n2, this.startZ + n3, bl, bl2);
    }

    @Override
    public int firstFull(Direction.Axis axis) {
        return Math.max(0, this.parent.firstFull(axis) - axis.choose(this.startX, this.startY, this.startZ));
    }

    @Override
    public int lastFull(Direction.Axis axis) {
        return Math.min(axis.choose(this.endX, this.endY, this.endZ), this.parent.lastFull(axis) - axis.choose(this.startX, this.startY, this.startZ));
    }
}

