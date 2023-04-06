/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 */
package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ArmorStandArmorModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Rotations;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;

public class ArmorStandModel
extends ArmorStandArmorModel {
    private final ModelPart bodyStick1;
    private final ModelPart bodyStick2;
    private final ModelPart shoulderStick;
    private final ModelPart basePlate;

    public ArmorStandModel() {
        this(0.0f);
    }

    public ArmorStandModel(float f) {
        super(f, 64, 64);
        this.head = new ModelPart(this, 0, 0);
        this.head.addBox(-1.0f, -7.0f, -1.0f, 2.0f, 7.0f, 2.0f, f);
        this.head.setPos(0.0f, 0.0f, 0.0f);
        this.body = new ModelPart(this, 0, 26);
        this.body.addBox(-6.0f, 0.0f, -1.5f, 12.0f, 3.0f, 3.0f, f);
        this.body.setPos(0.0f, 0.0f, 0.0f);
        this.rightArm = new ModelPart(this, 24, 0);
        this.rightArm.addBox(-2.0f, -2.0f, -1.0f, 2.0f, 12.0f, 2.0f, f);
        this.rightArm.setPos(-5.0f, 2.0f, 0.0f);
        this.leftArm = new ModelPart(this, 32, 16);
        this.leftArm.mirror = true;
        this.leftArm.addBox(0.0f, -2.0f, -1.0f, 2.0f, 12.0f, 2.0f, f);
        this.leftArm.setPos(5.0f, 2.0f, 0.0f);
        this.rightLeg = new ModelPart(this, 8, 0);
        this.rightLeg.addBox(-1.0f, 0.0f, -1.0f, 2.0f, 11.0f, 2.0f, f);
        this.rightLeg.setPos(-1.9f, 12.0f, 0.0f);
        this.leftLeg = new ModelPart(this, 40, 16);
        this.leftLeg.mirror = true;
        this.leftLeg.addBox(-1.0f, 0.0f, -1.0f, 2.0f, 11.0f, 2.0f, f);
        this.leftLeg.setPos(1.9f, 12.0f, 0.0f);
        this.bodyStick1 = new ModelPart(this, 16, 0);
        this.bodyStick1.addBox(-3.0f, 3.0f, -1.0f, 2.0f, 7.0f, 2.0f, f);
        this.bodyStick1.setPos(0.0f, 0.0f, 0.0f);
        this.bodyStick1.visible = true;
        this.bodyStick2 = new ModelPart(this, 48, 16);
        this.bodyStick2.addBox(1.0f, 3.0f, -1.0f, 2.0f, 7.0f, 2.0f, f);
        this.bodyStick2.setPos(0.0f, 0.0f, 0.0f);
        this.shoulderStick = new ModelPart(this, 0, 48);
        this.shoulderStick.addBox(-4.0f, 10.0f, -1.0f, 8.0f, 2.0f, 2.0f, f);
        this.shoulderStick.setPos(0.0f, 0.0f, 0.0f);
        this.basePlate = new ModelPart(this, 0, 32);
        this.basePlate.addBox(-6.0f, 11.0f, -6.0f, 12.0f, 1.0f, 12.0f, f);
        this.basePlate.setPos(0.0f, 12.0f, 0.0f);
        this.hat.visible = false;
    }

    @Override
    public void prepareMobModel(ArmorStand armorStand, float f, float f2, float f3) {
        this.basePlate.xRot = 0.0f;
        this.basePlate.yRot = 0.017453292f * -Mth.rotLerp(f3, armorStand.yRotO, armorStand.yRot);
        this.basePlate.zRot = 0.0f;
    }

    @Override
    public void setupAnim(ArmorStand armorStand, float f, float f2, float f3, float f4, float f5) {
        super.setupAnim(armorStand, f, f2, f3, f4, f5);
        this.leftArm.visible = armorStand.isShowArms();
        this.rightArm.visible = armorStand.isShowArms();
        this.basePlate.visible = !armorStand.isNoBasePlate();
        this.leftLeg.setPos(1.9f, 12.0f, 0.0f);
        this.rightLeg.setPos(-1.9f, 12.0f, 0.0f);
        this.bodyStick1.xRot = 0.017453292f * armorStand.getBodyPose().getX();
        this.bodyStick1.yRot = 0.017453292f * armorStand.getBodyPose().getY();
        this.bodyStick1.zRot = 0.017453292f * armorStand.getBodyPose().getZ();
        this.bodyStick2.xRot = 0.017453292f * armorStand.getBodyPose().getX();
        this.bodyStick2.yRot = 0.017453292f * armorStand.getBodyPose().getY();
        this.bodyStick2.zRot = 0.017453292f * armorStand.getBodyPose().getZ();
        this.shoulderStick.xRot = 0.017453292f * armorStand.getBodyPose().getX();
        this.shoulderStick.yRot = 0.017453292f * armorStand.getBodyPose().getY();
        this.shoulderStick.zRot = 0.017453292f * armorStand.getBodyPose().getZ();
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return Iterables.concat(super.bodyParts(), (Iterable)ImmutableList.of((Object)this.bodyStick1, (Object)this.bodyStick2, (Object)this.shoulderStick, (Object)this.basePlate));
    }

    @Override
    public void translateToHand(HumanoidArm humanoidArm, PoseStack poseStack) {
        ModelPart modelPart = this.getArm(humanoidArm);
        boolean bl = modelPart.visible;
        modelPart.visible = true;
        super.translateToHand(humanoidArm, poseStack);
        modelPart.visible = bl;
    }
}

