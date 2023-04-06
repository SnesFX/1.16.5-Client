/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class BreedGoal
extends Goal {
    private static final TargetingConditions PARTNER_TARGETING = new TargetingConditions().range(8.0).allowInvulnerable().allowSameTeam().allowUnseeable();
    protected final Animal animal;
    private final Class<? extends Animal> partnerClass;
    protected final Level level;
    protected Animal partner;
    private int loveTime;
    private final double speedModifier;

    public BreedGoal(Animal animal, double d) {
        this(animal, d, animal.getClass());
    }

    public BreedGoal(Animal animal, double d, Class<? extends Animal> class_) {
        this.animal = animal;
        this.level = animal.level;
        this.partnerClass = class_;
        this.speedModifier = d;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!this.animal.isInLove()) {
            return false;
        }
        this.partner = this.getFreePartner();
        return this.partner != null;
    }

    @Override
    public boolean canContinueToUse() {
        return this.partner.isAlive() && this.partner.isInLove() && this.loveTime < 60;
    }

    @Override
    public void stop() {
        this.partner = null;
        this.loveTime = 0;
    }

    @Override
    public void tick() {
        this.animal.getLookControl().setLookAt(this.partner, 10.0f, this.animal.getMaxHeadXRot());
        this.animal.getNavigation().moveTo(this.partner, this.speedModifier);
        ++this.loveTime;
        if (this.loveTime >= 60 && this.animal.distanceToSqr(this.partner) < 9.0) {
            this.breed();
        }
    }

    @Nullable
    private Animal getFreePartner() {
        List<? extends Animal> list = this.level.getNearbyEntities(this.partnerClass, PARTNER_TARGETING, this.animal, this.animal.getBoundingBox().inflate(8.0));
        double d = Double.MAX_VALUE;
        Animal animal = null;
        for (Animal animal2 : list) {
            if (!this.animal.canMate(animal2) || !(this.animal.distanceToSqr(animal2) < d)) continue;
            animal = animal2;
            d = this.animal.distanceToSqr(animal2);
        }
        return animal;
    }

    protected void breed() {
        this.animal.spawnChildFromBreeding((ServerLevel)this.level, this.partner);
    }
}

