/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.AbstractFlowerFeature;
import net.minecraft.world.level.levelgen.feature.BambooFeature;
import net.minecraft.world.level.levelgen.feature.BasaltColumnsFeature;
import net.minecraft.world.level.levelgen.feature.BasaltPillarFeature;
import net.minecraft.world.level.levelgen.feature.BlockBlobFeature;
import net.minecraft.world.level.levelgen.feature.BlockPileFeature;
import net.minecraft.world.level.levelgen.feature.BlueIceFeature;
import net.minecraft.world.level.levelgen.feature.BonusChestFeature;
import net.minecraft.world.level.levelgen.feature.ChorusPlantFeature;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.CoralClawFeature;
import net.minecraft.world.level.levelgen.feature.CoralMushroomFeature;
import net.minecraft.world.level.levelgen.feature.CoralTreeFeature;
import net.minecraft.world.level.levelgen.feature.DecoratedFeature;
import net.minecraft.world.level.levelgen.feature.DefaultFlowerFeature;
import net.minecraft.world.level.levelgen.feature.DeltaFeature;
import net.minecraft.world.level.levelgen.feature.DesertWellFeature;
import net.minecraft.world.level.levelgen.feature.DiskReplaceFeature;
import net.minecraft.world.level.levelgen.feature.EndGatewayFeature;
import net.minecraft.world.level.levelgen.feature.EndIslandFeature;
import net.minecraft.world.level.levelgen.feature.FillLayerFeature;
import net.minecraft.world.level.levelgen.feature.FossilFeature;
import net.minecraft.world.level.levelgen.feature.GlowstoneFeature;
import net.minecraft.world.level.levelgen.feature.HugeBrownMushroomFeature;
import net.minecraft.world.level.levelgen.feature.HugeFungusConfiguration;
import net.minecraft.world.level.levelgen.feature.HugeFungusFeature;
import net.minecraft.world.level.levelgen.feature.HugeRedMushroomFeature;
import net.minecraft.world.level.levelgen.feature.IcePatchFeature;
import net.minecraft.world.level.levelgen.feature.IceSpikeFeature;
import net.minecraft.world.level.levelgen.feature.IcebergFeature;
import net.minecraft.world.level.levelgen.feature.KelpFeature;
import net.minecraft.world.level.levelgen.feature.LakeFeature;
import net.minecraft.world.level.levelgen.feature.MonsterRoomFeature;
import net.minecraft.world.level.levelgen.feature.NetherForestVegetationFeature;
import net.minecraft.world.level.levelgen.feature.NoOpFeature;
import net.minecraft.world.level.levelgen.feature.NoSurfaceOreFeature;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.RandomBooleanSelectorFeature;
import net.minecraft.world.level.levelgen.feature.RandomPatchFeature;
import net.minecraft.world.level.levelgen.feature.RandomSelectorFeature;
import net.minecraft.world.level.levelgen.feature.ReplaceBlobsFeature;
import net.minecraft.world.level.levelgen.feature.ReplaceBlockFeature;
import net.minecraft.world.level.levelgen.feature.SeaPickleFeature;
import net.minecraft.world.level.levelgen.feature.SeagrassFeature;
import net.minecraft.world.level.levelgen.feature.SimpleBlockFeature;
import net.minecraft.world.level.levelgen.feature.SimpleRandomSelectorFeature;
import net.minecraft.world.level.levelgen.feature.SnowAndFreezeFeature;
import net.minecraft.world.level.levelgen.feature.SpikeFeature;
import net.minecraft.world.level.levelgen.feature.SpringFeature;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.TwistingVinesFeature;
import net.minecraft.world.level.levelgen.feature.VinesFeature;
import net.minecraft.world.level.levelgen.feature.VoidStartPlatformFeature;
import net.minecraft.world.level.levelgen.feature.WeepingVinesFeature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockPileConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ColumnFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.CountConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratedFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.DeltaFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.DiskConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.EndGatewayConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.LayerConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomBooleanFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceSphereConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleRandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SpikeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SpringConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;

public abstract class Feature<FC extends FeatureConfiguration> {
    public static final Feature<NoneFeatureConfiguration> NO_OP = Feature.register("no_op", new NoOpFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<TreeConfiguration> TREE = Feature.register("tree", new TreeFeature(TreeConfiguration.CODEC));
    public static final AbstractFlowerFeature<RandomPatchConfiguration> FLOWER = Feature.register("flower", new DefaultFlowerFeature(RandomPatchConfiguration.CODEC));
    public static final AbstractFlowerFeature<RandomPatchConfiguration> NO_BONEMEAL_FLOWER = Feature.register("no_bonemeal_flower", new DefaultFlowerFeature(RandomPatchConfiguration.CODEC));
    public static final Feature<RandomPatchConfiguration> RANDOM_PATCH = Feature.register("random_patch", new RandomPatchFeature(RandomPatchConfiguration.CODEC));
    public static final Feature<BlockPileConfiguration> BLOCK_PILE = Feature.register("block_pile", new BlockPileFeature(BlockPileConfiguration.CODEC));
    public static final Feature<SpringConfiguration> SPRING = Feature.register("spring_feature", new SpringFeature(SpringConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> CHORUS_PLANT = Feature.register("chorus_plant", new ChorusPlantFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<ReplaceBlockConfiguration> EMERALD_ORE = Feature.register("emerald_ore", new ReplaceBlockFeature(ReplaceBlockConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> VOID_START_PLATFORM = Feature.register("void_start_platform", new VoidStartPlatformFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> DESERT_WELL = Feature.register("desert_well", new DesertWellFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> FOSSIL = Feature.register("fossil", new FossilFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<HugeMushroomFeatureConfiguration> HUGE_RED_MUSHROOM = Feature.register("huge_red_mushroom", new HugeRedMushroomFeature(HugeMushroomFeatureConfiguration.CODEC));
    public static final Feature<HugeMushroomFeatureConfiguration> HUGE_BROWN_MUSHROOM = Feature.register("huge_brown_mushroom", new HugeBrownMushroomFeature(HugeMushroomFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> ICE_SPIKE = Feature.register("ice_spike", new IceSpikeFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> GLOWSTONE_BLOB = Feature.register("glowstone_blob", new GlowstoneFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> FREEZE_TOP_LAYER = Feature.register("freeze_top_layer", new SnowAndFreezeFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> VINES = Feature.register("vines", new VinesFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> MONSTER_ROOM = Feature.register("monster_room", new MonsterRoomFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> BLUE_ICE = Feature.register("blue_ice", new BlueIceFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<BlockStateConfiguration> ICEBERG = Feature.register("iceberg", new IcebergFeature(BlockStateConfiguration.CODEC));
    public static final Feature<BlockStateConfiguration> FOREST_ROCK = Feature.register("forest_rock", new BlockBlobFeature(BlockStateConfiguration.CODEC));
    public static final Feature<DiskConfiguration> DISK = Feature.register("disk", new DiskReplaceFeature(DiskConfiguration.CODEC));
    public static final Feature<DiskConfiguration> ICE_PATCH = Feature.register("ice_patch", new IcePatchFeature(DiskConfiguration.CODEC));
    public static final Feature<BlockStateConfiguration> LAKE = Feature.register("lake", new LakeFeature(BlockStateConfiguration.CODEC));
    public static final Feature<OreConfiguration> ORE = Feature.register("ore", new OreFeature(OreConfiguration.CODEC));
    public static final Feature<SpikeConfiguration> END_SPIKE = Feature.register("end_spike", new SpikeFeature(SpikeConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> END_ISLAND = Feature.register("end_island", new EndIslandFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<EndGatewayConfiguration> END_GATEWAY = Feature.register("end_gateway", new EndGatewayFeature(EndGatewayConfiguration.CODEC));
    public static final SeagrassFeature SEAGRASS = Feature.register("seagrass", new SeagrassFeature(ProbabilityFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> KELP = Feature.register("kelp", new KelpFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> CORAL_TREE = Feature.register("coral_tree", new CoralTreeFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> CORAL_MUSHROOM = Feature.register("coral_mushroom", new CoralMushroomFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> CORAL_CLAW = Feature.register("coral_claw", new CoralClawFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<CountConfiguration> SEA_PICKLE = Feature.register("sea_pickle", new SeaPickleFeature(CountConfiguration.CODEC));
    public static final Feature<SimpleBlockConfiguration> SIMPLE_BLOCK = Feature.register("simple_block", new SimpleBlockFeature(SimpleBlockConfiguration.CODEC));
    public static final Feature<ProbabilityFeatureConfiguration> BAMBOO = Feature.register("bamboo", new BambooFeature(ProbabilityFeatureConfiguration.CODEC));
    public static final Feature<HugeFungusConfiguration> HUGE_FUNGUS = Feature.register("huge_fungus", new HugeFungusFeature(HugeFungusConfiguration.CODEC));
    public static final Feature<BlockPileConfiguration> NETHER_FOREST_VEGETATION = Feature.register("nether_forest_vegetation", new NetherForestVegetationFeature(BlockPileConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> WEEPING_VINES = Feature.register("weeping_vines", new WeepingVinesFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> TWISTING_VINES = Feature.register("twisting_vines", new TwistingVinesFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<ColumnFeatureConfiguration> BASALT_COLUMNS = Feature.register("basalt_columns", new BasaltColumnsFeature(ColumnFeatureConfiguration.CODEC));
    public static final Feature<DeltaFeatureConfiguration> DELTA_FEATURE = Feature.register("delta_feature", new DeltaFeature(DeltaFeatureConfiguration.CODEC));
    public static final Feature<ReplaceSphereConfiguration> REPLACE_BLOBS = Feature.register("netherrack_replace_blobs", new ReplaceBlobsFeature(ReplaceSphereConfiguration.CODEC));
    public static final Feature<LayerConfiguration> FILL_LAYER = Feature.register("fill_layer", new FillLayerFeature(LayerConfiguration.CODEC));
    public static final BonusChestFeature BONUS_CHEST = Feature.register("bonus_chest", new BonusChestFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> BASALT_PILLAR = Feature.register("basalt_pillar", new BasaltPillarFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<OreConfiguration> NO_SURFACE_ORE = Feature.register("no_surface_ore", new NoSurfaceOreFeature(OreConfiguration.CODEC));
    public static final Feature<RandomFeatureConfiguration> RANDOM_SELECTOR = Feature.register("random_selector", new RandomSelectorFeature(RandomFeatureConfiguration.CODEC));
    public static final Feature<SimpleRandomFeatureConfiguration> SIMPLE_RANDOM_SELECTOR = Feature.register("simple_random_selector", new SimpleRandomSelectorFeature(SimpleRandomFeatureConfiguration.CODEC));
    public static final Feature<RandomBooleanFeatureConfiguration> RANDOM_BOOLEAN_SELECTOR = Feature.register("random_boolean_selector", new RandomBooleanSelectorFeature(RandomBooleanFeatureConfiguration.CODEC));
    public static final Feature<DecoratedFeatureConfiguration> DECORATED = Feature.register("decorated", new DecoratedFeature(DecoratedFeatureConfiguration.CODEC));
    private final Codec<ConfiguredFeature<FC, Feature<FC>>> configuredCodec;

    private static <C extends FeatureConfiguration, F extends Feature<C>> F register(String string, F f) {
        return (F)Registry.register(Registry.FEATURE, string, f);
    }

    public Feature(Codec<FC> codec) {
        this.configuredCodec = codec.fieldOf("config").xmap(featureConfiguration -> new ConfiguredFeature<FeatureConfiguration, Feature>(this, (FeatureConfiguration)featureConfiguration), configuredFeature -> configuredFeature.config).codec();
    }

    public Codec<ConfiguredFeature<FC, Feature<FC>>> configuredCodec() {
        return this.configuredCodec;
    }

    public ConfiguredFeature<FC, ?> configured(FC FC) {
        return new ConfiguredFeature<FC, Feature>(this, FC);
    }

    protected void setBlock(LevelWriter levelWriter, BlockPos blockPos, BlockState blockState) {
        levelWriter.setBlock(blockPos, blockState, 3);
    }

    public abstract boolean place(WorldGenLevel var1, ChunkGenerator var2, Random var3, BlockPos var4, FC var5);

    protected static boolean isStone(Block block) {
        return block == Blocks.STONE || block == Blocks.GRANITE || block == Blocks.DIORITE || block == Blocks.ANDESITE;
    }

    public static boolean isDirt(Block block) {
        return block == Blocks.DIRT || block == Blocks.GRASS_BLOCK || block == Blocks.PODZOL || block == Blocks.COARSE_DIRT || block == Blocks.MYCELIUM;
    }

    public static boolean isGrassOrDirt(LevelSimulatedReader levelSimulatedReader, BlockPos blockPos) {
        return levelSimulatedReader.isStateAtPosition(blockPos, blockState -> Feature.isDirt(blockState.getBlock()));
    }

    public static boolean isAir(LevelSimulatedReader levelSimulatedReader, BlockPos blockPos) {
        return levelSimulatedReader.isStateAtPosition(blockPos, BlockBehaviour.BlockStateBase::isAir);
    }
}

