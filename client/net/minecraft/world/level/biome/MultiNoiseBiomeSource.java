/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.Products
 *  com.mojang.datafixers.Products$P2
 *  com.mojang.datafixers.Products$P3
 *  com.mojang.datafixers.Products$P6
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.datafixers.util.Function3
 *  com.mojang.datafixers.util.Function6
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.PrimitiveCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 *  it.unimi.dsi.fastutil.doubles.DoubleArrayList
 *  it.unimi.dsi.fastutil.doubles.DoubleList
 */
package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function6;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class MultiNoiseBiomeSource
extends BiomeSource {
    private static final NoiseParameters DEFAULT_NOISE_PARAMETERS = new NoiseParameters(-7, (List<Double>)ImmutableList.of((Object)1.0, (Object)1.0));
    public static final MapCodec<MultiNoiseBiomeSource> DIRECT_CODEC = RecordCodecBuilder.mapCodec(instance2 -> instance2.group((App)Codec.LONG.fieldOf("seed").forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.seed), (App)RecordCodecBuilder.create(instance -> instance.group((App)Biome.ClimateParameters.CODEC.fieldOf("parameters").forGetter(Pair::getFirst), (App)Biome.CODEC.fieldOf("biome").forGetter(Pair::getSecond)).apply((Applicative)instance, (arg_0, arg_1) -> Pair.of(arg_0, arg_1))).listOf().fieldOf("biomes").forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.parameters), (App)NoiseParameters.CODEC.fieldOf("temperature_noise").forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.temperatureParams), (App)NoiseParameters.CODEC.fieldOf("humidity_noise").forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.humidityParams), (App)NoiseParameters.CODEC.fieldOf("altitude_noise").forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.altitudeParams), (App)NoiseParameters.CODEC.fieldOf("weirdness_noise").forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.weirdnessParams)).apply((Applicative)instance2, (arg_0, arg_1, arg_2, arg_3, arg_4, arg_5) -> MultiNoiseBiomeSource.new(arg_0, arg_1, arg_2, arg_3, arg_4, arg_5)));
    public static final Codec<MultiNoiseBiomeSource> CODEC = Codec.mapEither(PresetInstance.CODEC, DIRECT_CODEC).xmap(either -> (MultiNoiseBiomeSource)either.map(PresetInstance::biomeSource, Function.identity()), multiNoiseBiomeSource -> multiNoiseBiomeSource.preset().map(Either::left).orElseGet(() -> Either.right((Object)multiNoiseBiomeSource))).codec();
    private final NoiseParameters temperatureParams;
    private final NoiseParameters humidityParams;
    private final NoiseParameters altitudeParams;
    private final NoiseParameters weirdnessParams;
    private final NormalNoise temperatureNoise;
    private final NormalNoise humidityNoise;
    private final NormalNoise altitudeNoise;
    private final NormalNoise weirdnessNoise;
    private final List<Pair<Biome.ClimateParameters, Supplier<Biome>>> parameters;
    private final boolean useY;
    private final long seed;
    private final Optional<Pair<Registry<Biome>, Preset>> preset;

    private MultiNoiseBiomeSource(long l, List<Pair<Biome.ClimateParameters, Supplier<Biome>>> list, Optional<Pair<Registry<Biome>, Preset>> optional) {
        this(l, list, DEFAULT_NOISE_PARAMETERS, DEFAULT_NOISE_PARAMETERS, DEFAULT_NOISE_PARAMETERS, DEFAULT_NOISE_PARAMETERS, optional);
    }

    private MultiNoiseBiomeSource(long l, List<Pair<Biome.ClimateParameters, Supplier<Biome>>> list, NoiseParameters noiseParameters, NoiseParameters noiseParameters2, NoiseParameters noiseParameters3, NoiseParameters noiseParameters4) {
        this(l, list, noiseParameters, noiseParameters2, noiseParameters3, noiseParameters4, Optional.empty());
    }

    private MultiNoiseBiomeSource(long l, List<Pair<Biome.ClimateParameters, Supplier<Biome>>> list, NoiseParameters noiseParameters, NoiseParameters noiseParameters2, NoiseParameters noiseParameters3, NoiseParameters noiseParameters4, Optional<Pair<Registry<Biome>, Preset>> optional) {
        super(list.stream().map(Pair::getSecond));
        this.seed = l;
        this.preset = optional;
        this.temperatureParams = noiseParameters;
        this.humidityParams = noiseParameters2;
        this.altitudeParams = noiseParameters3;
        this.weirdnessParams = noiseParameters4;
        this.temperatureNoise = NormalNoise.create(new WorldgenRandom(l), noiseParameters.firstOctave(), noiseParameters.amplitudes());
        this.humidityNoise = NormalNoise.create(new WorldgenRandom(l + 1L), noiseParameters2.firstOctave(), noiseParameters2.amplitudes());
        this.altitudeNoise = NormalNoise.create(new WorldgenRandom(l + 2L), noiseParameters3.firstOctave(), noiseParameters3.amplitudes());
        this.weirdnessNoise = NormalNoise.create(new WorldgenRandom(l + 3L), noiseParameters4.firstOctave(), noiseParameters4.amplitudes());
        this.parameters = list;
        this.useY = false;
    }

    @Override
    protected Codec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Override
    public BiomeSource withSeed(long l) {
        return new MultiNoiseBiomeSource(l, this.parameters, this.temperatureParams, this.humidityParams, this.altitudeParams, this.weirdnessParams, this.preset);
    }

    private Optional<PresetInstance> preset() {
        return this.preset.map(pair -> new PresetInstance((Preset)pair.getSecond(), (Registry)pair.getFirst(), this.seed));
    }

    @Override
    public Biome getNoiseBiome(int n, int n2, int n3) {
        int n4 = this.useY ? n2 : 0;
        Biome.ClimateParameters climateParameters = new Biome.ClimateParameters((float)this.temperatureNoise.getValue(n, n4, n3), (float)this.humidityNoise.getValue(n, n4, n3), (float)this.altitudeNoise.getValue(n, n4, n3), (float)this.weirdnessNoise.getValue(n, n4, n3), 0.0f);
        return this.parameters.stream().min(Comparator.comparing(pair -> Float.valueOf(((Biome.ClimateParameters)pair.getFirst()).fitness(climateParameters)))).map(Pair::getSecond).map(Supplier::get).orElse(net.minecraft.data.worldgen.biome.Biomes.THE_VOID);
    }

    public boolean stable(long l) {
        return this.seed == l && this.preset.isPresent() && Objects.equals(this.preset.get().getSecond(), Preset.NETHER);
    }

    public static class Preset {
        private static final Map<ResourceLocation, Preset> BY_NAME = Maps.newHashMap();
        public static final Preset NETHER = new Preset(new ResourceLocation("nether"), (Function3<Preset, Registry<Biome>, Long, MultiNoiseBiomeSource>)((Function3)(preset, registry, l) -> new MultiNoiseBiomeSource((long)l, (List)ImmutableList.of((Object)Pair.of((Object)new Biome.ClimateParameters(0.0f, 0.0f, 0.0f, 0.0f, 0.0f), () -> registry.getOrThrow(Biomes.NETHER_WASTES)), (Object)Pair.of((Object)new Biome.ClimateParameters(0.0f, -0.5f, 0.0f, 0.0f, 0.0f), () -> registry.getOrThrow(Biomes.SOUL_SAND_VALLEY)), (Object)Pair.of((Object)new Biome.ClimateParameters(0.4f, 0.0f, 0.0f, 0.0f, 0.0f), () -> registry.getOrThrow(Biomes.CRIMSON_FOREST)), (Object)Pair.of((Object)new Biome.ClimateParameters(0.0f, 0.5f, 0.0f, 0.0f, 0.375f), () -> registry.getOrThrow(Biomes.WARPED_FOREST)), (Object)Pair.of((Object)new Biome.ClimateParameters(-0.5f, 0.0f, 0.0f, 0.0f, 0.175f), () -> registry.getOrThrow(Biomes.BASALT_DELTAS))), Optional.of(Pair.of((Object)registry, (Object)preset)))));
        private final ResourceLocation name;
        private final Function3<Preset, Registry<Biome>, Long, MultiNoiseBiomeSource> biomeSource;

        public Preset(ResourceLocation resourceLocation, Function3<Preset, Registry<Biome>, Long, MultiNoiseBiomeSource> function3) {
            this.name = resourceLocation;
            this.biomeSource = function3;
            BY_NAME.put(resourceLocation, this);
        }

        public MultiNoiseBiomeSource biomeSource(Registry<Biome> registry, long l) {
            return (MultiNoiseBiomeSource)this.biomeSource.apply((Object)this, registry, (Object)l);
        }
    }

    static final class PresetInstance {
        public static final MapCodec<PresetInstance> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)ResourceLocation.CODEC.flatXmap(resourceLocation -> Optional.ofNullable(Preset.BY_NAME.get(resourceLocation)).map(DataResult::success).orElseGet(() -> DataResult.error((String)("Unknown preset: " + resourceLocation))), preset -> DataResult.success((Object)preset.name)).fieldOf("preset").stable().forGetter(PresetInstance::preset), (App)RegistryLookupCodec.create(Registry.BIOME_REGISTRY).forGetter(PresetInstance::biomes), (App)Codec.LONG.fieldOf("seed").stable().forGetter(PresetInstance::seed)).apply((Applicative)instance, instance.stable((Object)((Function3)(arg_0, arg_1, arg_2) -> PresetInstance.new(arg_0, arg_1, arg_2)))));
        private final Preset preset;
        private final Registry<Biome> biomes;
        private final long seed;

        private PresetInstance(Preset preset, Registry<Biome> registry, long l) {
            this.preset = preset;
            this.biomes = registry;
            this.seed = l;
        }

        public Preset preset() {
            return this.preset;
        }

        public Registry<Biome> biomes() {
            return this.biomes;
        }

        public long seed() {
            return this.seed;
        }

        public MultiNoiseBiomeSource biomeSource() {
            return this.preset.biomeSource(this.biomes, this.seed);
        }
    }

    static class NoiseParameters {
        private final int firstOctave;
        private final DoubleList amplitudes;
        public static final Codec<NoiseParameters> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("firstOctave").forGetter(NoiseParameters::firstOctave), (App)Codec.DOUBLE.listOf().fieldOf("amplitudes").forGetter(NoiseParameters::amplitudes)).apply((Applicative)instance, (arg_0, arg_1) -> NoiseParameters.new(arg_0, arg_1)));

        public NoiseParameters(int n, List<Double> list) {
            this.firstOctave = n;
            this.amplitudes = new DoubleArrayList(list);
        }

        public int firstOctave() {
            return this.firstOctave;
        }

        public DoubleList amplitudes() {
            return this.amplitudes;
        }
    }

}

