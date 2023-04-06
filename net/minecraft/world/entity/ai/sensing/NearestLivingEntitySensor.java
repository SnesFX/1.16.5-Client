/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.phys.AABB;

public class NearestLivingEntitySensor
extends Sensor<LivingEntity> {
    @Override
    protected void doTick(ServerLevel serverLevel, LivingEntity livingEntity) {
        AABB aABB = livingEntity.getBoundingBox().inflate(16.0, 16.0, 16.0);
        List<LivingEntity> list = serverLevel.getEntitiesOfClass(LivingEntity.class, aABB, livingEntity2 -> livingEntity2 != livingEntity && livingEntity2.isAlive());
        list.sort(Comparator.comparingDouble(livingEntity::distanceToSqr));
        Brain<?> brain = livingEntity.getBrain();
        brain.setMemory(MemoryModuleType.LIVING_ENTITIES, list);
        brain.setMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES, list.stream().filter(livingEntity2 -> NearestLivingEntitySensor.isEntityTargetable(livingEntity, livingEntity2)).collect(Collectors.toList()));
    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.LIVING_ENTITIES, MemoryModuleType.VISIBLE_LIVING_ENTITIES);
    }
}

