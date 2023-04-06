/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 */
package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Turtle;

public class TurtleModel<T extends Turtle>
extends QuadrupedModel<T> {
    private final ModelPart eggBelly;

    public TurtleModel(float f) {
        super(12, f, true, 120.0f, 0.0f, 9.0f, 6.0f, 120);
        this.texWidth = 128;
        this.texHeight = 64;
        this.head = new ModelPart(this, 3, 0);
        this.head.addBox(-3.0f, -1.0f, -3.0f, 6.0f, 5.0f, 6.0f, 0.0f);
        this.head.setPos(0.0f, 19.0f, -10.0f);
        this.body = new ModelPart(this);
        this.body.texOffs(7, 37).addBox(-9.5f, 3.0f, -10.0f, 19.0f, 20.0f, 6.0f, 0.0f);
        this.body.texOffs(31, 1).addBox(-5.5f, 3.0f, -13.0f, 11.0f, 18.0f, 3.0f, 0.0f);
        this.body.setPos(0.0f, 11.0f, -10.0f);
        this.eggBelly = new ModelPart(this);
        this.eggBelly.texOffs(70, 33).addBox(-4.5f, 3.0f, -14.0f, 9.0f, 18.0f, 1.0f, 0.0f);
        this.eggBelly.setPos(0.0f, 11.0f, -10.0f);
        boolean bl = true;
        this.leg0 = new ModelPart(this, 1, 23);
        this.leg0.addBox(-2.0f, 0.0f, 0.0f, 4.0f, 1.0f, 10.0f, 0.0f);
        this.leg0.setPos(-3.5f, 22.0f, 11.0f);
        this.leg1 = new ModelPart(this, 1, 12);
        this.leg1.addBox(-2.0f, 0.0f, 0.0f, 4.0f, 1.0f, 10.0f, 0.0f);
        this.leg1.setPos(3.5f, 22.0f, 11.0f);
        this.leg2 = new ModelPart(this, 27, 30);
        this.leg2.addBox(-13.0f, 0.0f, -2.0f, 13.0f, 1.0f, 5.0f, 0.0f);
        this.leg2.setPos(-5.0f, 21.0f, -4.0f);
        this.leg3 = new ModelPart(this, 27, 24);
        this.leg3.addBox(0.0f, 0.0f, -2.0f, 13.0f, 1.0f, 5.0f, 0.0f);
        this.leg3.setPos(5.0f, 21.0f, -4.0f);
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return Iterables.concat(super.bodyParts(), (Iterable)ImmutableList.of((Object)this.eggBelly));
    }

    @Override
    public void setupAnim(T t, float f, float f2, float f3, float f4, float f5) {
        super.setupAnim(t, f, f2, f3, f4, f5);
        this.leg0.xRot = Mth.cos(f * 0.6662f * 0.6f) * 0.5f * f2;
        this.leg1.xRot = Mth.cos(f * 0.6662f * 0.6f + 3.1415927f) * 0.5f * f2;
        this.leg2.zRot = Mth.cos(f * 0.6662f * 0.6f + 3.1415927f) * 0.5f * f2;
        this.leg3.zRot = Mth.cos(f * 0.6662f * 0.6f) * 0.5f * f2;
        this.leg2.xRot = 0.0f;
        this.leg3.xRot = 0.0f;
        this.leg2.yRot = 0.0f;
        this.leg3.yRot = 0.0f;
        this.leg0.yRot = 0.0f;
        this.leg1.yRot = 0.0f;
        this.eggBelly.xRot = 1.5707964f;
        if (!((Entity)t).isInWater() && ((Entity)t).isOnGround()) {
            float f6 = ((Turtle)t).isLayingEgg() ? 4.0f : 1.0f;
            float f7 = ((Turtle)t).isLayingEgg() ? 2.0f : 1.0f;
            float f8 = 5.0f;
            this.leg2.yRot = Mth.cos(f6 * f * 5.0f + 3.1415927f) * 8.0f * f2 * f7;
            this.leg2.zRot = 0.0f;
            this.leg3.yRot = Mth.cos(f6 * f * 5.0f) * 8.0f * f2 * f7;
            this.leg3.zRot = 0.0f;
            this.leg0.yRot = Mth.cos(f * 5.0f + 3.1415927f) * 3.0f * f2;
            this.leg0.xRot = 0.0f;
            this.leg1.yRot = Mth.cos(f * 5.0f) * 3.0f * f2;
            this.leg1.xRot = 0.0f;
        }
        this.eggBelly.visible = !this.young && ((Turtle)t).hasEgg();
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int n, int n2, float f, float f2, float f3, float f4) {
        boolean bl = this.eggBelly.visible;
        if (bl) {
            poseStack.pushPose();
            poseStack.translate(0.0, -0.07999999821186066, 0.0);
        }
        super.renderToBuffer(poseStack, vertexConsumer, n, n2, f, f2, f3, f4);
        if (bl) {
            poseStack.popPose();
        }
    }
}

