/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.ai.control;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;

public class DolphinLookControl
extends LookControl {
    private final int maxYRotFromCenter;

    public DolphinLookControl(Mob mob, int n) {
        super(mob);
        this.maxYRotFromCenter = n;
    }

    @Override
    public void tick() {
        if (this.hasWanted) {
            this.hasWanted = false;
            this.mob.yHeadRot = this.rotateTowards(this.mob.yHeadRot, this.getYRotD() + 20.0f, this.yMaxRotSpeed);
            this.mob.xRot = this.rotateTowards(this.mob.xRot, this.getXRotD() + 10.0f, this.xMaxRotAngle);
        } else {
            if (this.mob.getNavigation().isDone()) {
                this.mob.xRot = this.rotateTowards(this.mob.xRot, 0.0f, 5.0f);
            }
            this.mob.yHeadRot = this.rotateTowards(this.mob.yHeadRot, this.mob.yBodyRot, this.yMaxRotSpeed);
        }
        float f = Mth.wrapDegrees(this.mob.yHeadRot - this.mob.yBodyRot);
        if (f < (float)(-this.maxYRotFromCenter)) {
            this.mob.yBodyRot -= 4.0f;
        } else if (f > (float)this.maxYRotFromCenter) {
            this.mob.yBodyRot += 4.0f;
        }
    }
}

