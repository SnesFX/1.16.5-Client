/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.animal.horse.TraderLlama;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class LlamaFollowCaravanGoal
extends Goal {
    public final Llama llama;
    private double speedModifier;
    private int distCheckCounter;

    public LlamaFollowCaravanGoal(Llama llama, double d) {
        this.llama = llama;
        this.speedModifier = d;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        double d;
        Llama llama;
        if (this.llama.isLeashed() || this.llama.inCaravan()) {
            return false;
        }
        List<Entity> list = this.llama.level.getEntities(this.llama, this.llama.getBoundingBox().inflate(9.0, 4.0, 9.0), entity -> {
            EntityType<?> entityType = entity.getType();
            return entityType == EntityType.LLAMA || entityType == EntityType.TRADER_LLAMA;
        });
        Mob mob = null;
        double d2 = Double.MAX_VALUE;
        for (Entity entity2 : list) {
            llama = (Llama)entity2;
            if (!llama.inCaravan() || llama.hasCaravanTail() || (d = this.llama.distanceToSqr(llama)) > d2) continue;
            d2 = d;
            mob = llama;
        }
        if (mob == null) {
            for (Entity entity2 : list) {
                llama = (Llama)entity2;
                if (!llama.isLeashed() || llama.hasCaravanTail() || (d = this.llama.distanceToSqr(llama)) > d2) continue;
                d2 = d;
                mob = llama;
            }
        }
        if (mob == null) {
            return false;
        }
        if (d2 < 4.0) {
            return false;
        }
        if (!mob.isLeashed() && !this.firstIsLeashed((Llama)mob, 1)) {
            return false;
        }
        this.llama.joinCaravan((Llama)mob);
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (!(this.llama.inCaravan() && this.llama.getCaravanHead().isAlive() && this.firstIsLeashed(this.llama, 0))) {
            return false;
        }
        double d = this.llama.distanceToSqr(this.llama.getCaravanHead());
        if (d > 676.0) {
            if (this.speedModifier <= 3.0) {
                this.speedModifier *= 1.2;
                this.distCheckCounter = 40;
                return true;
            }
            if (this.distCheckCounter == 0) {
                return false;
            }
        }
        if (this.distCheckCounter > 0) {
            --this.distCheckCounter;
        }
        return true;
    }

    @Override
    public void stop() {
        this.llama.leaveCaravan();
        this.speedModifier = 2.1;
    }

    @Override
    public void tick() {
        if (!this.llama.inCaravan()) {
            return;
        }
        if (this.llama.getLeashHolder() instanceof LeashFenceKnotEntity) {
            return;
        }
        Llama llama = this.llama.getCaravanHead();
        double d = this.llama.distanceTo(llama);
        float f = 2.0f;
        Vec3 vec3 = new Vec3(llama.getX() - this.llama.getX(), llama.getY() - this.llama.getY(), llama.getZ() - this.llama.getZ()).normalize().scale(Math.max(d - 2.0, 0.0));
        this.llama.getNavigation().moveTo(this.llama.getX() + vec3.x, this.llama.getY() + vec3.y, this.llama.getZ() + vec3.z, this.speedModifier);
    }

    private boolean firstIsLeashed(Llama llama, int n) {
        if (n > 8) {
            return false;
        }
        if (llama.inCaravan()) {
            if (llama.getCaravanHead().isLeashed()) {
                return true;
            }
            return this.firstIsLeashed(llama.getCaravanHead(), ++n);
        }
        return false;
    }
}

