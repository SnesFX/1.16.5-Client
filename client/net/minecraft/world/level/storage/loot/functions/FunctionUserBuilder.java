/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.storage.loot.functions;

import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

public interface FunctionUserBuilder<T> {
    public T apply(LootItemFunction.Builder var1);

    public T unwrap();
}

