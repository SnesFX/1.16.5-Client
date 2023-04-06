/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.block.state.BlockState;

public class CarriedBlockLayer
extends RenderLayer<EnderMan, EndermanModel<EnderMan>> {
    public CarriedBlockLayer(RenderLayerParent<EnderMan, EndermanModel<EnderMan>> renderLayerParent) {
        super(renderLayerParent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int n, EnderMan enderMan, float f, float f2, float f3, float f4, float f5, float f6) {
        BlockState blockState = enderMan.getCarriedBlock();
        if (blockState == null) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.0, 0.6875, -0.75);
        poseStack.mulPose(Vector3f.XP.rotationDegrees(20.0f));
        poseStack.mulPose(Vector3f.YP.rotationDegrees(45.0f));
        poseStack.translate(0.25, 0.1875, 0.25);
        float f7 = 0.5f;
        poseStack.scale(-0.5f, -0.5f, 0.5f);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(90.0f));
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(blockState, poseStack, multiBufferSource, n, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
}

