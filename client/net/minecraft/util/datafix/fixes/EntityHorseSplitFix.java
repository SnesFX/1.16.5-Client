/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.types.templates.TaggedChoice
 *  com.mojang.datafixers.types.templates.TaggedChoice$TaggedChoiceType
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.OptionalDynamic
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.EntityRenameFix;
import net.minecraft.util.datafix.fixes.References;

public class EntityHorseSplitFix
extends EntityRenameFix {
    public EntityHorseSplitFix(Schema schema, boolean bl) {
        super("EntityHorseSplitFix", schema, bl);
    }

    @Override
    protected Pair<String, Typed<?>> fix(String string, Typed<?> typed) {
        Dynamic dynamic = (Dynamic)typed.get(DSL.remainderFinder());
        if (Objects.equals("EntityHorse", string)) {
            String string2;
            int n = dynamic.get("Type").asInt(0);
            switch (n) {
                default: {
                    string2 = "Horse";
                    break;
                }
                case 1: {
                    string2 = "Donkey";
                    break;
                }
                case 2: {
                    string2 = "Mule";
                    break;
                }
                case 3: {
                    string2 = "ZombieHorse";
                    break;
                }
                case 4: {
                    string2 = "SkeletonHorse";
                }
            }
            dynamic.remove("Type");
            Type type = (Type)this.getOutputSchema().findChoiceType(References.ENTITY).types().get(string2);
            return Pair.of((Object)string2, (Object)((Pair)typed.write().flatMap(((Type)type)::readTyped).result().orElseThrow(() -> new IllegalStateException("Could not parse the new horse"))).getFirst());
        }
        return Pair.of((Object)string, typed);
    }
}

