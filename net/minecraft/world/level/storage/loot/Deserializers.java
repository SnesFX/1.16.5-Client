/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.GsonBuilder
 */
package net.minecraft.world.level.storage.loot;

import com.google.gson.GsonBuilder;
import java.lang.reflect.Type;
import net.minecraft.world.level.storage.loot.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.ConstantIntValue;
import net.minecraft.world.level.storage.loot.IntLimiter;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.RandomValueBounds;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;

public class Deserializers {
    public static GsonBuilder createConditionSerializer() {
        return new GsonBuilder().registerTypeAdapter(RandomValueBounds.class, (Object)new RandomValueBounds.Serializer()).registerTypeAdapter(BinomialDistributionGenerator.class, (Object)new BinomialDistributionGenerator.Serializer()).registerTypeAdapter(ConstantIntValue.class, (Object)new ConstantIntValue.Serializer()).registerTypeHierarchyAdapter(LootItemCondition.class, LootItemConditions.createGsonAdapter()).registerTypeHierarchyAdapter(LootContext.EntityTarget.class, (Object)new LootContext.EntityTarget.Serializer());
    }

    public static GsonBuilder createFunctionSerializer() {
        return Deserializers.createConditionSerializer().registerTypeAdapter(IntLimiter.class, (Object)new IntLimiter.Serializer()).registerTypeHierarchyAdapter(LootPoolEntryContainer.class, LootPoolEntries.createGsonAdapter()).registerTypeHierarchyAdapter(LootItemFunction.class, LootItemFunctions.createGsonAdapter());
    }

    public static GsonBuilder createLootTableSerializer() {
        return Deserializers.createFunctionSerializer().registerTypeAdapter(LootPool.class, (Object)new LootPool.Serializer()).registerTypeAdapter(LootTable.class, (Object)new LootTable.Serializer());
    }
}

