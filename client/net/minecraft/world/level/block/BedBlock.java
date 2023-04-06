/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.ArrayUtils
 */
package net.minecraft.world.level.block;

import com.mojang.datafixers.util.Either;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.ArrayUtils;

public class BedBlock
extends HorizontalDirectionalBlock
implements EntityBlock {
    public static final EnumProperty<BedPart> PART = BlockStateProperties.BED_PART;
    public static final BooleanProperty OCCUPIED = BlockStateProperties.OCCUPIED;
    protected static final VoxelShape BASE = Block.box(0.0, 3.0, 0.0, 16.0, 9.0, 16.0);
    protected static final VoxelShape LEG_NORTH_WEST = Block.box(0.0, 0.0, 0.0, 3.0, 3.0, 3.0);
    protected static final VoxelShape LEG_SOUTH_WEST = Block.box(0.0, 0.0, 13.0, 3.0, 3.0, 16.0);
    protected static final VoxelShape LEG_NORTH_EAST = Block.box(13.0, 0.0, 0.0, 16.0, 3.0, 3.0);
    protected static final VoxelShape LEG_SOUTH_EAST = Block.box(13.0, 0.0, 13.0, 16.0, 3.0, 16.0);
    protected static final VoxelShape NORTH_SHAPE = Shapes.or(BASE, LEG_NORTH_WEST, LEG_NORTH_EAST);
    protected static final VoxelShape SOUTH_SHAPE = Shapes.or(BASE, LEG_SOUTH_WEST, LEG_SOUTH_EAST);
    protected static final VoxelShape WEST_SHAPE = Shapes.or(BASE, LEG_NORTH_WEST, LEG_SOUTH_WEST);
    protected static final VoxelShape EAST_SHAPE = Shapes.or(BASE, LEG_NORTH_EAST, LEG_SOUTH_EAST);
    private final DyeColor color;

    public BedBlock(DyeColor dyeColor, BlockBehaviour.Properties properties) {
        super(properties);
        this.color = dyeColor;
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(PART, BedPart.FOOT)).setValue(OCCUPIED, false));
    }

    @Nullable
    public static Direction getBedOrientation(BlockGetter blockGetter, BlockPos blockPos) {
        BlockState blockState = blockGetter.getBlockState(blockPos);
        return blockState.getBlock() instanceof BedBlock ? blockState.getValue(FACING) : null;
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (level.isClientSide) {
            return InteractionResult.CONSUME;
        }
        if (blockState.getValue(PART) != BedPart.HEAD) {
            blockPos = blockPos.relative(blockState.getValue(FACING));
            blockState = level.getBlockState(blockPos);
            if (!blockState.is(this)) {
                return InteractionResult.CONSUME;
            }
        }
        if (!BedBlock.canSetSpawn(level)) {
            level.removeBlock(blockPos, false);
            BlockPos blockPos2 = blockPos.relative(blockState.getValue(FACING).getOpposite());
            if (level.getBlockState(blockPos2).is(this)) {
                level.removeBlock(blockPos2, false);
            }
            level.explode(null, DamageSource.badRespawnPointExplosion(), null, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, 5.0f, true, Explosion.BlockInteraction.DESTROY);
            return InteractionResult.SUCCESS;
        }
        if (blockState.getValue(OCCUPIED).booleanValue()) {
            if (!this.kickVillagerOutOfBed(level, blockPos)) {
                player.displayClientMessage(new TranslatableComponent("block.minecraft.bed.occupied"), true);
            }
            return InteractionResult.SUCCESS;
        }
        player.startSleepInBed(blockPos).ifLeft(bedSleepingProblem -> {
            if (bedSleepingProblem != null) {
                player.displayClientMessage(bedSleepingProblem.getMessage(), true);
            }
        });
        return InteractionResult.SUCCESS;
    }

    public static boolean canSetSpawn(Level level) {
        return level.dimensionType().bedWorks();
    }

    private boolean kickVillagerOutOfBed(Level level, BlockPos blockPos) {
        List<Villager> list = level.getEntitiesOfClass(Villager.class, new AABB(blockPos), LivingEntity::isSleeping);
        if (list.isEmpty()) {
            return false;
        }
        list.get(0).stopSleeping();
        return true;
    }

    @Override
    public void fallOn(Level level, BlockPos blockPos, Entity entity, float f) {
        super.fallOn(level, blockPos, entity, f * 0.5f);
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter blockGetter, Entity entity) {
        if (entity.isSuppressingBounce()) {
            super.updateEntityAfterFallOn(blockGetter, entity);
        } else {
            this.bounceUp(entity);
        }
    }

    private void bounceUp(Entity entity) {
        Vec3 vec3 = entity.getDeltaMovement();
        if (vec3.y < 0.0) {
            double d = entity instanceof LivingEntity ? 1.0 : 0.8;
            entity.setDeltaMovement(vec3.x, -vec3.y * 0.6600000262260437 * d, vec3.z);
        }
    }

    @Override
    public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
        if (direction == BedBlock.getNeighbourDirection(blockState.getValue(PART), blockState.getValue(FACING))) {
            if (blockState2.is(this) && blockState2.getValue(PART) != blockState.getValue(PART)) {
                return (BlockState)blockState.setValue(OCCUPIED, blockState2.getValue(OCCUPIED));
            }
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2);
    }

    private static Direction getNeighbourDirection(BedPart bedPart, Direction direction) {
        return bedPart == BedPart.FOOT ? direction : direction.getOpposite();
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
        BlockPos blockPos2;
        BlockState blockState2;
        BedPart bedPart;
        if (!level.isClientSide && player.isCreative() && (bedPart = blockState.getValue(PART)) == BedPart.FOOT && (blockState2 = level.getBlockState(blockPos2 = blockPos.relative(BedBlock.getNeighbourDirection(bedPart, blockState.getValue(FACING))))).getBlock() == this && blockState2.getValue(PART) == BedPart.HEAD) {
            level.setBlock(blockPos2, Blocks.AIR.defaultBlockState(), 35);
            level.levelEvent(player, 2001, blockPos2, Block.getId(blockState2));
        }
        super.playerWillDestroy(level, blockPos, blockState, player);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        Direction direction = blockPlaceContext.getHorizontalDirection();
        BlockPos blockPos = blockPlaceContext.getClickedPos();
        BlockPos blockPos2 = blockPos.relative(direction);
        if (blockPlaceContext.getLevel().getBlockState(blockPos2).canBeReplaced(blockPlaceContext)) {
            return (BlockState)this.defaultBlockState().setValue(FACING, direction);
        }
        return null;
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        Direction direction = BedBlock.getConnectedDirection(blockState).getOpposite();
        switch (direction) {
            case NORTH: {
                return NORTH_SHAPE;
            }
            case SOUTH: {
                return SOUTH_SHAPE;
            }
            case WEST: {
                return WEST_SHAPE;
            }
        }
        return EAST_SHAPE;
    }

    public static Direction getConnectedDirection(BlockState blockState) {
        Direction direction = blockState.getValue(FACING);
        return blockState.getValue(PART) == BedPart.HEAD ? direction.getOpposite() : direction;
    }

    public static DoubleBlockCombiner.BlockType getBlockType(BlockState blockState) {
        BedPart bedPart = blockState.getValue(PART);
        if (bedPart == BedPart.HEAD) {
            return DoubleBlockCombiner.BlockType.FIRST;
        }
        return DoubleBlockCombiner.BlockType.SECOND;
    }

    private static boolean isBunkBed(BlockGetter blockGetter, BlockPos blockPos) {
        return blockGetter.getBlockState(blockPos.below()).getBlock() instanceof BedBlock;
    }

    public static Optional<Vec3> findStandUpPosition(EntityType<?> entityType, CollisionGetter collisionGetter, BlockPos blockPos, float f) {
        Direction direction;
        Direction direction2 = collisionGetter.getBlockState(blockPos).getValue(FACING);
        Direction direction3 = direction2.getClockWise();
        Direction direction4 = direction = direction3.isFacingAngle(f) ? direction3.getOpposite() : direction3;
        if (BedBlock.isBunkBed(collisionGetter, blockPos)) {
            return BedBlock.findBunkBedStandUpPosition(entityType, collisionGetter, blockPos, direction2, direction);
        }
        int[][] arrn = BedBlock.bedStandUpOffsets(direction2, direction);
        Optional<Vec3> optional = BedBlock.findStandUpPositionAtOffset(entityType, collisionGetter, blockPos, arrn, true);
        if (optional.isPresent()) {
            return optional;
        }
        return BedBlock.findStandUpPositionAtOffset(entityType, collisionGetter, blockPos, arrn, false);
    }

    private static Optional<Vec3> findBunkBedStandUpPosition(EntityType<?> entityType, CollisionGetter collisionGetter, BlockPos blockPos, Direction direction, Direction direction2) {
        int[][] arrn = BedBlock.bedSurroundStandUpOffsets(direction, direction2);
        Optional<Vec3> optional = BedBlock.findStandUpPositionAtOffset(entityType, collisionGetter, blockPos, arrn, true);
        if (optional.isPresent()) {
            return optional;
        }
        BlockPos blockPos2 = blockPos.below();
        Optional<Vec3> optional2 = BedBlock.findStandUpPositionAtOffset(entityType, collisionGetter, blockPos2, arrn, true);
        if (optional2.isPresent()) {
            return optional2;
        }
        int[][] arrn2 = BedBlock.bedAboveStandUpOffsets(direction);
        Optional<Vec3> optional3 = BedBlock.findStandUpPositionAtOffset(entityType, collisionGetter, blockPos, arrn2, true);
        if (optional3.isPresent()) {
            return optional3;
        }
        Optional<Vec3> optional4 = BedBlock.findStandUpPositionAtOffset(entityType, collisionGetter, blockPos, arrn, false);
        if (optional4.isPresent()) {
            return optional4;
        }
        Optional<Vec3> optional5 = BedBlock.findStandUpPositionAtOffset(entityType, collisionGetter, blockPos2, arrn, false);
        if (optional5.isPresent()) {
            return optional5;
        }
        return BedBlock.findStandUpPositionAtOffset(entityType, collisionGetter, blockPos, arrn2, false);
    }

    private static Optional<Vec3> findStandUpPositionAtOffset(EntityType<?> entityType, CollisionGetter collisionGetter, BlockPos blockPos, int[][] arrn, boolean bl) {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int[] arrn2 : arrn) {
            mutableBlockPos.set(blockPos.getX() + arrn2[0], blockPos.getY(), blockPos.getZ() + arrn2[1]);
            Vec3 vec3 = DismountHelper.findSafeDismountLocation(entityType, collisionGetter, mutableBlockPos, bl);
            if (vec3 == null) continue;
            return Optional.of(vec3);
        }
        return Optional.empty();
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState blockState) {
        return PushReaction.DESTROY;
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART, OCCUPIED);
    }

    @Override
    public BlockEntity newBlockEntity(BlockGetter blockGetter) {
        return new BedBlockEntity(this.color);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
        super.setPlacedBy(level, blockPos, blockState, livingEntity, itemStack);
        if (!level.isClientSide) {
            BlockPos blockPos2 = blockPos.relative(blockState.getValue(FACING));
            level.setBlock(blockPos2, (BlockState)blockState.setValue(PART, BedPart.HEAD), 3);
            level.blockUpdated(blockPos, Blocks.AIR);
            blockState.updateNeighbourShapes(level, blockPos, 3);
        }
    }

    public DyeColor getColor() {
        return this.color;
    }

    @Override
    public long getSeed(BlockState blockState, BlockPos blockPos) {
        BlockPos blockPos2 = blockPos.relative(blockState.getValue(FACING), blockState.getValue(PART) == BedPart.HEAD ? 0 : 1);
        return Mth.getSeed(blockPos2.getX(), blockPos.getY(), blockPos2.getZ());
    }

    @Override
    public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
        return false;
    }

    private static int[][] bedStandUpOffsets(Direction direction, Direction direction2) {
        return (int[][])ArrayUtils.addAll((Object[])BedBlock.bedSurroundStandUpOffsets(direction, direction2), (Object[])BedBlock.bedAboveStandUpOffsets(direction));
    }

    private static int[][] bedSurroundStandUpOffsets(Direction direction, Direction direction2) {
        return new int[][]{{direction2.getStepX(), direction2.getStepZ()}, {direction2.getStepX() - direction.getStepX(), direction2.getStepZ() - direction.getStepZ()}, {direction2.getStepX() - direction.getStepX() * 2, direction2.getStepZ() - direction.getStepZ() * 2}, {-direction.getStepX() * 2, -direction.getStepZ() * 2}, {-direction2.getStepX() - direction.getStepX() * 2, -direction2.getStepZ() - direction.getStepZ() * 2}, {-direction2.getStepX() - direction.getStepX(), -direction2.getStepZ() - direction.getStepZ()}, {-direction2.getStepX(), -direction2.getStepZ()}, {-direction2.getStepX() + direction.getStepX(), -direction2.getStepZ() + direction.getStepZ()}, {direction.getStepX(), direction.getStepZ()}, {direction2.getStepX() + direction.getStepX(), direction2.getStepZ() + direction.getStepZ()}};
    }

    private static int[][] bedAboveStandUpOffsets(Direction direction) {
        return new int[][]{{0, 0}, {-direction.getStepX(), -direction.getStepZ()}};
    }

}

