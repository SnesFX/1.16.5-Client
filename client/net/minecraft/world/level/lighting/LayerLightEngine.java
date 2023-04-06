/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.mutable.MutableInt
 */
package net.minecraft.world.level.lighting;

import java.util.Arrays;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.DataLayerStorageMap;
import net.minecraft.world.level.lighting.DynamicGraphMinFixedPoint;
import net.minecraft.world.level.lighting.LayerLightEventListener;
import net.minecraft.world.level.lighting.LayerLightSectionStorage;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableInt;

public abstract class LayerLightEngine<M extends DataLayerStorageMap<M>, S extends LayerLightSectionStorage<M>>
extends DynamicGraphMinFixedPoint
implements LayerLightEventListener {
    private static final Direction[] DIRECTIONS = Direction.values();
    protected final LightChunkGetter chunkSource;
    protected final LightLayer layer;
    protected final S storage;
    private boolean runningLightUpdates;
    protected final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
    private final long[] lastChunkPos = new long[2];
    private final BlockGetter[] lastChunk = new BlockGetter[2];

    public LayerLightEngine(LightChunkGetter lightChunkGetter, LightLayer lightLayer, S s) {
        super(16, 256, 8192);
        this.chunkSource = lightChunkGetter;
        this.layer = lightLayer;
        this.storage = s;
        this.clearCache();
    }

    @Override
    protected void checkNode(long l) {
        ((LayerLightSectionStorage)this.storage).runAllUpdates();
        if (((LayerLightSectionStorage)this.storage).storingLightForSection(SectionPos.blockToSection(l))) {
            super.checkNode(l);
        }
    }

    @Nullable
    private BlockGetter getChunk(int n, int n2) {
        long l = ChunkPos.asLong(n, n2);
        for (int i = 0; i < 2; ++i) {
            if (l != this.lastChunkPos[i]) continue;
            return this.lastChunk[i];
        }
        BlockGetter blockGetter = this.chunkSource.getChunkForLighting(n, n2);
        for (int i = 1; i > 0; --i) {
            this.lastChunkPos[i] = this.lastChunkPos[i - 1];
            this.lastChunk[i] = this.lastChunk[i - 1];
        }
        this.lastChunkPos[0] = l;
        this.lastChunk[0] = blockGetter;
        return blockGetter;
    }

    private void clearCache() {
        Arrays.fill(this.lastChunkPos, ChunkPos.INVALID_CHUNK_POS);
        Arrays.fill(this.lastChunk, null);
    }

    protected BlockState getStateAndOpacity(long l, @Nullable MutableInt mutableInt) {
        int n;
        boolean bl;
        if (l == Long.MAX_VALUE) {
            if (mutableInt != null) {
                mutableInt.setValue(0);
            }
            return Blocks.AIR.defaultBlockState();
        }
        int n2 = SectionPos.blockToSectionCoord(BlockPos.getX(l));
        BlockGetter blockGetter = this.getChunk(n2, n = SectionPos.blockToSectionCoord(BlockPos.getZ(l)));
        if (blockGetter == null) {
            if (mutableInt != null) {
                mutableInt.setValue(16);
            }
            return Blocks.BEDROCK.defaultBlockState();
        }
        this.pos.set(l);
        BlockState blockState = blockGetter.getBlockState(this.pos);
        boolean bl2 = bl = blockState.canOcclude() && blockState.useShapeForLightOcclusion();
        if (mutableInt != null) {
            mutableInt.setValue(blockState.getLightBlock(this.chunkSource.getLevel(), this.pos));
        }
        return bl ? blockState : Blocks.AIR.defaultBlockState();
    }

    protected VoxelShape getShape(BlockState blockState, long l, Direction direction) {
        return blockState.canOcclude() ? blockState.getFaceOcclusionShape(this.chunkSource.getLevel(), this.pos.set(l), direction) : Shapes.empty();
    }

    public static int getLightBlockInto(BlockGetter blockGetter, BlockState blockState, BlockPos blockPos, BlockState blockState2, BlockPos blockPos2, Direction direction, int n) {
        VoxelShape voxelShape;
        boolean bl;
        boolean bl2 = blockState.canOcclude() && blockState.useShapeForLightOcclusion();
        boolean bl3 = bl = blockState2.canOcclude() && blockState2.useShapeForLightOcclusion();
        if (!bl2 && !bl) {
            return n;
        }
        VoxelShape voxelShape2 = bl2 ? blockState.getOcclusionShape(blockGetter, blockPos) : Shapes.empty();
        VoxelShape voxelShape3 = voxelShape = bl ? blockState2.getOcclusionShape(blockGetter, blockPos2) : Shapes.empty();
        if (Shapes.mergedFaceOccludes(voxelShape2, voxelShape, direction)) {
            return 16;
        }
        return n;
    }

    @Override
    protected boolean isSource(long l) {
        return l == Long.MAX_VALUE;
    }

    @Override
    protected int getComputedLevel(long l, long l2, int n) {
        return 0;
    }

    @Override
    protected int getLevel(long l) {
        if (l == Long.MAX_VALUE) {
            return 0;
        }
        return 15 - ((LayerLightSectionStorage)this.storage).getStoredLevel(l);
    }

    protected int getLevel(DataLayer dataLayer, long l) {
        return 15 - dataLayer.get(SectionPos.sectionRelative(BlockPos.getX(l)), SectionPos.sectionRelative(BlockPos.getY(l)), SectionPos.sectionRelative(BlockPos.getZ(l)));
    }

    @Override
    protected void setLevel(long l, int n) {
        ((LayerLightSectionStorage)this.storage).setStoredLevel(l, Math.min(15, 15 - n));
    }

    @Override
    protected int computeLevelFromNeighbor(long l, long l2, int n) {
        return 0;
    }

    public boolean hasLightWork() {
        return this.hasWork() || ((DynamicGraphMinFixedPoint)this.storage).hasWork() || ((LayerLightSectionStorage)this.storage).hasInconsistencies();
    }

    public int runUpdates(int n, boolean bl, boolean bl2) {
        if (!this.runningLightUpdates) {
            if (((DynamicGraphMinFixedPoint)this.storage).hasWork() && (n = ((DynamicGraphMinFixedPoint)this.storage).runUpdates(n)) == 0) {
                return n;
            }
            ((LayerLightSectionStorage)this.storage).markNewInconsistencies(this, bl, bl2);
        }
        this.runningLightUpdates = true;
        if (this.hasWork()) {
            n = this.runUpdates(n);
            this.clearCache();
            if (n == 0) {
                return n;
            }
        }
        this.runningLightUpdates = false;
        ((LayerLightSectionStorage)this.storage).swapSectionMap();
        return n;
    }

    protected void queueSectionData(long l, @Nullable DataLayer dataLayer, boolean bl) {
        ((LayerLightSectionStorage)this.storage).queueSectionData(l, dataLayer, bl);
    }

    @Nullable
    @Override
    public DataLayer getDataLayerData(SectionPos sectionPos) {
        return ((LayerLightSectionStorage)this.storage).getDataLayerData(sectionPos.asLong());
    }

    @Override
    public int getLightValue(BlockPos blockPos) {
        return ((LayerLightSectionStorage)this.storage).getLightValue(blockPos.asLong());
    }

    public String getDebugData(long l) {
        return "" + ((LayerLightSectionStorage)this.storage).getLevel(l);
    }

    public void checkBlock(BlockPos blockPos) {
        long l = blockPos.asLong();
        this.checkNode(l);
        for (Direction direction : DIRECTIONS) {
            this.checkNode(BlockPos.offset(l, direction));
        }
    }

    public void onBlockEmissionIncrease(BlockPos blockPos, int n) {
    }

    @Override
    public void updateSectionStatus(SectionPos sectionPos, boolean bl) {
        ((LayerLightSectionStorage)this.storage).updateSectionStatus(sectionPos.asLong(), bl);
    }

    public void enableLightSources(ChunkPos chunkPos, boolean bl) {
        long l = SectionPos.getZeroNode(SectionPos.asLong(chunkPos.x, 0, chunkPos.z));
        ((LayerLightSectionStorage)this.storage).enableLightSources(l, bl);
    }

    public void retainData(ChunkPos chunkPos, boolean bl) {
        long l = SectionPos.getZeroNode(SectionPos.asLong(chunkPos.x, 0, chunkPos.z));
        ((LayerLightSectionStorage)this.storage).retainData(l, bl);
    }
}

