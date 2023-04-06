/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import java.util.Random;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class GhastModel<T extends Entity>
extends ListModel<T> {
    private final ModelPart[] tentacles = new ModelPart[9];
    private final ImmutableList<ModelPart> parts;

    public GhastModel() {
        ImmutableList.Builder builder = ImmutableList.builder();
        ModelPart modelPart = new ModelPart(this, 0, 0);
        modelPart.addBox(-8.0f, -8.0f, -8.0f, 16.0f, 16.0f, 16.0f);
        modelPart.y = 17.6f;
        builder.add((Object)modelPart);
        Random random = new Random(1660L);
        for (int i = 0; i < this.tentacles.length; ++i) {
            this.tentacles[i] = new ModelPart(this, 0, 0);
            float f = (((float)(i % 3) - (float)(i / 3 % 2) * 0.5f + 0.25f) / 2.0f * 2.0f - 1.0f) * 5.0f;
            float f2 = ((float)(i / 3) / 2.0f * 2.0f - 1.0f) * 5.0f;
            int n = random.nextInt(7) + 8;
            this.tentacles[i].addBox(-1.0f, 0.0f, -1.0f, 2.0f, n, 2.0f);
            this.tentacles[i].x = f;
            this.tentacles[i].z = f2;
            this.tentacles[i].y = 24.6f;
            builder.add((Object)this.tentacles[i]);
        }
        this.parts = builder.build();
    }

    @Override
    public void setupAnim(T t, float f, float f2, float f3, float f4, float f5) {
        for (int i = 0; i < this.tentacles.length; ++i) {
            this.tentacles[i].xRot = 0.2f * Mth.sin(f3 * 0.3f + (float)i) + 0.4f;
        }
    }

    @Override
    public Iterable<ModelPart> parts() {
        return this.parts;
    }
}

