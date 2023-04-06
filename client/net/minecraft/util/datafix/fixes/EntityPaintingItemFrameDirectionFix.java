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
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.References;

public class EntityPaintingItemFrameDirectionFix
extends DataFix {
    private static final int[][] DIRECTIONS = new int[][]{{0, 0, 1}, {-1, 0, 0}, {0, 0, -1}, {1, 0, 0}};

    public EntityPaintingItemFrameDirectionFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    private Dynamic<?> doFix(Dynamic<?> dynamic, boolean bl, boolean bl2) {
        if ((bl || bl2) && !dynamic.get("Facing").asNumber().result().isPresent()) {
            int n;
            if (dynamic.get("Direction").asNumber().result().isPresent()) {
                n = dynamic.get("Direction").asByte((byte)0) % DIRECTIONS.length;
                int[] arrn = DIRECTIONS[n];
                dynamic = dynamic.set("TileX", dynamic.createInt(dynamic.get("TileX").asInt(0) + arrn[0]));
                dynamic = dynamic.set("TileY", dynamic.createInt(dynamic.get("TileY").asInt(0) + arrn[1]));
                dynamic = dynamic.set("TileZ", dynamic.createInt(dynamic.get("TileZ").asInt(0) + arrn[2]));
                dynamic = dynamic.remove("Direction");
                if (bl2 && dynamic.get("ItemRotation").asNumber().result().isPresent()) {
                    dynamic = dynamic.set("ItemRotation", dynamic.createByte((byte)(dynamic.get("ItemRotation").asByte((byte)0) * 2)));
                }
            } else {
                n = dynamic.get("Dir").asByte((byte)0) % DIRECTIONS.length;
                dynamic = dynamic.remove("Dir");
            }
            dynamic = dynamic.set("Facing", dynamic.createByte((byte)n));
        }
        return dynamic;
    }

    public TypeRewriteRule makeRule() {
        Type type = this.getInputSchema().getChoiceType(References.ENTITY, "Painting");
        OpticFinder opticFinder = DSL.namedChoice((String)"Painting", (Type)type);
        Type type2 = this.getInputSchema().getChoiceType(References.ENTITY, "ItemFrame");
        OpticFinder opticFinder2 = DSL.namedChoice((String)"ItemFrame", (Type)type2);
        Type type3 = this.getInputSchema().getType(References.ENTITY);
        TypeRewriteRule typeRewriteRule = this.fixTypeEverywhereTyped("EntityPaintingFix", type3, typed2 -> typed2.updateTyped(opticFinder, type, typed -> typed.update(DSL.remainderFinder(), dynamic -> this.doFix((Dynamic<?>)dynamic, true, false))));
        TypeRewriteRule typeRewriteRule2 = this.fixTypeEverywhereTyped("EntityItemFrameFix", type3, typed2 -> typed2.updateTyped(opticFinder2, type2, typed -> typed.update(DSL.remainderFinder(), dynamic -> this.doFix((Dynamic<?>)dynamic, false, true))));
        return TypeRewriteRule.seq((TypeRewriteRule)typeRewriteRule, (TypeRewriteRule)typeRewriteRule2);
    }
}

