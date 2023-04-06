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
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.Level;

public class StopAttackingIfTargetInvalid<E extends Mob>
extends Behavior<E> {
    private final Predicate<LivingEntity> stopAttackingWhen;

    public StopAttackingIfTargetInvalid(Predicate<LivingEntity> predicate) {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, (Object)((Object)MemoryStatus.VALUE_PRESENT), MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, (Object)((Object)MemoryStatus.REGISTERED)));
        this.stopAttackingWhen = predicate;
    }

    public StopAttackingIfTargetInvalid() {
        this(livingEntity -> false);
    }

    @Override
    protected void start(ServerLevel serverLevel, E e, long l) {
        if (StopAttackingIfTargetInvalid.isTiredOfTryingToReachTarget(e)) {
            this.clearAttackTarget(e);
            return;
        }
        if (this.isCurrentTargetDeadOrRemoved(e)) {
            this.clearAttackTarget(e);
            return;
        }
        if (this.isCurrentTargetInDifferentLevel(e)) {
            this.clearAttackTarget(e);
            return;
        }
        if (!EntitySelector.ATTACK_ALLOWED.test(this.getAttackTarget(e))) {
            this.clearAttackTarget(e);
            return;
        }
        if (this.stopAttackingWhen.test(this.getAttackTarget(e))) {
            this.clearAttackTarget(e);
            return;
        }
    }

    private boolean isCurrentTargetInDifferentLevel(E e) {
        return this.getAttackTarget(e).level != ((Mob)e).level;
    }

    private LivingEntity getAttackTarget(E e) {
        return ((LivingEntity)e).getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
    }

    private static <E extends LivingEntity> boolean isTiredOfTryingToReachTarget(E e) {
        Optional<Long> optional = ((LivingEntity)e).getBrain().getMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        return optional.isPresent() && ((LivingEntity)e).level.getGameTime() - optional.get() > 200L;
    }

    private boolean isCurrentTargetDeadOrRemoved(E e) {
        Optional<LivingEntity> optional = ((LivingEntity)e).getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);
        return optional.isPresent() && !optional.get().isAlive();
    }

    private void clearAttackTarget(E e) {
        ((LivingEntity)e).getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
    }
}

