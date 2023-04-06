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
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.util.datafix.fixes.References;

public class RedstoneWireConnectionsFix
extends DataFix {
    public RedstoneWireConnectionsFix(Schema schema) {
        super(schema, false);
    }

    protected TypeRewriteRule makeRule() {
        Schema schema = this.getInputSchema();
        return this.fixTypeEverywhereTyped("RedstoneConnectionsFix", schema.getType(References.BLOCK_STATE), typed -> typed.update(DSL.remainderFinder(), this::updateRedstoneConnections));
    }

    private <T> Dynamic<T> updateRedstoneConnections(Dynamic<T> dynamic) {
        boolean bl = dynamic.get("Name").asString().result().filter("minecraft:redstone_wire"::equals).isPresent();
        if (!bl) {
            return dynamic;
        }
        return dynamic.update("Properties", dynamic2 -> {
            String string = dynamic2.get("east").asString("none");
            String string2 = dynamic2.get("west").asString("none");
            String string3 = dynamic2.get("north").asString("none");
            String string4 = dynamic2.get("south").asString("none");
            boolean bl = RedstoneWireConnectionsFix.isConnected(string) || RedstoneWireConnectionsFix.isConnected(string2);
            boolean bl2 = RedstoneWireConnectionsFix.isConnected(string3) || RedstoneWireConnectionsFix.isConnected(string4);
            String string5 = !RedstoneWireConnectionsFix.isConnected(string) && !bl2 ? "side" : string;
            String string6 = !RedstoneWireConnectionsFix.isConnected(string2) && !bl2 ? "side" : string2;
            String string7 = !RedstoneWireConnectionsFix.isConnected(string3) && !bl ? "side" : string3;
            String string8 = !RedstoneWireConnectionsFix.isConnected(string4) && !bl ? "side" : string4;
            return dynamic2.update("east", dynamic -> dynamic.createString(string5)).update("west", dynamic -> dynamic.createString(string6)).update("north", dynamic -> dynamic.createString(string7)).update("south", dynamic -> dynamic.createString(string8));
        });
    }

    private static boolean isConnected(String string) {
        return !"none".equals(string);
    }
}

