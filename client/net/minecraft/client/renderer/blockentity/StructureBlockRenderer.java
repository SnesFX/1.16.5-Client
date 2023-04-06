/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StructureMode;

public class StructureBlockRenderer
extends BlockEntityRenderer<StructureBlockEntity> {
    public StructureBlockRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
    }

    @Override
    public void render(StructureBlockEntity structureBlockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int n, int n2) {
        double d;
        double d2;
        double d3;
        double d4;
        double d5;
        double d6;
        if (!Minecraft.getInstance().player.canUseGameMasterBlocks() && !Minecraft.getInstance().player.isSpectator()) {
            return;
        }
        BlockPos blockPos = structureBlockEntity.getStructurePos();
        BlockPos blockPos2 = structureBlockEntity.getStructureSize();
        if (blockPos2.getX() < 1 || blockPos2.getY() < 1 || blockPos2.getZ() < 1) {
            return;
        }
        if (structureBlockEntity.getMode() != StructureMode.SAVE && structureBlockEntity.getMode() != StructureMode.LOAD) {
            return;
        }
        double d7 = blockPos.getX();
        double d8 = blockPos.getZ();
        double d9 = blockPos.getY();
        double d10 = d9 + (double)blockPos2.getY();
        switch (structureBlockEntity.getMirror()) {
            case LEFT_RIGHT: {
                d3 = blockPos2.getX();
                d5 = -blockPos2.getZ();
                break;
            }
            case FRONT_BACK: {
                d3 = -blockPos2.getX();
                d5 = blockPos2.getZ();
                break;
            }
            default: {
                d3 = blockPos2.getX();
                d5 = blockPos2.getZ();
            }
        }
        switch (structureBlockEntity.getRotation()) {
            case CLOCKWISE_90: {
                d = d5 < 0.0 ? d7 : d7 + 1.0;
                d6 = d3 < 0.0 ? d8 + 1.0 : d8;
                d2 = d - d5;
                d4 = d6 + d3;
                break;
            }
            case CLOCKWISE_180: {
                d = d3 < 0.0 ? d7 : d7 + 1.0;
                d6 = d5 < 0.0 ? d8 : d8 + 1.0;
                d2 = d - d3;
                d4 = d6 - d5;
                break;
            }
            case COUNTERCLOCKWISE_90: {
                d = d5 < 0.0 ? d7 + 1.0 : d7;
                d6 = d3 < 0.0 ? d8 : d8 + 1.0;
                d2 = d + d5;
                d4 = d6 - d3;
                break;
            }
            default: {
                d = d3 < 0.0 ? d7 + 1.0 : d7;
                d6 = d5 < 0.0 ? d8 + 1.0 : d8;
                d2 = d + d3;
                d4 = d6 + d5;
            }
        }
        float f2 = 1.0f;
        float f3 = 0.9f;
        float f4 = 0.5f;
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.lines());
        if (structureBlockEntity.getMode() == StructureMode.SAVE || structureBlockEntity.getShowBoundingBox()) {
            LevelRenderer.renderLineBox(poseStack, vertexConsumer, d, d9, d6, d2, d10, d4, 0.9f, 0.9f, 0.9f, 1.0f, 0.5f, 0.5f, 0.5f);
        }
        if (structureBlockEntity.getMode() == StructureMode.SAVE && structureBlockEntity.getShowAir()) {
            this.renderInvisibleBlocks(structureBlockEntity, vertexConsumer, blockPos, true, poseStack);
            this.renderInvisibleBlocks(structureBlockEntity, vertexConsumer, blockPos, false, poseStack);
        }
    }

    private void renderInvisibleBlocks(StructureBlockEntity structureBlockEntity, VertexConsumer vertexConsumer, BlockPos blockPos, boolean bl, PoseStack poseStack) {
        Level level = structureBlockEntity.getLevel();
        BlockPos blockPos2 = structureBlockEntity.getBlockPos();
        BlockPos blockPos3 = blockPos2.offset(blockPos);
        for (BlockPos blockPos4 : BlockPos.betweenClosed(blockPos3, blockPos3.offset(structureBlockEntity.getStructureSize()).offset(-1, -1, -1))) {
            BlockState blockState = level.getBlockState(blockPos4);
            boolean bl2 = blockState.isAir();
            boolean bl3 = blockState.is(Blocks.STRUCTURE_VOID);
            if (!bl2 && !bl3) continue;
            float f = bl2 ? 0.05f : 0.0f;
            double d = (float)(blockPos4.getX() - blockPos2.getX()) + 0.45f - f;
            double d2 = (float)(blockPos4.getY() - blockPos2.getY()) + 0.45f - f;
            double d3 = (float)(blockPos4.getZ() - blockPos2.getZ()) + 0.45f - f;
            double d4 = (float)(blockPos4.getX() - blockPos2.getX()) + 0.55f + f;
            double d5 = (float)(blockPos4.getY() - blockPos2.getY()) + 0.55f + f;
            double d6 = (float)(blockPos4.getZ() - blockPos2.getZ()) + 0.55f + f;
            if (bl) {
                LevelRenderer.renderLineBox(poseStack, vertexConsumer, d, d2, d3, d4, d5, d6, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f);
                continue;
            }
            if (bl2) {
                LevelRenderer.renderLineBox(poseStack, vertexConsumer, d, d2, d3, d4, d5, d6, 0.5f, 0.5f, 1.0f, 1.0f, 0.5f, 0.5f, 1.0f);
                continue;
            }
            LevelRenderer.renderLineBox(poseStack, vertexConsumer, d, d2, d3, d4, d5, d6, 1.0f, 0.25f, 0.25f, 1.0f, 1.0f, 0.25f, 0.25f);
        }
    }

    @Override
    public boolean shouldRenderOffScreen(StructureBlockEntity structureBlockEntity) {
        return true;
    }

}

