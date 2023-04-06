/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicLike
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicLike;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.datafix.fixes.AbstractUUIDFix;
import net.minecraft.util.datafix.fixes.NamedEntityFix;
import net.minecraft.util.datafix.fixes.References;

public class GossipUUIDFix
extends NamedEntityFix {
    public GossipUUIDFix(Schema schema, String string) {
        super(schema, false, "Gossip for for " + string, References.ENTITY, string);
    }

    @Override
    protected Typed<?> fix(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), dynamic2 -> dynamic2.update("Gossips", dynamic -> (Dynamic)DataFixUtils.orElse(dynamic.asStreamOpt().result().map(stream -> stream.map(dynamic -> AbstractUUIDFix.replaceUUIDLeastMost(dynamic, "Target", "Target").orElse((Dynamic<?>)dynamic))).map(((Dynamic)dynamic)::createList), (Object)dynamic)));
    }
}

