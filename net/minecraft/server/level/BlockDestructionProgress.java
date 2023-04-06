/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.server.level;

import net.minecraft.core.BlockPos;

public class BlockDestructionProgress
implements Comparable<BlockDestructionProgress> {
    private final int id;
    private final BlockPos pos;
    private int progress;
    private int updatedRenderTick;

    public BlockDestructionProgress(int n, BlockPos blockPos) {
        this.id = n;
        this.pos = blockPos;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public void setProgress(int n) {
        if (n > 10) {
            n = 10;
        }
        this.progress = n;
    }

    public int getProgress() {
        return this.progress;
    }

    public void updateTick(int n) {
        this.updatedRenderTick = n;
    }

    public int getUpdatedRenderTick() {
        return this.updatedRenderTick;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        BlockDestructionProgress blockDestructionProgress = (BlockDestructionProgress)object;
        return this.id == blockDestructionProgress.id;
    }

    public int hashCode() {
        return Integer.hashCode(this.id);
    }

    @Override
    public int compareTo(BlockDestructionProgress blockDestructionProgress) {
        if (this.progress != blockDestructionProgress.progress) {
            return Integer.compare(this.progress, blockDestructionProgress.progress);
        }
        return Integer.compare(this.id, blockDestructionProgress.id);
    }

    @Override
    public /* synthetic */ int compareTo(Object object) {
        return this.compareTo((BlockDestructionProgress)object);
    }
}

