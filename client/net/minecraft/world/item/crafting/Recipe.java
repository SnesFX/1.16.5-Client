/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.item.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public interface Recipe<C extends Container> {
    public boolean matches(C var1, Level var2);

    public ItemStack assemble(C var1);

    public boolean canCraftInDimensions(int var1, int var2);

    public ItemStack getResultItem();

    default public NonNullList<ItemStack> getRemainingItems(C c) {
        NonNullList<ItemStack> nonNullList = NonNullList.withSize(c.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < nonNullList.size(); ++i) {
            Item item = c.getItem(i).getItem();
            if (!item.hasCraftingRemainingItem()) continue;
            nonNullList.set(i, new ItemStack(item.getCraftingRemainingItem()));
        }
        return nonNullList;
    }

    default public NonNullList<Ingredient> getIngredients() {
        return NonNullList.create();
    }

    default public boolean isSpecial() {
        return false;
    }

    default public String getGroup() {
        return "";
    }

    default public ItemStack getToastSymbol() {
        return new ItemStack(Blocks.CRAFTING_TABLE);
    }

    public ResourceLocation getId();

    public RecipeSerializer<?> getSerializer();

    public RecipeType<?> getType();
}

