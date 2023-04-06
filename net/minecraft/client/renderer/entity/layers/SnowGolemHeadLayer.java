/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.SnowGolemModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class SnowGolemHeadLayer
extends RenderLayer<SnowGolem, SnowGolemModel<SnowGolem>> {
    public SnowGolemHeadLayer(RenderLayerParent<SnowGolem, SnowGolemModel<SnowGolem>> renderLayerParent) {
        super(renderLayerParent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int n, SnowGolem snowGolem, float f, float f2, float f3, float f4, float f5, float f6) {
        if (snowGolem.isInvisible() || !snowGolem.hasPumpkin()) {
            return;
        }
        poseStack.pushPose();
        ((SnowGolemModel)this.getParentModel()).getHead().translateAndRotate(poseStack);
        float f7 = 0.625f;
        poseStack.translate(0.0, -0.34375, 0.0);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0f));
        poseStack.scale(0.625f, -0.625f, -0.625f);
        ItemStack itemStack = new ItemStack(Blocks.CARVED_PUMPKIN);
        Minecraft.getInstance().getItemRenderer().renderStatic(snowGolem, itemStack, ItemTransforms.TransformType.HEAD, false, poseStack, multiBufferSource, snowGolem.level, n, LivingEntityRenderer.getOverlayCoords(snowGolem, 0.0f));
        poseStack.popPose();
    }
}

