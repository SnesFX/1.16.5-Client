/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.world.item.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;

public class SimpleCookingSerializer<T extends AbstractCookingRecipe>
implements RecipeSerializer<T> {
    private final int defaultCookingTime;
    private final CookieBaker<T> factory;

    public SimpleCookingSerializer(CookieBaker<T> cookieBaker, int n) {
        this.defaultCookingTime = n;
        this.factory = cookieBaker;
    }

    @Override
    public T fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
        String string = GsonHelper.getAsString(jsonObject, "group", "");
        JsonArray jsonArray = GsonHelper.isArrayNode(jsonObject, "ingredient") ? GsonHelper.getAsJsonArray(jsonObject, "ingredient") : GsonHelper.getAsJsonObject(jsonObject, "ingredient");
        Ingredient ingredient = Ingredient.fromJson((JsonElement)jsonArray);
        String string2 = GsonHelper.getAsString(jsonObject, "result");
        ResourceLocation resourceLocation2 = new ResourceLocation(string2);
        ItemStack itemStack = new ItemStack(Registry.ITEM.getOptional(resourceLocation2).orElseThrow(() -> new IllegalStateException("Item: " + string2 + " does not exist")));
        float f = GsonHelper.getAsFloat(jsonObject, "experience", 0.0f);
        int n = GsonHelper.getAsInt(jsonObject, "cookingtime", this.defaultCookingTime);
        return this.factory.create(resourceLocation, string, ingredient, itemStack, f, n);
    }

    @Override
    public T fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
        String string = friendlyByteBuf.readUtf(32767);
        Ingredient ingredient = Ingredient.fromNetwork(friendlyByteBuf);
        ItemStack itemStack = friendlyByteBuf.readItem();
        float f = friendlyByteBuf.readFloat();
        int n = friendlyByteBuf.readVarInt();
        return this.factory.create(resourceLocation, string, ingredient, itemStack, f, n);
    }

    @Override
    public void toNetwork(FriendlyByteBuf friendlyByteBuf, T t) {
        friendlyByteBuf.writeUtf(((AbstractCookingRecipe)t).group);
        ((AbstractCookingRecipe)t).ingredient.toNetwork(friendlyByteBuf);
        friendlyByteBuf.writeItem(((AbstractCookingRecipe)t).result);
        friendlyByteBuf.writeFloat(((AbstractCookingRecipe)t).experience);
        friendlyByteBuf.writeVarInt(((AbstractCookingRecipe)t).cookingTime);
    }

    @Override
    public /* synthetic */ Recipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
        return this.fromNetwork(resourceLocation, friendlyByteBuf);
    }

    @Override
    public /* synthetic */ Recipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
        return this.fromJson(resourceLocation, jsonObject);
    }

    static interface CookieBaker<T extends AbstractCookingRecipe> {
        public T create(ResourceLocation var1, String var2, Ingredient var3, ItemStack var4, float var5, int var6);
    }

}

