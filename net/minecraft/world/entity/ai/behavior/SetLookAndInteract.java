/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class SetLookAndInteract
extends Behavior<LivingEntity> {
    private final EntityType<?> type;
    private final int interactionRangeSqr;
    private final Predicate<LivingEntity> targetFilter;
    private final Predicate<LivingEntity> selfFilter;

    public SetLookAndInteract(EntityType<?> entityType, int n, Predicate<LivingEntity> predicate, Predicate<LivingEntity> predicate2) {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryStatus.REGISTERED), MemoryModuleType.INTERACTION_TARGET, (Object)((Object)MemoryStatus.VALUE_ABSENT), MemoryModuleType.VISIBLE_LIVING_ENTITIES, (Object)((Object)MemoryStatus.VALUE_PRESENT)));
        this.type = entityType;
        this.interactionRangeSqr = n * n;
        this.targetFilter = predicate2;
        this.selfFilter = predicate;
    }

    public SetLookAndInteract(EntityType<?> entityType, int n) {
        this(entityType, n, livingEntity -> true, livingEntity -> true);
    }

    @Override
    public boolean checkExtraStartConditions(ServerLevel serverLevel, LivingEntity livingEntity) {
        return this.selfFilter.test(livingEntity) && this.getVisibleEntities(livingEntity).stream().anyMatch(this::isMatchingTarget);
    }

    @Override
    public void start(ServerLevel serverLevel, LivingEntity livingEntity, long l) {
        super.start(serverLevel, livingEntity, l);
        Brain<?> brain = livingEntity.getBrain();
        brain.getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).ifPresent(list -> list.stream().filter(livingEntity2 -> livingEntity2.distanceToSqr(livingEntity) <= (double)this.interactionRangeSqr).filter(this::isMatchingTarget).findFirst().ifPresent(livingEntity -> {
            brain.setMemory(MemoryModuleType.INTERACTION_TARGET, livingEntity);
            brain.setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker((Entity)livingEntity, true));
        }));
    }

    private boolean isMatchingTarget(LivingEntity livingEntity) {
        return this.type.equals(livingEntity.getType()) && this.targetFilter.test(livingEntity);
    }

    private List<LivingEntity> getVisibleEntities(LivingEntity livingEntity) {
        return livingEntity.getBrain().getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).get();
    }
}

