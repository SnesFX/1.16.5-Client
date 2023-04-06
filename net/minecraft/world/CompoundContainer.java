/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class CompoundContainer
implements Container {
    private final Container container1;
    private final Container container2;

    public CompoundContainer(Container container, Container container2) {
        if (container == null) {
            container = container2;
        }
        if (container2 == null) {
            container2 = container;
        }
        this.container1 = container;
        this.container2 = container2;
    }

    @Override
    public int getContainerSize() {
        return this.container1.getContainerSize() + this.container2.getContainerSize();
    }

    @Override
    public boolean isEmpty() {
        return this.container1.isEmpty() && this.container2.isEmpty();
    }

    public boolean contains(Container container) {
        return this.container1 == container || this.container2 == container;
    }

    @Override
    public ItemStack getItem(int n) {
        if (n >= this.container1.getContainerSize()) {
            return this.container2.getItem(n - this.container1.getContainerSize());
        }
        return this.container1.getItem(n);
    }

    @Override
    public ItemStack removeItem(int n, int n2) {
        if (n >= this.container1.getContainerSize()) {
            return this.container2.removeItem(n - this.container1.getContainerSize(), n2);
        }
        return this.container1.removeItem(n, n2);
    }

    @Override
    public ItemStack removeItemNoUpdate(int n) {
        if (n >= this.container1.getContainerSize()) {
            return this.container2.removeItemNoUpdate(n - this.container1.getContainerSize());
        }
        return this.container1.removeItemNoUpdate(n);
    }

    @Override
    public void setItem(int n, ItemStack itemStack) {
        if (n >= this.container1.getContainerSize()) {
            this.container2.setItem(n - this.container1.getContainerSize(), itemStack);
        } else {
            this.container1.setItem(n, itemStack);
        }
    }

    @Override
    public int getMaxStackSize() {
        return this.container1.getMaxStackSize();
    }

    @Override
    public void setChanged() {
        this.container1.setChanged();
        this.container2.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container1.stillValid(player) && this.container2.stillValid(player);
    }

    @Override
    public void startOpen(Player player) {
        this.container1.startOpen(player);
        this.container2.startOpen(player);
    }

    @Override
    public void stopOpen(Player player) {
        this.container1.stopOpen(player);
        this.container2.stopOpen(player);
    }

    @Override
    public boolean canPlaceItem(int n, ItemStack itemStack) {
        if (n >= this.container1.getContainerSize()) {
            return this.container2.canPlaceItem(n - this.container1.getContainerSize(), itemStack);
        }
        return this.container1.canPlaceItem(n, itemStack);
    }

    @Override
    public void clearContent() {
        this.container1.clearContent();
        this.container2.clearContent();
    }
}

