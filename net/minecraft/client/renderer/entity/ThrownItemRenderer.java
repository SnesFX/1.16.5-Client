/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;

public class ThrownItemRenderer<T extends Entity>
extends EntityRenderer<T> {
    private final ItemRenderer itemRenderer;
    private final float scale;
    private final boolean fullBright;

    public ThrownItemRenderer(EntityRenderDispatcher entityRenderDispatcher, ItemRenderer itemRenderer, float f, boolean bl) {
        super(entityRenderDispatcher);
        this.itemRenderer = itemRenderer;
        this.scale = f;
        this.fullBright = bl;
    }

    public ThrownItemRenderer(EntityRenderDispatcher entityRenderDispatcher, ItemRenderer itemRenderer) {
        this(entityRenderDispatcher, itemRenderer, 1.0f, false);
    }

    @Override
    protected int getBlockLightLevel(T t, BlockPos blockPos) {
        return this.fullBright ? 15 : super.getBlockLightLevel(t, blockPos);
    }

    @Override
    public void render(T t, float f, float f2, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
        if (((Entity)t).tickCount < 2 && this.entityRenderDispatcher.camera.getEntity().distanceToSqr((Entity)t) < 12.25) {
            return;
        }
        poseStack.pushPose();
        poseStack.scale(this.scale, this.scale, this.scale);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0f));
        this.itemRenderer.renderStatic(((ItemSupplier)t).getItem(), ItemTransforms.TransformType.GROUND, n, OverlayTexture.NO_OVERLAY, poseStack, multiBufferSource);
        poseStack.popPose();
        super.render(t, f, f2, poseStack, multiBufferSource, n);
    }

    @Override
    public ResourceLocation getTextureLocation(Entity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}

