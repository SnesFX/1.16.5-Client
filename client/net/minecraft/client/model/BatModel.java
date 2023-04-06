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
import net.minecraft.world.entity.ambient.Bat;

public class BatModel
extends ListModel<Bat> {
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart rightWingTip;
    private final ModelPart leftWingTip;

    public BatModel() {
        this.texWidth = 64;
        this.texHeight = 64;
        this.head = new ModelPart(this, 0, 0);
        this.head.addBox(-3.0f, -3.0f, -3.0f, 6.0f, 6.0f, 6.0f);
        ModelPart modelPart = new ModelPart(this, 24, 0);
        modelPart.addBox(-4.0f, -6.0f, -2.0f, 3.0f, 4.0f, 1.0f);
        this.head.addChild(modelPart);
        ModelPart modelPart2 = new ModelPart(this, 24, 0);
        modelPart2.mirror = true;
        modelPart2.addBox(1.0f, -6.0f, -2.0f, 3.0f, 4.0f, 1.0f);
        this.head.addChild(modelPart2);
        this.body = new ModelPart(this, 0, 16);
        this.body.addBox(-3.0f, 4.0f, -3.0f, 6.0f, 12.0f, 6.0f);
        this.body.texOffs(0, 34).addBox(-5.0f, 16.0f, 0.0f, 10.0f, 6.0f, 1.0f);
        this.rightWing = new ModelPart(this, 42, 0);
        this.rightWing.addBox(-12.0f, 1.0f, 1.5f, 10.0f, 16.0f, 1.0f);
        this.rightWingTip = new ModelPart(this, 24, 16);
        this.rightWingTip.setPos(-12.0f, 1.0f, 1.5f);
        this.rightWingTip.addBox(-8.0f, 1.0f, 0.0f, 8.0f, 12.0f, 1.0f);
        this.leftWing = new ModelPart(this, 42, 0);
        this.leftWing.mirror = true;
        this.leftWing.addBox(2.0f, 1.0f, 1.5f, 10.0f, 16.0f, 1.0f);
        this.leftWingTip = new ModelPart(this, 24, 16);
        this.leftWingTip.mirror = true;
        this.leftWingTip.setPos(12.0f, 1.0f, 1.5f);
        this.leftWingTip.addBox(0.0f, 1.0f, 0.0f, 8.0f, 12.0f, 1.0f);
        this.body.addChild(this.rightWing);
        this.body.addChild(this.leftWing);
        this.rightWing.addChild(this.rightWingTip);
        this.leftWing.addChild(this.leftWingTip);
    }

    @Override
    public Iterable<ModelPart> parts() {
        return ImmutableList.of((Object)this.head, (Object)this.body);
    }

    @Override
    public void setupAnim(Bat bat, float f, float f2, float f3, float f4, float f5) {
        if (bat.isResting()) {
            this.head.xRot = f5 * 0.017453292f;
            this.head.yRot = 3.1415927f - f4 * 0.017453292f;
            this.head.zRot = 3.1415927f;
            this.head.setPos(0.0f, -2.0f, 0.0f);
            this.rightWing.setPos(-3.0f, 0.0f, 3.0f);
            this.leftWing.setPos(3.0f, 0.0f, 3.0f);
            this.body.xRot = 3.1415927f;
            this.rightWing.xRot = -0.15707964f;
            this.rightWing.yRot = -1.2566371f;
            this.rightWingTip.yRot = -1.7278761f;
            this.leftWing.xRot = this.rightWing.xRot;
            this.leftWing.yRot = -this.rightWing.yRot;
            this.leftWingTip.yRot = -this.rightWingTip.yRot;
        } else {
            this.head.xRot = f5 * 0.017453292f;
            this.head.yRot = f4 * 0.017453292f;
            this.head.zRot = 0.0f;
            this.head.setPos(0.0f, 0.0f, 0.0f);
            this.rightWing.setPos(0.0f, 0.0f, 0.0f);
            this.leftWing.setPos(0.0f, 0.0f, 0.0f);
            this.body.xRot = 0.7853982f + Mth.cos(f3 * 0.1f) * 0.15f;
            this.body.yRot = 0.0f;
            this.rightWing.yRot = Mth.cos(f3 * 1.3f) * 3.1415927f * 0.25f;
            this.leftWing.yRot = -this.rightWing.yRot;
            this.rightWingTip.yRot = this.rightWing.yRot * 0.5f;
            this.leftWingTip.yRot = -this.rightWing.yRot * 0.5f;
        }
    }
}

