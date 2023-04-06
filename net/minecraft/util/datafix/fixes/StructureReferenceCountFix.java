/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.util.datafix.fixes.References;

public class StructureReferenceCountFix
extends DataFix {
    public StructureReferenceCountFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    protected TypeRewriteRule makeRule() {
        Type type = this.getInputSchema().getType(References.STRUCTURE_FEATURE);
        return this.fixTypeEverywhereTyped("Structure Reference Fix", type, typed -> typed.update(DSL.remainderFinder(), StructureReferenceCountFix::setCountToAtLeastOne));
    }

    private static <T> Dynamic<T> setCountToAtLeastOne(Dynamic<T> dynamic2) {
        return dynamic2.update("references", dynamic -> dynamic.createInt(dynamic.asNumber().map(Number::intValue).result().filter(n -> n > 0).orElse(1).intValue()));
    }
}

