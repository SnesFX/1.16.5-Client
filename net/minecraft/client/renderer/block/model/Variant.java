/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 */
package net.minecraft.client.renderer.block.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.math.Transformation;
import java.lang.reflect.Type;
import java.util.Objects;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class Variant
implements ModelState {
    private final ResourceLocation modelLocation;
    private final Transformation rotation;
    private final boolean uvLock;
    private final int weight;

    public Variant(ResourceLocation resourceLocation, Transformation transformation, boolean bl, int n) {
        this.modelLocation = resourceLocation;
        this.rotation = transformation;
        this.uvLock = bl;
        this.weight = n;
    }

    public ResourceLocation getModelLocation() {
        return this.modelLocation;
    }

    @Override
    public Transformation getRotation() {
        return this.rotation;
    }

    @Override
    public boolean isUvLocked() {
        return this.uvLock;
    }

    public int getWeight() {
        return this.weight;
    }

    public String toString() {
        return "Variant{modelLocation=" + this.modelLocation + ", rotation=" + this.rotation + ", uvLock=" + this.uvLock + ", weight=" + this.weight + '}';
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof Variant) {
            Variant variant = (Variant)object;
            return this.modelLocation.equals(variant.modelLocation) && Objects.equals(this.rotation, variant.rotation) && this.uvLock == variant.uvLock && this.weight == variant.weight;
        }
        return false;
    }

    public int hashCode() {
        int n = this.modelLocation.hashCode();
        n = 31 * n + this.rotation.hashCode();
        n = 31 * n + Boolean.valueOf(this.uvLock).hashCode();
        n = 31 * n + this.weight;
        return n;
    }

    public static class Deserializer
    implements JsonDeserializer<Variant> {
        public Variant deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            ResourceLocation resourceLocation = this.getModel(jsonObject);
            BlockModelRotation blockModelRotation = this.getBlockRotation(jsonObject);
            boolean bl = this.getUvLock(jsonObject);
            int n = this.getWeight(jsonObject);
            return new Variant(resourceLocation, blockModelRotation.getRotation(), bl, n);
        }

        private boolean getUvLock(JsonObject jsonObject) {
            return GsonHelper.getAsBoolean(jsonObject, "uvlock", false);
        }

        protected BlockModelRotation getBlockRotation(JsonObject jsonObject) {
            int n;
            int n2 = GsonHelper.getAsInt(jsonObject, "x", 0);
            BlockModelRotation blockModelRotation = BlockModelRotation.by(n2, n = GsonHelper.getAsInt(jsonObject, "y", 0));
            if (blockModelRotation == null) {
                throw new JsonParseException("Invalid BlockModelRotation x: " + n2 + ", y: " + n);
            }
            return blockModelRotation;
        }

        protected ResourceLocation getModel(JsonObject jsonObject) {
            return new ResourceLocation(GsonHelper.getAsString(jsonObject, "model"));
        }

        protected int getWeight(JsonObject jsonObject) {
            int n = GsonHelper.getAsInt(jsonObject, "weight", 1);
            if (n < 1) {
                throw new JsonParseException("Invalid weight " + n + " found, expected integer >= 1");
            }
            return n;
        }

        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }

}

