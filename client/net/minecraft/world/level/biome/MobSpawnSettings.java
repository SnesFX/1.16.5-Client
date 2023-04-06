/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.Products
 *  com.mojang.datafixers.Products$P2
 *  com.mojang.datafixers.Products$P4
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Function4
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Keyable
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.PrimitiveCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.WeighedRandom;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.Pig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MobSpawnSettings {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final MobSpawnSettings EMPTY = new MobSpawnSettings(0.1f, (Map)Stream.of(MobCategory.values()).collect(ImmutableMap.toImmutableMap(mobCategory -> mobCategory, mobCategory -> ImmutableList.of())), (Map<EntityType<?>, MobSpawnCost>)ImmutableMap.of(), false);
    public static final MapCodec<MobSpawnSettings> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.FLOAT.optionalFieldOf("creature_spawn_probability", (Object)Float.valueOf(0.1f)).forGetter(mobSpawnSettings -> Float.valueOf(mobSpawnSettings.creatureGenerationProbability)), (App)Codec.simpleMap(MobCategory.CODEC, (Codec)SpawnerData.CODEC.listOf().promotePartial(Util.prefix("Spawn data: ", ((Logger)LOGGER)::error)), (Keyable)StringRepresentable.keys(MobCategory.values())).fieldOf("spawners").forGetter(mobSpawnSettings -> mobSpawnSettings.spawners), (App)Codec.simpleMap(Registry.ENTITY_TYPE, MobSpawnCost.CODEC, Registry.ENTITY_TYPE).fieldOf("spawn_costs").forGetter(mobSpawnSettings -> mobSpawnSettings.mobSpawnCosts), (App)Codec.BOOL.fieldOf("player_spawn_friendly").orElse((Object)false).forGetter(MobSpawnSettings::playerSpawnFriendly)).apply((Applicative)instance, (arg_0, arg_1, arg_2, arg_3) -> MobSpawnSettings.new(arg_0, arg_1, arg_2, arg_3)));
    private final float creatureGenerationProbability;
    private final Map<MobCategory, List<SpawnerData>> spawners;
    private final Map<EntityType<?>, MobSpawnCost> mobSpawnCosts;
    private final boolean playerSpawnFriendly;

    private MobSpawnSettings(float f, Map<MobCategory, List<SpawnerData>> map, Map<EntityType<?>, MobSpawnCost> map2, boolean bl) {
        this.creatureGenerationProbability = f;
        this.spawners = map;
        this.mobSpawnCosts = map2;
        this.playerSpawnFriendly = bl;
    }

    public List<SpawnerData> getMobs(MobCategory mobCategory) {
        return this.spawners.getOrDefault(mobCategory, (List<SpawnerData>)ImmutableList.of());
    }

    @Nullable
    public MobSpawnCost getMobSpawnCost(EntityType<?> entityType) {
        return this.mobSpawnCosts.get(entityType);
    }

    public float getCreatureProbability() {
        return this.creatureGenerationProbability;
    }

    public boolean playerSpawnFriendly() {
        return this.playerSpawnFriendly;
    }

    public static class Builder {
        private final Map<MobCategory, List<SpawnerData>> spawners = (Map)Stream.of(MobCategory.values()).collect(ImmutableMap.toImmutableMap(mobCategory -> mobCategory, mobCategory -> Lists.newArrayList()));
        private final Map<EntityType<?>, MobSpawnCost> mobSpawnCosts = Maps.newLinkedHashMap();
        private float creatureGenerationProbability = 0.1f;
        private boolean playerCanSpawn;

        public Builder addSpawn(MobCategory mobCategory, SpawnerData spawnerData) {
            this.spawners.get(mobCategory).add(spawnerData);
            return this;
        }

        public Builder addMobCharge(EntityType<?> entityType, double d, double d2) {
            this.mobSpawnCosts.put(entityType, new MobSpawnCost(d2, d));
            return this;
        }

        public Builder creatureGenerationProbability(float f) {
            this.creatureGenerationProbability = f;
            return this;
        }

        public Builder setPlayerCanSpawn() {
            this.playerCanSpawn = true;
            return this;
        }

        public MobSpawnSettings build() {
            return new MobSpawnSettings(this.creatureGenerationProbability, (Map)this.spawners.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry -> ImmutableList.copyOf((Collection)((Collection)entry.getValue())))), (Map)ImmutableMap.copyOf(this.mobSpawnCosts), this.playerCanSpawn);
        }
    }

    public static class MobSpawnCost {
        public static final Codec<MobSpawnCost> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.DOUBLE.fieldOf("energy_budget").forGetter(mobSpawnCost -> mobSpawnCost.energyBudget), (App)Codec.DOUBLE.fieldOf("charge").forGetter(mobSpawnCost -> mobSpawnCost.charge)).apply((Applicative)instance, (arg_0, arg_1) -> MobSpawnCost.new(arg_0, arg_1)));
        private final double energyBudget;
        private final double charge;

        private MobSpawnCost(double d, double d2) {
            this.energyBudget = d;
            this.charge = d2;
        }

        public double getEnergyBudget() {
            return this.energyBudget;
        }

        public double getCharge() {
            return this.charge;
        }
    }

    public static class SpawnerData
    extends WeighedRandom.WeighedRandomItem {
        public static final Codec<SpawnerData> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Registry.ENTITY_TYPE.fieldOf("type").forGetter(spawnerData -> spawnerData.type), (App)Codec.INT.fieldOf("weight").forGetter(spawnerData -> spawnerData.weight), (App)Codec.INT.fieldOf("minCount").forGetter(spawnerData -> spawnerData.minCount), (App)Codec.INT.fieldOf("maxCount").forGetter(spawnerData -> spawnerData.maxCount)).apply((Applicative)instance, (arg_0, arg_1, arg_2, arg_3) -> SpawnerData.new(arg_0, arg_1, arg_2, arg_3)));
        public final EntityType<?> type;
        public final int minCount;
        public final int maxCount;

        public SpawnerData(EntityType<?> entityType, int n, int n2, int n3) {
            super(n);
            this.type = entityType.getCategory() == MobCategory.MISC ? EntityType.PIG : entityType;
            this.minCount = n2;
            this.maxCount = n3;
        }

        public String toString() {
            return EntityType.getKey(this.type) + "*(" + this.minCount + "-" + this.maxCount + "):" + this.weight;
        }
    }

}

