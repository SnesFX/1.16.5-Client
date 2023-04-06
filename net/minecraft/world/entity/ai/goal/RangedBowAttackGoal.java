/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.Random;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.sensing.Sensing;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class RangedBowAttackGoal<T extends Monster>
extends Goal {
    private final T mob;
    private final double speedModifier;
    private int attackIntervalMin;
    private final float attackRadiusSqr;
    private int attackTime = -1;
    private int seeTime;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;

    public RangedBowAttackGoal(T t, double d, int n, float f) {
        this.mob = t;
        this.speedModifier = d;
        this.attackIntervalMin = n;
        this.attackRadiusSqr = f * f;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    public void setMinAttackInterval(int n) {
        this.attackIntervalMin = n;
    }

    @Override
    public boolean canUse() {
        if (((Mob)this.mob).getTarget() == null) {
            return false;
        }
        return this.isHoldingBow();
    }

    protected boolean isHoldingBow() {
        return ((LivingEntity)this.mob).isHolding(Items.BOW);
    }

    @Override
    public boolean canContinueToUse() {
        return (this.canUse() || !((Mob)this.mob).getNavigation().isDone()) && this.isHoldingBow();
    }

    @Override
    public void start() {
        super.start();
        ((Mob)this.mob).setAggressive(true);
    }

    @Override
    public void stop() {
        super.stop();
        ((Mob)this.mob).setAggressive(false);
        this.seeTime = 0;
        this.attackTime = -1;
        ((LivingEntity)this.mob).stopUsingItem();
    }

    @Override
    public void tick() {
        boolean bl;
        LivingEntity livingEntity = ((Mob)this.mob).getTarget();
        if (livingEntity == null) {
            return;
        }
        double d = ((Entity)this.mob).distanceToSqr(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
        boolean bl2 = ((Mob)this.mob).getSensing().canSee(livingEntity);
        boolean bl3 = bl = this.seeTime > 0;
        if (bl2 != bl) {
            this.seeTime = 0;
        }
        this.seeTime = bl2 ? ++this.seeTime : --this.seeTime;
        if (d > (double)this.attackRadiusSqr || this.seeTime < 20) {
            ((Mob)this.mob).getNavigation().moveTo(livingEntity, this.speedModifier);
            this.strafingTime = -1;
        } else {
            ((Mob)this.mob).getNavigation().stop();
            ++this.strafingTime;
        }
        if (this.strafingTime >= 20) {
            if ((double)((LivingEntity)this.mob).getRandom().nextFloat() < 0.3) {
                boolean bl4 = this.strafingClockwise = !this.strafingClockwise;
            }
            if ((double)((LivingEntity)this.mob).getRandom().nextFloat() < 0.3) {
                this.strafingBackwards = !this.strafingBackwards;
            }
            this.strafingTime = 0;
        }
        if (this.strafingTime > -1) {
            if (d > (double)(this.attackRadiusSqr * 0.75f)) {
                this.strafingBackwards = false;
            } else if (d < (double)(this.attackRadiusSqr * 0.25f)) {
                this.strafingBackwards = true;
            }
            ((Mob)this.mob).getMoveControl().strafe(this.strafingBackwards ? -0.5f : 0.5f, this.strafingClockwise ? 0.5f : -0.5f);
            ((Mob)this.mob).lookAt(livingEntity, 30.0f, 30.0f);
        } else {
            ((Mob)this.mob).getLookControl().setLookAt(livingEntity, 30.0f, 30.0f);
        }
        if (((LivingEntity)this.mob).isUsingItem()) {
            int n;
            if (!bl2 && this.seeTime < -60) {
                ((LivingEntity)this.mob).stopUsingItem();
            } else if (bl2 && (n = ((LivingEntity)this.mob).getTicksUsingItem()) >= 20) {
                ((LivingEntity)this.mob).stopUsingItem();
                ((RangedAttackMob)this.mob).performRangedAttack(livingEntity, BowItem.getPowerForTime(n));
                this.attackTime = this.attackIntervalMin;
            }
        } else if (--this.attackTime <= 0 && this.seeTime >= -60) {
            ((LivingEntity)this.mob).startUsingItem(ProjectileUtil.getWeaponHoldingHand(this.mob, Items.BOW));
        }
    }
}

