/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.inventory;

import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class CartographyTableMenu
extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    private long lastSoundTime;
    public final Container container = new SimpleContainer(2){

        @Override
        public void setChanged() {
            CartographyTableMenu.this.slotsChanged(this);
            super.setChanged();
        }
    };
    private final ResultContainer resultContainer = new ResultContainer(){

        @Override
        public void setChanged() {
            CartographyTableMenu.this.slotsChanged(this);
            super.setChanged();
        }
    };

    public CartographyTableMenu(int n, Inventory inventory) {
        this(n, inventory, ContainerLevelAccess.NULL);
    }

    public CartographyTableMenu(int n, Inventory inventory, final ContainerLevelAccess containerLevelAccess) {
        super(MenuType.CARTOGRAPHY_TABLE, n);
        int n2;
        this.access = containerLevelAccess;
        this.addSlot(new Slot(this.container, 0, 15, 15){

            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return itemStack.getItem() == Items.FILLED_MAP;
            }
        });
        this.addSlot(new Slot(this.container, 1, 15, 52){

            @Override
            public boolean mayPlace(ItemStack itemStack) {
                Item item = itemStack.getItem();
                return item == Items.PAPER || item == Items.MAP || item == Items.GLASS_PANE;
            }
        });
        this.addSlot(new Slot(this.resultContainer, 2, 145, 39){

            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return false;
            }

            @Override
            public ItemStack onTake(Player player, ItemStack itemStack) {
                ((Slot)CartographyTableMenu.this.slots.get(0)).remove(1);
                ((Slot)CartographyTableMenu.this.slots.get(1)).remove(1);
                itemStack.getItem().onCraftedBy(itemStack, player.level, player);
                containerLevelAccess.execute((level, blockPos) -> {
                    long l = level.getGameTime();
                    if (CartographyTableMenu.this.lastSoundTime != l) {
                        level.playSound(null, (BlockPos)blockPos, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundSource.BLOCKS, 1.0f, 1.0f);
                        CartographyTableMenu.this.lastSoundTime = l;
                    }
                });
                return super.onTake(player, itemStack);
            }
        });
        for (n2 = 0; n2 < 3; ++n2) {
            for (int i = 0; i < 9; ++i) {
                this.addSlot(new Slot(inventory, i + n2 * 9 + 9, 8 + i * 18, 84 + n2 * 18));
            }
        }
        for (n2 = 0; n2 < 9; ++n2) {
            this.addSlot(new Slot(inventory, n2, 8 + n2 * 18, 142));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return CartographyTableMenu.stillValid(this.access, player, Blocks.CARTOGRAPHY_TABLE);
    }

    @Override
    public void slotsChanged(Container container) {
        ItemStack itemStack = this.container.getItem(0);
        ItemStack itemStack2 = this.container.getItem(1);
        ItemStack itemStack3 = this.resultContainer.getItem(2);
        if (!itemStack3.isEmpty() && (itemStack.isEmpty() || itemStack2.isEmpty())) {
            this.resultContainer.removeItemNoUpdate(2);
        } else if (!itemStack.isEmpty() && !itemStack2.isEmpty()) {
            this.setupResultSlot(itemStack, itemStack2, itemStack3);
        }
    }

    private void setupResultSlot(ItemStack itemStack, ItemStack itemStack2, ItemStack itemStack3) {
        this.access.execute((level, blockPos) -> {
            ItemStack itemStack4;
            Item item = itemStack2.getItem();
            MapItemSavedData mapItemSavedData = MapItem.getSavedData(itemStack, level);
            if (mapItemSavedData == null) {
                return;
            }
            if (item == Items.PAPER && !mapItemSavedData.locked && mapItemSavedData.scale < 4) {
                itemStack4 = itemStack.copy();
                itemStack4.setCount(1);
                itemStack4.getOrCreateTag().putInt("map_scale_direction", 1);
                this.broadcastChanges();
            } else if (item == Items.GLASS_PANE && !mapItemSavedData.locked) {
                itemStack4 = itemStack.copy();
                itemStack4.setCount(1);
                itemStack4.getOrCreateTag().putBoolean("map_to_lock", true);
                this.broadcastChanges();
            } else if (item == Items.MAP) {
                itemStack4 = itemStack.copy();
                itemStack4.setCount(2);
                this.broadcastChanges();
            } else {
                this.resultContainer.removeItemNoUpdate(2);
                this.broadcastChanges();
                return;
            }
            if (!ItemStack.matches(itemStack4, itemStack3)) {
                this.resultContainer.setItem(2, itemStack4);
                this.broadcastChanges();
            }
        });
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack itemStack, Slot slot) {
        return slot.container != this.resultContainer && super.canTakeItemForPickAll(itemStack, slot);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int n) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(n);
        if (slot != null && slot.hasItem()) {
            ItemStack itemStack2;
            ItemStack itemStack3 = itemStack2 = slot.getItem();
            Item item = itemStack3.getItem();
            itemStack = itemStack3.copy();
            if (n == 2) {
                item.onCraftedBy(itemStack3, player.level, player);
                if (!this.moveItemStackTo(itemStack3, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemStack3, itemStack);
            } else if (n == 1 || n == 0 ? !this.moveItemStackTo(itemStack3, 3, 39, false) : (item == Items.FILLED_MAP ? !this.moveItemStackTo(itemStack3, 0, 1, false) : (item == Items.PAPER || item == Items.MAP || item == Items.GLASS_PANE ? !this.moveItemStackTo(itemStack3, 1, 2, false) : (n >= 3 && n < 30 ? !this.moveItemStackTo(itemStack3, 30, 39, false) : n >= 30 && n < 39 && !this.moveItemStackTo(itemStack3, 3, 30, false))))) {
                return ItemStack.EMPTY;
            }
            if (itemStack3.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            }
            slot.setChanged();
            if (itemStack3.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, itemStack3);
            this.broadcastChanges();
        }
        return itemStack;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.resultContainer.removeItemNoUpdate(2);
        this.access.execute((level, blockPos) -> this.clearContainer(player, player.level, this.container));
    }

}

