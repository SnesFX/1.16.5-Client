/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class BecomePassiveIfMemoryPresent
extends Behavior<LivingEntity> {
    private final int pacifyDuration;

    public BecomePassiveIfMemoryPresent(MemoryModuleType<?> memoryModuleType, int n) {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, (Object)((Object)MemoryStatus.REGISTERED), MemoryModuleType.PACIFIED, (Object)((Object)MemoryStatus.VALUE_ABSENT), memoryModuleType, (Object)((Object)MemoryStatus.VALUE_PRESENT)));
        this.pacifyDuration = n;
    }

    @Override
    protected void start(ServerLevel serverLevel, LivingEntity livingEntity, long l) {
        livingEntity.getBrain().setMemoryWithExpiry(MemoryModuleType.PACIFIED, true, this.pacifyDuration);
        livingEntity.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
    }
}

