/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class WitchModel<T extends Entity>
extends VillagerModel<T> {
    private boolean holdingItem;
    private final ModelPart mole = new ModelPart(this).setTexSize(64, 128);

    public WitchModel(float f) {
        super(f, 64, 128);
        this.mole.setPos(0.0f, -2.0f, 0.0f);
        this.mole.texOffs(0, 0).addBox(0.0f, 3.0f, -6.75f, 1.0f, 1.0f, 1.0f, -0.25f);
        this.nose.addChild(this.mole);
        this.head = new ModelPart(this).setTexSize(64, 128);
        this.head.setPos(0.0f, 0.0f, 0.0f);
        this.head.texOffs(0, 0).addBox(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f, f);
        this.hat = new ModelPart(this).setTexSize(64, 128);
        this.hat.setPos(-5.0f, -10.03125f, -5.0f);
        this.hat.texOffs(0, 64).addBox(0.0f, 0.0f, 0.0f, 10.0f, 2.0f, 10.0f);
        this.head.addChild(this.hat);
        this.head.addChild(this.nose);
        ModelPart modelPart = new ModelPart(this).setTexSize(64, 128);
        modelPart.setPos(1.75f, -4.0f, 2.0f);
        modelPart.texOffs(0, 76).addBox(0.0f, 0.0f, 0.0f, 7.0f, 4.0f, 7.0f);
        modelPart.xRot = -0.05235988f;
        modelPart.zRot = 0.02617994f;
        this.hat.addChild(modelPart);
        ModelPart modelPart2 = new ModelPart(this).setTexSize(64, 128);
        modelPart2.setPos(1.75f, -4.0f, 2.0f);
        modelPart2.texOffs(0, 87).addBox(0.0f, 0.0f, 0.0f, 4.0f, 4.0f, 4.0f);
        modelPart2.xRot = -0.10471976f;
        modelPart2.zRot = 0.05235988f;
        modelPart.addChild(modelPart2);
        ModelPart modelPart3 = new ModelPart(this).setTexSize(64, 128);
        modelPart3.setPos(1.75f, -2.0f, 2.0f);
        modelPart3.texOffs(0, 95).addBox(0.0f, 0.0f, 0.0f, 1.0f, 2.0f, 1.0f, 0.25f);
        modelPart3.xRot = -0.20943952f;
        modelPart3.zRot = 0.10471976f;
        modelPart2.addChild(modelPart3);
    }

    @Override
    public void setupAnim(T t, float f, float f2, float f3, float f4, float f5) {
        super.setupAnim(t, f, f2, f3, f4, f5);
        this.nose.setPos(0.0f, -2.0f, 0.0f);
        float f6 = 0.01f * (float)(((Entity)t).getId() % 10);
        this.nose.xRot = Mth.sin((float)((Entity)t).tickCount * f6) * 4.5f * 0.017453292f;
        this.nose.yRot = 0.0f;
        this.nose.zRot = Mth.cos((float)((Entity)t).tickCount * f6) * 2.5f * 0.017453292f;
        if (this.holdingItem) {
            this.nose.setPos(0.0f, 1.0f, -1.5f);
            this.nose.xRot = -0.9f;
        }
    }

    public ModelPart getNose() {
        return this.nose;
    }

    public void setHoldingItem(boolean bl) {
        this.holdingItem = bl;
    }
}

