/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.serialization.DynamicOps
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.level.storage;

import com.google.common.collect.Lists;
import com.mojang.serialization.DynamicOps;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.FilterInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryReadOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.biome.OverworldBiomeSource;
import net.minecraft.world.level.chunk.storage.OldChunkStorage;
import net.minecraft.world.level.chunk.storage.RegionFile;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class McRegionUpgrader {
    private static final Logger LOGGER = LogManager.getLogger();

    static boolean convertLevel(LevelStorageSource.LevelStorageAccess levelStorageAccess, ProgressListener progressListener) {
        progressListener.progressStagePercentage(0);
        ArrayList arrayList = Lists.newArrayList();
        ArrayList arrayList2 = Lists.newArrayList();
        ArrayList arrayList3 = Lists.newArrayList();
        File file = levelStorageAccess.getDimensionPath(Level.OVERWORLD);
        File file2 = levelStorageAccess.getDimensionPath(Level.NETHER);
        File file3 = levelStorageAccess.getDimensionPath(Level.END);
        LOGGER.info("Scanning folders...");
        McRegionUpgrader.addRegionFiles(file, arrayList);
        if (file2.exists()) {
            McRegionUpgrader.addRegionFiles(file2, arrayList2);
        }
        if (file3.exists()) {
            McRegionUpgrader.addRegionFiles(file3, arrayList3);
        }
        int n = arrayList.size() + arrayList2.size() + arrayList3.size();
        LOGGER.info("Total conversion count is {}", (Object)n);
        RegistryAccess.RegistryHolder registryHolder = RegistryAccess.builtin();
        RegistryReadOps<Tag> registryReadOps = RegistryReadOps.create(NbtOps.INSTANCE, ResourceManager.Empty.INSTANCE, registryHolder);
        WorldData worldData = levelStorageAccess.getDataTag(registryReadOps, DataPackConfig.DEFAULT);
        long l = worldData != null ? worldData.worldGenSettings().seed() : 0L;
        WritableRegistry<Biome> writableRegistry = registryHolder.registryOrThrow(Registry.BIOME_REGISTRY);
        BiomeSource biomeSource = worldData != null && worldData.worldGenSettings().isFlatWorld() ? new FixedBiomeSource(writableRegistry.getOrThrow(Biomes.PLAINS)) : new OverworldBiomeSource(l, false, false, writableRegistry);
        McRegionUpgrader.convertRegions(registryHolder, new File(file, "region"), arrayList, biomeSource, 0, n, progressListener);
        McRegionUpgrader.convertRegions(registryHolder, new File(file2, "region"), arrayList2, new FixedBiomeSource(writableRegistry.getOrThrow(Biomes.NETHER_WASTES)), arrayList.size(), n, progressListener);
        McRegionUpgrader.convertRegions(registryHolder, new File(file3, "region"), arrayList3, new FixedBiomeSource(writableRegistry.getOrThrow(Biomes.THE_END)), arrayList.size() + arrayList2.size(), n, progressListener);
        McRegionUpgrader.makeMcrLevelDatBackup(levelStorageAccess);
        levelStorageAccess.saveDataTag(registryHolder, worldData);
        return true;
    }

    private static void makeMcrLevelDatBackup(LevelStorageSource.LevelStorageAccess levelStorageAccess) {
        File file = levelStorageAccess.getLevelPath(LevelResource.LEVEL_DATA_FILE).toFile();
        if (!file.exists()) {
            LOGGER.warn("Unable to create level.dat_mcr backup");
            return;
        }
        File file2 = new File(file.getParent(), "level.dat_mcr");
        if (!file.renameTo(file2)) {
            LOGGER.warn("Unable to create level.dat_mcr backup");
        }
    }

    private static void convertRegions(RegistryAccess.RegistryHolder registryHolder, File file, Iterable<File> iterable, BiomeSource biomeSource, int n, int n2, ProgressListener progressListener) {
        for (File file2 : iterable) {
            McRegionUpgrader.convertRegion(registryHolder, file, file2, biomeSource, n, n2, progressListener);
            int n3 = (int)Math.round(100.0 * (double)(++n) / (double)n2);
            progressListener.progressStagePercentage(n3);
        }
    }

    private static void convertRegion(RegistryAccess.RegistryHolder registryHolder, File file, File file2, BiomeSource biomeSource, int n, int n2, ProgressListener progressListener) {
        String string = file2.getName();
        try {
            try (RegionFile regionFile = new RegionFile(file2, file, true);
                 RegionFile regionFile2 = new RegionFile(new File(file, string.substring(0, string.length() - ".mcr".length()) + ".mca"), file, true);){
                for (int i = 0; i < 32; ++i) {
                    int n3;
                    for (n3 = 0; n3 < 32; ++n3) {
                        Object object;
                        CompoundTag compoundTag;
                        Object object2;
                        ChunkPos chunkPos = new ChunkPos(i, n3);
                        if (!regionFile.hasChunk(chunkPos) || regionFile2.hasChunk(chunkPos)) continue;
                        try {
                            object = regionFile.getChunkDataInputStream(chunkPos);
                            object2 = null;
                            try {
                                if (object == null) {
                                    LOGGER.warn("Failed to fetch input stream for chunk {}", (Object)chunkPos);
                                    continue;
                                }
                                compoundTag = NbtIo.read((DataInput)object);
                            }
                            catch (Throwable throwable) {
                                object2 = throwable;
                                throw throwable;
                            }
                            finally {
                                if (object != null) {
                                    if (object2 != null) {
                                        try {
                                            ((FilterInputStream)object).close();
                                        }
                                        catch (Throwable throwable) {
                                            ((Throwable)object2).addSuppressed(throwable);
                                        }
                                    } else {
                                        ((FilterInputStream)object).close();
                                    }
                                }
                            }
                        }
                        catch (IOException iOException) {
                            LOGGER.warn("Failed to read data for chunk {}", (Object)chunkPos, (Object)iOException);
                            continue;
                        }
                        object = compoundTag.getCompound("Level");
                        object2 = OldChunkStorage.load((CompoundTag)object);
                        CompoundTag compoundTag2 = new CompoundTag();
                        CompoundTag compoundTag3 = new CompoundTag();
                        compoundTag2.put("Level", compoundTag3);
                        OldChunkStorage.convertToAnvilFormat(registryHolder, (OldChunkStorage.OldLevelChunk)object2, compoundTag3, biomeSource);
                        try (DataOutputStream dataOutputStream = regionFile2.getChunkDataOutputStream(chunkPos);){
                            NbtIo.write(compoundTag2, dataOutputStream);
                            continue;
                        }
                    }
                    n3 = (int)Math.round(100.0 * (double)(n * 1024) / (double)(n2 * 1024));
                    int n4 = (int)Math.round(100.0 * (double)((i + 1) * 32 + n * 1024) / (double)(n2 * 1024));
                    if (n4 <= n3) continue;
                    progressListener.progressStagePercentage(n4);
                }
            }
        }
        catch (IOException iOException) {
            LOGGER.error("Failed to upgrade region file {}", (Object)file2, (Object)iOException);
        }
    }

    private static void addRegionFiles(File file2, Collection<File> collection) {
        File file3 = new File(file2, "region");
        File[] arrfile = file3.listFiles((file, string) -> string.endsWith(".mcr"));
        if (arrfile != null) {
            Collections.addAll(collection, arrfile);
        }
    }
}

