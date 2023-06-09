/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.item.crafting;

import java.util.Collection;
import java.util.Map;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class MapExtendingRecipe
extends ShapedRecipe {
    public MapExtendingRecipe(ResourceLocation resourceLocation) {
        super(resourceLocation, "", 3, 3, NonNullList.of(Ingredient.EMPTY, Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER), Ingredient.of(Items.FILLED_MAP), Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER)), new ItemStack(Items.MAP));
    }

    @Override
    public boolean matches(CraftingContainer craftingContainer, Level level) {
        if (!super.matches(craftingContainer, level)) {
            return false;
        }
        ItemStack itemStack = ItemStack.EMPTY;
        for (int i = 0; i < craftingContainer.getContainerSize() && itemStack.isEmpty(); ++i) {
            ItemStack itemStack2 = craftingContainer.getItem(i);
            if (itemStack2.getItem() != Items.FILLED_MAP) continue;
            itemStack = itemStack2;
        }
        if (itemStack.isEmpty()) {
            return false;
        }
        MapItemSavedData mapItemSavedData = MapItem.getOrCreateSavedData(itemStack, level);
        if (mapItemSavedData == null) {
            return false;
        }
        if (this.isExplorationMap(mapItemSavedData)) {
            return false;
        }
        return mapItemSavedData.scale < 4;
    }

    private boolean isExplorationMap(MapItemSavedData mapItemSavedData) {
        if (mapItemSavedData.decorations != null) {
            for (MapDecoration mapDecoration : mapItemSavedData.decorations.values()) {
                if (mapDecoration.getType() != MapDecoration.Type.MANSION && mapDecoration.getType() != MapDecoration.Type.MONUMENT) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public ItemStack assemble(CraftingContainer craftingContainer) {
        ItemStack itemStack = ItemStack.EMPTY;
        for (int i = 0; i < craftingContainer.getContainerSize() && itemStack.isEmpty(); ++i) {
            ItemStack itemStack2 = craftingContainer.getItem(i);
            if (itemStack2.getItem() != Items.FILLED_MAP) continue;
            itemStack = itemStack2;
        }
        itemStack = itemStack.copy();
        itemStack.setCount(1);
        itemStack.getOrCreateTag().putInt("map_scale_direction", 1);
        return itemStack;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.MAP_EXTENDING;
    }
}

