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
import net.minecraft.util.datafix.schemas.NamespacedSchema;
import net.minecraft.util.datafix.schemas.V100;

public class V1470
extends NamespacedSchema {
    public V1470(int n, Schema schema) {
        super(n, schema);
    }

    protected static void registerMob(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
        schema.register(map, string, () -> V100.equipment(schema));
    }

    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
        Map map = super.registerEntities(schema);
        V1470.registerMob(schema, map, "minecraft:turtle");
        V1470.registerMob(schema, map, "minecraft:cod_mob");
        V1470.registerMob(schema, map, "minecraft:tropical_fish");
        V1470.registerMob(schema, map, "minecraft:salmon_mob");
        V1470.registerMob(schema, map, "minecraft:puffer_fish");
        V1470.registerMob(schema, map, "minecraft:phantom");
        V1470.registerMob(schema, map, "minecraft:dolphin");
        V1470.registerMob(schema, map, "minecraft:drowned");
        schema.register(map, "minecraft:trident", string -> DSL.optionalFields((String)"inBlockState", (TypeTemplate)References.BLOCK_STATE.in(schema)));
        return map;
    }
}

