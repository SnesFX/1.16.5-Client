/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrowingPlantBodyBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TwistingVinesPlant
extends GrowingPlantBodyBlock {
    public static final VoxelShape SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);

    public TwistingVinesPlant(BlockBehaviour.Properties properties) {
        super(properties, Direction.UP, SHAPE, false);
    }

    @Override
    protected GrowingPlantHeadBlock getHeadBlock() {
        return (GrowingPlantHeadBlock)Blocks.TWISTING_VINES;
    }
}

