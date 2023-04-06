/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.ai.targeting;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.sensing.Sensing;

public class TargetingConditions {
    public static final TargetingConditions DEFAULT = new TargetingConditions();
    private double range = -1.0;
    private boolean allowInvulnerable;
    private boolean allowSameTeam;
    private boolean allowUnseeable;
    private boolean allowNonAttackable;
    private boolean testInvisible = true;
    private Predicate<LivingEntity> selector;

    public TargetingConditions range(double d) {
        this.range = d;
        return this;
    }

    public TargetingConditions allowInvulnerable() {
        this.allowInvulnerable = true;
        return this;
    }

    public TargetingConditions allowSameTeam() {
        this.allowSameTeam = true;
        return this;
    }

    public TargetingConditions allowUnseeable() {
        this.allowUnseeable = true;
        return this;
    }

    public TargetingConditions allowNonAttackable() {
        this.allowNonAttackable = true;
        return this;
    }

    public TargetingConditions ignoreInvisibilityTesting() {
        this.testInvisible = false;
        return this;
    }

    public TargetingConditions selector(@Nullable Predicate<LivingEntity> predicate) {
        this.selector = predicate;
        return this;
    }

    public boolean test(@Nullable LivingEntity livingEntity, LivingEntity livingEntity2) {
        if (livingEntity == livingEntity2) {
            return false;
        }
        if (livingEntity2.isSpectator()) {
            return false;
        }
        if (!livingEntity2.isAlive()) {
            return false;
        }
        if (!this.allowInvulnerable && livingEntity2.isInvulnerable()) {
            return false;
        }
        if (this.selector != null && !this.selector.test(livingEntity2)) {
            return false;
        }
        if (livingEntity != null) {
            if (!this.allowNonAttackable) {
                if (!livingEntity.canAttack(livingEntity2)) {
                    return false;
                }
                if (!livingEntity.canAttackType(livingEntity2.getType())) {
                    return false;
                }
            }
            if (!this.allowSameTeam && livingEntity.isAlliedTo(livingEntity2)) {
                return false;
            }
            if (this.range > 0.0) {
                double d = this.testInvisible ? livingEntity2.getVisibilityPercent(livingEntity) : 1.0;
                double d2 = Math.max(this.range * d, 2.0);
                double d3 = livingEntity.distanceToSqr(livingEntity2.getX(), livingEntity2.getY(), livingEntity2.getZ());
                if (d3 > d2 * d2) {
                    return false;
                }
            }
            if (!this.allowUnseeable && livingEntity instanceof Mob && !((Mob)livingEntity).getSensing().canSee(livingEntity2)) {
                return false;
            }
        }
        return true;
    }
}

