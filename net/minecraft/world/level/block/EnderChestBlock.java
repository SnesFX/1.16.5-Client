/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.block;

import java.util.OptionalInt;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.block.AbstractChestBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EnderChestBlock
extends AbstractChestBlock<EnderChestBlockEntity>
implements SimpleWaterloggedBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);
    private static final Component CONTAINER_TITLE = new TranslatableComponent("container.enderchest");

    protected EnderChestBlock(BlockBehaviour.Properties properties) {
        super(properties, () -> BlockEntityType.ENDER_CHEST);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(WATERLOGGED, false));
    }

    @Override
    public DoubleBlockCombiner.NeighborCombineResult<? extends ChestBlockEntity> combine(BlockState blockState, Level level, BlockPos blockPos, boolean bl) {
        return DoubleBlockCombiner.Combiner::acceptNone;
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        FluidState fluidState;
        return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, blockPlaceContext.getHorizontalDirection().getOpposite())).setValue(WATERLOGGED, (fluidState = blockPlaceContext.getLevel().getFluidState(blockPlaceContext.getClickedPos())).getType() == Fluids.WATER);
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player2, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        PlayerEnderChestContainer playerEnderChestContainer = player2.getEnderChestInventory();
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (playerEnderChestContainer == null || !(blockEntity instanceof EnderChestBlockEntity)) {
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        BlockPos blockPos2 = blockPos.above();
        if (level.getBlockState(blockPos2).isRedstoneConductor(level, blockPos2)) {
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        EnderChestBlockEntity enderChestBlockEntity = (EnderChestBlockEntity)blockEntity;
        playerEnderChestContainer.setActiveChest(enderChestBlockEntity);
        player2.openMenu(new SimpleMenuProvider((n, inventory, player) -> ChestMenu.threeRows(n, inventory, playerEnderChestContainer), CONTAINER_TITLE));
        player2.awardStat(Stats.OPEN_ENDERCHEST);
        PiglinAi.angerNearbyPiglins(player2, true);
        return InteractionResult.CONSUME;
    }

    @Override
    public BlockEntity newBlockEntity(BlockGetter blockGetter) {
        return new EnderChestBlockEntity();
    }

    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
        for (int i = 0; i < 3; ++i) {
            int n = random.nextInt(2) * 2 - 1;
            int n2 = random.nextInt(2) * 2 - 1;
            double d = (double)blockPos.getX() + 0.5 + 0.25 * (double)n;
            double d2 = (float)blockPos.getY() + random.nextFloat();
            double d3 = (double)blockPos.getZ() + 0.5 + 0.25 * (double)n2;
            double d4 = random.nextFloat() * (float)n;
            double d5 = ((double)random.nextFloat() - 0.5) * 0.125;
            double d6 = random.nextFloat() * (float)n2;
            level.addParticle(ParticleTypes.PORTAL, d, d2, d3, d4, d5, d6);
        }
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
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState blockState) {
        if (blockState.getValue(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState(blockState);
    }

    @Override
    public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
        if (blockState.getValue(WATERLOGGED).booleanValue()) {
            levelAccessor.getLiquidTicks().scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        }
        return super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2);
    }

    @Override
    public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
        return false;
    }
}

