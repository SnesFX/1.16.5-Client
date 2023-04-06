/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonPrimitive
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 */
package net.minecraft.world.level.storage.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Random;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.level.storage.loot.RandomIntGenerator;

public class RandomValueBounds
implements RandomIntGenerator {
    private final float min;
    private final float max;

    public RandomValueBounds(float f, float f2) {
        this.min = f;
        this.max = f2;
    }

    public RandomValueBounds(float f) {
        this.min = f;
        this.max = f;
    }

    public static RandomValueBounds between(float f, float f2) {
        return new RandomValueBounds(f, f2);
    }

    public float getMin() {
        return this.min;
    }

    public float getMax() {
        return this.max;
    }

    @Override
    public int getInt(Random random) {
        return Mth.nextInt(random, Mth.floor(this.min), Mth.floor(this.max));
    }

    public float getFloat(Random random) {
        return Mth.nextFloat(random, this.min, this.max);
    }

    public boolean matchesValue(int n) {
        return (float)n <= this.max && (float)n >= this.min;
    }

    @Override
    public ResourceLocation getType() {
        return UNIFORM;
    }

    public static class Serializer
    implements JsonDeserializer<RandomValueBounds>,
    JsonSerializer<RandomValueBounds> {
        public RandomValueBounds deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (GsonHelper.isNumberValue(jsonElement)) {
                return new RandomValueBounds(GsonHelper.convertToFloat(jsonElement, "value"));
            }
            JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, "value");
            float f = GsonHelper.getAsFloat(jsonObject, "min");
            float f2 = GsonHelper.getAsFloat(jsonObject, "max");
            return new RandomValueBounds(f, f2);
        }

        public JsonElement serialize(RandomValueBounds randomValueBounds, Type type, JsonSerializationContext jsonSerializationContext) {
            if (randomValueBounds.min == randomValueBounds.max) {
                return new JsonPrimitive((Number)Float.valueOf(randomValueBounds.min));
            }
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("min", (Number)Float.valueOf(randomValueBounds.min));
            jsonObject.addProperty("max", (Number)Float.valueOf(randomValueBounds.max));
            return jsonObject;
        }

        public /* synthetic */ JsonElement serialize(Object object, Type type, JsonSerializationContext jsonSerializationContext) {
            return this.serialize((RandomValueBounds)object, type, jsonSerializationContext);
        }

        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }

}

