/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class SquidModel<T extends Entity>
extends ListModel<T> {
    private final ModelPart body;
    private final ModelPart[] tentacles = new ModelPart[8];
    private final ImmutableList<ModelPart> parts;

    public SquidModel() {
        int n = -16;
        this.body = new ModelPart(this, 0, 0);
        this.body.addBox(-6.0f, -8.0f, -6.0f, 12.0f, 16.0f, 12.0f);
        this.body.y += 8.0f;
        for (int i = 0; i < this.tentacles.length; ++i) {
            this.tentacles[i] = new ModelPart(this, 48, 0);
            double d = (double)i * 3.141592653589793 * 2.0 / (double)this.tentacles.length;
            float f = (float)Math.cos(d) * 5.0f;
            float f2 = (float)Math.sin(d) * 5.0f;
            this.tentacles[i].addBox(-1.0f, 0.0f, -1.0f, 2.0f, 18.0f, 2.0f);
            this.tentacles[i].x = f;
            this.tentacles[i].z = f2;
            this.tentacles[i].y = 15.0f;
            d = (double)i * 3.141592653589793 * -2.0 / (double)this.tentacles.length + 1.5707963267948966;
            this.tentacles[i].yRot = (float)d;
        }
        ImmutableList.Builder builder = ImmutableList.builder();
        builder.add((Object)this.body);
        builder.addAll(Arrays.asList(this.tentacles));
        this.parts = builder.build();
    }

    @Override
    public void setupAnim(T t, float f, float f2, float f3, float f4, float f5) {
        for (ModelPart modelPart : this.tentacles) {
            modelPart.xRot = f3;
        }
    }

    @Override
    public Iterable<ModelPart> parts() {
        return this.parts;
    }
}

