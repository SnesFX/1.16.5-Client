/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class ResultSlot
extends Slot {
    private final CraftingContainer craftSlots;
    private final Player player;
    private int removeCount;

    public ResultSlot(Player player, CraftingContainer craftingContainer, Container container, int n, int n2, int n3) {
        super(container, n, n2, n3);
        this.player = player;
        this.craftSlots = craftingContainer;
    }

    @Override
    public boolean mayPlace(ItemStack itemStack) {
        return false;
    }

    @Override
    public ItemStack remove(int n) {
        if (this.hasItem()) {
            this.removeCount += Math.min(n, this.getItem().getCount());
        }
        return super.remove(n);
    }

    @Override
    protected void onQuickCraft(ItemStack itemStack, int n) {
        this.removeCount += n;
        this.checkTakeAchievements(itemStack);
    }

    @Override
    protected void onSwapCraft(int n) {
        this.removeCount += n;
    }

    @Override
    protected void checkTakeAchievements(ItemStack itemStack) {
        if (this.removeCount > 0) {
            itemStack.onCraftedBy(this.player.level, this.player, this.removeCount);
        }
        if (this.container instanceof RecipeHolder) {
            ((RecipeHolder)((Object)this.container)).awardUsedRecipes(this.player);
        }
        this.removeCount = 0;
    }

    @Override
    public ItemStack onTake(Player player, ItemStack itemStack) {
        this.checkTakeAchievements(itemStack);
        NonNullList<ItemStack> nonNullList = player.level.getRecipeManager().getRemainingItemsFor(RecipeType.CRAFTING, this.craftSlots, player.level);
        for (int i = 0; i < nonNullList.size(); ++i) {
            ItemStack itemStack2 = this.craftSlots.getItem(i);
            ItemStack itemStack3 = nonNullList.get(i);
            if (!itemStack2.isEmpty()) {
                this.craftSlots.removeItem(i, 1);
                itemStack2 = this.craftSlots.getItem(i);
            }
            if (itemStack3.isEmpty()) continue;
            if (itemStack2.isEmpty()) {
                this.craftSlots.setItem(i, itemStack3);
                continue;
            }
            if (ItemStack.isSame(itemStack2, itemStack3) && ItemStack.tagMatches(itemStack2, itemStack3)) {
                itemStack3.grow(itemStack2.getCount());
                this.craftSlots.setItem(i, itemStack3);
                continue;
            }
            if (this.player.inventory.add(itemStack3)) continue;
            this.player.drop(itemStack3, false);
        }
        return itemStack;
    }
}

