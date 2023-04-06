/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Stopwatch
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.packs.resources;

import com.google.common.base.Stopwatch;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import net.minecraft.Util;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleReloadInstance;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ActiveProfiler;
import net.minecraft.util.profiling.ProfileResults;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProfiledReloadInstance
extends SimpleReloadInstance<State> {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Stopwatch total = Stopwatch.createUnstarted();

    public ProfiledReloadInstance(ResourceManager resourceManager2, List<PreparableReloadListener> list, Executor executor, Executor executor4, CompletableFuture<Unit> completableFuture) {
        super(executor, executor4, resourceManager2, list, (preparationBarrier, resourceManager, preparableReloadListener, executor2, executor3) -> {
            AtomicLong atomicLong = new AtomicLong();
            AtomicLong atomicLong2 = new AtomicLong();
            ActiveProfiler activeProfiler = new ActiveProfiler(Util.timeSource, () -> 0, false);
            ActiveProfiler activeProfiler2 = new ActiveProfiler(Util.timeSource, () -> 0, false);
            CompletableFuture<Void> completableFuture = preparableReloadListener.reload(preparationBarrier, resourceManager, activeProfiler, activeProfiler2, runnable -> executor2.execute(() -> {
                long l = Util.getNanos();
                runnable.run();
                atomicLong.addAndGet(Util.getNanos() - l);
            }), runnable -> executor3.execute(() -> {
                long l = Util.getNanos();
                runnable.run();
                atomicLong2.addAndGet(Util.getNanos() - l);
            }));
            return completableFuture.thenApplyAsync(void_ -> new State(preparableReloadListener.getName(), activeProfiler.getResults(), activeProfiler2.getResults(), atomicLong, atomicLong2), executor4);
        }, completableFuture);
        this.total.start();
        this.allDone.thenAcceptAsync(this::finish, executor4);
    }

    private void finish(List<State> list) {
        this.total.stop();
        int n = 0;
        LOGGER.info("Resource reload finished after " + this.total.elapsed(TimeUnit.MILLISECONDS) + " ms");
        for (State state : list) {
            ProfileResults profileResults = state.preparationResult;
            ProfileResults profileResults2 = state.reloadResult;
            int n2 = (int)((double)state.preparationNanos.get() / 1000000.0);
            int n3 = (int)((double)state.reloadNanos.get() / 1000000.0);
            int n4 = n2 + n3;
            String string = state.name;
            LOGGER.info(string + " took approximately " + n4 + " ms (" + n2 + " ms preparing, " + n3 + " ms applying)");
            n += n3;
        }
        LOGGER.info("Total blocking time: " + n + " ms");
    }

    public static class State {
        private final String name;
        private final ProfileResults preparationResult;
        private final ProfileResults reloadResult;
        private final AtomicLong preparationNanos;
        private final AtomicLong reloadNanos;

        private State(String string, ProfileResults profileResults, ProfileResults profileResults2, AtomicLong atomicLong, AtomicLong atomicLong2) {
            this.name = string;
            this.preparationResult = profileResults;
            this.reloadResult = profileResults2;
            this.preparationNanos = atomicLong;
            this.reloadNanos = atomicLong2;
        }
    }

}

