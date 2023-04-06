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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;

public class Mount<E extends LivingEntity>
extends Behavior<E> {
    private final float speedModifier;

    public Mount(float f) {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryStatus.REGISTERED), MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryStatus.VALUE_ABSENT), MemoryModuleType.RIDE_TARGET, (Object)((Object)MemoryStatus.VALUE_PRESENT)));
        this.speedModifier = f;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, E e) {
        return !((Entity)e).isPassenger();
    }

    @Override
    protected void start(ServerLevel serverLevel, E e, long l) {
        if (this.isCloseEnoughToStartRiding(e)) {
            ((Entity)e).startRiding(this.getRidableEntity(e));
        } else {
            BehaviorUtils.setWalkAndLookTargetMemories(e, this.getRidableEntity(e), this.speedModifier, 1);
        }
    }

    private boolean isCloseEnoughToStartRiding(E e) {
        return this.getRidableEntity(e).closerThan((Entity)e, 1.0);
    }

    private Entity getRidableEntity(E e) {
        return ((LivingEntity)e).getBrain().getMemory(MemoryModuleType.RIDE_TARGET).get();
    }
}

