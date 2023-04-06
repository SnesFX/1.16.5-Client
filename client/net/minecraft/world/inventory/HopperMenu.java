/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.inventory;

import java.util.List;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class HopperMenu
extends AbstractContainerMenu {
    private final Container hopper;

    public HopperMenu(int n, Inventory inventory) {
        this(n, inventory, new SimpleContainer(5));
    }

    public HopperMenu(int n, Inventory inventory, Container container) {
        super(MenuType.HOPPER, n);
        int n2;
        this.hopper = container;
        HopperMenu.checkContainerSize(container, 5);
        container.startOpen(inventory.player);
        int n3 = 51;
        for (n2 = 0; n2 < 5; ++n2) {
            this.addSlot(new Slot(container, n2, 44 + n2 * 18, 20));
        }
        for (n2 = 0; n2 < 3; ++n2) {
            for (int i = 0; i < 9; ++i) {
                this.addSlot(new Slot(inventory, i + n2 * 9 + 9, 8 + i * 18, n2 * 18 + 51));
            }
        }
        for (n2 = 0; n2 < 9; ++n2) {
            this.addSlot(new Slot(inventory, n2, 8 + n2 * 18, 109));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return this.hopper.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int n) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(n);
        if (slot != null && slot.hasItem()) {
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.copy();
            if (n < this.hopper.getContainerSize() ? !this.moveItemStackTo(itemStack2, this.hopper.getContainerSize(), this.slots.size(), true) : !this.moveItemStackTo(itemStack2, 0, this.hopper.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemStack;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.hopper.stopOpen(player);
    }
}

