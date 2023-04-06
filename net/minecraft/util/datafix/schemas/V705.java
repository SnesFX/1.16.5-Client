/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.templates.Hook
 *  com.mojang.datafixers.types.templates.Hook$HookFunction
 *  com.mojang.datafixers.types.templates.TypeTemplate
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.util.datafix.schemas;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.Hook;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;
import net.minecraft.util.datafix.schemas.V100;
import net.minecraft.util.datafix.schemas.V704;
import net.minecraft.util.datafix.schemas.V99;

public class V705
extends NamespacedSchema {
    protected static final Hook.HookFunction ADD_NAMES = new Hook.HookFunction(){

        public <T> T apply(DynamicOps<T> dynamicOps, T t) {
            return V99.addNames(new Dynamic(dynamicOps, t), V704.ITEM_TO_BLOCKENTITY, "minecraft:armor_stand");
        }
    };

    public V705(int n, Schema schema) {
        super(n, schema);
    }

    protected static void registerMob(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
        schema.register(map, string, () -> V100.equipment(schema));
    }

    protected static void registerThrowableProjectile(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
        schema.register(map, string, () -> DSL.optionalFields((String)"inTile", (TypeTemplate)References.BLOCK_NAME.in(schema)));
    }

    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
        HashMap hashMap = Maps.newHashMap();
        schema.registerSimple((Map)hashMap, "minecraft:area_effect_cloud");
        V705.registerMob(schema, hashMap, "minecraft:armor_stand");
        schema.register((Map)hashMap, "minecraft:arrow", string -> DSL.optionalFields((String)"inTile", (TypeTemplate)References.BLOCK_NAME.in(schema)));
        V705.registerMob(schema, hashMap, "minecraft:bat");
        V705.registerMob(schema, hashMap, "minecraft:blaze");
        schema.registerSimple((Map)hashMap, "minecraft:boat");
        V705.registerMob(schema, hashMap, "minecraft:cave_spider");
        schema.register((Map)hashMap, "minecraft:chest_minecart", string -> DSL.optionalFields((String)"DisplayTile", (TypeTemplate)References.BLOCK_NAME.in(schema), (String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in(schema))));
        V705.registerMob(schema, hashMap, "minecraft:chicken");
        schema.register((Map)hashMap, "minecraft:commandblock_minecart", string -> DSL.optionalFields((String)"DisplayTile", (TypeTemplate)References.BLOCK_NAME.in(schema)));
        V705.registerMob(schema, hashMap, "minecraft:cow");
        V705.registerMob(schema, hashMap, "minecraft:creeper");
        schema.register((Map)hashMap, "minecraft:donkey", string -> DSL.optionalFields((String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in(schema)), (String)"SaddleItem", (TypeTemplate)References.ITEM_STACK.in(schema), (TypeTemplate)V100.equipment(schema)));
        schema.registerSimple((Map)hashMap, "minecraft:dragon_fireball");
        V705.registerThrowableProjectile(schema, hashMap, "minecraft:egg");
        V705.registerMob(schema, hashMap, "minecraft:elder_guardian");
        schema.registerSimple((Map)hashMap, "minecraft:ender_crystal");
        V705.registerMob(schema, hashMap, "minecraft:ender_dragon");
        schema.register((Map)hashMap, "minecraft:enderman", string -> DSL.optionalFields((String)"carried", (TypeTemplate)References.BLOCK_NAME.in(schema), (TypeTemplate)V100.equipment(schema)));
        V705.registerMob(schema, hashMap, "minecraft:endermite");
        V705.registerThrowableProjectile(schema, hashMap, "minecraft:ender_pearl");
        schema.registerSimple((Map)hashMap, "minecraft:eye_of_ender_signal");
        schema.register((Map)hashMap, "minecraft:falling_block", string -> DSL.optionalFields((String)"Block", (TypeTemplate)References.BLOCK_NAME.in(schema), (String)"TileEntityData", (TypeTemplate)References.BLOCK_ENTITY.in(schema)));
        V705.registerThrowableProjectile(schema, hashMap, "minecraft:fireball");
        schema.register((Map)hashMap, "minecraft:fireworks_rocket", string -> DSL.optionalFields((String)"FireworksItem", (TypeTemplate)References.ITEM_STACK.in(schema)));
        schema.register((Map)hashMap, "minecraft:furnace_minecart", string -> DSL.optionalFields((String)"DisplayTile", (TypeTemplate)References.BLOCK_NAME.in(schema)));
        V705.registerMob(schema, hashMap, "minecraft:ghast");
        V705.registerMob(schema, hashMap, "minecraft:giant");
        V705.registerMob(schema, hashMap, "minecraft:guardian");
        schema.register((Map)hashMap, "minecraft:hopper_minecart", string -> DSL.optionalFields((String)"DisplayTile", (TypeTemplate)References.BLOCK_NAME.in(schema), (String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in(schema))));
        schema.register((Map)hashMap, "minecraft:horse", string -> DSL.optionalFields((String)"ArmorItem", (TypeTemplate)References.ITEM_STACK.in(schema), (String)"SaddleItem", (TypeTemplate)References.ITEM_STACK.in(schema), (TypeTemplate)V100.equipment(schema)));
        V705.registerMob(schema, hashMap, "minecraft:husk");
        schema.register((Map)hashMap, "minecraft:item", string -> DSL.optionalFields((String)"Item", (TypeTemplate)References.ITEM_STACK.in(schema)));
        schema.register((Map)hashMap, "minecraft:item_frame", string -> DSL.optionalFields((String)"Item", (TypeTemplate)References.ITEM_STACK.in(schema)));
        schema.registerSimple((Map)hashMap, "minecraft:leash_knot");
        V705.registerMob(schema, hashMap, "minecraft:magma_cube");
        schema.register((Map)hashMap, "minecraft:minecart", string -> DSL.optionalFields((String)"DisplayTile", (TypeTemplate)References.BLOCK_NAME.in(schema)));
        V705.registerMob(schema, hashMap, "minecraft:mooshroom");
        schema.register((Map)hashMap, "minecraft:mule", string -> DSL.optionalFields((String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in(schema)), (String)"SaddleItem", (TypeTemplate)References.ITEM_STACK.in(schema), (TypeTemplate)V100.equipment(schema)));
        V705.registerMob(schema, hashMap, "minecraft:ocelot");
        schema.registerSimple((Map)hashMap, "minecraft:painting");
        schema.registerSimple((Map)hashMap, "minecraft:parrot");
        V705.registerMob(schema, hashMap, "minecraft:pig");
        V705.registerMob(schema, hashMap, "minecraft:polar_bear");
        schema.register((Map)hashMap, "minecraft:potion", string -> DSL.optionalFields((String)"Potion", (TypeTemplate)References.ITEM_STACK.in(schema), (String)"inTile", (TypeTemplate)References.BLOCK_NAME.in(schema)));
        V705.registerMob(schema, hashMap, "minecraft:rabbit");
        V705.registerMob(schema, hashMap, "minecraft:sheep");
        V705.registerMob(schema, hashMap, "minecraft:shulker");
        schema.registerSimple((Map)hashMap, "minecraft:shulker_bullet");
        V705.registerMob(schema, hashMap, "minecraft:silverfish");
        V705.registerMob(schema, hashMap, "minecraft:skeleton");
        schema.register((Map)hashMap, "minecraft:skeleton_horse", string -> DSL.optionalFields((String)"SaddleItem", (TypeTemplate)References.ITEM_STACK.in(schema), (TypeTemplate)V100.equipment(schema)));
        V705.registerMob(schema, hashMap, "minecraft:slime");
        V705.registerThrowableProjectile(schema, hashMap, "minecraft:small_fireball");
        V705.registerThrowableProjectile(schema, hashMap, "minecraft:snowball");
        V705.registerMob(schema, hashMap, "minecraft:snowman");
        schema.register((Map)hashMap, "minecraft:spawner_minecart", string -> DSL.optionalFields((String)"DisplayTile", (TypeTemplate)References.BLOCK_NAME.in(schema), (TypeTemplate)References.UNTAGGED_SPAWNER.in(schema)));
        schema.register((Map)hashMap, "minecraft:spectral_arrow", string -> DSL.optionalFields((String)"inTile", (TypeTemplate)References.BLOCK_NAME.in(schema)));
        V705.registerMob(schema, hashMap, "minecraft:spider");
        V705.registerMob(schema, hashMap, "minecraft:squid");
        V705.registerMob(schema, hashMap, "minecraft:stray");
        schema.registerSimple((Map)hashMap, "minecraft:tnt");
        schema.register((Map)hashMap, "minecraft:tnt_minecart", string -> DSL.optionalFields((String)"DisplayTile", (TypeTemplate)References.BLOCK_NAME.in(schema)));
        schema.register((Map)hashMap, "minecraft:villager", string -> DSL.optionalFields((String)"Inventory", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in(schema)), (String)"Offers", (TypeTemplate)DSL.optionalFields((String)"Recipes", (TypeTemplate)DSL.list((TypeTemplate)DSL.optionalFields((String)"buy", (TypeTemplate)References.ITEM_STACK.in(schema), (String)"buyB", (TypeTemplate)References.ITEM_STACK.in(schema), (String)"sell", (TypeTemplate)References.ITEM_STACK.in(schema)))), (TypeTemplate)V100.equipment(schema)));
        V705.registerMob(schema, hashMap, "minecraft:villager_golem");
        V705.registerMob(schema, hashMap, "minecraft:witch");
        V705.registerMob(schema, hashMap, "minecraft:wither");
        V705.registerMob(schema, hashMap, "minecraft:wither_skeleton");
        V705.registerThrowableProjectile(schema, hashMap, "minecraft:wither_skull");
        V705.registerMob(schema, hashMap, "minecraft:wolf");
        V705.registerThrowableProjectile(schema, hashMap, "minecraft:xp_bottle");
        schema.registerSimple((Map)hashMap, "minecraft:xp_orb");
        V705.registerMob(schema, hashMap, "minecraft:zombie");
        schema.register((Map)hashMap, "minecraft:zombie_horse", string -> DSL.optionalFields((String)"SaddleItem", (TypeTemplate)References.ITEM_STACK.in(schema), (TypeTemplate)V100.equipment(schema)));
        V705.registerMob(schema, hashMap, "minecraft:zombie_pigman");
        V705.registerMob(schema, hashMap, "minecraft:zombie_villager");
        schema.registerSimple((Map)hashMap, "minecraft:evocation_fangs");
        V705.registerMob(schema, hashMap, "minecraft:evocation_illager");
        schema.registerSimple((Map)hashMap, "minecraft:illusion_illager");
        schema.register((Map)hashMap, "minecraft:llama", string -> DSL.optionalFields((String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in(schema)), (String)"SaddleItem", (TypeTemplate)References.ITEM_STACK.in(schema), (String)"DecorItem", (TypeTemplate)References.ITEM_STACK.in(schema), (TypeTemplate)V100.equipment(schema)));
        schema.registerSimple((Map)hashMap, "minecraft:llama_spit");
        V705.registerMob(schema, hashMap, "minecraft:vex");
        V705.registerMob(schema, hashMap, "minecraft:vindication_illager");
        return hashMap;
    }

    public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map2) {
        super.registerTypes(schema, map, map2);
        schema.registerType(true, References.ENTITY, () -> DSL.taggedChoiceLazy((String)"id", V705.namespacedString(), (Map)map));
        schema.registerType(true, References.ITEM_STACK, () -> DSL.hook((TypeTemplate)DSL.optionalFields((String)"id", (TypeTemplate)References.ITEM_NAME.in(schema), (String)"tag", (TypeTemplate)DSL.optionalFields((String)"EntityTag", (TypeTemplate)References.ENTITY_TREE.in(schema), (String)"BlockEntityTag", (TypeTemplate)References.BLOCK_ENTITY.in(schema), (String)"CanDestroy", (TypeTemplate)DSL.list((TypeTemplate)References.BLOCK_NAME.in(schema)), (String)"CanPlaceOn", (TypeTemplate)DSL.list((TypeTemplate)References.BLOCK_NAME.in(schema)))), (Hook.HookFunction)ADD_NAMES, (Hook.HookFunction)Hook.HookFunction.IDENTITY));
    }

}

