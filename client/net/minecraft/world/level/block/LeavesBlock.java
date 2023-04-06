/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LeavesBlock
extends Block {
    public static final IntegerProperty DISTANCE = BlockStateProperties.DISTANCE;
    public static final BooleanProperty PERSISTENT = BlockStateProperties.PERSISTENT;

    public LeavesBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(DISTANCE, 7)).setValue(PERSISTENT, false));
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return Shapes.empty();
    }

    @Override
    public boolean isRandomlyTicking(BlockState blockState) {
        return blockState.getValue(DISTANCE) == 7 && blockState.getValue(PERSISTENT) == false;
    }

    @Override
    public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, Random random) {
        if (!blockState.getValue(PERSISTENT).booleanValue() && blockState.getValue(DISTANCE) == 7) {
            LeavesBlock.dropResources(blockState, serverLevel, blockPos);
            serverLevel.removeBlock(blockPos, false);
        }
    }

    @Override
    public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, Random random) {
        serverLevel.setBlock(blockPos, LeavesBlock.updateDistance(blockState, serverLevel, blockPos), 3);
    }

    @Override
    public int getLightBlock(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return 1;
    }

    @Override
    public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
        int n = LeavesBlock.getDistanceAt(blockState2) + 1;
        if (n != 1 || blockState.getValue(DISTANCE) != n) {
            levelAccessor.getBlockTicks().scheduleTick(blockPos, this, 1);
        }
        return blockState;
    }

    private static BlockState updateDistance(BlockState blockState, LevelAccessor levelAccessor, BlockPos blockPos) {
        int n = 7;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (Direction direction : Direction.values()) {
            mutableBlockPos.setWithOffset(blockPos, direction);
            n = Math.min(n, LeavesBlock.getDistanceAt(levelAccessor.getBlockState(mutableBlockPos)) + 1);
            if (n == 1) break;
        }
        return (BlockState)blockState.setValue(DISTANCE, n);
    }

    private static int getDistanceAt(BlockState blockState) {
        if (BlockTags.LOGS.contains(blockState.getBlock())) {
            return 0;
        }
        if (blockState.getBlock() instanceof LeavesBlock) {
            return blockState.getValue(DISTANCE);
        }
        return 7;
    }

    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
        if (!level.isRainingAt(blockPos.above())) {
            return;
        }
        if (random.nextInt(15) != 1) {
            return;
        }
        BlockPos blockPos2 = blockPos.below();
        BlockState blockState2 = level.getBlockState(blockPos2);
        if (blockState2.canOcclude() && blockState2.isFaceSturdy(level, blockPos2, Direction.UP)) {
            return;
        }
        double d = (double)blockPos.getX() + random.nextDouble();
        double d2 = (double)blockPos.getY() - 0.05;
        double d3 = (double)blockPos.getZ() + random.nextDouble();
        level.addParticle(ParticleTypes.DRIPPING_WATER, d, d2, d3, 0.0, 0.0, 0.0);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DISTANCE, PERSISTENT);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return LeavesBlock.updateDistance((BlockState)this.defaultBlockState().setValue(PERSISTENT, true), blockPlaceContext.getLevel(), blockPlaceContext.getClickedPos());
    }
}

