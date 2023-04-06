/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.Products
 *  com.mojang.datafixers.Products$P4
 *  com.mojang.datafixers.Products$P5
 *  com.mojang.datafixers.Products$P7
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Function4
 *  com.mojang.datafixers.util.Function5
 *  com.mojang.datafixers.util.Function7
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.PrimitiveCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 *  it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Function4;
import com.mojang.datafixers.util.Function5;
import com.mojang.datafixers.util.Function7;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.AmbientAdditionsSettings;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Biome {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Codec<Biome> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group((App)ClimateSettings.CODEC.forGetter(biome -> biome.climateSettings), (App)BiomeCategory.CODEC.fieldOf("category").forGetter(biome -> biome.biomeCategory), (App)Codec.FLOAT.fieldOf("depth").forGetter(biome -> Float.valueOf(biome.depth)), (App)Codec.FLOAT.fieldOf("scale").forGetter(biome -> Float.valueOf(biome.scale)), (App)BiomeSpecialEffects.CODEC.fieldOf("effects").forGetter(biome -> biome.specialEffects), (App)BiomeGenerationSettings.CODEC.forGetter(biome -> biome.generationSettings), (App)MobSpawnSettings.CODEC.forGetter(biome -> biome.mobSettings)).apply((Applicative)instance, (arg_0, arg_1, arg_2, arg_3, arg_4, arg_5, arg_6) -> Biome.new(arg_0, arg_1, arg_2, arg_3, arg_4, arg_5, arg_6)));
    public static final Codec<Biome> NETWORK_CODEC = RecordCodecBuilder.create(instance -> instance.group((App)ClimateSettings.CODEC.forGetter(biome -> biome.climateSettings), (App)BiomeCategory.CODEC.fieldOf("category").forGetter(biome -> biome.biomeCategory), (App)Codec.FLOAT.fieldOf("depth").forGetter(biome -> Float.valueOf(biome.depth)), (App)Codec.FLOAT.fieldOf("scale").forGetter(biome -> Float.valueOf(biome.scale)), (App)BiomeSpecialEffects.CODEC.fieldOf("effects").forGetter(biome -> biome.specialEffects)).apply((Applicative)instance, (climateSettings, biomeCategory, f, f2, biomeSpecialEffects) -> new Biome((ClimateSettings)climateSettings, (BiomeCategory)biomeCategory, f.floatValue(), f2.floatValue(), (BiomeSpecialEffects)biomeSpecialEffects, BiomeGenerationSettings.EMPTY, MobSpawnSettings.EMPTY)));
    public static final Codec<Supplier<Biome>> CODEC = RegistryFileCodec.create(Registry.BIOME_REGISTRY, DIRECT_CODEC);
    public static final Codec<List<Supplier<Biome>>> LIST_CODEC = RegistryFileCodec.homogeneousList(Registry.BIOME_REGISTRY, DIRECT_CODEC);
    private final Map<Integer, List<StructureFeature<?>>> structuresByStep = Registry.STRUCTURE_FEATURE.stream().collect(Collectors.groupingBy(structureFeature -> structureFeature.step().ordinal()));
    private static final PerlinSimplexNoise TEMPERATURE_NOISE = new PerlinSimplexNoise(new WorldgenRandom(1234L), (List<Integer>)ImmutableList.of((Object)0));
    private static final PerlinSimplexNoise FROZEN_TEMPERATURE_NOISE = new PerlinSimplexNoise(new WorldgenRandom(3456L), (List<Integer>)ImmutableList.of((Object)-2, (Object)-1, (Object)0));
    public static final PerlinSimplexNoise BIOME_INFO_NOISE = new PerlinSimplexNoise(new WorldgenRandom(2345L), (List<Integer>)ImmutableList.of((Object)0));
    private final ClimateSettings climateSettings;
    private final BiomeGenerationSettings generationSettings;
    private final MobSpawnSettings mobSettings;
    private final float depth;
    private final float scale;
    private final BiomeCategory biomeCategory;
    private final BiomeSpecialEffects specialEffects;
    private final ThreadLocal<Long2FloatLinkedOpenHashMap> temperatureCache = ThreadLocal.withInitial(() -> Util.make(() -> {
        Long2FloatLinkedOpenHashMap long2FloatLinkedOpenHashMap = new Long2FloatLinkedOpenHashMap(1024, 0.25f){

            protected void rehash(int n) {
            }
        };
        long2FloatLinkedOpenHashMap.defaultReturnValue(Float.NaN);
        return long2FloatLinkedOpenHashMap;
    }));

    private Biome(ClimateSettings climateSettings, BiomeCategory biomeCategory, float f, float f2, BiomeSpecialEffects biomeSpecialEffects, BiomeGenerationSettings biomeGenerationSettings, MobSpawnSettings mobSpawnSettings) {
        this.climateSettings = climateSettings;
        this.generationSettings = biomeGenerationSettings;
        this.mobSettings = mobSpawnSettings;
        this.biomeCategory = biomeCategory;
        this.depth = f;
        this.scale = f2;
        this.specialEffects = biomeSpecialEffects;
    }

    public int getSkyColor() {
        return this.specialEffects.getSkyColor();
    }

    public MobSpawnSettings getMobSettings() {
        return this.mobSettings;
    }

    public Precipitation getPrecipitation() {
        return this.climateSettings.precipitation;
    }

    public boolean isHumid() {
        return this.getDownfall() > 0.85f;
    }

    private float getHeightAdjustedTemperature(BlockPos blockPos) {
        float f = this.climateSettings.temperatureModifier.modifyTemperature(blockPos, this.getBaseTemperature());
        if (blockPos.getY() > 64) {
            float f2 = (float)(TEMPERATURE_NOISE.getValue((float)blockPos.getX() / 8.0f, (float)blockPos.getZ() / 8.0f, false) * 4.0);
            return f - (f2 + (float)blockPos.getY() - 64.0f) * 0.05f / 30.0f;
        }
        return f;
    }

    public final float getTemperature(BlockPos blockPos) {
        long l = blockPos.asLong();
        Long2FloatLinkedOpenHashMap long2FloatLinkedOpenHashMap = this.temperatureCache.get();
        float f = long2FloatLinkedOpenHashMap.get(l);
        if (!Float.isNaN(f)) {
            return f;
        }
        float f2 = this.getHeightAdjustedTemperature(blockPos);
        if (long2FloatLinkedOpenHashMap.size() == 1024) {
            long2FloatLinkedOpenHashMap.removeFirstFloat();
        }
        long2FloatLinkedOpenHashMap.put(l, f2);
        return f2;
    }

    public boolean shouldFreeze(LevelReader levelReader, BlockPos blockPos) {
        return this.shouldFreeze(levelReader, blockPos, true);
    }

    public boolean shouldFreeze(LevelReader levelReader, BlockPos blockPos, boolean bl) {
        if (this.getTemperature(blockPos) >= 0.15f) {
            return false;
        }
        if (blockPos.getY() >= 0 && blockPos.getY() < 256 && levelReader.getBrightness(LightLayer.BLOCK, blockPos) < 10) {
            BlockState blockState = levelReader.getBlockState(blockPos);
            FluidState fluidState = levelReader.getFluidState(blockPos);
            if (fluidState.getType() == Fluids.WATER && blockState.getBlock() instanceof LiquidBlock) {
                boolean bl2;
                if (!bl) {
                    return true;
                }
                boolean bl3 = bl2 = levelReader.isWaterAt(blockPos.west()) && levelReader.isWaterAt(blockPos.east()) && levelReader.isWaterAt(blockPos.north()) && levelReader.isWaterAt(blockPos.south());
                if (!bl2) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean shouldSnow(LevelReader levelReader, BlockPos blockPos) {
        BlockState blockState;
        if (this.getTemperature(blockPos) >= 0.15f) {
            return false;
        }
        return blockPos.getY() >= 0 && blockPos.getY() < 256 && levelReader.getBrightness(LightLayer.BLOCK, blockPos) < 10 && (blockState = levelReader.getBlockState(blockPos)).isAir() && Blocks.SNOW.defaultBlockState().canSurvive(levelReader, blockPos);
    }

    public BiomeGenerationSettings getGenerationSettings() {
        return this.generationSettings;
    }

    public void generate(StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, WorldGenRegion worldGenRegion, long l, WorldgenRandom worldgenRandom, BlockPos blockPos) {
        List<List<Supplier<ConfiguredFeature<?, ?>>>> list = this.generationSettings.features();
        int n = GenerationStep.Decoration.values().length;
        for (int i = 0; i < n; ++i) {
            Object object;
            int n2 = 0;
            if (structureFeatureManager.shouldGenerateFeatures()) {
                List list2 = this.structuresByStep.getOrDefault(i, Collections.emptyList());
                Iterator iterator = list2.iterator();
                while (iterator.hasNext()) {
                    object = (StructureFeature)iterator.next();
                    worldgenRandom.setFeatureSeed(l, n2, i);
                    int n3 = blockPos.getX() >> 4;
                    int n4 = blockPos.getZ() >> 4;
                    int n5 = n3 << 4;
                    int n6 = n4 << 4;
                    try {
                        structureFeatureManager.startsForFeature(SectionPos.of(blockPos), (StructureFeature<?>)object).forEach(structureStart -> structureStart.placeInChunk(worldGenRegion, structureFeatureManager, chunkGenerator, worldgenRandom, new BoundingBox(n5, n6, n5 + 15, n6 + 15), new ChunkPos(n3, n4)));
                    }
                    catch (Exception exception) {
                        CrashReport crashReport = CrashReport.forThrowable(exception, "Feature placement");
                        crashReport.addCategory("Feature").setDetail("Id", Registry.STRUCTURE_FEATURE.getKey((StructureFeature<?>)object)).setDetail("Description", () -> Biome.lambda$generate$19((StructureFeature)object));
                        throw new ReportedException(crashReport);
                    }
                    ++n2;
                }
            }
            if (list.size() <= i) continue;
            for (Supplier supplier : list.get(i)) {
                object = (ConfiguredFeature)supplier.get();
                worldgenRandom.setFeatureSeed(l, n2, i);
                try {
                    ((ConfiguredFeature)object).place(worldGenRegion, chunkGenerator, worldgenRandom, blockPos);
                }
                catch (Exception exception) {
                    CrashReport crashReport = CrashReport.forThrowable(exception, "Feature placement");
                    crashReport.addCategory("Feature").setDetail("Id", Registry.FEATURE.getKey((Feature<?>)((ConfiguredFeature)object).feature)).setDetail("Config", ((ConfiguredFeature)object).config).setDetail("Description", () -> Biome.lambda$generate$20((ConfiguredFeature)object));
                    throw new ReportedException(crashReport);
                }
                ++n2;
            }
        }
    }

    public int getFogColor() {
        return this.specialEffects.getFogColor();
    }

    public int getGrassColor(double d, double d2) {
        int n = this.specialEffects.getGrassColorOverride().orElseGet(this::getGrassColorFromTexture);
        return this.specialEffects.getGrassColorModifier().modifyColor(d, d2, n);
    }

    private int getGrassColorFromTexture() {
        double d = Mth.clamp(this.climateSettings.temperature, 0.0f, 1.0f);
        double d2 = Mth.clamp(this.climateSettings.downfall, 0.0f, 1.0f);
        return GrassColor.get(d, d2);
    }

    public int getFoliageColor() {
        return this.specialEffects.getFoliageColorOverride().orElseGet(this::getFoliageColorFromTexture);
    }

    private int getFoliageColorFromTexture() {
        double d = Mth.clamp(this.climateSettings.temperature, 0.0f, 1.0f);
        double d2 = Mth.clamp(this.climateSettings.downfall, 0.0f, 1.0f);
        return FoliageColor.get(d, d2);
    }

    public void buildSurfaceAt(Random random, ChunkAccess chunkAccess, int n, int n2, int n3, double d, BlockState blockState, BlockState blockState2, int n4, long l) {
        ConfiguredSurfaceBuilder<?> configuredSurfaceBuilder = this.generationSettings.getSurfaceBuilder().get();
        configuredSurfaceBuilder.initNoise(l);
        configuredSurfaceBuilder.apply(random, chunkAccess, this, n, n2, n3, d, blockState, blockState2, n4, l);
    }

    public final float getDepth() {
        return this.depth;
    }

    public final float getDownfall() {
        return this.climateSettings.downfall;
    }

    public final float getScale() {
        return this.scale;
    }

    public final float getBaseTemperature() {
        return this.climateSettings.temperature;
    }

    public BiomeSpecialEffects getSpecialEffects() {
        return this.specialEffects;
    }

    public final int getWaterColor() {
        return this.specialEffects.getWaterColor();
    }

    public final int getWaterFogColor() {
        return this.specialEffects.getWaterFogColor();
    }

    public Optional<AmbientParticleSettings> getAmbientParticle() {
        return this.specialEffects.getAmbientParticleSettings();
    }

    public Optional<SoundEvent> getAmbientLoop() {
        return this.specialEffects.getAmbientLoopSoundEvent();
    }

    public Optional<AmbientMoodSettings> getAmbientMood() {
        return this.specialEffects.getAmbientMoodSettings();
    }

    public Optional<AmbientAdditionsSettings> getAmbientAdditions() {
        return this.specialEffects.getAmbientAdditionsSettings();
    }

    public Optional<Music> getBackgroundMusic() {
        return this.specialEffects.getBackgroundMusic();
    }

    public final BiomeCategory getBiomeCategory() {
        return this.biomeCategory;
    }

    public String toString() {
        ResourceLocation resourceLocation = BuiltinRegistries.BIOME.getKey(this);
        return resourceLocation == null ? super.toString() : resourceLocation.toString();
    }

    private static /* synthetic */ String lambda$generate$20(ConfiguredFeature configuredFeature) throws Exception {
        return configuredFeature.feature.toString();
    }

    private static /* synthetic */ String lambda$generate$19(StructureFeature structureFeature) throws Exception {
        return structureFeature.toString();
    }

    static class ClimateSettings {
        public static final MapCodec<ClimateSettings> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Precipitation.CODEC.fieldOf("precipitation").forGetter(climateSettings -> climateSettings.precipitation), (App)Codec.FLOAT.fieldOf("temperature").forGetter(climateSettings -> Float.valueOf(climateSettings.temperature)), (App)TemperatureModifier.CODEC.optionalFieldOf("temperature_modifier", (Object)TemperatureModifier.NONE).forGetter(climateSettings -> climateSettings.temperatureModifier), (App)Codec.FLOAT.fieldOf("downfall").forGetter(climateSettings -> Float.valueOf(climateSettings.downfall))).apply((Applicative)instance, (arg_0, arg_1, arg_2, arg_3) -> ClimateSettings.new(arg_0, arg_1, arg_2, arg_3)));
        private final Precipitation precipitation;
        private final float temperature;
        private final TemperatureModifier temperatureModifier;
        private final float downfall;

        private ClimateSettings(Precipitation precipitation, float f, TemperatureModifier temperatureModifier, float f2) {
            this.precipitation = precipitation;
            this.temperature = f;
            this.temperatureModifier = temperatureModifier;
            this.downfall = f2;
        }
    }

    public static class ClimateParameters {
        public static final Codec<ClimateParameters> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.floatRange((float)-2.0f, (float)2.0f).fieldOf("temperature").forGetter(climateParameters -> Float.valueOf(climateParameters.temperature)), (App)Codec.floatRange((float)-2.0f, (float)2.0f).fieldOf("humidity").forGetter(climateParameters -> Float.valueOf(climateParameters.humidity)), (App)Codec.floatRange((float)-2.0f, (float)2.0f).fieldOf("altitude").forGetter(climateParameters -> Float.valueOf(climateParameters.altitude)), (App)Codec.floatRange((float)-2.0f, (float)2.0f).fieldOf("weirdness").forGetter(climateParameters -> Float.valueOf(climateParameters.weirdness)), (App)Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("offset").forGetter(climateParameters -> Float.valueOf(climateParameters.offset))).apply((Applicative)instance, (arg_0, arg_1, arg_2, arg_3, arg_4) -> ClimateParameters.new(arg_0, arg_1, arg_2, arg_3, arg_4)));
        private final float temperature;
        private final float humidity;
        private final float altitude;
        private final float weirdness;
        private final float offset;

        public ClimateParameters(float f, float f2, float f3, float f4, float f5) {
            this.temperature = f;
            this.humidity = f2;
            this.altitude = f3;
            this.weirdness = f4;
            this.offset = f5;
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            ClimateParameters climateParameters = (ClimateParameters)object;
            if (Float.compare(climateParameters.temperature, this.temperature) != 0) {
                return false;
            }
            if (Float.compare(climateParameters.humidity, this.humidity) != 0) {
                return false;
            }
            if (Float.compare(climateParameters.altitude, this.altitude) != 0) {
                return false;
            }
            return Float.compare(climateParameters.weirdness, this.weirdness) == 0;
        }

        public int hashCode() {
            int n = this.temperature != 0.0f ? Float.floatToIntBits(this.temperature) : 0;
            n = 31 * n + (this.humidity != 0.0f ? Float.floatToIntBits(this.humidity) : 0);
            n = 31 * n + (this.altitude != 0.0f ? Float.floatToIntBits(this.altitude) : 0);
            n = 31 * n + (this.weirdness != 0.0f ? Float.floatToIntBits(this.weirdness) : 0);
            return n;
        }

        public float fitness(ClimateParameters climateParameters) {
            return (this.temperature - climateParameters.temperature) * (this.temperature - climateParameters.temperature) + (this.humidity - climateParameters.humidity) * (this.humidity - climateParameters.humidity) + (this.altitude - climateParameters.altitude) * (this.altitude - climateParameters.altitude) + (this.weirdness - climateParameters.weirdness) * (this.weirdness - climateParameters.weirdness) + (this.offset - climateParameters.offset) * (this.offset - climateParameters.offset);
        }
    }

    public static class BiomeBuilder {
        @Nullable
        private Precipitation precipitation;
        @Nullable
        private BiomeCategory biomeCategory;
        @Nullable
        private Float depth;
        @Nullable
        private Float scale;
        @Nullable
        private Float temperature;
        private TemperatureModifier temperatureModifier = TemperatureModifier.NONE;
        @Nullable
        private Float downfall;
        @Nullable
        private BiomeSpecialEffects specialEffects;
        @Nullable
        private MobSpawnSettings mobSpawnSettings;
        @Nullable
        private BiomeGenerationSettings generationSettings;

        public BiomeBuilder precipitation(Precipitation precipitation) {
            this.precipitation = precipitation;
            return this;
        }

        public BiomeBuilder biomeCategory(BiomeCategory biomeCategory) {
            this.biomeCategory = biomeCategory;
            return this;
        }

        public BiomeBuilder depth(float f) {
            this.depth = Float.valueOf(f);
            return this;
        }

        public BiomeBuilder scale(float f) {
            this.scale = Float.valueOf(f);
            return this;
        }

        public BiomeBuilder temperature(float f) {
            this.temperature = Float.valueOf(f);
            return this;
        }

        public BiomeBuilder downfall(float f) {
            this.downfall = Float.valueOf(f);
            return this;
        }

        public BiomeBuilder specialEffects(BiomeSpecialEffects biomeSpecialEffects) {
            this.specialEffects = biomeSpecialEffects;
            return this;
        }

        public BiomeBuilder mobSpawnSettings(MobSpawnSettings mobSpawnSettings) {
            this.mobSpawnSettings = mobSpawnSettings;
            return this;
        }

        public BiomeBuilder generationSettings(BiomeGenerationSettings biomeGenerationSettings) {
            this.generationSettings = biomeGenerationSettings;
            return this;
        }

        public BiomeBuilder temperatureAdjustment(TemperatureModifier temperatureModifier) {
            this.temperatureModifier = temperatureModifier;
            return this;
        }

        public Biome build() {
            if (this.precipitation == null || this.biomeCategory == null || this.depth == null || this.scale == null || this.temperature == null || this.downfall == null || this.specialEffects == null || this.mobSpawnSettings == null || this.generationSettings == null) {
                throw new IllegalStateException("You are missing parameters to build a proper biome\n" + this);
            }
            return new Biome(new ClimateSettings(this.precipitation, this.temperature.floatValue(), this.temperatureModifier, this.downfall.floatValue()), this.biomeCategory, this.depth.floatValue(), this.scale.floatValue(), this.specialEffects, this.generationSettings, this.mobSpawnSettings);
        }

        public String toString() {
            return "BiomeBuilder{\nprecipitation=" + this.precipitation + ",\nbiomeCategory=" + this.biomeCategory + ",\ndepth=" + this.depth + ",\nscale=" + this.scale + ",\ntemperature=" + this.temperature + ",\ntemperatureModifier=" + this.temperatureModifier + ",\ndownfall=" + this.downfall + ",\nspecialEffects=" + this.specialEffects + ",\nmobSpawnSettings=" + this.mobSpawnSettings + ",\ngenerationSettings=" + this.generationSettings + ",\n" + '}';
        }
    }

    public static enum TemperatureModifier implements StringRepresentable
    {
        NONE("none"){

            @Override
            public float modifyTemperature(BlockPos blockPos, float f) {
                return f;
            }
        }
        ,
        FROZEN("frozen"){

            @Override
            public float modifyTemperature(BlockPos blockPos, float f) {
                double d;
                double d2;
                double d3 = FROZEN_TEMPERATURE_NOISE.getValue((double)blockPos.getX() * 0.05, (double)blockPos.getZ() * 0.05, false) * 7.0;
                double d4 = d3 + (d2 = BIOME_INFO_NOISE.getValue((double)blockPos.getX() * 0.2, (double)blockPos.getZ() * 0.2, false));
                if (d4 < 0.3 && (d = BIOME_INFO_NOISE.getValue((double)blockPos.getX() * 0.09, (double)blockPos.getZ() * 0.09, false)) < 0.8) {
                    return 0.2f;
                }
                return f;
            }
        };
        
        private final String name;
        public static final Codec<TemperatureModifier> CODEC;
        private static final Map<String, TemperatureModifier> BY_NAME;

        public abstract float modifyTemperature(BlockPos var1, float var2);

        private TemperatureModifier(String string2) {
            this.name = string2;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public static TemperatureModifier byName(String string) {
            return BY_NAME.get(string);
        }

        static {
            CODEC = StringRepresentable.fromEnum(TemperatureModifier::values, TemperatureModifier::byName);
            BY_NAME = Arrays.stream(TemperatureModifier.values()).collect(Collectors.toMap(TemperatureModifier::getName, temperatureModifier -> temperatureModifier));
        }

    }

    public static enum Precipitation implements StringRepresentable
    {
        NONE("none"),
        RAIN("rain"),
        SNOW("snow");
        
        public static final Codec<Precipitation> CODEC;
        private static final Map<String, Precipitation> BY_NAME;
        private final String name;

        private Precipitation(String string2) {
            this.name = string2;
        }

        public String getName() {
            return this.name;
        }

        public static Precipitation byName(String string) {
            return BY_NAME.get(string);
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        static {
            CODEC = StringRepresentable.fromEnum(Precipitation::values, Precipitation::byName);
            BY_NAME = Arrays.stream(Precipitation.values()).collect(Collectors.toMap(Precipitation::getName, precipitation -> precipitation));
        }
    }

    public static enum BiomeCategory implements StringRepresentable
    {
        NONE("none"),
        TAIGA("taiga"),
        EXTREME_HILLS("extreme_hills"),
        JUNGLE("jungle"),
        MESA("mesa"),
        PLAINS("plains"),
        SAVANNA("savanna"),
        ICY("icy"),
        THEEND("the_end"),
        BEACH("beach"),
        FOREST("forest"),
        OCEAN("ocean"),
        DESERT("desert"),
        RIVER("river"),
        SWAMP("swamp"),
        MUSHROOM("mushroom"),
        NETHER("nether");
        
        public static final Codec<BiomeCategory> CODEC;
        private static final Map<String, BiomeCategory> BY_NAME;
        private final String name;

        private BiomeCategory(String string2) {
            this.name = string2;
        }

        public String getName() {
            return this.name;
        }

        public static BiomeCategory byName(String string) {
            return BY_NAME.get(string);
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        static {
            CODEC = StringRepresentable.fromEnum(BiomeCategory::values, BiomeCategory::byName);
            BY_NAME = Arrays.stream(BiomeCategory.values()).collect(Collectors.toMap(BiomeCategory::getName, biomeCategory -> biomeCategory));
        }
    }

}

