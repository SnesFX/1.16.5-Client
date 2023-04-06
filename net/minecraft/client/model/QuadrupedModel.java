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

public class QuadrupedModel<T extends Entity>
extends AgeableListModel<T> {
    protected ModelPart head = new ModelPart(this, 0, 0);
    protected ModelPart body;
    protected ModelPart leg0;
    protected ModelPart leg1;
    protected ModelPart leg2;
    protected ModelPart leg3;

    public QuadrupedModel(int n, float f, boolean bl, float f2, float f3, float f4, float f5, int n2) {
        super(bl, f2, f3, f4, f5, n2);
        this.head.addBox(-4.0f, -4.0f, -8.0f, 8.0f, 8.0f, 8.0f, f);
        this.head.setPos(0.0f, 18 - n, -6.0f);
        this.body = new ModelPart(this, 28, 8);
        this.body.addBox(-5.0f, -10.0f, -7.0f, 10.0f, 16.0f, 8.0f, f);
        this.body.setPos(0.0f, 17 - n, 2.0f);
        this.leg0 = new ModelPart(this, 0, 16);
        this.leg0.addBox(-2.0f, 0.0f, -2.0f, 4.0f, (float)n, 4.0f, f);
        this.leg0.setPos(-3.0f, 24 - n, 7.0f);
        this.leg1 = new ModelPart(this, 0, 16);
        this.leg1.addBox(-2.0f, 0.0f, -2.0f, 4.0f, (float)n, 4.0f, f);
        this.leg1.setPos(3.0f, 24 - n, 7.0f);
        this.leg2 = new ModelPart(this, 0, 16);
        this.leg2.addBox(-2.0f, 0.0f, -2.0f, 4.0f, (float)n, 4.0f, f);
        this.leg2.setPos(-3.0f, 24 - n, -5.0f);
        this.leg3 = new ModelPart(this, 0, 16);
        this.leg3.addBox(-2.0f, 0.0f, -2.0f, 4.0f, (float)n, 4.0f, f);
        this.leg3.setPos(3.0f, 24 - n, -5.0f);
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
        this.head.xRot = f5 * 0.017453292f;
        this.head.yRot = f4 * 0.017453292f;
        this.body.xRot = 1.5707964f;
        this.leg0.xRot = Mth.cos(f * 0.6662f) * 1.4f * f2;
        this.leg1.xRot = Mth.cos(f * 0.6662f + 3.1415927f) * 1.4f * f2;
        this.leg2.xRot = Mth.cos(f * 0.6662f + 3.1415927f) * 1.4f * f2;
        this.leg3.xRot = Mth.cos(f * 0.6662f) * 1.4f * f2;
    }
}

