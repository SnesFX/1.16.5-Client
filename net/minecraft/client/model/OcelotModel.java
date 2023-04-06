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

public class OcelotModel<T extends Entity>
extends AgeableListModel<T> {
    protected final ModelPart backLegL;
    protected final ModelPart backLegR;
    protected final ModelPart frontLegL;
    protected final ModelPart frontLegR;
    protected final ModelPart tail1;
    protected final ModelPart tail2;
    protected final ModelPart head = new ModelPart(this);
    protected final ModelPart body;
    protected int state = 1;

    public OcelotModel(float f) {
        super(true, 10.0f, 4.0f);
        this.head.addBox("main", -2.5f, -2.0f, -3.0f, 5, 4, 5, f, 0, 0);
        this.head.addBox("nose", -1.5f, 0.0f, -4.0f, 3, 2, 2, f, 0, 24);
        this.head.addBox("ear1", -2.0f, -3.0f, 0.0f, 1, 1, 2, f, 0, 10);
        this.head.addBox("ear2", 1.0f, -3.0f, 0.0f, 1, 1, 2, f, 6, 10);
        this.head.setPos(0.0f, 15.0f, -9.0f);
        this.body = new ModelPart(this, 20, 0);
        this.body.addBox(-2.0f, 3.0f, -8.0f, 4.0f, 16.0f, 6.0f, f);
        this.body.setPos(0.0f, 12.0f, -10.0f);
        this.tail1 = new ModelPart(this, 0, 15);
        this.tail1.addBox(-0.5f, 0.0f, 0.0f, 1.0f, 8.0f, 1.0f, f);
        this.tail1.xRot = 0.9f;
        this.tail1.setPos(0.0f, 15.0f, 8.0f);
        this.tail2 = new ModelPart(this, 4, 15);
        this.tail2.addBox(-0.5f, 0.0f, 0.0f, 1.0f, 8.0f, 1.0f, f);
        this.tail2.setPos(0.0f, 20.0f, 14.0f);
        this.backLegL = new ModelPart(this, 8, 13);
        this.backLegL.addBox(-1.0f, 0.0f, 1.0f, 2.0f, 6.0f, 2.0f, f);
        this.backLegL.setPos(1.1f, 18.0f, 5.0f);
        this.backLegR = new ModelPart(this, 8, 13);
        this.backLegR.addBox(-1.0f, 0.0f, 1.0f, 2.0f, 6.0f, 2.0f, f);
        this.backLegR.setPos(-1.1f, 18.0f, 5.0f);
        this.frontLegL = new ModelPart(this, 40, 0);
        this.frontLegL.addBox(-1.0f, 0.0f, 0.0f, 2.0f, 10.0f, 2.0f, f);
        this.frontLegL.setPos(1.2f, 14.1f, -5.0f);
        this.frontLegR = new ModelPart(this, 40, 0);
        this.frontLegR.addBox(-1.0f, 0.0f, 0.0f, 2.0f, 10.0f, 2.0f, f);
        this.frontLegR.setPos(-1.2f, 14.1f, -5.0f);
    }

    @Override
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of((Object)this.head);
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of((Object)this.body, (Object)this.backLegL, (Object)this.backLegR, (Object)this.frontLegL, (Object)this.frontLegR, (Object)this.tail1, (Object)this.tail2);
    }

    @Override
    public void setupAnim(T t, float f, float f2, float f3, float f4, float f5) {
        this.head.xRot = f5 * 0.017453292f;
        this.head.yRot = f4 * 0.017453292f;
        if (this.state != 3) {
            this.body.xRot = 1.5707964f;
            if (this.state == 2) {
                this.backLegL.xRot = Mth.cos(f * 0.6662f) * f2;
                this.backLegR.xRot = Mth.cos(f * 0.6662f + 0.3f) * f2;
                this.frontLegL.xRot = Mth.cos(f * 0.6662f + 3.1415927f + 0.3f) * f2;
                this.frontLegR.xRot = Mth.cos(f * 0.6662f + 3.1415927f) * f2;
                this.tail2.xRot = 1.7278761f + 0.31415927f * Mth.cos(f) * f2;
            } else {
                this.backLegL.xRot = Mth.cos(f * 0.6662f) * f2;
                this.backLegR.xRot = Mth.cos(f * 0.6662f + 3.1415927f) * f2;
                this.frontLegL.xRot = Mth.cos(f * 0.6662f + 3.1415927f) * f2;
                this.frontLegR.xRot = Mth.cos(f * 0.6662f) * f2;
                this.tail2.xRot = this.state == 1 ? 1.7278761f + 0.7853982f * Mth.cos(f) * f2 : 1.7278761f + 0.47123894f * Mth.cos(f) * f2;
            }
        }
    }

    @Override
    public void prepareMobModel(T t, float f, float f2, float f3) {
        this.body.y = 12.0f;
        this.body.z = -10.0f;
        this.head.y = 15.0f;
        this.head.z = -9.0f;
        this.tail1.y = 15.0f;
        this.tail1.z = 8.0f;
        this.tail2.y = 20.0f;
        this.tail2.z = 14.0f;
        this.frontLegL.y = 14.1f;
        this.frontLegL.z = -5.0f;
        this.frontLegR.y = 14.1f;
        this.frontLegR.z = -5.0f;
        this.backLegL.y = 18.0f;
        this.backLegL.z = 5.0f;
        this.backLegR.y = 18.0f;
        this.backLegR.z = 5.0f;
        this.tail1.xRot = 0.9f;
        if (((Entity)t).isCrouching()) {
            this.body.y += 1.0f;
            this.head.y += 2.0f;
            this.tail1.y += 1.0f;
            this.tail2.y += -4.0f;
            this.tail2.z += 2.0f;
            this.tail1.xRot = 1.5707964f;
            this.tail2.xRot = 1.5707964f;
            this.state = 0;
        } else if (((Entity)t).isSprinting()) {
            this.tail2.y = this.tail1.y;
            this.tail2.z += 2.0f;
            this.tail1.xRot = 1.5707964f;
            this.tail2.xRot = 1.5707964f;
            this.state = 2;
        } else {
            this.state = 1;
        }
    }
}

