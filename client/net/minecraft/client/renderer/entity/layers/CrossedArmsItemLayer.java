/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class CrossedArmsItemLayer<T extends LivingEntity, M extends EntityModel<T>>
extends RenderLayer<T, M> {
    public CrossedArmsItemLayer(RenderLayerParent<T, M> renderLayerParent) {
        super(renderLayerParent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int n, T t, float f, float f2, float f3, float f4, float f5, float f6) {
        poseStack.pushPose();
        poseStack.translate(0.0, 0.4000000059604645, -0.4000000059604645);
        poseStack.mulPose(Vector3f.XP.rotationDegrees(180.0f));
        ItemStack itemStack = ((LivingEntity)t).getItemBySlot(EquipmentSlot.MAINHAND);
        Minecraft.getInstance().getItemInHandRenderer().renderItem((LivingEntity)t, itemStack, ItemTransforms.TransformType.GROUND, false, poseStack, multiBufferSource, n);
        poseStack.popPose();
    }
}

