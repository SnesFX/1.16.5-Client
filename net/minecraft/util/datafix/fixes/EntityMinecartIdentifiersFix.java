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
 *  com.mojang.datafixers.types.templates.TaggedChoice
 *  com.mojang.datafixers.types.templates.TaggedChoice$TaggedChoiceType
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
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
import com.mojang.datafixers.types.templates.TaggedChoice;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.OptionalDynamic;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class EntityMinecartIdentifiersFix
extends DataFix {
    private static final List<String> MINECART_BY_ID = Lists.newArrayList((Object[])new String[]{"MinecartRideable", "MinecartChest", "MinecartFurnace"});

    public EntityMinecartIdentifiersFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    public TypeRewriteRule makeRule() {
        TaggedChoice.TaggedChoiceType taggedChoiceType = this.getInputSchema().findChoiceType(References.ENTITY);
        TaggedChoice.TaggedChoiceType taggedChoiceType2 = this.getOutputSchema().findChoiceType(References.ENTITY);
        return this.fixTypeEverywhere("EntityMinecartIdentifiersFix", (Type)taggedChoiceType, (Type)taggedChoiceType2, dynamicOps -> pair -> {
            if (Objects.equals(pair.getFirst(), "Minecart")) {
                Typed typed = (Typed)taggedChoiceType.point(dynamicOps, (Object)"Minecart", pair.getSecond()).orElseThrow(IllegalStateException::new);
                Dynamic dynamic2 = (Dynamic)typed.getOrCreate(DSL.remainderFinder());
                int n = dynamic2.get("Type").asInt(0);
                String string = n > 0 && n < MINECART_BY_ID.size() ? MINECART_BY_ID.get(n) : "MinecartRideable";
                return Pair.of((Object)string, typed.write().map(dynamic -> ((Type)taggedChoiceType2.types().get(string)).read(dynamic)).result().orElseThrow(() -> new IllegalStateException("Could not read the new minecart.")));
            }
            return pair;
        });
    }
}

