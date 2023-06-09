/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public class MoveToSkySeeingSpot
extends Behavior<LivingEntity> {
    private final float speedModifier;

    public MoveToSkySeeingSpot(float f) {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryStatus.VALUE_ABSENT)));
        this.speedModifier = f;
    }

    @Override
    protected void start(ServerLevel serverLevel, LivingEntity livingEntity, long l) {
        Optional<Vec3> optional = Optional.ofNullable(this.getOutdoorPosition(serverLevel, livingEntity));
        if (optional.isPresent()) {
            livingEntity.getBrain().setMemory(MemoryModuleType.WALK_TARGET, optional.map(vec3 -> new WalkTarget((Vec3)vec3, this.speedModifier, 0)));
        }
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, LivingEntity livingEntity) {
        return !serverLevel.canSeeSky(livingEntity.blockPosition());
    }

    @Nullable
    private Vec3 getOutdoorPosition(ServerLevel serverLevel, LivingEntity livingEntity) {
        Random random = livingEntity.getRandom();
        BlockPos blockPos = livingEntity.blockPosition();
        for (int i = 0; i < 10; ++i) {
            BlockPos blockPos2 = blockPos.offset(random.nextInt(20) - 10, random.nextInt(6) - 3, random.nextInt(20) - 10);
            if (!MoveToSkySeeingSpot.hasNoBlocksAbove(serverLevel, livingEntity, blockPos2)) continue;
            return Vec3.atBottomCenterOf(blockPos2);
        }
        return null;
    }

    public static boolean hasNoBlocksAbove(ServerLevel serverLevel, LivingEntity livingEntity, BlockPos blockPos) {
        return serverLevel.canSeeSky(blockPos) && (double)serverLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, blockPos).getY() <= livingEntity.getY();
    }
}

