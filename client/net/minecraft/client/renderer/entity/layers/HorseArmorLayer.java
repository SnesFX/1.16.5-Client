/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.item.DyeableHorseArmorItem;
import net.minecraft.world.item.HorseArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class HorseArmorLayer
extends RenderLayer<Horse, HorseModel<Horse>> {
    private final HorseModel<Horse> model = new HorseModel(0.1f);

    public HorseArmorLayer(RenderLayerParent<Horse, HorseModel<Horse>> renderLayerParent) {
        super(renderLayerParent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int n, Horse horse, float f, float f2, float f3, float f4, float f5, float f6) {
        float f7;
        float f8;
        float f9;
        ItemStack itemStack = horse.getArmor();
        if (!(itemStack.getItem() instanceof HorseArmorItem)) {
            return;
        }
        HorseArmorItem horseArmorItem = (HorseArmorItem)itemStack.getItem();
        ((HorseModel)this.getParentModel()).copyPropertiesTo(this.model);
        this.model.prepareMobModel(horse, f, f2, f3);
        this.model.setupAnim(horse, f, f2, f4, f5, f6);
        if (horseArmorItem instanceof DyeableHorseArmorItem) {
            int n2 = ((DyeableHorseArmorItem)horseArmorItem).getColor(itemStack);
            f9 = (float)(n2 >> 16 & 0xFF) / 255.0f;
            f8 = (float)(n2 >> 8 & 0xFF) / 255.0f;
            f7 = (float)(n2 & 0xFF) / 255.0f;
        } else {
            f9 = 1.0f;
            f8 = 1.0f;
            f7 = 1.0f;
        }
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.entityCutoutNoCull(horseArmorItem.getTexture()));
        this.model.renderToBuffer(poseStack, vertexConsumer, n, OverlayTexture.NO_OVERLAY, f9, f8, f7, 1.0f);
    }
}

