/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CommandBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class CommandBlockEntity
extends BlockEntity {
    private boolean powered;
    private boolean auto;
    private boolean conditionMet;
    private boolean sendToClient;
    private final BaseCommandBlock commandBlock = new BaseCommandBlock(){

        @Override
        public void setCommand(String string) {
            super.setCommand(string);
            CommandBlockEntity.this.setChanged();
        }

        @Override
        public ServerLevel getLevel() {
            return (ServerLevel)CommandBlockEntity.this.level;
        }

        @Override
        public void onUpdated() {
            BlockState blockState = CommandBlockEntity.this.level.getBlockState(CommandBlockEntity.this.worldPosition);
            this.getLevel().sendBlockUpdated(CommandBlockEntity.this.worldPosition, blockState, blockState, 3);
        }

        @Override
        public Vec3 getPosition() {
            return Vec3.atCenterOf(CommandBlockEntity.this.worldPosition);
        }

        @Override
        public CommandSourceStack createCommandSourceStack() {
            return new CommandSourceStack(this, Vec3.atCenterOf(CommandBlockEntity.this.worldPosition), Vec2.ZERO, this.getLevel(), 2, this.getName().getString(), this.getName(), this.getLevel().getServer(), null);
        }
    };

    public CommandBlockEntity() {
        super(BlockEntityType.COMMAND_BLOCK);
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        super.save(compoundTag);
        this.commandBlock.save(compoundTag);
        compoundTag.putBoolean("powered", this.isPowered());
        compoundTag.putBoolean("conditionMet", this.wasConditionMet());
        compoundTag.putBoolean("auto", this.isAutomatic());
        return compoundTag;
    }

    @Override
    public void load(BlockState blockState, CompoundTag compoundTag) {
        super.load(blockState, compoundTag);
        this.commandBlock.load(compoundTag);
        this.powered = compoundTag.getBoolean("powered");
        this.conditionMet = compoundTag.getBoolean("conditionMet");
        this.setAutomatic(compoundTag.getBoolean("auto"));
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        if (this.isSendToClient()) {
            this.setSendToClient(false);
            CompoundTag compoundTag = this.save(new CompoundTag());
            return new ClientboundBlockEntityDataPacket(this.worldPosition, 2, compoundTag);
        }
        return null;
    }

    @Override
    public boolean onlyOpCanSetNbt() {
        return true;
    }

    public BaseCommandBlock getCommandBlock() {
        return this.commandBlock;
    }

    public void setPowered(boolean bl) {
        this.powered = bl;
    }

    public boolean isPowered() {
        return this.powered;
    }

    public boolean isAutomatic() {
        return this.auto;
    }

    public void setAutomatic(boolean bl) {
        boolean bl2 = this.auto;
        this.auto = bl;
        if (!bl2 && bl && !this.powered && this.level != null && this.getMode() != Mode.SEQUENCE) {
            this.scheduleTick();
        }
    }

    public void onModeSwitch() {
        Mode mode = this.getMode();
        if (mode == Mode.AUTO && (this.powered || this.auto) && this.level != null) {
            this.scheduleTick();
        }
    }

    private void scheduleTick() {
        Block block = this.getBlockState().getBlock();
        if (block instanceof CommandBlock) {
            this.markConditionMet();
            this.level.getBlockTicks().scheduleTick(this.worldPosition, block, 1);
        }
    }

    public boolean wasConditionMet() {
        return this.conditionMet;
    }

    public boolean markConditionMet() {
        this.conditionMet = true;
        if (this.isConditional()) {
            BlockEntity blockEntity;
            BlockPos blockPos = this.worldPosition.relative(this.level.getBlockState(this.worldPosition).getValue(CommandBlock.FACING).getOpposite());
            this.conditionMet = this.level.getBlockState(blockPos).getBlock() instanceof CommandBlock ? (blockEntity = this.level.getBlockEntity(blockPos)) instanceof CommandBlockEntity && ((CommandBlockEntity)blockEntity).getCommandBlock().getSuccessCount() > 0 : false;
        }
        return this.conditionMet;
    }

    public boolean isSendToClient() {
        return this.sendToClient;
    }

    public void setSendToClient(boolean bl) {
        this.sendToClient = bl;
    }

    public Mode getMode() {
        BlockState blockState = this.getBlockState();
        if (blockState.is(Blocks.COMMAND_BLOCK)) {
            return Mode.REDSTONE;
        }
        if (blockState.is(Blocks.REPEATING_COMMAND_BLOCK)) {
            return Mode.AUTO;
        }
        if (blockState.is(Blocks.CHAIN_COMMAND_BLOCK)) {
            return Mode.SEQUENCE;
        }
        return Mode.REDSTONE;
    }

    public boolean isConditional() {
        BlockState blockState = this.level.getBlockState(this.getBlockPos());
        if (blockState.getBlock() instanceof CommandBlock) {
            return blockState.getValue(CommandBlock.CONDITIONAL);
        }
        return false;
    }

    @Override
    public void clearRemoved() {
        this.clearCache();
        super.clearRemoved();
    }

    public static enum Mode {
        SEQUENCE,
        AUTO,
        REDSTONE;
        
    }

}

