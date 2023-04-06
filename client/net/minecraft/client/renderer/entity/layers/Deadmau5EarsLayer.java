/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class Deadmau5EarsLayer
extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    public Deadmau5EarsLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderLayerParent) {
        super(renderLayerParent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int n, AbstractClientPlayer abstractClientPlayer, float f, float f2, float f3, float f4, float f5, float f6) {
        if (!"deadmau5".equals(abstractClientPlayer.getName().getString()) || !abstractClientPlayer.isSkinLoaded() || abstractClientPlayer.isInvisible()) {
            return;
        }
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.entitySolid(abstractClientPlayer.getSkinTextureLocation()));
        int n2 = LivingEntityRenderer.getOverlayCoords(abstractClientPlayer, 0.0f);
        for (int i = 0; i < 2; ++i) {
            float f7 = Mth.lerp(f3, abstractClientPlayer.yRotO, abstractClientPlayer.yRot) - Mth.lerp(f3, abstractClientPlayer.yBodyRotO, abstractClientPlayer.yBodyRot);
            float f8 = Mth.lerp(f3, abstractClientPlayer.xRotO, abstractClientPlayer.xRot);
            poseStack.pushPose();
            poseStack.mulPose(Vector3f.YP.rotationDegrees(f7));
            poseStack.mulPose(Vector3f.XP.rotationDegrees(f8));
            poseStack.translate(0.375f * (float)(i * 2 - 1), 0.0, 0.0);
            poseStack.translate(0.0, -0.375, 0.0);
            poseStack.mulPose(Vector3f.XP.rotationDegrees(-f8));
            poseStack.mulPose(Vector3f.YP.rotationDegrees(-f7));
            float f9 = 1.3333334f;
            poseStack.scale(1.3333334f, 1.3333334f, 1.3333334f);
            ((PlayerModel)this.getParentModel()).renderEars(poseStack, vertexConsumer, n, n2);
            poseStack.popPose();
        }
    }
}

