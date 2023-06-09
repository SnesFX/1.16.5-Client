/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.data.worldgen;

import net.minecraft.data.worldgen.Carvers;
import net.minecraft.data.worldgen.Features;
import net.minecraft.data.worldgen.StructureFeatures;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.animal.horse.Donkey;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.MineshaftConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ShipwreckConfiguration;

public class BiomeDefaultFeatures {
    public static void addDefaultOverworldLandMesaStructures(BiomeGenerationSettings.Builder builder) {
        builder.addStructureStart(StructureFeatures.MINESHAFT_MESA);
        builder.addStructureStart(StructureFeatures.STRONGHOLD);
    }

    public static void addDefaultOverworldLandStructures(BiomeGenerationSettings.Builder builder) {
        builder.addStructureStart(StructureFeatures.MINESHAFT);
        builder.addStructureStart(StructureFeatures.STRONGHOLD);
    }

    public static void addDefaultOverworldOceanStructures(BiomeGenerationSettings.Builder builder) {
        builder.addStructureStart(StructureFeatures.MINESHAFT);
        builder.addStructureStart(StructureFeatures.SHIPWRECK);
    }

    public static void addDefaultCarvers(BiomeGenerationSettings.Builder builder) {
        builder.addCarver(GenerationStep.Carving.AIR, Carvers.CAVE);
        builder.addCarver(GenerationStep.Carving.AIR, Carvers.CANYON);
    }

    public static void addOceanCarvers(BiomeGenerationSettings.Builder builder) {
        builder.addCarver(GenerationStep.Carving.AIR, Carvers.OCEAN_CAVE);
        builder.addCarver(GenerationStep.Carving.AIR, Carvers.CANYON);
        builder.addCarver(GenerationStep.Carving.LIQUID, Carvers.UNDERWATER_CANYON);
        builder.addCarver(GenerationStep.Carving.LIQUID, Carvers.UNDERWATER_CAVE);
    }

    public static void addDefaultLakes(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.LAKES, Features.LAKE_WATER);
        builder.addFeature(GenerationStep.Decoration.LAKES, Features.LAKE_LAVA);
    }

    public static void addDesertLakes(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.LAKES, Features.LAKE_LAVA);
    }

    public static void addDefaultMonsterRoom(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_STRUCTURES, Features.MONSTER_ROOM);
    }

    public static void addDefaultUndergroundVariety(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Features.ORE_DIRT);
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Features.ORE_GRAVEL);
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Features.ORE_GRANITE);
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Features.ORE_DIORITE);
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Features.ORE_ANDESITE);
    }

    public static void addDefaultOres(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Features.ORE_COAL);
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Features.ORE_IRON);
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Features.ORE_GOLD);
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Features.ORE_REDSTONE);
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Features.ORE_DIAMOND);
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Features.ORE_LAPIS);
    }

    public static void addExtraGold(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Features.ORE_GOLD_EXTRA);
    }

    public static void addExtraEmeralds(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Features.ORE_EMERALD);
    }

    public static void addInfestedStone(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.ORE_INFESTED);
    }

    public static void addDefaultSoftDisks(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Features.DISK_SAND);
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Features.DISK_CLAY);
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Features.DISK_GRAVEL);
    }

    public static void addSwampClayDisk(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Features.DISK_CLAY);
    }

    public static void addMossyStoneBlock(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, Features.FOREST_ROCK);
    }

    public static void addFerns(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_LARGE_FERN);
    }

    public static void addBerryBushes(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_BERRY_DECORATED);
    }

    public static void addSparseBerryBushes(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_BERRY_SPARSE);
    }

    public static void addLightBambooVegetation(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.BAMBOO_LIGHT);
    }

    public static void addBambooVegetation(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.BAMBOO);
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.BAMBOO_VEGETATION);
    }

    public static void addTaigaTrees(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.TAIGA_VEGETATION);
    }

    public static void addWaterTrees(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.TREES_WATER);
    }

    public static void addBirchTrees(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.TREES_BIRCH);
    }

    public static void addOtherBirchTrees(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.BIRCH_OTHER);
    }

    public static void addTallBirchTrees(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.BIRCH_TALL);
    }

    public static void addSavannaTrees(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.TREES_SAVANNA);
    }

    public static void addShatteredSavannaTrees(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.TREES_SHATTERED_SAVANNA);
    }

    public static void addMountainTrees(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.TREES_MOUNTAIN);
    }

    public static void addMountainEdgeTrees(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.TREES_MOUNTAIN_EDGE);
    }

    public static void addJungleTrees(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.TREES_JUNGLE);
    }

    public static void addJungleEdgeTrees(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.TREES_JUNGLE_EDGE);
    }

    public static void addBadlandsTrees(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.OAK_BADLANDS);
    }

    public static void addSnowyTrees(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.SPRUCE_SNOWY);
    }

    public static void addJungleGrass(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_GRASS_JUNGLE);
    }

    public static void addSavannaGrass(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_TALL_GRASS);
    }

    public static void addShatteredSavannaGrass(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_GRASS_NORMAL);
    }

    public static void addSavannaExtraGrass(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_GRASS_SAVANNA);
    }

    public static void addBadlandGrass(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_GRASS_BADLANDS);
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_DEAD_BUSH_BADLANDS);
    }

    public static void addForestFlowers(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.FOREST_FLOWER_VEGETATION);
    }

    public static void addForestGrass(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_GRASS_FOREST);
    }

    public static void addSwampVegetation(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.SWAMP_TREE);
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.FLOWER_SWAMP);
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_GRASS_NORMAL);
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_DEAD_BUSH);
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_WATERLILLY);
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.BROWN_MUSHROOM_SWAMP);
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.RED_MUSHROOM_SWAMP);
    }

    public static void addMushroomFieldVegetation(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.MUSHROOM_FIELD_VEGETATION);
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.BROWN_MUSHROOM_TAIGA);
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.RED_MUSHROOM_TAIGA);
    }

    public static void addPlainVegetation(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PLAIN_VEGETATION);
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.FLOWER_PLAIN_DECORATED);
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_GRASS_PLAIN);
    }

    public static void addDesertVegetation(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_DEAD_BUSH_2);
    }

    public static void addGiantTaigaVegetation(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_GRASS_TAIGA);
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_DEAD_BUSH);
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.BROWN_MUSHROOM_GIANT);
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.RED_MUSHROOM_GIANT);
    }

    public static void addDefaultFlowers(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.FLOWER_DEFAULT);
    }

    public static void addWarmFlowers(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.FLOWER_WARM);
    }

    public static void addDefaultGrass(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_GRASS_BADLANDS);
    }

    public static void addTaigaGrass(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_GRASS_TAIGA_2);
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.BROWN_MUSHROOM_TAIGA);
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.RED_MUSHROOM_TAIGA);
    }

    public static void addPlainGrass(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_TALL_GRASS_2);
    }

    public static void addDefaultMushrooms(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.BROWN_MUSHROOM_NORMAL);
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.RED_MUSHROOM_NORMAL);
    }

    public static void addDefaultExtraVegetation(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_SUGAR_CANE);
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_PUMPKIN);
    }

    public static void addBadlandExtraVegetation(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_SUGAR_CANE_BADLANDS);
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_PUMPKIN);
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_CACTUS_DECORATED);
    }

    public static void addJungleExtraVegetation(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_MELON);
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.VINES);
    }

    public static void addDesertExtraVegetation(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_SUGAR_CANE_DESERT);
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_PUMPKIN);
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_CACTUS_DESERT);
    }

    public static void addSwampExtraVegetation(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_SUGAR_CANE_SWAMP);
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_PUMPKIN);
    }

    public static void addDesertExtraDecoration(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, Features.WELL);
    }

    public static void addFossilDecoration(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_STRUCTURES, Features.FOSSIL);
    }

    public static void addColdOceanExtraVegetation(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.KELP_COLD);
    }

    public static void addDefaultSeagrass(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.SEAGRASS_SIMPLE);
    }

    public static void addLukeWarmKelp(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.KELP_WARM);
    }

    public static void addDefaultSprings(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.SPRING_WATER);
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.SPRING_LAVA);
    }

    public static void addIcebergs(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, Features.ICEBERG_PACKED);
        builder.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, Features.ICEBERG_BLUE);
    }

    public static void addBlueIce(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, Features.BLUE_ICE);
    }

    public static void addSurfaceFreezing(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, Features.FREEZE_TOP_LAYER);
    }

    public static void addNetherDefaultOres(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.ORE_GRAVEL_NETHER);
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.ORE_BLACKSTONE);
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.ORE_GOLD_NETHER);
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.ORE_QUARTZ_NETHER);
        BiomeDefaultFeatures.addAncientDebris(builder);
    }

    public static void addAncientDebris(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.ORE_DEBRIS_LARGE);
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.ORE_DEBRIS_SMALL);
    }

    public static void farmAnimals(MobSpawnSettings.Builder builder) {
        builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.SHEEP, 12, 4, 4));
        builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.PIG, 10, 4, 4));
        builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.CHICKEN, 10, 4, 4));
        builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.COW, 8, 4, 4));
    }

    public static void ambientSpawns(MobSpawnSettings.Builder builder) {
        builder.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.BAT, 10, 8, 8));
    }

    public static void commonSpawns(MobSpawnSettings.Builder builder) {
        BiomeDefaultFeatures.ambientSpawns(builder);
        BiomeDefaultFeatures.monsters(builder, 95, 5, 100);
    }

    public static void oceanSpawns(MobSpawnSettings.Builder builder, int n, int n2, int n3) {
        builder.addSpawn(MobCategory.WATER_CREATURE, new MobSpawnSettings.SpawnerData(EntityType.SQUID, n, 1, n2));
        builder.addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.COD, n3, 3, 6));
        BiomeDefaultFeatures.commonSpawns(builder);
        builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.DROWNED, 5, 1, 1));
    }

    public static void warmOceanSpawns(MobSpawnSettings.Builder builder, int n, int n2) {
        builder.addSpawn(MobCategory.WATER_CREATURE, new MobSpawnSettings.SpawnerData(EntityType.SQUID, n, n2, 4));
        builder.addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.TROPICAL_FISH, 25, 8, 8));
        builder.addSpawn(MobCategory.WATER_CREATURE, new MobSpawnSettings.SpawnerData(EntityType.DOLPHIN, 2, 1, 2));
        BiomeDefaultFeatures.commonSpawns(builder);
    }

    public static void plainsSpawns(MobSpawnSettings.Builder builder) {
        BiomeDefaultFeatures.farmAnimals(builder);
        builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.HORSE, 5, 2, 6));
        builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.DONKEY, 1, 1, 3));
        BiomeDefaultFeatures.commonSpawns(builder);
    }

    public static void snowySpawns(MobSpawnSettings.Builder builder) {
        builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 10, 2, 3));
        builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.POLAR_BEAR, 1, 1, 2));
        BiomeDefaultFeatures.ambientSpawns(builder);
        BiomeDefaultFeatures.monsters(builder, 95, 5, 20);
        builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.STRAY, 80, 4, 4));
    }

    public static void desertSpawns(MobSpawnSettings.Builder builder) {
        builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 4, 2, 3));
        BiomeDefaultFeatures.ambientSpawns(builder);
        BiomeDefaultFeatures.monsters(builder, 19, 1, 100);
        builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.HUSK, 80, 4, 4));
    }

    public static void monsters(MobSpawnSettings.Builder builder, int n, int n2, int n3) {
        builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.SPIDER, 100, 4, 4));
        builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.ZOMBIE, n, 4, 4));
        builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.ZOMBIE_VILLAGER, n2, 1, 1));
        builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.SKELETON, n3, 4, 4));
        builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.CREEPER, 100, 4, 4));
        builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.SLIME, 100, 4, 4));
        builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.ENDERMAN, 10, 1, 4));
        builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.WITCH, 5, 1, 1));
    }

    public static void mooshroomSpawns(MobSpawnSettings.Builder builder) {
        builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.MOOSHROOM, 8, 4, 8));
        BiomeDefaultFeatures.ambientSpawns(builder);
    }

    public static void baseJungleSpawns(MobSpawnSettings.Builder builder) {
        BiomeDefaultFeatures.farmAnimals(builder);
        builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.CHICKEN, 10, 4, 4));
        BiomeDefaultFeatures.commonSpawns(builder);
    }

    public static void endSpawns(MobSpawnSettings.Builder builder) {
        builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.ENDERMAN, 10, 4, 4));
    }
}

