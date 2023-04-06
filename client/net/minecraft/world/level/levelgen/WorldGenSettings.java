/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.ImmutableSet
 *  com.google.gson.JsonObject
 *  com.mojang.datafixers.Products
 *  com.mojang.datafixers.Products$P5
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Function5
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  com.mojang.serialization.Lifecycle
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.PrimitiveCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.level.levelgen;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Function5;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.OverworldBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldGenSettings {
    public static final Codec<WorldGenSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.LONG.fieldOf("seed").stable().forGetter(WorldGenSettings::seed), (App)Codec.BOOL.fieldOf("generate_features").orElse((Object)true).stable().forGetter(WorldGenSettings::generateFeatures), (App)Codec.BOOL.fieldOf("bonus_chest").orElse((Object)false).stable().forGetter(WorldGenSettings::generateBonusChest), (App)MappedRegistry.dataPackCodec(Registry.LEVEL_STEM_REGISTRY, Lifecycle.stable(), LevelStem.CODEC).xmap(LevelStem::sortMap, Function.identity()).fieldOf("dimensions").forGetter(WorldGenSettings::dimensions), (App)Codec.STRING.optionalFieldOf("legacy_custom_options").stable().forGetter(worldGenSettings -> worldGenSettings.legacyCustomOptions)).apply((Applicative)instance, instance.stable((Object)((Function5)(arg_0, arg_1, arg_2, arg_3, arg_4) -> WorldGenSettings.new(arg_0, arg_1, arg_2, arg_3, arg_4))))).comapFlatMap(WorldGenSettings::guardExperimental, Function.identity());
    private static final Logger LOGGER = LogManager.getLogger();
    private final long seed;
    private final boolean generateFeatures;
    private final boolean generateBonusChest;
    private final MappedRegistry<LevelStem> dimensions;
    private final Optional<String> legacyCustomOptions;

    private DataResult<WorldGenSettings> guardExperimental() {
        LevelStem levelStem = this.dimensions.get(LevelStem.OVERWORLD);
        if (levelStem == null) {
            return DataResult.error((String)"Overworld settings missing");
        }
        if (this.stable()) {
            return DataResult.success((Object)this, (Lifecycle)Lifecycle.stable());
        }
        return DataResult.success((Object)this);
    }

    private boolean stable() {
        return LevelStem.stable(this.seed, this.dimensions);
    }

    public WorldGenSettings(long l, boolean bl, boolean bl2, MappedRegistry<LevelStem> mappedRegistry) {
        this(l, bl, bl2, mappedRegistry, Optional.empty());
        LevelStem levelStem = mappedRegistry.get(LevelStem.OVERWORLD);
        if (levelStem == null) {
            throw new IllegalStateException("Overworld settings missing");
        }
    }

    private WorldGenSettings(long l, boolean bl, boolean bl2, MappedRegistry<LevelStem> mappedRegistry, Optional<String> optional) {
        this.seed = l;
        this.generateFeatures = bl;
        this.generateBonusChest = bl2;
        this.dimensions = mappedRegistry;
        this.legacyCustomOptions = optional;
    }

    public static WorldGenSettings demoSettings(RegistryAccess registryAccess) {
        WritableRegistry<Biome> writableRegistry = registryAccess.registryOrThrow(Registry.BIOME_REGISTRY);
        int n = "North Carolina".hashCode();
        WritableRegistry<DimensionType> writableRegistry2 = registryAccess.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
        WritableRegistry<NoiseGeneratorSettings> writableRegistry3 = registryAccess.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY);
        return new WorldGenSettings(n, true, true, WorldGenSettings.withOverworld(writableRegistry2, DimensionType.defaultDimensions(writableRegistry2, writableRegistry, writableRegistry3, n), (ChunkGenerator)WorldGenSettings.makeDefaultOverworld(writableRegistry, writableRegistry3, n)));
    }

    public static WorldGenSettings makeDefault(Registry<DimensionType> registry, Registry<Biome> registry2, Registry<NoiseGeneratorSettings> registry3) {
        long l = new Random().nextLong();
        return new WorldGenSettings(l, true, false, WorldGenSettings.withOverworld(registry, DimensionType.defaultDimensions(registry, registry2, registry3, l), (ChunkGenerator)WorldGenSettings.makeDefaultOverworld(registry2, registry3, l)));
    }

    public static NoiseBasedChunkGenerator makeDefaultOverworld(Registry<Biome> registry, Registry<NoiseGeneratorSettings> registry2, long l) {
        return new NoiseBasedChunkGenerator(new OverworldBiomeSource(l, false, false, registry), l, () -> registry2.getOrThrow(NoiseGeneratorSettings.OVERWORLD));
    }

    public long seed() {
        return this.seed;
    }

    public boolean generateFeatures() {
        return this.generateFeatures;
    }

    public boolean generateBonusChest() {
        return this.generateBonusChest;
    }

    public static MappedRegistry<LevelStem> withOverworld(Registry<DimensionType> registry, MappedRegistry<LevelStem> mappedRegistry, ChunkGenerator chunkGenerator) {
        LevelStem levelStem = mappedRegistry.get(LevelStem.OVERWORLD);
        Supplier<DimensionType> supplier = () -> levelStem == null ? registry.getOrThrow(DimensionType.OVERWORLD_LOCATION) : levelStem.type();
        return WorldGenSettings.withOverworld(mappedRegistry, supplier, chunkGenerator);
    }

    public static MappedRegistry<LevelStem> withOverworld(MappedRegistry<LevelStem> mappedRegistry, Supplier<DimensionType> supplier, ChunkGenerator chunkGenerator) {
        MappedRegistry<LevelStem> mappedRegistry2 = new MappedRegistry<LevelStem>(Registry.LEVEL_STEM_REGISTRY, Lifecycle.experimental());
        mappedRegistry2.register(LevelStem.OVERWORLD, new LevelStem(supplier, chunkGenerator), Lifecycle.stable());
        for (Map.Entry<ResourceKey<LevelStem>, LevelStem> entry : mappedRegistry.entrySet()) {
            ResourceKey<LevelStem> resourceKey = entry.getKey();
            if (resourceKey == LevelStem.OVERWORLD) continue;
            mappedRegistry2.register(resourceKey, entry.getValue(), mappedRegistry.lifecycle(entry.getValue()));
        }
        return mappedRegistry2;
    }

    public MappedRegistry<LevelStem> dimensions() {
        return this.dimensions;
    }

    public ChunkGenerator overworld() {
        LevelStem levelStem = this.dimensions.get(LevelStem.OVERWORLD);
        if (levelStem == null) {
            throw new IllegalStateException("Overworld settings missing");
        }
        return levelStem.generator();
    }

    public ImmutableSet<ResourceKey<Level>> levels() {
        return (ImmutableSet)this.dimensions().entrySet().stream().map(entry -> ResourceKey.create(Registry.DIMENSION_REGISTRY, ((ResourceKey)entry.getKey()).location())).collect(ImmutableSet.toImmutableSet());
    }

    public boolean isDebug() {
        return this.overworld() instanceof DebugLevelSource;
    }

    public boolean isFlatWorld() {
        return this.overworld() instanceof FlatLevelSource;
    }

    public boolean isOldCustomizedWorld() {
        return this.legacyCustomOptions.isPresent();
    }

    public WorldGenSettings withBonusChest() {
        return new WorldGenSettings(this.seed, this.generateFeatures, true, this.dimensions, this.legacyCustomOptions);
    }

    public WorldGenSettings withFeaturesToggled() {
        return new WorldGenSettings(this.seed, !this.generateFeatures, this.generateBonusChest, this.dimensions);
    }

    public WorldGenSettings withBonusChestToggled() {
        return new WorldGenSettings(this.seed, this.generateFeatures, !this.generateBonusChest, this.dimensions);
    }

    public static WorldGenSettings create(RegistryAccess registryAccess, Properties properties) {
        String string2 = (String)MoreObjects.firstNonNull((Object)((String)properties.get("generator-settings")), (Object)"");
        properties.put("generator-settings", string2);
        String string3 = (String)MoreObjects.firstNonNull((Object)((String)properties.get("level-seed")), (Object)"");
        properties.put("level-seed", string3);
        String string4 = (String)properties.get("generate-structures");
        boolean bl = string4 == null || Boolean.parseBoolean(string4);
        properties.put("generate-structures", Objects.toString(bl));
        String string5 = (String)properties.get("level-type");
        String string6 = Optional.ofNullable(string5).map(string -> string.toLowerCase(Locale.ROOT)).orElse("default");
        properties.put("level-type", string6);
        long l = new Random().nextLong();
        if (!string3.isEmpty()) {
            try {
                long l2 = Long.parseLong(string3);
                if (l2 != 0L) {
                    l = l2;
                }
            }
            catch (NumberFormatException numberFormatException) {
                l = string3.hashCode();
            }
        }
        WritableRegistry<DimensionType> writableRegistry = registryAccess.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
        WritableRegistry<Biome> writableRegistry2 = registryAccess.registryOrThrow(Registry.BIOME_REGISTRY);
        WritableRegistry<NoiseGeneratorSettings> writableRegistry3 = registryAccess.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY);
        MappedRegistry<LevelStem> mappedRegistry = DimensionType.defaultDimensions(writableRegistry, writableRegistry2, writableRegistry3, l);
        switch (string6) {
            case "flat": {
                JsonObject jsonObject = !string2.isEmpty() ? GsonHelper.parse(string2) : new JsonObject();
                Dynamic dynamic = new Dynamic((DynamicOps)JsonOps.INSTANCE, (Object)jsonObject);
                return new WorldGenSettings(l, bl, false, WorldGenSettings.withOverworld(writableRegistry, mappedRegistry, (ChunkGenerator)new FlatLevelSource(FlatLevelGeneratorSettings.CODEC.parse(dynamic).resultOrPartial(((Logger)LOGGER)::error).orElseGet(() -> FlatLevelGeneratorSettings.getDefault(writableRegistry2)))));
            }
            case "debug_all_block_states": {
                return new WorldGenSettings(l, bl, false, WorldGenSettings.withOverworld(writableRegistry, mappedRegistry, (ChunkGenerator)new DebugLevelSource(writableRegistry2)));
            }
            case "amplified": {
                return new WorldGenSettings(l, bl, false, WorldGenSettings.withOverworld(writableRegistry, mappedRegistry, (ChunkGenerator)new NoiseBasedChunkGenerator(new OverworldBiomeSource(l, false, false, writableRegistry2), l, () -> writableRegistry3.getOrThrow(NoiseGeneratorSettings.AMPLIFIED))));
            }
            case "largebiomes": {
                return new WorldGenSettings(l, bl, false, WorldGenSettings.withOverworld(writableRegistry, mappedRegistry, (ChunkGenerator)new NoiseBasedChunkGenerator(new OverworldBiomeSource(l, false, true, writableRegistry2), l, () -> writableRegistry3.getOrThrow(NoiseGeneratorSettings.OVERWORLD))));
            }
        }
        return new WorldGenSettings(l, bl, false, WorldGenSettings.withOverworld(writableRegistry, mappedRegistry, (ChunkGenerator)WorldGenSettings.makeDefaultOverworld(writableRegistry2, writableRegistry3, l)));
    }

    public WorldGenSettings withSeed(boolean bl, OptionalLong optionalLong) {
        MappedRegistry<LevelStem> mappedRegistry;
        long l = optionalLong.orElse(this.seed);
        if (optionalLong.isPresent()) {
            mappedRegistry = new MappedRegistry<LevelStem>(Registry.LEVEL_STEM_REGISTRY, Lifecycle.experimental());
            long l2 = optionalLong.getAsLong();
            for (Map.Entry<ResourceKey<LevelStem>, LevelStem> entry : this.dimensions.entrySet()) {
                ResourceKey<LevelStem> resourceKey = entry.getKey();
                mappedRegistry.register(resourceKey, new LevelStem(entry.getValue().typeSupplier(), entry.getValue().generator().withSeed(l2)), this.dimensions.lifecycle(entry.getValue()));
            }
        } else {
            mappedRegistry = this.dimensions;
        }
        WorldGenSettings worldGenSettings = this.isDebug() ? new WorldGenSettings(l, false, false, mappedRegistry) : new WorldGenSettings(l, this.generateFeatures(), this.generateBonusChest() && !bl, mappedRegistry);
        return worldGenSettings;
    }
}

