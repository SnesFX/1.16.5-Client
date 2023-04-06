/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.OptionalDynamic
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import java.util.Objects;
import net.minecraft.util.datafix.fixes.SimpleEntityRenameFix;

public class EntityCatSplitFix
extends SimpleEntityRenameFix {
    public EntityCatSplitFix(Schema schema, boolean bl) {
        super("EntityCatSplitFix", schema, bl);
    }

    @Override
    protected Pair<String, Dynamic<?>> getNewNameAndTag(String string, Dynamic<?> dynamic) {
        if (Objects.equals("minecraft:ocelot", string)) {
            int n = dynamic.get("CatType").asInt(0);
            if (n == 0) {
                String string2 = dynamic.get("Owner").asString("");
                String string3 = dynamic.get("OwnerUUID").asString("");
                if (string2.length() > 0 || string3.length() > 0) {
                    dynamic.set("Trusting", dynamic.createBoolean(true));
                }
            } else if (n > 0 && n < 4) {
                dynamic = dynamic.set("CatType", dynamic.createInt(n));
                dynamic = dynamic.set("OwnerUUID", dynamic.createString(dynamic.get("OwnerUUID").asString("")));
                return Pair.of((Object)"minecraft:cat", (Object)dynamic);
            }
        }
        return Pair.of((Object)string, dynamic);
    }
}

