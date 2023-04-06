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

public class LlamaSpitModel<T extends Entity>
extends ListModel<T> {
    private final ModelPart main = new ModelPart(this);

    public LlamaSpitModel() {
        this(0.0f);
    }

    public LlamaSpitModel(float f) {
        int n = 2;
        this.main.texOffs(0, 0).addBox(-4.0f, 0.0f, 0.0f, 2.0f, 2.0f, 2.0f, f);
        this.main.texOffs(0, 0).addBox(0.0f, -4.0f, 0.0f, 2.0f, 2.0f, 2.0f, f);
        this.main.texOffs(0, 0).addBox(0.0f, 0.0f, -4.0f, 2.0f, 2.0f, 2.0f, f);
        this.main.texOffs(0, 0).addBox(0.0f, 0.0f, 0.0f, 2.0f, 2.0f, 2.0f, f);
        this.main.texOffs(0, 0).addBox(2.0f, 0.0f, 0.0f, 2.0f, 2.0f, 2.0f, f);
        this.main.texOffs(0, 0).addBox(0.0f, 2.0f, 0.0f, 2.0f, 2.0f, 2.0f, f);
        this.main.texOffs(0, 0).addBox(0.0f, 0.0f, 2.0f, 2.0f, 2.0f, 2.0f, f);
        this.main.setPos(0.0f, 0.0f, 0.0f);
    }

    @Override
    public void setupAnim(T t, float f, float f2, float f3, float f4, float f5) {
    }

    @Override
    public Iterable<ModelPart> parts() {
        return ImmutableList.of((Object)this.main);
    }
}

