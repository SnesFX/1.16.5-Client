/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.world.entity.monster.piglin;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;

public class RememberIfHoglinWasKilled<E extends Piglin>
extends Behavior<E> {
    public RememberIfHoglinWasKilled() {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, (Object)((Object)MemoryStatus.VALUE_PRESENT), MemoryModuleType.HUNTED_RECENTLY, (Object)((Object)MemoryStatus.REGISTERED)));
    }

    @Override
    protected void start(ServerLevel serverLevel, E e, long l) {
        if (this.isAttackTargetDeadHoglin(e)) {
            PiglinAi.dontKillAnyMoreHoglinsForAWhile(e);
        }
    }

    private boolean isAttackTargetDeadHoglin(E e) {
        LivingEntity livingEntity = ((Piglin)e).getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
        return livingEntity.getType() == EntityType.HOGLIN && livingEntity.isDeadOrDying();
    }
}

