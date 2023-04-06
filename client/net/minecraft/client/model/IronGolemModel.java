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
import net.minecraft.world.entity.animal.IronGolem;

public class IronGolemModel<T extends IronGolem>
extends ListModel<T> {
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart arm0;
    private final ModelPart arm1;
    private final ModelPart leg0;
    private final ModelPart leg1;

    public IronGolemModel() {
        int n = 128;
        int n2 = 128;
        this.head = new ModelPart(this).setTexSize(128, 128);
        this.head.setPos(0.0f, -7.0f, -2.0f);
        this.head.texOffs(0, 0).addBox(-4.0f, -12.0f, -5.5f, 8.0f, 10.0f, 8.0f, 0.0f);
        this.head.texOffs(24, 0).addBox(-1.0f, -5.0f, -7.5f, 2.0f, 4.0f, 2.0f, 0.0f);
        this.body = new ModelPart(this).setTexSize(128, 128);
        this.body.setPos(0.0f, -7.0f, 0.0f);
        this.body.texOffs(0, 40).addBox(-9.0f, -2.0f, -6.0f, 18.0f, 12.0f, 11.0f, 0.0f);
        this.body.texOffs(0, 70).addBox(-4.5f, 10.0f, -3.0f, 9.0f, 5.0f, 6.0f, 0.5f);
        this.arm0 = new ModelPart(this).setTexSize(128, 128);
        this.arm0.setPos(0.0f, -7.0f, 0.0f);
        this.arm0.texOffs(60, 21).addBox(-13.0f, -2.5f, -3.0f, 4.0f, 30.0f, 6.0f, 0.0f);
        this.arm1 = new ModelPart(this).setTexSize(128, 128);
        this.arm1.setPos(0.0f, -7.0f, 0.0f);
        this.arm1.texOffs(60, 58).addBox(9.0f, -2.5f, -3.0f, 4.0f, 30.0f, 6.0f, 0.0f);
        this.leg0 = new ModelPart(this, 0, 22).setTexSize(128, 128);
        this.leg0.setPos(-4.0f, 11.0f, 0.0f);
        this.leg0.texOffs(37, 0).addBox(-3.5f, -3.0f, -3.0f, 6.0f, 16.0f, 5.0f, 0.0f);
        this.leg1 = new ModelPart(this, 0, 22).setTexSize(128, 128);
        this.leg1.mirror = true;
        this.leg1.texOffs(60, 0).setPos(5.0f, 11.0f, 0.0f);
        this.leg1.addBox(-3.5f, -3.0f, -3.0f, 6.0f, 16.0f, 5.0f, 0.0f);
    }

    @Override
    public Iterable<ModelPart> parts() {
        return ImmutableList.of((Object)this.head, (Object)this.body, (Object)this.leg0, (Object)this.leg1, (Object)this.arm0, (Object)this.arm1);
    }

    @Override
    public void setupAnim(T t, float f, float f2, float f3, float f4, float f5) {
        this.head.yRot = f4 * 0.017453292f;
        this.head.xRot = f5 * 0.017453292f;
        this.leg0.xRot = -1.5f * Mth.triangleWave(f, 13.0f) * f2;
        this.leg1.xRot = 1.5f * Mth.triangleWave(f, 13.0f) * f2;
        this.leg0.yRot = 0.0f;
        this.leg1.yRot = 0.0f;
    }

    @Override
    public void prepareMobModel(T t, float f, float f2, float f3) {
        int n = ((IronGolem)t).getAttackAnimationTick();
        if (n > 0) {
            this.arm0.xRot = -2.0f + 1.5f * Mth.triangleWave((float)n - f3, 10.0f);
            this.arm1.xRot = -2.0f + 1.5f * Mth.triangleWave((float)n - f3, 10.0f);
        } else {
            int n2 = ((IronGolem)t).getOfferFlowerTick();
            if (n2 > 0) {
                this.arm0.xRot = -0.8f + 0.025f * Mth.triangleWave(n2, 70.0f);
                this.arm1.xRot = 0.0f;
            } else {
                this.arm0.xRot = (-0.2f + 1.5f * Mth.triangleWave(f, 13.0f)) * f2;
                this.arm1.xRot = (-0.2f - 1.5f * Mth.triangleWave(f, 13.0f)) * f2;
            }
        }
    }

    public ModelPart getFlowerHoldingArm() {
        return this.arm0;
    }
}

