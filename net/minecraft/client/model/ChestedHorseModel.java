/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.model;

import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

public class ChestedHorseModel<T extends AbstractChestedHorse>
extends HorseModel<T> {
    private final ModelPart boxL = new ModelPart(this, 26, 21);
    private final ModelPart boxR;

    public ChestedHorseModel(float f) {
        super(f);
        this.boxL.addBox(-4.0f, 0.0f, -2.0f, 8.0f, 8.0f, 3.0f);
        this.boxR = new ModelPart(this, 26, 21);
        this.boxR.addBox(-4.0f, 0.0f, -2.0f, 8.0f, 8.0f, 3.0f);
        this.boxL.yRot = -1.5707964f;
        this.boxR.yRot = 1.5707964f;
        this.boxL.setPos(6.0f, -8.0f, 0.0f);
        this.boxR.setPos(-6.0f, -8.0f, 0.0f);
        this.body.addChild(this.boxL);
        this.body.addChild(this.boxR);
    }

    @Override
    protected void addEarModels(ModelPart modelPart) {
        ModelPart modelPart2 = new ModelPart(this, 0, 12);
        modelPart2.addBox(-1.0f, -7.0f, 0.0f, 2.0f, 7.0f, 1.0f);
        modelPart2.setPos(1.25f, -10.0f, 4.0f);
        ModelPart modelPart3 = new ModelPart(this, 0, 12);
        modelPart3.addBox(-1.0f, -7.0f, 0.0f, 2.0f, 7.0f, 1.0f);
        modelPart3.setPos(-1.25f, -10.0f, 4.0f);
        modelPart2.xRot = 0.2617994f;
        modelPart2.zRot = 0.2617994f;
        modelPart3.xRot = 0.2617994f;
        modelPart3.zRot = -0.2617994f;
        modelPart.addChild(modelPart2);
        modelPart.addChild(modelPart3);
    }

    @Override
    public void setupAnim(T t, float f, float f2, float f3, float f4, float f5) {
        super.setupAnim(t, f, f2, f3, f4, f5);
        if (((AbstractChestedHorse)t).hasChest()) {
            this.boxL.visible = true;
            this.boxR.visible = true;
        } else {
            this.boxL.visible = false;
            this.boxR.visible = false;
        }
    }
}

