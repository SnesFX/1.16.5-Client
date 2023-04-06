/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.serialization.Codec
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.chunk;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.IdMap;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.data.worldgen.StructureFeatures;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkBiomeContainer;
import net.minecraft.world.level.chunk.FeatureAccess;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.StrongholdConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public abstract class ChunkGenerator {
    public static final Codec<ChunkGenerator> CODEC;
    protected final BiomeSource biomeSource;
    protected final BiomeSource runtimeBiomeSource;
    private final StructureSettings settings;
    private final long strongholdSeed;
    private final List<ChunkPos> strongholdPositions = Lists.newArrayList();

    public ChunkGenerator(BiomeSource biomeSource, StructureSettings structureSettings) {
        this(biomeSource, biomeSource, structureSettings, 0L);
    }

    public ChunkGenerator(BiomeSource biomeSource, BiomeSource biomeSource2, StructureSettings structureSettings, long l) {
        this.biomeSource = biomeSource;
        this.runtimeBiomeSource = biomeSource2;
        this.settings = structureSettings;
        this.strongholdSeed = l;
    }

    private void generateStrongholds() {
        if (!this.strongholdPositions.isEmpty()) {
            return;
        }
        StrongholdConfiguration strongholdConfiguration = this.settings.stronghold();
        if (strongholdConfiguration == null || strongholdConfiguration.count() == 0) {
            return;
        }
        ArrayList arrayList = Lists.newArrayList();
        for (Biome biome : this.biomeSource.possibleBiomes()) {
            if (!biome.getGenerationSettings().isValidStart(StructureFeature.STRONGHOLD)) continue;
            arrayList.add(biome);
        }
        int n = strongholdConfiguration.distance();
        int n2 = strongholdConfiguration.count();
        int n3 = strongholdConfiguration.spread();
        Random random = new Random();
        random.setSeed(this.strongholdSeed);
        double d = random.nextDouble() * 3.141592653589793 * 2.0;
        int n4 = 0;
        int n5 = 0;
        for (int i = 0; i < n2; ++i) {
            double d2 = (double)(4 * n + n * n5 * 6) + (random.nextDouble() - 0.5) * ((double)n * 2.5);
            int n6 = (int)Math.round(Math.cos(d) * d2);
            int n7 = (int)Math.round(Math.sin(d) * d2);
            BlockPos blockPos = this.biomeSource.findBiomeHorizontal((n6 << 4) + 8, 0, (n7 << 4) + 8, 112, arrayList::contains, random);
            if (blockPos != null) {
                n6 = blockPos.getX() >> 4;
                n7 = blockPos.getZ() >> 4;
            }
            this.strongholdPositions.add(new ChunkPos(n6, n7));
            d += 6.283185307179586 / (double)n3;
            if (++n4 != n3) continue;
            n4 = 0;
            n3 += 2 * n3 / (++n5 + 1);
            n3 = Math.min(n3, n2 - i);
            d += random.nextDouble() * 3.141592653589793 * 2.0;
        }
    }

    protected abstract Codec<? extends ChunkGenerator> codec();

    public abstract ChunkGenerator withSeed(long var1);

    public void createBiomes(Registry<Biome> registry, ChunkAccess chunkAccess) {
        ChunkPos chunkPos = chunkAccess.getPos();
        ((ProtoChunk)chunkAccess).setBiomes(new ChunkBiomeContainer(registry, chunkPos, this.runtimeBiomeSource));
    }

    public void applyCarvers(long l, BiomeManager biomeManager, ChunkAccess chunkAccess, GenerationStep.Carving carving) {
        BiomeManager biomeManager2 = biomeManager.withDifferentSource(this.biomeSource);
        WorldgenRandom worldgenRandom = new WorldgenRandom();
        int n = 8;
        ChunkPos chunkPos = chunkAccess.getPos();
        int n2 = chunkPos.x;
        int n3 = chunkPos.z;
        BiomeGenerationSettings biomeGenerationSettings = this.biomeSource.getNoiseBiome(chunkPos.x << 2, 0, chunkPos.z << 2).getGenerationSettings();
        BitSet bitSet = ((ProtoChunk)chunkAccess).getOrCreateCarvingMask(carving);
        for (int i = n2 - 8; i <= n2 + 8; ++i) {
            for (int j = n3 - 8; j <= n3 + 8; ++j) {
                List<Supplier<ConfiguredWorldCarver<?>>> list = biomeGenerationSettings.getCarvers(carving);
                ListIterator<Supplier<ConfiguredWorldCarver<?>>> listIterator = list.listIterator();
                while (listIterator.hasNext()) {
                    int n4 = listIterator.nextIndex();
                    ConfiguredWorldCarver<?> configuredWorldCarver = listIterator.next().get();
                    worldgenRandom.setLargeFeatureSeed(l + (long)n4, i, j);
                    if (!configuredWorldCarver.isStartChunk(worldgenRandom, i, j)) continue;
                    configuredWorldCarver.carve(chunkAccess, biomeManager2::getBiome, worldgenRandom, this.getSeaLevel(), i, j, n2, n3, bitSet);
                }
            }
        }
    }

    @Nullable
    public BlockPos findNearestMapFeature(ServerLevel serverLevel, StructureFeature<?> structureFeature, BlockPos blockPos, int n, boolean bl) {
        if (!this.biomeSource.canGenerateStructure(structureFeature)) {
            return null;
        }
        if (structureFeature == StructureFeature.STRONGHOLD) {
            this.generateStrongholds();
            BlockPos blockPos2 = null;
            double d = Double.MAX_VALUE;
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
            for (ChunkPos chunkPos : this.strongholdPositions) {
                mutableBlockPos.set((chunkPos.x << 4) + 8, 32, (chunkPos.z << 4) + 8);
                double d2 = mutableBlockPos.distSqr(blockPos);
                if (blockPos2 == null) {
                    blockPos2 = new BlockPos(mutableBlockPos);
                    d = d2;
                    continue;
                }
                if (!(d2 < d)) continue;
                blockPos2 = new BlockPos(mutableBlockPos);
                d = d2;
            }
            return blockPos2;
        }
        StructureFeatureConfiguration structureFeatureConfiguration = this.settings.getConfig(structureFeature);
        if (structureFeatureConfiguration == null) {
            return null;
        }
        return structureFeature.getNearestGeneratedFeature(serverLevel, serverLevel.structureFeatureManager(), blockPos, n, bl, serverLevel.getSeed(), structureFeatureConfiguration);
    }

    public void applyBiomeDecoration(WorldGenRegion worldGenRegion, StructureFeatureManager structureFeatureManager) {
        int n = worldGenRegion.getCenterX();
        int n2 = worldGenRegion.getCenterZ();
        int n3 = n * 16;
        int n4 = n2 * 16;
        BlockPos blockPos = new BlockPos(n3, 0, n4);
        Biome biome = this.biomeSource.getNoiseBiome((n << 2) + 2, 2, (n2 << 2) + 2);
        WorldgenRandom worldgenRandom = new WorldgenRandom();
        long l = worldgenRandom.setDecorationSeed(worldGenRegion.getSeed(), n3, n4);
        try {
            biome.generate(structureFeatureManager, this, worldGenRegion, l, worldgenRandom, blockPos);
        }
        catch (Exception exception) {
            CrashReport crashReport = CrashReport.forThrowable(exception, "Biome decoration");
            crashReport.addCategory("Generation").setDetail("CenterX", n).setDetail("CenterZ", n2).setDetail("Seed", l).setDetail("Biome", biome);
            throw new ReportedException(crashReport);
        }
    }

    public abstract void buildSurfaceAndBedrock(WorldGenRegion var1, ChunkAccess var2);

    public void spawnOriginalMobs(WorldGenRegion worldGenRegion) {
    }

    public StructureSettings getSettings() {
        return this.settings;
    }

    public int getSpawnHeight() {
        return 64;
    }

    public BiomeSource getBiomeSource() {
        return this.runtimeBiomeSource;
    }

    public int getGenDepth() {
        return 256;
    }

    public List<MobSpawnSettings.SpawnerData> getMobsAt(Biome biome, StructureFeatureManager structureFeatureManager, MobCategory mobCategory, BlockPos blockPos) {
        return biome.getMobSettings().getMobs(mobCategory);
    }

    public void createStructures(RegistryAccess registryAccess, StructureFeatureManager structureFeatureManager, ChunkAccess chunkAccess, StructureManager structureManager, long l) {
        ChunkPos chunkPos = chunkAccess.getPos();
        Biome biome = this.biomeSource.getNoiseBiome((chunkPos.x << 2) + 2, 0, (chunkPos.z << 2) + 2);
        this.createStructure(StructureFeatures.STRONGHOLD, registryAccess, structureFeatureManager, chunkAccess, structureManager, l, chunkPos, biome);
        for (Supplier<ConfiguredStructureFeature<?, ?>> supplier : biome.getGenerationSettings().structures()) {
            this.createStructure(supplier.get(), registryAccess, structureFeatureManager, chunkAccess, structureManager, l, chunkPos, biome);
        }
    }

    private void createStructure(ConfiguredStructureFeature<?, ?> configuredStructureFeature, RegistryAccess registryAccess, StructureFeatureManager structureFeatureManager, ChunkAccess chunkAccess, StructureManager structureManager, long l, ChunkPos chunkPos, Biome biome) {
        StructureStart<?> structureStart = structureFeatureManager.getStartForFeature(SectionPos.of(chunkAccess.getPos(), 0), (StructureFeature<?>)configuredStructureFeature.feature, chunkAccess);
        int n = structureStart != null ? structureStart.getReferences() : 0;
        StructureFeatureConfiguration structureFeatureConfiguration = this.settings.getConfig((StructureFeature<?>)configuredStructureFeature.feature);
        if (structureFeatureConfiguration != null) {
            StructureStart<?> structureStart2 = configuredStructureFeature.generate(registryAccess, this, this.biomeSource, structureManager, l, chunkPos, biome, n, structureFeatureConfiguration);
            structureFeatureManager.setStartForFeature(SectionPos.of(chunkAccess.getPos(), 0), (StructureFeature<?>)configuredStructureFeature.feature, structureStart2, chunkAccess);
        }
    }

    public void createReferences(WorldGenLevel worldGenLevel, StructureFeatureManager structureFeatureManager, ChunkAccess chunkAccess) {
        int n = 8;
        int n2 = chunkAccess.getPos().x;
        int n3 = chunkAccess.getPos().z;
        int n4 = n2 << 4;
        int n5 = n3 << 4;
        SectionPos sectionPos = SectionPos.of(chunkAccess.getPos(), 0);
        for (int i = n2 - 8; i <= n2 + 8; ++i) {
            for (int j = n3 - 8; j <= n3 + 8; ++j) {
                long l = ChunkPos.asLong(i, j);
                for (StructureStart<?> structureStart : worldGenLevel.getChunk(i, j).getAllStarts().values()) {
                    try {
                        if (structureStart == StructureStart.INVALID_START || !structureStart.getBoundingBox().intersects(n4, n5, n4 + 15, n5 + 15)) continue;
                        structureFeatureManager.addReferenceForFeature(sectionPos, structureStart.getFeature(), l, chunkAccess);
                        DebugPackets.sendStructurePacket(worldGenLevel, structureStart);
                    }
                    catch (Exception exception) {
                        CrashReport crashReport = CrashReport.forThrowable(exception, "Generating structure reference");
                        CrashReportCategory crashReportCategory = crashReport.addCategory("Structure");
                        crashReportCategory.setDetail("Id", () -> Registry.STRUCTURE_FEATURE.getKey(structureStart.getFeature()).toString());
                        crashReportCategory.setDetail("Name", () -> structureStart.getFeature().getFeatureName());
                        crashReportCategory.setDetail("Class", () -> structureStart.getFeature().getClass().getCanonicalName());
                        throw new ReportedException(crashReport);
                    }
                }
            }
        }
    }

    public abstract void fillFromNoise(LevelAccessor var1, StructureFeatureManager var2, ChunkAccess var3);

    public int getSeaLevel() {
        return 63;
    }

    public abstract int getBaseHeight(int var1, int var2, Heightmap.Types var3);

    public abstract BlockGetter getBaseColumn(int var1, int var2);

    public int getFirstFreeHeight(int n, int n2, Heightmap.Types types) {
        return this.getBaseHeight(n, n2, types);
    }

    public int getFirstOccupiedHeight(int n, int n2, Heightmap.Types types) {
        return this.getBaseHeight(n, n2, types) - 1;
    }

    public boolean hasStronghold(ChunkPos chunkPos) {
        this.generateStrongholds();
        return this.strongholdPositions.contains(chunkPos);
    }

    static {
        Registry.register(Registry.CHUNK_GENERATOR, "noise", NoiseBasedChunkGenerator.CODEC);
        Registry.register(Registry.CHUNK_GENERATOR, "flat", FlatLevelSource.CODEC);
        Registry.register(Registry.CHUNK_GENERATOR, "debug", DebugLevelSource.CODEC);
        CODEC = Registry.CHUNK_GENERATOR.dispatchStable(ChunkGenerator::codec, Function.identity());
    }
}

