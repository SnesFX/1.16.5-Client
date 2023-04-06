/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import java.util.Random;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemEntityRenderer
extends EntityRenderer<ItemEntity> {
    private final ItemRenderer itemRenderer;
    private final Random random = new Random();

    public ItemEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, ItemRenderer itemRenderer) {
        super(entityRenderDispatcher);
        this.itemRenderer = itemRenderer;
        this.shadowRadius = 0.15f;
        this.shadowStrength = 0.75f;
    }

    private int getRenderAmount(ItemStack itemStack) {
        int n = 1;
        if (itemStack.getCount() > 48) {
            n = 5;
        } else if (itemStack.getCount() > 32) {
            n = 4;
        } else if (itemStack.getCount() > 16) {
            n = 3;
        } else if (itemStack.getCount() > 1) {
            n = 2;
        }
        return n;
    }

    @Override
    public void render(ItemEntity itemEntity, float f, float f2, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
        float f3;
        float f4;
        poseStack.pushPose();
        ItemStack itemStack = itemEntity.getItem();
        int n2 = itemStack.isEmpty() ? 187 : Item.getId(itemStack.getItem()) + itemStack.getDamageValue();
        this.random.setSeed(n2);
        BakedModel bakedModel = this.itemRenderer.getModel(itemStack, itemEntity.level, null);
        boolean bl = bakedModel.isGui3d();
        int n3 = this.getRenderAmount(itemStack);
        float f5 = 0.25f;
        float f6 = Mth.sin(((float)itemEntity.getAge() + f2) / 10.0f + itemEntity.bobOffs) * 0.1f + 0.1f;
        float f7 = bakedModel.getTransforms().getTransform((ItemTransforms.TransformType)ItemTransforms.TransformType.GROUND).scale.y();
        poseStack.translate(0.0, f6 + 0.25f * f7, 0.0);
        float f8 = itemEntity.getSpin(f2);
        poseStack.mulPose(Vector3f.YP.rotation(f8));
        float f9 = bakedModel.getTransforms().ground.scale.x();
        float f10 = bakedModel.getTransforms().ground.scale.y();
        float f11 = bakedModel.getTransforms().ground.scale.z();
        if (!bl) {
            float f12 = -0.0f * (float)(n3 - 1) * 0.5f * f9;
            f4 = -0.0f * (float)(n3 - 1) * 0.5f * f10;
            f3 = -0.09375f * (float)(n3 - 1) * 0.5f * f11;
            poseStack.translate(f12, f4, f3);
        }
        for (int i = 0; i < n3; ++i) {
            poseStack.pushPose();
            if (i > 0) {
                if (bl) {
                    f4 = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    f3 = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    float f13 = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    poseStack.translate(f4, f3, f13);
                } else {
                    f4 = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f;
                    f3 = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f;
                    poseStack.translate(f4, f3, 0.0);
                }
            }
            this.itemRenderer.render(itemStack, ItemTransforms.TransformType.GROUND, false, poseStack, multiBufferSource, n, OverlayTexture.NO_OVERLAY, bakedModel);
            poseStack.popPose();
            if (bl) continue;
            poseStack.translate(0.0f * f9, 0.0f * f10, 0.09375f * f11);
        }
        poseStack.popPose();
        super.render(itemEntity, f, f2, poseStack, multiBufferSource, n);
    }

    @Override
    public ResourceLocation getTextureLocation(ItemEntity itemEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}

