/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.level.storage.loot;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootTable {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final LootTable EMPTY = new LootTable(LootContextParamSets.EMPTY, new LootPool[0], new LootItemFunction[0]);
    public static final LootContextParamSet DEFAULT_PARAM_SET = LootContextParamSets.ALL_PARAMS;
    private final LootContextParamSet paramSet;
    private final LootPool[] pools;
    private final LootItemFunction[] functions;
    private final BiFunction<ItemStack, LootContext, ItemStack> compositeFunction;

    private LootTable(LootContextParamSet lootContextParamSet, LootPool[] arrlootPool, LootItemFunction[] arrlootItemFunction) {
        this.paramSet = lootContextParamSet;
        this.pools = arrlootPool;
        this.functions = arrlootItemFunction;
        this.compositeFunction = LootItemFunctions.compose(arrlootItemFunction);
    }

    public static Consumer<ItemStack> createStackSplitter(Consumer<ItemStack> consumer) {
        return itemStack -> {
            if (itemStack.getCount() < itemStack.getMaxStackSize()) {
                consumer.accept((ItemStack)itemStack);
            } else {
                ItemStack itemStack2;
                for (int i = itemStack.getCount(); i > 0; i -= itemStack2.getCount()) {
                    itemStack2 = itemStack.copy();
                    itemStack2.setCount(Math.min(itemStack.getMaxStackSize(), i));
                    consumer.accept(itemStack2);
                }
            }
        };
    }

    public void getRandomItemsRaw(LootContext lootContext, Consumer<ItemStack> consumer) {
        if (lootContext.addVisitedTable(this)) {
            Consumer<ItemStack> consumer2 = LootItemFunction.decorate(this.compositeFunction, consumer, lootContext);
            for (LootPool lootPool : this.pools) {
                lootPool.addRandomItems(consumer2, lootContext);
            }
            lootContext.removeVisitedTable(this);
        } else {
            LOGGER.warn("Detected infinite loop in loot tables");
        }
    }

    public void getRandomItems(LootContext lootContext, Consumer<ItemStack> consumer) {
        this.getRandomItemsRaw(lootContext, LootTable.createStackSplitter(consumer));
    }

    public List<ItemStack> getRandomItems(LootContext lootContext) {
        ArrayList arrayList = Lists.newArrayList();
        this.getRandomItems(lootContext, arrayList::add);
        return arrayList;
    }

    public LootContextParamSet getParamSet() {
        return this.paramSet;
    }

    public void validate(ValidationContext validationContext) {
        int n;
        for (n = 0; n < this.pools.length; ++n) {
            this.pools[n].validate(validationContext.forChild(".pools[" + n + "]"));
        }
        for (n = 0; n < this.functions.length; ++n) {
            this.functions[n].validate(validationContext.forChild(".functions[" + n + "]"));
        }
    }

    public void fill(Container container, LootContext lootContext) {
        List<ItemStack> list = this.getRandomItems(lootContext);
        Random random = lootContext.getRandom();
        List<Integer> list2 = this.getAvailableSlots(container, random);
        this.shuffleAndSplitItems(list, list2.size(), random);
        for (ItemStack itemStack : list) {
            if (list2.isEmpty()) {
                LOGGER.warn("Tried to over-fill a container");
                return;
            }
            if (itemStack.isEmpty()) {
                container.setItem(list2.remove(list2.size() - 1), ItemStack.EMPTY);
                continue;
            }
            container.setItem(list2.remove(list2.size() - 1), itemStack);
        }
    }

    private void shuffleAndSplitItems(List<ItemStack> list, int n, Random random) {
        ArrayList arrayList = Lists.newArrayList();
        Object object = list.iterator();
        while (object.hasNext()) {
            ItemStack itemStack = object.next();
            if (itemStack.isEmpty()) {
                object.remove();
                continue;
            }
            if (itemStack.getCount() <= 1) continue;
            arrayList.add(itemStack);
            object.remove();
        }
        while (n - list.size() - arrayList.size() > 0 && !arrayList.isEmpty()) {
            object = (ItemStack)arrayList.remove(Mth.nextInt(random, 0, arrayList.size() - 1));
            int n2 = Mth.nextInt(random, 1, ((ItemStack)object).getCount() / 2);
            ItemStack itemStack = ((ItemStack)object).split(n2);
            if (((ItemStack)object).getCount() > 1 && random.nextBoolean()) {
                arrayList.add(object);
            } else {
                list.add((ItemStack)object);
            }
            if (itemStack.getCount() > 1 && random.nextBoolean()) {
                arrayList.add(itemStack);
                continue;
            }
            list.add(itemStack);
        }
        list.addAll(arrayList);
        Collections.shuffle(list, random);
    }

    private List<Integer> getAvailableSlots(Container container, Random random) {
        ArrayList arrayList = Lists.newArrayList();
        for (int i = 0; i < container.getContainerSize(); ++i) {
            if (!container.getItem(i).isEmpty()) continue;
            arrayList.add(i);
        }
        Collections.shuffle(arrayList, random);
        return arrayList;
    }

    public static Builder lootTable() {
        return new Builder();
    }

    public static class Serializer
    implements JsonDeserializer<LootTable>,
    JsonSerializer<LootTable> {
        public LootTable deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            LootItemFunction[] arrlootItemFunction;
            JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, "loot table");
            LootPool[] arrlootPool = GsonHelper.getAsObject(jsonObject, "pools", new LootPool[0], jsonDeserializationContext, LootPool[].class);
            LootContextParamSet lootContextParamSet = null;
            if (jsonObject.has("type")) {
                arrlootItemFunction = GsonHelper.getAsString(jsonObject, "type");
                lootContextParamSet = LootContextParamSets.get(new ResourceLocation((String)arrlootItemFunction));
            }
            arrlootItemFunction = GsonHelper.getAsObject(jsonObject, "functions", new LootItemFunction[0], jsonDeserializationContext, LootItemFunction[].class);
            return new LootTable(lootContextParamSet != null ? lootContextParamSet : LootContextParamSets.ALL_PARAMS, arrlootPool, arrlootItemFunction);
        }

        public JsonElement serialize(LootTable lootTable, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            if (lootTable.paramSet != DEFAULT_PARAM_SET) {
                ResourceLocation resourceLocation = LootContextParamSets.getKey(lootTable.paramSet);
                if (resourceLocation != null) {
                    jsonObject.addProperty("type", resourceLocation.toString());
                } else {
                    LOGGER.warn("Failed to find id for param set " + lootTable.paramSet);
                }
            }
            if (lootTable.pools.length > 0) {
                jsonObject.add("pools", jsonSerializationContext.serialize((Object)lootTable.pools));
            }
            if (!ArrayUtils.isEmpty((Object[])lootTable.functions)) {
                jsonObject.add("functions", jsonSerializationContext.serialize((Object)lootTable.functions));
            }
            return jsonObject;
        }

        public /* synthetic */ JsonElement serialize(Object object, Type type, JsonSerializationContext jsonSerializationContext) {
            return this.serialize((LootTable)object, type, jsonSerializationContext);
        }

        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }

    public static class Builder
    implements FunctionUserBuilder<Builder> {
        private final List<LootPool> pools = Lists.newArrayList();
        private final List<LootItemFunction> functions = Lists.newArrayList();
        private LootContextParamSet paramSet = DEFAULT_PARAM_SET;

        public Builder withPool(LootPool.Builder builder) {
            this.pools.add(builder.build());
            return this;
        }

        public Builder setParamSet(LootContextParamSet lootContextParamSet) {
            this.paramSet = lootContextParamSet;
            return this;
        }

        @Override
        public Builder apply(LootItemFunction.Builder builder) {
            this.functions.add(builder.build());
            return this;
        }

        @Override
        public Builder unwrap() {
            return this;
        }

        public LootTable build() {
            return new LootTable(this.paramSet, this.pools.toArray(new LootPool[0]), this.functions.toArray(new LootItemFunction[0]));
        }

        @Override
        public /* synthetic */ Object unwrap() {
            return this.unwrap();
        }

        @Override
        public /* synthetic */ Object apply(LootItemFunction.Builder builder) {
            return this.apply(builder);
        }
    }

}

