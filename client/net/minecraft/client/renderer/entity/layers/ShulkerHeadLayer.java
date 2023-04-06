/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.ShulkerRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.item.DyeColor;

public class ShulkerHeadLayer
extends RenderLayer<Shulker, ShulkerModel<Shulker>> {
    public ShulkerHeadLayer(RenderLayerParent<Shulker, ShulkerModel<Shulker>> renderLayerParent) {
        super(renderLayerParent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int n, Shulker shulker, float f, float f2, float f3, float f4, float f5, float f6) {
        poseStack.pushPose();
        poseStack.translate(0.0, 1.0, 0.0);
        poseStack.scale(-1.0f, -1.0f, 1.0f);
        Quaternion quaternion = shulker.getAttachFace().getOpposite().getRotation();
        quaternion.conj();
        poseStack.mulPose(quaternion);
        poseStack.scale(-1.0f, -1.0f, 1.0f);
        poseStack.translate(0.0, -1.0, 0.0);
        DyeColor dyeColor = shulker.getColor();
        ResourceLocation resourceLocation = dyeColor == null ? ShulkerRenderer.DEFAULT_TEXTURE_LOCATION : ShulkerRenderer.TEXTURE_LOCATION[dyeColor.getId()];
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.entitySolid(resourceLocation));
        ((ShulkerModel)this.getParentModel()).getHead().render(poseStack, vertexConsumer, n, LivingEntityRenderer.getOverlayCoords(shulker, 0.0f));
        poseStack.popPose();
    }
}

