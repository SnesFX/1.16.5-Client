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
import net.minecraft.util.datafix.fixes.AdvancementsRenameFix;

public class AdvancementsFix
extends AdvancementsRenameFix {
    private static final Map<String, String> RENAMES = ImmutableMap.builder().put((Object)"minecraft:recipes/brewing/speckled_melon", (Object)"minecraft:recipes/brewing/glistering_melon_slice").put((Object)"minecraft:recipes/building_blocks/black_stained_hardened_clay", (Object)"minecraft:recipes/building_blocks/black_terracotta").put((Object)"minecraft:recipes/building_blocks/blue_stained_hardened_clay", (Object)"minecraft:recipes/building_blocks/blue_terracotta").put((Object)"minecraft:recipes/building_blocks/brown_stained_hardened_clay", (Object)"minecraft:recipes/building_blocks/brown_terracotta").put((Object)"minecraft:recipes/building_blocks/cyan_stained_hardened_clay", (Object)"minecraft:recipes/building_blocks/cyan_terracotta").put((Object)"minecraft:recipes/building_blocks/gray_stained_hardened_clay", (Object)"minecraft:recipes/building_blocks/gray_terracotta").put((Object)"minecraft:recipes/building_blocks/green_stained_hardened_clay", (Object)"minecraft:recipes/building_blocks/green_terracotta").put((Object)"minecraft:recipes/building_blocks/light_blue_stained_hardened_clay", (Object)"minecraft:recipes/building_blocks/light_blue_terracotta").put((Object)"minecraft:recipes/building_blocks/light_gray_stained_hardened_clay", (Object)"minecraft:recipes/building_blocks/light_gray_terracotta").put((Object)"minecraft:recipes/building_blocks/lime_stained_hardened_clay", (Object)"minecraft:recipes/building_blocks/lime_terracotta").put((Object)"minecraft:recipes/building_blocks/magenta_stained_hardened_clay", (Object)"minecraft:recipes/building_blocks/magenta_terracotta").put((Object)"minecraft:recipes/building_blocks/orange_stained_hardened_clay", (Object)"minecraft:recipes/building_blocks/orange_terracotta").put((Object)"minecraft:recipes/building_blocks/pink_stained_hardened_clay", (Object)"minecraft:recipes/building_blocks/pink_terracotta").put((Object)"minecraft:recipes/building_blocks/purple_stained_hardened_clay", (Object)"minecraft:recipes/building_blocks/purple_terracotta").put((Object)"minecraft:recipes/building_blocks/red_stained_hardened_clay", (Object)"minecraft:recipes/building_blocks/red_terracotta").put((Object)"minecraft:recipes/building_blocks/white_stained_hardened_clay", (Object)"minecraft:recipes/building_blocks/white_terracotta").put((Object)"minecraft:recipes/building_blocks/yellow_stained_hardened_clay", (Object)"minecraft:recipes/building_blocks/yellow_terracotta").put((Object)"minecraft:recipes/building_blocks/acacia_wooden_slab", (Object)"minecraft:recipes/building_blocks/acacia_slab").put((Object)"minecraft:recipes/building_blocks/birch_wooden_slab", (Object)"minecraft:recipes/building_blocks/birch_slab").put((Object)"minecraft:recipes/building_blocks/dark_oak_wooden_slab", (Object)"minecraft:recipes/building_blocks/dark_oak_slab").put((Object)"minecraft:recipes/building_blocks/jungle_wooden_slab", (Object)"minecraft:recipes/building_blocks/jungle_slab").put((Object)"minecraft:recipes/building_blocks/oak_wooden_slab", (Object)"minecraft:recipes/building_blocks/oak_slab").put((Object)"minecraft:recipes/building_blocks/spruce_wooden_slab", (Object)"minecraft:recipes/building_blocks/spruce_slab").put((Object)"minecraft:recipes/building_blocks/brick_block", (Object)"minecraft:recipes/building_blocks/bricks").put((Object)"minecraft:recipes/building_blocks/chiseled_stonebrick", (Object)"minecraft:recipes/building_blocks/chiseled_stone_bricks").put((Object)"minecraft:recipes/building_blocks/end_bricks", (Object)"minecraft:recipes/building_blocks/end_stone_bricks").put((Object)"minecraft:recipes/building_blocks/lit_pumpkin", (Object)"minecraft:recipes/building_blocks/jack_o_lantern").put((Object)"minecraft:recipes/building_blocks/magma", (Object)"minecraft:recipes/building_blocks/magma_block").put((Object)"minecraft:recipes/building_blocks/melon_block", (Object)"minecraft:recipes/building_blocks/melon").put((Object)"minecraft:recipes/building_blocks/mossy_stonebrick", (Object)"minecraft:recipes/building_blocks/mossy_stone_bricks").put((Object)"minecraft:recipes/building_blocks/nether_brick", (Object)"minecraft:recipes/building_blocks/nether_bricks").put((Object)"minecraft:recipes/building_blocks/pillar_quartz_block", (Object)"minecraft:recipes/building_blocks/quartz_pillar").put((Object)"minecraft:recipes/building_blocks/red_nether_brick", (Object)"minecraft:recipes/building_blocks/red_nether_bricks").put((Object)"minecraft:recipes/building_blocks/snow", (Object)"minecraft:recipes/building_blocks/snow_block").put((Object)"minecraft:recipes/building_blocks/smooth_red_sandstone", (Object)"minecraft:recipes/building_blocks/cut_red_sandstone").put((Object)"minecraft:recipes/building_blocks/smooth_sandstone", (Object)"minecraft:recipes/building_blocks/cut_sandstone").put((Object)"minecraft:recipes/building_blocks/stonebrick", (Object)"minecraft:recipes/building_blocks/stone_bricks").put((Object)"minecraft:recipes/building_blocks/stone_stairs", (Object)"minecraft:recipes/building_blocks/cobblestone_stairs").put((Object)"minecraft:recipes/building_blocks/string_to_wool", (Object)"minecraft:recipes/building_blocks/white_wool_from_string").put((Object)"minecraft:recipes/decorations/fence", (Object)"minecraft:recipes/decorations/oak_fence").put((Object)"minecraft:recipes/decorations/purple_shulker_box", (Object)"minecraft:recipes/decorations/shulker_box").put((Object)"minecraft:recipes/decorations/slime", (Object)"minecraft:recipes/decorations/slime_block").put((Object)"minecraft:recipes/decorations/snow_layer", (Object)"minecraft:recipes/decorations/snow").put((Object)"minecraft:recipes/misc/bone_meal_from_block", (Object)"minecraft:recipes/misc/bone_meal_from_bone_block").put((Object)"minecraft:recipes/misc/bone_meal_from_bone", (Object)"minecraft:recipes/misc/bone_meal").put((Object)"minecraft:recipes/misc/gold_ingot_from_block", (Object)"minecraft:recipes/misc/gold_ingot_from_gold_block").put((Object)"minecraft:recipes/misc/iron_ingot_from_block", (Object)"minecraft:recipes/misc/iron_ingot_from_iron_block").put((Object)"minecraft:recipes/redstone/fence_gate", (Object)"minecraft:recipes/redstone/oak_fence_gate").put((Object)"minecraft:recipes/redstone/noteblock", (Object)"minecraft:recipes/redstone/note_block").put((Object)"minecraft:recipes/redstone/trapdoor", (Object)"minecraft:recipes/redstone/oak_trapdoor").put((Object)"minecraft:recipes/redstone/wooden_button", (Object)"minecraft:recipes/redstone/oak_button").put((Object)"minecraft:recipes/redstone/wooden_door", (Object)"minecraft:recipes/redstone/oak_door").put((Object)"minecraft:recipes/redstone/wooden_pressure_plate", (Object)"minecraft:recipes/redstone/oak_pressure_plate").put((Object)"minecraft:recipes/transportation/boat", (Object)"minecraft:recipes/transportation/oak_boat").put((Object)"minecraft:recipes/transportation/golden_rail", (Object)"minecraft:recipes/transportation/powered_rail").build();

    public AdvancementsFix(Schema schema, boolean bl) {
        super(schema, bl, "AdvancementsFix", string -> RENAMES.getOrDefault(string, (String)string));
    }
}

