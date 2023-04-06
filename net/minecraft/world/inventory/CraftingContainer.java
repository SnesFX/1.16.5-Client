/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;

public class CraftingContainer
implements Container,
StackedContentsCompatible {
    private final NonNullList<ItemStack> items;
    private final int width;
    private final int height;
    private final AbstractContainerMenu menu;

    public CraftingContainer(AbstractContainerMenu abstractContainerMenu, int n, int n2) {
        this.items = NonNullList.withSize(n * n2, ItemStack.EMPTY);
        this.menu = abstractContainerMenu;
        this.width = n;
        this.height = n2;
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : this.items) {
            if (itemStack.isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int n) {
        if (n >= this.getContainerSize()) {
            return ItemStack.EMPTY;
        }
        return this.items.get(n);
    }

    @Override
    public ItemStack removeItemNoUpdate(int n) {
        return ContainerHelper.takeItem(this.items, n);
    }

    @Override
    public ItemStack removeItem(int n, int n2) {
        ItemStack itemStack = ContainerHelper.removeItem(this.items, n, n2);
        if (!itemStack.isEmpty()) {
            this.menu.slotsChanged(this);
        }
        return itemStack;
    }

    @Override
    public void setItem(int n, ItemStack itemStack) {
        this.items.set(n, itemStack);
        this.menu.slotsChanged(this);
    }

    @Override
    public void setChanged() {
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    @Override
    public void fillStackedContents(StackedContents stackedContents) {
        for (ItemStack itemStack : this.items) {
            stackedContents.accountSimpleStack(itemStack);
        }
    }
}

