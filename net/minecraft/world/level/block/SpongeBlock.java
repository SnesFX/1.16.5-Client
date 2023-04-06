/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.world.level.block;

import com.google.common.collect.Lists;
import java.util.LinkedList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;

public class SpongeBlock
extends Block {
    protected SpongeBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public void onPlace(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (blockState2.is(blockState.getBlock())) {
            return;
        }
        this.tryAbsorbWater(level, blockPos);
    }

    @Override
    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
        this.tryAbsorbWater(level, blockPos);
        super.neighborChanged(blockState, level, blockPos, block, blockPos2, bl);
    }

    protected void tryAbsorbWater(Level level, BlockPos blockPos) {
        if (this.removeWaterBreadthFirstSearch(level, blockPos)) {
            level.setBlock(blockPos, Blocks.WET_SPONGE.defaultBlockState(), 2);
            level.levelEvent(2001, blockPos, Block.getId(Blocks.WATER.defaultBlockState()));
        }
    }

    private boolean removeWaterBreadthFirstSearch(Level level, BlockPos blockPos) {
        LinkedList linkedList = Lists.newLinkedList();
        linkedList.add(new Tuple<BlockPos, Integer>(blockPos, 0));
        int n = 0;
        while (!linkedList.isEmpty()) {
            Tuple tuple = (Tuple)linkedList.poll();
            BlockPos blockPos2 = (BlockPos)tuple.getA();
            int n2 = (Integer)tuple.getB();
            for (Direction direction : Direction.values()) {
                BlockPos blockPos3 = blockPos2.relative(direction);
                BlockState blockState = level.getBlockState(blockPos3);
                FluidState fluidState = level.getFluidState(blockPos3);
                Material material = blockState.getMaterial();
                if (!fluidState.is(FluidTags.WATER)) continue;
                if (blockState.getBlock() instanceof BucketPickup && ((BucketPickup)((Object)blockState.getBlock())).takeLiquid(level, blockPos3, blockState) != Fluids.EMPTY) {
                    ++n;
                    if (n2 >= 6) continue;
                    linkedList.add(new Tuple<BlockPos, Integer>(blockPos3, n2 + 1));
                    continue;
                }
                if (blockState.getBlock() instanceof LiquidBlock) {
                    level.setBlock(blockPos3, Blocks.AIR.defaultBlockState(), 3);
                    ++n;
                    if (n2 >= 6) continue;
                    linkedList.add(new Tuple<BlockPos, Integer>(blockPos3, n2 + 1));
                    continue;
                }
                if (material != Material.WATER_PLANT && material != Material.REPLACEABLE_WATER_PLANT) continue;
                BlockEntity blockEntity = blockState.getBlock().isEntityBlock() ? level.getBlockEntity(blockPos3) : null;
                SpongeBlock.dropResources(blockState, level, blockPos3, blockEntity);
                level.setBlock(blockPos3, Blocks.AIR.defaultBlockState(), 3);
                ++n;
                if (n2 >= 6) continue;
                linkedList.add(new Tuple<BlockPos, Integer>(blockPos3, n2 + 1));
            }
            if (n <= 64) continue;
            break;
        }
        return n > 0;
    }
}

