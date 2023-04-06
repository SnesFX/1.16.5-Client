/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.world.entity.monster.piglin;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;

public class StartHuntingHoglin<E extends Piglin>
extends Behavior<E> {
    public StartHuntingHoglin() {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN, (Object)((Object)MemoryStatus.VALUE_PRESENT), MemoryModuleType.ANGRY_AT, (Object)((Object)MemoryStatus.VALUE_ABSENT), MemoryModuleType.HUNTED_RECENTLY, (Object)((Object)MemoryStatus.VALUE_ABSENT), MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, (Object)((Object)MemoryStatus.REGISTERED)));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, Piglin piglin) {
        return !piglin.isBaby() && !PiglinAi.hasAnyoneNearbyHuntedRecently(piglin);
    }

    @Override
    protected void start(ServerLevel serverLevel, E e, long l) {
        Hoglin hoglin = ((Piglin)e).getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN).get();
        PiglinAi.setAngerTarget(e, hoglin);
        PiglinAi.dontKillAnyMoreHoglinsForAWhile(e);
        PiglinAi.broadcastAngerTarget(e, hoglin);
        PiglinAi.broadcastDontKillAnyMoreHoglinsForAWhile(e);
    }
}

