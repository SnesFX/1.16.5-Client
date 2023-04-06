/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  org.apache.commons.lang3.ArrayUtils
 */
package net.minecraft.world.level.storage.loot.entries;

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
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.ComposableEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import org.apache.commons.lang3.ArrayUtils;

public abstract class LootPoolEntryContainer
implements ComposableEntryContainer {
    protected final LootItemCondition[] conditions;
    private final Predicate<LootContext> compositeCondition;

    protected LootPoolEntryContainer(LootItemCondition[] arrlootItemCondition) {
        this.conditions = arrlootItemCondition;
        this.compositeCondition = LootItemConditions.andConditions(arrlootItemCondition);
    }

    public void validate(ValidationContext validationContext) {
        for (int i = 0; i < this.conditions.length; ++i) {
            this.conditions[i].validate(validationContext.forChild(".condition[" + i + "]"));
        }
    }

    protected final boolean canRun(LootContext lootContext) {
        return this.compositeCondition.test(lootContext);
    }

    public abstract LootPoolEntryType getType();

    public static abstract class Serializer<T extends LootPoolEntryContainer>
    implements net.minecraft.world.level.storage.loot.Serializer<T> {
        @Override
        public final void serialize(JsonObject jsonObject, T t, JsonSerializationContext jsonSerializationContext) {
            if (!ArrayUtils.isEmpty((Object[])((LootPoolEntryContainer)t).conditions)) {
                jsonObject.add("conditions", jsonSerializationContext.serialize((Object)((LootPoolEntryContainer)t).conditions));
            }
            this.serializeCustom(jsonObject, t, jsonSerializationContext);
        }

        @Override
        public final T deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            LootItemCondition[] arrlootItemCondition = GsonHelper.getAsObject(jsonObject, "conditions", new LootItemCondition[0], jsonDeserializationContext, LootItemCondition[].class);
            return this.deserializeCustom(jsonObject, jsonDeserializationContext, arrlootItemCondition);
        }

        public abstract void serializeCustom(JsonObject var1, T var2, JsonSerializationContext var3);

        public abstract T deserializeCustom(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3);

        @Override
        public /* synthetic */ Object deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return this.deserialize(jsonObject, jsonDeserializationContext);
        }

        @Override
        public /* synthetic */ void serialize(JsonObject jsonObject, Object object, JsonSerializationContext jsonSerializationContext) {
            this.serialize(jsonObject, (T)((LootPoolEntryContainer)object), jsonSerializationContext);
        }
    }

    public static abstract class Builder<T extends Builder<T>>
    implements ConditionUserBuilder<T> {
        private final List<LootItemCondition> conditions = Lists.newArrayList();

        protected abstract T getThis();

        @Override
        public T when(LootItemCondition.Builder builder) {
            this.conditions.add(builder.build());
            return this.getThis();
        }

        @Override
        public final T unwrap() {
            return this.getThis();
        }

        protected LootItemCondition[] getConditions() {
            return this.conditions.toArray(new LootItemCondition[0]);
        }

        public AlternativesEntry.Builder otherwise(Builder<?> builder) {
            return new AlternativesEntry.Builder(this, builder);
        }

        public abstract LootPoolEntryContainer build();

        @Override
        public /* synthetic */ Object unwrap() {
            return this.unwrap();
        }

        @Override
        public /* synthetic */ Object when(LootItemCondition.Builder builder) {
            return this.when(builder);
        }
    }

}

