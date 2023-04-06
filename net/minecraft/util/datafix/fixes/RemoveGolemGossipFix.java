/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.OptionalDynamic
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.util.datafix.fixes.NamedEntityFix;
import net.minecraft.util.datafix.fixes.References;

public class RemoveGolemGossipFix
extends NamedEntityFix {
    public RemoveGolemGossipFix(Schema schema, boolean bl) {
        super(schema, bl, "Remove Golem Gossip Fix", References.ENTITY, "minecraft:villager");
    }

    @Override
    protected Typed<?> fix(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), RemoveGolemGossipFix::fixValue);
    }

    private static Dynamic<?> fixValue(Dynamic<?> dynamic) {
        return dynamic.update("Gossips", dynamic3 -> dynamic.createList(dynamic3.asStream().filter(dynamic -> !dynamic.get("Type").asString("").equals("golem"))));
    }
}

