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

public class CodModel<T extends Entity>
extends ListModel<T> {
    private final ModelPart body;
    private final ModelPart topFin;
    private final ModelPart head;
    private final ModelPart nose;
    private final ModelPart sideFin0;
    private final ModelPart sideFin1;
    private final ModelPart tailFin;

    public CodModel() {
        this.texWidth = 32;
        this.texHeight = 32;
        int n = 22;
        this.body = new ModelPart(this, 0, 0);
        this.body.addBox(-1.0f, -2.0f, 0.0f, 2.0f, 4.0f, 7.0f);
        this.body.setPos(0.0f, 22.0f, 0.0f);
        this.head = new ModelPart(this, 11, 0);
        this.head.addBox(-1.0f, -2.0f, -3.0f, 2.0f, 4.0f, 3.0f);
        this.head.setPos(0.0f, 22.0f, 0.0f);
        this.nose = new ModelPart(this, 0, 0);
        this.nose.addBox(-1.0f, -2.0f, -1.0f, 2.0f, 3.0f, 1.0f);
        this.nose.setPos(0.0f, 22.0f, -3.0f);
        this.sideFin0 = new ModelPart(this, 22, 1);
        this.sideFin0.addBox(-2.0f, 0.0f, -1.0f, 2.0f, 0.0f, 2.0f);
        this.sideFin0.setPos(-1.0f, 23.0f, 0.0f);
        this.sideFin0.zRot = -0.7853982f;
        this.sideFin1 = new ModelPart(this, 22, 4);
        this.sideFin1.addBox(0.0f, 0.0f, -1.0f, 2.0f, 0.0f, 2.0f);
        this.sideFin1.setPos(1.0f, 23.0f, 0.0f);
        this.sideFin1.zRot = 0.7853982f;
        this.tailFin = new ModelPart(this, 22, 3);
        this.tailFin.addBox(0.0f, -2.0f, 0.0f, 0.0f, 4.0f, 4.0f);
        this.tailFin.setPos(0.0f, 22.0f, 7.0f);
        this.topFin = new ModelPart(this, 20, -6);
        this.topFin.addBox(0.0f, -1.0f, -1.0f, 0.0f, 1.0f, 6.0f);
        this.topFin.setPos(0.0f, 20.0f, 0.0f);
    }

    @Override
    public Iterable<ModelPart> parts() {
        return ImmutableList.of((Object)this.body, (Object)this.head, (Object)this.nose, (Object)this.sideFin0, (Object)this.sideFin1, (Object)this.tailFin, (Object)this.topFin);
    }

    @Override
    public void setupAnim(T t, float f, float f2, float f3, float f4, float f5) {
        float f6 = 1.0f;
        if (!((Entity)t).isInWater()) {
            f6 = 1.5f;
        }
        this.tailFin.yRot = -f6 * 0.45f * Mth.sin(0.6f * f3);
    }
}

