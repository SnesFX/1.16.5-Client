/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;

public class ItemFrameRenderer
extends EntityRenderer<ItemFrame> {
    private static final ModelResourceLocation FRAME_LOCATION = new ModelResourceLocation("item_frame", "map=false");
    private static final ModelResourceLocation MAP_FRAME_LOCATION = new ModelResourceLocation("item_frame", "map=true");
    private final Minecraft minecraft = Minecraft.getInstance();
    private final ItemRenderer itemRenderer;

    public ItemFrameRenderer(EntityRenderDispatcher entityRenderDispatcher, ItemRenderer itemRenderer) {
        super(entityRenderDispatcher);
        this.itemRenderer = itemRenderer;
    }

    @Override
    public void render(ItemFrame itemFrame, float f, float f2, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
        Object object;
        super.render(itemFrame, f, f2, poseStack, multiBufferSource, n);
        poseStack.pushPose();
        Direction direction = itemFrame.getDirection();
        Vec3 vec3 = this.getRenderOffset(itemFrame, f2);
        poseStack.translate(-vec3.x(), -vec3.y(), -vec3.z());
        double d = 0.46875;
        poseStack.translate((double)direction.getStepX() * 0.46875, (double)direction.getStepY() * 0.46875, (double)direction.getStepZ() * 0.46875);
        poseStack.mulPose(Vector3f.XP.rotationDegrees(itemFrame.xRot));
        poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0f - itemFrame.yRot));
        boolean bl = itemFrame.isInvisible();
        if (!bl) {
            object = this.minecraft.getBlockRenderer();
            ModelManager modelManager = ((BlockRenderDispatcher)object).getBlockModelShaper().getModelManager();
            ModelResourceLocation modelResourceLocation = itemFrame.getItem().getItem() == Items.FILLED_MAP ? MAP_FRAME_LOCATION : FRAME_LOCATION;
            poseStack.pushPose();
            poseStack.translate(-0.5, -0.5, -0.5);
            ((BlockRenderDispatcher)object).getModelRenderer().renderModel(poseStack.last(), multiBufferSource.getBuffer(Sheets.solidBlockSheet()), null, modelManager.getModel(modelResourceLocation), 1.0f, 1.0f, 1.0f, n, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }
        if (!((ItemStack)(object = itemFrame.getItem())).isEmpty()) {
            boolean bl2;
            boolean bl3 = bl2 = ((ItemStack)object).getItem() == Items.FILLED_MAP;
            if (bl) {
                poseStack.translate(0.0, 0.0, 0.5);
            } else {
                poseStack.translate(0.0, 0.0, 0.4375);
            }
            int n2 = bl2 ? itemFrame.getRotation() % 4 * 2 : itemFrame.getRotation();
            poseStack.mulPose(Vector3f.ZP.rotationDegrees((float)n2 * 360.0f / 8.0f));
            if (bl2) {
                poseStack.mulPose(Vector3f.ZP.rotationDegrees(180.0f));
                float f3 = 0.0078125f;
                poseStack.scale(0.0078125f, 0.0078125f, 0.0078125f);
                poseStack.translate(-64.0, -64.0, 0.0);
                MapItemSavedData mapItemSavedData = MapItem.getOrCreateSavedData((ItemStack)object, itemFrame.level);
                poseStack.translate(0.0, 0.0, -1.0);
                if (mapItemSavedData != null) {
                    this.minecraft.gameRenderer.getMapRenderer().render(poseStack, multiBufferSource, mapItemSavedData, true, n);
                }
            } else {
                poseStack.scale(0.5f, 0.5f, 0.5f);
                this.itemRenderer.renderStatic((ItemStack)object, ItemTransforms.TransformType.FIXED, n, OverlayTexture.NO_OVERLAY, poseStack, multiBufferSource);
            }
        }
        poseStack.popPose();
    }

    @Override
    public Vec3 getRenderOffset(ItemFrame itemFrame, float f) {
        return new Vec3((float)itemFrame.getDirection().getStepX() * 0.3f, -0.25, (float)itemFrame.getDirection().getStepZ() * 0.3f);
    }

    @Override
    public ResourceLocation getTextureLocation(ItemFrame itemFrame) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    @Override
    protected boolean shouldShowName(ItemFrame itemFrame) {
        if (!Minecraft.renderNames() || itemFrame.getItem().isEmpty() || !itemFrame.getItem().hasCustomHoverName() || this.entityRenderDispatcher.crosshairPickEntity != itemFrame) {
            return false;
        }
        double d = this.entityRenderDispatcher.distanceToSqr(itemFrame);
        float f = itemFrame.isDiscrete() ? 32.0f : 64.0f;
        return d < (double)(f * f);
    }

    @Override
    protected void renderNameTag(ItemFrame itemFrame, Component component, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
        super.renderNameTag(itemFrame, itemFrame.getItem().getHoverName(), poseStack, multiBufferSource, n);
    }
}

