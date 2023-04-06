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
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class OminousBannerRenameFix
extends DataFix {
    public OminousBannerRenameFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    private Dynamic<?> fixTag(Dynamic<?> dynamic) {
        Optional optional = dynamic.get("display").result();
        if (optional.isPresent()) {
            Dynamic dynamic2 = (Dynamic)optional.get();
            Optional optional2 = dynamic2.get("Name").asString().result();
            if (optional2.isPresent()) {
                String string = (String)optional2.get();
                string = string.replace("\"translate\":\"block.minecraft.illager_banner\"", "\"translate\":\"block.minecraft.ominous_banner\"");
                dynamic2 = dynamic2.set("Name", dynamic2.createString(string));
            }
            return dynamic.set("display", dynamic2);
        }
        return dynamic;
    }

    public TypeRewriteRule makeRule() {
        Type type = this.getInputSchema().getType(References.ITEM_STACK);
        OpticFinder opticFinder = DSL.fieldFinder((String)"id", (Type)DSL.named((String)References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString()));
        OpticFinder opticFinder2 = type.findField("tag");
        return this.fixTypeEverywhereTyped("OminousBannerRenameFix", type, typed -> {
            Optional optional;
            Optional optional2 = typed.getOptional(opticFinder);
            if (optional2.isPresent() && Objects.equals(((Pair)optional2.get()).getSecond(), "minecraft:white_banner") && (optional = typed.getOptionalTyped(opticFinder2)).isPresent()) {
                Typed typed2 = (Typed)optional.get();
                Dynamic dynamic = (Dynamic)typed2.get(DSL.remainderFinder());
                return typed.set(opticFinder2, typed2.set(DSL.remainderFinder(), this.fixTag(dynamic)));
            }
            return typed;
        });
    }
}

