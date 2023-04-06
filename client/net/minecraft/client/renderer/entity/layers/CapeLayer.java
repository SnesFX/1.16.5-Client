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
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CapeLayer
extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    public CapeLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderLayerParent) {
        super(renderLayerParent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int n, AbstractClientPlayer abstractClientPlayer, float f, float f2, float f3, float f4, float f5, float f6) {
        if (!abstractClientPlayer.isCapeLoaded() || abstractClientPlayer.isInvisible() || !abstractClientPlayer.isModelPartShown(PlayerModelPart.CAPE) || abstractClientPlayer.getCloakTextureLocation() == null) {
            return;
        }
        ItemStack itemStack = abstractClientPlayer.getItemBySlot(EquipmentSlot.CHEST);
        if (itemStack.getItem() == Items.ELYTRA) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.0, 0.0, 0.125);
        double d = Mth.lerp((double)f3, abstractClientPlayer.xCloakO, abstractClientPlayer.xCloak) - Mth.lerp((double)f3, abstractClientPlayer.xo, abstractClientPlayer.getX());
        double d2 = Mth.lerp((double)f3, abstractClientPlayer.yCloakO, abstractClientPlayer.yCloak) - Mth.lerp((double)f3, abstractClientPlayer.yo, abstractClientPlayer.getY());
        double d3 = Mth.lerp((double)f3, abstractClientPlayer.zCloakO, abstractClientPlayer.zCloak) - Mth.lerp((double)f3, abstractClientPlayer.zo, abstractClientPlayer.getZ());
        float f7 = abstractClientPlayer.yBodyRotO + (abstractClientPlayer.yBodyRot - abstractClientPlayer.yBodyRotO);
        double d4 = Mth.sin(f7 * 0.017453292f);
        double d5 = -Mth.cos(f7 * 0.017453292f);
        float f8 = (float)d2 * 10.0f;
        f8 = Mth.clamp(f8, -6.0f, 32.0f);
        float f9 = (float)(d * d4 + d3 * d5) * 100.0f;
        f9 = Mth.clamp(f9, 0.0f, 150.0f);
        float f10 = (float)(d * d5 - d3 * d4) * 100.0f;
        f10 = Mth.clamp(f10, -20.0f, 20.0f);
        if (f9 < 0.0f) {
            f9 = 0.0f;
        }
        float f11 = Mth.lerp(f3, abstractClientPlayer.oBob, abstractClientPlayer.bob);
        f8 += Mth.sin(Mth.lerp(f3, abstractClientPlayer.walkDistO, abstractClientPlayer.walkDist) * 6.0f) * 32.0f * f11;
        if (abstractClientPlayer.isCrouching()) {
            f8 += 25.0f;
        }
        poseStack.mulPose(Vector3f.XP.rotationDegrees(6.0f + f9 / 2.0f + f8));
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(f10 / 2.0f));
        poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0f - f10 / 2.0f));
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.entitySolid(abstractClientPlayer.getCloakTextureLocation()));
        ((PlayerModel)this.getParentModel()).renderCloak(poseStack, vertexConsumer, n, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
}

