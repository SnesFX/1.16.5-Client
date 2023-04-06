/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 */
package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CrossCollisionBlock
extends Block
implements SimpleWaterloggedBlock {
    public static final BooleanProperty NORTH = PipeBlock.NORTH;
    public static final BooleanProperty EAST = PipeBlock.EAST;
    public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
    public static final BooleanProperty WEST = PipeBlock.WEST;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION.entrySet().stream().filter(entry -> ((Direction)entry.getKey()).getAxis().isHorizontal()).collect(Util.toMap());
    protected final VoxelShape[] collisionShapeByIndex;
    protected final VoxelShape[] shapeByIndex;
    private final Object2IntMap<BlockState> stateToIndex = new Object2IntOpenHashMap();

    protected CrossCollisionBlock(float f, float f2, float f3, float f4, float f5, BlockBehaviour.Properties properties) {
        super(properties);
        this.collisionShapeByIndex = this.makeShapes(f, f2, f5, 0.0f, f5);
        this.shapeByIndex = this.makeShapes(f, f2, f3, 0.0f, f4);
        for (BlockState blockState : this.stateDefinition.getPossibleStates()) {
            this.getAABBIndex(blockState);
        }
    }

    protected VoxelShape[] makeShapes(float f, float f2, float f3, float f4, float f5) {
        float f6 = 8.0f - f;
        float f7 = 8.0f + f;
        float f8 = 8.0f - f2;
        float f9 = 8.0f + f2;
        VoxelShape voxelShape = Block.box(f6, 0.0, f6, f7, f3, f7);
        VoxelShape voxelShape2 = Block.box(f8, f4, 0.0, f9, f5, f9);
        VoxelShape voxelShape3 = Block.box(f8, f4, f8, f9, f5, 16.0);
        VoxelShape voxelShape4 = Block.box(0.0, f4, f8, f9, f5, f9);
        VoxelShape voxelShape5 = Block.box(f8, f4, f8, 16.0, f5, f9);
        VoxelShape voxelShape6 = Shapes.or(voxelShape2, voxelShape5);
        VoxelShape voxelShape7 = Shapes.or(voxelShape3, voxelShape4);
        VoxelShape[] arrvoxelShape = new VoxelShape[]{Shapes.empty(), voxelShape3, voxelShape4, voxelShape7, voxelShape2, Shapes.or(voxelShape3, voxelShape2), Shapes.or(voxelShape4, voxelShape2), Shapes.or(voxelShape7, voxelShape2), voxelShape5, Shapes.or(voxelShape3, voxelShape5), Shapes.or(voxelShape4, voxelShape5), Shapes.or(voxelShape7, voxelShape5), voxelShape6, Shapes.or(voxelShape3, voxelShape6), Shapes.or(voxelShape4, voxelShape6), Shapes.or(voxelShape7, voxelShape6)};
        for (int i = 0; i < 16; ++i) {
            arrvoxelShape[i] = Shapes.or(voxelShape, arrvoxelShape[i]);
        }
        return arrvoxelShape;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return blockState.getValue(WATERLOGGED) == false;
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return this.shapeByIndex[this.getAABBIndex(blockState)];
    }

    @Override
    public VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return this.collisionShapeByIndex[this.getAABBIndex(blockState)];
    }

    private static int indexFor(Direction direction) {
        return 1 << direction.get2DDataValue();
    }

    protected int getAABBIndex(BlockState blockState2) {
        return this.stateToIndex.computeIntIfAbsent((Object)blockState2, blockState -> {
            int n = 0;
            if (blockState.getValue(NORTH).booleanValue()) {
                n |= CrossCollisionBlock.indexFor(Direction.NORTH);
            }
            if (blockState.getValue(EAST).booleanValue()) {
                n |= CrossCollisionBlock.indexFor(Direction.EAST);
            }
            if (blockState.getValue(SOUTH).booleanValue()) {
                n |= CrossCollisionBlock.indexFor(Direction.SOUTH);
            }
            if (blockState.getValue(WEST).booleanValue()) {
                n |= CrossCollisionBlock.indexFor(Direction.WEST);
            }
            return n;
        });
    }

    @Override
    public FluidState getFluidState(BlockState blockState) {
        if (blockState.getValue(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState(blockState);
    }

    @Override
    public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public BlockState rotate(BlockState blockState, Rotation rotation) {
        switch (rotation) {
            case CLOCKWISE_180: {
                return (BlockState)((BlockState)((BlockState)((BlockState)blockState.setValue(NORTH, blockState.getValue(SOUTH))).setValue(EAST, blockState.getValue(WEST))).setValue(SOUTH, blockState.getValue(NORTH))).setValue(WEST, blockState.getValue(EAST));
            }
            case COUNTERCLOCKWISE_90: {
                return (BlockState)((BlockState)((BlockState)((BlockState)blockState.setValue(NORTH, blockState.getValue(EAST))).setValue(EAST, blockState.getValue(SOUTH))).setValue(SOUTH, blockState.getValue(WEST))).setValue(WEST, blockState.getValue(NORTH));
            }
            case CLOCKWISE_90: {
                return (BlockState)((BlockState)((BlockState)((BlockState)blockState.setValue(NORTH, blockState.getValue(WEST))).setValue(EAST, blockState.getValue(NORTH))).setValue(SOUTH, blockState.getValue(EAST))).setValue(WEST, blockState.getValue(SOUTH));
            }
        }
        return blockState;
    }

    @Override
    public BlockState mirror(BlockState blockState, Mirror mirror) {
        switch (mirror) {
            case LEFT_RIGHT: {
                return (BlockState)((BlockState)blockState.setValue(NORTH, blockState.getValue(SOUTH))).setValue(SOUTH, blockState.getValue(NORTH));
            }
            case FRONT_BACK: {
                return (BlockState)((BlockState)blockState.setValue(EAST, blockState.getValue(WEST))).setValue(WEST, blockState.getValue(EAST));
            }
        }
        return super.mirror(blockState, mirror);
    }

}

