/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Streams
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  javax.annotation.Nullable
 */
package net.minecraft.client.particle;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class ParticleDescription {
    @Nullable
    private final List<ResourceLocation> textures;

    private ParticleDescription(@Nullable List<ResourceLocation> list) {
        this.textures = list;
    }

    @Nullable
    public List<ResourceLocation> getTextures() {
        return this.textures;
    }

    public static ParticleDescription fromJson(JsonObject jsonObject) {
        JsonArray jsonArray = GsonHelper.getAsJsonArray(jsonObject, "textures", null);
        List list = jsonArray != null ? (List)Streams.stream((Iterable)jsonArray).map(jsonElement -> GsonHelper.convertToString(jsonElement, "texture")).map(ResourceLocation::new).collect(ImmutableList.toImmutableList()) : null;
        return new ParticleDescription(list);
    }
}

