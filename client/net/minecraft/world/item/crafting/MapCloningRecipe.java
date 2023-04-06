/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.item.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.Level;

public class MapCloningRecipe
extends CustomRecipe {
    public MapCloningRecipe(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }

    @Override
    public boolean matches(CraftingContainer craftingContainer, Level level) {
        int n = 0;
        ItemStack itemStack = ItemStack.EMPTY;
        for (int i = 0; i < craftingContainer.getContainerSize(); ++i) {
            ItemStack itemStack2 = craftingContainer.getItem(i);
            if (itemStack2.isEmpty()) continue;
            if (itemStack2.getItem() == Items.FILLED_MAP) {
                if (!itemStack.isEmpty()) {
                    return false;
                }
                itemStack = itemStack2;
                continue;
            }
            if (itemStack2.getItem() == Items.MAP) {
                ++n;
                continue;
            }
            return false;
        }
        return !itemStack.isEmpty() && n > 0;
    }

    @Override
    public ItemStack assemble(CraftingContainer craftingContainer) {
        int n = 0;
        ItemStack itemStack = ItemStack.EMPTY;
        for (int i = 0; i < craftingContainer.getContainerSize(); ++i) {
            ItemStack itemStack2 = craftingContainer.getItem(i);
            if (itemStack2.isEmpty()) continue;
            if (itemStack2.getItem() == Items.FILLED_MAP) {
                if (!itemStack.isEmpty()) {
                    return ItemStack.EMPTY;
                }
                itemStack = itemStack2;
                continue;
            }
            if (itemStack2.getItem() == Items.MAP) {
                ++n;
                continue;
            }
            return ItemStack.EMPTY;
        }
        if (itemStack.isEmpty() || n < 1) {
            return ItemStack.EMPTY;
        }
        ItemStack itemStack3 = itemStack.copy();
        itemStack3.setCount(n + 1);
        return itemStack3;
    }

    @Override
    public boolean canCraftInDimensions(int n, int n2) {
        return n >= 3 && n2 >= 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.MAP_CLONING;
    }
}

