/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.world;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class SimpleContainer
implements Container,
StackedContentsCompatible {
    private final int size;
    private final NonNullList<ItemStack> items;
    private List<ContainerListener> listeners;

    public SimpleContainer(int n) {
        this.size = n;
        this.items = NonNullList.withSize(n, ItemStack.EMPTY);
    }

    public SimpleContainer(ItemStack ... arritemStack) {
        this.size = arritemStack.length;
        this.items = NonNullList.of(ItemStack.EMPTY, arritemStack);
    }

    public void addListener(ContainerListener containerListener) {
        if (this.listeners == null) {
            this.listeners = Lists.newArrayList();
        }
        this.listeners.add(containerListener);
    }

    public void removeListener(ContainerListener containerListener) {
        this.listeners.remove(containerListener);
    }

    @Override
    public ItemStack getItem(int n) {
        if (n < 0 || n >= this.items.size()) {
            return ItemStack.EMPTY;
        }
        return this.items.get(n);
    }

    public List<ItemStack> removeAllItems() {
        List<ItemStack> list = this.items.stream().filter(itemStack -> !itemStack.isEmpty()).collect(Collectors.toList());
        this.clearContent();
        return list;
    }

    @Override
    public ItemStack removeItem(int n, int n2) {
        ItemStack itemStack = ContainerHelper.removeItem(this.items, n, n2);
        if (!itemStack.isEmpty()) {
            this.setChanged();
        }
        return itemStack;
    }

    public ItemStack removeItemType(Item item, int n) {
        ItemStack itemStack = new ItemStack(item, 0);
        for (int i = this.size - 1; i >= 0; --i) {
            ItemStack itemStack2 = this.getItem(i);
            if (!itemStack2.getItem().equals(item)) continue;
            int n2 = n - itemStack.getCount();
            ItemStack itemStack3 = itemStack2.split(n2);
            itemStack.grow(itemStack3.getCount());
            if (itemStack.getCount() == n) break;
        }
        if (!itemStack.isEmpty()) {
            this.setChanged();
        }
        return itemStack;
    }

    public ItemStack addItem(ItemStack itemStack) {
        ItemStack itemStack2 = itemStack.copy();
        this.moveItemToOccupiedSlotsWithSameType(itemStack2);
        if (itemStack2.isEmpty()) {
            return ItemStack.EMPTY;
        }
        this.moveItemToEmptySlots(itemStack2);
        if (itemStack2.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return itemStack2;
    }

    public boolean canAddItem(ItemStack itemStack) {
        boolean bl = false;
        for (ItemStack itemStack2 : this.items) {
            if (!itemStack2.isEmpty() && (!this.isSameItem(itemStack2, itemStack) || itemStack2.getCount() >= itemStack2.getMaxStackSize())) continue;
            bl = true;
            break;
        }
        return bl;
    }

    @Override
    public ItemStack removeItemNoUpdate(int n) {
        ItemStack itemStack = this.items.get(n);
        if (itemStack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        this.items.set(n, ItemStack.EMPTY);
        return itemStack;
    }

    @Override
    public void setItem(int n, ItemStack itemStack) {
        this.items.set(n, itemStack);
        if (!itemStack.isEmpty() && itemStack.getCount() > this.getMaxStackSize()) {
            itemStack.setCount(this.getMaxStackSize());
        }
        this.setChanged();
    }

    @Override
    public int getContainerSize() {
        return this.size;
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
    public void setChanged() {
        if (this.listeners != null) {
            for (ContainerListener containerListener : this.listeners) {
                containerListener.containerChanged(this);
            }
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        this.items.clear();
        this.setChanged();
    }

    @Override
    public void fillStackedContents(StackedContents stackedContents) {
        for (ItemStack itemStack : this.items) {
            stackedContents.accountStack(itemStack);
        }
    }

    public String toString() {
        return this.items.stream().filter(itemStack -> !itemStack.isEmpty()).collect(Collectors.toList()).toString();
    }

    private void moveItemToEmptySlots(ItemStack itemStack) {
        for (int i = 0; i < this.size; ++i) {
            ItemStack itemStack2 = this.getItem(i);
            if (!itemStack2.isEmpty()) continue;
            this.setItem(i, itemStack.copy());
            itemStack.setCount(0);
            return;
        }
    }

    private void moveItemToOccupiedSlotsWithSameType(ItemStack itemStack) {
        for (int i = 0; i < this.size; ++i) {
            ItemStack itemStack2 = this.getItem(i);
            if (!this.isSameItem(itemStack2, itemStack)) continue;
            this.moveItemsBetweenStacks(itemStack, itemStack2);
            if (!itemStack.isEmpty()) continue;
            return;
        }
    }

    private boolean isSameItem(ItemStack itemStack, ItemStack itemStack2) {
        return itemStack.getItem() == itemStack2.getItem() && ItemStack.tagMatches(itemStack, itemStack2);
    }

    private void moveItemsBetweenStacks(ItemStack itemStack, ItemStack itemStack2) {
        int n = Math.min(this.getMaxStackSize(), itemStack2.getMaxStackSize());
        int n2 = Math.min(itemStack.getCount(), n - itemStack2.getCount());
        if (n2 > 0) {
            itemStack2.grow(n2);
            itemStack.shrink(n2);
            this.setChanged();
        }
    }

    public void fromTag(ListTag listTag) {
        for (int i = 0; i < listTag.size(); ++i) {
            ItemStack itemStack = ItemStack.of(listTag.getCompound(i));
            if (itemStack.isEmpty()) continue;
            this.addItem(itemStack);
        }
    }

    public ListTag createTag() {
        ListTag listTag = new ListTag();
        for (int i = 0; i < this.getContainerSize(); ++i) {
            ItemStack itemStack = this.getItem(i);
            if (itemStack.isEmpty()) continue;
            listTag.add(itemStack.save(new CompoundTag()));
        }
        return listTag;
    }
}

