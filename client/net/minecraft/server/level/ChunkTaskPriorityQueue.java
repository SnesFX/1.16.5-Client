/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.util.Either
 *  it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongCollection
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  javax.annotation.Nullable
 */
package net.minecraft.server.level;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.LongFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.level.ChunkPos;

public class ChunkTaskPriorityQueue<T> {
    public static final int PRIORITY_LEVEL_COUNT = ChunkMap.MAX_CHUNK_DISTANCE + 2;
    private final List<Long2ObjectLinkedOpenHashMap<List<Optional<T>>>> taskQueue = IntStream.range(0, PRIORITY_LEVEL_COUNT).mapToObj(n -> new Long2ObjectLinkedOpenHashMap()).collect(Collectors.toList());
    private volatile int firstQueue = PRIORITY_LEVEL_COUNT;
    private final String name;
    private final LongSet acquired = new LongOpenHashSet();
    private final int maxTasks;

    public ChunkTaskPriorityQueue(String string, int n2) {
        this.name = string;
        this.maxTasks = n2;
    }

    protected void resortChunkTasks(int n, ChunkPos chunkPos, int n2) {
        if (n >= PRIORITY_LEVEL_COUNT) {
            return;
        }
        Long2ObjectLinkedOpenHashMap<List<Optional<T>>> long2ObjectLinkedOpenHashMap = this.taskQueue.get(n);
        List list = (List)long2ObjectLinkedOpenHashMap.remove(chunkPos.toLong());
        if (n == this.firstQueue) {
            while (this.firstQueue < PRIORITY_LEVEL_COUNT && this.taskQueue.get(this.firstQueue).isEmpty()) {
                ++this.firstQueue;
            }
        }
        if (list != null && !list.isEmpty()) {
            ((List)this.taskQueue.get(n2).computeIfAbsent(chunkPos.toLong(), l -> Lists.newArrayList())).addAll(list);
            this.firstQueue = Math.min(this.firstQueue, n2);
        }
    }

    protected void submit(Optional<T> optional, long l2, int n) {
        ((List)this.taskQueue.get(n).computeIfAbsent(l2, l -> Lists.newArrayList())).add(optional);
        this.firstQueue = Math.min(this.firstQueue, n);
    }

    protected void release(long l, boolean bl) {
        for (Long2ObjectLinkedOpenHashMap<List<Optional<T>>> long2ObjectLinkedOpenHashMap : this.taskQueue) {
            List list = (List)long2ObjectLinkedOpenHashMap.get(l);
            if (list == null) continue;
            if (bl) {
                list.clear();
            } else {
                list.removeIf(optional -> !optional.isPresent());
            }
            if (!list.isEmpty()) continue;
            long2ObjectLinkedOpenHashMap.remove(l);
        }
        while (this.firstQueue < PRIORITY_LEVEL_COUNT && this.taskQueue.get(this.firstQueue).isEmpty()) {
            ++this.firstQueue;
        }
        this.acquired.remove(l);
    }

    private Runnable acquire(long l) {
        return () -> this.acquired.add(l);
    }

    @Nullable
    public Stream<Either<T, Runnable>> pop() {
        if (this.acquired.size() >= this.maxTasks) {
            return null;
        }
        if (this.firstQueue < PRIORITY_LEVEL_COUNT) {
            int n = this.firstQueue;
            Long2ObjectLinkedOpenHashMap<List<Optional<T>>> long2ObjectLinkedOpenHashMap = this.taskQueue.get(n);
            long l = long2ObjectLinkedOpenHashMap.firstLongKey();
            List list = (List)long2ObjectLinkedOpenHashMap.removeFirst();
            while (this.firstQueue < PRIORITY_LEVEL_COUNT && this.taskQueue.get(this.firstQueue).isEmpty()) {
                ++this.firstQueue;
            }
            return list.stream().map(optional -> optional.map(Either::left).orElseGet(() -> Either.right((Object)this.acquire(l))));
        }
        return null;
    }

    public String toString() {
        return this.name + " " + this.firstQueue + "...";
    }

    @VisibleForTesting
    LongSet getAcquired() {
        return new LongOpenHashSet((LongCollection)this.acquired);
    }
}

