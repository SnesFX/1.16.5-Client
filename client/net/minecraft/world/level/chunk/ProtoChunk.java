/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 *  it.unimi.dsi.fastutil.shorts.ShortList
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.level.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.AbstractCollection;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkBiomeContainer;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoTickList;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProtoChunk
implements ChunkAccess {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ChunkPos chunkPos;
    private volatile boolean isDirty;
    @Nullable
    private ChunkBiomeContainer biomes;
    @Nullable
    private volatile LevelLightEngine lightEngine;
    private final Map<Heightmap.Types, Heightmap> heightmaps = Maps.newEnumMap(Heightmap.Types.class);
    private volatile ChunkStatus status = ChunkStatus.EMPTY;
    private final Map<BlockPos, BlockEntity> blockEntities = Maps.newHashMap();
    private final Map<BlockPos, CompoundTag> blockEntityNbts = Maps.newHashMap();
    private final LevelChunkSection[] sections = new LevelChunkSection[16];
    private final List<CompoundTag> entities = Lists.newArrayList();
    private final List<BlockPos> lights = Lists.newArrayList();
    private final ShortList[] postProcessing = new ShortList[16];
    private final Map<StructureFeature<?>, StructureStart<?>> structureStarts = Maps.newHashMap();
    private final Map<StructureFeature<?>, LongSet> structuresRefences = Maps.newHashMap();
    private final UpgradeData upgradeData;
    private final ProtoTickList<Block> blockTicks;
    private final ProtoTickList<Fluid> liquidTicks;
    private long inhabitedTime;
    private final Map<GenerationStep.Carving, BitSet> carvingMasks = new Object2ObjectArrayMap();
    private volatile boolean isLightCorrect;

    public ProtoChunk(ChunkPos chunkPos, UpgradeData upgradeData) {
        this(chunkPos, upgradeData, null, new ProtoTickList<Block>(block -> block == null || block.defaultBlockState().isAir(), chunkPos), new ProtoTickList<Fluid>(fluid -> fluid == null || fluid == Fluids.EMPTY, chunkPos));
    }

    public ProtoChunk(ChunkPos chunkPos, UpgradeData upgradeData, @Nullable LevelChunkSection[] arrlevelChunkSection, ProtoTickList<Block> protoTickList, ProtoTickList<Fluid> protoTickList2) {
        this.chunkPos = chunkPos;
        this.upgradeData = upgradeData;
        this.blockTicks = protoTickList;
        this.liquidTicks = protoTickList2;
        if (arrlevelChunkSection != null) {
            if (this.sections.length == arrlevelChunkSection.length) {
                System.arraycopy(arrlevelChunkSection, 0, this.sections, 0, this.sections.length);
            } else {
                LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", (Object)arrlevelChunkSection.length, (Object)this.sections.length);
            }
        }
    }

    @Override
    public BlockState getBlockState(BlockPos blockPos) {
        int n = blockPos.getY();
        if (Level.isOutsideBuildHeight(n)) {
            return Blocks.VOID_AIR.defaultBlockState();
        }
        LevelChunkSection levelChunkSection = this.getSections()[n >> 4];
        if (LevelChunkSection.isEmpty(levelChunkSection)) {
            return Blocks.AIR.defaultBlockState();
        }
        return levelChunkSection.getBlockState(blockPos.getX() & 0xF, n & 0xF, blockPos.getZ() & 0xF);
    }

    @Override
    public FluidState getFluidState(BlockPos blockPos) {
        int n = blockPos.getY();
        if (Level.isOutsideBuildHeight(n)) {
            return Fluids.EMPTY.defaultFluidState();
        }
        LevelChunkSection levelChunkSection = this.getSections()[n >> 4];
        if (LevelChunkSection.isEmpty(levelChunkSection)) {
            return Fluids.EMPTY.defaultFluidState();
        }
        return levelChunkSection.getFluidState(blockPos.getX() & 0xF, n & 0xF, blockPos.getZ() & 0xF);
    }

    @Override
    public Stream<BlockPos> getLights() {
        return this.lights.stream();
    }

    public ShortList[] getPackedLights() {
        ShortList[] arrshortList = new ShortList[16];
        for (BlockPos blockPos : this.lights) {
            ChunkAccess.getOrCreateOffsetList(arrshortList, blockPos.getY() >> 4).add(ProtoChunk.packOffsetCoordinates(blockPos));
        }
        return arrshortList;
    }

    public void addLight(short s, int n) {
        this.addLight(ProtoChunk.unpackOffsetCoordinates(s, n, this.chunkPos));
    }

    public void addLight(BlockPos blockPos) {
        this.lights.add(blockPos.immutable());
    }

    @Nullable
    @Override
    public BlockState setBlockState(BlockPos blockPos, BlockState blockState, boolean bl) {
        Heightmap.Types types;
        Object object;
        int n = blockPos.getX();
        int n2 = blockPos.getY();
        int n3 = blockPos.getZ();
        if (n2 < 0 || n2 >= 256) {
            return Blocks.VOID_AIR.defaultBlockState();
        }
        if (this.sections[n2 >> 4] == LevelChunk.EMPTY_SECTION && blockState.is(Blocks.AIR)) {
            return blockState;
        }
        if (blockState.getLightEmission() > 0) {
            this.lights.add(new BlockPos((n & 0xF) + this.getPos().getMinBlockX(), n2, (n3 & 0xF) + this.getPos().getMinBlockZ()));
        }
        LevelChunkSection levelChunkSection = this.getOrCreateSection(n2 >> 4);
        BlockState blockState2 = levelChunkSection.setBlockState(n & 0xF, n2 & 0xF, n3 & 0xF, blockState);
        if (this.status.isOrAfter(ChunkStatus.FEATURES) && blockState != blockState2 && (blockState.getLightBlock(this, blockPos) != blockState2.getLightBlock(this, blockPos) || blockState.getLightEmission() != blockState2.getLightEmission() || blockState.useShapeForLightOcclusion() || blockState2.useShapeForLightOcclusion())) {
            object = this.getLightEngine();
            ((LevelLightEngine)object).checkBlock(blockPos);
        }
        object = this.getStatus().heightmapsAfter();
        EnumSet<Heightmap.Types> enumSet = null;
        Iterator iterator = ((AbstractCollection)object).iterator();
        while (iterator.hasNext()) {
            types = (Heightmap.Types)iterator.next();
            Heightmap heightmap = this.heightmaps.get(types);
            if (heightmap != null) continue;
            if (enumSet == null) {
                enumSet = EnumSet.noneOf(Heightmap.Types.class);
            }
            enumSet.add(types);
        }
        if (enumSet != null) {
            Heightmap.primeHeightmaps(this, enumSet);
        }
        iterator = ((AbstractCollection)object).iterator();
        while (iterator.hasNext()) {
            types = (Heightmap.Types)iterator.next();
            this.heightmaps.get(types).update(n & 0xF, n2, n3 & 0xF, blockState);
        }
        return blockState2;
    }

    public LevelChunkSection getOrCreateSection(int n) {
        if (this.sections[n] == LevelChunk.EMPTY_SECTION) {
            this.sections[n] = new LevelChunkSection(n << 4);
        }
        return this.sections[n];
    }

    @Override
    public void setBlockEntity(BlockPos blockPos, BlockEntity blockEntity) {
        blockEntity.setPosition(blockPos);
        this.blockEntities.put(blockPos, blockEntity);
    }

    @Override
    public Set<BlockPos> getBlockEntitiesPos() {
        HashSet hashSet = Sets.newHashSet(this.blockEntityNbts.keySet());
        hashSet.addAll(this.blockEntities.keySet());
        return hashSet;
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(BlockPos blockPos) {
        return this.blockEntities.get(blockPos);
    }

    public Map<BlockPos, BlockEntity> getBlockEntities() {
        return this.blockEntities;
    }

    public void addEntity(CompoundTag compoundTag) {
        this.entities.add(compoundTag);
    }

    @Override
    public void addEntity(Entity entity) {
        if (entity.isPassenger()) {
            return;
        }
        CompoundTag compoundTag = new CompoundTag();
        entity.save(compoundTag);
        this.addEntity(compoundTag);
    }

    public List<CompoundTag> getEntities() {
        return this.entities;
    }

    public void setBiomes(ChunkBiomeContainer chunkBiomeContainer) {
        this.biomes = chunkBiomeContainer;
    }

    @Nullable
    @Override
    public ChunkBiomeContainer getBiomes() {
        return this.biomes;
    }

    @Override
    public void setUnsaved(boolean bl) {
        this.isDirty = bl;
    }

    @Override
    public boolean isUnsaved() {
        return this.isDirty;
    }

    @Override
    public ChunkStatus getStatus() {
        return this.status;
    }

    public void setStatus(ChunkStatus chunkStatus) {
        this.status = chunkStatus;
        this.setUnsaved(true);
    }

    @Override
    public LevelChunkSection[] getSections() {
        return this.sections;
    }

    @Nullable
    public LevelLightEngine getLightEngine() {
        return this.lightEngine;
    }

    @Override
    public Collection<Map.Entry<Heightmap.Types, Heightmap>> getHeightmaps() {
        return Collections.unmodifiableSet(this.heightmaps.entrySet());
    }

    @Override
    public void setHeightmap(Heightmap.Types types, long[] arrl) {
        this.getOrCreateHeightmapUnprimed(types).setRawData(arrl);
    }

    @Override
    public Heightmap getOrCreateHeightmapUnprimed(Heightmap.Types types2) {
        return this.heightmaps.computeIfAbsent(types2, types -> new Heightmap(this, (Heightmap.Types)types));
    }

    @Override
    public int getHeight(Heightmap.Types types, int n, int n2) {
        Heightmap heightmap = this.heightmaps.get(types);
        if (heightmap == null) {
            Heightmap.primeHeightmaps(this, EnumSet.of(types));
            heightmap = this.heightmaps.get(types);
        }
        return heightmap.getFirstAvailable(n & 0xF, n2 & 0xF) - 1;
    }

    @Override
    public ChunkPos getPos() {
        return this.chunkPos;
    }

    @Override
    public void setLastSaveTime(long l) {
    }

    @Nullable
    @Override
    public StructureStart<?> getStartForFeature(StructureFeature<?> structureFeature) {
        return this.structureStarts.get(structureFeature);
    }

    @Override
    public void setStartForFeature(StructureFeature<?> structureFeature, StructureStart<?> structureStart) {
        this.structureStarts.put(structureFeature, structureStart);
        this.isDirty = true;
    }

    @Override
    public Map<StructureFeature<?>, StructureStart<?>> getAllStarts() {
        return Collections.unmodifiableMap(this.structureStarts);
    }

    @Override
    public void setAllStarts(Map<StructureFeature<?>, StructureStart<?>> map) {
        this.structureStarts.clear();
        this.structureStarts.putAll(map);
        this.isDirty = true;
    }

    @Override
    public LongSet getReferencesForFeature(StructureFeature<?> structureFeature2) {
        return this.structuresRefences.computeIfAbsent(structureFeature2, structureFeature -> new LongOpenHashSet());
    }

    @Override
    public void addReferenceForFeature(StructureFeature<?> structureFeature2, long l) {
        this.structuresRefences.computeIfAbsent(structureFeature2, structureFeature -> new LongOpenHashSet()).add(l);
        this.isDirty = true;
    }

    @Override
    public Map<StructureFeature<?>, LongSet> getAllReferences() {
        return Collections.unmodifiableMap(this.structuresRefences);
    }

    @Override
    public void setAllReferences(Map<StructureFeature<?>, LongSet> map) {
        this.structuresRefences.clear();
        this.structuresRefences.putAll(map);
        this.isDirty = true;
    }

    public static short packOffsetCoordinates(BlockPos blockPos) {
        int n = blockPos.getX();
        int n2 = blockPos.getY();
        int n3 = blockPos.getZ();
        int n4 = n & 0xF;
        int n5 = n2 & 0xF;
        int n6 = n3 & 0xF;
        return (short)(n4 | n5 << 4 | n6 << 8);
    }

    public static BlockPos unpackOffsetCoordinates(short s, int n, ChunkPos chunkPos) {
        int n2 = (s & 0xF) + (chunkPos.x << 4);
        int n3 = (s >>> 4 & 0xF) + (n << 4);
        int n4 = (s >>> 8 & 0xF) + (chunkPos.z << 4);
        return new BlockPos(n2, n3, n4);
    }

    @Override
    public void markPosForPostprocessing(BlockPos blockPos) {
        if (!Level.isOutsideBuildHeight(blockPos)) {
            ChunkAccess.getOrCreateOffsetList(this.postProcessing, blockPos.getY() >> 4).add(ProtoChunk.packOffsetCoordinates(blockPos));
        }
    }

    @Override
    public ShortList[] getPostProcessing() {
        return this.postProcessing;
    }

    @Override
    public void addPackedPostProcess(short s, int n) {
        ChunkAccess.getOrCreateOffsetList(this.postProcessing, n).add(s);
    }

    public ProtoTickList<Block> getBlockTicks() {
        return this.blockTicks;
    }

    public ProtoTickList<Fluid> getLiquidTicks() {
        return this.liquidTicks;
    }

    @Override
    public UpgradeData getUpgradeData() {
        return this.upgradeData;
    }

    @Override
    public void setInhabitedTime(long l) {
        this.inhabitedTime = l;
    }

    @Override
    public long getInhabitedTime() {
        return this.inhabitedTime;
    }

    @Override
    public void setBlockEntityNbt(CompoundTag compoundTag) {
        this.blockEntityNbts.put(new BlockPos(compoundTag.getInt("x"), compoundTag.getInt("y"), compoundTag.getInt("z")), compoundTag);
    }

    public Map<BlockPos, CompoundTag> getBlockEntityNbts() {
        return Collections.unmodifiableMap(this.blockEntityNbts);
    }

    @Override
    public CompoundTag getBlockEntityNbt(BlockPos blockPos) {
        return this.blockEntityNbts.get(blockPos);
    }

    @Nullable
    @Override
    public CompoundTag getBlockEntityNbtForSaving(BlockPos blockPos) {
        BlockEntity blockEntity = this.getBlockEntity(blockPos);
        if (blockEntity != null) {
            return blockEntity.save(new CompoundTag());
        }
        return this.blockEntityNbts.get(blockPos);
    }

    @Override
    public void removeBlockEntity(BlockPos blockPos) {
        this.blockEntities.remove(blockPos);
        this.blockEntityNbts.remove(blockPos);
    }

    @Nullable
    public BitSet getCarvingMask(GenerationStep.Carving carving) {
        return this.carvingMasks.get(carving);
    }

    public BitSet getOrCreateCarvingMask(GenerationStep.Carving carving2) {
        return this.carvingMasks.computeIfAbsent(carving2, carving -> new BitSet(65536));
    }

    public void setCarvingMask(GenerationStep.Carving carving, BitSet bitSet) {
        this.carvingMasks.put(carving, bitSet);
    }

    public void setLightEngine(LevelLightEngine levelLightEngine) {
        this.lightEngine = levelLightEngine;
    }

    @Override
    public boolean isLightCorrect() {
        return this.isLightCorrect;
    }

    @Override
    public void setLightCorrect(boolean bl) {
        this.isLightCorrect = bl;
        this.setUnsaved(true);
    }

    public /* synthetic */ TickList getLiquidTicks() {
        return this.getLiquidTicks();
    }

    public /* synthetic */ TickList getBlockTicks() {
        return this.getBlockTicks();
    }
}

