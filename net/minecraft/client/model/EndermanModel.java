/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class EndermanModel<T extends LivingEntity>
extends HumanoidModel<T> {
    public boolean carrying;
    public boolean creepy;

    public EndermanModel(float f) {
        super(0.0f, -14.0f, 64, 32);
        float f2 = -14.0f;
        this.hat = new ModelPart(this, 0, 16);
        this.hat.addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, f - 0.5f);
        this.hat.setPos(0.0f, -14.0f, 0.0f);
        this.body = new ModelPart(this, 32, 16);
        this.body.addBox(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, f);
        this.body.setPos(0.0f, -14.0f, 0.0f);
        this.rightArm = new ModelPart(this, 56, 0);
        this.rightArm.addBox(-1.0f, -2.0f, -1.0f, 2.0f, 30.0f, 2.0f, f);
        this.rightArm.setPos(-3.0f, -12.0f, 0.0f);
        this.leftArm = new ModelPart(this, 56, 0);
        this.leftArm.mirror = true;
        this.leftArm.addBox(-1.0f, -2.0f, -1.0f, 2.0f, 30.0f, 2.0f, f);
        this.leftArm.setPos(5.0f, -12.0f, 0.0f);
        this.rightLeg = new ModelPart(this, 56, 0);
        this.rightLeg.addBox(-1.0f, 0.0f, -1.0f, 2.0f, 30.0f, 2.0f, f);
        this.rightLeg.setPos(-2.0f, -2.0f, 0.0f);
        this.leftLeg = new ModelPart(this, 56, 0);
        this.leftLeg.mirror = true;
        this.leftLeg.addBox(-1.0f, 0.0f, -1.0f, 2.0f, 30.0f, 2.0f, f);
        this.leftLeg.setPos(2.0f, -2.0f, 0.0f);
    }

    @Override
    public void setupAnim(T t, float f, float f2, float f3, float f4, float f5) {
        float f6;
        super.setupAnim(t, f, f2, f3, f4, f5);
        this.head.visible = true;
        float f7 = -14.0f;
        this.body.xRot = 0.0f;
        this.body.y = -14.0f;
        this.body.z = -0.0f;
        this.rightLeg.xRot -= 0.0f;
        this.leftLeg.xRot -= 0.0f;
        this.rightArm.xRot = (float)((double)this.rightArm.xRot * 0.5);
        this.leftArm.xRot = (float)((double)this.leftArm.xRot * 0.5);
        this.rightLeg.xRot = (float)((double)this.rightLeg.xRot * 0.5);
        this.leftLeg.xRot = (float)((double)this.leftLeg.xRot * 0.5);
        float f8 = 0.4f;
        if (this.rightArm.xRot > 0.4f) {
            this.rightArm.xRot = 0.4f;
        }
        if (this.leftArm.xRot > 0.4f) {
            this.leftArm.xRot = 0.4f;
        }
        if (this.rightArm.xRot < -0.4f) {
            this.rightArm.xRot = -0.4f;
        }
        if (this.leftArm.xRot < -0.4f) {
            this.leftArm.xRot = -0.4f;
        }
        if (this.rightLeg.xRot > 0.4f) {
            this.rightLeg.xRot = 0.4f;
        }
        if (this.leftLeg.xRot > 0.4f) {
            this.leftLeg.xRot = 0.4f;
        }
        if (this.rightLeg.xRot < -0.4f) {
            this.rightLeg.xRot = -0.4f;
        }
        if (this.leftLeg.xRot < -0.4f) {
            this.leftLeg.xRot = -0.4f;
        }
        if (this.carrying) {
            this.rightArm.xRot = -0.5f;
            this.leftArm.xRot = -0.5f;
            this.rightArm.zRot = 0.05f;
            this.leftArm.zRot = -0.05f;
        }
        this.rightArm.z = 0.0f;
        this.leftArm.z = 0.0f;
        this.rightLeg.z = 0.0f;
        this.leftLeg.z = 0.0f;
        this.rightLeg.y = -5.0f;
        this.leftLeg.y = -5.0f;
        this.head.z = -0.0f;
        this.head.y = -13.0f;
        this.hat.x = this.head.x;
        this.hat.y = this.head.y;
        this.hat.z = this.head.z;
        this.hat.xRot = this.head.xRot;
        this.hat.yRot = this.head.yRot;
        this.hat.zRot = this.head.zRot;
        if (this.creepy) {
            f6 = 1.0f;
            this.head.y -= 5.0f;
        }
        f6 = -14.0f;
        this.rightArm.setPos(-5.0f, -12.0f, 0.0f);
        this.leftArm.setPos(5.0f, -12.0f, 0.0f);
    }
}

