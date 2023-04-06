/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.DolphinModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.item.ItemStack;

public class DolphinCarryingItemLayer
extends RenderLayer<Dolphin, DolphinModel<Dolphin>> {
    public DolphinCarryingItemLayer(RenderLayerParent<Dolphin, DolphinModel<Dolphin>> renderLayerParent) {
        super(renderLayerParent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int n, Dolphin dolphin, float f, float f2, float f3, float f4, float f5, float f6) {
        boolean bl = dolphin.getMainArm() == HumanoidArm.RIGHT;
        poseStack.pushPose();
        float f7 = 1.0f;
        float f8 = -1.0f;
        float f9 = Mth.abs(dolphin.xRot) / 60.0f;
        if (dolphin.xRot < 0.0f) {
            poseStack.translate(0.0, 1.0f - f9 * 0.5f, -1.0f + f9 * 0.5f);
        } else {
            poseStack.translate(0.0, 1.0f + f9 * 0.8f, -1.0f + f9 * 0.2f);
        }
        ItemStack itemStack = bl ? dolphin.getMainHandItem() : dolphin.getOffhandItem();
        Minecraft.getInstance().getItemInHandRenderer().renderItem(dolphin, itemStack, ItemTransforms.TransformType.GROUND, false, poseStack, multiBufferSource, n);
        poseStack.popPose();
    }
}

