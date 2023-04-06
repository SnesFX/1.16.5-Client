/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  javax.annotation.Nullable
 */
package net.minecraft.data.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCookingSerializer;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.ItemLike;

public class SimpleCookingRecipeBuilder {
    private final Item result;
    private final Ingredient ingredient;
    private final float experience;
    private final int cookingTime;
    private final Advancement.Builder advancement = Advancement.Builder.advancement();
    private String group;
    private final SimpleCookingSerializer<?> serializer;

    private SimpleCookingRecipeBuilder(ItemLike itemLike, Ingredient ingredient, float f, int n, SimpleCookingSerializer<?> simpleCookingSerializer) {
        this.result = itemLike.asItem();
        this.ingredient = ingredient;
        this.experience = f;
        this.cookingTime = n;
        this.serializer = simpleCookingSerializer;
    }

    public static SimpleCookingRecipeBuilder cooking(Ingredient ingredient, ItemLike itemLike, float f, int n, SimpleCookingSerializer<?> simpleCookingSerializer) {
        return new SimpleCookingRecipeBuilder(itemLike, ingredient, f, n, simpleCookingSerializer);
    }

    public static SimpleCookingRecipeBuilder blasting(Ingredient ingredient, ItemLike itemLike, float f, int n) {
        return SimpleCookingRecipeBuilder.cooking(ingredient, itemLike, f, n, RecipeSerializer.BLASTING_RECIPE);
    }

    public static SimpleCookingRecipeBuilder smelting(Ingredient ingredient, ItemLike itemLike, float f, int n) {
        return SimpleCookingRecipeBuilder.cooking(ingredient, itemLike, f, n, RecipeSerializer.SMELTING_RECIPE);
    }

    public SimpleCookingRecipeBuilder unlockedBy(String string, CriterionTriggerInstance criterionTriggerInstance) {
        this.advancement.addCriterion(string, criterionTriggerInstance);
        return this;
    }

    public void save(Consumer<FinishedRecipe> consumer) {
        this.save(consumer, Registry.ITEM.getKey(this.result));
    }

    public void save(Consumer<FinishedRecipe> consumer, String string) {
        ResourceLocation resourceLocation = new ResourceLocation(string);
        ResourceLocation resourceLocation2 = Registry.ITEM.getKey(this.result);
        if (resourceLocation.equals(resourceLocation2)) {
            throw new IllegalStateException("Recipe " + resourceLocation + " should remove its 'save' argument");
        }
        this.save(consumer, resourceLocation);
    }

    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation resourceLocation) {
        this.ensureValid(resourceLocation);
        this.advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceLocation)).rewards(AdvancementRewards.Builder.recipe(resourceLocation)).requirements(RequirementsStrategy.OR);
        consumer.accept(new Result(resourceLocation, this.group == null ? "" : this.group, this.ingredient, this.result, this.experience, this.cookingTime, this.advancement, new ResourceLocation(resourceLocation.getNamespace(), "recipes/" + this.result.getItemCategory().getRecipeFolderName() + "/" + resourceLocation.getPath()), this.serializer));
    }

    private void ensureValid(ResourceLocation resourceLocation) {
        if (this.advancement.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + resourceLocation);
        }
    }

    public static class Result
    implements FinishedRecipe {
        private final ResourceLocation id;
        private final String group;
        private final Ingredient ingredient;
        private final Item result;
        private final float experience;
        private final int cookingTime;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;
        private final RecipeSerializer<? extends AbstractCookingRecipe> serializer;

        public Result(ResourceLocation resourceLocation, String string, Ingredient ingredient, Item item, float f, int n, Advancement.Builder builder, ResourceLocation resourceLocation2, RecipeSerializer<? extends AbstractCookingRecipe> recipeSerializer) {
            this.id = resourceLocation;
            this.group = string;
            this.ingredient = ingredient;
            this.result = item;
            this.experience = f;
            this.cookingTime = n;
            this.advancement = builder;
            this.advancementId = resourceLocation2;
            this.serializer = recipeSerializer;
        }

        @Override
        public void serializeRecipeData(JsonObject jsonObject) {
            if (!this.group.isEmpty()) {
                jsonObject.addProperty("group", this.group);
            }
            jsonObject.add("ingredient", this.ingredient.toJson());
            jsonObject.addProperty("result", Registry.ITEM.getKey(this.result).toString());
            jsonObject.addProperty("experience", (Number)Float.valueOf(this.experience));
            jsonObject.addProperty("cookingtime", (Number)this.cookingTime);
        }

        @Override
        public RecipeSerializer<?> getType() {
            return this.serializer;
        }

        @Override
        public ResourceLocation getId() {
            return this.id;
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return this.advancement.serializeToJson();
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return this.advancementId;
        }
    }

}

