/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package net.minecraft.data.worldgen;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.AxisAlignedLinearPosTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockRotProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockStateMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProcessorRule;
import net.minecraft.world.level.levelgen.structure.templatesystem.RandomBlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

public class ProcessorLists {
    private static final ProcessorRule ADD_GILDED_BLACKSTONE = new ProcessorRule(new RandomBlockMatchTest(Blocks.BLACKSTONE, 0.01f), AlwaysTrueTest.INSTANCE, Blocks.GILDED_BLACKSTONE.defaultBlockState());
    private static final ProcessorRule REMOVE_GILDED_BLACKSTONE = new ProcessorRule(new RandomBlockMatchTest(Blocks.GILDED_BLACKSTONE, 0.5f), AlwaysTrueTest.INSTANCE, Blocks.BLACKSTONE.defaultBlockState());
    public static final StructureProcessorList EMPTY = ProcessorLists.register("empty", (ImmutableList<StructureProcessor>)ImmutableList.of());
    public static final StructureProcessorList ZOMBIE_PLAINS = ProcessorLists.register("zombie_plains", (ImmutableList<StructureProcessor>)ImmutableList.of((Object)new RuleProcessor((List<? extends ProcessorRule>)ImmutableList.of((Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.COBBLESTONE, 0.8f), AlwaysTrueTest.INSTANCE, Blocks.MOSSY_COBBLESTONE.defaultBlockState()), (Object)new ProcessorRule(new TagMatchTest(BlockTags.DOORS), AlwaysTrueTest.INSTANCE, Blocks.AIR.defaultBlockState()), (Object)new ProcessorRule(new BlockMatchTest(Blocks.TORCH), AlwaysTrueTest.INSTANCE, Blocks.AIR.defaultBlockState()), (Object)new ProcessorRule(new BlockMatchTest(Blocks.WALL_TORCH), AlwaysTrueTest.INSTANCE, Blocks.AIR.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.COBBLESTONE, 0.07f), AlwaysTrueTest.INSTANCE, Blocks.COBWEB.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.MOSSY_COBBLESTONE, 0.07f), AlwaysTrueTest.INSTANCE, Blocks.COBWEB.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.WHITE_TERRACOTTA, 0.07f), AlwaysTrueTest.INSTANCE, Blocks.COBWEB.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.OAK_LOG, 0.05f), AlwaysTrueTest.INSTANCE, Blocks.COBWEB.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.OAK_PLANKS, 0.1f), AlwaysTrueTest.INSTANCE, Blocks.COBWEB.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.OAK_STAIRS, 0.1f), AlwaysTrueTest.INSTANCE, Blocks.COBWEB.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.STRIPPED_OAK_LOG, 0.02f), AlwaysTrueTest.INSTANCE, Blocks.COBWEB.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.GLASS_PANE, 0.5f), AlwaysTrueTest.INSTANCE, Blocks.COBWEB.defaultBlockState()), (Object[])new ProcessorRule[]{new ProcessorRule(new BlockStateMatchTest((BlockState)((BlockState)Blocks.GLASS_PANE.defaultBlockState().setValue(IronBarsBlock.NORTH, true)).setValue(IronBarsBlock.SOUTH, true)), AlwaysTrueTest.INSTANCE, (BlockState)((BlockState)Blocks.BROWN_STAINED_GLASS_PANE.defaultBlockState().setValue(IronBarsBlock.NORTH, true)).setValue(IronBarsBlock.SOUTH, true)), new ProcessorRule(new BlockStateMatchTest((BlockState)((BlockState)Blocks.GLASS_PANE.defaultBlockState().setValue(IronBarsBlock.EAST, true)).setValue(IronBarsBlock.WEST, true)), AlwaysTrueTest.INSTANCE, (BlockState)((BlockState)Blocks.BROWN_STAINED_GLASS_PANE.defaultBlockState().setValue(IronBarsBlock.EAST, true)).setValue(IronBarsBlock.WEST, true)), new ProcessorRule(new RandomBlockMatchTest(Blocks.WHEAT, 0.3f), AlwaysTrueTest.INSTANCE, Blocks.CARROTS.defaultBlockState()), new ProcessorRule(new RandomBlockMatchTest(Blocks.WHEAT, 0.2f), AlwaysTrueTest.INSTANCE, Blocks.POTATOES.defaultBlockState()), new ProcessorRule(new RandomBlockMatchTest(Blocks.WHEAT, 0.1f), AlwaysTrueTest.INSTANCE, Blocks.BEETROOTS.defaultBlockState())}))));
    public static final StructureProcessorList ZOMBIE_SAVANNA = ProcessorLists.register("zombie_savanna", (ImmutableList<StructureProcessor>)ImmutableList.of((Object)new RuleProcessor((List<? extends ProcessorRule>)ImmutableList.of((Object)new ProcessorRule(new TagMatchTest(BlockTags.DOORS), AlwaysTrueTest.INSTANCE, Blocks.AIR.defaultBlockState()), (Object)new ProcessorRule(new BlockMatchTest(Blocks.TORCH), AlwaysTrueTest.INSTANCE, Blocks.AIR.defaultBlockState()), (Object)new ProcessorRule(new BlockMatchTest(Blocks.WALL_TORCH), AlwaysTrueTest.INSTANCE, Blocks.AIR.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.ACACIA_PLANKS, 0.2f), AlwaysTrueTest.INSTANCE, Blocks.COBWEB.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.ACACIA_STAIRS, 0.2f), AlwaysTrueTest.INSTANCE, Blocks.COBWEB.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.ACACIA_LOG, 0.05f), AlwaysTrueTest.INSTANCE, Blocks.COBWEB.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.ACACIA_WOOD, 0.05f), AlwaysTrueTest.INSTANCE, Blocks.COBWEB.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.ORANGE_TERRACOTTA, 0.05f), AlwaysTrueTest.INSTANCE, Blocks.COBWEB.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.YELLOW_TERRACOTTA, 0.05f), AlwaysTrueTest.INSTANCE, Blocks.COBWEB.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.RED_TERRACOTTA, 0.05f), AlwaysTrueTest.INSTANCE, Blocks.COBWEB.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.GLASS_PANE, 0.5f), AlwaysTrueTest.INSTANCE, Blocks.COBWEB.defaultBlockState()), (Object)new ProcessorRule(new BlockStateMatchTest((BlockState)((BlockState)Blocks.GLASS_PANE.defaultBlockState().setValue(IronBarsBlock.NORTH, true)).setValue(IronBarsBlock.SOUTH, true)), AlwaysTrueTest.INSTANCE, (BlockState)((BlockState)Blocks.BROWN_STAINED_GLASS_PANE.defaultBlockState().setValue(IronBarsBlock.NORTH, true)).setValue(IronBarsBlock.SOUTH, true)), (Object[])new ProcessorRule[]{new ProcessorRule(new BlockStateMatchTest((BlockState)((BlockState)Blocks.GLASS_PANE.defaultBlockState().setValue(IronBarsBlock.EAST, true)).setValue(IronBarsBlock.WEST, true)), AlwaysTrueTest.INSTANCE, (BlockState)((BlockState)Blocks.BROWN_STAINED_GLASS_PANE.defaultBlockState().setValue(IronBarsBlock.EAST, true)).setValue(IronBarsBlock.WEST, true)), new ProcessorRule(new RandomBlockMatchTest(Blocks.WHEAT, 0.1f), AlwaysTrueTest.INSTANCE, Blocks.MELON_STEM.defaultBlockState())}))));
    public static final StructureProcessorList ZOMBIE_SNOWY = ProcessorLists.register("zombie_snowy", (ImmutableList<StructureProcessor>)ImmutableList.of((Object)new RuleProcessor((List<? extends ProcessorRule>)ImmutableList.of((Object)new ProcessorRule(new TagMatchTest(BlockTags.DOORS), AlwaysTrueTest.INSTANCE, Blocks.AIR.defaultBlockState()), (Object)new ProcessorRule(new BlockMatchTest(Blocks.TORCH), AlwaysTrueTest.INSTANCE, Blocks.AIR.defaultBlockState()), (Object)new ProcessorRule(new BlockMatchTest(Blocks.WALL_TORCH), AlwaysTrueTest.INSTANCE, Blocks.AIR.defaultBlockState()), (Object)new ProcessorRule(new BlockMatchTest(Blocks.LANTERN), AlwaysTrueTest.INSTANCE, Blocks.AIR.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.SPRUCE_PLANKS, 0.2f), AlwaysTrueTest.INSTANCE, Blocks.COBWEB.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.SPRUCE_SLAB, 0.4f), AlwaysTrueTest.INSTANCE, Blocks.COBWEB.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.STRIPPED_SPRUCE_LOG, 0.05f), AlwaysTrueTest.INSTANCE, Blocks.COBWEB.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.STRIPPED_SPRUCE_WOOD, 0.05f), AlwaysTrueTest.INSTANCE, Blocks.COBWEB.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.GLASS_PANE, 0.5f), AlwaysTrueTest.INSTANCE, Blocks.COBWEB.defaultBlockState()), (Object)new ProcessorRule(new BlockStateMatchTest((BlockState)((BlockState)Blocks.GLASS_PANE.defaultBlockState().setValue(IronBarsBlock.NORTH, true)).setValue(IronBarsBlock.SOUTH, true)), AlwaysTrueTest.INSTANCE, (BlockState)((BlockState)Blocks.BROWN_STAINED_GLASS_PANE.defaultBlockState().setValue(IronBarsBlock.NORTH, true)).setValue(IronBarsBlock.SOUTH, true)), (Object)new ProcessorRule(new BlockStateMatchTest((BlockState)((BlockState)Blocks.GLASS_PANE.defaultBlockState().setValue(IronBarsBlock.EAST, true)).setValue(IronBarsBlock.WEST, true)), AlwaysTrueTest.INSTANCE, (BlockState)((BlockState)Blocks.BROWN_STAINED_GLASS_PANE.defaultBlockState().setValue(IronBarsBlock.EAST, true)).setValue(IronBarsBlock.WEST, true)), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.WHEAT, 0.1f), AlwaysTrueTest.INSTANCE, Blocks.CARROTS.defaultBlockState()), (Object[])new ProcessorRule[]{new ProcessorRule(new RandomBlockMatchTest(Blocks.WHEAT, 0.8f), AlwaysTrueTest.INSTANCE, Blocks.POTATOES.defaultBlockState())}))));
    public static final StructureProcessorList ZOMBIE_TAIGA = ProcessorLists.register("zombie_taiga", (ImmutableList<StructureProcessor>)ImmutableList.of((Object)new RuleProcessor((List<? extends ProcessorRule>)ImmutableList.of((Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.COBBLESTONE, 0.8f), AlwaysTrueTest.INSTANCE, Blocks.MOSSY_COBBLESTONE.defaultBlockState()), (Object)new ProcessorRule(new TagMatchTest(BlockTags.DOORS), AlwaysTrueTest.INSTANCE, Blocks.AIR.defaultBlockState()), (Object)new ProcessorRule(new BlockMatchTest(Blocks.TORCH), AlwaysTrueTest.INSTANCE, Blocks.AIR.defaultBlockState()), (Object)new ProcessorRule(new BlockMatchTest(Blocks.WALL_TORCH), AlwaysTrueTest.INSTANCE, Blocks.AIR.defaultBlockState()), (Object)new ProcessorRule(new BlockMatchTest(Blocks.CAMPFIRE), AlwaysTrueTest.INSTANCE, (BlockState)Blocks.CAMPFIRE.defaultBlockState().setValue(CampfireBlock.LIT, false)), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.COBBLESTONE, 0.08f), AlwaysTrueTest.INSTANCE, Blocks.COBWEB.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.SPRUCE_LOG, 0.08f), AlwaysTrueTest.INSTANCE, Blocks.COBWEB.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.GLASS_PANE, 0.5f), AlwaysTrueTest.INSTANCE, Blocks.COBWEB.defaultBlockState()), (Object)new ProcessorRule(new BlockStateMatchTest((BlockState)((BlockState)Blocks.GLASS_PANE.defaultBlockState().setValue(IronBarsBlock.NORTH, true)).setValue(IronBarsBlock.SOUTH, true)), AlwaysTrueTest.INSTANCE, (BlockState)((BlockState)Blocks.BROWN_STAINED_GLASS_PANE.defaultBlockState().setValue(IronBarsBlock.NORTH, true)).setValue(IronBarsBlock.SOUTH, true)), (Object)new ProcessorRule(new BlockStateMatchTest((BlockState)((BlockState)Blocks.GLASS_PANE.defaultBlockState().setValue(IronBarsBlock.EAST, true)).setValue(IronBarsBlock.WEST, true)), AlwaysTrueTest.INSTANCE, (BlockState)((BlockState)Blocks.BROWN_STAINED_GLASS_PANE.defaultBlockState().setValue(IronBarsBlock.EAST, true)).setValue(IronBarsBlock.WEST, true)), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.WHEAT, 0.3f), AlwaysTrueTest.INSTANCE, Blocks.PUMPKIN_STEM.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.WHEAT, 0.2f), AlwaysTrueTest.INSTANCE, Blocks.POTATOES.defaultBlockState()), (Object[])new ProcessorRule[0]))));
    public static final StructureProcessorList ZOMBIE_DESERT = ProcessorLists.register("zombie_desert", (ImmutableList<StructureProcessor>)ImmutableList.of((Object)new RuleProcessor((List<? extends ProcessorRule>)ImmutableList.of((Object)new ProcessorRule(new TagMatchTest(BlockTags.DOORS), AlwaysTrueTest.INSTANCE, Blocks.AIR.defaultBlockState()), (Object)new ProcessorRule(new BlockMatchTest(Blocks.TORCH), AlwaysTrueTest.INSTANCE, Blocks.AIR.defaultBlockState()), (Object)new ProcessorRule(new BlockMatchTest(Blocks.WALL_TORCH), AlwaysTrueTest.INSTANCE, Blocks.AIR.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.SMOOTH_SANDSTONE, 0.08f), AlwaysTrueTest.INSTANCE, Blocks.COBWEB.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.CUT_SANDSTONE, 0.1f), AlwaysTrueTest.INSTANCE, Blocks.COBWEB.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.TERRACOTTA, 0.08f), AlwaysTrueTest.INSTANCE, Blocks.COBWEB.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.SMOOTH_SANDSTONE_STAIRS, 0.08f), AlwaysTrueTest.INSTANCE, Blocks.COBWEB.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.SMOOTH_SANDSTONE_SLAB, 0.08f), AlwaysTrueTest.INSTANCE, Blocks.COBWEB.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.WHEAT, 0.2f), AlwaysTrueTest.INSTANCE, Blocks.BEETROOTS.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.WHEAT, 0.1f), AlwaysTrueTest.INSTANCE, Blocks.MELON_STEM.defaultBlockState())))));
    public static final StructureProcessorList MOSSIFY_10_PERCENT = ProcessorLists.register("mossify_10_percent", (ImmutableList<StructureProcessor>)ImmutableList.of((Object)new RuleProcessor((List<? extends ProcessorRule>)ImmutableList.of((Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.COBBLESTONE, 0.1f), AlwaysTrueTest.INSTANCE, Blocks.MOSSY_COBBLESTONE.defaultBlockState())))));
    public static final StructureProcessorList MOSSIFY_20_PERCENT = ProcessorLists.register("mossify_20_percent", (ImmutableList<StructureProcessor>)ImmutableList.of((Object)new RuleProcessor((List<? extends ProcessorRule>)ImmutableList.of((Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.COBBLESTONE, 0.2f), AlwaysTrueTest.INSTANCE, Blocks.MOSSY_COBBLESTONE.defaultBlockState())))));
    public static final StructureProcessorList MOSSIFY_70_PERCENT = ProcessorLists.register("mossify_70_percent", (ImmutableList<StructureProcessor>)ImmutableList.of((Object)new RuleProcessor((List<? extends ProcessorRule>)ImmutableList.of((Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.COBBLESTONE, 0.7f), AlwaysTrueTest.INSTANCE, Blocks.MOSSY_COBBLESTONE.defaultBlockState())))));
    public static final StructureProcessorList STREET_PLAINS = ProcessorLists.register("street_plains", (ImmutableList<StructureProcessor>)ImmutableList.of((Object)new RuleProcessor((List<? extends ProcessorRule>)ImmutableList.of((Object)new ProcessorRule(new BlockMatchTest(Blocks.GRASS_PATH), new BlockMatchTest(Blocks.WATER), Blocks.OAK_PLANKS.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.GRASS_PATH, 0.1f), AlwaysTrueTest.INSTANCE, Blocks.GRASS_BLOCK.defaultBlockState()), (Object)new ProcessorRule(new BlockMatchTest(Blocks.GRASS_BLOCK), new BlockMatchTest(Blocks.WATER), Blocks.WATER.defaultBlockState()), (Object)new ProcessorRule(new BlockMatchTest(Blocks.DIRT), new BlockMatchTest(Blocks.WATER), Blocks.WATER.defaultBlockState())))));
    public static final StructureProcessorList STREET_SAVANNA = ProcessorLists.register("street_savanna", (ImmutableList<StructureProcessor>)ImmutableList.of((Object)new RuleProcessor((List<? extends ProcessorRule>)ImmutableList.of((Object)new ProcessorRule(new BlockMatchTest(Blocks.GRASS_PATH), new BlockMatchTest(Blocks.WATER), Blocks.ACACIA_PLANKS.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.GRASS_PATH, 0.2f), AlwaysTrueTest.INSTANCE, Blocks.GRASS_BLOCK.defaultBlockState()), (Object)new ProcessorRule(new BlockMatchTest(Blocks.GRASS_BLOCK), new BlockMatchTest(Blocks.WATER), Blocks.WATER.defaultBlockState()), (Object)new ProcessorRule(new BlockMatchTest(Blocks.DIRT), new BlockMatchTest(Blocks.WATER), Blocks.WATER.defaultBlockState())))));
    public static final StructureProcessorList STREET_SNOWY_OR_TAIGA = ProcessorLists.register("street_snowy_or_taiga", (ImmutableList<StructureProcessor>)ImmutableList.of((Object)new RuleProcessor((List<? extends ProcessorRule>)ImmutableList.of((Object)new ProcessorRule(new BlockMatchTest(Blocks.GRASS_PATH), new BlockMatchTest(Blocks.WATER), Blocks.SPRUCE_PLANKS.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.GRASS_PATH, 0.2f), AlwaysTrueTest.INSTANCE, Blocks.GRASS_BLOCK.defaultBlockState()), (Object)new ProcessorRule(new BlockMatchTest(Blocks.GRASS_BLOCK), new BlockMatchTest(Blocks.WATER), Blocks.WATER.defaultBlockState()), (Object)new ProcessorRule(new BlockMatchTest(Blocks.DIRT), new BlockMatchTest(Blocks.WATER), Blocks.WATER.defaultBlockState())))));
    public static final StructureProcessorList FARM_PLAINS = ProcessorLists.register("farm_plains", (ImmutableList<StructureProcessor>)ImmutableList.of((Object)new RuleProcessor((List<? extends ProcessorRule>)ImmutableList.of((Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.WHEAT, 0.3f), AlwaysTrueTest.INSTANCE, Blocks.CARROTS.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.WHEAT, 0.2f), AlwaysTrueTest.INSTANCE, Blocks.POTATOES.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.WHEAT, 0.1f), AlwaysTrueTest.INSTANCE, Blocks.BEETROOTS.defaultBlockState())))));
    public static final StructureProcessorList FARM_SAVANNA = ProcessorLists.register("farm_savanna", (ImmutableList<StructureProcessor>)ImmutableList.of((Object)new RuleProcessor((List<? extends ProcessorRule>)ImmutableList.of((Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.WHEAT, 0.1f), AlwaysTrueTest.INSTANCE, Blocks.MELON_STEM.defaultBlockState())))));
    public static final StructureProcessorList FARM_SNOWY = ProcessorLists.register("farm_snowy", (ImmutableList<StructureProcessor>)ImmutableList.of((Object)new RuleProcessor((List<? extends ProcessorRule>)ImmutableList.of((Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.WHEAT, 0.1f), AlwaysTrueTest.INSTANCE, Blocks.CARROTS.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.WHEAT, 0.8f), AlwaysTrueTest.INSTANCE, Blocks.POTATOES.defaultBlockState())))));
    public static final StructureProcessorList FARM_TAIGA = ProcessorLists.register("farm_taiga", (ImmutableList<StructureProcessor>)ImmutableList.of((Object)new RuleProcessor((List<? extends ProcessorRule>)ImmutableList.of((Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.WHEAT, 0.3f), AlwaysTrueTest.INSTANCE, Blocks.PUMPKIN_STEM.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.WHEAT, 0.2f), AlwaysTrueTest.INSTANCE, Blocks.POTATOES.defaultBlockState())))));
    public static final StructureProcessorList FARM_DESERT = ProcessorLists.register("farm_desert", (ImmutableList<StructureProcessor>)ImmutableList.of((Object)new RuleProcessor((List<? extends ProcessorRule>)ImmutableList.of((Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.WHEAT, 0.2f), AlwaysTrueTest.INSTANCE, Blocks.BEETROOTS.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.WHEAT, 0.1f), AlwaysTrueTest.INSTANCE, Blocks.MELON_STEM.defaultBlockState())))));
    public static final StructureProcessorList OUTPOST_ROT = ProcessorLists.register("outpost_rot", (ImmutableList<StructureProcessor>)ImmutableList.of((Object)new BlockRotProcessor(0.05f)));
    public static final StructureProcessorList BOTTOM_RAMPART = ProcessorLists.register("bottom_rampart", (ImmutableList<StructureProcessor>)ImmutableList.of((Object)new RuleProcessor((List<? extends ProcessorRule>)ImmutableList.of((Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.MAGMA_BLOCK, 0.75f), AlwaysTrueTest.INSTANCE, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS, 0.15f), AlwaysTrueTest.INSTANCE, Blocks.POLISHED_BLACKSTONE_BRICKS.defaultBlockState()), (Object)REMOVE_GILDED_BLACKSTONE, (Object)ADD_GILDED_BLACKSTONE))));
    public static final StructureProcessorList TREASURE_ROOMS = ProcessorLists.register("treasure_rooms", (ImmutableList<StructureProcessor>)ImmutableList.of((Object)new RuleProcessor((List<? extends ProcessorRule>)ImmutableList.of((Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.POLISHED_BLACKSTONE_BRICKS, 0.35f), AlwaysTrueTest.INSTANCE, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.CHISELED_POLISHED_BLACKSTONE, 0.1f), AlwaysTrueTest.INSTANCE, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.defaultBlockState()), (Object)REMOVE_GILDED_BLACKSTONE, (Object)ADD_GILDED_BLACKSTONE))));
    public static final StructureProcessorList HOUSING = ProcessorLists.register("housing", (ImmutableList<StructureProcessor>)ImmutableList.of((Object)new RuleProcessor((List<? extends ProcessorRule>)ImmutableList.of((Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.POLISHED_BLACKSTONE_BRICKS, 0.3f), AlwaysTrueTest.INSTANCE, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.BLACKSTONE, 1.0E-4f), AlwaysTrueTest.INSTANCE, Blocks.AIR.defaultBlockState()), (Object)REMOVE_GILDED_BLACKSTONE, (Object)ADD_GILDED_BLACKSTONE))));
    public static final StructureProcessorList SIDE_WALL_DEGRADATION = ProcessorLists.register("side_wall_degradation", (ImmutableList<StructureProcessor>)ImmutableList.of((Object)new RuleProcessor((List<? extends ProcessorRule>)ImmutableList.of((Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.CHISELED_POLISHED_BLACKSTONE, 0.5f), AlwaysTrueTest.INSTANCE, Blocks.AIR.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.GOLD_BLOCK, 0.1f), AlwaysTrueTest.INSTANCE, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.defaultBlockState()), (Object)REMOVE_GILDED_BLACKSTONE, (Object)ADD_GILDED_BLACKSTONE))));
    public static final StructureProcessorList STABLE_DEGRADATION = ProcessorLists.register("stable_degradation", (ImmutableList<StructureProcessor>)ImmutableList.of((Object)new RuleProcessor((List<? extends ProcessorRule>)ImmutableList.of((Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.POLISHED_BLACKSTONE_BRICKS, 0.1f), AlwaysTrueTest.INSTANCE, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.BLACKSTONE, 1.0E-4f), AlwaysTrueTest.INSTANCE, Blocks.AIR.defaultBlockState()), (Object)REMOVE_GILDED_BLACKSTONE, (Object)ADD_GILDED_BLACKSTONE))));
    public static final StructureProcessorList BASTION_GENERIC_DEGRADATION = ProcessorLists.register("bastion_generic_degradation", (ImmutableList<StructureProcessor>)ImmutableList.of((Object)new RuleProcessor((List<? extends ProcessorRule>)ImmutableList.of((Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.POLISHED_BLACKSTONE_BRICKS, 0.3f), AlwaysTrueTest.INSTANCE, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.BLACKSTONE, 1.0E-4f), AlwaysTrueTest.INSTANCE, Blocks.AIR.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.GOLD_BLOCK, 0.3f), AlwaysTrueTest.INSTANCE, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.defaultBlockState()), (Object)REMOVE_GILDED_BLACKSTONE, (Object)ADD_GILDED_BLACKSTONE))));
    public static final StructureProcessorList RAMPART_DEGRADATION = ProcessorLists.register("rampart_degradation", (ImmutableList<StructureProcessor>)ImmutableList.of((Object)new RuleProcessor((List<? extends ProcessorRule>)ImmutableList.of((Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.POLISHED_BLACKSTONE_BRICKS, 0.4f), AlwaysTrueTest.INSTANCE, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.BLACKSTONE, 0.01f), AlwaysTrueTest.INSTANCE, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.POLISHED_BLACKSTONE_BRICKS, 1.0E-4f), AlwaysTrueTest.INSTANCE, Blocks.AIR.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.BLACKSTONE, 1.0E-4f), AlwaysTrueTest.INSTANCE, Blocks.AIR.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.GOLD_BLOCK, 0.3f), AlwaysTrueTest.INSTANCE, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.defaultBlockState()), (Object)REMOVE_GILDED_BLACKSTONE, (Object)ADD_GILDED_BLACKSTONE))));
    public static final StructureProcessorList ENTRANCE_REPLACEMENT = ProcessorLists.register("entrance_replacement", (ImmutableList<StructureProcessor>)ImmutableList.of((Object)new RuleProcessor((List<? extends ProcessorRule>)ImmutableList.of((Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.CHISELED_POLISHED_BLACKSTONE, 0.5f), AlwaysTrueTest.INSTANCE, Blocks.AIR.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.GOLD_BLOCK, 0.6f), AlwaysTrueTest.INSTANCE, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.defaultBlockState()), (Object)REMOVE_GILDED_BLACKSTONE, (Object)ADD_GILDED_BLACKSTONE))));
    public static final StructureProcessorList BRIDGE = ProcessorLists.register("bridge", (ImmutableList<StructureProcessor>)ImmutableList.of((Object)new RuleProcessor((List<? extends ProcessorRule>)ImmutableList.of((Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.POLISHED_BLACKSTONE_BRICKS, 0.3f), AlwaysTrueTest.INSTANCE, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.BLACKSTONE, 1.0E-4f), AlwaysTrueTest.INSTANCE, Blocks.AIR.defaultBlockState())))));
    public static final StructureProcessorList ROOF = ProcessorLists.register("roof", (ImmutableList<StructureProcessor>)ImmutableList.of((Object)new RuleProcessor((List<? extends ProcessorRule>)ImmutableList.of((Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.POLISHED_BLACKSTONE_BRICKS, 0.3f), AlwaysTrueTest.INSTANCE, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.POLISHED_BLACKSTONE_BRICKS, 0.15f), AlwaysTrueTest.INSTANCE, Blocks.AIR.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.POLISHED_BLACKSTONE_BRICKS, 0.3f), AlwaysTrueTest.INSTANCE, Blocks.BLACKSTONE.defaultBlockState())))));
    public static final StructureProcessorList HIGH_WALL = ProcessorLists.register("high_wall", (ImmutableList<StructureProcessor>)ImmutableList.of((Object)new RuleProcessor((List<? extends ProcessorRule>)ImmutableList.of((Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.POLISHED_BLACKSTONE_BRICKS, 0.01f), AlwaysTrueTest.INSTANCE, Blocks.AIR.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.POLISHED_BLACKSTONE_BRICKS, 0.5f), AlwaysTrueTest.INSTANCE, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.defaultBlockState()), (Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.POLISHED_BLACKSTONE_BRICKS, 0.3f), AlwaysTrueTest.INSTANCE, Blocks.BLACKSTONE.defaultBlockState()), (Object)REMOVE_GILDED_BLACKSTONE))));
    public static final StructureProcessorList HIGH_RAMPART = ProcessorLists.register("high_rampart", (ImmutableList<StructureProcessor>)ImmutableList.of((Object)new RuleProcessor((List<? extends ProcessorRule>)ImmutableList.of((Object)new ProcessorRule(new RandomBlockMatchTest(Blocks.GOLD_BLOCK, 0.3f), AlwaysTrueTest.INSTANCE, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.defaultBlockState()), (Object)new ProcessorRule(AlwaysTrueTest.INSTANCE, AlwaysTrueTest.INSTANCE, new AxisAlignedLinearPosTest(0.0f, 0.05f, 0, 100, Direction.Axis.Y), Blocks.AIR.defaultBlockState()), (Object)REMOVE_GILDED_BLACKSTONE))));

    private static StructureProcessorList register(String string, ImmutableList<StructureProcessor> immutableList) {
        ResourceLocation resourceLocation = new ResourceLocation(string);
        StructureProcessorList structureProcessorList = new StructureProcessorList((List<StructureProcessor>)immutableList);
        return BuiltinRegistries.register(BuiltinRegistries.PROCESSOR_LIST, resourceLocation, structureProcessorList);
    }
}

