/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EndRodBlock
extends DirectionalBlock {
    protected static final VoxelShape Y_AXIS_AABB = Block.box(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);
    protected static final VoxelShape Z_AXIS_AABB = Block.box(6.0, 6.0, 0.0, 10.0, 10.0, 16.0);
    protected static final VoxelShape X_AXIS_AABB = Block.box(0.0, 6.0, 6.0, 16.0, 10.0, 10.0);

    protected EndRodBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.UP));
    }

    @Override
    public BlockState rotate(BlockState blockState, Rotation rotation) {
        return (BlockState)blockState.setValue(FACING, rotation.rotate(blockState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState blockState, Mirror mirror) {
        return (BlockState)blockState.setValue(FACING, mirror.mirror(blockState.getValue(FACING)));
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        switch (blockState.getValue(FACING).getAxis()) {
            default: {
                return X_AXIS_AABB;
            }
            case Z: {
                return Z_AXIS_AABB;
            }
            case Y: 
        }
        return Y_AXIS_AABB;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        Direction direction = blockPlaceContext.getClickedFace();
        BlockState blockState = blockPlaceContext.getLevel().getBlockState(blockPlaceContext.getClickedPos().relative(direction.getOpposite()));
        if (blockState.is(this) && blockState.getValue(FACING) == direction) {
            return (BlockState)this.defaultBlockState().setValue(FACING, direction.getOpposite());
        }
        return (BlockState)this.defaultBlockState().setValue(FACING, direction);
    }

    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
        Direction direction = blockState.getValue(FACING);
        double d = (double)blockPos.getX() + 0.55 - (double)(random.nextFloat() * 0.1f);
        double d2 = (double)blockPos.getY() + 0.55 - (double)(random.nextFloat() * 0.1f);
        double d3 = (double)blockPos.getZ() + 0.55 - (double)(random.nextFloat() * 0.1f);
        double d4 = 0.4f - (random.nextFloat() + random.nextFloat()) * 0.4f;
        if (random.nextInt(5) == 0) {
            level.addParticle(ParticleTypes.END_ROD, d + (double)direction.getStepX() * d4, d2 + (double)direction.getStepY() * d4, d3 + (double)direction.getStepZ() * d4, random.nextGaussian() * 0.005, random.nextGaussian() * 0.005, random.nextGaussian() * 0.005);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState blockState) {
        return PushReaction.NORMAL;
    }

    @Override
    public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
        return false;
    }

}

