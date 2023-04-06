/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashBasedTable
 *  com.google.common.collect.Table
 *  com.google.common.primitives.UnsignedLong
 *  com.mojang.serialization.Dynamic
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.level.timers;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.primitives.UnsignedLong;
import com.mojang.serialization.Dynamic;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.timers.TimerCallback;
import net.minecraft.world.level.timers.TimerCallbacks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TimerQueue<T> {
    private static final Logger LOGGER = LogManager.getLogger();
    private final TimerCallbacks<T> callbacksRegistry;
    private final Queue<Event<T>> queue = new PriorityQueue<Event<T>>(TimerQueue.createComparator());
    private UnsignedLong sequentialId = UnsignedLong.ZERO;
    private final Table<String, Long, Event<T>> events = HashBasedTable.create();

    private static <T> Comparator<Event<T>> createComparator() {
        return Comparator.comparingLong(event -> event.triggerTime).thenComparing(event -> event.sequentialId);
    }

    public TimerQueue(TimerCallbacks<T> timerCallbacks, Stream<Dynamic<Tag>> stream) {
        this(timerCallbacks);
        this.queue.clear();
        this.events.clear();
        this.sequentialId = UnsignedLong.ZERO;
        stream.forEach(dynamic -> {
            if (!(dynamic.getValue() instanceof CompoundTag)) {
                LOGGER.warn("Invalid format of events: {}", dynamic);
                return;
            }
            this.loadEvent((CompoundTag)dynamic.getValue());
        });
    }

    public TimerQueue(TimerCallbacks<T> timerCallbacks) {
        this.callbacksRegistry = timerCallbacks;
    }

    public void tick(T t, long l) {
        Event<T> event;
        while ((event = this.queue.peek()) != null && event.triggerTime <= l) {
            this.queue.remove();
            this.events.remove((Object)event.id, (Object)l);
            event.callback.handle(t, this, l);
        }
    }

    public void schedule(String string, long l, TimerCallback<T> timerCallback) {
        if (this.events.contains((Object)string, (Object)l)) {
            return;
        }
        this.sequentialId = this.sequentialId.plus(UnsignedLong.ONE);
        Event event = new Event(l, this.sequentialId, string, timerCallback);
        this.events.put((Object)string, (Object)l, event);
        this.queue.add(event);
    }

    public int remove(String string) {
        Collection collection = this.events.row((Object)string).values();
        collection.forEach(this.queue::remove);
        int n = collection.size();
        collection.clear();
        return n;
    }

    public Set<String> getEventsIds() {
        return Collections.unmodifiableSet(this.events.rowKeySet());
    }

    private void loadEvent(CompoundTag compoundTag) {
        CompoundTag compoundTag2 = compoundTag.getCompound("Callback");
        TimerCallback<T> timerCallback = this.callbacksRegistry.deserialize(compoundTag2);
        if (timerCallback != null) {
            String string = compoundTag.getString("Name");
            long l = compoundTag.getLong("TriggerTime");
            this.schedule(string, l, timerCallback);
        }
    }

    private CompoundTag storeEvent(Event<T> event) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putString("Name", event.id);
        compoundTag.putLong("TriggerTime", event.triggerTime);
        compoundTag.put("Callback", this.callbacksRegistry.serialize(event.callback));
        return compoundTag;
    }

    public ListTag store() {
        ListTag listTag = new ListTag();
        this.queue.stream().sorted(TimerQueue.createComparator()).map(this::storeEvent).forEach(listTag::add);
        return listTag;
    }

    public static class Event<T> {
        public final long triggerTime;
        public final UnsignedLong sequentialId;
        public final String id;
        public final TimerCallback<T> callback;

        private Event(long l, UnsignedLong unsignedLong, String string, TimerCallback<T> timerCallback) {
            this.triggerTime = l;
            this.sequentialId = unsignedLong;
            this.id = string;
            this.callback = timerCallback;
        }
    }

}

