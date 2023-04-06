/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class EraseMemoryIf<E extends LivingEntity>
extends Behavior<E> {
    private final Predicate<E> predicate;
    private final MemoryModuleType<?> memoryType;

    public EraseMemoryIf(Predicate<E> predicate, MemoryModuleType<?> memoryModuleType) {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(memoryModuleType, (Object)((Object)MemoryStatus.VALUE_PRESENT)));
        this.predicate = predicate;
        this.memoryType = memoryModuleType;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, E e) {
        return this.predicate.test(e);
    }

    @Override
    protected void start(ServerLevel serverLevel, E e, long l) {
        ((LivingEntity)e).getBrain().eraseMemory(this.memoryType);
    }
}

