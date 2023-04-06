/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;

public class BlockEventData {
    private final BlockPos pos;
    private final Block block;
    private final int paramA;
    private final int paramB;

    public BlockEventData(BlockPos blockPos, Block block, int n, int n2) {
        this.pos = blockPos;
        this.block = block;
        this.paramA = n;
        this.paramB = n2;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public Block getBlock() {
        return this.block;
    }

    public int getParamA() {
        return this.paramA;
    }

    public int getParamB() {
        return this.paramB;
    }

    public boolean equals(Object object) {
        if (object instanceof BlockEventData) {
            BlockEventData blockEventData = (BlockEventData)object;
            return this.pos.equals(blockEventData.pos) && this.paramA == blockEventData.paramA && this.paramB == blockEventData.paramB && this.block == blockEventData.block;
        }
        return false;
    }

    public int hashCode() {
        int n = this.pos.hashCode();
        n = 31 * n + this.block.hashCode();
        n = 31 * n + this.paramA;
        n = 31 * n + this.paramB;
        return n;
    }

    public String toString() {
        return "TE(" + this.pos + ")," + this.paramA + "," + this.paramB + "," + this.block;
    }
}

