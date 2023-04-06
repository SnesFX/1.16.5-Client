/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.NamedEntityFix;
import net.minecraft.util.datafix.fixes.References;

public class MemoryExpiryDataFix
extends NamedEntityFix {
    public MemoryExpiryDataFix(Schema schema, String string) {
        super(schema, false, "Memory expiry data fix (" + string + ")", References.ENTITY, string);
    }

    @Override
    protected Typed<?> fix(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), this::fixTag);
    }

    public Dynamic<?> fixTag(Dynamic<?> dynamic) {
        return dynamic.update("Brain", this::updateBrain);
    }

    private Dynamic<?> updateBrain(Dynamic<?> dynamic) {
        return dynamic.update("memories", this::updateMemories);
    }

    private Dynamic<?> updateMemories(Dynamic<?> dynamic) {
        return dynamic.updateMapValues(this::updateMemoryEntry);
    }

    private Pair<Dynamic<?>, Dynamic<?>> updateMemoryEntry(Pair<Dynamic<?>, Dynamic<?>> pair) {
        return pair.mapSecond(this::wrapMemoryValue);
    }

    private Dynamic<?> wrapMemoryValue(Dynamic<?> dynamic) {
        return dynamic.createMap((Map)ImmutableMap.of((Object)dynamic.createString("value"), dynamic));
    }
}

