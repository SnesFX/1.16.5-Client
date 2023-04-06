/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level;

import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkBiomeContainer;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;

public interface LevelReader
extends BlockAndTintGetter,
CollisionGetter,
BiomeManager.NoiseBiomeSource {
    @Nullable
    public ChunkAccess getChunk(int var1, int var2, ChunkStatus var3, boolean var4);

    @Deprecated
    public boolean hasChunk(int var1, int var2);

    public int getHeight(Heightmap.Types var1, int var2, int var3);

    public int getSkyDarken();

    public BiomeManager getBiomeManager();

    default public Biome getBiome(BlockPos blockPos) {
        return this.getBiomeManager().getBiome(blockPos);
    }

    default public Stream<BlockState> getBlockStatesIfLoaded(AABB aABB) {
        int n;
        int n2 = Mth.floor(aABB.minX);
        int n3 = Mth.floor(aABB.maxX);
        int n4 = Mth.floor(aABB.minY);
        int n5 = Mth.floor(aABB.maxY);
        int n6 = Mth.floor(aABB.minZ);
        if (this.hasChunksAt(n2, n4, n6, n3, n5, n = Mth.floor(aABB.maxZ))) {
            return this.getBlockStates(aABB);
        }
        return Stream.empty();
    }

    @Override
    default public int getBlockTint(BlockPos blockPos, ColorResolver colorResolver) {
        return colorResolver.getColor(this.getBiome(blockPos), blockPos.getX(), blockPos.getZ());
    }

    @Override
    default public Biome getNoiseBiome(int n, int n2, int n3) {
        ChunkAccess chunkAccess = this.getChunk(n >> 2, n3 >> 2, ChunkStatus.BIOMES, false);
        if (chunkAccess != null && chunkAccess.getBiomes() != null) {
            return chunkAccess.getBiomes().getNoiseBiome(n, n2, n3);
        }
        return this.getUncachedNoiseBiome(n, n2, n3);
    }

    public Biome getUncachedNoiseBiome(int var1, int var2, int var3);

    public boolean isClientSide();

    @Deprecated
    public int getSeaLevel();

    public DimensionType dimensionType();

    default public BlockPos getHeightmapPos(Heightmap.Types types, BlockPos blockPos) {
        return new BlockPos(blockPos.getX(), this.getHeight(types, blockPos.getX(), blockPos.getZ()), blockPos.getZ());
    }

    default public boolean isEmptyBlock(BlockPos blockPos) {
        return this.getBlockState(blockPos).isAir();
    }

    default public boolean canSeeSkyFromBelowWater(BlockPos blockPos) {
        if (blockPos.getY() >= this.getSeaLevel()) {
            return this.canSeeSky(blockPos);
        }
        BlockPos blockPos2 = new BlockPos(blockPos.getX(), this.getSeaLevel(), blockPos.getZ());
        if (!this.canSeeSky(blockPos2)) {
            return false;
        }
        blockPos2 = blockPos2.below();
        while (blockPos2.getY() > blockPos.getY()) {
            BlockState blockState = this.getBlockState(blockPos2);
            if (blockState.getLightBlock(this, blockPos2) > 0 && !blockState.getMaterial().isLiquid()) {
                return false;
            }
            blockPos2 = blockPos2.below();
        }
        return true;
    }

    @Deprecated
    default public float getBrightness(BlockPos blockPos) {
        return this.dimensionType().brightness(this.getMaxLocalRawBrightness(blockPos));
    }

    default public int getDirectSignal(BlockPos blockPos, Direction direction) {
        return this.getBlockState(blockPos).getDirectSignal(this, blockPos, direction);
    }

    default public ChunkAccess getChunk(BlockPos blockPos) {
        return this.getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4);
    }

    default public ChunkAccess getChunk(int n, int n2) {
        return this.getChunk(n, n2, ChunkStatus.FULL, true);
    }

    default public ChunkAccess getChunk(int n, int n2, ChunkStatus chunkStatus) {
        return this.getChunk(n, n2, chunkStatus, true);
    }

    @Nullable
    @Override
    default public BlockGetter getChunkForCollisions(int n, int n2) {
        return this.getChunk(n, n2, ChunkStatus.EMPTY, false);
    }

    default public boolean isWaterAt(BlockPos blockPos) {
        return this.getFluidState(blockPos).is(FluidTags.WATER);
    }

    default public boolean containsAnyLiquid(AABB aABB) {
        int n = Mth.floor(aABB.minX);
        int n2 = Mth.ceil(aABB.maxX);
        int n3 = Mth.floor(aABB.minY);
        int n4 = Mth.ceil(aABB.maxY);
        int n5 = Mth.floor(aABB.minZ);
        int n6 = Mth.ceil(aABB.maxZ);
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int i = n; i < n2; ++i) {
            for (int j = n3; j < n4; ++j) {
                for (int k = n5; k < n6; ++k) {
                    BlockState blockState = this.getBlockState(mutableBlockPos.set(i, j, k));
                    if (blockState.getFluidState().isEmpty()) continue;
                    return true;
                }
            }
        }
        return false;
    }

    default public int getMaxLocalRawBrightness(BlockPos blockPos) {
        return this.getMaxLocalRawBrightness(blockPos, this.getSkyDarken());
    }

    default public int getMaxLocalRawBrightness(BlockPos blockPos, int n) {
        if (blockPos.getX() < -30000000 || blockPos.getZ() < -30000000 || blockPos.getX() >= 30000000 || blockPos.getZ() >= 30000000) {
            return 15;
        }
        return this.getRawBrightness(blockPos, n);
    }

    @Deprecated
    default public boolean hasChunkAt(BlockPos blockPos) {
        return this.hasChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4);
    }

    @Deprecated
    default public boolean hasChunksAt(BlockPos blockPos, BlockPos blockPos2) {
        return this.hasChunksAt(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
    }

    @Deprecated
    default public boolean hasChunksAt(int n, int n2, int n3, int n4, int n5, int n6) {
        if (n5 < 0 || n2 >= 256) {
            return false;
        }
        n3 >>= 4;
        n6 >>= 4;
        for (int i = n >>= 4; i <= (n4 >>= 4); ++i) {
            for (int j = n3; j <= n6; ++j) {
                if (this.hasChunk(i, j)) continue;
                return false;
            }
        }
        return true;
    }
}

