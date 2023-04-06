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

public class SilverfishModel<T extends Entity>
extends ListModel<T> {
    private final ModelPart[] bodyParts;
    private final ModelPart[] bodyLayers;
    private final ImmutableList<ModelPart> parts;
    private final float[] zPlacement = new float[7];
    private static final int[][] BODY_SIZES = new int[][]{{3, 2, 2}, {4, 3, 2}, {6, 4, 3}, {3, 3, 3}, {2, 2, 3}, {2, 1, 2}, {1, 1, 2}};
    private static final int[][] BODY_TEXS = new int[][]{{0, 0}, {0, 4}, {0, 9}, {0, 16}, {0, 22}, {11, 0}, {13, 4}};

    public SilverfishModel() {
        this.bodyParts = new ModelPart[7];
        float f = -3.5f;
        for (int i = 0; i < this.bodyParts.length; ++i) {
            this.bodyParts[i] = new ModelPart(this, BODY_TEXS[i][0], BODY_TEXS[i][1]);
            this.bodyParts[i].addBox((float)BODY_SIZES[i][0] * -0.5f, 0.0f, (float)BODY_SIZES[i][2] * -0.5f, BODY_SIZES[i][0], BODY_SIZES[i][1], BODY_SIZES[i][2]);
            this.bodyParts[i].setPos(0.0f, 24 - BODY_SIZES[i][1], f);
            this.zPlacement[i] = f;
            if (i >= this.bodyParts.length - 1) continue;
            f += (float)(BODY_SIZES[i][2] + BODY_SIZES[i + 1][2]) * 0.5f;
        }
        this.bodyLayers = new ModelPart[3];
        this.bodyLayers[0] = new ModelPart(this, 20, 0);
        this.bodyLayers[0].addBox(-5.0f, 0.0f, (float)BODY_SIZES[2][2] * -0.5f, 10.0f, 8.0f, BODY_SIZES[2][2]);
        this.bodyLayers[0].setPos(0.0f, 16.0f, this.zPlacement[2]);
        this.bodyLayers[1] = new ModelPart(this, 20, 11);
        this.bodyLayers[1].addBox(-3.0f, 0.0f, (float)BODY_SIZES[4][2] * -0.5f, 6.0f, 4.0f, BODY_SIZES[4][2]);
        this.bodyLayers[1].setPos(0.0f, 20.0f, this.zPlacement[4]);
        this.bodyLayers[2] = new ModelPart(this, 20, 18);
        this.bodyLayers[2].addBox(-3.0f, 0.0f, (float)BODY_SIZES[4][2] * -0.5f, 6.0f, 5.0f, BODY_SIZES[1][2]);
        this.bodyLayers[2].setPos(0.0f, 19.0f, this.zPlacement[1]);
        ImmutableList.Builder builder = ImmutableList.builder();
        builder.addAll(Arrays.asList(this.bodyParts));
        builder.addAll(Arrays.asList(this.bodyLayers));
        this.parts = builder.build();
    }

    public ImmutableList<ModelPart> parts() {
        return this.parts;
    }

    @Override
    public void setupAnim(T t, float f, float f2, float f3, float f4, float f5) {
        for (int i = 0; i < this.bodyParts.length; ++i) {
            this.bodyParts[i].yRot = Mth.cos(f3 * 0.9f + (float)i * 0.15f * 3.1415927f) * 3.1415927f * 0.05f * (float)(1 + Math.abs(i - 2));
            this.bodyParts[i].x = Mth.sin(f3 * 0.9f + (float)i * 0.15f * 3.1415927f) * 3.1415927f * 0.2f * (float)Math.abs(i - 2);
        }
        this.bodyLayers[0].yRot = this.bodyParts[2].yRot;
        this.bodyLayers[1].yRot = this.bodyParts[4].yRot;
        this.bodyLayers[1].x = this.bodyParts[4].x;
        this.bodyLayers[2].yRot = this.bodyParts[1].yRot;
        this.bodyLayers[2].x = this.bodyParts[1].x;
    }

    @Override
    public /* synthetic */ Iterable parts() {
        return this.parts();
    }
}

