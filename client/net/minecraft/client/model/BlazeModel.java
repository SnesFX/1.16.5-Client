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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class BlazeModel<T extends Entity>
extends ListModel<T> {
    private final ModelPart[] upperBodyParts;
    private final ModelPart head = new ModelPart(this, 0, 0);
    private final ImmutableList<ModelPart> parts;

    public BlazeModel() {
        this.head.addBox(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f);
        this.upperBodyParts = new ModelPart[12];
        for (int i = 0; i < this.upperBodyParts.length; ++i) {
            this.upperBodyParts[i] = new ModelPart(this, 0, 16);
            this.upperBodyParts[i].addBox(0.0f, 0.0f, 0.0f, 2.0f, 8.0f, 2.0f);
        }
        ImmutableList.Builder builder = ImmutableList.builder();
        builder.add((Object)this.head);
        builder.addAll(Arrays.asList(this.upperBodyParts));
        this.parts = builder.build();
    }

    @Override
    public Iterable<ModelPart> parts() {
        return this.parts;
    }

    @Override
    public void setupAnim(T t, float f, float f2, float f3, float f4, float f5) {
        int n;
        float f6 = f3 * 3.1415927f * -0.1f;
        for (n = 0; n < 4; ++n) {
            this.upperBodyParts[n].y = -2.0f + Mth.cos(((float)(n * 2) + f3) * 0.25f);
            this.upperBodyParts[n].x = Mth.cos(f6) * 9.0f;
            this.upperBodyParts[n].z = Mth.sin(f6) * 9.0f;
            f6 += 1.5707964f;
        }
        f6 = 0.7853982f + f3 * 3.1415927f * 0.03f;
        for (n = 4; n < 8; ++n) {
            this.upperBodyParts[n].y = 2.0f + Mth.cos(((float)(n * 2) + f3) * 0.25f);
            this.upperBodyParts[n].x = Mth.cos(f6) * 7.0f;
            this.upperBodyParts[n].z = Mth.sin(f6) * 7.0f;
            f6 += 1.5707964f;
        }
        f6 = 0.47123894f + f3 * 3.1415927f * -0.05f;
        for (n = 8; n < 12; ++n) {
            this.upperBodyParts[n].y = 11.0f + Mth.cos(((float)n * 1.5f + f3) * 0.5f);
            this.upperBodyParts[n].x = Mth.cos(f6) * 5.0f;
            this.upperBodyParts[n].z = Mth.sin(f6) * 5.0f;
            f6 += 1.5707964f;
        }
        this.head.yRot = f4 * 0.017453292f;
        this.head.xRot = f5 * 0.017453292f;
    }
}

