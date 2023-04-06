/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 */
package net.minecraft.world.level.storage.loot.predicates;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;

public class LootItemRandomChanceCondition
implements LootItemCondition {
    private final float probability;

    private LootItemRandomChanceCondition(float f) {
        this.probability = f;
    }

    @Override
    public LootItemConditionType getType() {
        return LootItemConditions.RANDOM_CHANCE;
    }

    @Override
    public boolean test(LootContext lootContext) {
        return lootContext.getRandom().nextFloat() < this.probability;
    }

    public static LootItemCondition.Builder randomChance(float f) {
        return () -> new LootItemRandomChanceCondition(f);
    }

    @Override
    public /* synthetic */ boolean test(Object object) {
        return this.test((LootContext)object);
    }

    public static class Serializer
    implements net.minecraft.world.level.storage.loot.Serializer<LootItemRandomChanceCondition> {
        @Override
        public void serialize(JsonObject jsonObject, LootItemRandomChanceCondition lootItemRandomChanceCondition, JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("chance", (Number)Float.valueOf(lootItemRandomChanceCondition.probability));
        }

        @Override
        public LootItemRandomChanceCondition deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return new LootItemRandomChanceCondition(GsonHelper.getAsFloat(jsonObject, "chance"));
        }

        @Override
        public /* synthetic */ Object deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return this.deserialize(jsonObject, jsonDeserializationContext);
        }
    }

}

