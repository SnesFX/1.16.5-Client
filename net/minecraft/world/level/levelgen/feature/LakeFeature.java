/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderConfiguration;
import net.minecraft.world.level.material.Material;

public class LakeFeature
extends Feature<BlockStateConfiguration> {
    private static final BlockState AIR = Blocks.CAVE_AIR.defaultBlockState();

    public LakeFeature(Codec<BlockStateConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(WorldGenLevel worldGenLevel, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, BlockStateConfiguration blockStateConfiguration) {
        int n;
        int n2;
        Object object;
        while (blockPos.getY() > 5 && worldGenLevel.isEmptyBlock(blockPos)) {
            blockPos = blockPos.below();
        }
        if (blockPos.getY() <= 4) {
            return false;
        }
        if (worldGenLevel.startsForFeature(SectionPos.of(blockPos = blockPos.below(4)), StructureFeature.VILLAGE).findAny().isPresent()) {
            return false;
        }
        boolean[] arrbl = new boolean[2048];
        int n3 = random.nextInt(4) + 4;
        for (n2 = 0; n2 < n3; ++n2) {
            double d = random.nextDouble() * 6.0 + 3.0;
            double d2 = random.nextDouble() * 4.0 + 2.0;
            double d3 = random.nextDouble() * 6.0 + 3.0;
            double d4 = random.nextDouble() * (16.0 - d - 2.0) + 1.0 + d / 2.0;
            double d5 = random.nextDouble() * (8.0 - d2 - 4.0) + 2.0 + d2 / 2.0;
            double d6 = random.nextDouble() * (16.0 - d3 - 2.0) + 1.0 + d3 / 2.0;
            for (int i = 1; i < 15; ++i) {
                for (int j = 1; j < 15; ++j) {
                    for (int k = 1; k < 7; ++k) {
                        double d7 = ((double)i - d4) / (d / 2.0);
                        double d8 = ((double)k - d5) / (d2 / 2.0);
                        double d9 = ((double)j - d6) / (d3 / 2.0);
                        double d10 = d7 * d7 + d8 * d8 + d9 * d9;
                        if (!(d10 < 1.0)) continue;
                        arrbl[(i * 16 + j) * 8 + k] = true;
                    }
                }
            }
        }
        for (n2 = 0; n2 < 16; ++n2) {
            for (int i = 0; i < 16; ++i) {
                for (n = 0; n < 8; ++n) {
                    boolean bl;
                    boolean bl2 = bl = !arrbl[(n2 * 16 + i) * 8 + n] && (n2 < 15 && arrbl[((n2 + 1) * 16 + i) * 8 + n] || n2 > 0 && arrbl[((n2 - 1) * 16 + i) * 8 + n] || i < 15 && arrbl[(n2 * 16 + i + 1) * 8 + n] || i > 0 && arrbl[(n2 * 16 + (i - 1)) * 8 + n] || n < 7 && arrbl[(n2 * 16 + i) * 8 + n + 1] || n > 0 && arrbl[(n2 * 16 + i) * 8 + (n - 1)]);
                    if (!bl) continue;
                    object = worldGenLevel.getBlockState(blockPos.offset(n2, n, i)).getMaterial();
                    if (n >= 4 && ((Material)object).isLiquid()) {
                        return false;
                    }
                    if (n >= 4 || ((Material)object).isSolid() || worldGenLevel.getBlockState(blockPos.offset(n2, n, i)) == blockStateConfiguration.state) continue;
                    return false;
                }
            }
        }
        for (n2 = 0; n2 < 16; ++n2) {
            for (int i = 0; i < 16; ++i) {
                for (n = 0; n < 8; ++n) {
                    if (!arrbl[(n2 * 16 + i) * 8 + n]) continue;
                    worldGenLevel.setBlock(blockPos.offset(n2, n, i), n >= 4 ? AIR : blockStateConfiguration.state, 2);
                }
            }
        }
        for (n2 = 0; n2 < 16; ++n2) {
            for (int i = 0; i < 16; ++i) {
                for (n = 4; n < 8; ++n) {
                    BlockPos blockPos2;
                    if (!arrbl[(n2 * 16 + i) * 8 + n] || !LakeFeature.isDirt(worldGenLevel.getBlockState(blockPos2 = blockPos.offset(n2, n - 1, i)).getBlock()) || worldGenLevel.getBrightness(LightLayer.SKY, blockPos.offset(n2, n, i)) <= 0) continue;
                    object = worldGenLevel.getBiome(blockPos2);
                    if (((Biome)object).getGenerationSettings().getSurfaceBuilderConfig().getTopMaterial().is(Blocks.MYCELIUM)) {
                        worldGenLevel.setBlock(blockPos2, Blocks.MYCELIUM.defaultBlockState(), 2);
                        continue;
                    }
                    worldGenLevel.setBlock(blockPos2, Blocks.GRASS_BLOCK.defaultBlockState(), 2);
                }
            }
        }
        if (blockStateConfiguration.state.getMaterial() == Material.LAVA) {
            for (n2 = 0; n2 < 16; ++n2) {
                for (int i = 0; i < 16; ++i) {
                    for (n = 0; n < 8; ++n) {
                        boolean bl;
                        boolean bl3 = bl = !arrbl[(n2 * 16 + i) * 8 + n] && (n2 < 15 && arrbl[((n2 + 1) * 16 + i) * 8 + n] || n2 > 0 && arrbl[((n2 - 1) * 16 + i) * 8 + n] || i < 15 && arrbl[(n2 * 16 + i + 1) * 8 + n] || i > 0 && arrbl[(n2 * 16 + (i - 1)) * 8 + n] || n < 7 && arrbl[(n2 * 16 + i) * 8 + n + 1] || n > 0 && arrbl[(n2 * 16 + i) * 8 + (n - 1)]);
                        if (!bl || n >= 4 && random.nextInt(2) == 0 || !worldGenLevel.getBlockState(blockPos.offset(n2, n, i)).getMaterial().isSolid()) continue;
                        worldGenLevel.setBlock(blockPos.offset(n2, n, i), Blocks.STONE.defaultBlockState(), 2);
                    }
                }
            }
        }
        if (blockStateConfiguration.state.getMaterial() == Material.WATER) {
            for (n2 = 0; n2 < 16; ++n2) {
                for (int i = 0; i < 16; ++i) {
                    n = 4;
                    BlockPos blockPos3 = blockPos.offset(n2, 4, i);
                    if (!worldGenLevel.getBiome(blockPos3).shouldFreeze(worldGenLevel, blockPos3, false)) continue;
                    worldGenLevel.setBlock(blockPos3, Blocks.ICE.defaultBlockState(), 2);
                }
            }
        }
        return true;
    }
}

