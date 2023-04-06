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

public class ChickenModel<T extends Entity>
extends AgeableListModel<T> {
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart leg0;
    private final ModelPart leg1;
    private final ModelPart wing0;
    private final ModelPart wing1;
    private final ModelPart beak;
    private final ModelPart redThing;

    public ChickenModel() {
        int n = 16;
        this.head = new ModelPart(this, 0, 0);
        this.head.addBox(-2.0f, -6.0f, -2.0f, 4.0f, 6.0f, 3.0f, 0.0f);
        this.head.setPos(0.0f, 15.0f, -4.0f);
        this.beak = new ModelPart(this, 14, 0);
        this.beak.addBox(-2.0f, -4.0f, -4.0f, 4.0f, 2.0f, 2.0f, 0.0f);
        this.beak.setPos(0.0f, 15.0f, -4.0f);
        this.redThing = new ModelPart(this, 14, 4);
        this.redThing.addBox(-1.0f, -2.0f, -3.0f, 2.0f, 2.0f, 2.0f, 0.0f);
        this.redThing.setPos(0.0f, 15.0f, -4.0f);
        this.body = new ModelPart(this, 0, 9);
        this.body.addBox(-3.0f, -4.0f, -3.0f, 6.0f, 8.0f, 6.0f, 0.0f);
        this.body.setPos(0.0f, 16.0f, 0.0f);
        this.leg0 = new ModelPart(this, 26, 0);
        this.leg0.addBox(-1.0f, 0.0f, -3.0f, 3.0f, 5.0f, 3.0f);
        this.leg0.setPos(-2.0f, 19.0f, 1.0f);
        this.leg1 = new ModelPart(this, 26, 0);
        this.leg1.addBox(-1.0f, 0.0f, -3.0f, 3.0f, 5.0f, 3.0f);
        this.leg1.setPos(1.0f, 19.0f, 1.0f);
        this.wing0 = new ModelPart(this, 24, 13);
        this.wing0.addBox(0.0f, 0.0f, -3.0f, 1.0f, 4.0f, 6.0f);
        this.wing0.setPos(-4.0f, 13.0f, 0.0f);
        this.wing1 = new ModelPart(this, 24, 13);
        this.wing1.addBox(-1.0f, 0.0f, -3.0f, 1.0f, 4.0f, 6.0f);
        this.wing1.setPos(4.0f, 13.0f, 0.0f);
    }

    @Override
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of((Object)this.head, (Object)this.beak, (Object)this.redThing);
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of((Object)this.body, (Object)this.leg0, (Object)this.leg1, (Object)this.wing0, (Object)this.wing1);
    }

    @Override
    public void setupAnim(T t, float f, float f2, float f3, float f4, float f5) {
        this.head.xRot = f5 * 0.017453292f;
        this.head.yRot = f4 * 0.017453292f;
        this.beak.xRot = this.head.xRot;
        this.beak.yRot = this.head.yRot;
        this.redThing.xRot = this.head.xRot;
        this.redThing.yRot = this.head.yRot;
        this.body.xRot = 1.5707964f;
        this.leg0.xRot = Mth.cos(f * 0.6662f) * 1.4f * f2;
        this.leg1.xRot = Mth.cos(f * 0.6662f + 3.1415927f) * 1.4f * f2;
        this.wing0.zRot = f3;
        this.wing1.zRot = -f3;
    }
}

