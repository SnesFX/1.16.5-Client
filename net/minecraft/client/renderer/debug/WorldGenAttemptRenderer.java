/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.List;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;

public class WorldGenAttemptRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private final List<BlockPos> toRender = Lists.newArrayList();
    private final List<Float> scales = Lists.newArrayList();
    private final List<Float> alphas = Lists.newArrayList();
    private final List<Float> reds = Lists.newArrayList();
    private final List<Float> greens = Lists.newArrayList();
    private final List<Float> blues = Lists.newArrayList();

    public void addPos(BlockPos blockPos, float f, float f2, float f3, float f4, float f5) {
        this.toRender.add(blockPos);
        this.scales.add(Float.valueOf(f));
        this.alphas.add(Float.valueOf(f5));
        this.reds.add(Float.valueOf(f2));
        this.greens.add(Float.valueOf(f3));
        this.blues.add(Float.valueOf(f4));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, double d, double d2, double d3) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        bufferBuilder.begin(5, DefaultVertexFormat.POSITION_COLOR);
        for (int i = 0; i < this.toRender.size(); ++i) {
            BlockPos blockPos = this.toRender.get(i);
            Float f = this.scales.get(i);
            float f2 = f.floatValue() / 2.0f;
            LevelRenderer.addChainedFilledBoxVertices(bufferBuilder, (double)((float)blockPos.getX() + 0.5f - f2) - d, (double)((float)blockPos.getY() + 0.5f - f2) - d2, (double)((float)blockPos.getZ() + 0.5f - f2) - d3, (double)((float)blockPos.getX() + 0.5f + f2) - d, (double)((float)blockPos.getY() + 0.5f + f2) - d2, (double)((float)blockPos.getZ() + 0.5f + f2) - d3, this.reds.get(i).floatValue(), this.greens.get(i).floatValue(), this.blues.get(i).floatValue(), this.alphas.get(i).floatValue());
        }
        tesselator.end();
        RenderSystem.enableTexture();
        RenderSystem.popMatrix();
    }
}

