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
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class ItemShulkerBoxColorFix
extends DataFix {
    public static final String[] NAMES_BY_COLOR = new String[]{"minecraft:white_shulker_box", "minecraft:orange_shulker_box", "minecraft:magenta_shulker_box", "minecraft:light_blue_shulker_box", "minecraft:yellow_shulker_box", "minecraft:lime_shulker_box", "minecraft:pink_shulker_box", "minecraft:gray_shulker_box", "minecraft:silver_shulker_box", "minecraft:cyan_shulker_box", "minecraft:purple_shulker_box", "minecraft:blue_shulker_box", "minecraft:brown_shulker_box", "minecraft:green_shulker_box", "minecraft:red_shulker_box", "minecraft:black_shulker_box"};

    public ItemShulkerBoxColorFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    public TypeRewriteRule makeRule() {
        Type type = this.getInputSchema().getType(References.ITEM_STACK);
        OpticFinder opticFinder = DSL.fieldFinder((String)"id", (Type)DSL.named((String)References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString()));
        OpticFinder opticFinder2 = type.findField("tag");
        OpticFinder opticFinder3 = opticFinder2.type().findField("BlockEntityTag");
        return this.fixTypeEverywhereTyped("ItemShulkerBoxColorFix", type, typed -> {
            Typed typed2;
            Optional optional;
            Optional optional2;
            Optional optional3 = typed.getOptional(opticFinder);
            if (optional3.isPresent() && Objects.equals(((Pair)optional3.get()).getSecond(), "minecraft:shulker_box") && (optional2 = typed.getOptionalTyped(opticFinder2)).isPresent() && (optional = (typed2 = (Typed)optional2.get()).getOptionalTyped(opticFinder3)).isPresent()) {
                Typed typed3 = (Typed)optional.get();
                Dynamic dynamic = (Dynamic)typed3.get(DSL.remainderFinder());
                int n = dynamic.get("Color").asInt(0);
                dynamic.remove("Color");
                return typed.set(opticFinder2, typed2.set(opticFinder3, typed3.set(DSL.remainderFinder(), (Object)dynamic))).set(opticFinder, (Object)Pair.of((Object)References.ITEM_NAME.typeName(), (Object)NAMES_BY_COLOR[n % 16]));
            }
            return typed;
        });
    }
}

