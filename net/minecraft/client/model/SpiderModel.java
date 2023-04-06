/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class SpiderModel<T extends Entity>
extends ListModel<T> {
    private final ModelPart head;
    private final ModelPart body0;
    private final ModelPart body1;
    private final ModelPart leg0;
    private final ModelPart leg1;
    private final ModelPart leg2;
    private final ModelPart leg3;
    private final ModelPart leg4;
    private final ModelPart leg5;
    private final ModelPart leg6;
    private final ModelPart leg7;

    public SpiderModel() {
        float f = 0.0f;
        int n = 15;
        this.head = new ModelPart(this, 32, 4);
        this.head.addBox(-4.0f, -4.0f, -8.0f, 8.0f, 8.0f, 8.0f, 0.0f);
        this.head.setPos(0.0f, 15.0f, -3.0f);
        this.body0 = new ModelPart(this, 0, 0);
        this.body0.addBox(-3.0f, -3.0f, -3.0f, 6.0f, 6.0f, 6.0f, 0.0f);
        this.body0.setPos(0.0f, 15.0f, 0.0f);
        this.body1 = new ModelPart(this, 0, 12);
        this.body1.addBox(-5.0f, -4.0f, -6.0f, 10.0f, 8.0f, 12.0f, 0.0f);
        this.body1.setPos(0.0f, 15.0f, 9.0f);
        this.leg0 = new ModelPart(this, 18, 0);
        this.leg0.addBox(-15.0f, -1.0f, -1.0f, 16.0f, 2.0f, 2.0f, 0.0f);
        this.leg0.setPos(-4.0f, 15.0f, 2.0f);
        this.leg1 = new ModelPart(this, 18, 0);
        this.leg1.addBox(-1.0f, -1.0f, -1.0f, 16.0f, 2.0f, 2.0f, 0.0f);
        this.leg1.setPos(4.0f, 15.0f, 2.0f);
        this.leg2 = new ModelPart(this, 18, 0);
        this.leg2.addBox(-15.0f, -1.0f, -1.0f, 16.0f, 2.0f, 2.0f, 0.0f);
        this.leg2.setPos(-4.0f, 15.0f, 1.0f);
        this.leg3 = new ModelPart(this, 18, 0);
        this.leg3.addBox(-1.0f, -1.0f, -1.0f, 16.0f, 2.0f, 2.0f, 0.0f);
        this.leg3.setPos(4.0f, 15.0f, 1.0f);
        this.leg4 = new ModelPart(this, 18, 0);
        this.leg4.addBox(-15.0f, -1.0f, -1.0f, 16.0f, 2.0f, 2.0f, 0.0f);
        this.leg4.setPos(-4.0f, 15.0f, 0.0f);
        this.leg5 = new ModelPart(this, 18, 0);
        this.leg5.addBox(-1.0f, -1.0f, -1.0f, 16.0f, 2.0f, 2.0f, 0.0f);
        this.leg5.setPos(4.0f, 15.0f, 0.0f);
        this.leg6 = new ModelPart(this, 18, 0);
        this.leg6.addBox(-15.0f, -1.0f, -1.0f, 16.0f, 2.0f, 2.0f, 0.0f);
        this.leg6.setPos(-4.0f, 15.0f, -1.0f);
        this.leg7 = new ModelPart(this, 18, 0);
        this.leg7.addBox(-1.0f, -1.0f, -1.0f, 16.0f, 2.0f, 2.0f, 0.0f);
        this.leg7.setPos(4.0f, 15.0f, -1.0f);
    }

    @Override
    public Iterable<ModelPart> parts() {
        return ImmutableList.of((Object)this.head, (Object)this.body0, (Object)this.body1, (Object)this.leg0, (Object)this.leg1, (Object)this.leg2, (Object)this.leg3, (Object)this.leg4, (Object)this.leg5, (Object)this.leg6, (Object)this.leg7);
    }

    @Override
    public void setupAnim(T t, float f, float f2, float f3, float f4, float f5) {
        this.head.yRot = f4 * 0.017453292f;
        this.head.xRot = f5 * 0.017453292f;
        float f6 = 0.7853982f;
        this.leg0.zRot = -0.7853982f;
        this.leg1.zRot = 0.7853982f;
        this.leg2.zRot = -0.58119464f;
        this.leg3.zRot = 0.58119464f;
        this.leg4.zRot = -0.58119464f;
        this.leg5.zRot = 0.58119464f;
        this.leg6.zRot = -0.7853982f;
        this.leg7.zRot = 0.7853982f;
        float f7 = -0.0f;
        float f8 = 0.3926991f;
        this.leg0.yRot = 0.7853982f;
        this.leg1.yRot = -0.7853982f;
        this.leg2.yRot = 0.3926991f;
        this.leg3.yRot = -0.3926991f;
        this.leg4.yRot = -0.3926991f;
        this.leg5.yRot = 0.3926991f;
        this.leg6.yRot = -0.7853982f;
        this.leg7.yRot = 0.7853982f;
        float f9 = -(Mth.cos(f * 0.6662f * 2.0f + 0.0f) * 0.4f) * f2;
        float f10 = -(Mth.cos(f * 0.6662f * 2.0f + 3.1415927f) * 0.4f) * f2;
        float f11 = -(Mth.cos(f * 0.6662f * 2.0f + 1.5707964f) * 0.4f) * f2;
        float f12 = -(Mth.cos(f * 0.6662f * 2.0f + 4.712389f) * 0.4f) * f2;
        float f13 = Math.abs(Mth.sin(f * 0.6662f + 0.0f) * 0.4f) * f2;
        float f14 = Math.abs(Mth.sin(f * 0.6662f + 3.1415927f) * 0.4f) * f2;
        float f15 = Math.abs(Mth.sin(f * 0.6662f + 1.5707964f) * 0.4f) * f2;
        float f16 = Math.abs(Mth.sin(f * 0.6662f + 4.712389f) * 0.4f) * f2;
        this.leg0.yRot += f9;
        this.leg1.yRot += -f9;
        this.leg2.yRot += f10;
        this.leg3.yRot += -f10;
        this.leg4.yRot += f11;
        this.leg5.yRot += -f11;
        this.leg6.yRot += f12;
        this.leg7.yRot += -f12;
        this.leg0.zRot += f13;
        this.leg1.zRot += -f13;
        this.leg2.zRot += f14;
        this.leg3.zRot += -f14;
        this.leg4.zRot += f15;
        this.leg5.zRot += -f15;
        this.leg6.zRot += f16;
        this.leg7.zRot += -f16;
    }
}

