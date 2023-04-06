/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableCollection
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  com.google.common.collect.UnmodifiableIterator
 *  com.google.common.util.concurrent.ThreadFactoryBuilder
 *  com.mojang.datafixers.DataFixer
 *  it.unimi.dsi.fastutil.Hash
 *  it.unimi.dsi.fastutil.Hash$Strategy
 *  it.unimi.dsi.fastutil.objects.Object2FloatMap
 *  it.unimi.dsi.fastutil.objects.Object2FloatMaps
 *  it.unimi.dsi.fastutil.objects.Object2FloatOpenCustomHashMap
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.util.worldupdate;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMaps;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenCustomHashMap;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ThreadFactory;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import net.minecraft.world.level.chunk.storage.RegionFile;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldUpgrader {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ThreadFactory THREAD_FACTORY = new ThreadFactoryBuilder().setDaemon(true).build();
    private final ImmutableSet<ResourceKey<Level>> levels;
    private final boolean eraseCache;
    private final LevelStorageSource.LevelStorageAccess levelStorage;
    private final Thread thread;
    private final DataFixer dataFixer;
    private volatile boolean running = true;
    private volatile boolean finished;
    private volatile float progress;
    private volatile int totalChunks;
    private volatile int converted;
    private volatile int skipped;
    private final Object2FloatMap<ResourceKey<Level>> progressMap = Object2FloatMaps.synchronize((Object2FloatMap)new Object2FloatOpenCustomHashMap(Util.identityStrategy()));
    private volatile Component status = new TranslatableComponent("optimizeWorld.stage.counting");
    private static final Pattern REGEX = Pattern.compile("^r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca$");
    private final DimensionDataStorage overworldDataStorage;

    public WorldUpgrader(LevelStorageSource.LevelStorageAccess levelStorageAccess, DataFixer dataFixer, ImmutableSet<ResourceKey<Level>> immutableSet, boolean bl) {
        this.levels = immutableSet;
        this.eraseCache = bl;
        this.dataFixer = dataFixer;
        this.levelStorage = levelStorageAccess;
        this.overworldDataStorage = new DimensionDataStorage(new File(this.levelStorage.getDimensionPath(Level.OVERWORLD), "data"), dataFixer);
        this.thread = THREAD_FACTORY.newThread(this::work);
        this.thread.setUncaughtExceptionHandler((thread, throwable) -> {
            LOGGER.error("Error upgrading world", throwable);
            this.status = new TranslatableComponent("optimizeWorld.stage.failed");
            this.finished = true;
        });
        this.thread.start();
    }

    public void cancel() {
        this.running = false;
        try {
            this.thread.join();
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
    }

    private void work() {
        ImmutableMap.Builder builder;
        ResourceKey resourceKey2;
        this.totalChunks = 0;
        ImmutableMap.Builder builder2 = ImmutableMap.builder();
        for (ResourceKey resourceKey2 : this.levels) {
            builder = this.getAllChunkPos(resourceKey2);
            builder2.put((Object)resourceKey2, builder.listIterator());
            this.totalChunks += builder.size();
        }
        if (this.totalChunks == 0) {
            this.finished = true;
            return;
        }
        float f = this.totalChunks;
        resourceKey2 = builder2.build();
        builder = ImmutableMap.builder();
        for (ResourceKey resourceKey3 : this.levels) {
            File file = this.levelStorage.getDimensionPath(resourceKey3);
            builder.put((Object)resourceKey3, (Object)new ChunkStorage(new File(file, "region"), this.dataFixer, true));
        }
        UnmodifiableIterator unmodifiableIterator = builder.build();
        long l = Util.getMillis();
        this.status = new TranslatableComponent("optimizeWorld.stage.upgrading");
        while (this.running) {
            boolean bl = false;
            float f2 = 0.0f;
            for (ResourceKey resourceKey4 : this.levels) {
                Object object;
                ListIterator listIterator = (ListIterator)resourceKey2.get((Object)resourceKey4);
                ChunkStorage chunkStorage = (ChunkStorage)unmodifiableIterator.get((Object)resourceKey4);
                if (listIterator.hasNext()) {
                    object = (ChunkPos)listIterator.next();
                    boolean bl2 = false;
                    try {
                        CompoundTag compoundTag = chunkStorage.read((ChunkPos)object);
                        if (compoundTag != null) {
                            boolean bl3;
                            int n = ChunkStorage.getVersion(compoundTag);
                            CompoundTag compoundTag2 = chunkStorage.upgradeChunkTag(resourceKey4, () -> this.overworldDataStorage, compoundTag);
                            CompoundTag compoundTag3 = compoundTag2.getCompound("Level");
                            ChunkPos chunkPos = new ChunkPos(compoundTag3.getInt("xPos"), compoundTag3.getInt("zPos"));
                            if (!chunkPos.equals(object)) {
                                LOGGER.warn("Chunk {} has invalid position {}", object, (Object)chunkPos);
                            }
                            boolean bl4 = bl3 = n < SharedConstants.getCurrentVersion().getWorldVersion();
                            if (this.eraseCache) {
                                bl3 = bl3 || compoundTag3.contains("Heightmaps");
                                compoundTag3.remove("Heightmaps");
                                bl3 = bl3 || compoundTag3.contains("isLightOn");
                                compoundTag3.remove("isLightOn");
                            }
                            if (bl3) {
                                chunkStorage.write((ChunkPos)object, compoundTag2);
                                bl2 = true;
                            }
                        }
                    }
                    catch (ReportedException reportedException) {
                        Throwable throwable = reportedException.getCause();
                        if (throwable instanceof IOException) {
                            LOGGER.error("Error upgrading chunk {}", object, (Object)throwable);
                        }
                        throw reportedException;
                    }
                    catch (IOException iOException) {
                        LOGGER.error("Error upgrading chunk {}", object, (Object)iOException);
                    }
                    if (bl2) {
                        ++this.converted;
                    } else {
                        ++this.skipped;
                    }
                    bl = true;
                }
                object = (float)listIterator.nextIndex() / f;
                this.progressMap.put((Object)resourceKey4, (float)object);
                f2 += object;
            }
            this.progress = f2;
            if (bl) continue;
            this.running = false;
        }
        this.status = new TranslatableComponent("optimizeWorld.stage.finished");
        for (ChunkStorage chunkStorage : unmodifiableIterator.values()) {
            try {
                chunkStorage.close();
            }
            catch (IOException iOException) {
                LOGGER.error("Error upgrading chunk", (Throwable)iOException);
            }
        }
        this.overworldDataStorage.save();
        l = Util.getMillis() - l;
        LOGGER.info("World optimizaton finished after {} ms", (Object)l);
        this.finished = true;
    }

    private List<ChunkPos> getAllChunkPos(ResourceKey<Level> resourceKey) {
        File file2 = this.levelStorage.getDimensionPath(resourceKey);
        File file3 = new File(file2, "region");
        File[] arrfile = file3.listFiles((file, string) -> string.endsWith(".mca"));
        if (arrfile == null) {
            return ImmutableList.of();
        }
        ArrayList arrayList = Lists.newArrayList();
        for (File file4 : arrfile) {
            Matcher matcher = REGEX.matcher(file4.getName());
            if (!matcher.matches()) continue;
            int n = Integer.parseInt(matcher.group(1)) << 5;
            int n2 = Integer.parseInt(matcher.group(2)) << 5;
            try {
                try (RegionFile regionFile = new RegionFile(file4, file3, true);){
                    for (int i = 0; i < 32; ++i) {
                        for (int j = 0; j < 32; ++j) {
                            ChunkPos chunkPos = new ChunkPos(i + n, j + n2);
                            if (!regionFile.doesChunkExist(chunkPos)) continue;
                            arrayList.add(chunkPos);
                        }
                    }
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        return arrayList;
    }

    public boolean isFinished() {
        return this.finished;
    }

    public ImmutableSet<ResourceKey<Level>> levels() {
        return this.levels;
    }

    public float dimensionProgress(ResourceKey<Level> resourceKey) {
        return this.progressMap.getFloat(resourceKey);
    }

    public float getProgress() {
        return this.progress;
    }

    public int getTotalChunks() {
        return this.totalChunks;
    }

    public int getConverted() {
        return this.converted;
    }

    public int getSkipped() {
        return this.skipped;
    }

    public Component getStatus() {
        return this.status;
    }
}

