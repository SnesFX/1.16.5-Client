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

public class ShulkerBulletModel<T extends Entity>
extends ListModel<T> {
    private final ModelPart main;

    public ShulkerBulletModel() {
        this.texWidth = 64;
        this.texHeight = 32;
        this.main = new ModelPart(this);
        this.main.texOffs(0, 0).addBox(-4.0f, -4.0f, -1.0f, 8.0f, 8.0f, 2.0f, 0.0f);
        this.main.texOffs(0, 10).addBox(-1.0f, -4.0f, -4.0f, 2.0f, 8.0f, 8.0f, 0.0f);
        this.main.texOffs(20, 0).addBox(-4.0f, -1.0f, -4.0f, 8.0f, 2.0f, 8.0f, 0.0f);
        this.main.setPos(0.0f, 0.0f, 0.0f);
    }

    @Override
    public Iterable<ModelPart> parts() {
        return ImmutableList.of((Object)this.main);
    }

    @Override
    public void setupAnim(T t, float f, float f2, float f3, float f4, float f5) {
        this.main.yRot = f4 * 0.017453292f;
        this.main.xRot = f5 * 0.017453292f;
    }
}

