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

public class SnowGolemModel<T extends Entity>
extends ListModel<T> {
    private final ModelPart piece1;
    private final ModelPart piece2;
    private final ModelPart head;
    private final ModelPart arm1;
    private final ModelPart arm2;

    public SnowGolemModel() {
        float f = 4.0f;
        float f2 = 0.0f;
        this.head = new ModelPart(this, 0, 0).setTexSize(64, 64);
        this.head.addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, -0.5f);
        this.head.setPos(0.0f, 4.0f, 0.0f);
        this.arm1 = new ModelPart(this, 32, 0).setTexSize(64, 64);
        this.arm1.addBox(-1.0f, 0.0f, -1.0f, 12.0f, 2.0f, 2.0f, -0.5f);
        this.arm1.setPos(0.0f, 6.0f, 0.0f);
        this.arm2 = new ModelPart(this, 32, 0).setTexSize(64, 64);
        this.arm2.addBox(-1.0f, 0.0f, -1.0f, 12.0f, 2.0f, 2.0f, -0.5f);
        this.arm2.setPos(0.0f, 6.0f, 0.0f);
        this.piece1 = new ModelPart(this, 0, 16).setTexSize(64, 64);
        this.piece1.addBox(-5.0f, -10.0f, -5.0f, 10.0f, 10.0f, 10.0f, -0.5f);
        this.piece1.setPos(0.0f, 13.0f, 0.0f);
        this.piece2 = new ModelPart(this, 0, 36).setTexSize(64, 64);
        this.piece2.addBox(-6.0f, -12.0f, -6.0f, 12.0f, 12.0f, 12.0f, -0.5f);
        this.piece2.setPos(0.0f, 24.0f, 0.0f);
    }

    @Override
    public void setupAnim(T t, float f, float f2, float f3, float f4, float f5) {
        this.head.yRot = f4 * 0.017453292f;
        this.head.xRot = f5 * 0.017453292f;
        this.piece1.yRot = f4 * 0.017453292f * 0.25f;
        float f6 = Mth.sin(this.piece1.yRot);
        float f7 = Mth.cos(this.piece1.yRot);
        this.arm1.zRot = 1.0f;
        this.arm2.zRot = -1.0f;
        this.arm1.yRot = 0.0f + this.piece1.yRot;
        this.arm2.yRot = 3.1415927f + this.piece1.yRot;
        this.arm1.x = f7 * 5.0f;
        this.arm1.z = -f6 * 5.0f;
        this.arm2.x = -f7 * 5.0f;
        this.arm2.z = f6 * 5.0f;
    }

    @Override
    public Iterable<ModelPart> parts() {
        return ImmutableList.of((Object)this.piece1, (Object)this.piece2, (Object)this.head, (Object)this.arm1, (Object)this.arm2);
    }

    public ModelPart getHead() {
        return this.head;
    }
}

