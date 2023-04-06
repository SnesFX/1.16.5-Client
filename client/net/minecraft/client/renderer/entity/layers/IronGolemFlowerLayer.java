/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class IronGolemFlowerLayer
extends RenderLayer<IronGolem, IronGolemModel<IronGolem>> {
    public IronGolemFlowerLayer(RenderLayerParent<IronGolem, IronGolemModel<IronGolem>> renderLayerParent) {
        super(renderLayerParent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int n, IronGolem ironGolem, float f, float f2, float f3, float f4, float f5, float f6) {
        if (ironGolem.getOfferFlowerTick() == 0) {
            return;
        }
        poseStack.pushPose();
        ModelPart modelPart = ((IronGolemModel)this.getParentModel()).getFlowerHoldingArm();
        modelPart.translateAndRotate(poseStack);
        poseStack.translate(-1.1875, 1.0625, -0.9375);
        poseStack.translate(0.5, 0.5, 0.5);
        float f7 = 0.5f;
        poseStack.scale(0.5f, 0.5f, 0.5f);
        poseStack.mulPose(Vector3f.XP.rotationDegrees(-90.0f));
        poseStack.translate(-0.5, -0.5, -0.5);
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(Blocks.POPPY.defaultBlockState(), poseStack, multiBufferSource, n, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
}

