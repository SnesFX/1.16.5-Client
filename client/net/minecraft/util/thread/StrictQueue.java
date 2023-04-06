/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Queues
 *  javax.annotation.Nullable
 */
package net.minecraft.util.thread;

import com.google.common.collect.Queues;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public interface StrictQueue<T, F> {
    @Nullable
    public F pop();

    public boolean push(T var1);

    public boolean isEmpty();

    public static final class FixedPriorityQueue
    implements StrictQueue<IntRunnable, Runnable> {
        private final List<Queue<Runnable>> queueList;

        public FixedPriorityQueue(int n2) {
            this.queueList = IntStream.range(0, n2).mapToObj(n -> Queues.newConcurrentLinkedQueue()).collect(Collectors.toList());
        }

        @Nullable
        @Override
        public Runnable pop() {
            for (Queue<Runnable> queue : this.queueList) {
                Runnable runnable = queue.poll();
                if (runnable == null) continue;
                return runnable;
            }
            return null;
        }

        @Override
        public boolean push(IntRunnable intRunnable) {
            int n = intRunnable.getPriority();
            this.queueList.get(n).add(intRunnable);
            return true;
        }

        @Override
        public boolean isEmpty() {
            return this.queueList.stream().allMatch(Collection::isEmpty);
        }

        @Nullable
        @Override
        public /* synthetic */ Object pop() {
            return this.pop();
        }
    }

    public static final class IntRunnable
    implements Runnable {
        private final int priority;
        private final Runnable task;

        public IntRunnable(int n, Runnable runnable) {
            this.priority = n;
            this.task = runnable;
        }

        @Override
        public void run() {
            this.task.run();
        }

        public int getPriority() {
            return this.priority;
        }
    }

    public static final class QueueStrictQueue<T>
    implements StrictQueue<T, T> {
        private final Queue<T> queue;

        public QueueStrictQueue(Queue<T> queue) {
            this.queue = queue;
        }

        @Nullable
        @Override
        public T pop() {
            return this.queue.poll();
        }

        @Override
        public boolean push(T t) {
            return this.queue.add(t);
        }

        @Override
        public boolean isEmpty() {
            return this.queue.isEmpty();
        }
    }

}

