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
import java.util.function.Consumer;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class StrollToPoi
extends Behavior<PathfinderMob> {
    private final MemoryModuleType<GlobalPos> memoryType;
    private final int closeEnoughDist;
    private final int maxDistanceFromPoi;
    private final float speedModifier;
    private long nextOkStartTime;

    public StrollToPoi(MemoryModuleType<GlobalPos> memoryModuleType, float f, int n, int n2) {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryStatus.REGISTERED), memoryModuleType, (Object)((Object)MemoryStatus.VALUE_PRESENT)));
        this.memoryType = memoryModuleType;
        this.speedModifier = f;
        this.closeEnoughDist = n;
        this.maxDistanceFromPoi = n2;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, PathfinderMob pathfinderMob) {
        Optional<GlobalPos> optional = pathfinderMob.getBrain().getMemory(this.memoryType);
        return optional.isPresent() && serverLevel.dimension() == optional.get().dimension() && optional.get().pos().closerThan(pathfinderMob.position(), (double)this.maxDistanceFromPoi);
    }

    @Override
    protected void start(ServerLevel serverLevel, PathfinderMob pathfinderMob, long l) {
        if (l > this.nextOkStartTime) {
            Brain<?> brain = pathfinderMob.getBrain();
            Optional<GlobalPos> optional = brain.getMemory(this.memoryType);
            optional.ifPresent(globalPos -> brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(globalPos.pos(), this.speedModifier, this.closeEnoughDist)));
            this.nextOkStartTime = l + 80L;
        }
    }
}

