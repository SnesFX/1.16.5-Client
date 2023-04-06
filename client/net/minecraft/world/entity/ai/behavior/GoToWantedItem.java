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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.item.ItemEntity;

public class GoToWantedItem<E extends LivingEntity>
extends Behavior<E> {
    private final Predicate<E> predicate;
    private final int maxDistToWalk;
    private final float speedModifier;

    public GoToWantedItem(float f, boolean bl, int n) {
        this(livingEntity -> true, f, bl, n);
    }

    public GoToWantedItem(Predicate<E> predicate, float f, boolean bl, int n) {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryStatus.REGISTERED), MemoryModuleType.WALK_TARGET, (Object)((Object)(bl ? MemoryStatus.REGISTERED : MemoryStatus.VALUE_ABSENT)), MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, (Object)((Object)MemoryStatus.VALUE_PRESENT)));
        this.predicate = predicate;
        this.maxDistToWalk = n;
        this.speedModifier = f;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, E e) {
        return this.predicate.test(e) && this.getClosestLovedItem(e).closerThan((Entity)e, this.maxDistToWalk);
    }

    @Override
    protected void start(ServerLevel serverLevel, E e, long l) {
        BehaviorUtils.setWalkAndLookTargetMemories(e, this.getClosestLovedItem(e), this.speedModifier, 0);
    }

    private ItemEntity getClosestLovedItem(E e) {
        return ((LivingEntity)e).getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM).get();
    }
}

