/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.Products
 *  com.mojang.datafixers.Products$P2
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.levelgen;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.SwamplandHutFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.MineshaftConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OceanRuinConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RuinedPortalConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ShipwreckConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.StrongholdConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;

public class StructureSettings {
    public static final Codec<StructureSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)StrongholdConfiguration.CODEC.optionalFieldOf("stronghold").forGetter(structureSettings -> Optional.ofNullable(structureSettings.stronghold)), (App)Codec.simpleMap(Registry.STRUCTURE_FEATURE, StructureFeatureConfiguration.CODEC, Registry.STRUCTURE_FEATURE).fieldOf("structures").forGetter(structureSettings -> structureSettings.structureConfig)).apply((Applicative)instance, (arg_0, arg_1) -> StructureSettings.new(arg_0, arg_1)));
    public static final ImmutableMap<StructureFeature<?>, StructureFeatureConfiguration> DEFAULTS = ImmutableMap.builder().put(StructureFeature.VILLAGE, (Object)new StructureFeatureConfiguration(32, 8, 10387312)).put(StructureFeature.DESERT_PYRAMID, (Object)new StructureFeatureConfiguration(32, 8, 14357617)).put(StructureFeature.IGLOO, (Object)new StructureFeatureConfiguration(32, 8, 14357618)).put(StructureFeature.JUNGLE_TEMPLE, (Object)new StructureFeatureConfiguration(32, 8, 14357619)).put((Object)StructureFeature.SWAMP_HUT, (Object)new StructureFeatureConfiguration(32, 8, 14357620)).put(StructureFeature.PILLAGER_OUTPOST, (Object)new StructureFeatureConfiguration(32, 8, 165745296)).put(StructureFeature.STRONGHOLD, (Object)new StructureFeatureConfiguration(1, 0, 0)).put(StructureFeature.OCEAN_MONUMENT, (Object)new StructureFeatureConfiguration(32, 5, 10387313)).put(StructureFeature.END_CITY, (Object)new StructureFeatureConfiguration(20, 11, 10387313)).put(StructureFeature.WOODLAND_MANSION, (Object)new StructureFeatureConfiguration(80, 20, 10387319)).put(StructureFeature.BURIED_TREASURE, (Object)new StructureFeatureConfiguration(1, 0, 0)).put(StructureFeature.MINESHAFT, (Object)new StructureFeatureConfiguration(1, 0, 0)).put(StructureFeature.RUINED_PORTAL, (Object)new StructureFeatureConfiguration(40, 15, 34222645)).put(StructureFeature.SHIPWRECK, (Object)new StructureFeatureConfiguration(24, 4, 165745295)).put(StructureFeature.OCEAN_RUIN, (Object)new StructureFeatureConfiguration(20, 8, 14357621)).put(StructureFeature.BASTION_REMNANT, (Object)new StructureFeatureConfiguration(27, 4, 30084232)).put(StructureFeature.NETHER_BRIDGE, (Object)new StructureFeatureConfiguration(27, 4, 30084232)).put(StructureFeature.NETHER_FOSSIL, (Object)new StructureFeatureConfiguration(2, 1, 14357921)).build();
    public static final StrongholdConfiguration DEFAULT_STRONGHOLD;
    private final Map<StructureFeature<?>, StructureFeatureConfiguration> structureConfig;
    @Nullable
    private final StrongholdConfiguration stronghold;

    public StructureSettings(Optional<StrongholdConfiguration> optional, Map<StructureFeature<?>, StructureFeatureConfiguration> map) {
        this.stronghold = optional.orElse(null);
        this.structureConfig = map;
    }

    public StructureSettings(boolean bl) {
        this.structureConfig = Maps.newHashMap(DEFAULTS);
        this.stronghold = bl ? DEFAULT_STRONGHOLD : null;
    }

    public Map<StructureFeature<?>, StructureFeatureConfiguration> structureConfig() {
        return this.structureConfig;
    }

    @Nullable
    public StructureFeatureConfiguration getConfig(StructureFeature<?> structureFeature) {
        return this.structureConfig.get(structureFeature);
    }

    @Nullable
    public StrongholdConfiguration stronghold() {
        return this.stronghold;
    }

    static {
        for (StructureFeature structureFeature : Registry.STRUCTURE_FEATURE) {
            if (DEFAULTS.containsKey((Object)structureFeature)) continue;
            throw new IllegalStateException("Structure feature without default settings: " + Registry.STRUCTURE_FEATURE.getKey(structureFeature));
        }
        DEFAULT_STRONGHOLD = new StrongholdConfiguration(32, 3, 128);
    }
}

