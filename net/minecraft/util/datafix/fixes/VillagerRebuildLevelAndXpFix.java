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
 *  com.mojang.datafixers.types.templates.List
 *  com.mojang.datafixers.types.templates.List$ListType
 *  com.mojang.serialization.DataResult
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
import com.mojang.datafixers.types.templates.List;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.fixes.References;

public class VillagerRebuildLevelAndXpFix
extends DataFix {
    private static final int[] LEVEL_XP_THRESHOLDS = new int[]{0, 10, 50, 100, 150};

    public static int getMinXpPerLevel(int n) {
        return LEVEL_XP_THRESHOLDS[Mth.clamp(n - 1, 0, LEVEL_XP_THRESHOLDS.length - 1)];
    }

    public VillagerRebuildLevelAndXpFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    public TypeRewriteRule makeRule() {
        Type type = this.getInputSchema().getChoiceType(References.ENTITY, "minecraft:villager");
        OpticFinder opticFinder = DSL.namedChoice((String)"minecraft:villager", (Type)type);
        OpticFinder opticFinder2 = type.findField("Offers");
        Type type2 = opticFinder2.type();
        OpticFinder opticFinder3 = type2.findField("Recipes");
        List.ListType listType = (List.ListType)opticFinder3.type();
        OpticFinder opticFinder4 = listType.getElement().finder();
        return this.fixTypeEverywhereTyped("Villager level and xp rebuild", this.getInputSchema().getType(References.ENTITY), typed -> typed.updateTyped(opticFinder, type, typed2 -> {
            Optional optional;
            int n;
            Dynamic dynamic = (Dynamic)typed2.get(DSL.remainderFinder());
            int n2 = dynamic.get("VillagerData").get("level").asInt(0);
            Typed<?> typed3 = typed2;
            if ((n2 == 0 || n2 == 1) && (n2 = Mth.clamp((n = typed2.getOptionalTyped(opticFinder2).flatMap(typed -> typed.getOptionalTyped(opticFinder3)).map(typed -> typed.getAllTyped(opticFinder4).size()).orElse(0).intValue()) / 2, 1, 5)) > 1) {
                typed3 = VillagerRebuildLevelAndXpFix.addLevel(typed3, n2);
            }
            if (!(optional = dynamic.get("Xp").asNumber().result()).isPresent()) {
                typed3 = VillagerRebuildLevelAndXpFix.addXpFromLevel(typed3, n2);
            }
            return typed3;
        }));
    }

    private static Typed<?> addLevel(Typed<?> typed, int n) {
        return typed.update(DSL.remainderFinder(), dynamic2 -> dynamic2.update("VillagerData", dynamic -> dynamic.set("level", dynamic.createInt(n))));
    }

    private static Typed<?> addXpFromLevel(Typed<?> typed, int n) {
        int n2 = VillagerRebuildLevelAndXpFix.getMinXpPerLevel(n);
        return typed.update(DSL.remainderFinder(), dynamic -> dynamic.set("Xp", dynamic.createInt(n2)));
    }
}

