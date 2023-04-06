/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.server;

public class TickTask
implements Runnable {
    private final int tick;
    private final Runnable runnable;

    public TickTask(int n, Runnable runnable) {
        this.tick = n;
        this.runnable = runnable;
    }

    public int getTick() {
        return this.tick;
    }

    @Override
    public void run() {
        this.runnable.run();
    }
}

