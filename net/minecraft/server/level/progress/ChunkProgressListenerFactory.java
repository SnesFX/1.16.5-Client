/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.server.level.progress;

import net.minecraft.server.level.progress.ChunkProgressListener;

public interface ChunkProgressListenerFactory {
    public ChunkProgressListener create(int var1);
}

