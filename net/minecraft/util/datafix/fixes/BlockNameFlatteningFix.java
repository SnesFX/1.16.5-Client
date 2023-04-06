/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.util.Either
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DynamicOps;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.BlockStateData;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class BlockNameFlatteningFix
extends DataFix {
    public BlockNameFlatteningFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    public TypeRewriteRule makeRule() {
        Type type = this.getInputSchema().getType(References.BLOCK_NAME);
        Type type2 = this.getOutputSchema().getType(References.BLOCK_NAME);
        Type type3 = DSL.named((String)References.BLOCK_NAME.typeName(), (Type)DSL.or((Type)DSL.intType(), NamespacedSchema.namespacedString()));
        Type type4 = DSL.named((String)References.BLOCK_NAME.typeName(), NamespacedSchema.namespacedString());
        if (!Objects.equals((Object)type, (Object)type3) || !Objects.equals((Object)type2, (Object)type4)) {
            throw new IllegalStateException("Expected and actual types don't match.");
        }
        return this.fixTypeEverywhere("BlockNameFlatteningFix", type3, type4, dynamicOps -> pair -> pair.mapSecond(either -> (String)either.map(BlockStateData::upgradeBlock, string -> BlockStateData.upgradeBlock(NamespacedSchema.ensureNamespaced(string)))));
    }
}

