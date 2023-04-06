/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandBlock
extends BaseEntityBlock {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    public static final BooleanProperty CONDITIONAL = BlockStateProperties.CONDITIONAL;

    public CommandBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(CONDITIONAL, false));
    }

    @Override
    public BlockEntity newBlockEntity(BlockGetter blockGetter) {
        CommandBlockEntity commandBlockEntity = new CommandBlockEntity();
        commandBlockEntity.setAutomatic(this == Blocks.CHAIN_COMMAND_BLOCK);
        return commandBlockEntity;
    }

    @Override
    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
        if (level.isClientSide) {
            return;
        }
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (!(blockEntity instanceof CommandBlockEntity)) {
            return;
        }
        CommandBlockEntity commandBlockEntity = (CommandBlockEntity)blockEntity;
        boolean bl2 = level.hasNeighborSignal(blockPos);
        boolean bl3 = commandBlockEntity.isPowered();
        commandBlockEntity.setPowered(bl2);
        if (bl3 || commandBlockEntity.isAutomatic() || commandBlockEntity.getMode() == CommandBlockEntity.Mode.SEQUENCE) {
            return;
        }
        if (bl2) {
            commandBlockEntity.markConditionMet();
            level.getBlockTicks().scheduleTick(blockPos, this, 1);
        }
    }

    @Override
    public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, Random random) {
        BlockEntity blockEntity = serverLevel.getBlockEntity(blockPos);
        if (blockEntity instanceof CommandBlockEntity) {
            CommandBlockEntity commandBlockEntity = (CommandBlockEntity)blockEntity;
            BaseCommandBlock baseCommandBlock = commandBlockEntity.getCommandBlock();
            boolean bl = !StringUtil.isNullOrEmpty(baseCommandBlock.getCommand());
            CommandBlockEntity.Mode mode = commandBlockEntity.getMode();
            boolean bl2 = commandBlockEntity.wasConditionMet();
            if (mode == CommandBlockEntity.Mode.AUTO) {
                commandBlockEntity.markConditionMet();
                if (bl2) {
                    this.execute(blockState, serverLevel, blockPos, baseCommandBlock, bl);
                } else if (commandBlockEntity.isConditional()) {
                    baseCommandBlock.setSuccessCount(0);
                }
                if (commandBlockEntity.isPowered() || commandBlockEntity.isAutomatic()) {
                    serverLevel.getBlockTicks().scheduleTick(blockPos, this, 1);
                }
            } else if (mode == CommandBlockEntity.Mode.REDSTONE) {
                if (bl2) {
                    this.execute(blockState, serverLevel, blockPos, baseCommandBlock, bl);
                } else if (commandBlockEntity.isConditional()) {
                    baseCommandBlock.setSuccessCount(0);
                }
            }
            serverLevel.updateNeighbourForOutputSignal(blockPos, this);
        }
    }

    private void execute(BlockState blockState, Level level, BlockPos blockPos, BaseCommandBlock baseCommandBlock, boolean bl) {
        if (bl) {
            baseCommandBlock.performCommand(level);
        } else {
            baseCommandBlock.setSuccessCount(0);
        }
        CommandBlock.executeChain(level, blockPos, blockState.getValue(FACING));
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof CommandBlockEntity && player.canUseGameMasterBlocks()) {
            player.openCommandBlock((CommandBlockEntity)blockEntity);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState blockState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos) {
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof CommandBlockEntity) {
            return ((CommandBlockEntity)blockEntity).getCommandBlock().getSuccessCount();
        }
        return 0;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (!(blockEntity instanceof CommandBlockEntity)) {
            return;
        }
        CommandBlockEntity commandBlockEntity = (CommandBlockEntity)blockEntity;
        BaseCommandBlock baseCommandBlock = commandBlockEntity.getCommandBlock();
        if (itemStack.hasCustomHoverName()) {
            baseCommandBlock.setName(itemStack.getHoverName());
        }
        if (!level.isClientSide) {
            if (itemStack.getTagElement("BlockEntityTag") == null) {
                baseCommandBlock.setTrackOutput(level.getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK));
                commandBlockEntity.setAutomatic(this == Blocks.CHAIN_COMMAND_BLOCK);
            }
            if (commandBlockEntity.getMode() == CommandBlockEntity.Mode.SEQUENCE) {
                boolean bl = level.hasNeighborSignal(blockPos);
                commandBlockEntity.setPowered(bl);
            }
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
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
        builder.add(FACING, CONDITIONAL);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return (BlockState)this.defaultBlockState().setValue(FACING, blockPlaceContext.getNearestLookingDirection().getOpposite());
    }

    private static void executeChain(Level level, BlockPos blockPos, Direction direction) {
        BlockPos.MutableBlockPos mutableBlockPos = blockPos.mutable();
        GameRules gameRules = level.getGameRules();
        int n = gameRules.getInt(GameRules.RULE_MAX_COMMAND_CHAIN_LENGTH);
        while (n-- > 0) {
            CommandBlockEntity commandBlockEntity;
            BlockEntity blockEntity;
            mutableBlockPos.move(direction);
            BlockState blockState = level.getBlockState(mutableBlockPos);
            Block block = blockState.getBlock();
            if (!blockState.is(Blocks.CHAIN_COMMAND_BLOCK) || !((blockEntity = level.getBlockEntity(mutableBlockPos)) instanceof CommandBlockEntity) || (commandBlockEntity = (CommandBlockEntity)blockEntity).getMode() != CommandBlockEntity.Mode.SEQUENCE) break;
            if (commandBlockEntity.isPowered() || commandBlockEntity.isAutomatic()) {
                BaseCommandBlock baseCommandBlock = commandBlockEntity.getCommandBlock();
                if (commandBlockEntity.markConditionMet()) {
                    if (!baseCommandBlock.performCommand(level)) break;
                    level.updateNeighbourForOutputSignal(mutableBlockPos, block);
                } else if (commandBlockEntity.isConditional()) {
                    baseCommandBlock.setSuccessCount(0);
                }
            }
            direction = blockState.getValue(FACING);
        }
        if (n <= 0) {
            int n2 = Math.max(gameRules.getInt(GameRules.RULE_MAX_COMMAND_CHAIN_LENGTH), 0);
            LOGGER.warn("Command Block chain tried to execute more than {} steps!", (Object)n2);
        }
    }
}

