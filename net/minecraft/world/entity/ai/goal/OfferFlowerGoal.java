/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.Random;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class OfferFlowerGoal
extends Goal {
    private static final TargetingConditions OFFER_TARGER_CONTEXT = new TargetingConditions().range(6.0).allowSameTeam().allowInvulnerable();
    private final IronGolem golem;
    private Villager villager;
    private int tick;

    public OfferFlowerGoal(IronGolem ironGolem) {
        this.golem = ironGolem;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!this.golem.level.isDay()) {
            return false;
        }
        if (this.golem.getRandom().nextInt(8000) != 0) {
            return false;
        }
        this.villager = this.golem.level.getNearestEntity(Villager.class, OFFER_TARGER_CONTEXT, this.golem, this.golem.getX(), this.golem.getY(), this.golem.getZ(), this.golem.getBoundingBox().inflate(6.0, 2.0, 6.0));
        return this.villager != null;
    }

    @Override
    public boolean canContinueToUse() {
        return this.tick > 0;
    }

    @Override
    public void start() {
        this.tick = 400;
        this.golem.offerFlower(true);
    }

    @Override
    public void stop() {
        this.golem.offerFlower(false);
        this.villager = null;
    }

    @Override
    public void tick() {
        this.golem.getLookControl().setLookAt(this.villager, 30.0f, 30.0f);
        --this.tick;
    }
}

