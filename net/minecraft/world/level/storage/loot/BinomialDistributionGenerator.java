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
 */
package net.minecraft.world.level.storage.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Random;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.RandomIntGenerator;

public final class BinomialDistributionGenerator
implements RandomIntGenerator {
    private final int n;
    private final float p;

    public BinomialDistributionGenerator(int n, float f) {
        this.n = n;
        this.p = f;
    }

    @Override
    public int getInt(Random random) {
        int n = 0;
        for (int i = 0; i < this.n; ++i) {
            if (!(random.nextFloat() < this.p)) continue;
            ++n;
        }
        return n;
    }

    public static BinomialDistributionGenerator binomial(int n, float f) {
        return new BinomialDistributionGenerator(n, f);
    }

    @Override
    public ResourceLocation getType() {
        return BINOMIAL;
    }

    public static class Serializer
    implements JsonDeserializer<BinomialDistributionGenerator>,
    JsonSerializer<BinomialDistributionGenerator> {
        public BinomialDistributionGenerator deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, "value");
            int n = GsonHelper.getAsInt(jsonObject, "n");
            float f = GsonHelper.getAsFloat(jsonObject, "p");
            return new BinomialDistributionGenerator(n, f);
        }

        public JsonElement serialize(BinomialDistributionGenerator binomialDistributionGenerator, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("n", (Number)binomialDistributionGenerator.n);
            jsonObject.addProperty("p", (Number)Float.valueOf(binomialDistributionGenerator.p));
            return jsonObject;
        }

        public /* synthetic */ JsonElement serialize(Object object, Type type, JsonSerializationContext jsonSerializationContext) {
            return this.serialize((BinomialDistributionGenerator)object, type, jsonSerializationContext);
        }

        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }

}

