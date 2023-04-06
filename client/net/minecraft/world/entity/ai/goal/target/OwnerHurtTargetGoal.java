/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.ai.goal.target;

import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public class OwnerHurtTargetGoal
extends TargetGoal {
    private final TamableAnimal tameAnimal;
    private LivingEntity ownerLastHurt;
    private int timestamp;

    public OwnerHurtTargetGoal(TamableAnimal tamableAnimal) {
        super(tamableAnimal, false);
        this.tameAnimal = tamableAnimal;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if (!this.tameAnimal.isTame() || this.tameAnimal.isOrderedToSit()) {
            return false;
        }
        LivingEntity livingEntity = this.tameAnimal.getOwner();
        if (livingEntity == null) {
            return false;
        }
        this.ownerLastHurt = livingEntity.getLastHurtMob();
        int n = livingEntity.getLastHurtMobTimestamp();
        return n != this.timestamp && this.canAttack(this.ownerLastHurt, TargetingConditions.DEFAULT) && this.tameAnimal.wantsToAttack(this.ownerLastHurt, livingEntity);
    }

    @Override
    public void start() {
        this.mob.setTarget(this.ownerLastHurt);
        LivingEntity livingEntity = this.tameAnimal.getOwner();
        if (livingEntity != null) {
            this.timestamp = livingEntity.getLastHurtMobTimestamp();
        }
        super.start();
    }
}

