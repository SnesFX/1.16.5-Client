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
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class ItemPotionFix
extends DataFix {
    private static final String[] POTIONS = (String[])DataFixUtils.make((Object)new String[128], arrstring -> {
        arrstring[0] = "minecraft:water";
        arrstring[1] = "minecraft:regeneration";
        arrstring[2] = "minecraft:swiftness";
        arrstring[3] = "minecraft:fire_resistance";
        arrstring[4] = "minecraft:poison";
        arrstring[5] = "minecraft:healing";
        arrstring[6] = "minecraft:night_vision";
        arrstring[7] = null;
        arrstring[8] = "minecraft:weakness";
        arrstring[9] = "minecraft:strength";
        arrstring[10] = "minecraft:slowness";
        arrstring[11] = "minecraft:leaping";
        arrstring[12] = "minecraft:harming";
        arrstring[13] = "minecraft:water_breathing";
        arrstring[14] = "minecraft:invisibility";
        arrstring[15] = null;
        arrstring[16] = "minecraft:awkward";
        arrstring[17] = "minecraft:regeneration";
        arrstring[18] = "minecraft:swiftness";
        arrstring[19] = "minecraft:fire_resistance";
        arrstring[20] = "minecraft:poison";
        arrstring[21] = "minecraft:healing";
        arrstring[22] = "minecraft:night_vision";
        arrstring[23] = null;
        arrstring[24] = "minecraft:weakness";
        arrstring[25] = "minecraft:strength";
        arrstring[26] = "minecraft:slowness";
        arrstring[27] = "minecraft:leaping";
        arrstring[28] = "minecraft:harming";
        arrstring[29] = "minecraft:water_breathing";
        arrstring[30] = "minecraft:invisibility";
        arrstring[31] = null;
        arrstring[32] = "minecraft:thick";
        arrstring[33] = "minecraft:strong_regeneration";
        arrstring[34] = "minecraft:strong_swiftness";
        arrstring[35] = "minecraft:fire_resistance";
        arrstring[36] = "minecraft:strong_poison";
        arrstring[37] = "minecraft:strong_healing";
        arrstring[38] = "minecraft:night_vision";
        arrstring[39] = null;
        arrstring[40] = "minecraft:weakness";
        arrstring[41] = "minecraft:strong_strength";
        arrstring[42] = "minecraft:slowness";
        arrstring[43] = "minecraft:strong_leaping";
        arrstring[44] = "minecraft:strong_harming";
        arrstring[45] = "minecraft:water_breathing";
        arrstring[46] = "minecraft:invisibility";
        arrstring[47] = null;
        arrstring[48] = null;
        arrstring[49] = "minecraft:strong_regeneration";
        arrstring[50] = "minecraft:strong_swiftness";
        arrstring[51] = "minecraft:fire_resistance";
        arrstring[52] = "minecraft:strong_poison";
        arrstring[53] = "minecraft:strong_healing";
        arrstring[54] = "minecraft:night_vision";
        arrstring[55] = null;
        arrstring[56] = "minecraft:weakness";
        arrstring[57] = "minecraft:strong_strength";
        arrstring[58] = "minecraft:slowness";
        arrstring[59] = "minecraft:strong_leaping";
        arrstring[60] = "minecraft:strong_harming";
        arrstring[61] = "minecraft:water_breathing";
        arrstring[62] = "minecraft:invisibility";
        arrstring[63] = null;
        arrstring[64] = "minecraft:mundane";
        arrstring[65] = "minecraft:long_regeneration";
        arrstring[66] = "minecraft:long_swiftness";
        arrstring[67] = "minecraft:long_fire_resistance";
        arrstring[68] = "minecraft:long_poison";
        arrstring[69] = "minecraft:healing";
        arrstring[70] = "minecraft:long_night_vision";
        arrstring[71] = null;
        arrstring[72] = "minecraft:long_weakness";
        arrstring[73] = "minecraft:long_strength";
        arrstring[74] = "minecraft:long_slowness";
        arrstring[75] = "minecraft:long_leaping";
        arrstring[76] = "minecraft:harming";
        arrstring[77] = "minecraft:long_water_breathing";
        arrstring[78] = "minecraft:long_invisibility";
        arrstring[79] = null;
        arrstring[80] = "minecraft:awkward";
        arrstring[81] = "minecraft:long_regeneration";
        arrstring[82] = "minecraft:long_swiftness";
        arrstring[83] = "minecraft:long_fire_resistance";
        arrstring[84] = "minecraft:long_poison";
        arrstring[85] = "minecraft:healing";
        arrstring[86] = "minecraft:long_night_vision";
        arrstring[87] = null;
        arrstring[88] = "minecraft:long_weakness";
        arrstring[89] = "minecraft:long_strength";
        arrstring[90] = "minecraft:long_slowness";
        arrstring[91] = "minecraft:long_leaping";
        arrstring[92] = "minecraft:harming";
        arrstring[93] = "minecraft:long_water_breathing";
        arrstring[94] = "minecraft:long_invisibility";
        arrstring[95] = null;
        arrstring[96] = "minecraft:thick";
        arrstring[97] = "minecraft:regeneration";
        arrstring[98] = "minecraft:swiftness";
        arrstring[99] = "minecraft:long_fire_resistance";
        arrstring[100] = "minecraft:poison";
        arrstring[101] = "minecraft:strong_healing";
        arrstring[102] = "minecraft:long_night_vision";
        arrstring[103] = null;
        arrstring[104] = "minecraft:long_weakness";
        arrstring[105] = "minecraft:strength";
        arrstring[106] = "minecraft:long_slowness";
        arrstring[107] = "minecraft:leaping";
        arrstring[108] = "minecraft:strong_harming";
        arrstring[109] = "minecraft:long_water_breathing";
        arrstring[110] = "minecraft:long_invisibility";
        arrstring[111] = null;
        arrstring[112] = null;
        arrstring[113] = "minecraft:regeneration";
        arrstring[114] = "minecraft:swiftness";
        arrstring[115] = "minecraft:long_fire_resistance";
        arrstring[116] = "minecraft:poison";
        arrstring[117] = "minecraft:strong_healing";
        arrstring[118] = "minecraft:long_night_vision";
        arrstring[119] = null;
        arrstring[120] = "minecraft:long_weakness";
        arrstring[121] = "minecraft:strength";
        arrstring[122] = "minecraft:long_slowness";
        arrstring[123] = "minecraft:leaping";
        arrstring[124] = "minecraft:strong_harming";
        arrstring[125] = "minecraft:long_water_breathing";
        arrstring[126] = "minecraft:long_invisibility";
        arrstring[127] = null;
    });

    public ItemPotionFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    public TypeRewriteRule makeRule() {
        Type type = this.getInputSchema().getType(References.ITEM_STACK);
        OpticFinder opticFinder = DSL.fieldFinder((String)"id", (Type)DSL.named((String)References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString()));
        OpticFinder opticFinder2 = type.findField("tag");
        return this.fixTypeEverywhereTyped("ItemPotionFix", type, typed -> {
            Optional optional = typed.getOptional(opticFinder);
            if (optional.isPresent() && Objects.equals(((Pair)optional.get()).getSecond(), "minecraft:potion")) {
                Dynamic dynamic = (Dynamic)typed.get(DSL.remainderFinder());
                Optional optional2 = typed.getOptionalTyped(opticFinder2);
                short s = dynamic.get("Damage").asShort((short)0);
                if (optional2.isPresent()) {
                    Typed typed2 = typed;
                    Dynamic dynamic2 = (Dynamic)((Typed)optional2.get()).get(DSL.remainderFinder());
                    Optional optional3 = dynamic2.get("Potion").asString().result();
                    if (!optional3.isPresent()) {
                        String string;
                        Typed typed3 = ((Typed)optional2.get()).set(DSL.remainderFinder(), (Object)dynamic2.set("Potion", dynamic2.createString((string = POTIONS[s & 0x7F]) == null ? "minecraft:water" : string)));
                        typed2 = typed2.set(opticFinder2, typed3);
                        if ((s & 0x4000) == 16384) {
                            typed2 = typed2.set(opticFinder, (Object)Pair.of((Object)References.ITEM_NAME.typeName(), (Object)"minecraft:splash_potion"));
                        }
                    }
                    if (s != 0) {
                        dynamic = dynamic.set("Damage", dynamic.createShort((short)0));
                    }
                    return typed2.set(DSL.remainderFinder(), (Object)dynamic);
                }
            }
            return typed;
        });
    }
}

