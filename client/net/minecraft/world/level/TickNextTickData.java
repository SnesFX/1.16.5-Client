/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.TickPriority;

public class TickNextTickData<T> {
    private static long counter;
    private final T type;
    public final BlockPos pos;
    public final long triggerTick;
    public final TickPriority priority;
    private final long c = counter++;

    public TickNextTickData(BlockPos blockPos, T t) {
        this(blockPos, t, 0L, TickPriority.NORMAL);
    }

    public TickNextTickData(BlockPos blockPos, T t, long l, TickPriority tickPriority) {
        this.pos = blockPos.immutable();
        this.type = t;
        this.triggerTick = l;
        this.priority = tickPriority;
    }

    public boolean equals(Object object) {
        if (object instanceof TickNextTickData) {
            TickNextTickData tickNextTickData = (TickNextTickData)object;
            return this.pos.equals(tickNextTickData.pos) && this.type == tickNextTickData.type;
        }
        return false;
    }

    public int hashCode() {
        return this.pos.hashCode();
    }

    public static <T> Comparator<TickNextTickData<T>> createTimeComparator() {
        return Comparator.comparingLong(tickNextTickData -> tickNextTickData.triggerTick).thenComparing(tickNextTickData -> tickNextTickData.priority).thenComparingLong(tickNextTickData -> tickNextTickData.c);
    }

    public String toString() {
        return this.type + ": " + this.pos + ", " + this.triggerTick + ", " + (Object)((Object)this.priority) + ", " + this.c;
    }

    public T getType() {
        return this.type;
    }
}

