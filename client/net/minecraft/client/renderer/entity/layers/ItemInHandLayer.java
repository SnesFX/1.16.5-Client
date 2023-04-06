/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class ItemInHandLayer<T extends LivingEntity, M extends EntityModel<T>>
extends RenderLayer<T, M> {
    public ItemInHandLayer(RenderLayerParent<T, M> renderLayerParent) {
        super(renderLayerParent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int n, T t, float f, float f2, float f3, float f4, float f5, float f6) {
        ItemStack itemStack;
        boolean bl = ((LivingEntity)t).getMainArm() == HumanoidArm.RIGHT;
        ItemStack itemStack2 = bl ? ((LivingEntity)t).getOffhandItem() : ((LivingEntity)t).getMainHandItem();
        ItemStack itemStack3 = itemStack = bl ? ((LivingEntity)t).getMainHandItem() : ((LivingEntity)t).getOffhandItem();
        if (itemStack2.isEmpty() && itemStack.isEmpty()) {
            return;
        }
        poseStack.pushPose();
        if (((EntityModel)this.getParentModel()).young) {
            float f7 = 0.5f;
            poseStack.translate(0.0, 0.75, 0.0);
            poseStack.scale(0.5f, 0.5f, 0.5f);
        }
        this.renderArmWithItem((LivingEntity)t, itemStack, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, HumanoidArm.RIGHT, poseStack, multiBufferSource, n);
        this.renderArmWithItem((LivingEntity)t, itemStack2, ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND, HumanoidArm.LEFT, poseStack, multiBufferSource, n);
        poseStack.popPose();
    }

    private void renderArmWithItem(LivingEntity livingEntity, ItemStack itemStack, ItemTransforms.TransformType transformType, HumanoidArm humanoidArm, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
        if (itemStack.isEmpty()) {
            return;
        }
        poseStack.pushPose();
        ((ArmedModel)this.getParentModel()).translateToHand(humanoidArm, poseStack);
        poseStack.mulPose(Vector3f.XP.rotationDegrees(-90.0f));
        poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0f));
        boolean bl = humanoidArm == HumanoidArm.LEFT;
        poseStack.translate((float)(bl ? -1 : 1) / 16.0f, 0.125, -0.625);
        Minecraft.getInstance().getItemInHandRenderer().renderItem(livingEntity, itemStack, transformType, bl, poseStack, multiBufferSource, n);
        poseStack.popPose();
    }
}

