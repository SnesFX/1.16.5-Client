/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;

public class FollowMobGoal
extends Goal {
    private final Mob mob;
    private final Predicate<Mob> followPredicate;
    private Mob followingMob;
    private final double speedModifier;
    private final PathNavigation navigation;
    private int timeToRecalcPath;
    private final float stopDistance;
    private float oldWaterCost;
    private final float areaSize;

    public FollowMobGoal(Mob mob, double d, float f, float f2) {
        this.mob = mob;
        this.followPredicate = mob2 -> mob2 != null && mob.getClass() != mob2.getClass();
        this.speedModifier = d;
        this.navigation = mob.getNavigation();
        this.stopDistance = f;
        this.areaSize = f2;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        if (!(mob.getNavigation() instanceof GroundPathNavigation) && !(mob.getNavigation() instanceof FlyingPathNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowMobGoal");
        }
    }

    @Override
    public boolean canUse() {
        List<Mob> list = this.mob.level.getEntitiesOfClass(Mob.class, this.mob.getBoundingBox().inflate(this.areaSize), this.followPredicate);
        if (!list.isEmpty()) {
            for (Mob mob : list) {
                if (mob.isInvisible()) continue;
                this.followingMob = mob;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return this.followingMob != null && !this.navigation.isDone() && this.mob.distanceToSqr(this.followingMob) > (double)(this.stopDistance * this.stopDistance);
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.mob.getPathfindingMalus(BlockPathTypes.WATER);
        this.mob.setPathfindingMalus(BlockPathTypes.WATER, 0.0f);
    }

    @Override
    public void stop() {
        this.followingMob = null;
        this.navigation.stop();
        this.mob.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
    }

    @Override
    public void tick() {
        double d;
        double d2;
        if (this.followingMob == null || this.mob.isLeashed()) {
            return;
        }
        this.mob.getLookControl().setLookAt(this.followingMob, 10.0f, this.mob.getMaxHeadXRot());
        if (--this.timeToRecalcPath > 0) {
            return;
        }
        this.timeToRecalcPath = 10;
        double d3 = this.mob.getX() - this.followingMob.getX();
        double d4 = d3 * d3 + (d = this.mob.getY() - this.followingMob.getY()) * d + (d2 = this.mob.getZ() - this.followingMob.getZ()) * d2;
        if (d4 <= (double)(this.stopDistance * this.stopDistance)) {
            this.navigation.stop();
            LookControl lookControl = this.followingMob.getLookControl();
            if (d4 <= (double)this.stopDistance || lookControl.getWantedX() == this.mob.getX() && lookControl.getWantedY() == this.mob.getY() && lookControl.getWantedZ() == this.mob.getZ()) {
                double d5 = this.followingMob.getX() - this.mob.getX();
                double d6 = this.followingMob.getZ() - this.mob.getZ();
                this.navigation.moveTo(this.mob.getX() - d5, this.mob.getY(), this.mob.getZ() - d6, this.speedModifier);
            }
            return;
        }
        this.navigation.moveTo(this.followingMob, this.speedModifier);
    }
}

