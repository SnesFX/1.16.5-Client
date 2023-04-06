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

public class SalmonModel<T extends Entity>
extends ListModel<T> {
    private final ModelPart bodyFront;
    private final ModelPart bodyBack;
    private final ModelPart head;
    private final ModelPart sideFin0;
    private final ModelPart sideFin1;

    public SalmonModel() {
        this.texWidth = 32;
        this.texHeight = 32;
        int n = 20;
        this.bodyFront = new ModelPart(this, 0, 0);
        this.bodyFront.addBox(-1.5f, -2.5f, 0.0f, 3.0f, 5.0f, 8.0f);
        this.bodyFront.setPos(0.0f, 20.0f, 0.0f);
        this.bodyBack = new ModelPart(this, 0, 13);
        this.bodyBack.addBox(-1.5f, -2.5f, 0.0f, 3.0f, 5.0f, 8.0f);
        this.bodyBack.setPos(0.0f, 20.0f, 8.0f);
        this.head = new ModelPart(this, 22, 0);
        this.head.addBox(-1.0f, -2.0f, -3.0f, 2.0f, 4.0f, 3.0f);
        this.head.setPos(0.0f, 20.0f, 0.0f);
        ModelPart modelPart = new ModelPart(this, 20, 10);
        modelPart.addBox(0.0f, -2.5f, 0.0f, 0.0f, 5.0f, 6.0f);
        modelPart.setPos(0.0f, 0.0f, 8.0f);
        this.bodyBack.addChild(modelPart);
        ModelPart modelPart2 = new ModelPart(this, 2, 1);
        modelPart2.addBox(0.0f, 0.0f, 0.0f, 0.0f, 2.0f, 3.0f);
        modelPart2.setPos(0.0f, -4.5f, 5.0f);
        this.bodyFront.addChild(modelPart2);
        ModelPart modelPart3 = new ModelPart(this, 0, 2);
        modelPart3.addBox(0.0f, 0.0f, 0.0f, 0.0f, 2.0f, 4.0f);
        modelPart3.setPos(0.0f, -4.5f, -1.0f);
        this.bodyBack.addChild(modelPart3);
        this.sideFin0 = new ModelPart(this, -4, 0);
        this.sideFin0.addBox(-2.0f, 0.0f, 0.0f, 2.0f, 0.0f, 2.0f);
        this.sideFin0.setPos(-1.5f, 21.5f, 0.0f);
        this.sideFin0.zRot = -0.7853982f;
        this.sideFin1 = new ModelPart(this, 0, 0);
        this.sideFin1.addBox(0.0f, 0.0f, 0.0f, 2.0f, 0.0f, 2.0f);
        this.sideFin1.setPos(1.5f, 21.5f, 0.0f);
        this.sideFin1.zRot = 0.7853982f;
    }

    @Override
    public Iterable<ModelPart> parts() {
        return ImmutableList.of((Object)this.bodyFront, (Object)this.bodyBack, (Object)this.head, (Object)this.sideFin0, (Object)this.sideFin1);
    }

    @Override
    public void setupAnim(T t, float f, float f2, float f3, float f4, float f5) {
        float f6 = 1.0f;
        float f7 = 1.0f;
        if (!((Entity)t).isInWater()) {
            f6 = 1.3f;
            f7 = 1.7f;
        }
        this.bodyBack.yRot = -f6 * 0.25f * Mth.sin(f7 * 0.6f * f3);
    }
}

