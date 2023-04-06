/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.levelgen.placement;

import java.util.BitSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;

public class DecorationContext {
    private final WorldGenLevel level;
    private final ChunkGenerator generator;

    public DecorationContext(WorldGenLevel worldGenLevel, ChunkGenerator chunkGenerator) {
        this.level = worldGenLevel;
        this.generator = chunkGenerator;
    }

    public int getHeight(Heightmap.Types types, int n, int n2) {
        return this.level.getHeight(types, n, n2);
    }

    public int getGenDepth() {
        return this.generator.getGenDepth();
    }

    public int getSeaLevel() {
        return this.generator.getSeaLevel();
    }

    public BitSet getCarvingMask(ChunkPos chunkPos, GenerationStep.Carving carving) {
        return ((ProtoChunk)this.level.getChunk(chunkPos.x, chunkPos.z)).getOrCreateCarvingMask(carving);
    }

    public BlockState getBlockState(BlockPos blockPos) {
        return this.level.getBlockState(blockPos);
    }
}

