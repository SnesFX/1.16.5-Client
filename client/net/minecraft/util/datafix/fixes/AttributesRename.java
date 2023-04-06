/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicLike
 */
package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicLike;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.datafix.fixes.References;

public class AttributesRename
extends DataFix {
    private static final Map<String, String> RENAMES = ImmutableMap.builder().put((Object)"generic.maxHealth", (Object)"generic.max_health").put((Object)"Max Health", (Object)"generic.max_health").put((Object)"zombie.spawnReinforcements", (Object)"zombie.spawn_reinforcements").put((Object)"Spawn Reinforcements Chance", (Object)"zombie.spawn_reinforcements").put((Object)"horse.jumpStrength", (Object)"horse.jump_strength").put((Object)"Jump Strength", (Object)"horse.jump_strength").put((Object)"generic.followRange", (Object)"generic.follow_range").put((Object)"Follow Range", (Object)"generic.follow_range").put((Object)"generic.knockbackResistance", (Object)"generic.knockback_resistance").put((Object)"Knockback Resistance", (Object)"generic.knockback_resistance").put((Object)"generic.movementSpeed", (Object)"generic.movement_speed").put((Object)"Movement Speed", (Object)"generic.movement_speed").put((Object)"generic.flyingSpeed", (Object)"generic.flying_speed").put((Object)"Flying Speed", (Object)"generic.flying_speed").put((Object)"generic.attackDamage", (Object)"generic.attack_damage").put((Object)"generic.attackKnockback", (Object)"generic.attack_knockback").put((Object)"generic.attackSpeed", (Object)"generic.attack_speed").put((Object)"generic.armorToughness", (Object)"generic.armor_toughness").build();

    public AttributesRename(Schema schema) {
        super(schema, false);
    }

    protected TypeRewriteRule makeRule() {
        Type type = this.getInputSchema().getType(References.ITEM_STACK);
        OpticFinder opticFinder = type.findField("tag");
        return TypeRewriteRule.seq((TypeRewriteRule)this.fixTypeEverywhereTyped("Rename ItemStack Attributes", type, typed -> typed.updateTyped(opticFinder, AttributesRename::fixItemStackTag)), (TypeRewriteRule[])new TypeRewriteRule[]{this.fixTypeEverywhereTyped("Rename Entity Attributes", this.getInputSchema().getType(References.ENTITY), AttributesRename::fixEntity), this.fixTypeEverywhereTyped("Rename Player Attributes", this.getInputSchema().getType(References.PLAYER), AttributesRename::fixEntity)});
    }

    private static Dynamic<?> fixName(Dynamic<?> dynamic) {
        return (Dynamic)DataFixUtils.orElse(dynamic.asString().result().map(string -> RENAMES.getOrDefault(string, (String)string)).map(dynamic::createString), dynamic);
    }

    private static Typed<?> fixItemStackTag(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), dynamic2 -> dynamic2.update("AttributeModifiers", dynamic -> (Dynamic)DataFixUtils.orElse(dynamic.asStreamOpt().result().map(stream -> stream.map(dynamic -> dynamic.update("AttributeName", AttributesRename::fixName))).map(((Dynamic)dynamic)::createList), (Object)dynamic)));
    }

    private static Typed<?> fixEntity(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), dynamic2 -> dynamic2.update("Attributes", dynamic -> (Dynamic)DataFixUtils.orElse(dynamic.asStreamOpt().result().map(stream -> stream.map(dynamic -> dynamic.update("Name", AttributesRename::fixName))).map(((Dynamic)dynamic)::createList), (Object)dynamic)));
    }
}

