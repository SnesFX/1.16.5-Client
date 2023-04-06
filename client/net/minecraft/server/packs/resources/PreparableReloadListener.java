/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.server.packs.resources;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

public interface PreparableReloadListener {
    public CompletableFuture<Void> reload(PreparationBarrier var1, ResourceManager var2, ProfilerFiller var3, ProfilerFiller var4, Executor var5, Executor var6);

    default public String getName() {
        return this.getClass().getSimpleName();
    }

    public static interface PreparationBarrier {
        public <T> CompletableFuture<T> wait(T var1);
    }

}

