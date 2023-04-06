/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 *  com.google.gson.JsonSyntaxException
 *  com.mojang.datafixers.util.Pair
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.storage.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import java.lang.reflect.Type;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.SerializerType;

public class GsonAdapterFactory {
    public static <E, T extends SerializerType<E>> Builder<E, T> builder(Registry<T> registry, String string, String string2, Function<E, T> function) {
        return new Builder(registry, string, string2, function);
    }

    public static interface DefaultSerializer<T> {
        public JsonElement serialize(T var1, JsonSerializationContext var2);

        public T deserialize(JsonElement var1, JsonDeserializationContext var2);
    }

    static class JsonAdapter<E, T extends SerializerType<E>>
    implements JsonDeserializer<E>,
    JsonSerializer<E> {
        private final Registry<T> registry;
        private final String elementName;
        private final String typeKey;
        private final Function<E, T> typeGetter;
        @Nullable
        private final Pair<T, DefaultSerializer<? extends E>> defaultType;

        private JsonAdapter(Registry<T> registry, String string, String string2, Function<E, T> function, @Nullable Pair<T, DefaultSerializer<? extends E>> pair) {
            this.registry = registry;
            this.elementName = string;
            this.typeKey = string2;
            this.typeGetter = function;
            this.defaultType = pair;
        }

        public E deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, this.elementName);
                ResourceLocation resourceLocation = new ResourceLocation(GsonHelper.getAsString(jsonObject, this.typeKey));
                SerializerType serializerType = (SerializerType)this.registry.get(resourceLocation);
                if (serializerType == null) {
                    throw new JsonSyntaxException("Unknown type '" + resourceLocation + "'");
                }
                return (E)serializerType.getSerializer().deserialize(jsonObject, jsonDeserializationContext);
            }
            if (this.defaultType == null) {
                throw new UnsupportedOperationException("Object " + (Object)jsonElement + " can't be deserialized");
            }
            return (E)((DefaultSerializer)this.defaultType.getSecond()).deserialize(jsonElement, jsonDeserializationContext);
        }

        public JsonElement serialize(E e, Type type, JsonSerializationContext jsonSerializationContext) {
            SerializerType serializerType = (SerializerType)this.typeGetter.apply(e);
            if (this.defaultType != null && this.defaultType.getFirst() == serializerType) {
                return ((DefaultSerializer)this.defaultType.getSecond()).serialize(e, jsonSerializationContext);
            }
            if (serializerType == null) {
                throw new JsonSyntaxException("Unknown type: " + e);
            }
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(this.typeKey, this.registry.getKey(serializerType).toString());
            serializerType.getSerializer().serialize(jsonObject, e, jsonSerializationContext);
            return jsonObject;
        }
    }

    public static class Builder<E, T extends SerializerType<E>> {
        private final Registry<T> registry;
        private final String elementName;
        private final String typeKey;
        private final Function<E, T> typeGetter;
        @Nullable
        private Pair<T, DefaultSerializer<? extends E>> defaultType;

        private Builder(Registry<T> registry, String string, String string2, Function<E, T> function) {
            this.registry = registry;
            this.elementName = string;
            this.typeKey = string2;
            this.typeGetter = function;
        }

        public Object build() {
            return new JsonAdapter(this.registry, this.elementName, this.typeKey, this.typeGetter, this.defaultType);
        }
    }

}

