/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.model.dragon;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.ModelPart;

public class DragonHeadModel
extends SkullModel {
    private final ModelPart head;
    private final ModelPart jaw;

    public DragonHeadModel(float f) {
        this.texWidth = 256;
        this.texHeight = 256;
        float f2 = -16.0f;
        this.head = new ModelPart(this);
        this.head.addBox("upperlip", -6.0f, -1.0f, -24.0f, 12, 5, 16, f, 176, 44);
        this.head.addBox("upperhead", -8.0f, -8.0f, -10.0f, 16, 16, 16, f, 112, 30);
        this.head.mirror = true;
        this.head.addBox("scale", -5.0f, -12.0f, -4.0f, 2, 4, 6, f, 0, 0);
        this.head.addBox("nostril", -5.0f, -3.0f, -22.0f, 2, 2, 4, f, 112, 0);
        this.head.mirror = false;
        this.head.addBox("scale", 3.0f, -12.0f, -4.0f, 2, 4, 6, f, 0, 0);
        this.head.addBox("nostril", 3.0f, -3.0f, -22.0f, 2, 2, 4, f, 112, 0);
        this.jaw = new ModelPart(this);
        this.jaw.setPos(0.0f, 4.0f, -8.0f);
        this.jaw.addBox("jaw", -6.0f, 0.0f, -16.0f, 12, 4, 16, f, 176, 65);
        this.head.addChild(this.jaw);
    }

    @Override
    public void setupAnim(float f, float f2, float f3) {
        this.jaw.xRot = (float)(Math.sin(f * 3.1415927f * 0.2f) + 1.0) * 0.2f;
        this.head.yRot = f2 * 0.017453292f;
        this.head.xRot = f3 * 0.017453292f;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int n, int n2, float f, float f2, float f3, float f4) {
        poseStack.pushPose();
        poseStack.translate(0.0, -0.37437498569488525, 0.0);
        poseStack.scale(0.75f, 0.75f, 0.75f);
        this.head.render(poseStack, vertexConsumer, n, n2, f, f2, f3, f4);
        poseStack.popPose();
    }
}

