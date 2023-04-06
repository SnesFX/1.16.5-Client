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
import net.minecraft.world.entity.boss.wither.WitherBoss;

public class WitherBossModel<T extends WitherBoss>
extends ListModel<T> {
    private final ModelPart[] upperBodyParts;
    private final ModelPart[] heads;
    private final ImmutableList<ModelPart> parts;

    public WitherBossModel(float f) {
        this.texWidth = 64;
        this.texHeight = 64;
        this.upperBodyParts = new ModelPart[3];
        this.upperBodyParts[0] = new ModelPart(this, 0, 16);
        this.upperBodyParts[0].addBox(-10.0f, 3.9f, -0.5f, 20.0f, 3.0f, 3.0f, f);
        this.upperBodyParts[1] = new ModelPart(this).setTexSize(this.texWidth, this.texHeight);
        this.upperBodyParts[1].setPos(-2.0f, 6.9f, -0.5f);
        this.upperBodyParts[1].texOffs(0, 22).addBox(0.0f, 0.0f, 0.0f, 3.0f, 10.0f, 3.0f, f);
        this.upperBodyParts[1].texOffs(24, 22).addBox(-4.0f, 1.5f, 0.5f, 11.0f, 2.0f, 2.0f, f);
        this.upperBodyParts[1].texOffs(24, 22).addBox(-4.0f, 4.0f, 0.5f, 11.0f, 2.0f, 2.0f, f);
        this.upperBodyParts[1].texOffs(24, 22).addBox(-4.0f, 6.5f, 0.5f, 11.0f, 2.0f, 2.0f, f);
        this.upperBodyParts[2] = new ModelPart(this, 12, 22);
        this.upperBodyParts[2].addBox(0.0f, 0.0f, 0.0f, 3.0f, 6.0f, 3.0f, f);
        this.heads = new ModelPart[3];
        this.heads[0] = new ModelPart(this, 0, 0);
        this.heads[0].addBox(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f, f);
        this.heads[1] = new ModelPart(this, 32, 0);
        this.heads[1].addBox(-4.0f, -4.0f, -4.0f, 6.0f, 6.0f, 6.0f, f);
        this.heads[1].x = -8.0f;
        this.heads[1].y = 4.0f;
        this.heads[2] = new ModelPart(this, 32, 0);
        this.heads[2].addBox(-4.0f, -4.0f, -4.0f, 6.0f, 6.0f, 6.0f, f);
        this.heads[2].x = 10.0f;
        this.heads[2].y = 4.0f;
        ImmutableList.Builder builder = ImmutableList.builder();
        builder.addAll(Arrays.asList(this.heads));
        builder.addAll(Arrays.asList(this.upperBodyParts));
        this.parts = builder.build();
    }

    public ImmutableList<ModelPart> parts() {
        return this.parts;
    }

    @Override
    public void setupAnim(T t, float f, float f2, float f3, float f4, float f5) {
        float f6 = Mth.cos(f3 * 0.1f);
        this.upperBodyParts[1].xRot = (0.065f + 0.05f * f6) * 3.1415927f;
        this.upperBodyParts[2].setPos(-2.0f, 6.9f + Mth.cos(this.upperBodyParts[1].xRot) * 10.0f, -0.5f + Mth.sin(this.upperBodyParts[1].xRot) * 10.0f);
        this.upperBodyParts[2].xRot = (0.265f + 0.1f * f6) * 3.1415927f;
        this.heads[0].yRot = f4 * 0.017453292f;
        this.heads[0].xRot = f5 * 0.017453292f;
    }

    @Override
    public void prepareMobModel(T t, float f, float f2, float f3) {
        for (int i = 1; i < 3; ++i) {
            this.heads[i].yRot = (((WitherBoss)t).getHeadYRot(i - 1) - ((WitherBoss)t).yBodyRot) * 0.017453292f;
            this.heads[i].xRot = ((WitherBoss)t).getHeadXRot(i - 1) * 0.017453292f;
        }
    }

    @Override
    public /* synthetic */ Iterable parts() {
        return this.parts();
    }
}

