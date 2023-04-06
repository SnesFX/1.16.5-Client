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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class EvokerFangsModel<T extends Entity>
extends ListModel<T> {
    private final ModelPart base = new ModelPart(this, 0, 0);
    private final ModelPart upperJaw;
    private final ModelPart lowerJaw;

    public EvokerFangsModel() {
        this.base.setPos(-5.0f, 22.0f, -5.0f);
        this.base.addBox(0.0f, 0.0f, 0.0f, 10.0f, 12.0f, 10.0f);
        this.upperJaw = new ModelPart(this, 40, 0);
        this.upperJaw.setPos(1.5f, 22.0f, -4.0f);
        this.upperJaw.addBox(0.0f, 0.0f, 0.0f, 4.0f, 14.0f, 8.0f);
        this.lowerJaw = new ModelPart(this, 40, 0);
        this.lowerJaw.setPos(-1.5f, 22.0f, 4.0f);
        this.lowerJaw.addBox(0.0f, 0.0f, 0.0f, 4.0f, 14.0f, 8.0f);
    }

    @Override
    public void setupAnim(T t, float f, float f2, float f3, float f4, float f5) {
        float f6 = f * 2.0f;
        if (f6 > 1.0f) {
            f6 = 1.0f;
        }
        f6 = 1.0f - f6 * f6 * f6;
        this.upperJaw.zRot = 3.1415927f - f6 * 0.35f * 3.1415927f;
        this.lowerJaw.zRot = 3.1415927f + f6 * 0.35f * 3.1415927f;
        this.lowerJaw.yRot = 3.1415927f;
        float f7 = (f + Mth.sin(f * 2.7f)) * 0.6f * 12.0f;
        this.lowerJaw.y = this.upperJaw.y = 24.0f - f7;
        this.base.y = this.upperJaw.y;
    }

    @Override
    public Iterable<ModelPart> parts() {
        return ImmutableList.of((Object)this.base, (Object)this.upperJaw, (Object)this.lowerJaw);
    }
}

