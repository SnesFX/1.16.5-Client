/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.doubles.DoubleArrayList
 *  it.unimi.dsi.fastutil.doubles.DoubleList
 */
package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Arrays;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class ArrayVoxelShape
extends VoxelShape {
    private final DoubleList xs;
    private final DoubleList ys;
    private final DoubleList zs;

    protected ArrayVoxelShape(DiscreteVoxelShape discreteVoxelShape, double[] arrd, double[] arrd2, double[] arrd3) {
        this(discreteVoxelShape, (DoubleList)DoubleArrayList.wrap((double[])Arrays.copyOf(arrd, discreteVoxelShape.getXSize() + 1)), (DoubleList)DoubleArrayList.wrap((double[])Arrays.copyOf(arrd2, discreteVoxelShape.getYSize() + 1)), (DoubleList)DoubleArrayList.wrap((double[])Arrays.copyOf(arrd3, discreteVoxelShape.getZSize() + 1)));
    }

    ArrayVoxelShape(DiscreteVoxelShape discreteVoxelShape, DoubleList doubleList, DoubleList doubleList2, DoubleList doubleList3) {
        super(discreteVoxelShape);
        int n = discreteVoxelShape.getXSize() + 1;
        int n2 = discreteVoxelShape.getYSize() + 1;
        int n3 = discreteVoxelShape.getZSize() + 1;
        if (n != doubleList.size() || n2 != doubleList2.size() || n3 != doubleList3.size()) {
            throw Util.pauseInIde(new IllegalArgumentException("Lengths of point arrays must be consistent with the size of the VoxelShape."));
        }
        this.xs = doubleList;
        this.ys = doubleList2;
        this.zs = doubleList3;
    }

    @Override
    protected DoubleList getCoords(Direction.Axis axis) {
        switch (axis) {
            case X: {
                return this.xs;
            }
            case Y: {
                return this.ys;
            }
            case Z: {
                return this.zs;
            }
        }
        throw new IllegalArgumentException();
    }

}

