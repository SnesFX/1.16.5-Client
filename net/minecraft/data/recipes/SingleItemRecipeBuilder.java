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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.level.ItemLike;

public class SingleItemRecipeBuilder {
    private final Item result;
    private final Ingredient ingredient;
    private final int count;
    private final Advancement.Builder advancement = Advancement.Builder.advancement();
    private String group;
    private final RecipeSerializer<?> type;

    public SingleItemRecipeBuilder(RecipeSerializer<?> recipeSerializer, Ingredient ingredient, ItemLike itemLike, int n) {
        this.type = recipeSerializer;
        this.result = itemLike.asItem();
        this.ingredient = ingredient;
        this.count = n;
    }

    public static SingleItemRecipeBuilder stonecutting(Ingredient ingredient, ItemLike itemLike) {
        return new SingleItemRecipeBuilder(RecipeSerializer.STONECUTTER, ingredient, itemLike, 1);
    }

    public static SingleItemRecipeBuilder stonecutting(Ingredient ingredient, ItemLike itemLike, int n) {
        return new SingleItemRecipeBuilder(RecipeSerializer.STONECUTTER, ingredient, itemLike, n);
    }

    public SingleItemRecipeBuilder unlocks(String string, CriterionTriggerInstance criterionTriggerInstance) {
        this.advancement.addCriterion(string, criterionTriggerInstance);
        return this;
    }

    public void save(Consumer<FinishedRecipe> consumer, String string) {
        ResourceLocation resourceLocation = Registry.ITEM.getKey(this.result);
        if (new ResourceLocation(string).equals(resourceLocation)) {
            throw new IllegalStateException("Single Item Recipe " + string + " should remove its 'save' argument");
        }
        this.save(consumer, new ResourceLocation(string));
    }

    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation resourceLocation) {
        this.ensureValid(resourceLocation);
        this.advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceLocation)).rewards(AdvancementRewards.Builder.recipe(resourceLocation)).requirements(RequirementsStrategy.OR);
        consumer.accept(new Result(resourceLocation, this.type, this.group == null ? "" : this.group, this.ingredient, this.result, this.count, this.advancement, new ResourceLocation(resourceLocation.getNamespace(), "recipes/" + this.result.getItemCategory().getRecipeFolderName() + "/" + resourceLocation.getPath())));
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
        private final int count;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;
        private final RecipeSerializer<?> type;

        public Result(ResourceLocation resourceLocation, RecipeSerializer<?> recipeSerializer, String string, Ingredient ingredient, Item item, int n, Advancement.Builder builder, ResourceLocation resourceLocation2) {
            this.id = resourceLocation;
            this.type = recipeSerializer;
            this.group = string;
            this.ingredient = ingredient;
            this.result = item;
            this.count = n;
            this.advancement = builder;
            this.advancementId = resourceLocation2;
        }

        @Override
        public void serializeRecipeData(JsonObject jsonObject) {
            if (!this.group.isEmpty()) {
                jsonObject.addProperty("group", this.group);
            }
            jsonObject.add("ingredient", this.ingredient.toJson());
            jsonObject.addProperty("result", Registry.ITEM.getKey(this.result).toString());
            jsonObject.addProperty("count", (Number)this.count);
        }

        @Override
        public ResourceLocation getId() {
            return this.id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return this.type;
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

