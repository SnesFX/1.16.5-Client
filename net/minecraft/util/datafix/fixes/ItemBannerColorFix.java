/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
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

import com.google.common.collect.ImmutableMap;
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
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class ItemBannerColorFix
extends DataFix {
    public ItemBannerColorFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    public TypeRewriteRule makeRule() {
        Type type = this.getInputSchema().getType(References.ITEM_STACK);
        OpticFinder opticFinder = DSL.fieldFinder((String)"id", (Type)DSL.named((String)References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString()));
        OpticFinder opticFinder2 = type.findField("tag");
        OpticFinder opticFinder3 = opticFinder2.type().findField("BlockEntityTag");
        return this.fixTypeEverywhereTyped("ItemBannerColorFix", type, typed -> {
            Optional optional = typed.getOptional(opticFinder);
            if (optional.isPresent() && Objects.equals(((Pair)optional.get()).getSecond(), "minecraft:banner")) {
                Optional optional2;
                Typed typed2;
                Dynamic dynamic = (Dynamic)typed.get(DSL.remainderFinder());
                Optional optional3 = typed.getOptionalTyped(opticFinder2);
                if (optional3.isPresent() && (optional2 = (typed2 = (Typed)optional3.get()).getOptionalTyped(opticFinder3)).isPresent()) {
                    Typed typed3 = (Typed)optional2.get();
                    Dynamic dynamic2 = (Dynamic)typed2.get(DSL.remainderFinder());
                    Dynamic dynamic3 = (Dynamic)typed3.getOrCreate(DSL.remainderFinder());
                    if (dynamic3.get("Base").asNumber().result().isPresent()) {
                        Dynamic dynamic4;
                        Dynamic dynamic5;
                        dynamic = dynamic.set("Damage", dynamic.createShort((short)(dynamic3.get("Base").asInt(0) & 0xF)));
                        Optional optional4 = dynamic2.get("display").result();
                        if (optional4.isPresent() && Objects.equals((Object)(dynamic5 = (Dynamic)optional4.get()), (Object)(dynamic4 = dynamic5.createMap((Map)ImmutableMap.of((Object)dynamic5.createString("Lore"), (Object)dynamic5.createList(Stream.of(dynamic5.createString("(+NBT")))))))) {
                            return typed.set(DSL.remainderFinder(), (Object)dynamic);
                        }
                        dynamic3.remove("Base");
                        return typed.set(DSL.remainderFinder(), (Object)dynamic).set(opticFinder2, typed2.set(opticFinder3, typed3.set(DSL.remainderFinder(), (Object)dynamic3)));
                    }
                }
                return typed.set(DSL.remainderFinder(), (Object)dynamic);
            }
            return typed;
        });
    }
}

