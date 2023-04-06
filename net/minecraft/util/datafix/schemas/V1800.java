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

public class V1800
extends NamespacedSchema {
    public V1800(int n, Schema schema) {
        super(n, schema);
    }

    protected static void registerMob(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
        schema.register(map, string, () -> V100.equipment(schema));
    }

    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
        Map map = super.registerEntities(schema);
        V1800.registerMob(schema, map, "minecraft:panda");
        schema.register(map, "minecraft:pillager", string -> DSL.optionalFields((String)"Inventory", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in(schema)), (TypeTemplate)V100.equipment(schema)));
        return map;
    }
}

