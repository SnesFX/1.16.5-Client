/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.OptionalDynamic
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.NamedEntityFix;
import net.minecraft.util.datafix.fixes.References;

public class JigsawPropertiesFix
extends NamedEntityFix {
    public JigsawPropertiesFix(Schema schema, boolean bl) {
        super(schema, bl, "JigsawPropertiesFix", References.BLOCK_ENTITY, "minecraft:jigsaw");
    }

    private static Dynamic<?> fixTag(Dynamic<?> dynamic) {
        String string = dynamic.get("attachement_type").asString("minecraft:empty");
        String string2 = dynamic.get("target_pool").asString("minecraft:empty");
        return dynamic.set("name", dynamic.createString(string)).set("target", dynamic.createString(string)).remove("attachement_type").set("pool", dynamic.createString(string2)).remove("target_pool");
    }

    @Override
    protected Typed<?> fix(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), JigsawPropertiesFix::fixTag);
    }
}

