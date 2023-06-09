/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.util.Either
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.chunk;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.storage.WorldData;

public class ChunkStatus {
    private static final EnumSet<Heightmap.Types> PRE_FEATURES = EnumSet.of(Heightmap.Types.OCEAN_FLOOR_WG, Heightmap.Types.WORLD_SURFACE_WG);
    private static final EnumSet<Heightmap.Types> POST_FEATURES = EnumSet.of(Heightmap.Types.OCEAN_FLOOR, Heightmap.Types.WORLD_SURFACE, Heightmap.Types.MOTION_BLOCKING, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES);
    private static final LoadingTask PASSTHROUGH_LOAD_TASK = (chunkStatus, serverLevel, structureManager, threadedLevelLightEngine, function, chunkAccess) -> {
        if (chunkAccess instanceof ProtoChunk && !chunkAccess.getStatus().isOrAfter(chunkStatus)) {
            ((ProtoChunk)chunkAccess).setStatus(chunkStatus);
        }
        return CompletableFuture.completedFuture(Either.left((Object)chunkAccess));
    };
    public static final ChunkStatus EMPTY = ChunkStatus.registerSimple("empty", null, -1, PRE_FEATURES, ChunkType.PROTOCHUNK, (serverLevel, chunkGenerator, list, chunkAccess) -> {});
    public static final ChunkStatus STRUCTURE_STARTS = ChunkStatus.register("structure_starts", EMPTY, 0, PRE_FEATURES, ChunkType.PROTOCHUNK, (chunkStatus, serverLevel, chunkGenerator, structureManager, threadedLevelLightEngine, function, list, chunkAccess) -> {
        if (!chunkAccess.getStatus().isOrAfter(chunkStatus)) {
            if (serverLevel.getServer().getWorldData().worldGenSettings().generateFeatures()) {
                chunkGenerator.createStructures(serverLevel.registryAccess(), serverLevel.structureFeatureManager(), chunkAccess, structureManager, serverLevel.getSeed());
            }
            if (chunkAccess instanceof ProtoChunk) {
                ((ProtoChunk)chunkAccess).setStatus(chunkStatus);
            }
        }
        return CompletableFuture.completedFuture(Either.left((Object)chunkAccess));
    });
    public static final ChunkStatus STRUCTURE_REFERENCES = ChunkStatus.registerSimple("structure_references", STRUCTURE_STARTS, 8, PRE_FEATURES, ChunkType.PROTOCHUNK, (serverLevel, chunkGenerator, list, chunkAccess) -> {
        WorldGenRegion worldGenRegion = new WorldGenRegion(serverLevel, list);
        chunkGenerator.createReferences(worldGenRegion, serverLevel.structureFeatureManager().forWorldGenRegion(worldGenRegion), chunkAccess);
    });
    public static final ChunkStatus BIOMES = ChunkStatus.registerSimple("biomes", STRUCTURE_REFERENCES, 0, PRE_FEATURES, ChunkType.PROTOCHUNK, (serverLevel, chunkGenerator, list, chunkAccess) -> chunkGenerator.createBiomes(serverLevel.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY), chunkAccess));
    public static final ChunkStatus NOISE = ChunkStatus.registerSimple("noise", BIOMES, 8, PRE_FEATURES, ChunkType.PROTOCHUNK, (serverLevel, chunkGenerator, list, chunkAccess) -> {
        WorldGenRegion worldGenRegion = new WorldGenRegion(serverLevel, list);
        chunkGenerator.fillFromNoise(worldGenRegion, serverLevel.structureFeatureManager().forWorldGenRegion(worldGenRegion), chunkAccess);
    });
    public static final ChunkStatus SURFACE = ChunkStatus.registerSimple("surface", NOISE, 0, PRE_FEATURES, ChunkType.PROTOCHUNK, (serverLevel, chunkGenerator, list, chunkAccess) -> chunkGenerator.buildSurfaceAndBedrock(new WorldGenRegion(serverLevel, list), chunkAccess));
    public static final ChunkStatus CARVERS = ChunkStatus.registerSimple("carvers", SURFACE, 0, PRE_FEATURES, ChunkType.PROTOCHUNK, (serverLevel, chunkGenerator, list, chunkAccess) -> chunkGenerator.applyCarvers(serverLevel.getSeed(), serverLevel.getBiomeManager(), chunkAccess, GenerationStep.Carving.AIR));
    public static final ChunkStatus LIQUID_CARVERS = ChunkStatus.registerSimple("liquid_carvers", CARVERS, 0, POST_FEATURES, ChunkType.PROTOCHUNK, (serverLevel, chunkGenerator, list, chunkAccess) -> chunkGenerator.applyCarvers(serverLevel.getSeed(), serverLevel.getBiomeManager(), chunkAccess, GenerationStep.Carving.LIQUID));
    public static final ChunkStatus FEATURES = ChunkStatus.register("features", LIQUID_CARVERS, 8, POST_FEATURES, ChunkType.PROTOCHUNK, (chunkStatus, serverLevel, chunkGenerator, structureManager, threadedLevelLightEngine, function, list, chunkAccess) -> {
        ProtoChunk protoChunk = (ProtoChunk)chunkAccess;
        protoChunk.setLightEngine(threadedLevelLightEngine);
        if (!chunkAccess.getStatus().isOrAfter(chunkStatus)) {
            Heightmap.primeHeightmaps(chunkAccess, EnumSet.of(Heightmap.Types.MOTION_BLOCKING, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Heightmap.Types.OCEAN_FLOOR, Heightmap.Types.WORLD_SURFACE));
            WorldGenRegion worldGenRegion = new WorldGenRegion(serverLevel, list);
            chunkGenerator.applyBiomeDecoration(worldGenRegion, serverLevel.structureFeatureManager().forWorldGenRegion(worldGenRegion));
            protoChunk.setStatus(chunkStatus);
        }
        return CompletableFuture.completedFuture(Either.left((Object)chunkAccess));
    });
    public static final ChunkStatus LIGHT = ChunkStatus.register("light", FEATURES, 1, POST_FEATURES, ChunkType.PROTOCHUNK, (chunkStatus, serverLevel, chunkGenerator, structureManager, threadedLevelLightEngine, function, list, chunkAccess) -> ChunkStatus.lightChunk(chunkStatus, threadedLevelLightEngine, chunkAccess), (chunkStatus, serverLevel, structureManager, threadedLevelLightEngine, function, chunkAccess) -> ChunkStatus.lightChunk(chunkStatus, threadedLevelLightEngine, chunkAccess));
    public static final ChunkStatus SPAWN = ChunkStatus.registerSimple("spawn", LIGHT, 0, POST_FEATURES, ChunkType.PROTOCHUNK, (serverLevel, chunkGenerator, list, chunkAccess) -> chunkGenerator.spawnOriginalMobs(new WorldGenRegion(serverLevel, list)));
    public static final ChunkStatus HEIGHTMAPS = ChunkStatus.registerSimple("heightmaps", SPAWN, 0, POST_FEATURES, ChunkType.PROTOCHUNK, (serverLevel, chunkGenerator, list, chunkAccess) -> {});
    public static final ChunkStatus FULL = ChunkStatus.register("full", HEIGHTMAPS, 0, POST_FEATURES, ChunkType.LEVELCHUNK, (chunkStatus, serverLevel, chunkGenerator, structureManager, threadedLevelLightEngine, function, list, chunkAccess) -> (CompletableFuture)function.apply(chunkAccess), (chunkStatus, serverLevel, structureManager, threadedLevelLightEngine, function, chunkAccess) -> (CompletableFuture)function.apply(chunkAccess));
    private static final List<ChunkStatus> STATUS_BY_RANGE = ImmutableList.of((Object)FULL, (Object)FEATURES, (Object)LIQUID_CARVERS, (Object)STRUCTURE_STARTS, (Object)STRUCTURE_STARTS, (Object)STRUCTURE_STARTS, (Object)STRUCTURE_STARTS, (Object)STRUCTURE_STARTS, (Object)STRUCTURE_STARTS, (Object)STRUCTURE_STARTS, (Object)STRUCTURE_STARTS);
    private static final IntList RANGE_BY_STATUS = (IntList)Util.make(new IntArrayList(ChunkStatus.getStatusList().size()), intArrayList -> {
        int n = 0;
        for (int i = ChunkStatus.getStatusList().size() - 1; i >= 0; --i) {
            while (n + 1 < STATUS_BY_RANGE.size() && i <= STATUS_BY_RANGE.get(n + 1).getIndex()) {
                ++n;
            }
            intArrayList.add(0, n);
        }
    });
    private final String name;
    private final int index;
    private final ChunkStatus parent;
    private final GenerationTask generationTask;
    private final LoadingTask loadingTask;
    private final int range;
    private final ChunkType chunkType;
    private final EnumSet<Heightmap.Types> heightmapsAfter;

    private static CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> lightChunk(ChunkStatus chunkStatus, ThreadedLevelLightEngine threadedLevelLightEngine, ChunkAccess chunkAccess) {
        boolean bl = ChunkStatus.isLighted(chunkStatus, chunkAccess);
        if (!chunkAccess.getStatus().isOrAfter(chunkStatus)) {
            ((ProtoChunk)chunkAccess).setStatus(chunkStatus);
        }
        return threadedLevelLightEngine.lightChunk(chunkAccess, bl).thenApply(Either::left);
    }

    private static ChunkStatus registerSimple(String string, @Nullable ChunkStatus chunkStatus, int n, EnumSet<Heightmap.Types> enumSet, ChunkType chunkType, SimpleGenerationTask simpleGenerationTask) {
        return ChunkStatus.register(string, chunkStatus, n, enumSet, chunkType, simpleGenerationTask);
    }

    private static ChunkStatus register(String string, @Nullable ChunkStatus chunkStatus, int n, EnumSet<Heightmap.Types> enumSet, ChunkType chunkType, GenerationTask generationTask) {
        return ChunkStatus.register(string, chunkStatus, n, enumSet, chunkType, generationTask, PASSTHROUGH_LOAD_TASK);
    }

    private static ChunkStatus register(String string, @Nullable ChunkStatus chunkStatus, int n, EnumSet<Heightmap.Types> enumSet, ChunkType chunkType, GenerationTask generationTask, LoadingTask loadingTask) {
        return Registry.register(Registry.CHUNK_STATUS, string, new ChunkStatus(string, chunkStatus, n, enumSet, chunkType, generationTask, loadingTask));
    }

    public static List<ChunkStatus> getStatusList() {
        ChunkStatus chunkStatus;
        ArrayList arrayList = Lists.newArrayList();
        for (chunkStatus = ChunkStatus.FULL; chunkStatus.getParent() != chunkStatus; chunkStatus = chunkStatus.getParent()) {
            arrayList.add(chunkStatus);
        }
        arrayList.add(chunkStatus);
        Collections.reverse(arrayList);
        return arrayList;
    }

    private static boolean isLighted(ChunkStatus chunkStatus, ChunkAccess chunkAccess) {
        return chunkAccess.getStatus().isOrAfter(chunkStatus) && chunkAccess.isLightCorrect();
    }

    public static ChunkStatus getStatus(int n) {
        if (n >= STATUS_BY_RANGE.size()) {
            return EMPTY;
        }
        if (n < 0) {
            return FULL;
        }
        return STATUS_BY_RANGE.get(n);
    }

    public static int maxDistance() {
        return STATUS_BY_RANGE.size();
    }

    public static int getDistance(ChunkStatus chunkStatus) {
        return RANGE_BY_STATUS.getInt(chunkStatus.getIndex());
    }

    ChunkStatus(String string, @Nullable ChunkStatus chunkStatus, int n, EnumSet<Heightmap.Types> enumSet, ChunkType chunkType, GenerationTask generationTask, LoadingTask loadingTask) {
        this.name = string;
        this.parent = chunkStatus == null ? this : chunkStatus;
        this.generationTask = generationTask;
        this.loadingTask = loadingTask;
        this.range = n;
        this.chunkType = chunkType;
        this.heightmapsAfter = enumSet;
        this.index = chunkStatus == null ? 0 : chunkStatus.getIndex() + 1;
    }

    public int getIndex() {
        return this.index;
    }

    public String getName() {
        return this.name;
    }

    public ChunkStatus getParent() {
        return this.parent;
    }

    public CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> generate(ServerLevel serverLevel, ChunkGenerator chunkGenerator, StructureManager structureManager, ThreadedLevelLightEngine threadedLevelLightEngine, Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> function, List<ChunkAccess> list) {
        return this.generationTask.doWork(this, serverLevel, chunkGenerator, structureManager, threadedLevelLightEngine, function, list, list.get(list.size() / 2));
    }

    public CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> load(ServerLevel serverLevel, StructureManager structureManager, ThreadedLevelLightEngine threadedLevelLightEngine, Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> function, ChunkAccess chunkAccess) {
        return this.loadingTask.doWork(this, serverLevel, structureManager, threadedLevelLightEngine, function, chunkAccess);
    }

    public int getRange() {
        return this.range;
    }

    public ChunkType getChunkType() {
        return this.chunkType;
    }

    public static ChunkStatus byName(String string) {
        return Registry.CHUNK_STATUS.get(ResourceLocation.tryParse(string));
    }

    public EnumSet<Heightmap.Types> heightmapsAfter() {
        return this.heightmapsAfter;
    }

    public boolean isOrAfter(ChunkStatus chunkStatus) {
        return this.getIndex() >= chunkStatus.getIndex();
    }

    public String toString() {
        return Registry.CHUNK_STATUS.getKey(this).toString();
    }

    public static enum ChunkType {
        PROTOCHUNK,
        LEVELCHUNK;
        
    }

    static interface SimpleGenerationTask
    extends GenerationTask {
        @Override
        default public CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> doWork(ChunkStatus chunkStatus, ServerLevel serverLevel, ChunkGenerator chunkGenerator, StructureManager structureManager, ThreadedLevelLightEngine threadedLevelLightEngine, Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> function, List<ChunkAccess> list, ChunkAccess chunkAccess) {
            if (!chunkAccess.getStatus().isOrAfter(chunkStatus)) {
                this.doWork(serverLevel, chunkGenerator, list, chunkAccess);
                if (chunkAccess instanceof ProtoChunk) {
                    ((ProtoChunk)chunkAccess).setStatus(chunkStatus);
                }
            }
            return CompletableFuture.completedFuture(Either.left((Object)chunkAccess));
        }

        public void doWork(ServerLevel var1, ChunkGenerator var2, List<ChunkAccess> var3, ChunkAccess var4);
    }

    static interface LoadingTask {
        public CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> doWork(ChunkStatus var1, ServerLevel var2, StructureManager var3, ThreadedLevelLightEngine var4, Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> var5, ChunkAccess var6);
    }

    static interface GenerationTask {
        public CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> doWork(ChunkStatus var1, ServerLevel var2, ChunkGenerator var3, StructureManager var4, ThreadedLevelLightEngine var5, Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> var6, List<ChunkAccess> var7, ChunkAccess var8);
    }

}

