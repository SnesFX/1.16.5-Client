/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.mutable.MutableInt
 */
package net.minecraft.world.level.lighting;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.BlockLightSectionStorage;
import net.minecraft.world.level.lighting.LayerLightEngine;
import net.minecraft.world.level.lighting.LayerLightSectionStorage;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableInt;

public final class BlockLightEngine
extends LayerLightEngine<BlockLightSectionStorage.BlockDataLayerStorageMap, BlockLightSectionStorage> {
    private static final Direction[] DIRECTIONS = Direction.values();
    private final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

    public BlockLightEngine(LightChunkGetter lightChunkGetter) {
        super(lightChunkGetter, LightLayer.BLOCK, new BlockLightSectionStorage(lightChunkGetter));
    }

    private int getLightEmission(long l) {
        int n = BlockPos.getX(l);
        int n2 = BlockPos.getY(l);
        int n3 = BlockPos.getZ(l);
        BlockGetter blockGetter = this.chunkSource.getChunkForLighting(n >> 4, n3 >> 4);
        if (blockGetter != null) {
            return blockGetter.getLightEmission(this.pos.set(n, n2, n3));
        }
        return 0;
    }

    @Override
    protected int computeLevelFromNeighbor(long l, long l2, int n) {
        VoxelShape voxelShape;
        int n2;
        int n3;
        if (l2 == Long.MAX_VALUE) {
            return 15;
        }
        if (l == Long.MAX_VALUE) {
            return n + 15 - this.getLightEmission(l2);
        }
        if (n >= 15) {
            return n;
        }
        int n4 = Integer.signum(BlockPos.getX(l2) - BlockPos.getX(l));
        Direction direction = Direction.fromNormal(n4, n3 = Integer.signum(BlockPos.getY(l2) - BlockPos.getY(l)), n2 = Integer.signum(BlockPos.getZ(l2) - BlockPos.getZ(l)));
        if (direction == null) {
            return 15;
        }
        MutableInt mutableInt = new MutableInt();
        BlockState blockState = this.getStateAndOpacity(l2, mutableInt);
        if (mutableInt.getValue() >= 15) {
            return 15;
        }
        BlockState blockState2 = this.getStateAndOpacity(l, null);
        VoxelShape voxelShape2 = this.getShape(blockState2, l, direction);
        if (Shapes.faceShapeOccludes(voxelShape2, voxelShape = this.getShape(blockState, l2, direction.getOpposite()))) {
            return 15;
        }
        return n + Math.max(1, mutableInt.getValue());
    }

    @Override
    protected void checkNeighborsAfterUpdate(long l, int n, boolean bl) {
        long l2 = SectionPos.blockToSection(l);
        for (Direction direction : DIRECTIONS) {
            long l3 = BlockPos.offset(l, direction);
            long l4 = SectionPos.blockToSection(l3);
            if (l2 != l4 && !((BlockLightSectionStorage)this.storage).storingLightForSection(l4)) continue;
            this.checkNeighbor(l, l3, n, bl);
        }
    }

    @Override
    protected int getComputedLevel(long l, long l2, int n) {
        int n2 = n;
        if (Long.MAX_VALUE != l2) {
            int n3 = this.computeLevelFromNeighbor(Long.MAX_VALUE, l, 0);
            if (n2 > n3) {
                n2 = n3;
            }
            if (n2 == 0) {
                return n2;
            }
        }
        long l3 = SectionPos.blockToSection(l);
        DataLayer dataLayer = ((BlockLightSectionStorage)this.storage).getDataLayer(l3, true);
        for (Direction direction : DIRECTIONS) {
            long l4;
            DataLayer dataLayer2;
            long l5 = BlockPos.offset(l, direction);
            if (l5 == l2 || (dataLayer2 = l3 == (l4 = SectionPos.blockToSection(l5)) ? dataLayer : ((BlockLightSectionStorage)this.storage).getDataLayer(l4, true)) == null) continue;
            int n4 = this.computeLevelFromNeighbor(l5, l, this.getLevel(dataLayer2, l5));
            if (n2 > n4) {
                n2 = n4;
            }
            if (n2 != 0) continue;
            return n2;
        }
        return n2;
    }

    @Override
    public void onBlockEmissionIncrease(BlockPos blockPos, int n) {
        ((BlockLightSectionStorage)this.storage).runAllUpdates();
        this.checkEdge(Long.MAX_VALUE, blockPos.asLong(), 15 - n, true);
    }
}

