/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package net.minecraft.world.entity.player;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.tags.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Nameable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class Inventory
implements Container,
Nameable {
    public final NonNullList<ItemStack> items = NonNullList.withSize(36, ItemStack.EMPTY);
    public final NonNullList<ItemStack> armor = NonNullList.withSize(4, ItemStack.EMPTY);
    public final NonNullList<ItemStack> offhand = NonNullList.withSize(1, ItemStack.EMPTY);
    private final List<NonNullList<ItemStack>> compartments = ImmutableList.of(this.items, this.armor, this.offhand);
    public int selected;
    public final Player player;
    private ItemStack carried = ItemStack.EMPTY;
    private int timesChanged;

    public Inventory(Player player) {
        this.player = player;
    }

    public ItemStack getSelected() {
        if (Inventory.isHotbarSlot(this.selected)) {
            return this.items.get(this.selected);
        }
        return ItemStack.EMPTY;
    }

    public static int getSelectionSize() {
        return 9;
    }

    private boolean hasRemainingSpaceForItem(ItemStack itemStack, ItemStack itemStack2) {
        return !itemStack.isEmpty() && this.isSameItem(itemStack, itemStack2) && itemStack.isStackable() && itemStack.getCount() < itemStack.getMaxStackSize() && itemStack.getCount() < this.getMaxStackSize();
    }

    private boolean isSameItem(ItemStack itemStack, ItemStack itemStack2) {
        return itemStack.getItem() == itemStack2.getItem() && ItemStack.tagMatches(itemStack, itemStack2);
    }

    public int getFreeSlot() {
        for (int i = 0; i < this.items.size(); ++i) {
            if (!this.items.get(i).isEmpty()) continue;
            return i;
        }
        return -1;
    }

    public void setPickedItem(ItemStack itemStack) {
        int n = this.findSlotMatchingItem(itemStack);
        if (Inventory.isHotbarSlot(n)) {
            this.selected = n;
            return;
        }
        if (n == -1) {
            int n2;
            this.selected = this.getSuitableHotbarSlot();
            if (!this.items.get(this.selected).isEmpty() && (n2 = this.getFreeSlot()) != -1) {
                this.items.set(n2, this.items.get(this.selected));
            }
            this.items.set(this.selected, itemStack);
        } else {
            this.pickSlot(n);
        }
    }

    public void pickSlot(int n) {
        this.selected = this.getSuitableHotbarSlot();
        ItemStack itemStack = this.items.get(this.selected);
        this.items.set(this.selected, this.items.get(n));
        this.items.set(n, itemStack);
    }

    public static boolean isHotbarSlot(int n) {
        return n >= 0 && n < 9;
    }

    public int findSlotMatchingItem(ItemStack itemStack) {
        for (int i = 0; i < this.items.size(); ++i) {
            if (this.items.get(i).isEmpty() || !this.isSameItem(itemStack, this.items.get(i))) continue;
            return i;
        }
        return -1;
    }

    public int findSlotMatchingUnusedItem(ItemStack itemStack) {
        for (int i = 0; i < this.items.size(); ++i) {
            ItemStack itemStack2 = this.items.get(i);
            if (this.items.get(i).isEmpty() || !this.isSameItem(itemStack, this.items.get(i)) || this.items.get(i).isDamaged() || itemStack2.isEnchanted() || itemStack2.hasCustomHoverName()) continue;
            return i;
        }
        return -1;
    }

    public int getSuitableHotbarSlot() {
        int n;
        int n2;
        for (n2 = 0; n2 < 9; ++n2) {
            n = (this.selected + n2) % 9;
            if (!this.items.get(n).isEmpty()) continue;
            return n;
        }
        for (n2 = 0; n2 < 9; ++n2) {
            n = (this.selected + n2) % 9;
            if (this.items.get(n).isEnchanted()) continue;
            return n;
        }
        return this.selected;
    }

    public void swapPaint(double d) {
        if (d > 0.0) {
            d = 1.0;
        }
        if (d < 0.0) {
            d = -1.0;
        }
        this.selected = (int)((double)this.selected - d);
        while (this.selected < 0) {
            this.selected += 9;
        }
        while (this.selected >= 9) {
            this.selected -= 9;
        }
    }

    public int clearOrCountMatchingItems(Predicate<ItemStack> predicate, int n, Container container) {
        int n2 = 0;
        boolean bl = n == 0;
        n2 += ContainerHelper.clearOrCountMatchingItems(this, predicate, n - n2, bl);
        n2 += ContainerHelper.clearOrCountMatchingItems(container, predicate, n - n2, bl);
        n2 += ContainerHelper.clearOrCountMatchingItems(this.carried, predicate, n - n2, bl);
        if (this.carried.isEmpty()) {
            this.carried = ItemStack.EMPTY;
        }
        return n2;
    }

    private int addResource(ItemStack itemStack) {
        int n = this.getSlotWithRemainingSpace(itemStack);
        if (n == -1) {
            n = this.getFreeSlot();
        }
        if (n == -1) {
            return itemStack.getCount();
        }
        return this.addResource(n, itemStack);
    }

    private int addResource(int n, ItemStack itemStack) {
        int n2;
        Item item = itemStack.getItem();
        int n3 = itemStack.getCount();
        ItemStack itemStack2 = this.getItem(n);
        if (itemStack2.isEmpty()) {
            itemStack2 = new ItemStack(item, 0);
            if (itemStack.hasTag()) {
                itemStack2.setTag(itemStack.getTag().copy());
            }
            this.setItem(n, itemStack2);
        }
        if ((n2 = n3) > itemStack2.getMaxStackSize() - itemStack2.getCount()) {
            n2 = itemStack2.getMaxStackSize() - itemStack2.getCount();
        }
        if (n2 > this.getMaxStackSize() - itemStack2.getCount()) {
            n2 = this.getMaxStackSize() - itemStack2.getCount();
        }
        if (n2 == 0) {
            return n3;
        }
        itemStack2.grow(n2);
        itemStack2.setPopTime(5);
        return n3 -= n2;
    }

    public int getSlotWithRemainingSpace(ItemStack itemStack) {
        if (this.hasRemainingSpaceForItem(this.getItem(this.selected), itemStack)) {
            return this.selected;
        }
        if (this.hasRemainingSpaceForItem(this.getItem(40), itemStack)) {
            return 40;
        }
        for (int i = 0; i < this.items.size(); ++i) {
            if (!this.hasRemainingSpaceForItem(this.items.get(i), itemStack)) continue;
            return i;
        }
        return -1;
    }

    public void tick() {
        for (NonNullList<ItemStack> nonNullList : this.compartments) {
            for (int i = 0; i < nonNullList.size(); ++i) {
                if (nonNullList.get(i).isEmpty()) continue;
                nonNullList.get(i).inventoryTick(this.player.level, this.player, i, this.selected == i);
            }
        }
    }

    public boolean add(ItemStack itemStack) {
        return this.add(-1, itemStack);
    }

    public boolean add(int n, ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return false;
        }
        try {
            if (!itemStack.isDamaged()) {
                int n2;
                do {
                    n2 = itemStack.getCount();
                    if (n == -1) {
                        itemStack.setCount(this.addResource(itemStack));
                        continue;
                    }
                    itemStack.setCount(this.addResource(n, itemStack));
                } while (!itemStack.isEmpty() && itemStack.getCount() < n2);
                if (itemStack.getCount() == n2 && this.player.abilities.instabuild) {
                    itemStack.setCount(0);
                    return true;
                }
                return itemStack.getCount() < n2;
            }
            if (n == -1) {
                n = this.getFreeSlot();
            }
            if (n >= 0) {
                this.items.set(n, itemStack.copy());
                this.items.get(n).setPopTime(5);
                itemStack.setCount(0);
                return true;
            }
            if (this.player.abilities.instabuild) {
                itemStack.setCount(0);
                return true;
            }
            return false;
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.forThrowable(throwable, "Adding item to inventory");
            CrashReportCategory crashReportCategory = crashReport.addCategory("Item being added");
            crashReportCategory.setDetail("Item ID", Item.getId(itemStack.getItem()));
            crashReportCategory.setDetail("Item data", itemStack.getDamageValue());
            crashReportCategory.setDetail("Item name", () -> itemStack.getHoverName().getString());
            throw new ReportedException(crashReport);
        }
    }

    public void placeItemBackInInventory(Level level, ItemStack itemStack) {
        if (level.isClientSide) {
            return;
        }
        while (!itemStack.isEmpty()) {
            int n = this.getSlotWithRemainingSpace(itemStack);
            if (n == -1) {
                n = this.getFreeSlot();
            }
            if (n == -1) {
                this.player.drop(itemStack, false);
                break;
            }
            int n2 = itemStack.getMaxStackSize() - this.getItem(n).getCount();
            if (!this.add(n, itemStack.split(n2))) continue;
            ((ServerPlayer)this.player).connection.send(new ClientboundContainerSetSlotPacket(-2, n, this.getItem(n)));
        }
    }

    @Override
    public ItemStack removeItem(int n, int n2) {
        NonNullList<ItemStack> nonNullList = null;
        for (NonNullList<ItemStack> nonNullList2 : this.compartments) {
            if (n < nonNullList2.size()) {
                nonNullList = nonNullList2;
                break;
            }
            n -= nonNullList2.size();
        }
        if (nonNullList != null && !((ItemStack)nonNullList.get(n)).isEmpty()) {
            return ContainerHelper.removeItem(nonNullList, n, n2);
        }
        return ItemStack.EMPTY;
    }

    public void removeItem(ItemStack itemStack) {
        block0 : for (NonNullList<ItemStack> nonNullList : this.compartments) {
            for (int i = 0; i < nonNullList.size(); ++i) {
                if (nonNullList.get(i) != itemStack) continue;
                nonNullList.set(i, ItemStack.EMPTY);
                continue block0;
            }
        }
    }

    @Override
    public ItemStack removeItemNoUpdate(int n) {
        NonNullList<ItemStack> nonNullList = null;
        for (NonNullList<ItemStack> nonNullList2 : this.compartments) {
            if (n < nonNullList2.size()) {
                nonNullList = nonNullList2;
                break;
            }
            n -= nonNullList2.size();
        }
        if (nonNullList != null && !((ItemStack)nonNullList.get(n)).isEmpty()) {
            ItemStack itemStack = nonNullList.get(n);
            nonNullList.set(n, ItemStack.EMPTY);
            return itemStack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int n, ItemStack itemStack) {
        NonNullList<ItemStack> nonNullList = null;
        for (NonNullList<ItemStack> nonNullList2 : this.compartments) {
            if (n < nonNullList2.size()) {
                nonNullList = nonNullList2;
                break;
            }
            n -= nonNullList2.size();
        }
        if (nonNullList != null) {
            nonNullList.set(n, itemStack);
        }
    }

    public float getDestroySpeed(BlockState blockState) {
        return this.items.get(this.selected).getDestroySpeed(blockState);
    }

    public ListTag save(ListTag listTag) {
        int n;
        CompoundTag compoundTag;
        for (n = 0; n < this.items.size(); ++n) {
            if (this.items.get(n).isEmpty()) continue;
            compoundTag = new CompoundTag();
            compoundTag.putByte("Slot", (byte)n);
            this.items.get(n).save(compoundTag);
            listTag.add(compoundTag);
        }
        for (n = 0; n < this.armor.size(); ++n) {
            if (this.armor.get(n).isEmpty()) continue;
            compoundTag = new CompoundTag();
            compoundTag.putByte("Slot", (byte)(n + 100));
            this.armor.get(n).save(compoundTag);
            listTag.add(compoundTag);
        }
        for (n = 0; n < this.offhand.size(); ++n) {
            if (this.offhand.get(n).isEmpty()) continue;
            compoundTag = new CompoundTag();
            compoundTag.putByte("Slot", (byte)(n + 150));
            this.offhand.get(n).save(compoundTag);
            listTag.add(compoundTag);
        }
        return listTag;
    }

    public void load(ListTag listTag) {
        this.items.clear();
        this.armor.clear();
        this.offhand.clear();
        for (int i = 0; i < listTag.size(); ++i) {
            CompoundTag compoundTag = listTag.getCompound(i);
            int n = compoundTag.getByte("Slot") & 0xFF;
            ItemStack itemStack = ItemStack.of(compoundTag);
            if (itemStack.isEmpty()) continue;
            if (n >= 0 && n < this.items.size()) {
                this.items.set(n, itemStack);
                continue;
            }
            if (n >= 100 && n < this.armor.size() + 100) {
                this.armor.set(n - 100, itemStack);
                continue;
            }
            if (n < 150 || n >= this.offhand.size() + 150) continue;
            this.offhand.set(n - 150, itemStack);
        }
    }

    @Override
    public int getContainerSize() {
        return this.items.size() + this.armor.size() + this.offhand.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : this.items) {
            if (itemStack.isEmpty()) continue;
            return false;
        }
        for (ItemStack itemStack : this.armor) {
            if (itemStack.isEmpty()) continue;
            return false;
        }
        for (ItemStack itemStack : this.offhand) {
            if (itemStack.isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int n) {
        NonNullList<ItemStack> nonNullList = null;
        for (NonNullList<ItemStack> nonNullList2 : this.compartments) {
            if (n < nonNullList2.size()) {
                nonNullList = nonNullList2;
                break;
            }
            n -= nonNullList2.size();
        }
        return nonNullList == null ? ItemStack.EMPTY : (ItemStack)nonNullList.get(n);
    }

    @Override
    public Component getName() {
        return new TranslatableComponent("container.inventory");
    }

    public ItemStack getArmor(int n) {
        return this.armor.get(n);
    }

    public void hurtArmor(DamageSource damageSource, float f) {
        if (f <= 0.0f) {
            return;
        }
        if ((f /= 4.0f) < 1.0f) {
            f = 1.0f;
        }
        for (int i = 0; i < this.armor.size(); ++i) {
            ItemStack itemStack = this.armor.get(i);
            if (damageSource.isFire() && itemStack.getItem().isFireResistant() || !(itemStack.getItem() instanceof ArmorItem)) continue;
            int n = i;
            itemStack.hurtAndBreak((int)f, this.player, player -> player.broadcastBreakEvent(EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, n)));
        }
    }

    public void dropAll() {
        for (List list : this.compartments) {
            for (int i = 0; i < list.size(); ++i) {
                ItemStack itemStack = (ItemStack)list.get(i);
                if (itemStack.isEmpty()) continue;
                this.player.drop(itemStack, true, false);
                list.set(i, ItemStack.EMPTY);
            }
        }
    }

    @Override
    public void setChanged() {
        ++this.timesChanged;
    }

    public int getTimesChanged() {
        return this.timesChanged;
    }

    public void setCarried(ItemStack itemStack) {
        this.carried = itemStack;
    }

    public ItemStack getCarried() {
        return this.carried;
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.player.removed) {
            return false;
        }
        return !(player.distanceToSqr(this.player) > 64.0);
    }

    public boolean contains(ItemStack itemStack) {
        for (List list : this.compartments) {
            for (ItemStack itemStack2 : list) {
                if (itemStack2.isEmpty() || !itemStack2.sameItem(itemStack)) continue;
                return true;
            }
        }
        return false;
    }

    public boolean contains(Tag<Item> tag) {
        for (List list : this.compartments) {
            for (ItemStack itemStack : list) {
                if (itemStack.isEmpty() || !tag.contains(itemStack.getItem())) continue;
                return true;
            }
        }
        return false;
    }

    public void replaceWith(Inventory inventory) {
        for (int i = 0; i < this.getContainerSize(); ++i) {
            this.setItem(i, inventory.getItem(i));
        }
        this.selected = inventory.selected;
    }

    @Override
    public void clearContent() {
        for (List list : this.compartments) {
            list.clear();
        }
    }

    public void fillStackedContents(StackedContents stackedContents) {
        for (ItemStack itemStack : this.items) {
            stackedContents.accountSimpleStack(itemStack);
        }
    }
}

