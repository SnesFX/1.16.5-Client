/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;

public class AdultSensor
extends Sensor<AgableMob> {
    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleType.VISIBLE_LIVING_ENTITIES);
    }

    @Override
    protected void doTick(ServerLevel serverLevel, AgableMob agableMob) {
        agableMob.getBrain().getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).ifPresent(list -> this.setNearestVisibleAdult(agableMob, (List<LivingEntity>)list));
    }

    private void setNearestVisibleAdult(AgableMob agableMob2, List<LivingEntity> list) {
        Optional<AgableMob> optional = list.stream().filter(livingEntity -> livingEntity.getType() == agableMob2.getType()).map(livingEntity -> (AgableMob)livingEntity).filter(agableMob -> !agableMob.isBaby()).findFirst();
        agableMob2.getBrain().setMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT, optional);
    }
}

