/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.model;

import java.util.Arrays;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class EndermiteModel<T extends Entity>
extends ListModel<T> {
    private static final int[][] BODY_SIZES = new int[][]{{4, 3, 2}, {6, 4, 5}, {3, 3, 1}, {1, 2, 1}};
    private static final int[][] BODY_TEXS = new int[][]{{0, 0}, {0, 5}, {0, 14}, {0, 18}};
    private static final int BODY_COUNT = BODY_SIZES.length;
    private final ModelPart[] bodyParts = new ModelPart[BODY_COUNT];

    public EndermiteModel() {
        float f = -3.5f;
        for (int i = 0; i < this.bodyParts.length; ++i) {
            this.bodyParts[i] = new ModelPart(this, BODY_TEXS[i][0], BODY_TEXS[i][1]);
            this.bodyParts[i].addBox((float)BODY_SIZES[i][0] * -0.5f, 0.0f, (float)BODY_SIZES[i][2] * -0.5f, BODY_SIZES[i][0], BODY_SIZES[i][1], BODY_SIZES[i][2]);
            this.bodyParts[i].setPos(0.0f, 24 - BODY_SIZES[i][1], f);
            if (i >= this.bodyParts.length - 1) continue;
            f += (float)(BODY_SIZES[i][2] + BODY_SIZES[i + 1][2]) * 0.5f;
        }
    }

    @Override
    public Iterable<ModelPart> parts() {
        return Arrays.asList(this.bodyParts);
    }

    @Override
    public void setupAnim(T t, float f, float f2, float f3, float f4, float f5) {
        for (int i = 0; i < this.bodyParts.length; ++i) {
            this.bodyParts[i].yRot = Mth.cos(f3 * 0.9f + (float)i * 0.15f * 3.1415927f) * 3.1415927f * 0.01f * (float)(1 + Math.abs(i - 2));
            this.bodyParts[i].x = Mth.sin(f3 * 0.9f + (float)i * 0.15f * 3.1415927f) * 3.1415927f * 0.1f * (float)Math.abs(i - 2);
        }
    }
}

