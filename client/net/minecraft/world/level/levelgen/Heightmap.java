/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.serialization.Codec
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectListIterator
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.levelgen;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.util.BitStorage;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;

public class Heightmap {
    private static final Predicate<BlockState> NOT_AIR = blockState -> !blockState.isAir();
    private static final Predicate<BlockState> MATERIAL_MOTION_BLOCKING = blockState -> blockState.getMaterial().blocksMotion();
    private final BitStorage data = new BitStorage(9, 256);
    private final Predicate<BlockState> isOpaque;
    private final ChunkAccess chunk;

    public Heightmap(ChunkAccess chunkAccess, Types types) {
        this.isOpaque = types.isOpaque();
        this.chunk = chunkAccess;
    }

    public static void primeHeightmaps(ChunkAccess chunkAccess, Set<Types> set) {
        int n = set.size();
        ObjectArrayList objectArrayList = new ObjectArrayList(n);
        ObjectListIterator objectListIterator = objectArrayList.iterator();
        int n2 = chunkAccess.getHighestSectionPosition() + 16;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < 16; ++i) {
            block1 : for (int j = 0; j < 16; ++j) {
                for (Types types : set) {
                    objectArrayList.add((Object)chunkAccess.getOrCreateHeightmapUnprimed(types));
                }
                for (int k = n2 - 1; k >= 0; --k) {
                    mutableBlockPos.set(i, k, j);
                    BlockState blockState = chunkAccess.getBlockState(mutableBlockPos);
                    if (blockState.is(Blocks.AIR)) continue;
                    while (objectListIterator.hasNext()) {
                        Heightmap heightmap = (Heightmap)objectListIterator.next();
                        if (!heightmap.isOpaque.test(blockState)) continue;
                        heightmap.setHeight(i, j, k + 1);
                        objectListIterator.remove();
                    }
                    if (objectArrayList.isEmpty()) continue block1;
                    objectListIterator.back(n);
                }
            }
        }
    }

    public boolean update(int n, int n2, int n3, BlockState blockState) {
        int n4 = this.getFirstAvailable(n, n3);
        if (n2 <= n4 - 2) {
            return false;
        }
        if (this.isOpaque.test(blockState)) {
            if (n2 >= n4) {
                this.setHeight(n, n3, n2 + 1);
                return true;
            }
        } else if (n4 - 1 == n2) {
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
            for (int i = n2 - 1; i >= 0; --i) {
                mutableBlockPos.set(n, i, n3);
                if (!this.isOpaque.test(this.chunk.getBlockState(mutableBlockPos))) continue;
                this.setHeight(n, n3, i + 1);
                return true;
            }
            this.setHeight(n, n3, 0);
            return true;
        }
        return false;
    }

    public int getFirstAvailable(int n, int n2) {
        return this.getFirstAvailable(Heightmap.getIndex(n, n2));
    }

    private int getFirstAvailable(int n) {
        return this.data.get(n);
    }

    private void setHeight(int n, int n2, int n3) {
        this.data.set(Heightmap.getIndex(n, n2), n3);
    }

    public void setRawData(long[] arrl) {
        System.arraycopy(arrl, 0, this.data.getRaw(), 0, arrl.length);
    }

    public long[] getRawData() {
        return this.data.getRaw();
    }

    private static int getIndex(int n, int n2) {
        return n + n2 * 16;
    }

    static /* synthetic */ Predicate access$000() {
        return NOT_AIR;
    }

    static /* synthetic */ Predicate access$100() {
        return MATERIAL_MOTION_BLOCKING;
    }

    public static enum Types implements StringRepresentable
    {
        WORLD_SURFACE_WG("WORLD_SURFACE_WG", Usage.WORLDGEN, Heightmap.access$000()),
        WORLD_SURFACE("WORLD_SURFACE", Usage.CLIENT, Heightmap.access$000()),
        OCEAN_FLOOR_WG("OCEAN_FLOOR_WG", Usage.WORLDGEN, Heightmap.access$100()),
        OCEAN_FLOOR("OCEAN_FLOOR", Usage.LIVE_WORLD, Heightmap.access$100()),
        MOTION_BLOCKING("MOTION_BLOCKING", Usage.CLIENT, blockState -> blockState.getMaterial().blocksMotion() || !blockState.getFluidState().isEmpty()),
        MOTION_BLOCKING_NO_LEAVES("MOTION_BLOCKING_NO_LEAVES", Usage.LIVE_WORLD, blockState -> (blockState.getMaterial().blocksMotion() || !blockState.getFluidState().isEmpty()) && !(blockState.getBlock() instanceof LeavesBlock));
        
        public static final Codec<Types> CODEC;
        private final String serializationKey;
        private final Usage usage;
        private final Predicate<BlockState> isOpaque;
        private static final Map<String, Types> REVERSE_LOOKUP;

        private Types(String string2, Usage usage, Predicate<BlockState> predicate) {
            this.serializationKey = string2;
            this.usage = usage;
            this.isOpaque = predicate;
        }

        public String getSerializationKey() {
            return this.serializationKey;
        }

        public boolean sendToClient() {
            return this.usage == Usage.CLIENT;
        }

        public boolean keepAfterWorldgen() {
            return this.usage != Usage.WORLDGEN;
        }

        @Nullable
        public static Types getFromKey(String string) {
            return REVERSE_LOOKUP.get(string);
        }

        public Predicate<BlockState> isOpaque() {
            return this.isOpaque;
        }

        @Override
        public String getSerializedName() {
            return this.serializationKey;
        }

        static {
            CODEC = StringRepresentable.fromEnum(Types::values, Types::getFromKey);
            REVERSE_LOOKUP = Util.make(Maps.newHashMap(), hashMap -> {
                for (Types types : Types.values()) {
                    hashMap.put(types.serializationKey, types);
                }
            });
        }
    }

    public static enum Usage {
        WORLDGEN,
        LIVE_WORLD,
        CLIENT;
        
    }

}

