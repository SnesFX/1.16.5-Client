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
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class TippedArrowRecipe
extends CustomRecipe {
    public TippedArrowRecipe(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }

    @Override
    public boolean matches(CraftingContainer craftingContainer, Level level) {
        if (craftingContainer.getWidth() != 3 || craftingContainer.getHeight() != 3) {
            return false;
        }
        for (int i = 0; i < craftingContainer.getWidth(); ++i) {
            for (int j = 0; j < craftingContainer.getHeight(); ++j) {
                ItemStack itemStack = craftingContainer.getItem(i + j * craftingContainer.getWidth());
                if (itemStack.isEmpty()) {
                    return false;
                }
                Item item = itemStack.getItem();
                if (!(i == 1 && j == 1 ? item != Items.LINGERING_POTION : item != Items.ARROW)) continue;
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(CraftingContainer craftingContainer) {
        ItemStack itemStack = craftingContainer.getItem(1 + craftingContainer.getWidth());
        if (itemStack.getItem() != Items.LINGERING_POTION) {
            return ItemStack.EMPTY;
        }
        ItemStack itemStack2 = new ItemStack(Items.TIPPED_ARROW, 8);
        PotionUtils.setPotion(itemStack2, PotionUtils.getPotion(itemStack));
        PotionUtils.setCustomEffects(itemStack2, PotionUtils.getCustomEffects(itemStack));
        return itemStack2;
    }

    @Override
    public boolean canCraftInDimensions(int n, int n2) {
        return n >= 2 && n2 >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.TIPPED_ARROW;
    }
}

