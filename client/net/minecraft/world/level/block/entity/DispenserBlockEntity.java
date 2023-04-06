/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.block.entity;

import java.util.Random;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DispenserMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DispenserBlockEntity
extends RandomizableContainerBlockEntity {
    private static final Random RANDOM = new Random();
    private NonNullList<ItemStack> items = NonNullList.withSize(9, ItemStack.EMPTY);

    protected DispenserBlockEntity(BlockEntityType<?> blockEntityType) {
        super(blockEntityType);
    }

    public DispenserBlockEntity() {
        this(BlockEntityType.DISPENSER);
    }

    @Override
    public int getContainerSize() {
        return 9;
    }

    public int getRandomSlot() {
        this.unpackLootTable(null);
        int n = -1;
        int n2 = 1;
        for (int i = 0; i < this.items.size(); ++i) {
            if (this.items.get(i).isEmpty() || RANDOM.nextInt(n2++) != 0) continue;
            n = i;
        }
        return n;
    }

    public int addItem(ItemStack itemStack) {
        for (int i = 0; i < this.items.size(); ++i) {
            if (!this.items.get(i).isEmpty()) continue;
            this.setItem(i, itemStack);
            return i;
        }
        return -1;
    }

    @Override
    protected Component getDefaultName() {
        return new TranslatableComponent("container.dispenser");
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
    public CompoundTag save(CompoundTag compoundTag) {
        super.save(compoundTag);
        if (!this.trySaveLootTable(compoundTag)) {
            ContainerHelper.saveAllItems(compoundTag, this.items);
        }
        return compoundTag;
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
    protected AbstractContainerMenu createMenu(int n, Inventory inventory) {
        return new DispenserMenu(n, inventory, this);
    }
}

