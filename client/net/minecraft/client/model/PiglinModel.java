/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.model;

import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinArmPose;

public class PiglinModel<T extends Mob>
extends PlayerModel<T> {
    public final ModelPart earRight;
    public final ModelPart earLeft;
    private final ModelPart bodyDefault;
    private final ModelPart headDefault;
    private final ModelPart leftArmDefault;
    private final ModelPart rightArmDefault;

    public PiglinModel(float f, int n, int n2) {
        super(f, false);
        this.texWidth = n;
        this.texHeight = n2;
        this.body = new ModelPart(this, 16, 16);
        this.body.addBox(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, f);
        this.head = new ModelPart(this);
        this.head.texOffs(0, 0).addBox(-5.0f, -8.0f, -4.0f, 10.0f, 8.0f, 8.0f, f);
        this.head.texOffs(31, 1).addBox(-2.0f, -4.0f, -5.0f, 4.0f, 4.0f, 1.0f, f);
        this.head.texOffs(2, 4).addBox(2.0f, -2.0f, -5.0f, 1.0f, 2.0f, 1.0f, f);
        this.head.texOffs(2, 0).addBox(-3.0f, -2.0f, -5.0f, 1.0f, 2.0f, 1.0f, f);
        this.earRight = new ModelPart(this);
        this.earRight.setPos(4.5f, -6.0f, 0.0f);
        this.earRight.texOffs(51, 6).addBox(0.0f, 0.0f, -2.0f, 1.0f, 5.0f, 4.0f, f);
        this.head.addChild(this.earRight);
        this.earLeft = new ModelPart(this);
        this.earLeft.setPos(-4.5f, -6.0f, 0.0f);
        this.earLeft.texOffs(39, 6).addBox(-1.0f, 0.0f, -2.0f, 1.0f, 5.0f, 4.0f, f);
        this.head.addChild(this.earLeft);
        this.hat = new ModelPart(this);
        this.bodyDefault = this.body.createShallowCopy();
        this.headDefault = this.head.createShallowCopy();
        this.leftArmDefault = this.leftArm.createShallowCopy();
        this.rightArmDefault = this.leftArm.createShallowCopy();
    }

    @Override
    public void setupAnim(T t, float f, float f2, float f3, float f4, float f5) {
        this.body.copyFrom(this.bodyDefault);
        this.head.copyFrom(this.headDefault);
        this.leftArm.copyFrom(this.leftArmDefault);
        this.rightArm.copyFrom(this.rightArmDefault);
        super.setupAnim(t, f, f2, f3, f4, f5);
        float f6 = 0.5235988f;
        float f7 = f3 * 0.1f + f * 0.5f;
        float f8 = 0.08f + f2 * 0.4f;
        this.earRight.zRot = -0.5235988f - Mth.cos(f7 * 1.2f) * f8;
        this.earLeft.zRot = 0.5235988f + Mth.cos(f7) * f8;
        if (t instanceof AbstractPiglin) {
            AbstractPiglin abstractPiglin = (AbstractPiglin)t;
            PiglinArmPose piglinArmPose = abstractPiglin.getArmPose();
            if (piglinArmPose == PiglinArmPose.DANCING) {
                float f9 = f3 / 60.0f;
                this.earLeft.zRot = 0.5235988f + 0.017453292f * Mth.sin(f9 * 30.0f) * 10.0f;
                this.earRight.zRot = -0.5235988f - 0.017453292f * Mth.cos(f9 * 30.0f) * 10.0f;
                this.head.x = Mth.sin(f9 * 10.0f);
                this.head.y = Mth.sin(f9 * 40.0f) + 0.4f;
                this.rightArm.zRot = 0.017453292f * (70.0f + Mth.cos(f9 * 40.0f) * 10.0f);
                this.leftArm.zRot = this.rightArm.zRot * -1.0f;
                this.rightArm.y = Mth.sin(f9 * 40.0f) * 0.5f + 1.5f;
                this.leftArm.y = Mth.sin(f9 * 40.0f) * 0.5f + 1.5f;
                this.body.y = Mth.sin(f9 * 40.0f) * 0.35f;
            } else if (piglinArmPose == PiglinArmPose.ATTACKING_WITH_MELEE_WEAPON && this.attackTime == 0.0f) {
                this.holdWeaponHigh(t);
            } else if (piglinArmPose == PiglinArmPose.CROSSBOW_HOLD) {
                AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, !((Mob)t).isLeftHanded());
            } else if (piglinArmPose == PiglinArmPose.CROSSBOW_CHARGE) {
                AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, t, !((Mob)t).isLeftHanded());
            } else if (piglinArmPose == PiglinArmPose.ADMIRING_ITEM) {
                this.head.xRot = 0.5f;
                this.head.yRot = 0.0f;
                if (((Mob)t).isLeftHanded()) {
                    this.rightArm.yRot = -0.5f;
                    this.rightArm.xRot = -0.9f;
                } else {
                    this.leftArm.yRot = 0.5f;
                    this.leftArm.xRot = -0.9f;
                }
            }
        } else if (((Entity)t).getType() == EntityType.ZOMBIFIED_PIGLIN) {
            AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, ((Mob)t).isAggressive(), this.attackTime, f3);
        }
        this.leftPants.copyFrom(this.leftLeg);
        this.rightPants.copyFrom(this.rightLeg);
        this.leftSleeve.copyFrom(this.leftArm);
        this.rightSleeve.copyFrom(this.rightArm);
        this.jacket.copyFrom(this.body);
        this.hat.copyFrom(this.head);
    }

    @Override
    protected void setupAttackAnimation(T t, float f) {
        if (this.attackTime > 0.0f && t instanceof Piglin && ((Piglin)t).getArmPose() == PiglinArmPose.ATTACKING_WITH_MELEE_WEAPON) {
            AnimationUtils.swingWeaponDown(this.rightArm, this.leftArm, t, this.attackTime, f);
            return;
        }
        super.setupAttackAnimation(t, f);
    }

    private void holdWeaponHigh(T t) {
        if (((Mob)t).isLeftHanded()) {
            this.leftArm.xRot = -1.8f;
        } else {
            this.rightArm.xRot = -1.8f;
        }
    }
}

