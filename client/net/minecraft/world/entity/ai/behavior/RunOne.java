/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.GateBehavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class RunOne<E extends LivingEntity>
extends GateBehavior<E> {
    public RunOne(List<Pair<Behavior<? super E>, Integer>> list) {
        this((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(), list);
    }

    public RunOne(Map<MemoryModuleType<?>, MemoryStatus> map, List<Pair<Behavior<? super E>, Integer>> list) {
        super(map, (Set<MemoryModuleType<?>>)ImmutableSet.of(), GateBehavior.OrderPolicy.SHUFFLED, GateBehavior.RunningPolicy.RUN_ONE, list);
    }
}

