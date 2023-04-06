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
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;

public class DistancePredicate {
    public static final DistancePredicate ANY = new DistancePredicate(MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY);
    private final MinMaxBounds.Floats x;
    private final MinMaxBounds.Floats y;
    private final MinMaxBounds.Floats z;
    private final MinMaxBounds.Floats horizontal;
    private final MinMaxBounds.Floats absolute;

    public DistancePredicate(MinMaxBounds.Floats floats, MinMaxBounds.Floats floats2, MinMaxBounds.Floats floats3, MinMaxBounds.Floats floats4, MinMaxBounds.Floats floats5) {
        this.x = floats;
        this.y = floats2;
        this.z = floats3;
        this.horizontal = floats4;
        this.absolute = floats5;
    }

    public static DistancePredicate horizontal(MinMaxBounds.Floats floats) {
        return new DistancePredicate(MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, floats, MinMaxBounds.Floats.ANY);
    }

    public static DistancePredicate vertical(MinMaxBounds.Floats floats) {
        return new DistancePredicate(MinMaxBounds.Floats.ANY, floats, MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY);
    }

    public boolean matches(double d, double d2, double d3, double d4, double d5, double d6) {
        float f = (float)(d - d4);
        float f2 = (float)(d2 - d5);
        float f3 = (float)(d3 - d6);
        if (!(this.x.matches(Mth.abs(f)) && this.y.matches(Mth.abs(f2)) && this.z.matches(Mth.abs(f3)))) {
            return false;
        }
        if (!this.horizontal.matchesSqr(f * f + f3 * f3)) {
            return false;
        }
        return this.absolute.matchesSqr(f * f + f2 * f2 + f3 * f3);
    }

    public static DistancePredicate fromJson(@Nullable JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return ANY;
        }
        JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, "distance");
        MinMaxBounds.Floats floats = MinMaxBounds.Floats.fromJson(jsonObject.get("x"));
        MinMaxBounds.Floats floats2 = MinMaxBounds.Floats.fromJson(jsonObject.get("y"));
        MinMaxBounds.Floats floats3 = MinMaxBounds.Floats.fromJson(jsonObject.get("z"));
        MinMaxBounds.Floats floats4 = MinMaxBounds.Floats.fromJson(jsonObject.get("horizontal"));
        MinMaxBounds.Floats floats5 = MinMaxBounds.Floats.fromJson(jsonObject.get("absolute"));
        return new DistancePredicate(floats, floats2, floats3, floats4, floats5);
    }

    public JsonElement serializeToJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("x", this.x.serializeToJson());
        jsonObject.add("y", this.y.serializeToJson());
        jsonObject.add("z", this.z.serializeToJson());
        jsonObject.add("horizontal", this.horizontal.serializeToJson());
        jsonObject.add("absolute", this.absolute.serializeToJson());
        return jsonObject;
    }
}

