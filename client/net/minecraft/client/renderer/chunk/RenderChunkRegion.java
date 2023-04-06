/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer.chunk;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;

public class RenderChunkRegion
implements BlockAndTintGetter {
    protected final int centerX;
    protected final int centerZ;
    protected final BlockPos start;
    protected final int xLength;
    protected final int yLength;
    protected final int zLength;
    protected final LevelChunk[][] chunks;
    protected final BlockState[] blockStates;
    protected final FluidState[] fluidStates;
    protected final Level level;

    @Nullable
    public static RenderChunkRegion createIfNotEmpty(Level level, BlockPos blockPos, BlockPos blockPos2, int n) {
        int n2;
        int n3 = blockPos.getX() - n >> 4;
        int n4 = blockPos.getZ() - n >> 4;
        int n5 = blockPos2.getX() + n >> 4;
        int n6 = blockPos2.getZ() + n >> 4;
        LevelChunk[][] arrlevelChunk = new LevelChunk[n5 - n3 + 1][n6 - n4 + 1];
        for (n2 = n3; n2 <= n5; ++n2) {
            for (int i = n4; i <= n6; ++i) {
                arrlevelChunk[n2 - n3][i - n4] = level.getChunk(n2, i);
            }
        }
        if (RenderChunkRegion.isAllEmpty(blockPos, blockPos2, n3, n4, arrlevelChunk)) {
            return null;
        }
        n2 = 1;
        BlockPos blockPos3 = blockPos.offset(-1, -1, -1);
        BlockPos blockPos4 = blockPos2.offset(1, 1, 1);
        return new RenderChunkRegion(level, n3, n4, arrlevelChunk, blockPos3, blockPos4);
    }

    public static boolean isAllEmpty(BlockPos blockPos, BlockPos blockPos2, int n, int n2, LevelChunk[][] arrlevelChunk) {
        for (int i = blockPos.getX() >> 4; i <= blockPos2.getX() >> 4; ++i) {
            for (int j = blockPos.getZ() >> 4; j <= blockPos2.getZ() >> 4; ++j) {
                LevelChunk levelChunk = arrlevelChunk[i - n][j - n2];
                if (levelChunk.isYSpaceEmpty(blockPos.getY(), blockPos2.getY())) continue;
                return false;
            }
        }
        return true;
    }

    public RenderChunkRegion(Level level, int n, int n2, LevelChunk[][] arrlevelChunk, BlockPos blockPos, BlockPos blockPos2) {
        this.level = level;
        this.centerX = n;
        this.centerZ = n2;
        this.chunks = arrlevelChunk;
        this.start = blockPos;
        this.xLength = blockPos2.getX() - blockPos.getX() + 1;
        this.yLength = blockPos2.getY() - blockPos.getY() + 1;
        this.zLength = blockPos2.getZ() - blockPos.getZ() + 1;
        this.blockStates = new BlockState[this.xLength * this.yLength * this.zLength];
        this.fluidStates = new FluidState[this.xLength * this.yLength * this.zLength];
        for (BlockPos blockPos3 : BlockPos.betweenClosed(blockPos, blockPos2)) {
            int n3 = (blockPos3.getX() >> 4) - n;
            int n4 = (blockPos3.getZ() >> 4) - n2;
            LevelChunk levelChunk = arrlevelChunk[n3][n4];
            int n5 = this.index(blockPos3);
            this.blockStates[n5] = levelChunk.getBlockState(blockPos3);
            this.fluidStates[n5] = levelChunk.getFluidState(blockPos3);
        }
    }

    protected final int index(BlockPos blockPos) {
        return this.index(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    protected int index(int n, int n2, int n3) {
        int n4 = n - this.start.getX();
        int n5 = n2 - this.start.getY();
        int n6 = n3 - this.start.getZ();
        return n6 * this.xLength * this.yLength + n5 * this.xLength + n4;
    }

    @Override
    public BlockState getBlockState(BlockPos blockPos) {
        return this.blockStates[this.index(blockPos)];
    }

    @Override
    public FluidState getFluidState(BlockPos blockPos) {
        return this.fluidStates[this.index(blockPos)];
    }

    @Override
    public float getShade(Direction direction, boolean bl) {
        return this.level.getShade(direction, bl);
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return this.level.getLightEngine();
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(BlockPos blockPos) {
        return this.getBlockEntity(blockPos, LevelChunk.EntityCreationType.IMMEDIATE);
    }

    @Nullable
    public BlockEntity getBlockEntity(BlockPos blockPos, LevelChunk.EntityCreationType entityCreationType) {
        int n = (blockPos.getX() >> 4) - this.centerX;
        int n2 = (blockPos.getZ() >> 4) - this.centerZ;
        return this.chunks[n][n2].getBlockEntity(blockPos, entityCreationType);
    }

    @Override
    public int getBlockTint(BlockPos blockPos, ColorResolver colorResolver) {
        return this.level.getBlockTint(blockPos, colorResolver);
    }
}

