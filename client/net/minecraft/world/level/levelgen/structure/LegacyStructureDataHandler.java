/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Maps
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongArrayList
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.StructureFeatureIndexSavedData;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

public class LegacyStructureDataHandler {
    private static final Map<String, String> CURRENT_TO_LEGACY_MAP = Util.make(Maps.newHashMap(), hashMap -> {
        hashMap.put("Village", "Village");
        hashMap.put("Mineshaft", "Mineshaft");
        hashMap.put("Mansion", "Mansion");
        hashMap.put("Igloo", "Temple");
        hashMap.put("Desert_Pyramid", "Temple");
        hashMap.put("Jungle_Pyramid", "Temple");
        hashMap.put("Swamp_Hut", "Temple");
        hashMap.put("Stronghold", "Stronghold");
        hashMap.put("Monument", "Monument");
        hashMap.put("Fortress", "Fortress");
        hashMap.put("EndCity", "EndCity");
    });
    private static final Map<String, String> LEGACY_TO_CURRENT_MAP = Util.make(Maps.newHashMap(), hashMap -> {
        hashMap.put("Iglu", "Igloo");
        hashMap.put("TeDP", "Desert_Pyramid");
        hashMap.put("TeJP", "Jungle_Pyramid");
        hashMap.put("TeSH", "Swamp_Hut");
    });
    private final boolean hasLegacyData;
    private final Map<String, Long2ObjectMap<CompoundTag>> dataMap = Maps.newHashMap();
    private final Map<String, StructureFeatureIndexSavedData> indexMap = Maps.newHashMap();
    private final List<String> legacyKeys;
    private final List<String> currentKeys;

    public LegacyStructureDataHandler(@Nullable DimensionDataStorage dimensionDataStorage, List<String> list, List<String> list2) {
        this.legacyKeys = list;
        this.currentKeys = list2;
        this.populateCaches(dimensionDataStorage);
        boolean bl = false;
        for (String string : this.currentKeys) {
            bl |= this.dataMap.get(string) != null;
        }
        this.hasLegacyData = bl;
    }

    public void removeIndex(long l) {
        for (String string : this.legacyKeys) {
            StructureFeatureIndexSavedData structureFeatureIndexSavedData = this.indexMap.get(string);
            if (structureFeatureIndexSavedData == null || !structureFeatureIndexSavedData.hasUnhandledIndex(l)) continue;
            structureFeatureIndexSavedData.removeIndex(l);
            structureFeatureIndexSavedData.setDirty();
        }
    }

    public CompoundTag updateFromLegacy(CompoundTag compoundTag) {
        CompoundTag compoundTag2 = compoundTag.getCompound("Level");
        ChunkPos chunkPos = new ChunkPos(compoundTag2.getInt("xPos"), compoundTag2.getInt("zPos"));
        if (this.isUnhandledStructureStart(chunkPos.x, chunkPos.z)) {
            compoundTag = this.updateStructureStart(compoundTag, chunkPos);
        }
        CompoundTag compoundTag3 = compoundTag2.getCompound("Structures");
        CompoundTag compoundTag4 = compoundTag3.getCompound("References");
        for (String string : this.currentKeys) {
            StructureFeature structureFeature = (StructureFeature)StructureFeature.STRUCTURES_REGISTRY.get((Object)string.toLowerCase(Locale.ROOT));
            if (compoundTag4.contains(string, 12) || structureFeature == null) continue;
            int n = 8;
            LongArrayList longArrayList = new LongArrayList();
            for (int i = chunkPos.x - 8; i <= chunkPos.x + 8; ++i) {
                for (int j = chunkPos.z - 8; j <= chunkPos.z + 8; ++j) {
                    if (!this.hasLegacyStart(i, j, string)) continue;
                    longArrayList.add(ChunkPos.asLong(i, j));
                }
            }
            compoundTag4.putLongArray(string, (List<Long>)longArrayList);
        }
        compoundTag3.put("References", compoundTag4);
        compoundTag2.put("Structures", compoundTag3);
        compoundTag.put("Level", compoundTag2);
        return compoundTag;
    }

    private boolean hasLegacyStart(int n, int n2, String string) {
        if (!this.hasLegacyData) {
            return false;
        }
        return this.dataMap.get(string) != null && this.indexMap.get(CURRENT_TO_LEGACY_MAP.get(string)).hasStartIndex(ChunkPos.asLong(n, n2));
    }

    private boolean isUnhandledStructureStart(int n, int n2) {
        if (!this.hasLegacyData) {
            return false;
        }
        for (String string : this.currentKeys) {
            if (this.dataMap.get(string) == null || !this.indexMap.get(CURRENT_TO_LEGACY_MAP.get(string)).hasUnhandledIndex(ChunkPos.asLong(n, n2))) continue;
            return true;
        }
        return false;
    }

    private CompoundTag updateStructureStart(CompoundTag compoundTag, ChunkPos chunkPos) {
        CompoundTag compoundTag2 = compoundTag.getCompound("Level");
        CompoundTag compoundTag3 = compoundTag2.getCompound("Structures");
        CompoundTag compoundTag4 = compoundTag3.getCompound("Starts");
        for (String string : this.currentKeys) {
            CompoundTag compoundTag5;
            Long2ObjectMap<CompoundTag> long2ObjectMap = this.dataMap.get(string);
            if (long2ObjectMap == null) continue;
            long l = chunkPos.toLong();
            if (!this.indexMap.get(CURRENT_TO_LEGACY_MAP.get(string)).hasUnhandledIndex(l) || (compoundTag5 = (CompoundTag)long2ObjectMap.get(l)) == null) continue;
            compoundTag4.put(string, compoundTag5);
        }
        compoundTag3.put("Starts", compoundTag4);
        compoundTag2.put("Structures", compoundTag3);
        compoundTag.put("Level", compoundTag2);
        return compoundTag;
    }

    private void populateCaches(@Nullable DimensionDataStorage dimensionDataStorage) {
        if (dimensionDataStorage == null) {
            return;
        }
        for (String string2 : this.legacyKeys) {
            Tag tag;
            Object object;
            CompoundTag compoundTag = new CompoundTag();
            try {
                compoundTag = dimensionDataStorage.readTagFromDisk(string2, 1493).getCompound("data").getCompound("Features");
                if (compoundTag.isEmpty()) {
                    continue;
                }
            }
            catch (IOException iOException) {
                // empty catch block
            }
            for (String string3 : compoundTag.getAllKeys()) {
                String string4;
                String string5;
                object = compoundTag.getCompound(string3);
                long l = ChunkPos.asLong(((CompoundTag)object).getInt("ChunkX"), ((CompoundTag)object).getInt("ChunkZ"));
                tag = ((CompoundTag)object).getList("Children", 10);
                if (!((ListTag)tag).isEmpty() && (string4 = LEGACY_TO_CURRENT_MAP.get(string5 = ((ListTag)tag).getCompound(0).getString("id"))) != null) {
                    ((CompoundTag)object).putString("id", string4);
                }
                string5 = ((CompoundTag)object).getString("id");
                this.dataMap.computeIfAbsent(string5, string -> new Long2ObjectOpenHashMap()).put(l, object);
            }
            String string5 = string2 + "_index";
            StructureFeatureIndexSavedData object3 = dimensionDataStorage.computeIfAbsent(() -> new StructureFeatureIndexSavedData(string5), string5);
            if (object3.getAll().isEmpty()) {
                object = new StructureFeatureIndexSavedData(string5);
                this.indexMap.put(string2, (StructureFeatureIndexSavedData)object);
                for (String string6 : compoundTag.getAllKeys()) {
                    tag = compoundTag.getCompound(string6);
                    ((StructureFeatureIndexSavedData)object).addIndex(ChunkPos.asLong(((CompoundTag)tag).getInt("ChunkX"), ((CompoundTag)tag).getInt("ChunkZ")));
                }
                ((SavedData)object).setDirty();
                continue;
            }
            this.indexMap.put(string2, object3);
        }
    }

    public static LegacyStructureDataHandler getLegacyStructureHandler(ResourceKey<Level> resourceKey, @Nullable DimensionDataStorage dimensionDataStorage) {
        if (resourceKey == Level.OVERWORLD) {
            return new LegacyStructureDataHandler(dimensionDataStorage, (List<String>)ImmutableList.of((Object)"Monument", (Object)"Stronghold", (Object)"Village", (Object)"Mineshaft", (Object)"Temple", (Object)"Mansion"), (List<String>)ImmutableList.of((Object)"Village", (Object)"Mineshaft", (Object)"Mansion", (Object)"Igloo", (Object)"Desert_Pyramid", (Object)"Jungle_Pyramid", (Object)"Swamp_Hut", (Object)"Stronghold", (Object)"Monument"));
        }
        if (resourceKey == Level.NETHER) {
            ImmutableList immutableList = ImmutableList.of((Object)"Fortress");
            return new LegacyStructureDataHandler(dimensionDataStorage, (List<String>)immutableList, (List<String>)immutableList);
        }
        if (resourceKey == Level.END) {
            ImmutableList immutableList = ImmutableList.of((Object)"EndCity");
            return new LegacyStructureDataHandler(dimensionDataStorage, (List<String>)immutableList, (List<String>)immutableList);
        }
        throw new RuntimeException(String.format("Unknown dimension type : %s", resourceKey));
    }
}

