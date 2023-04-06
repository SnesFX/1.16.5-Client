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
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.phys.Vec3;

public class VillageBoundRandomStroll
extends Behavior<PathfinderMob> {
    private final float speedModifier;
    private final int maxXyDist;
    private final int maxYDist;

    public VillageBoundRandomStroll(float f) {
        this(f, 10, 7);
    }

    public VillageBoundRandomStroll(float f, int n, int n2) {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryStatus.VALUE_ABSENT)));
        this.speedModifier = f;
        this.maxXyDist = n;
        this.maxYDist = n2;
    }

    @Override
    protected void start(ServerLevel serverLevel, PathfinderMob pathfinderMob, long l) {
        BlockPos blockPos = pathfinderMob.blockPosition();
        if (serverLevel.isVillage(blockPos)) {
            this.setRandomPos(pathfinderMob);
        } else {
            SectionPos sectionPos = SectionPos.of(blockPos);
            SectionPos sectionPos2 = BehaviorUtils.findSectionClosestToVillage(serverLevel, sectionPos, 2);
            if (sectionPos2 != sectionPos) {
                this.setTargetedPos(pathfinderMob, sectionPos2);
            } else {
                this.setRandomPos(pathfinderMob);
            }
        }
    }

    private void setTargetedPos(PathfinderMob pathfinderMob, SectionPos sectionPos) {
        Optional<Vec3> optional = Optional.ofNullable(RandomPos.getPosTowards(pathfinderMob, this.maxXyDist, this.maxYDist, Vec3.atBottomCenterOf(sectionPos.center())));
        pathfinderMob.getBrain().setMemory(MemoryModuleType.WALK_TARGET, optional.map(vec3 -> new WalkTarget((Vec3)vec3, this.speedModifier, 0)));
    }

    private void setRandomPos(PathfinderMob pathfinderMob) {
        Optional<Vec3> optional = Optional.ofNullable(RandomPos.getLandPos(pathfinderMob, this.maxXyDist, this.maxYDist));
        pathfinderMob.getBrain().setMemory(MemoryModuleType.WALK_TARGET, optional.map(vec3 -> new WalkTarget((Vec3)vec3, this.speedModifier, 0)));
    }
}

