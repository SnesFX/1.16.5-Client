/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 */
package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;

public class PiglinBruteSpecificSensor
extends Sensor<LivingEntity> {
    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_NEMESIS, MemoryModuleType.NEARBY_ADULT_PIGLINS);
    }

    @Override
    protected void doTick(ServerLevel serverLevel, LivingEntity livingEntity) {
        Brain<?> brain = livingEntity.getBrain();
        Optional<Object> optional = Optional.empty();
        ArrayList arrayList = Lists.newArrayList();
        List<LivingEntity> list = brain.getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).orElse((List<LivingEntity>)ImmutableList.of());
        for (LivingEntity object2 : list) {
            if (!(object2 instanceof WitherSkeleton) && !(object2 instanceof WitherBoss)) continue;
            optional = Optional.of((Mob)object2);
            break;
        }
        List<LivingEntity> list2 = brain.getMemory(MemoryModuleType.LIVING_ENTITIES).orElse((List<LivingEntity>)ImmutableList.of());
        Iterator iterator = list2.iterator();
        while (iterator.hasNext()) {
            LivingEntity livingEntity2 = (LivingEntity)iterator.next();
            if (!(livingEntity2 instanceof AbstractPiglin) || !((AbstractPiglin)livingEntity2).isAdult()) continue;
            arrayList.add((AbstractPiglin)livingEntity2);
        }
        brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS, optional);
        brain.setMemory(MemoryModuleType.NEARBY_ADULT_PIGLINS, arrayList);
    }
}

