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

public class EntryGroup
extends CompositeEntryBase {
    EntryGroup(LootPoolEntryContainer[] arrlootPoolEntryContainer, LootItemCondition[] arrlootItemCondition) {
        super(arrlootPoolEntryContainer, arrlootItemCondition);
    }

    @Override
    public LootPoolEntryType getType() {
        return LootPoolEntries.GROUP;
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
                ComposableEntryContainer composableEntryContainer = arrcomposableEntryContainer[0];
                ComposableEntryContainer composableEntryContainer2 = arrcomposableEntryContainer[1];
                return (lootContext, consumer) -> {
                    composableEntryContainer.expand(lootContext, consumer);
                    composableEntryContainer2.expand(lootContext, consumer);
                    return true;
                };
            }
        }
        return (lootContext, consumer) -> {
            for (ComposableEntryContainer composableEntryContainer : arrcomposableEntryContainer) {
                composableEntryContainer.expand(lootContext, consumer);
            }
            return true;
        };
    }
}

