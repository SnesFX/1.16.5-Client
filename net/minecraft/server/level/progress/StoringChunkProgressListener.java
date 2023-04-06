/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  javax.annotation.Nullable
 */
package net.minecraft.server.level.progress;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import javax.annotation.Nullable;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.server.level.progress.LoggerChunkProgressListener;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkStatus;

public class StoringChunkProgressListener
implements ChunkProgressListener {
    private final LoggerChunkProgressListener delegate;
    private final Long2ObjectOpenHashMap<ChunkStatus> statuses;
    private ChunkPos spawnPos = new ChunkPos(0, 0);
    private final int fullDiameter;
    private final int radius;
    private final int diameter;
    private boolean started;

    public StoringChunkProgressListener(int n) {
        this.delegate = new LoggerChunkProgressListener(n);
        this.fullDiameter = n * 2 + 1;
        this.radius = n + ChunkStatus.maxDistance();
        this.diameter = this.radius * 2 + 1;
        this.statuses = new Long2ObjectOpenHashMap();
    }

    @Override
    public void updateSpawnPos(ChunkPos chunkPos) {
        if (!this.started) {
            return;
        }
        this.delegate.updateSpawnPos(chunkPos);
        this.spawnPos = chunkPos;
    }

    @Override
    public void onStatusChange(ChunkPos chunkPos, @Nullable ChunkStatus chunkStatus) {
        if (!this.started) {
            return;
        }
        this.delegate.onStatusChange(chunkPos, chunkStatus);
        if (chunkStatus == null) {
            this.statuses.remove(chunkPos.toLong());
        } else {
            this.statuses.put(chunkPos.toLong(), (Object)chunkStatus);
        }
    }

    public void start() {
        this.started = true;
        this.statuses.clear();
    }

    @Override
    public void stop() {
        this.started = false;
        this.delegate.stop();
    }

    public int getFullDiameter() {
        return this.fullDiameter;
    }

    public int getDiameter() {
        return this.diameter;
    }

    public int getProgress() {
        return this.delegate.getProgress();
    }

    @Nullable
    public ChunkStatus getStatus(int n, int n2) {
        return (ChunkStatus)this.statuses.get(ChunkPos.asLong(n + this.spawnPos.x - this.radius, n2 + this.spawnPos.z - this.radius));
    }
}

