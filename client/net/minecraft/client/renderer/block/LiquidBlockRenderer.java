/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.block;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LiquidBlockRenderer {
    private final TextureAtlasSprite[] lavaIcons = new TextureAtlasSprite[2];
    private final TextureAtlasSprite[] waterIcons = new TextureAtlasSprite[2];
    private TextureAtlasSprite waterOverlay;

    protected void setupSprites() {
        this.lavaIcons[0] = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(Blocks.LAVA.defaultBlockState()).getParticleIcon();
        this.lavaIcons[1] = ModelBakery.LAVA_FLOW.sprite();
        this.waterIcons[0] = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(Blocks.WATER.defaultBlockState()).getParticleIcon();
        this.waterIcons[1] = ModelBakery.WATER_FLOW.sprite();
        this.waterOverlay = ModelBakery.WATER_OVERLAY.sprite();
    }

    private static boolean isNeighborSameFluid(BlockGetter blockGetter, BlockPos blockPos, Direction direction, FluidState fluidState) {
        BlockPos blockPos2 = blockPos.relative(direction);
        FluidState fluidState2 = blockGetter.getFluidState(blockPos2);
        return fluidState2.getType().isSame(fluidState.getType());
    }

    private static boolean isFaceOccludedByState(BlockGetter blockGetter, Direction direction, float f, BlockPos blockPos, BlockState blockState) {
        if (blockState.canOcclude()) {
            VoxelShape voxelShape = Shapes.box(0.0, 0.0, 0.0, 1.0, f, 1.0);
            VoxelShape voxelShape2 = blockState.getOcclusionShape(blockGetter, blockPos);
            return Shapes.blockOccudes(voxelShape, voxelShape2, direction);
        }
        return false;
    }

    private static boolean isFaceOccludedByNeighbor(BlockGetter blockGetter, BlockPos blockPos, Direction direction, float f) {
        BlockPos blockPos2 = blockPos.relative(direction);
        BlockState blockState = blockGetter.getBlockState(blockPos2);
        return LiquidBlockRenderer.isFaceOccludedByState(blockGetter, direction, f, blockPos2, blockState);
    }

    private static boolean isFaceOccludedBySelf(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, Direction direction) {
        return LiquidBlockRenderer.isFaceOccludedByState(blockGetter, direction.getOpposite(), 1.0f, blockPos, blockState);
    }

    public static boolean shouldRenderFace(BlockAndTintGetter blockAndTintGetter, BlockPos blockPos, FluidState fluidState, BlockState blockState, Direction direction) {
        return !LiquidBlockRenderer.isFaceOccludedBySelf(blockAndTintGetter, blockPos, blockState, direction) && !LiquidBlockRenderer.isNeighborSameFluid(blockAndTintGetter, blockPos, direction, fluidState);
    }

    public boolean tesselate(BlockAndTintGetter blockAndTintGetter, BlockPos blockPos, VertexConsumer vertexConsumer, FluidState fluidState) {
        float f;
        float f2;
        float f3;
        float f4;
        float f5;
        float f6;
        float f7;
        float f8;
        float f9;
        float f10;
        float f11;
        boolean bl = fluidState.is(FluidTags.LAVA);
        TextureAtlasSprite[] arrtextureAtlasSprite = bl ? this.lavaIcons : this.waterIcons;
        BlockState blockState = blockAndTintGetter.getBlockState(blockPos);
        int n = bl ? 16777215 : BiomeColors.getAverageWaterColor(blockAndTintGetter, blockPos);
        float f12 = (float)(n >> 16 & 0xFF) / 255.0f;
        float f13 = (float)(n >> 8 & 0xFF) / 255.0f;
        float f14 = (float)(n & 0xFF) / 255.0f;
        boolean bl2 = !LiquidBlockRenderer.isNeighborSameFluid(blockAndTintGetter, blockPos, Direction.UP, fluidState);
        boolean bl3 = LiquidBlockRenderer.shouldRenderFace(blockAndTintGetter, blockPos, fluidState, blockState, Direction.DOWN) && !LiquidBlockRenderer.isFaceOccludedByNeighbor(blockAndTintGetter, blockPos, Direction.DOWN, 0.8888889f);
        boolean bl4 = LiquidBlockRenderer.shouldRenderFace(blockAndTintGetter, blockPos, fluidState, blockState, Direction.NORTH);
        boolean bl5 = LiquidBlockRenderer.shouldRenderFace(blockAndTintGetter, blockPos, fluidState, blockState, Direction.SOUTH);
        boolean bl6 = LiquidBlockRenderer.shouldRenderFace(blockAndTintGetter, blockPos, fluidState, blockState, Direction.WEST);
        boolean bl7 = LiquidBlockRenderer.shouldRenderFace(blockAndTintGetter, blockPos, fluidState, blockState, Direction.EAST);
        if (!(bl2 || bl3 || bl7 || bl6 || bl4 || bl5)) {
            return false;
        }
        boolean bl8 = false;
        float f15 = blockAndTintGetter.getShade(Direction.DOWN, true);
        float f16 = blockAndTintGetter.getShade(Direction.UP, true);
        float f17 = blockAndTintGetter.getShade(Direction.NORTH, true);
        float f18 = blockAndTintGetter.getShade(Direction.WEST, true);
        float f19 = this.getWaterHeight(blockAndTintGetter, blockPos, fluidState.getType());
        float f20 = this.getWaterHeight(blockAndTintGetter, blockPos.south(), fluidState.getType());
        float f21 = this.getWaterHeight(blockAndTintGetter, blockPos.east().south(), fluidState.getType());
        float f22 = this.getWaterHeight(blockAndTintGetter, blockPos.east(), fluidState.getType());
        double d = blockPos.getX() & 0xF;
        double d2 = blockPos.getY() & 0xF;
        double d3 = blockPos.getZ() & 0xF;
        float f23 = 0.001f;
        float f24 = f4 = bl3 ? 0.001f : 0.0f;
        if (bl2 && !LiquidBlockRenderer.isFaceOccludedByNeighbor(blockAndTintGetter, blockPos, Direction.UP, Math.min(Math.min(f19, f20), Math.min(f21, f22)))) {
            float f25;
            TextureAtlasSprite textureAtlasSprite;
            float f26;
            float f27;
            float f28;
            float f29;
            bl8 = true;
            f19 -= 0.001f;
            f20 -= 0.001f;
            f21 -= 0.001f;
            f22 -= 0.001f;
            Vec3 vec3 = fluidState.getFlow(blockAndTintGetter, blockPos);
            if (vec3.x == 0.0 && vec3.z == 0.0) {
                textureAtlasSprite = arrtextureAtlasSprite[0];
                f9 = textureAtlasSprite.getU(0.0);
                f29 = textureAtlasSprite.getV(0.0);
                f7 = f9;
                f11 = textureAtlasSprite.getV(16.0);
                f10 = textureAtlasSprite.getU(16.0);
                f6 = f11;
                f2 = f10;
                f = f29;
            } else {
                textureAtlasSprite = arrtextureAtlasSprite[1];
                f27 = (float)Mth.atan2(vec3.z, vec3.x) - 1.5707964f;
                f25 = Mth.sin(f27) * 0.25f;
                f26 = Mth.cos(f27) * 0.25f;
                f28 = 8.0f;
                f9 = textureAtlasSprite.getU(8.0f + (-f26 - f25) * 16.0f);
                f29 = textureAtlasSprite.getV(8.0f + (-f26 + f25) * 16.0f);
                f7 = textureAtlasSprite.getU(8.0f + (-f26 + f25) * 16.0f);
                f11 = textureAtlasSprite.getV(8.0f + (f26 + f25) * 16.0f);
                f10 = textureAtlasSprite.getU(8.0f + (f26 + f25) * 16.0f);
                f6 = textureAtlasSprite.getV(8.0f + (f26 - f25) * 16.0f);
                f2 = textureAtlasSprite.getU(8.0f + (f26 - f25) * 16.0f);
                f = textureAtlasSprite.getV(8.0f + (-f26 - f25) * 16.0f);
            }
            float f30 = (f9 + f7 + f10 + f2) / 4.0f;
            f27 = (f29 + f11 + f6 + f) / 4.0f;
            f25 = (float)arrtextureAtlasSprite[0].getWidth() / (arrtextureAtlasSprite[0].getU1() - arrtextureAtlasSprite[0].getU0());
            f26 = (float)arrtextureAtlasSprite[0].getHeight() / (arrtextureAtlasSprite[0].getV1() - arrtextureAtlasSprite[0].getV0());
            f28 = 4.0f / Math.max(f26, f25);
            f9 = Mth.lerp(f28, f9, f30);
            f7 = Mth.lerp(f28, f7, f30);
            f10 = Mth.lerp(f28, f10, f30);
            f2 = Mth.lerp(f28, f2, f30);
            f29 = Mth.lerp(f28, f29, f27);
            f11 = Mth.lerp(f28, f11, f27);
            f6 = Mth.lerp(f28, f6, f27);
            f = Mth.lerp(f28, f, f27);
            int n2 = this.getLightColor(blockAndTintGetter, blockPos);
            f5 = f16 * f12;
            f3 = f16 * f13;
            f8 = f16 * f14;
            this.vertex(vertexConsumer, d + 0.0, d2 + (double)f19, d3 + 0.0, f5, f3, f8, f9, f29, n2);
            this.vertex(vertexConsumer, d + 0.0, d2 + (double)f20, d3 + 1.0, f5, f3, f8, f7, f11, n2);
            this.vertex(vertexConsumer, d + 1.0, d2 + (double)f21, d3 + 1.0, f5, f3, f8, f10, f6, n2);
            this.vertex(vertexConsumer, d + 1.0, d2 + (double)f22, d3 + 0.0, f5, f3, f8, f2, f, n2);
            if (fluidState.shouldRenderBackwardUpFace(blockAndTintGetter, blockPos.above())) {
                this.vertex(vertexConsumer, d + 0.0, d2 + (double)f19, d3 + 0.0, f5, f3, f8, f9, f29, n2);
                this.vertex(vertexConsumer, d + 1.0, d2 + (double)f22, d3 + 0.0, f5, f3, f8, f2, f, n2);
                this.vertex(vertexConsumer, d + 1.0, d2 + (double)f21, d3 + 1.0, f5, f3, f8, f10, f6, n2);
                this.vertex(vertexConsumer, d + 0.0, d2 + (double)f20, d3 + 1.0, f5, f3, f8, f7, f11, n2);
            }
        }
        if (bl3) {
            f9 = arrtextureAtlasSprite[0].getU0();
            f7 = arrtextureAtlasSprite[0].getU1();
            f10 = arrtextureAtlasSprite[0].getV0();
            f2 = arrtextureAtlasSprite[0].getV1();
            int n3 = this.getLightColor(blockAndTintGetter, blockPos.below());
            f11 = f15 * f12;
            f6 = f15 * f13;
            f = f15 * f14;
            this.vertex(vertexConsumer, d, d2 + (double)f4, d3 + 1.0, f11, f6, f, f9, f2, n3);
            this.vertex(vertexConsumer, d, d2 + (double)f4, d3, f11, f6, f, f9, f10, n3);
            this.vertex(vertexConsumer, d + 1.0, d2 + (double)f4, d3, f11, f6, f, f7, f10, n3);
            this.vertex(vertexConsumer, d + 1.0, d2 + (double)f4, d3 + 1.0, f11, f6, f, f7, f2, n3);
            bl8 = true;
        }
        for (int i = 0; i < 4; ++i) {
            double d4;
            double d5;
            double d6;
            boolean bl9;
            Block block;
            double d7;
            Direction direction;
            if (i == 0) {
                f7 = f19;
                f10 = f22;
                d6 = d;
                d5 = d + 1.0;
                d7 = d3 + 0.0010000000474974513;
                d4 = d3 + 0.0010000000474974513;
                direction = Direction.NORTH;
                bl9 = bl4;
            } else if (i == 1) {
                f7 = f21;
                f10 = f20;
                d6 = d + 1.0;
                d5 = d;
                d7 = d3 + 1.0 - 0.0010000000474974513;
                d4 = d3 + 1.0 - 0.0010000000474974513;
                direction = Direction.SOUTH;
                bl9 = bl5;
            } else if (i == 2) {
                f7 = f20;
                f10 = f19;
                d6 = d + 0.0010000000474974513;
                d5 = d + 0.0010000000474974513;
                d7 = d3 + 1.0;
                d4 = d3;
                direction = Direction.WEST;
                bl9 = bl6;
            } else {
                f7 = f22;
                f10 = f21;
                d6 = d + 1.0 - 0.0010000000474974513;
                d5 = d + 1.0 - 0.0010000000474974513;
                d7 = d3;
                d4 = d3 + 1.0;
                direction = Direction.EAST;
                bl9 = bl7;
            }
            if (!bl9 || LiquidBlockRenderer.isFaceOccludedByNeighbor(blockAndTintGetter, blockPos, direction, Math.max(f7, f10))) continue;
            bl8 = true;
            BlockPos blockPos2 = blockPos.relative(direction);
            TextureAtlasSprite textureAtlasSprite = arrtextureAtlasSprite[1];
            if (!bl && ((block = blockAndTintGetter.getBlockState(blockPos2).getBlock()) instanceof HalfTransparentBlock || block instanceof LeavesBlock)) {
                textureAtlasSprite = this.waterOverlay;
            }
            f5 = textureAtlasSprite.getU(0.0);
            f3 = textureAtlasSprite.getU(8.0);
            f8 = textureAtlasSprite.getV((1.0f - f7) * 16.0f * 0.5f);
            float f31 = textureAtlasSprite.getV((1.0f - f10) * 16.0f * 0.5f);
            float f32 = textureAtlasSprite.getV(8.0);
            int n4 = this.getLightColor(blockAndTintGetter, blockPos2);
            float f33 = i < 2 ? f17 : f18;
            float f34 = f16 * f33 * f12;
            float f35 = f16 * f33 * f13;
            float f36 = f16 * f33 * f14;
            this.vertex(vertexConsumer, d6, d2 + (double)f7, d7, f34, f35, f36, f5, f8, n4);
            this.vertex(vertexConsumer, d5, d2 + (double)f10, d4, f34, f35, f36, f3, f31, n4);
            this.vertex(vertexConsumer, d5, d2 + (double)f4, d4, f34, f35, f36, f3, f32, n4);
            this.vertex(vertexConsumer, d6, d2 + (double)f4, d7, f34, f35, f36, f5, f32, n4);
            if (textureAtlasSprite == this.waterOverlay) continue;
            this.vertex(vertexConsumer, d6, d2 + (double)f4, d7, f34, f35, f36, f5, f32, n4);
            this.vertex(vertexConsumer, d5, d2 + (double)f4, d4, f34, f35, f36, f3, f32, n4);
            this.vertex(vertexConsumer, d5, d2 + (double)f10, d4, f34, f35, f36, f3, f31, n4);
            this.vertex(vertexConsumer, d6, d2 + (double)f7, d7, f34, f35, f36, f5, f8, n4);
        }
        return bl8;
    }

    private void vertex(VertexConsumer vertexConsumer, double d, double d2, double d3, float f, float f2, float f3, float f4, float f5, int n) {
        vertexConsumer.vertex(d, d2, d3).color(f, f2, f3, 1.0f).uv(f4, f5).uv2(n).normal(0.0f, 1.0f, 0.0f).endVertex();
    }

    private int getLightColor(BlockAndTintGetter blockAndTintGetter, BlockPos blockPos) {
        int n = LevelRenderer.getLightColor(blockAndTintGetter, blockPos);
        int n2 = LevelRenderer.getLightColor(blockAndTintGetter, blockPos.above());
        int n3 = n & 0xFF;
        int n4 = n2 & 0xFF;
        int n5 = n >> 16 & 0xFF;
        int n6 = n2 >> 16 & 0xFF;
        return (n3 > n4 ? n3 : n4) | (n5 > n6 ? n5 : n6) << 16;
    }

    private float getWaterHeight(BlockGetter blockGetter, BlockPos blockPos, Fluid fluid) {
        int n = 0;
        float f = 0.0f;
        for (int i = 0; i < 4; ++i) {
            BlockPos blockPos2 = blockPos.offset(-(i & 1), 0, -(i >> 1 & 1));
            if (blockGetter.getFluidState(blockPos2.above()).getType().isSame(fluid)) {
                return 1.0f;
            }
            FluidState fluidState = blockGetter.getFluidState(blockPos2);
            if (fluidState.getType().isSame(fluid)) {
                float f2 = fluidState.getHeight(blockGetter, blockPos2);
                if (f2 >= 0.8f) {
                    f += f2 * 10.0f;
                    n += 10;
                    continue;
                }
                f += f2;
                ++n;
                continue;
            }
            if (blockGetter.getBlockState(blockPos2).getMaterial().isSolid()) continue;
            ++n;
        }
        return f / (float)n;
    }
}

