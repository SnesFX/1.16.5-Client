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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ProjectileWeaponItem;

public class MeleeAttack
extends Behavior<Mob> {
    private final int cooldownBetweenAttacks;

    public MeleeAttack(int n) {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryStatus.REGISTERED), MemoryModuleType.ATTACK_TARGET, (Object)((Object)MemoryStatus.VALUE_PRESENT), MemoryModuleType.ATTACK_COOLING_DOWN, (Object)((Object)MemoryStatus.VALUE_ABSENT)));
        this.cooldownBetweenAttacks = n;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, Mob mob) {
        LivingEntity livingEntity = this.getAttackTarget(mob);
        return !this.isHoldingUsableProjectileWeapon(mob) && BehaviorUtils.canSee(mob, livingEntity) && BehaviorUtils.isWithinMeleeAttackRange(mob, livingEntity);
    }

    private boolean isHoldingUsableProjectileWeapon(Mob mob) {
        return mob.isHolding(item -> item instanceof ProjectileWeaponItem && mob.canFireProjectileWeapon((ProjectileWeaponItem)item));
    }

    @Override
    protected void start(ServerLevel serverLevel, Mob mob, long l) {
        LivingEntity livingEntity = this.getAttackTarget(mob);
        BehaviorUtils.lookAtEntity(mob, livingEntity);
        mob.swing(InteractionHand.MAIN_HAND);
        mob.doHurtTarget(livingEntity);
        mob.getBrain().setMemoryWithExpiry(MemoryModuleType.ATTACK_COOLING_DOWN, true, this.cooldownBetweenAttacks);
    }

    private LivingEntity getAttackTarget(Mob mob) {
        return mob.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
    }
}

