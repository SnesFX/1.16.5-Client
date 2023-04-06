/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package net.minecraft.client.renderer.blockentity;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import java.util.List;
import java.util.Random;
import java.util.function.IntFunction;
import java.util.stream.Collector;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.phys.Vec3;

public class TheEndPortalRenderer<T extends TheEndPortalBlockEntity>
extends BlockEntityRenderer<T> {
    public static final ResourceLocation END_SKY_LOCATION = new ResourceLocation("textures/environment/end_sky.png");
    public static final ResourceLocation END_PORTAL_LOCATION = new ResourceLocation("textures/entity/end_portal.png");
    private static final Random RANDOM = new Random(31100L);
    private static final List<RenderType> RENDER_TYPES = (List)IntStream.range(0, 16).mapToObj(n -> RenderType.endPortal(n + 1)).collect(ImmutableList.toImmutableList());

    public TheEndPortalRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
    }

    @Override
    public void render(T t, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int n, int n2) {
        RANDOM.setSeed(31100L);
        double d = ((BlockEntity)t).getBlockPos().distSqr(this.renderer.camera.getPosition(), true);
        int n3 = this.getPasses(d);
        float f2 = this.getOffset();
        Matrix4f matrix4f = poseStack.last().pose();
        this.renderCube(t, f2, 0.15f, matrix4f, multiBufferSource.getBuffer(RENDER_TYPES.get(0)));
        for (int i = 1; i < n3; ++i) {
            this.renderCube(t, f2, 2.0f / (float)(18 - i), matrix4f, multiBufferSource.getBuffer(RENDER_TYPES.get(i)));
        }
    }

    private void renderCube(T t, float f, float f2, Matrix4f matrix4f, VertexConsumer vertexConsumer) {
        float f3 = (RANDOM.nextFloat() * 0.5f + 0.1f) * f2;
        float f4 = (RANDOM.nextFloat() * 0.5f + 0.4f) * f2;
        float f5 = (RANDOM.nextFloat() * 0.5f + 0.5f) * f2;
        this.renderFace(t, matrix4f, vertexConsumer, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, f3, f4, f5, Direction.SOUTH);
        this.renderFace(t, matrix4f, vertexConsumer, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, f3, f4, f5, Direction.NORTH);
        this.renderFace(t, matrix4f, vertexConsumer, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, f3, f4, f5, Direction.EAST);
        this.renderFace(t, matrix4f, vertexConsumer, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, f3, f4, f5, Direction.WEST);
        this.renderFace(t, matrix4f, vertexConsumer, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, f3, f4, f5, Direction.DOWN);
        this.renderFace(t, matrix4f, vertexConsumer, 0.0f, 1.0f, f, f, 1.0f, 1.0f, 0.0f, 0.0f, f3, f4, f5, Direction.UP);
    }

    private void renderFace(T t, Matrix4f matrix4f, VertexConsumer vertexConsumer, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, float f10, float f11, Direction direction) {
        if (((TheEndPortalBlockEntity)t).shouldRenderFace(direction)) {
            vertexConsumer.vertex(matrix4f, f, f3, f5).color(f9, f10, f11, 1.0f).endVertex();
            vertexConsumer.vertex(matrix4f, f2, f3, f6).color(f9, f10, f11, 1.0f).endVertex();
            vertexConsumer.vertex(matrix4f, f2, f4, f7).color(f9, f10, f11, 1.0f).endVertex();
            vertexConsumer.vertex(matrix4f, f, f4, f8).color(f9, f10, f11, 1.0f).endVertex();
        }
    }

    protected int getPasses(double d) {
        if (d > 36864.0) {
            return 1;
        }
        if (d > 25600.0) {
            return 3;
        }
        if (d > 16384.0) {
            return 5;
        }
        if (d > 9216.0) {
            return 7;
        }
        if (d > 4096.0) {
            return 9;
        }
        if (d > 1024.0) {
            return 11;
        }
        if (d > 576.0) {
            return 13;
        }
        if (d > 256.0) {
            return 14;
        }
        return 15;
    }

    protected float getOffset() {
        return 0.75f;
    }
}

