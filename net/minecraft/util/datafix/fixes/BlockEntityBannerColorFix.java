/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicLike
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicLike;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.datafix.fixes.NamedEntityFix;
import net.minecraft.util.datafix.fixes.References;

public class BlockEntityBannerColorFix
extends NamedEntityFix {
    public BlockEntityBannerColorFix(Schema schema, boolean bl) {
        super(schema, bl, "BlockEntityBannerColorFix", References.BLOCK_ENTITY, "minecraft:banner");
    }

    public Dynamic<?> fixTag(Dynamic<?> dynamic2) {
        dynamic2 = dynamic2.update("Base", dynamic -> dynamic.createInt(15 - dynamic.asInt(0)));
        dynamic2 = dynamic2.update("Patterns", dynamic -> (Dynamic)DataFixUtils.orElse((Optional)dynamic.asStreamOpt().map(stream -> stream.map(dynamic2 -> dynamic2.update("Color", dynamic -> dynamic.createInt(15 - dynamic.asInt(0))))).map(((Dynamic)dynamic)::createList).result(), (Object)dynamic));
        return dynamic2;
    }

    @Override
    protected Typed<?> fix(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), this::fixTag);
    }
}

