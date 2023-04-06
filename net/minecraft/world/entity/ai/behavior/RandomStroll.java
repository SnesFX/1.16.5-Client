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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.phys.Vec3;

public class RandomStroll
extends Behavior<PathfinderMob> {
    private final float speedModifier;
    private final int maxHorizontalDistance;
    private final int maxVerticalDistance;

    public RandomStroll(float f) {
        this(f, 10, 7);
    }

    public RandomStroll(float f, int n, int n2) {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryStatus.VALUE_ABSENT)));
        this.speedModifier = f;
        this.maxHorizontalDistance = n;
        this.maxVerticalDistance = n2;
    }

    @Override
    protected void start(ServerLevel serverLevel, PathfinderMob pathfinderMob, long l) {
        Optional<Vec3> optional = Optional.ofNullable(RandomPos.getLandPos(pathfinderMob, this.maxHorizontalDistance, this.maxVerticalDistance));
        pathfinderMob.getBrain().setMemory(MemoryModuleType.WALK_TARGET, optional.map(vec3 -> new WalkTarget((Vec3)vec3, this.speedModifier, 0)));
    }
}

