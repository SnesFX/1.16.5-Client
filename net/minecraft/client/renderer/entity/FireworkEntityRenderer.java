/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;

public class FireworkEntityRenderer
extends EntityRenderer<FireworkRocketEntity> {
    private final ItemRenderer itemRenderer;

    public FireworkEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, ItemRenderer itemRenderer) {
        super(entityRenderDispatcher);
        this.itemRenderer = itemRenderer;
    }

    @Override
    public void render(FireworkRocketEntity fireworkRocketEntity, float f, float f2, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
        poseStack.pushPose();
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0f));
        if (fireworkRocketEntity.isShotAtAngle()) {
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(180.0f));
            poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0f));
            poseStack.mulPose(Vector3f.XP.rotationDegrees(90.0f));
        }
        this.itemRenderer.renderStatic(fireworkRocketEntity.getItem(), ItemTransforms.TransformType.GROUND, n, OverlayTexture.NO_OVERLAY, poseStack, multiBufferSource);
        poseStack.popPose();
        super.render(fireworkRocketEntity, f, f2, poseStack, multiBufferSource, n);
    }

    @Override
    public ResourceLocation getTextureLocation(FireworkRocketEntity fireworkRocketEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}

