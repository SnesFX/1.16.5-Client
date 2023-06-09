/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.ImmutableMultimap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.google.common.collect.Multimap
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.storage.loot;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContextUser;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ValidationContext {
    private final Multimap<String, String> problems;
    private final Supplier<String> context;
    private final LootContextParamSet params;
    private final Function<ResourceLocation, LootItemCondition> conditionResolver;
    private final Set<ResourceLocation> visitedConditions;
    private final Function<ResourceLocation, LootTable> tableResolver;
    private final Set<ResourceLocation> visitedTables;
    private String contextCache;

    public ValidationContext(LootContextParamSet lootContextParamSet, Function<ResourceLocation, LootItemCondition> function, Function<ResourceLocation, LootTable> function2) {
        this((Multimap<String, String>)HashMultimap.create(), () -> "", lootContextParamSet, function, (Set<ResourceLocation>)ImmutableSet.of(), function2, (Set<ResourceLocation>)ImmutableSet.of());
    }

    public ValidationContext(Multimap<String, String> multimap, Supplier<String> supplier, LootContextParamSet lootContextParamSet, Function<ResourceLocation, LootItemCondition> function, Set<ResourceLocation> set, Function<ResourceLocation, LootTable> function2, Set<ResourceLocation> set2) {
        this.problems = multimap;
        this.context = supplier;
        this.params = lootContextParamSet;
        this.conditionResolver = function;
        this.visitedConditions = set;
        this.tableResolver = function2;
        this.visitedTables = set2;
    }

    private String getContext() {
        if (this.contextCache == null) {
            this.contextCache = this.context.get();
        }
        return this.contextCache;
    }

    public void reportProblem(String string) {
        this.problems.put((Object)this.getContext(), (Object)string);
    }

    public ValidationContext forChild(String string) {
        return new ValidationContext(this.problems, () -> this.getContext() + string, this.params, this.conditionResolver, this.visitedConditions, this.tableResolver, this.visitedTables);
    }

    public ValidationContext enterTable(String string, ResourceLocation resourceLocation) {
        ImmutableSet immutableSet = ImmutableSet.builder().addAll(this.visitedTables).add((Object)resourceLocation).build();
        return new ValidationContext(this.problems, () -> this.getContext() + string, this.params, this.conditionResolver, this.visitedConditions, this.tableResolver, (Set<ResourceLocation>)immutableSet);
    }

    public ValidationContext enterCondition(String string, ResourceLocation resourceLocation) {
        ImmutableSet immutableSet = ImmutableSet.builder().addAll(this.visitedConditions).add((Object)resourceLocation).build();
        return new ValidationContext(this.problems, () -> this.getContext() + string, this.params, this.conditionResolver, (Set<ResourceLocation>)immutableSet, this.tableResolver, this.visitedTables);
    }

    public boolean hasVisitedTable(ResourceLocation resourceLocation) {
        return this.visitedTables.contains(resourceLocation);
    }

    public boolean hasVisitedCondition(ResourceLocation resourceLocation) {
        return this.visitedConditions.contains(resourceLocation);
    }

    public Multimap<String, String> getProblems() {
        return ImmutableMultimap.copyOf(this.problems);
    }

    public void validateUser(LootContextUser lootContextUser) {
        this.params.validateUser(this, lootContextUser);
    }

    @Nullable
    public LootTable resolveLootTable(ResourceLocation resourceLocation) {
        return this.tableResolver.apply(resourceLocation);
    }

    @Nullable
    public LootItemCondition resolveCondition(ResourceLocation resourceLocation) {
        return this.conditionResolver.apply(resourceLocation);
    }

    public ValidationContext setParams(LootContextParamSet lootContextParamSet) {
        return new ValidationContext(this.problems, this.context, lootContextParamSet, this.conditionResolver, this.visitedConditions, this.tableResolver, this.visitedTables);
    }
}

