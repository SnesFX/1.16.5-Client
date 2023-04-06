/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.server.level;

import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.TickPriority;

public class WorldGenTickList<T>
implements TickList<T> {
    private final Function<BlockPos, TickList<T>> index;

    public WorldGenTickList(Function<BlockPos, TickList<T>> function) {
        this.index = function;
    }

    @Override
    public boolean hasScheduledTick(BlockPos blockPos, T t) {
        return this.index.apply(blockPos).hasScheduledTick(blockPos, t);
    }

    @Override
    public void scheduleTick(BlockPos blockPos, T t, int n, TickPriority tickPriority) {
        this.index.apply(blockPos).scheduleTick(blockPos, t, n, tickPriority);
    }

    @Override
    public boolean willTickThisTick(BlockPos blockPos, T t) {
        return false;
    }
}

