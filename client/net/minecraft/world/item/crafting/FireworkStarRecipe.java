/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 */
package net.minecraft.world.item.crafting;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class FireworkStarRecipe
extends CustomRecipe {
    private static final Ingredient SHAPE_INGREDIENT = Ingredient.of(Items.FIRE_CHARGE, Items.FEATHER, Items.GOLD_NUGGET, Items.SKELETON_SKULL, Items.WITHER_SKELETON_SKULL, Items.CREEPER_HEAD, Items.PLAYER_HEAD, Items.DRAGON_HEAD, Items.ZOMBIE_HEAD);
    private static final Ingredient TRAIL_INGREDIENT = Ingredient.of(Items.DIAMOND);
    private static final Ingredient FLICKER_INGREDIENT = Ingredient.of(Items.GLOWSTONE_DUST);
    private static final Map<Item, FireworkRocketItem.Shape> SHAPE_BY_ITEM = Util.make(Maps.newHashMap(), hashMap -> {
        hashMap.put(Items.FIRE_CHARGE, FireworkRocketItem.Shape.LARGE_BALL);
        hashMap.put(Items.FEATHER, FireworkRocketItem.Shape.BURST);
        hashMap.put(Items.GOLD_NUGGET, FireworkRocketItem.Shape.STAR);
        hashMap.put(Items.SKELETON_SKULL, FireworkRocketItem.Shape.CREEPER);
        hashMap.put(Items.WITHER_SKELETON_SKULL, FireworkRocketItem.Shape.CREEPER);
        hashMap.put(Items.CREEPER_HEAD, FireworkRocketItem.Shape.CREEPER);
        hashMap.put(Items.PLAYER_HEAD, FireworkRocketItem.Shape.CREEPER);
        hashMap.put(Items.DRAGON_HEAD, FireworkRocketItem.Shape.CREEPER);
        hashMap.put(Items.ZOMBIE_HEAD, FireworkRocketItem.Shape.CREEPER);
    });
    private static final Ingredient GUNPOWDER_INGREDIENT = Ingredient.of(Items.GUNPOWDER);

    public FireworkStarRecipe(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }

    @Override
    public boolean matches(CraftingContainer craftingContainer, Level level) {
        boolean bl = false;
        boolean bl2 = false;
        boolean bl3 = false;
        boolean bl4 = false;
        boolean bl5 = false;
        for (int i = 0; i < craftingContainer.getContainerSize(); ++i) {
            ItemStack itemStack = craftingContainer.getItem(i);
            if (itemStack.isEmpty()) continue;
            if (SHAPE_INGREDIENT.test(itemStack)) {
                if (bl3) {
                    return false;
                }
                bl3 = true;
                continue;
            }
            if (FLICKER_INGREDIENT.test(itemStack)) {
                if (bl5) {
                    return false;
                }
                bl5 = true;
                continue;
            }
            if (TRAIL_INGREDIENT.test(itemStack)) {
                if (bl4) {
                    return false;
                }
                bl4 = true;
                continue;
            }
            if (GUNPOWDER_INGREDIENT.test(itemStack)) {
                if (bl) {
                    return false;
                }
                bl = true;
                continue;
            }
            if (itemStack.getItem() instanceof DyeItem) {
                bl2 = true;
                continue;
            }
            return false;
        }
        return bl && bl2;
    }

    @Override
    public ItemStack assemble(CraftingContainer craftingContainer) {
        ItemStack itemStack = new ItemStack(Items.FIREWORK_STAR);
        CompoundTag compoundTag = itemStack.getOrCreateTagElement("Explosion");
        FireworkRocketItem.Shape shape = FireworkRocketItem.Shape.SMALL_BALL;
        ArrayList arrayList = Lists.newArrayList();
        for (int i = 0; i < craftingContainer.getContainerSize(); ++i) {
            ItemStack itemStack2 = craftingContainer.getItem(i);
            if (itemStack2.isEmpty()) continue;
            if (SHAPE_INGREDIENT.test(itemStack2)) {
                shape = SHAPE_BY_ITEM.get(itemStack2.getItem());
                continue;
            }
            if (FLICKER_INGREDIENT.test(itemStack2)) {
                compoundTag.putBoolean("Flicker", true);
                continue;
            }
            if (TRAIL_INGREDIENT.test(itemStack2)) {
                compoundTag.putBoolean("Trail", true);
                continue;
            }
            if (!(itemStack2.getItem() instanceof DyeItem)) continue;
            arrayList.add(((DyeItem)itemStack2.getItem()).getDyeColor().getFireworkColor());
        }
        compoundTag.putIntArray("Colors", arrayList);
        compoundTag.putByte("Type", (byte)shape.getId());
        return itemStack;
    }

    @Override
    public boolean canCraftInDimensions(int n, int n2) {
        return n * n2 >= 2;
    }

    @Override
    public ItemStack getResultItem() {
        return new ItemStack(Items.FIREWORK_STAR);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.FIREWORK_STAR;
    }
}

