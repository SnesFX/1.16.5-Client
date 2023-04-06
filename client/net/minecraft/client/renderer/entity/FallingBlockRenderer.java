/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class FallingBlockRenderer
extends EntityRenderer<FallingBlockEntity> {
    public FallingBlockRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.shadowRadius = 0.5f;
    }

    @Override
    public void render(FallingBlockEntity fallingBlockEntity, float f, float f2, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
        BlockState blockState = fallingBlockEntity.getBlockState();
        if (blockState.getRenderShape() != RenderShape.MODEL) {
            return;
        }
        Level level = fallingBlockEntity.getLevel();
        if (blockState == level.getBlockState(fallingBlockEntity.blockPosition()) || blockState.getRenderShape() == RenderShape.INVISIBLE) {
            return;
        }
        poseStack.pushPose();
        BlockPos blockPos = new BlockPos(fallingBlockEntity.getX(), fallingBlockEntity.getBoundingBox().maxY, fallingBlockEntity.getZ());
        poseStack.translate(-0.5, 0.0, -0.5);
        BlockRenderDispatcher blockRenderDispatcher = Minecraft.getInstance().getBlockRenderer();
        blockRenderDispatcher.getModelRenderer().tesselateBlock(level, blockRenderDispatcher.getBlockModel(blockState), blockState, blockPos, poseStack, multiBufferSource.getBuffer(ItemBlockRenderTypes.getMovingBlockRenderType(blockState)), false, new Random(), blockState.getSeed(fallingBlockEntity.getStartPos()), OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        super.render(fallingBlockEntity, f, f2, poseStack, multiBufferSource, n);
    }

    @Override
    public ResourceLocation getTextureLocation(FallingBlockEntity fallingBlockEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}

