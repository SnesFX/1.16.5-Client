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
import java.util.UUID;
import java.util.function.BiPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;

public class StartCelebratingIfTargetDead
extends Behavior<LivingEntity> {
    private final int celebrateDuration;
    private final BiPredicate<LivingEntity, LivingEntity> dancePredicate;

    public StartCelebratingIfTargetDead(int n, BiPredicate<LivingEntity, LivingEntity> biPredicate) {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, (Object)((Object)MemoryStatus.VALUE_PRESENT), MemoryModuleType.ANGRY_AT, (Object)((Object)MemoryStatus.REGISTERED), MemoryModuleType.CELEBRATE_LOCATION, (Object)((Object)MemoryStatus.VALUE_ABSENT), MemoryModuleType.DANCING, (Object)((Object)MemoryStatus.REGISTERED)));
        this.celebrateDuration = n;
        this.dancePredicate = biPredicate;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, LivingEntity livingEntity) {
        return this.getAttackTarget(livingEntity).isDeadOrDying();
    }

    @Override
    protected void start(ServerLevel serverLevel, LivingEntity livingEntity, long l) {
        LivingEntity livingEntity2 = this.getAttackTarget(livingEntity);
        if (this.dancePredicate.test(livingEntity, livingEntity2)) {
            livingEntity.getBrain().setMemoryWithExpiry(MemoryModuleType.DANCING, true, this.celebrateDuration);
        }
        livingEntity.getBrain().setMemoryWithExpiry(MemoryModuleType.CELEBRATE_LOCATION, livingEntity2.blockPosition(), this.celebrateDuration);
        if (livingEntity2.getType() != EntityType.PLAYER || serverLevel.getGameRules().getBoolean(GameRules.RULE_FORGIVE_DEAD_PLAYERS)) {
            livingEntity.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
            livingEntity.getBrain().eraseMemory(MemoryModuleType.ANGRY_AT);
        }
    }

    private LivingEntity getAttackTarget(LivingEntity livingEntity) {
        return livingEntity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
    }
}

