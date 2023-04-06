/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Dynamic
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.util.datafix.fixes.AbstractUUIDFix;
import net.minecraft.util.datafix.fixes.References;
import org.apache.logging.log4j.Logger;

public class LevelUUIDFix
extends AbstractUUIDFix {
    public LevelUUIDFix(Schema schema) {
        super(schema, References.LEVEL);
    }

    protected TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("LevelUUIDFix", this.getInputSchema().getType(this.typeReference), typed2 -> typed2.updateTyped(DSL.remainderFinder(), typed -> typed.update(DSL.remainderFinder(), dynamic -> {
            dynamic = this.updateCustomBossEvents((Dynamic<?>)dynamic);
            dynamic = this.updateDragonFight((Dynamic<?>)dynamic);
            dynamic = this.updateWanderingTrader((Dynamic<?>)dynamic);
            return dynamic;
        })));
    }

    private Dynamic<?> updateWanderingTrader(Dynamic<?> dynamic) {
        return LevelUUIDFix.replaceUUIDString(dynamic, "WanderingTraderId", "WanderingTraderId").orElse(dynamic);
    }

    private Dynamic<?> updateDragonFight(Dynamic<?> dynamic2) {
        return dynamic2.update("DimensionData", dynamic -> dynamic.updateMapValues(pair -> pair.mapSecond(dynamic2 -> dynamic2.update("DragonFight", dynamic -> LevelUUIDFix.replaceUUIDLeastMost(dynamic, "DragonUUID", "Dragon").orElse((Dynamic<?>)dynamic)))));
    }

    private Dynamic<?> updateCustomBossEvents(Dynamic<?> dynamic2) {
        return dynamic2.update("CustomBossEvents", dynamic -> dynamic.updateMapValues(pair -> pair.mapSecond(dynamic -> dynamic.update("Players", dynamic3 -> dynamic.createList(dynamic3.asStream().map(dynamic -> LevelUUIDFix.createUUIDFromML(dynamic).orElseGet(() -> {
            LOGGER.warn("CustomBossEvents contains invalid UUIDs.");
            return dynamic;
        })))))));
    }
}

