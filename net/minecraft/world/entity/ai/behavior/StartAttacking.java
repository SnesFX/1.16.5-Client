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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class StartAttacking<E extends Mob>
extends Behavior<E> {
    private final Predicate<E> canAttackPredicate;
    private final Function<E, Optional<? extends LivingEntity>> targetFinderFunction;

    public StartAttacking(Predicate<E> predicate, Function<E, Optional<? extends LivingEntity>> function) {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, (Object)((Object)MemoryStatus.VALUE_ABSENT), MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, (Object)((Object)MemoryStatus.REGISTERED)));
        this.canAttackPredicate = predicate;
        this.targetFinderFunction = function;
    }

    public StartAttacking(Function<E, Optional<? extends LivingEntity>> function) {
        this(mob -> true, function);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, E e) {
        if (!this.canAttackPredicate.test(e)) {
            return false;
        }
        Optional<? extends LivingEntity> optional = this.targetFinderFunction.apply(e);
        return optional.isPresent() && optional.get().isAlive();
    }

    @Override
    protected void start(ServerLevel serverLevel, E e, long l) {
        this.targetFinderFunction.apply(e).ifPresent(livingEntity -> this.setAttackTarget(e, (LivingEntity)livingEntity));
    }

    private void setAttackTarget(E e, LivingEntity livingEntity) {
        ((LivingEntity)e).getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, livingEntity);
        ((LivingEntity)e).getBrain().eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
    }
}

