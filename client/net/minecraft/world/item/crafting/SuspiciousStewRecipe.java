/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.item.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SuspiciousStewItem;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBlock;

public class SuspiciousStewRecipe
extends CustomRecipe {
    public SuspiciousStewRecipe(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }

    @Override
    public boolean matches(CraftingContainer craftingContainer, Level level) {
        boolean bl = false;
        boolean bl2 = false;
        boolean bl3 = false;
        boolean bl4 = false;
        for (int i = 0; i < craftingContainer.getContainerSize(); ++i) {
            ItemStack itemStack = craftingContainer.getItem(i);
            if (itemStack.isEmpty()) continue;
            if (itemStack.getItem() == Blocks.BROWN_MUSHROOM.asItem() && !bl3) {
                bl3 = true;
                continue;
            }
            if (itemStack.getItem() == Blocks.RED_MUSHROOM.asItem() && !bl2) {
                bl2 = true;
                continue;
            }
            if (itemStack.getItem().is(ItemTags.SMALL_FLOWERS) && !bl) {
                bl = true;
                continue;
            }
            if (itemStack.getItem() == Items.BOWL && !bl4) {
                bl4 = true;
                continue;
            }
            return false;
        }
        return bl && bl3 && bl2 && bl4;
    }

    @Override
    public ItemStack assemble(CraftingContainer craftingContainer) {
        Object object;
        ItemStack itemStack = ItemStack.EMPTY;
        for (int i = 0; i < craftingContainer.getContainerSize(); ++i) {
            object = craftingContainer.getItem(i);
            if (((ItemStack)object).isEmpty() || !((ItemStack)object).getItem().is(ItemTags.SMALL_FLOWERS)) continue;
            itemStack = object;
            break;
        }
        ItemStack itemStack2 = new ItemStack(Items.SUSPICIOUS_STEW, 1);
        if (itemStack.getItem() instanceof BlockItem && ((BlockItem)itemStack.getItem()).getBlock() instanceof FlowerBlock) {
            object = (FlowerBlock)((BlockItem)itemStack.getItem()).getBlock();
            MobEffect mobEffect = ((FlowerBlock)object).getSuspiciousStewEffect();
            SuspiciousStewItem.saveMobEffect(itemStack2, mobEffect, ((FlowerBlock)object).getEffectDuration());
        }
        return itemStack2;
    }

    @Override
    public boolean canCraftInDimensions(int n, int n2) {
        return n >= 2 && n2 >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SUSPICIOUS_STEW;
    }
}

