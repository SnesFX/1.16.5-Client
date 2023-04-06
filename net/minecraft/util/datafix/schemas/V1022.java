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

public class V1022
extends Schema {
    public V1022(int n, Schema schema) {
        super(n, schema);
    }

    public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map2) {
        super.registerTypes(schema, map, map2);
        schema.registerType(false, References.RECIPE, () -> DSL.constType(NamespacedSchema.namespacedString()));
        schema.registerType(false, References.PLAYER, () -> DSL.optionalFields((String)"RootVehicle", (TypeTemplate)DSL.optionalFields((String)"Entity", (TypeTemplate)References.ENTITY_TREE.in(schema)), (String)"Inventory", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in(schema)), (String)"EnderItems", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in(schema)), (TypeTemplate)DSL.optionalFields((String)"ShoulderEntityLeft", (TypeTemplate)References.ENTITY_TREE.in(schema), (String)"ShoulderEntityRight", (TypeTemplate)References.ENTITY_TREE.in(schema), (String)"recipeBook", (TypeTemplate)DSL.optionalFields((String)"recipes", (TypeTemplate)DSL.list((TypeTemplate)References.RECIPE.in(schema)), (String)"toBeDisplayed", (TypeTemplate)DSL.list((TypeTemplate)References.RECIPE.in(schema))))));
        schema.registerType(false, References.HOTBAR, () -> DSL.compoundList((TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in(schema))));
    }
}

