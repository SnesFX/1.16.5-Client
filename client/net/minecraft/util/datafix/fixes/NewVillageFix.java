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
 *  com.mojang.datafixers.types.templates.CompoundList
 *  com.mojang.datafixers.types.templates.CompoundList$CompoundListType
 *  com.mojang.datafixers.util.Pair
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
import com.mojang.datafixers.types.templates.CompoundList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class NewVillageFix
extends DataFix {
    public NewVillageFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    protected TypeRewriteRule makeRule() {
        CompoundList.CompoundListType compoundListType = DSL.compoundList((Type)DSL.string(), (Type)this.getInputSchema().getType(References.STRUCTURE_FEATURE));
        OpticFinder opticFinder = compoundListType.finder();
        return this.cap(compoundListType);
    }

    private <SF> TypeRewriteRule cap(CompoundList.CompoundListType<String, SF> compoundListType) {
        Type type = this.getInputSchema().getType(References.CHUNK);
        Type type2 = this.getInputSchema().getType(References.STRUCTURE_FEATURE);
        OpticFinder opticFinder = type.findField("Level");
        OpticFinder opticFinder2 = opticFinder.type().findField("Structures");
        OpticFinder opticFinder3 = opticFinder2.type().findField("Starts");
        OpticFinder opticFinder4 = compoundListType.finder();
        return TypeRewriteRule.seq((TypeRewriteRule)this.fixTypeEverywhereTyped("NewVillageFix", type, typed2 -> typed2.updateTyped(opticFinder, typed -> typed.updateTyped(opticFinder2, typed2 -> typed2.updateTyped(opticFinder3, typed -> typed.update(opticFinder4, list -> list.stream().filter(pair -> !Objects.equals(pair.getFirst(), "Village")).map(pair -> pair.mapFirst(string -> string.equals("New_Village") ? "Village" : string)).collect(Collectors.toList()))).update(DSL.remainderFinder(), dynamic2 -> dynamic2.update("References", dynamic -> {
            Optional optional = dynamic.get("New_Village").result();
            return ((Dynamic)DataFixUtils.orElse(optional.map(dynamic2 -> dynamic.remove("New_Village").set("Village", dynamic2)), (Object)dynamic)).remove("Village");
        }))))), (TypeRewriteRule)this.fixTypeEverywhereTyped("NewVillageStartFix", type2, typed -> typed.update(DSL.remainderFinder(), dynamic2 -> dynamic2.update("id", dynamic -> Objects.equals(NamespacedSchema.ensureNamespaced(dynamic.asString("")), "minecraft:new_village") ? dynamic.createString("minecraft:village") : dynamic))));
    }
}

