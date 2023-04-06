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
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.apache.commons.lang3.ArrayUtils;

public abstract class LootPoolSingletonContainer
extends LootPoolEntryContainer {
    protected final int weight;
    protected final int quality;
    protected final LootItemFunction[] functions;
    private final BiFunction<ItemStack, LootContext, ItemStack> compositeFunction;
    private final LootPoolEntry entry = new EntryBase(){

        @Override
        public void createItemStack(Consumer<ItemStack> consumer, LootContext lootContext) {
            LootPoolSingletonContainer.this.createItemStack(LootItemFunction.decorate(LootPoolSingletonContainer.this.compositeFunction, consumer, lootContext), lootContext);
        }
    };

    protected LootPoolSingletonContainer(int n, int n2, LootItemCondition[] arrlootItemCondition, LootItemFunction[] arrlootItemFunction) {
        super(arrlootItemCondition);
        this.weight = n;
        this.quality = n2;
        this.functions = arrlootItemFunction;
        this.compositeFunction = LootItemFunctions.compose(arrlootItemFunction);
    }

    @Override
    public void validate(ValidationContext validationContext) {
        super.validate(validationContext);
        for (int i = 0; i < this.functions.length; ++i) {
            this.functions[i].validate(validationContext.forChild(".functions[" + i + "]"));
        }
    }

    protected abstract void createItemStack(Consumer<ItemStack> var1, LootContext var2);

    @Override
    public boolean expand(LootContext lootContext, Consumer<LootPoolEntry> consumer) {
        if (this.canRun(lootContext)) {
            consumer.accept(this.entry);
            return true;
        }
        return false;
    }

    public static Builder<?> simpleBuilder(EntryConstructor entryConstructor) {
        return new DummyBuilder(entryConstructor);
    }

    public static abstract class Serializer<T extends LootPoolSingletonContainer>
    extends LootPoolEntryContainer.Serializer<T> {
        @Override
        public void serializeCustom(JsonObject jsonObject, T t, JsonSerializationContext jsonSerializationContext) {
            if (((LootPoolSingletonContainer)t).weight != 1) {
                jsonObject.addProperty("weight", (Number)((LootPoolSingletonContainer)t).weight);
            }
            if (((LootPoolSingletonContainer)t).quality != 0) {
                jsonObject.addProperty("quality", (Number)((LootPoolSingletonContainer)t).quality);
            }
            if (!ArrayUtils.isEmpty((Object[])((LootPoolSingletonContainer)t).functions)) {
                jsonObject.add("functions", jsonSerializationContext.serialize((Object)((LootPoolSingletonContainer)t).functions));
            }
        }

        @Override
        public final T deserializeCustom(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] arrlootItemCondition) {
            int n = GsonHelper.getAsInt(jsonObject, "weight", 1);
            int n2 = GsonHelper.getAsInt(jsonObject, "quality", 0);
            LootItemFunction[] arrlootItemFunction = GsonHelper.getAsObject(jsonObject, "functions", new LootItemFunction[0], jsonDeserializationContext, LootItemFunction[].class);
            return this.deserialize(jsonObject, jsonDeserializationContext, n, n2, arrlootItemCondition, arrlootItemFunction);
        }

        protected abstract T deserialize(JsonObject var1, JsonDeserializationContext var2, int var3, int var4, LootItemCondition[] var5, LootItemFunction[] var6);

        @Override
        public /* synthetic */ LootPoolEntryContainer deserializeCustom(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] arrlootItemCondition) {
            return this.deserializeCustom(jsonObject, jsonDeserializationContext, arrlootItemCondition);
        }
    }

    static class DummyBuilder
    extends Builder<DummyBuilder> {
        private final EntryConstructor constructor;

        public DummyBuilder(EntryConstructor entryConstructor) {
            this.constructor = entryConstructor;
        }

        @Override
        protected DummyBuilder getThis() {
            return this;
        }

        @Override
        public LootPoolEntryContainer build() {
            return this.constructor.build(this.weight, this.quality, this.getConditions(), this.getFunctions());
        }

        @Override
        protected /* synthetic */ LootPoolEntryContainer.Builder getThis() {
            return this.getThis();
        }
    }

    @FunctionalInterface
    public static interface EntryConstructor {
        public LootPoolSingletonContainer build(int var1, int var2, LootItemCondition[] var3, LootItemFunction[] var4);
    }

    public static abstract class Builder<T extends Builder<T>>
    extends LootPoolEntryContainer.Builder<T>
    implements FunctionUserBuilder<T> {
        protected int weight = 1;
        protected int quality = 0;
        private final List<LootItemFunction> functions = Lists.newArrayList();

        @Override
        public T apply(LootItemFunction.Builder builder) {
            this.functions.add(builder.build());
            return (T)((Builder)this.getThis());
        }

        protected LootItemFunction[] getFunctions() {
            return this.functions.toArray(new LootItemFunction[0]);
        }

        public T setWeight(int n) {
            this.weight = n;
            return (T)((Builder)this.getThis());
        }

        public T setQuality(int n) {
            this.quality = n;
            return (T)((Builder)this.getThis());
        }

        @Override
        public /* synthetic */ Object apply(LootItemFunction.Builder builder) {
            return this.apply(builder);
        }
    }

    public abstract class EntryBase
    implements LootPoolEntry {
        protected EntryBase() {
        }

        @Override
        public int getWeight(float f) {
            return Math.max(Mth.floor((float)LootPoolSingletonContainer.this.weight + (float)LootPoolSingletonContainer.this.quality * f), 0);
        }
    }

}

