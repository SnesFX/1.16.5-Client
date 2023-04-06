/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 */
package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class RunIf<E extends LivingEntity>
extends Behavior<E> {
    private final Predicate<E> predicate;
    private final Behavior<? super E> wrappedBehavior;
    private final boolean checkWhileRunningAlso;

    public RunIf(Map<MemoryModuleType<?>, MemoryStatus> map, Predicate<E> predicate, Behavior<? super E> behavior, boolean bl) {
        super(RunIf.mergeMaps(map, behavior.entryCondition));
        this.predicate = predicate;
        this.wrappedBehavior = behavior;
        this.checkWhileRunningAlso = bl;
    }

    private static Map<MemoryModuleType<?>, MemoryStatus> mergeMaps(Map<MemoryModuleType<?>, MemoryStatus> map, Map<MemoryModuleType<?>, MemoryStatus> map2) {
        HashMap hashMap = Maps.newHashMap();
        hashMap.putAll(map);
        hashMap.putAll(map2);
        return hashMap;
    }

    public RunIf(Predicate<E> predicate, Behavior<? super E> behavior) {
        this((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(), (Predicate<? super E>)predicate, behavior, false);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, E e) {
        return this.predicate.test(e) && this.wrappedBehavior.checkExtraStartConditions(serverLevel, e);
    }

    @Override
    protected boolean canStillUse(ServerLevel serverLevel, E e, long l) {
        return this.checkWhileRunningAlso && this.predicate.test(e) && this.wrappedBehavior.canStillUse(serverLevel, e, l);
    }

    @Override
    protected boolean timedOut(long l) {
        return false;
    }

    @Override
    protected void start(ServerLevel serverLevel, E e, long l) {
        this.wrappedBehavior.start(serverLevel, e, l);
    }

    @Override
    protected void tick(ServerLevel serverLevel, E e, long l) {
        this.wrappedBehavior.tick(serverLevel, e, l);
    }

    @Override
    protected void stop(ServerLevel serverLevel, E e, long l) {
        this.wrappedBehavior.stop(serverLevel, e, l);
    }

    @Override
    public String toString() {
        return "RunIf: " + this.wrappedBehavior;
    }
}

