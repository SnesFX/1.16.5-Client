/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;

public class InsideBrownianWalk
extends Behavior<PathfinderMob> {
    private final float speedModifier;

    public InsideBrownianWalk(float f) {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryStatus.VALUE_ABSENT)));
        this.speedModifier = f;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, PathfinderMob pathfinderMob) {
        return !serverLevel.canSeeSky(pathfinderMob.blockPosition());
    }

    @Override
    protected void start(ServerLevel serverLevel, PathfinderMob pathfinderMob, long l) {
        BlockPos blockPos2 = pathfinderMob.blockPosition();
        List list = BlockPos.betweenClosedStream(blockPos2.offset(-1, -1, -1), blockPos2.offset(1, 1, 1)).map(BlockPos::immutable).collect(Collectors.toList());
        Collections.shuffle(list);
        Optional<BlockPos> optional = list.stream().filter(blockPos -> !serverLevel.canSeeSky((BlockPos)blockPos)).filter(blockPos -> serverLevel.loadedAndEntityCanStandOn((BlockPos)blockPos, pathfinderMob)).filter(blockPos -> serverLevel.noCollision(pathfinderMob)).findFirst();
        optional.ifPresent(blockPos -> pathfinderMob.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget((BlockPos)blockPos, this.speedModifier, 0)));
    }
}

