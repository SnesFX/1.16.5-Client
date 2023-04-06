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
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.item.ItemStack;

public class VexModel
extends HumanoidModel<Vex> {
    private final ModelPart leftWing;
    private final ModelPart rightWing;

    public VexModel() {
        super(0.0f, 0.0f, 64, 64);
        this.leftLeg.visible = false;
        this.hat.visible = false;
        this.rightLeg = new ModelPart(this, 32, 0);
        this.rightLeg.addBox(-1.0f, -1.0f, -2.0f, 6.0f, 10.0f, 4.0f, 0.0f);
        this.rightLeg.setPos(-1.9f, 12.0f, 0.0f);
        this.rightWing = new ModelPart(this, 0, 32);
        this.rightWing.addBox(-20.0f, 0.0f, 0.0f, 20.0f, 12.0f, 1.0f);
        this.leftWing = new ModelPart(this, 0, 32);
        this.leftWing.mirror = true;
        this.leftWing.addBox(0.0f, 0.0f, 0.0f, 20.0f, 12.0f, 1.0f);
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return Iterables.concat(super.bodyParts(), (Iterable)ImmutableList.of((Object)this.rightWing, (Object)this.leftWing));
    }

    @Override
    public void setupAnim(Vex vex, float f, float f2, float f3, float f4, float f5) {
        super.setupAnim(vex, f, f2, f3, f4, f5);
        if (vex.isCharging()) {
            if (vex.getMainHandItem().isEmpty()) {
                this.rightArm.xRot = 4.712389f;
                this.leftArm.xRot = 4.712389f;
            } else if (vex.getMainArm() == HumanoidArm.RIGHT) {
                this.rightArm.xRot = 3.7699115f;
            } else {
                this.leftArm.xRot = 3.7699115f;
            }
        }
        this.rightLeg.xRot += 0.62831855f;
        this.rightWing.z = 2.0f;
        this.leftWing.z = 2.0f;
        this.rightWing.y = 1.0f;
        this.leftWing.y = 1.0f;
        this.rightWing.yRot = 0.47123894f + Mth.cos(f3 * 0.8f) * 3.1415927f * 0.05f;
        this.leftWing.yRot = -this.rightWing.yRot;
        this.leftWing.zRot = -0.47123894f;
        this.leftWing.xRot = 0.47123894f;
        this.rightWing.xRot = 0.47123894f;
        this.rightWing.zRot = 0.47123894f;
    }
}

