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
 *  com.mojang.serialization.DynamicLike
 *  com.mojang.serialization.OptionalDynamic
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicLike;
import com.mojang.serialization.OptionalDynamic;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.datafix.fixes.NamedEntityFix;
import net.minecraft.util.datafix.fixes.References;

public class EntityShulkerRotationFix
extends NamedEntityFix {
    public EntityShulkerRotationFix(Schema schema) {
        super(schema, false, "EntityShulkerRotationFix", References.ENTITY, "minecraft:shulker");
    }

    public Dynamic<?> fixTag(Dynamic<?> dynamic2) {
        List list = dynamic2.get("Rotation").asList(dynamic -> dynamic.asDouble(180.0));
        if (!list.isEmpty()) {
            list.set(0, (Double)list.get(0) - 180.0);
            return dynamic2.set("Rotation", dynamic2.createList(list.stream().map(dynamic2::createDouble)));
        }
        return dynamic2;
    }

    @Override
    protected Typed<?> fix(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), this::fixTag);
    }
}

