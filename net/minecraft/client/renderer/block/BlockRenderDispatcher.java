/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Random;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class BlockRenderDispatcher
implements ResourceManagerReloadListener {
    private final BlockModelShaper blockModelShaper;
    private final ModelBlockRenderer modelRenderer;
    private final LiquidBlockRenderer liquidBlockRenderer;
    private final Random random = new Random();
    private final BlockColors blockColors;

    public BlockRenderDispatcher(BlockModelShaper blockModelShaper, BlockColors blockColors) {
        this.blockModelShaper = blockModelShaper;
        this.blockColors = blockColors;
        this.modelRenderer = new ModelBlockRenderer(this.blockColors);
        this.liquidBlockRenderer = new LiquidBlockRenderer();
    }

    public BlockModelShaper getBlockModelShaper() {
        return this.blockModelShaper;
    }

    public void renderBreakingTexture(BlockState blockState, BlockPos blockPos, BlockAndTintGetter blockAndTintGetter, PoseStack poseStack, VertexConsumer vertexConsumer) {
        if (blockState.getRenderShape() != RenderShape.MODEL) {
            return;
        }
        BakedModel bakedModel = this.blockModelShaper.getBlockModel(blockState);
        long l = blockState.getSeed(blockPos);
        this.modelRenderer.tesselateBlock(blockAndTintGetter, bakedModel, blockState, blockPos, poseStack, vertexConsumer, true, this.random, l, OverlayTexture.NO_OVERLAY);
    }

    public boolean renderBatched(BlockState blockState, BlockPos blockPos, BlockAndTintGetter blockAndTintGetter, PoseStack poseStack, VertexConsumer vertexConsumer, boolean bl, Random random) {
        try {
            RenderShape renderShape = blockState.getRenderShape();
            if (renderShape != RenderShape.MODEL) {
                return false;
            }
            return this.modelRenderer.tesselateBlock(blockAndTintGetter, this.getBlockModel(blockState), blockState, blockPos, poseStack, vertexConsumer, bl, random, blockState.getSeed(blockPos), OverlayTexture.NO_OVERLAY);
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.forThrowable(throwable, "Tesselating block in world");
            CrashReportCategory crashReportCategory = crashReport.addCategory("Block being tesselated");
            CrashReportCategory.populateBlockDetails(crashReportCategory, blockPos, blockState);
            throw new ReportedException(crashReport);
        }
    }

    public boolean renderLiquid(BlockPos blockPos, BlockAndTintGetter blockAndTintGetter, VertexConsumer vertexConsumer, FluidState fluidState) {
        try {
            return this.liquidBlockRenderer.tesselate(blockAndTintGetter, blockPos, vertexConsumer, fluidState);
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.forThrowable(throwable, "Tesselating liquid in world");
            CrashReportCategory crashReportCategory = crashReport.addCategory("Block being tesselated");
            CrashReportCategory.populateBlockDetails(crashReportCategory, blockPos, null);
            throw new ReportedException(crashReport);
        }
    }

    public ModelBlockRenderer getModelRenderer() {
        return this.modelRenderer;
    }

    public BakedModel getBlockModel(BlockState blockState) {
        return this.blockModelShaper.getBlockModel(blockState);
    }

    public void renderSingleBlock(BlockState blockState, PoseStack poseStack, MultiBufferSource multiBufferSource, int n, int n2) {
        RenderShape renderShape = blockState.getRenderShape();
        if (renderShape == RenderShape.INVISIBLE) {
            return;
        }
        switch (renderShape) {
            case MODEL: {
                BakedModel bakedModel = this.getBlockModel(blockState);
                int n3 = this.blockColors.getColor(blockState, null, null, 0);
                float f = (float)(n3 >> 16 & 0xFF) / 255.0f;
                float f2 = (float)(n3 >> 8 & 0xFF) / 255.0f;
                float f3 = (float)(n3 & 0xFF) / 255.0f;
                this.modelRenderer.renderModel(poseStack.last(), multiBufferSource.getBuffer(ItemBlockRenderTypes.getRenderType(blockState, false)), blockState, bakedModel, f, f2, f3, n, n2);
                break;
            }
            case ENTITYBLOCK_ANIMATED: {
                BlockEntityWithoutLevelRenderer.instance.renderByItem(new ItemStack(blockState.getBlock()), ItemTransforms.TransformType.NONE, poseStack, multiBufferSource, n, n2);
            }
        }
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        this.liquidBlockRenderer.setupSprites();
    }

}

