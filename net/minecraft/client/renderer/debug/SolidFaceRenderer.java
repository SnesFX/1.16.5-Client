/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SolidFaceRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;

    public SolidFaceRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, double d, double d2, double d3) {
        Level level = this.minecraft.player.level;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.lineWidth(2.0f);
        RenderSystem.disableTexture();
        RenderSystem.depthMask(false);
        BlockPos blockPos = new BlockPos(d, d2, d3);
        for (BlockPos blockPos2 : BlockPos.betweenClosed(blockPos.offset(-6, -6, -6), blockPos.offset(6, 6, 6))) {
            BlockState blockState = level.getBlockState(blockPos2);
            if (blockState.is(Blocks.AIR)) continue;
            VoxelShape voxelShape = blockState.getShape(level, blockPos2);
            for (AABB aABB : voxelShape.toAabbs()) {
                Tesselator tesselator;
                BufferBuilder bufferBuilder;
                AABB aABB2 = aABB.move(blockPos2).inflate(0.002).move(-d, -d2, -d3);
                double d4 = aABB2.minX;
                double d5 = aABB2.minY;
                double d6 = aABB2.minZ;
                double d7 = aABB2.maxX;
                double d8 = aABB2.maxY;
                double d9 = aABB2.maxZ;
                float f = 1.0f;
                float f2 = 0.0f;
                float f3 = 0.0f;
                float f4 = 0.5f;
                if (blockState.isFaceSturdy(level, blockPos2, Direction.WEST)) {
                    tesselator = Tesselator.getInstance();
                    bufferBuilder = tesselator.getBuilder();
                    bufferBuilder.begin(5, DefaultVertexFormat.POSITION_COLOR);
                    bufferBuilder.vertex(d4, d5, d6).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    bufferBuilder.vertex(d4, d5, d9).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    bufferBuilder.vertex(d4, d8, d6).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    bufferBuilder.vertex(d4, d8, d9).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    tesselator.end();
                }
                if (blockState.isFaceSturdy(level, blockPos2, Direction.SOUTH)) {
                    tesselator = Tesselator.getInstance();
                    bufferBuilder = tesselator.getBuilder();
                    bufferBuilder.begin(5, DefaultVertexFormat.POSITION_COLOR);
                    bufferBuilder.vertex(d4, d8, d9).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    bufferBuilder.vertex(d4, d5, d9).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    bufferBuilder.vertex(d7, d8, d9).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    bufferBuilder.vertex(d7, d5, d9).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    tesselator.end();
                }
                if (blockState.isFaceSturdy(level, blockPos2, Direction.EAST)) {
                    tesselator = Tesselator.getInstance();
                    bufferBuilder = tesselator.getBuilder();
                    bufferBuilder.begin(5, DefaultVertexFormat.POSITION_COLOR);
                    bufferBuilder.vertex(d7, d5, d9).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    bufferBuilder.vertex(d7, d5, d6).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    bufferBuilder.vertex(d7, d8, d9).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    bufferBuilder.vertex(d7, d8, d6).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    tesselator.end();
                }
                if (blockState.isFaceSturdy(level, blockPos2, Direction.NORTH)) {
                    tesselator = Tesselator.getInstance();
                    bufferBuilder = tesselator.getBuilder();
                    bufferBuilder.begin(5, DefaultVertexFormat.POSITION_COLOR);
                    bufferBuilder.vertex(d7, d8, d6).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    bufferBuilder.vertex(d7, d5, d6).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    bufferBuilder.vertex(d4, d8, d6).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    bufferBuilder.vertex(d4, d5, d6).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    tesselator.end();
                }
                if (blockState.isFaceSturdy(level, blockPos2, Direction.DOWN)) {
                    tesselator = Tesselator.getInstance();
                    bufferBuilder = tesselator.getBuilder();
                    bufferBuilder.begin(5, DefaultVertexFormat.POSITION_COLOR);
                    bufferBuilder.vertex(d4, d5, d6).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    bufferBuilder.vertex(d7, d5, d6).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    bufferBuilder.vertex(d4, d5, d9).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    bufferBuilder.vertex(d7, d5, d9).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    tesselator.end();
                }
                if (!blockState.isFaceSturdy(level, blockPos2, Direction.UP)) continue;
                tesselator = Tesselator.getInstance();
                bufferBuilder = tesselator.getBuilder();
                bufferBuilder.begin(5, DefaultVertexFormat.POSITION_COLOR);
                bufferBuilder.vertex(d4, d8, d6).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                bufferBuilder.vertex(d4, d8, d9).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                bufferBuilder.vertex(d7, d8, d6).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                bufferBuilder.vertex(d7, d8, d9).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                tesselator.end();
            }
        }
        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }
}

