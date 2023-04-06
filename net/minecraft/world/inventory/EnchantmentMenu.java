/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.inventory;

import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.EnchantedItemTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class EnchantmentMenu
extends AbstractContainerMenu {
    private final Container enchantSlots = new SimpleContainer(2){

        @Override
        public void setChanged() {
            super.setChanged();
            EnchantmentMenu.this.slotsChanged(this);
        }
    };
    private final ContainerLevelAccess access;
    private final Random random = new Random();
    private final DataSlot enchantmentSeed = DataSlot.standalone();
    public final int[] costs = new int[3];
    public final int[] enchantClue = new int[]{-1, -1, -1};
    public final int[] levelClue = new int[]{-1, -1, -1};

    public EnchantmentMenu(int n, Inventory inventory) {
        this(n, inventory, ContainerLevelAccess.NULL);
    }

    public EnchantmentMenu(int n, Inventory inventory, ContainerLevelAccess containerLevelAccess) {
        super(MenuType.ENCHANTMENT, n);
        int n2;
        this.access = containerLevelAccess;
        this.addSlot(new Slot(this.enchantSlots, 0, 15, 47){

            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return true;
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });
        this.addSlot(new Slot(this.enchantSlots, 1, 35, 47){

            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return itemStack.getItem() == Items.LAPIS_LAZULI;
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
        this.addDataSlot(DataSlot.shared(this.costs, 0));
        this.addDataSlot(DataSlot.shared(this.costs, 1));
        this.addDataSlot(DataSlot.shared(this.costs, 2));
        this.addDataSlot(this.enchantmentSeed).set(inventory.player.getEnchantmentSeed());
        this.addDataSlot(DataSlot.shared(this.enchantClue, 0));
        this.addDataSlot(DataSlot.shared(this.enchantClue, 1));
        this.addDataSlot(DataSlot.shared(this.enchantClue, 2));
        this.addDataSlot(DataSlot.shared(this.levelClue, 0));
        this.addDataSlot(DataSlot.shared(this.levelClue, 1));
        this.addDataSlot(DataSlot.shared(this.levelClue, 2));
    }

    @Override
    public void slotsChanged(Container container) {
        if (container == this.enchantSlots) {
            ItemStack itemStack = container.getItem(0);
            if (itemStack.isEmpty() || !itemStack.isEnchantable()) {
                for (int i = 0; i < 3; ++i) {
                    this.costs[i] = 0;
                    this.enchantClue[i] = -1;
                    this.levelClue[i] = -1;
                }
            } else {
                this.access.execute((level, blockPos) -> {
                    int n;
                    int n2 = 0;
                    for (n = -1; n <= 1; ++n) {
                        for (int i = -1; i <= 1; ++i) {
                            if (n == 0 && i == 0 || !level.isEmptyBlock(blockPos.offset(i, 0, n)) || !level.isEmptyBlock(blockPos.offset(i, 1, n))) continue;
                            if (level.getBlockState(blockPos.offset(i * 2, 0, n * 2)).is(Blocks.BOOKSHELF)) {
                                ++n2;
                            }
                            if (level.getBlockState(blockPos.offset(i * 2, 1, n * 2)).is(Blocks.BOOKSHELF)) {
                                ++n2;
                            }
                            if (i == 0 || n == 0) continue;
                            if (level.getBlockState(blockPos.offset(i * 2, 0, n)).is(Blocks.BOOKSHELF)) {
                                ++n2;
                            }
                            if (level.getBlockState(blockPos.offset(i * 2, 1, n)).is(Blocks.BOOKSHELF)) {
                                ++n2;
                            }
                            if (level.getBlockState(blockPos.offset(i, 0, n * 2)).is(Blocks.BOOKSHELF)) {
                                ++n2;
                            }
                            if (!level.getBlockState(blockPos.offset(i, 1, n * 2)).is(Blocks.BOOKSHELF)) continue;
                            ++n2;
                        }
                    }
                    this.random.setSeed(this.enchantmentSeed.get());
                    for (n = 0; n < 3; ++n) {
                        this.costs[n] = EnchantmentHelper.getEnchantmentCost(this.random, n, n2, itemStack);
                        this.enchantClue[n] = -1;
                        this.levelClue[n] = -1;
                        if (this.costs[n] >= n + 1) continue;
                        this.costs[n] = 0;
                    }
                    for (n = 0; n < 3; ++n) {
                        List<EnchantmentInstance> list;
                        if (this.costs[n] <= 0 || (list = this.getEnchantmentList(itemStack, n, this.costs[n])) == null || list.isEmpty()) continue;
                        EnchantmentInstance enchantmentInstance = list.get(this.random.nextInt(list.size()));
                        this.enchantClue[n] = Registry.ENCHANTMENT.getId(enchantmentInstance.enchantment);
                        this.levelClue[n] = enchantmentInstance.level;
                    }
                    this.broadcastChanges();
                });
            }
        }
    }

    @Override
    public boolean clickMenuButton(Player player, int n) {
        ItemStack itemStack = this.enchantSlots.getItem(0);
        ItemStack itemStack2 = this.enchantSlots.getItem(1);
        int n2 = n + 1;
        if ((itemStack2.isEmpty() || itemStack2.getCount() < n2) && !player.abilities.instabuild) {
            return false;
        }
        if (this.costs[n] > 0 && !itemStack.isEmpty() && (player.experienceLevel >= n2 && player.experienceLevel >= this.costs[n] || player.abilities.instabuild)) {
            this.access.execute((level, blockPos) -> {
                ItemStack itemStack3 = itemStack;
                List<EnchantmentInstance> list = this.getEnchantmentList(itemStack3, n, this.costs[n]);
                if (!list.isEmpty()) {
                    boolean bl;
                    player.onEnchantmentPerformed(itemStack3, n2);
                    boolean bl2 = bl = itemStack3.getItem() == Items.BOOK;
                    if (bl) {
                        itemStack3 = new ItemStack(Items.ENCHANTED_BOOK);
                        CompoundTag compoundTag = itemStack.getTag();
                        if (compoundTag != null) {
                            itemStack3.setTag(compoundTag.copy());
                        }
                        this.enchantSlots.setItem(0, itemStack3);
                    }
                    for (int i = 0; i < list.size(); ++i) {
                        EnchantmentInstance enchantmentInstance = list.get(i);
                        if (bl) {
                            EnchantedBookItem.addEnchantment(itemStack3, enchantmentInstance);
                            continue;
                        }
                        itemStack3.enchant(enchantmentInstance.enchantment, enchantmentInstance.level);
                    }
                    if (!player.abilities.instabuild) {
                        itemStack2.shrink(n2);
                        if (itemStack2.isEmpty()) {
                            this.enchantSlots.setItem(1, ItemStack.EMPTY);
                        }
                    }
                    player.awardStat(Stats.ENCHANT_ITEM);
                    if (player instanceof ServerPlayer) {
                        CriteriaTriggers.ENCHANTED_ITEM.trigger((ServerPlayer)player, itemStack3, n2);
                    }
                    this.enchantSlots.setChanged();
                    this.enchantmentSeed.set(player.getEnchantmentSeed());
                    this.slotsChanged(this.enchantSlots);
                    level.playSound(null, (BlockPos)blockPos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0f, level.random.nextFloat() * 0.1f + 0.9f);
                }
            });
            return true;
        }
        return false;
    }

    private List<EnchantmentInstance> getEnchantmentList(ItemStack itemStack, int n, int n2) {
        this.random.setSeed(this.enchantmentSeed.get() + n);
        List<EnchantmentInstance> list = EnchantmentHelper.selectEnchantment(this.random, itemStack, n2, false);
        if (itemStack.getItem() == Items.BOOK && list.size() > 1) {
            list.remove(this.random.nextInt(list.size()));
        }
        return list;
    }

    public int getGoldCount() {
        ItemStack itemStack = this.enchantSlots.getItem(1);
        if (itemStack.isEmpty()) {
            return 0;
        }
        return itemStack.getCount();
    }

    public int getEnchantmentSeed() {
        return this.enchantmentSeed.get();
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.access.execute((level, blockPos) -> this.clearContainer(player, player.level, this.enchantSlots));
    }

    @Override
    public boolean stillValid(Player player) {
        return EnchantmentMenu.stillValid(this.access, player, Blocks.ENCHANTING_TABLE);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int n) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(n);
        if (slot != null && slot.hasItem()) {
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.copy();
            if (n == 0) {
                if (!this.moveItemStackTo(itemStack2, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (n == 1) {
                if (!this.moveItemStackTo(itemStack2, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (itemStack2.getItem() == Items.LAPIS_LAZULI) {
                if (!this.moveItemStackTo(itemStack2, 1, 2, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!((Slot)this.slots.get(0)).hasItem() && ((Slot)this.slots.get(0)).mayPlace(itemStack2)) {
                ItemStack itemStack3 = itemStack2.copy();
                itemStack3.setCount(1);
                itemStack2.shrink(1);
                ((Slot)this.slots.get(0)).set(itemStack3);
            } else {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, itemStack2);
        }
        return itemStack;
    }

}

