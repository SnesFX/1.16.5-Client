/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.TickPriority;

public class EmptyTickList<T>
implements TickList<T> {
    private static final EmptyTickList<Object> INSTANCE = new EmptyTickList<T>();

    public static <T> EmptyTickList<T> empty() {
        return INSTANCE;
    }

    @Override
    public boolean hasScheduledTick(BlockPos blockPos, T t) {
        return false;
    }

    @Override
    public void scheduleTick(BlockPos blockPos, T t, int n) {
    }

    @Override
    public void scheduleTick(BlockPos blockPos, T t, int n, TickPriority tickPriority) {
    }

    @Override
    public boolean willTickThisTick(BlockPos blockPos, T t) {
        return false;
    }
}

