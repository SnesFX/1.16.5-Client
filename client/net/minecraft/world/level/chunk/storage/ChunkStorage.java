/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFixer
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.chunk.storage;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.storage.IOWorker;
import net.minecraft.world.level.levelgen.structure.LegacyStructureDataHandler;
import net.minecraft.world.level.storage.DimensionDataStorage;

public class ChunkStorage
implements AutoCloseable {
    private final IOWorker worker;
    protected final DataFixer fixerUpper;
    @Nullable
    private LegacyStructureDataHandler legacyStructureHandler;

    public ChunkStorage(File file, DataFixer dataFixer, boolean bl) {
        this.fixerUpper = dataFixer;
        this.worker = new IOWorker(file, bl, "chunk");
    }

    public CompoundTag upgradeChunkTag(ResourceKey<Level> resourceKey, Supplier<DimensionDataStorage> supplier, CompoundTag compoundTag) {
        int n = ChunkStorage.getVersion(compoundTag);
        int n2 = 1493;
        if (n < 1493 && (compoundTag = NbtUtils.update(this.fixerUpper, DataFixTypes.CHUNK, compoundTag, n, 1493)).getCompound("Level").getBoolean("hasLegacyStructureData")) {
            if (this.legacyStructureHandler == null) {
                this.legacyStructureHandler = LegacyStructureDataHandler.getLegacyStructureHandler(resourceKey, supplier.get());
            }
            compoundTag = this.legacyStructureHandler.updateFromLegacy(compoundTag);
        }
        compoundTag = NbtUtils.update(this.fixerUpper, DataFixTypes.CHUNK, compoundTag, Math.max(1493, n));
        if (n < SharedConstants.getCurrentVersion().getWorldVersion()) {
            compoundTag.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
        }
        return compoundTag;
    }

    public static int getVersion(CompoundTag compoundTag) {
        return compoundTag.contains("DataVersion", 99) ? compoundTag.getInt("DataVersion") : -1;
    }

    @Nullable
    public CompoundTag read(ChunkPos chunkPos) throws IOException {
        return this.worker.load(chunkPos);
    }

    public void write(ChunkPos chunkPos, CompoundTag compoundTag) {
        this.worker.store(chunkPos, compoundTag);
        if (this.legacyStructureHandler != null) {
            this.legacyStructureHandler.removeIndex(chunkPos.toLong());
        }
    }

    public void flushWorker() {
        this.worker.synchronize().join();
    }

    @Override
    public void close() throws IOException {
        this.worker.close();
    }
}

