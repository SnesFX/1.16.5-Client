/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level;

import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PathNavigationRegion
implements BlockGetter,
CollisionGetter {
    protected final int centerX;
    protected final int centerZ;
    protected final ChunkAccess[][] chunks;
    protected boolean allEmpty;
    protected final Level level;

    public PathNavigationRegion(Level level, BlockPos blockPos, BlockPos blockPos2) {
        int n;
        int n2;
        this.level = level;
        this.centerX = blockPos.getX() >> 4;
        this.centerZ = blockPos.getZ() >> 4;
        int n3 = blockPos2.getX() >> 4;
        int n4 = blockPos2.getZ() >> 4;
        this.chunks = new ChunkAccess[n3 - this.centerX + 1][n4 - this.centerZ + 1];
        ChunkSource chunkSource = level.getChunkSource();
        this.allEmpty = true;
        for (n2 = this.centerX; n2 <= n3; ++n2) {
            for (n = this.centerZ; n <= n4; ++n) {
                this.chunks[n2 - this.centerX][n - this.centerZ] = chunkSource.getChunkNow(n2, n);
            }
        }
        for (n2 = blockPos.getX() >> 4; n2 <= blockPos2.getX() >> 4; ++n2) {
            for (n = blockPos.getZ() >> 4; n <= blockPos2.getZ() >> 4; ++n) {
                ChunkAccess chunkAccess = this.chunks[n2 - this.centerX][n - this.centerZ];
                if (chunkAccess == null || chunkAccess.isYSpaceEmpty(blockPos.getY(), blockPos2.getY())) continue;
                this.allEmpty = false;
                return;
            }
        }
    }

    private ChunkAccess getChunk(BlockPos blockPos) {
        return this.getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4);
    }

    private ChunkAccess getChunk(int n, int n2) {
        int n3 = n - this.centerX;
        int n4 = n2 - this.centerZ;
        if (n3 < 0 || n3 >= this.chunks.length || n4 < 0 || n4 >= this.chunks[n3].length) {
            return new EmptyLevelChunk(this.level, new ChunkPos(n, n2));
        }
        ChunkAccess chunkAccess = this.chunks[n3][n4];
        return chunkAccess != null ? chunkAccess : new EmptyLevelChunk(this.level, new ChunkPos(n, n2));
    }

    @Override
    public WorldBorder getWorldBorder() {
        return this.level.getWorldBorder();
    }

    @Override
    public BlockGetter getChunkForCollisions(int n, int n2) {
        return this.getChunk(n, n2);
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(BlockPos blockPos) {
        ChunkAccess chunkAccess = this.getChunk(blockPos);
        return chunkAccess.getBlockEntity(blockPos);
    }

    @Override
    public BlockState getBlockState(BlockPos blockPos) {
        if (Level.isOutsideBuildHeight(blockPos)) {
            return Blocks.AIR.defaultBlockState();
        }
        ChunkAccess chunkAccess = this.getChunk(blockPos);
        return chunkAccess.getBlockState(blockPos);
    }

    @Override
    public Stream<VoxelShape> getEntityCollisions(@Nullable Entity entity, AABB aABB, Predicate<Entity> predicate) {
        return Stream.empty();
    }

    @Override
    public Stream<VoxelShape> getCollisions(@Nullable Entity entity, AABB aABB, Predicate<Entity> predicate) {
        return this.getBlockCollisions(entity, aABB);
    }

    @Override
    public FluidState getFluidState(BlockPos blockPos) {
        if (Level.isOutsideBuildHeight(blockPos)) {
            return Fluids.EMPTY.defaultFluidState();
        }
        ChunkAccess chunkAccess = this.getChunk(blockPos);
        return chunkAccess.getFluidState(blockPos);
    }
}

