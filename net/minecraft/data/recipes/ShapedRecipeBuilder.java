/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.data.recipes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import net.minecraft.tags.Tag;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.ItemLike;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShapedRecipeBuilder {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Item result;
    private final int count;
    private final List<String> rows = Lists.newArrayList();
    private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
    private final Advancement.Builder advancement = Advancement.Builder.advancement();
    private String group;

    public ShapedRecipeBuilder(ItemLike itemLike, int n) {
        this.result = itemLike.asItem();
        this.count = n;
    }

    public static ShapedRecipeBuilder shaped(ItemLike itemLike) {
        return ShapedRecipeBuilder.shaped(itemLike, 1);
    }

    public static ShapedRecipeBuilder shaped(ItemLike itemLike, int n) {
        return new ShapedRecipeBuilder(itemLike, n);
    }

    public ShapedRecipeBuilder define(Character c, Tag<Item> tag) {
        return this.define(c, Ingredient.of(tag));
    }

    public ShapedRecipeBuilder define(Character c, ItemLike itemLike) {
        return this.define(c, Ingredient.of(itemLike));
    }

    public ShapedRecipeBuilder define(Character c, Ingredient ingredient) {
        if (this.key.containsKey(c)) {
            throw new IllegalArgumentException("Symbol '" + c + "' is already defined!");
        }
        if (c.charValue() == ' ') {
            throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
        }
        this.key.put(c, ingredient);
        return this;
    }

    public ShapedRecipeBuilder pattern(String string) {
        if (!this.rows.isEmpty() && string.length() != this.rows.get(0).length()) {
            throw new IllegalArgumentException("Pattern must be the same width on every line!");
        }
        this.rows.add(string);
        return this;
    }

    public ShapedRecipeBuilder unlockedBy(String string, CriterionTriggerInstance criterionTriggerInstance) {
        this.advancement.addCriterion(string, criterionTriggerInstance);
        return this;
    }

    public ShapedRecipeBuilder group(String string) {
        this.group = string;
        return this;
    }

    public void save(Consumer<FinishedRecipe> consumer) {
        this.save(consumer, Registry.ITEM.getKey(this.result));
    }

    public void save(Consumer<FinishedRecipe> consumer, String string) {
        ResourceLocation resourceLocation = Registry.ITEM.getKey(this.result);
        if (new ResourceLocation(string).equals(resourceLocation)) {
            throw new IllegalStateException("Shaped Recipe " + string + " should remove its 'save' argument");
        }
        this.save(consumer, new ResourceLocation(string));
    }

    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation resourceLocation) {
        this.ensureValid(resourceLocation);
        this.advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceLocation)).rewards(AdvancementRewards.Builder.recipe(resourceLocation)).requirements(RequirementsStrategy.OR);
        consumer.accept(new Result(resourceLocation, this.result, this.count, this.group == null ? "" : this.group, this.rows, this.key, this.advancement, new ResourceLocation(resourceLocation.getNamespace(), "recipes/" + this.result.getItemCategory().getRecipeFolderName() + "/" + resourceLocation.getPath())));
    }

    private void ensureValid(ResourceLocation resourceLocation) {
        if (this.rows.isEmpty()) {
            throw new IllegalStateException("No pattern is defined for shaped recipe " + resourceLocation + "!");
        }
        HashSet hashSet = Sets.newHashSet(this.key.keySet());
        hashSet.remove(Character.valueOf(' '));
        for (String string : this.rows) {
            for (int i = 0; i < string.length(); ++i) {
                char c = string.charAt(i);
                if (!this.key.containsKey(Character.valueOf(c)) && c != ' ') {
                    throw new IllegalStateException("Pattern in recipe " + resourceLocation + " uses undefined symbol '" + c + "'");
                }
                hashSet.remove(Character.valueOf(c));
            }
        }
        if (!hashSet.isEmpty()) {
            throw new IllegalStateException("Ingredients are defined but not used in pattern for recipe " + resourceLocation);
        }
        if (this.rows.size() == 1 && this.rows.get(0).length() == 1) {
            throw new IllegalStateException("Shaped recipe " + resourceLocation + " only takes in a single item - should it be a shapeless recipe instead?");
        }
        if (this.advancement.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + resourceLocation);
        }
    }

    class Result
    implements FinishedRecipe {
        private final ResourceLocation id;
        private final Item result;
        private final int count;
        private final String group;
        private final List<String> pattern;
        private final Map<Character, Ingredient> key;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;

        public Result(ResourceLocation resourceLocation, Item item, int n, String string, List<String> list, Map<Character, Ingredient> map, Advancement.Builder builder, ResourceLocation resourceLocation2) {
            this.id = resourceLocation;
            this.result = item;
            this.count = n;
            this.group = string;
            this.pattern = list;
            this.key = map;
            this.advancement = builder;
            this.advancementId = resourceLocation2;
        }

        @Override
        public void serializeRecipeData(JsonObject jsonObject) {
            if (!this.group.isEmpty()) {
                jsonObject.addProperty("group", this.group);
            }
            JsonArray jsonArray = new JsonArray();
            for (String object2 : this.pattern) {
                jsonArray.add(object2);
            }
            jsonObject.add("pattern", (JsonElement)jsonArray);
            JsonObject jsonObject2 = new JsonObject();
            for (Map.Entry<Character, Ingredient> entry : this.key.entrySet()) {
                jsonObject2.add(String.valueOf(entry.getKey()), entry.getValue().toJson());
            }
            jsonObject.add("key", (JsonElement)jsonObject2);
            JsonObject jsonObject3 = new JsonObject();
            jsonObject3.addProperty("item", Registry.ITEM.getKey(this.result).toString());
            if (this.count > 1) {
                jsonObject3.addProperty("count", (Number)this.count);
            }
            jsonObject.add("result", (JsonElement)jsonObject3);
        }

        @Override
        public RecipeSerializer<?> getType() {
            return RecipeSerializer.SHAPED_RECIPE;
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

