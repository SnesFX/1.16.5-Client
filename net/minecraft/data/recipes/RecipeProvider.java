/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  com.google.common.hash.HashCode
 *  com.google.common.hash.HashFunction
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.data.recipes;

import com.google.common.collect.Sets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.EnterBlockTrigger;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.data.recipes.UpgradeRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.ArmorDyeRecipe;
import net.minecraft.world.item.crafting.BannerDuplicateRecipe;
import net.minecraft.world.item.crafting.BookCloningRecipe;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.FireworkRocketRecipe;
import net.minecraft.world.item.crafting.FireworkStarFadeRecipe;
import net.minecraft.world.item.crafting.FireworkStarRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.MapCloningRecipe;
import net.minecraft.world.item.crafting.MapExtendingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RepairItemRecipe;
import net.minecraft.world.item.crafting.ShieldDecorationRecipe;
import net.minecraft.world.item.crafting.ShulkerBoxColoring;
import net.minecraft.world.item.crafting.SimpleCookingSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.item.crafting.SuspiciousStewRecipe;
import net.minecraft.world.item.crafting.TippedArrowRecipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecipeProvider
implements DataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final DataGenerator generator;

    public RecipeProvider(DataGenerator dataGenerator) {
        this.generator = dataGenerator;
    }

    @Override
    public void run(HashCache hashCache) throws IOException {
        Path path = this.generator.getOutputFolder();
        HashSet hashSet = Sets.newHashSet();
        RecipeProvider.buildShapelessRecipes(finishedRecipe -> {
            if (!hashSet.add(finishedRecipe.getId())) {
                throw new IllegalStateException("Duplicate recipe " + finishedRecipe.getId());
            }
            RecipeProvider.saveRecipe(hashCache, finishedRecipe.serializeRecipe(), path.resolve("data/" + finishedRecipe.getId().getNamespace() + "/recipes/" + finishedRecipe.getId().getPath() + ".json"));
            JsonObject jsonObject = finishedRecipe.serializeAdvancement();
            if (jsonObject != null) {
                RecipeProvider.saveAdvancement(hashCache, jsonObject, path.resolve("data/" + finishedRecipe.getId().getNamespace() + "/advancements/" + finishedRecipe.getAdvancementId().getPath() + ".json"));
            }
        });
        RecipeProvider.saveAdvancement(hashCache, Advancement.Builder.advancement().addCriterion("impossible", new ImpossibleTrigger.TriggerInstance()).serializeToJson(), path.resolve("data/minecraft/advancements/recipes/root.json"));
    }

    private static void saveRecipe(HashCache hashCache, JsonObject jsonObject, Path path) {
        try {
            String string = GSON.toJson((JsonElement)jsonObject);
            String string2 = SHA1.hashUnencodedChars((CharSequence)string).toString();
            if (!Objects.equals(hashCache.getHash(path), string2) || !Files.exists(path, new LinkOption[0])) {
                Files.createDirectories(path.getParent(), new FileAttribute[0]);
                try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path, new OpenOption[0]);){
                    bufferedWriter.write(string);
                }
            }
            hashCache.putNew(path, string2);
        }
        catch (IOException iOException) {
            LOGGER.error("Couldn't save recipe {}", (Object)path, (Object)iOException);
        }
    }

    private static void saveAdvancement(HashCache hashCache, JsonObject jsonObject, Path path) {
        try {
            String string = GSON.toJson((JsonElement)jsonObject);
            String string2 = SHA1.hashUnencodedChars((CharSequence)string).toString();
            if (!Objects.equals(hashCache.getHash(path), string2) || !Files.exists(path, new LinkOption[0])) {
                Files.createDirectories(path.getParent(), new FileAttribute[0]);
                try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path, new OpenOption[0]);){
                    bufferedWriter.write(string);
                }
            }
            hashCache.putNew(path, string2);
        }
        catch (IOException iOException) {
            LOGGER.error("Couldn't save recipe advancement {}", (Object)path, (Object)iOException);
        }
    }

    private static void buildShapelessRecipes(Consumer<FinishedRecipe> consumer) {
        RecipeProvider.planksFromLog(consumer, Blocks.ACACIA_PLANKS, ItemTags.ACACIA_LOGS);
        RecipeProvider.planksFromLogs(consumer, Blocks.BIRCH_PLANKS, ItemTags.BIRCH_LOGS);
        RecipeProvider.planksFromLogs(consumer, Blocks.CRIMSON_PLANKS, ItemTags.CRIMSON_STEMS);
        RecipeProvider.planksFromLog(consumer, Blocks.DARK_OAK_PLANKS, ItemTags.DARK_OAK_LOGS);
        RecipeProvider.planksFromLogs(consumer, Blocks.JUNGLE_PLANKS, ItemTags.JUNGLE_LOGS);
        RecipeProvider.planksFromLogs(consumer, Blocks.OAK_PLANKS, ItemTags.OAK_LOGS);
        RecipeProvider.planksFromLogs(consumer, Blocks.SPRUCE_PLANKS, ItemTags.SPRUCE_LOGS);
        RecipeProvider.planksFromLogs(consumer, Blocks.WARPED_PLANKS, ItemTags.WARPED_STEMS);
        RecipeProvider.woodFromLogs(consumer, Blocks.ACACIA_WOOD, Blocks.ACACIA_LOG);
        RecipeProvider.woodFromLogs(consumer, Blocks.BIRCH_WOOD, Blocks.BIRCH_LOG);
        RecipeProvider.woodFromLogs(consumer, Blocks.DARK_OAK_WOOD, Blocks.DARK_OAK_LOG);
        RecipeProvider.woodFromLogs(consumer, Blocks.JUNGLE_WOOD, Blocks.JUNGLE_LOG);
        RecipeProvider.woodFromLogs(consumer, Blocks.OAK_WOOD, Blocks.OAK_LOG);
        RecipeProvider.woodFromLogs(consumer, Blocks.SPRUCE_WOOD, Blocks.SPRUCE_LOG);
        RecipeProvider.woodFromLogs(consumer, Blocks.CRIMSON_HYPHAE, Blocks.CRIMSON_STEM);
        RecipeProvider.woodFromLogs(consumer, Blocks.WARPED_HYPHAE, Blocks.WARPED_STEM);
        RecipeProvider.woodFromLogs(consumer, Blocks.STRIPPED_ACACIA_WOOD, Blocks.STRIPPED_ACACIA_LOG);
        RecipeProvider.woodFromLogs(consumer, Blocks.STRIPPED_BIRCH_WOOD, Blocks.STRIPPED_BIRCH_LOG);
        RecipeProvider.woodFromLogs(consumer, Blocks.STRIPPED_DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_LOG);
        RecipeProvider.woodFromLogs(consumer, Blocks.STRIPPED_JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_LOG);
        RecipeProvider.woodFromLogs(consumer, Blocks.STRIPPED_OAK_WOOD, Blocks.STRIPPED_OAK_LOG);
        RecipeProvider.woodFromLogs(consumer, Blocks.STRIPPED_SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_LOG);
        RecipeProvider.woodFromLogs(consumer, Blocks.STRIPPED_CRIMSON_HYPHAE, Blocks.STRIPPED_CRIMSON_STEM);
        RecipeProvider.woodFromLogs(consumer, Blocks.STRIPPED_WARPED_HYPHAE, Blocks.STRIPPED_WARPED_STEM);
        RecipeProvider.woodenBoat(consumer, Items.ACACIA_BOAT, Blocks.ACACIA_PLANKS);
        RecipeProvider.woodenBoat(consumer, Items.BIRCH_BOAT, Blocks.BIRCH_PLANKS);
        RecipeProvider.woodenBoat(consumer, Items.DARK_OAK_BOAT, Blocks.DARK_OAK_PLANKS);
        RecipeProvider.woodenBoat(consumer, Items.JUNGLE_BOAT, Blocks.JUNGLE_PLANKS);
        RecipeProvider.woodenBoat(consumer, Items.OAK_BOAT, Blocks.OAK_PLANKS);
        RecipeProvider.woodenBoat(consumer, Items.SPRUCE_BOAT, Blocks.SPRUCE_PLANKS);
        RecipeProvider.woodenButton(consumer, Blocks.ACACIA_BUTTON, Blocks.ACACIA_PLANKS);
        RecipeProvider.woodenDoor(consumer, Blocks.ACACIA_DOOR, Blocks.ACACIA_PLANKS);
        RecipeProvider.woodenFence(consumer, Blocks.ACACIA_FENCE, Blocks.ACACIA_PLANKS);
        RecipeProvider.woodenFenceGate(consumer, Blocks.ACACIA_FENCE_GATE, Blocks.ACACIA_PLANKS);
        RecipeProvider.woodenPressurePlate(consumer, Blocks.ACACIA_PRESSURE_PLATE, Blocks.ACACIA_PLANKS);
        RecipeProvider.woodenSlab(consumer, Blocks.ACACIA_SLAB, Blocks.ACACIA_PLANKS);
        RecipeProvider.woodenStairs(consumer, Blocks.ACACIA_STAIRS, Blocks.ACACIA_PLANKS);
        RecipeProvider.woodenTrapdoor(consumer, Blocks.ACACIA_TRAPDOOR, Blocks.ACACIA_PLANKS);
        RecipeProvider.woodenSign(consumer, Blocks.ACACIA_SIGN, Blocks.ACACIA_PLANKS);
        RecipeProvider.woodenButton(consumer, Blocks.BIRCH_BUTTON, Blocks.BIRCH_PLANKS);
        RecipeProvider.woodenDoor(consumer, Blocks.BIRCH_DOOR, Blocks.BIRCH_PLANKS);
        RecipeProvider.woodenFence(consumer, Blocks.BIRCH_FENCE, Blocks.BIRCH_PLANKS);
        RecipeProvider.woodenFenceGate(consumer, Blocks.BIRCH_FENCE_GATE, Blocks.BIRCH_PLANKS);
        RecipeProvider.woodenPressurePlate(consumer, Blocks.BIRCH_PRESSURE_PLATE, Blocks.BIRCH_PLANKS);
        RecipeProvider.woodenSlab(consumer, Blocks.BIRCH_SLAB, Blocks.BIRCH_PLANKS);
        RecipeProvider.woodenStairs(consumer, Blocks.BIRCH_STAIRS, Blocks.BIRCH_PLANKS);
        RecipeProvider.woodenTrapdoor(consumer, Blocks.BIRCH_TRAPDOOR, Blocks.BIRCH_PLANKS);
        RecipeProvider.woodenSign(consumer, Blocks.BIRCH_SIGN, Blocks.BIRCH_PLANKS);
        RecipeProvider.woodenButton(consumer, Blocks.CRIMSON_BUTTON, Blocks.CRIMSON_PLANKS);
        RecipeProvider.woodenDoor(consumer, Blocks.CRIMSON_DOOR, Blocks.CRIMSON_PLANKS);
        RecipeProvider.woodenFence(consumer, Blocks.CRIMSON_FENCE, Blocks.CRIMSON_PLANKS);
        RecipeProvider.woodenFenceGate(consumer, Blocks.CRIMSON_FENCE_GATE, Blocks.CRIMSON_PLANKS);
        RecipeProvider.woodenPressurePlate(consumer, Blocks.CRIMSON_PRESSURE_PLATE, Blocks.CRIMSON_PLANKS);
        RecipeProvider.woodenSlab(consumer, Blocks.CRIMSON_SLAB, Blocks.CRIMSON_PLANKS);
        RecipeProvider.woodenStairs(consumer, Blocks.CRIMSON_STAIRS, Blocks.CRIMSON_PLANKS);
        RecipeProvider.woodenTrapdoor(consumer, Blocks.CRIMSON_TRAPDOOR, Blocks.CRIMSON_PLANKS);
        RecipeProvider.woodenSign(consumer, Blocks.CRIMSON_SIGN, Blocks.CRIMSON_PLANKS);
        RecipeProvider.woodenButton(consumer, Blocks.DARK_OAK_BUTTON, Blocks.DARK_OAK_PLANKS);
        RecipeProvider.woodenDoor(consumer, Blocks.DARK_OAK_DOOR, Blocks.DARK_OAK_PLANKS);
        RecipeProvider.woodenFence(consumer, Blocks.DARK_OAK_FENCE, Blocks.DARK_OAK_PLANKS);
        RecipeProvider.woodenFenceGate(consumer, Blocks.DARK_OAK_FENCE_GATE, Blocks.DARK_OAK_PLANKS);
        RecipeProvider.woodenPressurePlate(consumer, Blocks.DARK_OAK_PRESSURE_PLATE, Blocks.DARK_OAK_PLANKS);
        RecipeProvider.woodenSlab(consumer, Blocks.DARK_OAK_SLAB, Blocks.DARK_OAK_PLANKS);
        RecipeProvider.woodenStairs(consumer, Blocks.DARK_OAK_STAIRS, Blocks.DARK_OAK_PLANKS);
        RecipeProvider.woodenTrapdoor(consumer, Blocks.DARK_OAK_TRAPDOOR, Blocks.DARK_OAK_PLANKS);
        RecipeProvider.woodenSign(consumer, Blocks.DARK_OAK_SIGN, Blocks.DARK_OAK_PLANKS);
        RecipeProvider.woodenButton(consumer, Blocks.JUNGLE_BUTTON, Blocks.JUNGLE_PLANKS);
        RecipeProvider.woodenDoor(consumer, Blocks.JUNGLE_DOOR, Blocks.JUNGLE_PLANKS);
        RecipeProvider.woodenFence(consumer, Blocks.JUNGLE_FENCE, Blocks.JUNGLE_PLANKS);
        RecipeProvider.woodenFenceGate(consumer, Blocks.JUNGLE_FENCE_GATE, Blocks.JUNGLE_PLANKS);
        RecipeProvider.woodenPressurePlate(consumer, Blocks.JUNGLE_PRESSURE_PLATE, Blocks.JUNGLE_PLANKS);
        RecipeProvider.woodenSlab(consumer, Blocks.JUNGLE_SLAB, Blocks.JUNGLE_PLANKS);
        RecipeProvider.woodenStairs(consumer, Blocks.JUNGLE_STAIRS, Blocks.JUNGLE_PLANKS);
        RecipeProvider.woodenTrapdoor(consumer, Blocks.JUNGLE_TRAPDOOR, Blocks.JUNGLE_PLANKS);
        RecipeProvider.woodenSign(consumer, Blocks.JUNGLE_SIGN, Blocks.JUNGLE_PLANKS);
        RecipeProvider.woodenButton(consumer, Blocks.OAK_BUTTON, Blocks.OAK_PLANKS);
        RecipeProvider.woodenDoor(consumer, Blocks.OAK_DOOR, Blocks.OAK_PLANKS);
        RecipeProvider.woodenFence(consumer, Blocks.OAK_FENCE, Blocks.OAK_PLANKS);
        RecipeProvider.woodenFenceGate(consumer, Blocks.OAK_FENCE_GATE, Blocks.OAK_PLANKS);
        RecipeProvider.woodenPressurePlate(consumer, Blocks.OAK_PRESSURE_PLATE, Blocks.OAK_PLANKS);
        RecipeProvider.woodenSlab(consumer, Blocks.OAK_SLAB, Blocks.OAK_PLANKS);
        RecipeProvider.woodenStairs(consumer, Blocks.OAK_STAIRS, Blocks.OAK_PLANKS);
        RecipeProvider.woodenTrapdoor(consumer, Blocks.OAK_TRAPDOOR, Blocks.OAK_PLANKS);
        RecipeProvider.woodenSign(consumer, Blocks.OAK_SIGN, Blocks.OAK_PLANKS);
        RecipeProvider.woodenButton(consumer, Blocks.SPRUCE_BUTTON, Blocks.SPRUCE_PLANKS);
        RecipeProvider.woodenDoor(consumer, Blocks.SPRUCE_DOOR, Blocks.SPRUCE_PLANKS);
        RecipeProvider.woodenFence(consumer, Blocks.SPRUCE_FENCE, Blocks.SPRUCE_PLANKS);
        RecipeProvider.woodenFenceGate(consumer, Blocks.SPRUCE_FENCE_GATE, Blocks.SPRUCE_PLANKS);
        RecipeProvider.woodenPressurePlate(consumer, Blocks.SPRUCE_PRESSURE_PLATE, Blocks.SPRUCE_PLANKS);
        RecipeProvider.woodenSlab(consumer, Blocks.SPRUCE_SLAB, Blocks.SPRUCE_PLANKS);
        RecipeProvider.woodenStairs(consumer, Blocks.SPRUCE_STAIRS, Blocks.SPRUCE_PLANKS);
        RecipeProvider.woodenTrapdoor(consumer, Blocks.SPRUCE_TRAPDOOR, Blocks.SPRUCE_PLANKS);
        RecipeProvider.woodenSign(consumer, Blocks.SPRUCE_SIGN, Blocks.SPRUCE_PLANKS);
        RecipeProvider.woodenButton(consumer, Blocks.WARPED_BUTTON, Blocks.WARPED_PLANKS);
        RecipeProvider.woodenDoor(consumer, Blocks.WARPED_DOOR, Blocks.WARPED_PLANKS);
        RecipeProvider.woodenFence(consumer, Blocks.WARPED_FENCE, Blocks.WARPED_PLANKS);
        RecipeProvider.woodenFenceGate(consumer, Blocks.WARPED_FENCE_GATE, Blocks.WARPED_PLANKS);
        RecipeProvider.woodenPressurePlate(consumer, Blocks.WARPED_PRESSURE_PLATE, Blocks.WARPED_PLANKS);
        RecipeProvider.woodenSlab(consumer, Blocks.WARPED_SLAB, Blocks.WARPED_PLANKS);
        RecipeProvider.woodenStairs(consumer, Blocks.WARPED_STAIRS, Blocks.WARPED_PLANKS);
        RecipeProvider.woodenTrapdoor(consumer, Blocks.WARPED_TRAPDOOR, Blocks.WARPED_PLANKS);
        RecipeProvider.woodenSign(consumer, Blocks.WARPED_SIGN, Blocks.WARPED_PLANKS);
        RecipeProvider.coloredWoolFromWhiteWoolAndDye(consumer, Blocks.BLACK_WOOL, Items.BLACK_DYE);
        RecipeProvider.carpetFromWool(consumer, Blocks.BLACK_CARPET, Blocks.BLACK_WOOL);
        RecipeProvider.coloredCarpetFromWhiteCarpetAndDye(consumer, Blocks.BLACK_CARPET, Items.BLACK_DYE);
        RecipeProvider.bedFromPlanksAndWool(consumer, Items.BLACK_BED, Blocks.BLACK_WOOL);
        RecipeProvider.bedFromWhiteBedAndDye(consumer, Items.BLACK_BED, Items.BLACK_DYE);
        RecipeProvider.banner(consumer, Items.BLACK_BANNER, Blocks.BLACK_WOOL);
        RecipeProvider.coloredWoolFromWhiteWoolAndDye(consumer, Blocks.BLUE_WOOL, Items.BLUE_DYE);
        RecipeProvider.carpetFromWool(consumer, Blocks.BLUE_CARPET, Blocks.BLUE_WOOL);
        RecipeProvider.coloredCarpetFromWhiteCarpetAndDye(consumer, Blocks.BLUE_CARPET, Items.BLUE_DYE);
        RecipeProvider.bedFromPlanksAndWool(consumer, Items.BLUE_BED, Blocks.BLUE_WOOL);
        RecipeProvider.bedFromWhiteBedAndDye(consumer, Items.BLUE_BED, Items.BLUE_DYE);
        RecipeProvider.banner(consumer, Items.BLUE_BANNER, Blocks.BLUE_WOOL);
        RecipeProvider.coloredWoolFromWhiteWoolAndDye(consumer, Blocks.BROWN_WOOL, Items.BROWN_DYE);
        RecipeProvider.carpetFromWool(consumer, Blocks.BROWN_CARPET, Blocks.BROWN_WOOL);
        RecipeProvider.coloredCarpetFromWhiteCarpetAndDye(consumer, Blocks.BROWN_CARPET, Items.BROWN_DYE);
        RecipeProvider.bedFromPlanksAndWool(consumer, Items.BROWN_BED, Blocks.BROWN_WOOL);
        RecipeProvider.bedFromWhiteBedAndDye(consumer, Items.BROWN_BED, Items.BROWN_DYE);
        RecipeProvider.banner(consumer, Items.BROWN_BANNER, Blocks.BROWN_WOOL);
        RecipeProvider.coloredWoolFromWhiteWoolAndDye(consumer, Blocks.CYAN_WOOL, Items.CYAN_DYE);
        RecipeProvider.carpetFromWool(consumer, Blocks.CYAN_CARPET, Blocks.CYAN_WOOL);
        RecipeProvider.coloredCarpetFromWhiteCarpetAndDye(consumer, Blocks.CYAN_CARPET, Items.CYAN_DYE);
        RecipeProvider.bedFromPlanksAndWool(consumer, Items.CYAN_BED, Blocks.CYAN_WOOL);
        RecipeProvider.bedFromWhiteBedAndDye(consumer, Items.CYAN_BED, Items.CYAN_DYE);
        RecipeProvider.banner(consumer, Items.CYAN_BANNER, Blocks.CYAN_WOOL);
        RecipeProvider.coloredWoolFromWhiteWoolAndDye(consumer, Blocks.GRAY_WOOL, Items.GRAY_DYE);
        RecipeProvider.carpetFromWool(consumer, Blocks.GRAY_CARPET, Blocks.GRAY_WOOL);
        RecipeProvider.coloredCarpetFromWhiteCarpetAndDye(consumer, Blocks.GRAY_CARPET, Items.GRAY_DYE);
        RecipeProvider.bedFromPlanksAndWool(consumer, Items.GRAY_BED, Blocks.GRAY_WOOL);
        RecipeProvider.bedFromWhiteBedAndDye(consumer, Items.GRAY_BED, Items.GRAY_DYE);
        RecipeProvider.banner(consumer, Items.GRAY_BANNER, Blocks.GRAY_WOOL);
        RecipeProvider.coloredWoolFromWhiteWoolAndDye(consumer, Blocks.GREEN_WOOL, Items.GREEN_DYE);
        RecipeProvider.carpetFromWool(consumer, Blocks.GREEN_CARPET, Blocks.GREEN_WOOL);
        RecipeProvider.coloredCarpetFromWhiteCarpetAndDye(consumer, Blocks.GREEN_CARPET, Items.GREEN_DYE);
        RecipeProvider.bedFromPlanksAndWool(consumer, Items.GREEN_BED, Blocks.GREEN_WOOL);
        RecipeProvider.bedFromWhiteBedAndDye(consumer, Items.GREEN_BED, Items.GREEN_DYE);
        RecipeProvider.banner(consumer, Items.GREEN_BANNER, Blocks.GREEN_WOOL);
        RecipeProvider.coloredWoolFromWhiteWoolAndDye(consumer, Blocks.LIGHT_BLUE_WOOL, Items.LIGHT_BLUE_DYE);
        RecipeProvider.carpetFromWool(consumer, Blocks.LIGHT_BLUE_CARPET, Blocks.LIGHT_BLUE_WOOL);
        RecipeProvider.coloredCarpetFromWhiteCarpetAndDye(consumer, Blocks.LIGHT_BLUE_CARPET, Items.LIGHT_BLUE_DYE);
        RecipeProvider.bedFromPlanksAndWool(consumer, Items.LIGHT_BLUE_BED, Blocks.LIGHT_BLUE_WOOL);
        RecipeProvider.bedFromWhiteBedAndDye(consumer, Items.LIGHT_BLUE_BED, Items.LIGHT_BLUE_DYE);
        RecipeProvider.banner(consumer, Items.LIGHT_BLUE_BANNER, Blocks.LIGHT_BLUE_WOOL);
        RecipeProvider.coloredWoolFromWhiteWoolAndDye(consumer, Blocks.LIGHT_GRAY_WOOL, Items.LIGHT_GRAY_DYE);
        RecipeProvider.carpetFromWool(consumer, Blocks.LIGHT_GRAY_CARPET, Blocks.LIGHT_GRAY_WOOL);
        RecipeProvider.coloredCarpetFromWhiteCarpetAndDye(consumer, Blocks.LIGHT_GRAY_CARPET, Items.LIGHT_GRAY_DYE);
        RecipeProvider.bedFromPlanksAndWool(consumer, Items.LIGHT_GRAY_BED, Blocks.LIGHT_GRAY_WOOL);
        RecipeProvider.bedFromWhiteBedAndDye(consumer, Items.LIGHT_GRAY_BED, Items.LIGHT_GRAY_DYE);
        RecipeProvider.banner(consumer, Items.LIGHT_GRAY_BANNER, Blocks.LIGHT_GRAY_WOOL);
        RecipeProvider.coloredWoolFromWhiteWoolAndDye(consumer, Blocks.LIME_WOOL, Items.LIME_DYE);
        RecipeProvider.carpetFromWool(consumer, Blocks.LIME_CARPET, Blocks.LIME_WOOL);
        RecipeProvider.coloredCarpetFromWhiteCarpetAndDye(consumer, Blocks.LIME_CARPET, Items.LIME_DYE);
        RecipeProvider.bedFromPlanksAndWool(consumer, Items.LIME_BED, Blocks.LIME_WOOL);
        RecipeProvider.bedFromWhiteBedAndDye(consumer, Items.LIME_BED, Items.LIME_DYE);
        RecipeProvider.banner(consumer, Items.LIME_BANNER, Blocks.LIME_WOOL);
        RecipeProvider.coloredWoolFromWhiteWoolAndDye(consumer, Blocks.MAGENTA_WOOL, Items.MAGENTA_DYE);
        RecipeProvider.carpetFromWool(consumer, Blocks.MAGENTA_CARPET, Blocks.MAGENTA_WOOL);
        RecipeProvider.coloredCarpetFromWhiteCarpetAndDye(consumer, Blocks.MAGENTA_CARPET, Items.MAGENTA_DYE);
        RecipeProvider.bedFromPlanksAndWool(consumer, Items.MAGENTA_BED, Blocks.MAGENTA_WOOL);
        RecipeProvider.bedFromWhiteBedAndDye(consumer, Items.MAGENTA_BED, Items.MAGENTA_DYE);
        RecipeProvider.banner(consumer, Items.MAGENTA_BANNER, Blocks.MAGENTA_WOOL);
        RecipeProvider.coloredWoolFromWhiteWoolAndDye(consumer, Blocks.ORANGE_WOOL, Items.ORANGE_DYE);
        RecipeProvider.carpetFromWool(consumer, Blocks.ORANGE_CARPET, Blocks.ORANGE_WOOL);
        RecipeProvider.coloredCarpetFromWhiteCarpetAndDye(consumer, Blocks.ORANGE_CARPET, Items.ORANGE_DYE);
        RecipeProvider.bedFromPlanksAndWool(consumer, Items.ORANGE_BED, Blocks.ORANGE_WOOL);
        RecipeProvider.bedFromWhiteBedAndDye(consumer, Items.ORANGE_BED, Items.ORANGE_DYE);
        RecipeProvider.banner(consumer, Items.ORANGE_BANNER, Blocks.ORANGE_WOOL);
        RecipeProvider.coloredWoolFromWhiteWoolAndDye(consumer, Blocks.PINK_WOOL, Items.PINK_DYE);
        RecipeProvider.carpetFromWool(consumer, Blocks.PINK_CARPET, Blocks.PINK_WOOL);
        RecipeProvider.coloredCarpetFromWhiteCarpetAndDye(consumer, Blocks.PINK_CARPET, Items.PINK_DYE);
        RecipeProvider.bedFromPlanksAndWool(consumer, Items.PINK_BED, Blocks.PINK_WOOL);
        RecipeProvider.bedFromWhiteBedAndDye(consumer, Items.PINK_BED, Items.PINK_DYE);
        RecipeProvider.banner(consumer, Items.PINK_BANNER, Blocks.PINK_WOOL);
        RecipeProvider.coloredWoolFromWhiteWoolAndDye(consumer, Blocks.PURPLE_WOOL, Items.PURPLE_DYE);
        RecipeProvider.carpetFromWool(consumer, Blocks.PURPLE_CARPET, Blocks.PURPLE_WOOL);
        RecipeProvider.coloredCarpetFromWhiteCarpetAndDye(consumer, Blocks.PURPLE_CARPET, Items.PURPLE_DYE);
        RecipeProvider.bedFromPlanksAndWool(consumer, Items.PURPLE_BED, Blocks.PURPLE_WOOL);
        RecipeProvider.bedFromWhiteBedAndDye(consumer, Items.PURPLE_BED, Items.PURPLE_DYE);
        RecipeProvider.banner(consumer, Items.PURPLE_BANNER, Blocks.PURPLE_WOOL);
        RecipeProvider.coloredWoolFromWhiteWoolAndDye(consumer, Blocks.RED_WOOL, Items.RED_DYE);
        RecipeProvider.carpetFromWool(consumer, Blocks.RED_CARPET, Blocks.RED_WOOL);
        RecipeProvider.coloredCarpetFromWhiteCarpetAndDye(consumer, Blocks.RED_CARPET, Items.RED_DYE);
        RecipeProvider.bedFromPlanksAndWool(consumer, Items.RED_BED, Blocks.RED_WOOL);
        RecipeProvider.bedFromWhiteBedAndDye(consumer, Items.RED_BED, Items.RED_DYE);
        RecipeProvider.banner(consumer, Items.RED_BANNER, Blocks.RED_WOOL);
        RecipeProvider.carpetFromWool(consumer, Blocks.WHITE_CARPET, Blocks.WHITE_WOOL);
        RecipeProvider.bedFromPlanksAndWool(consumer, Items.WHITE_BED, Blocks.WHITE_WOOL);
        RecipeProvider.banner(consumer, Items.WHITE_BANNER, Blocks.WHITE_WOOL);
        RecipeProvider.coloredWoolFromWhiteWoolAndDye(consumer, Blocks.YELLOW_WOOL, Items.YELLOW_DYE);
        RecipeProvider.carpetFromWool(consumer, Blocks.YELLOW_CARPET, Blocks.YELLOW_WOOL);
        RecipeProvider.coloredCarpetFromWhiteCarpetAndDye(consumer, Blocks.YELLOW_CARPET, Items.YELLOW_DYE);
        RecipeProvider.bedFromPlanksAndWool(consumer, Items.YELLOW_BED, Blocks.YELLOW_WOOL);
        RecipeProvider.bedFromWhiteBedAndDye(consumer, Items.YELLOW_BED, Items.YELLOW_DYE);
        RecipeProvider.banner(consumer, Items.YELLOW_BANNER, Blocks.YELLOW_WOOL);
        RecipeProvider.stainedGlassFromGlassAndDye(consumer, Blocks.BLACK_STAINED_GLASS, Items.BLACK_DYE);
        RecipeProvider.stainedGlassPaneFromStainedGlass(consumer, Blocks.BLACK_STAINED_GLASS_PANE, Blocks.BLACK_STAINED_GLASS);
        RecipeProvider.stainedGlassPaneFromGlassPaneAndDye(consumer, Blocks.BLACK_STAINED_GLASS_PANE, Items.BLACK_DYE);
        RecipeProvider.stainedGlassFromGlassAndDye(consumer, Blocks.BLUE_STAINED_GLASS, Items.BLUE_DYE);
        RecipeProvider.stainedGlassPaneFromStainedGlass(consumer, Blocks.BLUE_STAINED_GLASS_PANE, Blocks.BLUE_STAINED_GLASS);
        RecipeProvider.stainedGlassPaneFromGlassPaneAndDye(consumer, Blocks.BLUE_STAINED_GLASS_PANE, Items.BLUE_DYE);
        RecipeProvider.stainedGlassFromGlassAndDye(consumer, Blocks.BROWN_STAINED_GLASS, Items.BROWN_DYE);
        RecipeProvider.stainedGlassPaneFromStainedGlass(consumer, Blocks.BROWN_STAINED_GLASS_PANE, Blocks.BROWN_STAINED_GLASS);
        RecipeProvider.stainedGlassPaneFromGlassPaneAndDye(consumer, Blocks.BROWN_STAINED_GLASS_PANE, Items.BROWN_DYE);
        RecipeProvider.stainedGlassFromGlassAndDye(consumer, Blocks.CYAN_STAINED_GLASS, Items.CYAN_DYE);
        RecipeProvider.stainedGlassPaneFromStainedGlass(consumer, Blocks.CYAN_STAINED_GLASS_PANE, Blocks.CYAN_STAINED_GLASS);
        RecipeProvider.stainedGlassPaneFromGlassPaneAndDye(consumer, Blocks.CYAN_STAINED_GLASS_PANE, Items.CYAN_DYE);
        RecipeProvider.stainedGlassFromGlassAndDye(consumer, Blocks.GRAY_STAINED_GLASS, Items.GRAY_DYE);
        RecipeProvider.stainedGlassPaneFromStainedGlass(consumer, Blocks.GRAY_STAINED_GLASS_PANE, Blocks.GRAY_STAINED_GLASS);
        RecipeProvider.stainedGlassPaneFromGlassPaneAndDye(consumer, Blocks.GRAY_STAINED_GLASS_PANE, Items.GRAY_DYE);
        RecipeProvider.stainedGlassFromGlassAndDye(consumer, Blocks.GREEN_STAINED_GLASS, Items.GREEN_DYE);
        RecipeProvider.stainedGlassPaneFromStainedGlass(consumer, Blocks.GREEN_STAINED_GLASS_PANE, Blocks.GREEN_STAINED_GLASS);
        RecipeProvider.stainedGlassPaneFromGlassPaneAndDye(consumer, Blocks.GREEN_STAINED_GLASS_PANE, Items.GREEN_DYE);
        RecipeProvider.stainedGlassFromGlassAndDye(consumer, Blocks.LIGHT_BLUE_STAINED_GLASS, Items.LIGHT_BLUE_DYE);
        RecipeProvider.stainedGlassPaneFromStainedGlass(consumer, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, Blocks.LIGHT_BLUE_STAINED_GLASS);
        RecipeProvider.stainedGlassPaneFromGlassPaneAndDye(consumer, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, Items.LIGHT_BLUE_DYE);
        RecipeProvider.stainedGlassFromGlassAndDye(consumer, Blocks.LIGHT_GRAY_STAINED_GLASS, Items.LIGHT_GRAY_DYE);
        RecipeProvider.stainedGlassPaneFromStainedGlass(consumer, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, Blocks.LIGHT_GRAY_STAINED_GLASS);
        RecipeProvider.stainedGlassPaneFromGlassPaneAndDye(consumer, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, Items.LIGHT_GRAY_DYE);
        RecipeProvider.stainedGlassFromGlassAndDye(consumer, Blocks.LIME_STAINED_GLASS, Items.LIME_DYE);
        RecipeProvider.stainedGlassPaneFromStainedGlass(consumer, Blocks.LIME_STAINED_GLASS_PANE, Blocks.LIME_STAINED_GLASS);
        RecipeProvider.stainedGlassPaneFromGlassPaneAndDye(consumer, Blocks.LIME_STAINED_GLASS_PANE, Items.LIME_DYE);
        RecipeProvider.stainedGlassFromGlassAndDye(consumer, Blocks.MAGENTA_STAINED_GLASS, Items.MAGENTA_DYE);
        RecipeProvider.stainedGlassPaneFromStainedGlass(consumer, Blocks.MAGENTA_STAINED_GLASS_PANE, Blocks.MAGENTA_STAINED_GLASS);
        RecipeProvider.stainedGlassPaneFromGlassPaneAndDye(consumer, Blocks.MAGENTA_STAINED_GLASS_PANE, Items.MAGENTA_DYE);
        RecipeProvider.stainedGlassFromGlassAndDye(consumer, Blocks.ORANGE_STAINED_GLASS, Items.ORANGE_DYE);
        RecipeProvider.stainedGlassPaneFromStainedGlass(consumer, Blocks.ORANGE_STAINED_GLASS_PANE, Blocks.ORANGE_STAINED_GLASS);
        RecipeProvider.stainedGlassPaneFromGlassPaneAndDye(consumer, Blocks.ORANGE_STAINED_GLASS_PANE, Items.ORANGE_DYE);
        RecipeProvider.stainedGlassFromGlassAndDye(consumer, Blocks.PINK_STAINED_GLASS, Items.PINK_DYE);
        RecipeProvider.stainedGlassPaneFromStainedGlass(consumer, Blocks.PINK_STAINED_GLASS_PANE, Blocks.PINK_STAINED_GLASS);
        RecipeProvider.stainedGlassPaneFromGlassPaneAndDye(consumer, Blocks.PINK_STAINED_GLASS_PANE, Items.PINK_DYE);
        RecipeProvider.stainedGlassFromGlassAndDye(consumer, Blocks.PURPLE_STAINED_GLASS, Items.PURPLE_DYE);
        RecipeProvider.stainedGlassPaneFromStainedGlass(consumer, Blocks.PURPLE_STAINED_GLASS_PANE, Blocks.PURPLE_STAINED_GLASS);
        RecipeProvider.stainedGlassPaneFromGlassPaneAndDye(consumer, Blocks.PURPLE_STAINED_GLASS_PANE, Items.PURPLE_DYE);
        RecipeProvider.stainedGlassFromGlassAndDye(consumer, Blocks.RED_STAINED_GLASS, Items.RED_DYE);
        RecipeProvider.stainedGlassPaneFromStainedGlass(consumer, Blocks.RED_STAINED_GLASS_PANE, Blocks.RED_STAINED_GLASS);
        RecipeProvider.stainedGlassPaneFromGlassPaneAndDye(consumer, Blocks.RED_STAINED_GLASS_PANE, Items.RED_DYE);
        RecipeProvider.stainedGlassFromGlassAndDye(consumer, Blocks.WHITE_STAINED_GLASS, Items.WHITE_DYE);
        RecipeProvider.stainedGlassPaneFromStainedGlass(consumer, Blocks.WHITE_STAINED_GLASS_PANE, Blocks.WHITE_STAINED_GLASS);
        RecipeProvider.stainedGlassPaneFromGlassPaneAndDye(consumer, Blocks.WHITE_STAINED_GLASS_PANE, Items.WHITE_DYE);
        RecipeProvider.stainedGlassFromGlassAndDye(consumer, Blocks.YELLOW_STAINED_GLASS, Items.YELLOW_DYE);
        RecipeProvider.stainedGlassPaneFromStainedGlass(consumer, Blocks.YELLOW_STAINED_GLASS_PANE, Blocks.YELLOW_STAINED_GLASS);
        RecipeProvider.stainedGlassPaneFromGlassPaneAndDye(consumer, Blocks.YELLOW_STAINED_GLASS_PANE, Items.YELLOW_DYE);
        RecipeProvider.coloredTerracottaFromTerracottaAndDye(consumer, Blocks.BLACK_TERRACOTTA, Items.BLACK_DYE);
        RecipeProvider.coloredTerracottaFromTerracottaAndDye(consumer, Blocks.BLUE_TERRACOTTA, Items.BLUE_DYE);
        RecipeProvider.coloredTerracottaFromTerracottaAndDye(consumer, Blocks.BROWN_TERRACOTTA, Items.BROWN_DYE);
        RecipeProvider.coloredTerracottaFromTerracottaAndDye(consumer, Blocks.CYAN_TERRACOTTA, Items.CYAN_DYE);
        RecipeProvider.coloredTerracottaFromTerracottaAndDye(consumer, Blocks.GRAY_TERRACOTTA, Items.GRAY_DYE);
        RecipeProvider.coloredTerracottaFromTerracottaAndDye(consumer, Blocks.GREEN_TERRACOTTA, Items.GREEN_DYE);
        RecipeProvider.coloredTerracottaFromTerracottaAndDye(consumer, Blocks.LIGHT_BLUE_TERRACOTTA, Items.LIGHT_BLUE_DYE);
        RecipeProvider.coloredTerracottaFromTerracottaAndDye(consumer, Blocks.LIGHT_GRAY_TERRACOTTA, Items.LIGHT_GRAY_DYE);
        RecipeProvider.coloredTerracottaFromTerracottaAndDye(consumer, Blocks.LIME_TERRACOTTA, Items.LIME_DYE);
        RecipeProvider.coloredTerracottaFromTerracottaAndDye(consumer, Blocks.MAGENTA_TERRACOTTA, Items.MAGENTA_DYE);
        RecipeProvider.coloredTerracottaFromTerracottaAndDye(consumer, Blocks.ORANGE_TERRACOTTA, Items.ORANGE_DYE);
        RecipeProvider.coloredTerracottaFromTerracottaAndDye(consumer, Blocks.PINK_TERRACOTTA, Items.PINK_DYE);
        RecipeProvider.coloredTerracottaFromTerracottaAndDye(consumer, Blocks.PURPLE_TERRACOTTA, Items.PURPLE_DYE);
        RecipeProvider.coloredTerracottaFromTerracottaAndDye(consumer, Blocks.RED_TERRACOTTA, Items.RED_DYE);
        RecipeProvider.coloredTerracottaFromTerracottaAndDye(consumer, Blocks.WHITE_TERRACOTTA, Items.WHITE_DYE);
        RecipeProvider.coloredTerracottaFromTerracottaAndDye(consumer, Blocks.YELLOW_TERRACOTTA, Items.YELLOW_DYE);
        RecipeProvider.concretePowder(consumer, Blocks.BLACK_CONCRETE_POWDER, Items.BLACK_DYE);
        RecipeProvider.concretePowder(consumer, Blocks.BLUE_CONCRETE_POWDER, Items.BLUE_DYE);
        RecipeProvider.concretePowder(consumer, Blocks.BROWN_CONCRETE_POWDER, Items.BROWN_DYE);
        RecipeProvider.concretePowder(consumer, Blocks.CYAN_CONCRETE_POWDER, Items.CYAN_DYE);
        RecipeProvider.concretePowder(consumer, Blocks.GRAY_CONCRETE_POWDER, Items.GRAY_DYE);
        RecipeProvider.concretePowder(consumer, Blocks.GREEN_CONCRETE_POWDER, Items.GREEN_DYE);
        RecipeProvider.concretePowder(consumer, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Items.LIGHT_BLUE_DYE);
        RecipeProvider.concretePowder(consumer, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Items.LIGHT_GRAY_DYE);
        RecipeProvider.concretePowder(consumer, Blocks.LIME_CONCRETE_POWDER, Items.LIME_DYE);
        RecipeProvider.concretePowder(consumer, Blocks.MAGENTA_CONCRETE_POWDER, Items.MAGENTA_DYE);
        RecipeProvider.concretePowder(consumer, Blocks.ORANGE_CONCRETE_POWDER, Items.ORANGE_DYE);
        RecipeProvider.concretePowder(consumer, Blocks.PINK_CONCRETE_POWDER, Items.PINK_DYE);
        RecipeProvider.concretePowder(consumer, Blocks.PURPLE_CONCRETE_POWDER, Items.PURPLE_DYE);
        RecipeProvider.concretePowder(consumer, Blocks.RED_CONCRETE_POWDER, Items.RED_DYE);
        RecipeProvider.concretePowder(consumer, Blocks.WHITE_CONCRETE_POWDER, Items.WHITE_DYE);
        RecipeProvider.concretePowder(consumer, Blocks.YELLOW_CONCRETE_POWDER, Items.YELLOW_DYE);
        ShapedRecipeBuilder.shaped(Blocks.ACTIVATOR_RAIL, 6).define(Character.valueOf('#'), Blocks.REDSTONE_TORCH).define(Character.valueOf('S'), Items.STICK).define(Character.valueOf('X'), Items.IRON_INGOT).pattern("XSX").pattern("X#X").pattern("XSX").unlockedBy("has_rail", RecipeProvider.has(Blocks.RAIL)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Blocks.ANDESITE, 2).requires(Blocks.DIORITE).requires(Blocks.COBBLESTONE).unlockedBy("has_stone", RecipeProvider.has(Blocks.DIORITE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.ANVIL).define(Character.valueOf('I'), Blocks.IRON_BLOCK).define(Character.valueOf('i'), Items.IRON_INGOT).pattern("III").pattern(" i ").pattern("iii").unlockedBy("has_iron_block", RecipeProvider.has(Blocks.IRON_BLOCK)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.ARMOR_STAND).define(Character.valueOf('/'), Items.STICK).define(Character.valueOf('_'), Blocks.SMOOTH_STONE_SLAB).pattern("///").pattern(" / ").pattern("/_/").unlockedBy("has_stone_slab", RecipeProvider.has(Blocks.SMOOTH_STONE_SLAB)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.ARROW, 4).define(Character.valueOf('#'), Items.STICK).define(Character.valueOf('X'), Items.FLINT).define(Character.valueOf('Y'), Items.FEATHER).pattern("X").pattern("#").pattern("Y").unlockedBy("has_feather", RecipeProvider.has(Items.FEATHER)).unlockedBy("has_flint", RecipeProvider.has(Items.FLINT)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.BARREL, 1).define(Character.valueOf('P'), ItemTags.PLANKS).define(Character.valueOf('S'), ItemTags.WOODEN_SLABS).pattern("PSP").pattern("P P").pattern("PSP").unlockedBy("has_planks", RecipeProvider.has(ItemTags.PLANKS)).unlockedBy("has_wood_slab", RecipeProvider.has(ItemTags.WOODEN_SLABS)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.BEACON).define(Character.valueOf('S'), Items.NETHER_STAR).define(Character.valueOf('G'), Blocks.GLASS).define(Character.valueOf('O'), Blocks.OBSIDIAN).pattern("GGG").pattern("GSG").pattern("OOO").unlockedBy("has_nether_star", RecipeProvider.has(Items.NETHER_STAR)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.BEEHIVE).define(Character.valueOf('P'), ItemTags.PLANKS).define(Character.valueOf('H'), Items.HONEYCOMB).pattern("PPP").pattern("HHH").pattern("PPP").unlockedBy("has_honeycomb", RecipeProvider.has(Items.HONEYCOMB)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.BEETROOT_SOUP).requires(Items.BOWL).requires(Items.BEETROOT, 6).unlockedBy("has_beetroot", RecipeProvider.has(Items.BEETROOT)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.BLACK_DYE).requires(Items.INK_SAC).group("black_dye").unlockedBy("has_ink_sac", RecipeProvider.has(Items.INK_SAC)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.BLACK_DYE).requires(Blocks.WITHER_ROSE).group("black_dye").unlockedBy("has_black_flower", RecipeProvider.has(Blocks.WITHER_ROSE)).save(consumer, "black_dye_from_wither_rose");
        ShapelessRecipeBuilder.shapeless(Items.BLAZE_POWDER, 2).requires(Items.BLAZE_ROD).unlockedBy("has_blaze_rod", RecipeProvider.has(Items.BLAZE_ROD)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.BLUE_DYE).requires(Items.LAPIS_LAZULI).group("blue_dye").unlockedBy("has_lapis_lazuli", RecipeProvider.has(Items.LAPIS_LAZULI)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.BLUE_DYE).requires(Blocks.CORNFLOWER).group("blue_dye").unlockedBy("has_blue_flower", RecipeProvider.has(Blocks.CORNFLOWER)).save(consumer, "blue_dye_from_cornflower");
        ShapedRecipeBuilder.shaped(Blocks.BLUE_ICE).define(Character.valueOf('#'), Blocks.PACKED_ICE).pattern("###").pattern("###").pattern("###").unlockedBy("has_packed_ice", RecipeProvider.has(Blocks.PACKED_ICE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.BONE_BLOCK).define(Character.valueOf('X'), Items.BONE_MEAL).pattern("XXX").pattern("XXX").pattern("XXX").unlockedBy("has_bonemeal", RecipeProvider.has(Items.BONE_MEAL)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.BONE_MEAL, 3).requires(Items.BONE).group("bonemeal").unlockedBy("has_bone", RecipeProvider.has(Items.BONE)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.BONE_MEAL, 9).requires(Blocks.BONE_BLOCK).group("bonemeal").unlockedBy("has_bone_block", RecipeProvider.has(Blocks.BONE_BLOCK)).save(consumer, "bone_meal_from_bone_block");
        ShapelessRecipeBuilder.shapeless(Items.BOOK).requires(Items.PAPER, 3).requires(Items.LEATHER).unlockedBy("has_paper", RecipeProvider.has(Items.PAPER)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.BOOKSHELF).define(Character.valueOf('#'), ItemTags.PLANKS).define(Character.valueOf('X'), Items.BOOK).pattern("###").pattern("XXX").pattern("###").unlockedBy("has_book", RecipeProvider.has(Items.BOOK)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.BOW).define(Character.valueOf('#'), Items.STICK).define(Character.valueOf('X'), Items.STRING).pattern(" #X").pattern("# X").pattern(" #X").unlockedBy("has_string", RecipeProvider.has(Items.STRING)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.BOWL, 4).define(Character.valueOf('#'), ItemTags.PLANKS).pattern("# #").pattern(" # ").unlockedBy("has_brown_mushroom", RecipeProvider.has(Blocks.BROWN_MUSHROOM)).unlockedBy("has_red_mushroom", RecipeProvider.has(Blocks.RED_MUSHROOM)).unlockedBy("has_mushroom_stew", RecipeProvider.has(Items.MUSHROOM_STEW)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.BREAD).define(Character.valueOf('#'), Items.WHEAT).pattern("###").unlockedBy("has_wheat", RecipeProvider.has(Items.WHEAT)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.BREWING_STAND).define(Character.valueOf('B'), Items.BLAZE_ROD).define(Character.valueOf('#'), ItemTags.STONE_CRAFTING_MATERIALS).pattern(" B ").pattern("###").unlockedBy("has_blaze_rod", RecipeProvider.has(Items.BLAZE_ROD)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.BRICKS).define(Character.valueOf('#'), Items.BRICK).pattern("##").pattern("##").unlockedBy("has_brick", RecipeProvider.has(Items.BRICK)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.BRICK_SLAB, 6).define(Character.valueOf('#'), Blocks.BRICKS).pattern("###").unlockedBy("has_brick_block", RecipeProvider.has(Blocks.BRICKS)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.BRICK_STAIRS, 4).define(Character.valueOf('#'), Blocks.BRICKS).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_brick_block", RecipeProvider.has(Blocks.BRICKS)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.BROWN_DYE).requires(Items.COCOA_BEANS).group("brown_dye").unlockedBy("has_cocoa_beans", RecipeProvider.has(Items.COCOA_BEANS)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.BUCKET).define(Character.valueOf('#'), Items.IRON_INGOT).pattern("# #").pattern(" # ").unlockedBy("has_iron_ingot", RecipeProvider.has(Items.IRON_INGOT)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.CAKE).define(Character.valueOf('A'), Items.MILK_BUCKET).define(Character.valueOf('B'), Items.SUGAR).define(Character.valueOf('C'), Items.WHEAT).define(Character.valueOf('E'), Items.EGG).pattern("AAA").pattern("BEB").pattern("CCC").unlockedBy("has_egg", RecipeProvider.has(Items.EGG)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.CAMPFIRE).define(Character.valueOf('L'), ItemTags.LOGS).define(Character.valueOf('S'), Items.STICK).define(Character.valueOf('C'), ItemTags.COALS).pattern(" S ").pattern("SCS").pattern("LLL").unlockedBy("has_stick", RecipeProvider.has(Items.STICK)).unlockedBy("has_coal", RecipeProvider.has(ItemTags.COALS)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.CARROT_ON_A_STICK).define(Character.valueOf('#'), Items.FISHING_ROD).define(Character.valueOf('X'), Items.CARROT).pattern("# ").pattern(" X").unlockedBy("has_carrot", RecipeProvider.has(Items.CARROT)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.WARPED_FUNGUS_ON_A_STICK).define(Character.valueOf('#'), Items.FISHING_ROD).define(Character.valueOf('X'), Items.WARPED_FUNGUS).pattern("# ").pattern(" X").unlockedBy("has_warped_fungus", RecipeProvider.has(Items.WARPED_FUNGUS)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.CAULDRON).define(Character.valueOf('#'), Items.IRON_INGOT).pattern("# #").pattern("# #").pattern("###").unlockedBy("has_water_bucket", RecipeProvider.has(Items.WATER_BUCKET)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.COMPOSTER).define(Character.valueOf('#'), ItemTags.WOODEN_SLABS).pattern("# #").pattern("# #").pattern("###").unlockedBy("has_wood_slab", RecipeProvider.has(ItemTags.WOODEN_SLABS)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.CHEST).define(Character.valueOf('#'), ItemTags.PLANKS).pattern("###").pattern("# #").pattern("###").unlockedBy("has_lots_of_items", new InventoryChangeTrigger.TriggerInstance(EntityPredicate.Composite.ANY, MinMaxBounds.Ints.atLeast(10), MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, new ItemPredicate[0])).save(consumer);
        ShapedRecipeBuilder.shaped(Items.CHEST_MINECART).define(Character.valueOf('A'), Blocks.CHEST).define(Character.valueOf('B'), Items.MINECART).pattern("A").pattern("B").unlockedBy("has_minecart", RecipeProvider.has(Items.MINECART)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.CHISELED_NETHER_BRICKS).define(Character.valueOf('#'), Blocks.NETHER_BRICK_SLAB).pattern("#").pattern("#").unlockedBy("has_nether_bricks", RecipeProvider.has(Blocks.NETHER_BRICKS)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.CHISELED_QUARTZ_BLOCK).define(Character.valueOf('#'), Blocks.QUARTZ_SLAB).pattern("#").pattern("#").unlockedBy("has_chiseled_quartz_block", RecipeProvider.has(Blocks.CHISELED_QUARTZ_BLOCK)).unlockedBy("has_quartz_block", RecipeProvider.has(Blocks.QUARTZ_BLOCK)).unlockedBy("has_quartz_pillar", RecipeProvider.has(Blocks.QUARTZ_PILLAR)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.CHISELED_STONE_BRICKS).define(Character.valueOf('#'), Blocks.STONE_BRICK_SLAB).pattern("#").pattern("#").unlockedBy("has_stone_bricks", RecipeProvider.has(ItemTags.STONE_BRICKS)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.CLAY).define(Character.valueOf('#'), Items.CLAY_BALL).pattern("##").pattern("##").unlockedBy("has_clay_ball", RecipeProvider.has(Items.CLAY_BALL)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.CLOCK).define(Character.valueOf('#'), Items.GOLD_INGOT).define(Character.valueOf('X'), Items.REDSTONE).pattern(" # ").pattern("#X#").pattern(" # ").unlockedBy("has_redstone", RecipeProvider.has(Items.REDSTONE)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.COAL, 9).requires(Blocks.COAL_BLOCK).unlockedBy("has_coal_block", RecipeProvider.has(Blocks.COAL_BLOCK)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.COAL_BLOCK).define(Character.valueOf('#'), Items.COAL).pattern("###").pattern("###").pattern("###").unlockedBy("has_coal", RecipeProvider.has(Items.COAL)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.COARSE_DIRT, 4).define(Character.valueOf('D'), Blocks.DIRT).define(Character.valueOf('G'), Blocks.GRAVEL).pattern("DG").pattern("GD").unlockedBy("has_gravel", RecipeProvider.has(Blocks.GRAVEL)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.COBBLESTONE_SLAB, 6).define(Character.valueOf('#'), Blocks.COBBLESTONE).pattern("###").unlockedBy("has_cobblestone", RecipeProvider.has(Blocks.COBBLESTONE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.COBBLESTONE_WALL, 6).define(Character.valueOf('#'), Blocks.COBBLESTONE).pattern("###").pattern("###").unlockedBy("has_cobblestone", RecipeProvider.has(Blocks.COBBLESTONE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.COMPARATOR).define(Character.valueOf('#'), Blocks.REDSTONE_TORCH).define(Character.valueOf('X'), Items.QUARTZ).define(Character.valueOf('I'), Blocks.STONE).pattern(" # ").pattern("#X#").pattern("III").unlockedBy("has_quartz", RecipeProvider.has(Items.QUARTZ)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.COMPASS).define(Character.valueOf('#'), Items.IRON_INGOT).define(Character.valueOf('X'), Items.REDSTONE).pattern(" # ").pattern("#X#").pattern(" # ").unlockedBy("has_redstone", RecipeProvider.has(Items.REDSTONE)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.COOKIE, 8).define(Character.valueOf('#'), Items.WHEAT).define(Character.valueOf('X'), Items.COCOA_BEANS).pattern("#X#").unlockedBy("has_cocoa", RecipeProvider.has(Items.COCOA_BEANS)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.CRAFTING_TABLE).define(Character.valueOf('#'), ItemTags.PLANKS).pattern("##").pattern("##").unlockedBy("has_planks", RecipeProvider.has(ItemTags.PLANKS)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.CROSSBOW).define(Character.valueOf('~'), Items.STRING).define(Character.valueOf('#'), Items.STICK).define(Character.valueOf('&'), Items.IRON_INGOT).define(Character.valueOf('$'), Blocks.TRIPWIRE_HOOK).pattern("#&#").pattern("~$~").pattern(" # ").unlockedBy("has_string", RecipeProvider.has(Items.STRING)).unlockedBy("has_stick", RecipeProvider.has(Items.STICK)).unlockedBy("has_iron_ingot", RecipeProvider.has(Items.IRON_INGOT)).unlockedBy("has_tripwire_hook", RecipeProvider.has(Blocks.TRIPWIRE_HOOK)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.LOOM).define(Character.valueOf('#'), ItemTags.PLANKS).define(Character.valueOf('@'), Items.STRING).pattern("@@").pattern("##").unlockedBy("has_string", RecipeProvider.has(Items.STRING)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.CHISELED_RED_SANDSTONE).define(Character.valueOf('#'), Blocks.RED_SANDSTONE_SLAB).pattern("#").pattern("#").unlockedBy("has_red_sandstone", RecipeProvider.has(Blocks.RED_SANDSTONE)).unlockedBy("has_chiseled_red_sandstone", RecipeProvider.has(Blocks.CHISELED_RED_SANDSTONE)).unlockedBy("has_cut_red_sandstone", RecipeProvider.has(Blocks.CUT_RED_SANDSTONE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.CHISELED_SANDSTONE).define(Character.valueOf('#'), Blocks.SANDSTONE_SLAB).pattern("#").pattern("#").unlockedBy("has_stone_slab", RecipeProvider.has(Blocks.SANDSTONE_SLAB)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.CYAN_DYE, 2).requires(Items.BLUE_DYE).requires(Items.GREEN_DYE).unlockedBy("has_green_dye", RecipeProvider.has(Items.GREEN_DYE)).unlockedBy("has_blue_dye", RecipeProvider.has(Items.BLUE_DYE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.DARK_PRISMARINE).define(Character.valueOf('S'), Items.PRISMARINE_SHARD).define(Character.valueOf('I'), Items.BLACK_DYE).pattern("SSS").pattern("SIS").pattern("SSS").unlockedBy("has_prismarine_shard", RecipeProvider.has(Items.PRISMARINE_SHARD)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.PRISMARINE_STAIRS, 4).define(Character.valueOf('#'), Blocks.PRISMARINE).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_prismarine", RecipeProvider.has(Blocks.PRISMARINE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.PRISMARINE_BRICK_STAIRS, 4).define(Character.valueOf('#'), Blocks.PRISMARINE_BRICKS).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_prismarine_bricks", RecipeProvider.has(Blocks.PRISMARINE_BRICKS)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.DARK_PRISMARINE_STAIRS, 4).define(Character.valueOf('#'), Blocks.DARK_PRISMARINE).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_dark_prismarine", RecipeProvider.has(Blocks.DARK_PRISMARINE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.DAYLIGHT_DETECTOR).define(Character.valueOf('Q'), Items.QUARTZ).define(Character.valueOf('G'), Blocks.GLASS).define(Character.valueOf('W'), Ingredient.of(ItemTags.WOODEN_SLABS)).pattern("GGG").pattern("QQQ").pattern("WWW").unlockedBy("has_quartz", RecipeProvider.has(Items.QUARTZ)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.DETECTOR_RAIL, 6).define(Character.valueOf('R'), Items.REDSTONE).define(Character.valueOf('#'), Blocks.STONE_PRESSURE_PLATE).define(Character.valueOf('X'), Items.IRON_INGOT).pattern("X X").pattern("X#X").pattern("XRX").unlockedBy("has_rail", RecipeProvider.has(Blocks.RAIL)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.DIAMOND, 9).requires(Blocks.DIAMOND_BLOCK).unlockedBy("has_diamond_block", RecipeProvider.has(Blocks.DIAMOND_BLOCK)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.DIAMOND_AXE).define(Character.valueOf('#'), Items.STICK).define(Character.valueOf('X'), Items.DIAMOND).pattern("XX").pattern("X#").pattern(" #").unlockedBy("has_diamond", RecipeProvider.has(Items.DIAMOND)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.DIAMOND_BLOCK).define(Character.valueOf('#'), Items.DIAMOND).pattern("###").pattern("###").pattern("###").unlockedBy("has_diamond", RecipeProvider.has(Items.DIAMOND)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.DIAMOND_BOOTS).define(Character.valueOf('X'), Items.DIAMOND).pattern("X X").pattern("X X").unlockedBy("has_diamond", RecipeProvider.has(Items.DIAMOND)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.DIAMOND_CHESTPLATE).define(Character.valueOf('X'), Items.DIAMOND).pattern("X X").pattern("XXX").pattern("XXX").unlockedBy("has_diamond", RecipeProvider.has(Items.DIAMOND)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.DIAMOND_HELMET).define(Character.valueOf('X'), Items.DIAMOND).pattern("XXX").pattern("X X").unlockedBy("has_diamond", RecipeProvider.has(Items.DIAMOND)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.DIAMOND_HOE).define(Character.valueOf('#'), Items.STICK).define(Character.valueOf('X'), Items.DIAMOND).pattern("XX").pattern(" #").pattern(" #").unlockedBy("has_diamond", RecipeProvider.has(Items.DIAMOND)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.DIAMOND_LEGGINGS).define(Character.valueOf('X'), Items.DIAMOND).pattern("XXX").pattern("X X").pattern("X X").unlockedBy("has_diamond", RecipeProvider.has(Items.DIAMOND)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.DIAMOND_PICKAXE).define(Character.valueOf('#'), Items.STICK).define(Character.valueOf('X'), Items.DIAMOND).pattern("XXX").pattern(" # ").pattern(" # ").unlockedBy("has_diamond", RecipeProvider.has(Items.DIAMOND)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.DIAMOND_SHOVEL).define(Character.valueOf('#'), Items.STICK).define(Character.valueOf('X'), Items.DIAMOND).pattern("X").pattern("#").pattern("#").unlockedBy("has_diamond", RecipeProvider.has(Items.DIAMOND)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.DIAMOND_SWORD).define(Character.valueOf('#'), Items.STICK).define(Character.valueOf('X'), Items.DIAMOND).pattern("X").pattern("X").pattern("#").unlockedBy("has_diamond", RecipeProvider.has(Items.DIAMOND)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.DIORITE, 2).define(Character.valueOf('Q'), Items.QUARTZ).define(Character.valueOf('C'), Blocks.COBBLESTONE).pattern("CQ").pattern("QC").unlockedBy("has_quartz", RecipeProvider.has(Items.QUARTZ)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.DISPENSER).define(Character.valueOf('R'), Items.REDSTONE).define(Character.valueOf('#'), Blocks.COBBLESTONE).define(Character.valueOf('X'), Items.BOW).pattern("###").pattern("#X#").pattern("#R#").unlockedBy("has_bow", RecipeProvider.has(Items.BOW)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.DROPPER).define(Character.valueOf('R'), Items.REDSTONE).define(Character.valueOf('#'), Blocks.COBBLESTONE).pattern("###").pattern("# #").pattern("#R#").unlockedBy("has_redstone", RecipeProvider.has(Items.REDSTONE)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.EMERALD, 9).requires(Blocks.EMERALD_BLOCK).unlockedBy("has_emerald_block", RecipeProvider.has(Blocks.EMERALD_BLOCK)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.EMERALD_BLOCK).define(Character.valueOf('#'), Items.EMERALD).pattern("###").pattern("###").pattern("###").unlockedBy("has_emerald", RecipeProvider.has(Items.EMERALD)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.ENCHANTING_TABLE).define(Character.valueOf('B'), Items.BOOK).define(Character.valueOf('#'), Blocks.OBSIDIAN).define(Character.valueOf('D'), Items.DIAMOND).pattern(" B ").pattern("D#D").pattern("###").unlockedBy("has_obsidian", RecipeProvider.has(Blocks.OBSIDIAN)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.ENDER_CHEST).define(Character.valueOf('#'), Blocks.OBSIDIAN).define(Character.valueOf('E'), Items.ENDER_EYE).pattern("###").pattern("#E#").pattern("###").unlockedBy("has_ender_eye", RecipeProvider.has(Items.ENDER_EYE)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.ENDER_EYE).requires(Items.ENDER_PEARL).requires(Items.BLAZE_POWDER).unlockedBy("has_blaze_powder", RecipeProvider.has(Items.BLAZE_POWDER)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.END_STONE_BRICKS, 4).define(Character.valueOf('#'), Blocks.END_STONE).pattern("##").pattern("##").unlockedBy("has_end_stone", RecipeProvider.has(Blocks.END_STONE)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.END_CRYSTAL).define(Character.valueOf('T'), Items.GHAST_TEAR).define(Character.valueOf('E'), Items.ENDER_EYE).define(Character.valueOf('G'), Blocks.GLASS).pattern("GGG").pattern("GEG").pattern("GTG").unlockedBy("has_ender_eye", RecipeProvider.has(Items.ENDER_EYE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.END_ROD, 4).define(Character.valueOf('#'), Items.POPPED_CHORUS_FRUIT).define(Character.valueOf('/'), Items.BLAZE_ROD).pattern("/").pattern("#").unlockedBy("has_chorus_fruit_popped", RecipeProvider.has(Items.POPPED_CHORUS_FRUIT)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.FERMENTED_SPIDER_EYE).requires(Items.SPIDER_EYE).requires(Blocks.BROWN_MUSHROOM).requires(Items.SUGAR).unlockedBy("has_spider_eye", RecipeProvider.has(Items.SPIDER_EYE)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.FIRE_CHARGE, 3).requires(Items.GUNPOWDER).requires(Items.BLAZE_POWDER).requires(Ingredient.of(Items.COAL, Items.CHARCOAL)).unlockedBy("has_blaze_powder", RecipeProvider.has(Items.BLAZE_POWDER)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.FISHING_ROD).define(Character.valueOf('#'), Items.STICK).define(Character.valueOf('X'), Items.STRING).pattern("  #").pattern(" #X").pattern("# X").unlockedBy("has_string", RecipeProvider.has(Items.STRING)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.FLINT_AND_STEEL).requires(Items.IRON_INGOT).requires(Items.FLINT).unlockedBy("has_flint", RecipeProvider.has(Items.FLINT)).unlockedBy("has_obsidian", RecipeProvider.has(Blocks.OBSIDIAN)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.FLOWER_POT).define(Character.valueOf('#'), Items.BRICK).pattern("# #").pattern(" # ").unlockedBy("has_brick", RecipeProvider.has(Items.BRICK)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.FURNACE).define(Character.valueOf('#'), ItemTags.STONE_CRAFTING_MATERIALS).pattern("###").pattern("# #").pattern("###").unlockedBy("has_cobblestone", RecipeProvider.has(ItemTags.STONE_CRAFTING_MATERIALS)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.FURNACE_MINECART).define(Character.valueOf('A'), Blocks.FURNACE).define(Character.valueOf('B'), Items.MINECART).pattern("A").pattern("B").unlockedBy("has_minecart", RecipeProvider.has(Items.MINECART)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.GLASS_BOTTLE, 3).define(Character.valueOf('#'), Blocks.GLASS).pattern("# #").pattern(" # ").unlockedBy("has_glass", RecipeProvider.has(Blocks.GLASS)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.GLASS_PANE, 16).define(Character.valueOf('#'), Blocks.GLASS).pattern("###").pattern("###").unlockedBy("has_glass", RecipeProvider.has(Blocks.GLASS)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.GLOWSTONE).define(Character.valueOf('#'), Items.GLOWSTONE_DUST).pattern("##").pattern("##").unlockedBy("has_glowstone_dust", RecipeProvider.has(Items.GLOWSTONE_DUST)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.GOLDEN_APPLE).define(Character.valueOf('#'), Items.GOLD_INGOT).define(Character.valueOf('X'), Items.APPLE).pattern("###").pattern("#X#").pattern("###").unlockedBy("has_gold_ingot", RecipeProvider.has(Items.GOLD_INGOT)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.GOLDEN_AXE).define(Character.valueOf('#'), Items.STICK).define(Character.valueOf('X'), Items.GOLD_INGOT).pattern("XX").pattern("X#").pattern(" #").unlockedBy("has_gold_ingot", RecipeProvider.has(Items.GOLD_INGOT)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.GOLDEN_BOOTS).define(Character.valueOf('X'), Items.GOLD_INGOT).pattern("X X").pattern("X X").unlockedBy("has_gold_ingot", RecipeProvider.has(Items.GOLD_INGOT)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.GOLDEN_CARROT).define(Character.valueOf('#'), Items.GOLD_NUGGET).define(Character.valueOf('X'), Items.CARROT).pattern("###").pattern("#X#").pattern("###").unlockedBy("has_gold_nugget", RecipeProvider.has(Items.GOLD_NUGGET)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.GOLDEN_CHESTPLATE).define(Character.valueOf('X'), Items.GOLD_INGOT).pattern("X X").pattern("XXX").pattern("XXX").unlockedBy("has_gold_ingot", RecipeProvider.has(Items.GOLD_INGOT)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.GOLDEN_HELMET).define(Character.valueOf('X'), Items.GOLD_INGOT).pattern("XXX").pattern("X X").unlockedBy("has_gold_ingot", RecipeProvider.has(Items.GOLD_INGOT)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.GOLDEN_HOE).define(Character.valueOf('#'), Items.STICK).define(Character.valueOf('X'), Items.GOLD_INGOT).pattern("XX").pattern(" #").pattern(" #").unlockedBy("has_gold_ingot", RecipeProvider.has(Items.GOLD_INGOT)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.GOLDEN_LEGGINGS).define(Character.valueOf('X'), Items.GOLD_INGOT).pattern("XXX").pattern("X X").pattern("X X").unlockedBy("has_gold_ingot", RecipeProvider.has(Items.GOLD_INGOT)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.GOLDEN_PICKAXE).define(Character.valueOf('#'), Items.STICK).define(Character.valueOf('X'), Items.GOLD_INGOT).pattern("XXX").pattern(" # ").pattern(" # ").unlockedBy("has_gold_ingot", RecipeProvider.has(Items.GOLD_INGOT)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.POWERED_RAIL, 6).define(Character.valueOf('R'), Items.REDSTONE).define(Character.valueOf('#'), Items.STICK).define(Character.valueOf('X'), Items.GOLD_INGOT).pattern("X X").pattern("X#X").pattern("XRX").unlockedBy("has_rail", RecipeProvider.has(Blocks.RAIL)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.GOLDEN_SHOVEL).define(Character.valueOf('#'), Items.STICK).define(Character.valueOf('X'), Items.GOLD_INGOT).pattern("X").pattern("#").pattern("#").unlockedBy("has_gold_ingot", RecipeProvider.has(Items.GOLD_INGOT)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.GOLDEN_SWORD).define(Character.valueOf('#'), Items.STICK).define(Character.valueOf('X'), Items.GOLD_INGOT).pattern("X").pattern("X").pattern("#").unlockedBy("has_gold_ingot", RecipeProvider.has(Items.GOLD_INGOT)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.GOLD_BLOCK).define(Character.valueOf('#'), Items.GOLD_INGOT).pattern("###").pattern("###").pattern("###").unlockedBy("has_gold_ingot", RecipeProvider.has(Items.GOLD_INGOT)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.GOLD_INGOT, 9).requires(Blocks.GOLD_BLOCK).group("gold_ingot").unlockedBy("has_gold_block", RecipeProvider.has(Blocks.GOLD_BLOCK)).save(consumer, "gold_ingot_from_gold_block");
        ShapedRecipeBuilder.shaped(Items.GOLD_INGOT).define(Character.valueOf('#'), Items.GOLD_NUGGET).pattern("###").pattern("###").pattern("###").group("gold_ingot").unlockedBy("has_gold_nugget", RecipeProvider.has(Items.GOLD_NUGGET)).save(consumer, "gold_ingot_from_nuggets");
        ShapelessRecipeBuilder.shapeless(Items.GOLD_NUGGET, 9).requires(Items.GOLD_INGOT).unlockedBy("has_gold_ingot", RecipeProvider.has(Items.GOLD_INGOT)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Blocks.GRANITE).requires(Blocks.DIORITE).requires(Items.QUARTZ).unlockedBy("has_quartz", RecipeProvider.has(Items.QUARTZ)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.GRAY_DYE, 2).requires(Items.BLACK_DYE).requires(Items.WHITE_DYE).unlockedBy("has_white_dye", RecipeProvider.has(Items.WHITE_DYE)).unlockedBy("has_black_dye", RecipeProvider.has(Items.BLACK_DYE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.HAY_BLOCK).define(Character.valueOf('#'), Items.WHEAT).pattern("###").pattern("###").pattern("###").unlockedBy("has_wheat", RecipeProvider.has(Items.WHEAT)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE).define(Character.valueOf('#'), Items.IRON_INGOT).pattern("##").unlockedBy("has_iron_ingot", RecipeProvider.has(Items.IRON_INGOT)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.HONEY_BOTTLE, 4).requires(Items.HONEY_BLOCK).requires(Items.GLASS_BOTTLE, 4).unlockedBy("has_honey_block", RecipeProvider.has(Blocks.HONEY_BLOCK)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.HONEY_BLOCK, 1).define(Character.valueOf('S'), Items.HONEY_BOTTLE).pattern("SS").pattern("SS").unlockedBy("has_honey_bottle", RecipeProvider.has(Items.HONEY_BOTTLE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.HONEYCOMB_BLOCK).define(Character.valueOf('H'), Items.HONEYCOMB).pattern("HH").pattern("HH").unlockedBy("has_honeycomb", RecipeProvider.has(Items.HONEYCOMB)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.HOPPER).define(Character.valueOf('C'), Blocks.CHEST).define(Character.valueOf('I'), Items.IRON_INGOT).pattern("I I").pattern("ICI").pattern(" I ").unlockedBy("has_iron_ingot", RecipeProvider.has(Items.IRON_INGOT)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.HOPPER_MINECART).define(Character.valueOf('A'), Blocks.HOPPER).define(Character.valueOf('B'), Items.MINECART).pattern("A").pattern("B").unlockedBy("has_minecart", RecipeProvider.has(Items.MINECART)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.IRON_AXE).define(Character.valueOf('#'), Items.STICK).define(Character.valueOf('X'), Items.IRON_INGOT).pattern("XX").pattern("X#").pattern(" #").unlockedBy("has_iron_ingot", RecipeProvider.has(Items.IRON_INGOT)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.IRON_BARS, 16).define(Character.valueOf('#'), Items.IRON_INGOT).pattern("###").pattern("###").unlockedBy("has_iron_ingot", RecipeProvider.has(Items.IRON_INGOT)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.IRON_BLOCK).define(Character.valueOf('#'), Items.IRON_INGOT).pattern("###").pattern("###").pattern("###").unlockedBy("has_iron_ingot", RecipeProvider.has(Items.IRON_INGOT)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.IRON_BOOTS).define(Character.valueOf('X'), Items.IRON_INGOT).pattern("X X").pattern("X X").unlockedBy("has_iron_ingot", RecipeProvider.has(Items.IRON_INGOT)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.IRON_CHESTPLATE).define(Character.valueOf('X'), Items.IRON_INGOT).pattern("X X").pattern("XXX").pattern("XXX").unlockedBy("has_iron_ingot", RecipeProvider.has(Items.IRON_INGOT)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.IRON_DOOR, 3).define(Character.valueOf('#'), Items.IRON_INGOT).pattern("##").pattern("##").pattern("##").unlockedBy("has_iron_ingot", RecipeProvider.has(Items.IRON_INGOT)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.IRON_HELMET).define(Character.valueOf('X'), Items.IRON_INGOT).pattern("XXX").pattern("X X").unlockedBy("has_iron_ingot", RecipeProvider.has(Items.IRON_INGOT)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.IRON_HOE).define(Character.valueOf('#'), Items.STICK).define(Character.valueOf('X'), Items.IRON_INGOT).pattern("XX").pattern(" #").pattern(" #").unlockedBy("has_iron_ingot", RecipeProvider.has(Items.IRON_INGOT)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.IRON_INGOT, 9).requires(Blocks.IRON_BLOCK).group("iron_ingot").unlockedBy("has_iron_block", RecipeProvider.has(Blocks.IRON_BLOCK)).save(consumer, "iron_ingot_from_iron_block");
        ShapedRecipeBuilder.shaped(Items.IRON_INGOT).define(Character.valueOf('#'), Items.IRON_NUGGET).pattern("###").pattern("###").pattern("###").group("iron_ingot").unlockedBy("has_iron_nugget", RecipeProvider.has(Items.IRON_NUGGET)).save(consumer, "iron_ingot_from_nuggets");
        ShapedRecipeBuilder.shaped(Items.IRON_LEGGINGS).define(Character.valueOf('X'), Items.IRON_INGOT).pattern("XXX").pattern("X X").pattern("X X").unlockedBy("has_iron_ingot", RecipeProvider.has(Items.IRON_INGOT)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.IRON_NUGGET, 9).requires(Items.IRON_INGOT).unlockedBy("has_iron_ingot", RecipeProvider.has(Items.IRON_INGOT)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.IRON_PICKAXE).define(Character.valueOf('#'), Items.STICK).define(Character.valueOf('X'), Items.IRON_INGOT).pattern("XXX").pattern(" # ").pattern(" # ").unlockedBy("has_iron_ingot", RecipeProvider.has(Items.IRON_INGOT)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.IRON_SHOVEL).define(Character.valueOf('#'), Items.STICK).define(Character.valueOf('X'), Items.IRON_INGOT).pattern("X").pattern("#").pattern("#").unlockedBy("has_iron_ingot", RecipeProvider.has(Items.IRON_INGOT)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.IRON_SWORD).define(Character.valueOf('#'), Items.STICK).define(Character.valueOf('X'), Items.IRON_INGOT).pattern("X").pattern("X").pattern("#").unlockedBy("has_iron_ingot", RecipeProvider.has(Items.IRON_INGOT)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.IRON_TRAPDOOR).define(Character.valueOf('#'), Items.IRON_INGOT).pattern("##").pattern("##").unlockedBy("has_iron_ingot", RecipeProvider.has(Items.IRON_INGOT)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.ITEM_FRAME).define(Character.valueOf('#'), Items.STICK).define(Character.valueOf('X'), Items.LEATHER).pattern("###").pattern("#X#").pattern("###").unlockedBy("has_leather", RecipeProvider.has(Items.LEATHER)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.JUKEBOX).define(Character.valueOf('#'), ItemTags.PLANKS).define(Character.valueOf('X'), Items.DIAMOND).pattern("###").pattern("#X#").pattern("###").unlockedBy("has_diamond", RecipeProvider.has(Items.DIAMOND)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.LADDER, 3).define(Character.valueOf('#'), Items.STICK).pattern("# #").pattern("###").pattern("# #").unlockedBy("has_stick", RecipeProvider.has(Items.STICK)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.LAPIS_BLOCK).define(Character.valueOf('#'), Items.LAPIS_LAZULI).pattern("###").pattern("###").pattern("###").unlockedBy("has_lapis", RecipeProvider.has(Items.LAPIS_LAZULI)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.LAPIS_LAZULI, 9).requires(Blocks.LAPIS_BLOCK).unlockedBy("has_lapis_block", RecipeProvider.has(Blocks.LAPIS_BLOCK)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.LEAD, 2).define(Character.valueOf('~'), Items.STRING).define(Character.valueOf('O'), Items.SLIME_BALL).pattern("~~ ").pattern("~O ").pattern("  ~").unlockedBy("has_slime_ball", RecipeProvider.has(Items.SLIME_BALL)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.LEATHER).define(Character.valueOf('#'), Items.RABBIT_HIDE).pattern("##").pattern("##").unlockedBy("has_rabbit_hide", RecipeProvider.has(Items.RABBIT_HIDE)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.LEATHER_BOOTS).define(Character.valueOf('X'), Items.LEATHER).pattern("X X").pattern("X X").unlockedBy("has_leather", RecipeProvider.has(Items.LEATHER)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.LEATHER_CHESTPLATE).define(Character.valueOf('X'), Items.LEATHER).pattern("X X").pattern("XXX").pattern("XXX").unlockedBy("has_leather", RecipeProvider.has(Items.LEATHER)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.LEATHER_HELMET).define(Character.valueOf('X'), Items.LEATHER).pattern("XXX").pattern("X X").unlockedBy("has_leather", RecipeProvider.has(Items.LEATHER)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.LEATHER_LEGGINGS).define(Character.valueOf('X'), Items.LEATHER).pattern("XXX").pattern("X X").pattern("X X").unlockedBy("has_leather", RecipeProvider.has(Items.LEATHER)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.LEATHER_HORSE_ARMOR).define(Character.valueOf('X'), Items.LEATHER).pattern("X X").pattern("XXX").pattern("X X").unlockedBy("has_leather", RecipeProvider.has(Items.LEATHER)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.LECTERN).define(Character.valueOf('S'), ItemTags.WOODEN_SLABS).define(Character.valueOf('B'), Blocks.BOOKSHELF).pattern("SSS").pattern(" B ").pattern(" S ").unlockedBy("has_book", RecipeProvider.has(Items.BOOK)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.LEVER).define(Character.valueOf('#'), Blocks.COBBLESTONE).define(Character.valueOf('X'), Items.STICK).pattern("X").pattern("#").unlockedBy("has_cobblestone", RecipeProvider.has(Blocks.COBBLESTONE)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.LIGHT_BLUE_DYE).requires(Blocks.BLUE_ORCHID).group("light_blue_dye").unlockedBy("has_red_flower", RecipeProvider.has(Blocks.BLUE_ORCHID)).save(consumer, "light_blue_dye_from_blue_orchid");
        ShapelessRecipeBuilder.shapeless(Items.LIGHT_BLUE_DYE, 2).requires(Items.BLUE_DYE).requires(Items.WHITE_DYE).group("light_blue_dye").unlockedBy("has_blue_dye", RecipeProvider.has(Items.BLUE_DYE)).unlockedBy("has_white_dye", RecipeProvider.has(Items.WHITE_DYE)).save(consumer, "light_blue_dye_from_blue_white_dye");
        ShapelessRecipeBuilder.shapeless(Items.LIGHT_GRAY_DYE).requires(Blocks.AZURE_BLUET).group("light_gray_dye").unlockedBy("has_red_flower", RecipeProvider.has(Blocks.AZURE_BLUET)).save(consumer, "light_gray_dye_from_azure_bluet");
        ShapelessRecipeBuilder.shapeless(Items.LIGHT_GRAY_DYE, 2).requires(Items.GRAY_DYE).requires(Items.WHITE_DYE).group("light_gray_dye").unlockedBy("has_gray_dye", RecipeProvider.has(Items.GRAY_DYE)).unlockedBy("has_white_dye", RecipeProvider.has(Items.WHITE_DYE)).save(consumer, "light_gray_dye_from_gray_white_dye");
        ShapelessRecipeBuilder.shapeless(Items.LIGHT_GRAY_DYE, 3).requires(Items.BLACK_DYE).requires(Items.WHITE_DYE, 2).group("light_gray_dye").unlockedBy("has_white_dye", RecipeProvider.has(Items.WHITE_DYE)).unlockedBy("has_black_dye", RecipeProvider.has(Items.BLACK_DYE)).save(consumer, "light_gray_dye_from_black_white_dye");
        ShapelessRecipeBuilder.shapeless(Items.LIGHT_GRAY_DYE).requires(Blocks.OXEYE_DAISY).group("light_gray_dye").unlockedBy("has_red_flower", RecipeProvider.has(Blocks.OXEYE_DAISY)).save(consumer, "light_gray_dye_from_oxeye_daisy");
        ShapelessRecipeBuilder.shapeless(Items.LIGHT_GRAY_DYE).requires(Blocks.WHITE_TULIP).group("light_gray_dye").unlockedBy("has_red_flower", RecipeProvider.has(Blocks.WHITE_TULIP)).save(consumer, "light_gray_dye_from_white_tulip");
        ShapedRecipeBuilder.shaped(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE).define(Character.valueOf('#'), Items.GOLD_INGOT).pattern("##").unlockedBy("has_gold_ingot", RecipeProvider.has(Items.GOLD_INGOT)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.LIME_DYE, 2).requires(Items.GREEN_DYE).requires(Items.WHITE_DYE).unlockedBy("has_green_dye", RecipeProvider.has(Items.GREEN_DYE)).unlockedBy("has_white_dye", RecipeProvider.has(Items.WHITE_DYE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.JACK_O_LANTERN).define(Character.valueOf('A'), Blocks.CARVED_PUMPKIN).define(Character.valueOf('B'), Blocks.TORCH).pattern("A").pattern("B").unlockedBy("has_carved_pumpkin", RecipeProvider.has(Blocks.CARVED_PUMPKIN)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.MAGENTA_DYE).requires(Blocks.ALLIUM).group("magenta_dye").unlockedBy("has_red_flower", RecipeProvider.has(Blocks.ALLIUM)).save(consumer, "magenta_dye_from_allium");
        ShapelessRecipeBuilder.shapeless(Items.MAGENTA_DYE, 4).requires(Items.BLUE_DYE).requires(Items.RED_DYE, 2).requires(Items.WHITE_DYE).group("magenta_dye").unlockedBy("has_blue_dye", RecipeProvider.has(Items.BLUE_DYE)).unlockedBy("has_rose_red", RecipeProvider.has(Items.RED_DYE)).unlockedBy("has_white_dye", RecipeProvider.has(Items.WHITE_DYE)).save(consumer, "magenta_dye_from_blue_red_white_dye");
        ShapelessRecipeBuilder.shapeless(Items.MAGENTA_DYE, 3).requires(Items.BLUE_DYE).requires(Items.RED_DYE).requires(Items.PINK_DYE).group("magenta_dye").unlockedBy("has_pink_dye", RecipeProvider.has(Items.PINK_DYE)).unlockedBy("has_blue_dye", RecipeProvider.has(Items.BLUE_DYE)).unlockedBy("has_red_dye", RecipeProvider.has(Items.RED_DYE)).save(consumer, "magenta_dye_from_blue_red_pink");
        ShapelessRecipeBuilder.shapeless(Items.MAGENTA_DYE, 2).requires(Blocks.LILAC).group("magenta_dye").unlockedBy("has_double_plant", RecipeProvider.has(Blocks.LILAC)).save(consumer, "magenta_dye_from_lilac");
        ShapelessRecipeBuilder.shapeless(Items.MAGENTA_DYE, 2).requires(Items.PURPLE_DYE).requires(Items.PINK_DYE).group("magenta_dye").unlockedBy("has_pink_dye", RecipeProvider.has(Items.PINK_DYE)).unlockedBy("has_purple_dye", RecipeProvider.has(Items.PURPLE_DYE)).save(consumer, "magenta_dye_from_purple_and_pink");
        ShapedRecipeBuilder.shaped(Blocks.MAGMA_BLOCK).define(Character.valueOf('#'), Items.MAGMA_CREAM).pattern("##").pattern("##").unlockedBy("has_magma_cream", RecipeProvider.has(Items.MAGMA_CREAM)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.MAGMA_CREAM).requires(Items.BLAZE_POWDER).requires(Items.SLIME_BALL).unlockedBy("has_blaze_powder", RecipeProvider.has(Items.BLAZE_POWDER)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.MAP).define(Character.valueOf('#'), Items.PAPER).define(Character.valueOf('X'), Items.COMPASS).pattern("###").pattern("#X#").pattern("###").unlockedBy("has_compass", RecipeProvider.has(Items.COMPASS)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.MELON).define(Character.valueOf('M'), Items.MELON_SLICE).pattern("MMM").pattern("MMM").pattern("MMM").unlockedBy("has_melon", RecipeProvider.has(Items.MELON_SLICE)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.MELON_SEEDS).requires(Items.MELON_SLICE).unlockedBy("has_melon", RecipeProvider.has(Items.MELON_SLICE)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.MINECART).define(Character.valueOf('#'), Items.IRON_INGOT).pattern("# #").pattern("###").unlockedBy("has_iron_ingot", RecipeProvider.has(Items.IRON_INGOT)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Blocks.MOSSY_COBBLESTONE).requires(Blocks.COBBLESTONE).requires(Blocks.VINE).unlockedBy("has_vine", RecipeProvider.has(Blocks.VINE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.MOSSY_COBBLESTONE_WALL, 6).define(Character.valueOf('#'), Blocks.MOSSY_COBBLESTONE).pattern("###").pattern("###").unlockedBy("has_mossy_cobblestone", RecipeProvider.has(Blocks.MOSSY_COBBLESTONE)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Blocks.MOSSY_STONE_BRICKS).requires(Blocks.STONE_BRICKS).requires(Blocks.VINE).unlockedBy("has_mossy_cobblestone", RecipeProvider.has(Blocks.MOSSY_COBBLESTONE)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.MUSHROOM_STEW).requires(Blocks.BROWN_MUSHROOM).requires(Blocks.RED_MUSHROOM).requires(Items.BOWL).unlockedBy("has_mushroom_stew", RecipeProvider.has(Items.MUSHROOM_STEW)).unlockedBy("has_bowl", RecipeProvider.has(Items.BOWL)).unlockedBy("has_brown_mushroom", RecipeProvider.has(Blocks.BROWN_MUSHROOM)).unlockedBy("has_red_mushroom", RecipeProvider.has(Blocks.RED_MUSHROOM)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.NETHER_BRICKS).define(Character.valueOf('N'), Items.NETHER_BRICK).pattern("NN").pattern("NN").unlockedBy("has_netherbrick", RecipeProvider.has(Items.NETHER_BRICK)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.NETHER_BRICK_FENCE, 6).define(Character.valueOf('#'), Blocks.NETHER_BRICKS).define(Character.valueOf('-'), Items.NETHER_BRICK).pattern("#-#").pattern("#-#").unlockedBy("has_nether_brick", RecipeProvider.has(Blocks.NETHER_BRICKS)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.NETHER_BRICK_SLAB, 6).define(Character.valueOf('#'), Blocks.NETHER_BRICKS).pattern("###").unlockedBy("has_nether_brick", RecipeProvider.has(Blocks.NETHER_BRICKS)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.NETHER_BRICK_STAIRS, 4).define(Character.valueOf('#'), Blocks.NETHER_BRICKS).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_nether_brick", RecipeProvider.has(Blocks.NETHER_BRICKS)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.NETHER_WART_BLOCK).define(Character.valueOf('#'), Items.NETHER_WART).pattern("###").pattern("###").pattern("###").unlockedBy("has_nether_wart", RecipeProvider.has(Items.NETHER_WART)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.NOTE_BLOCK).define(Character.valueOf('#'), ItemTags.PLANKS).define(Character.valueOf('X'), Items.REDSTONE).pattern("###").pattern("#X#").pattern("###").unlockedBy("has_redstone", RecipeProvider.has(Items.REDSTONE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.OBSERVER).define(Character.valueOf('Q'), Items.QUARTZ).define(Character.valueOf('R'), Items.REDSTONE).define(Character.valueOf('#'), Blocks.COBBLESTONE).pattern("###").pattern("RRQ").pattern("###").unlockedBy("has_quartz", RecipeProvider.has(Items.QUARTZ)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.ORANGE_DYE).requires(Blocks.ORANGE_TULIP).group("orange_dye").unlockedBy("has_red_flower", RecipeProvider.has(Blocks.ORANGE_TULIP)).save(consumer, "orange_dye_from_orange_tulip");
        ShapelessRecipeBuilder.shapeless(Items.ORANGE_DYE, 2).requires(Items.RED_DYE).requires(Items.YELLOW_DYE).group("orange_dye").unlockedBy("has_red_dye", RecipeProvider.has(Items.RED_DYE)).unlockedBy("has_yellow_dye", RecipeProvider.has(Items.YELLOW_DYE)).save(consumer, "orange_dye_from_red_yellow");
        ShapedRecipeBuilder.shaped(Items.PAINTING).define(Character.valueOf('#'), Items.STICK).define(Character.valueOf('X'), Ingredient.of(ItemTags.WOOL)).pattern("###").pattern("#X#").pattern("###").unlockedBy("has_wool", RecipeProvider.has(ItemTags.WOOL)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.PAPER, 3).define(Character.valueOf('#'), Blocks.SUGAR_CANE).pattern("###").unlockedBy("has_reeds", RecipeProvider.has(Blocks.SUGAR_CANE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.QUARTZ_PILLAR, 2).define(Character.valueOf('#'), Blocks.QUARTZ_BLOCK).pattern("#").pattern("#").unlockedBy("has_chiseled_quartz_block", RecipeProvider.has(Blocks.CHISELED_QUARTZ_BLOCK)).unlockedBy("has_quartz_block", RecipeProvider.has(Blocks.QUARTZ_BLOCK)).unlockedBy("has_quartz_pillar", RecipeProvider.has(Blocks.QUARTZ_PILLAR)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Blocks.PACKED_ICE).requires(Blocks.ICE, 9).unlockedBy("has_ice", RecipeProvider.has(Blocks.ICE)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.PINK_DYE, 2).requires(Blocks.PEONY).group("pink_dye").unlockedBy("has_double_plant", RecipeProvider.has(Blocks.PEONY)).save(consumer, "pink_dye_from_peony");
        ShapelessRecipeBuilder.shapeless(Items.PINK_DYE).requires(Blocks.PINK_TULIP).group("pink_dye").unlockedBy("has_red_flower", RecipeProvider.has(Blocks.PINK_TULIP)).save(consumer, "pink_dye_from_pink_tulip");
        ShapelessRecipeBuilder.shapeless(Items.PINK_DYE, 2).requires(Items.RED_DYE).requires(Items.WHITE_DYE).group("pink_dye").unlockedBy("has_white_dye", RecipeProvider.has(Items.WHITE_DYE)).unlockedBy("has_red_dye", RecipeProvider.has(Items.RED_DYE)).save(consumer, "pink_dye_from_red_white_dye");
        ShapedRecipeBuilder.shaped(Blocks.PISTON).define(Character.valueOf('R'), Items.REDSTONE).define(Character.valueOf('#'), Blocks.COBBLESTONE).define(Character.valueOf('T'), ItemTags.PLANKS).define(Character.valueOf('X'), Items.IRON_INGOT).pattern("TTT").pattern("#X#").pattern("#R#").unlockedBy("has_redstone", RecipeProvider.has(Items.REDSTONE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.POLISHED_BASALT, 4).define(Character.valueOf('S'), Blocks.BASALT).pattern("SS").pattern("SS").unlockedBy("has_basalt", RecipeProvider.has(Blocks.BASALT)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.POLISHED_GRANITE, 4).define(Character.valueOf('S'), Blocks.GRANITE).pattern("SS").pattern("SS").unlockedBy("has_stone", RecipeProvider.has(Blocks.GRANITE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.POLISHED_DIORITE, 4).define(Character.valueOf('S'), Blocks.DIORITE).pattern("SS").pattern("SS").unlockedBy("has_stone", RecipeProvider.has(Blocks.DIORITE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.POLISHED_ANDESITE, 4).define(Character.valueOf('S'), Blocks.ANDESITE).pattern("SS").pattern("SS").unlockedBy("has_stone", RecipeProvider.has(Blocks.ANDESITE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.PRISMARINE).define(Character.valueOf('S'), Items.PRISMARINE_SHARD).pattern("SS").pattern("SS").unlockedBy("has_prismarine_shard", RecipeProvider.has(Items.PRISMARINE_SHARD)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.PRISMARINE_BRICKS).define(Character.valueOf('S'), Items.PRISMARINE_SHARD).pattern("SSS").pattern("SSS").pattern("SSS").unlockedBy("has_prismarine_shard", RecipeProvider.has(Items.PRISMARINE_SHARD)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.PRISMARINE_SLAB, 6).define(Character.valueOf('#'), Blocks.PRISMARINE).pattern("###").unlockedBy("has_prismarine", RecipeProvider.has(Blocks.PRISMARINE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.PRISMARINE_BRICK_SLAB, 6).define(Character.valueOf('#'), Blocks.PRISMARINE_BRICKS).pattern("###").unlockedBy("has_prismarine_bricks", RecipeProvider.has(Blocks.PRISMARINE_BRICKS)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.DARK_PRISMARINE_SLAB, 6).define(Character.valueOf('#'), Blocks.DARK_PRISMARINE).pattern("###").unlockedBy("has_dark_prismarine", RecipeProvider.has(Blocks.DARK_PRISMARINE)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.PUMPKIN_PIE).requires(Blocks.PUMPKIN).requires(Items.SUGAR).requires(Items.EGG).unlockedBy("has_carved_pumpkin", RecipeProvider.has(Blocks.CARVED_PUMPKIN)).unlockedBy("has_pumpkin", RecipeProvider.has(Blocks.PUMPKIN)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.PUMPKIN_SEEDS, 4).requires(Blocks.PUMPKIN).unlockedBy("has_pumpkin", RecipeProvider.has(Blocks.PUMPKIN)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.PURPLE_DYE, 2).requires(Items.BLUE_DYE).requires(Items.RED_DYE).unlockedBy("has_blue_dye", RecipeProvider.has(Items.BLUE_DYE)).unlockedBy("has_red_dye", RecipeProvider.has(Items.RED_DYE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.SHULKER_BOX).define(Character.valueOf('#'), Blocks.CHEST).define(Character.valueOf('-'), Items.SHULKER_SHELL).pattern("-").pattern("#").pattern("-").unlockedBy("has_shulker_shell", RecipeProvider.has(Items.SHULKER_SHELL)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.PURPUR_BLOCK, 4).define(Character.valueOf('F'), Items.POPPED_CHORUS_FRUIT).pattern("FF").pattern("FF").unlockedBy("has_chorus_fruit_popped", RecipeProvider.has(Items.POPPED_CHORUS_FRUIT)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.PURPUR_PILLAR).define(Character.valueOf('#'), Blocks.PURPUR_SLAB).pattern("#").pattern("#").unlockedBy("has_purpur_block", RecipeProvider.has(Blocks.PURPUR_BLOCK)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.PURPUR_SLAB, 6).define(Character.valueOf('#'), Ingredient.of(Blocks.PURPUR_BLOCK, Blocks.PURPUR_PILLAR)).pattern("###").unlockedBy("has_purpur_block", RecipeProvider.has(Blocks.PURPUR_BLOCK)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.PURPUR_STAIRS, 4).define(Character.valueOf('#'), Ingredient.of(Blocks.PURPUR_BLOCK, Blocks.PURPUR_PILLAR)).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_purpur_block", RecipeProvider.has(Blocks.PURPUR_BLOCK)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.QUARTZ_BLOCK).define(Character.valueOf('#'), Items.QUARTZ).pattern("##").pattern("##").unlockedBy("has_quartz", RecipeProvider.has(Items.QUARTZ)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.QUARTZ_BRICKS, 4).define(Character.valueOf('#'), Blocks.QUARTZ_BLOCK).pattern("##").pattern("##").unlockedBy("has_quartz_block", RecipeProvider.has(Blocks.QUARTZ_BLOCK)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.QUARTZ_SLAB, 6).define(Character.valueOf('#'), Ingredient.of(Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_BLOCK, Blocks.QUARTZ_PILLAR)).pattern("###").unlockedBy("has_chiseled_quartz_block", RecipeProvider.has(Blocks.CHISELED_QUARTZ_BLOCK)).unlockedBy("has_quartz_block", RecipeProvider.has(Blocks.QUARTZ_BLOCK)).unlockedBy("has_quartz_pillar", RecipeProvider.has(Blocks.QUARTZ_PILLAR)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.QUARTZ_STAIRS, 4).define(Character.valueOf('#'), Ingredient.of(Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_BLOCK, Blocks.QUARTZ_PILLAR)).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_chiseled_quartz_block", RecipeProvider.has(Blocks.CHISELED_QUARTZ_BLOCK)).unlockedBy("has_quartz_block", RecipeProvider.has(Blocks.QUARTZ_BLOCK)).unlockedBy("has_quartz_pillar", RecipeProvider.has(Blocks.QUARTZ_PILLAR)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.RABBIT_STEW).requires(Items.BAKED_POTATO).requires(Items.COOKED_RABBIT).requires(Items.BOWL).requires(Items.CARROT).requires(Blocks.BROWN_MUSHROOM).group("rabbit_stew").unlockedBy("has_cooked_rabbit", RecipeProvider.has(Items.COOKED_RABBIT)).save(consumer, "rabbit_stew_from_brown_mushroom");
        ShapelessRecipeBuilder.shapeless(Items.RABBIT_STEW).requires(Items.BAKED_POTATO).requires(Items.COOKED_RABBIT).requires(Items.BOWL).requires(Items.CARROT).requires(Blocks.RED_MUSHROOM).group("rabbit_stew").unlockedBy("has_cooked_rabbit", RecipeProvider.has(Items.COOKED_RABBIT)).save(consumer, "rabbit_stew_from_red_mushroom");
        ShapedRecipeBuilder.shaped(Blocks.RAIL, 16).define(Character.valueOf('#'), Items.STICK).define(Character.valueOf('X'), Items.IRON_INGOT).pattern("X X").pattern("X#X").pattern("X X").unlockedBy("has_minecart", RecipeProvider.has(Items.MINECART)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.REDSTONE, 9).requires(Blocks.REDSTONE_BLOCK).unlockedBy("has_redstone_block", RecipeProvider.has(Blocks.REDSTONE_BLOCK)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.REDSTONE_BLOCK).define(Character.valueOf('#'), Items.REDSTONE).pattern("###").pattern("###").pattern("###").unlockedBy("has_redstone", RecipeProvider.has(Items.REDSTONE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.REDSTONE_LAMP).define(Character.valueOf('R'), Items.REDSTONE).define(Character.valueOf('G'), Blocks.GLOWSTONE).pattern(" R ").pattern("RGR").pattern(" R ").unlockedBy("has_glowstone", RecipeProvider.has(Blocks.GLOWSTONE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.REDSTONE_TORCH).define(Character.valueOf('#'), Items.STICK).define(Character.valueOf('X'), Items.REDSTONE).pattern("X").pattern("#").unlockedBy("has_redstone", RecipeProvider.has(Items.REDSTONE)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.RED_DYE).requires(Items.BEETROOT).group("red_dye").unlockedBy("has_beetroot", RecipeProvider.has(Items.BEETROOT)).save(consumer, "red_dye_from_beetroot");
        ShapelessRecipeBuilder.shapeless(Items.RED_DYE).requires(Blocks.POPPY).group("red_dye").unlockedBy("has_red_flower", RecipeProvider.has(Blocks.POPPY)).save(consumer, "red_dye_from_poppy");
        ShapelessRecipeBuilder.shapeless(Items.RED_DYE, 2).requires(Blocks.ROSE_BUSH).group("red_dye").unlockedBy("has_double_plant", RecipeProvider.has(Blocks.ROSE_BUSH)).save(consumer, "red_dye_from_rose_bush");
        ShapelessRecipeBuilder.shapeless(Items.RED_DYE).requires(Blocks.RED_TULIP).group("red_dye").unlockedBy("has_red_flower", RecipeProvider.has(Blocks.RED_TULIP)).save(consumer, "red_dye_from_tulip");
        ShapedRecipeBuilder.shaped(Blocks.RED_NETHER_BRICKS).define(Character.valueOf('W'), Items.NETHER_WART).define(Character.valueOf('N'), Items.NETHER_BRICK).pattern("NW").pattern("WN").unlockedBy("has_nether_wart", RecipeProvider.has(Items.NETHER_WART)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.RED_SANDSTONE).define(Character.valueOf('#'), Blocks.RED_SAND).pattern("##").pattern("##").unlockedBy("has_sand", RecipeProvider.has(Blocks.RED_SAND)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.RED_SANDSTONE_SLAB, 6).define(Character.valueOf('#'), Ingredient.of(Blocks.RED_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE)).pattern("###").unlockedBy("has_red_sandstone", RecipeProvider.has(Blocks.RED_SANDSTONE)).unlockedBy("has_chiseled_red_sandstone", RecipeProvider.has(Blocks.CHISELED_RED_SANDSTONE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.CUT_RED_SANDSTONE_SLAB, 6).define(Character.valueOf('#'), Blocks.CUT_RED_SANDSTONE).pattern("###").unlockedBy("has_cut_red_sandstone", RecipeProvider.has(Blocks.CUT_RED_SANDSTONE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.RED_SANDSTONE_STAIRS, 4).define(Character.valueOf('#'), Ingredient.of(Blocks.RED_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE, Blocks.CUT_RED_SANDSTONE)).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_red_sandstone", RecipeProvider.has(Blocks.RED_SANDSTONE)).unlockedBy("has_chiseled_red_sandstone", RecipeProvider.has(Blocks.CHISELED_RED_SANDSTONE)).unlockedBy("has_cut_red_sandstone", RecipeProvider.has(Blocks.CUT_RED_SANDSTONE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.REPEATER).define(Character.valueOf('#'), Blocks.REDSTONE_TORCH).define(Character.valueOf('X'), Items.REDSTONE).define(Character.valueOf('I'), Blocks.STONE).pattern("#X#").pattern("III").unlockedBy("has_redstone_torch", RecipeProvider.has(Blocks.REDSTONE_TORCH)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.SANDSTONE).define(Character.valueOf('#'), Blocks.SAND).pattern("##").pattern("##").unlockedBy("has_sand", RecipeProvider.has(Blocks.SAND)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.SANDSTONE_SLAB, 6).define(Character.valueOf('#'), Ingredient.of(Blocks.SANDSTONE, Blocks.CHISELED_SANDSTONE)).pattern("###").unlockedBy("has_sandstone", RecipeProvider.has(Blocks.SANDSTONE)).unlockedBy("has_chiseled_sandstone", RecipeProvider.has(Blocks.CHISELED_SANDSTONE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.CUT_SANDSTONE_SLAB, 6).define(Character.valueOf('#'), Blocks.CUT_SANDSTONE).pattern("###").unlockedBy("has_cut_sandstone", RecipeProvider.has(Blocks.CUT_SANDSTONE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.SANDSTONE_STAIRS, 4).define(Character.valueOf('#'), Ingredient.of(Blocks.SANDSTONE, Blocks.CHISELED_SANDSTONE, Blocks.CUT_SANDSTONE)).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_sandstone", RecipeProvider.has(Blocks.SANDSTONE)).unlockedBy("has_chiseled_sandstone", RecipeProvider.has(Blocks.CHISELED_SANDSTONE)).unlockedBy("has_cut_sandstone", RecipeProvider.has(Blocks.CUT_SANDSTONE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.SEA_LANTERN).define(Character.valueOf('S'), Items.PRISMARINE_SHARD).define(Character.valueOf('C'), Items.PRISMARINE_CRYSTALS).pattern("SCS").pattern("CCC").pattern("SCS").unlockedBy("has_prismarine_crystals", RecipeProvider.has(Items.PRISMARINE_CRYSTALS)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.SHEARS).define(Character.valueOf('#'), Items.IRON_INGOT).pattern(" #").pattern("# ").unlockedBy("has_iron_ingot", RecipeProvider.has(Items.IRON_INGOT)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.SHIELD).define(Character.valueOf('W'), ItemTags.PLANKS).define(Character.valueOf('o'), Items.IRON_INGOT).pattern("WoW").pattern("WWW").pattern(" W ").unlockedBy("has_iron_ingot", RecipeProvider.has(Items.IRON_INGOT)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.SLIME_BLOCK).define(Character.valueOf('#'), Items.SLIME_BALL).pattern("###").pattern("###").pattern("###").unlockedBy("has_slime_ball", RecipeProvider.has(Items.SLIME_BALL)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.SLIME_BALL, 9).requires(Blocks.SLIME_BLOCK).unlockedBy("has_slime", RecipeProvider.has(Blocks.SLIME_BLOCK)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.CUT_RED_SANDSTONE, 4).define(Character.valueOf('#'), Blocks.RED_SANDSTONE).pattern("##").pattern("##").unlockedBy("has_red_sandstone", RecipeProvider.has(Blocks.RED_SANDSTONE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.CUT_SANDSTONE, 4).define(Character.valueOf('#'), Blocks.SANDSTONE).pattern("##").pattern("##").unlockedBy("has_sandstone", RecipeProvider.has(Blocks.SANDSTONE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.SNOW_BLOCK).define(Character.valueOf('#'), Items.SNOWBALL).pattern("##").pattern("##").unlockedBy("has_snowball", RecipeProvider.has(Items.SNOWBALL)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.SNOW, 6).define(Character.valueOf('#'), Blocks.SNOW_BLOCK).pattern("###").unlockedBy("has_snowball", RecipeProvider.has(Items.SNOWBALL)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.SOUL_CAMPFIRE).define(Character.valueOf('L'), ItemTags.LOGS).define(Character.valueOf('S'), Items.STICK).define(Character.valueOf('#'), ItemTags.SOUL_FIRE_BASE_BLOCKS).pattern(" S ").pattern("S#S").pattern("LLL").unlockedBy("has_stick", RecipeProvider.has(Items.STICK)).unlockedBy("has_soul_sand", RecipeProvider.has(ItemTags.SOUL_FIRE_BASE_BLOCKS)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.GLISTERING_MELON_SLICE).define(Character.valueOf('#'), Items.GOLD_NUGGET).define(Character.valueOf('X'), Items.MELON_SLICE).pattern("###").pattern("#X#").pattern("###").unlockedBy("has_melon", RecipeProvider.has(Items.MELON_SLICE)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.SPECTRAL_ARROW, 2).define(Character.valueOf('#'), Items.GLOWSTONE_DUST).define(Character.valueOf('X'), Items.ARROW).pattern(" # ").pattern("#X#").pattern(" # ").unlockedBy("has_glowstone_dust", RecipeProvider.has(Items.GLOWSTONE_DUST)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.STICK, 4).define(Character.valueOf('#'), ItemTags.PLANKS).pattern("#").pattern("#").group("sticks").unlockedBy("has_planks", RecipeProvider.has(ItemTags.PLANKS)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.STICK, 1).define(Character.valueOf('#'), Blocks.BAMBOO).pattern("#").pattern("#").group("sticks").unlockedBy("has_bamboo", RecipeProvider.has(Blocks.BAMBOO)).save(consumer, "stick_from_bamboo_item");
        ShapedRecipeBuilder.shaped(Blocks.STICKY_PISTON).define(Character.valueOf('P'), Blocks.PISTON).define(Character.valueOf('S'), Items.SLIME_BALL).pattern("S").pattern("P").unlockedBy("has_slime_ball", RecipeProvider.has(Items.SLIME_BALL)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.STONE_BRICKS, 4).define(Character.valueOf('#'), Blocks.STONE).pattern("##").pattern("##").unlockedBy("has_stone", RecipeProvider.has(Blocks.STONE)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.STONE_AXE).define(Character.valueOf('#'), Items.STICK).define(Character.valueOf('X'), ItemTags.STONE_TOOL_MATERIALS).pattern("XX").pattern("X#").pattern(" #").unlockedBy("has_cobblestone", RecipeProvider.has(ItemTags.STONE_TOOL_MATERIALS)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.STONE_BRICK_SLAB, 6).define(Character.valueOf('#'), Blocks.STONE_BRICKS).pattern("###").unlockedBy("has_stone_bricks", RecipeProvider.has(ItemTags.STONE_BRICKS)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.STONE_BRICK_STAIRS, 4).define(Character.valueOf('#'), Blocks.STONE_BRICKS).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_stone_bricks", RecipeProvider.has(ItemTags.STONE_BRICKS)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Blocks.STONE_BUTTON).requires(Blocks.STONE).unlockedBy("has_stone", RecipeProvider.has(Blocks.STONE)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.STONE_HOE).define(Character.valueOf('#'), Items.STICK).define(Character.valueOf('X'), ItemTags.STONE_TOOL_MATERIALS).pattern("XX").pattern(" #").pattern(" #").unlockedBy("has_cobblestone", RecipeProvider.has(ItemTags.STONE_TOOL_MATERIALS)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.STONE_PICKAXE).define(Character.valueOf('#'), Items.STICK).define(Character.valueOf('X'), ItemTags.STONE_TOOL_MATERIALS).pattern("XXX").pattern(" # ").pattern(" # ").unlockedBy("has_cobblestone", RecipeProvider.has(ItemTags.STONE_TOOL_MATERIALS)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.STONE_PRESSURE_PLATE).define(Character.valueOf('#'), Blocks.STONE).pattern("##").unlockedBy("has_stone", RecipeProvider.has(Blocks.STONE)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.STONE_SHOVEL).define(Character.valueOf('#'), Items.STICK).define(Character.valueOf('X'), ItemTags.STONE_TOOL_MATERIALS).pattern("X").pattern("#").pattern("#").unlockedBy("has_cobblestone", RecipeProvider.has(ItemTags.STONE_TOOL_MATERIALS)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.STONE_SLAB, 6).define(Character.valueOf('#'), Blocks.STONE).pattern("###").unlockedBy("has_stone", RecipeProvider.has(Blocks.STONE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.SMOOTH_STONE_SLAB, 6).define(Character.valueOf('#'), Blocks.SMOOTH_STONE).pattern("###").unlockedBy("has_smooth_stone", RecipeProvider.has(Blocks.SMOOTH_STONE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.COBBLESTONE_STAIRS, 4).define(Character.valueOf('#'), Blocks.COBBLESTONE).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_cobblestone", RecipeProvider.has(Blocks.COBBLESTONE)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.STONE_SWORD).define(Character.valueOf('#'), Items.STICK).define(Character.valueOf('X'), ItemTags.STONE_TOOL_MATERIALS).pattern("X").pattern("X").pattern("#").unlockedBy("has_cobblestone", RecipeProvider.has(ItemTags.STONE_TOOL_MATERIALS)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.WHITE_WOOL).define(Character.valueOf('#'), Items.STRING).pattern("##").pattern("##").unlockedBy("has_string", RecipeProvider.has(Items.STRING)).save(consumer, "white_wool_from_string");
        ShapelessRecipeBuilder.shapeless(Items.SUGAR).requires(Blocks.SUGAR_CANE).group("sugar").unlockedBy("has_reeds", RecipeProvider.has(Blocks.SUGAR_CANE)).save(consumer, "sugar_from_sugar_cane");
        ShapelessRecipeBuilder.shapeless(Items.SUGAR, 3).requires(Items.HONEY_BOTTLE).group("sugar").unlockedBy("has_honey_bottle", RecipeProvider.has(Items.HONEY_BOTTLE)).save(consumer, "sugar_from_honey_bottle");
        ShapedRecipeBuilder.shaped(Blocks.TARGET).define(Character.valueOf('H'), Items.HAY_BLOCK).define(Character.valueOf('R'), Items.REDSTONE).pattern(" R ").pattern("RHR").pattern(" R ").unlockedBy("has_redstone", RecipeProvider.has(Items.REDSTONE)).unlockedBy("has_hay_block", RecipeProvider.has(Blocks.HAY_BLOCK)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.TNT).define(Character.valueOf('#'), Ingredient.of(Blocks.SAND, Blocks.RED_SAND)).define(Character.valueOf('X'), Items.GUNPOWDER).pattern("X#X").pattern("#X#").pattern("X#X").unlockedBy("has_gunpowder", RecipeProvider.has(Items.GUNPOWDER)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.TNT_MINECART).define(Character.valueOf('A'), Blocks.TNT).define(Character.valueOf('B'), Items.MINECART).pattern("A").pattern("B").unlockedBy("has_minecart", RecipeProvider.has(Items.MINECART)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.TORCH, 4).define(Character.valueOf('#'), Items.STICK).define(Character.valueOf('X'), Ingredient.of(Items.COAL, Items.CHARCOAL)).pattern("X").pattern("#").unlockedBy("has_stone_pickaxe", RecipeProvider.has(Items.STONE_PICKAXE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.SOUL_TORCH, 4).define(Character.valueOf('X'), Ingredient.of(Items.COAL, Items.CHARCOAL)).define(Character.valueOf('#'), Items.STICK).define(Character.valueOf('S'), ItemTags.SOUL_FIRE_BASE_BLOCKS).pattern("X").pattern("#").pattern("S").unlockedBy("has_soul_sand", RecipeProvider.has(ItemTags.SOUL_FIRE_BASE_BLOCKS)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.LANTERN).define(Character.valueOf('#'), Items.TORCH).define(Character.valueOf('X'), Items.IRON_NUGGET).pattern("XXX").pattern("X#X").pattern("XXX").unlockedBy("has_iron_nugget", RecipeProvider.has(Items.IRON_NUGGET)).unlockedBy("has_iron_ingot", RecipeProvider.has(Items.IRON_INGOT)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.SOUL_LANTERN).define(Character.valueOf('#'), Items.SOUL_TORCH).define(Character.valueOf('X'), Items.IRON_NUGGET).pattern("XXX").pattern("X#X").pattern("XXX").unlockedBy("has_soul_torch", RecipeProvider.has(Items.SOUL_TORCH)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Blocks.TRAPPED_CHEST).requires(Blocks.CHEST).requires(Blocks.TRIPWIRE_HOOK).unlockedBy("has_tripwire_hook", RecipeProvider.has(Blocks.TRIPWIRE_HOOK)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.TRIPWIRE_HOOK, 2).define(Character.valueOf('#'), ItemTags.PLANKS).define(Character.valueOf('S'), Items.STICK).define(Character.valueOf('I'), Items.IRON_INGOT).pattern("I").pattern("S").pattern("#").unlockedBy("has_string", RecipeProvider.has(Items.STRING)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.TURTLE_HELMET).define(Character.valueOf('X'), Items.SCUTE).pattern("XXX").pattern("X X").unlockedBy("has_scute", RecipeProvider.has(Items.SCUTE)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.WHEAT, 9).requires(Blocks.HAY_BLOCK).unlockedBy("has_hay_block", RecipeProvider.has(Blocks.HAY_BLOCK)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.WHITE_DYE).requires(Items.BONE_MEAL).group("white_dye").unlockedBy("has_bone_meal", RecipeProvider.has(Items.BONE_MEAL)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.WHITE_DYE).requires(Blocks.LILY_OF_THE_VALLEY).group("white_dye").unlockedBy("has_white_flower", RecipeProvider.has(Blocks.LILY_OF_THE_VALLEY)).save(consumer, "white_dye_from_lily_of_the_valley");
        ShapedRecipeBuilder.shaped(Items.WOODEN_AXE).define(Character.valueOf('#'), Items.STICK).define(Character.valueOf('X'), ItemTags.PLANKS).pattern("XX").pattern("X#").pattern(" #").unlockedBy("has_stick", RecipeProvider.has(Items.STICK)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.WOODEN_HOE).define(Character.valueOf('#'), Items.STICK).define(Character.valueOf('X'), ItemTags.PLANKS).pattern("XX").pattern(" #").pattern(" #").unlockedBy("has_stick", RecipeProvider.has(Items.STICK)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.WOODEN_PICKAXE).define(Character.valueOf('#'), Items.STICK).define(Character.valueOf('X'), ItemTags.PLANKS).pattern("XXX").pattern(" # ").pattern(" # ").unlockedBy("has_stick", RecipeProvider.has(Items.STICK)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.WOODEN_SHOVEL).define(Character.valueOf('#'), Items.STICK).define(Character.valueOf('X'), ItemTags.PLANKS).pattern("X").pattern("#").pattern("#").unlockedBy("has_stick", RecipeProvider.has(Items.STICK)).save(consumer);
        ShapedRecipeBuilder.shaped(Items.WOODEN_SWORD).define(Character.valueOf('#'), Items.STICK).define(Character.valueOf('X'), ItemTags.PLANKS).pattern("X").pattern("X").pattern("#").unlockedBy("has_stick", RecipeProvider.has(Items.STICK)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.WRITABLE_BOOK).requires(Items.BOOK).requires(Items.INK_SAC).requires(Items.FEATHER).unlockedBy("has_book", RecipeProvider.has(Items.BOOK)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.YELLOW_DYE).requires(Blocks.DANDELION).group("yellow_dye").unlockedBy("has_yellow_flower", RecipeProvider.has(Blocks.DANDELION)).save(consumer, "yellow_dye_from_dandelion");
        ShapelessRecipeBuilder.shapeless(Items.YELLOW_DYE, 2).requires(Blocks.SUNFLOWER).group("yellow_dye").unlockedBy("has_double_plant", RecipeProvider.has(Blocks.SUNFLOWER)).save(consumer, "yellow_dye_from_sunflower");
        ShapelessRecipeBuilder.shapeless(Items.DRIED_KELP, 9).requires(Blocks.DRIED_KELP_BLOCK).unlockedBy("has_dried_kelp_block", RecipeProvider.has(Blocks.DRIED_KELP_BLOCK)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Blocks.DRIED_KELP_BLOCK).requires(Items.DRIED_KELP, 9).unlockedBy("has_dried_kelp", RecipeProvider.has(Items.DRIED_KELP)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.CONDUIT).define(Character.valueOf('#'), Items.NAUTILUS_SHELL).define(Character.valueOf('X'), Items.HEART_OF_THE_SEA).pattern("###").pattern("#X#").pattern("###").unlockedBy("has_nautilus_core", RecipeProvider.has(Items.HEART_OF_THE_SEA)).unlockedBy("has_nautilus_shell", RecipeProvider.has(Items.NAUTILUS_SHELL)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.POLISHED_GRANITE_STAIRS, 4).define(Character.valueOf('#'), Blocks.POLISHED_GRANITE).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_polished_granite", RecipeProvider.has(Blocks.POLISHED_GRANITE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.SMOOTH_RED_SANDSTONE_STAIRS, 4).define(Character.valueOf('#'), Blocks.SMOOTH_RED_SANDSTONE).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_smooth_red_sandstone", RecipeProvider.has(Blocks.SMOOTH_RED_SANDSTONE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.MOSSY_STONE_BRICK_STAIRS, 4).define(Character.valueOf('#'), Blocks.MOSSY_STONE_BRICKS).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_mossy_stone_bricks", RecipeProvider.has(Blocks.MOSSY_STONE_BRICKS)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.POLISHED_DIORITE_STAIRS, 4).define(Character.valueOf('#'), Blocks.POLISHED_DIORITE).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_polished_diorite", RecipeProvider.has(Blocks.POLISHED_DIORITE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.MOSSY_COBBLESTONE_STAIRS, 4).define(Character.valueOf('#'), Blocks.MOSSY_COBBLESTONE).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_mossy_cobblestone", RecipeProvider.has(Blocks.MOSSY_COBBLESTONE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.END_STONE_BRICK_STAIRS, 4).define(Character.valueOf('#'), Blocks.END_STONE_BRICKS).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_end_stone_bricks", RecipeProvider.has(Blocks.END_STONE_BRICKS)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.STONE_STAIRS, 4).define(Character.valueOf('#'), Blocks.STONE).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_stone", RecipeProvider.has(Blocks.STONE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.SMOOTH_SANDSTONE_STAIRS, 4).define(Character.valueOf('#'), Blocks.SMOOTH_SANDSTONE).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_smooth_sandstone", RecipeProvider.has(Blocks.SMOOTH_SANDSTONE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.SMOOTH_QUARTZ_STAIRS, 4).define(Character.valueOf('#'), Blocks.SMOOTH_QUARTZ).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_smooth_quartz", RecipeProvider.has(Blocks.SMOOTH_QUARTZ)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.GRANITE_STAIRS, 4).define(Character.valueOf('#'), Blocks.GRANITE).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_granite", RecipeProvider.has(Blocks.GRANITE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.ANDESITE_STAIRS, 4).define(Character.valueOf('#'), Blocks.ANDESITE).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_andesite", RecipeProvider.has(Blocks.ANDESITE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.RED_NETHER_BRICK_STAIRS, 4).define(Character.valueOf('#'), Blocks.RED_NETHER_BRICKS).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_red_nether_bricks", RecipeProvider.has(Blocks.RED_NETHER_BRICKS)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.POLISHED_ANDESITE_STAIRS, 4).define(Character.valueOf('#'), Blocks.POLISHED_ANDESITE).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_polished_andesite", RecipeProvider.has(Blocks.POLISHED_ANDESITE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.DIORITE_STAIRS, 4).define(Character.valueOf('#'), Blocks.DIORITE).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_diorite", RecipeProvider.has(Blocks.DIORITE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.POLISHED_GRANITE_SLAB, 6).define(Character.valueOf('#'), Blocks.POLISHED_GRANITE).pattern("###").unlockedBy("has_polished_granite", RecipeProvider.has(Blocks.POLISHED_GRANITE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.SMOOTH_RED_SANDSTONE_SLAB, 6).define(Character.valueOf('#'), Blocks.SMOOTH_RED_SANDSTONE).pattern("###").unlockedBy("has_smooth_red_sandstone", RecipeProvider.has(Blocks.SMOOTH_RED_SANDSTONE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.MOSSY_STONE_BRICK_SLAB, 6).define(Character.valueOf('#'), Blocks.MOSSY_STONE_BRICKS).pattern("###").unlockedBy("has_mossy_stone_bricks", RecipeProvider.has(Blocks.MOSSY_STONE_BRICKS)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.POLISHED_DIORITE_SLAB, 6).define(Character.valueOf('#'), Blocks.POLISHED_DIORITE).pattern("###").unlockedBy("has_polished_diorite", RecipeProvider.has(Blocks.POLISHED_DIORITE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.MOSSY_COBBLESTONE_SLAB, 6).define(Character.valueOf('#'), Blocks.MOSSY_COBBLESTONE).pattern("###").unlockedBy("has_mossy_cobblestone", RecipeProvider.has(Blocks.MOSSY_COBBLESTONE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.END_STONE_BRICK_SLAB, 6).define(Character.valueOf('#'), Blocks.END_STONE_BRICKS).pattern("###").unlockedBy("has_end_stone_bricks", RecipeProvider.has(Blocks.END_STONE_BRICKS)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.SMOOTH_SANDSTONE_SLAB, 6).define(Character.valueOf('#'), Blocks.SMOOTH_SANDSTONE).pattern("###").unlockedBy("has_smooth_sandstone", RecipeProvider.has(Blocks.SMOOTH_SANDSTONE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.SMOOTH_QUARTZ_SLAB, 6).define(Character.valueOf('#'), Blocks.SMOOTH_QUARTZ).pattern("###").unlockedBy("has_smooth_quartz", RecipeProvider.has(Blocks.SMOOTH_QUARTZ)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.GRANITE_SLAB, 6).define(Character.valueOf('#'), Blocks.GRANITE).pattern("###").unlockedBy("has_granite", RecipeProvider.has(Blocks.GRANITE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.ANDESITE_SLAB, 6).define(Character.valueOf('#'), Blocks.ANDESITE).pattern("###").unlockedBy("has_andesite", RecipeProvider.has(Blocks.ANDESITE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.RED_NETHER_BRICK_SLAB, 6).define(Character.valueOf('#'), Blocks.RED_NETHER_BRICKS).pattern("###").unlockedBy("has_red_nether_bricks", RecipeProvider.has(Blocks.RED_NETHER_BRICKS)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.POLISHED_ANDESITE_SLAB, 6).define(Character.valueOf('#'), Blocks.POLISHED_ANDESITE).pattern("###").unlockedBy("has_polished_andesite", RecipeProvider.has(Blocks.POLISHED_ANDESITE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.DIORITE_SLAB, 6).define(Character.valueOf('#'), Blocks.DIORITE).pattern("###").unlockedBy("has_diorite", RecipeProvider.has(Blocks.DIORITE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.BRICK_WALL, 6).define(Character.valueOf('#'), Blocks.BRICKS).pattern("###").pattern("###").unlockedBy("has_bricks", RecipeProvider.has(Blocks.BRICKS)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.PRISMARINE_WALL, 6).define(Character.valueOf('#'), Blocks.PRISMARINE).pattern("###").pattern("###").unlockedBy("has_prismarine", RecipeProvider.has(Blocks.PRISMARINE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.RED_SANDSTONE_WALL, 6).define(Character.valueOf('#'), Blocks.RED_SANDSTONE).pattern("###").pattern("###").unlockedBy("has_red_sandstone", RecipeProvider.has(Blocks.RED_SANDSTONE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.MOSSY_STONE_BRICK_WALL, 6).define(Character.valueOf('#'), Blocks.MOSSY_STONE_BRICKS).pattern("###").pattern("###").unlockedBy("has_mossy_stone_bricks", RecipeProvider.has(Blocks.MOSSY_STONE_BRICKS)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.GRANITE_WALL, 6).define(Character.valueOf('#'), Blocks.GRANITE).pattern("###").pattern("###").unlockedBy("has_granite", RecipeProvider.has(Blocks.GRANITE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.STONE_BRICK_WALL, 6).define(Character.valueOf('#'), Blocks.STONE_BRICKS).pattern("###").pattern("###").unlockedBy("has_stone_bricks", RecipeProvider.has(Blocks.STONE_BRICKS)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.NETHER_BRICK_WALL, 6).define(Character.valueOf('#'), Blocks.NETHER_BRICKS).pattern("###").pattern("###").unlockedBy("has_nether_bricks", RecipeProvider.has(Blocks.NETHER_BRICKS)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.ANDESITE_WALL, 6).define(Character.valueOf('#'), Blocks.ANDESITE).pattern("###").pattern("###").unlockedBy("has_andesite", RecipeProvider.has(Blocks.ANDESITE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.RED_NETHER_BRICK_WALL, 6).define(Character.valueOf('#'), Blocks.RED_NETHER_BRICKS).pattern("###").pattern("###").unlockedBy("has_red_nether_bricks", RecipeProvider.has(Blocks.RED_NETHER_BRICKS)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.SANDSTONE_WALL, 6).define(Character.valueOf('#'), Blocks.SANDSTONE).pattern("###").pattern("###").unlockedBy("has_sandstone", RecipeProvider.has(Blocks.SANDSTONE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.END_STONE_BRICK_WALL, 6).define(Character.valueOf('#'), Blocks.END_STONE_BRICKS).pattern("###").pattern("###").unlockedBy("has_end_stone_bricks", RecipeProvider.has(Blocks.END_STONE_BRICKS)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.DIORITE_WALL, 6).define(Character.valueOf('#'), Blocks.DIORITE).pattern("###").pattern("###").unlockedBy("has_diorite", RecipeProvider.has(Blocks.DIORITE)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.CREEPER_BANNER_PATTERN).requires(Items.PAPER).requires(Items.CREEPER_HEAD).unlockedBy("has_creeper_head", RecipeProvider.has(Items.CREEPER_HEAD)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.SKULL_BANNER_PATTERN).requires(Items.PAPER).requires(Items.WITHER_SKELETON_SKULL).unlockedBy("has_wither_skeleton_skull", RecipeProvider.has(Items.WITHER_SKELETON_SKULL)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.FLOWER_BANNER_PATTERN).requires(Items.PAPER).requires(Blocks.OXEYE_DAISY).unlockedBy("has_oxeye_daisy", RecipeProvider.has(Blocks.OXEYE_DAISY)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.MOJANG_BANNER_PATTERN).requires(Items.PAPER).requires(Items.ENCHANTED_GOLDEN_APPLE).unlockedBy("has_enchanted_golden_apple", RecipeProvider.has(Items.ENCHANTED_GOLDEN_APPLE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.SCAFFOLDING, 6).define(Character.valueOf('~'), Items.STRING).define(Character.valueOf('I'), Blocks.BAMBOO).pattern("I~I").pattern("I I").pattern("I I").unlockedBy("has_bamboo", RecipeProvider.has(Blocks.BAMBOO)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.GRINDSTONE).define(Character.valueOf('I'), Items.STICK).define(Character.valueOf('-'), Blocks.STONE_SLAB).define(Character.valueOf('#'), ItemTags.PLANKS).pattern("I-I").pattern("# #").unlockedBy("has_stone_slab", RecipeProvider.has(Blocks.STONE_SLAB)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.BLAST_FURNACE).define(Character.valueOf('#'), Blocks.SMOOTH_STONE).define(Character.valueOf('X'), Blocks.FURNACE).define(Character.valueOf('I'), Items.IRON_INGOT).pattern("III").pattern("IXI").pattern("###").unlockedBy("has_smooth_stone", RecipeProvider.has(Blocks.SMOOTH_STONE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.SMOKER).define(Character.valueOf('#'), ItemTags.LOGS).define(Character.valueOf('X'), Blocks.FURNACE).pattern(" # ").pattern("#X#").pattern(" # ").unlockedBy("has_furnace", RecipeProvider.has(Blocks.FURNACE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.CARTOGRAPHY_TABLE).define(Character.valueOf('#'), ItemTags.PLANKS).define(Character.valueOf('@'), Items.PAPER).pattern("@@").pattern("##").pattern("##").unlockedBy("has_paper", RecipeProvider.has(Items.PAPER)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.SMITHING_TABLE).define(Character.valueOf('#'), ItemTags.PLANKS).define(Character.valueOf('@'), Items.IRON_INGOT).pattern("@@").pattern("##").pattern("##").unlockedBy("has_iron_ingot", RecipeProvider.has(Items.IRON_INGOT)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.FLETCHING_TABLE).define(Character.valueOf('#'), ItemTags.PLANKS).define(Character.valueOf('@'), Items.FLINT).pattern("@@").pattern("##").pattern("##").unlockedBy("has_flint", RecipeProvider.has(Items.FLINT)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.STONECUTTER).define(Character.valueOf('I'), Items.IRON_INGOT).define(Character.valueOf('#'), Blocks.STONE).pattern(" I ").pattern("###").unlockedBy("has_stone", RecipeProvider.has(Blocks.STONE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.LODESTONE).define(Character.valueOf('S'), Items.CHISELED_STONE_BRICKS).define(Character.valueOf('#'), Items.NETHERITE_INGOT).pattern("SSS").pattern("S#S").pattern("SSS").unlockedBy("has_netherite_ingot", RecipeProvider.has(Items.NETHERITE_INGOT)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.NETHERITE_BLOCK).define(Character.valueOf('#'), Items.NETHERITE_INGOT).pattern("###").pattern("###").pattern("###").unlockedBy("has_netherite_ingot", RecipeProvider.has(Items.NETHERITE_INGOT)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Items.NETHERITE_INGOT, 9).requires(Blocks.NETHERITE_BLOCK).group("netherite_ingot").unlockedBy("has_netherite_block", RecipeProvider.has(Blocks.NETHERITE_BLOCK)).save(consumer, "netherite_ingot_from_netherite_block");
        ShapelessRecipeBuilder.shapeless(Items.NETHERITE_INGOT).requires(Items.NETHERITE_SCRAP, 4).requires(Items.GOLD_INGOT, 4).group("netherite_ingot").unlockedBy("has_netherite_scrap", RecipeProvider.has(Items.NETHERITE_SCRAP)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.RESPAWN_ANCHOR).define(Character.valueOf('O'), Blocks.CRYING_OBSIDIAN).define(Character.valueOf('G'), Blocks.GLOWSTONE).pattern("OOO").pattern("GGG").pattern("OOO").unlockedBy("has_obsidian", RecipeProvider.has(Blocks.CRYING_OBSIDIAN)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.BLACKSTONE_STAIRS, 4).define(Character.valueOf('#'), Blocks.BLACKSTONE).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_blackstone", RecipeProvider.has(Blocks.BLACKSTONE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.POLISHED_BLACKSTONE_STAIRS, 4).define(Character.valueOf('#'), Blocks.POLISHED_BLACKSTONE).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_polished_blackstone", RecipeProvider.has(Blocks.POLISHED_BLACKSTONE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, 4).define(Character.valueOf('#'), Blocks.POLISHED_BLACKSTONE_BRICKS).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_polished_blackstone_bricks", RecipeProvider.has(Blocks.POLISHED_BLACKSTONE_BRICKS)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.BLACKSTONE_SLAB, 6).define(Character.valueOf('#'), Blocks.BLACKSTONE).pattern("###").unlockedBy("has_blackstone", RecipeProvider.has(Blocks.BLACKSTONE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.POLISHED_BLACKSTONE_SLAB, 6).define(Character.valueOf('#'), Blocks.POLISHED_BLACKSTONE).pattern("###").unlockedBy("has_polished_blackstone", RecipeProvider.has(Blocks.POLISHED_BLACKSTONE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, 6).define(Character.valueOf('#'), Blocks.POLISHED_BLACKSTONE_BRICKS).pattern("###").unlockedBy("has_polished_blackstone_bricks", RecipeProvider.has(Blocks.POLISHED_BLACKSTONE_BRICKS)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.POLISHED_BLACKSTONE, 4).define(Character.valueOf('S'), Blocks.BLACKSTONE).pattern("SS").pattern("SS").unlockedBy("has_blackstone", RecipeProvider.has(Blocks.BLACKSTONE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.POLISHED_BLACKSTONE_BRICKS, 4).define(Character.valueOf('#'), Blocks.POLISHED_BLACKSTONE).pattern("##").pattern("##").unlockedBy("has_polished_blackstone", RecipeProvider.has(Blocks.POLISHED_BLACKSTONE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.CHISELED_POLISHED_BLACKSTONE).define(Character.valueOf('#'), Blocks.POLISHED_BLACKSTONE_SLAB).pattern("#").pattern("#").unlockedBy("has_polished_blackstone", RecipeProvider.has(Blocks.POLISHED_BLACKSTONE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.BLACKSTONE_WALL, 6).define(Character.valueOf('#'), Blocks.BLACKSTONE).pattern("###").pattern("###").unlockedBy("has_blackstone", RecipeProvider.has(Blocks.BLACKSTONE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.POLISHED_BLACKSTONE_WALL, 6).define(Character.valueOf('#'), Blocks.POLISHED_BLACKSTONE).pattern("###").pattern("###").unlockedBy("has_polished_blackstone", RecipeProvider.has(Blocks.POLISHED_BLACKSTONE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.POLISHED_BLACKSTONE_BRICK_WALL, 6).define(Character.valueOf('#'), Blocks.POLISHED_BLACKSTONE_BRICKS).pattern("###").pattern("###").unlockedBy("has_polished_blackstone_bricks", RecipeProvider.has(Blocks.POLISHED_BLACKSTONE_BRICKS)).save(consumer);
        ShapelessRecipeBuilder.shapeless(Blocks.POLISHED_BLACKSTONE_BUTTON).requires(Blocks.POLISHED_BLACKSTONE).unlockedBy("has_polished_blackstone", RecipeProvider.has(Blocks.POLISHED_BLACKSTONE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE).define(Character.valueOf('#'), Blocks.POLISHED_BLACKSTONE).pattern("##").unlockedBy("has_polished_blackstone", RecipeProvider.has(Blocks.POLISHED_BLACKSTONE)).save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.CHAIN).define(Character.valueOf('I'), Items.IRON_INGOT).define(Character.valueOf('N'), Items.IRON_NUGGET).pattern("N").pattern("I").pattern("N").unlockedBy("has_iron_nugget", RecipeProvider.has(Items.IRON_NUGGET)).unlockedBy("has_iron_ingot", RecipeProvider.has(Items.IRON_INGOT)).save(consumer);
        SpecialRecipeBuilder.special(RecipeSerializer.ARMOR_DYE).save(consumer, "armor_dye");
        SpecialRecipeBuilder.special(RecipeSerializer.BANNER_DUPLICATE).save(consumer, "banner_duplicate");
        SpecialRecipeBuilder.special(RecipeSerializer.BOOK_CLONING).save(consumer, "book_cloning");
        SpecialRecipeBuilder.special(RecipeSerializer.FIREWORK_ROCKET).save(consumer, "firework_rocket");
        SpecialRecipeBuilder.special(RecipeSerializer.FIREWORK_STAR).save(consumer, "firework_star");
        SpecialRecipeBuilder.special(RecipeSerializer.FIREWORK_STAR_FADE).save(consumer, "firework_star_fade");
        SpecialRecipeBuilder.special(RecipeSerializer.MAP_CLONING).save(consumer, "map_cloning");
        SpecialRecipeBuilder.special(RecipeSerializer.MAP_EXTENDING).save(consumer, "map_extending");
        SpecialRecipeBuilder.special(RecipeSerializer.REPAIR_ITEM).save(consumer, "repair_item");
        SpecialRecipeBuilder.special(RecipeSerializer.SHIELD_DECORATION).save(consumer, "shield_decoration");
        SpecialRecipeBuilder.special(RecipeSerializer.SHULKER_BOX_COLORING).save(consumer, "shulker_box_coloring");
        SpecialRecipeBuilder.special(RecipeSerializer.TIPPED_ARROW).save(consumer, "tipped_arrow");
        SpecialRecipeBuilder.special(RecipeSerializer.SUSPICIOUS_STEW).save(consumer, "suspicious_stew");
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.POTATO), Items.BAKED_POTATO, 0.35f, 200).unlockedBy("has_potato", RecipeProvider.has(Items.POTATO)).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.CLAY_BALL), Items.BRICK, 0.3f, 200).unlockedBy("has_clay_ball", RecipeProvider.has(Items.CLAY_BALL)).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ItemTags.LOGS_THAT_BURN), Items.CHARCOAL, 0.15f, 200).unlockedBy("has_log", RecipeProvider.has(ItemTags.LOGS_THAT_BURN)).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.CHORUS_FRUIT), Items.POPPED_CHORUS_FRUIT, 0.1f, 200).unlockedBy("has_chorus_fruit", RecipeProvider.has(Items.CHORUS_FRUIT)).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.COAL_ORE.asItem()), Items.COAL, 0.1f, 200).unlockedBy("has_coal_ore", RecipeProvider.has(Blocks.COAL_ORE)).save(consumer, "coal_from_smelting");
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.BEEF), Items.COOKED_BEEF, 0.35f, 200).unlockedBy("has_beef", RecipeProvider.has(Items.BEEF)).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.CHICKEN), Items.COOKED_CHICKEN, 0.35f, 200).unlockedBy("has_chicken", RecipeProvider.has(Items.CHICKEN)).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.COD), Items.COOKED_COD, 0.35f, 200).unlockedBy("has_cod", RecipeProvider.has(Items.COD)).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.KELP), Items.DRIED_KELP, 0.1f, 200).unlockedBy("has_kelp", RecipeProvider.has(Blocks.KELP)).save(consumer, "dried_kelp_from_smelting");
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.SALMON), Items.COOKED_SALMON, 0.35f, 200).unlockedBy("has_salmon", RecipeProvider.has(Items.SALMON)).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.MUTTON), Items.COOKED_MUTTON, 0.35f, 200).unlockedBy("has_mutton", RecipeProvider.has(Items.MUTTON)).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.PORKCHOP), Items.COOKED_PORKCHOP, 0.35f, 200).unlockedBy("has_porkchop", RecipeProvider.has(Items.PORKCHOP)).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.RABBIT), Items.COOKED_RABBIT, 0.35f, 200).unlockedBy("has_rabbit", RecipeProvider.has(Items.RABBIT)).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.DIAMOND_ORE.asItem()), Items.DIAMOND, 1.0f, 200).unlockedBy("has_diamond_ore", RecipeProvider.has(Blocks.DIAMOND_ORE)).save(consumer, "diamond_from_smelting");
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.LAPIS_ORE.asItem()), Items.LAPIS_LAZULI, 0.2f, 200).unlockedBy("has_lapis_ore", RecipeProvider.has(Blocks.LAPIS_ORE)).save(consumer, "lapis_from_smelting");
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.EMERALD_ORE.asItem()), Items.EMERALD, 1.0f, 200).unlockedBy("has_emerald_ore", RecipeProvider.has(Blocks.EMERALD_ORE)).save(consumer, "emerald_from_smelting");
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ItemTags.SAND), Blocks.GLASS.asItem(), 0.1f, 200).unlockedBy("has_sand", RecipeProvider.has(ItemTags.SAND)).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ItemTags.GOLD_ORES), Items.GOLD_INGOT, 1.0f, 200).unlockedBy("has_gold_ore", RecipeProvider.has(ItemTags.GOLD_ORES)).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.SEA_PICKLE.asItem()), Items.LIME_DYE, 0.1f, 200).unlockedBy("has_sea_pickle", RecipeProvider.has(Blocks.SEA_PICKLE)).save(consumer, "lime_dye_from_smelting");
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.CACTUS.asItem()), Items.GREEN_DYE, 1.0f, 200).unlockedBy("has_cactus", RecipeProvider.has(Blocks.CACTUS)).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.GOLDEN_PICKAXE, Items.GOLDEN_SHOVEL, Items.GOLDEN_AXE, Items.GOLDEN_HOE, Items.GOLDEN_SWORD, Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS, Items.GOLDEN_HORSE_ARMOR), Items.GOLD_NUGGET, 0.1f, 200).unlockedBy("has_golden_pickaxe", RecipeProvider.has(Items.GOLDEN_PICKAXE)).unlockedBy("has_golden_shovel", RecipeProvider.has(Items.GOLDEN_SHOVEL)).unlockedBy("has_golden_axe", RecipeProvider.has(Items.GOLDEN_AXE)).unlockedBy("has_golden_hoe", RecipeProvider.has(Items.GOLDEN_HOE)).unlockedBy("has_golden_sword", RecipeProvider.has(Items.GOLDEN_SWORD)).unlockedBy("has_golden_helmet", RecipeProvider.has(Items.GOLDEN_HELMET)).unlockedBy("has_golden_chestplate", RecipeProvider.has(Items.GOLDEN_CHESTPLATE)).unlockedBy("has_golden_leggings", RecipeProvider.has(Items.GOLDEN_LEGGINGS)).unlockedBy("has_golden_boots", RecipeProvider.has(Items.GOLDEN_BOOTS)).unlockedBy("has_golden_horse_armor", RecipeProvider.has(Items.GOLDEN_HORSE_ARMOR)).save(consumer, "gold_nugget_from_smelting");
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.IRON_PICKAXE, Items.IRON_SHOVEL, Items.IRON_AXE, Items.IRON_HOE, Items.IRON_SWORD, Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS, Items.IRON_HORSE_ARMOR, Items.CHAINMAIL_HELMET, Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_LEGGINGS, Items.CHAINMAIL_BOOTS), Items.IRON_NUGGET, 0.1f, 200).unlockedBy("has_iron_pickaxe", RecipeProvider.has(Items.IRON_PICKAXE)).unlockedBy("has_iron_shovel", RecipeProvider.has(Items.IRON_SHOVEL)).unlockedBy("has_iron_axe", RecipeProvider.has(Items.IRON_AXE)).unlockedBy("has_iron_hoe", RecipeProvider.has(Items.IRON_HOE)).unlockedBy("has_iron_sword", RecipeProvider.has(Items.IRON_SWORD)).unlockedBy("has_iron_helmet", RecipeProvider.has(Items.IRON_HELMET)).unlockedBy("has_iron_chestplate", RecipeProvider.has(Items.IRON_CHESTPLATE)).unlockedBy("has_iron_leggings", RecipeProvider.has(Items.IRON_LEGGINGS)).unlockedBy("has_iron_boots", RecipeProvider.has(Items.IRON_BOOTS)).unlockedBy("has_iron_horse_armor", RecipeProvider.has(Items.IRON_HORSE_ARMOR)).unlockedBy("has_chainmail_helmet", RecipeProvider.has(Items.CHAINMAIL_HELMET)).unlockedBy("has_chainmail_chestplate", RecipeProvider.has(Items.CHAINMAIL_CHESTPLATE)).unlockedBy("has_chainmail_leggings", RecipeProvider.has(Items.CHAINMAIL_LEGGINGS)).unlockedBy("has_chainmail_boots", RecipeProvider.has(Items.CHAINMAIL_BOOTS)).save(consumer, "iron_nugget_from_smelting");
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.IRON_ORE.asItem()), Items.IRON_INGOT, 0.7f, 200).unlockedBy("has_iron_ore", RecipeProvider.has(Blocks.IRON_ORE.asItem())).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.CLAY), Blocks.TERRACOTTA.asItem(), 0.35f, 200).unlockedBy("has_clay_block", RecipeProvider.has(Blocks.CLAY)).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.NETHERRACK), Items.NETHER_BRICK, 0.1f, 200).unlockedBy("has_netherrack", RecipeProvider.has(Blocks.NETHERRACK)).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.NETHER_QUARTZ_ORE), Items.QUARTZ, 0.2f, 200).unlockedBy("has_nether_quartz_ore", RecipeProvider.has(Blocks.NETHER_QUARTZ_ORE)).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.REDSTONE_ORE), Items.REDSTONE, 0.7f, 200).unlockedBy("has_redstone_ore", RecipeProvider.has(Blocks.REDSTONE_ORE)).save(consumer, "redstone_from_smelting");
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.WET_SPONGE), Blocks.SPONGE.asItem(), 0.15f, 200).unlockedBy("has_wet_sponge", RecipeProvider.has(Blocks.WET_SPONGE)).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.COBBLESTONE), Blocks.STONE.asItem(), 0.1f, 200).unlockedBy("has_cobblestone", RecipeProvider.has(Blocks.COBBLESTONE)).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.STONE), Blocks.SMOOTH_STONE.asItem(), 0.1f, 200).unlockedBy("has_stone", RecipeProvider.has(Blocks.STONE)).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.SANDSTONE), Blocks.SMOOTH_SANDSTONE.asItem(), 0.1f, 200).unlockedBy("has_sandstone", RecipeProvider.has(Blocks.SANDSTONE)).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.RED_SANDSTONE), Blocks.SMOOTH_RED_SANDSTONE.asItem(), 0.1f, 200).unlockedBy("has_red_sandstone", RecipeProvider.has(Blocks.RED_SANDSTONE)).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.QUARTZ_BLOCK), Blocks.SMOOTH_QUARTZ.asItem(), 0.1f, 200).unlockedBy("has_quartz_block", RecipeProvider.has(Blocks.QUARTZ_BLOCK)).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.STONE_BRICKS), Blocks.CRACKED_STONE_BRICKS.asItem(), 0.1f, 200).unlockedBy("has_stone_bricks", RecipeProvider.has(Blocks.STONE_BRICKS)).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.BLACK_TERRACOTTA), Blocks.BLACK_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).unlockedBy("has_black_terracotta", RecipeProvider.has(Blocks.BLACK_TERRACOTTA)).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.BLUE_TERRACOTTA), Blocks.BLUE_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).unlockedBy("has_blue_terracotta", RecipeProvider.has(Blocks.BLUE_TERRACOTTA)).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.BROWN_TERRACOTTA), Blocks.BROWN_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).unlockedBy("has_brown_terracotta", RecipeProvider.has(Blocks.BROWN_TERRACOTTA)).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.CYAN_TERRACOTTA), Blocks.CYAN_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).unlockedBy("has_cyan_terracotta", RecipeProvider.has(Blocks.CYAN_TERRACOTTA)).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.GRAY_TERRACOTTA), Blocks.GRAY_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).unlockedBy("has_gray_terracotta", RecipeProvider.has(Blocks.GRAY_TERRACOTTA)).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.GREEN_TERRACOTTA), Blocks.GREEN_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).unlockedBy("has_green_terracotta", RecipeProvider.has(Blocks.GREEN_TERRACOTTA)).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.LIGHT_BLUE_TERRACOTTA), Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).unlockedBy("has_light_blue_terracotta", RecipeProvider.has(Blocks.LIGHT_BLUE_TERRACOTTA)).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.LIGHT_GRAY_TERRACOTTA), Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).unlockedBy("has_light_gray_terracotta", RecipeProvider.has(Blocks.LIGHT_GRAY_TERRACOTTA)).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.LIME_TERRACOTTA), Blocks.LIME_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).unlockedBy("has_lime_terracotta", RecipeProvider.has(Blocks.LIME_TERRACOTTA)).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.MAGENTA_TERRACOTTA), Blocks.MAGENTA_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).unlockedBy("has_magenta_terracotta", RecipeProvider.has(Blocks.MAGENTA_TERRACOTTA)).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.ORANGE_TERRACOTTA), Blocks.ORANGE_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).unlockedBy("has_orange_terracotta", RecipeProvider.has(Blocks.ORANGE_TERRACOTTA)).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.PINK_TERRACOTTA), Blocks.PINK_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).unlockedBy("has_pink_terracotta", RecipeProvider.has(Blocks.PINK_TERRACOTTA)).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.PURPLE_TERRACOTTA), Blocks.PURPLE_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).unlockedBy("has_purple_terracotta", RecipeProvider.has(Blocks.PURPLE_TERRACOTTA)).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.RED_TERRACOTTA), Blocks.RED_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).unlockedBy("has_red_terracotta", RecipeProvider.has(Blocks.RED_TERRACOTTA)).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.WHITE_TERRACOTTA), Blocks.WHITE_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).unlockedBy("has_white_terracotta", RecipeProvider.has(Blocks.WHITE_TERRACOTTA)).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.YELLOW_TERRACOTTA), Blocks.YELLOW_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).unlockedBy("has_yellow_terracotta", RecipeProvider.has(Blocks.YELLOW_TERRACOTTA)).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.ANCIENT_DEBRIS), Items.NETHERITE_SCRAP, 2.0f, 200).unlockedBy("has_ancient_debris", RecipeProvider.has(Blocks.ANCIENT_DEBRIS)).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.POLISHED_BLACKSTONE_BRICKS), Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.asItem(), 0.1f, 200).unlockedBy("has_blackstone_bricks", RecipeProvider.has(Blocks.POLISHED_BLACKSTONE_BRICKS)).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.NETHER_BRICKS), Blocks.CRACKED_NETHER_BRICKS.asItem(), 0.1f, 200).unlockedBy("has_nether_bricks", RecipeProvider.has(Blocks.NETHER_BRICKS)).save(consumer);
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(Blocks.IRON_ORE.asItem()), Items.IRON_INGOT, 0.7f, 100).unlockedBy("has_iron_ore", RecipeProvider.has(Blocks.IRON_ORE.asItem())).save(consumer, "iron_ingot_from_blasting");
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(ItemTags.GOLD_ORES), Items.GOLD_INGOT, 1.0f, 100).unlockedBy("has_gold_ore", RecipeProvider.has(ItemTags.GOLD_ORES)).save(consumer, "gold_ingot_from_blasting");
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(Blocks.DIAMOND_ORE.asItem()), Items.DIAMOND, 1.0f, 100).unlockedBy("has_diamond_ore", RecipeProvider.has(Blocks.DIAMOND_ORE)).save(consumer, "diamond_from_blasting");
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(Blocks.LAPIS_ORE.asItem()), Items.LAPIS_LAZULI, 0.2f, 100).unlockedBy("has_lapis_ore", RecipeProvider.has(Blocks.LAPIS_ORE)).save(consumer, "lapis_from_blasting");
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(Blocks.REDSTONE_ORE), Items.REDSTONE, 0.7f, 100).unlockedBy("has_redstone_ore", RecipeProvider.has(Blocks.REDSTONE_ORE)).save(consumer, "redstone_from_blasting");
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(Blocks.COAL_ORE.asItem()), Items.COAL, 0.1f, 100).unlockedBy("has_coal_ore", RecipeProvider.has(Blocks.COAL_ORE)).save(consumer, "coal_from_blasting");
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(Blocks.EMERALD_ORE.asItem()), Items.EMERALD, 1.0f, 100).unlockedBy("has_emerald_ore", RecipeProvider.has(Blocks.EMERALD_ORE)).save(consumer, "emerald_from_blasting");
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(Blocks.NETHER_QUARTZ_ORE), Items.QUARTZ, 0.2f, 100).unlockedBy("has_nether_quartz_ore", RecipeProvider.has(Blocks.NETHER_QUARTZ_ORE)).save(consumer, "quartz_from_blasting");
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(Items.GOLDEN_PICKAXE, Items.GOLDEN_SHOVEL, Items.GOLDEN_AXE, Items.GOLDEN_HOE, Items.GOLDEN_SWORD, Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS, Items.GOLDEN_HORSE_ARMOR), Items.GOLD_NUGGET, 0.1f, 100).unlockedBy("has_golden_pickaxe", RecipeProvider.has(Items.GOLDEN_PICKAXE)).unlockedBy("has_golden_shovel", RecipeProvider.has(Items.GOLDEN_SHOVEL)).unlockedBy("has_golden_axe", RecipeProvider.has(Items.GOLDEN_AXE)).unlockedBy("has_golden_hoe", RecipeProvider.has(Items.GOLDEN_HOE)).unlockedBy("has_golden_sword", RecipeProvider.has(Items.GOLDEN_SWORD)).unlockedBy("has_golden_helmet", RecipeProvider.has(Items.GOLDEN_HELMET)).unlockedBy("has_golden_chestplate", RecipeProvider.has(Items.GOLDEN_CHESTPLATE)).unlockedBy("has_golden_leggings", RecipeProvider.has(Items.GOLDEN_LEGGINGS)).unlockedBy("has_golden_boots", RecipeProvider.has(Items.GOLDEN_BOOTS)).unlockedBy("has_golden_horse_armor", RecipeProvider.has(Items.GOLDEN_HORSE_ARMOR)).save(consumer, "gold_nugget_from_blasting");
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(Items.IRON_PICKAXE, Items.IRON_SHOVEL, Items.IRON_AXE, Items.IRON_HOE, Items.IRON_SWORD, Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS, Items.IRON_HORSE_ARMOR, Items.CHAINMAIL_HELMET, Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_LEGGINGS, Items.CHAINMAIL_BOOTS), Items.IRON_NUGGET, 0.1f, 100).unlockedBy("has_iron_pickaxe", RecipeProvider.has(Items.IRON_PICKAXE)).unlockedBy("has_iron_shovel", RecipeProvider.has(Items.IRON_SHOVEL)).unlockedBy("has_iron_axe", RecipeProvider.has(Items.IRON_AXE)).unlockedBy("has_iron_hoe", RecipeProvider.has(Items.IRON_HOE)).unlockedBy("has_iron_sword", RecipeProvider.has(Items.IRON_SWORD)).unlockedBy("has_iron_helmet", RecipeProvider.has(Items.IRON_HELMET)).unlockedBy("has_iron_chestplate", RecipeProvider.has(Items.IRON_CHESTPLATE)).unlockedBy("has_iron_leggings", RecipeProvider.has(Items.IRON_LEGGINGS)).unlockedBy("has_iron_boots", RecipeProvider.has(Items.IRON_BOOTS)).unlockedBy("has_iron_horse_armor", RecipeProvider.has(Items.IRON_HORSE_ARMOR)).unlockedBy("has_chainmail_helmet", RecipeProvider.has(Items.CHAINMAIL_HELMET)).unlockedBy("has_chainmail_chestplate", RecipeProvider.has(Items.CHAINMAIL_CHESTPLATE)).unlockedBy("has_chainmail_leggings", RecipeProvider.has(Items.CHAINMAIL_LEGGINGS)).unlockedBy("has_chainmail_boots", RecipeProvider.has(Items.CHAINMAIL_BOOTS)).save(consumer, "iron_nugget_from_blasting");
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(Blocks.ANCIENT_DEBRIS), Items.NETHERITE_SCRAP, 2.0f, 100).unlockedBy("has_ancient_debris", RecipeProvider.has(Blocks.ANCIENT_DEBRIS)).save(consumer, "netherite_scrap_from_blasting");
        RecipeProvider.cookRecipes(consumer, "smoking", RecipeSerializer.SMOKING_RECIPE, 100);
        RecipeProvider.cookRecipes(consumer, "campfire_cooking", RecipeSerializer.CAMPFIRE_COOKING_RECIPE, 600);
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.STONE), Blocks.STONE_SLAB, 2).unlocks("has_stone", RecipeProvider.has(Blocks.STONE)).save(consumer, "stone_slab_from_stone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.STONE), Blocks.STONE_STAIRS).unlocks("has_stone", RecipeProvider.has(Blocks.STONE)).save(consumer, "stone_stairs_from_stone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.STONE), Blocks.STONE_BRICKS).unlocks("has_stone", RecipeProvider.has(Blocks.STONE)).save(consumer, "stone_bricks_from_stone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.STONE), Blocks.STONE_BRICK_SLAB, 2).unlocks("has_stone", RecipeProvider.has(Blocks.STONE)).save(consumer, "stone_brick_slab_from_stone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.STONE), Blocks.STONE_BRICK_STAIRS).unlocks("has_stone", RecipeProvider.has(Blocks.STONE)).save(consumer, "stone_brick_stairs_from_stone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.STONE), Blocks.CHISELED_STONE_BRICKS).unlocks("has_stone", RecipeProvider.has(Blocks.STONE)).save(consumer, "chiseled_stone_bricks_stone_from_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.STONE), Blocks.STONE_BRICK_WALL).unlocks("has_stone", RecipeProvider.has(Blocks.STONE)).save(consumer, "stone_brick_walls_from_stone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.SANDSTONE), Blocks.CUT_SANDSTONE).unlocks("has_sandstone", RecipeProvider.has(Blocks.SANDSTONE)).save(consumer, "cut_sandstone_from_sandstone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.SANDSTONE), Blocks.SANDSTONE_SLAB, 2).unlocks("has_sandstone", RecipeProvider.has(Blocks.SANDSTONE)).save(consumer, "sandstone_slab_from_sandstone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.SANDSTONE), Blocks.CUT_SANDSTONE_SLAB, 2).unlocks("has_sandstone", RecipeProvider.has(Blocks.SANDSTONE)).save(consumer, "cut_sandstone_slab_from_sandstone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.CUT_SANDSTONE), Blocks.CUT_SANDSTONE_SLAB, 2).unlocks("has_cut_sandstone", RecipeProvider.has(Blocks.SANDSTONE)).save(consumer, "cut_sandstone_slab_from_cut_sandstone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.SANDSTONE), Blocks.SANDSTONE_STAIRS).unlocks("has_sandstone", RecipeProvider.has(Blocks.SANDSTONE)).save(consumer, "sandstone_stairs_from_sandstone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.SANDSTONE), Blocks.SANDSTONE_WALL).unlocks("has_sandstone", RecipeProvider.has(Blocks.SANDSTONE)).save(consumer, "sandstone_wall_from_sandstone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.SANDSTONE), Blocks.CHISELED_SANDSTONE).unlocks("has_sandstone", RecipeProvider.has(Blocks.SANDSTONE)).save(consumer, "chiseled_sandstone_from_sandstone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.RED_SANDSTONE), Blocks.CUT_RED_SANDSTONE).unlocks("has_red_sandstone", RecipeProvider.has(Blocks.RED_SANDSTONE)).save(consumer, "cut_red_sandstone_from_red_sandstone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.RED_SANDSTONE), Blocks.RED_SANDSTONE_SLAB, 2).unlocks("has_red_sandstone", RecipeProvider.has(Blocks.RED_SANDSTONE)).save(consumer, "red_sandstone_slab_from_red_sandstone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.RED_SANDSTONE), Blocks.CUT_RED_SANDSTONE_SLAB, 2).unlocks("has_red_sandstone", RecipeProvider.has(Blocks.RED_SANDSTONE)).save(consumer, "cut_red_sandstone_slab_from_red_sandstone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.CUT_RED_SANDSTONE), Blocks.CUT_RED_SANDSTONE_SLAB, 2).unlocks("has_cut_red_sandstone", RecipeProvider.has(Blocks.RED_SANDSTONE)).save(consumer, "cut_red_sandstone_slab_from_cut_red_sandstone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.RED_SANDSTONE), Blocks.RED_SANDSTONE_STAIRS).unlocks("has_red_sandstone", RecipeProvider.has(Blocks.RED_SANDSTONE)).save(consumer, "red_sandstone_stairs_from_red_sandstone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.RED_SANDSTONE), Blocks.RED_SANDSTONE_WALL).unlocks("has_red_sandstone", RecipeProvider.has(Blocks.RED_SANDSTONE)).save(consumer, "red_sandstone_wall_from_red_sandstone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.RED_SANDSTONE), Blocks.CHISELED_RED_SANDSTONE).unlocks("has_red_sandstone", RecipeProvider.has(Blocks.RED_SANDSTONE)).save(consumer, "chiseled_red_sandstone_from_red_sandstone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.QUARTZ_BLOCK), Blocks.QUARTZ_SLAB, 2).unlocks("has_quartz_block", RecipeProvider.has(Blocks.QUARTZ_BLOCK)).save(consumer, "quartz_slab_from_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.QUARTZ_BLOCK), Blocks.QUARTZ_STAIRS).unlocks("has_quartz_block", RecipeProvider.has(Blocks.QUARTZ_BLOCK)).save(consumer, "quartz_stairs_from_quartz_block_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.QUARTZ_BLOCK), Blocks.QUARTZ_PILLAR).unlocks("has_quartz_block", RecipeProvider.has(Blocks.QUARTZ_BLOCK)).save(consumer, "quartz_pillar_from_quartz_block_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.QUARTZ_BLOCK), Blocks.CHISELED_QUARTZ_BLOCK).unlocks("has_quartz_block", RecipeProvider.has(Blocks.QUARTZ_BLOCK)).save(consumer, "chiseled_quartz_block_from_quartz_block_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.QUARTZ_BLOCK), Blocks.QUARTZ_BRICKS).unlocks("has_quartz_block", RecipeProvider.has(Blocks.QUARTZ_BLOCK)).save(consumer, "quartz_bricks_from_quartz_block_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.COBBLESTONE), Blocks.COBBLESTONE_STAIRS).unlocks("has_cobblestone", RecipeProvider.has(Blocks.COBBLESTONE)).save(consumer, "cobblestone_stairs_from_cobblestone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.COBBLESTONE), Blocks.COBBLESTONE_SLAB, 2).unlocks("has_cobblestone", RecipeProvider.has(Blocks.COBBLESTONE)).save(consumer, "cobblestone_slab_from_cobblestone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.COBBLESTONE), Blocks.COBBLESTONE_WALL).unlocks("has_cobblestone", RecipeProvider.has(Blocks.COBBLESTONE)).save(consumer, "cobblestone_wall_from_cobblestone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.STONE_BRICKS), Blocks.STONE_BRICK_SLAB, 2).unlocks("has_stone_bricks", RecipeProvider.has(Blocks.STONE_BRICKS)).save(consumer, "stone_brick_slab_from_stone_bricks_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.STONE_BRICKS), Blocks.STONE_BRICK_STAIRS).unlocks("has_stone_bricks", RecipeProvider.has(Blocks.STONE_BRICKS)).save(consumer, "stone_brick_stairs_from_stone_bricks_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.STONE_BRICKS), Blocks.STONE_BRICK_WALL).unlocks("has_stone_bricks", RecipeProvider.has(Blocks.STONE_BRICKS)).save(consumer, "stone_brick_wall_from_stone_bricks_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.STONE_BRICKS), Blocks.CHISELED_STONE_BRICKS).unlocks("has_stone_bricks", RecipeProvider.has(Blocks.STONE_BRICKS)).save(consumer, "chiseled_stone_bricks_from_stone_bricks_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.BRICKS), Blocks.BRICK_SLAB, 2).unlocks("has_bricks", RecipeProvider.has(Blocks.BRICKS)).save(consumer, "brick_slab_from_bricks_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.BRICKS), Blocks.BRICK_STAIRS).unlocks("has_bricks", RecipeProvider.has(Blocks.BRICKS)).save(consumer, "brick_stairs_from_bricks_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.BRICKS), Blocks.BRICK_WALL).unlocks("has_bricks", RecipeProvider.has(Blocks.BRICKS)).save(consumer, "brick_wall_from_bricks_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.NETHER_BRICKS), Blocks.NETHER_BRICK_SLAB, 2).unlocks("has_nether_bricks", RecipeProvider.has(Blocks.NETHER_BRICKS)).save(consumer, "nether_brick_slab_from_nether_bricks_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.NETHER_BRICKS), Blocks.NETHER_BRICK_STAIRS).unlocks("has_nether_bricks", RecipeProvider.has(Blocks.NETHER_BRICKS)).save(consumer, "nether_brick_stairs_from_nether_bricks_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.NETHER_BRICKS), Blocks.NETHER_BRICK_WALL).unlocks("has_nether_bricks", RecipeProvider.has(Blocks.NETHER_BRICKS)).save(consumer, "nether_brick_wall_from_nether_bricks_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.NETHER_BRICKS), Blocks.CHISELED_NETHER_BRICKS).unlocks("has_nether_bricks", RecipeProvider.has(Blocks.NETHER_BRICKS)).save(consumer, "chiseled_nether_bricks_from_nether_bricks_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.RED_NETHER_BRICKS), Blocks.RED_NETHER_BRICK_SLAB, 2).unlocks("has_nether_bricks", RecipeProvider.has(Blocks.RED_NETHER_BRICKS)).save(consumer, "red_nether_brick_slab_from_red_nether_bricks_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.RED_NETHER_BRICKS), Blocks.RED_NETHER_BRICK_STAIRS).unlocks("has_nether_bricks", RecipeProvider.has(Blocks.RED_NETHER_BRICKS)).save(consumer, "red_nether_brick_stairs_from_red_nether_bricks_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.RED_NETHER_BRICKS), Blocks.RED_NETHER_BRICK_WALL).unlocks("has_nether_bricks", RecipeProvider.has(Blocks.RED_NETHER_BRICKS)).save(consumer, "red_nether_brick_wall_from_red_nether_bricks_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.PURPUR_BLOCK), Blocks.PURPUR_SLAB, 2).unlocks("has_purpur_block", RecipeProvider.has(Blocks.PURPUR_BLOCK)).save(consumer, "purpur_slab_from_purpur_block_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.PURPUR_BLOCK), Blocks.PURPUR_STAIRS).unlocks("has_purpur_block", RecipeProvider.has(Blocks.PURPUR_BLOCK)).save(consumer, "purpur_stairs_from_purpur_block_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.PURPUR_BLOCK), Blocks.PURPUR_PILLAR).unlocks("has_purpur_block", RecipeProvider.has(Blocks.PURPUR_BLOCK)).save(consumer, "purpur_pillar_from_purpur_block_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.PRISMARINE), Blocks.PRISMARINE_SLAB, 2).unlocks("has_prismarine", RecipeProvider.has(Blocks.PRISMARINE)).save(consumer, "prismarine_slab_from_prismarine_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.PRISMARINE), Blocks.PRISMARINE_STAIRS).unlocks("has_prismarine", RecipeProvider.has(Blocks.PRISMARINE)).save(consumer, "prismarine_stairs_from_prismarine_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.PRISMARINE), Blocks.PRISMARINE_WALL).unlocks("has_prismarine", RecipeProvider.has(Blocks.PRISMARINE)).save(consumer, "prismarine_wall_from_prismarine_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.PRISMARINE_BRICKS), Blocks.PRISMARINE_BRICK_SLAB, 2).unlocks("has_prismarine_brick", RecipeProvider.has(Blocks.PRISMARINE_BRICKS)).save(consumer, "prismarine_brick_slab_from_prismarine_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.PRISMARINE_BRICKS), Blocks.PRISMARINE_BRICK_STAIRS).unlocks("has_prismarine_brick", RecipeProvider.has(Blocks.PRISMARINE_BRICKS)).save(consumer, "prismarine_brick_stairs_from_prismarine_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.DARK_PRISMARINE), Blocks.DARK_PRISMARINE_SLAB, 2).unlocks("has_dark_prismarine", RecipeProvider.has(Blocks.DARK_PRISMARINE)).save(consumer, "dark_prismarine_slab_from_dark_prismarine_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.DARK_PRISMARINE), Blocks.DARK_PRISMARINE_STAIRS).unlocks("has_dark_prismarine", RecipeProvider.has(Blocks.DARK_PRISMARINE)).save(consumer, "dark_prismarine_stairs_from_dark_prismarine_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.ANDESITE), Blocks.ANDESITE_SLAB, 2).unlocks("has_andesite", RecipeProvider.has(Blocks.ANDESITE)).save(consumer, "andesite_slab_from_andesite_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.ANDESITE), Blocks.ANDESITE_STAIRS).unlocks("has_andesite", RecipeProvider.has(Blocks.ANDESITE)).save(consumer, "andesite_stairs_from_andesite_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.ANDESITE), Blocks.ANDESITE_WALL).unlocks("has_andesite", RecipeProvider.has(Blocks.ANDESITE)).save(consumer, "andesite_wall_from_andesite_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.ANDESITE), Blocks.POLISHED_ANDESITE).unlocks("has_andesite", RecipeProvider.has(Blocks.ANDESITE)).save(consumer, "polished_andesite_from_andesite_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.ANDESITE), Blocks.POLISHED_ANDESITE_SLAB, 2).unlocks("has_andesite", RecipeProvider.has(Blocks.ANDESITE)).save(consumer, "polished_andesite_slab_from_andesite_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.ANDESITE), Blocks.POLISHED_ANDESITE_STAIRS).unlocks("has_andesite", RecipeProvider.has(Blocks.ANDESITE)).save(consumer, "polished_andesite_stairs_from_andesite_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.POLISHED_ANDESITE), Blocks.POLISHED_ANDESITE_SLAB, 2).unlocks("has_polished_andesite", RecipeProvider.has(Blocks.POLISHED_ANDESITE)).save(consumer, "polished_andesite_slab_from_polished_andesite_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.POLISHED_ANDESITE), Blocks.POLISHED_ANDESITE_STAIRS).unlocks("has_polished_andesite", RecipeProvider.has(Blocks.POLISHED_ANDESITE)).save(consumer, "polished_andesite_stairs_from_polished_andesite_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.BASALT), Blocks.POLISHED_BASALT).unlocks("has_basalt", RecipeProvider.has(Blocks.BASALT)).save(consumer, "polished_basalt_from_basalt_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.GRANITE), Blocks.GRANITE_SLAB, 2).unlocks("has_granite", RecipeProvider.has(Blocks.GRANITE)).save(consumer, "granite_slab_from_granite_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.GRANITE), Blocks.GRANITE_STAIRS).unlocks("has_granite", RecipeProvider.has(Blocks.GRANITE)).save(consumer, "granite_stairs_from_granite_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.GRANITE), Blocks.GRANITE_WALL).unlocks("has_granite", RecipeProvider.has(Blocks.GRANITE)).save(consumer, "granite_wall_from_granite_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.GRANITE), Blocks.POLISHED_GRANITE).unlocks("has_granite", RecipeProvider.has(Blocks.GRANITE)).save(consumer, "polished_granite_from_granite_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.GRANITE), Blocks.POLISHED_GRANITE_SLAB, 2).unlocks("has_granite", RecipeProvider.has(Blocks.GRANITE)).save(consumer, "polished_granite_slab_from_granite_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.GRANITE), Blocks.POLISHED_GRANITE_STAIRS).unlocks("has_granite", RecipeProvider.has(Blocks.GRANITE)).save(consumer, "polished_granite_stairs_from_granite_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.POLISHED_GRANITE), Blocks.POLISHED_GRANITE_SLAB, 2).unlocks("has_polished_granite", RecipeProvider.has(Blocks.POLISHED_GRANITE)).save(consumer, "polished_granite_slab_from_polished_granite_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.POLISHED_GRANITE), Blocks.POLISHED_GRANITE_STAIRS).unlocks("has_polished_granite", RecipeProvider.has(Blocks.POLISHED_GRANITE)).save(consumer, "polished_granite_stairs_from_polished_granite_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.DIORITE), Blocks.DIORITE_SLAB, 2).unlocks("has_diorite", RecipeProvider.has(Blocks.DIORITE)).save(consumer, "diorite_slab_from_diorite_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.DIORITE), Blocks.DIORITE_STAIRS).unlocks("has_diorite", RecipeProvider.has(Blocks.DIORITE)).save(consumer, "diorite_stairs_from_diorite_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.DIORITE), Blocks.DIORITE_WALL).unlocks("has_diorite", RecipeProvider.has(Blocks.DIORITE)).save(consumer, "diorite_wall_from_diorite_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.DIORITE), Blocks.POLISHED_DIORITE).unlocks("has_diorite", RecipeProvider.has(Blocks.DIORITE)).save(consumer, "polished_diorite_from_diorite_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.DIORITE), Blocks.POLISHED_DIORITE_SLAB, 2).unlocks("has_diorite", RecipeProvider.has(Blocks.POLISHED_DIORITE)).save(consumer, "polished_diorite_slab_from_diorite_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.DIORITE), Blocks.POLISHED_DIORITE_STAIRS).unlocks("has_diorite", RecipeProvider.has(Blocks.POLISHED_DIORITE)).save(consumer, "polished_diorite_stairs_from_diorite_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.POLISHED_DIORITE), Blocks.POLISHED_DIORITE_SLAB, 2).unlocks("has_polished_diorite", RecipeProvider.has(Blocks.POLISHED_DIORITE)).save(consumer, "polished_diorite_slab_from_polished_diorite_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.POLISHED_DIORITE), Blocks.POLISHED_DIORITE_STAIRS).unlocks("has_polished_diorite", RecipeProvider.has(Blocks.POLISHED_DIORITE)).save(consumer, "polished_diorite_stairs_from_polished_diorite_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.MOSSY_STONE_BRICKS), Blocks.MOSSY_STONE_BRICK_SLAB, 2).unlocks("has_mossy_stone_bricks", RecipeProvider.has(Blocks.MOSSY_STONE_BRICKS)).save(consumer, "mossy_stone_brick_slab_from_mossy_stone_brick_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.MOSSY_STONE_BRICKS), Blocks.MOSSY_STONE_BRICK_STAIRS).unlocks("has_mossy_stone_bricks", RecipeProvider.has(Blocks.MOSSY_STONE_BRICKS)).save(consumer, "mossy_stone_brick_stairs_from_mossy_stone_brick_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.MOSSY_STONE_BRICKS), Blocks.MOSSY_STONE_BRICK_WALL).unlocks("has_mossy_stone_bricks", RecipeProvider.has(Blocks.MOSSY_STONE_BRICKS)).save(consumer, "mossy_stone_brick_wall_from_mossy_stone_brick_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.MOSSY_COBBLESTONE), Blocks.MOSSY_COBBLESTONE_SLAB, 2).unlocks("has_mossy_cobblestone", RecipeProvider.has(Blocks.MOSSY_COBBLESTONE)).save(consumer, "mossy_cobblestone_slab_from_mossy_cobblestone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.MOSSY_COBBLESTONE), Blocks.MOSSY_COBBLESTONE_STAIRS).unlocks("has_mossy_cobblestone", RecipeProvider.has(Blocks.MOSSY_COBBLESTONE)).save(consumer, "mossy_cobblestone_stairs_from_mossy_cobblestone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.MOSSY_COBBLESTONE), Blocks.MOSSY_COBBLESTONE_WALL).unlocks("has_mossy_cobblestone", RecipeProvider.has(Blocks.MOSSY_COBBLESTONE)).save(consumer, "mossy_cobblestone_wall_from_mossy_cobblestone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.SMOOTH_SANDSTONE), Blocks.SMOOTH_SANDSTONE_SLAB, 2).unlocks("has_smooth_sandstone", RecipeProvider.has(Blocks.SMOOTH_SANDSTONE)).save(consumer, "smooth_sandstone_slab_from_smooth_sandstone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.SMOOTH_SANDSTONE), Blocks.SMOOTH_SANDSTONE_STAIRS).unlocks("has_mossy_cobblestone", RecipeProvider.has(Blocks.SMOOTH_SANDSTONE)).save(consumer, "smooth_sandstone_stairs_from_smooth_sandstone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.SMOOTH_RED_SANDSTONE), Blocks.SMOOTH_RED_SANDSTONE_SLAB, 2).unlocks("has_smooth_red_sandstone", RecipeProvider.has(Blocks.SMOOTH_RED_SANDSTONE)).save(consumer, "smooth_red_sandstone_slab_from_smooth_red_sandstone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.SMOOTH_RED_SANDSTONE), Blocks.SMOOTH_RED_SANDSTONE_STAIRS).unlocks("has_smooth_red_sandstone", RecipeProvider.has(Blocks.SMOOTH_RED_SANDSTONE)).save(consumer, "smooth_red_sandstone_stairs_from_smooth_red_sandstone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.SMOOTH_QUARTZ), Blocks.SMOOTH_QUARTZ_SLAB, 2).unlocks("has_smooth_quartz", RecipeProvider.has(Blocks.SMOOTH_QUARTZ)).save(consumer, "smooth_quartz_slab_from_smooth_quartz_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.SMOOTH_QUARTZ), Blocks.SMOOTH_QUARTZ_STAIRS).unlocks("has_smooth_quartz", RecipeProvider.has(Blocks.SMOOTH_QUARTZ)).save(consumer, "smooth_quartz_stairs_from_smooth_quartz_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.END_STONE_BRICKS), Blocks.END_STONE_BRICK_SLAB, 2).unlocks("has_end_stone_brick", RecipeProvider.has(Blocks.END_STONE_BRICKS)).save(consumer, "end_stone_brick_slab_from_end_stone_brick_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.END_STONE_BRICKS), Blocks.END_STONE_BRICK_STAIRS).unlocks("has_end_stone_brick", RecipeProvider.has(Blocks.END_STONE_BRICKS)).save(consumer, "end_stone_brick_stairs_from_end_stone_brick_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.END_STONE_BRICKS), Blocks.END_STONE_BRICK_WALL).unlocks("has_end_stone_brick", RecipeProvider.has(Blocks.END_STONE_BRICKS)).save(consumer, "end_stone_brick_wall_from_end_stone_brick_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.END_STONE), Blocks.END_STONE_BRICKS).unlocks("has_end_stone", RecipeProvider.has(Blocks.END_STONE)).save(consumer, "end_stone_bricks_from_end_stone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.END_STONE), Blocks.END_STONE_BRICK_SLAB, 2).unlocks("has_end_stone", RecipeProvider.has(Blocks.END_STONE)).save(consumer, "end_stone_brick_slab_from_end_stone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.END_STONE), Blocks.END_STONE_BRICK_STAIRS).unlocks("has_end_stone", RecipeProvider.has(Blocks.END_STONE)).save(consumer, "end_stone_brick_stairs_from_end_stone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.END_STONE), Blocks.END_STONE_BRICK_WALL).unlocks("has_end_stone", RecipeProvider.has(Blocks.END_STONE)).save(consumer, "end_stone_brick_wall_from_end_stone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.SMOOTH_STONE), Blocks.SMOOTH_STONE_SLAB, 2).unlocks("has_smooth_stone", RecipeProvider.has(Blocks.SMOOTH_STONE)).save(consumer, "smooth_stone_slab_from_smooth_stone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.BLACKSTONE), Blocks.BLACKSTONE_SLAB, 2).unlocks("has_blackstone", RecipeProvider.has(Blocks.BLACKSTONE)).save(consumer, "blackstone_slab_from_blackstone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.BLACKSTONE), Blocks.BLACKSTONE_STAIRS).unlocks("has_blackstone", RecipeProvider.has(Blocks.BLACKSTONE)).save(consumer, "blackstone_stairs_from_blackstone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.BLACKSTONE), Blocks.BLACKSTONE_WALL).unlocks("has_blackstone", RecipeProvider.has(Blocks.BLACKSTONE)).save(consumer, "blackstone_wall_from_blackstone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.BLACKSTONE), Blocks.POLISHED_BLACKSTONE).unlocks("has_blackstone", RecipeProvider.has(Blocks.BLACKSTONE)).save(consumer, "polished_blackstone_from_blackstone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.BLACKSTONE), Blocks.POLISHED_BLACKSTONE_WALL).unlocks("has_blackstone", RecipeProvider.has(Blocks.BLACKSTONE)).save(consumer, "polished_blackstone_wall_from_blackstone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.BLACKSTONE), Blocks.POLISHED_BLACKSTONE_SLAB, 2).unlocks("has_blackstone", RecipeProvider.has(Blocks.BLACKSTONE)).save(consumer, "polished_blackstone_slab_from_blackstone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.BLACKSTONE), Blocks.POLISHED_BLACKSTONE_STAIRS).unlocks("has_blackstone", RecipeProvider.has(Blocks.BLACKSTONE)).save(consumer, "polished_blackstone_stairs_from_blackstone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.BLACKSTONE), Blocks.CHISELED_POLISHED_BLACKSTONE).unlocks("has_blackstone", RecipeProvider.has(Blocks.BLACKSTONE)).save(consumer, "chiseled_polished_blackstone_from_blackstone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.BLACKSTONE), Blocks.POLISHED_BLACKSTONE_BRICKS).unlocks("has_blackstone", RecipeProvider.has(Blocks.BLACKSTONE)).save(consumer, "polished_blackstone_bricks_from_blackstone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.BLACKSTONE), Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, 2).unlocks("has_blackstone", RecipeProvider.has(Blocks.BLACKSTONE)).save(consumer, "polished_blackstone_brick_slab_from_blackstone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.BLACKSTONE), Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS).unlocks("has_blackstone", RecipeProvider.has(Blocks.BLACKSTONE)).save(consumer, "polished_blackstone_brick_stairs_from_blackstone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.BLACKSTONE), Blocks.POLISHED_BLACKSTONE_BRICK_WALL).unlocks("has_blackstone", RecipeProvider.has(Blocks.BLACKSTONE)).save(consumer, "polished_blackstone_brick_wall_from_blackstone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.POLISHED_BLACKSTONE), Blocks.POLISHED_BLACKSTONE_SLAB, 2).unlocks("has_polished_blackstone", RecipeProvider.has(Blocks.POLISHED_BLACKSTONE)).save(consumer, "polished_blackstone_slab_from_polished_blackstone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.POLISHED_BLACKSTONE), Blocks.POLISHED_BLACKSTONE_STAIRS).unlocks("has_polished_blackstone", RecipeProvider.has(Blocks.POLISHED_BLACKSTONE)).save(consumer, "polished_blackstone_stairs_from_polished_blackstone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.POLISHED_BLACKSTONE), Blocks.POLISHED_BLACKSTONE_BRICKS).unlocks("has_polished_blackstone", RecipeProvider.has(Blocks.POLISHED_BLACKSTONE)).save(consumer, "polished_blackstone_bricks_from_polished_blackstone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.POLISHED_BLACKSTONE), Blocks.POLISHED_BLACKSTONE_WALL).unlocks("has_polished_blackstone", RecipeProvider.has(Blocks.POLISHED_BLACKSTONE)).save(consumer, "polished_blackstone_wall_from_polished_blackstone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.POLISHED_BLACKSTONE), Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, 2).unlocks("has_polished_blackstone", RecipeProvider.has(Blocks.POLISHED_BLACKSTONE)).save(consumer, "polished_blackstone_brick_slab_from_polished_blackstone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.POLISHED_BLACKSTONE), Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS).unlocks("has_polished_blackstone", RecipeProvider.has(Blocks.POLISHED_BLACKSTONE)).save(consumer, "polished_blackstone_brick_stairs_from_polished_blackstone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.POLISHED_BLACKSTONE), Blocks.POLISHED_BLACKSTONE_BRICK_WALL).unlocks("has_polished_blackstone", RecipeProvider.has(Blocks.POLISHED_BLACKSTONE)).save(consumer, "polished_blackstone_brick_wall_from_polished_blackstone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.POLISHED_BLACKSTONE), Blocks.CHISELED_POLISHED_BLACKSTONE).unlocks("has_polished_blackstone", RecipeProvider.has(Blocks.POLISHED_BLACKSTONE)).save(consumer, "chiseled_polished_blackstone_from_polished_blackstone_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.POLISHED_BLACKSTONE_BRICKS), Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, 2).unlocks("has_polished_blackstone_bricks", RecipeProvider.has(Blocks.POLISHED_BLACKSTONE_BRICKS)).save(consumer, "polished_blackstone_brick_slab_from_polished_blackstone_bricks_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.POLISHED_BLACKSTONE_BRICKS), Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS).unlocks("has_polished_blackstone_bricks", RecipeProvider.has(Blocks.POLISHED_BLACKSTONE_BRICKS)).save(consumer, "polished_blackstone_brick_stairs_from_polished_blackstone_bricks_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.POLISHED_BLACKSTONE_BRICKS), Blocks.POLISHED_BLACKSTONE_BRICK_WALL).unlocks("has_polished_blackstone_bricks", RecipeProvider.has(Blocks.POLISHED_BLACKSTONE_BRICKS)).save(consumer, "polished_blackstone_brick_wall_from_polished_blackstone_bricks_stonecutting");
        RecipeProvider.netheriteSmithing(consumer, Items.DIAMOND_CHESTPLATE, Items.NETHERITE_CHESTPLATE);
        RecipeProvider.netheriteSmithing(consumer, Items.DIAMOND_LEGGINGS, Items.NETHERITE_LEGGINGS);
        RecipeProvider.netheriteSmithing(consumer, Items.DIAMOND_HELMET, Items.NETHERITE_HELMET);
        RecipeProvider.netheriteSmithing(consumer, Items.DIAMOND_BOOTS, Items.NETHERITE_BOOTS);
        RecipeProvider.netheriteSmithing(consumer, Items.DIAMOND_SWORD, Items.NETHERITE_SWORD);
        RecipeProvider.netheriteSmithing(consumer, Items.DIAMOND_AXE, Items.NETHERITE_AXE);
        RecipeProvider.netheriteSmithing(consumer, Items.DIAMOND_PICKAXE, Items.NETHERITE_PICKAXE);
        RecipeProvider.netheriteSmithing(consumer, Items.DIAMOND_HOE, Items.NETHERITE_HOE);
        RecipeProvider.netheriteSmithing(consumer, Items.DIAMOND_SHOVEL, Items.NETHERITE_SHOVEL);
    }

    private static void netheriteSmithing(Consumer<FinishedRecipe> consumer, Item item, Item item2) {
        UpgradeRecipeBuilder.smithing(Ingredient.of(item), Ingredient.of(Items.NETHERITE_INGOT), item2).unlocks("has_netherite_ingot", RecipeProvider.has(Items.NETHERITE_INGOT)).save(consumer, Registry.ITEM.getKey(item2.asItem()).getPath() + "_smithing");
    }

    private static void planksFromLog(Consumer<FinishedRecipe> consumer, ItemLike itemLike, Tag<Item> tag) {
        ShapelessRecipeBuilder.shapeless(itemLike, 4).requires(tag).group("planks").unlockedBy("has_log", RecipeProvider.has(tag)).save(consumer);
    }

    private static void planksFromLogs(Consumer<FinishedRecipe> consumer, ItemLike itemLike, Tag<Item> tag) {
        ShapelessRecipeBuilder.shapeless(itemLike, 4).requires(tag).group("planks").unlockedBy("has_logs", RecipeProvider.has(tag)).save(consumer);
    }

    private static void woodFromLogs(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        ShapedRecipeBuilder.shaped(itemLike, 3).define(Character.valueOf('#'), itemLike2).pattern("##").pattern("##").group("bark").unlockedBy("has_log", RecipeProvider.has(itemLike2)).save(consumer);
    }

    private static void woodenBoat(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        ShapedRecipeBuilder.shaped(itemLike).define(Character.valueOf('#'), itemLike2).pattern("# #").pattern("###").group("boat").unlockedBy("in_water", RecipeProvider.insideOf(Blocks.WATER)).save(consumer);
    }

    private static void woodenButton(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        ShapelessRecipeBuilder.shapeless(itemLike).requires(itemLike2).group("wooden_button").unlockedBy("has_planks", RecipeProvider.has(itemLike2)).save(consumer);
    }

    private static void woodenDoor(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        ShapedRecipeBuilder.shaped(itemLike, 3).define(Character.valueOf('#'), itemLike2).pattern("##").pattern("##").pattern("##").group("wooden_door").unlockedBy("has_planks", RecipeProvider.has(itemLike2)).save(consumer);
    }

    private static void woodenFence(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        ShapedRecipeBuilder.shaped(itemLike, 3).define(Character.valueOf('#'), Items.STICK).define(Character.valueOf('W'), itemLike2).pattern("W#W").pattern("W#W").group("wooden_fence").unlockedBy("has_planks", RecipeProvider.has(itemLike2)).save(consumer);
    }

    private static void woodenFenceGate(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        ShapedRecipeBuilder.shaped(itemLike).define(Character.valueOf('#'), Items.STICK).define(Character.valueOf('W'), itemLike2).pattern("#W#").pattern("#W#").group("wooden_fence_gate").unlockedBy("has_planks", RecipeProvider.has(itemLike2)).save(consumer);
    }

    private static void woodenPressurePlate(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        ShapedRecipeBuilder.shaped(itemLike).define(Character.valueOf('#'), itemLike2).pattern("##").group("wooden_pressure_plate").unlockedBy("has_planks", RecipeProvider.has(itemLike2)).save(consumer);
    }

    private static void woodenSlab(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        ShapedRecipeBuilder.shaped(itemLike, 6).define(Character.valueOf('#'), itemLike2).pattern("###").group("wooden_slab").unlockedBy("has_planks", RecipeProvider.has(itemLike2)).save(consumer);
    }

    private static void woodenStairs(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        ShapedRecipeBuilder.shaped(itemLike, 4).define(Character.valueOf('#'), itemLike2).pattern("#  ").pattern("## ").pattern("###").group("wooden_stairs").unlockedBy("has_planks", RecipeProvider.has(itemLike2)).save(consumer);
    }

    private static void woodenTrapdoor(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        ShapedRecipeBuilder.shaped(itemLike, 2).define(Character.valueOf('#'), itemLike2).pattern("###").pattern("###").group("wooden_trapdoor").unlockedBy("has_planks", RecipeProvider.has(itemLike2)).save(consumer);
    }

    private static void woodenSign(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        String string = Registry.ITEM.getKey(itemLike2.asItem()).getPath();
        ShapedRecipeBuilder.shaped(itemLike, 3).group("sign").define(Character.valueOf('#'), itemLike2).define(Character.valueOf('X'), Items.STICK).pattern("###").pattern("###").pattern(" X ").unlockedBy("has_" + string, RecipeProvider.has(itemLike2)).save(consumer);
    }

    private static void coloredWoolFromWhiteWoolAndDye(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        ShapelessRecipeBuilder.shapeless(itemLike).requires(itemLike2).requires(Blocks.WHITE_WOOL).group("wool").unlockedBy("has_white_wool", RecipeProvider.has(Blocks.WHITE_WOOL)).save(consumer);
    }

    private static void carpetFromWool(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        String string = Registry.ITEM.getKey(itemLike2.asItem()).getPath();
        ShapedRecipeBuilder.shaped(itemLike, 3).define(Character.valueOf('#'), itemLike2).pattern("##").group("carpet").unlockedBy("has_" + string, RecipeProvider.has(itemLike2)).save(consumer);
    }

    private static void coloredCarpetFromWhiteCarpetAndDye(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        String string = Registry.ITEM.getKey(itemLike.asItem()).getPath();
        String string2 = Registry.ITEM.getKey(itemLike2.asItem()).getPath();
        ShapedRecipeBuilder.shaped(itemLike, 8).define(Character.valueOf('#'), Blocks.WHITE_CARPET).define(Character.valueOf('$'), itemLike2).pattern("###").pattern("#$#").pattern("###").group("carpet").unlockedBy("has_white_carpet", RecipeProvider.has(Blocks.WHITE_CARPET)).unlockedBy("has_" + string2, RecipeProvider.has(itemLike2)).save(consumer, string + "_from_white_carpet");
    }

    private static void bedFromPlanksAndWool(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        String string = Registry.ITEM.getKey(itemLike2.asItem()).getPath();
        ShapedRecipeBuilder.shaped(itemLike).define(Character.valueOf('#'), itemLike2).define(Character.valueOf('X'), ItemTags.PLANKS).pattern("###").pattern("XXX").group("bed").unlockedBy("has_" + string, RecipeProvider.has(itemLike2)).save(consumer);
    }

    private static void bedFromWhiteBedAndDye(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        String string = Registry.ITEM.getKey(itemLike.asItem()).getPath();
        ShapelessRecipeBuilder.shapeless(itemLike).requires(Items.WHITE_BED).requires(itemLike2).group("dyed_bed").unlockedBy("has_bed", RecipeProvider.has(Items.WHITE_BED)).save(consumer, string + "_from_white_bed");
    }

    private static void banner(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        String string = Registry.ITEM.getKey(itemLike2.asItem()).getPath();
        ShapedRecipeBuilder.shaped(itemLike).define(Character.valueOf('#'), itemLike2).define(Character.valueOf('|'), Items.STICK).pattern("###").pattern("###").pattern(" | ").group("banner").unlockedBy("has_" + string, RecipeProvider.has(itemLike2)).save(consumer);
    }

    private static void stainedGlassFromGlassAndDye(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        ShapedRecipeBuilder.shaped(itemLike, 8).define(Character.valueOf('#'), Blocks.GLASS).define(Character.valueOf('X'), itemLike2).pattern("###").pattern("#X#").pattern("###").group("stained_glass").unlockedBy("has_glass", RecipeProvider.has(Blocks.GLASS)).save(consumer);
    }

    private static void stainedGlassPaneFromStainedGlass(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        ShapedRecipeBuilder.shaped(itemLike, 16).define(Character.valueOf('#'), itemLike2).pattern("###").pattern("###").group("stained_glass_pane").unlockedBy("has_glass", RecipeProvider.has(itemLike2)).save(consumer);
    }

    private static void stainedGlassPaneFromGlassPaneAndDye(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        String string = Registry.ITEM.getKey(itemLike.asItem()).getPath();
        String string2 = Registry.ITEM.getKey(itemLike2.asItem()).getPath();
        ShapedRecipeBuilder.shaped(itemLike, 8).define(Character.valueOf('#'), Blocks.GLASS_PANE).define(Character.valueOf('$'), itemLike2).pattern("###").pattern("#$#").pattern("###").group("stained_glass_pane").unlockedBy("has_glass_pane", RecipeProvider.has(Blocks.GLASS_PANE)).unlockedBy("has_" + string2, RecipeProvider.has(itemLike2)).save(consumer, string + "_from_glass_pane");
    }

    private static void coloredTerracottaFromTerracottaAndDye(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        ShapedRecipeBuilder.shaped(itemLike, 8).define(Character.valueOf('#'), Blocks.TERRACOTTA).define(Character.valueOf('X'), itemLike2).pattern("###").pattern("#X#").pattern("###").group("stained_terracotta").unlockedBy("has_terracotta", RecipeProvider.has(Blocks.TERRACOTTA)).save(consumer);
    }

    private static void concretePowder(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        ShapelessRecipeBuilder.shapeless(itemLike, 8).requires(itemLike2).requires(Blocks.SAND, 4).requires(Blocks.GRAVEL, 4).group("concrete_powder").unlockedBy("has_sand", RecipeProvider.has(Blocks.SAND)).unlockedBy("has_gravel", RecipeProvider.has(Blocks.GRAVEL)).save(consumer);
    }

    private static void cookRecipes(Consumer<FinishedRecipe> consumer, String string, SimpleCookingSerializer<?> simpleCookingSerializer, int n) {
        SimpleCookingRecipeBuilder.cooking(Ingredient.of(Items.BEEF), Items.COOKED_BEEF, 0.35f, n, simpleCookingSerializer).unlockedBy("has_beef", RecipeProvider.has(Items.BEEF)).save(consumer, "cooked_beef_from_" + string);
        SimpleCookingRecipeBuilder.cooking(Ingredient.of(Items.CHICKEN), Items.COOKED_CHICKEN, 0.35f, n, simpleCookingSerializer).unlockedBy("has_chicken", RecipeProvider.has(Items.CHICKEN)).save(consumer, "cooked_chicken_from_" + string);
        SimpleCookingRecipeBuilder.cooking(Ingredient.of(Items.COD), Items.COOKED_COD, 0.35f, n, simpleCookingSerializer).unlockedBy("has_cod", RecipeProvider.has(Items.COD)).save(consumer, "cooked_cod_from_" + string);
        SimpleCookingRecipeBuilder.cooking(Ingredient.of(Blocks.KELP), Items.DRIED_KELP, 0.1f, n, simpleCookingSerializer).unlockedBy("has_kelp", RecipeProvider.has(Blocks.KELP)).save(consumer, "dried_kelp_from_" + string);
        SimpleCookingRecipeBuilder.cooking(Ingredient.of(Items.SALMON), Items.COOKED_SALMON, 0.35f, n, simpleCookingSerializer).unlockedBy("has_salmon", RecipeProvider.has(Items.SALMON)).save(consumer, "cooked_salmon_from_" + string);
        SimpleCookingRecipeBuilder.cooking(Ingredient.of(Items.MUTTON), Items.COOKED_MUTTON, 0.35f, n, simpleCookingSerializer).unlockedBy("has_mutton", RecipeProvider.has(Items.MUTTON)).save(consumer, "cooked_mutton_from_" + string);
        SimpleCookingRecipeBuilder.cooking(Ingredient.of(Items.PORKCHOP), Items.COOKED_PORKCHOP, 0.35f, n, simpleCookingSerializer).unlockedBy("has_porkchop", RecipeProvider.has(Items.PORKCHOP)).save(consumer, "cooked_porkchop_from_" + string);
        SimpleCookingRecipeBuilder.cooking(Ingredient.of(Items.POTATO), Items.BAKED_POTATO, 0.35f, n, simpleCookingSerializer).unlockedBy("has_potato", RecipeProvider.has(Items.POTATO)).save(consumer, "baked_potato_from_" + string);
        SimpleCookingRecipeBuilder.cooking(Ingredient.of(Items.RABBIT), Items.COOKED_RABBIT, 0.35f, n, simpleCookingSerializer).unlockedBy("has_rabbit", RecipeProvider.has(Items.RABBIT)).save(consumer, "cooked_rabbit_from_" + string);
    }

    private static EnterBlockTrigger.TriggerInstance insideOf(Block block) {
        return new EnterBlockTrigger.TriggerInstance(EntityPredicate.Composite.ANY, block, StatePropertiesPredicate.ANY);
    }

    private static InventoryChangeTrigger.TriggerInstance has(ItemLike itemLike) {
        return RecipeProvider.inventoryTrigger(ItemPredicate.Builder.item().of(itemLike).build());
    }

    private static InventoryChangeTrigger.TriggerInstance has(Tag<Item> tag) {
        return RecipeProvider.inventoryTrigger(ItemPredicate.Builder.item().of(tag).build());
    }

    private static InventoryChangeTrigger.TriggerInstance inventoryTrigger(ItemPredicate ... arritemPredicate) {
        return new InventoryChangeTrigger.TriggerInstance(EntityPredicate.Composite.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, arritemPredicate);
    }

    @Override
    public String getName() {
        return "Recipes";
    }
}

