/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelUtils;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.phys.Vec3;

public class BeeModel<T extends Bee>
extends AgeableListModel<T> {
    private final ModelPart bone;
    private final ModelPart body;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart frontLeg;
    private final ModelPart midLeg;
    private final ModelPart backLeg;
    private final ModelPart stinger;
    private final ModelPart leftAntenna;
    private final ModelPart rightAntenna;
    private float rollAmount;

    public BeeModel() {
        super(false, 24.0f, 0.0f);
        this.texWidth = 64;
        this.texHeight = 64;
        this.bone = new ModelPart(this);
        this.bone.setPos(0.0f, 19.0f, 0.0f);
        this.body = new ModelPart(this, 0, 0);
        this.body.setPos(0.0f, 0.0f, 0.0f);
        this.bone.addChild(this.body);
        this.body.addBox(-3.5f, -4.0f, -5.0f, 7.0f, 7.0f, 10.0f, 0.0f);
        this.stinger = new ModelPart(this, 26, 7);
        this.stinger.addBox(0.0f, -1.0f, 5.0f, 0.0f, 1.0f, 2.0f, 0.0f);
        this.body.addChild(this.stinger);
        this.leftAntenna = new ModelPart(this, 2, 0);
        this.leftAntenna.setPos(0.0f, -2.0f, -5.0f);
        this.leftAntenna.addBox(1.5f, -2.0f, -3.0f, 1.0f, 2.0f, 3.0f, 0.0f);
        this.rightAntenna = new ModelPart(this, 2, 3);
        this.rightAntenna.setPos(0.0f, -2.0f, -5.0f);
        this.rightAntenna.addBox(-2.5f, -2.0f, -3.0f, 1.0f, 2.0f, 3.0f, 0.0f);
        this.body.addChild(this.leftAntenna);
        this.body.addChild(this.rightAntenna);
        this.rightWing = new ModelPart(this, 0, 18);
        this.rightWing.setPos(-1.5f, -4.0f, -3.0f);
        this.rightWing.xRot = 0.0f;
        this.rightWing.yRot = -0.2618f;
        this.rightWing.zRot = 0.0f;
        this.bone.addChild(this.rightWing);
        this.rightWing.addBox(-9.0f, 0.0f, 0.0f, 9.0f, 0.0f, 6.0f, 0.001f);
        this.leftWing = new ModelPart(this, 0, 18);
        this.leftWing.setPos(1.5f, -4.0f, -3.0f);
        this.leftWing.xRot = 0.0f;
        this.leftWing.yRot = 0.2618f;
        this.leftWing.zRot = 0.0f;
        this.leftWing.mirror = true;
        this.bone.addChild(this.leftWing);
        this.leftWing.addBox(0.0f, 0.0f, 0.0f, 9.0f, 0.0f, 6.0f, 0.001f);
        this.frontLeg = new ModelPart(this);
        this.frontLeg.setPos(1.5f, 3.0f, -2.0f);
        this.bone.addChild(this.frontLeg);
        this.frontLeg.addBox("frontLegBox", -5.0f, 0.0f, 0.0f, 7, 2, 0, 0.0f, 26, 1);
        this.midLeg = new ModelPart(this);
        this.midLeg.setPos(1.5f, 3.0f, 0.0f);
        this.bone.addChild(this.midLeg);
        this.midLeg.addBox("midLegBox", -5.0f, 0.0f, 0.0f, 7, 2, 0, 0.0f, 26, 3);
        this.backLeg = new ModelPart(this);
        this.backLeg.setPos(1.5f, 3.0f, 2.0f);
        this.bone.addChild(this.backLeg);
        this.backLeg.addBox("backLegBox", -5.0f, 0.0f, 0.0f, 7, 2, 0, 0.0f, 26, 5);
    }

    @Override
    public void prepareMobModel(T t, float f, float f2, float f3) {
        super.prepareMobModel(t, f, f2, f3);
        this.rollAmount = ((Bee)t).getRollAmount(f3);
        this.stinger.visible = !((Bee)t).hasStung();
    }

    @Override
    public void setupAnim(T t, float f, float f2, float f3, float f4, float f5) {
        float f6;
        boolean bl;
        this.rightWing.xRot = 0.0f;
        this.leftAntenna.xRot = 0.0f;
        this.rightAntenna.xRot = 0.0f;
        this.bone.xRot = 0.0f;
        this.bone.y = 19.0f;
        boolean bl2 = bl = ((Entity)t).isOnGround() && ((Entity)t).getDeltaMovement().lengthSqr() < 1.0E-7;
        if (bl) {
            this.rightWing.yRot = -0.2618f;
            this.rightWing.zRot = 0.0f;
            this.leftWing.xRot = 0.0f;
            this.leftWing.yRot = 0.2618f;
            this.leftWing.zRot = 0.0f;
            this.frontLeg.xRot = 0.0f;
            this.midLeg.xRot = 0.0f;
            this.backLeg.xRot = 0.0f;
        } else {
            f6 = f3 * 2.1f;
            this.rightWing.yRot = 0.0f;
            this.rightWing.zRot = Mth.cos(f6) * 3.1415927f * 0.15f;
            this.leftWing.xRot = this.rightWing.xRot;
            this.leftWing.yRot = this.rightWing.yRot;
            this.leftWing.zRot = -this.rightWing.zRot;
            this.frontLeg.xRot = 0.7853982f;
            this.midLeg.xRot = 0.7853982f;
            this.backLeg.xRot = 0.7853982f;
            this.bone.xRot = 0.0f;
            this.bone.yRot = 0.0f;
            this.bone.zRot = 0.0f;
        }
        if (!t.isAngry()) {
            this.bone.xRot = 0.0f;
            this.bone.yRot = 0.0f;
            this.bone.zRot = 0.0f;
            if (!bl) {
                f6 = Mth.cos(f3 * 0.18f);
                this.bone.xRot = 0.1f + f6 * 3.1415927f * 0.025f;
                this.leftAntenna.xRot = f6 * 3.1415927f * 0.03f;
                this.rightAntenna.xRot = f6 * 3.1415927f * 0.03f;
                this.frontLeg.xRot = -f6 * 3.1415927f * 0.1f + 0.3926991f;
                this.backLeg.xRot = -f6 * 3.1415927f * 0.05f + 0.7853982f;
                this.bone.y = 19.0f - Mth.cos(f3 * 0.18f) * 0.9f;
            }
        }
        if (this.rollAmount > 0.0f) {
            this.bone.xRot = ModelUtils.rotlerpRad(this.bone.xRot, 3.0915928f, this.rollAmount);
        }
    }

    @Override
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of();
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of((Object)this.bone);
    }
}

