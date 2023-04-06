/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;

public class StopBeingAngryIfTargetDead<E extends Mob>
extends Behavior<E> {
    public StopBeingAngryIfTargetDead() {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.ANGRY_AT, (Object)((Object)MemoryStatus.VALUE_PRESENT)));
    }

    @Override
    protected void start(ServerLevel serverLevel, E e, long l) {
        BehaviorUtils.getLivingEntityFromUUIDMemory(e, MemoryModuleType.ANGRY_AT).ifPresent(livingEntity -> {
            if (livingEntity.isDeadOrDying() && (livingEntity.getType() != EntityType.PLAYER || serverLevel.getGameRules().getBoolean(GameRules.RULE_FORGIVE_DEAD_PLAYERS))) {
                e.getBrain().eraseMemory(MemoryModuleType.ANGRY_AT);
            }
        });
    }
}

