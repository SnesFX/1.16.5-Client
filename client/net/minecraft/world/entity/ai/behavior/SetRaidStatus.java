/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.schedule.Activity;

public class SetRaidStatus
extends Behavior<LivingEntity> {
    public SetRaidStatus() {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of());
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, LivingEntity livingEntity) {
        return serverLevel.random.nextInt(20) == 0;
    }

    @Override
    protected void start(ServerLevel serverLevel, LivingEntity livingEntity, long l) {
        Brain<?> brain = livingEntity.getBrain();
        Raid raid = serverLevel.getRaidAt(livingEntity.blockPosition());
        if (raid != null) {
            if (!raid.hasFirstWaveSpawned() || raid.isBetweenWaves()) {
                brain.setDefaultActivity(Activity.PRE_RAID);
                brain.setActiveActivityIfPossible(Activity.PRE_RAID);
            } else {
                brain.setDefaultActivity(Activity.RAID);
                brain.setActiveActivityIfPossible(Activity.RAID);
            }
        }
    }
}

