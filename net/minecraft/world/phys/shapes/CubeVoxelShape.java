/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.doubles.DoubleList
 */
package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.shapes.CubePointRange;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class CubeVoxelShape
extends VoxelShape {
    protected CubeVoxelShape(DiscreteVoxelShape discreteVoxelShape) {
        super(discreteVoxelShape);
    }

    @Override
    protected DoubleList getCoords(Direction.Axis axis) {
        return new CubePointRange(this.shape.getSize(axis));
    }

    @Override
    protected int findIndex(Direction.Axis axis, double d) {
        int n = this.shape.getSize(axis);
        return Mth.clamp(Mth.floor(d * (double)n), -1, n);
    }
}

