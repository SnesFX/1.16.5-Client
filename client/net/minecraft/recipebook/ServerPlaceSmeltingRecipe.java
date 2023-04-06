/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 *  it.unimi.dsi.fastutil.ints.IntListIterator
 */
package net.minecraft.recipebook;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import net.minecraft.recipebook.ServerPlaceRecipe;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

public class ServerPlaceSmeltingRecipe<C extends Container>
extends ServerPlaceRecipe<C> {
    private boolean recipeMatchesPlaced;

    public ServerPlaceSmeltingRecipe(RecipeBookMenu<C> recipeBookMenu) {
        super(recipeBookMenu);
    }

    @Override
    protected void handleRecipeClicked(Recipe<C> recipe, boolean bl) {
        ItemStack itemStack;
        this.recipeMatchesPlaced = this.menu.recipeMatches(recipe);
        int n = this.stackedContents.getBiggestCraftableStack(recipe, null);
        if (this.recipeMatchesPlaced && ((itemStack = this.menu.getSlot(0).getItem()).isEmpty() || n <= itemStack.getCount())) {
            return;
        }
        IntArrayList intArrayList = new IntArrayList();
        int n2 = this.getStackSize(bl, n, this.recipeMatchesPlaced);
        if (!this.stackedContents.canCraft(recipe, (IntList)intArrayList, n2)) {
            return;
        }
        if (!this.recipeMatchesPlaced) {
            this.moveItemToInventory(this.menu.getResultSlotIndex());
            this.moveItemToInventory(0);
        }
        this.placeRecipe(n2, (IntList)intArrayList);
    }

    @Override
    protected void clearGrid() {
        this.moveItemToInventory(this.menu.getResultSlotIndex());
        super.clearGrid();
    }

    protected void placeRecipe(int n, IntList intList) {
        IntListIterator intListIterator = intList.iterator();
        Slot slot = this.menu.getSlot(0);
        ItemStack itemStack = StackedContents.fromStackingIndex((Integer)intListIterator.next());
        if (itemStack.isEmpty()) {
            return;
        }
        int n2 = Math.min(itemStack.getMaxStackSize(), n);
        if (this.recipeMatchesPlaced) {
            n2 -= slot.getItem().getCount();
        }
        for (int i = 0; i < n2; ++i) {
            this.moveItemToGrid(slot, itemStack);
        }
    }
}

