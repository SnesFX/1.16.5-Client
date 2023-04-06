/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMaps
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.level;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.util.WeighedRandom;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.PotentialCalculator;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.biome.NearestNeighborBiomeZoomer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkBiomeContainer;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class NaturalSpawner {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int MAGIC_NUMBER = (int)Math.pow(17.0, 2.0);
    private static final MobCategory[] SPAWNING_CATEGORIES = (MobCategory[])Stream.of(MobCategory.values()).filter(mobCategory -> mobCategory != MobCategory.MISC).toArray(n -> new MobCategory[n]);

    public static SpawnState createState(int n, Iterable<Entity> iterable, ChunkGetter chunkGetter) {
        PotentialCalculator potentialCalculator = new PotentialCalculator();
        Object2IntOpenHashMap object2IntOpenHashMap = new Object2IntOpenHashMap();
        for (Entity entity : iterable) {
            Object object;
            if (entity instanceof Mob && (((Mob)(object = (Mob)entity)).isPersistenceRequired() || ((Mob)object).requiresCustomPersistence()) || (object = entity.getType().getCategory()) == MobCategory.MISC) continue;
            BlockPos blockPos = entity.blockPosition();
            long l = ChunkPos.asLong(blockPos.getX() >> 4, blockPos.getZ() >> 4);
            chunkGetter.query(l, arg_0 -> NaturalSpawner.lambda$createState$2(blockPos, entity, potentialCalculator, object2IntOpenHashMap, (MobCategory)object, arg_0));
        }
        return new SpawnState(n, object2IntOpenHashMap, potentialCalculator);
    }

    private static Biome getRoughBiome(BlockPos blockPos, ChunkAccess chunkAccess) {
        return NearestNeighborBiomeZoomer.INSTANCE.getBiome(0L, blockPos.getX(), blockPos.getY(), blockPos.getZ(), chunkAccess.getBiomes());
    }

    public static void spawnForChunk(ServerLevel serverLevel, LevelChunk levelChunk, SpawnState spawnState, boolean bl, boolean bl2, boolean bl3) {
        serverLevel.getProfiler().push("spawner");
        for (MobCategory mobCategory : SPAWNING_CATEGORIES) {
            if (!bl && mobCategory.isFriendly() || !bl2 && !mobCategory.isFriendly() || !bl3 && mobCategory.isPersistent() || !spawnState.canSpawnForCategory(mobCategory)) continue;
            NaturalSpawner.spawnCategoryForChunk(mobCategory, serverLevel, levelChunk, (entityType, blockPos, chunkAccess) -> spawnState.canSpawn(entityType, blockPos, chunkAccess), (mob, chunkAccess) -> spawnState.afterSpawn(mob, chunkAccess));
        }
        serverLevel.getProfiler().pop();
    }

    public static void spawnCategoryForChunk(MobCategory mobCategory, ServerLevel serverLevel, LevelChunk levelChunk, SpawnPredicate spawnPredicate, AfterSpawnCallback afterSpawnCallback) {
        BlockPos blockPos = NaturalSpawner.getRandomPosWithin(serverLevel, levelChunk);
        if (blockPos.getY() < 1) {
            return;
        }
        NaturalSpawner.spawnCategoryForPosition(mobCategory, serverLevel, levelChunk, blockPos, spawnPredicate, afterSpawnCallback);
    }

    public static void spawnCategoryForPosition(MobCategory mobCategory, ServerLevel serverLevel, ChunkAccess chunkAccess, BlockPos blockPos, SpawnPredicate spawnPredicate, AfterSpawnCallback afterSpawnCallback) {
        StructureFeatureManager structureFeatureManager = serverLevel.structureFeatureManager();
        ChunkGenerator chunkGenerator = serverLevel.getChunkSource().getGenerator();
        int n = blockPos.getY();
        BlockState blockState = chunkAccess.getBlockState(blockPos);
        if (blockState.isRedstoneConductor(chunkAccess, blockPos)) {
            return;
        }
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        int n2 = 0;
        block0 : for (int i = 0; i < 3; ++i) {
            int n3 = blockPos.getX();
            int n4 = blockPos.getZ();
            int n5 = 6;
            MobSpawnSettings.SpawnerData spawnerData = null;
            SpawnGroupData spawnGroupData = null;
            int n6 = Mth.ceil(serverLevel.random.nextFloat() * 4.0f);
            int n7 = 0;
            for (int j = 0; j < n6; ++j) {
                double d;
                mutableBlockPos.set(n3 += serverLevel.random.nextInt(6) - serverLevel.random.nextInt(6), n, n4 += serverLevel.random.nextInt(6) - serverLevel.random.nextInt(6));
                double d2 = (double)n3 + 0.5;
                double d3 = (double)n4 + 0.5;
                Player player = serverLevel.getNearestPlayer(d2, (double)n, d3, -1.0, false);
                if (player == null || !NaturalSpawner.isRightDistanceToPlayerAndSpawnPoint(serverLevel, chunkAccess, mutableBlockPos, d = player.distanceToSqr(d2, n, d3))) continue;
                if (spawnerData == null) {
                    spawnerData = NaturalSpawner.getRandomSpawnMobAt(serverLevel, structureFeatureManager, chunkGenerator, mobCategory, serverLevel.random, mutableBlockPos);
                    if (spawnerData == null) continue block0;
                    n6 = spawnerData.minCount + serverLevel.random.nextInt(1 + spawnerData.maxCount - spawnerData.minCount);
                }
                if (!NaturalSpawner.isValidSpawnPostitionForType(serverLevel, mobCategory, structureFeatureManager, chunkGenerator, spawnerData, mutableBlockPos, d) || !spawnPredicate.test(spawnerData.type, mutableBlockPos, chunkAccess)) continue;
                Mob mob = NaturalSpawner.getMobForSpawn(serverLevel, spawnerData.type);
                if (mob == null) {
                    return;
                }
                mob.moveTo(d2, n, d3, serverLevel.random.nextFloat() * 360.0f, 0.0f);
                if (!NaturalSpawner.isValidPositionForMob(serverLevel, mob, d)) continue;
                spawnGroupData = mob.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(mob.blockPosition()), MobSpawnType.NATURAL, spawnGroupData, null);
                ++n7;
                serverLevel.addFreshEntityWithPassengers(mob);
                afterSpawnCallback.run(mob, chunkAccess);
                if (++n2 >= mob.getMaxSpawnClusterSize()) {
                    return;
                }
                if (mob.isMaxGroupSizeReached(n7)) continue block0;
            }
        }
    }

    private static boolean isRightDistanceToPlayerAndSpawnPoint(ServerLevel serverLevel, ChunkAccess chunkAccess, BlockPos.MutableBlockPos mutableBlockPos, double d) {
        if (d <= 576.0) {
            return false;
        }
        if (serverLevel.getSharedSpawnPos().closerThan(new Vec3((double)mutableBlockPos.getX() + 0.5, mutableBlockPos.getY(), (double)mutableBlockPos.getZ() + 0.5), 24.0)) {
            return false;
        }
        ChunkPos chunkPos = new ChunkPos(mutableBlockPos);
        return Objects.equals(chunkPos, chunkAccess.getPos()) || serverLevel.getChunkSource().isEntityTickingChunk(chunkPos);
    }

    private static boolean isValidSpawnPostitionForType(ServerLevel serverLevel, MobCategory mobCategory, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, MobSpawnSettings.SpawnerData spawnerData, BlockPos.MutableBlockPos mutableBlockPos, double d) {
        EntityType<?> entityType = spawnerData.type;
        if (entityType.getCategory() == MobCategory.MISC) {
            return false;
        }
        if (!entityType.canSpawnFarFromPlayer() && d > (double)(entityType.getCategory().getDespawnDistance() * entityType.getCategory().getDespawnDistance())) {
            return false;
        }
        if (!entityType.canSummon() || !NaturalSpawner.canSpawnMobAt(serverLevel, structureFeatureManager, chunkGenerator, mobCategory, spawnerData, mutableBlockPos)) {
            return false;
        }
        SpawnPlacements.Type type = SpawnPlacements.getPlacementType(entityType);
        if (!NaturalSpawner.isSpawnPositionOk(type, serverLevel, mutableBlockPos, entityType)) {
            return false;
        }
        if (!SpawnPlacements.checkSpawnRules(entityType, serverLevel, MobSpawnType.NATURAL, mutableBlockPos, serverLevel.random)) {
            return false;
        }
        return serverLevel.noCollision(entityType.getAABB((double)mutableBlockPos.getX() + 0.5, mutableBlockPos.getY(), (double)mutableBlockPos.getZ() + 0.5));
    }

    @Nullable
    private static Mob getMobForSpawn(ServerLevel serverLevel, EntityType<?> entityType) {
        Mob mob;
        try {
            Object obj = entityType.create(serverLevel);
            if (!(obj instanceof Mob)) {
                throw new IllegalStateException("Trying to spawn a non-mob: " + Registry.ENTITY_TYPE.getKey(entityType));
            }
            mob = (Mob)obj;
        }
        catch (Exception exception) {
            LOGGER.warn("Failed to create mob", (Throwable)exception);
            return null;
        }
        return mob;
    }

    private static boolean isValidPositionForMob(ServerLevel serverLevel, Mob mob, double d) {
        if (d > (double)(mob.getType().getCategory().getDespawnDistance() * mob.getType().getCategory().getDespawnDistance()) && mob.removeWhenFarAway(d)) {
            return false;
        }
        return mob.checkSpawnRules(serverLevel, MobSpawnType.NATURAL) && mob.checkSpawnObstruction(serverLevel);
    }

    @Nullable
    private static MobSpawnSettings.SpawnerData getRandomSpawnMobAt(ServerLevel serverLevel, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, MobCategory mobCategory, Random random, BlockPos blockPos) {
        Biome biome = serverLevel.getBiome(blockPos);
        if (mobCategory == MobCategory.WATER_AMBIENT && biome.getBiomeCategory() == Biome.BiomeCategory.RIVER && random.nextFloat() < 0.98f) {
            return null;
        }
        List<MobSpawnSettings.SpawnerData> list = NaturalSpawner.mobsAt(serverLevel, structureFeatureManager, chunkGenerator, mobCategory, blockPos, biome);
        if (list.isEmpty()) {
            return null;
        }
        return WeighedRandom.getRandomItem(random, list);
    }

    private static boolean canSpawnMobAt(ServerLevel serverLevel, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, MobCategory mobCategory, MobSpawnSettings.SpawnerData spawnerData, BlockPos blockPos) {
        return NaturalSpawner.mobsAt(serverLevel, structureFeatureManager, chunkGenerator, mobCategory, blockPos, null).contains(spawnerData);
    }

    private static List<MobSpawnSettings.SpawnerData> mobsAt(ServerLevel serverLevel, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, MobCategory mobCategory, BlockPos blockPos, @Nullable Biome biome) {
        if (mobCategory == MobCategory.MONSTER && serverLevel.getBlockState(blockPos.below()).getBlock() == Blocks.NETHER_BRICKS && structureFeatureManager.getStructureAt(blockPos, false, StructureFeature.NETHER_BRIDGE).isValid()) {
            return StructureFeature.NETHER_BRIDGE.getSpecialEnemies();
        }
        return chunkGenerator.getMobsAt(biome != null ? biome : serverLevel.getBiome(blockPos), structureFeatureManager, mobCategory, blockPos);
    }

    private static BlockPos getRandomPosWithin(Level level, LevelChunk levelChunk) {
        ChunkPos chunkPos = levelChunk.getPos();
        int n = chunkPos.getMinBlockX() + level.random.nextInt(16);
        int n2 = chunkPos.getMinBlockZ() + level.random.nextInt(16);
        int n3 = levelChunk.getHeight(Heightmap.Types.WORLD_SURFACE, n, n2) + 1;
        int n4 = level.random.nextInt(n3 + 1);
        return new BlockPos(n, n4, n2);
    }

    public static boolean isValidEmptySpawnBlock(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, FluidState fluidState, EntityType<?> entityType) {
        if (blockState.isCollisionShapeFullBlock(blockGetter, blockPos)) {
            return false;
        }
        if (blockState.isSignalSource()) {
            return false;
        }
        if (!fluidState.isEmpty()) {
            return false;
        }
        if (blockState.is(BlockTags.PREVENT_MOB_SPAWNING_INSIDE)) {
            return false;
        }
        return !entityType.isBlockDangerous(blockState);
    }

    public static boolean isSpawnPositionOk(SpawnPlacements.Type type, LevelReader levelReader, BlockPos blockPos, @Nullable EntityType<?> entityType) {
        if (type == SpawnPlacements.Type.NO_RESTRICTIONS) {
            return true;
        }
        if (entityType == null || !levelReader.getWorldBorder().isWithinBounds(blockPos)) {
            return false;
        }
        BlockState blockState = levelReader.getBlockState(blockPos);
        FluidState fluidState = levelReader.getFluidState(blockPos);
        BlockPos blockPos2 = blockPos.above();
        BlockPos blockPos3 = blockPos.below();
        switch (type) {
            case IN_WATER: {
                return fluidState.is(FluidTags.WATER) && levelReader.getFluidState(blockPos3).is(FluidTags.WATER) && !levelReader.getBlockState(blockPos2).isRedstoneConductor(levelReader, blockPos2);
            }
            case IN_LAVA: {
                return fluidState.is(FluidTags.LAVA);
            }
        }
        BlockState blockState2 = levelReader.getBlockState(blockPos3);
        if (!blockState2.isValidSpawn(levelReader, blockPos3, entityType)) {
            return false;
        }
        return NaturalSpawner.isValidEmptySpawnBlock(levelReader, blockPos, blockState, fluidState, entityType) && NaturalSpawner.isValidEmptySpawnBlock(levelReader, blockPos2, levelReader.getBlockState(blockPos2), levelReader.getFluidState(blockPos2), entityType);
    }

    public static void spawnMobsForChunkGeneration(ServerLevelAccessor serverLevelAccessor, Biome biome, int n, int n2, Random random) {
        MobSpawnSettings mobSpawnSettings = biome.getMobSettings();
        List<MobSpawnSettings.SpawnerData> list = mobSpawnSettings.getMobs(MobCategory.CREATURE);
        if (list.isEmpty()) {
            return;
        }
        int n3 = n << 4;
        int n4 = n2 << 4;
        while (random.nextFloat() < mobSpawnSettings.getCreatureProbability()) {
            MobSpawnSettings.SpawnerData spawnerData = WeighedRandom.getRandomItem(random, list);
            int n5 = spawnerData.minCount + random.nextInt(1 + spawnerData.maxCount - spawnerData.minCount);
            SpawnGroupData spawnGroupData = null;
            int n6 = n3 + random.nextInt(16);
            int n7 = n4 + random.nextInt(16);
            int n8 = n6;
            int n9 = n7;
            for (int i = 0; i < n5; ++i) {
                boolean bl = false;
                for (int j = 0; !bl && j < 4; ++j) {
                    BlockPos blockPos = NaturalSpawner.getTopNonCollidingPos(serverLevelAccessor, spawnerData.type, n6, n7);
                    if (spawnerData.type.canSummon() && NaturalSpawner.isSpawnPositionOk(SpawnPlacements.getPlacementType(spawnerData.type), serverLevelAccessor, blockPos, spawnerData.type)) {
                        Object obj;
                        Mob mob;
                        float f = spawnerData.type.getWidth();
                        double d = Mth.clamp((double)n6, (double)n3 + (double)f, (double)n3 + 16.0 - (double)f);
                        double d2 = Mth.clamp((double)n7, (double)n4 + (double)f, (double)n4 + 16.0 - (double)f);
                        if (!serverLevelAccessor.noCollision(spawnerData.type.getAABB(d, blockPos.getY(), d2)) || !SpawnPlacements.checkSpawnRules(spawnerData.type, serverLevelAccessor, MobSpawnType.CHUNK_GENERATION, new BlockPos(d, (double)blockPos.getY(), d2), serverLevelAccessor.getRandom())) continue;
                        try {
                            obj = spawnerData.type.create(serverLevelAccessor.getLevel());
                        }
                        catch (Exception exception) {
                            LOGGER.warn("Failed to create mob", (Throwable)exception);
                            continue;
                        }
                        ((Entity)obj).moveTo(d, blockPos.getY(), d2, random.nextFloat() * 360.0f, 0.0f);
                        if (obj instanceof Mob && (mob = (Mob)obj).checkSpawnRules(serverLevelAccessor, MobSpawnType.CHUNK_GENERATION) && mob.checkSpawnObstruction(serverLevelAccessor)) {
                            spawnGroupData = mob.finalizeSpawn(serverLevelAccessor, serverLevelAccessor.getCurrentDifficultyAt(mob.blockPosition()), MobSpawnType.CHUNK_GENERATION, spawnGroupData, null);
                            serverLevelAccessor.addFreshEntityWithPassengers(mob);
                            bl = true;
                        }
                    }
                    n6 += random.nextInt(5) - random.nextInt(5);
                    n7 += random.nextInt(5) - random.nextInt(5);
                    while (n6 < n3 || n6 >= n3 + 16 || n7 < n4 || n7 >= n4 + 16) {
                        n6 = n8 + random.nextInt(5) - random.nextInt(5);
                        n7 = n9 + random.nextInt(5) - random.nextInt(5);
                    }
                }
            }
        }
    }

    private static BlockPos getTopNonCollidingPos(LevelReader levelReader, EntityType<?> entityType, int n, int n2) {
        Vec3i vec3i;
        int n3 = levelReader.getHeight(SpawnPlacements.getHeightmapType(entityType), n, n2);
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos(n, n3, n2);
        if (levelReader.dimensionType().hasCeiling()) {
            do {
                mutableBlockPos.move(Direction.DOWN);
            } while (!levelReader.getBlockState(mutableBlockPos).isAir());
            do {
                mutableBlockPos.move(Direction.DOWN);
            } while (levelReader.getBlockState(mutableBlockPos).isAir() && mutableBlockPos.getY() > 0);
        }
        if (SpawnPlacements.getPlacementType(entityType) == SpawnPlacements.Type.ON_GROUND && levelReader.getBlockState((BlockPos)(vec3i = mutableBlockPos.below())).isPathfindable(levelReader, (BlockPos)vec3i, PathComputationType.LAND)) {
            return vec3i;
        }
        return mutableBlockPos.immutable();
    }

    private static /* synthetic */ void lambda$createState$2(BlockPos blockPos, Entity entity, PotentialCalculator potentialCalculator, Object2IntOpenHashMap object2IntOpenHashMap, MobCategory mobCategory, LevelChunk levelChunk) {
        MobSpawnSettings.MobSpawnCost mobSpawnCost = NaturalSpawner.getRoughBiome(blockPos, levelChunk).getMobSettings().getMobSpawnCost(entity.getType());
        if (mobSpawnCost != null) {
            potentialCalculator.addCharge(entity.blockPosition(), mobSpawnCost.getCharge());
        }
        object2IntOpenHashMap.addTo((Object)mobCategory, 1);
    }

    @FunctionalInterface
    public static interface ChunkGetter {
        public void query(long var1, Consumer<LevelChunk> var3);
    }

    @FunctionalInterface
    public static interface AfterSpawnCallback {
        public void run(Mob var1, ChunkAccess var2);
    }

    @FunctionalInterface
    public static interface SpawnPredicate {
        public boolean test(EntityType<?> var1, BlockPos var2, ChunkAccess var3);
    }

    public static class SpawnState {
        private final int spawnableChunkCount;
        private final Object2IntOpenHashMap<MobCategory> mobCategoryCounts;
        private final PotentialCalculator spawnPotential;
        private final Object2IntMap<MobCategory> unmodifiableMobCategoryCounts;
        @Nullable
        private BlockPos lastCheckedPos;
        @Nullable
        private EntityType<?> lastCheckedType;
        private double lastCharge;

        private SpawnState(int n, Object2IntOpenHashMap<MobCategory> object2IntOpenHashMap, PotentialCalculator potentialCalculator) {
            this.spawnableChunkCount = n;
            this.mobCategoryCounts = object2IntOpenHashMap;
            this.spawnPotential = potentialCalculator;
            this.unmodifiableMobCategoryCounts = Object2IntMaps.unmodifiable(object2IntOpenHashMap);
        }

        private boolean canSpawn(EntityType<?> entityType, BlockPos blockPos, ChunkAccess chunkAccess) {
            double d;
            this.lastCheckedPos = blockPos;
            this.lastCheckedType = entityType;
            MobSpawnSettings.MobSpawnCost mobSpawnCost = NaturalSpawner.getRoughBiome(blockPos, chunkAccess).getMobSettings().getMobSpawnCost(entityType);
            if (mobSpawnCost == null) {
                this.lastCharge = 0.0;
                return true;
            }
            this.lastCharge = d = mobSpawnCost.getCharge();
            double d2 = this.spawnPotential.getPotentialEnergyChange(blockPos, d);
            return d2 <= mobSpawnCost.getEnergyBudget();
        }

        private void afterSpawn(Mob mob, ChunkAccess chunkAccess) {
            MobSpawnSettings.MobSpawnCost mobSpawnCost;
            EntityType<?> entityType = mob.getType();
            BlockPos blockPos = mob.blockPosition();
            double d = blockPos.equals(this.lastCheckedPos) && entityType == this.lastCheckedType ? this.lastCharge : ((mobSpawnCost = NaturalSpawner.getRoughBiome(blockPos, chunkAccess).getMobSettings().getMobSpawnCost(entityType)) != null ? mobSpawnCost.getCharge() : 0.0);
            this.spawnPotential.addCharge(blockPos, d);
            this.mobCategoryCounts.addTo((Object)entityType.getCategory(), 1);
        }

        public int getSpawnableChunkCount() {
            return this.spawnableChunkCount;
        }

        public Object2IntMap<MobCategory> getMobCategoryCounts() {
            return this.unmodifiableMobCategoryCounts;
        }

        private boolean canSpawnForCategory(MobCategory mobCategory) {
            int n = mobCategory.getMaxInstancesPerChunk() * this.spawnableChunkCount / MAGIC_NUMBER;
            return this.mobCategoryCounts.getInt((Object)mobCategory) < n;
        }
    }

}

