/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  javax.annotation.Nullable
 */
package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;

public class LightPredicate {
    public static final LightPredicate ANY = new LightPredicate(MinMaxBounds.Ints.ANY);
    private final MinMaxBounds.Ints composite;

    private LightPredicate(MinMaxBounds.Ints ints) {
        this.composite = ints;
    }

    public boolean matches(ServerLevel serverLevel, BlockPos blockPos) {
        if (this == ANY) {
            return true;
        }
        if (!serverLevel.isLoaded(blockPos)) {
            return false;
        }
        return this.composite.matches(serverLevel.getMaxLocalRawBrightness(blockPos));
    }

    public JsonElement serializeToJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("light", this.composite.serializeToJson());
        return jsonObject;
    }

    public static LightPredicate fromJson(@Nullable JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return ANY;
        }
        JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, "light");
        MinMaxBounds.Ints ints = MinMaxBounds.Ints.fromJson(jsonObject.get("light"));
        return new LightPredicate(ints);
    }
}

