/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.surfacebuilders;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderBaseConfiguration;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderConfiguration;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;

public class BadlandsSurfaceBuilder
extends SurfaceBuilder<SurfaceBuilderBaseConfiguration> {
    private static final BlockState WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.defaultBlockState();
    private static final BlockState ORANGE_TERRACOTTA = Blocks.ORANGE_TERRACOTTA.defaultBlockState();
    private static final BlockState TERRACOTTA = Blocks.TERRACOTTA.defaultBlockState();
    private static final BlockState YELLOW_TERRACOTTA = Blocks.YELLOW_TERRACOTTA.defaultBlockState();
    private static final BlockState BROWN_TERRACOTTA = Blocks.BROWN_TERRACOTTA.defaultBlockState();
    private static final BlockState RED_TERRACOTTA = Blocks.RED_TERRACOTTA.defaultBlockState();
    private static final BlockState LIGHT_GRAY_TERRACOTTA = Blocks.LIGHT_GRAY_TERRACOTTA.defaultBlockState();
    protected BlockState[] clayBands;
    protected long seed;
    protected PerlinSimplexNoise pillarNoise;
    protected PerlinSimplexNoise pillarRoofNoise;
    protected PerlinSimplexNoise clayBandsOffsetNoise;

    public BadlandsSurfaceBuilder(Codec<SurfaceBuilderBaseConfiguration> codec) {
        super(codec);
    }

    @Override
    public void apply(Random random, ChunkAccess chunkAccess, Biome biome, int n, int n2, int n3, double d, BlockState blockState, BlockState blockState2, int n4, long l, SurfaceBuilderBaseConfiguration surfaceBuilderBaseConfiguration) {
        int n5 = n & 0xF;
        int n6 = n2 & 0xF;
        BlockState blockState3 = WHITE_TERRACOTTA;
        SurfaceBuilderConfiguration surfaceBuilderConfiguration = biome.getGenerationSettings().getSurfaceBuilderConfig();
        BlockState blockState4 = surfaceBuilderConfiguration.getUnderMaterial();
        BlockState blockState5 = surfaceBuilderConfiguration.getTopMaterial();
        BlockState blockState6 = blockState4;
        int n7 = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
        boolean bl = Math.cos(d / 3.0 * 3.141592653589793) > 0.0;
        int n8 = -1;
        boolean bl2 = false;
        int n9 = 0;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int i = n3; i >= 0; --i) {
            if (n9 >= 15) continue;
            mutableBlockPos.set(n5, i, n6);
            BlockState blockState7 = chunkAccess.getBlockState(mutableBlockPos);
            if (blockState7.isAir()) {
                n8 = -1;
                continue;
            }
            if (!blockState7.is(blockState.getBlock())) continue;
            if (n8 == -1) {
                Object object;
                bl2 = false;
                if (n7 <= 0) {
                    blockState3 = Blocks.AIR.defaultBlockState();
                    blockState6 = blockState;
                } else if (i >= n4 - 4 && i <= n4 + 1) {
                    blockState3 = WHITE_TERRACOTTA;
                    blockState6 = blockState4;
                }
                if (i < n4 && (blockState3 == null || blockState3.isAir())) {
                    blockState3 = blockState2;
                }
                n8 = n7 + Math.max(0, i - n4);
                if (i >= n4 - 1) {
                    if (i > n4 + 3 + n7) {
                        object = i < 64 || i > 127 ? ORANGE_TERRACOTTA : (bl ? TERRACOTTA : this.getBand(n, i, n2));
                        chunkAccess.setBlockState(mutableBlockPos, (BlockState)object, false);
                    } else {
                        chunkAccess.setBlockState(mutableBlockPos, blockState5, false);
                        bl2 = true;
                    }
                } else {
                    chunkAccess.setBlockState(mutableBlockPos, blockState6, false);
                    object = blockState6.getBlock();
                    if (object == Blocks.WHITE_TERRACOTTA || object == Blocks.ORANGE_TERRACOTTA || object == Blocks.MAGENTA_TERRACOTTA || object == Blocks.LIGHT_BLUE_TERRACOTTA || object == Blocks.YELLOW_TERRACOTTA || object == Blocks.LIME_TERRACOTTA || object == Blocks.PINK_TERRACOTTA || object == Blocks.GRAY_TERRACOTTA || object == Blocks.LIGHT_GRAY_TERRACOTTA || object == Blocks.CYAN_TERRACOTTA || object == Blocks.PURPLE_TERRACOTTA || object == Blocks.BLUE_TERRACOTTA || object == Blocks.BROWN_TERRACOTTA || object == Blocks.GREEN_TERRACOTTA || object == Blocks.RED_TERRACOTTA || object == Blocks.BLACK_TERRACOTTA) {
                        chunkAccess.setBlockState(mutableBlockPos, ORANGE_TERRACOTTA, false);
                    }
                }
            } else if (n8 > 0) {
                --n8;
                if (bl2) {
                    chunkAccess.setBlockState(mutableBlockPos, ORANGE_TERRACOTTA, false);
                } else {
                    chunkAccess.setBlockState(mutableBlockPos, this.getBand(n, i, n2), false);
                }
            }
            ++n9;
        }
    }

    @Override
    public void initNoise(long l) {
        if (this.seed != l || this.clayBands == null) {
            this.generateBands(l);
        }
        if (this.seed != l || this.pillarNoise == null || this.pillarRoofNoise == null) {
            WorldgenRandom worldgenRandom = new WorldgenRandom(l);
            this.pillarNoise = new PerlinSimplexNoise(worldgenRandom, IntStream.rangeClosed(-3, 0));
            this.pillarRoofNoise = new PerlinSimplexNoise(worldgenRandom, (List<Integer>)ImmutableList.of((Object)0));
        }
        this.seed = l;
    }

    protected void generateBands(long l) {
        int n;
        int n2;
        int n3;
        int n4;
        int n5;
        int n6;
        int n7;
        this.clayBands = new BlockState[64];
        Arrays.fill(this.clayBands, TERRACOTTA);
        WorldgenRandom worldgenRandom = new WorldgenRandom(l);
        this.clayBandsOffsetNoise = new PerlinSimplexNoise(worldgenRandom, (List<Integer>)ImmutableList.of((Object)0));
        for (n6 = 0; n6 < 64; ++n6) {
            if ((n6 += worldgenRandom.nextInt(5) + 1) >= 64) continue;
            this.clayBands[n6] = ORANGE_TERRACOTTA;
        }
        n6 = worldgenRandom.nextInt(4) + 2;
        for (n = 0; n < n6; ++n) {
            n4 = worldgenRandom.nextInt(3) + 1;
            n5 = worldgenRandom.nextInt(64);
            for (n3 = 0; n5 + n3 < 64 && n3 < n4; ++n3) {
                this.clayBands[n5 + n3] = YELLOW_TERRACOTTA;
            }
        }
        n = worldgenRandom.nextInt(4) + 2;
        for (n4 = 0; n4 < n; ++n4) {
            n5 = worldgenRandom.nextInt(3) + 2;
            n3 = worldgenRandom.nextInt(64);
            for (n2 = 0; n3 + n2 < 64 && n2 < n5; ++n2) {
                this.clayBands[n3 + n2] = BROWN_TERRACOTTA;
            }
        }
        n4 = worldgenRandom.nextInt(4) + 2;
        for (n5 = 0; n5 < n4; ++n5) {
            n3 = worldgenRandom.nextInt(3) + 1;
            n2 = worldgenRandom.nextInt(64);
            for (n7 = 0; n2 + n7 < 64 && n7 < n3; ++n7) {
                this.clayBands[n2 + n7] = RED_TERRACOTTA;
            }
        }
        n5 = worldgenRandom.nextInt(3) + 3;
        n3 = 0;
        for (n2 = 0; n2 < n5; ++n2) {
            n7 = 1;
            for (int i = 0; (n3 += worldgenRandom.nextInt(16) + 4) + i < 64 && i < 1; ++i) {
                this.clayBands[n3 + i] = WHITE_TERRACOTTA;
                if (n3 + i > 1 && worldgenRandom.nextBoolean()) {
                    this.clayBands[n3 + i - 1] = LIGHT_GRAY_TERRACOTTA;
                }
                if (n3 + i >= 63 || !worldgenRandom.nextBoolean()) continue;
                this.clayBands[n3 + i + 1] = LIGHT_GRAY_TERRACOTTA;
            }
        }
    }

    protected BlockState getBand(int n, int n2, int n3) {
        int n4 = (int)Math.round(this.clayBandsOffsetNoise.getValue((double)n / 512.0, (double)n3 / 512.0, false) * 2.0);
        return this.clayBands[(n2 + n4 + 64) % 64];
    }
}

