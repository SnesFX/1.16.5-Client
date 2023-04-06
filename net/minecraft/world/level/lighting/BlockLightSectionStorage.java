/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 */
package net.minecraft.world.level.lighting;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.DataLayerStorageMap;
import net.minecraft.world.level.lighting.LayerLightSectionStorage;

public class BlockLightSectionStorage
extends LayerLightSectionStorage<BlockDataLayerStorageMap> {
    protected BlockLightSectionStorage(LightChunkGetter lightChunkGetter) {
        super(LightLayer.BLOCK, lightChunkGetter, new BlockDataLayerStorageMap((Long2ObjectOpenHashMap<DataLayer>)new Long2ObjectOpenHashMap()));
    }

    @Override
    protected int getLightValue(long l) {
        long l2 = SectionPos.blockToSection(l);
        DataLayer dataLayer = this.getDataLayer(l2, false);
        if (dataLayer == null) {
            return 0;
        }
        return dataLayer.get(SectionPos.sectionRelative(BlockPos.getX(l)), SectionPos.sectionRelative(BlockPos.getY(l)), SectionPos.sectionRelative(BlockPos.getZ(l)));
    }

    public static final class BlockDataLayerStorageMap
    extends DataLayerStorageMap<BlockDataLayerStorageMap> {
        public BlockDataLayerStorageMap(Long2ObjectOpenHashMap<DataLayer> long2ObjectOpenHashMap) {
            super(long2ObjectOpenHashMap);
        }

        @Override
        public BlockDataLayerStorageMap copy() {
            return new BlockDataLayerStorageMap((Long2ObjectOpenHashMap<DataLayer>)this.map.clone());
        }

        @Override
        public /* synthetic */ DataLayerStorageMap copy() {
            return this.copy();
        }
    }

}

