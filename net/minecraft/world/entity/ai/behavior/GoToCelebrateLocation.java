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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.level.Level;

public class GoToCelebrateLocation<E extends Mob>
extends Behavior<E> {
    private final int closeEnoughDist;
    private final float speedModifier;

    public GoToCelebrateLocation(int n, float f) {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.CELEBRATE_LOCATION, (Object)((Object)MemoryStatus.VALUE_PRESENT), MemoryModuleType.ATTACK_TARGET, (Object)((Object)MemoryStatus.VALUE_ABSENT), MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryStatus.VALUE_ABSENT), MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryStatus.REGISTERED)));
        this.closeEnoughDist = n;
        this.speedModifier = f;
    }

    @Override
    protected void start(ServerLevel serverLevel, Mob mob, long l) {
        BlockPos blockPos = GoToCelebrateLocation.getCelebrateLocation(mob);
        boolean bl = blockPos.closerThan(mob.blockPosition(), (double)this.closeEnoughDist);
        if (!bl) {
            BehaviorUtils.setWalkAndLookTargetMemories((LivingEntity)mob, GoToCelebrateLocation.getNearbyPos(mob, blockPos), this.speedModifier, this.closeEnoughDist);
        }
    }

    private static BlockPos getNearbyPos(Mob mob, BlockPos blockPos) {
        Random random = mob.level.random;
        return blockPos.offset(GoToCelebrateLocation.getRandomOffset(random), 0, GoToCelebrateLocation.getRandomOffset(random));
    }

    private static int getRandomOffset(Random random) {
        return random.nextInt(3) - 1;
    }

    private static BlockPos getCelebrateLocation(Mob mob) {
        return mob.getBrain().getMemory(MemoryModuleType.CELEBRATE_LOCATION).get();
    }
}

