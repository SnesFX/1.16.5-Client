/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.server.level.progress;

import javax.annotation.Nullable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkStatus;

public interface ChunkProgressListener {
    public void updateSpawnPos(ChunkPos var1);

    public void onStatusChange(ChunkPos var1, @Nullable ChunkStatus var2);

    public void stop();
}

