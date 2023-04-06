/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;

public class FollowOwnerGoal
extends Goal {
    private final TamableAnimal tamable;
    private LivingEntity owner;
    private final LevelReader level;
    private final double speedModifier;
    private final PathNavigation navigation;
    private int timeToRecalcPath;
    private final float stopDistance;
    private final float startDistance;
    private float oldWaterCost;
    private final boolean canFly;

    public FollowOwnerGoal(TamableAnimal tamableAnimal, double d, float f, float f2, boolean bl) {
        this.tamable = tamableAnimal;
        this.level = tamableAnimal.level;
        this.speedModifier = d;
        this.navigation = tamableAnimal.getNavigation();
        this.startDistance = f;
        this.stopDistance = f2;
        this.canFly = bl;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        if (!(tamableAnimal.getNavigation() instanceof GroundPathNavigation) && !(tamableAnimal.getNavigation() instanceof FlyingPathNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
    }

    @Override
    public boolean canUse() {
        LivingEntity livingEntity = this.tamable.getOwner();
        if (livingEntity == null) {
            return false;
        }
        if (livingEntity.isSpectator()) {
            return false;
        }
        if (this.tamable.isOrderedToSit()) {
            return false;
        }
        if (this.tamable.distanceToSqr(livingEntity) < (double)(this.startDistance * this.startDistance)) {
            return false;
        }
        this.owner = livingEntity;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.navigation.isDone()) {
            return false;
        }
        if (this.tamable.isOrderedToSit()) {
            return false;
        }
        return !(this.tamable.distanceToSqr(this.owner) <= (double)(this.stopDistance * this.stopDistance));
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.tamable.getPathfindingMalus(BlockPathTypes.WATER);
        this.tamable.setPathfindingMalus(BlockPathTypes.WATER, 0.0f);
    }

    @Override
    public void stop() {
        this.owner = null;
        this.navigation.stop();
        this.tamable.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
    }

    @Override
    public void tick() {
        this.tamable.getLookControl().setLookAt(this.owner, 10.0f, this.tamable.getMaxHeadXRot());
        if (--this.timeToRecalcPath > 0) {
            return;
        }
        this.timeToRecalcPath = 10;
        if (this.tamable.isLeashed() || this.tamable.isPassenger()) {
            return;
        }
        if (this.tamable.distanceToSqr(this.owner) >= 144.0) {
            this.teleportToOwner();
        } else {
            this.navigation.moveTo(this.owner, this.speedModifier);
        }
    }

    private void teleportToOwner() {
        BlockPos blockPos = this.owner.blockPosition();
        for (int i = 0; i < 10; ++i) {
            int n = this.randomIntInclusive(-3, 3);
            int n2 = this.randomIntInclusive(-1, 1);
            int n3 = this.randomIntInclusive(-3, 3);
            boolean bl = this.maybeTeleportTo(blockPos.getX() + n, blockPos.getY() + n2, blockPos.getZ() + n3);
            if (!bl) continue;
            return;
        }
    }

    private boolean maybeTeleportTo(int n, int n2, int n3) {
        if (Math.abs((double)n - this.owner.getX()) < 2.0 && Math.abs((double)n3 - this.owner.getZ()) < 2.0) {
            return false;
        }
        if (!this.canTeleportTo(new BlockPos(n, n2, n3))) {
            return false;
        }
        this.tamable.moveTo((double)n + 0.5, n2, (double)n3 + 0.5, this.tamable.yRot, this.tamable.xRot);
        this.navigation.stop();
        return true;
    }

    private boolean canTeleportTo(BlockPos blockPos) {
        BlockPathTypes blockPathTypes = WalkNodeEvaluator.getBlockPathTypeStatic(this.level, blockPos.mutable());
        if (blockPathTypes != BlockPathTypes.WALKABLE) {
            return false;
        }
        BlockState blockState = this.level.getBlockState(blockPos.below());
        if (!this.canFly && blockState.getBlock() instanceof LeavesBlock) {
            return false;
        }
        BlockPos blockPos2 = blockPos.subtract(this.tamable.blockPosition());
        return this.level.noCollision(this.tamable, this.tamable.getBoundingBox().move(blockPos2));
    }

    private int randomIntInclusive(int n, int n2) {
        return this.tamable.getRandom().nextInt(n2 - n + 1) + n;
    }
}

