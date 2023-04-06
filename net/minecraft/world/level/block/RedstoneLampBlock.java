/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class RedstoneLampBlock
extends Block {
    public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

    public RedstoneLampBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue(LIT, false));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return (BlockState)this.defaultBlockState().setValue(LIT, blockPlaceContext.getLevel().hasNeighborSignal(blockPlaceContext.getClickedPos()));
    }

    @Override
    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
        if (level.isClientSide) {
            return;
        }
        boolean bl2 = blockState.getValue(LIT);
        if (bl2 != level.hasNeighborSignal(blockPos)) {
            if (bl2) {
                level.getBlockTicks().scheduleTick(blockPos, this, 4);
            } else {
                level.setBlock(blockPos, (BlockState)blockState.cycle(LIT), 2);
            }
        }
    }

    @Override
    public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, Random random) {
        if (blockState.getValue(LIT).booleanValue() && !serverLevel.hasNeighborSignal(blockPos)) {
            serverLevel.setBlock(blockPos, (BlockState)blockState.cycle(LIT), 2);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }
}

