/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.inventory;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class GrindstoneMenu
extends AbstractContainerMenu {
    private final Container resultSlots = new ResultContainer();
    private final Container repairSlots = new SimpleContainer(2){

        @Override
        public void setChanged() {
            super.setChanged();
            GrindstoneMenu.this.slotsChanged(this);
        }
    };
    private final ContainerLevelAccess access;

    public GrindstoneMenu(int n, Inventory inventory) {
        this(n, inventory, ContainerLevelAccess.NULL);
    }

    public GrindstoneMenu(int n, Inventory inventory, final ContainerLevelAccess containerLevelAccess) {
        super(MenuType.GRINDSTONE, n);
        int n2;
        this.access = containerLevelAccess;
        this.addSlot(new Slot(this.repairSlots, 0, 49, 19){

            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return itemStack.isDamageableItem() || itemStack.getItem() == Items.ENCHANTED_BOOK || itemStack.isEnchanted();
            }
        });
        this.addSlot(new Slot(this.repairSlots, 1, 49, 40){

            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return itemStack.isDamageableItem() || itemStack.getItem() == Items.ENCHANTED_BOOK || itemStack.isEnchanted();
            }
        });
        this.addSlot(new Slot(this.resultSlots, 2, 129, 34){

            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return false;
            }

            @Override
            public ItemStack onTake(Player player, ItemStack itemStack) {
                containerLevelAccess.execute((level, blockPos) -> {
                    int n;
                    for (int i = this.getExperienceAmount((Level)level); i > 0; i -= n) {
                        n = ExperienceOrb.getExperienceValue(i);
                        level.addFreshEntity(new ExperienceOrb((Level)level, blockPos.getX(), (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, n));
                    }
                    level.levelEvent(1042, (BlockPos)blockPos, 0);
                });
                GrindstoneMenu.this.repairSlots.setItem(0, ItemStack.EMPTY);
                GrindstoneMenu.this.repairSlots.setItem(1, ItemStack.EMPTY);
                return itemStack;
            }

            private int getExperienceAmount(Level level) {
                int n = 0;
                n += this.getExperienceFromItem(GrindstoneMenu.this.repairSlots.getItem(0));
                if ((n += this.getExperienceFromItem(GrindstoneMenu.this.repairSlots.getItem(1))) > 0) {
                    int n2 = (int)Math.ceil((double)n / 2.0);
                    return n2 + level.random.nextInt(n2);
                }
                return 0;
            }

            private int getExperienceFromItem(ItemStack itemStack) {
                int n = 0;
                Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemStack);
                for (Map.Entry<Enchantment, Integer> entry : map.entrySet()) {
                    Enchantment enchantment = entry.getKey();
                    Integer n2 = entry.getValue();
                    if (enchantment.isCurse()) continue;
                    n += enchantment.getMinCost(n2);
                }
                return n;
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
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        if (container == this.repairSlots) {
            this.createResult();
        }
    }

    /*
     * Enabled aggressive block sorting
     */
    private void createResult() {
        boolean bl;
        ItemStack itemStack = this.repairSlots.getItem(0);
        ItemStack itemStack2 = this.repairSlots.getItem(1);
        boolean bl2 = !itemStack.isEmpty() || !itemStack2.isEmpty();
        boolean bl3 = bl = !itemStack.isEmpty() && !itemStack2.isEmpty();
        if (bl2) {
            ItemStack itemStack3;
            int n;
            boolean bl4;
            boolean bl5 = bl4 = !itemStack.isEmpty() && itemStack.getItem() != Items.ENCHANTED_BOOK && !itemStack.isEnchanted() || !itemStack2.isEmpty() && itemStack2.getItem() != Items.ENCHANTED_BOOK && !itemStack2.isEnchanted();
            if (itemStack.getCount() > 1 || itemStack2.getCount() > 1 || !bl && bl4) {
                this.resultSlots.setItem(0, ItemStack.EMPTY);
                this.broadcastChanges();
                return;
            }
            int n2 = 1;
            if (bl) {
                if (itemStack.getItem() != itemStack2.getItem()) {
                    this.resultSlots.setItem(0, ItemStack.EMPTY);
                    this.broadcastChanges();
                    return;
                }
                Item item = itemStack.getItem();
                int n3 = item.getMaxDamage() - itemStack.getDamageValue();
                int n4 = item.getMaxDamage() - itemStack2.getDamageValue();
                int n5 = n3 + n4 + item.getMaxDamage() * 5 / 100;
                n = Math.max(item.getMaxDamage() - n5, 0);
                itemStack3 = this.mergeEnchants(itemStack, itemStack2);
                if (!itemStack3.isDamageableItem()) {
                    if (!ItemStack.matches(itemStack, itemStack2)) {
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                        this.broadcastChanges();
                        return;
                    }
                    n2 = 2;
                }
            } else {
                boolean bl6 = !itemStack.isEmpty();
                n = bl6 ? itemStack.getDamageValue() : itemStack2.getDamageValue();
                itemStack3 = bl6 ? itemStack : itemStack2;
            }
            this.resultSlots.setItem(0, this.removeNonCurses(itemStack3, n, n2));
        } else {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
        }
        this.broadcastChanges();
    }

    private ItemStack mergeEnchants(ItemStack itemStack, ItemStack itemStack2) {
        ItemStack itemStack3 = itemStack.copy();
        Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemStack2);
        for (Map.Entry<Enchantment, Integer> entry : map.entrySet()) {
            Enchantment enchantment = entry.getKey();
            if (enchantment.isCurse() && EnchantmentHelper.getItemEnchantmentLevel(enchantment, itemStack3) != 0) continue;
            itemStack3.enchant(enchantment, entry.getValue());
        }
        return itemStack3;
    }

    private ItemStack removeNonCurses(ItemStack itemStack, int n, int n2) {
        ItemStack itemStack2 = itemStack.copy();
        itemStack2.removeTagKey("Enchantments");
        itemStack2.removeTagKey("StoredEnchantments");
        if (n > 0) {
            itemStack2.setDamageValue(n);
        } else {
            itemStack2.removeTagKey("Damage");
        }
        itemStack2.setCount(n2);
        Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemStack).entrySet().stream().filter(entry -> ((Enchantment)entry.getKey()).isCurse()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        EnchantmentHelper.setEnchantments(map, itemStack2);
        itemStack2.setRepairCost(0);
        if (itemStack2.getItem() == Items.ENCHANTED_BOOK && map.size() == 0) {
            itemStack2 = new ItemStack(Items.BOOK);
            if (itemStack.hasCustomHoverName()) {
                itemStack2.setHoverName(itemStack.getHoverName());
            }
        }
        for (int i = 0; i < map.size(); ++i) {
            itemStack2.setRepairCost(AnvilMenu.calculateIncreasedRepairCost(itemStack2.getBaseRepairCost()));
        }
        return itemStack2;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.access.execute((level, blockPos) -> this.clearContainer(player, (Level)level, this.repairSlots));
    }

    @Override
    public boolean stillValid(Player player) {
        return GrindstoneMenu.stillValid(this.access, player, Blocks.GRINDSTONE);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int n) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(n);
        if (slot != null && slot.hasItem()) {
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.copy();
            ItemStack itemStack3 = this.repairSlots.getItem(0);
            ItemStack itemStack4 = this.repairSlots.getItem(1);
            if (n == 2) {
                if (!this.moveItemStackTo(itemStack2, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemStack2, itemStack);
            } else if (n == 0 || n == 1 ? !this.moveItemStackTo(itemStack2, 3, 39, false) : (itemStack3.isEmpty() || itemStack4.isEmpty() ? !this.moveItemStackTo(itemStack2, 0, 2, false) : (n >= 3 && n < 30 ? !this.moveItemStackTo(itemStack2, 30, 39, false) : n >= 30 && n < 39 && !this.moveItemStackTo(itemStack2, 3, 30, false)))) {
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

