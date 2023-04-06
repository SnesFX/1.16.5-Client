/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 */
package net.minecraft.world.inventory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractContainerMenu {
    private final NonNullList<ItemStack> lastSlots = NonNullList.create();
    public final List<Slot> slots = Lists.newArrayList();
    private final List<DataSlot> dataSlots = Lists.newArrayList();
    @Nullable
    private final MenuType<?> menuType;
    public final int containerId;
    private short changeUid;
    private int quickcraftType = -1;
    private int quickcraftStatus;
    private final Set<Slot> quickcraftSlots = Sets.newHashSet();
    private final List<ContainerListener> containerListeners = Lists.newArrayList();
    private final Set<Player> unSynchedPlayers = Sets.newHashSet();

    protected AbstractContainerMenu(@Nullable MenuType<?> menuType, int n) {
        this.menuType = menuType;
        this.containerId = n;
    }

    protected static boolean stillValid(ContainerLevelAccess containerLevelAccess, Player player, Block block) {
        return containerLevelAccess.evaluate((level, blockPos) -> {
            if (!level.getBlockState((BlockPos)blockPos).is(block)) {
                return false;
            }
            return player.distanceToSqr((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5) <= 64.0;
        }, true);
    }

    public MenuType<?> getType() {
        if (this.menuType == null) {
            throw new UnsupportedOperationException("Unable to construct this menu by type");
        }
        return this.menuType;
    }

    protected static void checkContainerSize(Container container, int n) {
        int n2 = container.getContainerSize();
        if (n2 < n) {
            throw new IllegalArgumentException("Container size " + n2 + " is smaller than expected " + n);
        }
    }

    protected static void checkContainerDataCount(ContainerData containerData, int n) {
        int n2 = containerData.getCount();
        if (n2 < n) {
            throw new IllegalArgumentException("Container data count " + n2 + " is smaller than expected " + n);
        }
    }

    protected Slot addSlot(Slot slot) {
        slot.index = this.slots.size();
        this.slots.add(slot);
        this.lastSlots.add(ItemStack.EMPTY);
        return slot;
    }

    protected DataSlot addDataSlot(DataSlot dataSlot) {
        this.dataSlots.add(dataSlot);
        return dataSlot;
    }

    protected void addDataSlots(ContainerData containerData) {
        for (int i = 0; i < containerData.getCount(); ++i) {
            this.addDataSlot(DataSlot.forContainer(containerData, i));
        }
    }

    public void addSlotListener(ContainerListener containerListener) {
        if (this.containerListeners.contains(containerListener)) {
            return;
        }
        this.containerListeners.add(containerListener);
        containerListener.refreshContainer(this, this.getItems());
        this.broadcastChanges();
    }

    public void removeSlotListener(ContainerListener containerListener) {
        this.containerListeners.remove(containerListener);
    }

    public NonNullList<ItemStack> getItems() {
        NonNullList<ItemStack> nonNullList = NonNullList.create();
        for (int i = 0; i < this.slots.size(); ++i) {
            nonNullList.add(this.slots.get(i).getItem());
        }
        return nonNullList;
    }

    public void broadcastChanges() {
        Object object;
        int n;
        for (n = 0; n < this.slots.size(); ++n) {
            object = this.slots.get(n).getItem();
            ItemStack itemStack = this.lastSlots.get(n);
            if (ItemStack.matches(itemStack, (ItemStack)object)) continue;
            ItemStack object2 = ((ItemStack)object).copy();
            this.lastSlots.set(n, object2);
            for (ContainerListener containerListener : this.containerListeners) {
                containerListener.slotChanged(this, n, object2);
            }
        }
        for (n = 0; n < this.dataSlots.size(); ++n) {
            object = this.dataSlots.get(n);
            if (!((DataSlot)object).checkAndClearUpdateFlag()) continue;
            for (ContainerListener containerListener : this.containerListeners) {
                containerListener.setContainerData(this, n, ((DataSlot)object).get());
            }
        }
    }

    public boolean clickMenuButton(Player player, int n) {
        return false;
    }

    public Slot getSlot(int n) {
        return this.slots.get(n);
    }

    public ItemStack quickMoveStack(Player player, int n) {
        Slot slot = this.slots.get(n);
        if (slot != null) {
            return slot.getItem();
        }
        return ItemStack.EMPTY;
    }

    public ItemStack clicked(int n, int n2, ClickType clickType, Player player) {
        try {
            return this.doClick(n, n2, clickType, player);
        }
        catch (Exception exception) {
            CrashReport crashReport = CrashReport.forThrowable(exception, "Container click");
            CrashReportCategory crashReportCategory = crashReport.addCategory("Click info");
            crashReportCategory.setDetail("Menu Type", () -> this.menuType != null ? Registry.MENU.getKey(this.menuType).toString() : "<no type>");
            crashReportCategory.setDetail("Menu Class", () -> this.getClass().getCanonicalName());
            crashReportCategory.setDetail("Slot Count", this.slots.size());
            crashReportCategory.setDetail("Slot", n);
            crashReportCategory.setDetail("Button", n2);
            crashReportCategory.setDetail("Type", (Object)clickType);
            throw new ReportedException(crashReport);
        }
    }

    private ItemStack doClick(int n, int n2, ClickType clickType, Player player) {
        ItemStack itemStack = ItemStack.EMPTY;
        Inventory inventory = player.inventory;
        if (clickType == ClickType.QUICK_CRAFT) {
            int n3 = this.quickcraftStatus;
            this.quickcraftStatus = AbstractContainerMenu.getQuickcraftHeader(n2);
            if ((n3 != 1 || this.quickcraftStatus != 2) && n3 != this.quickcraftStatus) {
                this.resetQuickCraft();
            } else if (inventory.getCarried().isEmpty()) {
                this.resetQuickCraft();
            } else if (this.quickcraftStatus == 0) {
                this.quickcraftType = AbstractContainerMenu.getQuickcraftType(n2);
                if (AbstractContainerMenu.isValidQuickcraftType(this.quickcraftType, player)) {
                    this.quickcraftStatus = 1;
                    this.quickcraftSlots.clear();
                } else {
                    this.resetQuickCraft();
                }
            } else if (this.quickcraftStatus == 1) {
                Slot slot = this.slots.get(n);
                ItemStack itemStack2 = inventory.getCarried();
                if (slot != null && AbstractContainerMenu.canItemQuickReplace(slot, itemStack2, true) && slot.mayPlace(itemStack2) && (this.quickcraftType == 2 || itemStack2.getCount() > this.quickcraftSlots.size()) && this.canDragTo(slot)) {
                    this.quickcraftSlots.add(slot);
                }
            } else if (this.quickcraftStatus == 2) {
                if (!this.quickcraftSlots.isEmpty()) {
                    ItemStack itemStack3 = inventory.getCarried().copy();
                    int n4 = inventory.getCarried().getCount();
                    for (Slot slot : this.quickcraftSlots) {
                        ItemStack itemStack4 = inventory.getCarried();
                        if (slot == null || !AbstractContainerMenu.canItemQuickReplace(slot, itemStack4, true) || !slot.mayPlace(itemStack4) || this.quickcraftType != 2 && itemStack4.getCount() < this.quickcraftSlots.size() || !this.canDragTo(slot)) continue;
                        ItemStack itemStack5 = itemStack3.copy();
                        int n5 = slot.hasItem() ? slot.getItem().getCount() : 0;
                        AbstractContainerMenu.getQuickCraftSlotCount(this.quickcraftSlots, this.quickcraftType, itemStack5, n5);
                        int n6 = Math.min(itemStack5.getMaxStackSize(), slot.getMaxStackSize(itemStack5));
                        if (itemStack5.getCount() > n6) {
                            itemStack5.setCount(n6);
                        }
                        n4 -= itemStack5.getCount() - n5;
                        slot.set(itemStack5);
                    }
                    itemStack3.setCount(n4);
                    inventory.setCarried(itemStack3);
                }
                this.resetQuickCraft();
            } else {
                this.resetQuickCraft();
            }
        } else if (this.quickcraftStatus != 0) {
            this.resetQuickCraft();
        } else if (!(clickType != ClickType.PICKUP && clickType != ClickType.QUICK_MOVE || n2 != 0 && n2 != 1)) {
            if (n == -999) {
                if (!inventory.getCarried().isEmpty()) {
                    if (n2 == 0) {
                        player.drop(inventory.getCarried(), true);
                        inventory.setCarried(ItemStack.EMPTY);
                    }
                    if (n2 == 1) {
                        player.drop(inventory.getCarried().split(1), true);
                    }
                }
            } else if (clickType == ClickType.QUICK_MOVE) {
                if (n < 0) {
                    return ItemStack.EMPTY;
                }
                Slot slot = this.slots.get(n);
                if (slot == null || !slot.mayPickup(player)) {
                    return ItemStack.EMPTY;
                }
                ItemStack itemStack6 = this.quickMoveStack(player, n);
                while (!itemStack6.isEmpty() && ItemStack.isSame(slot.getItem(), itemStack6)) {
                    itemStack = itemStack6.copy();
                    itemStack6 = this.quickMoveStack(player, n);
                }
            } else {
                if (n < 0) {
                    return ItemStack.EMPTY;
                }
                Slot slot = this.slots.get(n);
                if (slot != null) {
                    ItemStack itemStack7 = slot.getItem();
                    ItemStack itemStack8 = inventory.getCarried();
                    if (!itemStack7.isEmpty()) {
                        itemStack = itemStack7.copy();
                    }
                    if (itemStack7.isEmpty()) {
                        if (!itemStack8.isEmpty() && slot.mayPlace(itemStack8)) {
                            int n7;
                            int n8 = n7 = n2 == 0 ? itemStack8.getCount() : 1;
                            if (n7 > slot.getMaxStackSize(itemStack8)) {
                                n7 = slot.getMaxStackSize(itemStack8);
                            }
                            slot.set(itemStack8.split(n7));
                        }
                    } else if (slot.mayPickup(player)) {
                        int n9;
                        if (itemStack8.isEmpty()) {
                            if (itemStack7.isEmpty()) {
                                slot.set(ItemStack.EMPTY);
                                inventory.setCarried(ItemStack.EMPTY);
                            } else {
                                int n10 = n2 == 0 ? itemStack7.getCount() : (itemStack7.getCount() + 1) / 2;
                                inventory.setCarried(slot.remove(n10));
                                if (itemStack7.isEmpty()) {
                                    slot.set(ItemStack.EMPTY);
                                }
                                slot.onTake(player, inventory.getCarried());
                            }
                        } else if (slot.mayPlace(itemStack8)) {
                            if (AbstractContainerMenu.consideredTheSameItem(itemStack7, itemStack8)) {
                                int n11;
                                int n12 = n11 = n2 == 0 ? itemStack8.getCount() : 1;
                                if (n11 > slot.getMaxStackSize(itemStack8) - itemStack7.getCount()) {
                                    n11 = slot.getMaxStackSize(itemStack8) - itemStack7.getCount();
                                }
                                if (n11 > itemStack8.getMaxStackSize() - itemStack7.getCount()) {
                                    n11 = itemStack8.getMaxStackSize() - itemStack7.getCount();
                                }
                                itemStack8.shrink(n11);
                                itemStack7.grow(n11);
                            } else if (itemStack8.getCount() <= slot.getMaxStackSize(itemStack8)) {
                                slot.set(itemStack8);
                                inventory.setCarried(itemStack7);
                            }
                        } else if (itemStack8.getMaxStackSize() > 1 && AbstractContainerMenu.consideredTheSameItem(itemStack7, itemStack8) && !itemStack7.isEmpty() && (n9 = itemStack7.getCount()) + itemStack8.getCount() <= itemStack8.getMaxStackSize()) {
                            itemStack8.grow(n9);
                            itemStack7 = slot.remove(n9);
                            if (itemStack7.isEmpty()) {
                                slot.set(ItemStack.EMPTY);
                            }
                            slot.onTake(player, inventory.getCarried());
                        }
                    }
                    slot.setChanged();
                }
            }
        } else if (clickType == ClickType.SWAP) {
            Slot slot = this.slots.get(n);
            ItemStack itemStack9 = inventory.getItem(n2);
            ItemStack itemStack10 = slot.getItem();
            if (!itemStack9.isEmpty() || !itemStack10.isEmpty()) {
                if (itemStack9.isEmpty()) {
                    if (slot.mayPickup(player)) {
                        inventory.setItem(n2, itemStack10);
                        slot.onSwapCraft(itemStack10.getCount());
                        slot.set(ItemStack.EMPTY);
                        slot.onTake(player, itemStack10);
                    }
                } else if (itemStack10.isEmpty()) {
                    if (slot.mayPlace(itemStack9)) {
                        int n13 = slot.getMaxStackSize(itemStack9);
                        if (itemStack9.getCount() > n13) {
                            slot.set(itemStack9.split(n13));
                        } else {
                            slot.set(itemStack9);
                            inventory.setItem(n2, ItemStack.EMPTY);
                        }
                    }
                } else if (slot.mayPickup(player) && slot.mayPlace(itemStack9)) {
                    int n14 = slot.getMaxStackSize(itemStack9);
                    if (itemStack9.getCount() > n14) {
                        slot.set(itemStack9.split(n14));
                        slot.onTake(player, itemStack10);
                        if (!inventory.add(itemStack10)) {
                            player.drop(itemStack10, true);
                        }
                    } else {
                        slot.set(itemStack9);
                        inventory.setItem(n2, itemStack10);
                        slot.onTake(player, itemStack10);
                    }
                }
            }
        } else if (clickType == ClickType.CLONE && player.abilities.instabuild && inventory.getCarried().isEmpty() && n >= 0) {
            Slot slot = this.slots.get(n);
            if (slot != null && slot.hasItem()) {
                ItemStack itemStack11 = slot.getItem().copy();
                itemStack11.setCount(itemStack11.getMaxStackSize());
                inventory.setCarried(itemStack11);
            }
        } else if (clickType == ClickType.THROW && inventory.getCarried().isEmpty() && n >= 0) {
            Slot slot = this.slots.get(n);
            if (slot != null && slot.hasItem() && slot.mayPickup(player)) {
                ItemStack itemStack12 = slot.remove(n2 == 0 ? 1 : slot.getItem().getCount());
                slot.onTake(player, itemStack12);
                player.drop(itemStack12, true);
            }
        } else if (clickType == ClickType.PICKUP_ALL && n >= 0) {
            Slot slot = this.slots.get(n);
            ItemStack itemStack13 = inventory.getCarried();
            if (!(itemStack13.isEmpty() || slot != null && slot.hasItem() && slot.mayPickup(player))) {
                int n15 = n2 == 0 ? 0 : this.slots.size() - 1;
                int n16 = n2 == 0 ? 1 : -1;
                for (int i = 0; i < 2; ++i) {
                    for (int j = n15; j >= 0 && j < this.slots.size() && itemStack13.getCount() < itemStack13.getMaxStackSize(); j += n16) {
                        Slot slot2 = this.slots.get(j);
                        if (!slot2.hasItem() || !AbstractContainerMenu.canItemQuickReplace(slot2, itemStack13, true) || !slot2.mayPickup(player) || !this.canTakeItemForPickAll(itemStack13, slot2)) continue;
                        ItemStack itemStack14 = slot2.getItem();
                        if (i == 0 && itemStack14.getCount() == itemStack14.getMaxStackSize()) continue;
                        int n17 = Math.min(itemStack13.getMaxStackSize() - itemStack13.getCount(), itemStack14.getCount());
                        ItemStack itemStack15 = slot2.remove(n17);
                        itemStack13.grow(n17);
                        if (itemStack15.isEmpty()) {
                            slot2.set(ItemStack.EMPTY);
                        }
                        slot2.onTake(player, itemStack15);
                    }
                }
            }
            this.broadcastChanges();
        }
        return itemStack;
    }

    public static boolean consideredTheSameItem(ItemStack itemStack, ItemStack itemStack2) {
        return itemStack.getItem() == itemStack2.getItem() && ItemStack.tagMatches(itemStack, itemStack2);
    }

    public boolean canTakeItemForPickAll(ItemStack itemStack, Slot slot) {
        return true;
    }

    public void removed(Player player) {
        Inventory inventory = player.inventory;
        if (!inventory.getCarried().isEmpty()) {
            player.drop(inventory.getCarried(), false);
            inventory.setCarried(ItemStack.EMPTY);
        }
    }

    protected void clearContainer(Player player, Level level, Container container) {
        if (!player.isAlive() || player instanceof ServerPlayer && ((ServerPlayer)player).hasDisconnected()) {
            for (int i = 0; i < container.getContainerSize(); ++i) {
                player.drop(container.removeItemNoUpdate(i), false);
            }
            return;
        }
        for (int i = 0; i < container.getContainerSize(); ++i) {
            player.inventory.placeItemBackInInventory(level, container.removeItemNoUpdate(i));
        }
    }

    public void slotsChanged(Container container) {
        this.broadcastChanges();
    }

    public void setItem(int n, ItemStack itemStack) {
        this.getSlot(n).set(itemStack);
    }

    public void setAll(List<ItemStack> list) {
        for (int i = 0; i < list.size(); ++i) {
            this.getSlot(i).set(list.get(i));
        }
    }

    public void setData(int n, int n2) {
        this.dataSlots.get(n).set(n2);
    }

    public short backup(Inventory inventory) {
        this.changeUid = (short)(this.changeUid + 1);
        return this.changeUid;
    }

    public boolean isSynched(Player player) {
        return !this.unSynchedPlayers.contains(player);
    }

    public void setSynched(Player player, boolean bl) {
        if (bl) {
            this.unSynchedPlayers.remove(player);
        } else {
            this.unSynchedPlayers.add(player);
        }
    }

    public abstract boolean stillValid(Player var1);

    protected boolean moveItemStackTo(ItemStack itemStack, int n, int n2, boolean bl) {
        ItemStack itemStack2;
        Slot slot;
        boolean bl2 = false;
        int n3 = n;
        if (bl) {
            n3 = n2 - 1;
        }
        if (itemStack.isStackable()) {
            while (!itemStack.isEmpty() && (bl ? n3 >= n : n3 < n2)) {
                slot = this.slots.get(n3);
                itemStack2 = slot.getItem();
                if (!itemStack2.isEmpty() && AbstractContainerMenu.consideredTheSameItem(itemStack, itemStack2)) {
                    int n4 = itemStack2.getCount() + itemStack.getCount();
                    if (n4 <= itemStack.getMaxStackSize()) {
                        itemStack.setCount(0);
                        itemStack2.setCount(n4);
                        slot.setChanged();
                        bl2 = true;
                    } else if (itemStack2.getCount() < itemStack.getMaxStackSize()) {
                        itemStack.shrink(itemStack.getMaxStackSize() - itemStack2.getCount());
                        itemStack2.setCount(itemStack.getMaxStackSize());
                        slot.setChanged();
                        bl2 = true;
                    }
                }
                if (bl) {
                    --n3;
                    continue;
                }
                ++n3;
            }
        }
        if (!itemStack.isEmpty()) {
            n3 = bl ? n2 - 1 : n;
            while (bl ? n3 >= n : n3 < n2) {
                slot = this.slots.get(n3);
                itemStack2 = slot.getItem();
                if (itemStack2.isEmpty() && slot.mayPlace(itemStack)) {
                    if (itemStack.getCount() > slot.getMaxStackSize()) {
                        slot.set(itemStack.split(slot.getMaxStackSize()));
                    } else {
                        slot.set(itemStack.split(itemStack.getCount()));
                    }
                    slot.setChanged();
                    bl2 = true;
                    break;
                }
                if (bl) {
                    --n3;
                    continue;
                }
                ++n3;
            }
        }
        return bl2;
    }

    public static int getQuickcraftType(int n) {
        return n >> 2 & 3;
    }

    public static int getQuickcraftHeader(int n) {
        return n & 3;
    }

    public static int getQuickcraftMask(int n, int n2) {
        return n & 3 | (n2 & 3) << 2;
    }

    public static boolean isValidQuickcraftType(int n, Player player) {
        if (n == 0) {
            return true;
        }
        if (n == 1) {
            return true;
        }
        return n == 2 && player.abilities.instabuild;
    }

    protected void resetQuickCraft() {
        this.quickcraftStatus = 0;
        this.quickcraftSlots.clear();
    }

    public static boolean canItemQuickReplace(@Nullable Slot slot, ItemStack itemStack, boolean bl) {
        boolean bl2;
        boolean bl3 = bl2 = slot == null || !slot.hasItem();
        if (!bl2 && itemStack.sameItem(slot.getItem()) && ItemStack.tagMatches(slot.getItem(), itemStack)) {
            return slot.getItem().getCount() + (bl ? 0 : itemStack.getCount()) <= itemStack.getMaxStackSize();
        }
        return bl2;
    }

    public static void getQuickCraftSlotCount(Set<Slot> set, int n, ItemStack itemStack, int n2) {
        switch (n) {
            case 0: {
                itemStack.setCount(Mth.floor((float)itemStack.getCount() / (float)set.size()));
                break;
            }
            case 1: {
                itemStack.setCount(1);
                break;
            }
            case 2: {
                itemStack.setCount(itemStack.getItem().getMaxStackSize());
            }
        }
        itemStack.grow(n2);
    }

    public boolean canDragTo(Slot slot) {
        return true;
    }

    public static int getRedstoneSignalFromBlockEntity(@Nullable BlockEntity blockEntity) {
        if (blockEntity instanceof Container) {
            return AbstractContainerMenu.getRedstoneSignalFromContainer((Container)((Object)blockEntity));
        }
        return 0;
    }

    public static int getRedstoneSignalFromContainer(@Nullable Container container) {
        if (container == null) {
            return 0;
        }
        int n = 0;
        float f = 0.0f;
        for (int i = 0; i < container.getContainerSize(); ++i) {
            ItemStack itemStack = container.getItem(i);
            if (itemStack.isEmpty()) continue;
            f += (float)itemStack.getCount() / (float)Math.min(container.getMaxStackSize(), itemStack.getMaxStackSize());
            ++n;
        }
        return Mth.floor((f /= (float)container.getContainerSize()) * 14.0f) + (n > 0 ? 1 : 0);
    }
}

