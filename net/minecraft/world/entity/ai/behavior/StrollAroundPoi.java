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
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Position;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class StrollAroundPoi
extends Behavior<PathfinderMob> {
    private final MemoryModuleType<GlobalPos> memoryType;
    private long nextOkStartTime;
    private final int maxDistanceFromPoi;
    private float speedModifier;

    public StrollAroundPoi(MemoryModuleType<GlobalPos> memoryModuleType, float f, int n) {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryStatus.REGISTERED), memoryModuleType, (Object)((Object)MemoryStatus.VALUE_PRESENT)));
        this.memoryType = memoryModuleType;
        this.speedModifier = f;
        this.maxDistanceFromPoi = n;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, PathfinderMob pathfinderMob) {
        Optional<GlobalPos> optional = pathfinderMob.getBrain().getMemory(this.memoryType);
        return optional.isPresent() && serverLevel.dimension() == optional.get().dimension() && optional.get().pos().closerThan(pathfinderMob.position(), (double)this.maxDistanceFromPoi);
    }

    @Override
    protected void start(ServerLevel serverLevel, PathfinderMob pathfinderMob, long l) {
        if (l > this.nextOkStartTime) {
            Optional<Vec3> optional = Optional.ofNullable(RandomPos.getLandPos(pathfinderMob, 8, 6));
            pathfinderMob.getBrain().setMemory(MemoryModuleType.WALK_TARGET, optional.map(vec3 -> new WalkTarget((Vec3)vec3, this.speedModifier, 1)));
            this.nextOkStartTime = l + 180L;
        }
    }
}

