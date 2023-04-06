/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

public interface LootContextUser {
    default public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of();
    }

    default public void validate(ValidationContext validationContext) {
        validationContext.validateUser(this);
    }
}

