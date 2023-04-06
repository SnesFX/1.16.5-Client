/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.ImmutableSet
 */
package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Illusioner;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;

public class VillagerHostilesSensor
extends Sensor<LivingEntity> {
    private static final ImmutableMap<EntityType<?>, Float> ACCEPTABLE_DISTANCE_FROM_HOSTILES = ImmutableMap.builder().put(EntityType.DROWNED, (Object)Float.valueOf(8.0f)).put(EntityType.EVOKER, (Object)Float.valueOf(12.0f)).put(EntityType.HUSK, (Object)Float.valueOf(8.0f)).put(EntityType.ILLUSIONER, (Object)Float.valueOf(12.0f)).put(EntityType.PILLAGER, (Object)Float.valueOf(15.0f)).put(EntityType.RAVAGER, (Object)Float.valueOf(12.0f)).put(EntityType.VEX, (Object)Float.valueOf(8.0f)).put(EntityType.VINDICATOR, (Object)Float.valueOf(10.0f)).put(EntityType.ZOGLIN, (Object)Float.valueOf(10.0f)).put(EntityType.ZOMBIE, (Object)Float.valueOf(8.0f)).put(EntityType.ZOMBIE_VILLAGER, (Object)Float.valueOf(8.0f)).build();

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_HOSTILE);
    }

    @Override
    protected void doTick(ServerLevel serverLevel, LivingEntity livingEntity) {
        livingEntity.getBrain().setMemory(MemoryModuleType.NEAREST_HOSTILE, this.getNearestHostile(livingEntity));
    }

    private Optional<LivingEntity> getNearestHostile(LivingEntity livingEntity) {
        return this.getVisibleEntities(livingEntity).flatMap(list -> list.stream().filter(this::isHostile).filter(livingEntity2 -> this.isClose(livingEntity, (LivingEntity)livingEntity2)).min((livingEntity2, livingEntity3) -> this.compareMobDistance(livingEntity, (LivingEntity)livingEntity2, (LivingEntity)livingEntity3)));
    }

    private Optional<List<LivingEntity>> getVisibleEntities(LivingEntity livingEntity) {
        return livingEntity.getBrain().getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES);
    }

    private int compareMobDistance(LivingEntity livingEntity, LivingEntity livingEntity2, LivingEntity livingEntity3) {
        return Mth.floor(livingEntity2.distanceToSqr(livingEntity) - livingEntity3.distanceToSqr(livingEntity));
    }

    private boolean isClose(LivingEntity livingEntity, LivingEntity livingEntity2) {
        float f = ((Float)ACCEPTABLE_DISTANCE_FROM_HOSTILES.get(livingEntity2.getType())).floatValue();
        return livingEntity2.distanceToSqr(livingEntity) <= (double)(f * f);
    }

    private boolean isHostile(LivingEntity livingEntity) {
        return ACCEPTABLE_DISTANCE_FROM_HOSTILES.containsKey(livingEntity.getType());
    }
}

