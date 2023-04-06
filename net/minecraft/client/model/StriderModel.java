/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Strider;

public class StriderModel<T extends Strider>
extends ListModel<T> {
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart body;
    private final ModelPart bristle0;
    private final ModelPart bristle1;
    private final ModelPart bristle2;
    private final ModelPart bristle3;
    private final ModelPart bristle4;
    private final ModelPart bristle5;

    public StriderModel() {
        this.texWidth = 64;
        this.texHeight = 128;
        this.rightLeg = new ModelPart(this, 0, 32);
        this.rightLeg.setPos(-4.0f, 8.0f, 0.0f);
        this.rightLeg.addBox(-2.0f, 0.0f, -2.0f, 4.0f, 16.0f, 4.0f, 0.0f);
        this.leftLeg = new ModelPart(this, 0, 55);
        this.leftLeg.setPos(4.0f, 8.0f, 0.0f);
        this.leftLeg.addBox(-2.0f, 0.0f, -2.0f, 4.0f, 16.0f, 4.0f, 0.0f);
        this.body = new ModelPart(this, 0, 0);
        this.body.setPos(0.0f, 1.0f, 0.0f);
        this.body.addBox(-8.0f, -6.0f, -8.0f, 16.0f, 14.0f, 16.0f, 0.0f);
        this.bristle0 = new ModelPart(this, 16, 65);
        this.bristle0.setPos(-8.0f, 4.0f, -8.0f);
        this.bristle0.addBox(-12.0f, 0.0f, 0.0f, 12.0f, 0.0f, 16.0f, 0.0f, true);
        this.setRotationAngle(this.bristle0, 0.0f, 0.0f, -1.2217305f);
        this.bristle1 = new ModelPart(this, 16, 49);
        this.bristle1.setPos(-8.0f, -1.0f, -8.0f);
        this.bristle1.addBox(-12.0f, 0.0f, 0.0f, 12.0f, 0.0f, 16.0f, 0.0f, true);
        this.setRotationAngle(this.bristle1, 0.0f, 0.0f, -1.134464f);
        this.bristle2 = new ModelPart(this, 16, 33);
        this.bristle2.setPos(-8.0f, -5.0f, -8.0f);
        this.bristle2.addBox(-12.0f, 0.0f, 0.0f, 12.0f, 0.0f, 16.0f, 0.0f, true);
        this.setRotationAngle(this.bristle2, 0.0f, 0.0f, -0.87266463f);
        this.bristle3 = new ModelPart(this, 16, 33);
        this.bristle3.setPos(8.0f, -6.0f, -8.0f);
        this.bristle3.addBox(0.0f, 0.0f, 0.0f, 12.0f, 0.0f, 16.0f, 0.0f);
        this.setRotationAngle(this.bristle3, 0.0f, 0.0f, 0.87266463f);
        this.bristle4 = new ModelPart(this, 16, 49);
        this.bristle4.setPos(8.0f, -2.0f, -8.0f);
        this.bristle4.addBox(0.0f, 0.0f, 0.0f, 12.0f, 0.0f, 16.0f, 0.0f);
        this.setRotationAngle(this.bristle4, 0.0f, 0.0f, 1.134464f);
        this.bristle5 = new ModelPart(this, 16, 65);
        this.bristle5.setPos(8.0f, 3.0f, -8.0f);
        this.bristle5.addBox(0.0f, 0.0f, 0.0f, 12.0f, 0.0f, 16.0f, 0.0f);
        this.setRotationAngle(this.bristle5, 0.0f, 0.0f, 1.2217305f);
        this.body.addChild(this.bristle0);
        this.body.addChild(this.bristle1);
        this.body.addChild(this.bristle2);
        this.body.addChild(this.bristle3);
        this.body.addChild(this.bristle4);
        this.body.addChild(this.bristle5);
    }

    @Override
    public void setupAnim(Strider strider, float f, float f2, float f3, float f4, float f5) {
        f2 = Math.min(0.25f, f2);
        if (strider.getPassengers().size() <= 0) {
            this.body.xRot = f5 * 0.017453292f;
            this.body.yRot = f4 * 0.017453292f;
        } else {
            this.body.xRot = 0.0f;
            this.body.yRot = 0.0f;
        }
        float f6 = 1.5f;
        this.body.zRot = 0.1f * Mth.sin(f * 1.5f) * 4.0f * f2;
        this.body.y = 2.0f;
        this.body.y -= 2.0f * Mth.cos(f * 1.5f) * 2.0f * f2;
        this.leftLeg.xRot = Mth.sin(f * 1.5f * 0.5f) * 2.0f * f2;
        this.rightLeg.xRot = Mth.sin(f * 1.5f * 0.5f + 3.1415927f) * 2.0f * f2;
        this.leftLeg.zRot = 0.17453292f * Mth.cos(f * 1.5f * 0.5f) * f2;
        this.rightLeg.zRot = 0.17453292f * Mth.cos(f * 1.5f * 0.5f + 3.1415927f) * f2;
        this.leftLeg.y = 8.0f + 2.0f * Mth.sin(f * 1.5f * 0.5f + 3.1415927f) * 2.0f * f2;
        this.rightLeg.y = 8.0f + 2.0f * Mth.sin(f * 1.5f * 0.5f) * 2.0f * f2;
        this.bristle0.zRot = -1.2217305f;
        this.bristle1.zRot = -1.134464f;
        this.bristle2.zRot = -0.87266463f;
        this.bristle3.zRot = 0.87266463f;
        this.bristle4.zRot = 1.134464f;
        this.bristle5.zRot = 1.2217305f;
        float f7 = Mth.cos(f * 1.5f + 3.1415927f) * f2;
        this.bristle0.zRot += f7 * 1.3f;
        this.bristle1.zRot += f7 * 1.2f;
        this.bristle2.zRot += f7 * 0.6f;
        this.bristle3.zRot += f7 * 0.6f;
        this.bristle4.zRot += f7 * 1.2f;
        this.bristle5.zRot += f7 * 1.3f;
        float f8 = 1.0f;
        float f9 = 1.0f;
        this.bristle0.zRot += 0.05f * Mth.sin(f3 * 1.0f * -0.4f);
        this.bristle1.zRot += 0.1f * Mth.sin(f3 * 1.0f * 0.2f);
        this.bristle2.zRot += 0.1f * Mth.sin(f3 * 1.0f * 0.4f);
        this.bristle3.zRot += 0.1f * Mth.sin(f3 * 1.0f * 0.4f);
        this.bristle4.zRot += 0.1f * Mth.sin(f3 * 1.0f * 0.2f);
        this.bristle5.zRot += 0.05f * Mth.sin(f3 * 1.0f * -0.4f);
    }

    public void setRotationAngle(ModelPart modelPart, float f, float f2, float f3) {
        modelPart.xRot = f;
        modelPart.yRot = f2;
        modelPart.zRot = f3;
    }

    @Override
    public Iterable<ModelPart> parts() {
        return ImmutableList.of((Object)this.body, (Object)this.leftLeg, (Object)this.rightLeg);
    }
}

