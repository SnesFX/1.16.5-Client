/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.templates.TypeTemplate
 */
package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V100
extends Schema {
    public V100(int n, Schema schema) {
        super(n, schema);
    }

    protected static TypeTemplate equipment(Schema schema) {
        return DSL.optionalFields((String)"ArmorItems", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in(schema)), (String)"HandItems", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in(schema)));
    }

    protected static void registerMob(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
        schema.register(map, string, () -> V100.equipment(schema));
    }

    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
        Map map = super.registerEntities(schema);
        V100.registerMob(schema, map, "ArmorStand");
        V100.registerMob(schema, map, "Creeper");
        V100.registerMob(schema, map, "Skeleton");
        V100.registerMob(schema, map, "Spider");
        V100.registerMob(schema, map, "Giant");
        V100.registerMob(schema, map, "Zombie");
        V100.registerMob(schema, map, "Slime");
        V100.registerMob(schema, map, "Ghast");
        V100.registerMob(schema, map, "PigZombie");
        schema.register(map, "Enderman", string -> DSL.optionalFields((String)"carried", (TypeTemplate)References.BLOCK_NAME.in(schema), (TypeTemplate)V100.equipment(schema)));
        V100.registerMob(schema, map, "CaveSpider");
        V100.registerMob(schema, map, "Silverfish");
        V100.registerMob(schema, map, "Blaze");
        V100.registerMob(schema, map, "LavaSlime");
        V100.registerMob(schema, map, "EnderDragon");
        V100.registerMob(schema, map, "WitherBoss");
        V100.registerMob(schema, map, "Bat");
        V100.registerMob(schema, map, "Witch");
        V100.registerMob(schema, map, "Endermite");
        V100.registerMob(schema, map, "Guardian");
        V100.registerMob(schema, map, "Pig");
        V100.registerMob(schema, map, "Sheep");
        V100.registerMob(schema, map, "Cow");
        V100.registerMob(schema, map, "Chicken");
        V100.registerMob(schema, map, "Squid");
        V100.registerMob(schema, map, "Wolf");
        V100.registerMob(schema, map, "MushroomCow");
        V100.registerMob(schema, map, "SnowMan");
        V100.registerMob(schema, map, "Ozelot");
        V100.registerMob(schema, map, "VillagerGolem");
        schema.register(map, "EntityHorse", string -> DSL.optionalFields((String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in(schema)), (String)"ArmorItem", (TypeTemplate)References.ITEM_STACK.in(schema), (String)"SaddleItem", (TypeTemplate)References.ITEM_STACK.in(schema), (TypeTemplate)V100.equipment(schema)));
        V100.registerMob(schema, map, "Rabbit");
        schema.register(map, "Villager", string -> DSL.optionalFields((String)"Inventory", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in(schema)), (String)"Offers", (TypeTemplate)DSL.optionalFields((String)"Recipes", (TypeTemplate)DSL.list((TypeTemplate)DSL.optionalFields((String)"buy", (TypeTemplate)References.ITEM_STACK.in(schema), (String)"buyB", (TypeTemplate)References.ITEM_STACK.in(schema), (String)"sell", (TypeTemplate)References.ITEM_STACK.in(schema)))), (TypeTemplate)V100.equipment(schema)));
        V100.registerMob(schema, map, "Shulker");
        schema.registerSimple(map, "AreaEffectCloud");
        schema.registerSimple(map, "ShulkerBullet");
        return map;
    }

    public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map2) {
        super.registerTypes(schema, map, map2);
        schema.registerType(false, References.STRUCTURE, () -> DSL.optionalFields((String)"entities", (TypeTemplate)DSL.list((TypeTemplate)DSL.optionalFields((String)"nbt", (TypeTemplate)References.ENTITY_TREE.in(schema))), (String)"blocks", (TypeTemplate)DSL.list((TypeTemplate)DSL.optionalFields((String)"nbt", (TypeTemplate)References.BLOCK_ENTITY.in(schema))), (String)"palette", (TypeTemplate)DSL.list((TypeTemplate)References.BLOCK_STATE.in(schema))));
        schema.registerType(false, References.BLOCK_STATE, DSL::remainder);
    }
}

