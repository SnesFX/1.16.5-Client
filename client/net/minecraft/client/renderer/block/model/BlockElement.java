/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
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
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.math.Vector3f;
import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockElementRotation;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.core.Direction;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;

public class BlockElement {
    public final Vector3f from;
    public final Vector3f to;
    public final Map<Direction, BlockElementFace> faces;
    public final BlockElementRotation rotation;
    public final boolean shade;

    public BlockElement(Vector3f vector3f, Vector3f vector3f2, Map<Direction, BlockElementFace> map, @Nullable BlockElementRotation blockElementRotation, boolean bl) {
        this.from = vector3f;
        this.to = vector3f2;
        this.faces = map;
        this.rotation = blockElementRotation;
        this.shade = bl;
        this.fillUvs();
    }

    private void fillUvs() {
        for (Map.Entry<Direction, BlockElementFace> entry : this.faces.entrySet()) {
            float[] arrf = this.uvsByFace(entry.getKey());
            entry.getValue().uv.setMissingUv(arrf);
        }
    }

    private float[] uvsByFace(Direction direction) {
        switch (direction) {
            case DOWN: {
                return new float[]{this.from.x(), 16.0f - this.to.z(), this.to.x(), 16.0f - this.from.z()};
            }
            case UP: {
                return new float[]{this.from.x(), this.from.z(), this.to.x(), this.to.z()};
            }
            default: {
                return new float[]{16.0f - this.to.x(), 16.0f - this.to.y(), 16.0f - this.from.x(), 16.0f - this.from.y()};
            }
            case SOUTH: {
                return new float[]{this.from.x(), 16.0f - this.to.y(), this.to.x(), 16.0f - this.from.y()};
            }
            case WEST: {
                return new float[]{this.from.z(), 16.0f - this.to.y(), this.to.z(), 16.0f - this.from.y()};
            }
            case EAST: 
        }
        return new float[]{16.0f - this.to.z(), 16.0f - this.to.y(), 16.0f - this.from.z(), 16.0f - this.from.y()};
    }

    public static class Deserializer
    implements JsonDeserializer<BlockElement> {
        protected Deserializer() {
        }

        public BlockElement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Vector3f vector3f = this.getFrom(jsonObject);
            Vector3f vector3f2 = this.getTo(jsonObject);
            BlockElementRotation blockElementRotation = this.getRotation(jsonObject);
            Map<Direction, BlockElementFace> map = this.getFaces(jsonDeserializationContext, jsonObject);
            if (jsonObject.has("shade") && !GsonHelper.isBooleanValue(jsonObject, "shade")) {
                throw new JsonParseException("Expected shade to be a Boolean");
            }
            boolean bl = GsonHelper.getAsBoolean(jsonObject, "shade", true);
            return new BlockElement(vector3f, vector3f2, map, blockElementRotation, bl);
        }

        @Nullable
        private BlockElementRotation getRotation(JsonObject jsonObject) {
            BlockElementRotation blockElementRotation = null;
            if (jsonObject.has("rotation")) {
                JsonObject jsonObject2 = GsonHelper.getAsJsonObject(jsonObject, "rotation");
                Vector3f vector3f = this.getVector3f(jsonObject2, "origin");
                vector3f.mul(0.0625f);
                Direction.Axis axis = this.getAxis(jsonObject2);
                float f = this.getAngle(jsonObject2);
                boolean bl = GsonHelper.getAsBoolean(jsonObject2, "rescale", false);
                blockElementRotation = new BlockElementRotation(vector3f, axis, f, bl);
            }
            return blockElementRotation;
        }

        private float getAngle(JsonObject jsonObject) {
            float f = GsonHelper.getAsFloat(jsonObject, "angle");
            if (f != 0.0f && Mth.abs(f) != 22.5f && Mth.abs(f) != 45.0f) {
                throw new JsonParseException("Invalid rotation " + f + " found, only -45/-22.5/0/22.5/45 allowed");
            }
            return f;
        }

        private Direction.Axis getAxis(JsonObject jsonObject) {
            String string = GsonHelper.getAsString(jsonObject, "axis");
            Direction.Axis axis = Direction.Axis.byName(string.toLowerCase(Locale.ROOT));
            if (axis == null) {
                throw new JsonParseException("Invalid rotation axis: " + string);
            }
            return axis;
        }

        private Map<Direction, BlockElementFace> getFaces(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
            Map<Direction, BlockElementFace> map = this.filterNullFromFaces(jsonDeserializationContext, jsonObject);
            if (map.isEmpty()) {
                throw new JsonParseException("Expected between 1 and 6 unique faces, got 0");
            }
            return map;
        }

        private Map<Direction, BlockElementFace> filterNullFromFaces(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
            EnumMap enumMap = Maps.newEnumMap(Direction.class);
            JsonObject jsonObject2 = GsonHelper.getAsJsonObject(jsonObject, "faces");
            for (Map.Entry entry : jsonObject2.entrySet()) {
                Direction direction = this.getFacing((String)entry.getKey());
                enumMap.put(direction, jsonDeserializationContext.deserialize((JsonElement)entry.getValue(), BlockElementFace.class));
            }
            return enumMap;
        }

        private Direction getFacing(String string) {
            Direction direction = Direction.byName(string);
            if (direction == null) {
                throw new JsonParseException("Unknown facing: " + string);
            }
            return direction;
        }

        private Vector3f getTo(JsonObject jsonObject) {
            Vector3f vector3f = this.getVector3f(jsonObject, "to");
            if (vector3f.x() < -16.0f || vector3f.y() < -16.0f || vector3f.z() < -16.0f || vector3f.x() > 32.0f || vector3f.y() > 32.0f || vector3f.z() > 32.0f) {
                throw new JsonParseException("'to' specifier exceeds the allowed boundaries: " + vector3f);
            }
            return vector3f;
        }

        private Vector3f getFrom(JsonObject jsonObject) {
            Vector3f vector3f = this.getVector3f(jsonObject, "from");
            if (vector3f.x() < -16.0f || vector3f.y() < -16.0f || vector3f.z() < -16.0f || vector3f.x() > 32.0f || vector3f.y() > 32.0f || vector3f.z() > 32.0f) {
                throw new JsonParseException("'from' specifier exceeds the allowed boundaries: " + vector3f);
            }
            return vector3f;
        }

        private Vector3f getVector3f(JsonObject jsonObject, String string) {
            JsonArray jsonArray = GsonHelper.getAsJsonArray(jsonObject, string);
            if (jsonArray.size() != 3) {
                throw new JsonParseException("Expected 3 " + string + " values, found: " + jsonArray.size());
            }
            float[] arrf = new float[3];
            for (int i = 0; i < arrf.length; ++i) {
                arrf[i] = GsonHelper.convertToFloat(jsonArray.get(i), string + "[" + i + "]");
            }
            return new Vector3f(arrf[0], arrf[1], arrf[2]);
        }

        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }

}

