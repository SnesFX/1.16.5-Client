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
import java.lang.reflect.Type;
import java.util.function.IntUnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;

public class IntLimiter
implements IntUnaryOperator {
    private final Integer min;
    private final Integer max;
    private final IntUnaryOperator op;

    private IntLimiter(@Nullable Integer n4, @Nullable Integer n5) {
        this.min = n4;
        this.max = n5;
        if (n4 == null) {
            if (n5 == null) {
                this.op = n -> n;
            } else {
                int n6 = n5;
                this.op = n2 -> Math.min(n6, n2);
            }
        } else {
            int n7 = n4;
            if (n5 == null) {
                this.op = n2 -> Math.max(n7, n2);
            } else {
                int n8 = n5;
                this.op = n3 -> Mth.clamp(n3, n7, n8);
            }
        }
    }

    public static IntLimiter clamp(int n, int n2) {
        return new IntLimiter(n, n2);
    }

    public static IntLimiter lowerBound(int n) {
        return new IntLimiter(n, null);
    }

    public static IntLimiter upperBound(int n) {
        return new IntLimiter(null, n);
    }

    @Override
    public int applyAsInt(int n) {
        return this.op.applyAsInt(n);
    }

    public static class Serializer
    implements JsonDeserializer<IntLimiter>,
    JsonSerializer<IntLimiter> {
        public IntLimiter deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, "value");
            Integer n = jsonObject.has("min") ? Integer.valueOf(GsonHelper.getAsInt(jsonObject, "min")) : null;
            Integer n2 = jsonObject.has("max") ? Integer.valueOf(GsonHelper.getAsInt(jsonObject, "max")) : null;
            return new IntLimiter(n, n2);
        }

        public JsonElement serialize(IntLimiter intLimiter, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            if (intLimiter.max != null) {
                jsonObject.addProperty("max", (Number)intLimiter.max);
            }
            if (intLimiter.min != null) {
                jsonObject.addProperty("min", (Number)intLimiter.min);
            }
            return jsonObject;
        }

        public /* synthetic */ JsonElement serialize(Object object, Type type, JsonSerializationContext jsonSerializationContext) {
            return this.serialize((IntLimiter)object, type, jsonSerializationContext);
        }

        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }

}

