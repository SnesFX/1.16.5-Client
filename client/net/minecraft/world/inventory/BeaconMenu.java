/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.inventory;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class BeaconMenu
extends AbstractContainerMenu {
    private final Container beacon = new SimpleContainer(1){

        @Override
        public boolean canPlaceItem(int n, ItemStack itemStack) {
            return itemStack.getItem().is(ItemTags.BEACON_PAYMENT_ITEMS);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    };
    private final PaymentSlot paymentSlot;
    private final ContainerLevelAccess access;
    private final ContainerData beaconData;

    public BeaconMenu(int n, Container container) {
        this(n, container, new SimpleContainerData(3), ContainerLevelAccess.NULL);
    }

    public BeaconMenu(int n, Container container, ContainerData containerData, ContainerLevelAccess containerLevelAccess) {
        super(MenuType.BEACON, n);
        int n2;
        BeaconMenu.checkContainerDataCount(containerData, 3);
        this.beaconData = containerData;
        this.access = containerLevelAccess;
        this.paymentSlot = new PaymentSlot(this.beacon, 0, 136, 110);
        this.addSlot(this.paymentSlot);
        this.addDataSlots(containerData);
        int n3 = 36;
        int n4 = 137;
        for (n2 = 0; n2 < 3; ++n2) {
            for (int i = 0; i < 9; ++i) {
                this.addSlot(new Slot(container, i + n2 * 9 + 9, 36 + i * 18, 137 + n2 * 18));
            }
        }
        for (n2 = 0; n2 < 9; ++n2) {
            this.addSlot(new Slot(container, n2, 36 + n2 * 18, 195));
        }
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (player.level.isClientSide) {
            return;
        }
        ItemStack itemStack = this.paymentSlot.remove(this.paymentSlot.getMaxStackSize());
        if (!itemStack.isEmpty()) {
            player.drop(itemStack, false);
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return BeaconMenu.stillValid(this.access, player, Blocks.BEACON);
    }

    @Override
    public void setData(int n, int n2) {
        super.setData(n, n2);
        this.broadcastChanges();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int n) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(n);
        if (slot != null && slot.hasItem()) {
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.copy();
            if (n == 0) {
                if (!this.moveItemStackTo(itemStack2, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemStack2, itemStack);
            } else if (!this.paymentSlot.hasItem() && this.paymentSlot.mayPlace(itemStack2) && itemStack2.getCount() == 1 ? !this.moveItemStackTo(itemStack2, 0, 1, false) : (n >= 1 && n < 28 ? !this.moveItemStackTo(itemStack2, 28, 37, false) : (n >= 28 && n < 37 ? !this.moveItemStackTo(itemStack2, 1, 28, false) : !this.moveItemStackTo(itemStack2, 1, 37, false)))) {
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

    public int getLevels() {
        return this.beaconData.get(0);
    }

    @Nullable
    public MobEffect getPrimaryEffect() {
        return MobEffect.byId(this.beaconData.get(1));
    }

    @Nullable
    public MobEffect getSecondaryEffect() {
        return MobEffect.byId(this.beaconData.get(2));
    }

    public void updateEffects(int n, int n2) {
        if (this.paymentSlot.hasItem()) {
            this.beaconData.set(1, n);
            this.beaconData.set(2, n2);
            this.paymentSlot.remove(1);
        }
    }

    public boolean hasPayment() {
        return !this.beacon.getItem(0).isEmpty();
    }

    class PaymentSlot
    extends Slot {
        public PaymentSlot(Container container, int n, int n2, int n3) {
            super(container, n, n2, n3);
        }

        @Override
        public boolean mayPlace(ItemStack itemStack) {
            return itemStack.getItem().is(ItemTags.BEACON_PAYMENT_ITEMS);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }

}

