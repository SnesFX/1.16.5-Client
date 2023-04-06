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
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.OptionalDynamic
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.OptionalDynamic;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.NamedEntityFix;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class EntityHorseSaddleFix
extends NamedEntityFix {
    public EntityHorseSaddleFix(Schema schema, boolean bl) {
        super(schema, bl, "EntityHorseSaddleFix", References.ENTITY, "EntityHorse");
    }

    @Override
    protected Typed<?> fix(Typed<?> typed) {
        OpticFinder opticFinder = DSL.fieldFinder((String)"id", (Type)DSL.named((String)References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString()));
        Type type = this.getInputSchema().getTypeRaw(References.ITEM_STACK);
        OpticFinder opticFinder2 = DSL.fieldFinder((String)"SaddleItem", (Type)type);
        Optional optional = typed.getOptionalTyped(opticFinder2);
        Dynamic dynamic = (Dynamic)typed.get(DSL.remainderFinder());
        if (!optional.isPresent() && dynamic.get("Saddle").asBoolean(false)) {
            Typed typed2 = (Typed)type.pointTyped(typed.getOps()).orElseThrow(IllegalStateException::new);
            typed2 = typed2.set(opticFinder, (Object)Pair.of((Object)References.ITEM_NAME.typeName(), (Object)"minecraft:saddle"));
            Dynamic dynamic2 = dynamic.emptyMap();
            dynamic2 = dynamic2.set("Count", dynamic2.createByte((byte)1));
            dynamic2 = dynamic2.set("Damage", dynamic2.createShort((short)0));
            typed2 = typed2.set(DSL.remainderFinder(), (Object)dynamic2);
            dynamic.remove("Saddle");
            return typed.set(opticFinder2, typed2).set(DSL.remainderFinder(), (Object)dynamic);
        }
        return typed;
    }
}

