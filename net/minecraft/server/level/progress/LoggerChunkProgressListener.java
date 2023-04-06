/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.level.progress;

import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerChunkProgressListener
implements ChunkProgressListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private final int maxCount;
    private int count;
    private long startTime;
    private long nextTickTime = Long.MAX_VALUE;

    public LoggerChunkProgressListener(int n) {
        int n2 = n * 2 + 1;
        this.maxCount = n2 * n2;
    }

    @Override
    public void updateSpawnPos(ChunkPos chunkPos) {
        this.startTime = this.nextTickTime = Util.getMillis();
    }

    @Override
    public void onStatusChange(ChunkPos chunkPos, @Nullable ChunkStatus chunkStatus) {
        if (chunkStatus == ChunkStatus.FULL) {
            ++this.count;
        }
        int n = this.getProgress();
        if (Util.getMillis() > this.nextTickTime) {
            this.nextTickTime += 500L;
            LOGGER.info(new TranslatableComponent("menu.preparingSpawn", Mth.clamp(n, 0, 100)).getString());
        }
    }

    @Override
    public void stop() {
        LOGGER.info("Time elapsed: {} ms", (Object)(Util.getMillis() - this.startTime));
        this.nextTickTime = Long.MAX_VALUE;
    }

    public int getProgress() {
        return Mth.floor((float)this.count * 100.0f / (float)this.maxCount);
    }
}

