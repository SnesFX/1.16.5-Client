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
import net.minecraft.world.inventory.ShulkerBoxSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ShulkerBoxMenu
extends AbstractContainerMenu {
    private final Container container;

    public ShulkerBoxMenu(int n, Inventory inventory) {
        this(n, inventory, new SimpleContainer(27));
    }

    public ShulkerBoxMenu(int n, Inventory inventory, Container container) {
        super(MenuType.SHULKER_BOX, n);
        int n2;
        int n3;
        ShulkerBoxMenu.checkContainerSize(container, 27);
        this.container = container;
        container.startOpen(inventory.player);
        int n4 = 3;
        int n5 = 9;
        for (n2 = 0; n2 < 3; ++n2) {
            for (n3 = 0; n3 < 9; ++n3) {
                this.addSlot(new ShulkerBoxSlot(container, n3 + n2 * 9, 8 + n3 * 18, 18 + n2 * 18));
            }
        }
        for (n2 = 0; n2 < 3; ++n2) {
            for (n3 = 0; n3 < 9; ++n3) {
                this.addSlot(new Slot(inventory, n3 + n2 * 9 + 9, 8 + n3 * 18, 84 + n2 * 18));
            }
        }
        for (n2 = 0; n2 < 9; ++n2) {
            this.addSlot(new Slot(inventory, n2, 8 + n2 * 18, 142));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int n) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(n);
        if (slot != null && slot.hasItem()) {
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.copy();
            if (n < this.container.getContainerSize() ? !this.moveItemStackTo(itemStack2, this.container.getContainerSize(), this.slots.size(), true) : !this.moveItemStackTo(itemStack2, 0, this.container.getContainerSize(), false)) {
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
        this.container.stopOpen(player);
    }
}

