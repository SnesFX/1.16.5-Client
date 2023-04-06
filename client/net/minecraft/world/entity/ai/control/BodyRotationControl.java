/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.ai.control;

import java.util.List;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

public class BodyRotationControl {
    private final Mob mob;
    private int headStableTime;
    private float lastStableYHeadRot;

    public BodyRotationControl(Mob mob) {
        this.mob = mob;
    }

    public void clientTick() {
        if (this.isMoving()) {
            this.mob.yBodyRot = this.mob.yRot;
            this.rotateHeadIfNecessary();
            this.lastStableYHeadRot = this.mob.yHeadRot;
            this.headStableTime = 0;
            return;
        }
        if (this.notCarryingMobPassengers()) {
            if (Math.abs(this.mob.yHeadRot - this.lastStableYHeadRot) > 15.0f) {
                this.headStableTime = 0;
                this.lastStableYHeadRot = this.mob.yHeadRot;
                this.rotateBodyIfNecessary();
            } else {
                ++this.headStableTime;
                if (this.headStableTime > 10) {
                    this.rotateHeadTowardsFront();
                }
            }
        }
    }

    private void rotateBodyIfNecessary() {
        this.mob.yBodyRot = Mth.rotateIfNecessary(this.mob.yBodyRot, this.mob.yHeadRot, this.mob.getMaxHeadYRot());
    }

    private void rotateHeadIfNecessary() {
        this.mob.yHeadRot = Mth.rotateIfNecessary(this.mob.yHeadRot, this.mob.yBodyRot, this.mob.getMaxHeadYRot());
    }

    private void rotateHeadTowardsFront() {
        int n = this.headStableTime - 10;
        float f = Mth.clamp((float)n / 10.0f, 0.0f, 1.0f);
        float f2 = (float)this.mob.getMaxHeadYRot() * (1.0f - f);
        this.mob.yBodyRot = Mth.rotateIfNecessary(this.mob.yBodyRot, this.mob.yHeadRot, f2);
    }

    private boolean notCarryingMobPassengers() {
        return this.mob.getPassengers().isEmpty() || !(this.mob.getPassengers().get(0) instanceof Mob);
    }

    private boolean isMoving() {
        double d;
        double d2 = this.mob.getX() - this.mob.xo;
        return d2 * d2 + (d = this.mob.getZ() - this.mob.zo) * d > 2.500000277905201E-7;
    }
}

