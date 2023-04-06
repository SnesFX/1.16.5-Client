/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.ai.control;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;

public class FlyingMoveControl
extends MoveControl {
    private final int maxTurn;
    private final boolean hoversInPlace;

    public FlyingMoveControl(Mob mob, int n, boolean bl) {
        super(mob);
        this.maxTurn = n;
        this.hoversInPlace = bl;
    }

    @Override
    public void tick() {
        if (this.operation == MoveControl.Operation.MOVE_TO) {
            this.operation = MoveControl.Operation.WAIT;
            this.mob.setNoGravity(true);
            double d = this.wantedX - this.mob.getX();
            double d2 = this.wantedY - this.mob.getY();
            double d3 = this.wantedZ - this.mob.getZ();
            double d4 = d * d + d2 * d2 + d3 * d3;
            if (d4 < 2.500000277905201E-7) {
                this.mob.setYya(0.0f);
                this.mob.setZza(0.0f);
                return;
            }
            float f = (float)(Mth.atan2(d3, d) * 57.2957763671875) - 90.0f;
            this.mob.yRot = this.rotlerp(this.mob.yRot, f, 90.0f);
            float f2 = this.mob.isOnGround() ? (float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)) : (float)(this.speedModifier * this.mob.getAttributeValue(Attributes.FLYING_SPEED));
            this.mob.setSpeed(f2);
            double d5 = Mth.sqrt(d * d + d3 * d3);
            float f3 = (float)(-(Mth.atan2(d2, d5) * 57.2957763671875));
            this.mob.xRot = this.rotlerp(this.mob.xRot, f3, this.maxTurn);
            this.mob.setYya(d2 > 0.0 ? f2 : -f2);
        } else {
            if (!this.hoversInPlace) {
                this.mob.setNoGravity(false);
            }
            this.mob.setYya(0.0f);
            this.mob.setZza(0.0f);
        }
    }
}

