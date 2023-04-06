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
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class JumpOnBed
extends Behavior<Mob> {
    private final float speedModifier;
    @Nullable
    private BlockPos targetBed;
    private int remainingTimeToReachBed;
    private int remainingJumps;
    private int remainingCooldownUntilNextJump;

    public JumpOnBed(float f) {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.NEAREST_BED, (Object)((Object)MemoryStatus.VALUE_PRESENT), MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryStatus.VALUE_ABSENT)));
        this.speedModifier = f;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, Mob mob) {
        return mob.isBaby() && this.nearBed(serverLevel, mob);
    }

    @Override
    protected void start(ServerLevel serverLevel, Mob mob, long l) {
        super.start(serverLevel, mob, l);
        this.getNearestBed(mob).ifPresent(blockPos -> {
            this.targetBed = blockPos;
            this.remainingTimeToReachBed = 100;
            this.remainingJumps = 3 + serverLevel.random.nextInt(4);
            this.remainingCooldownUntilNextJump = 0;
            this.startWalkingTowardsBed(mob, (BlockPos)blockPos);
        });
    }

    @Override
    protected void stop(ServerLevel serverLevel, Mob mob, long l) {
        super.stop(serverLevel, mob, l);
        this.targetBed = null;
        this.remainingTimeToReachBed = 0;
        this.remainingJumps = 0;
        this.remainingCooldownUntilNextJump = 0;
    }

    @Override
    protected boolean canStillUse(ServerLevel serverLevel, Mob mob, long l) {
        return mob.isBaby() && this.targetBed != null && this.isBed(serverLevel, this.targetBed) && !this.tiredOfWalking(serverLevel, mob) && !this.tiredOfJumping(serverLevel, mob);
    }

    @Override
    protected boolean timedOut(long l) {
        return false;
    }

    @Override
    protected void tick(ServerLevel serverLevel, Mob mob, long l) {
        if (!this.onOrOverBed(serverLevel, mob)) {
            --this.remainingTimeToReachBed;
            return;
        }
        if (this.remainingCooldownUntilNextJump > 0) {
            --this.remainingCooldownUntilNextJump;
            return;
        }
        if (this.onBedSurface(serverLevel, mob)) {
            mob.getJumpControl().jump();
            --this.remainingJumps;
            this.remainingCooldownUntilNextJump = 5;
        }
    }

    private void startWalkingTowardsBed(Mob mob, BlockPos blockPos) {
        mob.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(blockPos, this.speedModifier, 0));
    }

    private boolean nearBed(ServerLevel serverLevel, Mob mob) {
        return this.onOrOverBed(serverLevel, mob) || this.getNearestBed(mob).isPresent();
    }

    private boolean onOrOverBed(ServerLevel serverLevel, Mob mob) {
        BlockPos blockPos = mob.blockPosition();
        BlockPos blockPos2 = blockPos.below();
        return this.isBed(serverLevel, blockPos) || this.isBed(serverLevel, blockPos2);
    }

    private boolean onBedSurface(ServerLevel serverLevel, Mob mob) {
        return this.isBed(serverLevel, mob.blockPosition());
    }

    private boolean isBed(ServerLevel serverLevel, BlockPos blockPos) {
        return serverLevel.getBlockState(blockPos).is(BlockTags.BEDS);
    }

    private Optional<BlockPos> getNearestBed(Mob mob) {
        return mob.getBrain().getMemory(MemoryModuleType.NEAREST_BED);
    }

    private boolean tiredOfWalking(ServerLevel serverLevel, Mob mob) {
        return !this.onOrOverBed(serverLevel, mob) && this.remainingTimeToReachBed <= 0;
    }

    private boolean tiredOfJumping(ServerLevel serverLevel, Mob mob) {
        return this.onOrOverBed(serverLevel, mob) && this.remainingJumps <= 0;
    }

    @Override
    protected /* synthetic */ boolean canStillUse(ServerLevel serverLevel, LivingEntity livingEntity, long l) {
        return this.canStillUse(serverLevel, (Mob)livingEntity, l);
    }

    @Override
    protected /* synthetic */ void stop(ServerLevel serverLevel, LivingEntity livingEntity, long l) {
        this.stop(serverLevel, (Mob)livingEntity, l);
    }
}

