/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.storage.loot.entries;

import java.util.function.Consumer;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.ComposableEntryContainer;
import net.minecraft.world.level.storage.loot.entries.CompositeEntryBase;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SequentialEntry
extends CompositeEntryBase {
    SequentialEntry(LootPoolEntryContainer[] arrlootPoolEntryContainer, LootItemCondition[] arrlootItemCondition) {
        super(arrlootPoolEntryContainer, arrlootItemCondition);
    }

    @Override
    public LootPoolEntryType getType() {
        return LootPoolEntries.SEQUENCE;
    }

    @Override
    protected ComposableEntryContainer compose(ComposableEntryContainer[] arrcomposableEntryContainer) {
        switch (arrcomposableEntryContainer.length) {
            case 0: {
                return ALWAYS_TRUE;
            }
            case 1: {
                return arrcomposableEntryContainer[0];
            }
            case 2: {
                return arrcomposableEntryContainer[0].and(arrcomposableEntryContainer[1]);
            }
        }
        return (lootContext, consumer) -> {
            for (ComposableEntryContainer composableEntryContainer : arrcomposableEntryContainer) {
                if (composableEntryContainer.expand(lootContext, consumer)) continue;
                return false;
            }
            return true;
        };
    }
}

