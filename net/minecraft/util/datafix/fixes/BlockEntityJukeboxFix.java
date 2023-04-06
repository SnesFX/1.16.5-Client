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
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.ItemIdFix;
import net.minecraft.util.datafix.fixes.ItemStackTheFlatteningFix;
import net.minecraft.util.datafix.fixes.NamedEntityFix;
import net.minecraft.util.datafix.fixes.References;

public class BlockEntityJukeboxFix
extends NamedEntityFix {
    public BlockEntityJukeboxFix(Schema schema, boolean bl) {
        super(schema, bl, "BlockEntityJukeboxFix", References.BLOCK_ENTITY, "minecraft:jukebox");
    }

    @Override
    protected Typed<?> fix(Typed<?> typed) {
        Type type = this.getInputSchema().getChoiceType(References.BLOCK_ENTITY, "minecraft:jukebox");
        Type type2 = type.findFieldType("RecordItem");
        OpticFinder opticFinder = DSL.fieldFinder((String)"RecordItem", (Type)type2);
        Dynamic dynamic = (Dynamic)typed.get(DSL.remainderFinder());
        int n = dynamic.get("Record").asInt(0);
        if (n > 0) {
            dynamic.remove("Record");
            String string = ItemStackTheFlatteningFix.updateItem(ItemIdFix.getItem(n), 0);
            if (string != null) {
                Dynamic dynamic2 = dynamic.emptyMap();
                dynamic2 = dynamic2.set("id", dynamic2.createString(string));
                dynamic2 = dynamic2.set("Count", dynamic2.createByte((byte)1));
                return typed.set(opticFinder, (Typed)((Pair)type2.readTyped(dynamic2).result().orElseThrow(() -> new IllegalStateException("Could not create record item stack."))).getFirst()).set(DSL.remainderFinder(), (Object)dynamic);
            }
        }
        return typed;
    }
}

