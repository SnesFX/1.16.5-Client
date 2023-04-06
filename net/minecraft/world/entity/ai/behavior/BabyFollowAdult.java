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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.IntRange;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;

public class BabyFollowAdult<E extends AgableMob>
extends Behavior<E> {
    private final IntRange followRange;
    private final float speedModifier;

    public BabyFollowAdult(IntRange intRange, float f) {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.NEAREST_VISIBLE_ADULT, (Object)((Object)MemoryStatus.VALUE_PRESENT), MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryStatus.VALUE_ABSENT)));
        this.followRange = intRange;
        this.speedModifier = f;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, E e) {
        if (!((AgableMob)e).isBaby()) {
            return false;
        }
        AgableMob agableMob = this.getNearestAdult(e);
        return ((Entity)e).closerThan(agableMob, this.followRange.getMaxInclusive() + 1) && !((Entity)e).closerThan(agableMob, this.followRange.getMinInclusive());
    }

    @Override
    protected void start(ServerLevel serverLevel, E e, long l) {
        BehaviorUtils.setWalkAndLookTargetMemories(e, this.getNearestAdult(e), this.speedModifier, this.followRange.getMinInclusive() - 1);
    }

    private AgableMob getNearestAdult(E e) {
        return ((LivingEntity)e).getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT).get();
    }
}

