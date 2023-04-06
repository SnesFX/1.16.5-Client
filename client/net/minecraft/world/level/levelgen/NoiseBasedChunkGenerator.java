/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Products
 *  com.mojang.datafixers.Products$P3
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Function3
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.PrimitiveCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectList
 *  it.unimi.dsi.fastutil.objects.ObjectListIterator
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.levelgen;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.biome.TheEndBiomeSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseSamplingSettings;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.NoiseSlideSettings;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.SwamplandHutFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.structures.JigsawJunction;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import net.minecraft.world.level.levelgen.synth.SurfaceNoise;

public final class NoiseBasedChunkGenerator
extends ChunkGenerator {
    public static final Codec<NoiseBasedChunkGenerator> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)BiomeSource.CODEC.fieldOf("biome_source").forGetter(noiseBasedChunkGenerator -> noiseBasedChunkGenerator.biomeSource), (App)Codec.LONG.fieldOf("seed").stable().forGetter(noiseBasedChunkGenerator -> noiseBasedChunkGenerator.seed), (App)NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(noiseBasedChunkGenerator -> noiseBasedChunkGenerator.settings)).apply((Applicative)instance, instance.stable((Object)((Function3)(arg_0, arg_1, arg_2) -> NoiseBasedChunkGenerator.new(arg_0, arg_1, arg_2)))));
    private static final float[] BEARD_KERNEL = Util.make(new float[13824], arrf -> {
        for (int i = 0; i < 24; ++i) {
            for (int j = 0; j < 24; ++j) {
                for (int k = 0; k < 24; ++k) {
                    arrf[i * 24 * 24 + j * 24 + k] = (float)NoiseBasedChunkGenerator.computeContribution(j - 12, k - 12, i - 12);
                }
            }
        }
    });
    private static final float[] BIOME_WEIGHTS = Util.make(new float[25], arrf -> {
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                float f;
                arrf[i + 2 + (j + 2) * 5] = f = 10.0f / Mth.sqrt((float)(i * i + j * j) + 0.2f);
            }
        }
    });
    private static final BlockState AIR = Blocks.AIR.defaultBlockState();
    private final int chunkHeight;
    private final int chunkWidth;
    private final int chunkCountX;
    private final int chunkCountY;
    private final int chunkCountZ;
    protected final WorldgenRandom random;
    private final PerlinNoise minLimitPerlinNoise;
    private final PerlinNoise maxLimitPerlinNoise;
    private final PerlinNoise mainPerlinNoise;
    private final SurfaceNoise surfaceNoise;
    private final PerlinNoise depthNoise;
    @Nullable
    private final SimplexNoise islandNoise;
    protected final BlockState defaultBlock;
    protected final BlockState defaultFluid;
    private final long seed;
    protected final Supplier<NoiseGeneratorSettings> settings;
    private final int height;

    public NoiseBasedChunkGenerator(BiomeSource biomeSource, long l, Supplier<NoiseGeneratorSettings> supplier) {
        this(biomeSource, biomeSource, l, supplier);
    }

    private NoiseBasedChunkGenerator(BiomeSource biomeSource, BiomeSource biomeSource2, long l, Supplier<NoiseGeneratorSettings> supplier) {
        super(biomeSource, biomeSource2, supplier.get().structureSettings(), l);
        this.seed = l;
        NoiseGeneratorSettings noiseGeneratorSettings = supplier.get();
        this.settings = supplier;
        NoiseSettings noiseSettings = noiseGeneratorSettings.noiseSettings();
        this.height = noiseSettings.height();
        this.chunkHeight = noiseSettings.noiseSizeVertical() * 4;
        this.chunkWidth = noiseSettings.noiseSizeHorizontal() * 4;
        this.defaultBlock = noiseGeneratorSettings.getDefaultBlock();
        this.defaultFluid = noiseGeneratorSettings.getDefaultFluid();
        this.chunkCountX = 16 / this.chunkWidth;
        this.chunkCountY = noiseSettings.height() / this.chunkHeight;
        this.chunkCountZ = 16 / this.chunkWidth;
        this.random = new WorldgenRandom(l);
        this.minLimitPerlinNoise = new PerlinNoise(this.random, IntStream.rangeClosed(-15, 0));
        this.maxLimitPerlinNoise = new PerlinNoise(this.random, IntStream.rangeClosed(-15, 0));
        this.mainPerlinNoise = new PerlinNoise(this.random, IntStream.rangeClosed(-7, 0));
        this.surfaceNoise = noiseSettings.useSimplexSurfaceNoise() ? new PerlinSimplexNoise(this.random, IntStream.rangeClosed(-3, 0)) : new PerlinNoise(this.random, IntStream.rangeClosed(-3, 0));
        this.random.consumeCount(2620);
        this.depthNoise = new PerlinNoise(this.random, IntStream.rangeClosed(-15, 0));
        if (noiseSettings.islandNoiseOverride()) {
            WorldgenRandom worldgenRandom = new WorldgenRandom(l);
            worldgenRandom.consumeCount(17292);
            this.islandNoise = new SimplexNoise(worldgenRandom);
        } else {
            this.islandNoise = null;
        }
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public ChunkGenerator withSeed(long l) {
        return new NoiseBasedChunkGenerator(this.biomeSource.withSeed(l), l, this.settings);
    }

    public boolean stable(long l, ResourceKey<NoiseGeneratorSettings> resourceKey) {
        return this.seed == l && this.settings.get().stable(resourceKey);
    }

    private double sampleAndClampNoise(int n, int n2, int n3, double d, double d2, double d3, double d4) {
        double d5 = 0.0;
        double d6 = 0.0;
        double d7 = 0.0;
        boolean bl = true;
        double d8 = 1.0;
        for (int i = 0; i < 16; ++i) {
            ImprovedNoise improvedNoise;
            ImprovedNoise improvedNoise2;
            double d9 = PerlinNoise.wrap((double)n * d * d8);
            double d10 = PerlinNoise.wrap((double)n2 * d2 * d8);
            double d11 = PerlinNoise.wrap((double)n3 * d * d8);
            double d12 = d2 * d8;
            ImprovedNoise improvedNoise3 = this.minLimitPerlinNoise.getOctaveNoise(i);
            if (improvedNoise3 != null) {
                d5 += improvedNoise3.noise(d9, d10, d11, d12, (double)n2 * d12) / d8;
            }
            if ((improvedNoise = this.maxLimitPerlinNoise.getOctaveNoise(i)) != null) {
                d6 += improvedNoise.noise(d9, d10, d11, d12, (double)n2 * d12) / d8;
            }
            if (i < 8 && (improvedNoise2 = this.mainPerlinNoise.getOctaveNoise(i)) != null) {
                d7 += improvedNoise2.noise(PerlinNoise.wrap((double)n * d3 * d8), PerlinNoise.wrap((double)n2 * d4 * d8), PerlinNoise.wrap((double)n3 * d3 * d8), d4 * d8, (double)n2 * d4 * d8) / d8;
            }
            d8 /= 2.0;
        }
        return Mth.clampedLerp(d5 / 512.0, d6 / 512.0, (d7 / 10.0 + 1.0) / 2.0);
    }

    private double[] makeAndFillNoiseColumn(int n, int n2) {
        double[] arrd = new double[this.chunkCountY + 1];
        this.fillNoiseColumn(arrd, n, n2);
        return arrd;
    }

    private void fillNoiseColumn(double[] arrd, int n, int n2) {
        double d;
        double d2;
        double d3;
        double d4;
        NoiseSettings noiseSettings = this.settings.get().noiseSettings();
        if (this.islandNoise != null) {
            d = TheEndBiomeSource.getHeightValue(this.islandNoise, n, n2) - 8.0f;
            d4 = d > 0.0 ? 0.25 : 1.0;
        } else {
            float f = 0.0f;
            float f2 = 0.0f;
            float f3 = 0.0f;
            int n3 = 2;
            int n4 = this.getSeaLevel();
            float f4 = this.biomeSource.getNoiseBiome(n, n4, n2).getDepth();
            for (int i = -2; i <= 2; ++i) {
                for (int j = -2; j <= 2; ++j) {
                    float f5;
                    float f6;
                    Biome biome = this.biomeSource.getNoiseBiome(n + i, n4, n2 + j);
                    float f7 = biome.getDepth();
                    float f8 = biome.getScale();
                    if (noiseSettings.isAmplified() && f7 > 0.0f) {
                        f6 = 1.0f + f7 * 2.0f;
                        f5 = 1.0f + f8 * 4.0f;
                    } else {
                        f6 = f7;
                        f5 = f8;
                    }
                    float f9 = f7 > f4 ? 0.5f : 1.0f;
                    float f10 = f9 * BIOME_WEIGHTS[i + 2 + (j + 2) * 5] / (f6 + 2.0f);
                    f += f5 * f10;
                    f2 += f6 * f10;
                    f3 += f10;
                }
            }
            float f11 = f2 / f3;
            float f12 = f / f3;
            d2 = f11 * 0.5f - 0.125f;
            d3 = f12 * 0.9f + 0.1f;
            d = d2 * 0.265625;
            d4 = 96.0 / d3;
        }
        double d5 = 684.412 * noiseSettings.noiseSamplingSettings().xzScale();
        double d6 = 684.412 * noiseSettings.noiseSamplingSettings().yScale();
        double d7 = d5 / noiseSettings.noiseSamplingSettings().xzFactor();
        double d8 = d6 / noiseSettings.noiseSamplingSettings().yFactor();
        d2 = noiseSettings.topSlideSettings().target();
        d3 = noiseSettings.topSlideSettings().size();
        double d9 = noiseSettings.topSlideSettings().offset();
        double d10 = noiseSettings.bottomSlideSettings().target();
        double d11 = noiseSettings.bottomSlideSettings().size();
        double d12 = noiseSettings.bottomSlideSettings().offset();
        double d13 = noiseSettings.randomDensityOffset() ? this.getRandomDensity(n, n2) : 0.0;
        double d14 = noiseSettings.densityFactor();
        double d15 = noiseSettings.densityOffset();
        for (int i = 0; i <= this.chunkCountY; ++i) {
            double d16;
            double d17 = this.sampleAndClampNoise(n, i, n2, d5, d6, d7, d8);
            double d18 = 1.0 - (double)i * 2.0 / (double)this.chunkCountY + d13;
            double d19 = d18 * d14 + d15;
            double d20 = (d19 + d) * d4;
            d17 = d20 > 0.0 ? (d17 += d20 * 4.0) : (d17 += d20);
            if (d3 > 0.0) {
                d16 = ((double)(this.chunkCountY - i) - d9) / d3;
                d17 = Mth.clampedLerp(d2, d17, d16);
            }
            if (d11 > 0.0) {
                d16 = ((double)i - d12) / d11;
                d17 = Mth.clampedLerp(d10, d17, d16);
            }
            arrd[i] = d17;
        }
    }

    private double getRandomDensity(int n, int n2) {
        double d = this.depthNoise.getValue(n * 200, 10.0, n2 * 200, 1.0, 0.0, true);
        double d2 = d < 0.0 ? -d * 0.3 : d;
        double d3 = d2 * 24.575625 - 2.0;
        if (d3 < 0.0) {
            return d3 * 0.009486607142857142;
        }
        return Math.min(d3, 1.0) * 0.006640625;
    }

    @Override
    public int getBaseHeight(int n, int n2, Heightmap.Types types) {
        return this.iterateNoiseColumn(n, n2, null, types.isOpaque());
    }

    @Override
    public BlockGetter getBaseColumn(int n, int n2) {
        BlockState[] arrblockState = new BlockState[this.chunkCountY * this.chunkHeight];
        this.iterateNoiseColumn(n, n2, arrblockState, null);
        return new NoiseColumn(arrblockState);
    }

    private int iterateNoiseColumn(int n, int n2, @Nullable BlockState[] arrblockState, @Nullable Predicate<BlockState> predicate) {
        int n3 = Math.floorDiv(n, this.chunkWidth);
        int n4 = Math.floorDiv(n2, this.chunkWidth);
        int n5 = Math.floorMod(n, this.chunkWidth);
        int n6 = Math.floorMod(n2, this.chunkWidth);
        double d = (double)n5 / (double)this.chunkWidth;
        double d2 = (double)n6 / (double)this.chunkWidth;
        double[][] arrarrd = new double[][]{this.makeAndFillNoiseColumn(n3, n4), this.makeAndFillNoiseColumn(n3, n4 + 1), this.makeAndFillNoiseColumn(n3 + 1, n4), this.makeAndFillNoiseColumn(n3 + 1, n4 + 1)};
        for (int i = this.chunkCountY - 1; i >= 0; --i) {
            double d3 = arrarrd[0][i];
            double d4 = arrarrd[1][i];
            double d5 = arrarrd[2][i];
            double d6 = arrarrd[3][i];
            double d7 = arrarrd[0][i + 1];
            double d8 = arrarrd[1][i + 1];
            double d9 = arrarrd[2][i + 1];
            double d10 = arrarrd[3][i + 1];
            for (int j = this.chunkHeight - 1; j >= 0; --j) {
                double d11 = (double)j / (double)this.chunkHeight;
                double d12 = Mth.lerp3(d11, d, d2, d3, d7, d5, d9, d4, d8, d6, d10);
                int n7 = i * this.chunkHeight + j;
                BlockState blockState = this.generateBaseState(d12, n7);
                if (arrblockState != null) {
                    arrblockState[n7] = blockState;
                }
                if (predicate == null || !predicate.test(blockState)) continue;
                return n7 + 1;
            }
        }
        return 0;
    }

    protected BlockState generateBaseState(double d, int n) {
        BlockState blockState = d > 0.0 ? this.defaultBlock : (n < this.getSeaLevel() ? this.defaultFluid : AIR);
        return blockState;
    }

    @Override
    public void buildSurfaceAndBedrock(WorldGenRegion worldGenRegion, ChunkAccess chunkAccess) {
        ChunkPos chunkPos = chunkAccess.getPos();
        int n = chunkPos.x;
        int n2 = chunkPos.z;
        WorldgenRandom worldgenRandom = new WorldgenRandom();
        worldgenRandom.setBaseChunkSeed(n, n2);
        ChunkPos chunkPos2 = chunkAccess.getPos();
        int n3 = chunkPos2.getMinBlockX();
        int n4 = chunkPos2.getMinBlockZ();
        double d = 0.0625;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                int n5 = n3 + i;
                int n6 = n4 + j;
                int n7 = chunkAccess.getHeight(Heightmap.Types.WORLD_SURFACE_WG, i, j) + 1;
                double d2 = this.surfaceNoise.getSurfaceNoiseValue((double)n5 * 0.0625, (double)n6 * 0.0625, 0.0625, (double)i * 0.0625) * 15.0;
                worldGenRegion.getBiome(mutableBlockPos.set(n3 + i, n7, n4 + j)).buildSurfaceAt(worldgenRandom, chunkAccess, n5, n6, n7, d2, this.defaultBlock, this.defaultFluid, this.getSeaLevel(), worldGenRegion.getSeed());
            }
        }
        this.setBedrock(chunkAccess, worldgenRandom);
    }

    private void setBedrock(ChunkAccess chunkAccess, Random random) {
        boolean bl;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        int n = chunkAccess.getPos().getMinBlockX();
        int n2 = chunkAccess.getPos().getMinBlockZ();
        NoiseGeneratorSettings noiseGeneratorSettings = this.settings.get();
        int n3 = noiseGeneratorSettings.getBedrockFloorPosition();
        int n4 = this.height - 1 - noiseGeneratorSettings.getBedrockRoofPosition();
        int n5 = 5;
        boolean bl2 = n4 + 4 >= 0 && n4 < this.height;
        boolean bl3 = bl = n3 + 4 >= 0 && n3 < this.height;
        if (!bl2 && !bl) {
            return;
        }
        for (BlockPos blockPos : BlockPos.betweenClosed(n, 0, n2, n + 15, 0, n2 + 15)) {
            int n6;
            if (bl2) {
                for (n6 = 0; n6 < 5; ++n6) {
                    if (n6 > random.nextInt(5)) continue;
                    chunkAccess.setBlockState(mutableBlockPos.set(blockPos.getX(), n4 - n6, blockPos.getZ()), Blocks.BEDROCK.defaultBlockState(), false);
                }
            }
            if (!bl) continue;
            for (n6 = 4; n6 >= 0; --n6) {
                if (n6 > random.nextInt(5)) continue;
                chunkAccess.setBlockState(mutableBlockPos.set(blockPos.getX(), n3 + n6, blockPos.getZ()), Blocks.BEDROCK.defaultBlockState(), false);
            }
        }
    }

    @Override
    public void fillFromNoise(LevelAccessor levelAccessor, StructureFeatureManager structureFeatureManager, ChunkAccess chunkAccess) {
        ObjectArrayList objectArrayList = new ObjectArrayList(10);
        ObjectArrayList objectArrayList2 = new ObjectArrayList(32);
        ChunkPos chunkPos = chunkAccess.getPos();
        int n = chunkPos.x;
        int n2 = chunkPos.z;
        int n3 = n << 4;
        int n4 = n2 << 4;
        for (StructureFeature<?> structureFeature : StructureFeature.NOISE_AFFECTING_FEATURES) {
            structureFeatureManager.startsForFeature(SectionPos.of(chunkPos, 0), structureFeature).forEach(arg_0 -> NoiseBasedChunkGenerator.lambda$fillFromNoise$6(chunkPos, (ObjectList)objectArrayList, n3, n4, (ObjectList)objectArrayList2, arg_0));
        }
        double[][][] arrd = new double[2][this.chunkCountZ + 1][this.chunkCountY + 1];
        for (int i = 0; i < this.chunkCountZ + 1; ++i) {
            arrd[0][i] = new double[this.chunkCountY + 1];
            this.fillNoiseColumn(arrd[0][i], n * this.chunkCountX, n2 * this.chunkCountZ + i);
            arrd[1][i] = new double[this.chunkCountY + 1];
        }
        ProtoChunk protoChunk = (ProtoChunk)chunkAccess;
        Heightmap heightmap = protoChunk.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
        Heightmap heightmap2 = protoChunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        ObjectListIterator objectListIterator = objectArrayList.iterator();
        ObjectListIterator objectListIterator2 = objectArrayList2.iterator();
        for (int i = 0; i < this.chunkCountX; ++i) {
            int n5;
            for (n5 = 0; n5 < this.chunkCountZ + 1; ++n5) {
                this.fillNoiseColumn(arrd[1][n5], n * this.chunkCountX + i + 1, n2 * this.chunkCountZ + n5);
            }
            for (n5 = 0; n5 < this.chunkCountZ; ++n5) {
                LevelChunkSection levelChunkSection = protoChunk.getOrCreateSection(15);
                levelChunkSection.acquire();
                for (int j = this.chunkCountY - 1; j >= 0; --j) {
                    double d = arrd[0][n5][j];
                    double d2 = arrd[0][n5 + 1][j];
                    double d3 = arrd[1][n5][j];
                    double d4 = arrd[1][n5 + 1][j];
                    double d5 = arrd[0][n5][j + 1];
                    double d6 = arrd[0][n5 + 1][j + 1];
                    double d7 = arrd[1][n5][j + 1];
                    double d8 = arrd[1][n5 + 1][j + 1];
                    for (int k = this.chunkHeight - 1; k >= 0; --k) {
                        int n6 = j * this.chunkHeight + k;
                        int n7 = n6 & 0xF;
                        int n8 = n6 >> 4;
                        if (levelChunkSection.bottomBlockY() >> 4 != n8) {
                            levelChunkSection.release();
                            levelChunkSection = protoChunk.getOrCreateSection(n8);
                            levelChunkSection.acquire();
                        }
                        double d9 = (double)k / (double)this.chunkHeight;
                        double d10 = Mth.lerp(d9, d, d5);
                        double d11 = Mth.lerp(d9, d3, d7);
                        double d12 = Mth.lerp(d9, d2, d6);
                        double d13 = Mth.lerp(d9, d4, d8);
                        for (int i2 = 0; i2 < this.chunkWidth; ++i2) {
                            int n9 = n3 + i * this.chunkWidth + i2;
                            int n10 = n9 & 0xF;
                            double d14 = (double)i2 / (double)this.chunkWidth;
                            double d15 = Mth.lerp(d14, d10, d11);
                            double d16 = Mth.lerp(d14, d12, d13);
                            for (int i3 = 0; i3 < this.chunkWidth; ++i3) {
                                Object object;
                                int n11;
                                int n12;
                                int n13 = n4 + n5 * this.chunkWidth + i3;
                                int n14 = n13 & 0xF;
                                double d17 = (double)i3 / (double)this.chunkWidth;
                                double d18 = Mth.lerp(d17, d15, d16);
                                double d19 = Mth.clamp(d18 / 200.0, -1.0, 1.0);
                                d19 = d19 / 2.0 - d19 * d19 * d19 / 24.0;
                                while (objectListIterator.hasNext()) {
                                    object = (StructurePiece)objectListIterator.next();
                                    BoundingBox boundingBox = ((StructurePiece)object).getBoundingBox();
                                    n12 = Math.max(0, Math.max(boundingBox.x0 - n9, n9 - boundingBox.x1));
                                    n11 = n6 - (boundingBox.y0 + (object instanceof PoolElementStructurePiece ? ((PoolElementStructurePiece)object).getGroundLevelDelta() : 0));
                                    int n15 = Math.max(0, Math.max(boundingBox.z0 - n13, n13 - boundingBox.z1));
                                    d19 += NoiseBasedChunkGenerator.getContribution(n12, n11, n15) * 0.8;
                                }
                                objectListIterator.back(objectArrayList.size());
                                while (objectListIterator2.hasNext()) {
                                    object = (JigsawJunction)objectListIterator2.next();
                                    int n16 = n9 - ((JigsawJunction)object).getSourceX();
                                    n12 = n6 - ((JigsawJunction)object).getSourceGroundY();
                                    n11 = n13 - ((JigsawJunction)object).getSourceZ();
                                    d19 += NoiseBasedChunkGenerator.getContribution(n16, n12, n11) * 0.4;
                                }
                                objectListIterator2.back(objectArrayList2.size());
                                object = this.generateBaseState(d19, n6);
                                if (object == AIR) continue;
                                if (((BlockBehaviour.BlockStateBase)object).getLightEmission() != 0) {
                                    mutableBlockPos.set(n9, n6, n13);
                                    protoChunk.addLight(mutableBlockPos);
                                }
                                levelChunkSection.setBlockState(n10, n7, n14, (BlockState)object, false);
                                heightmap.update(n10, n6, n14, (BlockState)object);
                                heightmap2.update(n10, n6, n14, (BlockState)object);
                            }
                        }
                    }
                }
                levelChunkSection.release();
            }
            double[][] arrd2 = arrd[0];
            arrd[0] = arrd[1];
            arrd[1] = arrd2;
        }
    }

    private static double getContribution(int n, int n2, int n3) {
        int n4 = n + 12;
        int n5 = n2 + 12;
        int n6 = n3 + 12;
        if (n4 < 0 || n4 >= 24) {
            return 0.0;
        }
        if (n5 < 0 || n5 >= 24) {
            return 0.0;
        }
        if (n6 < 0 || n6 >= 24) {
            return 0.0;
        }
        return BEARD_KERNEL[n6 * 24 * 24 + n4 * 24 + n5];
    }

    private static double computeContribution(int n, int n2, int n3) {
        double d = n * n + n3 * n3;
        double d2 = (double)n2 + 0.5;
        double d3 = d2 * d2;
        double d4 = Math.pow(2.718281828459045, -(d3 / 16.0 + d / 16.0));
        double d5 = -d2 * Mth.fastInvSqrt(d3 / 2.0 + d / 2.0) / 2.0;
        return d5 * d4;
    }

    @Override
    public int getGenDepth() {
        return this.height;
    }

    @Override
    public int getSeaLevel() {
        return this.settings.get().seaLevel();
    }

    @Override
    public List<MobSpawnSettings.SpawnerData> getMobsAt(Biome biome, StructureFeatureManager structureFeatureManager, MobCategory mobCategory, BlockPos blockPos) {
        if (structureFeatureManager.getStructureAt(blockPos, true, StructureFeature.SWAMP_HUT).isValid()) {
            if (mobCategory == MobCategory.MONSTER) {
                return StructureFeature.SWAMP_HUT.getSpecialEnemies();
            }
            if (mobCategory == MobCategory.CREATURE) {
                return StructureFeature.SWAMP_HUT.getSpecialAnimals();
            }
        }
        if (mobCategory == MobCategory.MONSTER) {
            if (structureFeatureManager.getStructureAt(blockPos, false, StructureFeature.PILLAGER_OUTPOST).isValid()) {
                return StructureFeature.PILLAGER_OUTPOST.getSpecialEnemies();
            }
            if (structureFeatureManager.getStructureAt(blockPos, false, StructureFeature.OCEAN_MONUMENT).isValid()) {
                return StructureFeature.OCEAN_MONUMENT.getSpecialEnemies();
            }
            if (structureFeatureManager.getStructureAt(blockPos, true, StructureFeature.NETHER_BRIDGE).isValid()) {
                return StructureFeature.NETHER_BRIDGE.getSpecialEnemies();
            }
        }
        return super.getMobsAt(biome, structureFeatureManager, mobCategory, blockPos);
    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion worldGenRegion) {
        if (this.settings.get().disableMobGeneration()) {
            return;
        }
        int n = worldGenRegion.getCenterX();
        int n2 = worldGenRegion.getCenterZ();
        Biome biome = worldGenRegion.getBiome(new ChunkPos(n, n2).getWorldPosition());
        WorldgenRandom worldgenRandom = new WorldgenRandom();
        worldgenRandom.setDecorationSeed(worldGenRegion.getSeed(), n << 4, n2 << 4);
        NaturalSpawner.spawnMobsForChunkGeneration(worldGenRegion, biome, n, n2, worldgenRandom);
    }

    private static /* synthetic */ void lambda$fillFromNoise$6(ChunkPos chunkPos, ObjectList objectList, int n, int n2, ObjectList objectList2, StructureStart structureStart) {
        for (StructurePiece structurePiece : structureStart.getPieces()) {
            if (!structurePiece.isCloseToChunk(chunkPos, 12)) continue;
            if (structurePiece instanceof PoolElementStructurePiece) {
                PoolElementStructurePiece poolElementStructurePiece = (PoolElementStructurePiece)structurePiece;
                StructureTemplatePool.Projection projection = poolElementStructurePiece.getElement().getProjection();
                if (projection == StructureTemplatePool.Projection.RIGID) {
                    objectList.add((Object)poolElementStructurePiece);
                }
                for (JigsawJunction jigsawJunction : poolElementStructurePiece.getJunctions()) {
                    int n3 = jigsawJunction.getSourceX();
                    int n4 = jigsawJunction.getSourceZ();
                    if (n3 <= n - 12 || n4 <= n2 - 12 || n3 >= n + 15 + 12 || n4 >= n2 + 15 + 12) continue;
                    objectList2.add((Object)jigsawJunction);
                }
                continue;
            }
            objectList.add((Object)structurePiece);
        }
    }
}

