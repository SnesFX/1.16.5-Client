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
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;

public class LlamaModel<T extends AbstractChestedHorse>
extends EntityModel<T> {
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart leg0;
    private final ModelPart leg1;
    private final ModelPart leg2;
    private final ModelPart leg3;
    private final ModelPart chest1;
    private final ModelPart chest2;

    public LlamaModel(float f) {
        this.texWidth = 128;
        this.texHeight = 64;
        this.head = new ModelPart(this, 0, 0);
        this.head.addBox(-2.0f, -14.0f, -10.0f, 4.0f, 4.0f, 9.0f, f);
        this.head.setPos(0.0f, 7.0f, -6.0f);
        this.head.texOffs(0, 14).addBox(-4.0f, -16.0f, -6.0f, 8.0f, 18.0f, 6.0f, f);
        this.head.texOffs(17, 0).addBox(-4.0f, -19.0f, -4.0f, 3.0f, 3.0f, 2.0f, f);
        this.head.texOffs(17, 0).addBox(1.0f, -19.0f, -4.0f, 3.0f, 3.0f, 2.0f, f);
        this.body = new ModelPart(this, 29, 0);
        this.body.addBox(-6.0f, -10.0f, -7.0f, 12.0f, 18.0f, 10.0f, f);
        this.body.setPos(0.0f, 5.0f, 2.0f);
        this.chest1 = new ModelPart(this, 45, 28);
        this.chest1.addBox(-3.0f, 0.0f, 0.0f, 8.0f, 8.0f, 3.0f, f);
        this.chest1.setPos(-8.5f, 3.0f, 3.0f);
        this.chest1.yRot = 1.5707964f;
        this.chest2 = new ModelPart(this, 45, 41);
        this.chest2.addBox(-3.0f, 0.0f, 0.0f, 8.0f, 8.0f, 3.0f, f);
        this.chest2.setPos(5.5f, 3.0f, 3.0f);
        this.chest2.yRot = 1.5707964f;
        int n = 4;
        int n2 = 14;
        this.leg0 = new ModelPart(this, 29, 29);
        this.leg0.addBox(-2.0f, 0.0f, -2.0f, 4.0f, 14.0f, 4.0f, f);
        this.leg0.setPos(-2.5f, 10.0f, 6.0f);
        this.leg1 = new ModelPart(this, 29, 29);
        this.leg1.addBox(-2.0f, 0.0f, -2.0f, 4.0f, 14.0f, 4.0f, f);
        this.leg1.setPos(2.5f, 10.0f, 6.0f);
        this.leg2 = new ModelPart(this, 29, 29);
        this.leg2.addBox(-2.0f, 0.0f, -2.0f, 4.0f, 14.0f, 4.0f, f);
        this.leg2.setPos(-2.5f, 10.0f, -4.0f);
        this.leg3 = new ModelPart(this, 29, 29);
        this.leg3.addBox(-2.0f, 0.0f, -2.0f, 4.0f, 14.0f, 4.0f, f);
        this.leg3.setPos(2.5f, 10.0f, -4.0f);
        this.leg0.x -= 1.0f;
        this.leg1.x += 1.0f;
        this.leg0.z += 0.0f;
        this.leg1.z += 0.0f;
        this.leg2.x -= 1.0f;
        this.leg3.x += 1.0f;
        this.leg2.z -= 1.0f;
        this.leg3.z -= 1.0f;
    }

    @Override
    public void setupAnim(T t, float f, float f2, float f3, float f4, float f5) {
        boolean bl;
        this.head.xRot = f5 * 0.017453292f;
        this.head.yRot = f4 * 0.017453292f;
        this.body.xRot = 1.5707964f;
        this.leg0.xRot = Mth.cos(f * 0.6662f) * 1.4f * f2;
        this.leg1.xRot = Mth.cos(f * 0.6662f + 3.1415927f) * 1.4f * f2;
        this.leg2.xRot = Mth.cos(f * 0.6662f + 3.1415927f) * 1.4f * f2;
        this.leg3.xRot = Mth.cos(f * 0.6662f) * 1.4f * f2;
        this.chest1.visible = bl = !((AgableMob)t).isBaby() && ((AbstractChestedHorse)t).hasChest();
        this.chest2.visible = bl;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int n, int n2, float f, float f2, float f3, float f4) {
        if (this.young) {
            float f5 = 2.0f;
            poseStack.pushPose();
            float f6 = 0.7f;
            poseStack.scale(0.71428573f, 0.64935064f, 0.7936508f);
            poseStack.translate(0.0, 1.3125, 0.2199999988079071);
            this.head.render(poseStack, vertexConsumer, n, n2, f, f2, f3, f4);
            poseStack.popPose();
            poseStack.pushPose();
            float f7 = 1.1f;
            poseStack.scale(0.625f, 0.45454544f, 0.45454544f);
            poseStack.translate(0.0, 2.0625, 0.0);
            this.body.render(poseStack, vertexConsumer, n, n2, f, f2, f3, f4);
            poseStack.popPose();
            poseStack.pushPose();
            poseStack.scale(0.45454544f, 0.41322312f, 0.45454544f);
            poseStack.translate(0.0, 2.0625, 0.0);
            ImmutableList.of((Object)this.leg0, (Object)this.leg1, (Object)this.leg2, (Object)this.leg3, (Object)this.chest1, (Object)this.chest2).forEach(modelPart -> modelPart.render(poseStack, vertexConsumer, n, n2, f, f2, f3, f4));
            poseStack.popPose();
        } else {
            ImmutableList.of((Object)this.head, (Object)this.body, (Object)this.leg0, (Object)this.leg1, (Object)this.leg2, (Object)this.leg3, (Object)this.chest1, (Object)this.chest2).forEach(modelPart -> modelPart.render(poseStack, vertexConsumer, n, n2, f, f2, f3, f4));
        }
    }
}

