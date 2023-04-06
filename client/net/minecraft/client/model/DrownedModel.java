/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class DrownedModel<T extends Zombie>
extends ZombieModel<T> {
    public DrownedModel(float f, float f2, int n, int n2) {
        super(f, f2, n, n2);
        this.rightArm = new ModelPart(this, 32, 48);
        this.rightArm.addBox(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, f);
        this.rightArm.setPos(-5.0f, 2.0f + f2, 0.0f);
        this.rightLeg = new ModelPart(this, 16, 48);
        this.rightLeg.addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, f);
        this.rightLeg.setPos(-1.9f, 12.0f + f2, 0.0f);
    }

    public DrownedModel(float f, boolean bl) {
        super(f, 0.0f, 64, bl ? 32 : 64);
    }

    @Override
    public void prepareMobModel(T t, float f, float f2, float f3) {
        this.rightArmPose = HumanoidModel.ArmPose.EMPTY;
        this.leftArmPose = HumanoidModel.ArmPose.EMPTY;
        ItemStack itemStack = ((LivingEntity)t).getItemInHand(InteractionHand.MAIN_HAND);
        if (itemStack.getItem() == Items.TRIDENT && ((Mob)t).isAggressive()) {
            if (((Mob)t).getMainArm() == HumanoidArm.RIGHT) {
                this.rightArmPose = HumanoidModel.ArmPose.THROW_SPEAR;
            } else {
                this.leftArmPose = HumanoidModel.ArmPose.THROW_SPEAR;
            }
        }
        super.prepareMobModel(t, f, f2, f3);
    }

    @Override
    public void setupAnim(T t, float f, float f2, float f3, float f4, float f5) {
        super.setupAnim(t, f, f2, f3, f4, f5);
        if (this.leftArmPose == HumanoidModel.ArmPose.THROW_SPEAR) {
            this.leftArm.xRot = this.leftArm.xRot * 0.5f - 3.1415927f;
            this.leftArm.yRot = 0.0f;
        }
        if (this.rightArmPose == HumanoidModel.ArmPose.THROW_SPEAR) {
            this.rightArm.xRot = this.rightArm.xRot * 0.5f - 3.1415927f;
            this.rightArm.yRot = 0.0f;
        }
        if (this.swimAmount > 0.0f) {
            this.rightArm.xRot = this.rotlerpRad(this.swimAmount, this.rightArm.xRot, -2.5132742f) + this.swimAmount * 0.35f * Mth.sin(0.1f * f3);
            this.leftArm.xRot = this.rotlerpRad(this.swimAmount, this.leftArm.xRot, -2.5132742f) - this.swimAmount * 0.35f * Mth.sin(0.1f * f3);
            this.rightArm.zRot = this.rotlerpRad(this.swimAmount, this.rightArm.zRot, -0.15f);
            this.leftArm.zRot = this.rotlerpRad(this.swimAmount, this.leftArm.zRot, 0.15f);
            this.leftLeg.xRot -= this.swimAmount * 0.55f * Mth.sin(0.1f * f3);
            this.rightLeg.xRot += this.swimAmount * 0.55f * Mth.sin(0.1f * f3);
            this.head.xRot = 0.0f;
        }
    }
}

