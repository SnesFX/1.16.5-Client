/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.Products
 *  com.mojang.datafixers.Products$P8
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Function8
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.PrimitiveCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 */
package net.minecraft.world.level.levelgen;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Function8;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.NoiseSamplingSettings;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.NoiseSlideSettings;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RuinedPortalConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.StrongholdConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;

public final class NoiseGeneratorSettings {
    public static final Codec<NoiseGeneratorSettings> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group((App)StructureSettings.CODEC.fieldOf("structures").forGetter(NoiseGeneratorSettings::structureSettings), (App)NoiseSettings.CODEC.fieldOf("noise").forGetter(NoiseGeneratorSettings::noiseSettings), (App)BlockState.CODEC.fieldOf("default_block").forGetter(NoiseGeneratorSettings::getDefaultBlock), (App)BlockState.CODEC.fieldOf("default_fluid").forGetter(NoiseGeneratorSettings::getDefaultFluid), (App)Codec.intRange((int)-20, (int)276).fieldOf("bedrock_roof_position").forGetter(NoiseGeneratorSettings::getBedrockRoofPosition), (App)Codec.intRange((int)-20, (int)276).fieldOf("bedrock_floor_position").forGetter(NoiseGeneratorSettings::getBedrockFloorPosition), (App)Codec.intRange((int)0, (int)255).fieldOf("sea_level").forGetter(NoiseGeneratorSettings::seaLevel), (App)Codec.BOOL.fieldOf("disable_mob_generation").forGetter(NoiseGeneratorSettings::disableMobGeneration)).apply((Applicative)instance, (arg_0, arg_1, arg_2, arg_3, arg_4, arg_5, arg_6, arg_7) -> NoiseGeneratorSettings.new(arg_0, arg_1, arg_2, arg_3, arg_4, arg_5, arg_6, arg_7)));
    public static final Codec<Supplier<NoiseGeneratorSettings>> CODEC = RegistryFileCodec.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, DIRECT_CODEC);
    private final StructureSettings structureSettings;
    private final NoiseSettings noiseSettings;
    private final BlockState defaultBlock;
    private final BlockState defaultFluid;
    private final int bedrockRoofPosition;
    private final int bedrockFloorPosition;
    private final int seaLevel;
    private final boolean disableMobGeneration;
    public static final ResourceKey<NoiseGeneratorSettings> OVERWORLD = ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation("overworld"));
    public static final ResourceKey<NoiseGeneratorSettings> AMPLIFIED = ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation("amplified"));
    public static final ResourceKey<NoiseGeneratorSettings> NETHER = ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation("nether"));
    public static final ResourceKey<NoiseGeneratorSettings> END = ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation("end"));
    public static final ResourceKey<NoiseGeneratorSettings> CAVES = ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation("caves"));
    public static final ResourceKey<NoiseGeneratorSettings> FLOATING_ISLANDS = ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation("floating_islands"));
    private static final NoiseGeneratorSettings BUILTIN_OVERWORLD = NoiseGeneratorSettings.register(OVERWORLD, NoiseGeneratorSettings.overworld(new StructureSettings(true), false, OVERWORLD.location()));

    private NoiseGeneratorSettings(StructureSettings structureSettings, NoiseSettings noiseSettings, BlockState blockState, BlockState blockState2, int n, int n2, int n3, boolean bl) {
        this.structureSettings = structureSettings;
        this.noiseSettings = noiseSettings;
        this.defaultBlock = blockState;
        this.defaultFluid = blockState2;
        this.bedrockRoofPosition = n;
        this.bedrockFloorPosition = n2;
        this.seaLevel = n3;
        this.disableMobGeneration = bl;
    }

    public StructureSettings structureSettings() {
        return this.structureSettings;
    }

    public NoiseSettings noiseSettings() {
        return this.noiseSettings;
    }

    public BlockState getDefaultBlock() {
        return this.defaultBlock;
    }

    public BlockState getDefaultFluid() {
        return this.defaultFluid;
    }

    public int getBedrockRoofPosition() {
        return this.bedrockRoofPosition;
    }

    public int getBedrockFloorPosition() {
        return this.bedrockFloorPosition;
    }

    public int seaLevel() {
        return this.seaLevel;
    }

    @Deprecated
    protected boolean disableMobGeneration() {
        return this.disableMobGeneration;
    }

    public boolean stable(ResourceKey<NoiseGeneratorSettings> resourceKey) {
        return Objects.equals(this, BuiltinRegistries.NOISE_GENERATOR_SETTINGS.get(resourceKey));
    }

    private static NoiseGeneratorSettings register(ResourceKey<NoiseGeneratorSettings> resourceKey, NoiseGeneratorSettings noiseGeneratorSettings) {
        BuiltinRegistries.register(BuiltinRegistries.NOISE_GENERATOR_SETTINGS, resourceKey.location(), noiseGeneratorSettings);
        return noiseGeneratorSettings;
    }

    public static NoiseGeneratorSettings bootstrap() {
        return BUILTIN_OVERWORLD;
    }

    private static NoiseGeneratorSettings end(StructureSettings structureSettings, BlockState blockState, BlockState blockState2, ResourceLocation resourceLocation, boolean bl, boolean bl2) {
        return new NoiseGeneratorSettings(structureSettings, new NoiseSettings(128, new NoiseSamplingSettings(2.0, 1.0, 80.0, 160.0), new NoiseSlideSettings(-3000, 64, -46), new NoiseSlideSettings(-30, 7, 1), 2, 1, 0.0, 0.0, true, false, bl2, false), blockState, blockState2, -10, -10, 0, bl);
    }

    private static NoiseGeneratorSettings nether(StructureSettings structureSettings, BlockState blockState, BlockState blockState2, ResourceLocation resourceLocation) {
        HashMap hashMap = Maps.newHashMap(StructureSettings.DEFAULTS);
        hashMap.put(StructureFeature.RUINED_PORTAL, new StructureFeatureConfiguration(25, 10, 34222645));
        return new NoiseGeneratorSettings(new StructureSettings(Optional.ofNullable(structureSettings.stronghold()), hashMap), new NoiseSettings(128, new NoiseSamplingSettings(1.0, 3.0, 80.0, 60.0), new NoiseSlideSettings(120, 3, 0), new NoiseSlideSettings(320, 4, -1), 1, 2, 0.0, 0.019921875, false, false, false, false), blockState, blockState2, 0, 0, 32, false);
    }

    private static NoiseGeneratorSettings overworld(StructureSettings structureSettings, boolean bl, ResourceLocation resourceLocation) {
        double d = 0.9999999814507745;
        return new NoiseGeneratorSettings(structureSettings, new NoiseSettings(256, new NoiseSamplingSettings(0.9999999814507745, 0.9999999814507745, 80.0, 160.0), new NoiseSlideSettings(-10, 3, 0), new NoiseSlideSettings(-30, 0, 0), 1, 2, 1.0, -0.46875, true, true, false, bl), Blocks.STONE.defaultBlockState(), Blocks.WATER.defaultBlockState(), -10, 0, 63, false);
    }

    static {
        NoiseGeneratorSettings.register(AMPLIFIED, NoiseGeneratorSettings.overworld(new StructureSettings(true), true, AMPLIFIED.location()));
        NoiseGeneratorSettings.register(NETHER, NoiseGeneratorSettings.nether(new StructureSettings(false), Blocks.NETHERRACK.defaultBlockState(), Blocks.LAVA.defaultBlockState(), NETHER.location()));
        NoiseGeneratorSettings.register(END, NoiseGeneratorSettings.end(new StructureSettings(false), Blocks.END_STONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), END.location(), true, true));
        NoiseGeneratorSettings.register(CAVES, NoiseGeneratorSettings.nether(new StructureSettings(true), Blocks.STONE.defaultBlockState(), Blocks.WATER.defaultBlockState(), CAVES.location()));
        NoiseGeneratorSettings.register(FLOATING_ISLANDS, NoiseGeneratorSettings.end(new StructureSettings(true), Blocks.STONE.defaultBlockState(), Blocks.WATER.defaultBlockState(), FLOATING_ISLANDS.location(), false, false));
    }
}

