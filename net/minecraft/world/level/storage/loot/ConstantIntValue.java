/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonPrimitive
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 */
package net.minecraft.world.level.storage.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Random;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.RandomIntGenerator;

public final class ConstantIntValue
implements RandomIntGenerator {
    private final int value;

    public ConstantIntValue(int n) {
        this.value = n;
    }

    @Override
    public int getInt(Random random) {
        return this.value;
    }

    @Override
    public ResourceLocation getType() {
        return CONSTANT;
    }

    public static ConstantIntValue exactly(int n) {
        return new ConstantIntValue(n);
    }

    public static class Serializer
    implements JsonDeserializer<ConstantIntValue>,
    JsonSerializer<ConstantIntValue> {
        public ConstantIntValue deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return new ConstantIntValue(GsonHelper.convertToInt(jsonElement, "value"));
        }

        public JsonElement serialize(ConstantIntValue constantIntValue, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive((Number)constantIntValue.value);
        }

        public /* synthetic */ JsonElement serialize(Object object, Type type, JsonSerializationContext jsonSerializationContext) {
            return this.serialize((ConstantIntValue)object, type, jsonSerializationContext);
        }

        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }

}

