/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.References;

public class ForcePoiRebuild
extends DataFix {
    public ForcePoiRebuild(Schema schema, boolean bl) {
        super(schema, bl);
    }

    protected TypeRewriteRule makeRule() {
        Type type = DSL.named((String)References.POI_CHUNK.typeName(), (Type)DSL.remainderType());
        if (!Objects.equals((Object)type, (Object)this.getInputSchema().getType(References.POI_CHUNK))) {
            throw new IllegalStateException("Poi type is not what was expected.");
        }
        return this.fixTypeEverywhere("POI rebuild", type, dynamicOps -> pair -> pair.mapSecond(ForcePoiRebuild::cap));
    }

    private static <T> Dynamic<T> cap(Dynamic<T> dynamic2) {
        return dynamic2.update("Sections", dynamic -> dynamic.updateMapValues(pair -> pair.mapSecond(dynamic -> dynamic.remove("Valid"))));
    }
}

