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
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Fox;

public class FoxModel<T extends Fox>
extends AgeableListModel<T> {
    public final ModelPart head;
    private final ModelPart earL;
    private final ModelPart earR;
    private final ModelPart nose;
    private final ModelPart body;
    private final ModelPart leg0;
    private final ModelPart leg1;
    private final ModelPart leg2;
    private final ModelPart leg3;
    private final ModelPart tail;
    private float legMotionPos;

    public FoxModel() {
        super(true, 8.0f, 3.35f);
        this.texWidth = 48;
        this.texHeight = 32;
        this.head = new ModelPart(this, 1, 5);
        this.head.addBox(-3.0f, -2.0f, -5.0f, 8.0f, 6.0f, 6.0f);
        this.head.setPos(-1.0f, 16.5f, -3.0f);
        this.earL = new ModelPart(this, 8, 1);
        this.earL.addBox(-3.0f, -4.0f, -4.0f, 2.0f, 2.0f, 1.0f);
        this.earR = new ModelPart(this, 15, 1);
        this.earR.addBox(3.0f, -4.0f, -4.0f, 2.0f, 2.0f, 1.0f);
        this.nose = new ModelPart(this, 6, 18);
        this.nose.addBox(-1.0f, 2.01f, -8.0f, 4.0f, 2.0f, 3.0f);
        this.head.addChild(this.earL);
        this.head.addChild(this.earR);
        this.head.addChild(this.nose);
        this.body = new ModelPart(this, 24, 15);
        this.body.addBox(-3.0f, 3.999f, -3.5f, 6.0f, 11.0f, 6.0f);
        this.body.setPos(0.0f, 16.0f, -6.0f);
        float f = 0.001f;
        this.leg0 = new ModelPart(this, 13, 24);
        this.leg0.addBox(2.0f, 0.5f, -1.0f, 2.0f, 6.0f, 2.0f, 0.001f);
        this.leg0.setPos(-5.0f, 17.5f, 7.0f);
        this.leg1 = new ModelPart(this, 4, 24);
        this.leg1.addBox(2.0f, 0.5f, -1.0f, 2.0f, 6.0f, 2.0f, 0.001f);
        this.leg1.setPos(-1.0f, 17.5f, 7.0f);
        this.leg2 = new ModelPart(this, 13, 24);
        this.leg2.addBox(2.0f, 0.5f, -1.0f, 2.0f, 6.0f, 2.0f, 0.001f);
        this.leg2.setPos(-5.0f, 17.5f, 0.0f);
        this.leg3 = new ModelPart(this, 4, 24);
        this.leg3.addBox(2.0f, 0.5f, -1.0f, 2.0f, 6.0f, 2.0f, 0.001f);
        this.leg3.setPos(-1.0f, 17.5f, 0.0f);
        this.tail = new ModelPart(this, 30, 0);
        this.tail.addBox(2.0f, 0.0f, -1.0f, 4.0f, 9.0f, 5.0f);
        this.tail.setPos(-4.0f, 15.0f, -1.0f);
        this.body.addChild(this.tail);
    }

    @Override
    public void prepareMobModel(T t, float f, float f2, float f3) {
        this.body.xRot = 1.5707964f;
        this.tail.xRot = -0.05235988f;
        this.leg0.xRot = Mth.cos(f * 0.6662f) * 1.4f * f2;
        this.leg1.xRot = Mth.cos(f * 0.6662f + 3.1415927f) * 1.4f * f2;
        this.leg2.xRot = Mth.cos(f * 0.6662f + 3.1415927f) * 1.4f * f2;
        this.leg3.xRot = Mth.cos(f * 0.6662f) * 1.4f * f2;
        this.head.setPos(-1.0f, 16.5f, -3.0f);
        this.head.yRot = 0.0f;
        this.head.zRot = ((Fox)t).getHeadRollAngle(f3);
        this.leg0.visible = true;
        this.leg1.visible = true;
        this.leg2.visible = true;
        this.leg3.visible = true;
        this.body.setPos(0.0f, 16.0f, -6.0f);
        this.body.zRot = 0.0f;
        this.leg0.setPos(-5.0f, 17.5f, 7.0f);
        this.leg1.setPos(-1.0f, 17.5f, 7.0f);
        if (((Fox)t).isCrouching()) {
            this.body.xRot = 1.6755161f;
            float f4 = ((Fox)t).getCrouchAmount(f3);
            this.body.setPos(0.0f, 16.0f + ((Fox)t).getCrouchAmount(f3), -6.0f);
            this.head.setPos(-1.0f, 16.5f + f4, -3.0f);
            this.head.yRot = 0.0f;
        } else if (((Fox)t).isSleeping()) {
            this.body.zRot = -1.5707964f;
            this.body.setPos(0.0f, 21.0f, -6.0f);
            this.tail.xRot = -2.6179938f;
            if (this.young) {
                this.tail.xRot = -2.1816616f;
                this.body.setPos(0.0f, 21.0f, -2.0f);
            }
            this.head.setPos(1.0f, 19.49f, -3.0f);
            this.head.xRot = 0.0f;
            this.head.yRot = -2.0943952f;
            this.head.zRot = 0.0f;
            this.leg0.visible = false;
            this.leg1.visible = false;
            this.leg2.visible = false;
            this.leg3.visible = false;
        } else if (((Fox)t).isSitting()) {
            this.body.xRot = 0.5235988f;
            this.body.setPos(0.0f, 9.0f, -3.0f);
            this.tail.xRot = 0.7853982f;
            this.tail.setPos(-4.0f, 15.0f, -2.0f);
            this.head.setPos(-1.0f, 10.0f, -0.25f);
            this.head.xRot = 0.0f;
            this.head.yRot = 0.0f;
            if (this.young) {
                this.head.setPos(-1.0f, 13.0f, -3.75f);
            }
            this.leg0.xRot = -1.3089969f;
            this.leg0.setPos(-5.0f, 21.5f, 6.75f);
            this.leg1.xRot = -1.3089969f;
            this.leg1.setPos(-1.0f, 21.5f, 6.75f);
            this.leg2.xRot = -0.2617994f;
            this.leg3.xRot = -0.2617994f;
        }
    }

    @Override
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of((Object)this.head);
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of((Object)this.body, (Object)this.leg0, (Object)this.leg1, (Object)this.leg2, (Object)this.leg3);
    }

    @Override
    public void setupAnim(T t, float f, float f2, float f3, float f4, float f5) {
        float f6;
        if (!(((Fox)t).isSleeping() || ((Fox)t).isFaceplanted() || ((Fox)t).isCrouching())) {
            this.head.xRot = f5 * 0.017453292f;
            this.head.yRot = f4 * 0.017453292f;
        }
        if (((Fox)t).isSleeping()) {
            this.head.xRot = 0.0f;
            this.head.yRot = -2.0943952f;
            this.head.zRot = Mth.cos(f3 * 0.027f) / 22.0f;
        }
        if (((Fox)t).isCrouching()) {
            this.body.yRot = f6 = Mth.cos(f3) * 0.01f;
            this.leg0.zRot = f6;
            this.leg1.zRot = f6;
            this.leg2.zRot = f6 / 2.0f;
            this.leg3.zRot = f6 / 2.0f;
        }
        if (((Fox)t).isFaceplanted()) {
            f6 = 0.1f;
            this.legMotionPos += 0.67f;
            this.leg0.xRot = Mth.cos(this.legMotionPos * 0.4662f) * 0.1f;
            this.leg1.xRot = Mth.cos(this.legMotionPos * 0.4662f + 3.1415927f) * 0.1f;
            this.leg2.xRot = Mth.cos(this.legMotionPos * 0.4662f + 3.1415927f) * 0.1f;
            this.leg3.xRot = Mth.cos(this.legMotionPos * 0.4662f) * 0.1f;
        }
    }
}

