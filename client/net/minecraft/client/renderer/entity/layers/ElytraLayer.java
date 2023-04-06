/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ElytraLayer<T extends LivingEntity, M extends EntityModel<T>>
extends RenderLayer<T, M> {
    private static final ResourceLocation WINGS_LOCATION = new ResourceLocation("textures/entity/elytra.png");
    private final ElytraModel<T> elytraModel = new ElytraModel();

    public ElytraLayer(RenderLayerParent<T, M> renderLayerParent) {
        super(renderLayerParent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int n, T t, float f, float f2, float f3, float f4, float f5, float f6) {
        Object object;
        ItemStack itemStack = ((LivingEntity)t).getItemBySlot(EquipmentSlot.CHEST);
        if (itemStack.getItem() != Items.ELYTRA) {
            return;
        }
        ResourceLocation resourceLocation = t instanceof AbstractClientPlayer ? (((AbstractClientPlayer)(object = (AbstractClientPlayer)t)).isElytraLoaded() && ((AbstractClientPlayer)object).getElytraTextureLocation() != null ? ((AbstractClientPlayer)object).getElytraTextureLocation() : (((AbstractClientPlayer)object).isCapeLoaded() && ((AbstractClientPlayer)object).getCloakTextureLocation() != null && ((Player)object).isModelPartShown(PlayerModelPart.CAPE) ? ((AbstractClientPlayer)object).getCloakTextureLocation() : WINGS_LOCATION)) : WINGS_LOCATION;
        poseStack.pushPose();
        poseStack.translate(0.0, 0.0, 0.125);
        ((EntityModel)this.getParentModel()).copyPropertiesTo(this.elytraModel);
        this.elytraModel.setupAnim(t, f, f2, f4, f5, f6);
        object = ItemRenderer.getArmorFoilBuffer(multiBufferSource, RenderType.armorCutoutNoCull(resourceLocation), false, itemStack.hasFoil());
        this.elytraModel.renderToBuffer(poseStack, (VertexConsumer)object, n, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
        poseStack.popPose();
    }
}

