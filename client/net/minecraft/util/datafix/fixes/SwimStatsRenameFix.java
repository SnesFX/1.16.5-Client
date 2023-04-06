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
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class SwimStatsRenameFix
extends DataFix {
    public SwimStatsRenameFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    protected TypeRewriteRule makeRule() {
        Type type = this.getOutputSchema().getType(References.STATS);
        Type type2 = this.getInputSchema().getType(References.STATS);
        OpticFinder opticFinder = type2.findField("stats");
        OpticFinder opticFinder2 = opticFinder.type().findField("minecraft:custom");
        OpticFinder opticFinder3 = NamespacedSchema.namespacedString().finder();
        return this.fixTypeEverywhereTyped("SwimStatsRenameFix", type2, type, typed -> typed.updateTyped(opticFinder, typed2 -> typed2.updateTyped(opticFinder2, typed -> typed.update(opticFinder3, string -> {
            if (string.equals("minecraft:swim_one_cm")) {
                return "minecraft:walk_on_water_one_cm";
            }
            if (string.equals("minecraft:dive_one_cm")) {
                return "minecraft:walk_under_water_one_cm";
            }
            return string;
        }))));
    }
}

