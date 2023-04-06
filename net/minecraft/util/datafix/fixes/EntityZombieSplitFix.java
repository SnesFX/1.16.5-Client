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

public class EntityZombieSplitFix
extends SimpleEntityRenameFix {
    public EntityZombieSplitFix(Schema schema, boolean bl) {
        super("EntityZombieSplitFix", schema, bl);
    }

    @Override
    protected Pair<String, Dynamic<?>> getNewNameAndTag(String string, Dynamic<?> dynamic) {
        if (Objects.equals("Zombie", string)) {
            String string2 = "Zombie";
            int n = dynamic.get("ZombieType").asInt(0);
            switch (n) {
                default: {
                    break;
                }
                case 1: 
                case 2: 
                case 3: 
                case 4: 
                case 5: {
                    string2 = "ZombieVillager";
                    dynamic = dynamic.set("Profession", dynamic.createInt(n - 1));
                    break;
                }
                case 6: {
                    string2 = "Husk";
                }
            }
            dynamic = dynamic.remove("ZombieType");
            return Pair.of((Object)string2, (Object)dynamic);
        }
        return Pair.of((Object)string, dynamic);
    }
}

