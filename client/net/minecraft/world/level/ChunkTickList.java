/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.world.level;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.TickNextTickData;
import net.minecraft.world.level.TickPriority;

public class ChunkTickList<T>
implements TickList<T> {
    private final List<ScheduledTick<T>> ticks;
    private final Function<T, ResourceLocation> toId;

    public ChunkTickList(Function<T, ResourceLocation> function, List<TickNextTickData<T>> list, long l) {
        this(function, list.stream().map(tickNextTickData -> new ScheduledTick(tickNextTickData.getType(), tickNextTickData.pos, (int)(tickNextTickData.triggerTick - l), tickNextTickData.priority)).collect(Collectors.toList()));
    }

    private ChunkTickList(Function<T, ResourceLocation> function, List<ScheduledTick<T>> list) {
        this.ticks = list;
        this.toId = function;
    }

    @Override
    public boolean hasScheduledTick(BlockPos blockPos, T t) {
        return false;
    }

    @Override
    public void scheduleTick(BlockPos blockPos, T t, int n, TickPriority tickPriority) {
        this.ticks.add(new ScheduledTick(t, blockPos, n, tickPriority));
    }

    @Override
    public boolean willTickThisTick(BlockPos blockPos, T t) {
        return false;
    }

    public ListTag save() {
        ListTag listTag = new ListTag();
        for (ScheduledTick<T> scheduledTick : this.ticks) {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putString("i", this.toId.apply(scheduledTick.type).toString());
            compoundTag.putInt("x", scheduledTick.pos.getX());
            compoundTag.putInt("y", scheduledTick.pos.getY());
            compoundTag.putInt("z", scheduledTick.pos.getZ());
            compoundTag.putInt("t", scheduledTick.delay);
            compoundTag.putInt("p", scheduledTick.priority.getValue());
            listTag.add(compoundTag);
        }
        return listTag;
    }

    public static <T> ChunkTickList<T> create(ListTag listTag, Function<T, ResourceLocation> function, Function<ResourceLocation, T> function2) {
        ArrayList arrayList = Lists.newArrayList();
        for (int i = 0; i < listTag.size(); ++i) {
            CompoundTag compoundTag = listTag.getCompound(i);
            T t = function2.apply(new ResourceLocation(compoundTag.getString("i")));
            if (t == null) continue;
            BlockPos blockPos = new BlockPos(compoundTag.getInt("x"), compoundTag.getInt("y"), compoundTag.getInt("z"));
            arrayList.add(new ScheduledTick(t, blockPos, compoundTag.getInt("t"), TickPriority.byValue(compoundTag.getInt("p"))));
        }
        return new ChunkTickList<T>(function, arrayList);
    }

    public void copyOut(TickList<T> tickList) {
        this.ticks.forEach(scheduledTick -> tickList.scheduleTick(scheduledTick.pos, scheduledTick.type, scheduledTick.delay, scheduledTick.priority));
    }

    static class ScheduledTick<T> {
        private final T type;
        public final BlockPos pos;
        public final int delay;
        public final TickPriority priority;

        private ScheduledTick(T t, BlockPos blockPos, int n, TickPriority tickPriority) {
            this.type = t;
            this.pos = blockPos;
            this.delay = n;
            this.priority = tickPriority;
        }

        public String toString() {
            return this.type + ": " + this.pos + ", " + this.delay + ", " + (Object)((Object)this.priority);
        }
    }

}

