/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block;

import com.google.common.base.MoreObjects;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.TripWireBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TripWireHookBlock
extends Block {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty ATTACHED = BlockStateProperties.ATTACHED;
    protected static final VoxelShape NORTH_AABB = Block.box(5.0, 0.0, 10.0, 11.0, 10.0, 16.0);
    protected static final VoxelShape SOUTH_AABB = Block.box(5.0, 0.0, 0.0, 11.0, 10.0, 6.0);
    protected static final VoxelShape WEST_AABB = Block.box(10.0, 0.0, 5.0, 16.0, 10.0, 11.0);
    protected static final VoxelShape EAST_AABB = Block.box(0.0, 0.0, 5.0, 6.0, 10.0, 11.0);

    public TripWireHookBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(POWERED, false)).setValue(ATTACHED, false));
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        switch (blockState.getValue(FACING)) {
            default: {
                return EAST_AABB;
            }
            case WEST: {
                return WEST_AABB;
            }
            case SOUTH: {
                return SOUTH_AABB;
            }
            case NORTH: 
        }
        return NORTH_AABB;
    }

    @Override
    public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        Direction direction = blockState.getValue(FACING);
        BlockPos blockPos2 = blockPos.relative(direction.getOpposite());
        BlockState blockState2 = levelReader.getBlockState(blockPos2);
        return direction.getAxis().isHorizontal() && blockState2.isFaceSturdy(levelReader, blockPos2, direction);
    }

    @Override
    public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
        if (direction.getOpposite() == blockState.getValue(FACING) && !blockState.canSurvive(levelAccessor, blockPos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        Direction[] arrdirection;
        BlockState blockState = (BlockState)((BlockState)this.defaultBlockState().setValue(POWERED, false)).setValue(ATTACHED, false);
        Level level = blockPlaceContext.getLevel();
        BlockPos blockPos = blockPlaceContext.getClickedPos();
        for (Direction direction : arrdirection = blockPlaceContext.getNearestLookingDirections()) {
            Direction direction2;
            if (!direction.getAxis().isHorizontal() || !(blockState = (BlockState)blockState.setValue(FACING, direction2 = direction.getOpposite())).canSurvive(level, blockPos)) continue;
            return blockState;
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
        this.calculateState(level, blockPos, blockState, false, false, -1, null);
    }

    public void calculateState(Level level, BlockPos blockPos, BlockState blockState, boolean bl, boolean bl2, int n, @Nullable BlockState blockState2) {
        Object object;
        BlockPos blockPos2;
        Direction direction = blockState.getValue(FACING);
        boolean bl3 = blockState.getValue(ATTACHED);
        boolean bl4 = blockState.getValue(POWERED);
        boolean bl5 = !bl;
        boolean bl6 = false;
        int n2 = 0;
        BlockState[] arrblockState = new BlockState[42];
        for (int i = 1; i < 42; ++i) {
            blockPos2 = blockPos.relative(direction, i);
            object = level.getBlockState(blockPos2);
            if (((BlockBehaviour.BlockStateBase)object).is(Blocks.TRIPWIRE_HOOK)) {
                if (((StateHolder)object).getValue(FACING) != direction.getOpposite()) break;
                n2 = i;
                break;
            }
            if (((BlockBehaviour.BlockStateBase)object).is(Blocks.TRIPWIRE) || i == n) {
                if (i == n) {
                    object = (BlockState)MoreObjects.firstNonNull((Object)blockState2, (Object)object);
                }
                boolean bl7 = ((StateHolder)object).getValue(TripWireBlock.DISARMED) == false;
                boolean bl8 = ((StateHolder)object).getValue(TripWireBlock.POWERED);
                bl6 |= bl7 && bl8;
                arrblockState[i] = object;
                if (i != n) continue;
                level.getBlockTicks().scheduleTick(blockPos, this, 10);
                bl5 &= bl7;
                continue;
            }
            arrblockState[i] = null;
            bl5 = false;
        }
        BlockState blockState3 = (BlockState)((BlockState)this.defaultBlockState().setValue(ATTACHED, bl5)).setValue(POWERED, bl6 &= (bl5 &= n2 > 1));
        if (n2 > 0) {
            blockPos2 = blockPos.relative(direction, n2);
            object = direction.getOpposite();
            level.setBlock(blockPos2, (BlockState)blockState3.setValue(FACING, object), 3);
            this.notifyNeighbors(level, blockPos2, (Direction)object);
            this.playSound(level, blockPos2, bl5, bl6, bl3, bl4);
        }
        this.playSound(level, blockPos, bl5, bl6, bl3, bl4);
        if (!bl) {
            level.setBlock(blockPos, (BlockState)blockState3.setValue(FACING, direction), 3);
            if (bl2) {
                this.notifyNeighbors(level, blockPos, direction);
            }
        }
        if (bl3 != bl5) {
            for (int i = 1; i < n2; ++i) {
                object = blockPos.relative(direction, i);
                BlockState blockState4 = arrblockState[i];
                if (blockState4 == null) continue;
                level.setBlock((BlockPos)object, (BlockState)blockState4.setValue(ATTACHED, bl5), 3);
                if (level.getBlockState((BlockPos)object).isAir()) continue;
            }
        }
    }

    @Override
    public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, Random random) {
        this.calculateState(serverLevel, blockPos, blockState, false, true, -1, null);
    }

    private void playSound(Level level, BlockPos blockPos, boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        if (bl2 && !bl4) {
            level.playSound(null, blockPos, SoundEvents.TRIPWIRE_CLICK_ON, SoundSource.BLOCKS, 0.4f, 0.6f);
        } else if (!bl2 && bl4) {
            level.playSound(null, blockPos, SoundEvents.TRIPWIRE_CLICK_OFF, SoundSource.BLOCKS, 0.4f, 0.5f);
        } else if (bl && !bl3) {
            level.playSound(null, blockPos, SoundEvents.TRIPWIRE_ATTACH, SoundSource.BLOCKS, 0.4f, 0.7f);
        } else if (!bl && bl3) {
            level.playSound(null, blockPos, SoundEvents.TRIPWIRE_DETACH, SoundSource.BLOCKS, 0.4f, 1.2f / (level.random.nextFloat() * 0.2f + 0.9f));
        }
    }

    private void notifyNeighbors(Level level, BlockPos blockPos, Direction direction) {
        level.updateNeighborsAt(blockPos, this);
        level.updateNeighborsAt(blockPos.relative(direction.getOpposite()), this);
    }

    @Override
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (bl || blockState.is(blockState2.getBlock())) {
            return;
        }
        boolean bl2 = blockState.getValue(ATTACHED);
        boolean bl3 = blockState.getValue(POWERED);
        if (bl2 || bl3) {
            this.calculateState(level, blockPos, blockState, true, false, -1, null);
        }
        if (bl3) {
            level.updateNeighborsAt(blockPos, this);
            level.updateNeighborsAt(blockPos.relative(blockState.getValue(FACING).getOpposite()), this);
        }
        super.onRemove(blockState, level, blockPos, blockState2, bl);
    }

    @Override
    public int getSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        return blockState.getValue(POWERED) != false ? 15 : 0;
    }

    @Override
    public int getDirectSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        if (!blockState.getValue(POWERED).booleanValue()) {
            return 0;
        }
        if (blockState.getValue(FACING) == direction) {
            return 15;
        }
        return 0;
    }

    @Override
    public boolean isSignalSource(BlockState blockState) {
        return true;
    }

    @Override
    public BlockState rotate(BlockState blockState, Rotation rotation) {
        return (BlockState)blockState.setValue(FACING, rotation.rotate(blockState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState blockState, Mirror mirror) {
        return blockState.rotate(mirror.getRotation(blockState.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, ATTACHED);
    }

}

