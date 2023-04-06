/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Multimap
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.PredicateManager;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootTables
extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = Deserializers.createLootTableSerializer().create();
    private Map<ResourceLocation, LootTable> tables = ImmutableMap.of();
    private final PredicateManager predicateManager;

    public LootTables(PredicateManager predicateManager) {
        super(GSON, "loot_tables");
        this.predicateManager = predicateManager;
    }

    public LootTable get(ResourceLocation resourceLocation) {
        return this.tables.getOrDefault(resourceLocation, LootTable.EMPTY);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        JsonElement jsonElement2 = map.remove(BuiltInLootTables.EMPTY);
        if (jsonElement2 != null) {
            LOGGER.warn("Datapack tried to redefine {} loot table, ignoring", (Object)BuiltInLootTables.EMPTY);
        }
        map.forEach((resourceLocation, jsonElement) -> {
            try {
                LootTable lootTable = (LootTable)GSON.fromJson(jsonElement, LootTable.class);
                builder.put(resourceLocation, (Object)lootTable);
            }
            catch (Exception exception) {
                LOGGER.error("Couldn't parse loot table {}", resourceLocation, (Object)exception);
            }
        });
        builder.put((Object)BuiltInLootTables.EMPTY, (Object)LootTable.EMPTY);
        ImmutableMap immutableMap = builder.build();
        ValidationContext validationContext = new ValidationContext(LootContextParamSets.ALL_PARAMS, this.predicateManager::get, ((ImmutableMap)immutableMap)::get);
        immutableMap.forEach((resourceLocation, lootTable) -> LootTables.validate(validationContext, resourceLocation, lootTable));
        validationContext.getProblems().forEach((string, string2) -> LOGGER.warn("Found validation problem in " + string + ": " + string2));
        this.tables = immutableMap;
    }

    public static void validate(ValidationContext validationContext, ResourceLocation resourceLocation, LootTable lootTable) {
        lootTable.validate(validationContext.setParams(lootTable.getParamSet()).enterTable("{" + resourceLocation + "}", resourceLocation));
    }

    public static JsonElement serialize(LootTable lootTable) {
        return GSON.toJsonTree((Object)lootTable);
    }

    public Set<ResourceLocation> getIds() {
        return this.tables.keySet();
    }
}

