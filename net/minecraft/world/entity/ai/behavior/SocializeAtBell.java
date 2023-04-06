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
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Position;
import net.minecraft.resources.ResourceKey;
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
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SocializeAtBell
extends Behavior<LivingEntity> {
    public SocializeAtBell() {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryStatus.REGISTERED), MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryStatus.REGISTERED), MemoryModuleType.MEETING_POINT, (Object)((Object)MemoryStatus.VALUE_PRESENT), MemoryModuleType.VISIBLE_LIVING_ENTITIES, (Object)((Object)MemoryStatus.VALUE_PRESENT), MemoryModuleType.INTERACTION_TARGET, (Object)((Object)MemoryStatus.VALUE_ABSENT)));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, LivingEntity livingEntity2) {
        Brain<?> brain = livingEntity2.getBrain();
        Optional<GlobalPos> optional = brain.getMemory(MemoryModuleType.MEETING_POINT);
        return serverLevel.getRandom().nextInt(100) == 0 && optional.isPresent() && serverLevel.dimension() == optional.get().dimension() && optional.get().pos().closerThan(livingEntity2.position(), 4.0) && brain.getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).get().stream().anyMatch(livingEntity -> EntityType.VILLAGER.equals(livingEntity.getType()));
    }

    @Override
    protected void start(ServerLevel serverLevel, LivingEntity livingEntity, long l) {
        Brain<?> brain = livingEntity.getBrain();
        brain.getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).ifPresent(list -> list.stream().filter(livingEntity -> EntityType.VILLAGER.equals(livingEntity.getType())).filter(livingEntity2 -> livingEntity2.distanceToSqr(livingEntity) <= 32.0).findFirst().ifPresent(livingEntity -> {
            brain.setMemory(MemoryModuleType.INTERACTION_TARGET, livingEntity);
            brain.setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker((Entity)livingEntity, true));
            brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityTracker((Entity)livingEntity, false), 0.3f, 1));
        }));
    }
}

