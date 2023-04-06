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
import net.minecraft.util.datafix.fixes.NamedEntityFix;
import net.minecraft.util.datafix.fixes.References;

public class CatTypeFix
extends NamedEntityFix {
    public CatTypeFix(Schema schema, boolean bl) {
        super(schema, bl, "CatTypeFix", References.ENTITY, "minecraft:cat");
    }

    public Dynamic<?> fixTag(Dynamic<?> dynamic) {
        if (dynamic.get("CatType").asInt(0) == 9) {
            return dynamic.set("CatType", dynamic.createInt(10));
        }
        return dynamic;
    }

    @Override
    protected Typed<?> fix(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), this::fixTag);
    }
}

