/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.ColorableAgeableListModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Wolf;

public class WolfModel<T extends Wolf>
extends ColorableAgeableListModel<T> {
    private final ModelPart head;
    private final ModelPart realHead;
    private final ModelPart body;
    private final ModelPart leg0;
    private final ModelPart leg1;
    private final ModelPart leg2;
    private final ModelPart leg3;
    private final ModelPart tail;
    private final ModelPart realTail;
    private final ModelPart upperBody;

    public WolfModel() {
        float f = 0.0f;
        float f2 = 13.5f;
        this.head = new ModelPart(this, 0, 0);
        this.head.setPos(-1.0f, 13.5f, -7.0f);
        this.realHead = new ModelPart(this, 0, 0);
        this.realHead.addBox(-2.0f, -3.0f, -2.0f, 6.0f, 6.0f, 4.0f, 0.0f);
        this.head.addChild(this.realHead);
        this.body = new ModelPart(this, 18, 14);
        this.body.addBox(-3.0f, -2.0f, -3.0f, 6.0f, 9.0f, 6.0f, 0.0f);
        this.body.setPos(0.0f, 14.0f, 2.0f);
        this.upperBody = new ModelPart(this, 21, 0);
        this.upperBody.addBox(-3.0f, -3.0f, -3.0f, 8.0f, 6.0f, 7.0f, 0.0f);
        this.upperBody.setPos(-1.0f, 14.0f, 2.0f);
        this.leg0 = new ModelPart(this, 0, 18);
        this.leg0.addBox(0.0f, 0.0f, -1.0f, 2.0f, 8.0f, 2.0f, 0.0f);
        this.leg0.setPos(-2.5f, 16.0f, 7.0f);
        this.leg1 = new ModelPart(this, 0, 18);
        this.leg1.addBox(0.0f, 0.0f, -1.0f, 2.0f, 8.0f, 2.0f, 0.0f);
        this.leg1.setPos(0.5f, 16.0f, 7.0f);
        this.leg2 = new ModelPart(this, 0, 18);
        this.leg2.addBox(0.0f, 0.0f, -1.0f, 2.0f, 8.0f, 2.0f, 0.0f);
        this.leg2.setPos(-2.5f, 16.0f, -4.0f);
        this.leg3 = new ModelPart(this, 0, 18);
        this.leg3.addBox(0.0f, 0.0f, -1.0f, 2.0f, 8.0f, 2.0f, 0.0f);
        this.leg3.setPos(0.5f, 16.0f, -4.0f);
        this.tail = new ModelPart(this, 9, 18);
        this.tail.setPos(-1.0f, 12.0f, 8.0f);
        this.realTail = new ModelPart(this, 9, 18);
        this.realTail.addBox(0.0f, 0.0f, -1.0f, 2.0f, 8.0f, 2.0f, 0.0f);
        this.tail.addChild(this.realTail);
        this.realHead.texOffs(16, 14).addBox(-2.0f, -5.0f, 0.0f, 2.0f, 2.0f, 1.0f, 0.0f);
        this.realHead.texOffs(16, 14).addBox(2.0f, -5.0f, 0.0f, 2.0f, 2.0f, 1.0f, 0.0f);
        this.realHead.texOffs(0, 10).addBox(-0.5f, 0.0f, -5.0f, 3.0f, 3.0f, 4.0f, 0.0f);
    }

    @Override
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of((Object)this.head);
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of((Object)this.body, (Object)this.leg0, (Object)this.leg1, (Object)this.leg2, (Object)this.leg3, (Object)this.tail, (Object)this.upperBody);
    }

    @Override
    public void prepareMobModel(T t, float f, float f2, float f3) {
        this.tail.yRot = t.isAngry() ? 0.0f : Mth.cos(f * 0.6662f) * 1.4f * f2;
        if (((TamableAnimal)t).isInSittingPose()) {
            this.upperBody.setPos(-1.0f, 16.0f, -3.0f);
            this.upperBody.xRot = 1.2566371f;
            this.upperBody.yRot = 0.0f;
            this.body.setPos(0.0f, 18.0f, 0.0f);
            this.body.xRot = 0.7853982f;
            this.tail.setPos(-1.0f, 21.0f, 6.0f);
            this.leg0.setPos(-2.5f, 22.7f, 2.0f);
            this.leg0.xRot = 4.712389f;
            this.leg1.setPos(0.5f, 22.7f, 2.0f);
            this.leg1.xRot = 4.712389f;
            this.leg2.xRot = 5.811947f;
            this.leg2.setPos(-2.49f, 17.0f, -4.0f);
            this.leg3.xRot = 5.811947f;
            this.leg3.setPos(0.51f, 17.0f, -4.0f);
        } else {
            this.body.setPos(0.0f, 14.0f, 2.0f);
            this.body.xRot = 1.5707964f;
            this.upperBody.setPos(-1.0f, 14.0f, -3.0f);
            this.upperBody.xRot = this.body.xRot;
            this.tail.setPos(-1.0f, 12.0f, 8.0f);
            this.leg0.setPos(-2.5f, 16.0f, 7.0f);
            this.leg1.setPos(0.5f, 16.0f, 7.0f);
            this.leg2.setPos(-2.5f, 16.0f, -4.0f);
            this.leg3.setPos(0.5f, 16.0f, -4.0f);
            this.leg0.xRot = Mth.cos(f * 0.6662f) * 1.4f * f2;
            this.leg1.xRot = Mth.cos(f * 0.6662f + 3.1415927f) * 1.4f * f2;
            this.leg2.xRot = Mth.cos(f * 0.6662f + 3.1415927f) * 1.4f * f2;
            this.leg3.xRot = Mth.cos(f * 0.6662f) * 1.4f * f2;
        }
        this.realHead.zRot = ((Wolf)t).getHeadRollAngle(f3) + ((Wolf)t).getBodyRollAngle(f3, 0.0f);
        this.upperBody.zRot = ((Wolf)t).getBodyRollAngle(f3, -0.08f);
        this.body.zRot = ((Wolf)t).getBodyRollAngle(f3, -0.16f);
        this.realTail.zRot = ((Wolf)t).getBodyRollAngle(f3, -0.2f);
    }

    @Override
    public void setupAnim(T t, float f, float f2, float f3, float f4, float f5) {
        this.head.xRot = f5 * 0.017453292f;
        this.head.yRot = f4 * 0.017453292f;
        this.tail.xRot = f3;
    }
}

