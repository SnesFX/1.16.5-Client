/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.types.templates.Hook
 *  com.mojang.datafixers.types.templates.Hook$HookFunction
 *  com.mojang.datafixers.types.templates.TypeTemplate
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.OptionalDynamic
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.util.datafix.schemas;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.Hook;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.OptionalDynamic;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class V99
extends Schema {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<String, String> ITEM_TO_BLOCKENTITY = (Map)DataFixUtils.make((Object)Maps.newHashMap(), hashMap -> {
        hashMap.put("minecraft:furnace", "Furnace");
        hashMap.put("minecraft:lit_furnace", "Furnace");
        hashMap.put("minecraft:chest", "Chest");
        hashMap.put("minecraft:trapped_chest", "Chest");
        hashMap.put("minecraft:ender_chest", "EnderChest");
        hashMap.put("minecraft:jukebox", "RecordPlayer");
        hashMap.put("minecraft:dispenser", "Trap");
        hashMap.put("minecraft:dropper", "Dropper");
        hashMap.put("minecraft:sign", "Sign");
        hashMap.put("minecraft:mob_spawner", "MobSpawner");
        hashMap.put("minecraft:noteblock", "Music");
        hashMap.put("minecraft:brewing_stand", "Cauldron");
        hashMap.put("minecraft:enhanting_table", "EnchantTable");
        hashMap.put("minecraft:command_block", "CommandBlock");
        hashMap.put("minecraft:beacon", "Beacon");
        hashMap.put("minecraft:skull", "Skull");
        hashMap.put("minecraft:daylight_detector", "DLDetector");
        hashMap.put("minecraft:hopper", "Hopper");
        hashMap.put("minecraft:banner", "Banner");
        hashMap.put("minecraft:flower_pot", "FlowerPot");
        hashMap.put("minecraft:repeating_command_block", "CommandBlock");
        hashMap.put("minecraft:chain_command_block", "CommandBlock");
        hashMap.put("minecraft:standing_sign", "Sign");
        hashMap.put("minecraft:wall_sign", "Sign");
        hashMap.put("minecraft:piston_head", "Piston");
        hashMap.put("minecraft:daylight_detector_inverted", "DLDetector");
        hashMap.put("minecraft:unpowered_comparator", "Comparator");
        hashMap.put("minecraft:powered_comparator", "Comparator");
        hashMap.put("minecraft:wall_banner", "Banner");
        hashMap.put("minecraft:standing_banner", "Banner");
        hashMap.put("minecraft:structure_block", "Structure");
        hashMap.put("minecraft:end_portal", "Airportal");
        hashMap.put("minecraft:end_gateway", "EndGateway");
        hashMap.put("minecraft:shield", "Banner");
    });
    protected static final Hook.HookFunction ADD_NAMES = new Hook.HookFunction(){

        public <T> T apply(DynamicOps<T> dynamicOps, T t) {
            return V99.addNames(new Dynamic(dynamicOps, t), ITEM_TO_BLOCKENTITY, "ArmorStand");
        }
    };

    public V99(int n, Schema schema) {
        super(n, schema);
    }

    protected static TypeTemplate equipment(Schema schema) {
        return DSL.optionalFields((String)"Equipment", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in(schema)));
    }

    protected static void registerMob(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
        schema.register(map, string, () -> V99.equipment(schema));
    }

    protected static void registerThrowableProjectile(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
        schema.register(map, string, () -> DSL.optionalFields((String)"inTile", (TypeTemplate)References.BLOCK_NAME.in(schema)));
    }

    protected static void registerMinecart(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
        schema.register(map, string, () -> DSL.optionalFields((String)"DisplayTile", (TypeTemplate)References.BLOCK_NAME.in(schema)));
    }

    protected static void registerInventory(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
        schema.register(map, string, () -> DSL.optionalFields((String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in(schema))));
    }

    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
        HashMap hashMap = Maps.newHashMap();
        schema.register((Map)hashMap, "Item", string -> DSL.optionalFields((String)"Item", (TypeTemplate)References.ITEM_STACK.in(schema)));
        schema.registerSimple((Map)hashMap, "XPOrb");
        V99.registerThrowableProjectile(schema, hashMap, "ThrownEgg");
        schema.registerSimple((Map)hashMap, "LeashKnot");
        schema.registerSimple((Map)hashMap, "Painting");
        schema.register((Map)hashMap, "Arrow", string -> DSL.optionalFields((String)"inTile", (TypeTemplate)References.BLOCK_NAME.in(schema)));
        schema.register((Map)hashMap, "TippedArrow", string -> DSL.optionalFields((String)"inTile", (TypeTemplate)References.BLOCK_NAME.in(schema)));
        schema.register((Map)hashMap, "SpectralArrow", string -> DSL.optionalFields((String)"inTile", (TypeTemplate)References.BLOCK_NAME.in(schema)));
        V99.registerThrowableProjectile(schema, hashMap, "Snowball");
        V99.registerThrowableProjectile(schema, hashMap, "Fireball");
        V99.registerThrowableProjectile(schema, hashMap, "SmallFireball");
        V99.registerThrowableProjectile(schema, hashMap, "ThrownEnderpearl");
        schema.registerSimple((Map)hashMap, "EyeOfEnderSignal");
        schema.register((Map)hashMap, "ThrownPotion", string -> DSL.optionalFields((String)"inTile", (TypeTemplate)References.BLOCK_NAME.in(schema), (String)"Potion", (TypeTemplate)References.ITEM_STACK.in(schema)));
        V99.registerThrowableProjectile(schema, hashMap, "ThrownExpBottle");
        schema.register((Map)hashMap, "ItemFrame", string -> DSL.optionalFields((String)"Item", (TypeTemplate)References.ITEM_STACK.in(schema)));
        V99.registerThrowableProjectile(schema, hashMap, "WitherSkull");
        schema.registerSimple((Map)hashMap, "PrimedTnt");
        schema.register((Map)hashMap, "FallingSand", string -> DSL.optionalFields((String)"Block", (TypeTemplate)References.BLOCK_NAME.in(schema), (String)"TileEntityData", (TypeTemplate)References.BLOCK_ENTITY.in(schema)));
        schema.register((Map)hashMap, "FireworksRocketEntity", string -> DSL.optionalFields((String)"FireworksItem", (TypeTemplate)References.ITEM_STACK.in(schema)));
        schema.registerSimple((Map)hashMap, "Boat");
        schema.register((Map)hashMap, "Minecart", () -> DSL.optionalFields((String)"DisplayTile", (TypeTemplate)References.BLOCK_NAME.in(schema), (String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in(schema))));
        V99.registerMinecart(schema, hashMap, "MinecartRideable");
        schema.register((Map)hashMap, "MinecartChest", string -> DSL.optionalFields((String)"DisplayTile", (TypeTemplate)References.BLOCK_NAME.in(schema), (String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in(schema))));
        V99.registerMinecart(schema, hashMap, "MinecartFurnace");
        V99.registerMinecart(schema, hashMap, "MinecartTNT");
        schema.register((Map)hashMap, "MinecartSpawner", () -> DSL.optionalFields((String)"DisplayTile", (TypeTemplate)References.BLOCK_NAME.in(schema), (TypeTemplate)References.UNTAGGED_SPAWNER.in(schema)));
        schema.register((Map)hashMap, "MinecartHopper", string -> DSL.optionalFields((String)"DisplayTile", (TypeTemplate)References.BLOCK_NAME.in(schema), (String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in(schema))));
        V99.registerMinecart(schema, hashMap, "MinecartCommandBlock");
        V99.registerMob(schema, hashMap, "ArmorStand");
        V99.registerMob(schema, hashMap, "Creeper");
        V99.registerMob(schema, hashMap, "Skeleton");
        V99.registerMob(schema, hashMap, "Spider");
        V99.registerMob(schema, hashMap, "Giant");
        V99.registerMob(schema, hashMap, "Zombie");
        V99.registerMob(schema, hashMap, "Slime");
        V99.registerMob(schema, hashMap, "Ghast");
        V99.registerMob(schema, hashMap, "PigZombie");
        schema.register((Map)hashMap, "Enderman", string -> DSL.optionalFields((String)"carried", (TypeTemplate)References.BLOCK_NAME.in(schema), (TypeTemplate)V99.equipment(schema)));
        V99.registerMob(schema, hashMap, "CaveSpider");
        V99.registerMob(schema, hashMap, "Silverfish");
        V99.registerMob(schema, hashMap, "Blaze");
        V99.registerMob(schema, hashMap, "LavaSlime");
        V99.registerMob(schema, hashMap, "EnderDragon");
        V99.registerMob(schema, hashMap, "WitherBoss");
        V99.registerMob(schema, hashMap, "Bat");
        V99.registerMob(schema, hashMap, "Witch");
        V99.registerMob(schema, hashMap, "Endermite");
        V99.registerMob(schema, hashMap, "Guardian");
        V99.registerMob(schema, hashMap, "Pig");
        V99.registerMob(schema, hashMap, "Sheep");
        V99.registerMob(schema, hashMap, "Cow");
        V99.registerMob(schema, hashMap, "Chicken");
        V99.registerMob(schema, hashMap, "Squid");
        V99.registerMob(schema, hashMap, "Wolf");
        V99.registerMob(schema, hashMap, "MushroomCow");
        V99.registerMob(schema, hashMap, "SnowMan");
        V99.registerMob(schema, hashMap, "Ozelot");
        V99.registerMob(schema, hashMap, "VillagerGolem");
        schema.register((Map)hashMap, "EntityHorse", string -> DSL.optionalFields((String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in(schema)), (String)"ArmorItem", (TypeTemplate)References.ITEM_STACK.in(schema), (String)"SaddleItem", (TypeTemplate)References.ITEM_STACK.in(schema), (TypeTemplate)V99.equipment(schema)));
        V99.registerMob(schema, hashMap, "Rabbit");
        schema.register((Map)hashMap, "Villager", string -> DSL.optionalFields((String)"Inventory", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in(schema)), (String)"Offers", (TypeTemplate)DSL.optionalFields((String)"Recipes", (TypeTemplate)DSL.list((TypeTemplate)DSL.optionalFields((String)"buy", (TypeTemplate)References.ITEM_STACK.in(schema), (String)"buyB", (TypeTemplate)References.ITEM_STACK.in(schema), (String)"sell", (TypeTemplate)References.ITEM_STACK.in(schema)))), (TypeTemplate)V99.equipment(schema)));
        schema.registerSimple((Map)hashMap, "EnderCrystal");
        schema.registerSimple((Map)hashMap, "AreaEffectCloud");
        schema.registerSimple((Map)hashMap, "ShulkerBullet");
        V99.registerMob(schema, hashMap, "Shulker");
        return hashMap;
    }

    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
        HashMap hashMap = Maps.newHashMap();
        V99.registerInventory(schema, hashMap, "Furnace");
        V99.registerInventory(schema, hashMap, "Chest");
        schema.registerSimple((Map)hashMap, "EnderChest");
        schema.register((Map)hashMap, "RecordPlayer", string -> DSL.optionalFields((String)"RecordItem", (TypeTemplate)References.ITEM_STACK.in(schema)));
        V99.registerInventory(schema, hashMap, "Trap");
        V99.registerInventory(schema, hashMap, "Dropper");
        schema.registerSimple((Map)hashMap, "Sign");
        schema.register((Map)hashMap, "MobSpawner", string -> References.UNTAGGED_SPAWNER.in(schema));
        schema.registerSimple((Map)hashMap, "Music");
        schema.registerSimple((Map)hashMap, "Piston");
        V99.registerInventory(schema, hashMap, "Cauldron");
        schema.registerSimple((Map)hashMap, "EnchantTable");
        schema.registerSimple((Map)hashMap, "Airportal");
        schema.registerSimple((Map)hashMap, "Control");
        schema.registerSimple((Map)hashMap, "Beacon");
        schema.registerSimple((Map)hashMap, "Skull");
        schema.registerSimple((Map)hashMap, "DLDetector");
        V99.registerInventory(schema, hashMap, "Hopper");
        schema.registerSimple((Map)hashMap, "Comparator");
        schema.register((Map)hashMap, "FlowerPot", string -> DSL.optionalFields((String)"Item", (TypeTemplate)DSL.or((TypeTemplate)DSL.constType((Type)DSL.intType()), (TypeTemplate)References.ITEM_NAME.in(schema))));
        schema.registerSimple((Map)hashMap, "Banner");
        schema.registerSimple((Map)hashMap, "Structure");
        schema.registerSimple((Map)hashMap, "EndGateway");
        return hashMap;
    }

    public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map2) {
        schema.registerType(false, References.LEVEL, DSL::remainder);
        schema.registerType(false, References.PLAYER, () -> DSL.optionalFields((String)"Inventory", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in(schema)), (String)"EnderItems", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in(schema))));
        schema.registerType(false, References.CHUNK, () -> DSL.fields((String)"Level", (TypeTemplate)DSL.optionalFields((String)"Entities", (TypeTemplate)DSL.list((TypeTemplate)References.ENTITY_TREE.in(schema)), (String)"TileEntities", (TypeTemplate)DSL.list((TypeTemplate)References.BLOCK_ENTITY.in(schema)), (String)"TileTicks", (TypeTemplate)DSL.list((TypeTemplate)DSL.fields((String)"i", (TypeTemplate)References.BLOCK_NAME.in(schema))))));
        schema.registerType(true, References.BLOCK_ENTITY, () -> DSL.taggedChoiceLazy((String)"id", (Type)DSL.string(), (Map)map2));
        schema.registerType(true, References.ENTITY_TREE, () -> DSL.optionalFields((String)"Riding", (TypeTemplate)References.ENTITY_TREE.in(schema), (TypeTemplate)References.ENTITY.in(schema)));
        schema.registerType(false, References.ENTITY_NAME, () -> DSL.constType(NamespacedSchema.namespacedString()));
        schema.registerType(true, References.ENTITY, () -> DSL.taggedChoiceLazy((String)"id", (Type)DSL.string(), (Map)map));
        schema.registerType(true, References.ITEM_STACK, () -> DSL.hook((TypeTemplate)DSL.optionalFields((String)"id", (TypeTemplate)DSL.or((TypeTemplate)DSL.constType((Type)DSL.intType()), (TypeTemplate)References.ITEM_NAME.in(schema)), (String)"tag", (TypeTemplate)DSL.optionalFields((String)"EntityTag", (TypeTemplate)References.ENTITY_TREE.in(schema), (String)"BlockEntityTag", (TypeTemplate)References.BLOCK_ENTITY.in(schema), (String)"CanDestroy", (TypeTemplate)DSL.list((TypeTemplate)References.BLOCK_NAME.in(schema)), (String)"CanPlaceOn", (TypeTemplate)DSL.list((TypeTemplate)References.BLOCK_NAME.in(schema)))), (Hook.HookFunction)ADD_NAMES, (Hook.HookFunction)Hook.HookFunction.IDENTITY));
        schema.registerType(false, References.OPTIONS, DSL::remainder);
        schema.registerType(false, References.BLOCK_NAME, () -> DSL.or((TypeTemplate)DSL.constType((Type)DSL.intType()), (TypeTemplate)DSL.constType(NamespacedSchema.namespacedString())));
        schema.registerType(false, References.ITEM_NAME, () -> DSL.constType(NamespacedSchema.namespacedString()));
        schema.registerType(false, References.STATS, DSL::remainder);
        schema.registerType(false, References.SAVED_DATA, () -> DSL.optionalFields((String)"data", (TypeTemplate)DSL.optionalFields((String)"Features", (TypeTemplate)DSL.compoundList((TypeTemplate)References.STRUCTURE_FEATURE.in(schema)), (String)"Objectives", (TypeTemplate)DSL.list((TypeTemplate)References.OBJECTIVE.in(schema)), (String)"Teams", (TypeTemplate)DSL.list((TypeTemplate)References.TEAM.in(schema)))));
        schema.registerType(false, References.STRUCTURE_FEATURE, DSL::remainder);
        schema.registerType(false, References.OBJECTIVE, DSL::remainder);
        schema.registerType(false, References.TEAM, DSL::remainder);
        schema.registerType(true, References.UNTAGGED_SPAWNER, DSL::remainder);
        schema.registerType(false, References.POI_CHUNK, DSL::remainder);
        schema.registerType(true, References.WORLD_GEN_SETTINGS, DSL::remainder);
    }

    protected static <T> T addNames(Dynamic<T> dynamic, Map<String, String> map, String string) {
        return (T)dynamic.update("tag", dynamic3 -> dynamic3.update("BlockEntityTag", dynamic2 -> {
            String string = dynamic.get("id").asString("");
            String string2 = (String)map.get(NamespacedSchema.ensureNamespaced(string));
            if (string2 != null) {
                return dynamic2.set("id", dynamic.createString(string2));
            }
            LOGGER.warn("Unable to resolve BlockEntity for ItemStack: {}", (Object)string);
            return dynamic2;
        }).update("EntityTag", dynamic2 -> {
            String string2 = dynamic.get("id").asString("");
            if (Objects.equals(NamespacedSchema.ensureNamespaced(string2), "minecraft:armor_stand")) {
                return dynamic2.set("id", dynamic.createString(string));
            }
            return dynamic2;
        })).getValue();
    }

}

