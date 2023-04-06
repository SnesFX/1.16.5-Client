/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.structure;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BeardedStructureStart;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.NetherFossilPieces;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class NetherFossilFeature
extends StructureFeature<NoneFeatureConfiguration> {
    public NetherFossilFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public StructureFeature.StructureStartFactory<NoneFeatureConfiguration> getStartFactory() {
        return (arg_0, arg_1, arg_2, arg_3, arg_4, arg_5) -> FeatureStart.new(arg_0, arg_1, arg_2, arg_3, arg_4, arg_5);
    }

    public static class FeatureStart
    extends BeardedStructureStart<NoneFeatureConfiguration> {
        public FeatureStart(StructureFeature<NoneFeatureConfiguration> structureFeature, int n, int n2, BoundingBox boundingBox, int n3, long l) {
            super(structureFeature, n, n2, boundingBox, n3, l);
        }

        @Override
        public void generatePieces(RegistryAccess registryAccess, ChunkGenerator chunkGenerator, StructureManager structureManager, int n, int n2, Biome biome, NoneFeatureConfiguration noneFeatureConfiguration) {
            int n3;
            ChunkPos chunkPos = new ChunkPos(n, n2);
            int n4 = chunkPos.getMinBlockX() + this.random.nextInt(16);
            int n5 = chunkPos.getMinBlockZ() + this.random.nextInt(16);
            int n6 = chunkGenerator.getSeaLevel();
            BlockGetter blockGetter = chunkGenerator.getBaseColumn(n4, n5);
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos(n4, n3, n5);
            for (n3 = n6 + this.random.nextInt((int)(chunkGenerator.getGenDepth() - 2 - n6)); n3 > n6; --n3) {
                BlockState blockState = blockGetter.getBlockState(mutableBlockPos);
                mutableBlockPos.move(Direction.DOWN);
                BlockState blockState2 = blockGetter.getBlockState(mutableBlockPos);
                if (blockState.isAir() && (blockState2.is(Blocks.SOUL_SAND) || blockState2.isFaceSturdy(blockGetter, mutableBlockPos, Direction.UP))) break;
            }
            if (n3 <= n6) {
                return;
            }
            NetherFossilPieces.addPieces(structureManager, this.pieces, this.random, new BlockPos(n4, n3, n5));
            this.calculateBoundingBox();
        }
    }

}

