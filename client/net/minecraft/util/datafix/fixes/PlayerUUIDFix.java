/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.AbstractUUIDFix;
import net.minecraft.util.datafix.fixes.EntityUUIDFix;
import net.minecraft.util.datafix.fixes.References;

public class PlayerUUIDFix
extends AbstractUUIDFix {
    public PlayerUUIDFix(Schema schema) {
        super(schema, References.PLAYER);
    }

    protected TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("PlayerUUIDFix", this.getInputSchema().getType(this.typeReference), typed2 -> {
            OpticFinder opticFinder = typed2.getType().findField("RootVehicle");
            return typed2.updateTyped(opticFinder, opticFinder.type(), typed -> typed.update(DSL.remainderFinder(), dynamic -> PlayerUUIDFix.replaceUUIDLeastMost(dynamic, "Attach", "Attach").orElse((Dynamic<?>)dynamic))).update(DSL.remainderFinder(), dynamic -> EntityUUIDFix.updateEntityUUID(EntityUUIDFix.updateLivingEntity(dynamic)));
        });
    }
}

