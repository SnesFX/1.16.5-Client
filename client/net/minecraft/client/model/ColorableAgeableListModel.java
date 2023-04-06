/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.world.entity.Entity;

public abstract class ColorableAgeableListModel<E extends Entity>
extends AgeableListModel<E> {
    private float r = 1.0f;
    private float g = 1.0f;
    private float b = 1.0f;

    public void setColor(float f, float f2, float f3) {
        this.r = f;
        this.g = f2;
        this.b = f3;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int n, int n2, float f, float f2, float f3, float f4) {
        super.renderToBuffer(poseStack, vertexConsumer, n, n2, this.r * f, this.g * f2, this.b * f3, f4);
    }
}

