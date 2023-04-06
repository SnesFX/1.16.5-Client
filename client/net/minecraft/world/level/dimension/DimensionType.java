/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Products
 *  com.mojang.datafixers.Products$P14
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Function14
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.Lifecycle
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.PrimitiveCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 */
package net.minecraft.world.level.dimension;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Function14;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.File;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.BiomeZoomer;
import net.minecraft.world.level.biome.FuzzyOffsetBiomeZoomer;
import net.minecraft.world.level.biome.FuzzyOffsetConstantColumnBiomeZoomer;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.TheEndBiomeSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

public class DimensionType {
    public static final ResourceLocation OVERWORLD_EFFECTS = new ResourceLocation("overworld");
    public static final ResourceLocation NETHER_EFFECTS = new ResourceLocation("the_nether");
    public static final ResourceLocation END_EFFECTS = new ResourceLocation("the_end");
    public static final Codec<DimensionType> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.LONG.optionalFieldOf("fixed_time").xmap(optional -> optional.map(OptionalLong::of).orElseGet(OptionalLong::empty), optionalLong -> optionalLong.isPresent() ? Optional.of(optionalLong.getAsLong()) : Optional.empty()).forGetter(dimensionType -> dimensionType.fixedTime), (App)Codec.BOOL.fieldOf("has_skylight").forGetter(DimensionType::hasSkyLight), (App)Codec.BOOL.fieldOf("has_ceiling").forGetter(DimensionType::hasCeiling), (App)Codec.BOOL.fieldOf("ultrawarm").forGetter(DimensionType::ultraWarm), (App)Codec.BOOL.fieldOf("natural").forGetter(DimensionType::natural), (App)Codec.doubleRange((double)9.999999747378752E-6, (double)3.0E7).fieldOf("coordinate_scale").forGetter(DimensionType::coordinateScale), (App)Codec.BOOL.fieldOf("piglin_safe").forGetter(DimensionType::piglinSafe), (App)Codec.BOOL.fieldOf("bed_works").forGetter(DimensionType::bedWorks), (App)Codec.BOOL.fieldOf("respawn_anchor_works").forGetter(DimensionType::respawnAnchorWorks), (App)Codec.BOOL.fieldOf("has_raids").forGetter(DimensionType::hasRaids), (App)Codec.intRange((int)0, (int)256).fieldOf("logical_height").forGetter(DimensionType::logicalHeight), (App)ResourceLocation.CODEC.fieldOf("infiniburn").forGetter(dimensionType -> dimensionType.infiniburn), (App)ResourceLocation.CODEC.fieldOf("effects").orElse((Object)OVERWORLD_EFFECTS).forGetter(dimensionType -> dimensionType.effectsLocation), (App)Codec.FLOAT.fieldOf("ambient_light").forGetter(dimensionType -> Float.valueOf(dimensionType.ambientLight))).apply((Applicative)instance, (arg_0, arg_1, arg_2, arg_3, arg_4, arg_5, arg_6, arg_7, arg_8, arg_9, arg_10, arg_11, arg_12, arg_13) -> DimensionType.new(arg_0, arg_1, arg_2, arg_3, arg_4, arg_5, arg_6, arg_7, arg_8, arg_9, arg_10, arg_11, arg_12, arg_13)));
    public static final float[] MOON_BRIGHTNESS_PER_PHASE = new float[]{1.0f, 0.75f, 0.5f, 0.25f, 0.0f, 0.25f, 0.5f, 0.75f};
    public static final ResourceKey<DimensionType> OVERWORLD_LOCATION = ResourceKey.create(Registry.DIMENSION_TYPE_REGISTRY, new ResourceLocation("overworld"));
    public static final ResourceKey<DimensionType> NETHER_LOCATION = ResourceKey.create(Registry.DIMENSION_TYPE_REGISTRY, new ResourceLocation("the_nether"));
    public static final ResourceKey<DimensionType> END_LOCATION = ResourceKey.create(Registry.DIMENSION_TYPE_REGISTRY, new ResourceLocation("the_end"));
    protected static final DimensionType DEFAULT_OVERWORLD = new DimensionType(OptionalLong.empty(), true, false, false, true, 1.0, false, false, true, false, true, 256, FuzzyOffsetConstantColumnBiomeZoomer.INSTANCE, BlockTags.INFINIBURN_OVERWORLD.getName(), OVERWORLD_EFFECTS, 0.0f);
    protected static final DimensionType DEFAULT_NETHER = new DimensionType(OptionalLong.of(18000L), false, true, true, false, 8.0, false, true, false, true, false, 128, FuzzyOffsetBiomeZoomer.INSTANCE, BlockTags.INFINIBURN_NETHER.getName(), NETHER_EFFECTS, 0.1f);
    protected static final DimensionType DEFAULT_END = new DimensionType(OptionalLong.of(6000L), false, false, false, false, 1.0, true, false, false, false, true, 256, FuzzyOffsetBiomeZoomer.INSTANCE, BlockTags.INFINIBURN_END.getName(), END_EFFECTS, 0.0f);
    public static final ResourceKey<DimensionType> OVERWORLD_CAVES_LOCATION = ResourceKey.create(Registry.DIMENSION_TYPE_REGISTRY, new ResourceLocation("overworld_caves"));
    protected static final DimensionType DEFAULT_OVERWORLD_CAVES = new DimensionType(OptionalLong.empty(), true, true, false, true, 1.0, false, false, true, false, true, 256, FuzzyOffsetConstantColumnBiomeZoomer.INSTANCE, BlockTags.INFINIBURN_OVERWORLD.getName(), OVERWORLD_EFFECTS, 0.0f);
    public static final Codec<Supplier<DimensionType>> CODEC = RegistryFileCodec.create(Registry.DIMENSION_TYPE_REGISTRY, DIRECT_CODEC);
    private final OptionalLong fixedTime;
    private final boolean hasSkylight;
    private final boolean hasCeiling;
    private final boolean ultraWarm;
    private final boolean natural;
    private final double coordinateScale;
    private final boolean createDragonFight;
    private final boolean piglinSafe;
    private final boolean bedWorks;
    private final boolean respawnAnchorWorks;
    private final boolean hasRaids;
    private final int logicalHeight;
    private final BiomeZoomer biomeZoomer;
    private final ResourceLocation infiniburn;
    private final ResourceLocation effectsLocation;
    private final float ambientLight;
    private final transient float[] brightnessRamp;

    protected DimensionType(OptionalLong optionalLong, boolean bl, boolean bl2, boolean bl3, boolean bl4, double d, boolean bl5, boolean bl6, boolean bl7, boolean bl8, int n, ResourceLocation resourceLocation, ResourceLocation resourceLocation2, float f) {
        this(optionalLong, bl, bl2, bl3, bl4, d, false, bl5, bl6, bl7, bl8, n, FuzzyOffsetBiomeZoomer.INSTANCE, resourceLocation, resourceLocation2, f);
    }

    protected DimensionType(OptionalLong optionalLong, boolean bl, boolean bl2, boolean bl3, boolean bl4, double d, boolean bl5, boolean bl6, boolean bl7, boolean bl8, boolean bl9, int n, BiomeZoomer biomeZoomer, ResourceLocation resourceLocation, ResourceLocation resourceLocation2, float f) {
        this.fixedTime = optionalLong;
        this.hasSkylight = bl;
        this.hasCeiling = bl2;
        this.ultraWarm = bl3;
        this.natural = bl4;
        this.coordinateScale = d;
        this.createDragonFight = bl5;
        this.piglinSafe = bl6;
        this.bedWorks = bl7;
        this.respawnAnchorWorks = bl8;
        this.hasRaids = bl9;
        this.logicalHeight = n;
        this.biomeZoomer = biomeZoomer;
        this.infiniburn = resourceLocation;
        this.effectsLocation = resourceLocation2;
        this.ambientLight = f;
        this.brightnessRamp = DimensionType.fillBrightnessRamp(f);
    }

    private static float[] fillBrightnessRamp(float f) {
        float[] arrf = new float[16];
        for (int i = 0; i <= 15; ++i) {
            float f2 = (float)i / 15.0f;
            float f3 = f2 / (4.0f - 3.0f * f2);
            arrf[i] = Mth.lerp(f, f3, 1.0f);
        }
        return arrf;
    }

    @Deprecated
    public static DataResult<ResourceKey<Level>> parseLegacy(Dynamic<?> dynamic) {
        Optional optional = dynamic.asNumber().result();
        if (optional.isPresent()) {
            int n = ((Number)optional.get()).intValue();
            if (n == -1) {
                return DataResult.success(Level.NETHER);
            }
            if (n == 0) {
                return DataResult.success(Level.OVERWORLD);
            }
            if (n == 1) {
                return DataResult.success(Level.END);
            }
        }
        return Level.RESOURCE_KEY_CODEC.parse(dynamic);
    }

    public static RegistryAccess.RegistryHolder registerBuiltin(RegistryAccess.RegistryHolder registryHolder) {
        WritableRegistry<DimensionType> writableRegistry = registryHolder.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
        writableRegistry.register(OVERWORLD_LOCATION, DEFAULT_OVERWORLD, Lifecycle.stable());
        writableRegistry.register(OVERWORLD_CAVES_LOCATION, DEFAULT_OVERWORLD_CAVES, Lifecycle.stable());
        writableRegistry.register(NETHER_LOCATION, DEFAULT_NETHER, Lifecycle.stable());
        writableRegistry.register(END_LOCATION, DEFAULT_END, Lifecycle.stable());
        return registryHolder;
    }

    private static ChunkGenerator defaultEndGenerator(Registry<Biome> registry, Registry<NoiseGeneratorSettings> registry2, long l) {
        return new NoiseBasedChunkGenerator(new TheEndBiomeSource(registry, l), l, () -> registry2.getOrThrow(NoiseGeneratorSettings.END));
    }

    private static ChunkGenerator defaultNetherGenerator(Registry<Biome> registry, Registry<NoiseGeneratorSettings> registry2, long l) {
        return new NoiseBasedChunkGenerator(MultiNoiseBiomeSource.Preset.NETHER.biomeSource(registry, l), l, () -> registry2.getOrThrow(NoiseGeneratorSettings.NETHER));
    }

    public static MappedRegistry<LevelStem> defaultDimensions(Registry<DimensionType> registry, Registry<Biome> registry2, Registry<NoiseGeneratorSettings> registry3, long l) {
        MappedRegistry<LevelStem> mappedRegistry = new MappedRegistry<LevelStem>(Registry.LEVEL_STEM_REGISTRY, Lifecycle.experimental());
        mappedRegistry.register(LevelStem.NETHER, new LevelStem(() -> registry.getOrThrow(NETHER_LOCATION), DimensionType.defaultNetherGenerator(registry2, registry3, l)), Lifecycle.stable());
        mappedRegistry.register(LevelStem.END, new LevelStem(() -> registry.getOrThrow(END_LOCATION), DimensionType.defaultEndGenerator(registry2, registry3, l)), Lifecycle.stable());
        return mappedRegistry;
    }

    public static double getTeleportationScale(DimensionType dimensionType, DimensionType dimensionType2) {
        double d = dimensionType.coordinateScale();
        double d2 = dimensionType2.coordinateScale();
        return d / d2;
    }

    @Deprecated
    public String getFileSuffix() {
        if (this.equalTo(DEFAULT_END)) {
            return "_end";
        }
        return "";
    }

    public static File getStorageFolder(ResourceKey<Level> resourceKey, File file) {
        if (resourceKey == Level.OVERWORLD) {
            return file;
        }
        if (resourceKey == Level.END) {
            return new File(file, "DIM1");
        }
        if (resourceKey == Level.NETHER) {
            return new File(file, "DIM-1");
        }
        return new File(file, "dimensions/" + resourceKey.location().getNamespace() + "/" + resourceKey.location().getPath());
    }

    public boolean hasSkyLight() {
        return this.hasSkylight;
    }

    public boolean hasCeiling() {
        return this.hasCeiling;
    }

    public boolean ultraWarm() {
        return this.ultraWarm;
    }

    public boolean natural() {
        return this.natural;
    }

    public double coordinateScale() {
        return this.coordinateScale;
    }

    public boolean piglinSafe() {
        return this.piglinSafe;
    }

    public boolean bedWorks() {
        return this.bedWorks;
    }

    public boolean respawnAnchorWorks() {
        return this.respawnAnchorWorks;
    }

    public boolean hasRaids() {
        return this.hasRaids;
    }

    public int logicalHeight() {
        return this.logicalHeight;
    }

    public boolean createDragonFight() {
        return this.createDragonFight;
    }

    public BiomeZoomer getBiomeZoomer() {
        return this.biomeZoomer;
    }

    public boolean hasFixedTime() {
        return this.fixedTime.isPresent();
    }

    public float timeOfDay(long l) {
        double d = Mth.frac((double)this.fixedTime.orElse(l) / 24000.0 - 0.25);
        double d2 = 0.5 - Math.cos(d * 3.141592653589793) / 2.0;
        return (float)(d * 2.0 + d2) / 3.0f;
    }

    public int moonPhase(long l) {
        return (int)(l / 24000L % 8L + 8L) % 8;
    }

    public float brightness(int n) {
        return this.brightnessRamp[n];
    }

    public Tag<Block> infiniburn() {
        Tag<Block> tag = BlockTags.getAllTags().getTag(this.infiniburn);
        return tag != null ? tag : BlockTags.INFINIBURN_OVERWORLD;
    }

    public ResourceLocation effectsLocation() {
        return this.effectsLocation;
    }

    public boolean equalTo(DimensionType dimensionType) {
        if (this == dimensionType) {
            return true;
        }
        return this.hasSkylight == dimensionType.hasSkylight && this.hasCeiling == dimensionType.hasCeiling && this.ultraWarm == dimensionType.ultraWarm && this.natural == dimensionType.natural && this.coordinateScale == dimensionType.coordinateScale && this.createDragonFight == dimensionType.createDragonFight && this.piglinSafe == dimensionType.piglinSafe && this.bedWorks == dimensionType.bedWorks && this.respawnAnchorWorks == dimensionType.respawnAnchorWorks && this.hasRaids == dimensionType.hasRaids && this.logicalHeight == dimensionType.logicalHeight && Float.compare(dimensionType.ambientLight, this.ambientLight) == 0 && this.fixedTime.equals(dimensionType.fixedTime) && this.biomeZoomer.equals(dimensionType.biomeZoomer) && this.infiniburn.equals(dimensionType.infiniburn) && this.effectsLocation.equals(dimensionType.effectsLocation);
    }
}

