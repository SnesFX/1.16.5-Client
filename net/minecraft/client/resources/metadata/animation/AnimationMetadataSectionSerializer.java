/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  org.apache.commons.lang3.Validate
 */
package net.minecraft.client.resources.metadata.animation;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.resources.metadata.animation.AnimationFrame;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.util.GsonHelper;
import org.apache.commons.lang3.Validate;

public class AnimationMetadataSectionSerializer
implements MetadataSectionSerializer<AnimationMetadataSection> {
    @Override
    public AnimationMetadataSection fromJson(JsonObject jsonObject) {
        int n;
        ArrayList arrayList = Lists.newArrayList();
        int n2 = GsonHelper.getAsInt(jsonObject, "frametime", 1);
        if (n2 != 1) {
            Validate.inclusiveBetween((long)1L, (long)Integer.MAX_VALUE, (long)n2, (String)"Invalid default frame time");
        }
        if (jsonObject.has("frames")) {
            try {
                JsonArray jsonArray = GsonHelper.getAsJsonArray(jsonObject, "frames");
                for (n = 0; n < jsonArray.size(); ++n) {
                    JsonElement jsonElement = jsonArray.get(n);
                    AnimationFrame animationFrame = this.getFrame(n, jsonElement);
                    if (animationFrame == null) continue;
                    arrayList.add(animationFrame);
                }
            }
            catch (ClassCastException classCastException) {
                throw new JsonParseException("Invalid animation->frames: expected array, was " + (Object)jsonObject.get("frames"), (Throwable)classCastException);
            }
        }
        int n3 = GsonHelper.getAsInt(jsonObject, "width", -1);
        n = GsonHelper.getAsInt(jsonObject, "height", -1);
        if (n3 != -1) {
            Validate.inclusiveBetween((long)1L, (long)Integer.MAX_VALUE, (long)n3, (String)"Invalid width");
        }
        if (n != -1) {
            Validate.inclusiveBetween((long)1L, (long)Integer.MAX_VALUE, (long)n, (String)"Invalid height");
        }
        boolean bl = GsonHelper.getAsBoolean(jsonObject, "interpolate", false);
        return new AnimationMetadataSection(arrayList, n3, n, n2, bl);
    }

    private AnimationFrame getFrame(int n, JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            return new AnimationFrame(GsonHelper.convertToInt(jsonElement, "frames[" + n + "]"));
        }
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, "frames[" + n + "]");
            int n2 = GsonHelper.getAsInt(jsonObject, "time", -1);
            if (jsonObject.has("time")) {
                Validate.inclusiveBetween((long)1L, (long)Integer.MAX_VALUE, (long)n2, (String)"Invalid frame time");
            }
            int n3 = GsonHelper.getAsInt(jsonObject, "index");
            Validate.inclusiveBetween((long)0L, (long)Integer.MAX_VALUE, (long)n3, (String)"Invalid frame index");
            return new AnimationFrame(n3, n2);
        }
        return null;
    }

    @Override
    public String getMetadataSectionName() {
        return "animation";
    }

    @Override
    public /* synthetic */ Object fromJson(JsonObject jsonObject) {
        return this.fromJson(jsonObject);
    }
}

