/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMaps
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  it.unimi.dsi.fastutil.objects.ObjectSet
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.lighting;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.function.LongPredicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.SectionTracker;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.DataLayerStorageMap;
import net.minecraft.world.level.lighting.LayerLightEngine;

public abstract class LayerLightSectionStorage<M extends DataLayerStorageMap<M>>
extends SectionTracker {
    protected static final DataLayer EMPTY_DATA = new DataLayer();
    private static final Direction[] DIRECTIONS = Direction.values();
    private final LightLayer layer;
    private final LightChunkGetter chunkSource;
    protected final LongSet dataSectionSet = new LongOpenHashSet();
    protected final LongSet toMarkNoData = new LongOpenHashSet();
    protected final LongSet toMarkData = new LongOpenHashSet();
    protected volatile M visibleSectionData;
    protected final M updatingSectionData;
    protected final LongSet changedSections = new LongOpenHashSet();
    protected final LongSet sectionsAffectedByLightUpdates = new LongOpenHashSet();
    protected final Long2ObjectMap<DataLayer> queuedSections = Long2ObjectMaps.synchronize((Long2ObjectMap)new Long2ObjectOpenHashMap());
    private final LongSet untrustedSections = new LongOpenHashSet();
    private final LongSet columnsToRetainQueuedDataFor = new LongOpenHashSet();
    private final LongSet toRemove = new LongOpenHashSet();
    protected volatile boolean hasToRemove;

    protected LayerLightSectionStorage(LightLayer lightLayer, LightChunkGetter lightChunkGetter, M m) {
        super(3, 16, 256);
        this.layer = lightLayer;
        this.chunkSource = lightChunkGetter;
        this.updatingSectionData = m;
        this.visibleSectionData = ((DataLayerStorageMap)m).copy();
        ((DataLayerStorageMap)this.visibleSectionData).disableCache();
    }

    protected boolean storingLightForSection(long l) {
        return this.getDataLayer(l, true) != null;
    }

    @Nullable
    protected DataLayer getDataLayer(long l, boolean bl) {
        return this.getDataLayer(bl ? this.updatingSectionData : this.visibleSectionData, l);
    }

    @Nullable
    protected DataLayer getDataLayer(M m, long l) {
        return ((DataLayerStorageMap)m).getLayer(l);
    }

    @Nullable
    public DataLayer getDataLayerData(long l) {
        DataLayer dataLayer = (DataLayer)this.queuedSections.get(l);
        if (dataLayer != null) {
            return dataLayer;
        }
        return this.getDataLayer(l, false);
    }

    protected abstract int getLightValue(long var1);

    protected int getStoredLevel(long l) {
        long l2 = SectionPos.blockToSection(l);
        DataLayer dataLayer = this.getDataLayer(l2, true);
        return dataLayer.get(SectionPos.sectionRelative(BlockPos.getX(l)), SectionPos.sectionRelative(BlockPos.getY(l)), SectionPos.sectionRelative(BlockPos.getZ(l)));
    }

    protected void setStoredLevel(long l, int n) {
        long l2 = SectionPos.blockToSection(l);
        if (this.changedSections.add(l2)) {
            ((DataLayerStorageMap)this.updatingSectionData).copyDataLayer(l2);
        }
        DataLayer dataLayer = this.getDataLayer(l2, true);
        dataLayer.set(SectionPos.sectionRelative(BlockPos.getX(l)), SectionPos.sectionRelative(BlockPos.getY(l)), SectionPos.sectionRelative(BlockPos.getZ(l)), n);
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                for (int k = -1; k <= 1; ++k) {
                    this.sectionsAffectedByLightUpdates.add(SectionPos.blockToSection(BlockPos.offset(l, j, k, i)));
                }
            }
        }
    }

    @Override
    protected int getLevel(long l) {
        if (l == Long.MAX_VALUE) {
            return 2;
        }
        if (this.dataSectionSet.contains(l)) {
            return 0;
        }
        if (!this.toRemove.contains(l) && ((DataLayerStorageMap)this.updatingSectionData).hasLayer(l)) {
            return 1;
        }
        return 2;
    }

    @Override
    protected int getLevelFromSource(long l) {
        if (this.toMarkNoData.contains(l)) {
            return 2;
        }
        if (this.dataSectionSet.contains(l) || this.toMarkData.contains(l)) {
            return 0;
        }
        return 2;
    }

    @Override
    protected void setLevel(long l, int n) {
        int n2 = this.getLevel(l);
        if (n2 != 0 && n == 0) {
            this.dataSectionSet.add(l);
            this.toMarkData.remove(l);
        }
        if (n2 == 0 && n != 0) {
            this.dataSectionSet.remove(l);
            this.toMarkNoData.remove(l);
        }
        if (n2 >= 2 && n != 2) {
            if (this.toRemove.contains(l)) {
                this.toRemove.remove(l);
            } else {
                ((DataLayerStorageMap)this.updatingSectionData).setLayer(l, this.createDataLayer(l));
                this.changedSections.add(l);
                this.onNodeAdded(l);
                for (int i = -1; i <= 1; ++i) {
                    for (int j = -1; j <= 1; ++j) {
                        for (int k = -1; k <= 1; ++k) {
                            this.sectionsAffectedByLightUpdates.add(SectionPos.blockToSection(BlockPos.offset(l, j, k, i)));
                        }
                    }
                }
            }
        }
        if (n2 != 2 && n >= 2) {
            this.toRemove.add(l);
        }
        this.hasToRemove = !this.toRemove.isEmpty();
    }

    protected DataLayer createDataLayer(long l) {
        DataLayer dataLayer = (DataLayer)this.queuedSections.get(l);
        if (dataLayer != null) {
            return dataLayer;
        }
        return new DataLayer();
    }

    protected void clearQueuedSectionBlocks(LayerLightEngine<?, ?> layerLightEngine, long l) {
        if (layerLightEngine.getQueueSize() < 8192) {
            layerLightEngine.removeIf(l2 -> SectionPos.blockToSection(l2) == l);
            return;
        }
        int n = SectionPos.sectionToBlockCoord(SectionPos.x(l));
        int n2 = SectionPos.sectionToBlockCoord(SectionPos.y(l));
        int n3 = SectionPos.sectionToBlockCoord(SectionPos.z(l));
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                for (int k = 0; k < 16; ++k) {
                    long l3 = BlockPos.asLong(n + i, n2 + j, n3 + k);
                    layerLightEngine.removeFromQueue(l3);
                }
            }
        }
    }

    protected boolean hasInconsistencies() {
        return this.hasToRemove;
    }

    protected void markNewInconsistencies(LayerLightEngine<M, ?> layerLightEngine, boolean bl, boolean bl2) {
        long l;
        long l2;
        DataLayer dataLayer;
        if (!this.hasInconsistencies() && this.queuedSections.isEmpty()) {
            return;
        }
        LongIterator longIterator = this.toRemove.iterator();
        while (longIterator.hasNext()) {
            l2 = (Long)longIterator.next();
            this.clearQueuedSectionBlocks(layerLightEngine, l2);
            DataLayer dataLayer2 = (DataLayer)this.queuedSections.remove(l2);
            dataLayer = ((DataLayerStorageMap)this.updatingSectionData).removeLayer(l2);
            if (!this.columnsToRetainQueuedDataFor.contains(SectionPos.getZeroNode(l2))) continue;
            if (dataLayer2 != null) {
                this.queuedSections.put(l2, (Object)dataLayer2);
                continue;
            }
            if (dataLayer == null) continue;
            this.queuedSections.put(l2, (Object)dataLayer);
        }
        ((DataLayerStorageMap)this.updatingSectionData).clearCache();
        longIterator = this.toRemove.iterator();
        while (longIterator.hasNext()) {
            l2 = (Long)longIterator.next();
            this.onNodeRemoved(l2);
        }
        this.toRemove.clear();
        this.hasToRemove = false;
        for (Long2ObjectMap.Entry entry : this.queuedSections.long2ObjectEntrySet()) {
            l = entry.getLongKey();
            if (!this.storingLightForSection(l)) continue;
            dataLayer = (DataLayer)entry.getValue();
            if (((DataLayerStorageMap)this.updatingSectionData).getLayer(l) == dataLayer) continue;
            this.clearQueuedSectionBlocks(layerLightEngine, l);
            ((DataLayerStorageMap)this.updatingSectionData).setLayer(l, dataLayer);
            this.changedSections.add(l);
        }
        ((DataLayerStorageMap)this.updatingSectionData).clearCache();
        if (!bl2) {
            longIterator = this.queuedSections.keySet().iterator();
            while (longIterator.hasNext()) {
                l2 = (Long)longIterator.next();
                this.checkEdgesForSection(layerLightEngine, l2);
            }
        } else {
            longIterator = this.untrustedSections.iterator();
            while (longIterator.hasNext()) {
                l2 = (Long)longIterator.next();
                this.checkEdgesForSection(layerLightEngine, l2);
            }
        }
        this.untrustedSections.clear();
        longIterator = this.queuedSections.long2ObjectEntrySet().iterator();
        while (longIterator.hasNext()) {
            Long2ObjectMap.Entry entry = (Long2ObjectMap.Entry)longIterator.next();
            l = entry.getLongKey();
            if (!this.storingLightForSection(l)) continue;
            longIterator.remove();
        }
    }

    private void checkEdgesForSection(LayerLightEngine<M, ?> layerLightEngine, long l) {
        if (!this.storingLightForSection(l)) {
            return;
        }
        int n = SectionPos.sectionToBlockCoord(SectionPos.x(l));
        int n2 = SectionPos.sectionToBlockCoord(SectionPos.y(l));
        int n3 = SectionPos.sectionToBlockCoord(SectionPos.z(l));
        for (Direction direction : DIRECTIONS) {
            long l2 = SectionPos.offset(l, direction);
            if (this.queuedSections.containsKey(l2) || !this.storingLightForSection(l2)) continue;
            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    long l3;
                    long l4;
                    switch (direction) {
                        case DOWN: {
                            l3 = BlockPos.asLong(n + j, n2, n3 + i);
                            l4 = BlockPos.asLong(n + j, n2 - 1, n3 + i);
                            break;
                        }
                        case UP: {
                            l3 = BlockPos.asLong(n + j, n2 + 16 - 1, n3 + i);
                            l4 = BlockPos.asLong(n + j, n2 + 16, n3 + i);
                            break;
                        }
                        case NORTH: {
                            l3 = BlockPos.asLong(n + i, n2 + j, n3);
                            l4 = BlockPos.asLong(n + i, n2 + j, n3 - 1);
                            break;
                        }
                        case SOUTH: {
                            l3 = BlockPos.asLong(n + i, n2 + j, n3 + 16 - 1);
                            l4 = BlockPos.asLong(n + i, n2 + j, n3 + 16);
                            break;
                        }
                        case WEST: {
                            l3 = BlockPos.asLong(n, n2 + i, n3 + j);
                            l4 = BlockPos.asLong(n - 1, n2 + i, n3 + j);
                            break;
                        }
                        default: {
                            l3 = BlockPos.asLong(n + 16 - 1, n2 + i, n3 + j);
                            l4 = BlockPos.asLong(n + 16, n2 + i, n3 + j);
                        }
                    }
                    layerLightEngine.checkEdge(l3, l4, layerLightEngine.computeLevelFromNeighbor(l3, l4, layerLightEngine.getLevel(l3)), false);
                    layerLightEngine.checkEdge(l4, l3, layerLightEngine.computeLevelFromNeighbor(l4, l3, layerLightEngine.getLevel(l4)), false);
                }
            }
        }
    }

    protected void onNodeAdded(long l) {
    }

    protected void onNodeRemoved(long l) {
    }

    protected void enableLightSources(long l, boolean bl) {
    }

    public void retainData(long l, boolean bl) {
        if (bl) {
            this.columnsToRetainQueuedDataFor.add(l);
        } else {
            this.columnsToRetainQueuedDataFor.remove(l);
        }
    }

    protected void queueSectionData(long l, @Nullable DataLayer dataLayer, boolean bl) {
        if (dataLayer != null) {
            this.queuedSections.put(l, (Object)dataLayer);
            if (!bl) {
                this.untrustedSections.add(l);
            }
        } else {
            this.queuedSections.remove(l);
        }
    }

    protected void updateSectionStatus(long l, boolean bl) {
        boolean bl2 = this.dataSectionSet.contains(l);
        if (!bl2 && !bl) {
            this.toMarkData.add(l);
            this.checkEdge(Long.MAX_VALUE, l, 0, true);
        }
        if (bl2 && bl) {
            this.toMarkNoData.add(l);
            this.checkEdge(Long.MAX_VALUE, l, 2, false);
        }
    }

    protected void runAllUpdates() {
        if (this.hasWork()) {
            this.runUpdates(Integer.MAX_VALUE);
        }
    }

    protected void swapSectionMap() {
        Object object;
        if (!this.changedSections.isEmpty()) {
            object = ((DataLayerStorageMap)this.updatingSectionData).copy();
            ((DataLayerStorageMap)object).disableCache();
            this.visibleSectionData = object;
            this.changedSections.clear();
        }
        if (!this.sectionsAffectedByLightUpdates.isEmpty()) {
            object = this.sectionsAffectedByLightUpdates.iterator();
            while (object.hasNext()) {
                long l = object.nextLong();
                this.chunkSource.onLightUpdate(this.layer, SectionPos.of(l));
            }
            this.sectionsAffectedByLightUpdates.clear();
        }
    }

}

