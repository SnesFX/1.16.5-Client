/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.OptionalDynamic
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.datafix.fixes.References;

public class MobSpawnerEntityIdentifiersFix
extends DataFix {
    public MobSpawnerEntityIdentifiersFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    private Dynamic<?> fix(Dynamic<?> dynamic2) {
        Optional optional;
        if (!"MobSpawner".equals(dynamic2.get("id").asString(""))) {
            return dynamic2;
        }
        Optional optional2 = dynamic2.get("EntityId").asString().result();
        if (optional2.isPresent()) {
            optional = (Dynamic)DataFixUtils.orElse((Optional)dynamic2.get("SpawnData").result(), (Object)dynamic2.emptyMap());
            optional = optional.set("id", optional.createString(((String)optional2.get()).isEmpty() ? "Pig" : (String)optional2.get()));
            dynamic2 = dynamic2.set("SpawnData", (Dynamic)optional);
            dynamic2 = dynamic2.remove("EntityId");
        }
        if ((optional = dynamic2.get("SpawnPotentials").asStreamOpt().result()).isPresent()) {
            dynamic2 = dynamic2.set("SpawnPotentials", dynamic2.createList(((Stream)optional.get()).map(dynamic -> {
                Optional optional = dynamic.get("Type").asString().result();
                if (optional.isPresent()) {
                    Dynamic dynamic2 = ((Dynamic)DataFixUtils.orElse((Optional)dynamic.get("Properties").result(), (Object)dynamic.emptyMap())).set("id", dynamic.createString((String)optional.get()));
                    return dynamic.set("Entity", dynamic2).remove("Type").remove("Properties");
                }
                return dynamic;
            })));
        }
        return dynamic2;
    }

    public TypeRewriteRule makeRule() {
        Type type = this.getOutputSchema().getType(References.UNTAGGED_SPAWNER);
        return this.fixTypeEverywhereTyped("MobSpawnerEntityIdentifiersFix", this.getInputSchema().getType(References.UNTAGGED_SPAWNER), type, typed -> {
            Dynamic dynamic = (Dynamic)typed.get(DSL.remainderFinder());
            DataResult dataResult = type.readTyped(this.fix(dynamic = dynamic.set("id", dynamic.createString("MobSpawner"))));
            if (!dataResult.result().isPresent()) {
                return typed;
            }
            return (Typed)((Pair)dataResult.result().get()).getFirst();
        });
    }
}

