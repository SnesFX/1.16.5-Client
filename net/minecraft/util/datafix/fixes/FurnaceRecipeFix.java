/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.util.Either
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.datafixers.util.Unit
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.OptionalDynamic
 */
package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.References;

public class FurnaceRecipeFix
extends DataFix {
    public FurnaceRecipeFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    protected TypeRewriteRule makeRule() {
        return this.cap(this.getOutputSchema().getTypeRaw(References.RECIPE));
    }

    private <R> TypeRewriteRule cap(Type<R> type) {
        Type type2 = DSL.and((Type)DSL.optional((Type)DSL.field((String)"RecipesUsed", (Type)DSL.and((Type)DSL.compoundList(type, (Type)DSL.intType()), (Type)DSL.remainderType()))), (Type)DSL.remainderType());
        OpticFinder opticFinder = DSL.namedChoice((String)"minecraft:furnace", (Type)this.getInputSchema().getChoiceType(References.BLOCK_ENTITY, "minecraft:furnace"));
        OpticFinder opticFinder2 = DSL.namedChoice((String)"minecraft:blast_furnace", (Type)this.getInputSchema().getChoiceType(References.BLOCK_ENTITY, "minecraft:blast_furnace"));
        OpticFinder opticFinder3 = DSL.namedChoice((String)"minecraft:smoker", (Type)this.getInputSchema().getChoiceType(References.BLOCK_ENTITY, "minecraft:smoker"));
        Type type3 = this.getOutputSchema().getChoiceType(References.BLOCK_ENTITY, "minecraft:furnace");
        Type type4 = this.getOutputSchema().getChoiceType(References.BLOCK_ENTITY, "minecraft:blast_furnace");
        Type type5 = this.getOutputSchema().getChoiceType(References.BLOCK_ENTITY, "minecraft:smoker");
        Type type6 = this.getInputSchema().getType(References.BLOCK_ENTITY);
        Type type7 = this.getOutputSchema().getType(References.BLOCK_ENTITY);
        return this.fixTypeEverywhereTyped("FurnaceRecipesFix", type6, type7, typed2 -> typed2.updateTyped(opticFinder, type3, typed -> this.updateFurnaceContents(type, (Type<Pair<Either<Pair<List<Pair<R, Integer>>, Dynamic<?>>, Unit>, Dynamic<?>>>)type2, (Typed<?>)typed)).updateTyped(opticFinder2, type4, typed -> this.updateFurnaceContents(type, (Type<Pair<Either<Pair<List<Pair<R, Integer>>, Dynamic<?>>, Unit>, Dynamic<?>>>)type2, (Typed<?>)typed)).updateTyped(opticFinder3, type5, typed -> this.updateFurnaceContents(type, (Type<Pair<Either<Pair<List<Pair<R, Integer>>, Dynamic<?>>, Unit>, Dynamic<?>>>)type2, (Typed<?>)typed)));
    }

    private <R> Typed<?> updateFurnaceContents(Type<R> type, Type<Pair<Either<Pair<List<Pair<R, Integer>>, Dynamic<?>>, Unit>, Dynamic<?>>> type2, Typed<?> typed) {
        Dynamic dynamic2 = (Dynamic)typed.getOrCreate(DSL.remainderFinder());
        int n = dynamic2.get("RecipesUsedSize").asInt(0);
        dynamic2 = dynamic2.remove("RecipesUsedSize");
        ArrayList arrayList = Lists.newArrayList();
        for (int i = 0; i < n; ++i) {
            String string = "RecipeLocation" + i;
            String string2 = "RecipeAmount" + i;
            Optional optional = dynamic2.get(string).result();
            int n2 = dynamic2.get(string2).asInt(0);
            if (n2 > 0) {
                optional.ifPresent(dynamic -> {
                    Optional optional = type.read(dynamic).result();
                    optional.ifPresent(pair -> arrayList.add(Pair.of((Object)pair.getFirst(), (Object)n2)));
                });
            }
            dynamic2 = dynamic2.remove(string).remove(string2);
        }
        return typed.set(DSL.remainderFinder(), type2, (Object)Pair.of((Object)Either.left((Object)Pair.of((Object)arrayList, (Object)dynamic2.emptyMap())), (Object)dynamic2));
    }
}

