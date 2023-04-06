/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.datafix.fixes.EntityRenameFix;

public abstract class SimpleEntityRenameFix
extends EntityRenameFix {
    public SimpleEntityRenameFix(String string, Schema schema, boolean bl) {
        super(string, schema, bl);
    }

    @Override
    protected Pair<String, Typed<?>> fix(String string, Typed<?> typed) {
        Pair<String, Dynamic<?>> pair = this.getNewNameAndTag(string, (Dynamic)typed.getOrCreate(DSL.remainderFinder()));
        return Pair.of((Object)pair.getFirst(), (Object)typed.set(DSL.remainderFinder(), pair.getSecond()));
    }

    protected abstract Pair<String, Dynamic<?>> getNewNameAndTag(String var1, Dynamic<?> var2);
}

