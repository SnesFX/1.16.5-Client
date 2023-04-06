/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.item.crafting;

import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public interface CraftingRecipe
extends Recipe<CraftingContainer> {
    @Override
    default public RecipeType<?> getType() {
        return RecipeType.CRAFTING;
    }
}

