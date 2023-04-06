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
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class V1451_1
extends NamespacedSchema {
    public V1451_1(int n, Schema schema) {
        super(n, schema);
    }

    public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map2) {
        super.registerTypes(schema, map, map2);
        schema.registerType(false, References.CHUNK, () -> DSL.fields((String)"Level", (TypeTemplate)DSL.optionalFields((String)"Entities", (TypeTemplate)DSL.list((TypeTemplate)References.ENTITY_TREE.in(schema)), (String)"TileEntities", (TypeTemplate)DSL.list((TypeTemplate)References.BLOCK_ENTITY.in(schema)), (String)"TileTicks", (TypeTemplate)DSL.list((TypeTemplate)DSL.fields((String)"i", (TypeTemplate)References.BLOCK_NAME.in(schema))), (String)"Sections", (TypeTemplate)DSL.list((TypeTemplate)DSL.optionalFields((String)"Palette", (TypeTemplate)DSL.list((TypeTemplate)References.BLOCK_STATE.in(schema)))))));
    }
}

