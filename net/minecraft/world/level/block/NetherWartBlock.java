/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class NetherWartBlock
extends BushBlock {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{Block.box(0.0, 0.0, 0.0, 16.0, 5.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 11.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 14.0, 16.0)};

    protected NetherWartBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0));
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return SHAPE_BY_AGE[blockState.getValue(AGE)];
    }

    @Override
    protected boolean mayPlaceOn(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return blockState.is(Blocks.SOUL_SAND);
    }

    @Override
    public boolean isRandomlyTicking(BlockState blockState) {
        return blockState.getValue(AGE) < 3;
    }

    @Override
    public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, Random random) {
        int n = blockState.getValue(AGE);
        if (n < 3 && random.nextInt(10) == 0) {
            blockState = (BlockState)blockState.setValue(AGE, n + 1);
            serverLevel.setBlock(blockPos, blockState, 2);
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
        return new ItemStack(Items.NETHER_WART);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }
}

