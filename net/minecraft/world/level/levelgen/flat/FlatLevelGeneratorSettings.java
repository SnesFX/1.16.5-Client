/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.Products
 *  com.mojang.datafixers.Products$P6
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Function6
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.PrimitiveCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.level.levelgen.flat;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Function6;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.Features;
import net.minecraft.data.worldgen.StructureFeatures;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.SwamplandHutFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.LayerConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.MineshaftConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OceanRuinConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RuinedPortalConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ShipwreckConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.StrongholdConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.surfacebuilders.ConfiguredSurfaceBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FlatLevelGeneratorSettings {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Codec<FlatLevelGeneratorSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)RegistryLookupCodec.create(Registry.BIOME_REGISTRY).forGetter(flatLevelGeneratorSettings -> flatLevelGeneratorSettings.biomes), (App)StructureSettings.CODEC.fieldOf("structures").forGetter(FlatLevelGeneratorSettings::structureSettings), (App)FlatLayerInfo.CODEC.listOf().fieldOf("layers").forGetter(FlatLevelGeneratorSettings::getLayersInfo), (App)Codec.BOOL.fieldOf("lakes").orElse((Object)false).forGetter(flatLevelGeneratorSettings -> flatLevelGeneratorSettings.addLakes), (App)Codec.BOOL.fieldOf("features").orElse((Object)false).forGetter(flatLevelGeneratorSettings -> flatLevelGeneratorSettings.decoration), (App)Biome.CODEC.optionalFieldOf("biome").orElseGet(Optional::empty).forGetter(flatLevelGeneratorSettings -> Optional.of(flatLevelGeneratorSettings.biome))).apply((Applicative)instance, (arg_0, arg_1, arg_2, arg_3, arg_4, arg_5) -> FlatLevelGeneratorSettings.new(arg_0, arg_1, arg_2, arg_3, arg_4, arg_5))).stable();
    private static final Map<StructureFeature<?>, ConfiguredStructureFeature<?, ?>> STRUCTURE_FEATURES = Util.make(Maps.newHashMap(), hashMap -> {
        hashMap.put(StructureFeature.MINESHAFT, StructureFeatures.MINESHAFT);
        hashMap.put(StructureFeature.VILLAGE, StructureFeatures.VILLAGE_PLAINS);
        hashMap.put(StructureFeature.STRONGHOLD, StructureFeatures.STRONGHOLD);
        hashMap.put(StructureFeature.SWAMP_HUT, StructureFeatures.SWAMP_HUT);
        hashMap.put(StructureFeature.DESERT_PYRAMID, StructureFeatures.DESERT_PYRAMID);
        hashMap.put(StructureFeature.JUNGLE_TEMPLE, StructureFeatures.JUNGLE_TEMPLE);
        hashMap.put(StructureFeature.IGLOO, StructureFeatures.IGLOO);
        hashMap.put(StructureFeature.OCEAN_RUIN, StructureFeatures.OCEAN_RUIN_COLD);
        hashMap.put(StructureFeature.SHIPWRECK, StructureFeatures.SHIPWRECK);
        hashMap.put(StructureFeature.OCEAN_MONUMENT, StructureFeatures.OCEAN_MONUMENT);
        hashMap.put(StructureFeature.END_CITY, StructureFeatures.END_CITY);
        hashMap.put(StructureFeature.WOODLAND_MANSION, StructureFeatures.WOODLAND_MANSION);
        hashMap.put(StructureFeature.NETHER_BRIDGE, StructureFeatures.NETHER_BRIDGE);
        hashMap.put(StructureFeature.PILLAGER_OUTPOST, StructureFeatures.PILLAGER_OUTPOST);
        hashMap.put(StructureFeature.RUINED_PORTAL, StructureFeatures.RUINED_PORTAL_STANDARD);
        hashMap.put(StructureFeature.BASTION_REMNANT, StructureFeatures.BASTION_REMNANT);
    });
    private final Registry<Biome> biomes;
    private final StructureSettings structureSettings;
    private final List<FlatLayerInfo> layersInfo = Lists.newArrayList();
    private Supplier<Biome> biome;
    private final BlockState[] layers = new BlockState[256];
    private boolean voidGen;
    private boolean decoration = false;
    private boolean addLakes = false;

    public FlatLevelGeneratorSettings(Registry<Biome> registry, StructureSettings structureSettings, List<FlatLayerInfo> list, boolean bl, boolean bl2, Optional<Supplier<Biome>> optional) {
        this(structureSettings, registry);
        if (bl) {
            this.setAddLakes();
        }
        if (bl2) {
            this.setDecoration();
        }
        this.layersInfo.addAll(list);
        this.updateLayers();
        if (!optional.isPresent()) {
            LOGGER.error("Unknown biome, defaulting to plains");
            this.biome = () -> registry.getOrThrow(Biomes.PLAINS);
        } else {
            this.biome = optional.get();
        }
    }

    public FlatLevelGeneratorSettings(StructureSettings structureSettings, Registry<Biome> registry) {
        this.biomes = registry;
        this.structureSettings = structureSettings;
        this.biome = () -> registry.getOrThrow(Biomes.PLAINS);
    }

    public FlatLevelGeneratorSettings withStructureSettings(StructureSettings structureSettings) {
        return this.withLayers(this.layersInfo, structureSettings);
    }

    public FlatLevelGeneratorSettings withLayers(List<FlatLayerInfo> list, StructureSettings structureSettings) {
        FlatLevelGeneratorSettings flatLevelGeneratorSettings = new FlatLevelGeneratorSettings(structureSettings, this.biomes);
        for (FlatLayerInfo flatLayerInfo : list) {
            flatLevelGeneratorSettings.layersInfo.add(new FlatLayerInfo(flatLayerInfo.getHeight(), flatLayerInfo.getBlockState().getBlock()));
            flatLevelGeneratorSettings.updateLayers();
        }
        flatLevelGeneratorSettings.setBiome(this.biome);
        if (this.decoration) {
            flatLevelGeneratorSettings.setDecoration();
        }
        if (this.addLakes) {
            flatLevelGeneratorSettings.setAddLakes();
        }
        return flatLevelGeneratorSettings;
    }

    public void setDecoration() {
        this.decoration = true;
    }

    public void setAddLakes() {
        this.addLakes = true;
    }

    public Biome getBiomeFromSettings() {
        boolean bl;
        Object object;
        int n;
        Biome biome = this.getBiome();
        BiomeGenerationSettings biomeGenerationSettings = biome.getGenerationSettings();
        BiomeGenerationSettings.Builder builder = new BiomeGenerationSettings.Builder().surfaceBuilder(biomeGenerationSettings.getSurfaceBuilder());
        if (this.addLakes) {
            builder.addFeature(GenerationStep.Decoration.LAKES, Features.LAKE_WATER);
            builder.addFeature(GenerationStep.Decoration.LAKES, Features.LAKE_LAVA);
        }
        for (Map.Entry<StructureFeature<?>, StructureFeatureConfiguration> arrblockState2 : this.structureSettings.structureConfig().entrySet()) {
            builder.addStructureStart(biomeGenerationSettings.withBiomeConfig(STRUCTURE_FEATURES.get(arrblockState2.getKey())));
        }
        boolean bl2 = bl = (!this.voidGen || this.biomes.getResourceKey(biome).equals(Optional.of(Biomes.THE_VOID))) && this.decoration;
        if (bl) {
            List<List<Supplier<ConfiguredFeature<?, ?>>>> list = biomeGenerationSettings.features();
            for (n = 0; n < list.size(); ++n) {
                if (n == GenerationStep.Decoration.UNDERGROUND_STRUCTURES.ordinal() || n == GenerationStep.Decoration.SURFACE_STRUCTURES.ordinal()) continue;
                object = list.get(n);
                Iterator iterator = object.iterator();
                while (iterator.hasNext()) {
                    Supplier supplier = (Supplier)iterator.next();
                    builder.addFeature(n, supplier);
                }
            }
        }
        BlockState[] arrblockState = this.getLayers();
        for (n = 0; n < arrblockState.length; ++n) {
            object = arrblockState[n];
            if (object == null || Heightmap.Types.MOTION_BLOCKING.isOpaque().test((BlockState)object)) continue;
            this.layers[n] = null;
            builder.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, Feature.FILL_LAYER.configured(new LayerConfiguration(n, (BlockState)object)));
        }
        return new Biome.BiomeBuilder().precipitation(biome.getPrecipitation()).biomeCategory(biome.getBiomeCategory()).depth(biome.getDepth()).scale(biome.getScale()).temperature(biome.getBaseTemperature()).downfall(biome.getDownfall()).specialEffects(biome.getSpecialEffects()).generationSettings(builder.build()).mobSpawnSettings(biome.getMobSettings()).build();
    }

    public StructureSettings structureSettings() {
        return this.structureSettings;
    }

    public Biome getBiome() {
        return this.biome.get();
    }

    public void setBiome(Supplier<Biome> supplier) {
        this.biome = supplier;
    }

    public List<FlatLayerInfo> getLayersInfo() {
        return this.layersInfo;
    }

    public BlockState[] getLayers() {
        return this.layers;
    }

    public void updateLayers() {
        Arrays.fill(this.layers, 0, this.layers.length, null);
        int n = 0;
        for (FlatLayerInfo flatLayerInfo : this.layersInfo) {
            flatLayerInfo.setStart(n);
            n += flatLayerInfo.getHeight();
        }
        this.voidGen = true;
        for (FlatLayerInfo flatLayerInfo : this.layersInfo) {
            for (int i = flatLayerInfo.getStart(); i < flatLayerInfo.getStart() + flatLayerInfo.getHeight(); ++i) {
                BlockState blockState = flatLayerInfo.getBlockState();
                if (blockState.is(Blocks.AIR)) continue;
                this.voidGen = false;
                this.layers[i] = blockState;
            }
        }
    }

    public static FlatLevelGeneratorSettings getDefault(Registry<Biome> registry) {
        StructureSettings structureSettings = new StructureSettings(Optional.of(StructureSettings.DEFAULT_STRONGHOLD), Maps.newHashMap((Map)ImmutableMap.of(StructureFeature.VILLAGE, (Object)StructureSettings.DEFAULTS.get(StructureFeature.VILLAGE))));
        FlatLevelGeneratorSettings flatLevelGeneratorSettings = new FlatLevelGeneratorSettings(structureSettings, registry);
        flatLevelGeneratorSettings.biome = () -> registry.getOrThrow(Biomes.PLAINS);
        flatLevelGeneratorSettings.getLayersInfo().add(new FlatLayerInfo(1, Blocks.BEDROCK));
        flatLevelGeneratorSettings.getLayersInfo().add(new FlatLayerInfo(2, Blocks.DIRT));
        flatLevelGeneratorSettings.getLayersInfo().add(new FlatLayerInfo(1, Blocks.GRASS_BLOCK));
        flatLevelGeneratorSettings.updateLayers();
        return flatLevelGeneratorSettings;
    }
}

