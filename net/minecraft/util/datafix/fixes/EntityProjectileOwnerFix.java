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
 *  com.mojang.serialization.Dynamic
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
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.IntStream;
import net.minecraft.util.datafix.fixes.References;

public class EntityProjectileOwnerFix
extends DataFix {
    public EntityProjectileOwnerFix(Schema schema) {
        super(schema, false);
    }

    protected TypeRewriteRule makeRule() {
        Schema schema = this.getInputSchema();
        return this.fixTypeEverywhereTyped("EntityProjectileOwner", schema.getType(References.ENTITY), this::updateProjectiles);
    }

    private Typed<?> updateProjectiles(Typed<?> typed) {
        typed = this.updateEntity(typed, "minecraft:egg", this::updateOwnerThrowable);
        typed = this.updateEntity(typed, "minecraft:ender_pearl", this::updateOwnerThrowable);
        typed = this.updateEntity(typed, "minecraft:experience_bottle", this::updateOwnerThrowable);
        typed = this.updateEntity(typed, "minecraft:snowball", this::updateOwnerThrowable);
        typed = this.updateEntity(typed, "minecraft:potion", this::updateOwnerThrowable);
        typed = this.updateEntity(typed, "minecraft:potion", this::updateItemPotion);
        typed = this.updateEntity(typed, "minecraft:llama_spit", this::updateOwnerLlamaSpit);
        typed = this.updateEntity(typed, "minecraft:arrow", this::updateOwnerArrow);
        typed = this.updateEntity(typed, "minecraft:spectral_arrow", this::updateOwnerArrow);
        typed = this.updateEntity(typed, "minecraft:trident", this::updateOwnerArrow);
        return typed;
    }

    private Dynamic<?> updateOwnerArrow(Dynamic<?> dynamic) {
        long l = dynamic.get("OwnerUUIDMost").asLong(0L);
        long l2 = dynamic.get("OwnerUUIDLeast").asLong(0L);
        return this.setUUID(dynamic, l, l2).remove("OwnerUUIDMost").remove("OwnerUUIDLeast");
    }

    private Dynamic<?> updateOwnerLlamaSpit(Dynamic<?> dynamic) {
        OptionalDynamic optionalDynamic = dynamic.get("Owner");
        long l = optionalDynamic.get("OwnerUUIDMost").asLong(0L);
        long l2 = optionalDynamic.get("OwnerUUIDLeast").asLong(0L);
        return this.setUUID(dynamic, l, l2).remove("Owner");
    }

    private Dynamic<?> updateItemPotion(Dynamic<?> dynamic) {
        OptionalDynamic optionalDynamic = dynamic.get("Potion");
        return dynamic.set("Item", optionalDynamic.orElseEmptyMap()).remove("Potion");
    }

    private Dynamic<?> updateOwnerThrowable(Dynamic<?> dynamic) {
        String string = "owner";
        OptionalDynamic optionalDynamic = dynamic.get("owner");
        long l = optionalDynamic.get("M").asLong(0L);
        long l2 = optionalDynamic.get("L").asLong(0L);
        return this.setUUID(dynamic, l, l2).remove("owner");
    }

    private Dynamic<?> setUUID(Dynamic<?> dynamic, long l, long l2) {
        String string = "OwnerUUID";
        if (l != 0L && l2 != 0L) {
            return dynamic.set("OwnerUUID", dynamic.createIntList(Arrays.stream(EntityProjectileOwnerFix.createUUIDArray(l, l2))));
        }
        return dynamic;
    }

    private static int[] createUUIDArray(long l, long l2) {
        return new int[]{(int)(l >> 32), (int)l, (int)(l2 >> 32), (int)l2};
    }

    private Typed<?> updateEntity(Typed<?> typed2, String string, Function<Dynamic<?>, Dynamic<?>> function) {
        Type type = this.getInputSchema().getChoiceType(References.ENTITY, string);
        Type type2 = this.getOutputSchema().getChoiceType(References.ENTITY, string);
        return typed2.updateTyped(DSL.namedChoice((String)string, (Type)type), type2, typed -> typed.update(DSL.remainderFinder(), function));
    }
}

