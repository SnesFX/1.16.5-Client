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

public class DispenserMenu
extends AbstractContainerMenu {
    private final Container dispenser;

    public DispenserMenu(int n, Inventory inventory) {
        this(n, inventory, new SimpleContainer(9));
    }

    public DispenserMenu(int n, Inventory inventory, Container container) {
        super(MenuType.GENERIC_3x3, n);
        int n2;
        int n3;
        DispenserMenu.checkContainerSize(container, 9);
        this.dispenser = container;
        container.startOpen(inventory.player);
        for (n3 = 0; n3 < 3; ++n3) {
            for (n2 = 0; n2 < 3; ++n2) {
                this.addSlot(new Slot(container, n2 + n3 * 3, 62 + n2 * 18, 17 + n3 * 18));
            }
        }
        for (n3 = 0; n3 < 3; ++n3) {
            for (n2 = 0; n2 < 9; ++n2) {
                this.addSlot(new Slot(inventory, n2 + n3 * 9 + 9, 8 + n2 * 18, 84 + n3 * 18));
            }
        }
        for (n3 = 0; n3 < 9; ++n3) {
            this.addSlot(new Slot(inventory, n3, 8 + n3 * 18, 142));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return this.dispenser.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int n) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(n);
        if (slot != null && slot.hasItem()) {
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.copy();
            if (n < 9 ? !this.moveItemStackTo(itemStack2, 9, 45, true) : !this.moveItemStackTo(itemStack2, 0, 9, false)) {
                return ItemStack.EMPTY;
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

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.dispenser.stopOpen(player);
    }
}

