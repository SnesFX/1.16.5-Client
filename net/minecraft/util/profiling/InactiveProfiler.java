/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.util.profiling;

import java.util.function.Supplier;
import net.minecraft.util.profiling.EmptyProfileResults;
import net.minecraft.util.profiling.ProfileCollector;
import net.minecraft.util.profiling.ProfileResults;

public class InactiveProfiler
implements ProfileCollector {
    public static final InactiveProfiler INSTANCE = new InactiveProfiler();

    private InactiveProfiler() {
    }

    @Override
    public void startTick() {
    }

    @Override
    public void endTick() {
    }

    @Override
    public void push(String string) {
    }

    @Override
    public void push(Supplier<String> supplier) {
    }

    @Override
    public void pop() {
    }

    @Override
    public void popPush(String string) {
    }

    @Override
    public void popPush(Supplier<String> supplier) {
    }

    @Override
    public void incrementCounter(String string) {
    }

    @Override
    public void incrementCounter(Supplier<String> supplier) {
    }

    @Override
    public ProfileResults getResults() {
        return EmptyProfileResults.EMPTY;
    }
}

