/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 */
package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;

public class CaveDebugRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private final Map<BlockPos, BlockPos> tunnelsList = Maps.newHashMap();
    private final Map<BlockPos, Float> thicknessMap = Maps.newHashMap();
    private final List<BlockPos> startPoses = Lists.newArrayList();

    public void addTunnel(BlockPos blockPos, List<BlockPos> list, List<Float> list2) {
        for (int i = 0; i < list.size(); ++i) {
            this.tunnelsList.put(list.get(i), blockPos);
            this.thicknessMap.put(list.get(i), list2.get(i));
        }
        this.startPoses.add(blockPos);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, double d, double d2, double d3) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        BlockPos blockPos = new BlockPos(d, 0.0, d3);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        bufferBuilder.begin(5, DefaultVertexFormat.POSITION_COLOR);
        for (Map.Entry<BlockPos, BlockPos> object : this.tunnelsList.entrySet()) {
            BlockPos blockPos2 = object.getKey();
            BlockPos blockPos3 = object.getValue();
            float f = (float)(blockPos3.getX() * 128 % 256) / 256.0f;
            float f2 = (float)(blockPos3.getY() * 128 % 256) / 256.0f;
            float f3 = (float)(blockPos3.getZ() * 128 % 256) / 256.0f;
            float f4 = this.thicknessMap.get(blockPos2).floatValue();
            if (!blockPos.closerThan(blockPos2, 160.0)) continue;
            LevelRenderer.addChainedFilledBoxVertices(bufferBuilder, (double)((float)blockPos2.getX() + 0.5f) - d - (double)f4, (double)((float)blockPos2.getY() + 0.5f) - d2 - (double)f4, (double)((float)blockPos2.getZ() + 0.5f) - d3 - (double)f4, (double)((float)blockPos2.getX() + 0.5f) - d + (double)f4, (double)((float)blockPos2.getY() + 0.5f) - d2 + (double)f4, (double)((float)blockPos2.getZ() + 0.5f) - d3 + (double)f4, f, f2, f3, 0.5f);
        }
        for (BlockPos blockPos4 : this.startPoses) {
            if (!blockPos.closerThan(blockPos4, 160.0)) continue;
            LevelRenderer.addChainedFilledBoxVertices(bufferBuilder, (double)blockPos4.getX() - d, (double)blockPos4.getY() - d2, (double)blockPos4.getZ() - d3, (double)((float)blockPos4.getX() + 1.0f) - d, (double)((float)blockPos4.getY() + 1.0f) - d2, (double)((float)blockPos4.getZ() + 1.0f) - d3, 1.0f, 1.0f, 1.0f, 1.0f);
        }
        tesselator.end();
        RenderSystem.enableDepthTest();
        RenderSystem.enableTexture();
        RenderSystem.popMatrix();
    }
}

