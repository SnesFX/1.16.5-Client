/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.mojang.datafixers.schemas.Schema
 */
package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.RecipesRenameFix;

public class RecipesRenameningFix
extends RecipesRenameFix {
    private static final Map<String, String> RECIPES = ImmutableMap.builder().put((Object)"minecraft:acacia_bark", (Object)"minecraft:acacia_wood").put((Object)"minecraft:birch_bark", (Object)"minecraft:birch_wood").put((Object)"minecraft:dark_oak_bark", (Object)"minecraft:dark_oak_wood").put((Object)"minecraft:jungle_bark", (Object)"minecraft:jungle_wood").put((Object)"minecraft:oak_bark", (Object)"minecraft:oak_wood").put((Object)"minecraft:spruce_bark", (Object)"minecraft:spruce_wood").build();

    public RecipesRenameningFix(Schema schema, boolean bl) {
        super(schema, bl, "Recipes renamening fix", string -> RECIPES.getOrDefault(string, (String)string));
    }
}

