/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.MultiVariant;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.block.model.multipart.MultiPart;
import net.minecraft.client.renderer.block.model.multipart.Selector;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class BlockModelDefinition {
    private final Map<String, MultiVariant> variants = Maps.newLinkedHashMap();
    private MultiPart multiPart;

    public static BlockModelDefinition fromStream(Context context, Reader reader) {
        return GsonHelper.fromJson(context.gson, reader, BlockModelDefinition.class);
    }

    public BlockModelDefinition(Map<String, MultiVariant> map, MultiPart multiPart) {
        this.multiPart = multiPart;
        this.variants.putAll(map);
    }

    public BlockModelDefinition(List<BlockModelDefinition> list) {
        BlockModelDefinition blockModelDefinition = null;
        for (BlockModelDefinition blockModelDefinition2 : list) {
            if (blockModelDefinition2.isMultiPart()) {
                this.variants.clear();
                blockModelDefinition = blockModelDefinition2;
            }
            this.variants.putAll(blockModelDefinition2.variants);
        }
        if (blockModelDefinition != null) {
            this.multiPart = blockModelDefinition.multiPart;
        }
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof BlockModelDefinition) {
            BlockModelDefinition blockModelDefinition = (BlockModelDefinition)object;
            if (this.variants.equals(blockModelDefinition.variants)) {
                return this.isMultiPart() ? this.multiPart.equals(blockModelDefinition.multiPart) : !blockModelDefinition.isMultiPart();
            }
        }
        return false;
    }

    public int hashCode() {
        return 31 * this.variants.hashCode() + (this.isMultiPart() ? this.multiPart.hashCode() : 0);
    }

    public Map<String, MultiVariant> getVariants() {
        return this.variants;
    }

    public boolean isMultiPart() {
        return this.multiPart != null;
    }

    public MultiPart getMultiPart() {
        return this.multiPart;
    }

    public static class Deserializer
    implements JsonDeserializer<BlockModelDefinition> {
        public BlockModelDefinition deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Map<String, MultiVariant> map = this.getVariants(jsonDeserializationContext, jsonObject);
            MultiPart multiPart = this.getMultiPart(jsonDeserializationContext, jsonObject);
            if (map.isEmpty() && (multiPart == null || multiPart.getMultiVariants().isEmpty())) {
                throw new JsonParseException("Neither 'variants' nor 'multipart' found");
            }
            return new BlockModelDefinition(map, multiPart);
        }

        protected Map<String, MultiVariant> getVariants(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
            HashMap hashMap = Maps.newHashMap();
            if (jsonObject.has("variants")) {
                JsonObject jsonObject2 = GsonHelper.getAsJsonObject(jsonObject, "variants");
                for (Map.Entry entry : jsonObject2.entrySet()) {
                    hashMap.put(entry.getKey(), jsonDeserializationContext.deserialize((JsonElement)entry.getValue(), MultiVariant.class));
                }
            }
            return hashMap;
        }

        @Nullable
        protected MultiPart getMultiPart(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
            if (!jsonObject.has("multipart")) {
                return null;
            }
            JsonArray jsonArray = GsonHelper.getAsJsonArray(jsonObject, "multipart");
            return (MultiPart)jsonDeserializationContext.deserialize((JsonElement)jsonArray, MultiPart.class);
        }

        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }

    public static final class Context {
        protected final Gson gson = new GsonBuilder().registerTypeAdapter(BlockModelDefinition.class, (Object)new Deserializer()).registerTypeAdapter(Variant.class, (Object)new Variant.Deserializer()).registerTypeAdapter(MultiVariant.class, (Object)new MultiVariant.Deserializer()).registerTypeAdapter(MultiPart.class, (Object)new MultiPart.Deserializer(this)).registerTypeAdapter(Selector.class, (Object)new Selector.Deserializer()).create();
        private StateDefinition<Block, BlockState> definition;

        public StateDefinition<Block, BlockState> getDefinition() {
            return this.definition;
        }

        public void setDefinition(StateDefinition<Block, BlockState> stateDefinition) {
            this.definition = stateDefinition;
        }
    }

}

