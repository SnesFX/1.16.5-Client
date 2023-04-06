/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  javax.annotation.Nullable
 */
package net.minecraft.data.recipes;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

public interface FinishedRecipe {
    public void serializeRecipeData(JsonObject var1);

    default public JsonObject serializeRecipe() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", Registry.RECIPE_SERIALIZER.getKey(this.getType()).toString());
        this.serializeRecipeData(jsonObject);
        return jsonObject;
    }

    public ResourceLocation getId();

    public RecipeSerializer<?> getType();

    @Nullable
    public JsonObject serializeAdvancement();

    @Nullable
    public ResourceLocation getAdvancementId();
}

