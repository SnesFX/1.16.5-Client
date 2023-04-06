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
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DynamicOps;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public abstract class ItemRenameFix
extends DataFix {
    private final String name;

    public ItemRenameFix(Schema schema, String string) {
        super(schema, false);
        this.name = string;
    }

    public TypeRewriteRule makeRule() {
        Type type = DSL.named((String)References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString());
        if (!Objects.equals((Object)this.getInputSchema().getType(References.ITEM_NAME), (Object)type)) {
            throw new IllegalStateException("item name type is not what was expected.");
        }
        return this.fixTypeEverywhere(this.name, type, dynamicOps -> pair -> pair.mapSecond(this::fixItem));
    }

    protected abstract String fixItem(String var1);

    public static DataFix create(Schema schema, String string, final Function<String, String> function) {
        return new ItemRenameFix(schema, string){

            @Override
            protected String fixItem(String string) {
                return (String)function.apply(string);
            }
        };
    }

}

