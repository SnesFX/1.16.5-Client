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
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

public class HorseModel<T extends AbstractHorse>
extends AgeableListModel<T> {
    protected final ModelPart body;
    protected final ModelPart headParts;
    private final ModelPart leg1;
    private final ModelPart leg2;
    private final ModelPart leg3;
    private final ModelPart leg4;
    private final ModelPart babyLeg1;
    private final ModelPart babyLeg2;
    private final ModelPart babyLeg3;
    private final ModelPart babyLeg4;
    private final ModelPart tail;
    private final ModelPart[] saddleParts;
    private final ModelPart[] ridingParts;

    public HorseModel(float f) {
        super(true, 16.2f, 1.36f, 2.7272f, 2.0f, 20.0f);
        this.texWidth = 64;
        this.texHeight = 64;
        this.body = new ModelPart(this, 0, 32);
        this.body.addBox(-5.0f, -8.0f, -17.0f, 10.0f, 10.0f, 22.0f, 0.05f);
        this.body.setPos(0.0f, 11.0f, 5.0f);
        this.headParts = new ModelPart(this, 0, 35);
        this.headParts.addBox(-2.05f, -6.0f, -2.0f, 4.0f, 12.0f, 7.0f);
        this.headParts.xRot = 0.5235988f;
        ModelPart modelPart = new ModelPart(this, 0, 13);
        modelPart.addBox(-3.0f, -11.0f, -2.0f, 6.0f, 5.0f, 7.0f, f);
        ModelPart modelPart2 = new ModelPart(this, 56, 36);
        modelPart2.addBox(-1.0f, -11.0f, 5.01f, 2.0f, 16.0f, 2.0f, f);
        ModelPart modelPart3 = new ModelPart(this, 0, 25);
        modelPart3.addBox(-2.0f, -11.0f, -7.0f, 4.0f, 5.0f, 5.0f, f);
        this.headParts.addChild(modelPart);
        this.headParts.addChild(modelPart2);
        this.headParts.addChild(modelPart3);
        this.addEarModels(this.headParts);
        this.leg1 = new ModelPart(this, 48, 21);
        this.leg1.mirror = true;
        this.leg1.addBox(-3.0f, -1.01f, -1.0f, 4.0f, 11.0f, 4.0f, f);
        this.leg1.setPos(4.0f, 14.0f, 7.0f);
        this.leg2 = new ModelPart(this, 48, 21);
        this.leg2.addBox(-1.0f, -1.01f, -1.0f, 4.0f, 11.0f, 4.0f, f);
        this.leg2.setPos(-4.0f, 14.0f, 7.0f);
        this.leg3 = new ModelPart(this, 48, 21);
        this.leg3.mirror = true;
        this.leg3.addBox(-3.0f, -1.01f, -1.9f, 4.0f, 11.0f, 4.0f, f);
        this.leg3.setPos(4.0f, 6.0f, -12.0f);
        this.leg4 = new ModelPart(this, 48, 21);
        this.leg4.addBox(-1.0f, -1.01f, -1.9f, 4.0f, 11.0f, 4.0f, f);
        this.leg4.setPos(-4.0f, 6.0f, -12.0f);
        float f2 = 5.5f;
        this.babyLeg1 = new ModelPart(this, 48, 21);
        this.babyLeg1.mirror = true;
        this.babyLeg1.addBox(-3.0f, -1.01f, -1.0f, 4.0f, 11.0f, 4.0f, f, f + 5.5f, f);
        this.babyLeg1.setPos(4.0f, 14.0f, 7.0f);
        this.babyLeg2 = new ModelPart(this, 48, 21);
        this.babyLeg2.addBox(-1.0f, -1.01f, -1.0f, 4.0f, 11.0f, 4.0f, f, f + 5.5f, f);
        this.babyLeg2.setPos(-4.0f, 14.0f, 7.0f);
        this.babyLeg3 = new ModelPart(this, 48, 21);
        this.babyLeg3.mirror = true;
        this.babyLeg3.addBox(-3.0f, -1.01f, -1.9f, 4.0f, 11.0f, 4.0f, f, f + 5.5f, f);
        this.babyLeg3.setPos(4.0f, 6.0f, -12.0f);
        this.babyLeg4 = new ModelPart(this, 48, 21);
        this.babyLeg4.addBox(-1.0f, -1.01f, -1.9f, 4.0f, 11.0f, 4.0f, f, f + 5.5f, f);
        this.babyLeg4.setPos(-4.0f, 6.0f, -12.0f);
        this.tail = new ModelPart(this, 42, 36);
        this.tail.addBox(-1.5f, 0.0f, 0.0f, 3.0f, 14.0f, 4.0f, f);
        this.tail.setPos(0.0f, -5.0f, 2.0f);
        this.tail.xRot = 0.5235988f;
        this.body.addChild(this.tail);
        ModelPart modelPart4 = new ModelPart(this, 26, 0);
        modelPart4.addBox(-5.0f, -8.0f, -9.0f, 10.0f, 9.0f, 9.0f, 0.5f);
        this.body.addChild(modelPart4);
        ModelPart modelPart5 = new ModelPart(this, 29, 5);
        modelPart5.addBox(2.0f, -9.0f, -6.0f, 1.0f, 2.0f, 2.0f, f);
        this.headParts.addChild(modelPart5);
        ModelPart modelPart6 = new ModelPart(this, 29, 5);
        modelPart6.addBox(-3.0f, -9.0f, -6.0f, 1.0f, 2.0f, 2.0f, f);
        this.headParts.addChild(modelPart6);
        ModelPart modelPart7 = new ModelPart(this, 32, 2);
        modelPart7.addBox(3.1f, -6.0f, -8.0f, 0.0f, 3.0f, 16.0f, f);
        modelPart7.xRot = -0.5235988f;
        this.headParts.addChild(modelPart7);
        ModelPart modelPart8 = new ModelPart(this, 32, 2);
        modelPart8.addBox(-3.1f, -6.0f, -8.0f, 0.0f, 3.0f, 16.0f, f);
        modelPart8.xRot = -0.5235988f;
        this.headParts.addChild(modelPart8);
        ModelPart modelPart9 = new ModelPart(this, 1, 1);
        modelPart9.addBox(-3.0f, -11.0f, -1.9f, 6.0f, 5.0f, 6.0f, 0.2f);
        this.headParts.addChild(modelPart9);
        ModelPart modelPart10 = new ModelPart(this, 19, 0);
        modelPart10.addBox(-2.0f, -11.0f, -4.0f, 4.0f, 5.0f, 2.0f, 0.2f);
        this.headParts.addChild(modelPart10);
        this.saddleParts = new ModelPart[]{modelPart4, modelPart5, modelPart6, modelPart9, modelPart10};
        this.ridingParts = new ModelPart[]{modelPart7, modelPart8};
    }

    protected void addEarModels(ModelPart modelPart) {
        ModelPart modelPart2 = new ModelPart(this, 19, 16);
        modelPart2.addBox(0.55f, -13.0f, 4.0f, 2.0f, 3.0f, 1.0f, -0.001f);
        ModelPart modelPart3 = new ModelPart(this, 19, 16);
        modelPart3.addBox(-2.55f, -13.0f, 4.0f, 2.0f, 3.0f, 1.0f, -0.001f);
        modelPart.addChild(modelPart2);
        modelPart.addChild(modelPart3);
    }

    @Override
    public void setupAnim(T t, float f, float f2, float f3, float f4, float f5) {
        boolean bl = ((AbstractHorse)t).isSaddled();
        boolean bl2 = ((Entity)t).isVehicle();
        for (ModelPart modelPart : this.saddleParts) {
            modelPart.visible = bl;
        }
        for (ModelPart modelPart : this.ridingParts) {
            modelPart.visible = bl2 && bl;
        }
        this.body.y = 11.0f;
    }

    @Override
    public Iterable<ModelPart> headParts() {
        return ImmutableList.of((Object)this.headParts);
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of((Object)this.body, (Object)this.leg1, (Object)this.leg2, (Object)this.leg3, (Object)this.leg4, (Object)this.babyLeg1, (Object)this.babyLeg2, (Object)this.babyLeg3, (Object)this.babyLeg4);
    }

    @Override
    public void prepareMobModel(T t, float f, float f2, float f3) {
        super.prepareMobModel(t, f, f2, f3);
        float f4 = Mth.rotlerp(((AbstractHorse)t).yBodyRotO, ((AbstractHorse)t).yBodyRot, f3);
        float f5 = Mth.rotlerp(((AbstractHorse)t).yHeadRotO, ((AbstractHorse)t).yHeadRot, f3);
        float f6 = Mth.lerp(f3, ((AbstractHorse)t).xRotO, ((AbstractHorse)t).xRot);
        float f7 = f5 - f4;
        float f8 = f6 * 0.017453292f;
        if (f7 > 20.0f) {
            f7 = 20.0f;
        }
        if (f7 < -20.0f) {
            f7 = -20.0f;
        }
        if (f2 > 0.2f) {
            f8 += Mth.cos(f * 0.4f) * 0.15f * f2;
        }
        float f9 = ((AbstractHorse)t).getEatAnim(f3);
        float f10 = ((AbstractHorse)t).getStandAnim(f3);
        float f11 = 1.0f - f10;
        float f12 = ((AbstractHorse)t).getMouthAnim(f3);
        boolean bl = ((AbstractHorse)t).tailCounter != 0;
        float f13 = (float)((AbstractHorse)t).tickCount + f3;
        this.headParts.y = 4.0f;
        this.headParts.z = -12.0f;
        this.body.xRot = 0.0f;
        this.headParts.xRot = 0.5235988f + f8;
        this.headParts.yRot = f7 * 0.017453292f;
        float f14 = ((Entity)t).isInWater() ? 0.2f : 1.0f;
        float f15 = Mth.cos(f14 * f * 0.6662f + 3.1415927f);
        float f16 = f15 * 0.8f * f2;
        float f17 = (1.0f - Math.max(f10, f9)) * (0.5235988f + f8 + f12 * Mth.sin(f13) * 0.05f);
        this.headParts.xRot = f10 * (0.2617994f + f8) + f9 * (2.1816616f + Mth.sin(f13) * 0.05f) + f17;
        this.headParts.yRot = f10 * f7 * 0.017453292f + (1.0f - Math.max(f10, f9)) * this.headParts.yRot;
        this.headParts.y = f10 * -4.0f + f9 * 11.0f + (1.0f - Math.max(f10, f9)) * this.headParts.y;
        this.headParts.z = f10 * -4.0f + f9 * -12.0f + (1.0f - Math.max(f10, f9)) * this.headParts.z;
        this.body.xRot = f10 * -0.7853982f + f11 * this.body.xRot;
        float f18 = 0.2617994f * f10;
        float f19 = Mth.cos(f13 * 0.6f + 3.1415927f);
        this.leg3.y = 2.0f * f10 + 14.0f * f11;
        this.leg3.z = -6.0f * f10 - 10.0f * f11;
        this.leg4.y = this.leg3.y;
        this.leg4.z = this.leg3.z;
        float f20 = (-1.0471976f + f19) * f10 + f16 * f11;
        float f21 = (-1.0471976f - f19) * f10 - f16 * f11;
        this.leg1.xRot = f18 - f15 * 0.5f * f2 * f11;
        this.leg2.xRot = f18 + f15 * 0.5f * f2 * f11;
        this.leg3.xRot = f20;
        this.leg4.xRot = f21;
        this.tail.xRot = 0.5235988f + f2 * 0.75f;
        this.tail.y = -5.0f + f2;
        this.tail.z = 2.0f + f2 * 2.0f;
        this.tail.yRot = bl ? Mth.cos(f13 * 0.7f) : 0.0f;
        this.babyLeg1.y = this.leg1.y;
        this.babyLeg1.z = this.leg1.z;
        this.babyLeg1.xRot = this.leg1.xRot;
        this.babyLeg2.y = this.leg2.y;
        this.babyLeg2.z = this.leg2.z;
        this.babyLeg2.xRot = this.leg2.xRot;
        this.babyLeg3.y = this.leg3.y;
        this.babyLeg3.z = this.leg3.z;
        this.babyLeg3.xRot = this.leg3.xRot;
        this.babyLeg4.y = this.leg4.y;
        this.babyLeg4.z = this.leg4.z;
        this.babyLeg4.xRot = this.leg4.xRot;
        boolean bl2 = ((AgableMob)t).isBaby();
        this.leg1.visible = !bl2;
        this.leg2.visible = !bl2;
        this.leg3.visible = !bl2;
        this.leg4.visible = !bl2;
        this.babyLeg1.visible = bl2;
        this.babyLeg2.visible = bl2;
        this.babyLeg3.visible = bl2;
        this.babyLeg4.visible = bl2;
        this.body.y = bl2 ? 10.8f : 0.0f;
    }
}

