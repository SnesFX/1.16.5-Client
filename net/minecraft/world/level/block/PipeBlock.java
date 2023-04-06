/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package net.minecraft.world.level.block;

import com.google.common.collect.Maps;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PipeBlock
extends Block {
    private static final Direction[] DIRECTIONS = Direction.values();
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = Util.make(Maps.newEnumMap(Direction.class), enumMap -> {
        enumMap.put(Direction.NORTH, NORTH);
        enumMap.put(Direction.EAST, EAST);
        enumMap.put(Direction.SOUTH, SOUTH);
        enumMap.put(Direction.WEST, WEST);
        enumMap.put(Direction.UP, UP);
        enumMap.put(Direction.DOWN, DOWN);
    });
    protected final VoxelShape[] shapeByIndex;

    protected PipeBlock(float f, BlockBehaviour.Properties properties) {
        super(properties);
        this.shapeByIndex = this.makeShapes(f);
    }

    private VoxelShape[] makeShapes(float f) {
        float f2 = 0.5f - f;
        float f3 = 0.5f + f;
        VoxelShape voxelShape = Block.box(f2 * 16.0f, f2 * 16.0f, f2 * 16.0f, f3 * 16.0f, f3 * 16.0f, f3 * 16.0f);
        VoxelShape[] arrvoxelShape = new VoxelShape[DIRECTIONS.length];
        for (int i = 0; i < DIRECTIONS.length; ++i) {
            Direction direction = DIRECTIONS[i];
            arrvoxelShape[i] = Shapes.box(0.5 + Math.min((double)(-f), (double)direction.getStepX() * 0.5), 0.5 + Math.min((double)(-f), (double)direction.getStepY() * 0.5), 0.5 + Math.min((double)(-f), (double)direction.getStepZ() * 0.5), 0.5 + Math.max((double)f, (double)direction.getStepX() * 0.5), 0.5 + Math.max((double)f, (double)direction.getStepY() * 0.5), 0.5 + Math.max((double)f, (double)direction.getStepZ() * 0.5));
        }
        VoxelShape[] arrvoxelShape2 = new VoxelShape[64];
        for (int i = 0; i < 64; ++i) {
            VoxelShape voxelShape2 = voxelShape;
            for (int j = 0; j < DIRECTIONS.length; ++j) {
                if ((i & 1 << j) == 0) continue;
                voxelShape2 = Shapes.or(voxelShape2, arrvoxelShape[j]);
            }
            arrvoxelShape2[i] = voxelShape2;
        }
        return arrvoxelShape2;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return false;
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return this.shapeByIndex[this.getAABBIndex(blockState)];
    }

    protected int getAABBIndex(BlockState blockState) {
        int n = 0;
        for (int i = 0; i < DIRECTIONS.length; ++i) {
            if (!((Boolean)blockState.getValue(PROPERTY_BY_DIRECTION.get(DIRECTIONS[i]))).booleanValue()) continue;
            n |= 1 << i;
        }
        return n;
    }
}

