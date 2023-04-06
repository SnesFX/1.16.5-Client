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

public class UpdateActivityFromSchedule
extends Behavior<LivingEntity> {
    public UpdateActivityFromSchedule() {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of());
    }

    @Override
    protected void start(ServerLevel serverLevel, LivingEntity livingEntity, long l) {
        livingEntity.getBrain().updateActivityFromSchedule(serverLevel.getDayTime(), serverLevel.getGameTime());
    }
}

