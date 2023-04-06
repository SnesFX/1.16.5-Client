/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSyntaxException
 *  javax.annotation.Nullable
 */
package net.minecraft.advancements;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class Criterion {
    private final CriterionTriggerInstance trigger;

    public Criterion(CriterionTriggerInstance criterionTriggerInstance) {
        this.trigger = criterionTriggerInstance;
    }

    public Criterion() {
        this.trigger = null;
    }

    public void serializeToNetwork(FriendlyByteBuf friendlyByteBuf) {
    }

    public static Criterion criterionFromJson(JsonObject jsonObject, DeserializationContext deserializationContext) {
        ResourceLocation resourceLocation = new ResourceLocation(GsonHelper.getAsString(jsonObject, "trigger"));
        CriterionTrigger criterionTrigger = CriteriaTriggers.getCriterion(resourceLocation);
        if (criterionTrigger == null) {
            throw new JsonSyntaxException("Invalid criterion trigger: " + resourceLocation);
        }
        Object t = criterionTrigger.createInstance(GsonHelper.getAsJsonObject(jsonObject, "conditions", new JsonObject()), deserializationContext);
        return new Criterion((CriterionTriggerInstance)t);
    }

    public static Criterion criterionFromNetwork(FriendlyByteBuf friendlyByteBuf) {
        return new Criterion();
    }

    public static Map<String, Criterion> criteriaFromJson(JsonObject jsonObject, DeserializationContext deserializationContext) {
        HashMap hashMap = Maps.newHashMap();
        for (Map.Entry entry : jsonObject.entrySet()) {
            hashMap.put(entry.getKey(), Criterion.criterionFromJson(GsonHelper.convertToJsonObject((JsonElement)entry.getValue(), "criterion"), deserializationContext));
        }
        return hashMap;
    }

    public static Map<String, Criterion> criteriaFromNetwork(FriendlyByteBuf friendlyByteBuf) {
        HashMap hashMap = Maps.newHashMap();
        int n = friendlyByteBuf.readVarInt();
        for (int i = 0; i < n; ++i) {
            hashMap.put(friendlyByteBuf.readUtf(32767), Criterion.criterionFromNetwork(friendlyByteBuf));
        }
        return hashMap;
    }

    public static void serializeToNetwork(Map<String, Criterion> map, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeVarInt(map.size());
        for (Map.Entry<String, Criterion> entry : map.entrySet()) {
            friendlyByteBuf.writeUtf(entry.getKey());
            entry.getValue().serializeToNetwork(friendlyByteBuf);
        }
    }

    @Nullable
    public CriterionTriggerInstance getTrigger() {
        return this.trigger;
    }

    public JsonElement serializeToJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("trigger", this.trigger.getCriterion().toString());
        JsonObject jsonObject2 = this.trigger.serializeToJson(SerializationContext.INSTANCE);
        if (jsonObject2.size() != 0) {
            jsonObject.add("conditions", (JsonElement)jsonObject2);
        }
        return jsonObject;
    }
}

