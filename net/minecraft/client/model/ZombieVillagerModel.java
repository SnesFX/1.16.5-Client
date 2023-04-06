/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.model;

import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.VillagerHeadModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Zombie;

public class ZombieVillagerModel<T extends Zombie>
extends HumanoidModel<T>
implements VillagerHeadModel {
    private ModelPart hatRim;

    public ZombieVillagerModel(float f, boolean bl) {
        super(f, 0.0f, 64, bl ? 32 : 64);
        if (bl) {
            this.head = new ModelPart(this, 0, 0);
            this.head.addBox(-4.0f, -10.0f, -4.0f, 8.0f, 8.0f, 8.0f, f);
            this.body = new ModelPart(this, 16, 16);
            this.body.addBox(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, f + 0.1f);
            this.rightLeg = new ModelPart(this, 0, 16);
            this.rightLeg.setPos(-2.0f, 12.0f, 0.0f);
            this.rightLeg.addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, f + 0.1f);
            this.leftLeg = new ModelPart(this, 0, 16);
            this.leftLeg.mirror = true;
            this.leftLeg.setPos(2.0f, 12.0f, 0.0f);
            this.leftLeg.addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, f + 0.1f);
        } else {
            this.head = new ModelPart(this, 0, 0);
            this.head.texOffs(0, 0).addBox(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f, f);
            this.head.texOffs(24, 0).addBox(-1.0f, -3.0f, -6.0f, 2.0f, 4.0f, 2.0f, f);
            this.hat = new ModelPart(this, 32, 0);
            this.hat.addBox(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f, f + 0.5f);
            this.hatRim = new ModelPart(this);
            this.hatRim.texOffs(30, 47).addBox(-8.0f, -8.0f, -6.0f, 16.0f, 16.0f, 1.0f, f);
            this.hatRim.xRot = -1.5707964f;
            this.hat.addChild(this.hatRim);
            this.body = new ModelPart(this, 16, 20);
            this.body.addBox(-4.0f, 0.0f, -3.0f, 8.0f, 12.0f, 6.0f, f);
            this.body.texOffs(0, 38).addBox(-4.0f, 0.0f, -3.0f, 8.0f, 18.0f, 6.0f, f + 0.05f);
            this.rightArm = new ModelPart(this, 44, 22);
            this.rightArm.addBox(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, f);
            this.rightArm.setPos(-5.0f, 2.0f, 0.0f);
            this.leftArm = new ModelPart(this, 44, 22);
            this.leftArm.mirror = true;
            this.leftArm.addBox(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, f);
            this.leftArm.setPos(5.0f, 2.0f, 0.0f);
            this.rightLeg = new ModelPart(this, 0, 22);
            this.rightLeg.setPos(-2.0f, 12.0f, 0.0f);
            this.rightLeg.addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, f);
            this.leftLeg = new ModelPart(this, 0, 22);
            this.leftLeg.mirror = true;
            this.leftLeg.setPos(2.0f, 12.0f, 0.0f);
            this.leftLeg.addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, f);
        }
    }

    @Override
    public void setupAnim(T t, float f, float f2, float f3, float f4, float f5) {
        super.setupAnim(t, f, f2, f3, f4, f5);
        AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, ((Mob)t).isAggressive(), this.attackTime, f3);
    }

    @Override
    public void hatVisible(boolean bl) {
        this.head.visible = bl;
        this.hat.visible = bl;
        this.hatRim.visible = bl;
    }
}

