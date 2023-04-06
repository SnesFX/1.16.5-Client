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
import net.minecraft.world.entity.Entity;

public class LeashKnotModel<T extends Entity>
extends ListModel<T> {
    private final ModelPart knot;

    public LeashKnotModel() {
        this.texWidth = 32;
        this.texHeight = 32;
        this.knot = new ModelPart(this, 0, 0);
        this.knot.addBox(-3.0f, -6.0f, -3.0f, 6.0f, 8.0f, 6.0f, 0.0f);
        this.knot.setPos(0.0f, 0.0f, 0.0f);
    }

    @Override
    public Iterable<ModelPart> parts() {
        return ImmutableList.of((Object)this.knot);
    }

    @Override
    public void setupAnim(T t, float f, float f2, float f3, float f4, float f5) {
        this.knot.yRot = f4 * 0.017453292f;
        this.knot.xRot = f5 * 0.017453292f;
    }
}

