/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParseException
 *  com.mojang.datafixers.util.Pair
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.WeightedBakedModel;
import net.minecraft.resources.ResourceLocation;

public class MultiVariant
implements UnbakedModel {
    private final List<Variant> variants;

    public MultiVariant(List<Variant> list) {
        this.variants = list;
    }

    public List<Variant> getVariants() {
        return this.variants;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof MultiVariant) {
            MultiVariant multiVariant = (MultiVariant)object;
            return this.variants.equals(multiVariant.variants);
        }
        return false;
    }

    public int hashCode() {
        return this.variants.hashCode();
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return this.getVariants().stream().map(Variant::getModelLocation).collect(Collectors.toSet());
    }

    @Override
    public Collection<Material> getMaterials(Function<ResourceLocation, UnbakedModel> function, Set<Pair<String, String>> set) {
        return this.getVariants().stream().map(Variant::getModelLocation).distinct().flatMap(resourceLocation -> ((UnbakedModel)function.apply((ResourceLocation)resourceLocation)).getMaterials(function, set).stream()).collect(Collectors.toSet());
    }

    @Nullable
    @Override
    public BakedModel bake(ModelBakery modelBakery, Function<Material, TextureAtlasSprite> function, ModelState modelState, ResourceLocation resourceLocation) {
        if (this.getVariants().isEmpty()) {
            return null;
        }
        WeightedBakedModel.Builder builder = new WeightedBakedModel.Builder();
        for (Variant variant : this.getVariants()) {
            BakedModel bakedModel = modelBakery.bake(variant.getModelLocation(), variant);
            builder.add(bakedModel, variant.getWeight());
        }
        return builder.build();
    }

    public static class Deserializer
    implements JsonDeserializer<MultiVariant> {
        public MultiVariant deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            ArrayList arrayList = Lists.newArrayList();
            if (jsonElement.isJsonArray()) {
                JsonArray jsonArray = jsonElement.getAsJsonArray();
                if (jsonArray.size() == 0) {
                    throw new JsonParseException("Empty variant array");
                }
                for (JsonElement jsonElement2 : jsonArray) {
                    arrayList.add(jsonDeserializationContext.deserialize(jsonElement2, Variant.class));
                }
            } else {
                arrayList.add(jsonDeserializationContext.deserialize(jsonElement, Variant.class));
            }
            return new MultiVariant(arrayList);
        }

        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }

}

