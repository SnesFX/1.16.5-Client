/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

public class OcelotAttackGoal
extends Goal {
    private final BlockGetter level;
    private final Mob mob;
    private LivingEntity target;
    private int attackTime;

    public OcelotAttackGoal(Mob mob) {
        this.mob = mob;
        this.level = mob.level;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity == null) {
            return false;
        }
        this.target = livingEntity;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (!this.target.isAlive()) {
            return false;
        }
        if (this.mob.distanceToSqr(this.target) > 225.0) {
            return false;
        }
        return !this.mob.getNavigation().isDone() || this.canUse();
    }

    @Override
    public void stop() {
        this.target = null;
        this.mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        this.mob.getLookControl().setLookAt(this.target, 30.0f, 30.0f);
        double d = this.mob.getBbWidth() * 2.0f * (this.mob.getBbWidth() * 2.0f);
        double d2 = this.mob.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
        double d3 = 0.8;
        if (d2 > d && d2 < 16.0) {
            d3 = 1.33;
        } else if (d2 < 225.0) {
            d3 = 0.6;
        }
        this.mob.getNavigation().moveTo(this.target, d3);
        this.attackTime = Math.max(this.attackTime - 1, 0);
        if (d2 > d) {
            return;
        }
        if (this.attackTime > 0) {
            return;
        }
        this.attackTime = 20;
        this.mob.doHurtTarget(this.target);
    }
}

