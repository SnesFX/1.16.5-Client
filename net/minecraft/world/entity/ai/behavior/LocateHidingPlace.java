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
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Position;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class LocateHidingPlace
extends Behavior<LivingEntity> {
    private final float speedModifier;
    private final int radius;
    private final int closeEnoughDist;
    private Optional<BlockPos> currentPos = Optional.empty();

    public LocateHidingPlace(int n, float f, int n2) {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryStatus.VALUE_ABSENT), MemoryModuleType.HOME, (Object)((Object)MemoryStatus.REGISTERED), MemoryModuleType.HIDING_PLACE, (Object)((Object)MemoryStatus.REGISTERED)));
        this.radius = n;
        this.speedModifier = f;
        this.closeEnoughDist = n2;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, LivingEntity livingEntity) {
        Optional<BlockPos> optional = serverLevel.getPoiManager().find(poiType -> poiType == PoiType.HOME, blockPos -> true, livingEntity.blockPosition(), this.closeEnoughDist + 1, PoiManager.Occupancy.ANY);
        this.currentPos = optional.isPresent() && optional.get().closerThan(livingEntity.position(), (double)this.closeEnoughDist) ? optional : Optional.empty();
        return true;
    }

    @Override
    protected void start(ServerLevel serverLevel, LivingEntity livingEntity, long l) {
        Optional<GlobalPos> optional;
        Brain<?> brain = livingEntity.getBrain();
        Optional<BlockPos> optional2 = this.currentPos;
        if (!optional2.isPresent() && !(optional2 = serverLevel.getPoiManager().getRandom(poiType -> poiType == PoiType.HOME, blockPos -> true, PoiManager.Occupancy.ANY, livingEntity.blockPosition(), this.radius, livingEntity.getRandom())).isPresent() && (optional = brain.getMemory(MemoryModuleType.HOME)).isPresent()) {
            optional2 = Optional.of(optional.get().pos());
        }
        if (optional2.isPresent()) {
            brain.eraseMemory(MemoryModuleType.PATH);
            brain.eraseMemory(MemoryModuleType.LOOK_TARGET);
            brain.eraseMemory(MemoryModuleType.BREED_TARGET);
            brain.eraseMemory(MemoryModuleType.INTERACTION_TARGET);
            brain.setMemory(MemoryModuleType.HIDING_PLACE, GlobalPos.of(serverLevel.dimension(), optional2.get()));
            if (!optional2.get().closerThan(livingEntity.position(), (double)this.closeEnoughDist)) {
                brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(optional2.get(), this.speedModifier, this.closeEnoughDist));
            }
        }
    }
}

