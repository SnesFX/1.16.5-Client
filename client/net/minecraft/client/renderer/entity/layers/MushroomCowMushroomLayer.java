/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.level.block.state.BlockState;

public class MushroomCowMushroomLayer<T extends MushroomCow>
extends RenderLayer<T, CowModel<T>> {
    public MushroomCowMushroomLayer(RenderLayerParent<T, CowModel<T>> renderLayerParent) {
        super(renderLayerParent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int n, T t, float f, float f2, float f3, float f4, float f5, float f6) {
        if (((AgableMob)t).isBaby() || ((Entity)t).isInvisible()) {
            return;
        }
        BlockRenderDispatcher blockRenderDispatcher = Minecraft.getInstance().getBlockRenderer();
        BlockState blockState = ((MushroomCow)t).getMushroomType().getBlockState();
        int n2 = LivingEntityRenderer.getOverlayCoords(t, 0.0f);
        poseStack.pushPose();
        poseStack.translate(0.20000000298023224, -0.3499999940395355, 0.5);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(-48.0f));
        poseStack.scale(-1.0f, -1.0f, 1.0f);
        poseStack.translate(-0.5, -0.5, -0.5);
        blockRenderDispatcher.renderSingleBlock(blockState, poseStack, multiBufferSource, n, n2);
        poseStack.popPose();
        poseStack.pushPose();
        poseStack.translate(0.20000000298023224, -0.3499999940395355, 0.5);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(42.0f));
        poseStack.translate(0.10000000149011612, 0.0, -0.6000000238418579);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(-48.0f));
        poseStack.scale(-1.0f, -1.0f, 1.0f);
        poseStack.translate(-0.5, -0.5, -0.5);
        blockRenderDispatcher.renderSingleBlock(blockState, poseStack, multiBufferSource, n, n2);
        poseStack.popPose();
        poseStack.pushPose();
        ((CowModel)this.getParentModel()).getHead().translateAndRotate(poseStack);
        poseStack.translate(0.0, -0.699999988079071, -0.20000000298023224);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(-78.0f));
        poseStack.scale(-1.0f, -1.0f, 1.0f);
        poseStack.translate(-0.5, -0.5, -0.5);
        blockRenderDispatcher.renderSingleBlock(blockState, poseStack, multiBufferSource, n, n2);
        poseStack.popPose();
    }
}

