/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.inventory;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;

public class PlayerEnderChestContainer
extends SimpleContainer {
    private EnderChestBlockEntity activeChest;

    public PlayerEnderChestContainer() {
        super(27);
    }

    public void setActiveChest(EnderChestBlockEntity enderChestBlockEntity) {
        this.activeChest = enderChestBlockEntity;
    }

    @Override
    public void fromTag(ListTag listTag) {
        int n;
        for (n = 0; n < this.getContainerSize(); ++n) {
            this.setItem(n, ItemStack.EMPTY);
        }
        for (n = 0; n < listTag.size(); ++n) {
            CompoundTag compoundTag = listTag.getCompound(n);
            int n2 = compoundTag.getByte("Slot") & 0xFF;
            if (n2 < 0 || n2 >= this.getContainerSize()) continue;
            this.setItem(n2, ItemStack.of(compoundTag));
        }
    }

    @Override
    public ListTag createTag() {
        ListTag listTag = new ListTag();
        for (int i = 0; i < this.getContainerSize(); ++i) {
            ItemStack itemStack = this.getItem(i);
            if (itemStack.isEmpty()) continue;
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putByte("Slot", (byte)i);
            itemStack.save(compoundTag);
            listTag.add(compoundTag);
        }
        return listTag;
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.activeChest != null && !this.activeChest.stillValid(player)) {
            return false;
        }
        return super.stillValid(player);
    }

    @Override
    public void startOpen(Player player) {
        if (this.activeChest != null) {
            this.activeChest.startOpen();
        }
        super.startOpen(player);
    }

    @Override
    public void stopOpen(Player player) {
        if (this.activeChest != null) {
            this.activeChest.stopOpen();
        }
        super.stopOpen(player);
        this.activeChest = null;
    }
}

