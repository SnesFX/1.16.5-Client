/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.item.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class BookCloningRecipe
extends CustomRecipe {
    public BookCloningRecipe(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }

    @Override
    public boolean matches(CraftingContainer craftingContainer, Level level) {
        int n = 0;
        ItemStack itemStack = ItemStack.EMPTY;
        for (int i = 0; i < craftingContainer.getContainerSize(); ++i) {
            ItemStack itemStack2 = craftingContainer.getItem(i);
            if (itemStack2.isEmpty()) continue;
            if (itemStack2.getItem() == Items.WRITTEN_BOOK) {
                if (!itemStack.isEmpty()) {
                    return false;
                }
                itemStack = itemStack2;
                continue;
            }
            if (itemStack2.getItem() == Items.WRITABLE_BOOK) {
                ++n;
                continue;
            }
            return false;
        }
        return !itemStack.isEmpty() && itemStack.hasTag() && n > 0;
    }

    @Override
    public ItemStack assemble(CraftingContainer craftingContainer) {
        Object object;
        int n = 0;
        ItemStack itemStack = ItemStack.EMPTY;
        for (int i = 0; i < craftingContainer.getContainerSize(); ++i) {
            object = craftingContainer.getItem(i);
            if (((ItemStack)object).isEmpty()) continue;
            if (((ItemStack)object).getItem() == Items.WRITTEN_BOOK) {
                if (!itemStack.isEmpty()) {
                    return ItemStack.EMPTY;
                }
                itemStack = object;
                continue;
            }
            if (((ItemStack)object).getItem() == Items.WRITABLE_BOOK) {
                ++n;
                continue;
            }
            return ItemStack.EMPTY;
        }
        if (itemStack.isEmpty() || !itemStack.hasTag() || n < 1 || WrittenBookItem.getGeneration(itemStack) >= 2) {
            return ItemStack.EMPTY;
        }
        ItemStack itemStack2 = new ItemStack(Items.WRITTEN_BOOK, n);
        object = itemStack.getTag().copy();
        ((CompoundTag)object).putInt("generation", WrittenBookItem.getGeneration(itemStack) + 1);
        itemStack2.setTag((CompoundTag)object);
        return itemStack2;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer craftingContainer) {
        NonNullList<ItemStack> nonNullList = NonNullList.withSize(craftingContainer.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < nonNullList.size(); ++i) {
            ItemStack itemStack = craftingContainer.getItem(i);
            if (itemStack.getItem().hasCraftingRemainingItem()) {
                nonNullList.set(i, new ItemStack(itemStack.getItem().getCraftingRemainingItem()));
                continue;
            }
            if (!(itemStack.getItem() instanceof WrittenBookItem)) continue;
            ItemStack itemStack2 = itemStack.copy();
            itemStack2.setCount(1);
            nonNullList.set(i, itemStack2);
            break;
        }
        return nonNullList;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.BOOK_CLONING;
    }

    @Override
    public boolean canCraftInDimensions(int n, int n2) {
        return n >= 3 && n2 >= 3;
    }
}

