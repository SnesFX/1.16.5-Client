/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.Vec3;

public abstract class PathfinderMob
extends Mob {
    protected PathfinderMob(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    public float getWalkTargetValue(BlockPos blockPos) {
        return this.getWalkTargetValue(blockPos, this.level);
    }

    public float getWalkTargetValue(BlockPos blockPos, LevelReader levelReader) {
        return 0.0f;
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor levelAccessor, MobSpawnType mobSpawnType) {
        return this.getWalkTargetValue(this.blockPosition(), levelAccessor) >= 0.0f;
    }

    public boolean isPathFinding() {
        return !this.getNavigation().isDone();
    }

    @Override
    protected void tickLeash() {
        super.tickLeash();
        Entity entity = this.getLeashHolder();
        if (entity != null && entity.level == this.level) {
            this.restrictTo(entity.blockPosition(), 5);
            float f = this.distanceTo(entity);
            if (this instanceof TamableAnimal && ((TamableAnimal)this).isInSittingPose()) {
                if (f > 10.0f) {
                    this.dropLeash(true, true);
                }
                return;
            }
            this.onLeashDistance(f);
            if (f > 10.0f) {
                this.dropLeash(true, true);
                this.goalSelector.disableControlFlag(Goal.Flag.MOVE);
            } else if (f > 6.0f) {
                double d = (entity.getX() - this.getX()) / (double)f;
                double d2 = (entity.getY() - this.getY()) / (double)f;
                double d3 = (entity.getZ() - this.getZ()) / (double)f;
                this.setDeltaMovement(this.getDeltaMovement().add(Math.copySign(d * d * 0.4, d), Math.copySign(d2 * d2 * 0.4, d2), Math.copySign(d3 * d3 * 0.4, d3)));
            } else {
                this.goalSelector.enableControlFlag(Goal.Flag.MOVE);
                float f2 = 2.0f;
                Vec3 vec3 = new Vec3(entity.getX() - this.getX(), entity.getY() - this.getY(), entity.getZ() - this.getZ()).normalize().scale(Math.max(f - 2.0f, 0.0f));
                this.getNavigation().moveTo(this.getX() + vec3.x, this.getY() + vec3.y, this.getZ() + vec3.z, this.followLeashSpeed());
            }
        }
    }

    protected double followLeashSpeed() {
        return 1.0;
    }

    protected void onLeashDistance(float f) {
    }
}

