/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.ModelPart;

public class HumanoidHeadModel
extends SkullModel {
    private final ModelPart hat = new ModelPart(this, 32, 0);

    public HumanoidHeadModel() {
        super(0, 0, 64, 64);
        this.hat.addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, 0.25f);
        this.hat.setPos(0.0f, 0.0f, 0.0f);
    }

    @Override
    public void setupAnim(float f, float f2, float f3) {
        super.setupAnim(f, f2, f3);
        this.hat.yRot = this.head.yRot;
        this.hat.xRot = this.head.xRot;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int n, int n2, float f, float f2, float f3, float f4) {
        super.renderToBuffer(poseStack, vertexConsumer, n, n2, f, f2, f3, f4);
        this.hat.render(poseStack, vertexConsumer, n, n2, f, f2, f3, f4);
    }
}

