/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.ai.goal.target;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.sensing.Sensing;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.scores.Team;

public abstract class TargetGoal
extends Goal {
    protected final Mob mob;
    protected final boolean mustSee;
    private final boolean mustReach;
    private int reachCache;
    private int reachCacheTime;
    private int unseenTicks;
    protected LivingEntity targetMob;
    protected int unseenMemoryTicks = 60;

    public TargetGoal(Mob mob, boolean bl) {
        this(mob, bl, false);
    }

    public TargetGoal(Mob mob, boolean bl, boolean bl2) {
        this.mob = mob;
        this.mustSee = bl;
        this.mustReach = bl2;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity == null) {
            livingEntity = this.targetMob;
        }
        if (livingEntity == null) {
            return false;
        }
        if (!livingEntity.isAlive()) {
            return false;
        }
        Team team = this.mob.getTeam();
        Team team2 = livingEntity.getTeam();
        if (team != null && team2 == team) {
            return false;
        }
        double d = this.getFollowDistance();
        if (this.mob.distanceToSqr(livingEntity) > d * d) {
            return false;
        }
        if (this.mustSee) {
            if (this.mob.getSensing().canSee(livingEntity)) {
                this.unseenTicks = 0;
            } else if (++this.unseenTicks > this.unseenMemoryTicks) {
                return false;
            }
        }
        if (livingEntity instanceof Player && ((Player)livingEntity).abilities.invulnerable) {
            return false;
        }
        this.mob.setTarget(livingEntity);
        return true;
    }

    protected double getFollowDistance() {
        return this.mob.getAttributeValue(Attributes.FOLLOW_RANGE);
    }

    @Override
    public void start() {
        this.reachCache = 0;
        this.reachCacheTime = 0;
        this.unseenTicks = 0;
    }

    @Override
    public void stop() {
        this.mob.setTarget(null);
        this.targetMob = null;
    }

    protected boolean canAttack(@Nullable LivingEntity livingEntity, TargetingConditions targetingConditions) {
        if (livingEntity == null) {
            return false;
        }
        if (!targetingConditions.test(this.mob, livingEntity)) {
            return false;
        }
        if (!this.mob.isWithinRestriction(livingEntity.blockPosition())) {
            return false;
        }
        if (this.mustReach) {
            if (--this.reachCacheTime <= 0) {
                this.reachCache = 0;
            }
            if (this.reachCache == 0) {
                int n = this.reachCache = this.canReach(livingEntity) ? 1 : 2;
            }
            if (this.reachCache == 2) {
                return false;
            }
        }
        return true;
    }

    private boolean canReach(LivingEntity livingEntity) {
        int n;
        this.reachCacheTime = 10 + this.mob.getRandom().nextInt(5);
        Path path = this.mob.getNavigation().createPath(livingEntity, 0);
        if (path == null) {
            return false;
        }
        Node node = path.getEndNode();
        if (node == null) {
            return false;
        }
        int n2 = node.x - Mth.floor(livingEntity.getX());
        return (double)(n2 * n2 + (n = node.z - Mth.floor(livingEntity.getZ())) * n) <= 2.25;
    }

    public TargetGoal setUnseenMemoryTicks(int n) {
        this.unseenMemoryTicks = n;
        return this;
    }
}

