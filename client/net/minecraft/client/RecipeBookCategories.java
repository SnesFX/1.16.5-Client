/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public enum RecipeBookCategories {
    CRAFTING_SEARCH(new ItemStack(Items.COMPASS)),
    CRAFTING_BUILDING_BLOCKS(new ItemStack(Blocks.BRICKS)),
    CRAFTING_REDSTONE(new ItemStack(Items.REDSTONE)),
    CRAFTING_EQUIPMENT(new ItemStack(Items.IRON_AXE), new ItemStack(Items.GOLDEN_SWORD)),
    CRAFTING_MISC(new ItemStack(Items.LAVA_BUCKET), new ItemStack(Items.APPLE)),
    FURNACE_SEARCH(new ItemStack(Items.COMPASS)),
    FURNACE_FOOD(new ItemStack(Items.PORKCHOP)),
    FURNACE_BLOCKS(new ItemStack(Blocks.STONE)),
    FURNACE_MISC(new ItemStack(Items.LAVA_BUCKET), new ItemStack(Items.EMERALD)),
    BLAST_FURNACE_SEARCH(new ItemStack(Items.COMPASS)),
    BLAST_FURNACE_BLOCKS(new ItemStack(Blocks.REDSTONE_ORE)),
    BLAST_FURNACE_MISC(new ItemStack(Items.IRON_SHOVEL), new ItemStack(Items.GOLDEN_LEGGINGS)),
    SMOKER_SEARCH(new ItemStack(Items.COMPASS)),
    SMOKER_FOOD(new ItemStack(Items.PORKCHOP)),
    STONECUTTER(new ItemStack(Items.CHISELED_STONE_BRICKS)),
    SMITHING(new ItemStack(Items.NETHERITE_CHESTPLATE)),
    CAMPFIRE(new ItemStack(Items.PORKCHOP)),
    UNKNOWN(new ItemStack(Items.BARRIER));
    
    public static final List<RecipeBookCategories> SMOKER_CATEGORIES;
    public static final List<RecipeBookCategories> BLAST_FURNACE_CATEGORIES;
    public static final List<RecipeBookCategories> FURNACE_CATEGORIES;
    public static final List<RecipeBookCategories> CRAFTING_CATEGORIES;
    public static final Map<RecipeBookCategories, List<RecipeBookCategories>> AGGREGATE_CATEGORIES;
    private final List<ItemStack> itemIcons;

    private RecipeBookCategories(ItemStack ... arritemStack) {
        this.itemIcons = ImmutableList.copyOf((Object[])arritemStack);
    }

    public static List<RecipeBookCategories> getCategories(RecipeBookType recipeBookType) {
        switch (recipeBookType) {
            case CRAFTING: {
                return CRAFTING_CATEGORIES;
            }
            case FURNACE: {
                return FURNACE_CATEGORIES;
            }
            case BLAST_FURNACE: {
                return BLAST_FURNACE_CATEGORIES;
            }
            case SMOKER: {
                return SMOKER_CATEGORIES;
            }
        }
        return ImmutableList.of();
    }

    public List<ItemStack> getIconItems() {
        return this.itemIcons;
    }

    static {
        SMOKER_CATEGORIES = ImmutableList.of((Object)((Object)SMOKER_SEARCH), (Object)((Object)SMOKER_FOOD));
        BLAST_FURNACE_CATEGORIES = ImmutableList.of((Object)((Object)BLAST_FURNACE_SEARCH), (Object)((Object)BLAST_FURNACE_BLOCKS), (Object)((Object)BLAST_FURNACE_MISC));
        FURNACE_CATEGORIES = ImmutableList.of((Object)((Object)FURNACE_SEARCH), (Object)((Object)FURNACE_FOOD), (Object)((Object)FURNACE_BLOCKS), (Object)((Object)FURNACE_MISC));
        CRAFTING_CATEGORIES = ImmutableList.of((Object)((Object)CRAFTING_SEARCH), (Object)((Object)CRAFTING_EQUIPMENT), (Object)((Object)CRAFTING_BUILDING_BLOCKS), (Object)((Object)CRAFTING_MISC), (Object)((Object)CRAFTING_REDSTONE));
        AGGREGATE_CATEGORIES = ImmutableMap.of((Object)((Object)CRAFTING_SEARCH), (Object)ImmutableList.of((Object)((Object)CRAFTING_EQUIPMENT), (Object)((Object)CRAFTING_BUILDING_BLOCKS), (Object)((Object)CRAFTING_MISC), (Object)((Object)CRAFTING_REDSTONE)), (Object)((Object)FURNACE_SEARCH), (Object)ImmutableList.of((Object)((Object)FURNACE_FOOD), (Object)((Object)FURNACE_BLOCKS), (Object)((Object)FURNACE_MISC)), (Object)((Object)BLAST_FURNACE_SEARCH), (Object)ImmutableList.of((Object)((Object)BLAST_FURNACE_BLOCKS), (Object)((Object)BLAST_FURNACE_MISC)), (Object)((Object)SMOKER_SEARCH), (Object)ImmutableList.of((Object)((Object)SMOKER_FOOD)));
    }

}

