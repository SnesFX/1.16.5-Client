/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LeverBlock
extends FaceAttachedHorizontalDirectionalBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    protected static final VoxelShape NORTH_AABB = Block.box(5.0, 4.0, 10.0, 11.0, 12.0, 16.0);
    protected static final VoxelShape SOUTH_AABB = Block.box(5.0, 4.0, 0.0, 11.0, 12.0, 6.0);
    protected static final VoxelShape WEST_AABB = Block.box(10.0, 4.0, 5.0, 16.0, 12.0, 11.0);
    protected static final VoxelShape EAST_AABB = Block.box(0.0, 4.0, 5.0, 6.0, 12.0, 11.0);
    protected static final VoxelShape UP_AABB_Z = Block.box(5.0, 0.0, 4.0, 11.0, 6.0, 12.0);
    protected static final VoxelShape UP_AABB_X = Block.box(4.0, 0.0, 5.0, 12.0, 6.0, 11.0);
    protected static final VoxelShape DOWN_AABB_Z = Block.box(5.0, 10.0, 4.0, 11.0, 16.0, 12.0);
    protected static final VoxelShape DOWN_AABB_X = Block.box(4.0, 10.0, 5.0, 12.0, 16.0, 11.0);

    protected LeverBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(POWERED, false)).setValue(FACE, AttachFace.WALL));
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        switch ((AttachFace)blockState.getValue(FACE)) {
            case FLOOR: {
                switch (blockState.getValue(FACING).getAxis()) {
                    case X: {
                        return UP_AABB_X;
                    }
                }
                return UP_AABB_Z;
            }
            case WALL: {
                switch (blockState.getValue(FACING)) {
                    case EAST: {
                        return EAST_AABB;
                    }
                    case WEST: {
                        return WEST_AABB;
                    }
                    case SOUTH: {
                        return SOUTH_AABB;
                    }
                }
                return NORTH_AABB;
            }
        }
        switch (blockState.getValue(FACING).getAxis()) {
            case X: {
                return DOWN_AABB_X;
            }
        }
        return DOWN_AABB_Z;
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (level.isClientSide) {
            BlockState blockState2 = (BlockState)blockState.cycle(POWERED);
            if (blockState2.getValue(POWERED).booleanValue()) {
                LeverBlock.makeParticle(blockState2, level, blockPos, 1.0f);
            }
            return InteractionResult.SUCCESS;
        }
        BlockState blockState3 = this.pull(blockState, level, blockPos);
        float f = blockState3.getValue(POWERED) != false ? 0.6f : 0.5f;
        level.playSound(null, blockPos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.3f, f);
        return InteractionResult.CONSUME;
    }

    public BlockState pull(BlockState blockState, Level level, BlockPos blockPos) {
        blockState = (BlockState)blockState.cycle(POWERED);
        level.setBlock(blockPos, blockState, 3);
        this.updateNeighbours(blockState, level, blockPos);
        return blockState;
    }

    private static void makeParticle(BlockState blockState, LevelAccessor levelAccessor, BlockPos blockPos, float f) {
        Direction direction = blockState.getValue(FACING).getOpposite();
        Direction direction2 = LeverBlock.getConnectedDirection(blockState).getOpposite();
        double d = (double)blockPos.getX() + 0.5 + 0.1 * (double)direction.getStepX() + 0.2 * (double)direction2.getStepX();
        double d2 = (double)blockPos.getY() + 0.5 + 0.1 * (double)direction.getStepY() + 0.2 * (double)direction2.getStepY();
        double d3 = (double)blockPos.getZ() + 0.5 + 0.1 * (double)direction.getStepZ() + 0.2 * (double)direction2.getStepZ();
        levelAccessor.addParticle(new DustParticleOptions(1.0f, 0.0f, 0.0f, f), d, d2, d3, 0.0, 0.0, 0.0);
    }

    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
        if (blockState.getValue(POWERED).booleanValue() && random.nextFloat() < 0.25f) {
            LeverBlock.makeParticle(blockState, level, blockPos, 0.5f);
        }
    }

    @Override
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (bl || blockState.is(blockState2.getBlock())) {
            return;
        }
        if (blockState.getValue(POWERED).booleanValue()) {
            this.updateNeighbours(blockState, level, blockPos);
        }
        super.onRemove(blockState, level, blockPos, blockState2, bl);
    }

    @Override
    public int getSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        return blockState.getValue(POWERED) != false ? 15 : 0;
    }

    @Override
    public int getDirectSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        if (blockState.getValue(POWERED).booleanValue() && LeverBlock.getConnectedDirection(blockState) == direction) {
            return 15;
        }
        return 0;
    }

    @Override
    public boolean isSignalSource(BlockState blockState) {
        return true;
    }

    private void updateNeighbours(BlockState blockState, Level level, BlockPos blockPos) {
        level.updateNeighborsAt(blockPos, this);
        level.updateNeighborsAt(blockPos.relative(LeverBlock.getConnectedDirection(blockState).getOpposite()), this);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACE, FACING, POWERED);
    }

}

