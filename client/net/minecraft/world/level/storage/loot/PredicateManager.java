/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Multimap
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PredicateManager
extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = Deserializers.createConditionSerializer().create();
    private Map<ResourceLocation, LootItemCondition> conditions = ImmutableMap.of();

    public PredicateManager() {
        super(GSON, "predicates");
    }

    @Nullable
    public LootItemCondition get(ResourceLocation resourceLocation) {
        return this.conditions.get(resourceLocation);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        map.forEach((resourceLocation, jsonElement) -> {
            try {
                if (jsonElement.isJsonArray()) {
                    LootItemCondition[] arrlootItemCondition = (LootItemCondition[])GSON.fromJson(jsonElement, LootItemCondition[].class);
                    builder.put(resourceLocation, (Object)new CompositePredicate(arrlootItemCondition));
                } else {
                    LootItemCondition lootItemCondition = (LootItemCondition)GSON.fromJson(jsonElement, LootItemCondition.class);
                    builder.put(resourceLocation, (Object)lootItemCondition);
                }
            }
            catch (Exception exception) {
                LOGGER.error("Couldn't parse loot table {}", resourceLocation, (Object)exception);
            }
        });
        ImmutableMap immutableMap = builder.build();
        ValidationContext validationContext = new ValidationContext(LootContextParamSets.ALL_PARAMS, ((Map)immutableMap)::get, resourceLocation -> null);
        immutableMap.forEach((resourceLocation, lootItemCondition) -> lootItemCondition.validate(validationContext.enterCondition("{" + resourceLocation + "}", (ResourceLocation)resourceLocation)));
        validationContext.getProblems().forEach((string, string2) -> LOGGER.warn("Found validation problem in " + string + ": " + string2));
        this.conditions = immutableMap;
    }

    public Set<ResourceLocation> getKeys() {
        return Collections.unmodifiableSet(this.conditions.keySet());
    }

    static class CompositePredicate
    implements LootItemCondition {
        private final LootItemCondition[] terms;
        private final Predicate<LootContext> composedPredicate;

        private CompositePredicate(LootItemCondition[] arrlootItemCondition) {
            this.terms = arrlootItemCondition;
            this.composedPredicate = LootItemConditions.andConditions(arrlootItemCondition);
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

        @Override
        public LootItemConditionType getType() {
            throw new UnsupportedOperationException();
        }

        @Override
        public /* synthetic */ boolean test(Object object) {
            return this.test((LootContext)object);
        }
    }

}

