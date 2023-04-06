/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.server.packs.repository;

import java.util.function.Consumer;
import net.minecraft.server.packs.repository.Pack;

public interface RepositorySource {
    public void loadPacks(Consumer<Pack> var1, Pack.PackConstructor var2);
}

