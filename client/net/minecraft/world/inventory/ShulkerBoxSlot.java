/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;

public class ShulkerBoxSlot
extends Slot {
    public ShulkerBoxSlot(Container container, int n, int n2, int n3) {
        super(container, n, n2, n3);
    }

    @Override
    public boolean mayPlace(ItemStack itemStack) {
        return !(Block.byItem(itemStack.getItem()) instanceof ShulkerBoxBlock);
    }
}

