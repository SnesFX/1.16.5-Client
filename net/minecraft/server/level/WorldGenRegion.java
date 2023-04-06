/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.level;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenTickList;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeZoomer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.AABB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldGenRegion
implements WorldGenLevel {
    private static final Logger LOGGER = LogManager.getLogger();
    private final List<ChunkAccess> cache;
    private final int x;
    private final int z;
    private final int size;
    private final ServerLevel level;
    private final long seed;
    private final LevelData levelData;
    private final Random random;
    private final DimensionType dimensionType;
    private final TickList<Block> blockTicks = new WorldGenTickList<Block>(blockPos -> this.getChunk((BlockPos)blockPos).getBlockTicks());
    private final TickList<Fluid> liquidTicks = new WorldGenTickList<Fluid>(blockPos -> this.getChunk((BlockPos)blockPos).getLiquidTicks());
    private final BiomeManager biomeManager;
    private final ChunkPos firstPos;
    private final ChunkPos lastPos;
    private final StructureFeatureManager structureFeatureManager;

    public WorldGenRegion(ServerLevel serverLevel, List<ChunkAccess> list) {
        int n = Mth.floor(Math.sqrt(list.size()));
        if (n * n != list.size()) {
            throw Util.pauseInIde(new IllegalStateException("Cache size is not a square."));
        }
        ChunkPos chunkPos = list.get(list.size() / 2).getPos();
        this.cache = list;
        this.x = chunkPos.x;
        this.z = chunkPos.z;
        this.size = n;
        this.level = serverLevel;
        this.seed = serverLevel.getSeed();
        this.levelData = serverLevel.getLevelData();
        this.random = serverLevel.getRandom();
        this.dimensionType = serverLevel.dimensionType();
        this.biomeManager = new BiomeManager(this, BiomeManager.obfuscateSeed(this.seed), serverLevel.dimensionType().getBiomeZoomer());
        this.firstPos = list.get(0).getPos();
        this.lastPos = list.get(list.size() - 1).getPos();
        this.structureFeatureManager = serverLevel.structureFeatureManager().forWorldGenRegion(this);
    }

    public int getCenterX() {
        return this.x;
    }

    public int getCenterZ() {
        return this.z;
    }

    @Override
    public ChunkAccess getChunk(int n, int n2) {
        return this.getChunk(n, n2, ChunkStatus.EMPTY);
    }

    @Nullable
    @Override
    public ChunkAccess getChunk(int n, int n2, ChunkStatus chunkStatus, boolean bl) {
        ChunkAccess chunkAccess;
        if (this.hasChunk(n, n2)) {
            int n3 = n - this.firstPos.x;
            int n4 = n2 - this.firstPos.z;
            chunkAccess = this.cache.get(n3 + n4 * this.size);
            if (chunkAccess.getStatus().isOrAfter(chunkStatus)) {
                return chunkAccess;
            }
        } else {
            chunkAccess = null;
        }
        if (!bl) {
            return null;
        }
        LOGGER.error("Requested chunk : {} {}", (Object)n, (Object)n2);
        LOGGER.error("Region bounds : {} {} | {} {}", (Object)this.firstPos.x, (Object)this.firstPos.z, (Object)this.lastPos.x, (Object)this.lastPos.z);
        if (chunkAccess != null) {
            throw Util.pauseInIde(new RuntimeException(String.format("Chunk is not of correct status. Expecting %s, got %s | %s %s", chunkStatus, chunkAccess.getStatus(), n, n2)));
        }
        throw Util.pauseInIde(new RuntimeException(String.format("We are asking a region for a chunk out of bound | %s %s", n, n2)));
    }

    @Override
    public boolean hasChunk(int n, int n2) {
        return n >= this.firstPos.x && n <= this.lastPos.x && n2 >= this.firstPos.z && n2 <= this.lastPos.z;
    }

    @Override
    public BlockState getBlockState(BlockPos blockPos) {
        return this.getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4).getBlockState(blockPos);
    }

    @Override
    public FluidState getFluidState(BlockPos blockPos) {
        return this.getChunk(blockPos).getFluidState(blockPos);
    }

    @Nullable
    @Override
    public Player getNearestPlayer(double d, double d2, double d3, double d4, Predicate<Entity> predicate) {
        return null;
    }

    @Override
    public int getSkyDarken() {
        return 0;
    }

    @Override
    public BiomeManager getBiomeManager() {
        return this.biomeManager;
    }

    @Override
    public Biome getUncachedNoiseBiome(int n, int n2, int n3) {
        return this.level.getUncachedNoiseBiome(n, n2, n3);
    }

    @Override
    public float getShade(Direction direction, boolean bl) {
        return 1.0f;
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return this.level.getLightEngine();
    }

    @Override
    public boolean destroyBlock(BlockPos blockPos, boolean bl, @Nullable Entity entity, int n) {
        BlockState blockState = this.getBlockState(blockPos);
        if (blockState.isAir()) {
            return false;
        }
        if (bl) {
            BlockEntity blockEntity = blockState.getBlock().isEntityBlock() ? this.getBlockEntity(blockPos) : null;
            Block.dropResources(blockState, this.level, blockPos, blockEntity, entity, ItemStack.EMPTY);
        }
        return this.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3, n);
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(BlockPos blockPos) {
        ChunkAccess chunkAccess = this.getChunk(blockPos);
        BlockEntity blockEntity = chunkAccess.getBlockEntity(blockPos);
        if (blockEntity != null) {
            return blockEntity;
        }
        CompoundTag compoundTag = chunkAccess.getBlockEntityNbt(blockPos);
        BlockState blockState = chunkAccess.getBlockState(blockPos);
        if (compoundTag != null) {
            if ("DUMMY".equals(compoundTag.getString("id"))) {
                Block block = blockState.getBlock();
                if (!(block instanceof EntityBlock)) {
                    return null;
                }
                blockEntity = ((EntityBlock)((Object)block)).newBlockEntity(this.level);
            } else {
                blockEntity = BlockEntity.loadStatic(blockState, compoundTag);
            }
            if (blockEntity != null) {
                chunkAccess.setBlockEntity(blockPos, blockEntity);
                return blockEntity;
            }
        }
        if (blockState.getBlock() instanceof EntityBlock) {
            LOGGER.warn("Tried to access a block entity before it was created. {}", (Object)blockPos);
        }
        return null;
    }

    @Override
    public boolean setBlock(BlockPos blockPos, BlockState blockState, int n, int n2) {
        Block block;
        ChunkAccess chunkAccess = this.getChunk(blockPos);
        BlockState blockState2 = chunkAccess.setBlockState(blockPos, blockState, false);
        if (blockState2 != null) {
            this.level.onBlockStateChange(blockPos, blockState2, blockState);
        }
        if ((block = blockState.getBlock()).isEntityBlock()) {
            if (chunkAccess.getStatus().getChunkType() == ChunkStatus.ChunkType.LEVELCHUNK) {
                chunkAccess.setBlockEntity(blockPos, ((EntityBlock)((Object)block)).newBlockEntity(this));
            } else {
                CompoundTag compoundTag = new CompoundTag();
                compoundTag.putInt("x", blockPos.getX());
                compoundTag.putInt("y", blockPos.getY());
                compoundTag.putInt("z", blockPos.getZ());
                compoundTag.putString("id", "DUMMY");
                chunkAccess.setBlockEntityNbt(compoundTag);
            }
        } else if (blockState2 != null && blockState2.getBlock().isEntityBlock()) {
            chunkAccess.removeBlockEntity(blockPos);
        }
        if (blockState.hasPostProcess(this, blockPos)) {
            this.markPosForPostprocessing(blockPos);
        }
        return true;
    }

    private void markPosForPostprocessing(BlockPos blockPos) {
        this.getChunk(blockPos).markPosForPostprocessing(blockPos);
    }

    @Override
    public boolean addFreshEntity(Entity entity) {
        int n = Mth.floor(entity.getX() / 16.0);
        int n2 = Mth.floor(entity.getZ() / 16.0);
        this.getChunk(n, n2).addEntity(entity);
        return true;
    }

    @Override
    public boolean removeBlock(BlockPos blockPos, boolean bl) {
        return this.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
    }

    @Override
    public WorldBorder getWorldBorder() {
        return this.level.getWorldBorder();
    }

    @Override
    public boolean isClientSide() {
        return false;
    }

    @Deprecated
    @Override
    public ServerLevel getLevel() {
        return this.level;
    }

    @Override
    public RegistryAccess registryAccess() {
        return this.level.registryAccess();
    }

    @Override
    public LevelData getLevelData() {
        return this.levelData;
    }

    @Override
    public DifficultyInstance getCurrentDifficultyAt(BlockPos blockPos) {
        if (!this.hasChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4)) {
            throw new RuntimeException("We are asking a region for a chunk out of bound");
        }
        return new DifficultyInstance(this.level.getDifficulty(), this.level.getDayTime(), 0L, this.level.getMoonBrightness());
    }

    @Override
    public ChunkSource getChunkSource() {
        return this.level.getChunkSource();
    }

    @Override
    public long getSeed() {
        return this.seed;
    }

    @Override
    public TickList<Block> getBlockTicks() {
        return this.blockTicks;
    }

    @Override
    public TickList<Fluid> getLiquidTicks() {
        return this.liquidTicks;
    }

    @Override
    public int getSeaLevel() {
        return this.level.getSeaLevel();
    }

    @Override
    public Random getRandom() {
        return this.random;
    }

    @Override
    public int getHeight(Heightmap.Types types, int n, int n2) {
        return this.getChunk(n >> 4, n2 >> 4).getHeight(types, n & 0xF, n2 & 0xF) + 1;
    }

    @Override
    public void playSound(@Nullable Player player, BlockPos blockPos, SoundEvent soundEvent, SoundSource soundSource, float f, float f2) {
    }

    @Override
    public void addParticle(ParticleOptions particleOptions, double d, double d2, double d3, double d4, double d5, double d6) {
    }

    @Override
    public void levelEvent(@Nullable Player player, int n, BlockPos blockPos, int n2) {
    }

    @Override
    public DimensionType dimensionType() {
        return this.dimensionType;
    }

    @Override
    public boolean isStateAtPosition(BlockPos blockPos, Predicate<BlockState> predicate) {
        return predicate.test(this.getBlockState(blockPos));
    }

    @Override
    public <T extends Entity> List<T> getEntitiesOfClass(Class<? extends T> class_, AABB aABB, @Nullable Predicate<? super T> predicate) {
        return Collections.emptyList();
    }

    @Override
    public List<Entity> getEntities(@Nullable Entity entity, AABB aABB, @Nullable Predicate<? super Entity> predicate) {
        return Collections.emptyList();
    }

    public List<Player> players() {
        return Collections.emptyList();
    }

    @Override
    public Stream<? extends StructureStart<?>> startsForFeature(SectionPos sectionPos, StructureFeature<?> structureFeature) {
        return this.structureFeatureManager.startsForFeature(sectionPos, structureFeature);
    }
}

