/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.doubles.DoubleList
 */
package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.CubePointRange;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import net.minecraft.world.phys.shapes.SubShape;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SliceShape
extends VoxelShape {
    private final VoxelShape delegate;
    private final Direction.Axis axis;
    private static final DoubleList SLICE_COORDS = new CubePointRange(1);

    public SliceShape(VoxelShape voxelShape, Direction.Axis axis, int n) {
        super(SliceShape.makeSlice(voxelShape.shape, axis, n));
        this.delegate = voxelShape;
        this.axis = axis;
    }

    private static DiscreteVoxelShape makeSlice(DiscreteVoxelShape discreteVoxelShape, Direction.Axis axis, int n) {
        return new SubShape(discreteVoxelShape, axis.choose(n, 0, 0), axis.choose(0, n, 0), axis.choose(0, 0, n), axis.choose(n + 1, discreteVoxelShape.xSize, discreteVoxelShape.xSize), axis.choose(discreteVoxelShape.ySize, n + 1, discreteVoxelShape.ySize), axis.choose(discreteVoxelShape.zSize, discreteVoxelShape.zSize, n + 1));
    }

    @Override
    protected DoubleList getCoords(Direction.Axis axis) {
        if (axis == this.axis) {
            return SLICE_COORDS;
        }
        return this.delegate.getCoords(axis);
    }
}

