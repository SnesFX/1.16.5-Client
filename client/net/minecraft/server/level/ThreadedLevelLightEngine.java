/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectList
 *  it.unimi.dsi.fastutil.objects.ObjectListIterator
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.level;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ChunkTaskPriorityQueueSorter;
import net.minecraft.util.thread.ProcessorHandle;
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThreadedLevelLightEngine
extends LevelLightEngine
implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ProcessorMailbox<Runnable> taskMailbox;
    private final ObjectList<Pair<TaskType, Runnable>> lightTasks = new ObjectArrayList();
    private final ChunkMap chunkMap;
    private final ProcessorHandle<ChunkTaskPriorityQueueSorter.Message<Runnable>> sorterMailbox;
    private volatile int taskPerBatch = 5;
    private final AtomicBoolean scheduled = new AtomicBoolean();

    public ThreadedLevelLightEngine(LightChunkGetter lightChunkGetter, ChunkMap chunkMap, boolean bl, ProcessorMailbox<Runnable> processorMailbox, ProcessorHandle<ChunkTaskPriorityQueueSorter.Message<Runnable>> processorHandle) {
        super(lightChunkGetter, true, bl);
        this.chunkMap = chunkMap;
        this.sorterMailbox = processorHandle;
        this.taskMailbox = processorMailbox;
    }

    @Override
    public void close() {
    }

    @Override
    public int runUpdates(int n, boolean bl, boolean bl2) {
        throw Util.pauseInIde(new UnsupportedOperationException("Ran authomatically on a different thread!"));
    }

    @Override
    public void onBlockEmissionIncrease(BlockPos blockPos, int n) {
        throw Util.pauseInIde(new UnsupportedOperationException("Ran authomatically on a different thread!"));
    }

    @Override
    public void checkBlock(BlockPos blockPos) {
        BlockPos blockPos2 = blockPos.immutable();
        this.addTask(blockPos.getX() >> 4, blockPos.getZ() >> 4, TaskType.POST_UPDATE, Util.name(() -> super.checkBlock(blockPos2), () -> "checkBlock " + blockPos2));
    }

    protected void updateChunkStatus(ChunkPos chunkPos) {
        this.addTask(chunkPos.x, chunkPos.z, () -> 0, TaskType.PRE_UPDATE, Util.name(() -> {
            int n;
            super.retainData(chunkPos, false);
            super.enableLightSources(chunkPos, false);
            for (n = -1; n < 17; ++n) {
                super.queueSectionData(LightLayer.BLOCK, SectionPos.of(chunkPos, n), null, true);
                super.queueSectionData(LightLayer.SKY, SectionPos.of(chunkPos, n), null, true);
            }
            for (n = 0; n < 16; ++n) {
                super.updateSectionStatus(SectionPos.of(chunkPos, n), true);
            }
        }, () -> "updateChunkStatus " + chunkPos + " " + true));
    }

    @Override
    public void updateSectionStatus(SectionPos sectionPos, boolean bl) {
        this.addTask(sectionPos.x(), sectionPos.z(), () -> 0, TaskType.PRE_UPDATE, Util.name(() -> super.updateSectionStatus(sectionPos, bl), () -> "updateSectionStatus " + sectionPos + " " + bl));
    }

    @Override
    public void enableLightSources(ChunkPos chunkPos, boolean bl) {
        this.addTask(chunkPos.x, chunkPos.z, TaskType.PRE_UPDATE, Util.name(() -> super.enableLightSources(chunkPos, bl), () -> "enableLight " + chunkPos + " " + bl));
    }

    @Override
    public void queueSectionData(LightLayer lightLayer, SectionPos sectionPos, @Nullable DataLayer dataLayer, boolean bl) {
        this.addTask(sectionPos.x(), sectionPos.z(), () -> 0, TaskType.PRE_UPDATE, Util.name(() -> super.queueSectionData(lightLayer, sectionPos, dataLayer, bl), () -> "queueData " + sectionPos));
    }

    private void addTask(int n, int n2, TaskType taskType, Runnable runnable) {
        this.addTask(n, n2, this.chunkMap.getChunkQueueLevel(ChunkPos.asLong(n, n2)), taskType, runnable);
    }

    private void addTask(int n, int n2, IntSupplier intSupplier, TaskType taskType, Runnable runnable) {
        this.sorterMailbox.tell(ChunkTaskPriorityQueueSorter.message(() -> {
            this.lightTasks.add((Object)Pair.of((Object)((Object)taskType), (Object)runnable));
            if (this.lightTasks.size() >= this.taskPerBatch) {
                this.runUpdate();
            }
        }, ChunkPos.asLong(n, n2), intSupplier));
    }

    @Override
    public void retainData(ChunkPos chunkPos, boolean bl) {
        this.addTask(chunkPos.x, chunkPos.z, () -> 0, TaskType.PRE_UPDATE, Util.name(() -> super.retainData(chunkPos, bl), () -> "retainData " + chunkPos));
    }

    public CompletableFuture<ChunkAccess> lightChunk(ChunkAccess chunkAccess, boolean bl) {
        ChunkPos chunkPos = chunkAccess.getPos();
        chunkAccess.setLightCorrect(false);
        this.addTask(chunkPos.x, chunkPos.z, TaskType.PRE_UPDATE, Util.name(() -> {
            LevelChunkSection[] arrlevelChunkSection = chunkAccess.getSections();
            for (int i = 0; i < 16; ++i) {
                LevelChunkSection levelChunkSection = arrlevelChunkSection[i];
                if (LevelChunkSection.isEmpty(levelChunkSection)) continue;
                super.updateSectionStatus(SectionPos.of(chunkPos, i), false);
            }
            super.enableLightSources(chunkPos, true);
            if (!bl) {
                chunkAccess.getLights().forEach(blockPos -> super.onBlockEmissionIncrease((BlockPos)blockPos, chunkAccess.getLightEmission((BlockPos)blockPos)));
            }
            this.chunkMap.releaseLightTicket(chunkPos);
        }, () -> "lightChunk " + chunkPos + " " + bl));
        return CompletableFuture.supplyAsync(() -> {
            chunkAccess.setLightCorrect(true);
            super.retainData(chunkPos, false);
            return chunkAccess;
        }, runnable -> this.addTask(chunkPos.x, chunkPos.z, TaskType.POST_UPDATE, runnable));
    }

    public void tryScheduleUpdate() {
        if ((!this.lightTasks.isEmpty() || super.hasLightWork()) && this.scheduled.compareAndSet(false, true)) {
            this.taskMailbox.tell(() -> {
                this.runUpdate();
                this.scheduled.set(false);
            });
        }
    }

    private void runUpdate() {
        Pair pair;
        int n;
        int n2 = Math.min(this.lightTasks.size(), this.taskPerBatch);
        ObjectListIterator objectListIterator = this.lightTasks.iterator();
        for (n = 0; objectListIterator.hasNext() && n < n2; ++n) {
            pair = (Pair)objectListIterator.next();
            if (pair.getFirst() != TaskType.PRE_UPDATE) continue;
            ((Runnable)pair.getSecond()).run();
        }
        objectListIterator.back(n);
        super.runUpdates(Integer.MAX_VALUE, true, true);
        for (n = 0; objectListIterator.hasNext() && n < n2; ++n) {
            pair = (Pair)objectListIterator.next();
            if (pair.getFirst() == TaskType.POST_UPDATE) {
                ((Runnable)pair.getSecond()).run();
            }
            objectListIterator.remove();
        }
    }

    public void setTaskPerBatch(int n) {
        this.taskPerBatch = n;
    }

    static enum TaskType {
        PRE_UPDATE,
        POST_UPDATE;
        
    }

}

