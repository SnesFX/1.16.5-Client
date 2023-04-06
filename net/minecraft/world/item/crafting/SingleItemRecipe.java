/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package net.minecraft.world.item.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;

public abstract class SingleItemRecipe
implements Recipe<Container> {
    protected final Ingredient ingredient;
    protected final ItemStack result;
    private final RecipeType<?> type;
    private final RecipeSerializer<?> serializer;
    protected final ResourceLocation id;
    protected final String group;

    public SingleItemRecipe(RecipeType<?> recipeType, RecipeSerializer<?> recipeSerializer, ResourceLocation resourceLocation, String string, Ingredient ingredient, ItemStack itemStack) {
        this.type = recipeType;
        this.serializer = recipeSerializer;
        this.id = resourceLocation;
        this.group = string;
        this.ingredient = ingredient;
        this.result = itemStack;
    }

    @Override
    public RecipeType<?> getType() {
        return this.type;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return this.serializer;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public ItemStack getResultItem() {
        return this.result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> nonNullList = NonNullList.create();
        nonNullList.add(this.ingredient);
        return nonNullList;
    }

    @Override
    public boolean canCraftInDimensions(int n, int n2) {
        return true;
    }

    @Override
    public ItemStack assemble(Container container) {
        return this.result.copy();
    }

    public static class Serializer<T extends SingleItemRecipe>
    implements RecipeSerializer<T> {
        final SingleItemMaker<T> factory;

        protected Serializer(SingleItemMaker<T> singleItemMaker) {
            this.factory = singleItemMaker;
        }

        @Override
        public T fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            String string = GsonHelper.getAsString(jsonObject, "group", "");
            Ingredient ingredient = GsonHelper.isArrayNode(jsonObject, "ingredient") ? Ingredient.fromJson((JsonElement)GsonHelper.getAsJsonArray(jsonObject, "ingredient")) : Ingredient.fromJson((JsonElement)GsonHelper.getAsJsonObject(jsonObject, "ingredient"));
            String string2 = GsonHelper.getAsString(jsonObject, "result");
            int n = GsonHelper.getAsInt(jsonObject, "count");
            ItemStack itemStack = new ItemStack(Registry.ITEM.get(new ResourceLocation(string2)), n);
            return this.factory.create(resourceLocation, string, ingredient, itemStack);
        }

        @Override
        public T fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
            String string = friendlyByteBuf.readUtf(32767);
            Ingredient ingredient = Ingredient.fromNetwork(friendlyByteBuf);
            ItemStack itemStack = friendlyByteBuf.readItem();
            return this.factory.create(resourceLocation, string, ingredient, itemStack);
        }

        @Override
        public void toNetwork(FriendlyByteBuf friendlyByteBuf, T t) {
            friendlyByteBuf.writeUtf(((SingleItemRecipe)t).group);
            ((SingleItemRecipe)t).ingredient.toNetwork(friendlyByteBuf);
            friendlyByteBuf.writeItem(((SingleItemRecipe)t).result);
        }

        @Override
        public /* synthetic */ Recipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
            return this.fromNetwork(resourceLocation, friendlyByteBuf);
        }

        @Override
        public /* synthetic */ Recipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            return this.fromJson(resourceLocation, jsonObject);
        }

        static interface SingleItemMaker<T extends SingleItemRecipe> {
            public T create(ResourceLocation var1, String var2, Ingredient var3, ItemStack var4);
        }

    }

}

