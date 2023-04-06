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
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;

public class ExplosionCondition
implements LootItemCondition {
    private static final ExplosionCondition INSTANCE = new ExplosionCondition();

    private ExplosionCondition() {
    }

    @Override
    public LootItemConditionType getType() {
        return LootItemConditions.SURVIVES_EXPLOSION;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParams.EXPLOSION_RADIUS);
    }

    @Override
    public boolean test(LootContext lootContext) {
        Float f = lootContext.getParamOrNull(LootContextParams.EXPLOSION_RADIUS);
        if (f != null) {
            Random random = lootContext.getRandom();
            float f2 = 1.0f / f.floatValue();
            return random.nextFloat() <= f2;
        }
        return true;
    }

    public static LootItemCondition.Builder survivesExplosion() {
        return () -> INSTANCE;
    }

    @Override
    public /* synthetic */ boolean test(Object object) {
        return this.test((LootContext)object);
    }

    public static class Serializer
    implements net.minecraft.world.level.storage.loot.Serializer<ExplosionCondition> {
        @Override
        public void serialize(JsonObject jsonObject, ExplosionCondition explosionCondition, JsonSerializationContext jsonSerializationContext) {
        }

        @Override
        public ExplosionCondition deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return INSTANCE;
        }

        @Override
        public /* synthetic */ Object deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return this.deserialize(jsonObject, jsonDeserializationContext);
        }
    }

}

