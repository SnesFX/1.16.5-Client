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
 *  com.mojang.datafixers.util.Pair
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
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class ItemWaterPotionFix
extends DataFix {
    public ItemWaterPotionFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    public TypeRewriteRule makeRule() {
        Type type = this.getInputSchema().getType(References.ITEM_STACK);
        OpticFinder opticFinder = DSL.fieldFinder((String)"id", (Type)DSL.named((String)References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString()));
        OpticFinder opticFinder2 = type.findField("tag");
        return this.fixTypeEverywhereTyped("ItemWaterPotionFix", type, typed -> {
            String string;
            Optional optional = typed.getOptional(opticFinder);
            if (optional.isPresent() && ("minecraft:potion".equals(string = (String)((Pair)optional.get()).getSecond()) || "minecraft:splash_potion".equals(string) || "minecraft:lingering_potion".equals(string) || "minecraft:tipped_arrow".equals(string))) {
                Typed typed2 = typed.getOrCreateTyped(opticFinder2);
                Dynamic dynamic = (Dynamic)typed2.get(DSL.remainderFinder());
                if (!dynamic.get("Potion").asString().result().isPresent()) {
                    dynamic = dynamic.set("Potion", dynamic.createString("minecraft:water"));
                }
                return typed.set(opticFinder2, typed2.set(DSL.remainderFinder(), (Object)dynamic));
            }
            return typed;
        });
    }
}

