/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.phys.shapes;

import net.minecraft.core.AxisCycle;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;

public abstract class DiscreteVoxelShape {
    private static final Direction.Axis[] AXIS_VALUES = Direction.Axis.values();
    protected final int xSize;
    protected final int ySize;
    protected final int zSize;

    protected DiscreteVoxelShape(int n, int n2, int n3) {
        this.xSize = n;
        this.ySize = n2;
        this.zSize = n3;
    }

    public boolean isFullWide(AxisCycle axisCycle, int n, int n2, int n3) {
        return this.isFullWide(axisCycle.cycle(n, n2, n3, Direction.Axis.X), axisCycle.cycle(n, n2, n3, Direction.Axis.Y), axisCycle.cycle(n, n2, n3, Direction.Axis.Z));
    }

    public boolean isFullWide(int n, int n2, int n3) {
        if (n < 0 || n2 < 0 || n3 < 0) {
            return false;
        }
        if (n >= this.xSize || n2 >= this.ySize || n3 >= this.zSize) {
            return false;
        }
        return this.isFull(n, n2, n3);
    }

    public boolean isFull(AxisCycle axisCycle, int n, int n2, int n3) {
        return this.isFull(axisCycle.cycle(n, n2, n3, Direction.Axis.X), axisCycle.cycle(n, n2, n3, Direction.Axis.Y), axisCycle.cycle(n, n2, n3, Direction.Axis.Z));
    }

    public abstract boolean isFull(int var1, int var2, int var3);

    public abstract void setFull(int var1, int var2, int var3, boolean var4, boolean var5);

    public boolean isEmpty() {
        for (Direction.Axis axis : AXIS_VALUES) {
            if (this.firstFull(axis) < this.lastFull(axis)) continue;
            return true;
        }
        return false;
    }

    public abstract int firstFull(Direction.Axis var1);

    public abstract int lastFull(Direction.Axis var1);

    public int lastFull(Direction.Axis axis, int n, int n2) {
        if (n < 0 || n2 < 0) {
            return 0;
        }
        Direction.Axis axis2 = AxisCycle.FORWARD.cycle(axis);
        Direction.Axis axis3 = AxisCycle.BACKWARD.cycle(axis);
        if (n >= this.getSize(axis2) || n2 >= this.getSize(axis3)) {
            return 0;
        }
        int n3 = this.getSize(axis);
        AxisCycle axisCycle = AxisCycle.between(Direction.Axis.X, axis);
        for (int i = n3 - 1; i >= 0; --i) {
            if (!this.isFull(axisCycle, i, n, n2)) continue;
            return i + 1;
        }
        return 0;
    }

    public int getSize(Direction.Axis axis) {
        return axis.choose(this.xSize, this.ySize, this.zSize);
    }

    public int getXSize() {
        return this.getSize(Direction.Axis.X);
    }

    public int getYSize() {
        return this.getSize(Direction.Axis.Y);
    }

    public int getZSize() {
        return this.getSize(Direction.Axis.Z);
    }

    public void forAllEdges(IntLineConsumer intLineConsumer, boolean bl) {
        this.forAllAxisEdges(intLineConsumer, AxisCycle.NONE, bl);
        this.forAllAxisEdges(intLineConsumer, AxisCycle.FORWARD, bl);
        this.forAllAxisEdges(intLineConsumer, AxisCycle.BACKWARD, bl);
    }

    private void forAllAxisEdges(IntLineConsumer intLineConsumer, AxisCycle axisCycle, boolean bl) {
        AxisCycle axisCycle2 = axisCycle.inverse();
        int n = this.getSize(axisCycle2.cycle(Direction.Axis.X));
        int n2 = this.getSize(axisCycle2.cycle(Direction.Axis.Y));
        int n3 = this.getSize(axisCycle2.cycle(Direction.Axis.Z));
        for (int i = 0; i <= n; ++i) {
            for (int j = 0; j <= n2; ++j) {
                int n4 = -1;
                for (int k = 0; k <= n3; ++k) {
                    int n5 = 0;
                    int n6 = 0;
                    for (int i2 = 0; i2 <= 1; ++i2) {
                        for (int i3 = 0; i3 <= 1; ++i3) {
                            if (!this.isFullWide(axisCycle2, i + i2 - 1, j + i3 - 1, k)) continue;
                            ++n5;
                            n6 ^= i2 ^ i3;
                        }
                    }
                    if (n5 == 1 || n5 == 3 || n5 == 2 && !(n6 & true)) {
                        if (bl) {
                            if (n4 != -1) continue;
                            n4 = k;
                            continue;
                        }
                        intLineConsumer.consume(axisCycle2.cycle(i, j, k, Direction.Axis.X), axisCycle2.cycle(i, j, k, Direction.Axis.Y), axisCycle2.cycle(i, j, k, Direction.Axis.Z), axisCycle2.cycle(i, j, k + 1, Direction.Axis.X), axisCycle2.cycle(i, j, k + 1, Direction.Axis.Y), axisCycle2.cycle(i, j, k + 1, Direction.Axis.Z));
                        continue;
                    }
                    if (n4 == -1) continue;
                    intLineConsumer.consume(axisCycle2.cycle(i, j, n4, Direction.Axis.X), axisCycle2.cycle(i, j, n4, Direction.Axis.Y), axisCycle2.cycle(i, j, n4, Direction.Axis.Z), axisCycle2.cycle(i, j, k, Direction.Axis.X), axisCycle2.cycle(i, j, k, Direction.Axis.Y), axisCycle2.cycle(i, j, k, Direction.Axis.Z));
                    n4 = -1;
                }
            }
        }
    }

    protected boolean isZStripFull(int n, int n2, int n3, int n4) {
        for (int i = n; i < n2; ++i) {
            if (this.isFullWide(n3, n4, i)) continue;
            return false;
        }
        return true;
    }

    protected void setZStrip(int n, int n2, int n3, int n4, boolean bl) {
        for (int i = n; i < n2; ++i) {
            this.setFull(n3, n4, i, false, bl);
        }
    }

    protected boolean isXZRectangleFull(int n, int n2, int n3, int n4, int n5) {
        for (int i = n; i < n2; ++i) {
            if (this.isZStripFull(n3, n4, i, n5)) continue;
            return false;
        }
        return true;
    }

    public void forAllBoxes(IntLineConsumer intLineConsumer, boolean bl) {
        BitSetDiscreteVoxelShape bitSetDiscreteVoxelShape = new BitSetDiscreteVoxelShape(this);
        for (int i = 0; i <= this.xSize; ++i) {
            for (int j = 0; j <= this.ySize; ++j) {
                int n = -1;
                for (int k = 0; k <= this.zSize; ++k) {
                    int n2;
                    if (bitSetDiscreteVoxelShape.isFullWide(i, j, k)) {
                        if (bl) {
                            if (n != -1) continue;
                            n = k;
                            continue;
                        }
                        intLineConsumer.consume(i, j, k, i + 1, j + 1, k + 1);
                        continue;
                    }
                    if (n == -1) continue;
                    int n3 = i;
                    int n4 = i;
                    int n5 = j;
                    int n6 = j;
                    ((DiscreteVoxelShape)bitSetDiscreteVoxelShape).setZStrip(n, k, i, j, false);
                    while (((DiscreteVoxelShape)bitSetDiscreteVoxelShape).isZStripFull(n, k, n3 - 1, n5)) {
                        ((DiscreteVoxelShape)bitSetDiscreteVoxelShape).setZStrip(n, k, n3 - 1, n5, false);
                        --n3;
                    }
                    while (((DiscreteVoxelShape)bitSetDiscreteVoxelShape).isZStripFull(n, k, n4 + 1, n5)) {
                        ((DiscreteVoxelShape)bitSetDiscreteVoxelShape).setZStrip(n, k, n4 + 1, n5, false);
                        ++n4;
                    }
                    while (bitSetDiscreteVoxelShape.isXZRectangleFull(n3, n4 + 1, n, k, n5 - 1)) {
                        for (n2 = n3; n2 <= n4; ++n2) {
                            ((DiscreteVoxelShape)bitSetDiscreteVoxelShape).setZStrip(n, k, n2, n5 - 1, false);
                        }
                        --n5;
                    }
                    while (bitSetDiscreteVoxelShape.isXZRectangleFull(n3, n4 + 1, n, k, n6 + 1)) {
                        for (n2 = n3; n2 <= n4; ++n2) {
                            ((DiscreteVoxelShape)bitSetDiscreteVoxelShape).setZStrip(n, k, n2, n6 + 1, false);
                        }
                        ++n6;
                    }
                    intLineConsumer.consume(n3, n5, n, n4 + 1, n6 + 1, k);
                    n = -1;
                }
            }
        }
    }

    public void forAllFaces(IntFaceConsumer intFaceConsumer) {
        this.forAllAxisFaces(intFaceConsumer, AxisCycle.NONE);
        this.forAllAxisFaces(intFaceConsumer, AxisCycle.FORWARD);
        this.forAllAxisFaces(intFaceConsumer, AxisCycle.BACKWARD);
    }

    private void forAllAxisFaces(IntFaceConsumer intFaceConsumer, AxisCycle axisCycle) {
        AxisCycle axisCycle2 = axisCycle.inverse();
        Direction.Axis axis = axisCycle2.cycle(Direction.Axis.Z);
        int n = this.getSize(axisCycle2.cycle(Direction.Axis.X));
        int n2 = this.getSize(axisCycle2.cycle(Direction.Axis.Y));
        int n3 = this.getSize(axis);
        Direction direction = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.NEGATIVE);
        Direction direction2 = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n2; ++j) {
                boolean bl = false;
                for (int k = 0; k <= n3; ++k) {
                    boolean bl2;
                    boolean bl3 = bl2 = k != n3 && this.isFull(axisCycle2, i, j, k);
                    if (!bl && bl2) {
                        intFaceConsumer.consume(direction, axisCycle2.cycle(i, j, k, Direction.Axis.X), axisCycle2.cycle(i, j, k, Direction.Axis.Y), axisCycle2.cycle(i, j, k, Direction.Axis.Z));
                    }
                    if (bl && !bl2) {
                        intFaceConsumer.consume(direction2, axisCycle2.cycle(i, j, k - 1, Direction.Axis.X), axisCycle2.cycle(i, j, k - 1, Direction.Axis.Y), axisCycle2.cycle(i, j, k - 1, Direction.Axis.Z));
                    }
                    bl = bl2;
                }
            }
        }
    }

    public static interface IntFaceConsumer {
        public void consume(Direction var1, int var2, int var3, int var4);
    }

    public static interface IntLineConsumer {
        public void consume(int var1, int var2, int var3, int var4, int var5, int var6);
    }

}

