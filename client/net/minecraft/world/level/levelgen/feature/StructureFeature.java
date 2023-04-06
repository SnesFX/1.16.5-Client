/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.HashBiMap
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Maps
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.FeatureAccess;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.BastionFeature;
import net.minecraft.world.level.levelgen.feature.BuriedTreasureFeature;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.DesertPyramidFeature;
import net.minecraft.world.level.levelgen.feature.EndCityFeature;
import net.minecraft.world.level.levelgen.feature.IglooFeature;
import net.minecraft.world.level.levelgen.feature.JunglePyramidFeature;
import net.minecraft.world.level.levelgen.feature.MineshaftFeature;
import net.minecraft.world.level.levelgen.feature.NetherFortressFeature;
import net.minecraft.world.level.levelgen.feature.OceanMonumentFeature;
import net.minecraft.world.level.levelgen.feature.PillagerOutpostFeature;
import net.minecraft.world.level.levelgen.feature.RuinedPortalFeature;
import net.minecraft.world.level.levelgen.feature.ShipwreckFeature;
import net.minecraft.world.level.levelgen.feature.StrongholdFeature;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.feature.SwamplandHutFeature;
import net.minecraft.world.level.levelgen.feature.VillageFeature;
import net.minecraft.world.level.levelgen.feature.WoodlandMansionFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.MineshaftConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OceanRuinConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RuinedPortalConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ShipwreckConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.NetherFossilFeature;
import net.minecraft.world.level.levelgen.structure.OceanRuinFeature;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class StructureFeature<C extends FeatureConfiguration> {
    public static final BiMap<String, StructureFeature<?>> STRUCTURES_REGISTRY = HashBiMap.create();
    private static final Map<StructureFeature<?>, GenerationStep.Decoration> STEP = Maps.newHashMap();
    private static final Logger LOGGER = LogManager.getLogger();
    public static final StructureFeature<JigsawConfiguration> PILLAGER_OUTPOST = StructureFeature.register("Pillager_Outpost", new PillagerOutpostFeature(JigsawConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
    public static final StructureFeature<MineshaftConfiguration> MINESHAFT = StructureFeature.register("Mineshaft", new MineshaftFeature(MineshaftConfiguration.CODEC), GenerationStep.Decoration.UNDERGROUND_STRUCTURES);
    public static final StructureFeature<NoneFeatureConfiguration> WOODLAND_MANSION = StructureFeature.register("Mansion", new WoodlandMansionFeature(NoneFeatureConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
    public static final StructureFeature<NoneFeatureConfiguration> JUNGLE_TEMPLE = StructureFeature.register("Jungle_Pyramid", new JunglePyramidFeature(NoneFeatureConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
    public static final StructureFeature<NoneFeatureConfiguration> DESERT_PYRAMID = StructureFeature.register("Desert_Pyramid", new DesertPyramidFeature(NoneFeatureConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
    public static final StructureFeature<NoneFeatureConfiguration> IGLOO = StructureFeature.register("Igloo", new IglooFeature(NoneFeatureConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
    public static final StructureFeature<RuinedPortalConfiguration> RUINED_PORTAL = StructureFeature.register("Ruined_Portal", new RuinedPortalFeature(RuinedPortalConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
    public static final StructureFeature<ShipwreckConfiguration> SHIPWRECK = StructureFeature.register("Shipwreck", new ShipwreckFeature(ShipwreckConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
    public static final SwamplandHutFeature SWAMP_HUT = StructureFeature.register("Swamp_Hut", new SwamplandHutFeature(NoneFeatureConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
    public static final StructureFeature<NoneFeatureConfiguration> STRONGHOLD = StructureFeature.register("Stronghold", new StrongholdFeature(NoneFeatureConfiguration.CODEC), GenerationStep.Decoration.STRONGHOLDS);
    public static final StructureFeature<NoneFeatureConfiguration> OCEAN_MONUMENT = StructureFeature.register("Monument", new OceanMonumentFeature(NoneFeatureConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
    public static final StructureFeature<OceanRuinConfiguration> OCEAN_RUIN = StructureFeature.register("Ocean_Ruin", new OceanRuinFeature(OceanRuinConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
    public static final StructureFeature<NoneFeatureConfiguration> NETHER_BRIDGE = StructureFeature.register("Fortress", new NetherFortressFeature(NoneFeatureConfiguration.CODEC), GenerationStep.Decoration.UNDERGROUND_DECORATION);
    public static final StructureFeature<NoneFeatureConfiguration> END_CITY = StructureFeature.register("EndCity", new EndCityFeature(NoneFeatureConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
    public static final StructureFeature<ProbabilityFeatureConfiguration> BURIED_TREASURE = StructureFeature.register("Buried_Treasure", new BuriedTreasureFeature(ProbabilityFeatureConfiguration.CODEC), GenerationStep.Decoration.UNDERGROUND_STRUCTURES);
    public static final StructureFeature<JigsawConfiguration> VILLAGE = StructureFeature.register("Village", new VillageFeature(JigsawConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
    public static final StructureFeature<NoneFeatureConfiguration> NETHER_FOSSIL = StructureFeature.register("Nether_Fossil", new NetherFossilFeature(NoneFeatureConfiguration.CODEC), GenerationStep.Decoration.UNDERGROUND_DECORATION);
    public static final StructureFeature<JigsawConfiguration> BASTION_REMNANT = StructureFeature.register("Bastion_Remnant", new BastionFeature(JigsawConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
    public static final List<StructureFeature<?>> NOISE_AFFECTING_FEATURES = ImmutableList.of(PILLAGER_OUTPOST, VILLAGE, NETHER_FOSSIL);
    private static final ResourceLocation JIGSAW_RENAME = new ResourceLocation("jigsaw");
    private static final Map<ResourceLocation, ResourceLocation> RENAMES = ImmutableMap.builder().put((Object)new ResourceLocation("nvi"), (Object)JIGSAW_RENAME).put((Object)new ResourceLocation("pcp"), (Object)JIGSAW_RENAME).put((Object)new ResourceLocation("bastionremnant"), (Object)JIGSAW_RENAME).put((Object)new ResourceLocation("runtime"), (Object)JIGSAW_RENAME).build();
    private final Codec<ConfiguredStructureFeature<C, StructureFeature<C>>> configuredStructureCodec;

    private static <F extends StructureFeature<?>> F register(String string, F f, GenerationStep.Decoration decoration) {
        STRUCTURES_REGISTRY.put((Object)string.toLowerCase(Locale.ROOT), f);
        STEP.put(f, decoration);
        return (F)Registry.register(Registry.STRUCTURE_FEATURE, string.toLowerCase(Locale.ROOT), f);
    }

    public StructureFeature(Codec<C> codec) {
        this.configuredStructureCodec = codec.fieldOf("config").xmap(featureConfiguration -> new ConfiguredStructureFeature<FeatureConfiguration, StructureFeature>(this, (FeatureConfiguration)featureConfiguration), configuredStructureFeature -> configuredStructureFeature.config).codec();
    }

    public GenerationStep.Decoration step() {
        return STEP.get(this);
    }

    public static void bootstrap() {
    }

    @Nullable
    public static StructureStart<?> loadStaticStart(StructureManager structureManager, CompoundTag compoundTag, long l) {
        String string = compoundTag.getString("id");
        if ("INVALID".equals(string)) {
            return StructureStart.INVALID_START;
        }
        StructureFeature<?> structureFeature = Registry.STRUCTURE_FEATURE.get(new ResourceLocation(string.toLowerCase(Locale.ROOT)));
        if (structureFeature == null) {
            LOGGER.error("Unknown feature id: {}", (Object)string);
            return null;
        }
        int n = compoundTag.getInt("ChunkX");
        int n2 = compoundTag.getInt("ChunkZ");
        int n3 = compoundTag.getInt("references");
        BoundingBox boundingBox = compoundTag.contains("BB") ? new BoundingBox(compoundTag.getIntArray("BB")) : BoundingBox.getUnknownBox();
        ListTag listTag = compoundTag.getList("Children", 10);
        try {
            StructureStart<?> structureStart = super.createStart(n, n2, boundingBox, n3, l);
            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag compoundTag2 = listTag.getCompound(i);
                String string2 = compoundTag2.getString("id").toLowerCase(Locale.ROOT);
                ResourceLocation resourceLocation = new ResourceLocation(string2);
                ResourceLocation resourceLocation2 = RENAMES.getOrDefault(resourceLocation, resourceLocation);
                StructurePieceType structurePieceType = Registry.STRUCTURE_PIECE.get(resourceLocation2);
                if (structurePieceType == null) {
                    LOGGER.error("Unknown structure piece id: {}", (Object)resourceLocation2);
                    continue;
                }
                try {
                    StructurePiece structurePiece = structurePieceType.load(structureManager, compoundTag2);
                    structureStart.getPieces().add(structurePiece);
                    continue;
                }
                catch (Exception exception) {
                    LOGGER.error("Exception loading structure piece with id {}", (Object)resourceLocation2, (Object)exception);
                }
            }
            return structureStart;
        }
        catch (Exception exception) {
            LOGGER.error("Failed Start with id {}", (Object)string, (Object)exception);
            return null;
        }
    }

    public Codec<ConfiguredStructureFeature<C, StructureFeature<C>>> configuredStructureCodec() {
        return this.configuredStructureCodec;
    }

    public ConfiguredStructureFeature<C, ? extends StructureFeature<C>> configured(C c) {
        return new ConfiguredStructureFeature<C, StructureFeature>(this, c);
    }

    @Nullable
    public BlockPos getNearestGeneratedFeature(LevelReader levelReader, StructureFeatureManager structureFeatureManager, BlockPos blockPos, int n, boolean bl, long l, StructureFeatureConfiguration structureFeatureConfiguration) {
        int n2 = structureFeatureConfiguration.spacing();
        int n3 = blockPos.getX() >> 4;
        int n4 = blockPos.getZ() >> 4;
        WorldgenRandom worldgenRandom = new WorldgenRandom();
        block0 : for (int i = 0; i <= n; ++i) {
            for (int j = -i; j <= i; ++j) {
                boolean bl2 = j == -i || j == i;
                for (int k = -i; k <= i; ++k) {
                    boolean bl3;
                    boolean bl4 = bl3 = k == -i || k == i;
                    if (!bl2 && !bl3) continue;
                    int n5 = n3 + n2 * j;
                    int n6 = n4 + n2 * k;
                    ChunkPos chunkPos = this.getPotentialFeatureChunk(structureFeatureConfiguration, l, worldgenRandom, n5, n6);
                    ChunkAccess chunkAccess = levelReader.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.STRUCTURE_STARTS);
                    StructureStart<?> structureStart = structureFeatureManager.getStartForFeature(SectionPos.of(chunkAccess.getPos(), 0), this, chunkAccess);
                    if (structureStart != null && structureStart.isValid()) {
                        if (bl && structureStart.canBeReferenced()) {
                            structureStart.addReference();
                            return structureStart.getLocatePos();
                        }
                        if (!bl) {
                            return structureStart.getLocatePos();
                        }
                    }
                    if (i == 0) break;
                }
                if (i == 0) continue block0;
            }
        }
        return null;
    }

    protected boolean linearSeparation() {
        return true;
    }

    public final ChunkPos getPotentialFeatureChunk(StructureFeatureConfiguration structureFeatureConfiguration, long l, WorldgenRandom worldgenRandom, int n, int n2) {
        int n3;
        int n4;
        int n5 = structureFeatureConfiguration.spacing();
        int n6 = structureFeatureConfiguration.separation();
        int n7 = Math.floorDiv(n, n5);
        int n8 = Math.floorDiv(n2, n5);
        worldgenRandom.setLargeFeatureWithSalt(l, n7, n8, structureFeatureConfiguration.salt());
        if (this.linearSeparation()) {
            n4 = worldgenRandom.nextInt(n5 - n6);
            n3 = worldgenRandom.nextInt(n5 - n6);
        } else {
            n4 = (worldgenRandom.nextInt(n5 - n6) + worldgenRandom.nextInt(n5 - n6)) / 2;
            n3 = (worldgenRandom.nextInt(n5 - n6) + worldgenRandom.nextInt(n5 - n6)) / 2;
        }
        return new ChunkPos(n7 * n5 + n4, n8 * n5 + n3);
    }

    protected boolean isFeatureChunk(ChunkGenerator chunkGenerator, BiomeSource biomeSource, long l, WorldgenRandom worldgenRandom, int n, int n2, Biome biome, ChunkPos chunkPos, C c) {
        return true;
    }

    private StructureStart<C> createStart(int n, int n2, BoundingBox boundingBox, int n3, long l) {
        return this.getStartFactory().create(this, n, n2, boundingBox, n3, l);
    }

    public StructureStart<?> generate(RegistryAccess registryAccess, ChunkGenerator chunkGenerator, BiomeSource biomeSource, StructureManager structureManager, long l, ChunkPos chunkPos, Biome biome, int n, WorldgenRandom worldgenRandom, StructureFeatureConfiguration structureFeatureConfiguration, C c) {
        ChunkPos chunkPos2 = this.getPotentialFeatureChunk(structureFeatureConfiguration, l, worldgenRandom, chunkPos.x, chunkPos.z);
        if (chunkPos.x == chunkPos2.x && chunkPos.z == chunkPos2.z && this.isFeatureChunk(chunkGenerator, biomeSource, l, worldgenRandom, chunkPos.x, chunkPos.z, biome, chunkPos2, c)) {
            StructureStart<C> structureStart = this.createStart(chunkPos.x, chunkPos.z, BoundingBox.getUnknownBox(), n, l);
            structureStart.generatePieces(registryAccess, chunkGenerator, structureManager, chunkPos.x, chunkPos.z, biome, c);
            if (structureStart.isValid()) {
                return structureStart;
            }
        }
        return StructureStart.INVALID_START;
    }

    public abstract StructureStartFactory<C> getStartFactory();

    public String getFeatureName() {
        return (String)STRUCTURES_REGISTRY.inverse().get((Object)this);
    }

    public List<MobSpawnSettings.SpawnerData> getSpecialEnemies() {
        return ImmutableList.of();
    }

    public List<MobSpawnSettings.SpawnerData> getSpecialAnimals() {
        return ImmutableList.of();
    }

    public static interface StructureStartFactory<C extends FeatureConfiguration> {
        public StructureStart<C> create(StructureFeature<C> var1, int var2, int var3, BoundingBox var4, int var5, long var6);
    }

}

