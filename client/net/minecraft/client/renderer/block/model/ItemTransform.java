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
 */
package net.minecraft.client.renderer.block.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import java.lang.reflect.Type;
import net.minecraft.util.GsonHelper;

public class ItemTransform {
    public static final ItemTransform NO_TRANSFORM = new ItemTransform(new Vector3f(), new Vector3f(), new Vector3f(1.0f, 1.0f, 1.0f));
    public final Vector3f rotation;
    public final Vector3f translation;
    public final Vector3f scale;

    public ItemTransform(Vector3f vector3f, Vector3f vector3f2, Vector3f vector3f3) {
        this.rotation = vector3f.copy();
        this.translation = vector3f2.copy();
        this.scale = vector3f3.copy();
    }

    public void apply(boolean bl, PoseStack poseStack) {
        if (this == NO_TRANSFORM) {
            return;
        }
        float f = this.rotation.x();
        float f2 = this.rotation.y();
        float f3 = this.rotation.z();
        if (bl) {
            f2 = -f2;
            f3 = -f3;
        }
        int n = bl ? -1 : 1;
        poseStack.translate((float)n * this.translation.x(), this.translation.y(), this.translation.z());
        poseStack.mulPose(new Quaternion(f, f2, f3, true));
        poseStack.scale(this.scale.x(), this.scale.y(), this.scale.z());
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (this.getClass() == object.getClass()) {
            ItemTransform itemTransform = (ItemTransform)object;
            return this.rotation.equals(itemTransform.rotation) && this.scale.equals(itemTransform.scale) && this.translation.equals(itemTransform.translation);
        }
        return false;
    }

    public int hashCode() {
        int n = this.rotation.hashCode();
        n = 31 * n + this.translation.hashCode();
        n = 31 * n + this.scale.hashCode();
        return n;
    }

    public static class Deserializer
    implements JsonDeserializer<ItemTransform> {
        private static final Vector3f DEFAULT_ROTATION = new Vector3f(0.0f, 0.0f, 0.0f);
        private static final Vector3f DEFAULT_TRANSLATION = new Vector3f(0.0f, 0.0f, 0.0f);
        private static final Vector3f DEFAULT_SCALE = new Vector3f(1.0f, 1.0f, 1.0f);

        protected Deserializer() {
        }

        public ItemTransform deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Vector3f vector3f = this.getVector3f(jsonObject, "rotation", DEFAULT_ROTATION);
            Vector3f vector3f2 = this.getVector3f(jsonObject, "translation", DEFAULT_TRANSLATION);
            vector3f2.mul(0.0625f);
            vector3f2.clamp(-5.0f, 5.0f);
            Vector3f vector3f3 = this.getVector3f(jsonObject, "scale", DEFAULT_SCALE);
            vector3f3.clamp(-4.0f, 4.0f);
            return new ItemTransform(vector3f, vector3f2, vector3f3);
        }

        private Vector3f getVector3f(JsonObject jsonObject, String string, Vector3f vector3f) {
            if (!jsonObject.has(string)) {
                return vector3f;
            }
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

