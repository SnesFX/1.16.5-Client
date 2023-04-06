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

public class SlimeModel<T extends Entity>
extends ListModel<T> {
    private final ModelPart cube;
    private final ModelPart eye0;
    private final ModelPart eye1;
    private final ModelPart mouth;

    public SlimeModel(int n) {
        this.cube = new ModelPart(this, 0, n);
        this.eye0 = new ModelPart(this, 32, 0);
        this.eye1 = new ModelPart(this, 32, 4);
        this.mouth = new ModelPart(this, 32, 8);
        if (n > 0) {
            this.cube.addBox(-3.0f, 17.0f, -3.0f, 6.0f, 6.0f, 6.0f);
            this.eye0.addBox(-3.25f, 18.0f, -3.5f, 2.0f, 2.0f, 2.0f);
            this.eye1.addBox(1.25f, 18.0f, -3.5f, 2.0f, 2.0f, 2.0f);
            this.mouth.addBox(0.0f, 21.0f, -3.5f, 1.0f, 1.0f, 1.0f);
        } else {
            this.cube.addBox(-4.0f, 16.0f, -4.0f, 8.0f, 8.0f, 8.0f);
        }
    }

    @Override
    public void setupAnim(T t, float f, float f2, float f3, float f4, float f5) {
    }

    @Override
    public Iterable<ModelPart> parts() {
        return ImmutableList.of((Object)this.cube, (Object)this.eye0, (Object)this.eye1, (Object)this.mouth);
    }
}

