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
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SetWalkTargetFromBlockMemory
extends Behavior<Villager> {
    private final MemoryModuleType<GlobalPos> memoryType;
    private final float speedModifier;
    private final int closeEnoughDist;
    private final int tooFarDistance;
    private final int tooLongUnreachableDuration;

    public SetWalkTargetFromBlockMemory(MemoryModuleType<GlobalPos> memoryModuleType, float f, int n, int n2, int n3) {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, (Object)((Object)MemoryStatus.REGISTERED), MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryStatus.VALUE_ABSENT), memoryModuleType, (Object)((Object)MemoryStatus.VALUE_PRESENT)));
        this.memoryType = memoryModuleType;
        this.speedModifier = f;
        this.closeEnoughDist = n;
        this.tooFarDistance = n2;
        this.tooLongUnreachableDuration = n3;
    }

    private void dropPOI(Villager villager, long l) {
        Brain<Villager> brain = villager.getBrain();
        villager.releasePoi(this.memoryType);
        brain.eraseMemory(this.memoryType);
        brain.setMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, l);
    }

    @Override
    protected void start(ServerLevel serverLevel, Villager villager, long l) {
        Brain<Villager> brain = villager.getBrain();
        brain.getMemory(this.memoryType).ifPresent(globalPos -> {
            if (this.wrongDimension(serverLevel, (GlobalPos)globalPos) || this.tiredOfTryingToFindTarget(serverLevel, villager)) {
                this.dropPOI(villager, l);
            } else if (this.tooFar(villager, (GlobalPos)globalPos)) {
                int n;
                Vec3 vec3 = null;
                int n2 = 1000;
                for (n = 0; n < 1000 && (vec3 == null || this.tooFar(villager, GlobalPos.of(serverLevel.dimension(), new BlockPos(vec3)))); ++n) {
                    vec3 = RandomPos.getPosTowards(villager, 15, 7, Vec3.atBottomCenterOf(globalPos.pos()));
                }
                if (n == 1000) {
                    this.dropPOI(villager, l);
                    return;
                }
                brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3, this.speedModifier, this.closeEnoughDist));
            } else if (!this.closeEnough(serverLevel, villager, (GlobalPos)globalPos)) {
                brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(globalPos.pos(), this.speedModifier, this.closeEnoughDist));
            }
        });
    }

    private boolean tiredOfTryingToFindTarget(ServerLevel serverLevel, Villager villager) {
        Optional<Long> optional = villager.getBrain().getMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        if (optional.isPresent()) {
            return serverLevel.getGameTime() - optional.get() > (long)this.tooLongUnreachableDuration;
        }
        return false;
    }

    private boolean tooFar(Villager villager, GlobalPos globalPos) {
        return globalPos.pos().distManhattan(villager.blockPosition()) > this.tooFarDistance;
    }

    private boolean wrongDimension(ServerLevel serverLevel, GlobalPos globalPos) {
        return globalPos.dimension() != serverLevel.dimension();
    }

    private boolean closeEnough(ServerLevel serverLevel, Villager villager, GlobalPos globalPos) {
        return globalPos.dimension() == serverLevel.dimension() && globalPos.pos().distManhattan(villager.blockPosition()) <= this.closeEnoughDist;
    }
}

