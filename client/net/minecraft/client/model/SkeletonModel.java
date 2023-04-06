/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SkeletonModel<T extends Mob>
extends HumanoidModel<T> {
    public SkeletonModel() {
        this(0.0f, false);
    }

    public SkeletonModel(float f, boolean bl) {
        super(f);
        if (!bl) {
            this.rightArm = new ModelPart(this, 40, 16);
            this.rightArm.addBox(-1.0f, -2.0f, -1.0f, 2.0f, 12.0f, 2.0f, f);
            this.rightArm.setPos(-5.0f, 2.0f, 0.0f);
            this.leftArm = new ModelPart(this, 40, 16);
            this.leftArm.mirror = true;
            this.leftArm.addBox(-1.0f, -2.0f, -1.0f, 2.0f, 12.0f, 2.0f, f);
            this.leftArm.setPos(5.0f, 2.0f, 0.0f);
            this.rightLeg = new ModelPart(this, 0, 16);
            this.rightLeg.addBox(-1.0f, 0.0f, -1.0f, 2.0f, 12.0f, 2.0f, f);
            this.rightLeg.setPos(-2.0f, 12.0f, 0.0f);
            this.leftLeg = new ModelPart(this, 0, 16);
            this.leftLeg.mirror = true;
            this.leftLeg.addBox(-1.0f, 0.0f, -1.0f, 2.0f, 12.0f, 2.0f, f);
            this.leftLeg.setPos(2.0f, 12.0f, 0.0f);
        }
    }

    @Override
    public void prepareMobModel(T t, float f, float f2, float f3) {
        this.rightArmPose = HumanoidModel.ArmPose.EMPTY;
        this.leftArmPose = HumanoidModel.ArmPose.EMPTY;
        ItemStack itemStack = ((LivingEntity)t).getItemInHand(InteractionHand.MAIN_HAND);
        if (itemStack.getItem() == Items.BOW && ((Mob)t).isAggressive()) {
            if (((Mob)t).getMainArm() == HumanoidArm.RIGHT) {
                this.rightArmPose = HumanoidModel.ArmPose.BOW_AND_ARROW;
            } else {
                this.leftArmPose = HumanoidModel.ArmPose.BOW_AND_ARROW;
            }
        }
        super.prepareMobModel(t, f, f2, f3);
    }

    @Override
    public void setupAnim(T t, float f, float f2, float f3, float f4, float f5) {
        super.setupAnim(t, f, f2, f3, f4, f5);
        ItemStack itemStack = ((LivingEntity)t).getMainHandItem();
        if (((Mob)t).isAggressive() && (itemStack.isEmpty() || itemStack.getItem() != Items.BOW)) {
            float f6 = Mth.sin(this.attackTime * 3.1415927f);
            float f7 = Mth.sin((1.0f - (1.0f - this.attackTime) * (1.0f - this.attackTime)) * 3.1415927f);
            this.rightArm.zRot = 0.0f;
            this.leftArm.zRot = 0.0f;
            this.rightArm.yRot = -(0.1f - f6 * 0.6f);
            this.leftArm.yRot = 0.1f - f6 * 0.6f;
            this.rightArm.xRot = -1.5707964f;
            this.leftArm.xRot = -1.5707964f;
            this.rightArm.xRot -= f6 * 1.2f - f7 * 0.4f;
            this.leftArm.xRot -= f6 * 1.2f - f7 * 0.4f;
            AnimationUtils.bobArms(this.rightArm, this.leftArm, f3);
        }
    }

    @Override
    public void translateToHand(HumanoidArm humanoidArm, PoseStack poseStack) {
        float f = humanoidArm == HumanoidArm.RIGHT ? 1.0f : -1.0f;
        ModelPart modelPart = this.getArm(humanoidArm);
        modelPart.x += f;
        modelPart.translateAndRotate(poseStack);
        modelPart.x -= f;
    }
}

