/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.ColorableListModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class TropicalFishModelB<T extends Entity>
extends ColorableListModel<T> {
    private final ModelPart body;
    private final ModelPart tail;
    private final ModelPart leftFin;
    private final ModelPart rightFin;
    private final ModelPart topFin;
    private final ModelPart bottomFin;

    public TropicalFishModelB(float f) {
        this.texWidth = 32;
        this.texHeight = 32;
        int n = 19;
        this.body = new ModelPart(this, 0, 20);
        this.body.addBox(-1.0f, -3.0f, -3.0f, 2.0f, 6.0f, 6.0f, f);
        this.body.setPos(0.0f, 19.0f, 0.0f);
        this.tail = new ModelPart(this, 21, 16);
        this.tail.addBox(0.0f, -3.0f, 0.0f, 0.0f, 6.0f, 5.0f, f);
        this.tail.setPos(0.0f, 19.0f, 3.0f);
        this.leftFin = new ModelPart(this, 2, 16);
        this.leftFin.addBox(-2.0f, 0.0f, 0.0f, 2.0f, 2.0f, 0.0f, f);
        this.leftFin.setPos(-1.0f, 20.0f, 0.0f);
        this.leftFin.yRot = 0.7853982f;
        this.rightFin = new ModelPart(this, 2, 12);
        this.rightFin.addBox(0.0f, 0.0f, 0.0f, 2.0f, 2.0f, 0.0f, f);
        this.rightFin.setPos(1.0f, 20.0f, 0.0f);
        this.rightFin.yRot = -0.7853982f;
        this.topFin = new ModelPart(this, 20, 11);
        this.topFin.addBox(0.0f, -4.0f, 0.0f, 0.0f, 4.0f, 6.0f, f);
        this.topFin.setPos(0.0f, 16.0f, -3.0f);
        this.bottomFin = new ModelPart(this, 20, 21);
        this.bottomFin.addBox(0.0f, 0.0f, 0.0f, 0.0f, 4.0f, 6.0f, f);
        this.bottomFin.setPos(0.0f, 22.0f, -3.0f);
    }

    @Override
    public Iterable<ModelPart> parts() {
        return ImmutableList.of((Object)this.body, (Object)this.tail, (Object)this.leftFin, (Object)this.rightFin, (Object)this.topFin, (Object)this.bottomFin);
    }

    @Override
    public void setupAnim(T t, float f, float f2, float f3, float f4, float f5) {
        float f6 = 1.0f;
        if (!((Entity)t).isInWater()) {
            f6 = 1.5f;
        }
        this.tail.yRot = -f6 * 0.45f * Mth.sin(0.6f * f3);
    }
}

