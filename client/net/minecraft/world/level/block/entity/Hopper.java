/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface Hopper
extends Container {
    public static final VoxelShape INSIDE = Block.box(2.0, 11.0, 2.0, 14.0, 16.0, 14.0);
    public static final VoxelShape ABOVE = Block.box(0.0, 16.0, 0.0, 16.0, 32.0, 16.0);
    public static final VoxelShape SUCK = Shapes.or(INSIDE, ABOVE);

    default public VoxelShape getSuckShape() {
        return SUCK;
    }

    @Nullable
    public Level getLevel();

    public double getLevelX();

    public double getLevelY();

    public double getLevelZ();
}

