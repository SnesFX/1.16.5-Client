/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CollisionBoxRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;
    private double lastUpdateTime = Double.MIN_VALUE;
    private List<VoxelShape> shapes = Collections.emptyList();

    public CollisionBoxRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, double d, double d2, double d3) {
        Object object;
        double d4 = Util.getNanos();
        if (d4 - this.lastUpdateTime > 1.0E8) {
            this.lastUpdateTime = d4;
            object = this.minecraft.gameRenderer.getMainCamera().getEntity();
            this.shapes = ((Entity)object).level.getCollisions((Entity)object, ((Entity)object).getBoundingBox().inflate(6.0), entity -> true).collect(Collectors.toList());
        }
        object = multiBufferSource.getBuffer(RenderType.lines());
        for (VoxelShape voxelShape : this.shapes) {
            LevelRenderer.renderVoxelShape(poseStack, (VertexConsumer)object, voxelShape, -d, -d2, -d3, 1.0f, 1.0f, 1.0f, 1.0f);
        }
    }
}

