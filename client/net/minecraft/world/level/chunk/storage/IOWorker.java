/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.util.Either
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.level.chunk.storage;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Unit;
import net.minecraft.util.thread.ProcessorHandle;
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraft.util.thread.StrictQueue;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.RegionFileStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IOWorker
implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    private final AtomicBoolean shutdownRequested = new AtomicBoolean();
    private final ProcessorMailbox<StrictQueue.IntRunnable> mailbox;
    private final RegionFileStorage storage;
    private final Map<ChunkPos, PendingStore> pendingWrites = Maps.newLinkedHashMap();

    protected IOWorker(File file, boolean bl, String string) {
        this.storage = new RegionFileStorage(file, bl);
        this.mailbox = new ProcessorMailbox<StrictQueue.IntRunnable>(new StrictQueue.FixedPriorityQueue(Priority.values().length), Util.ioPool(), "IOWorker-" + string);
    }

    public CompletableFuture<Void> store(ChunkPos chunkPos, CompoundTag compoundTag) {
        return this.submitTask(() -> {
            PendingStore pendingStore = this.pendingWrites.computeIfAbsent(chunkPos, chunkPos -> new PendingStore(compoundTag));
            pendingStore.data = compoundTag;
            return Either.left((Object)pendingStore.result);
        }).thenCompose(Function.identity());
    }

    @Nullable
    public CompoundTag load(ChunkPos chunkPos) throws IOException {
        CompletableFuture<T> completableFuture = this.submitTask(() -> {
            PendingStore pendingStore = this.pendingWrites.get(chunkPos);
            if (pendingStore != null) {
                return Either.left((Object)pendingStore.data);
            }
            try {
                CompoundTag compoundTag = this.storage.read(chunkPos);
                return Either.left((Object)compoundTag);
            }
            catch (Exception exception) {
                LOGGER.warn("Failed to read chunk {}", (Object)chunkPos, (Object)exception);
                return Either.right((Object)exception);
            }
        });
        try {
            return (CompoundTag)completableFuture.join();
        }
        catch (CompletionException completionException) {
            if (completionException.getCause() instanceof IOException) {
                throw (IOException)completionException.getCause();
            }
            throw completionException;
        }
    }

    public CompletableFuture<Void> synchronize() {
        CompletionStage completionStage = this.submitTask(() -> Either.left(CompletableFuture.allOf((CompletableFuture[])this.pendingWrites.values().stream().map(pendingStore -> pendingStore.result).toArray(n -> new CompletableFuture[n])))).thenCompose(Function.identity());
        return ((CompletableFuture)completionStage).thenCompose(void_ -> this.submitTask(() -> {
            try {
                this.storage.flush();
                return Either.left(null);
            }
            catch (Exception exception) {
                LOGGER.warn("Failed to synchronized chunks", (Throwable)exception);
                return Either.right((Object)exception);
            }
        }));
    }

    private <T> CompletableFuture<T> submitTask(Supplier<Either<T, Exception>> supplier) {
        return this.mailbox.askEither(processorHandle -> new StrictQueue.IntRunnable(Priority.HIGH.ordinal(), () -> this.lambda$null$8(processorHandle, (Supplier)supplier)));
    }

    private void storePendingChunk() {
        Iterator<Map.Entry<ChunkPos, PendingStore>> iterator = this.pendingWrites.entrySet().iterator();
        if (!iterator.hasNext()) {
            return;
        }
        Map.Entry<ChunkPos, PendingStore> entry = iterator.next();
        iterator.remove();
        this.runStore(entry.getKey(), entry.getValue());
        this.tellStorePending();
    }

    private void tellStorePending() {
        this.mailbox.tell(new StrictQueue.IntRunnable(Priority.LOW.ordinal(), this::storePendingChunk));
    }

    private void runStore(ChunkPos chunkPos, PendingStore pendingStore) {
        try {
            this.storage.write(chunkPos, pendingStore.data);
            pendingStore.result.complete(null);
        }
        catch (Exception exception) {
            LOGGER.error("Failed to store chunk {}", (Object)chunkPos, (Object)exception);
            pendingStore.result.completeExceptionally(exception);
        }
    }

    @Override
    public void close() throws IOException {
        if (!this.shutdownRequested.compareAndSet(false, true)) {
            return;
        }
        CompletableFuture completableFuture = this.mailbox.ask(processorHandle -> new StrictQueue.IntRunnable(Priority.HIGH.ordinal(), () -> processorHandle.tell(Unit.INSTANCE)));
        try {
            completableFuture.join();
        }
        catch (CompletionException completionException) {
            if (completionException.getCause() instanceof IOException) {
                throw (IOException)completionException.getCause();
            }
            throw completionException;
        }
        this.mailbox.close();
        this.pendingWrites.forEach((arg_0, arg_1) -> this.runStore(arg_0, arg_1));
        this.pendingWrites.clear();
        try {
            this.storage.close();
        }
        catch (Exception exception) {
            LOGGER.error("Failed to close storage", (Throwable)exception);
        }
    }

    private /* synthetic */ void lambda$null$8(ProcessorHandle processorHandle, Supplier supplier) {
        if (!this.shutdownRequested.get()) {
            processorHandle.tell(supplier.get());
        }
        this.tellStorePending();
    }

    static class PendingStore {
        private CompoundTag data;
        private final CompletableFuture<Void> result = new CompletableFuture();

        public PendingStore(CompoundTag compoundTag) {
            this.data = compoundTag;
        }
    }

    static enum Priority {
        HIGH,
        LOW;
        
    }

}

