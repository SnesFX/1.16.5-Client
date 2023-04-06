/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.server.packs.resources;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;

public interface ResourceManagerReloadListener
extends PreparableReloadListener {
    @Override
    default public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller profilerFiller, ProfilerFiller profilerFiller2, Executor executor, Executor executor2) {
        return preparationBarrier.wait(Unit.INSTANCE).thenRunAsync(() -> {
            profilerFiller2.startTick();
            profilerFiller2.push("listener");
            this.onResourceManagerReload(resourceManager);
            profilerFiller2.pop();
            profilerFiller2.endTick();
        }, executor2);
    }

    public void onResourceManagerReload(ResourceManager var1);
}

