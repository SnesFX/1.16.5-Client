/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 *  javax.annotation.Nullable
 */
package net.minecraft.advancements;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;

public class AdvancementProgress
implements Comparable<AdvancementProgress> {
    private final Map<String, CriterionProgress> criteria = Maps.newHashMap();
    private String[][] requirements = new String[0][];

    public void update(Map<String, Criterion> map, String[][] arrstring) {
        Set<String> set = map.keySet();
        this.criteria.entrySet().removeIf(entry -> !set.contains(entry.getKey()));
        for (String string : set) {
            if (this.criteria.containsKey(string)) continue;
            this.criteria.put(string, new CriterionProgress());
        }
        this.requirements = arrstring;
    }

    public boolean isDone() {
        if (this.requirements.length == 0) {
            return false;
        }
        for (String[] arrstring : this.requirements) {
            boolean bl = false;
            for (String string : arrstring) {
                CriterionProgress criterionProgress = this.getCriterion(string);
                if (criterionProgress == null || !criterionProgress.isDone()) continue;
                bl = true;
                break;
            }
            if (bl) continue;
            return false;
        }
        return true;
    }

    public boolean hasProgress() {
        for (CriterionProgress criterionProgress : this.criteria.values()) {
            if (!criterionProgress.isDone()) continue;
            return true;
        }
        return false;
    }

    public boolean grantProgress(String string) {
        CriterionProgress criterionProgress = this.criteria.get(string);
        if (criterionProgress != null && !criterionProgress.isDone()) {
            criterionProgress.grant();
            return true;
        }
        return false;
    }

    public boolean revokeProgress(String string) {
        CriterionProgress criterionProgress = this.criteria.get(string);
        if (criterionProgress != null && criterionProgress.isDone()) {
            criterionProgress.revoke();
            return true;
        }
        return false;
    }

    public String toString() {
        return "AdvancementProgress{criteria=" + this.criteria + ", requirements=" + Arrays.deepToString((Object[])this.requirements) + '}';
    }

    public void serializeToNetwork(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeVarInt(this.criteria.size());
        for (Map.Entry<String, CriterionProgress> entry : this.criteria.entrySet()) {
            friendlyByteBuf.writeUtf(entry.getKey());
            entry.getValue().serializeToNetwork(friendlyByteBuf);
        }
    }

    public static AdvancementProgress fromNetwork(FriendlyByteBuf friendlyByteBuf) {
        AdvancementProgress advancementProgress = new AdvancementProgress();
        int n = friendlyByteBuf.readVarInt();
        for (int i = 0; i < n; ++i) {
            advancementProgress.criteria.put(friendlyByteBuf.readUtf(32767), CriterionProgress.fromNetwork(friendlyByteBuf));
        }
        return advancementProgress;
    }

    @Nullable
    public CriterionProgress getCriterion(String string) {
        return this.criteria.get(string);
    }

    public float getPercent() {
        if (this.criteria.isEmpty()) {
            return 0.0f;
        }
        float f = this.requirements.length;
        float f2 = this.countCompletedRequirements();
        return f2 / f;
    }

    @Nullable
    public String getProgressText() {
        if (this.criteria.isEmpty()) {
            return null;
        }
        int n = this.requirements.length;
        if (n <= 1) {
            return null;
        }
        int n2 = this.countCompletedRequirements();
        return n2 + "/" + n;
    }

    private int countCompletedRequirements() {
        int n = 0;
        for (String[] arrstring : this.requirements) {
            boolean bl = false;
            for (String string : arrstring) {
                CriterionProgress criterionProgress = this.getCriterion(string);
                if (criterionProgress == null || !criterionProgress.isDone()) continue;
                bl = true;
                break;
            }
            if (!bl) continue;
            ++n;
        }
        return n;
    }

    public Iterable<String> getRemainingCriteria() {
        ArrayList arrayList = Lists.newArrayList();
        for (Map.Entry<String, CriterionProgress> entry : this.criteria.entrySet()) {
            if (entry.getValue().isDone()) continue;
            arrayList.add(entry.getKey());
        }
        return arrayList;
    }

    public Iterable<String> getCompletedCriteria() {
        ArrayList arrayList = Lists.newArrayList();
        for (Map.Entry<String, CriterionProgress> entry : this.criteria.entrySet()) {
            if (!entry.getValue().isDone()) continue;
            arrayList.add(entry.getKey());
        }
        return arrayList;
    }

    @Nullable
    public Date getFirstProgressDate() {
        Date date = null;
        for (CriterionProgress criterionProgress : this.criteria.values()) {
            if (!criterionProgress.isDone() || date != null && !criterionProgress.getObtained().before(date)) continue;
            date = criterionProgress.getObtained();
        }
        return date;
    }

    @Override
    public int compareTo(AdvancementProgress advancementProgress) {
        Date date = this.getFirstProgressDate();
        Date date2 = advancementProgress.getFirstProgressDate();
        if (date == null && date2 != null) {
            return 1;
        }
        if (date != null && date2 == null) {
            return -1;
        }
        if (date == null && date2 == null) {
            return 0;
        }
        return date.compareTo(date2);
    }

    @Override
    public /* synthetic */ int compareTo(Object object) {
        return this.compareTo((AdvancementProgress)object);
    }

    public static class Serializer
    implements JsonDeserializer<AdvancementProgress>,
    JsonSerializer<AdvancementProgress> {
        public JsonElement serialize(AdvancementProgress advancementProgress, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            JsonObject jsonObject2 = new JsonObject();
            for (Map.Entry entry : advancementProgress.criteria.entrySet()) {
                CriterionProgress criterionProgress = (CriterionProgress)entry.getValue();
                if (!criterionProgress.isDone()) continue;
                jsonObject2.add((String)entry.getKey(), criterionProgress.serializeToJson());
            }
            if (!jsonObject2.entrySet().isEmpty()) {
                jsonObject.add("criteria", (JsonElement)jsonObject2);
            }
            jsonObject.addProperty("done", Boolean.valueOf(advancementProgress.isDone()));
            return jsonObject;
        }

        public AdvancementProgress deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, "advancement");
            JsonObject jsonObject2 = GsonHelper.getAsJsonObject(jsonObject, "criteria", new JsonObject());
            AdvancementProgress advancementProgress = new AdvancementProgress();
            for (Map.Entry entry : jsonObject2.entrySet()) {
                String string = (String)entry.getKey();
                advancementProgress.criteria.put(string, CriterionProgress.fromJson(GsonHelper.convertToString((JsonElement)entry.getValue(), string)));
            }
            return advancementProgress;
        }

        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }

        public /* synthetic */ JsonElement serialize(Object object, Type type, JsonSerializationContext jsonSerializationContext) {
            return this.serialize((AdvancementProgress)object, type, jsonSerializationContext);
        }
    }

}

