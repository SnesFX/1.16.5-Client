/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.item.crafting;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.Level;

public class ShieldDecorationRecipe
extends CustomRecipe {
    public ShieldDecorationRecipe(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }

    @Override
    public boolean matches(CraftingContainer craftingContainer, Level level) {
        ItemStack itemStack = ItemStack.EMPTY;
        ItemStack itemStack2 = ItemStack.EMPTY;
        for (int i = 0; i < craftingContainer.getContainerSize(); ++i) {
            ItemStack itemStack3 = craftingContainer.getItem(i);
            if (itemStack3.isEmpty()) continue;
            if (itemStack3.getItem() instanceof BannerItem) {
                if (!itemStack2.isEmpty()) {
                    return false;
                }
                itemStack2 = itemStack3;
                continue;
            }
            if (itemStack3.getItem() == Items.SHIELD) {
                if (!itemStack.isEmpty()) {
                    return false;
                }
                if (itemStack3.getTagElement("BlockEntityTag") != null) {
                    return false;
                }
                itemStack = itemStack3;
                continue;
            }
            return false;
        }
        return !itemStack.isEmpty() && !itemStack2.isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingContainer craftingContainer) {
        Object object;
        ItemStack itemStack = ItemStack.EMPTY;
        ItemStack itemStack2 = ItemStack.EMPTY;
        for (int i = 0; i < craftingContainer.getContainerSize(); ++i) {
            object = craftingContainer.getItem(i);
            if (((ItemStack)object).isEmpty()) continue;
            if (((ItemStack)object).getItem() instanceof BannerItem) {
                itemStack = object;
                continue;
            }
            if (((ItemStack)object).getItem() != Items.SHIELD) continue;
            itemStack2 = ((ItemStack)object).copy();
        }
        if (itemStack2.isEmpty()) {
            return itemStack2;
        }
        CompoundTag compoundTag = itemStack.getTagElement("BlockEntityTag");
        object = compoundTag == null ? new CompoundTag() : compoundTag.copy();
        ((CompoundTag)object).putInt("Base", ((BannerItem)itemStack.getItem()).getColor().getId());
        itemStack2.addTagElement("BlockEntityTag", (Tag)object);
        return itemStack2;
    }

    @Override
    public boolean canCraftInDimensions(int n, int n2) {
        return n * n2 >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SHIELD_DECORATION;
    }
}

