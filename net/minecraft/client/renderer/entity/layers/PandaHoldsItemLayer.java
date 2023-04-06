/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PandaModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.item.ItemStack;

public class PandaHoldsItemLayer
extends RenderLayer<Panda, PandaModel<Panda>> {
    public PandaHoldsItemLayer(RenderLayerParent<Panda, PandaModel<Panda>> renderLayerParent) {
        super(renderLayerParent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int n, Panda panda, float f, float f2, float f3, float f4, float f5, float f6) {
        ItemStack itemStack = panda.getItemBySlot(EquipmentSlot.MAINHAND);
        if (!panda.isSitting() || panda.isScared()) {
            return;
        }
        float f7 = -0.6f;
        float f8 = 1.4f;
        if (panda.isEating()) {
            f7 -= 0.2f * Mth.sin(f4 * 0.6f) + 0.2f;
            f8 -= 0.09f * Mth.sin(f4 * 0.6f);
        }
        poseStack.pushPose();
        poseStack.translate(0.10000000149011612, f8, f7);
        Minecraft.getInstance().getItemInHandRenderer().renderItem(panda, itemStack, ItemTransforms.TransformType.GROUND, false, poseStack, multiBufferSource, n);
        poseStack.popPose();
    }
}

