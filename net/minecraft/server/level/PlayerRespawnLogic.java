/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.server.level;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderConfiguration;
import net.minecraft.world.level.material.FluidState;

public class PlayerRespawnLogic {
    @Nullable
    protected static BlockPos getOverworldRespawnPos(ServerLevel serverLevel, int n, int n2, boolean bl) {
        int n3;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos(n, 0, n2);
        Biome biome = serverLevel.getBiome(mutableBlockPos);
        boolean bl2 = serverLevel.dimensionType().hasCeiling();
        BlockState blockState = biome.getGenerationSettings().getSurfaceBuilderConfig().getTopMaterial();
        if (bl && !blockState.getBlock().is(BlockTags.VALID_SPAWN)) {
            return null;
        }
        LevelChunk levelChunk = serverLevel.getChunk(n >> 4, n2 >> 4);
        int n4 = n3 = bl2 ? serverLevel.getChunkSource().getGenerator().getSpawnHeight() : levelChunk.getHeight(Heightmap.Types.MOTION_BLOCKING, n & 0xF, n2 & 0xF);
        if (n3 < 0) {
            return null;
        }
        int n5 = levelChunk.getHeight(Heightmap.Types.WORLD_SURFACE, n & 0xF, n2 & 0xF);
        if (n5 <= n3 && n5 > levelChunk.getHeight(Heightmap.Types.OCEAN_FLOOR, n & 0xF, n2 & 0xF)) {
            return null;
        }
        for (int i = n3 + 1; i >= 0; --i) {
            mutableBlockPos.set(n, i, n2);
            BlockState blockState2 = serverLevel.getBlockState(mutableBlockPos);
            if (!blockState2.getFluidState().isEmpty()) break;
            if (!blockState2.equals(blockState)) continue;
            return ((BlockPos)mutableBlockPos.above()).immutable();
        }
        return null;
    }

    @Nullable
    public static BlockPos getSpawnPosInChunk(ServerLevel serverLevel, ChunkPos chunkPos, boolean bl) {
        for (int i = chunkPos.getMinBlockX(); i <= chunkPos.getMaxBlockX(); ++i) {
            for (int j = chunkPos.getMinBlockZ(); j <= chunkPos.getMaxBlockZ(); ++j) {
                BlockPos blockPos = PlayerRespawnLogic.getOverworldRespawnPos(serverLevel, i, j, bl);
                if (blockPos == null) continue;
                return blockPos;
            }
        }
        return null;
    }
}

