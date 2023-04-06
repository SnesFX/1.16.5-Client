/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;

public class PathfindingRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private final Map<Integer, Path> pathMap = Maps.newHashMap();
    private final Map<Integer, Float> pathMaxDist = Maps.newHashMap();
    private final Map<Integer, Long> creationMap = Maps.newHashMap();

    public void addPath(int n, Path path, float f) {
        this.pathMap.put(n, path);
        this.creationMap.put(n, Util.getMillis());
        this.pathMaxDist.put(n, Float.valueOf(f));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, double d, double d2, double d3) {
        if (this.pathMap.isEmpty()) {
            return;
        }
        long l = Util.getMillis();
        for (Integer n : this.pathMap.keySet()) {
            Path path = this.pathMap.get(n);
            float f = this.pathMaxDist.get(n).floatValue();
            PathfindingRenderer.renderPath(path, f, true, true, d, d2, d3);
        }
        for (Integer n : this.creationMap.keySet().toArray(new Integer[0])) {
            if (l - this.creationMap.get(n) <= 5000L) continue;
            this.pathMap.remove(n);
            this.creationMap.remove(n);
        }
    }

    public static void renderPath(Path path, float f, boolean bl, boolean bl2, double d, double d2, double d3) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(0.0f, 1.0f, 0.0f, 0.75f);
        RenderSystem.disableTexture();
        RenderSystem.lineWidth(6.0f);
        PathfindingRenderer.doRenderPath(path, f, bl, bl2, d, d2, d3);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
    }

    private static void doRenderPath(Path path, float f, boolean bl, boolean bl2, double d, double d2, double d3) {
        int n;
        PathfindingRenderer.renderPathLine(path, d, d2, d3);
        BlockPos blockPos = path.getTarget();
        if (PathfindingRenderer.distanceToCamera(blockPos, d, d2, d3) <= 80.0f) {
            DebugRenderer.renderFilledBox(new AABB((float)blockPos.getX() + 0.25f, (float)blockPos.getY() + 0.25f, (double)blockPos.getZ() + 0.25, (float)blockPos.getX() + 0.75f, (float)blockPos.getY() + 0.75f, (float)blockPos.getZ() + 0.75f).move(-d, -d2, -d3), 0.0f, 1.0f, 0.0f, 0.5f);
            for (n = 0; n < path.getNodeCount(); ++n) {
                Node node = path.getNode(n);
                if (!(PathfindingRenderer.distanceToCamera(node.asBlockPos(), d, d2, d3) <= 80.0f)) continue;
                float f2 = n == path.getNextNodeIndex() ? 1.0f : 0.0f;
                float f3 = n == path.getNextNodeIndex() ? 0.0f : 1.0f;
                DebugRenderer.renderFilledBox(new AABB((float)node.x + 0.5f - f, (float)node.y + 0.01f * (float)n, (float)node.z + 0.5f - f, (float)node.x + 0.5f + f, (float)node.y + 0.25f + 0.01f * (float)n, (float)node.z + 0.5f + f).move(-d, -d2, -d3), f2, 0.0f, f3, 0.5f);
            }
        }
        if (bl) {
            for (Node node : path.getClosedSet()) {
                if (!(PathfindingRenderer.distanceToCamera(node.asBlockPos(), d, d2, d3) <= 80.0f)) continue;
                DebugRenderer.renderFilledBox(new AABB((float)node.x + 0.5f - f / 2.0f, (float)node.y + 0.01f, (float)node.z + 0.5f - f / 2.0f, (float)node.x + 0.5f + f / 2.0f, (double)node.y + 0.1, (float)node.z + 0.5f + f / 2.0f).move(-d, -d2, -d3), 1.0f, 0.8f, 0.8f, 0.5f);
            }
            for (Node node : path.getOpenSet()) {
                if (!(PathfindingRenderer.distanceToCamera(node.asBlockPos(), d, d2, d3) <= 80.0f)) continue;
                DebugRenderer.renderFilledBox(new AABB((float)node.x + 0.5f - f / 2.0f, (float)node.y + 0.01f, (float)node.z + 0.5f - f / 2.0f, (float)node.x + 0.5f + f / 2.0f, (double)node.y + 0.1, (float)node.z + 0.5f + f / 2.0f).move(-d, -d2, -d3), 0.8f, 1.0f, 1.0f, 0.5f);
            }
        }
        if (bl2) {
            for (n = 0; n < path.getNodeCount(); ++n) {
                Node node = path.getNode(n);
                if (!(PathfindingRenderer.distanceToCamera(node.asBlockPos(), d, d2, d3) <= 80.0f)) continue;
                DebugRenderer.renderFloatingText(String.format("%s", new Object[]{node.type}), (double)node.x + 0.5, (double)node.y + 0.75, (double)node.z + 0.5, -1, 0.02f, true, 0.0f, true);
                DebugRenderer.renderFloatingText(String.format(Locale.ROOT, "%.2f", Float.valueOf(node.costMalus)), (double)node.x + 0.5, (double)node.y + 0.25, (double)node.z + 0.5, -1, 0.02f, true, 0.0f, true);
            }
        }
    }

    public static void renderPathLine(Path path, double d, double d2, double d3) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        bufferBuilder.begin(3, DefaultVertexFormat.POSITION_COLOR);
        for (int i = 0; i < path.getNodeCount(); ++i) {
            Node node = path.getNode(i);
            if (PathfindingRenderer.distanceToCamera(node.asBlockPos(), d, d2, d3) > 80.0f) continue;
            float f = (float)i / (float)path.getNodeCount() * 0.33f;
            int n = i == 0 ? 0 : Mth.hsvToRgb(f, 0.9f, 0.9f);
            int n2 = n >> 16 & 0xFF;
            int n3 = n >> 8 & 0xFF;
            int n4 = n & 0xFF;
            bufferBuilder.vertex((double)node.x - d + 0.5, (double)node.y - d2 + 0.5, (double)node.z - d3 + 0.5).color(n2, n3, n4, 255).endVertex();
        }
        tesselator.end();
    }

    private static float distanceToCamera(BlockPos blockPos, double d, double d2, double d3) {
        return (float)(Math.abs((double)blockPos.getX() - d) + Math.abs((double)blockPos.getY() - d2) + Math.abs((double)blockPos.getZ() - d3));
    }
}

