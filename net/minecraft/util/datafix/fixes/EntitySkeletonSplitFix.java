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

public class EntitySkeletonSplitFix
extends SimpleEntityRenameFix {
    public EntitySkeletonSplitFix(Schema schema, boolean bl) {
        super("EntitySkeletonSplitFix", schema, bl);
    }

    @Override
    protected Pair<String, Dynamic<?>> getNewNameAndTag(String string, Dynamic<?> dynamic) {
        if (Objects.equals(string, "Skeleton")) {
            int n = dynamic.get("SkeletonType").asInt(0);
            if (n == 1) {
                string = "WitherSkeleton";
            } else if (n == 2) {
                string = "Stray";
            }
        }
        return Pair.of((Object)string, dynamic);
    }
}

