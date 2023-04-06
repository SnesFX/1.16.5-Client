/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class FurnaceFuelSlot
extends Slot {
    private final AbstractFurnaceMenu menu;

    public FurnaceFuelSlot(AbstractFurnaceMenu abstractFurnaceMenu, Container container, int n, int n2, int n3) {
        super(container, n, n2, n3);
        this.menu = abstractFurnaceMenu;
    }

    @Override
    public boolean mayPlace(ItemStack itemStack) {
        return this.menu.isFuel(itemStack) || FurnaceFuelSlot.isBucket(itemStack);
    }

    @Override
    public int getMaxStackSize(ItemStack itemStack) {
        return FurnaceFuelSlot.isBucket(itemStack) ? 1 : super.getMaxStackSize(itemStack);
    }

    public static boolean isBucket(ItemStack itemStack) {
        return itemStack.getItem() == Items.BUCKET;
    }
}

