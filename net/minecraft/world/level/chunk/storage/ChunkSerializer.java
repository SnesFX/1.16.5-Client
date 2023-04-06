/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.Maps
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.shorts.ShortList
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.level.chunk.storage;

import com.google.common.collect.BiMap;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.LongPredicate;
import java.util.function.Predicate;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.IdMap;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.WritableRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.util.ClassInstanceMultiMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ChunkTickList;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerTickList;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkBiomeContainer;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.ProtoTickList;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.lighting.LayerLightEventListener;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkSerializer {
    private static final Logger LOGGER = LogManager.getLogger();

    public static ProtoChunk read(ServerLevel serverLevel, StructureManager structureManager, PoiManager poiManager, ChunkPos chunkPos, CompoundTag compoundTag) {
        Object object3;
        ListTag listTag;
        int n;
        TickList<Block> tickList;
        Object object4;
        ChunkGenerator chunkGenerator = serverLevel.getChunkSource().getGenerator();
        BiomeSource biomeSource = chunkGenerator.getBiomeSource();
        CompoundTag compoundTag2 = compoundTag.getCompound("Level");
        ChunkPos chunkPos2 = new ChunkPos(compoundTag2.getInt("xPos"), compoundTag2.getInt("zPos"));
        if (!Objects.equals(chunkPos, chunkPos2)) {
            LOGGER.error("Chunk file at {} is in the wrong location; relocating. (Expected {}, got {})", (Object)chunkPos, (Object)chunkPos, (Object)chunkPos2);
        }
        ChunkBiomeContainer chunkBiomeContainer = new ChunkBiomeContainer(serverLevel.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY), chunkPos, biomeSource, compoundTag2.contains("Biomes", 11) ? compoundTag2.getIntArray("Biomes") : null);
        UpgradeData upgradeData = compoundTag2.contains("UpgradeData", 10) ? new UpgradeData(compoundTag2.getCompound("UpgradeData")) : UpgradeData.EMPTY;
        ProtoTickList<Block> protoTickList = new ProtoTickList<Block>(block -> block == null || block.defaultBlockState().isAir(), chunkPos, compoundTag2.getList("ToBeTicked", 9));
        ProtoTickList<Fluid> protoTickList2 = new ProtoTickList<Fluid>(fluid -> fluid == null || fluid == Fluids.EMPTY, chunkPos, compoundTag2.getList("LiquidsToBeTicked", 9));
        boolean bl = compoundTag2.getBoolean("isLightOn");
        ListTag listTag2 = compoundTag2.getList("Sections", 10);
        int n2 = 16;
        LevelChunkSection[] arrlevelChunkSection = new LevelChunkSection[16];
        boolean bl2 = serverLevel.dimensionType().hasSkyLight();
        ServerChunkCache serverChunkCache = serverLevel.getChunkSource();
        LevelLightEngine levelLightEngine = ((ChunkSource)serverChunkCache).getLightEngine();
        if (bl) {
            levelLightEngine.retainData(chunkPos, true);
        }
        for (int i = 0; i < listTag2.size(); ++i) {
            CompoundTag compoundTag3 = listTag2.getCompound(i);
            byte by = compoundTag3.getByte("Y");
            if (compoundTag3.contains("Palette", 9) && compoundTag3.contains("BlockStates", 12)) {
                object4 = new LevelChunkSection(by << 4);
                ((LevelChunkSection)object4).getStates().read(compoundTag3.getList("Palette", 10), compoundTag3.getLongArray("BlockStates"));
                ((LevelChunkSection)object4).recalcBlockCounts();
                if (!((LevelChunkSection)object4).isEmpty()) {
                    arrlevelChunkSection[by] = object4;
                }
                poiManager.checkConsistencyWithBlocks(chunkPos, (LevelChunkSection)object4);
            }
            if (!bl) continue;
            if (compoundTag3.contains("BlockLight", 7)) {
                levelLightEngine.queueSectionData(LightLayer.BLOCK, SectionPos.of(chunkPos, by), new DataLayer(compoundTag3.getByteArray("BlockLight")), true);
            }
            if (!bl2 || !compoundTag3.contains("SkyLight", 7)) continue;
            levelLightEngine.queueSectionData(LightLayer.SKY, SectionPos.of(chunkPos, by), new DataLayer(compoundTag3.getByteArray("SkyLight")), true);
        }
        long l = compoundTag2.getLong("InhabitedTime");
        ChunkStatus.ChunkType chunkType = ChunkSerializer.getChunkTypeFromTag(compoundTag);
        if (chunkType == ChunkStatus.ChunkType.LEVELCHUNK) {
            tickList = compoundTag2.contains("TileTicks", 9) ? ChunkTickList.create(compoundTag2.getList("TileTicks", 10), Registry.BLOCK::getKey, Registry.BLOCK::get) : protoTickList;
            object3 = compoundTag2.contains("LiquidTicks", 9) ? ChunkTickList.create(compoundTag2.getList("LiquidTicks", 10), Registry.FLUID::getKey, Registry.FLUID::get) : protoTickList2;
            object4 = new LevelChunk(serverLevel.getLevel(), chunkPos, chunkBiomeContainer, upgradeData, tickList, (TickList<Fluid>)object3, l, arrlevelChunkSection, levelChunk -> ChunkSerializer.postLoadChunk(compoundTag2, levelChunk));
        } else {
            tickList = new ProtoChunk(chunkPos, upgradeData, arrlevelChunkSection, protoTickList, protoTickList2);
            ((ProtoChunk)((Object)tickList)).setBiomes(chunkBiomeContainer);
            object4 = tickList;
            object4.setInhabitedTime(l);
            ((ProtoChunk)((Object)tickList)).setStatus(ChunkStatus.byName(compoundTag2.getString("Status")));
            if (object4.getStatus().isOrAfter(ChunkStatus.FEATURES)) {
                ((ProtoChunk)((Object)tickList)).setLightEngine(levelLightEngine);
            }
            if (!bl && object4.getStatus().isOrAfter(ChunkStatus.LIGHT)) {
                for (BlockPos object22 : BlockPos.betweenClosed(chunkPos.getMinBlockX(), 0, chunkPos.getMinBlockZ(), chunkPos.getMaxBlockX(), 255, chunkPos.getMaxBlockZ())) {
                    if (object4.getBlockState(object22).getLightEmission() == 0) continue;
                    ((ProtoChunk)((Object)tickList)).addLight(object22);
                }
            }
        }
        object4.setLightCorrect(bl);
        tickList = compoundTag2.getCompound("Heightmaps");
        object3 = EnumSet.noneOf(Heightmap.Types.class);
        for (Heightmap.Types types : object4.getStatus().heightmapsAfter()) {
            String string = types.getSerializationKey();
            if (((CompoundTag)((Object)tickList)).contains(string, 12)) {
                object4.setHeightmap(types, ((CompoundTag)((Object)tickList)).getLongArray(string));
                continue;
            }
            ((AbstractCollection)object3).add((Heightmap.Types)types);
        }
        Heightmap.primeHeightmaps((ChunkAccess)object4, object3);
        CompoundTag compoundTag3 = compoundTag2.getCompound("Structures");
        object4.setAllStarts(ChunkSerializer.unpackStructureStart(structureManager, compoundTag3, serverLevel.getSeed()));
        object4.setAllReferences(ChunkSerializer.unpackStructureReferences(chunkPos, compoundTag3));
        if (compoundTag2.getBoolean("shouldSave")) {
            object4.setUnsaved(true);
        }
        ListTag listTag3 = compoundTag2.getList("PostProcessing", 9);
        for (int i = 0; i < listTag3.size(); ++i) {
            listTag = listTag3.getList(i);
            for (n = 0; n < listTag.size(); ++n) {
                object4.addPackedPostProcess(listTag.getShort(n), i);
            }
        }
        if (chunkType == ChunkStatus.ChunkType.LEVELCHUNK) {
            return new ImposterProtoChunk((LevelChunk)object4);
        }
        ProtoChunk protoChunk = (ProtoChunk)object4;
        listTag = compoundTag2.getList("Entities", 10);
        for (n = 0; n < listTag.size(); ++n) {
            protoChunk.addEntity(listTag.getCompound(n));
        }
        ListTag listTag4 = compoundTag2.getList("TileEntities", 10);
        for (int i = 0; i < listTag4.size(); ++i) {
            CompoundTag compoundTag4 = listTag4.getCompound(i);
            object4.setBlockEntityNbt(compoundTag4);
        }
        ListTag listTag5 = compoundTag2.getList("Lights", 9);
        for (int i = 0; i < listTag5.size(); ++i) {
            ListTag listTag6 = listTag5.getList(i);
            for (int j = 0; j < listTag6.size(); ++j) {
                protoChunk.addLight(listTag6.getShort(j), i);
            }
        }
        CompoundTag compoundTag5 = compoundTag2.getCompound("CarvingMasks");
        for (String string : compoundTag5.getAllKeys()) {
            GenerationStep.Carving carving = GenerationStep.Carving.valueOf(string);
            protoChunk.setCarvingMask(carving, BitSet.valueOf(compoundTag5.getByteArray(string)));
        }
        return protoChunk;
    }

    public static CompoundTag write(ServerLevel serverLevel, ChunkAccess chunkAccess) {
        ChunkBiomeContainer chunkBiomeContainer;
        Object object;
        Object object2;
        Object object3;
        ChunkPos chunkPos = chunkAccess.getPos();
        CompoundTag compoundTag = new CompoundTag();
        CompoundTag compoundTag2 = new CompoundTag();
        compoundTag.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
        compoundTag.put("Level", compoundTag2);
        compoundTag2.putInt("xPos", chunkPos.x);
        compoundTag2.putInt("zPos", chunkPos.z);
        compoundTag2.putLong("LastUpdate", serverLevel.getGameTime());
        compoundTag2.putLong("InhabitedTime", chunkAccess.getInhabitedTime());
        compoundTag2.putString("Status", chunkAccess.getStatus().getName());
        UpgradeData upgradeData = chunkAccess.getUpgradeData();
        if (!upgradeData.isEmpty()) {
            compoundTag2.put("UpgradeData", upgradeData.write());
        }
        LevelChunkSection[] arrlevelChunkSection = chunkAccess.getSections();
        ListTag listTag = new ListTag();
        ThreadedLevelLightEngine threadedLevelLightEngine = serverLevel.getChunkSource().getLightEngine();
        boolean bl = chunkAccess.isLightCorrect();
        for (int i = -1; i < 17; ++i) {
            int n = i;
            object2 = Arrays.stream(arrlevelChunkSection).filter(levelChunkSection -> levelChunkSection != null && levelChunkSection.bottomBlockY() >> 4 == n).findFirst().orElse(LevelChunk.EMPTY_SECTION);
            DataLayer object5 = threadedLevelLightEngine.getLayerListener(LightLayer.BLOCK).getDataLayerData(SectionPos.of(chunkPos, n));
            object = threadedLevelLightEngine.getLayerListener(LightLayer.SKY).getDataLayerData(SectionPos.of(chunkPos, n));
            if (object2 == LevelChunk.EMPTY_SECTION && object5 == null && object == null) continue;
            object3 = new CompoundTag();
            ((CompoundTag)object3).putByte("Y", (byte)(n & 0xFF));
            if (object2 != LevelChunk.EMPTY_SECTION) {
                ((LevelChunkSection)object2).getStates().write((CompoundTag)object3, "Palette", "BlockStates");
            }
            if (object5 != null && !object5.isEmpty()) {
                ((CompoundTag)object3).putByteArray("BlockLight", object5.getData());
            }
            if (object != null && !((DataLayer)object).isEmpty()) {
                ((CompoundTag)object3).putByteArray("SkyLight", ((DataLayer)object).getData());
            }
            listTag.add(object3);
        }
        compoundTag2.put("Sections", listTag);
        if (bl) {
            compoundTag2.putBoolean("isLightOn", true);
        }
        if ((chunkBiomeContainer = chunkAccess.getBiomes()) != null) {
            compoundTag2.putIntArray("Biomes", chunkBiomeContainer.writeBiomes());
        }
        ListTag listTag2 = new ListTag();
        for (BlockPos blockPos : chunkAccess.getBlockEntitiesPos()) {
            object = chunkAccess.getBlockEntityNbtForSaving(blockPos);
            if (object == null) continue;
            listTag2.add(object);
        }
        compoundTag2.put("TileEntities", listTag2);
        object2 = new ListTag();
        if (chunkAccess.getStatus().getChunkType() == ChunkStatus.ChunkType.LEVELCHUNK) {
            LevelChunk levelChunk = (LevelChunk)chunkAccess;
            levelChunk.setLastSaveHadEntities(false);
            for (int i = 0; i < levelChunk.getEntitySections().length; ++i) {
                for (Entity entity : levelChunk.getEntitySections()[i]) {
                    CompoundTag compoundTag3;
                    if (!entity.save(compoundTag3 = new CompoundTag())) continue;
                    levelChunk.setLastSaveHadEntities(true);
                    ((AbstractList)object2).add(compoundTag3);
                }
            }
        } else {
            ProtoChunk protoChunk = (ProtoChunk)chunkAccess;
            ((AbstractCollection)object2).addAll(protoChunk.getEntities());
            compoundTag2.put("Lights", ChunkSerializer.packOffsets(protoChunk.getPackedLights()));
            object = new CompoundTag();
            for (GenerationStep.Carving carving : GenerationStep.Carving.values()) {
                BitSet bitSet = protoChunk.getCarvingMask(carving);
                if (bitSet == null) continue;
                ((CompoundTag)object).putByteArray(carving.toString(), bitSet.toByteArray());
            }
            compoundTag2.put("CarvingMasks", (Tag)object);
        }
        compoundTag2.put("Entities", (Tag)object2);
        TickList<Block> tickList = chunkAccess.getBlockTicks();
        if (tickList instanceof ProtoTickList) {
            compoundTag2.put("ToBeTicked", ((ProtoTickList)tickList).save());
        } else if (tickList instanceof ChunkTickList) {
            compoundTag2.put("TileTicks", ((ChunkTickList)tickList).save());
        } else {
            compoundTag2.put("TileTicks", ((ServerTickList)serverLevel.getBlockTicks()).save(chunkPos));
        }
        TickList<Fluid> tickList2 = chunkAccess.getLiquidTicks();
        if (tickList2 instanceof ProtoTickList) {
            compoundTag2.put("LiquidsToBeTicked", ((ProtoTickList)tickList2).save());
        } else if (tickList2 instanceof ChunkTickList) {
            compoundTag2.put("LiquidTicks", ((ChunkTickList)tickList2).save());
        } else {
            compoundTag2.put("LiquidTicks", ((ServerTickList)serverLevel.getLiquidTicks()).save(chunkPos));
        }
        compoundTag2.put("PostProcessing", ChunkSerializer.packOffsets(chunkAccess.getPostProcessing()));
        object3 = new CompoundTag();
        for (Map.Entry<Heightmap.Types, Heightmap> entry : chunkAccess.getHeightmaps()) {
            if (!chunkAccess.getStatus().heightmapsAfter().contains(entry.getKey())) continue;
            ((CompoundTag)object3).put(entry.getKey().getSerializationKey(), new LongArrayTag(entry.getValue().getRawData()));
        }
        compoundTag2.put("Heightmaps", (Tag)object3);
        compoundTag2.put("Structures", ChunkSerializer.packStructureData(chunkPos, chunkAccess.getAllStarts(), chunkAccess.getAllReferences()));
        return compoundTag;
    }

    public static ChunkStatus.ChunkType getChunkTypeFromTag(@Nullable CompoundTag compoundTag) {
        ChunkStatus chunkStatus;
        if (compoundTag != null && (chunkStatus = ChunkStatus.byName(compoundTag.getCompound("Level").getString("Status"))) != null) {
            return chunkStatus.getChunkType();
        }
        return ChunkStatus.ChunkType.PROTOCHUNK;
    }

    private static void postLoadChunk(CompoundTag compoundTag, LevelChunk levelChunk) {
        ListTag listTag = compoundTag.getList("Entities", 10);
        Level level = levelChunk.getLevel();
        for (int i = 0; i < listTag.size(); ++i) {
            CompoundTag compoundTag2 = listTag.getCompound(i);
            EntityType.loadEntityRecursive(compoundTag2, level, entity -> {
                levelChunk.addEntity((Entity)entity);
                return entity;
            });
            levelChunk.setLastSaveHadEntities(true);
        }
        ListTag listTag2 = compoundTag.getList("TileEntities", 10);
        for (int i = 0; i < listTag2.size(); ++i) {
            CompoundTag compoundTag3 = listTag2.getCompound(i);
            boolean bl = compoundTag3.getBoolean("keepPacked");
            if (bl) {
                levelChunk.setBlockEntityNbt(compoundTag3);
                continue;
            }
            BlockPos blockPos = new BlockPos(compoundTag3.getInt("x"), compoundTag3.getInt("y"), compoundTag3.getInt("z"));
            BlockEntity blockEntity = BlockEntity.loadStatic(levelChunk.getBlockState(blockPos), compoundTag3);
            if (blockEntity == null) continue;
            levelChunk.addBlockEntity(blockEntity);
        }
    }

    private static CompoundTag packStructureData(ChunkPos chunkPos, Map<StructureFeature<?>, StructureStart<?>> map, Map<StructureFeature<?>, LongSet> map2) {
        CompoundTag compoundTag = new CompoundTag();
        CompoundTag compoundTag2 = new CompoundTag();
        for (Map.Entry<StructureFeature<?>, StructureStart<?>> object : map.entrySet()) {
            compoundTag2.put(object.getKey().getFeatureName(), object.getValue().createTag(chunkPos.x, chunkPos.z));
        }
        compoundTag.put("Starts", compoundTag2);
        CompoundTag compoundTag3 = new CompoundTag();
        for (Map.Entry<StructureFeature<?>, LongSet> entry : map2.entrySet()) {
            compoundTag3.put(entry.getKey().getFeatureName(), new LongArrayTag(entry.getValue()));
        }
        compoundTag.put("References", compoundTag3);
        return compoundTag;
    }

    private static Map<StructureFeature<?>, StructureStart<?>> unpackStructureStart(StructureManager structureManager, CompoundTag compoundTag, long l) {
        HashMap hashMap = Maps.newHashMap();
        CompoundTag compoundTag2 = compoundTag.getCompound("Starts");
        for (String string : compoundTag2.getAllKeys()) {
            String string2 = string.toLowerCase(Locale.ROOT);
            StructureFeature structureFeature = (StructureFeature)StructureFeature.STRUCTURES_REGISTRY.get((Object)string2);
            if (structureFeature == null) {
                LOGGER.error("Unknown structure start: {}", (Object)string2);
                continue;
            }
            StructureStart<?> structureStart = StructureFeature.loadStaticStart(structureManager, compoundTag2.getCompound(string), l);
            if (structureStart == null) continue;
            hashMap.put(structureFeature, structureStart);
        }
        return hashMap;
    }

    private static Map<StructureFeature<?>, LongSet> unpackStructureReferences(ChunkPos chunkPos, CompoundTag compoundTag) {
        HashMap hashMap = Maps.newHashMap();
        CompoundTag compoundTag2 = compoundTag.getCompound("References");
        for (String string : compoundTag2.getAllKeys()) {
            hashMap.put(StructureFeature.STRUCTURES_REGISTRY.get((Object)string.toLowerCase(Locale.ROOT)), new LongOpenHashSet(Arrays.stream(compoundTag2.getLongArray(string)).filter(l -> {
                ChunkPos chunkPos2 = new ChunkPos(l);
                if (chunkPos2.getChessboardDistance(chunkPos) > 8) {
                    LOGGER.warn("Found invalid structure reference [ {} @ {} ] for chunk {}.", (Object)string, (Object)chunkPos2, (Object)chunkPos);
                    return false;
                }
                return true;
            }).toArray()));
        }
        return hashMap;
    }

    public static ListTag packOffsets(ShortList[] arrshortList) {
        ListTag listTag = new ListTag();
        for (ShortList shortList : arrshortList) {
            ListTag listTag2 = new ListTag();
            if (shortList != null) {
                for (Short s : shortList) {
                    listTag2.add(ShortTag.valueOf(s));
                }
            }
            listTag.add(listTag2);
        }
        return listTag;
    }
}

