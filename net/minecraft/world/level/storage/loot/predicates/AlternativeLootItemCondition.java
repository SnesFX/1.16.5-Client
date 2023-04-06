/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 */
package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;

public class AlternativeLootItemCondition
implements LootItemCondition {
    private final LootItemCondition[] terms;
    private final Predicate<LootContext> composedPredicate;

    private AlternativeLootItemCondition(LootItemCondition[] arrlootItemCondition) {
        this.terms = arrlootItemCondition;
        this.composedPredicate = LootItemConditions.orConditions(arrlootItemCondition);
    }

    @Override
    public LootItemConditionType getType() {
        return LootItemConditions.ALTERNATIVE;
    }

    @Override
    public final boolean test(LootContext lootContext) {
        return this.composedPredicate.test(lootContext);
    }

    @Override
    public void validate(ValidationContext validationContext) {
        LootItemCondition.super.validate(validationContext);
        for (int i = 0; i < this.terms.length; ++i) {
            this.terms[i].validate(validationContext.forChild(".term[" + i + "]"));
        }
    }

    public static Builder alternative(LootItemCondition.Builder ... arrbuilder) {
        return new Builder(arrbuilder);
    }

    @Override
    public /* synthetic */ boolean test(Object object) {
        return this.test((LootContext)object);
    }

    public static class Serializer
    implements net.minecraft.world.level.storage.loot.Serializer<AlternativeLootItemCondition> {
        @Override
        public void serialize(JsonObject jsonObject, AlternativeLootItemCondition alternativeLootItemCondition, JsonSerializationContext jsonSerializationContext) {
            jsonObject.add("terms", jsonSerializationContext.serialize((Object)alternativeLootItemCondition.terms));
        }

        @Override
        public AlternativeLootItemCondition deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            LootItemCondition[] arrlootItemCondition = GsonHelper.getAsObject(jsonObject, "terms", jsonDeserializationContext, LootItemCondition[].class);
            return new AlternativeLootItemCondition(arrlootItemCondition);
        }

        @Override
        public /* synthetic */ Object deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return this.deserialize(jsonObject, jsonDeserializationContext);
        }
    }

    public static class Builder
    implements LootItemCondition.Builder {
        private final List<LootItemCondition> terms = Lists.newArrayList();

        public Builder(LootItemCondition.Builder ... arrbuilder) {
            for (LootItemCondition.Builder builder : arrbuilder) {
                this.terms.add(builder.build());
            }
        }

        @Override
        public Builder or(LootItemCondition.Builder builder) {
            this.terms.add(builder.build());
            return this;
        }

        @Override
        public LootItemCondition build() {
            return new AlternativeLootItemCondition(this.terms.toArray(new LootItemCondition[0]));
        }
    }

}

