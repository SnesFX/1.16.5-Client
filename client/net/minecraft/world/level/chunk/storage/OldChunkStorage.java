/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.chunk.storage;

import net.minecraft.core.IdMap;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkBiomeContainer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.OldDataLayer;

public class OldChunkStorage {
    public static OldLevelChunk load(CompoundTag compoundTag) {
        int n = compoundTag.getInt("xPos");
        int n2 = compoundTag.getInt("zPos");
        OldLevelChunk oldLevelChunk = new OldLevelChunk(n, n2);
        oldLevelChunk.blocks = compoundTag.getByteArray("Blocks");
        oldLevelChunk.data = new OldDataLayer(compoundTag.getByteArray("Data"), 7);
        oldLevelChunk.skyLight = new OldDataLayer(compoundTag.getByteArray("SkyLight"), 7);
        oldLevelChunk.blockLight = new OldDataLayer(compoundTag.getByteArray("BlockLight"), 7);
        oldLevelChunk.heightmap = compoundTag.getByteArray("HeightMap");
        oldLevelChunk.terrainPopulated = compoundTag.getBoolean("TerrainPopulated");
        oldLevelChunk.entities = compoundTag.getList("Entities", 10);
        oldLevelChunk.blockEntities = compoundTag.getList("TileEntities", 10);
        oldLevelChunk.blockTicks = compoundTag.getList("TileTicks", 10);
        try {
            oldLevelChunk.lastUpdated = compoundTag.getLong("LastUpdate");
        }
        catch (ClassCastException classCastException) {
            oldLevelChunk.lastUpdated = compoundTag.getInt("LastUpdate");
        }
        return oldLevelChunk;
    }

    public static void convertToAnvilFormat(RegistryAccess.RegistryHolder registryHolder, OldLevelChunk oldLevelChunk, CompoundTag compoundTag, BiomeSource biomeSource) {
        compoundTag.putInt("xPos", oldLevelChunk.x);
        compoundTag.putInt("zPos", oldLevelChunk.z);
        compoundTag.putLong("LastUpdate", oldLevelChunk.lastUpdated);
        int[] arrn = new int[oldLevelChunk.heightmap.length];
        for (int i = 0; i < oldLevelChunk.heightmap.length; ++i) {
            arrn[i] = oldLevelChunk.heightmap[i];
        }
        compoundTag.putIntArray("HeightMap", arrn);
        compoundTag.putBoolean("TerrainPopulated", oldLevelChunk.terrainPopulated);
        ListTag listTag = new ListTag();
        for (int i = 0; i < 8; ++i) {
            Object object;
            Object object2;
            Object object3;
            boolean bl = true;
            for (int j = 0; j < 16 && bl; ++j) {
                block3 : for (object2 = 0; object2 < 16 && bl; ++object2) {
                    for (object3 = 0; object3 < 16; ++object3) {
                        object = j << 11 | object3 << 7 | object2 + (i << 4);
                        byte by = oldLevelChunk.blocks[object];
                        if (by == 0) continue;
                        bl = false;
                        continue block3;
                    }
                }
            }
            if (bl) continue;
            byte[] arrby = new byte[4096];
            object2 = new DataLayer();
            object3 = new DataLayer();
            object = new DataLayer();
            for (int j = 0; j < 16; ++j) {
                for (int k = 0; k < 16; ++k) {
                    for (int i2 = 0; i2 < 16; ++i2) {
                        int n = j << 11 | i2 << 7 | k + (i << 4);
                        byte by = oldLevelChunk.blocks[n];
                        arrby[k << 8 | i2 << 4 | j] = (byte)(by & 0xFF);
                        ((DataLayer)object2).set(j, k, i2, oldLevelChunk.data.get(j, k + (i << 4), i2));
                        ((DataLayer)object3).set(j, k, i2, oldLevelChunk.skyLight.get(j, k + (i << 4), i2));
                        ((DataLayer)object).set(j, k, i2, oldLevelChunk.blockLight.get(j, k + (i << 4), i2));
                    }
                }
            }
            CompoundTag compoundTag2 = new CompoundTag();
            compoundTag2.putByte("Y", (byte)(i & 0xFF));
            compoundTag2.putByteArray("Blocks", arrby);
            compoundTag2.putByteArray("Data", ((DataLayer)object2).getData());
            compoundTag2.putByteArray("SkyLight", ((DataLayer)object3).getData());
            compoundTag2.putByteArray("BlockLight", ((DataLayer)object).getData());
            listTag.add(compoundTag2);
        }
        compoundTag.put("Sections", listTag);
        compoundTag.putIntArray("Biomes", new ChunkBiomeContainer(registryHolder.registryOrThrow(Registry.BIOME_REGISTRY), new ChunkPos(oldLevelChunk.x, oldLevelChunk.z), biomeSource).writeBiomes());
        compoundTag.put("Entities", oldLevelChunk.entities);
        compoundTag.put("TileEntities", oldLevelChunk.blockEntities);
        if (oldLevelChunk.blockTicks != null) {
            compoundTag.put("TileTicks", oldLevelChunk.blockTicks);
        }
        compoundTag.putBoolean("convertedFromAlphaFormat", true);
    }

    public static class OldLevelChunk {
        public long lastUpdated;
        public boolean terrainPopulated;
        public byte[] heightmap;
        public OldDataLayer blockLight;
        public OldDataLayer skyLight;
        public OldDataLayer data;
        public byte[] blocks;
        public ListTag entities;
        public ListTag blockEntities;
        public ListTag blockTicks;
        public final int x;
        public final int z;

        public OldLevelChunk(int n, int n2) {
            this.x = n;
            this.z = n2;
        }
    }

}

