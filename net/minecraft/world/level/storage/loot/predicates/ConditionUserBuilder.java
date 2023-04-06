/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.storage.loot.predicates;

import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public interface ConditionUserBuilder<T> {
    public T when(LootItemCondition.Builder var1);

    public T unwrap();
}

