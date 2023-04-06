/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonPrimitive
 *  javax.annotation.Nullable
 */
package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import javax.annotation.Nullable;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.FishingHook;

public class FishingHookPredicate {
    public static final FishingHookPredicate ANY = new FishingHookPredicate(false);
    private boolean inOpenWater;

    private FishingHookPredicate(boolean bl) {
        this.inOpenWater = bl;
    }

    public static FishingHookPredicate inOpenWater(boolean bl) {
        return new FishingHookPredicate(bl);
    }

    public static FishingHookPredicate fromJson(@Nullable JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return ANY;
        }
        JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, "fishing_hook");
        JsonElement jsonElement2 = jsonObject.get("in_open_water");
        if (jsonElement2 != null) {
            return new FishingHookPredicate(GsonHelper.convertToBoolean(jsonElement2, "in_open_water"));
        }
        return ANY;
    }

    public JsonElement serializeToJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("in_open_water", (JsonElement)new JsonPrimitive(Boolean.valueOf(this.inOpenWater)));
        return jsonObject;
    }

    public boolean matches(Entity entity) {
        if (this == ANY) {
            return true;
        }
        if (!(entity instanceof FishingHook)) {
            return false;
        }
        FishingHook fishingHook = (FishingHook)entity;
        return this.inOpenWater == fishingHook.isOpenWaterFishing();
    }
}

