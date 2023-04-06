/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Position;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class StrollToPoiList
extends Behavior<Villager> {
    private final MemoryModuleType<List<GlobalPos>> strollToMemoryType;
    private final MemoryModuleType<GlobalPos> mustBeCloseToMemoryType;
    private final float speedModifier;
    private final int closeEnoughDist;
    private final int maxDistanceFromPoi;
    private long nextOkStartTime;
    @Nullable
    private GlobalPos targetPos;

    public StrollToPoiList(MemoryModuleType<List<GlobalPos>> memoryModuleType, float f, int n, int n2, MemoryModuleType<GlobalPos> memoryModuleType2) {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryStatus.REGISTERED), memoryModuleType, (Object)((Object)MemoryStatus.VALUE_PRESENT), memoryModuleType2, (Object)((Object)MemoryStatus.VALUE_PRESENT)));
        this.strollToMemoryType = memoryModuleType;
        this.speedModifier = f;
        this.closeEnoughDist = n;
        this.maxDistanceFromPoi = n2;
        this.mustBeCloseToMemoryType = memoryModuleType2;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, Villager villager) {
        List<GlobalPos> list;
        Optional<List<GlobalPos>> optional = villager.getBrain().getMemory(this.strollToMemoryType);
        Optional<GlobalPos> optional2 = villager.getBrain().getMemory(this.mustBeCloseToMemoryType);
        if (optional.isPresent() && optional2.isPresent() && !(list = optional.get()).isEmpty()) {
            this.targetPos = list.get(serverLevel.getRandom().nextInt(list.size()));
            return this.targetPos != null && serverLevel.dimension() == this.targetPos.dimension() && optional2.get().pos().closerThan(villager.position(), (double)this.maxDistanceFromPoi);
        }
        return false;
    }

    @Override
    protected void start(ServerLevel serverLevel, Villager villager, long l) {
        if (l > this.nextOkStartTime && this.targetPos != null) {
            villager.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(this.targetPos.pos(), this.speedModifier, this.closeEnoughDist));
            this.nextOkStartTime = l + 100L;
        }
    }
}

