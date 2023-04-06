/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Queues
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.TickNextTickData;
import net.minecraft.world.level.TickPriority;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class ServerTickList<T>
implements TickList<T> {
    protected final Predicate<T> ignore;
    private final Function<T, ResourceLocation> toId;
    private final Set<TickNextTickData<T>> tickNextTickSet = Sets.newHashSet();
    private final TreeSet<TickNextTickData<T>> tickNextTickList = Sets.newTreeSet(TickNextTickData.createTimeComparator());
    private final ServerLevel level;
    private final Queue<TickNextTickData<T>> currentlyTicking = Queues.newArrayDeque();
    private final List<TickNextTickData<T>> alreadyTicked = Lists.newArrayList();
    private final Consumer<TickNextTickData<T>> ticker;

    public ServerTickList(ServerLevel serverLevel, Predicate<T> predicate, Function<T, ResourceLocation> function, Consumer<TickNextTickData<T>> consumer) {
        this.ignore = predicate;
        this.toId = function;
        this.level = serverLevel;
        this.ticker = consumer;
    }

    public void tick() {
        TickNextTickData<T> tickNextTickData;
        int n = this.tickNextTickList.size();
        if (n != this.tickNextTickSet.size()) {
            throw new IllegalStateException("TickNextTick list out of synch");
        }
        if (n > 65536) {
            n = 65536;
        }
        ServerChunkCache serverChunkCache = this.level.getChunkSource();
        Iterator<TickNextTickData<T>> iterator = this.tickNextTickList.iterator();
        this.level.getProfiler().push("cleaning");
        while (n > 0 && iterator.hasNext()) {
            tickNextTickData = iterator.next();
            if (tickNextTickData.triggerTick > this.level.getGameTime()) break;
            if (!serverChunkCache.isTickingChunk(tickNextTickData.pos)) continue;
            iterator.remove();
            this.tickNextTickSet.remove(tickNextTickData);
            this.currentlyTicking.add(tickNextTickData);
            --n;
        }
        this.level.getProfiler().popPush("ticking");
        while ((tickNextTickData = this.currentlyTicking.poll()) != null) {
            if (serverChunkCache.isTickingChunk(tickNextTickData.pos)) {
                try {
                    this.alreadyTicked.add(tickNextTickData);
                    this.ticker.accept(tickNextTickData);
                    continue;
                }
                catch (Throwable throwable) {
                    CrashReport crashReport = CrashReport.forThrowable(throwable, "Exception while ticking");
                    CrashReportCategory crashReportCategory = crashReport.addCategory("Block being ticked");
                    CrashReportCategory.populateBlockDetails(crashReportCategory, tickNextTickData.pos, null);
                    throw new ReportedException(crashReport);
                }
            }
            this.scheduleTick(tickNextTickData.pos, tickNextTickData.getType(), 0);
        }
        this.level.getProfiler().pop();
        this.alreadyTicked.clear();
        this.currentlyTicking.clear();
    }

    @Override
    public boolean willTickThisTick(BlockPos blockPos, T t) {
        return this.currentlyTicking.contains(new TickNextTickData<T>(blockPos, t));
    }

    public List<TickNextTickData<T>> fetchTicksInChunk(ChunkPos chunkPos, boolean bl, boolean bl2) {
        int n = (chunkPos.x << 4) - 2;
        int n2 = n + 16 + 2;
        int n3 = (chunkPos.z << 4) - 2;
        int n4 = n3 + 16 + 2;
        return this.fetchTicksInArea(new BoundingBox(n, 0, n3, n2, 256, n4), bl, bl2);
    }

    public List<TickNextTickData<T>> fetchTicksInArea(BoundingBox boundingBox, boolean bl, boolean bl2) {
        List<TickNextTickData<T>> list = this.fetchTicksInArea(null, this.tickNextTickList, boundingBox, bl);
        if (bl && list != null) {
            this.tickNextTickSet.removeAll(list);
        }
        list = this.fetchTicksInArea(list, this.currentlyTicking, boundingBox, bl);
        if (!bl2) {
            list = this.fetchTicksInArea(list, this.alreadyTicked, boundingBox, bl);
        }
        return list == null ? Collections.emptyList() : list;
    }

    @Nullable
    private List<TickNextTickData<T>> fetchTicksInArea(@Nullable List<TickNextTickData<T>> arrayList, Collection<TickNextTickData<T>> collection, BoundingBox boundingBox, boolean bl) {
        Iterator<TickNextTickData<T>> iterator = collection.iterator();
        while (iterator.hasNext()) {
            TickNextTickData<T> tickNextTickData = iterator.next();
            BlockPos blockPos = tickNextTickData.pos;
            if (blockPos.getX() < boundingBox.x0 || blockPos.getX() >= boundingBox.x1 || blockPos.getZ() < boundingBox.z0 || blockPos.getZ() >= boundingBox.z1) continue;
            if (bl) {
                iterator.remove();
            }
            if (arrayList == null) {
                arrayList = Lists.newArrayList();
            }
            arrayList.add(tickNextTickData);
        }
        return arrayList;
    }

    public void copy(BoundingBox boundingBox, BlockPos blockPos) {
        List<TickNextTickData<T>> list = this.fetchTicksInArea(boundingBox, false, false);
        for (TickNextTickData<T> tickNextTickData : list) {
            if (!boundingBox.isInside(tickNextTickData.pos)) continue;
            BlockPos blockPos2 = tickNextTickData.pos.offset(blockPos);
            T t = tickNextTickData.getType();
            this.addTickData(new TickNextTickData<T>(blockPos2, t, tickNextTickData.triggerTick, tickNextTickData.priority));
        }
    }

    public ListTag save(ChunkPos chunkPos) {
        List<TickNextTickData<T>> list = this.fetchTicksInChunk(chunkPos, false, true);
        return ServerTickList.saveTickList(this.toId, list, this.level.getGameTime());
    }

    private static <T> ListTag saveTickList(Function<T, ResourceLocation> function, Iterable<TickNextTickData<T>> iterable, long l) {
        ListTag listTag = new ListTag();
        for (TickNextTickData<T> tickNextTickData : iterable) {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putString("i", function.apply(tickNextTickData.getType()).toString());
            compoundTag.putInt("x", tickNextTickData.pos.getX());
            compoundTag.putInt("y", tickNextTickData.pos.getY());
            compoundTag.putInt("z", tickNextTickData.pos.getZ());
            compoundTag.putInt("t", (int)(tickNextTickData.triggerTick - l));
            compoundTag.putInt("p", tickNextTickData.priority.getValue());
            listTag.add(compoundTag);
        }
        return listTag;
    }

    @Override
    public boolean hasScheduledTick(BlockPos blockPos, T t) {
        return this.tickNextTickSet.contains(new TickNextTickData<T>(blockPos, t));
    }

    @Override
    public void scheduleTick(BlockPos blockPos, T t, int n, TickPriority tickPriority) {
        if (!this.ignore.test(t)) {
            this.addTickData(new TickNextTickData<T>(blockPos, t, (long)n + this.level.getGameTime(), tickPriority));
        }
    }

    private void addTickData(TickNextTickData<T> tickNextTickData) {
        if (!this.tickNextTickSet.contains(tickNextTickData)) {
            this.tickNextTickSet.add(tickNextTickData);
            this.tickNextTickList.add(tickNextTickData);
        }
    }

    public int size() {
        return this.tickNextTickSet.size();
    }
}

