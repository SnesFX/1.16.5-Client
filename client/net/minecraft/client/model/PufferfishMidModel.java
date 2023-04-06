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

public class PufferfishMidModel<T extends Entity>
extends ListModel<T> {
    private final ModelPart cube;
    private final ModelPart finBlue0;
    private final ModelPart finBlue1;
    private final ModelPart finTop0;
    private final ModelPart finTop1;
    private final ModelPart finSide0;
    private final ModelPart finSide1;
    private final ModelPart finSide2;
    private final ModelPart finSide3;
    private final ModelPart finBottom0;
    private final ModelPart finBottom1;

    public PufferfishMidModel() {
        this.texWidth = 32;
        this.texHeight = 32;
        int n = 22;
        this.cube = new ModelPart(this, 12, 22);
        this.cube.addBox(-2.5f, -5.0f, -2.5f, 5.0f, 5.0f, 5.0f);
        this.cube.setPos(0.0f, 22.0f, 0.0f);
        this.finBlue0 = new ModelPart(this, 24, 0);
        this.finBlue0.addBox(-2.0f, 0.0f, 0.0f, 2.0f, 0.0f, 2.0f);
        this.finBlue0.setPos(-2.5f, 17.0f, -1.5f);
        this.finBlue1 = new ModelPart(this, 24, 3);
        this.finBlue1.addBox(0.0f, 0.0f, 0.0f, 2.0f, 0.0f, 2.0f);
        this.finBlue1.setPos(2.5f, 17.0f, -1.5f);
        this.finTop0 = new ModelPart(this, 15, 16);
        this.finTop0.addBox(-2.5f, -1.0f, 0.0f, 5.0f, 1.0f, 1.0f);
        this.finTop0.setPos(0.0f, 17.0f, -2.5f);
        this.finTop0.xRot = 0.7853982f;
        this.finTop1 = new ModelPart(this, 10, 16);
        this.finTop1.addBox(-2.5f, -1.0f, -1.0f, 5.0f, 1.0f, 1.0f);
        this.finTop1.setPos(0.0f, 17.0f, 2.5f);
        this.finTop1.xRot = -0.7853982f;
        this.finSide0 = new ModelPart(this, 8, 16);
        this.finSide0.addBox(-1.0f, -5.0f, 0.0f, 1.0f, 5.0f, 1.0f);
        this.finSide0.setPos(-2.5f, 22.0f, -2.5f);
        this.finSide0.yRot = -0.7853982f;
        this.finSide1 = new ModelPart(this, 8, 16);
        this.finSide1.addBox(-1.0f, -5.0f, 0.0f, 1.0f, 5.0f, 1.0f);
        this.finSide1.setPos(-2.5f, 22.0f, 2.5f);
        this.finSide1.yRot = 0.7853982f;
        this.finSide2 = new ModelPart(this, 4, 16);
        this.finSide2.addBox(0.0f, -5.0f, 0.0f, 1.0f, 5.0f, 1.0f);
        this.finSide2.setPos(2.5f, 22.0f, 2.5f);
        this.finSide2.yRot = -0.7853982f;
        this.finSide3 = new ModelPart(this, 0, 16);
        this.finSide3.addBox(0.0f, -5.0f, 0.0f, 1.0f, 5.0f, 1.0f);
        this.finSide3.setPos(2.5f, 22.0f, -2.5f);
        this.finSide3.yRot = 0.7853982f;
        this.finBottom0 = new ModelPart(this, 8, 22);
        this.finBottom0.addBox(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        this.finBottom0.setPos(0.5f, 22.0f, 2.5f);
        this.finBottom0.xRot = 0.7853982f;
        this.finBottom1 = new ModelPart(this, 17, 21);
        this.finBottom1.addBox(-2.5f, 0.0f, 0.0f, 5.0f, 1.0f, 1.0f);
        this.finBottom1.setPos(0.0f, 22.0f, -2.5f);
        this.finBottom1.xRot = -0.7853982f;
    }

    @Override
    public Iterable<ModelPart> parts() {
        return ImmutableList.of((Object)this.cube, (Object)this.finBlue0, (Object)this.finBlue1, (Object)this.finTop0, (Object)this.finTop1, (Object)this.finSide0, (Object)this.finSide1, (Object)this.finSide2, (Object)this.finSide3, (Object)this.finBottom0, (Object)this.finBottom1);
    }

    @Override
    public void setupAnim(T t, float f, float f2, float f3, float f4, float f5) {
        this.finBlue0.zRot = -0.2f + 0.4f * Mth.sin(f3 * 0.2f);
        this.finBlue1.zRot = 0.2f - 0.4f * Mth.sin(f3 * 0.2f);
    }
}

