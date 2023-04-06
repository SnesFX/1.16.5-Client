/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 */
package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;

public class LootItemRandomChanceWithLootingCondition
implements LootItemCondition {
    private final float percent;
    private final float lootingMultiplier;

    private LootItemRandomChanceWithLootingCondition(float f, float f2) {
        this.percent = f;
        this.lootingMultiplier = f2;
    }

    @Override
    public LootItemConditionType getType() {
        return LootItemConditions.RANDOM_CHANCE_WITH_LOOTING;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParams.KILLER_ENTITY);
    }

    @Override
    public boolean test(LootContext lootContext) {
        Entity entity = lootContext.getParamOrNull(LootContextParams.KILLER_ENTITY);
        int n = 0;
        if (entity instanceof LivingEntity) {
            n = EnchantmentHelper.getMobLooting((LivingEntity)entity);
        }
        return lootContext.getRandom().nextFloat() < this.percent + (float)n * this.lootingMultiplier;
    }

    public static LootItemCondition.Builder randomChanceAndLootingBoost(float f, float f2) {
        return () -> new LootItemRandomChanceWithLootingCondition(f, f2);
    }

    @Override
    public /* synthetic */ boolean test(Object object) {
        return this.test((LootContext)object);
    }

    public static class Serializer
    implements net.minecraft.world.level.storage.loot.Serializer<LootItemRandomChanceWithLootingCondition> {
        @Override
        public void serialize(JsonObject jsonObject, LootItemRandomChanceWithLootingCondition lootItemRandomChanceWithLootingCondition, JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("chance", (Number)Float.valueOf(lootItemRandomChanceWithLootingCondition.percent));
            jsonObject.addProperty("looting_multiplier", (Number)Float.valueOf(lootItemRandomChanceWithLootingCondition.lootingMultiplier));
        }

        @Override
        public LootItemRandomChanceWithLootingCondition deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return new LootItemRandomChanceWithLootingCondition(GsonHelper.getAsFloat(jsonObject, "chance"), GsonHelper.getAsFloat(jsonObject, "looting_multiplier"));
        }

        @Override
        public /* synthetic */ Object deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return this.deserialize(jsonObject, jsonDeserializationContext);
        }
    }

}

