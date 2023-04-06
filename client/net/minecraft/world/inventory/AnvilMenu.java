/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.inventory;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnvilMenu
extends ItemCombinerMenu {
    private static final Logger LOGGER = LogManager.getLogger();
    private int repairItemCountCost;
    private String itemName;
    private final DataSlot cost = DataSlot.standalone();

    public AnvilMenu(int n, Inventory inventory) {
        this(n, inventory, ContainerLevelAccess.NULL);
    }

    public AnvilMenu(int n, Inventory inventory, ContainerLevelAccess containerLevelAccess) {
        super(MenuType.ANVIL, n, inventory, containerLevelAccess);
        this.addDataSlot(this.cost);
    }

    @Override
    protected boolean isValidBlock(BlockState blockState) {
        return blockState.is(BlockTags.ANVIL);
    }

    @Override
    protected boolean mayPickup(Player player, boolean bl) {
        return (player.abilities.instabuild || player.experienceLevel >= this.cost.get()) && this.cost.get() > 0;
    }

    @Override
    protected ItemStack onTake(Player player, ItemStack itemStack) {
        if (!player.abilities.instabuild) {
            player.giveExperienceLevels(-this.cost.get());
        }
        this.inputSlots.setItem(0, ItemStack.EMPTY);
        if (this.repairItemCountCost > 0) {
            ItemStack itemStack2 = this.inputSlots.getItem(1);
            if (!itemStack2.isEmpty() && itemStack2.getCount() > this.repairItemCountCost) {
                itemStack2.shrink(this.repairItemCountCost);
                this.inputSlots.setItem(1, itemStack2);
            } else {
                this.inputSlots.setItem(1, ItemStack.EMPTY);
            }
        } else {
            this.inputSlots.setItem(1, ItemStack.EMPTY);
        }
        this.cost.set(0);
        this.access.execute((level, blockPos) -> {
            BlockState blockState = level.getBlockState((BlockPos)blockPos);
            if (!player.abilities.instabuild && blockState.is(BlockTags.ANVIL) && player.getRandom().nextFloat() < 0.12f) {
                BlockState blockState2 = AnvilBlock.damage(blockState);
                if (blockState2 == null) {
                    level.removeBlock((BlockPos)blockPos, false);
                    level.levelEvent(1029, (BlockPos)blockPos, 0);
                } else {
                    level.setBlock((BlockPos)blockPos, blockState2, 2);
                    level.levelEvent(1030, (BlockPos)blockPos, 0);
                }
            } else {
                level.levelEvent(1030, (BlockPos)blockPos, 0);
            }
        });
        return itemStack;
    }

    @Override
    public void createResult() {
        int n;
        ItemStack itemStack = this.inputSlots.getItem(0);
        this.cost.set(1);
        int n2 = 0;
        int n3 = 0;
        int n4 = 0;
        if (itemStack.isEmpty()) {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
            this.cost.set(0);
            return;
        }
        ItemStack itemStack2 = itemStack.copy();
        ItemStack itemStack3 = this.inputSlots.getItem(1);
        Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemStack2);
        n3 += itemStack.getBaseRepairCost() + (itemStack3.isEmpty() ? 0 : itemStack3.getBaseRepairCost());
        this.repairItemCountCost = 0;
        if (!itemStack3.isEmpty()) {
            int n5 = n = itemStack3.getItem() == Items.ENCHANTED_BOOK && !EnchantedBookItem.getEnchantments(itemStack3).isEmpty() ? 1 : 0;
            if (itemStack2.isDamageableItem() && itemStack2.getItem().isValidRepairItem(itemStack, itemStack3)) {
                int n6;
                int n7 = Math.min(itemStack2.getDamageValue(), itemStack2.getMaxDamage() / 4);
                if (n7 <= 0) {
                    this.resultSlots.setItem(0, ItemStack.EMPTY);
                    this.cost.set(0);
                    return;
                }
                for (n6 = 0; n7 > 0 && n6 < itemStack3.getCount(); ++n6) {
                    int n8 = itemStack2.getDamageValue() - n7;
                    itemStack2.setDamageValue(n8);
                    ++n2;
                    n7 = Math.min(itemStack2.getDamageValue(), itemStack2.getMaxDamage() / 4);
                }
                this.repairItemCountCost = n6;
            } else {
                int n9;
                int n10;
                if (!(n != 0 || itemStack2.getItem() == itemStack3.getItem() && itemStack2.isDamageableItem())) {
                    this.resultSlots.setItem(0, ItemStack.EMPTY);
                    this.cost.set(0);
                    return;
                }
                if (itemStack2.isDamageableItem() && n == 0) {
                    int n11 = itemStack.getMaxDamage() - itemStack.getDamageValue();
                    n9 = itemStack3.getMaxDamage() - itemStack3.getDamageValue();
                    n10 = n9 + itemStack2.getMaxDamage() * 12 / 100;
                    int n12 = n11 + n10;
                    int n13 = itemStack2.getMaxDamage() - n12;
                    if (n13 < 0) {
                        n13 = 0;
                    }
                    if (n13 < itemStack2.getDamageValue()) {
                        itemStack2.setDamageValue(n13);
                        n2 += 2;
                    }
                }
                Map<Enchantment, Integer> map2 = EnchantmentHelper.getEnchantments(itemStack3);
                n9 = 0;
                n10 = 0;
                for (Enchantment enchantment : map2.keySet()) {
                    int n14;
                    if (enchantment == null) continue;
                    int n15 = map.getOrDefault(enchantment, 0);
                    n14 = n15 == (n14 = map2.get(enchantment).intValue()) ? n14 + 1 : Math.max(n14, n15);
                    boolean bl = enchantment.canEnchant(itemStack);
                    if (this.player.abilities.instabuild || itemStack.getItem() == Items.ENCHANTED_BOOK) {
                        bl = true;
                    }
                    for (Enchantment enchantment2 : map.keySet()) {
                        if (enchantment2 == enchantment || enchantment.isCompatibleWith(enchantment2)) continue;
                        bl = false;
                        ++n2;
                    }
                    if (!bl) {
                        n10 = 1;
                        continue;
                    }
                    n9 = 1;
                    if (n14 > enchantment.getMaxLevel()) {
                        n14 = enchantment.getMaxLevel();
                    }
                    map.put(enchantment, n14);
                    int n16 = 0;
                    switch (enchantment.getRarity()) {
                        case COMMON: {
                            n16 = 1;
                            break;
                        }
                        case UNCOMMON: {
                            n16 = 2;
                            break;
                        }
                        case RARE: {
                            n16 = 4;
                            break;
                        }
                        case VERY_RARE: {
                            n16 = 8;
                        }
                    }
                    if (n != 0) {
                        n16 = Math.max(1, n16 / 2);
                    }
                    n2 += n16 * n14;
                    if (itemStack.getCount() <= 1) continue;
                    n2 = 40;
                }
                if (n10 != 0 && n9 == 0) {
                    this.resultSlots.setItem(0, ItemStack.EMPTY);
                    this.cost.set(0);
                    return;
                }
            }
        }
        if (StringUtils.isBlank((CharSequence)this.itemName)) {
            if (itemStack.hasCustomHoverName()) {
                n4 = 1;
                n2 += n4;
                itemStack2.resetHoverName();
            }
        } else if (!this.itemName.equals(itemStack.getHoverName().getString())) {
            n4 = 1;
            n2 += n4;
            itemStack2.setHoverName(new TextComponent(this.itemName));
        }
        this.cost.set(n3 + n2);
        if (n2 <= 0) {
            itemStack2 = ItemStack.EMPTY;
        }
        if (n4 == n2 && n4 > 0 && this.cost.get() >= 40) {
            this.cost.set(39);
        }
        if (this.cost.get() >= 40 && !this.player.abilities.instabuild) {
            itemStack2 = ItemStack.EMPTY;
        }
        if (!itemStack2.isEmpty()) {
            n = itemStack2.getBaseRepairCost();
            if (!itemStack3.isEmpty() && n < itemStack3.getBaseRepairCost()) {
                n = itemStack3.getBaseRepairCost();
            }
            if (n4 != n2 || n4 == 0) {
                n = AnvilMenu.calculateIncreasedRepairCost(n);
            }
            itemStack2.setRepairCost(n);
            EnchantmentHelper.setEnchantments(map, itemStack2);
        }
        this.resultSlots.setItem(0, itemStack2);
        this.broadcastChanges();
    }

    public static int calculateIncreasedRepairCost(int n) {
        return n * 2 + 1;
    }

    public void setItemName(String string) {
        this.itemName = string;
        if (this.getSlot(2).hasItem()) {
            ItemStack itemStack = this.getSlot(2).getItem();
            if (StringUtils.isBlank((CharSequence)string)) {
                itemStack.resetHoverName();
            } else {
                itemStack.setHoverName(new TextComponent(this.itemName));
            }
        }
        this.createResult();
    }

    public int getCost() {
        return this.cost.get();
    }

}

