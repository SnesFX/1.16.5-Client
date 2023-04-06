/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.storage.loot.predicates;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;

public class WeatherCheck
implements LootItemCondition {
    @Nullable
    private final Boolean isRaining;
    @Nullable
    private final Boolean isThundering;

    private WeatherCheck(@Nullable Boolean bl, @Nullable Boolean bl2) {
        this.isRaining = bl;
        this.isThundering = bl2;
    }

    @Override
    public LootItemConditionType getType() {
        return LootItemConditions.WEATHER_CHECK;
    }

    @Override
    public boolean test(LootContext lootContext) {
        ServerLevel serverLevel = lootContext.getLevel();
        if (this.isRaining != null && this.isRaining.booleanValue() != serverLevel.isRaining()) {
            return false;
        }
        return this.isThundering == null || this.isThundering.booleanValue() == serverLevel.isThundering();
    }

    @Override
    public /* synthetic */ boolean test(Object object) {
        return this.test((LootContext)object);
    }

    public static class Serializer
    implements net.minecraft.world.level.storage.loot.Serializer<WeatherCheck> {
        @Override
        public void serialize(JsonObject jsonObject, WeatherCheck weatherCheck, JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("raining", weatherCheck.isRaining);
            jsonObject.addProperty("thundering", weatherCheck.isThundering);
        }

        @Override
        public WeatherCheck deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            Boolean bl = jsonObject.has("raining") ? Boolean.valueOf(GsonHelper.getAsBoolean(jsonObject, "raining")) : null;
            Boolean bl2 = jsonObject.has("thundering") ? Boolean.valueOf(GsonHelper.getAsBoolean(jsonObject, "thundering")) : null;
            return new WeatherCheck(bl, bl2);
        }

        @Override
        public /* synthetic */ Object deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return this.deserialize(jsonObject, jsonDeserializationContext);
        }
    }

}

