/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParseException
 *  com.mojang.datafixers.util.Pair
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer.block.model.multipart;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.renderer.block.model.MultiVariant;
import net.minecraft.client.renderer.block.model.multipart.Selector;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.MultiPartBakedModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class MultiPart
implements UnbakedModel {
    private final StateDefinition<Block, BlockState> definition;
    private final List<Selector> selectors;

    public MultiPart(StateDefinition<Block, BlockState> stateDefinition, List<Selector> list) {
        this.definition = stateDefinition;
        this.selectors = list;
    }

    public List<Selector> getSelectors() {
        return this.selectors;
    }

    public Set<MultiVariant> getMultiVariants() {
        HashSet hashSet = Sets.newHashSet();
        for (Selector selector : this.selectors) {
            hashSet.add(selector.getVariant());
        }
        return hashSet;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof MultiPart) {
            MultiPart multiPart = (MultiPart)object;
            return Objects.equals(this.definition, multiPart.definition) && Objects.equals(this.selectors, multiPart.selectors);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.definition, this.selectors);
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return this.getSelectors().stream().flatMap(selector -> selector.getVariant().getDependencies().stream()).collect(Collectors.toSet());
    }

    @Override
    public Collection<Material> getMaterials(Function<ResourceLocation, UnbakedModel> function, Set<Pair<String, String>> set) {
        return this.getSelectors().stream().flatMap(selector -> selector.getVariant().getMaterials(function, set).stream()).collect(Collectors.toSet());
    }

    @Nullable
    @Override
    public BakedModel bake(ModelBakery modelBakery, Function<Material, TextureAtlasSprite> function, ModelState modelState, ResourceLocation resourceLocation) {
        MultiPartBakedModel.Builder builder = new MultiPartBakedModel.Builder();
        for (Selector selector : this.getSelectors()) {
            BakedModel bakedModel = selector.getVariant().bake(modelBakery, function, modelState, resourceLocation);
            if (bakedModel == null) continue;
            builder.add(selector.getPredicate(this.definition), bakedModel);
        }
        return builder.build();
    }

    public static class Deserializer
    implements JsonDeserializer<MultiPart> {
        private final BlockModelDefinition.Context context;

        public Deserializer(BlockModelDefinition.Context context) {
            this.context = context;
        }

        public MultiPart deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return new MultiPart(this.context.getDefinition(), this.getSelectors(jsonDeserializationContext, jsonElement.getAsJsonArray()));
        }

        private List<Selector> getSelectors(JsonDeserializationContext jsonDeserializationContext, JsonArray jsonArray) {
            ArrayList arrayList = Lists.newArrayList();
            for (JsonElement jsonElement : jsonArray) {
                arrayList.add(jsonDeserializationContext.deserialize(jsonElement, Selector.class));
            }
            return arrayList;
        }

        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }

}

