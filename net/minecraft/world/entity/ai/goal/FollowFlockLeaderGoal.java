/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.ai.goal;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.AbstractSchoolingFish;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class FollowFlockLeaderGoal
extends Goal {
    private final AbstractSchoolingFish mob;
    private int timeToRecalcPath;
    private int nextStartTick;

    public FollowFlockLeaderGoal(AbstractSchoolingFish abstractSchoolingFish) {
        this.mob = abstractSchoolingFish;
        this.nextStartTick = this.nextStartTick(abstractSchoolingFish);
    }

    protected int nextStartTick(AbstractSchoolingFish abstractSchoolingFish) {
        return 200 + abstractSchoolingFish.getRandom().nextInt(200) % 20;
    }

    @Override
    public boolean canUse() {
        if (this.mob.hasFollowers()) {
            return false;
        }
        if (this.mob.isFollower()) {
            return true;
        }
        if (this.nextStartTick > 0) {
            --this.nextStartTick;
            return false;
        }
        this.nextStartTick = this.nextStartTick(this.mob);
        Predicate<AbstractSchoolingFish> predicate = abstractSchoolingFish -> abstractSchoolingFish.canBeFollowed() || !abstractSchoolingFish.isFollower();
        List<AbstractSchoolingFish> list = this.mob.level.getEntitiesOfClass(this.mob.getClass(), this.mob.getBoundingBox().inflate(8.0, 8.0, 8.0), predicate);
        AbstractSchoolingFish abstractSchoolingFish2 = list.stream().filter(AbstractSchoolingFish::canBeFollowed).findAny().orElse(this.mob);
        abstractSchoolingFish2.addFollowers(list.stream().filter(abstractSchoolingFish -> !abstractSchoolingFish.isFollower()));
        return this.mob.isFollower();
    }

    @Override
    public boolean canContinueToUse() {
        return this.mob.isFollower() && this.mob.inRangeOfLeader();
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
    }

    @Override
    public void stop() {
        this.mob.stopFollowing();
    }

    @Override
    public void tick() {
        if (--this.timeToRecalcPath > 0) {
            return;
        }
        this.timeToRecalcPath = 10;
        this.mob.pathToLeader();
    }
}

