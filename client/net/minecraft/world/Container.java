/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world;

import java.util.Set;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface Container
extends Clearable {
    public int getContainerSize();

    public boolean isEmpty();

    public ItemStack getItem(int var1);

    public ItemStack removeItem(int var1, int var2);

    public ItemStack removeItemNoUpdate(int var1);

    public void setItem(int var1, ItemStack var2);

    default public int getMaxStackSize() {
        return 64;
    }

    public void setChanged();

    public boolean stillValid(Player var1);

    default public void startOpen(Player player) {
    }

    default public void stopOpen(Player player) {
    }

    default public boolean canPlaceItem(int n, ItemStack itemStack) {
        return true;
    }

    default public int countItem(Item item) {
        int n = 0;
        for (int i = 0; i < this.getContainerSize(); ++i) {
            ItemStack itemStack = this.getItem(i);
            if (!itemStack.getItem().equals(item)) continue;
            n += itemStack.getCount();
        }
        return n;
    }

    default public boolean hasAnyOf(Set<Item> set) {
        for (int i = 0; i < this.getContainerSize(); ++i) {
            ItemStack itemStack = this.getItem(i);
            if (!set.contains(itemStack.getItem()) || itemStack.getCount() <= 0) continue;
            return true;
        }
        return false;
    }
}

