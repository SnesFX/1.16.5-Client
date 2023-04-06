/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class LookAtPlayerGoal
extends Goal {
    protected final Mob mob;
    protected Entity lookAt;
    protected final float lookDistance;
    private int lookTime;
    protected final float probability;
    protected final Class<? extends LivingEntity> lookAtType;
    protected final TargetingConditions lookAtContext;

    public LookAtPlayerGoal(Mob mob, Class<? extends LivingEntity> class_, float f) {
        this(mob, class_, f, 0.02f);
    }

    public LookAtPlayerGoal(Mob mob, Class<? extends LivingEntity> class_, float f, float f2) {
        this.mob = mob;
        this.lookAtType = class_;
        this.lookDistance = f;
        this.probability = f2;
        this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        this.lookAtContext = class_ == Player.class ? new TargetingConditions().range(f).allowSameTeam().allowInvulnerable().allowNonAttackable().selector(livingEntity -> EntitySelector.notRiding(mob).test((Entity)livingEntity)) : new TargetingConditions().range(f).allowSameTeam().allowInvulnerable().allowNonAttackable();
    }

    @Override
    public boolean canUse() {
        if (this.mob.getRandom().nextFloat() >= this.probability) {
            return false;
        }
        if (this.mob.getTarget() != null) {
            this.lookAt = this.mob.getTarget();
        }
        this.lookAt = this.lookAtType == Player.class ? this.mob.level.getNearestPlayer(this.lookAtContext, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ()) : this.mob.level.getNearestLoadedEntity(this.lookAtType, this.lookAtContext, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ(), this.mob.getBoundingBox().inflate(this.lookDistance, 3.0, this.lookDistance));
        return this.lookAt != null;
    }

    @Override
    public boolean canContinueToUse() {
        if (!this.lookAt.isAlive()) {
            return false;
        }
        if (this.mob.distanceToSqr(this.lookAt) > (double)(this.lookDistance * this.lookDistance)) {
            return false;
        }
        return this.lookTime > 0;
    }

    @Override
    public void start() {
        this.lookTime = 40 + this.mob.getRandom().nextInt(40);
    }

    @Override
    public void stop() {
        this.lookAt = null;
    }

    @Override
    public void tick() {
        this.mob.getLookControl().setLookAt(this.lookAt.getX(), this.lookAt.getEyeY(), this.lookAt.getZ());
        --this.lookTime;
    }
}

