/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 */
package net.minecraft.client.gui.screens.worldselection;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.client.gui.screens.CreateBuffetWorldScreen;
import net.minecraft.client.gui.screens.CreateFlatWorldScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldGenSettingsComponent;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.biome.OverworldBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;

public abstract class WorldPreset {
    public static final WorldPreset NORMAL = new WorldPreset("default"){

        @Override
        protected ChunkGenerator generator(Registry<Biome> registry, Registry<NoiseGeneratorSettings> registry2, long l) {
            return new NoiseBasedChunkGenerator(new OverworldBiomeSource(l, false, false, registry), l, () -> registry2.getOrThrow(NoiseGeneratorSettings.OVERWORLD));
        }
    };
    private static final WorldPreset FLAT = new WorldPreset("flat"){

        @Override
        protected ChunkGenerator generator(Registry<Biome> registry, Registry<NoiseGeneratorSettings> registry2, long l) {
            return new FlatLevelSource(FlatLevelGeneratorSettings.getDefault(registry));
        }
    };
    private static final WorldPreset LARGE_BIOMES = new WorldPreset("large_biomes"){

        @Override
        protected ChunkGenerator generator(Registry<Biome> registry, Registry<NoiseGeneratorSettings> registry2, long l) {
            return new NoiseBasedChunkGenerator(new OverworldBiomeSource(l, false, true, registry), l, () -> registry2.getOrThrow(NoiseGeneratorSettings.OVERWORLD));
        }
    };
    public static final WorldPreset AMPLIFIED = new WorldPreset("amplified"){

        @Override
        protected ChunkGenerator generator(Registry<Biome> registry, Registry<NoiseGeneratorSettings> registry2, long l) {
            return new NoiseBasedChunkGenerator(new OverworldBiomeSource(l, false, false, registry), l, () -> registry2.getOrThrow(NoiseGeneratorSettings.AMPLIFIED));
        }
    };
    private static final WorldPreset SINGLE_BIOME_SURFACE = new WorldPreset("single_biome_surface"){

        @Override
        protected ChunkGenerator generator(Registry<Biome> registry, Registry<NoiseGeneratorSettings> registry2, long l) {
            return new NoiseBasedChunkGenerator(new FixedBiomeSource(registry.getOrThrow(Biomes.PLAINS)), l, () -> registry2.getOrThrow(NoiseGeneratorSettings.OVERWORLD));
        }
    };
    private static final WorldPreset SINGLE_BIOME_CAVES = new WorldPreset("single_biome_caves"){

        @Override
        public WorldGenSettings create(RegistryAccess.RegistryHolder registryHolder, long l, boolean bl, boolean bl2) {
            WritableRegistry<Biome> writableRegistry = registryHolder.registryOrThrow(Registry.BIOME_REGISTRY);
            WritableRegistry<DimensionType> writableRegistry2 = registryHolder.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
            WritableRegistry<NoiseGeneratorSettings> writableRegistry3 = registryHolder.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY);
            return new WorldGenSettings(l, bl, bl2, WorldGenSettings.withOverworld(DimensionType.defaultDimensions(writableRegistry2, writableRegistry, writableRegistry3, l), () -> writableRegistry2.getOrThrow(DimensionType.OVERWORLD_CAVES_LOCATION), this.generator(writableRegistry, writableRegistry3, l)));
        }

        @Override
        protected ChunkGenerator generator(Registry<Biome> registry, Registry<NoiseGeneratorSettings> registry2, long l) {
            return new NoiseBasedChunkGenerator(new FixedBiomeSource(registry.getOrThrow(Biomes.PLAINS)), l, () -> registry2.getOrThrow(NoiseGeneratorSettings.CAVES));
        }
    };
    private static final WorldPreset SINGLE_BIOME_FLOATING_ISLANDS = new WorldPreset("single_biome_floating_islands"){

        @Override
        protected ChunkGenerator generator(Registry<Biome> registry, Registry<NoiseGeneratorSettings> registry2, long l) {
            return new NoiseBasedChunkGenerator(new FixedBiomeSource(registry.getOrThrow(Biomes.PLAINS)), l, () -> registry2.getOrThrow(NoiseGeneratorSettings.FLOATING_ISLANDS));
        }
    };
    private static final WorldPreset DEBUG = new WorldPreset("debug_all_block_states"){

        @Override
        protected ChunkGenerator generator(Registry<Biome> registry, Registry<NoiseGeneratorSettings> registry2, long l) {
            return new DebugLevelSource(registry);
        }
    };
    protected static final List<WorldPreset> PRESETS = Lists.newArrayList((Object[])new WorldPreset[]{NORMAL, FLAT, LARGE_BIOMES, AMPLIFIED, SINGLE_BIOME_SURFACE, SINGLE_BIOME_CAVES, SINGLE_BIOME_FLOATING_ISLANDS, DEBUG});
    protected static final Map<Optional<WorldPreset>, PresetEditor> EDITORS = ImmutableMap.of(Optional.of(FLAT), (createWorldScreen, worldGenSettings) -> {
        ChunkGenerator chunkGenerator = worldGenSettings.overworld();
        return new CreateFlatWorldScreen(createWorldScreen, flatLevelGeneratorSettings -> createWorldScreen.worldGenSettingsComponent.updateSettings(new WorldGenSettings(worldGenSettings.seed(), worldGenSettings.generateFeatures(), worldGenSettings.generateBonusChest(), WorldGenSettings.withOverworld(createWorldScreen.worldGenSettingsComponent.registryHolder().registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY), worldGenSettings.dimensions(), (ChunkGenerator)new FlatLevelSource((FlatLevelGeneratorSettings)flatLevelGeneratorSettings)))), chunkGenerator instanceof FlatLevelSource ? ((FlatLevelSource)chunkGenerator).settings() : FlatLevelGeneratorSettings.getDefault(createWorldScreen.worldGenSettingsComponent.registryHolder().registryOrThrow(Registry.BIOME_REGISTRY)));
    }, Optional.of(SINGLE_BIOME_SURFACE), (createWorldScreen, worldGenSettings) -> new CreateBuffetWorldScreen(createWorldScreen, createWorldScreen.worldGenSettingsComponent.registryHolder(), biome -> createWorldScreen.worldGenSettingsComponent.updateSettings(WorldPreset.fromBuffetSettings(createWorldScreen.worldGenSettingsComponent.registryHolder(), worldGenSettings, SINGLE_BIOME_SURFACE, biome)), WorldPreset.parseBuffetSettings(createWorldScreen.worldGenSettingsComponent.registryHolder(), worldGenSettings)), Optional.of(SINGLE_BIOME_CAVES), (createWorldScreen, worldGenSettings) -> new CreateBuffetWorldScreen(createWorldScreen, createWorldScreen.worldGenSettingsComponent.registryHolder(), biome -> createWorldScreen.worldGenSettingsComponent.updateSettings(WorldPreset.fromBuffetSettings(createWorldScreen.worldGenSettingsComponent.registryHolder(), worldGenSettings, SINGLE_BIOME_CAVES, biome)), WorldPreset.parseBuffetSettings(createWorldScreen.worldGenSettingsComponent.registryHolder(), worldGenSettings)), Optional.of(SINGLE_BIOME_FLOATING_ISLANDS), (createWorldScreen, worldGenSettings) -> new CreateBuffetWorldScreen(createWorldScreen, createWorldScreen.worldGenSettingsComponent.registryHolder(), biome -> createWorldScreen.worldGenSettingsComponent.updateSettings(WorldPreset.fromBuffetSettings(createWorldScreen.worldGenSettingsComponent.registryHolder(), worldGenSettings, SINGLE_BIOME_FLOATING_ISLANDS, biome)), WorldPreset.parseBuffetSettings(createWorldScreen.worldGenSettingsComponent.registryHolder(), worldGenSettings)));
    private final Component description;

    private WorldPreset(String string) {
        this.description = new TranslatableComponent("generator." + string);
    }

    private static WorldGenSettings fromBuffetSettings(RegistryAccess registryAccess, WorldGenSettings worldGenSettings, WorldPreset worldPreset, Biome biome) {
        FixedBiomeSource fixedBiomeSource = new FixedBiomeSource(biome);
        WritableRegistry<DimensionType> writableRegistry = registryAccess.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
        WritableRegistry<NoiseGeneratorSettings> writableRegistry2 = registryAccess.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY);
        Supplier<NoiseGeneratorSettings> supplier = worldPreset == SINGLE_BIOME_CAVES ? () -> writableRegistry2.getOrThrow(NoiseGeneratorSettings.CAVES) : (worldPreset == SINGLE_BIOME_FLOATING_ISLANDS ? () -> writableRegistry2.getOrThrow(NoiseGeneratorSettings.FLOATING_ISLANDS) : () -> writableRegistry2.getOrThrow(NoiseGeneratorSettings.OVERWORLD));
        return new WorldGenSettings(worldGenSettings.seed(), worldGenSettings.generateFeatures(), worldGenSettings.generateBonusChest(), WorldGenSettings.withOverworld(writableRegistry, worldGenSettings.dimensions(), (ChunkGenerator)new NoiseBasedChunkGenerator(fixedBiomeSource, worldGenSettings.seed(), supplier)));
    }

    private static Biome parseBuffetSettings(RegistryAccess registryAccess, WorldGenSettings worldGenSettings) {
        return worldGenSettings.overworld().getBiomeSource().possibleBiomes().stream().findFirst().orElse(registryAccess.registryOrThrow(Registry.BIOME_REGISTRY).getOrThrow(Biomes.PLAINS));
    }

    public static Optional<WorldPreset> of(WorldGenSettings worldGenSettings) {
        ChunkGenerator chunkGenerator = worldGenSettings.overworld();
        if (chunkGenerator instanceof FlatLevelSource) {
            return Optional.of(FLAT);
        }
        if (chunkGenerator instanceof DebugLevelSource) {
            return Optional.of(DEBUG);
        }
        return Optional.empty();
    }

    public Component description() {
        return this.description;
    }

    public WorldGenSettings create(RegistryAccess.RegistryHolder registryHolder, long l, boolean bl, boolean bl2) {
        WritableRegistry<Biome> writableRegistry = registryHolder.registryOrThrow(Registry.BIOME_REGISTRY);
        WritableRegistry<DimensionType> writableRegistry2 = registryHolder.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
        WritableRegistry<NoiseGeneratorSettings> writableRegistry3 = registryHolder.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY);
        return new WorldGenSettings(l, bl, bl2, WorldGenSettings.withOverworld(writableRegistry2, DimensionType.defaultDimensions(writableRegistry2, writableRegistry, writableRegistry3, l), this.generator(writableRegistry, writableRegistry3, l)));
    }

    protected abstract ChunkGenerator generator(Registry<Biome> var1, Registry<NoiseGeneratorSettings> var2, long var3);

    public static interface PresetEditor {
        public Screen createEditScreen(CreateWorldScreen var1, WorldGenSettings var2);
    }

}

