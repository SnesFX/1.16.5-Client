/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.doubles.DoubleList
 */
package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.BitSet;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import net.minecraft.world.phys.shapes.IndexMerger;

public final class BitSetDiscreteVoxelShape
extends DiscreteVoxelShape {
    private final BitSet storage;
    private int xMin;
    private int yMin;
    private int zMin;
    private int xMax;
    private int yMax;
    private int zMax;

    public BitSetDiscreteVoxelShape(int n, int n2, int n3) {
        this(n, n2, n3, n, n2, n3, 0, 0, 0);
    }

    public BitSetDiscreteVoxelShape(int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8, int n9) {
        super(n, n2, n3);
        this.storage = new BitSet(n * n2 * n3);
        this.xMin = n4;
        this.yMin = n5;
        this.zMin = n6;
        this.xMax = n7;
        this.yMax = n8;
        this.zMax = n9;
    }

    public BitSetDiscreteVoxelShape(DiscreteVoxelShape discreteVoxelShape) {
        super(discreteVoxelShape.xSize, discreteVoxelShape.ySize, discreteVoxelShape.zSize);
        if (discreteVoxelShape instanceof BitSetDiscreteVoxelShape) {
            this.storage = (BitSet)((BitSetDiscreteVoxelShape)discreteVoxelShape).storage.clone();
        } else {
            this.storage = new BitSet(this.xSize * this.ySize * this.zSize);
            for (int i = 0; i < this.xSize; ++i) {
                for (int j = 0; j < this.ySize; ++j) {
                    for (int k = 0; k < this.zSize; ++k) {
                        if (!discreteVoxelShape.isFull(i, j, k)) continue;
                        this.storage.set(this.getIndex(i, j, k));
                    }
                }
            }
        }
        this.xMin = discreteVoxelShape.firstFull(Direction.Axis.X);
        this.yMin = discreteVoxelShape.firstFull(Direction.Axis.Y);
        this.zMin = discreteVoxelShape.firstFull(Direction.Axis.Z);
        this.xMax = discreteVoxelShape.lastFull(Direction.Axis.X);
        this.yMax = discreteVoxelShape.lastFull(Direction.Axis.Y);
        this.zMax = discreteVoxelShape.lastFull(Direction.Axis.Z);
    }

    protected int getIndex(int n, int n2, int n3) {
        return (n * this.ySize + n2) * this.zSize + n3;
    }

    @Override
    public boolean isFull(int n, int n2, int n3) {
        return this.storage.get(this.getIndex(n, n2, n3));
    }

    @Override
    public void setFull(int n, int n2, int n3, boolean bl, boolean bl2) {
        this.storage.set(this.getIndex(n, n2, n3), bl2);
        if (bl && bl2) {
            this.xMin = Math.min(this.xMin, n);
            this.yMin = Math.min(this.yMin, n2);
            this.zMin = Math.min(this.zMin, n3);
            this.xMax = Math.max(this.xMax, n + 1);
            this.yMax = Math.max(this.yMax, n2 + 1);
            this.zMax = Math.max(this.zMax, n3 + 1);
        }
    }

    @Override
    public boolean isEmpty() {
        return this.storage.isEmpty();
    }

    @Override
    public int firstFull(Direction.Axis axis) {
        return axis.choose(this.xMin, this.yMin, this.zMin);
    }

    @Override
    public int lastFull(Direction.Axis axis) {
        return axis.choose(this.xMax, this.yMax, this.zMax);
    }

    @Override
    protected boolean isZStripFull(int n, int n2, int n3, int n4) {
        if (n3 < 0 || n4 < 0 || n < 0) {
            return false;
        }
        if (n3 >= this.xSize || n4 >= this.ySize || n2 > this.zSize) {
            return false;
        }
        return this.storage.nextClearBit(this.getIndex(n3, n4, n)) >= this.getIndex(n3, n4, n2);
    }

    @Override
    protected void setZStrip(int n, int n2, int n3, int n4, boolean bl) {
        this.storage.set(this.getIndex(n3, n4, n), this.getIndex(n3, n4, n2), bl);
    }

    static BitSetDiscreteVoxelShape join(DiscreteVoxelShape discreteVoxelShape, DiscreteVoxelShape discreteVoxelShape2, IndexMerger indexMerger, IndexMerger indexMerger2, IndexMerger indexMerger3, BooleanOp booleanOp) {
        BitSetDiscreteVoxelShape bitSetDiscreteVoxelShape = new BitSetDiscreteVoxelShape(indexMerger.getList().size() - 1, indexMerger2.getList().size() - 1, indexMerger3.getList().size() - 1);
        int[] arrn = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};
        indexMerger.forMergedIndexes((n, n2, n3) -> {
            boolean[] arrbl = new boolean[]{false};
            boolean bl = indexMerger2.forMergedIndexes((n4, n5, n6) -> {
                boolean[] arrbl2 = new boolean[]{false};
                boolean bl = indexMerger3.forMergedIndexes((n7, n8, n9) -> {
                    boolean bl = booleanOp.apply(discreteVoxelShape.isFullWide(n, n4, n7), discreteVoxelShape2.isFullWide(n2, n5, n8));
                    if (bl) {
                        bitSetDiscreteVoxelShape.storage.set(bitSetDiscreteVoxelShape.getIndex(n3, n6, n9));
                        arrn[2] = Math.min(arrn[2], n9);
                        arrn[5] = Math.max(arrn[5], n9);
                        arrbl[0] = true;
                    }
                    return true;
                });
                if (arrbl2[0]) {
                    arrn[1] = Math.min(arrn[1], n6);
                    arrn[4] = Math.max(arrn[4], n6);
                    arrbl[0] = true;
                }
                return bl;
            });
            if (arrbl[0]) {
                arrn[0] = Math.min(arrn[0], n3);
                arrn[3] = Math.max(arrn[3], n3);
            }
            return bl;
        });
        bitSetDiscreteVoxelShape.xMin = arrn[0];
        bitSetDiscreteVoxelShape.yMin = arrn[1];
        bitSetDiscreteVoxelShape.zMin = arrn[2];
        bitSetDiscreteVoxelShape.xMax = arrn[3] + 1;
        bitSetDiscreteVoxelShape.yMax = arrn[4] + 1;
        bitSetDiscreteVoxelShape.zMax = arrn[5] + 1;
        return bitSetDiscreteVoxelShape;
    }
}

