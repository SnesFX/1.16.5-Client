/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DSL$TypeReference
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
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.AbstractUUIDFix;
import net.minecraft.util.datafix.fixes.References;

public class BlockEntityUUIDFix
extends AbstractUUIDFix {
    public BlockEntityUUIDFix(Schema schema) {
        super(schema, References.BLOCK_ENTITY);
    }

    protected TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("BlockEntityUUIDFix", this.getInputSchema().getType(this.typeReference), typed -> {
            typed = this.updateNamedChoice((Typed<?>)typed, "minecraft:conduit", this::updateConduit);
            typed = this.updateNamedChoice((Typed<?>)typed, "minecraft:skull", this::updateSkull);
            return typed;
        });
    }

    private Dynamic<?> updateSkull(Dynamic<?> dynamic3) {
        return dynamic3.get("Owner").get().map(dynamic -> BlockEntityUUIDFix.replaceUUIDString(dynamic, "Id", "Id").orElse((Dynamic<?>)dynamic)).map(dynamic2 -> dynamic3.remove("Owner").set("SkullOwner", dynamic2)).result().orElse(dynamic3);
    }

    private Dynamic<?> updateConduit(Dynamic<?> dynamic) {
        return BlockEntityUUIDFix.replaceUUIDMLTag(dynamic, "target_uuid", "Target").orElse(dynamic);
    }
}

