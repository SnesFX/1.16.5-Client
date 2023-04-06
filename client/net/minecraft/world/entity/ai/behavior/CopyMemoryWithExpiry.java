/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.IntRange;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class CopyMemoryWithExpiry<E extends Mob, T>
extends Behavior<E> {
    private final Predicate<E> predicate;
    private final MemoryModuleType<? extends T> sourceMemory;
    private final MemoryModuleType<T> targetMemory;
    private final IntRange durationOfCopy;

    public CopyMemoryWithExpiry(Predicate<E> predicate, MemoryModuleType<? extends T> memoryModuleType, MemoryModuleType<T> memoryModuleType2, IntRange intRange) {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(memoryModuleType, (Object)((Object)MemoryStatus.VALUE_PRESENT), memoryModuleType2, (Object)((Object)MemoryStatus.VALUE_ABSENT)));
        this.predicate = predicate;
        this.sourceMemory = memoryModuleType;
        this.targetMemory = memoryModuleType2;
        this.durationOfCopy = intRange;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, E e) {
        return this.predicate.test(e);
    }

    @Override
    protected void start(ServerLevel serverLevel, E e, long l) {
        Brain<?> brain = ((LivingEntity)e).getBrain();
        brain.setMemoryWithExpiry(this.targetMemory, brain.getMemory(this.sourceMemory).get(), this.durationOfCopy.randomValue(serverLevel.random));
    }
}

