/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.inventory;

import net.minecraft.recipebook.ServerPlaceRecipe;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.Recipe;

public abstract class RecipeBookMenu<C extends Container>
extends AbstractContainerMenu {
    public RecipeBookMenu(MenuType<?> menuType, int n) {
        super(menuType, n);
    }

    public void handlePlacement(boolean bl, Recipe<?> recipe, ServerPlayer serverPlayer) {
        new ServerPlaceRecipe(this).recipeClicked(serverPlayer, recipe, bl);
    }

    public abstract void fillCraftSlotsStackedContents(StackedContents var1);

    public abstract void clearCraftingContent();

    public abstract boolean recipeMatches(Recipe<? super C> var1);

    public abstract int getResultSlotIndex();

    public abstract int getGridWidth();

    public abstract int getGridHeight();

    public abstract int getSize();

    public abstract RecipeBookType getRecipeBookType();
}

