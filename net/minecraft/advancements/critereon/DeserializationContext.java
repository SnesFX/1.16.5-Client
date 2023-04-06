/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Multimap
 *  com.google.gson.Gson
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.advancements.critereon;

import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.PredicateManager;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DeserializationContext {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ResourceLocation id;
    private final PredicateManager predicateManager;
    private final Gson predicateGson = Deserializers.createConditionSerializer().create();

    public DeserializationContext(ResourceLocation resourceLocation, PredicateManager predicateManager) {
        this.id = resourceLocation;
        this.predicateManager = predicateManager;
    }

    public final LootItemCondition[] deserializeConditions(JsonArray jsonArray, String string, LootContextParamSet lootContextParamSet) {
        LootItemCondition[] arrlootItemCondition = (LootItemCondition[])this.predicateGson.fromJson((JsonElement)jsonArray, LootItemCondition[].class);
        ValidationContext validationContext = new ValidationContext(lootContextParamSet, this.predicateManager::get, resourceLocation -> null);
        for (LootItemCondition lootItemCondition : arrlootItemCondition) {
            lootItemCondition.validate(validationContext);
            validationContext.getProblems().forEach((string2, string3) -> LOGGER.warn("Found validation problem in advancement trigger {}/{}: {}", (Object)string, string2, string3));
        }
        return arrlootItemCondition;
    }

    public ResourceLocation getAdvancementId() {
        return this.id;
    }
}

