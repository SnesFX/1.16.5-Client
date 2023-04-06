/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.OptionalDynamic
 */
package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.OptionalDynamic;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.References;

public class ReorganizePoi
extends DataFix {
    public ReorganizePoi(Schema schema, boolean bl) {
        super(schema, bl);
    }

    protected TypeRewriteRule makeRule() {
        Type type = DSL.named((String)References.POI_CHUNK.typeName(), (Type)DSL.remainderType());
        if (!Objects.equals((Object)type, (Object)this.getInputSchema().getType(References.POI_CHUNK))) {
            throw new IllegalStateException("Poi type is not what was expected.");
        }
        return this.fixTypeEverywhere("POI reorganization", type, dynamicOps -> pair -> pair.mapSecond(ReorganizePoi::cap));
    }

    private static <T> Dynamic<T> cap(Dynamic<T> dynamic) {
        HashMap hashMap = Maps.newHashMap();
        for (int i = 0; i < 16; ++i) {
            String string = String.valueOf(i);
            Optional optional = dynamic.get(string).result();
            if (!optional.isPresent()) continue;
            Dynamic dynamic2 = (Dynamic)optional.get();
            Dynamic dynamic3 = dynamic.createMap((Map)ImmutableMap.of((Object)dynamic.createString("Records"), (Object)dynamic2));
            hashMap.put(dynamic.createInt(i), dynamic3);
            dynamic = dynamic.remove(string);
        }
        return dynamic.set("Sections", dynamic.createMap((Map)hashMap));
    }
}

