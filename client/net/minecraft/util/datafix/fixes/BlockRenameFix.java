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
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
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
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.OptionalDynamic;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public abstract class BlockRenameFix
extends DataFix {
    private final String name;

    public BlockRenameFix(Schema schema, String string) {
        super(schema, false);
        this.name = string;
    }

    public TypeRewriteRule makeRule() {
        Type type;
        Type type2 = this.getInputSchema().getType(References.BLOCK_NAME);
        if (!Objects.equals((Object)type2, (Object)(type = DSL.named((String)References.BLOCK_NAME.typeName(), NamespacedSchema.namespacedString())))) {
            throw new IllegalStateException("block type is not what was expected.");
        }
        TypeRewriteRule typeRewriteRule = this.fixTypeEverywhere(this.name + " for block", type, dynamicOps -> pair -> pair.mapSecond(this::fixBlock));
        TypeRewriteRule typeRewriteRule2 = this.fixTypeEverywhereTyped(this.name + " for block_state", this.getInputSchema().getType(References.BLOCK_STATE), typed -> typed.update(DSL.remainderFinder(), dynamic -> {
            Optional optional = dynamic.get("Name").asString().result();
            if (optional.isPresent()) {
                return dynamic.set("Name", dynamic.createString(this.fixBlock((String)optional.get())));
            }
            return dynamic;
        }));
        return TypeRewriteRule.seq((TypeRewriteRule)typeRewriteRule, (TypeRewriteRule)typeRewriteRule2);
    }

    protected abstract String fixBlock(String var1);

    public static DataFix create(Schema schema, String string, final Function<String, String> function) {
        return new BlockRenameFix(schema, string){

            @Override
            protected String fixBlock(String string) {
                return (String)function.apply(string);
            }
        };
    }

}

