/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2BooleanFunction
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.util.thread;

import it.unimi.dsi.fastutil.ints.Int2BooleanFunction;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.SharedConstants;
import net.minecraft.util.thread.ProcessorHandle;
import net.minecraft.util.thread.StrictQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProcessorMailbox<T>
implements ProcessorHandle<T>,
AutoCloseable,
Runnable {
    private static final Logger LOGGER = LogManager.getLogger();
    private final AtomicInteger status = new AtomicInteger(0);
    public final StrictQueue<? super T, ? extends Runnable> queue;
    private final Executor dispatcher;
    private final String name;

    public static ProcessorMailbox<Runnable> create(Executor executor, String string) {
        return new ProcessorMailbox<Runnable>(new StrictQueue.QueueStrictQueue(new ConcurrentLinkedQueue()), executor, string);
    }

    public ProcessorMailbox(StrictQueue<? super T, ? extends Runnable> strictQueue, Executor executor, String string) {
        this.dispatcher = executor;
        this.queue = strictQueue;
        this.name = string;
    }

    private boolean setAsScheduled() {
        int n;
        do {
            if (((n = this.status.get()) & 3) == 0) continue;
            return false;
        } while (!this.status.compareAndSet(n, n | 2));
        return true;
    }

    private void setAsIdle() {
        int n;
        while (!this.status.compareAndSet(n = this.status.get(), n & 0xFFFFFFFD)) {
        }
    }

    private boolean canBeScheduled() {
        if ((this.status.get() & 1) != 0) {
            return false;
        }
        return !this.queue.isEmpty();
    }

    @Override
    public void close() {
        int n;
        while (!this.status.compareAndSet(n = this.status.get(), n | 1)) {
        }
    }

    private boolean shouldProcess() {
        return (this.status.get() & 2) != 0;
    }

    private boolean pollTask() {
        Thread thread;
        String string;
        if (!this.shouldProcess()) {
            return false;
        }
        Runnable runnable = this.queue.pop();
        if (runnable == null) {
            return false;
        }
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            thread = Thread.currentThread();
            string = thread.getName();
            thread.setName(this.name);
        } else {
            thread = null;
            string = null;
        }
        runnable.run();
        if (thread != null) {
            thread.setName(string);
        }
        return true;
    }

    @Override
    public void run() {
        try {
            this.pollUntil(n -> n == 0);
        }
        finally {
            this.setAsIdle();
            this.registerForExecution();
        }
    }

    @Override
    public void tell(T t) {
        this.queue.push(t);
        this.registerForExecution();
    }

    private void registerForExecution() {
        if (this.canBeScheduled() && this.setAsScheduled()) {
            try {
                this.dispatcher.execute(this);
            }
            catch (RejectedExecutionException rejectedExecutionException) {
                try {
                    this.dispatcher.execute(this);
                }
                catch (RejectedExecutionException rejectedExecutionException2) {
                    LOGGER.error("Cound not schedule mailbox", (Throwable)rejectedExecutionException2);
                }
            }
        }
    }

    private int pollUntil(Int2BooleanFunction int2BooleanFunction) {
        int n = 0;
        while (int2BooleanFunction.get(n) && this.pollTask()) {
            ++n;
        }
        return n;
    }

    public String toString() {
        return this.name + " " + this.status.get() + " " + this.queue.isEmpty();
    }

    @Override
    public String name() {
        return this.name;
    }
}

