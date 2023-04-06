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

public class CreeperModel<T extends Entity>
extends ListModel<T> {
    private final ModelPart head;
    private final ModelPart hair;
    private final ModelPart body;
    private final ModelPart leg0;
    private final ModelPart leg1;
    private final ModelPart leg2;
    private final ModelPart leg3;

    public CreeperModel() {
        this(0.0f);
    }

    public CreeperModel(float f) {
        int n = 6;
        this.head = new ModelPart(this, 0, 0);
        this.head.addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, f);
        this.head.setPos(0.0f, 6.0f, 0.0f);
        this.hair = new ModelPart(this, 32, 0);
        this.hair.addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, f + 0.5f);
        this.hair.setPos(0.0f, 6.0f, 0.0f);
        this.body = new ModelPart(this, 16, 16);
        this.body.addBox(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, f);
        this.body.setPos(0.0f, 6.0f, 0.0f);
        this.leg0 = new ModelPart(this, 0, 16);
        this.leg0.addBox(-2.0f, 0.0f, -2.0f, 4.0f, 6.0f, 4.0f, f);
        this.leg0.setPos(-2.0f, 18.0f, 4.0f);
        this.leg1 = new ModelPart(this, 0, 16);
        this.leg1.addBox(-2.0f, 0.0f, -2.0f, 4.0f, 6.0f, 4.0f, f);
        this.leg1.setPos(2.0f, 18.0f, 4.0f);
        this.leg2 = new ModelPart(this, 0, 16);
        this.leg2.addBox(-2.0f, 0.0f, -2.0f, 4.0f, 6.0f, 4.0f, f);
        this.leg2.setPos(-2.0f, 18.0f, -4.0f);
        this.leg3 = new ModelPart(this, 0, 16);
        this.leg3.addBox(-2.0f, 0.0f, -2.0f, 4.0f, 6.0f, 4.0f, f);
        this.leg3.setPos(2.0f, 18.0f, -4.0f);
    }

    @Override
    public Iterable<ModelPart> parts() {
        return ImmutableList.of((Object)this.head, (Object)this.body, (Object)this.leg0, (Object)this.leg1, (Object)this.leg2, (Object)this.leg3);
    }

    @Override
    public void setupAnim(T t, float f, float f2, float f3, float f4, float f5) {
        this.head.yRot = f4 * 0.017453292f;
        this.head.xRot = f5 * 0.017453292f;
        this.leg0.xRot = Mth.cos(f * 0.6662f) * 1.4f * f2;
        this.leg1.xRot = Mth.cos(f * 0.6662f + 3.1415927f) * 1.4f * f2;
        this.leg2.xRot = Mth.cos(f * 0.6662f + 3.1415927f) * 1.4f * f2;
        this.leg3.xRot = Mth.cos(f * 0.6662f) * 1.4f * f2;
    }
}

