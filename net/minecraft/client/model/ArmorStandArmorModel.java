/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Rotations;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;

public class ArmorStandArmorModel
extends HumanoidModel<ArmorStand> {
    public ArmorStandArmorModel(float f) {
        this(f, 64, 32);
    }

    protected ArmorStandArmorModel(float f, int n, int n2) {
        super(f, 0.0f, n, n2);
    }

    @Override
    public void setupAnim(ArmorStand armorStand, float f, float f2, float f3, float f4, float f5) {
        this.head.xRot = 0.017453292f * armorStand.getHeadPose().getX();
        this.head.yRot = 0.017453292f * armorStand.getHeadPose().getY();
        this.head.zRot = 0.017453292f * armorStand.getHeadPose().getZ();
        this.head.setPos(0.0f, 1.0f, 0.0f);
        this.body.xRot = 0.017453292f * armorStand.getBodyPose().getX();
        this.body.yRot = 0.017453292f * armorStand.getBodyPose().getY();
        this.body.zRot = 0.017453292f * armorStand.getBodyPose().getZ();
        this.leftArm.xRot = 0.017453292f * armorStand.getLeftArmPose().getX();
        this.leftArm.yRot = 0.017453292f * armorStand.getLeftArmPose().getY();
        this.leftArm.zRot = 0.017453292f * armorStand.getLeftArmPose().getZ();
        this.rightArm.xRot = 0.017453292f * armorStand.getRightArmPose().getX();
        this.rightArm.yRot = 0.017453292f * armorStand.getRightArmPose().getY();
        this.rightArm.zRot = 0.017453292f * armorStand.getRightArmPose().getZ();
        this.leftLeg.xRot = 0.017453292f * armorStand.getLeftLegPose().getX();
        this.leftLeg.yRot = 0.017453292f * armorStand.getLeftLegPose().getY();
        this.leftLeg.zRot = 0.017453292f * armorStand.getLeftLegPose().getZ();
        this.leftLeg.setPos(1.9f, 11.0f, 0.0f);
        this.rightLeg.xRot = 0.017453292f * armorStand.getRightLegPose().getX();
        this.rightLeg.yRot = 0.017453292f * armorStand.getRightLegPose().getY();
        this.rightLeg.zRot = 0.017453292f * armorStand.getRightLegPose().getZ();
        this.rightLeg.setPos(-1.9f, 11.0f, 0.0f);
        this.hat.copyFrom(this.head);
    }
}

