/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.inventory;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public abstract class ItemCombinerMenu
extends AbstractContainerMenu {
    protected final ResultContainer resultSlots = new ResultContainer();
    protected final Container inputSlots = new SimpleContainer(2){

        @Override
        public void setChanged() {
            super.setChanged();
            ItemCombinerMenu.this.slotsChanged(this);
        }
    };
    protected final ContainerLevelAccess access;
    protected final Player player;

    protected abstract boolean mayPickup(Player var1, boolean var2);

    protected abstract ItemStack onTake(Player var1, ItemStack var2);

    protected abstract boolean isValidBlock(BlockState var1);

    public ItemCombinerMenu(@Nullable MenuType<?> menuType, int n, Inventory inventory, ContainerLevelAccess containerLevelAccess) {
        super(menuType, n);
        int n2;
        this.access = containerLevelAccess;
        this.player = inventory.player;
        this.addSlot(new Slot(this.inputSlots, 0, 27, 47));
        this.addSlot(new Slot(this.inputSlots, 1, 76, 47));
        this.addSlot(new Slot(this.resultSlots, 2, 134, 47){

            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return false;
            }

            @Override
            public boolean mayPickup(Player player) {
                return ItemCombinerMenu.this.mayPickup(player, this.hasItem());
            }

            @Override
            public ItemStack onTake(Player player, ItemStack itemStack) {
                return ItemCombinerMenu.this.onTake(player, itemStack);
            }
        });
        for (n2 = 0; n2 < 3; ++n2) {
            for (int i = 0; i < 9; ++i) {
                this.addSlot(new Slot(inventory, i + n2 * 9 + 9, 8 + i * 18, 84 + n2 * 18));
            }
        }
        for (n2 = 0; n2 < 9; ++n2) {
            this.addSlot(new Slot(inventory, n2, 8 + n2 * 18, 142));
        }
    }

    public abstract void createResult();

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        if (container == this.inputSlots) {
            this.createResult();
        }
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.access.execute((level, blockPos) -> this.clearContainer(player, (Level)level, this.inputSlots));
    }

    @Override
    public boolean stillValid(Player player) {
        return this.access.evaluate((level, blockPos) -> {
            if (!this.isValidBlock(level.getBlockState((BlockPos)blockPos))) {
                return false;
            }
            return player.distanceToSqr((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5) <= 64.0;
        }, true);
    }

    protected boolean shouldQuickMoveToAdditionalSlot(ItemStack itemStack) {
        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int n) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(n);
        if (slot != null && slot.hasItem()) {
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.copy();
            if (n == 2) {
                if (!this.moveItemStackTo(itemStack2, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemStack2, itemStack);
            } else if (n == 0 || n == 1) {
                if (!this.moveItemStackTo(itemStack2, 3, 39, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (n >= 3 && n < 39) {
                int n2;
                int n3 = n2 = this.shouldQuickMoveToAdditionalSlot(itemStack) ? 1 : 0;
                if (!this.moveItemStackTo(itemStack2, n2, 2, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if (itemStack2.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, itemStack2);
        }
        return itemStack;
    }

}

