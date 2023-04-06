/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.OptionalDynamic
 */
package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import java.util.Map;
import java.util.Optional;
import net.minecraft.util.datafix.fixes.NamedEntityFix;
import net.minecraft.util.datafix.fixes.References;

public class VillagerDataFix
extends NamedEntityFix {
    public VillagerDataFix(Schema schema, String string) {
        super(schema, false, "Villager profession data fix (" + string + ")", References.ENTITY, string);
    }

    @Override
    protected Typed<?> fix(Typed<?> typed) {
        Dynamic dynamic = (Dynamic)typed.get(DSL.remainderFinder());
        return typed.set(DSL.remainderFinder(), (Object)dynamic.remove("Profession").remove("Career").remove("CareerLevel").set("VillagerData", dynamic.createMap((Map)ImmutableMap.of((Object)dynamic.createString("type"), (Object)dynamic.createString("minecraft:plains"), (Object)dynamic.createString("profession"), (Object)dynamic.createString(VillagerDataFix.upgradeData(dynamic.get("Profession").asInt(0), dynamic.get("Career").asInt(0))), (Object)dynamic.createString("level"), (Object)DataFixUtils.orElse((Optional)dynamic.get("CareerLevel").result(), (Object)dynamic.createInt(1))))));
    }

    private static String upgradeData(int n, int n2) {
        if (n == 0) {
            if (n2 == 2) {
                return "minecraft:fisherman";
            }
            if (n2 == 3) {
                return "minecraft:shepherd";
            }
            if (n2 == 4) {
                return "minecraft:fletcher";
            }
            return "minecraft:farmer";
        }
        if (n == 1) {
            if (n2 == 2) {
                return "minecraft:cartographer";
            }
            return "minecraft:librarian";
        }
        if (n == 2) {
            return "minecraft:cleric";
        }
        if (n == 3) {
            if (n2 == 2) {
                return "minecraft:weaponsmith";
            }
            if (n2 == 3) {
                return "minecraft:toolsmith";
            }
            return "minecraft:armorer";
        }
        if (n == 4) {
            if (n2 == 2) {
                return "minecraft:leatherworker";
            }
            return "minecraft:butcher";
        }
        if (n == 5) {
            return "minecraft:nitwit";
        }
        return "minecraft:none";
    }
}

