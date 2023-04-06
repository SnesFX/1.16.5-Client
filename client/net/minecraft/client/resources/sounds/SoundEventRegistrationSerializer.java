/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  org.apache.commons.lang3.Validate
 */
package net.minecraft.client.resources.sounds;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundEventRegistration;
import net.minecraft.util.GsonHelper;
import org.apache.commons.lang3.Validate;

public class SoundEventRegistrationSerializer
implements JsonDeserializer<SoundEventRegistration> {
    public SoundEventRegistration deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, "entry");
        boolean bl = GsonHelper.getAsBoolean(jsonObject, "replace", false);
        String string = GsonHelper.getAsString(jsonObject, "subtitle", null);
        List<Sound> list = this.getSounds(jsonObject);
        return new SoundEventRegistration(list, bl, string);
    }

    private List<Sound> getSounds(JsonObject jsonObject) {
        ArrayList arrayList = Lists.newArrayList();
        if (jsonObject.has("sounds")) {
            JsonArray jsonArray = GsonHelper.getAsJsonArray(jsonObject, "sounds");
            for (int i = 0; i < jsonArray.size(); ++i) {
                JsonElement jsonElement = jsonArray.get(i);
                if (GsonHelper.isStringValue(jsonElement)) {
                    String string = GsonHelper.convertToString(jsonElement, "sound");
                    arrayList.add(new Sound(string, 1.0f, 1.0f, 1, Sound.Type.FILE, false, false, 16));
                    continue;
                }
                arrayList.add(this.getSound(GsonHelper.convertToJsonObject(jsonElement, "sound")));
            }
        }
        return arrayList;
    }

    private Sound getSound(JsonObject jsonObject) {
        String string = GsonHelper.getAsString(jsonObject, "name");
        Sound.Type type = this.getType(jsonObject, Sound.Type.FILE);
        float f = GsonHelper.getAsFloat(jsonObject, "volume", 1.0f);
        Validate.isTrue((boolean)(f > 0.0f), (String)"Invalid volume", (Object[])new Object[0]);
        float f2 = GsonHelper.getAsFloat(jsonObject, "pitch", 1.0f);
        Validate.isTrue((boolean)(f2 > 0.0f), (String)"Invalid pitch", (Object[])new Object[0]);
        int n = GsonHelper.getAsInt(jsonObject, "weight", 1);
        Validate.isTrue((boolean)(n > 0), (String)"Invalid weight", (Object[])new Object[0]);
        boolean bl = GsonHelper.getAsBoolean(jsonObject, "preload", false);
        boolean bl2 = GsonHelper.getAsBoolean(jsonObject, "stream", false);
        int n2 = GsonHelper.getAsInt(jsonObject, "attenuation_distance", 16);
        return new Sound(string, f, f2, n, type, bl2, bl, n2);
    }

    private Sound.Type getType(JsonObject jsonObject, Sound.Type type) {
        Sound.Type type2 = type;
        if (jsonObject.has("type")) {
            type2 = Sound.Type.getByName(GsonHelper.getAsString(jsonObject, "type"));
            Validate.notNull((Object)((Object)type2), (String)"Invalid type", (Object[])new Object[0]);
        }
        return type2;
    }

    public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return this.deserialize(jsonElement, type, jsonDeserializationContext);
    }
}

