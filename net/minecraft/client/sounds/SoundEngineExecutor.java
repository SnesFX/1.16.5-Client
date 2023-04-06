/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.sounds;

import java.util.concurrent.locks.LockSupport;
import java.util.function.BooleanSupplier;
import net.minecraft.util.thread.BlockableEventLoop;

public class SoundEngineExecutor
extends BlockableEventLoop<Runnable> {
    private Thread thread = this.createThread();
    private volatile boolean shutdown;

    public SoundEngineExecutor() {
        super("Sound executor");
    }

    private Thread createThread() {
        Thread thread = new Thread(this::run);
        thread.setDaemon(true);
        thread.setName("Sound engine");
        thread.start();
        return thread;
    }

    @Override
    protected Runnable wrapRunnable(Runnable runnable) {
        return runnable;
    }

    @Override
    protected boolean shouldRun(Runnable runnable) {
        return !this.shutdown;
    }

    @Override
    protected Thread getRunningThread() {
        return this.thread;
    }

    private void run() {
        while (!this.shutdown) {
            this.managedBlock(() -> this.shutdown);
        }
    }

    @Override
    protected void waitForTasks() {
        LockSupport.park("waiting for tasks");
    }

    public void flush() {
        this.shutdown = true;
        this.thread.interrupt();
        try {
            this.thread.join();
        }
        catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
        }
        this.dropAllTasks();
        this.shutdown = false;
        this.thread = this.createThread();
    }
}

