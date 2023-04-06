/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.math.Vector3f;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.ObserverBlock;
import net.minecraft.world.level.block.RepeaterBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RedStoneWireBlock
extends Block {
    public static final EnumProperty<RedstoneSide> NORTH = BlockStateProperties.NORTH_REDSTONE;
    public static final EnumProperty<RedstoneSide> EAST = BlockStateProperties.EAST_REDSTONE;
    public static final EnumProperty<RedstoneSide> SOUTH = BlockStateProperties.SOUTH_REDSTONE;
    public static final EnumProperty<RedstoneSide> WEST = BlockStateProperties.WEST_REDSTONE;
    public static final IntegerProperty POWER = BlockStateProperties.POWER;
    public static final Map<Direction, EnumProperty<RedstoneSide>> PROPERTY_BY_DIRECTION = Maps.newEnumMap((Map)ImmutableMap.of((Object)Direction.NORTH, NORTH, (Object)Direction.EAST, EAST, (Object)Direction.SOUTH, SOUTH, (Object)Direction.WEST, WEST));
    private static final VoxelShape SHAPE_DOT = Block.box(3.0, 0.0, 3.0, 13.0, 1.0, 13.0);
    private static final Map<Direction, VoxelShape> SHAPES_FLOOR = Maps.newEnumMap((Map)ImmutableMap.of((Object)Direction.NORTH, (Object)Block.box(3.0, 0.0, 0.0, 13.0, 1.0, 13.0), (Object)Direction.SOUTH, (Object)Block.box(3.0, 0.0, 3.0, 13.0, 1.0, 16.0), (Object)Direction.EAST, (Object)Block.box(3.0, 0.0, 3.0, 16.0, 1.0, 13.0), (Object)Direction.WEST, (Object)Block.box(0.0, 0.0, 3.0, 13.0, 1.0, 13.0)));
    private static final Map<Direction, VoxelShape> SHAPES_UP = Maps.newEnumMap((Map)ImmutableMap.of((Object)Direction.NORTH, (Object)Shapes.or(SHAPES_FLOOR.get(Direction.NORTH), Block.box(3.0, 0.0, 0.0, 13.0, 16.0, 1.0)), (Object)Direction.SOUTH, (Object)Shapes.or(SHAPES_FLOOR.get(Direction.SOUTH), Block.box(3.0, 0.0, 15.0, 13.0, 16.0, 16.0)), (Object)Direction.EAST, (Object)Shapes.or(SHAPES_FLOOR.get(Direction.EAST), Block.box(15.0, 0.0, 3.0, 16.0, 16.0, 13.0)), (Object)Direction.WEST, (Object)Shapes.or(SHAPES_FLOOR.get(Direction.WEST), Block.box(0.0, 0.0, 3.0, 1.0, 16.0, 13.0))));
    private final Map<BlockState, VoxelShape> SHAPES_CACHE = Maps.newHashMap();
    private static final Vector3f[] COLORS = new Vector3f[16];
    private final BlockState crossState;
    private boolean shouldSignal = true;

    public RedStoneWireBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(NORTH, RedstoneSide.NONE)).setValue(EAST, RedstoneSide.NONE)).setValue(SOUTH, RedstoneSide.NONE)).setValue(WEST, RedstoneSide.NONE)).setValue(POWER, 0));
        this.crossState = (BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(NORTH, RedstoneSide.SIDE)).setValue(EAST, RedstoneSide.SIDE)).setValue(SOUTH, RedstoneSide.SIDE)).setValue(WEST, RedstoneSide.SIDE);
        for (BlockState blockState : this.getStateDefinition().getPossibleStates()) {
            if (blockState.getValue(POWER) != 0) continue;
            this.SHAPES_CACHE.put(blockState, this.calculateShape(blockState));
        }
    }

    private VoxelShape calculateShape(BlockState blockState) {
        VoxelShape voxelShape = SHAPE_DOT;
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            RedstoneSide redstoneSide = (RedstoneSide)blockState.getValue(PROPERTY_BY_DIRECTION.get(direction));
            if (redstoneSide == RedstoneSide.SIDE) {
                voxelShape = Shapes.or(voxelShape, SHAPES_FLOOR.get(direction));
                continue;
            }
            if (redstoneSide != RedstoneSide.UP) continue;
            voxelShape = Shapes.or(voxelShape, SHAPES_UP.get(direction));
        }
        return voxelShape;
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return this.SHAPES_CACHE.get(blockState.setValue(POWER, 0));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return this.getConnectionState(blockPlaceContext.getLevel(), this.crossState, blockPlaceContext.getClickedPos());
    }

    private BlockState getConnectionState(BlockGetter blockGetter, BlockState blockState, BlockPos blockPos) {
        boolean bl;
        boolean bl2 = RedStoneWireBlock.isDot(blockState);
        blockState = this.getMissingConnections(blockGetter, (BlockState)this.defaultBlockState().setValue(POWER, blockState.getValue(POWER)), blockPos);
        if (bl2 && RedStoneWireBlock.isDot(blockState)) {
            return blockState;
        }
        boolean bl3 = blockState.getValue(NORTH).isConnected();
        boolean bl4 = blockState.getValue(SOUTH).isConnected();
        boolean bl5 = blockState.getValue(EAST).isConnected();
        boolean bl6 = blockState.getValue(WEST).isConnected();
        boolean bl7 = !bl3 && !bl4;
        boolean bl8 = bl = !bl5 && !bl6;
        if (!bl6 && bl7) {
            blockState = (BlockState)blockState.setValue(WEST, RedstoneSide.SIDE);
        }
        if (!bl5 && bl7) {
            blockState = (BlockState)blockState.setValue(EAST, RedstoneSide.SIDE);
        }
        if (!bl3 && bl) {
            blockState = (BlockState)blockState.setValue(NORTH, RedstoneSide.SIDE);
        }
        if (!bl4 && bl) {
            blockState = (BlockState)blockState.setValue(SOUTH, RedstoneSide.SIDE);
        }
        return blockState;
    }

    private BlockState getMissingConnections(BlockGetter blockGetter, BlockState blockState, BlockPos blockPos) {
        boolean bl = !blockGetter.getBlockState(blockPos.above()).isRedstoneConductor(blockGetter, blockPos);
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (((RedstoneSide)blockState.getValue(PROPERTY_BY_DIRECTION.get(direction))).isConnected()) continue;
            RedstoneSide redstoneSide = this.getConnectingSide(blockGetter, blockPos, direction, bl);
            blockState = (BlockState)blockState.setValue(PROPERTY_BY_DIRECTION.get(direction), redstoneSide);
        }
        return blockState;
    }

    @Override
    public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
        if (direction == Direction.DOWN) {
            return blockState;
        }
        if (direction == Direction.UP) {
            return this.getConnectionState(levelAccessor, blockState, blockPos);
        }
        RedstoneSide redstoneSide = this.getConnectingSide(levelAccessor, blockPos, direction);
        if (redstoneSide.isConnected() == ((RedstoneSide)blockState.getValue(PROPERTY_BY_DIRECTION.get(direction))).isConnected() && !RedStoneWireBlock.isCross(blockState)) {
            return (BlockState)blockState.setValue(PROPERTY_BY_DIRECTION.get(direction), redstoneSide);
        }
        return this.getConnectionState(levelAccessor, (BlockState)((BlockState)this.crossState.setValue(POWER, blockState.getValue(POWER))).setValue(PROPERTY_BY_DIRECTION.get(direction), redstoneSide), blockPos);
    }

    private static boolean isCross(BlockState blockState) {
        return blockState.getValue(NORTH).isConnected() && blockState.getValue(SOUTH).isConnected() && blockState.getValue(EAST).isConnected() && blockState.getValue(WEST).isConnected();
    }

    private static boolean isDot(BlockState blockState) {
        return !blockState.getValue(NORTH).isConnected() && !blockState.getValue(SOUTH).isConnected() && !blockState.getValue(EAST).isConnected() && !blockState.getValue(WEST).isConnected();
    }

    @Override
    public void updateIndirectNeighbourShapes(BlockState blockState, LevelAccessor levelAccessor, BlockPos blockPos, int n, int n2) {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            Object object;
            Object object2;
            RedstoneSide redstoneSide = (RedstoneSide)blockState.getValue(PROPERTY_BY_DIRECTION.get(direction));
            if (redstoneSide == RedstoneSide.NONE || levelAccessor.getBlockState(mutableBlockPos.setWithOffset(blockPos, direction)).is(this)) continue;
            mutableBlockPos.move(Direction.DOWN);
            BlockState blockState2 = levelAccessor.getBlockState(mutableBlockPos);
            if (!blockState2.is(Blocks.OBSERVER)) {
                object2 = mutableBlockPos.relative(direction.getOpposite());
                object = blockState2.updateShape(direction.getOpposite(), levelAccessor.getBlockState((BlockPos)object2), levelAccessor, mutableBlockPos, (BlockPos)object2);
                RedStoneWireBlock.updateOrDestroy(blockState2, (BlockState)object, levelAccessor, mutableBlockPos, n, n2);
            }
            mutableBlockPos.setWithOffset(blockPos, direction).move(Direction.UP);
            object2 = levelAccessor.getBlockState(mutableBlockPos);
            if (((BlockBehaviour.BlockStateBase)object2).is(Blocks.OBSERVER)) continue;
            object = mutableBlockPos.relative(direction.getOpposite());
            BlockState blockState3 = ((BlockBehaviour.BlockStateBase)object2).updateShape(direction.getOpposite(), levelAccessor.getBlockState((BlockPos)object), levelAccessor, mutableBlockPos, (BlockPos)object);
            RedStoneWireBlock.updateOrDestroy((BlockState)object2, blockState3, levelAccessor, mutableBlockPos, n, n2);
        }
    }

    private RedstoneSide getConnectingSide(BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        return this.getConnectingSide(blockGetter, blockPos, direction, !blockGetter.getBlockState(blockPos.above()).isRedstoneConductor(blockGetter, blockPos));
    }

    private RedstoneSide getConnectingSide(BlockGetter blockGetter, BlockPos blockPos, Direction direction, boolean bl) {
        boolean bl2;
        BlockPos blockPos2 = blockPos.relative(direction);
        BlockState blockState = blockGetter.getBlockState(blockPos2);
        if (bl && (bl2 = this.canSurviveOn(blockGetter, blockPos2, blockState)) && RedStoneWireBlock.shouldConnectTo(blockGetter.getBlockState(blockPos2.above()))) {
            if (blockState.isFaceSturdy(blockGetter, blockPos2, direction.getOpposite())) {
                return RedstoneSide.UP;
            }
            return RedstoneSide.SIDE;
        }
        if (RedStoneWireBlock.shouldConnectTo(blockState, direction) || !blockState.isRedstoneConductor(blockGetter, blockPos2) && RedStoneWireBlock.shouldConnectTo(blockGetter.getBlockState(blockPos2.below()))) {
            return RedstoneSide.SIDE;
        }
        return RedstoneSide.NONE;
    }

    @Override
    public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        BlockPos blockPos2 = blockPos.below();
        BlockState blockState2 = levelReader.getBlockState(blockPos2);
        return this.canSurviveOn(levelReader, blockPos2, blockState2);
    }

    private boolean canSurviveOn(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
        return blockState.isFaceSturdy(blockGetter, blockPos, Direction.UP) || blockState.is(Blocks.HOPPER);
    }

    private void updatePowerStrength(Level level, BlockPos blockPos, BlockState blockState) {
        int n = this.calculateTargetStrength(level, blockPos);
        if (blockState.getValue(POWER) != n) {
            if (level.getBlockState(blockPos) == blockState) {
                level.setBlock(blockPos, (BlockState)blockState.setValue(POWER, n), 2);
            }
            HashSet hashSet = Sets.newHashSet();
            hashSet.add(blockPos);
            for (Direction direction : Direction.values()) {
                hashSet.add(blockPos.relative(direction));
            }
            for (BlockPos blockPos2 : hashSet) {
                level.updateNeighborsAt(blockPos2, this);
            }
        }
    }

    private int calculateTargetStrength(Level level, BlockPos blockPos) {
        this.shouldSignal = false;
        int n = level.getBestNeighborSignal(blockPos);
        this.shouldSignal = true;
        int n2 = 0;
        if (n < 15) {
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                BlockPos blockPos2 = blockPos.relative(direction);
                BlockState blockState = level.getBlockState(blockPos2);
                n2 = Math.max(n2, this.getWireSignal(blockState));
                BlockPos blockPos3 = blockPos.above();
                if (blockState.isRedstoneConductor(level, blockPos2) && !level.getBlockState(blockPos3).isRedstoneConductor(level, blockPos3)) {
                    n2 = Math.max(n2, this.getWireSignal(level.getBlockState(blockPos2.above())));
                    continue;
                }
                if (blockState.isRedstoneConductor(level, blockPos2)) continue;
                n2 = Math.max(n2, this.getWireSignal(level.getBlockState(blockPos2.below())));
            }
        }
        return Math.max(n, n2 - 1);
    }

    private int getWireSignal(BlockState blockState) {
        return blockState.is(this) ? blockState.getValue(POWER) : 0;
    }

    private void checkCornerChangeAt(Level level, BlockPos blockPos) {
        if (!level.getBlockState(blockPos).is(this)) {
            return;
        }
        level.updateNeighborsAt(blockPos, this);
        for (Direction direction : Direction.values()) {
            level.updateNeighborsAt(blockPos.relative(direction), this);
        }
    }

    @Override
    public void onPlace(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (blockState2.is(blockState.getBlock()) || level.isClientSide) {
            return;
        }
        this.updatePowerStrength(level, blockPos, blockState);
        for (Direction direction : Direction.Plane.VERTICAL) {
            level.updateNeighborsAt(blockPos.relative(direction), this);
        }
        this.updateNeighborsOfNeighboringWires(level, blockPos);
    }

    @Override
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (bl || blockState.is(blockState2.getBlock())) {
            return;
        }
        super.onRemove(blockState, level, blockPos, blockState2, bl);
        if (level.isClientSide) {
            return;
        }
        for (Direction direction : Direction.values()) {
            level.updateNeighborsAt(blockPos.relative(direction), this);
        }
        this.updatePowerStrength(level, blockPos, blockState);
        this.updateNeighborsOfNeighboringWires(level, blockPos);
    }

    private void updateNeighborsOfNeighboringWires(Level level, BlockPos blockPos) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            this.checkCornerChangeAt(level, blockPos.relative(direction));
        }
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos blockPos2 = blockPos.relative(direction);
            if (level.getBlockState(blockPos2).isRedstoneConductor(level, blockPos2)) {
                this.checkCornerChangeAt(level, blockPos2.above());
                continue;
            }
            this.checkCornerChangeAt(level, blockPos2.below());
        }
    }

    @Override
    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
        if (level.isClientSide) {
            return;
        }
        if (blockState.canSurvive(level, blockPos)) {
            this.updatePowerStrength(level, blockPos, blockState);
        } else {
            RedStoneWireBlock.dropResources(blockState, level, blockPos);
            level.removeBlock(blockPos, false);
        }
    }

    @Override
    public int getDirectSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        if (!this.shouldSignal) {
            return 0;
        }
        return blockState.getSignal(blockGetter, blockPos, direction);
    }

    @Override
    public int getSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        if (!this.shouldSignal || direction == Direction.DOWN) {
            return 0;
        }
        int n = blockState.getValue(POWER);
        if (n == 0) {
            return 0;
        }
        if (direction == Direction.UP || ((RedstoneSide)this.getConnectionState(blockGetter, blockState, blockPos).getValue(PROPERTY_BY_DIRECTION.get(direction.getOpposite()))).isConnected()) {
            return n;
        }
        return 0;
    }

    protected static boolean shouldConnectTo(BlockState blockState) {
        return RedStoneWireBlock.shouldConnectTo(blockState, null);
    }

    protected static boolean shouldConnectTo(BlockState blockState, @Nullable Direction direction) {
        if (blockState.is(Blocks.REDSTONE_WIRE)) {
            return true;
        }
        if (blockState.is(Blocks.REPEATER)) {
            Direction direction2 = blockState.getValue(RepeaterBlock.FACING);
            return direction2 == direction || direction2.getOpposite() == direction;
        }
        if (blockState.is(Blocks.OBSERVER)) {
            return direction == blockState.getValue(ObserverBlock.FACING);
        }
        return blockState.isSignalSource() && direction != null;
    }

    @Override
    public boolean isSignalSource(BlockState blockState) {
        return this.shouldSignal;
    }

    public static int getColorForPower(int n) {
        Vector3f vector3f = COLORS[n];
        return Mth.color(vector3f.x(), vector3f.y(), vector3f.z());
    }

    private void spawnParticlesAlongLine(Level level, Random random, BlockPos blockPos, Vector3f vector3f, Direction direction, Direction direction2, float f, float f2) {
        float f3 = f2 - f;
        if (random.nextFloat() >= 0.2f * f3) {
            return;
        }
        float f4 = 0.4375f;
        float f5 = f + f3 * random.nextFloat();
        double d = 0.5 + (double)(0.4375f * (float)direction.getStepX()) + (double)(f5 * (float)direction2.getStepX());
        double d2 = 0.5 + (double)(0.4375f * (float)direction.getStepY()) + (double)(f5 * (float)direction2.getStepY());
        double d3 = 0.5 + (double)(0.4375f * (float)direction.getStepZ()) + (double)(f5 * (float)direction2.getStepZ());
        level.addParticle(new DustParticleOptions(vector3f.x(), vector3f.y(), vector3f.z(), 1.0f), (double)blockPos.getX() + d, (double)blockPos.getY() + d2, (double)blockPos.getZ() + d3, 0.0, 0.0, 0.0);
    }

    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
        int n = blockState.getValue(POWER);
        if (n == 0) {
            return;
        }
        block4 : for (Direction direction : Direction.Plane.HORIZONTAL) {
            RedstoneSide redstoneSide = (RedstoneSide)blockState.getValue(PROPERTY_BY_DIRECTION.get(direction));
            switch (redstoneSide) {
                case UP: {
                    this.spawnParticlesAlongLine(level, random, blockPos, COLORS[n], direction, Direction.UP, -0.5f, 0.5f);
                }
                case SIDE: {
                    this.spawnParticlesAlongLine(level, random, blockPos, COLORS[n], Direction.DOWN, direction, 0.0f, 0.5f);
                    continue block4;
                }
            }
            this.spawnParticlesAlongLine(level, random, blockPos, COLORS[n], Direction.DOWN, direction, 0.0f, 0.3f);
        }
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

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, POWER);
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (!player.abilities.mayBuild) {
            return InteractionResult.PASS;
        }
        if (RedStoneWireBlock.isCross(blockState) || RedStoneWireBlock.isDot(blockState)) {
            BlockState blockState2 = RedStoneWireBlock.isCross(blockState) ? this.defaultBlockState() : this.crossState;
            blockState2 = (BlockState)blockState2.setValue(POWER, blockState.getValue(POWER));
            if ((blockState2 = this.getConnectionState(level, blockState2, blockPos)) != blockState) {
                level.setBlock(blockPos, blockState2, 3);
                this.updatesOnShapeChange(level, blockPos, blockState, blockState2);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    private void updatesOnShapeChange(Level level, BlockPos blockPos, BlockState blockState, BlockState blockState2) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos blockPos2 = blockPos.relative(direction);
            if (((RedstoneSide)blockState.getValue(PROPERTY_BY_DIRECTION.get(direction))).isConnected() == ((RedstoneSide)blockState2.getValue(PROPERTY_BY_DIRECTION.get(direction))).isConnected() || !level.getBlockState(blockPos2).isRedstoneConductor(level, blockPos2)) continue;
            level.updateNeighborsAtExceptFromFacing(blockPos2, blockState2.getBlock(), direction.getOpposite());
        }
    }

    static {
        for (int i = 0; i <= 15; ++i) {
            float f;
            float f2 = f * 0.6f + ((f = (float)i / 15.0f) > 0.0f ? 0.4f : 0.3f);
            float f3 = Mth.clamp(f * f * 0.7f - 0.5f, 0.0f, 1.0f);
            float f4 = Mth.clamp(f * f * 0.6f - 0.7f, 0.0f, 1.0f);
            RedStoneWireBlock.COLORS[i] = new Vector3f(f2, f3, f4);
        }
    }

}

