/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Sheep;

public class SheepModel<T extends Sheep>
extends QuadrupedModel<T> {
    private float headXRot;

    public SheepModel() {
        super(12, 0.0f, false, 8.0f, 4.0f, 2.0f, 2.0f, 24);
        this.head = new ModelPart(this, 0, 0);
        this.head.addBox(-3.0f, -4.0f, -6.0f, 6.0f, 6.0f, 8.0f, 0.0f);
        this.head.setPos(0.0f, 6.0f, -8.0f);
        this.body = new ModelPart(this, 28, 8);
        this.body.addBox(-4.0f, -10.0f, -7.0f, 8.0f, 16.0f, 6.0f, 0.0f);
        this.body.setPos(0.0f, 5.0f, 2.0f);
    }

    @Override
    public void prepareMobModel(T t, float f, float f2, float f3) {
        super.prepareMobModel(t, f, f2, f3);
        this.head.y = 6.0f + ((Sheep)t).getHeadEatPositionScale(f3) * 9.0f;
        this.headXRot = ((Sheep)t).getHeadEatAngleScale(f3);
    }

    @Override
    public void setupAnim(T t, float f, float f2, float f3, float f4, float f5) {
        super.setupAnim(t, f, f2, f3, f4, f5);
        this.head.xRot = this.headXRot;
    }
}

