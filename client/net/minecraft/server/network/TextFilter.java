/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.server.network;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface TextFilter {
    public void join();

    public void leave();

    public CompletableFuture<Optional<String>> processStreamMessage(String var1);

    public CompletableFuture<Optional<List<String>>> processMessageBundle(List<String> var1);
}

