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
import java.util.function.BiPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.Level;

public class DismountOrSkipMounting<E extends LivingEntity, T extends Entity>
extends Behavior<E> {
    private final int maxWalkDistToRideTarget;
    private final BiPredicate<E, Entity> dontRideIf;

    public DismountOrSkipMounting(int n, BiPredicate<E, Entity> biPredicate) {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.RIDE_TARGET, (Object)((Object)MemoryStatus.REGISTERED)));
        this.maxWalkDistToRideTarget = n;
        this.dontRideIf = biPredicate;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, E e) {
        Entity entity = ((Entity)e).getVehicle();
        Entity entity2 = ((LivingEntity)e).getBrain().getMemory(MemoryModuleType.RIDE_TARGET).orElse(null);
        if (entity == null && entity2 == null) {
            return false;
        }
        Entity entity3 = entity == null ? entity2 : entity;
        return !this.isVehicleValid(e, entity3) || this.dontRideIf.test(e, entity3);
    }

    private boolean isVehicleValid(E e, Entity entity) {
        return entity.isAlive() && entity.closerThan((Entity)e, this.maxWalkDistToRideTarget) && entity.level == ((LivingEntity)e).level;
    }

    @Override
    protected void start(ServerLevel serverLevel, E e, long l) {
        ((LivingEntity)e).stopRiding();
        ((LivingEntity)e).getBrain().eraseMemory(MemoryModuleType.RIDE_TARGET);
    }
}

