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
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.OptionalDynamic
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.References;

public class HeightmapRenamingFix
extends DataFix {
    public HeightmapRenamingFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    protected TypeRewriteRule makeRule() {
        Type type = this.getInputSchema().getType(References.CHUNK);
        OpticFinder opticFinder = type.findField("Level");
        return this.fixTypeEverywhereTyped("HeightmapRenamingFix", type, typed2 -> typed2.updateTyped(opticFinder, typed -> typed.update(DSL.remainderFinder(), this::fix)));
    }

    private Dynamic<?> fix(Dynamic<?> dynamic) {
        Optional optional;
        Optional optional2;
        Optional optional3;
        Optional optional4 = dynamic.get("Heightmaps").result();
        if (!optional4.isPresent()) {
            return dynamic;
        }
        Dynamic dynamic2 = (Dynamic)optional4.get();
        Optional optional5 = dynamic2.get("LIQUID").result();
        if (optional5.isPresent()) {
            dynamic2 = dynamic2.remove("LIQUID");
            dynamic2 = dynamic2.set("WORLD_SURFACE_WG", (Dynamic)optional5.get());
        }
        if ((optional3 = dynamic2.get("SOLID").result()).isPresent()) {
            dynamic2 = dynamic2.remove("SOLID");
            dynamic2 = dynamic2.set("OCEAN_FLOOR_WG", (Dynamic)optional3.get());
            dynamic2 = dynamic2.set("OCEAN_FLOOR", (Dynamic)optional3.get());
        }
        if ((optional = dynamic2.get("LIGHT").result()).isPresent()) {
            dynamic2 = dynamic2.remove("LIGHT");
            dynamic2 = dynamic2.set("LIGHT_BLOCKING", (Dynamic)optional.get());
        }
        if ((optional2 = dynamic2.get("RAIN").result()).isPresent()) {
            dynamic2 = dynamic2.remove("RAIN");
            dynamic2 = dynamic2.set("MOTION_BLOCKING", (Dynamic)optional2.get());
            dynamic2 = dynamic2.set("MOTION_BLOCKING_NO_LEAVES", (Dynamic)optional2.get());
        }
        return dynamic.set("Heightmaps", dynamic2);
    }
}

