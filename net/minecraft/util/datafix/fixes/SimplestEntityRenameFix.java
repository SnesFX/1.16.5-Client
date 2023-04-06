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
 *  com.mojang.datafixers.types.templates.TaggedChoice
 *  com.mojang.datafixers.types.templates.TaggedChoice$TaggedChoiceType
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DynamicOps;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public abstract class SimplestEntityRenameFix
extends DataFix {
    private final String name;

    public SimplestEntityRenameFix(String string, Schema schema, boolean bl) {
        super(schema, bl);
        this.name = string;
    }

    public TypeRewriteRule makeRule() {
        TaggedChoice.TaggedChoiceType taggedChoiceType = this.getInputSchema().findChoiceType(References.ENTITY);
        TaggedChoice.TaggedChoiceType taggedChoiceType2 = this.getOutputSchema().findChoiceType(References.ENTITY);
        Type type = DSL.named((String)References.ENTITY_NAME.typeName(), NamespacedSchema.namespacedString());
        if (!Objects.equals((Object)this.getOutputSchema().getType(References.ENTITY_NAME), (Object)type)) {
            throw new IllegalStateException("Entity name type is not what was expected.");
        }
        return TypeRewriteRule.seq((TypeRewriteRule)this.fixTypeEverywhere(this.name, (Type)taggedChoiceType, (Type)taggedChoiceType2, dynamicOps -> pair -> pair.mapFirst(string -> {
            String string2 = this.rename((String)string);
            Type type = (Type)taggedChoiceType.types().get(string);
            Type type2 = (Type)taggedChoiceType2.types().get(string2);
            if (!type2.equals((Object)type, true, true)) {
                throw new IllegalStateException(String.format("Dynamic type check failed: %s not equal to %s", new Object[]{type2, type}));
            }
            return string2;
        })), (TypeRewriteRule)this.fixTypeEverywhere(this.name + " for entity name", type, dynamicOps -> pair -> pair.mapSecond(this::rename)));
    }

    protected abstract String rename(String var1);
}

