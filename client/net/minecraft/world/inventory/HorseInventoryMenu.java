/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.inventory;

import java.util.List;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class HorseInventoryMenu
extends AbstractContainerMenu {
    private final Container horseContainer;
    private final AbstractHorse horse;

    public HorseInventoryMenu(int n, Inventory inventory, Container container, final AbstractHorse abstractHorse) {
        super(null, n);
        int n2;
        int n3;
        this.horseContainer = container;
        this.horse = abstractHorse;
        int n4 = 3;
        container.startOpen(inventory.player);
        int n5 = -18;
        this.addSlot(new Slot(container, 0, 8, 18){

            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return itemStack.getItem() == Items.SADDLE && !this.hasItem() && abstractHorse.isSaddleable();
            }

            @Override
            public boolean isActive() {
                return abstractHorse.isSaddleable();
            }
        });
        this.addSlot(new Slot(container, 1, 8, 36){

            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return abstractHorse.isArmor(itemStack);
            }

            @Override
            public boolean isActive() {
                return abstractHorse.canWearArmor();
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });
        if (abstractHorse instanceof AbstractChestedHorse && ((AbstractChestedHorse)abstractHorse).hasChest()) {
            for (n3 = 0; n3 < 3; ++n3) {
                for (n2 = 0; n2 < ((AbstractChestedHorse)abstractHorse).getInventoryColumns(); ++n2) {
                    this.addSlot(new Slot(container, 2 + n2 + n3 * ((AbstractChestedHorse)abstractHorse).getInventoryColumns(), 80 + n2 * 18, 18 + n3 * 18));
                }
            }
        }
        for (n3 = 0; n3 < 3; ++n3) {
            for (n2 = 0; n2 < 9; ++n2) {
                this.addSlot(new Slot(inventory, n2 + n3 * 9 + 9, 8 + n2 * 18, 102 + n3 * 18 + -18));
            }
        }
        for (n3 = 0; n3 < 9; ++n3) {
            this.addSlot(new Slot(inventory, n3, 8 + n3 * 18, 142));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return this.horseContainer.stillValid(player) && this.horse.isAlive() && this.horse.distanceTo(player) < 8.0f;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int n) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(n);
        if (slot != null && slot.hasItem()) {
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.copy();
            int n2 = this.horseContainer.getContainerSize();
            if (n < n2) {
                if (!this.moveItemStackTo(itemStack2, n2, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.getSlot(1).mayPlace(itemStack2) && !this.getSlot(1).hasItem()) {
                if (!this.moveItemStackTo(itemStack2, 1, 2, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.getSlot(0).mayPlace(itemStack2)) {
                if (!this.moveItemStackTo(itemStack2, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (n2 <= 2 || !this.moveItemStackTo(itemStack2, 2, n2, false)) {
                int n3;
                int n4 = n2;
                int n5 = n3 = n4 + 27;
                int n6 = n5 + 9;
                if (n >= n5 && n < n6 ? !this.moveItemStackTo(itemStack2, n4, n3, false) : (n >= n4 && n < n3 ? !this.moveItemStackTo(itemStack2, n5, n6, false) : !this.moveItemStackTo(itemStack2, n5, n3, false))) {
                    return ItemStack.EMPTY;
                }
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
        this.horseContainer.stopOpen(player);
    }

}

