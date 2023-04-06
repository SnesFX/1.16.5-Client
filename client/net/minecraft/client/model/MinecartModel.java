/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.model;

import java.util.Arrays;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class MinecartModel<T extends Entity>
extends ListModel<T> {
    private final ModelPart[] cubes = new ModelPart[6];

    public MinecartModel() {
        this.cubes[0] = new ModelPart(this, 0, 10);
        this.cubes[1] = new ModelPart(this, 0, 0);
        this.cubes[2] = new ModelPart(this, 0, 0);
        this.cubes[3] = new ModelPart(this, 0, 0);
        this.cubes[4] = new ModelPart(this, 0, 0);
        this.cubes[5] = new ModelPart(this, 44, 10);
        int n = 20;
        int n2 = 8;
        int n3 = 16;
        int n4 = 4;
        this.cubes[0].addBox(-10.0f, -8.0f, -1.0f, 20.0f, 16.0f, 2.0f, 0.0f);
        this.cubes[0].setPos(0.0f, 4.0f, 0.0f);
        this.cubes[5].addBox(-9.0f, -7.0f, -1.0f, 18.0f, 14.0f, 1.0f, 0.0f);
        this.cubes[5].setPos(0.0f, 4.0f, 0.0f);
        this.cubes[1].addBox(-8.0f, -9.0f, -1.0f, 16.0f, 8.0f, 2.0f, 0.0f);
        this.cubes[1].setPos(-9.0f, 4.0f, 0.0f);
        this.cubes[2].addBox(-8.0f, -9.0f, -1.0f, 16.0f, 8.0f, 2.0f, 0.0f);
        this.cubes[2].setPos(9.0f, 4.0f, 0.0f);
        this.cubes[3].addBox(-8.0f, -9.0f, -1.0f, 16.0f, 8.0f, 2.0f, 0.0f);
        this.cubes[3].setPos(0.0f, 4.0f, -7.0f);
        this.cubes[4].addBox(-8.0f, -9.0f, -1.0f, 16.0f, 8.0f, 2.0f, 0.0f);
        this.cubes[4].setPos(0.0f, 4.0f, 7.0f);
        this.cubes[0].xRot = 1.5707964f;
        this.cubes[1].yRot = 4.712389f;
        this.cubes[2].yRot = 1.5707964f;
        this.cubes[3].yRot = 3.1415927f;
        this.cubes[5].xRot = -1.5707964f;
    }

    @Override
    public void setupAnim(T t, float f, float f2, float f3, float f4, float f5) {
        this.cubes[5].y = 4.0f - f3;
    }

    @Override
    public Iterable<ModelPart> parts() {
        return Arrays.asList(this.cubes);
    }
}

