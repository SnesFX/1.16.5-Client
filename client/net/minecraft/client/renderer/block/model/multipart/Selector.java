/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Streams
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 */
package net.minecraft.client.renderer.block.model.multipart;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Streams;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.client.renderer.block.model.MultiVariant;
import net.minecraft.client.renderer.block.model.multipart.AndCondition;
import net.minecraft.client.renderer.block.model.multipart.Condition;
import net.minecraft.client.renderer.block.model.multipart.KeyValueCondition;
import net.minecraft.client.renderer.block.model.multipart.OrCondition;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class Selector {
    private final Condition condition;
    private final MultiVariant variant;

    public Selector(Condition condition, MultiVariant multiVariant) {
        if (condition == null) {
            throw new IllegalArgumentException("Missing condition for selector");
        }
        if (multiVariant == null) {
            throw new IllegalArgumentException("Missing variant for selector");
        }
        this.condition = condition;
        this.variant = multiVariant;
    }

    public MultiVariant getVariant() {
        return this.variant;
    }

    public Predicate<BlockState> getPredicate(StateDefinition<Block, BlockState> stateDefinition) {
        return this.condition.getPredicate(stateDefinition);
    }

    public boolean equals(Object object) {
        return this == object;
    }

    public int hashCode() {
        return System.identityHashCode(this);
    }

    public static class Deserializer
    implements JsonDeserializer<Selector> {
        public Selector deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            return new Selector(this.getSelector(jsonObject), (MultiVariant)jsonDeserializationContext.deserialize(jsonObject.get("apply"), MultiVariant.class));
        }

        private Condition getSelector(JsonObject jsonObject) {
            if (jsonObject.has("when")) {
                return Deserializer.getCondition(GsonHelper.getAsJsonObject(jsonObject, "when"));
            }
            return Condition.TRUE;
        }

        @VisibleForTesting
        static Condition getCondition(JsonObject jsonObject) {
            Set set = jsonObject.entrySet();
            if (set.isEmpty()) {
                throw new JsonParseException("No elements found in selector");
            }
            if (set.size() == 1) {
                if (jsonObject.has("OR")) {
                    List list = Streams.stream((Iterable)GsonHelper.getAsJsonArray(jsonObject, "OR")).map(jsonElement -> Deserializer.getCondition(jsonElement.getAsJsonObject())).collect(Collectors.toList());
                    return new OrCondition(list);
                }
                if (jsonObject.has("AND")) {
                    List list = Streams.stream((Iterable)GsonHelper.getAsJsonArray(jsonObject, "AND")).map(jsonElement -> Deserializer.getCondition(jsonElement.getAsJsonObject())).collect(Collectors.toList());
                    return new AndCondition(list);
                }
                return Deserializer.getKeyValueCondition((Map.Entry)set.iterator().next());
            }
            return new AndCondition(set.stream().map(Deserializer::getKeyValueCondition).collect(Collectors.toList()));
        }

        private static Condition getKeyValueCondition(Map.Entry<String, JsonElement> entry) {
            return new KeyValueCondition(entry.getKey(), entry.getValue().getAsString());
        }

        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }

}

