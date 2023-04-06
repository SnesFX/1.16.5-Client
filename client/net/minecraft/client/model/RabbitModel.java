/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Consumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Rabbit;

public class RabbitModel<T extends Rabbit>
extends EntityModel<T> {
    private final ModelPart rearFootLeft = new ModelPart(this, 26, 24);
    private final ModelPart rearFootRight;
    private final ModelPart haunchLeft;
    private final ModelPart haunchRight;
    private final ModelPart body;
    private final ModelPart frontLegLeft;
    private final ModelPart frontLegRight;
    private final ModelPart head;
    private final ModelPart earRight;
    private final ModelPart earLeft;
    private final ModelPart tail;
    private final ModelPart nose;
    private float jumpRotation;

    public RabbitModel() {
        this.rearFootLeft.addBox(-1.0f, 5.5f, -3.7f, 2.0f, 1.0f, 7.0f);
        this.rearFootLeft.setPos(3.0f, 17.5f, 3.7f);
        this.rearFootLeft.mirror = true;
        this.setRotation(this.rearFootLeft, 0.0f, 0.0f, 0.0f);
        this.rearFootRight = new ModelPart(this, 8, 24);
        this.rearFootRight.addBox(-1.0f, 5.5f, -3.7f, 2.0f, 1.0f, 7.0f);
        this.rearFootRight.setPos(-3.0f, 17.5f, 3.7f);
        this.rearFootRight.mirror = true;
        this.setRotation(this.rearFootRight, 0.0f, 0.0f, 0.0f);
        this.haunchLeft = new ModelPart(this, 30, 15);
        this.haunchLeft.addBox(-1.0f, 0.0f, 0.0f, 2.0f, 4.0f, 5.0f);
        this.haunchLeft.setPos(3.0f, 17.5f, 3.7f);
        this.haunchLeft.mirror = true;
        this.setRotation(this.haunchLeft, -0.34906584f, 0.0f, 0.0f);
        this.haunchRight = new ModelPart(this, 16, 15);
        this.haunchRight.addBox(-1.0f, 0.0f, 0.0f, 2.0f, 4.0f, 5.0f);
        this.haunchRight.setPos(-3.0f, 17.5f, 3.7f);
        this.haunchRight.mirror = true;
        this.setRotation(this.haunchRight, -0.34906584f, 0.0f, 0.0f);
        this.body = new ModelPart(this, 0, 0);
        this.body.addBox(-3.0f, -2.0f, -10.0f, 6.0f, 5.0f, 10.0f);
        this.body.setPos(0.0f, 19.0f, 8.0f);
        this.body.mirror = true;
        this.setRotation(this.body, -0.34906584f, 0.0f, 0.0f);
        this.frontLegLeft = new ModelPart(this, 8, 15);
        this.frontLegLeft.addBox(-1.0f, 0.0f, -1.0f, 2.0f, 7.0f, 2.0f);
        this.frontLegLeft.setPos(3.0f, 17.0f, -1.0f);
        this.frontLegLeft.mirror = true;
        this.setRotation(this.frontLegLeft, -0.17453292f, 0.0f, 0.0f);
        this.frontLegRight = new ModelPart(this, 0, 15);
        this.frontLegRight.addBox(-1.0f, 0.0f, -1.0f, 2.0f, 7.0f, 2.0f);
        this.frontLegRight.setPos(-3.0f, 17.0f, -1.0f);
        this.frontLegRight.mirror = true;
        this.setRotation(this.frontLegRight, -0.17453292f, 0.0f, 0.0f);
        this.head = new ModelPart(this, 32, 0);
        this.head.addBox(-2.5f, -4.0f, -5.0f, 5.0f, 4.0f, 5.0f);
        this.head.setPos(0.0f, 16.0f, -1.0f);
        this.head.mirror = true;
        this.setRotation(this.head, 0.0f, 0.0f, 0.0f);
        this.earRight = new ModelPart(this, 52, 0);
        this.earRight.addBox(-2.5f, -9.0f, -1.0f, 2.0f, 5.0f, 1.0f);
        this.earRight.setPos(0.0f, 16.0f, -1.0f);
        this.earRight.mirror = true;
        this.setRotation(this.earRight, 0.0f, -0.2617994f, 0.0f);
        this.earLeft = new ModelPart(this, 58, 0);
        this.earLeft.addBox(0.5f, -9.0f, -1.0f, 2.0f, 5.0f, 1.0f);
        this.earLeft.setPos(0.0f, 16.0f, -1.0f);
        this.earLeft.mirror = true;
        this.setRotation(this.earLeft, 0.0f, 0.2617994f, 0.0f);
        this.tail = new ModelPart(this, 52, 6);
        this.tail.addBox(-1.5f, -1.5f, 0.0f, 3.0f, 3.0f, 2.0f);
        this.tail.setPos(0.0f, 20.0f, 7.0f);
        this.tail.mirror = true;
        this.setRotation(this.tail, -0.3490659f, 0.0f, 0.0f);
        this.nose = new ModelPart(this, 32, 9);
        this.nose.addBox(-0.5f, -2.5f, -5.5f, 1.0f, 1.0f, 1.0f);
        this.nose.setPos(0.0f, 16.0f, -1.0f);
        this.nose.mirror = true;
        this.setRotation(this.nose, 0.0f, 0.0f, 0.0f);
    }

    private void setRotation(ModelPart modelPart, float f, float f2, float f3) {
        modelPart.xRot = f;
        modelPart.yRot = f2;
        modelPart.zRot = f3;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int n, int n2, float f, float f2, float f3, float f4) {
        if (this.young) {
            float f5 = 1.5f;
            poseStack.pushPose();
            poseStack.scale(0.56666666f, 0.56666666f, 0.56666666f);
            poseStack.translate(0.0, 1.375, 0.125);
            ImmutableList.of((Object)this.head, (Object)this.earLeft, (Object)this.earRight, (Object)this.nose).forEach(modelPart -> modelPart.render(poseStack, vertexConsumer, n, n2, f, f2, f3, f4));
            poseStack.popPose();
            poseStack.pushPose();
            poseStack.scale(0.4f, 0.4f, 0.4f);
            poseStack.translate(0.0, 2.25, 0.0);
            ImmutableList.of((Object)this.rearFootLeft, (Object)this.rearFootRight, (Object)this.haunchLeft, (Object)this.haunchRight, (Object)this.body, (Object)this.frontLegLeft, (Object)this.frontLegRight, (Object)this.tail).forEach(modelPart -> modelPart.render(poseStack, vertexConsumer, n, n2, f, f2, f3, f4));
            poseStack.popPose();
        } else {
            poseStack.pushPose();
            poseStack.scale(0.6f, 0.6f, 0.6f);
            poseStack.translate(0.0, 1.0, 0.0);
            ImmutableList.of((Object)this.rearFootLeft, (Object)this.rearFootRight, (Object)this.haunchLeft, (Object)this.haunchRight, (Object)this.body, (Object)this.frontLegLeft, (Object)this.frontLegRight, (Object)this.head, (Object)this.earRight, (Object)this.earLeft, (Object)this.tail, (Object)this.nose, (Object[])new ModelPart[0]).forEach(modelPart -> modelPart.render(poseStack, vertexConsumer, n, n2, f, f2, f3, f4));
            poseStack.popPose();
        }
    }

    @Override
    public void setupAnim(T t, float f, float f2, float f3, float f4, float f5) {
        float f6 = f3 - (float)((Rabbit)t).tickCount;
        this.nose.xRot = f5 * 0.017453292f;
        this.head.xRot = f5 * 0.017453292f;
        this.earRight.xRot = f5 * 0.017453292f;
        this.earLeft.xRot = f5 * 0.017453292f;
        this.nose.yRot = f4 * 0.017453292f;
        this.head.yRot = f4 * 0.017453292f;
        this.earRight.yRot = this.nose.yRot - 0.2617994f;
        this.earLeft.yRot = this.nose.yRot + 0.2617994f;
        this.jumpRotation = Mth.sin(((Rabbit)t).getJumpCompletion(f6) * 3.1415927f);
        this.haunchLeft.xRot = (this.jumpRotation * 50.0f - 21.0f) * 0.017453292f;
        this.haunchRight.xRot = (this.jumpRotation * 50.0f - 21.0f) * 0.017453292f;
        this.rearFootLeft.xRot = this.jumpRotation * 50.0f * 0.017453292f;
        this.rearFootRight.xRot = this.jumpRotation * 50.0f * 0.017453292f;
        this.frontLegLeft.xRot = (this.jumpRotation * -40.0f - 11.0f) * 0.017453292f;
        this.frontLegRight.xRot = (this.jumpRotation * -40.0f - 11.0f) * 0.017453292f;
    }

    @Override
    public void prepareMobModel(T t, float f, float f2, float f3) {
        super.prepareMobModel(t, f, f2, f3);
        this.jumpRotation = Mth.sin(((Rabbit)t).getJumpCompletion(f3) * 3.1415927f);
    }
}

