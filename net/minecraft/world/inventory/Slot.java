/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  javax.annotation.Nullable
 */
package net.minecraft.world.inventory;

import com.mojang.datafixers.util.Pair;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class Slot {
    private final int slot;
    public final Container container;
    public int index;
    public final int x;
    public final int y;

    public Slot(Container container, int n, int n2, int n3) {
        this.container = container;
        this.slot = n;
        this.x = n2;
        this.y = n3;
    }

    public void onQuickCraft(ItemStack itemStack, ItemStack itemStack2) {
        int n = itemStack2.getCount() - itemStack.getCount();
        if (n > 0) {
            this.onQuickCraft(itemStack2, n);
        }
    }

    protected void onQuickCraft(ItemStack itemStack, int n) {
    }

    protected void onSwapCraft(int n) {
    }

    protected void checkTakeAchievements(ItemStack itemStack) {
    }

    public ItemStack onTake(Player player, ItemStack itemStack) {
        this.setChanged();
        return itemStack;
    }

    public boolean mayPlace(ItemStack itemStack) {
        return true;
    }

    public ItemStack getItem() {
        return this.container.getItem(this.slot);
    }

    public boolean hasItem() {
        return !this.getItem().isEmpty();
    }

    public void set(ItemStack itemStack) {
        this.container.setItem(this.slot, itemStack);
        this.setChanged();
    }

    public void setChanged() {
        this.container.setChanged();
    }

    public int getMaxStackSize() {
        return this.container.getMaxStackSize();
    }

    public int getMaxStackSize(ItemStack itemStack) {
        return this.getMaxStackSize();
    }

    @Nullable
    public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
        return null;
    }

    public ItemStack remove(int n) {
        return this.container.removeItem(this.slot, n);
    }

    public boolean mayPickup(Player player) {
        return true;
    }

    public boolean isActive() {
        return true;
    }
}

