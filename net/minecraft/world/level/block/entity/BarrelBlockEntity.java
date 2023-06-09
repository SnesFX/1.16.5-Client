/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.block.entity;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class BarrelBlockEntity
extends RandomizableContainerBlockEntity {
    private NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);
    private int openCount;

    private BarrelBlockEntity(BlockEntityType<?> blockEntityType) {
        super(blockEntityType);
    }

    public BarrelBlockEntity() {
        this(BlockEntityType.BARREL);
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        super.save(compoundTag);
        if (!this.trySaveLootTable(compoundTag)) {
            ContainerHelper.saveAllItems(compoundTag, this.items);
        }
        return compoundTag;
    }

    @Override
    public void load(BlockState blockState, CompoundTag compoundTag) {
        super.load(blockState, compoundTag);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(compoundTag)) {
            ContainerHelper.loadAllItems(compoundTag, this.items);
        }
    }

    @Override
    public int getContainerSize() {
        return 27;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> nonNullList) {
        this.items = nonNullList;
    }

    @Override
    protected Component getDefaultName() {
        return new TranslatableComponent("container.barrel");
    }

    @Override
    protected AbstractContainerMenu createMenu(int n, Inventory inventory) {
        return ChestMenu.threeRows(n, inventory, this);
    }

    @Override
    public void startOpen(Player player) {
        if (!player.isSpectator()) {
            if (this.openCount < 0) {
                this.openCount = 0;
            }
            ++this.openCount;
            BlockState blockState = this.getBlockState();
            boolean bl = blockState.getValue(BarrelBlock.OPEN);
            if (!bl) {
                this.playSound(blockState, SoundEvents.BARREL_OPEN);
                this.updateBlockState(blockState, true);
            }
            this.scheduleRecheck();
        }
    }

    private void scheduleRecheck() {
        this.level.getBlockTicks().scheduleTick(this.getBlockPos(), this.getBlockState().getBlock(), 5);
    }

    public void recheckOpen() {
        int n = this.worldPosition.getX();
        int n2 = this.worldPosition.getY();
        int n3 = this.worldPosition.getZ();
        this.openCount = ChestBlockEntity.getOpenCount(this.level, this, n, n2, n3);
        if (this.openCount > 0) {
            this.scheduleRecheck();
        } else {
            BlockState blockState = this.getBlockState();
            if (!blockState.is(Blocks.BARREL)) {
                this.setRemoved();
                return;
            }
            boolean bl = blockState.getValue(BarrelBlock.OPEN);
            if (bl) {
                this.playSound(blockState, SoundEvents.BARREL_CLOSE);
                this.updateBlockState(blockState, false);
            }
        }
    }

    @Override
    public void stopOpen(Player player) {
        if (!player.isSpectator()) {
            --this.openCount;
        }
    }

    private void updateBlockState(BlockState blockState, boolean bl) {
        this.level.setBlock(this.getBlockPos(), (BlockState)blockState.setValue(BarrelBlock.OPEN, bl), 3);
    }

    private void playSound(BlockState blockState, SoundEvent soundEvent) {
        Vec3i vec3i = blockState.getValue(BarrelBlock.FACING).getNormal();
        double d = (double)this.worldPosition.getX() + 0.5 + (double)vec3i.getX() / 2.0;
        double d2 = (double)this.worldPosition.getY() + 0.5 + (double)vec3i.getY() / 2.0;
        double d3 = (double)this.worldPosition.getZ() + 0.5 + (double)vec3i.getZ() / 2.0;
        this.level.playSound(null, d, d2, d3, soundEvent, SoundSource.BLOCKS, 0.5f, this.level.random.nextFloat() * 0.1f + 0.9f);
    }
}

