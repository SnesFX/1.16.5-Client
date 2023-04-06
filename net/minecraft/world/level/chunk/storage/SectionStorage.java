/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.OptionalDynamic
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.level.chunk.storage;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.OptionalDynamic;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.storage.IOWorker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SectionStorage<R>
implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    private final IOWorker worker;
    private final Long2ObjectMap<Optional<R>> storage = new Long2ObjectOpenHashMap();
    private final LongLinkedOpenHashSet dirty = new LongLinkedOpenHashSet();
    private final Function<Runnable, Codec<R>> codec;
    private final Function<Runnable, R> factory;
    private final DataFixer fixerUpper;
    private final DataFixTypes type;

    public SectionStorage(File file, Function<Runnable, Codec<R>> function, Function<Runnable, R> function2, DataFixer dataFixer, DataFixTypes dataFixTypes, boolean bl) {
        this.codec = function;
        this.factory = function2;
        this.fixerUpper = dataFixer;
        this.type = dataFixTypes;
        this.worker = new IOWorker(file, bl, file.getName());
    }

    protected void tick(BooleanSupplier booleanSupplier) {
        while (!this.dirty.isEmpty() && booleanSupplier.getAsBoolean()) {
            ChunkPos chunkPos = SectionPos.of(this.dirty.firstLong()).chunk();
            this.writeColumn(chunkPos);
        }
    }

    @Nullable
    protected Optional<R> get(long l) {
        return (Optional)this.storage.get(l);
    }

    protected Optional<R> getOrLoad(long l) {
        SectionPos sectionPos = SectionPos.of(l);
        if (this.outsideStoredRange(sectionPos)) {
            return Optional.empty();
        }
        Optional<R> optional = this.get(l);
        if (optional != null) {
            return optional;
        }
        this.readColumn(sectionPos.chunk());
        optional = this.get(l);
        if (optional == null) {
            throw Util.pauseInIde(new IllegalStateException());
        }
        return optional;
    }

    protected boolean outsideStoredRange(SectionPos sectionPos) {
        return Level.isOutsideBuildHeight(SectionPos.sectionToBlockCoord(sectionPos.y()));
    }

    protected R getOrCreate(long l) {
        Optional<R> optional = this.getOrLoad(l);
        if (optional.isPresent()) {
            return optional.get();
        }
        R r = this.factory.apply(() -> this.setDirty(l));
        this.storage.put(l, Optional.of(r));
        return r;
    }

    private void readColumn(ChunkPos chunkPos) {
        this.readColumn(chunkPos, NbtOps.INSTANCE, this.tryRead(chunkPos));
    }

    @Nullable
    private CompoundTag tryRead(ChunkPos chunkPos) {
        try {
            return this.worker.load(chunkPos);
        }
        catch (IOException iOException) {
            LOGGER.error("Error reading chunk {} data from disk", (Object)chunkPos, (Object)iOException);
            return null;
        }
    }

    private <T> void readColumn(ChunkPos chunkPos, DynamicOps<T> dynamicOps, @Nullable T t) {
        if (t == null) {
            for (int i = 0; i < 16; ++i) {
                this.storage.put(SectionPos.of(chunkPos, i).asLong(), Optional.empty());
            }
        } else {
            int n;
            Dynamic dynamic2 = new Dynamic(dynamicOps, t);
            int n2 = SectionStorage.getVersion(dynamic2);
            boolean bl = n2 != (n = SharedConstants.getCurrentVersion().getWorldVersion());
            Dynamic dynamic3 = this.fixerUpper.update(this.type.getType(), dynamic2, n2, n);
            OptionalDynamic optionalDynamic = dynamic3.get("Sections");
            for (int i = 0; i < 16; ++i) {
                long l = SectionPos.of(chunkPos, i).asLong();
                Optional optional = optionalDynamic.get(Integer.toString(i)).result().flatMap(dynamic -> this.codec.apply(() -> this.setDirty(l)).parse(dynamic).resultOrPartial(((Logger)LOGGER)::error));
                this.storage.put(l, optional);
                optional.ifPresent(object -> {
                    this.onSectionLoad(l);
                    if (bl) {
                        this.setDirty(l);
                    }
                });
            }
        }
    }

    private void writeColumn(ChunkPos chunkPos) {
        Dynamic<Tag> dynamic = this.writeColumn(chunkPos, NbtOps.INSTANCE);
        Tag tag = (Tag)dynamic.getValue();
        if (tag instanceof CompoundTag) {
            this.worker.store(chunkPos, (CompoundTag)tag);
        } else {
            LOGGER.error("Expected compound tag, got {}", (Object)tag);
        }
    }

    private <T> Dynamic<T> writeColumn(ChunkPos chunkPos, DynamicOps<T> dynamicOps) {
        HashMap hashMap = Maps.newHashMap();
        for (int i = 0; i < 16; ++i) {
            long l = SectionPos.of(chunkPos, i).asLong();
            this.dirty.remove(l);
            Optional optional = (Optional)this.storage.get(l);
            if (optional == null || !optional.isPresent()) continue;
            DataResult dataResult = this.codec.apply(() -> this.setDirty(l)).encodeStart(dynamicOps, optional.get());
            String string = Integer.toString(i);
            dataResult.resultOrPartial(((Logger)LOGGER)::error).ifPresent(object -> hashMap.put(dynamicOps.createString(string), object));
        }
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("Sections"), (Object)dynamicOps.createMap((Map)hashMap), (Object)dynamicOps.createString("DataVersion"), (Object)dynamicOps.createInt(SharedConstants.getCurrentVersion().getWorldVersion()))));
    }

    protected void onSectionLoad(long l) {
    }

    protected void setDirty(long l) {
        Optional optional = (Optional)this.storage.get(l);
        if (optional == null || !optional.isPresent()) {
            LOGGER.warn("No data for position: {}", (Object)SectionPos.of(l));
            return;
        }
        this.dirty.add(l);
    }

    private static int getVersion(Dynamic<?> dynamic) {
        return dynamic.get("DataVersion").asInt(1945);
    }

    public void flush(ChunkPos chunkPos) {
        if (!this.dirty.isEmpty()) {
            for (int i = 0; i < 16; ++i) {
                long l = SectionPos.of(chunkPos, i).asLong();
                if (!this.dirty.contains(l)) continue;
                this.writeColumn(chunkPos);
                return;
            }
        }
    }

    @Override
    public void close() throws IOException {
        this.worker.close();
    }
}

