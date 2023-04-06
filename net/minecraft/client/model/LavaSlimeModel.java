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
import net.minecraft.world.entity.monster.Slime;

public class LavaSlimeModel<T extends Slime>
extends ListModel<T> {
    private final ModelPart[] bodyCubes = new ModelPart[8];
    private final ModelPart insideCube;
    private final ImmutableList<ModelPart> parts;

    public LavaSlimeModel() {
        for (int i = 0; i < this.bodyCubes.length; ++i) {
            int n = 0;
            int n2 = i;
            if (i == 2) {
                n = 24;
                n2 = 10;
            } else if (i == 3) {
                n = 24;
                n2 = 19;
            }
            this.bodyCubes[i] = new ModelPart(this, n, n2);
            this.bodyCubes[i].addBox(-4.0f, 16 + i, -4.0f, 8.0f, 1.0f, 8.0f);
        }
        this.insideCube = new ModelPart(this, 0, 16);
        this.insideCube.addBox(-2.0f, 18.0f, -2.0f, 4.0f, 4.0f, 4.0f);
        ImmutableList.Builder builder = ImmutableList.builder();
        builder.add((Object)this.insideCube);
        builder.addAll(Arrays.asList(this.bodyCubes));
        this.parts = builder.build();
    }

    @Override
    public void setupAnim(T t, float f, float f2, float f3, float f4, float f5) {
    }

    @Override
    public void prepareMobModel(T t, float f, float f2, float f3) {
        float f4 = Mth.lerp(f3, ((Slime)t).oSquish, ((Slime)t).squish);
        if (f4 < 0.0f) {
            f4 = 0.0f;
        }
        for (int i = 0; i < this.bodyCubes.length; ++i) {
            this.bodyCubes[i].y = (float)(-(4 - i)) * f4 * 1.7f;
        }
    }

    public ImmutableList<ModelPart> parts() {
        return this.parts;
    }

    @Override
    public /* synthetic */ Iterable parts() {
        return this.parts();
    }
}

