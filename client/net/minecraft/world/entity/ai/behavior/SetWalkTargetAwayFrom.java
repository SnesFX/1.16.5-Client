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
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.phys.Vec3;

public class SetWalkTargetAwayFrom<T>
extends Behavior<PathfinderMob> {
    private final MemoryModuleType<T> walkAwayFromMemory;
    private final float speedModifier;
    private final int desiredDistance;
    private final Function<T, Vec3> toPosition;

    public SetWalkTargetAwayFrom(MemoryModuleType<T> memoryModuleType, float f, int n, boolean bl, Function<T, Vec3> function) {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)(bl ? MemoryStatus.REGISTERED : MemoryStatus.VALUE_ABSENT)), memoryModuleType, (Object)((Object)MemoryStatus.VALUE_PRESENT)));
        this.walkAwayFromMemory = memoryModuleType;
        this.speedModifier = f;
        this.desiredDistance = n;
        this.toPosition = function;
    }

    public static SetWalkTargetAwayFrom<BlockPos> pos(MemoryModuleType<BlockPos> memoryModuleType, float f, int n, boolean bl) {
        return new SetWalkTargetAwayFrom<BlockPos>(memoryModuleType, f, n, bl, Vec3::atBottomCenterOf);
    }

    public static SetWalkTargetAwayFrom<? extends Entity> entity(MemoryModuleType<? extends Entity> memoryModuleType, float f, int n, boolean bl) {
        return new SetWalkTargetAwayFrom<Entity>(memoryModuleType, f, n, bl, Entity::position);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, PathfinderMob pathfinderMob) {
        if (this.alreadyWalkingAwayFromPosWithSameSpeed(pathfinderMob)) {
            return false;
        }
        return pathfinderMob.position().closerThan(this.getPosToAvoid(pathfinderMob), this.desiredDistance);
    }

    private Vec3 getPosToAvoid(PathfinderMob pathfinderMob) {
        return this.toPosition.apply(pathfinderMob.getBrain().getMemory(this.walkAwayFromMemory).get());
    }

    private boolean alreadyWalkingAwayFromPosWithSameSpeed(PathfinderMob pathfinderMob) {
        Vec3 vec3;
        if (!pathfinderMob.getBrain().hasMemoryValue(MemoryModuleType.WALK_TARGET)) {
            return false;
        }
        WalkTarget walkTarget = pathfinderMob.getBrain().getMemory(MemoryModuleType.WALK_TARGET).get();
        if (walkTarget.getSpeedModifier() != this.speedModifier) {
            return false;
        }
        Vec3 vec32 = walkTarget.getTarget().currentPosition().subtract(pathfinderMob.position());
        return vec32.dot(vec3 = this.getPosToAvoid(pathfinderMob).subtract(pathfinderMob.position())) < 0.0;
    }

    @Override
    protected void start(ServerLevel serverLevel, PathfinderMob pathfinderMob, long l) {
        SetWalkTargetAwayFrom.moveAwayFrom(pathfinderMob, this.getPosToAvoid(pathfinderMob), this.speedModifier);
    }

    private static void moveAwayFrom(PathfinderMob pathfinderMob, Vec3 vec3, float f) {
        for (int i = 0; i < 10; ++i) {
            Vec3 vec32 = RandomPos.getLandPosAvoid(pathfinderMob, 16, 7, vec3);
            if (vec32 == null) continue;
            pathfinderMob.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(vec32, f, 0));
            return;
        }
    }
}

