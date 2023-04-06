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
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.NamedEntityFix;
import net.minecraft.util.datafix.fixes.References;

public class BlockEntityKeepPacked
extends NamedEntityFix {
    public BlockEntityKeepPacked(Schema schema, boolean bl) {
        super(schema, bl, "BlockEntityKeepPacked", References.BLOCK_ENTITY, "DUMMY");
    }

    private static Dynamic<?> fixTag(Dynamic<?> dynamic) {
        return dynamic.set("keepPacked", dynamic.createBoolean(true));
    }

    @Override
    protected Typed<?> fix(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), BlockEntityKeepPacked::fixTag);
    }
}

