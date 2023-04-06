/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.inventory;

import java.util.List;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.BrewedPotionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionUtils;

public class BrewingStandMenu
extends AbstractContainerMenu {
    private final Container brewingStand;
    private final ContainerData brewingStandData;
    private final Slot ingredientSlot;

    public BrewingStandMenu(int n, Inventory inventory) {
        this(n, inventory, new SimpleContainer(5), new SimpleContainerData(2));
    }

    public BrewingStandMenu(int n, Inventory inventory, Container container, ContainerData containerData) {
        super(MenuType.BREWING_STAND, n);
        int n2;
        BrewingStandMenu.checkContainerSize(container, 5);
        BrewingStandMenu.checkContainerDataCount(containerData, 2);
        this.brewingStand = container;
        this.brewingStandData = containerData;
        this.addSlot(new PotionSlot(container, 0, 56, 51));
        this.addSlot(new PotionSlot(container, 1, 79, 58));
        this.addSlot(new PotionSlot(container, 2, 102, 51));
        this.ingredientSlot = this.addSlot(new IngredientsSlot(container, 3, 79, 17));
        this.addSlot(new FuelSlot(container, 4, 17, 17));
        this.addDataSlots(containerData);
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
        return this.brewingStand.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int n) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(n);
        if (slot != null && slot.hasItem()) {
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.copy();
            if (n >= 0 && n <= 2 || n == 3 || n == 4) {
                if (!this.moveItemStackTo(itemStack2, 5, 41, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemStack2, itemStack);
            } else if (FuelSlot.mayPlaceItem(itemStack) ? this.moveItemStackTo(itemStack2, 4, 5, false) || this.ingredientSlot.mayPlace(itemStack2) && !this.moveItemStackTo(itemStack2, 3, 4, false) : (this.ingredientSlot.mayPlace(itemStack2) ? !this.moveItemStackTo(itemStack2, 3, 4, false) : (PotionSlot.mayPlaceItem(itemStack) && itemStack.getCount() == 1 ? !this.moveItemStackTo(itemStack2, 0, 3, false) : (n >= 5 && n < 32 ? !this.moveItemStackTo(itemStack2, 32, 41, false) : (n >= 32 && n < 41 ? !this.moveItemStackTo(itemStack2, 5, 32, false) : !this.moveItemStackTo(itemStack2, 5, 41, false)))))) {
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

    public int getFuel() {
        return this.brewingStandData.get(1);
    }

    public int getBrewingTicks() {
        return this.brewingStandData.get(0);
    }

    static class FuelSlot
    extends Slot {
        public FuelSlot(Container container, int n, int n2, int n3) {
            super(container, n, n2, n3);
        }

        @Override
        public boolean mayPlace(ItemStack itemStack) {
            return FuelSlot.mayPlaceItem(itemStack);
        }

        public static boolean mayPlaceItem(ItemStack itemStack) {
            return itemStack.getItem() == Items.BLAZE_POWDER;
        }

        @Override
        public int getMaxStackSize() {
            return 64;
        }
    }

    static class IngredientsSlot
    extends Slot {
        public IngredientsSlot(Container container, int n, int n2, int n3) {
            super(container, n, n2, n3);
        }

        @Override
        public boolean mayPlace(ItemStack itemStack) {
            return PotionBrewing.isIngredient(itemStack);
        }

        @Override
        public int getMaxStackSize() {
            return 64;
        }
    }

    static class PotionSlot
    extends Slot {
        public PotionSlot(Container container, int n, int n2, int n3) {
            super(container, n, n2, n3);
        }

        @Override
        public boolean mayPlace(ItemStack itemStack) {
            return PotionSlot.mayPlaceItem(itemStack);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public ItemStack onTake(Player player, ItemStack itemStack) {
            Potion potion = PotionUtils.getPotion(itemStack);
            if (player instanceof ServerPlayer) {
                CriteriaTriggers.BREWED_POTION.trigger((ServerPlayer)player, potion);
            }
            super.onTake(player, itemStack);
            return itemStack;
        }

        public static boolean mayPlaceItem(ItemStack itemStack) {
            Item item = itemStack.getItem();
            return item == Items.POTION || item == Items.SPLASH_POTION || item == Items.LINGERING_POTION || item == Items.GLASS_BOTTLE;
        }
    }

}

