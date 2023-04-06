/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ForwardingList
 */
package net.minecraft.client.player.inventory;

import com.google.common.collect.ForwardingList;
import java.util.Collection;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class Hotbar
extends ForwardingList<ItemStack> {
    private final NonNullList<ItemStack> items = NonNullList.withSize(Inventory.getSelectionSize(), ItemStack.EMPTY);

    protected List<ItemStack> delegate() {
        return this.items;
    }

    public ListTag createTag() {
        ListTag listTag = new ListTag();
        for (ItemStack itemStack : this.delegate()) {
            listTag.add(itemStack.save(new CompoundTag()));
        }
        return listTag;
    }

    public void fromTag(ListTag listTag) {
        Collection collection = this.delegate();
        for (int i = 0; i < collection.size(); ++i) {
            collection.set(i, ItemStack.of(listTag.getCompound(i)));
        }
    }

    public boolean isEmpty() {
        for (ItemStack itemStack : this.delegate()) {
            if (itemStack.isEmpty()) continue;
            return false;
        }
        return true;
    }
}

