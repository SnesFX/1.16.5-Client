/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.OptionalDynamic
 */
package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.References;

public class ChunkStatusFix2
extends DataFix {
    private static final Map<String, String> RENAMES_AND_DOWNGRADES = ImmutableMap.builder().put((Object)"structure_references", (Object)"empty").put((Object)"biomes", (Object)"empty").put((Object)"base", (Object)"surface").put((Object)"carved", (Object)"carvers").put((Object)"liquid_carved", (Object)"liquid_carvers").put((Object)"decorated", (Object)"features").put((Object)"lighted", (Object)"light").put((Object)"mobs_spawned", (Object)"spawn").put((Object)"finalized", (Object)"heightmaps").put((Object)"fullchunk", (Object)"full").build();

    public ChunkStatusFix2(Schema schema, boolean bl) {
        super(schema, bl);
    }

    protected TypeRewriteRule makeRule() {
        Type type = this.getInputSchema().getType(References.CHUNK);
        Type type2 = type.findFieldType("Level");
        OpticFinder opticFinder = DSL.fieldFinder((String)"Level", (Type)type2);
        return this.fixTypeEverywhereTyped("ChunkStatusFix2", type, this.getOutputSchema().getType(References.CHUNK), typed2 -> typed2.updateTyped(opticFinder, typed -> {
            String string;
            Dynamic dynamic = (Dynamic)typed.get(DSL.remainderFinder());
            String string2 = dynamic.get("Status").asString("empty");
            if (Objects.equals(string2, string = RENAMES_AND_DOWNGRADES.getOrDefault(string2, "empty"))) {
                return typed;
            }
            return typed.set(DSL.remainderFinder(), (Object)dynamic.set("Status", dynamic.createString(string)));
        }));
    }
}

