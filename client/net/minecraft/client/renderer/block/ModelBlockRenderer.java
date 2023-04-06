/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ModelBlockRenderer {
    private final BlockColors blockColors;
    private static final ThreadLocal<Cache> CACHE = ThreadLocal.withInitial(() -> new Cache());

    public ModelBlockRenderer(BlockColors blockColors) {
        this.blockColors = blockColors;
    }

    public boolean tesselateBlock(BlockAndTintGetter blockAndTintGetter, BakedModel bakedModel, BlockState blockState, BlockPos blockPos, PoseStack poseStack, VertexConsumer vertexConsumer, boolean bl, Random random, long l, int n) {
        boolean bl2 = Minecraft.useAmbientOcclusion() && blockState.getLightEmission() == 0 && bakedModel.useAmbientOcclusion();
        Vec3 vec3 = blockState.getOffset(blockAndTintGetter, blockPos);
        poseStack.translate(vec3.x, vec3.y, vec3.z);
        try {
            if (bl2) {
                return this.tesselateWithAO(blockAndTintGetter, bakedModel, blockState, blockPos, poseStack, vertexConsumer, bl, random, l, n);
            }
            return this.tesselateWithoutAO(blockAndTintGetter, bakedModel, blockState, blockPos, poseStack, vertexConsumer, bl, random, l, n);
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.forThrowable(throwable, "Tesselating block model");
            CrashReportCategory crashReportCategory = crashReport.addCategory("Block model being tesselated");
            CrashReportCategory.populateBlockDetails(crashReportCategory, blockPos, blockState);
            crashReportCategory.setDetail("Using AO", bl2);
            throw new ReportedException(crashReport);
        }
    }

    public boolean tesselateWithAO(BlockAndTintGetter blockAndTintGetter, BakedModel bakedModel, BlockState blockState, BlockPos blockPos, PoseStack poseStack, VertexConsumer vertexConsumer, boolean bl, Random random, long l, int n) {
        boolean bl2 = false;
        float[] arrf = new float[Direction.values().length * 2];
        BitSet bitSet = new BitSet(3);
        AmbientOcclusionFace ambientOcclusionFace = new AmbientOcclusionFace();
        for (Direction direction : Direction.values()) {
            random.setSeed(l);
            List<BakedQuad> list = bakedModel.getQuads(blockState, direction, random);
            if (list.isEmpty() || bl && !Block.shouldRenderFace(blockState, blockAndTintGetter, blockPos, direction)) continue;
            this.renderModelFaceAO(blockAndTintGetter, blockState, blockPos, poseStack, vertexConsumer, list, arrf, bitSet, ambientOcclusionFace, n);
            bl2 = true;
        }
        random.setSeed(l);
        List<BakedQuad> list = bakedModel.getQuads(blockState, null, random);
        if (!list.isEmpty()) {
            this.renderModelFaceAO(blockAndTintGetter, blockState, blockPos, poseStack, vertexConsumer, list, arrf, bitSet, ambientOcclusionFace, n);
            bl2 = true;
        }
        return bl2;
    }

    public boolean tesselateWithoutAO(BlockAndTintGetter blockAndTintGetter, BakedModel bakedModel, BlockState blockState, BlockPos blockPos, PoseStack poseStack, VertexConsumer vertexConsumer, boolean bl, Random random, long l, int n) {
        boolean bl2 = false;
        BitSet bitSet = new BitSet(3);
        for (Direction direction : Direction.values()) {
            random.setSeed(l);
            List<BakedQuad> list = bakedModel.getQuads(blockState, direction, random);
            if (list.isEmpty() || bl && !Block.shouldRenderFace(blockState, blockAndTintGetter, blockPos, direction)) continue;
            int n2 = LevelRenderer.getLightColor(blockAndTintGetter, blockState, blockPos.relative(direction));
            this.renderModelFaceFlat(blockAndTintGetter, blockState, blockPos, n2, n, false, poseStack, vertexConsumer, list, bitSet);
            bl2 = true;
        }
        random.setSeed(l);
        List<BakedQuad> list = bakedModel.getQuads(blockState, null, random);
        if (!list.isEmpty()) {
            this.renderModelFaceFlat(blockAndTintGetter, blockState, blockPos, -1, n, true, poseStack, vertexConsumer, list, bitSet);
            bl2 = true;
        }
        return bl2;
    }

    private void renderModelFaceAO(BlockAndTintGetter blockAndTintGetter, BlockState blockState, BlockPos blockPos, PoseStack poseStack, VertexConsumer vertexConsumer, List<BakedQuad> list, float[] arrf, BitSet bitSet, AmbientOcclusionFace ambientOcclusionFace, int n) {
        for (BakedQuad bakedQuad : list) {
            this.calculateShape(blockAndTintGetter, blockState, blockPos, bakedQuad.getVertices(), bakedQuad.getDirection(), arrf, bitSet);
            ambientOcclusionFace.calculate(blockAndTintGetter, blockState, blockPos, bakedQuad.getDirection(), arrf, bitSet, bakedQuad.isShade());
            this.putQuadData(blockAndTintGetter, blockState, blockPos, vertexConsumer, poseStack.last(), bakedQuad, ambientOcclusionFace.brightness[0], ambientOcclusionFace.brightness[1], ambientOcclusionFace.brightness[2], ambientOcclusionFace.brightness[3], ambientOcclusionFace.lightmap[0], ambientOcclusionFace.lightmap[1], ambientOcclusionFace.lightmap[2], ambientOcclusionFace.lightmap[3], n);
        }
    }

    private void putQuadData(BlockAndTintGetter blockAndTintGetter, BlockState blockState, BlockPos blockPos, VertexConsumer vertexConsumer, PoseStack.Pose pose, BakedQuad bakedQuad, float f, float f2, float f3, float f4, int n, int n2, int n3, int n4, int n5) {
        float f5;
        float f6;
        float f7;
        if (bakedQuad.isTinted()) {
            int n6 = this.blockColors.getColor(blockState, blockAndTintGetter, blockPos, bakedQuad.getTintIndex());
            f7 = (float)(n6 >> 16 & 0xFF) / 255.0f;
            f5 = (float)(n6 >> 8 & 0xFF) / 255.0f;
            f6 = (float)(n6 & 0xFF) / 255.0f;
        } else {
            f7 = 1.0f;
            f5 = 1.0f;
            f6 = 1.0f;
        }
        vertexConsumer.putBulkData(pose, bakedQuad, new float[]{f, f2, f3, f4}, f7, f5, f6, new int[]{n, n2, n3, n4}, n5, true);
    }

    private void calculateShape(BlockAndTintGetter blockAndTintGetter, BlockState blockState, BlockPos blockPos, int[] arrn, Direction direction, @Nullable float[] arrf, BitSet bitSet) {
        float f;
        int n;
        float f2 = 32.0f;
        float f3 = 32.0f;
        float f4 = 32.0f;
        float f5 = -32.0f;
        float f6 = -32.0f;
        float f7 = -32.0f;
        for (n = 0; n < 4; ++n) {
            f = Float.intBitsToFloat(arrn[n * 8]);
            float f8 = Float.intBitsToFloat(arrn[n * 8 + 1]);
            float f9 = Float.intBitsToFloat(arrn[n * 8 + 2]);
            f2 = Math.min(f2, f);
            f3 = Math.min(f3, f8);
            f4 = Math.min(f4, f9);
            f5 = Math.max(f5, f);
            f6 = Math.max(f6, f8);
            f7 = Math.max(f7, f9);
        }
        if (arrf != null) {
            arrf[Direction.WEST.get3DDataValue()] = f2;
            arrf[Direction.EAST.get3DDataValue()] = f5;
            arrf[Direction.DOWN.get3DDataValue()] = f3;
            arrf[Direction.UP.get3DDataValue()] = f6;
            arrf[Direction.NORTH.get3DDataValue()] = f4;
            arrf[Direction.SOUTH.get3DDataValue()] = f7;
            n = Direction.values().length;
            arrf[Direction.WEST.get3DDataValue() + n] = 1.0f - f2;
            arrf[Direction.EAST.get3DDataValue() + n] = 1.0f - f5;
            arrf[Direction.DOWN.get3DDataValue() + n] = 1.0f - f3;
            arrf[Direction.UP.get3DDataValue() + n] = 1.0f - f6;
            arrf[Direction.NORTH.get3DDataValue() + n] = 1.0f - f4;
            arrf[Direction.SOUTH.get3DDataValue() + n] = 1.0f - f7;
        }
        float f10 = 1.0E-4f;
        f = 0.9999f;
        switch (direction) {
            case DOWN: {
                bitSet.set(1, f2 >= 1.0E-4f || f4 >= 1.0E-4f || f5 <= 0.9999f || f7 <= 0.9999f);
                bitSet.set(0, f3 == f6 && (f3 < 1.0E-4f || blockState.isCollisionShapeFullBlock(blockAndTintGetter, blockPos)));
                break;
            }
            case UP: {
                bitSet.set(1, f2 >= 1.0E-4f || f4 >= 1.0E-4f || f5 <= 0.9999f || f7 <= 0.9999f);
                bitSet.set(0, f3 == f6 && (f6 > 0.9999f || blockState.isCollisionShapeFullBlock(blockAndTintGetter, blockPos)));
                break;
            }
            case NORTH: {
                bitSet.set(1, f2 >= 1.0E-4f || f3 >= 1.0E-4f || f5 <= 0.9999f || f6 <= 0.9999f);
                bitSet.set(0, f4 == f7 && (f4 < 1.0E-4f || blockState.isCollisionShapeFullBlock(blockAndTintGetter, blockPos)));
                break;
            }
            case SOUTH: {
                bitSet.set(1, f2 >= 1.0E-4f || f3 >= 1.0E-4f || f5 <= 0.9999f || f6 <= 0.9999f);
                bitSet.set(0, f4 == f7 && (f7 > 0.9999f || blockState.isCollisionShapeFullBlock(blockAndTintGetter, blockPos)));
                break;
            }
            case WEST: {
                bitSet.set(1, f3 >= 1.0E-4f || f4 >= 1.0E-4f || f6 <= 0.9999f || f7 <= 0.9999f);
                bitSet.set(0, f2 == f5 && (f2 < 1.0E-4f || blockState.isCollisionShapeFullBlock(blockAndTintGetter, blockPos)));
                break;
            }
            case EAST: {
                bitSet.set(1, f3 >= 1.0E-4f || f4 >= 1.0E-4f || f6 <= 0.9999f || f7 <= 0.9999f);
                bitSet.set(0, f2 == f5 && (f5 > 0.9999f || blockState.isCollisionShapeFullBlock(blockAndTintGetter, blockPos)));
            }
        }
    }

    private void renderModelFaceFlat(BlockAndTintGetter blockAndTintGetter, BlockState blockState, BlockPos blockPos, int n, int n2, boolean bl, PoseStack poseStack, VertexConsumer vertexConsumer, List<BakedQuad> list, BitSet bitSet) {
        for (BakedQuad bakedQuad : list) {
            if (bl) {
                this.calculateShape(blockAndTintGetter, blockState, blockPos, bakedQuad.getVertices(), bakedQuad.getDirection(), null, bitSet);
                BlockPos blockPos2 = bitSet.get(0) ? blockPos.relative(bakedQuad.getDirection()) : blockPos;
                n = LevelRenderer.getLightColor(blockAndTintGetter, blockState, blockPos2);
            }
            float f = blockAndTintGetter.getShade(bakedQuad.getDirection(), bakedQuad.isShade());
            this.putQuadData(blockAndTintGetter, blockState, blockPos, vertexConsumer, poseStack.last(), bakedQuad, f, f, f, f, n, n, n, n, n2);
        }
    }

    public void renderModel(PoseStack.Pose pose, VertexConsumer vertexConsumer, @Nullable BlockState blockState, BakedModel bakedModel, float f, float f2, float f3, int n, int n2) {
        Random random = new Random();
        long l = 42L;
        for (Direction direction : Direction.values()) {
            random.setSeed(42L);
            ModelBlockRenderer.renderQuadList(pose, vertexConsumer, f, f2, f3, bakedModel.getQuads(blockState, direction, random), n, n2);
        }
        random.setSeed(42L);
        ModelBlockRenderer.renderQuadList(pose, vertexConsumer, f, f2, f3, bakedModel.getQuads(blockState, null, random), n, n2);
    }

    private static void renderQuadList(PoseStack.Pose pose, VertexConsumer vertexConsumer, float f, float f2, float f3, List<BakedQuad> list, int n, int n2) {
        for (BakedQuad bakedQuad : list) {
            float f4;
            float f5;
            float f6;
            if (bakedQuad.isTinted()) {
                f5 = Mth.clamp(f, 0.0f, 1.0f);
                f6 = Mth.clamp(f2, 0.0f, 1.0f);
                f4 = Mth.clamp(f3, 0.0f, 1.0f);
            } else {
                f5 = 1.0f;
                f6 = 1.0f;
                f4 = 1.0f;
            }
            vertexConsumer.putBulkData(pose, bakedQuad, f5, f6, f4, n, n2);
        }
    }

    public static void enableCaching() {
        CACHE.get().enable();
    }

    public static void clearCache() {
        CACHE.get().disable();
    }

    public static enum AdjacencyInfo {
        DOWN(new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH}, 0.5f, true, new SizeInfo[]{SizeInfo.FLIP_WEST, SizeInfo.SOUTH, SizeInfo.FLIP_WEST, SizeInfo.FLIP_SOUTH, SizeInfo.WEST, SizeInfo.FLIP_SOUTH, SizeInfo.WEST, SizeInfo.SOUTH}, new SizeInfo[]{SizeInfo.FLIP_WEST, SizeInfo.NORTH, SizeInfo.FLIP_WEST, SizeInfo.FLIP_NORTH, SizeInfo.WEST, SizeInfo.FLIP_NORTH, SizeInfo.WEST, SizeInfo.NORTH}, new SizeInfo[]{SizeInfo.FLIP_EAST, SizeInfo.NORTH, SizeInfo.FLIP_EAST, SizeInfo.FLIP_NORTH, SizeInfo.EAST, SizeInfo.FLIP_NORTH, SizeInfo.EAST, SizeInfo.NORTH}, new SizeInfo[]{SizeInfo.FLIP_EAST, SizeInfo.SOUTH, SizeInfo.FLIP_EAST, SizeInfo.FLIP_SOUTH, SizeInfo.EAST, SizeInfo.FLIP_SOUTH, SizeInfo.EAST, SizeInfo.SOUTH}),
        UP(new Direction[]{Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH}, 1.0f, true, new SizeInfo[]{SizeInfo.EAST, SizeInfo.SOUTH, SizeInfo.EAST, SizeInfo.FLIP_SOUTH, SizeInfo.FLIP_EAST, SizeInfo.FLIP_SOUTH, SizeInfo.FLIP_EAST, SizeInfo.SOUTH}, new SizeInfo[]{SizeInfo.EAST, SizeInfo.NORTH, SizeInfo.EAST, SizeInfo.FLIP_NORTH, SizeInfo.FLIP_EAST, SizeInfo.FLIP_NORTH, SizeInfo.FLIP_EAST, SizeInfo.NORTH}, new SizeInfo[]{SizeInfo.WEST, SizeInfo.NORTH, SizeInfo.WEST, SizeInfo.FLIP_NORTH, SizeInfo.FLIP_WEST, SizeInfo.FLIP_NORTH, SizeInfo.FLIP_WEST, SizeInfo.NORTH}, new SizeInfo[]{SizeInfo.WEST, SizeInfo.SOUTH, SizeInfo.WEST, SizeInfo.FLIP_SOUTH, SizeInfo.FLIP_WEST, SizeInfo.FLIP_SOUTH, SizeInfo.FLIP_WEST, SizeInfo.SOUTH}),
        NORTH(new Direction[]{Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST}, 0.8f, true, new SizeInfo[]{SizeInfo.UP, SizeInfo.FLIP_WEST, SizeInfo.UP, SizeInfo.WEST, SizeInfo.FLIP_UP, SizeInfo.WEST, SizeInfo.FLIP_UP, SizeInfo.FLIP_WEST}, new SizeInfo[]{SizeInfo.UP, SizeInfo.FLIP_EAST, SizeInfo.UP, SizeInfo.EAST, SizeInfo.FLIP_UP, SizeInfo.EAST, SizeInfo.FLIP_UP, SizeInfo.FLIP_EAST}, new SizeInfo[]{SizeInfo.DOWN, SizeInfo.FLIP_EAST, SizeInfo.DOWN, SizeInfo.EAST, SizeInfo.FLIP_DOWN, SizeInfo.EAST, SizeInfo.FLIP_DOWN, SizeInfo.FLIP_EAST}, new SizeInfo[]{SizeInfo.DOWN, SizeInfo.FLIP_WEST, SizeInfo.DOWN, SizeInfo.WEST, SizeInfo.FLIP_DOWN, SizeInfo.WEST, SizeInfo.FLIP_DOWN, SizeInfo.FLIP_WEST}),
        SOUTH(new Direction[]{Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP}, 0.8f, true, new SizeInfo[]{SizeInfo.UP, SizeInfo.FLIP_WEST, SizeInfo.FLIP_UP, SizeInfo.FLIP_WEST, SizeInfo.FLIP_UP, SizeInfo.WEST, SizeInfo.UP, SizeInfo.WEST}, new SizeInfo[]{SizeInfo.DOWN, SizeInfo.FLIP_WEST, SizeInfo.FLIP_DOWN, SizeInfo.FLIP_WEST, SizeInfo.FLIP_DOWN, SizeInfo.WEST, SizeInfo.DOWN, SizeInfo.WEST}, new SizeInfo[]{SizeInfo.DOWN, SizeInfo.FLIP_EAST, SizeInfo.FLIP_DOWN, SizeInfo.FLIP_EAST, SizeInfo.FLIP_DOWN, SizeInfo.EAST, SizeInfo.DOWN, SizeInfo.EAST}, new SizeInfo[]{SizeInfo.UP, SizeInfo.FLIP_EAST, SizeInfo.FLIP_UP, SizeInfo.FLIP_EAST, SizeInfo.FLIP_UP, SizeInfo.EAST, SizeInfo.UP, SizeInfo.EAST}),
        WEST(new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH}, 0.6f, true, new SizeInfo[]{SizeInfo.UP, SizeInfo.SOUTH, SizeInfo.UP, SizeInfo.FLIP_SOUTH, SizeInfo.FLIP_UP, SizeInfo.FLIP_SOUTH, SizeInfo.FLIP_UP, SizeInfo.SOUTH}, new SizeInfo[]{SizeInfo.UP, SizeInfo.NORTH, SizeInfo.UP, SizeInfo.FLIP_NORTH, SizeInfo.FLIP_UP, SizeInfo.FLIP_NORTH, SizeInfo.FLIP_UP, SizeInfo.NORTH}, new SizeInfo[]{SizeInfo.DOWN, SizeInfo.NORTH, SizeInfo.DOWN, SizeInfo.FLIP_NORTH, SizeInfo.FLIP_DOWN, SizeInfo.FLIP_NORTH, SizeInfo.FLIP_DOWN, SizeInfo.NORTH}, new SizeInfo[]{SizeInfo.DOWN, SizeInfo.SOUTH, SizeInfo.DOWN, SizeInfo.FLIP_SOUTH, SizeInfo.FLIP_DOWN, SizeInfo.FLIP_SOUTH, SizeInfo.FLIP_DOWN, SizeInfo.SOUTH}),
        EAST(new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH}, 0.6f, true, new SizeInfo[]{SizeInfo.FLIP_DOWN, SizeInfo.SOUTH, SizeInfo.FLIP_DOWN, SizeInfo.FLIP_SOUTH, SizeInfo.DOWN, SizeInfo.FLIP_SOUTH, SizeInfo.DOWN, SizeInfo.SOUTH}, new SizeInfo[]{SizeInfo.FLIP_DOWN, SizeInfo.NORTH, SizeInfo.FLIP_DOWN, SizeInfo.FLIP_NORTH, SizeInfo.DOWN, SizeInfo.FLIP_NORTH, SizeInfo.DOWN, SizeInfo.NORTH}, new SizeInfo[]{SizeInfo.FLIP_UP, SizeInfo.NORTH, SizeInfo.FLIP_UP, SizeInfo.FLIP_NORTH, SizeInfo.UP, SizeInfo.FLIP_NORTH, SizeInfo.UP, SizeInfo.NORTH}, new SizeInfo[]{SizeInfo.FLIP_UP, SizeInfo.SOUTH, SizeInfo.FLIP_UP, SizeInfo.FLIP_SOUTH, SizeInfo.UP, SizeInfo.FLIP_SOUTH, SizeInfo.UP, SizeInfo.SOUTH});
        
        private final Direction[] corners;
        private final boolean doNonCubicWeight;
        private final SizeInfo[] vert0Weights;
        private final SizeInfo[] vert1Weights;
        private final SizeInfo[] vert2Weights;
        private final SizeInfo[] vert3Weights;
        private static final AdjacencyInfo[] BY_FACING;

        private AdjacencyInfo(Direction[] arrdirection, float f, boolean bl, SizeInfo[] arrsizeInfo, SizeInfo[] arrsizeInfo2, SizeInfo[] arrsizeInfo3, SizeInfo[] arrsizeInfo4) {
            this.corners = arrdirection;
            this.doNonCubicWeight = bl;
            this.vert0Weights = arrsizeInfo;
            this.vert1Weights = arrsizeInfo2;
            this.vert2Weights = arrsizeInfo3;
            this.vert3Weights = arrsizeInfo4;
        }

        public static AdjacencyInfo fromFacing(Direction direction) {
            return BY_FACING[direction.get3DDataValue()];
        }

        static {
            BY_FACING = Util.make(new AdjacencyInfo[6], arradjacencyInfo -> {
                arradjacencyInfo[Direction.DOWN.get3DDataValue()] = DOWN;
                arradjacencyInfo[Direction.UP.get3DDataValue()] = UP;
                arradjacencyInfo[Direction.NORTH.get3DDataValue()] = NORTH;
                arradjacencyInfo[Direction.SOUTH.get3DDataValue()] = SOUTH;
                arradjacencyInfo[Direction.WEST.get3DDataValue()] = WEST;
                arradjacencyInfo[Direction.EAST.get3DDataValue()] = EAST;
            });
        }
    }

    public static enum SizeInfo {
        DOWN(Direction.DOWN, false),
        UP(Direction.UP, false),
        NORTH(Direction.NORTH, false),
        SOUTH(Direction.SOUTH, false),
        WEST(Direction.WEST, false),
        EAST(Direction.EAST, false),
        FLIP_DOWN(Direction.DOWN, true),
        FLIP_UP(Direction.UP, true),
        FLIP_NORTH(Direction.NORTH, true),
        FLIP_SOUTH(Direction.SOUTH, true),
        FLIP_WEST(Direction.WEST, true),
        FLIP_EAST(Direction.EAST, true);
        
        private final int shape;

        private SizeInfo(Direction direction, boolean bl) {
            this.shape = direction.get3DDataValue() + (bl ? Direction.values().length : 0);
        }
    }

    class AmbientOcclusionFace {
        private final float[] brightness = new float[4];
        private final int[] lightmap = new int[4];

        public void calculate(BlockAndTintGetter blockAndTintGetter, BlockState blockState, BlockPos blockPos, Direction direction, float[] arrf, BitSet bitSet, boolean bl) {
            float f;
            int n;
            int n2;
            float f2;
            int n3;
            BlockState blockState2;
            float f3;
            int n4;
            float f4;
            float f5;
            boolean bl2;
            float f6;
            float f7;
            float f8;
            BlockPos blockPos2 = bitSet.get(0) ? blockPos.relative(direction) : blockPos;
            AdjacencyInfo adjacencyInfo = AdjacencyInfo.fromFacing(direction);
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
            Cache cache = (Cache)CACHE.get();
            mutableBlockPos.setWithOffset(blockPos2, adjacencyInfo.corners[0]);
            BlockState blockState3 = blockAndTintGetter.getBlockState(mutableBlockPos);
            int n5 = cache.getLightColor(blockState3, blockAndTintGetter, mutableBlockPos);
            float f9 = cache.getShadeBrightness(blockState3, blockAndTintGetter, mutableBlockPos);
            mutableBlockPos.setWithOffset(blockPos2, adjacencyInfo.corners[1]);
            BlockState blockState4 = blockAndTintGetter.getBlockState(mutableBlockPos);
            int n6 = cache.getLightColor(blockState4, blockAndTintGetter, mutableBlockPos);
            float f10 = cache.getShadeBrightness(blockState4, blockAndTintGetter, mutableBlockPos);
            mutableBlockPos.setWithOffset(blockPos2, adjacencyInfo.corners[2]);
            BlockState blockState5 = blockAndTintGetter.getBlockState(mutableBlockPos);
            int n7 = cache.getLightColor(blockState5, blockAndTintGetter, mutableBlockPos);
            float f11 = cache.getShadeBrightness(blockState5, blockAndTintGetter, mutableBlockPos);
            mutableBlockPos.setWithOffset(blockPos2, adjacencyInfo.corners[3]);
            BlockState blockState6 = blockAndTintGetter.getBlockState(mutableBlockPos);
            int n8 = cache.getLightColor(blockState6, blockAndTintGetter, mutableBlockPos);
            float f12 = cache.getShadeBrightness(blockState6, blockAndTintGetter, mutableBlockPos);
            mutableBlockPos.setWithOffset(blockPos2, adjacencyInfo.corners[0]).move(direction);
            boolean bl3 = blockAndTintGetter.getBlockState(mutableBlockPos).getLightBlock(blockAndTintGetter, mutableBlockPos) == 0;
            mutableBlockPos.setWithOffset(blockPos2, adjacencyInfo.corners[1]).move(direction);
            boolean bl4 = blockAndTintGetter.getBlockState(mutableBlockPos).getLightBlock(blockAndTintGetter, mutableBlockPos) == 0;
            mutableBlockPos.setWithOffset(blockPos2, adjacencyInfo.corners[2]).move(direction);
            boolean bl5 = blockAndTintGetter.getBlockState(mutableBlockPos).getLightBlock(blockAndTintGetter, mutableBlockPos) == 0;
            mutableBlockPos.setWithOffset(blockPos2, adjacencyInfo.corners[3]).move(direction);
            boolean bl6 = bl2 = blockAndTintGetter.getBlockState(mutableBlockPos).getLightBlock(blockAndTintGetter, mutableBlockPos) == 0;
            if (bl5 || bl3) {
                mutableBlockPos.setWithOffset(blockPos2, adjacencyInfo.corners[0]).move(adjacencyInfo.corners[2]);
                blockState2 = blockAndTintGetter.getBlockState(mutableBlockPos);
                f2 = cache.getShadeBrightness(blockState2, blockAndTintGetter, mutableBlockPos);
                n2 = cache.getLightColor(blockState2, blockAndTintGetter, mutableBlockPos);
            } else {
                f2 = f9;
                n2 = n5;
            }
            if (bl2 || bl3) {
                mutableBlockPos.setWithOffset(blockPos2, adjacencyInfo.corners[0]).move(adjacencyInfo.corners[3]);
                blockState2 = blockAndTintGetter.getBlockState(mutableBlockPos);
                f7 = cache.getShadeBrightness(blockState2, blockAndTintGetter, mutableBlockPos);
                n3 = cache.getLightColor(blockState2, blockAndTintGetter, mutableBlockPos);
            } else {
                f7 = f9;
                n3 = n5;
            }
            if (bl5 || bl4) {
                mutableBlockPos.setWithOffset(blockPos2, adjacencyInfo.corners[1]).move(adjacencyInfo.corners[2]);
                blockState2 = blockAndTintGetter.getBlockState(mutableBlockPos);
                f3 = cache.getShadeBrightness(blockState2, blockAndTintGetter, mutableBlockPos);
                n4 = cache.getLightColor(blockState2, blockAndTintGetter, mutableBlockPos);
            } else {
                f3 = f9;
                n4 = n5;
            }
            if (bl2 || bl4) {
                mutableBlockPos.setWithOffset(blockPos2, adjacencyInfo.corners[1]).move(adjacencyInfo.corners[3]);
                blockState2 = blockAndTintGetter.getBlockState(mutableBlockPos);
                f5 = cache.getShadeBrightness(blockState2, blockAndTintGetter, mutableBlockPos);
                n = cache.getLightColor(blockState2, blockAndTintGetter, mutableBlockPos);
            } else {
                f5 = f9;
                n = n5;
            }
            int n9 = cache.getLightColor(blockState, blockAndTintGetter, blockPos);
            mutableBlockPos.setWithOffset(blockPos, direction);
            BlockState blockState7 = blockAndTintGetter.getBlockState(mutableBlockPos);
            if (bitSet.get(0) || !blockState7.isSolidRender(blockAndTintGetter, mutableBlockPos)) {
                n9 = cache.getLightColor(blockState7, blockAndTintGetter, mutableBlockPos);
            }
            float f13 = bitSet.get(0) ? cache.getShadeBrightness(blockAndTintGetter.getBlockState(blockPos2), blockAndTintGetter, blockPos2) : cache.getShadeBrightness(blockAndTintGetter.getBlockState(blockPos), blockAndTintGetter, blockPos);
            AmbientVertexRemap ambientVertexRemap = AmbientVertexRemap.fromFacing(direction);
            if (!bitSet.get(1) || !adjacencyInfo.doNonCubicWeight) {
                f8 = (f12 + f9 + f7 + f13) * 0.25f;
                f4 = (f11 + f9 + f2 + f13) * 0.25f;
                f = (f11 + f10 + f3 + f13) * 0.25f;
                f6 = (f12 + f10 + f5 + f13) * 0.25f;
                this.lightmap[AmbientVertexRemap.access$500((AmbientVertexRemap)ambientVertexRemap)] = this.blend(n8, n5, n3, n9);
                this.lightmap[AmbientVertexRemap.access$600((AmbientVertexRemap)ambientVertexRemap)] = this.blend(n7, n5, n2, n9);
                this.lightmap[AmbientVertexRemap.access$700((AmbientVertexRemap)ambientVertexRemap)] = this.blend(n7, n6, n4, n9);
                this.lightmap[AmbientVertexRemap.access$800((AmbientVertexRemap)ambientVertexRemap)] = this.blend(n8, n6, n, n9);
                this.brightness[AmbientVertexRemap.access$500((AmbientVertexRemap)ambientVertexRemap)] = f8;
                this.brightness[AmbientVertexRemap.access$600((AmbientVertexRemap)ambientVertexRemap)] = f4;
                this.brightness[AmbientVertexRemap.access$700((AmbientVertexRemap)ambientVertexRemap)] = f;
                this.brightness[AmbientVertexRemap.access$800((AmbientVertexRemap)ambientVertexRemap)] = f6;
            } else {
                f8 = (f12 + f9 + f7 + f13) * 0.25f;
                f4 = (f11 + f9 + f2 + f13) * 0.25f;
                f = (f11 + f10 + f3 + f13) * 0.25f;
                f6 = (f12 + f10 + f5 + f13) * 0.25f;
                float f14 = arrf[adjacencyInfo.vert0Weights[0].shape] * arrf[adjacencyInfo.vert0Weights[1].shape];
                float f15 = arrf[adjacencyInfo.vert0Weights[2].shape] * arrf[adjacencyInfo.vert0Weights[3].shape];
                float f16 = arrf[adjacencyInfo.vert0Weights[4].shape] * arrf[adjacencyInfo.vert0Weights[5].shape];
                float f17 = arrf[adjacencyInfo.vert0Weights[6].shape] * arrf[adjacencyInfo.vert0Weights[7].shape];
                float f18 = arrf[adjacencyInfo.vert1Weights[0].shape] * arrf[adjacencyInfo.vert1Weights[1].shape];
                float f19 = arrf[adjacencyInfo.vert1Weights[2].shape] * arrf[adjacencyInfo.vert1Weights[3].shape];
                float f20 = arrf[adjacencyInfo.vert1Weights[4].shape] * arrf[adjacencyInfo.vert1Weights[5].shape];
                float f21 = arrf[adjacencyInfo.vert1Weights[6].shape] * arrf[adjacencyInfo.vert1Weights[7].shape];
                float f22 = arrf[adjacencyInfo.vert2Weights[0].shape] * arrf[adjacencyInfo.vert2Weights[1].shape];
                float f23 = arrf[adjacencyInfo.vert2Weights[2].shape] * arrf[adjacencyInfo.vert2Weights[3].shape];
                float f24 = arrf[adjacencyInfo.vert2Weights[4].shape] * arrf[adjacencyInfo.vert2Weights[5].shape];
                float f25 = arrf[adjacencyInfo.vert2Weights[6].shape] * arrf[adjacencyInfo.vert2Weights[7].shape];
                float f26 = arrf[adjacencyInfo.vert3Weights[0].shape] * arrf[adjacencyInfo.vert3Weights[1].shape];
                float f27 = arrf[adjacencyInfo.vert3Weights[2].shape] * arrf[adjacencyInfo.vert3Weights[3].shape];
                float f28 = arrf[adjacencyInfo.vert3Weights[4].shape] * arrf[adjacencyInfo.vert3Weights[5].shape];
                float f29 = arrf[adjacencyInfo.vert3Weights[6].shape] * arrf[adjacencyInfo.vert3Weights[7].shape];
                this.brightness[AmbientVertexRemap.access$500((AmbientVertexRemap)ambientVertexRemap)] = f8 * f14 + f4 * f15 + f * f16 + f6 * f17;
                this.brightness[AmbientVertexRemap.access$600((AmbientVertexRemap)ambientVertexRemap)] = f8 * f18 + f4 * f19 + f * f20 + f6 * f21;
                this.brightness[AmbientVertexRemap.access$700((AmbientVertexRemap)ambientVertexRemap)] = f8 * f22 + f4 * f23 + f * f24 + f6 * f25;
                this.brightness[AmbientVertexRemap.access$800((AmbientVertexRemap)ambientVertexRemap)] = f8 * f26 + f4 * f27 + f * f28 + f6 * f29;
                int n10 = this.blend(n8, n5, n3, n9);
                int n11 = this.blend(n7, n5, n2, n9);
                int n12 = this.blend(n7, n6, n4, n9);
                int n13 = this.blend(n8, n6, n, n9);
                this.lightmap[AmbientVertexRemap.access$500((AmbientVertexRemap)ambientVertexRemap)] = this.blend(n10, n11, n12, n13, f14, f15, f16, f17);
                this.lightmap[AmbientVertexRemap.access$600((AmbientVertexRemap)ambientVertexRemap)] = this.blend(n10, n11, n12, n13, f18, f19, f20, f21);
                this.lightmap[AmbientVertexRemap.access$700((AmbientVertexRemap)ambientVertexRemap)] = this.blend(n10, n11, n12, n13, f22, f23, f24, f25);
                this.lightmap[AmbientVertexRemap.access$800((AmbientVertexRemap)ambientVertexRemap)] = this.blend(n10, n11, n12, n13, f26, f27, f28, f29);
            }
            f8 = blockAndTintGetter.getShade(direction, bl);
            int n14 = 0;
            while (n14 < this.brightness.length) {
                float[] arrf2 = this.brightness;
                int n15 = n14++;
                arrf2[n15] = arrf2[n15] * f8;
            }
        }

        private int blend(int n, int n2, int n3, int n4) {
            if (n == 0) {
                n = n4;
            }
            if (n2 == 0) {
                n2 = n4;
            }
            if (n3 == 0) {
                n3 = n4;
            }
            return n + n2 + n3 + n4 >> 2 & 0xFF00FF;
        }

        private int blend(int n, int n2, int n3, int n4, float f, float f2, float f3, float f4) {
            int n5 = (int)((float)(n >> 16 & 0xFF) * f + (float)(n2 >> 16 & 0xFF) * f2 + (float)(n3 >> 16 & 0xFF) * f3 + (float)(n4 >> 16 & 0xFF) * f4) & 0xFF;
            int n6 = (int)((float)(n & 0xFF) * f + (float)(n2 & 0xFF) * f2 + (float)(n3 & 0xFF) * f3 + (float)(n4 & 0xFF) * f4) & 0xFF;
            return n5 << 16 | n6;
        }
    }

    static class Cache {
        private boolean enabled;
        private final Long2IntLinkedOpenHashMap colorCache = Util.make(() -> {
            Long2IntLinkedOpenHashMap long2IntLinkedOpenHashMap = new Long2IntLinkedOpenHashMap(100, 0.25f){

                protected void rehash(int n) {
                }
            };
            long2IntLinkedOpenHashMap.defaultReturnValue(Integer.MAX_VALUE);
            return long2IntLinkedOpenHashMap;
        });
        private final Long2FloatLinkedOpenHashMap brightnessCache = Util.make(() -> {
            Long2FloatLinkedOpenHashMap long2FloatLinkedOpenHashMap = new Long2FloatLinkedOpenHashMap(100, 0.25f){

                protected void rehash(int n) {
                }
            };
            long2FloatLinkedOpenHashMap.defaultReturnValue(Float.NaN);
            return long2FloatLinkedOpenHashMap;
        });

        private Cache() {
        }

        public void enable() {
            this.enabled = true;
        }

        public void disable() {
            this.enabled = false;
            this.colorCache.clear();
            this.brightnessCache.clear();
        }

        public int getLightColor(BlockState blockState, BlockAndTintGetter blockAndTintGetter, BlockPos blockPos) {
            int n;
            long l = blockPos.asLong();
            if (this.enabled && (n = this.colorCache.get(l)) != Integer.MAX_VALUE) {
                return n;
            }
            n = LevelRenderer.getLightColor(blockAndTintGetter, blockState, blockPos);
            if (this.enabled) {
                if (this.colorCache.size() == 100) {
                    this.colorCache.removeFirstInt();
                }
                this.colorCache.put(l, n);
            }
            return n;
        }

        public float getShadeBrightness(BlockState blockState, BlockAndTintGetter blockAndTintGetter, BlockPos blockPos) {
            float f;
            long l = blockPos.asLong();
            if (this.enabled && !Float.isNaN(f = this.brightnessCache.get(l))) {
                return f;
            }
            f = blockState.getShadeBrightness(blockAndTintGetter, blockPos);
            if (this.enabled) {
                if (this.brightnessCache.size() == 100) {
                    this.brightnessCache.removeFirstFloat();
                }
                this.brightnessCache.put(l, f);
            }
            return f;
        }

    }

    static enum AmbientVertexRemap {
        DOWN(0, 1, 2, 3),
        UP(2, 3, 0, 1),
        NORTH(3, 0, 1, 2),
        SOUTH(0, 1, 2, 3),
        WEST(3, 0, 1, 2),
        EAST(1, 2, 3, 0);
        
        private final int vert0;
        private final int vert1;
        private final int vert2;
        private final int vert3;
        private static final AmbientVertexRemap[] BY_FACING;

        private AmbientVertexRemap(int n2, int n3, int n4, int n5) {
            this.vert0 = n2;
            this.vert1 = n3;
            this.vert2 = n4;
            this.vert3 = n5;
        }

        public static AmbientVertexRemap fromFacing(Direction direction) {
            return BY_FACING[direction.get3DDataValue()];
        }

        static /* synthetic */ int access$500(AmbientVertexRemap ambientVertexRemap) {
            return ambientVertexRemap.vert0;
        }

        static /* synthetic */ int access$600(AmbientVertexRemap ambientVertexRemap) {
            return ambientVertexRemap.vert1;
        }

        static /* synthetic */ int access$700(AmbientVertexRemap ambientVertexRemap) {
            return ambientVertexRemap.vert2;
        }

        static /* synthetic */ int access$800(AmbientVertexRemap ambientVertexRemap) {
            return ambientVertexRemap.vert3;
        }

        static {
            BY_FACING = Util.make(new AmbientVertexRemap[6], arrambientVertexRemap -> {
                arrambientVertexRemap[Direction.DOWN.get3DDataValue()] = DOWN;
                arrambientVertexRemap[Direction.UP.get3DDataValue()] = UP;
                arrambientVertexRemap[Direction.NORTH.get3DDataValue()] = NORTH;
                arrambientVertexRemap[Direction.SOUTH.get3DDataValue()] = SOUTH;
                arrambientVertexRemap[Direction.WEST.get3DDataValue()] = WEST;
                arrambientVertexRemap[Direction.EAST.get3DDataValue()] = EAST;
            });
        }
    }

}

