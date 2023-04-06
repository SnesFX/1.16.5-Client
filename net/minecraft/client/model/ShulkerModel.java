/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import java.util.function.Function;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Shulker;

public class ShulkerModel<T extends Shulker>
extends ListModel<T> {
    private final ModelPart base;
    private final ModelPart lid = new ModelPart(64, 64, 0, 0);
    private final ModelPart head;

    public ShulkerModel() {
        super(RenderType::entityCutoutNoCullZOffset);
        this.base = new ModelPart(64, 64, 0, 28);
        this.head = new ModelPart(64, 64, 0, 52);
        this.lid.addBox(-8.0f, -16.0f, -8.0f, 16.0f, 12.0f, 16.0f);
        this.lid.setPos(0.0f, 24.0f, 0.0f);
        this.base.addBox(-8.0f, -8.0f, -8.0f, 16.0f, 8.0f, 16.0f);
        this.base.setPos(0.0f, 24.0f, 0.0f);
        this.head.addBox(-3.0f, 0.0f, -3.0f, 6.0f, 6.0f, 6.0f);
        this.head.setPos(0.0f, 12.0f, 0.0f);
    }

    @Override
    public void setupAnim(T t, float f, float f2, float f3, float f4, float f5) {
        float f6 = f3 - (float)((Shulker)t).tickCount;
        float f7 = (0.5f + ((Shulker)t).getClientPeekAmount(f6)) * 3.1415927f;
        float f8 = -1.0f + Mth.sin(f7);
        float f9 = 0.0f;
        if (f7 > 3.1415927f) {
            f9 = Mth.sin(f3 * 0.1f) * 0.7f;
        }
        this.lid.setPos(0.0f, 16.0f + Mth.sin(f7) * 8.0f + f9, 0.0f);
        this.lid.yRot = ((Shulker)t).getClientPeekAmount(f6) > 0.3f ? f8 * f8 * f8 * f8 * 3.1415927f * 0.125f : 0.0f;
        this.head.xRot = f5 * 0.017453292f;
        this.head.yRot = (((Shulker)t).yHeadRot - 180.0f - ((Shulker)t).yBodyRot) * 0.017453292f;
    }

    @Override
    public Iterable<ModelPart> parts() {
        return ImmutableList.of((Object)this.base, (Object)this.lid);
    }

    public ModelPart getBase() {
        return this.base;
    }

    public ModelPart getLid() {
        return this.lid;
    }

    public ModelPart getHead() {
        return this.head;
    }
}

