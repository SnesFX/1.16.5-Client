/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang3.ArrayUtils
 */
package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.ComposableEntryContainer;
import net.minecraft.world.level.storage.loot.entries.CompositeEntryBase;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.apache.commons.lang3.ArrayUtils;

public class AlternativesEntry
extends CompositeEntryBase {
    AlternativesEntry(LootPoolEntryContainer[] arrlootPoolEntryContainer, LootItemCondition[] arrlootItemCondition) {
        super(arrlootPoolEntryContainer, arrlootItemCondition);
    }

    @Override
    public LootPoolEntryType getType() {
        return LootPoolEntries.ALTERNATIVES;
    }

    @Override
    protected ComposableEntryContainer compose(ComposableEntryContainer[] arrcomposableEntryContainer) {
        switch (arrcomposableEntryContainer.length) {
            case 0: {
                return ALWAYS_FALSE;
            }
            case 1: {
                return arrcomposableEntryContainer[0];
            }
            case 2: {
                return arrcomposableEntryContainer[0].or(arrcomposableEntryContainer[1]);
            }
        }
        return (lootContext, consumer) -> {
            for (ComposableEntryContainer composableEntryContainer : arrcomposableEntryContainer) {
                if (!composableEntryContainer.expand(lootContext, consumer)) continue;
                return true;
            }
            return false;
        };
    }

    @Override
    public void validate(ValidationContext validationContext) {
        super.validate(validationContext);
        for (int i = 0; i < this.children.length - 1; ++i) {
            if (!ArrayUtils.isEmpty((Object[])this.children[i].conditions)) continue;
            validationContext.reportProblem("Unreachable entry!");
        }
    }

    public static Builder alternatives(LootPoolEntryContainer.Builder<?> ... arrbuilder) {
        return new Builder(arrbuilder);
    }

    public static class Builder
    extends LootPoolEntryContainer.Builder<Builder> {
        private final List<LootPoolEntryContainer> entries = Lists.newArrayList();

        public Builder(LootPoolEntryContainer.Builder<?> ... arrbuilder) {
            for (LootPoolEntryContainer.Builder<?> builder : arrbuilder) {
                this.entries.add(builder.build());
            }
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public Builder otherwise(LootPoolEntryContainer.Builder<?> builder) {
            this.entries.add(builder.build());
            return this;
        }

        @Override
        public LootPoolEntryContainer build() {
            return new AlternativesEntry(this.entries.toArray(new LootPoolEntryContainer[0]), this.getConditions());
        }

        @Override
        protected /* synthetic */ LootPoolEntryContainer.Builder getThis() {
            return this.getThis();
        }
    }

}

