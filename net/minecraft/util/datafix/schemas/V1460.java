/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.types.templates.Hook
 *  com.mojang.datafixers.types.templates.Hook$HookFunction
 *  com.mojang.datafixers.types.templates.TypeTemplate
 */
package net.minecraft.util.datafix.schemas;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.Hook;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;
import net.minecraft.util.datafix.schemas.V100;
import net.minecraft.util.datafix.schemas.V705;

public class V1460
extends NamespacedSchema {
    public V1460(int n, Schema schema) {
        super(n, schema);
    }

    protected static void registerMob(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
        schema.register(map, string, () -> V100.equipment(schema));
    }

    protected static void registerInventory(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
        schema.register(map, string, () -> DSL.optionalFields((String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in(schema))));
    }

    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
        HashMap hashMap = Maps.newHashMap();
        schema.registerSimple((Map)hashMap, "minecraft:area_effect_cloud");
        V1460.registerMob(schema, hashMap, "minecraft:armor_stand");
        schema.register((Map)hashMap, "minecraft:arrow", string -> DSL.optionalFields((String)"inBlockState", (TypeTemplate)References.BLOCK_STATE.in(schema)));
        V1460.registerMob(schema, hashMap, "minecraft:bat");
        V1460.registerMob(schema, hashMap, "minecraft:blaze");
        schema.registerSimple((Map)hashMap, "minecraft:boat");
        V1460.registerMob(schema, hashMap, "minecraft:cave_spider");
        schema.register((Map)hashMap, "minecraft:chest_minecart", string -> DSL.optionalFields((String)"DisplayState", (TypeTemplate)References.BLOCK_STATE.in(schema), (String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in(schema))));
        V1460.registerMob(schema, hashMap, "minecraft:chicken");
        schema.register((Map)hashMap, "minecraft:commandblock_minecart", string -> DSL.optionalFields((String)"DisplayState", (TypeTemplate)References.BLOCK_STATE.in(schema)));
        V1460.registerMob(schema, hashMap, "minecraft:cow");
        V1460.registerMob(schema, hashMap, "minecraft:creeper");
        schema.register((Map)hashMap, "minecraft:donkey", string -> DSL.optionalFields((String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in(schema)), (String)"SaddleItem", (TypeTemplate)References.ITEM_STACK.in(schema), (TypeTemplate)V100.equipment(schema)));
        schema.registerSimple((Map)hashMap, "minecraft:dragon_fireball");
        schema.registerSimple((Map)hashMap, "minecraft:egg");
        V1460.registerMob(schema, hashMap, "minecraft:elder_guardian");
        schema.registerSimple((Map)hashMap, "minecraft:ender_crystal");
        V1460.registerMob(schema, hashMap, "minecraft:ender_dragon");
        schema.register((Map)hashMap, "minecraft:enderman", string -> DSL.optionalFields((String)"carriedBlockState", (TypeTemplate)References.BLOCK_STATE.in(schema), (TypeTemplate)V100.equipment(schema)));
        V1460.registerMob(schema, hashMap, "minecraft:endermite");
        schema.registerSimple((Map)hashMap, "minecraft:ender_pearl");
        schema.registerSimple((Map)hashMap, "minecraft:evocation_fangs");
        V1460.registerMob(schema, hashMap, "minecraft:evocation_illager");
        schema.registerSimple((Map)hashMap, "minecraft:eye_of_ender_signal");
        schema.register((Map)hashMap, "minecraft:falling_block", string -> DSL.optionalFields((String)"BlockState", (TypeTemplate)References.BLOCK_STATE.in(schema), (String)"TileEntityData", (TypeTemplate)References.BLOCK_ENTITY.in(schema)));
        schema.registerSimple((Map)hashMap, "minecraft:fireball");
        schema.register((Map)hashMap, "minecraft:fireworks_rocket", string -> DSL.optionalFields((String)"FireworksItem", (TypeTemplate)References.ITEM_STACK.in(schema)));
        schema.register((Map)hashMap, "minecraft:furnace_minecart", string -> DSL.optionalFields((String)"DisplayState", (TypeTemplate)References.BLOCK_STATE.in(schema)));
        V1460.registerMob(schema, hashMap, "minecraft:ghast");
        V1460.registerMob(schema, hashMap, "minecraft:giant");
        V1460.registerMob(schema, hashMap, "minecraft:guardian");
        schema.register((Map)hashMap, "minecraft:hopper_minecart", string -> DSL.optionalFields((String)"DisplayState", (TypeTemplate)References.BLOCK_STATE.in(schema), (String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in(schema))));
        schema.register((Map)hashMap, "minecraft:horse", string -> DSL.optionalFields((String)"ArmorItem", (TypeTemplate)References.ITEM_STACK.in(schema), (String)"SaddleItem", (TypeTemplate)References.ITEM_STACK.in(schema), (TypeTemplate)V100.equipment(schema)));
        V1460.registerMob(schema, hashMap, "minecraft:husk");
        schema.registerSimple((Map)hashMap, "minecraft:illusion_illager");
        schema.register((Map)hashMap, "minecraft:item", string -> DSL.optionalFields((String)"Item", (TypeTemplate)References.ITEM_STACK.in(schema)));
        schema.register((Map)hashMap, "minecraft:item_frame", string -> DSL.optionalFields((String)"Item", (TypeTemplate)References.ITEM_STACK.in(schema)));
        schema.registerSimple((Map)hashMap, "minecraft:leash_knot");
        schema.register((Map)hashMap, "minecraft:llama", string -> DSL.optionalFields((String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in(schema)), (String)"SaddleItem", (TypeTemplate)References.ITEM_STACK.in(schema), (String)"DecorItem", (TypeTemplate)References.ITEM_STACK.in(schema), (TypeTemplate)V100.equipment(schema)));
        schema.registerSimple((Map)hashMap, "minecraft:llama_spit");
        V1460.registerMob(schema, hashMap, "minecraft:magma_cube");
        schema.register((Map)hashMap, "minecraft:minecart", string -> DSL.optionalFields((String)"DisplayState", (TypeTemplate)References.BLOCK_STATE.in(schema)));
        V1460.registerMob(schema, hashMap, "minecraft:mooshroom");
        schema.register((Map)hashMap, "minecraft:mule", string -> DSL.optionalFields((String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in(schema)), (String)"SaddleItem", (TypeTemplate)References.ITEM_STACK.in(schema), (TypeTemplate)V100.equipment(schema)));
        V1460.registerMob(schema, hashMap, "minecraft:ocelot");
        schema.registerSimple((Map)hashMap, "minecraft:painting");
        schema.registerSimple((Map)hashMap, "minecraft:parrot");
        V1460.registerMob(schema, hashMap, "minecraft:pig");
        V1460.registerMob(schema, hashMap, "minecraft:polar_bear");
        schema.register((Map)hashMap, "minecraft:potion", string -> DSL.optionalFields((String)"Potion", (TypeTemplate)References.ITEM_STACK.in(schema)));
        V1460.registerMob(schema, hashMap, "minecraft:rabbit");
        V1460.registerMob(schema, hashMap, "minecraft:sheep");
        V1460.registerMob(schema, hashMap, "minecraft:shulker");
        schema.registerSimple((Map)hashMap, "minecraft:shulker_bullet");
        V1460.registerMob(schema, hashMap, "minecraft:silverfish");
        V1460.registerMob(schema, hashMap, "minecraft:skeleton");
        schema.register((Map)hashMap, "minecraft:skeleton_horse", string -> DSL.optionalFields((String)"SaddleItem", (TypeTemplate)References.ITEM_STACK.in(schema), (TypeTemplate)V100.equipment(schema)));
        V1460.registerMob(schema, hashMap, "minecraft:slime");
        schema.registerSimple((Map)hashMap, "minecraft:small_fireball");
        schema.registerSimple((Map)hashMap, "minecraft:snowball");
        V1460.registerMob(schema, hashMap, "minecraft:snowman");
        schema.register((Map)hashMap, "minecraft:spawner_minecart", string -> DSL.optionalFields((String)"DisplayState", (TypeTemplate)References.BLOCK_STATE.in(schema), (TypeTemplate)References.UNTAGGED_SPAWNER.in(schema)));
        schema.register((Map)hashMap, "minecraft:spectral_arrow", string -> DSL.optionalFields((String)"inBlockState", (TypeTemplate)References.BLOCK_STATE.in(schema)));
        V1460.registerMob(schema, hashMap, "minecraft:spider");
        V1460.registerMob(schema, hashMap, "minecraft:squid");
        V1460.registerMob(schema, hashMap, "minecraft:stray");
        schema.registerSimple((Map)hashMap, "minecraft:tnt");
        schema.register((Map)hashMap, "minecraft:tnt_minecart", string -> DSL.optionalFields((String)"DisplayState", (TypeTemplate)References.BLOCK_STATE.in(schema)));
        V1460.registerMob(schema, hashMap, "minecraft:vex");
        schema.register((Map)hashMap, "minecraft:villager", string -> DSL.optionalFields((String)"Inventory", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in(schema)), (String)"Offers", (TypeTemplate)DSL.optionalFields((String)"Recipes", (TypeTemplate)DSL.list((TypeTemplate)DSL.optionalFields((String)"buy", (TypeTemplate)References.ITEM_STACK.in(schema), (String)"buyB", (TypeTemplate)References.ITEM_STACK.in(schema), (String)"sell", (TypeTemplate)References.ITEM_STACK.in(schema)))), (TypeTemplate)V100.equipment(schema)));
        V1460.registerMob(schema, hashMap, "minecraft:villager_golem");
        V1460.registerMob(schema, hashMap, "minecraft:vindication_illager");
        V1460.registerMob(schema, hashMap, "minecraft:witch");
        V1460.registerMob(schema, hashMap, "minecraft:wither");
        V1460.registerMob(schema, hashMap, "minecraft:wither_skeleton");
        schema.registerSimple((Map)hashMap, "minecraft:wither_skull");
        V1460.registerMob(schema, hashMap, "minecraft:wolf");
        schema.registerSimple((Map)hashMap, "minecraft:xp_bottle");
        schema.registerSimple((Map)hashMap, "minecraft:xp_orb");
        V1460.registerMob(schema, hashMap, "minecraft:zombie");
        schema.register((Map)hashMap, "minecraft:zombie_horse", string -> DSL.optionalFields((String)"SaddleItem", (TypeTemplate)References.ITEM_STACK.in(schema), (TypeTemplate)V100.equipment(schema)));
        V1460.registerMob(schema, hashMap, "minecraft:zombie_pigman");
        V1460.registerMob(schema, hashMap, "minecraft:zombie_villager");
        return hashMap;
    }

    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
        HashMap hashMap = Maps.newHashMap();
        V1460.registerInventory(schema, hashMap, "minecraft:furnace");
        V1460.registerInventory(schema, hashMap, "minecraft:chest");
        V1460.registerInventory(schema, hashMap, "minecraft:trapped_chest");
        schema.registerSimple((Map)hashMap, "minecraft:ender_chest");
        schema.register((Map)hashMap, "minecraft:jukebox", string -> DSL.optionalFields((String)"RecordItem", (TypeTemplate)References.ITEM_STACK.in(schema)));
        V1460.registerInventory(schema, hashMap, "minecraft:dispenser");
        V1460.registerInventory(schema, hashMap, "minecraft:dropper");
        schema.registerSimple((Map)hashMap, "minecraft:sign");
        schema.register((Map)hashMap, "minecraft:mob_spawner", string -> References.UNTAGGED_SPAWNER.in(schema));
        schema.register((Map)hashMap, "minecraft:piston", string -> DSL.optionalFields((String)"blockState", (TypeTemplate)References.BLOCK_STATE.in(schema)));
        V1460.registerInventory(schema, hashMap, "minecraft:brewing_stand");
        schema.registerSimple((Map)hashMap, "minecraft:enchanting_table");
        schema.registerSimple((Map)hashMap, "minecraft:end_portal");
        schema.registerSimple((Map)hashMap, "minecraft:beacon");
        schema.registerSimple((Map)hashMap, "minecraft:skull");
        schema.registerSimple((Map)hashMap, "minecraft:daylight_detector");
        V1460.registerInventory(schema, hashMap, "minecraft:hopper");
        schema.registerSimple((Map)hashMap, "minecraft:comparator");
        schema.registerSimple((Map)hashMap, "minecraft:banner");
        schema.registerSimple((Map)hashMap, "minecraft:structure_block");
        schema.registerSimple((Map)hashMap, "minecraft:end_gateway");
        schema.registerSimple((Map)hashMap, "minecraft:command_block");
        V1460.registerInventory(schema, hashMap, "minecraft:shulker_box");
        schema.registerSimple((Map)hashMap, "minecraft:bed");
        return hashMap;
    }

    public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map2) {
        schema.registerType(false, References.LEVEL, DSL::remainder);
        schema.registerType(false, References.RECIPE, () -> DSL.constType(V1460.namespacedString()));
        schema.registerType(false, References.PLAYER, () -> DSL.optionalFields((String)"RootVehicle", (TypeTemplate)DSL.optionalFields((String)"Entity", (TypeTemplate)References.ENTITY_TREE.in(schema)), (String)"Inventory", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in(schema)), (String)"EnderItems", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in(schema)), (TypeTemplate)DSL.optionalFields((String)"ShoulderEntityLeft", (TypeTemplate)References.ENTITY_TREE.in(schema), (String)"ShoulderEntityRight", (TypeTemplate)References.ENTITY_TREE.in(schema), (String)"recipeBook", (TypeTemplate)DSL.optionalFields((String)"recipes", (TypeTemplate)DSL.list((TypeTemplate)References.RECIPE.in(schema)), (String)"toBeDisplayed", (TypeTemplate)DSL.list((TypeTemplate)References.RECIPE.in(schema))))));
        schema.registerType(false, References.CHUNK, () -> DSL.fields((String)"Level", (TypeTemplate)DSL.optionalFields((String)"Entities", (TypeTemplate)DSL.list((TypeTemplate)References.ENTITY_TREE.in(schema)), (String)"TileEntities", (TypeTemplate)DSL.list((TypeTemplate)References.BLOCK_ENTITY.in(schema)), (String)"TileTicks", (TypeTemplate)DSL.list((TypeTemplate)DSL.fields((String)"i", (TypeTemplate)References.BLOCK_NAME.in(schema))), (String)"Sections", (TypeTemplate)DSL.list((TypeTemplate)DSL.optionalFields((String)"Palette", (TypeTemplate)DSL.list((TypeTemplate)References.BLOCK_STATE.in(schema)))))));
        schema.registerType(true, References.BLOCK_ENTITY, () -> DSL.taggedChoiceLazy((String)"id", V1460.namespacedString(), (Map)map2));
        schema.registerType(true, References.ENTITY_TREE, () -> DSL.optionalFields((String)"Passengers", (TypeTemplate)DSL.list((TypeTemplate)References.ENTITY_TREE.in(schema)), (TypeTemplate)References.ENTITY.in(schema)));
        schema.registerType(true, References.ENTITY, () -> DSL.taggedChoiceLazy((String)"id", V1460.namespacedString(), (Map)map));
        schema.registerType(true, References.ITEM_STACK, () -> DSL.hook((TypeTemplate)DSL.optionalFields((String)"id", (TypeTemplate)References.ITEM_NAME.in(schema), (String)"tag", (TypeTemplate)DSL.optionalFields((String)"EntityTag", (TypeTemplate)References.ENTITY_TREE.in(schema), (String)"BlockEntityTag", (TypeTemplate)References.BLOCK_ENTITY.in(schema), (String)"CanDestroy", (TypeTemplate)DSL.list((TypeTemplate)References.BLOCK_NAME.in(schema)), (String)"CanPlaceOn", (TypeTemplate)DSL.list((TypeTemplate)References.BLOCK_NAME.in(schema)))), (Hook.HookFunction)V705.ADD_NAMES, (Hook.HookFunction)Hook.HookFunction.IDENTITY));
        schema.registerType(false, References.HOTBAR, () -> DSL.compoundList((TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in(schema))));
        schema.registerType(false, References.OPTIONS, DSL::remainder);
        schema.registerType(false, References.STRUCTURE, () -> DSL.optionalFields((String)"entities", (TypeTemplate)DSL.list((TypeTemplate)DSL.optionalFields((String)"nbt", (TypeTemplate)References.ENTITY_TREE.in(schema))), (String)"blocks", (TypeTemplate)DSL.list((TypeTemplate)DSL.optionalFields((String)"nbt", (TypeTemplate)References.BLOCK_ENTITY.in(schema))), (String)"palette", (TypeTemplate)DSL.list((TypeTemplate)References.BLOCK_STATE.in(schema))));
        schema.registerType(false, References.BLOCK_NAME, () -> DSL.constType(V1460.namespacedString()));
        schema.registerType(false, References.ITEM_NAME, () -> DSL.constType(V1460.namespacedString()));
        schema.registerType(false, References.BLOCK_STATE, DSL::remainder);
        Supplier<TypeTemplate> supplier = () -> DSL.compoundList((TypeTemplate)References.ITEM_NAME.in(schema), (TypeTemplate)DSL.constType((Type)DSL.intType()));
        schema.registerType(false, References.STATS, () -> DSL.optionalFields((String)"stats", (TypeTemplate)DSL.optionalFields((String)"minecraft:mined", (TypeTemplate)DSL.compoundList((TypeTemplate)References.BLOCK_NAME.in(schema), (TypeTemplate)DSL.constType((Type)DSL.intType())), (String)"minecraft:crafted", (TypeTemplate)((TypeTemplate)supplier.get()), (String)"minecraft:used", (TypeTemplate)((TypeTemplate)supplier.get()), (String)"minecraft:broken", (TypeTemplate)((TypeTemplate)supplier.get()), (String)"minecraft:picked_up", (TypeTemplate)((TypeTemplate)supplier.get()), (TypeTemplate)DSL.optionalFields((String)"minecraft:dropped", (TypeTemplate)((TypeTemplate)supplier.get()), (String)"minecraft:killed", (TypeTemplate)DSL.compoundList((TypeTemplate)References.ENTITY_NAME.in(schema), (TypeTemplate)DSL.constType((Type)DSL.intType())), (String)"minecraft:killed_by", (TypeTemplate)DSL.compoundList((TypeTemplate)References.ENTITY_NAME.in(schema), (TypeTemplate)DSL.constType((Type)DSL.intType())), (String)"minecraft:custom", (TypeTemplate)DSL.compoundList((TypeTemplate)DSL.constType(V1460.namespacedString()), (TypeTemplate)DSL.constType((Type)DSL.intType()))))));
        schema.registerType(false, References.SAVED_DATA, () -> DSL.optionalFields((String)"data", (TypeTemplate)DSL.optionalFields((String)"Features", (TypeTemplate)DSL.compoundList((TypeTemplate)References.STRUCTURE_FEATURE.in(schema)), (String)"Objectives", (TypeTemplate)DSL.list((TypeTemplate)References.OBJECTIVE.in(schema)), (String)"Teams", (TypeTemplate)DSL.list((TypeTemplate)References.TEAM.in(schema)))));
        schema.registerType(false, References.STRUCTURE_FEATURE, () -> DSL.optionalFields((String)"Children", (TypeTemplate)DSL.list((TypeTemplate)DSL.optionalFields((String)"CA", (TypeTemplate)References.BLOCK_STATE.in(schema), (String)"CB", (TypeTemplate)References.BLOCK_STATE.in(schema), (String)"CC", (TypeTemplate)References.BLOCK_STATE.in(schema), (String)"CD", (TypeTemplate)References.BLOCK_STATE.in(schema)))));
        schema.registerType(false, References.OBJECTIVE, DSL::remainder);
        schema.registerType(false, References.TEAM, DSL::remainder);
        schema.registerType(true, References.UNTAGGED_SPAWNER, () -> DSL.optionalFields((String)"SpawnPotentials", (TypeTemplate)DSL.list((TypeTemplate)DSL.fields((String)"Entity", (TypeTemplate)References.ENTITY_TREE.in(schema))), (String)"SpawnData", (TypeTemplate)References.ENTITY_TREE.in(schema)));
        schema.registerType(false, References.ADVANCEMENTS, () -> DSL.optionalFields((String)"minecraft:adventure/adventuring_time", (TypeTemplate)DSL.optionalFields((String)"criteria", (TypeTemplate)DSL.compoundList((TypeTemplate)References.BIOME.in(schema), (TypeTemplate)DSL.constType((Type)DSL.string()))), (String)"minecraft:adventure/kill_a_mob", (TypeTemplate)DSL.optionalFields((String)"criteria", (TypeTemplate)DSL.compoundList((TypeTemplate)References.ENTITY_NAME.in(schema), (TypeTemplate)DSL.constType((Type)DSL.string()))), (String)"minecraft:adventure/kill_all_mobs", (TypeTemplate)DSL.optionalFields((String)"criteria", (TypeTemplate)DSL.compoundList((TypeTemplate)References.ENTITY_NAME.in(schema), (TypeTemplate)DSL.constType((Type)DSL.string()))), (String)"minecraft:husbandry/bred_all_animals", (TypeTemplate)DSL.optionalFields((String)"criteria", (TypeTemplate)DSL.compoundList((TypeTemplate)References.ENTITY_NAME.in(schema), (TypeTemplate)DSL.constType((Type)DSL.string())))));
        schema.registerType(false, References.BIOME, () -> DSL.constType(V1460.namespacedString()));
        schema.registerType(false, References.ENTITY_NAME, () -> DSL.constType(V1460.namespacedString()));
        schema.registerType(false, References.POI_CHUNK, DSL::remainder);
        schema.registerType(true, References.WORLD_GEN_SETTINGS, DSL::remainder);
    }
}

