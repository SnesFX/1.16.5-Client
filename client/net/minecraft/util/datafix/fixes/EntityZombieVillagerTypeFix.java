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
 *  com.mojang.serialization.OptionalDynamic
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.NamedEntityFix;
import net.minecraft.util.datafix.fixes.References;

public class EntityZombieVillagerTypeFix
extends NamedEntityFix {
    private static final Random RANDOM = new Random();

    public EntityZombieVillagerTypeFix(Schema schema, boolean bl) {
        super(schema, bl, "EntityZombieVillagerTypeFix", References.ENTITY, "Zombie");
    }

    public Dynamic<?> fixTag(Dynamic<?> dynamic) {
        if (dynamic.get("IsVillager").asBoolean(false)) {
            if (!dynamic.get("ZombieType").result().isPresent()) {
                int n = this.getVillagerProfession(dynamic.get("VillagerProfession").asInt(-1));
                if (n == -1) {
                    n = this.getVillagerProfession(RANDOM.nextInt(6));
                }
                dynamic = dynamic.set("ZombieType", dynamic.createInt(n));
            }
            dynamic = dynamic.remove("IsVillager");
        }
        return dynamic;
    }

    private int getVillagerProfession(int n) {
        if (n < 0 || n >= 6) {
            return -1;
        }
        return n;
    }

    @Override
    protected Typed<?> fix(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), this::fixTag);
    }
}

