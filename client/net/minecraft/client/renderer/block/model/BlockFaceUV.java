/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer.block.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import net.minecraft.util.GsonHelper;

public class BlockFaceUV {
    public float[] uvs;
    public final int rotation;

    public BlockFaceUV(@Nullable float[] arrf, int n) {
        this.uvs = arrf;
        this.rotation = n;
    }

    public float getU(int n) {
        if (this.uvs == null) {
            throw new NullPointerException("uvs");
        }
        int n2 = this.getShiftedIndex(n);
        return this.uvs[n2 == 0 || n2 == 1 ? 0 : 2];
    }

    public float getV(int n) {
        if (this.uvs == null) {
            throw new NullPointerException("uvs");
        }
        int n2 = this.getShiftedIndex(n);
        return this.uvs[n2 == 0 || n2 == 3 ? 1 : 3];
    }

    private int getShiftedIndex(int n) {
        return (n + this.rotation / 90) % 4;
    }

    public int getReverseIndex(int n) {
        return (n + 4 - this.rotation / 90) % 4;
    }

    public void setMissingUv(float[] arrf) {
        if (this.uvs == null) {
            this.uvs = arrf;
        }
    }

    public static class Deserializer
    implements JsonDeserializer<BlockFaceUV> {
        protected Deserializer() {
        }

        public BlockFaceUV deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            float[] arrf = this.getUVs(jsonObject);
            int n = this.getRotation(jsonObject);
            return new BlockFaceUV(arrf, n);
        }

        protected int getRotation(JsonObject jsonObject) {
            int n = GsonHelper.getAsInt(jsonObject, "rotation", 0);
            if (n < 0 || n % 90 != 0 || n / 90 > 3) {
                throw new JsonParseException("Invalid rotation " + n + " found, only 0/90/180/270 allowed");
            }
            return n;
        }

        @Nullable
        private float[] getUVs(JsonObject jsonObject) {
            if (!jsonObject.has("uv")) {
                return null;
            }
            JsonArray jsonArray = GsonHelper.getAsJsonArray(jsonObject, "uv");
            if (jsonArray.size() != 4) {
                throw new JsonParseException("Expected 4 uv values, found: " + jsonArray.size());
            }
            float[] arrf = new float[4];
            for (int i = 0; i < arrf.length; ++i) {
                arrf[i] = GsonHelper.convertToFloat(jsonArray.get(i), "uv[" + i + "]");
            }
            return arrf;
        }

        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }

}

