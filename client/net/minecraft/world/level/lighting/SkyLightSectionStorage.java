/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 */
package net.minecraft.world.level.lighting;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Arrays;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.DataLayerStorageMap;
import net.minecraft.world.level.lighting.FlatDataLayer;
import net.minecraft.world.level.lighting.LayerLightEngine;
import net.minecraft.world.level.lighting.LayerLightSectionStorage;

public class SkyLightSectionStorage
extends LayerLightSectionStorage<SkyDataLayerStorageMap> {
    private static final Direction[] HORIZONTALS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
    private final LongSet sectionsWithSources = new LongOpenHashSet();
    private final LongSet sectionsToAddSourcesTo = new LongOpenHashSet();
    private final LongSet sectionsToRemoveSourcesFrom = new LongOpenHashSet();
    private final LongSet columnsWithSkySources = new LongOpenHashSet();
    private volatile boolean hasSourceInconsistencies;

    protected SkyLightSectionStorage(LightChunkGetter lightChunkGetter) {
        super(LightLayer.SKY, lightChunkGetter, new SkyDataLayerStorageMap((Long2ObjectOpenHashMap<DataLayer>)new Long2ObjectOpenHashMap(), new Long2IntOpenHashMap(), Integer.MAX_VALUE));
    }

    @Override
    protected int getLightValue(long l) {
        long l2 = SectionPos.blockToSection(l);
        int n = SectionPos.y(l2);
        SkyDataLayerStorageMap skyDataLayerStorageMap = (SkyDataLayerStorageMap)this.visibleSectionData;
        int n2 = skyDataLayerStorageMap.topSections.get(SectionPos.getZeroNode(l2));
        if (n2 == skyDataLayerStorageMap.currentLowestY || n >= n2) {
            return 15;
        }
        DataLayer dataLayer = this.getDataLayer(skyDataLayerStorageMap, l2);
        if (dataLayer == null) {
            l = BlockPos.getFlatIndex(l);
            while (dataLayer == null) {
                l2 = SectionPos.offset(l2, Direction.UP);
                if (++n >= n2) {
                    return 15;
                }
                l = BlockPos.offset(l, 0, 16, 0);
                dataLayer = this.getDataLayer(skyDataLayerStorageMap, l2);
            }
        }
        return dataLayer.get(SectionPos.sectionRelative(BlockPos.getX(l)), SectionPos.sectionRelative(BlockPos.getY(l)), SectionPos.sectionRelative(BlockPos.getZ(l)));
    }

    @Override
    protected void onNodeAdded(long l) {
        int n = SectionPos.y(l);
        if (((SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY > n) {
            ((SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY = n;
            ((SkyDataLayerStorageMap)this.updatingSectionData).topSections.defaultReturnValue(((SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY);
        }
        long l2 = SectionPos.getZeroNode(l);
        int n2 = ((SkyDataLayerStorageMap)this.updatingSectionData).topSections.get(l2);
        if (n2 < n + 1) {
            ((SkyDataLayerStorageMap)this.updatingSectionData).topSections.put(l2, n + 1);
            if (this.columnsWithSkySources.contains(l2)) {
                this.queueAddSource(l);
                if (n2 > ((SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY) {
                    long l3 = SectionPos.asLong(SectionPos.x(l), n2 - 1, SectionPos.z(l));
                    this.queueRemoveSource(l3);
                }
                this.recheckInconsistencyFlag();
            }
        }
    }

    private void queueRemoveSource(long l) {
        this.sectionsToRemoveSourcesFrom.add(l);
        this.sectionsToAddSourcesTo.remove(l);
    }

    private void queueAddSource(long l) {
        this.sectionsToAddSourcesTo.add(l);
        this.sectionsToRemoveSourcesFrom.remove(l);
    }

    private void recheckInconsistencyFlag() {
        this.hasSourceInconsistencies = !this.sectionsToAddSourcesTo.isEmpty() || !this.sectionsToRemoveSourcesFrom.isEmpty();
    }

    @Override
    protected void onNodeRemoved(long l) {
        long l2 = SectionPos.getZeroNode(l);
        boolean bl = this.columnsWithSkySources.contains(l2);
        if (bl) {
            this.queueRemoveSource(l);
        }
        int n = SectionPos.y(l);
        if (((SkyDataLayerStorageMap)this.updatingSectionData).topSections.get(l2) == n + 1) {
            long l3 = l;
            while (!this.storingLightForSection(l3) && this.hasSectionsBelow(n)) {
                --n;
                l3 = SectionPos.offset(l3, Direction.DOWN);
            }
            if (this.storingLightForSection(l3)) {
                ((SkyDataLayerStorageMap)this.updatingSectionData).topSections.put(l2, n + 1);
                if (bl) {
                    this.queueAddSource(l3);
                }
            } else {
                ((SkyDataLayerStorageMap)this.updatingSectionData).topSections.remove(l2);
            }
        }
        if (bl) {
            this.recheckInconsistencyFlag();
        }
    }

    @Override
    protected void enableLightSources(long l, boolean bl) {
        this.runAllUpdates();
        if (bl && this.columnsWithSkySources.add(l)) {
            int n = ((SkyDataLayerStorageMap)this.updatingSectionData).topSections.get(l);
            if (n != ((SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY) {
                long l2 = SectionPos.asLong(SectionPos.x(l), n - 1, SectionPos.z(l));
                this.queueAddSource(l2);
                this.recheckInconsistencyFlag();
            }
        } else if (!bl) {
            this.columnsWithSkySources.remove(l);
        }
    }

    @Override
    protected boolean hasInconsistencies() {
        return super.hasInconsistencies() || this.hasSourceInconsistencies;
    }

    @Override
    protected DataLayer createDataLayer(long l) {
        DataLayer dataLayer;
        DataLayer dataLayer2 = (DataLayer)this.queuedSections.get(l);
        if (dataLayer2 != null) {
            return dataLayer2;
        }
        long l2 = SectionPos.offset(l, Direction.UP);
        int n = ((SkyDataLayerStorageMap)this.updatingSectionData).topSections.get(SectionPos.getZeroNode(l));
        if (n == ((SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY || SectionPos.y(l2) >= n) {
            return new DataLayer();
        }
        while ((dataLayer = this.getDataLayer(l2, true)) == null) {
            l2 = SectionPos.offset(l2, Direction.UP);
        }
        return new DataLayer(new FlatDataLayer(dataLayer, 0).getData());
    }

    @Override
    protected void markNewInconsistencies(LayerLightEngine<SkyDataLayerStorageMap, ?> layerLightEngine, boolean bl, boolean bl2) {
        long l;
        LongIterator longIterator;
        int n;
        int n2;
        super.markNewInconsistencies(layerLightEngine, bl, bl2);
        if (!bl) {
            return;
        }
        if (!this.sectionsToAddSourcesTo.isEmpty()) {
            longIterator = this.sectionsToAddSourcesTo.iterator();
            while (longIterator.hasNext()) {
                int n3;
                l = (Long)longIterator.next();
                n = this.getLevel(l);
                if (n == 2 || this.sectionsToRemoveSourcesFrom.contains(l) || !this.sectionsWithSources.add(l)) continue;
                if (n == 1) {
                    long l2;
                    this.clearQueuedSectionBlocks(layerLightEngine, l);
                    if (this.changedSections.add(l)) {
                        ((SkyDataLayerStorageMap)this.updatingSectionData).copyDataLayer(l);
                    }
                    Arrays.fill(this.getDataLayer(l, true).getData(), (byte)-1);
                    n2 = SectionPos.sectionToBlockCoord(SectionPos.x(l));
                    n3 = SectionPos.sectionToBlockCoord(SectionPos.y(l));
                    int n4 = SectionPos.sectionToBlockCoord(SectionPos.z(l));
                    Direction[] arrdirection = HORIZONTALS;
                    int n5 = arrdirection.length;
                    for (int i = 0; i < n5; ++i) {
                        Direction direction = arrdirection[i];
                        l2 = SectionPos.offset(l, direction);
                        if (!this.sectionsToRemoveSourcesFrom.contains(l2) && (this.sectionsWithSources.contains(l2) || this.sectionsToAddSourcesTo.contains(l2)) || !this.storingLightForSection(l2)) continue;
                        for (int j = 0; j < 16; ++j) {
                            for (int k = 0; k < 16; ++k) {
                                long l3;
                                long l4;
                                switch (direction) {
                                    case NORTH: {
                                        l3 = BlockPos.asLong(n2 + j, n3 + k, n4);
                                        l4 = BlockPos.asLong(n2 + j, n3 + k, n4 - 1);
                                        break;
                                    }
                                    case SOUTH: {
                                        l3 = BlockPos.asLong(n2 + j, n3 + k, n4 + 16 - 1);
                                        l4 = BlockPos.asLong(n2 + j, n3 + k, n4 + 16);
                                        break;
                                    }
                                    case WEST: {
                                        l3 = BlockPos.asLong(n2, n3 + j, n4 + k);
                                        l4 = BlockPos.asLong(n2 - 1, n3 + j, n4 + k);
                                        break;
                                    }
                                    default: {
                                        l3 = BlockPos.asLong(n2 + 16 - 1, n3 + j, n4 + k);
                                        l4 = BlockPos.asLong(n2 + 16, n3 + j, n4 + k);
                                    }
                                }
                                layerLightEngine.checkEdge(l3, l4, layerLightEngine.computeLevelFromNeighbor(l3, l4, 0), true);
                            }
                        }
                    }
                    for (int i = 0; i < 16; ++i) {
                        for (n5 = 0; n5 < 16; ++n5) {
                            long l5 = BlockPos.asLong(SectionPos.sectionToBlockCoord(SectionPos.x(l)) + i, SectionPos.sectionToBlockCoord(SectionPos.y(l)), SectionPos.sectionToBlockCoord(SectionPos.z(l)) + n5);
                            l2 = BlockPos.asLong(SectionPos.sectionToBlockCoord(SectionPos.x(l)) + i, SectionPos.sectionToBlockCoord(SectionPos.y(l)) - 1, SectionPos.sectionToBlockCoord(SectionPos.z(l)) + n5);
                            layerLightEngine.checkEdge(l5, l2, layerLightEngine.computeLevelFromNeighbor(l5, l2, 0), true);
                        }
                    }
                    continue;
                }
                for (n2 = 0; n2 < 16; ++n2) {
                    for (n3 = 0; n3 < 16; ++n3) {
                        long l6 = BlockPos.asLong(SectionPos.sectionToBlockCoord(SectionPos.x(l)) + n2, SectionPos.sectionToBlockCoord(SectionPos.y(l)) + 16 - 1, SectionPos.sectionToBlockCoord(SectionPos.z(l)) + n3);
                        layerLightEngine.checkEdge(Long.MAX_VALUE, l6, 0, true);
                    }
                }
            }
        }
        this.sectionsToAddSourcesTo.clear();
        if (!this.sectionsToRemoveSourcesFrom.isEmpty()) {
            longIterator = this.sectionsToRemoveSourcesFrom.iterator();
            while (longIterator.hasNext()) {
                l = (Long)longIterator.next();
                if (!this.sectionsWithSources.remove(l) || !this.storingLightForSection(l)) continue;
                for (n = 0; n < 16; ++n) {
                    for (n2 = 0; n2 < 16; ++n2) {
                        long l7 = BlockPos.asLong(SectionPos.sectionToBlockCoord(SectionPos.x(l)) + n, SectionPos.sectionToBlockCoord(SectionPos.y(l)) + 16 - 1, SectionPos.sectionToBlockCoord(SectionPos.z(l)) + n2);
                        layerLightEngine.checkEdge(Long.MAX_VALUE, l7, 15, false);
                    }
                }
            }
        }
        this.sectionsToRemoveSourcesFrom.clear();
        this.hasSourceInconsistencies = false;
    }

    protected boolean hasSectionsBelow(int n) {
        return n >= ((SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY;
    }

    protected boolean hasLightSource(long l) {
        int n = BlockPos.getY(l);
        if ((n & 0xF) != 15) {
            return false;
        }
        long l2 = SectionPos.blockToSection(l);
        long l3 = SectionPos.getZeroNode(l2);
        if (!this.columnsWithSkySources.contains(l3)) {
            return false;
        }
        int n2 = ((SkyDataLayerStorageMap)this.updatingSectionData).topSections.get(l3);
        return SectionPos.sectionToBlockCoord(n2) == n + 16;
    }

    protected boolean isAboveData(long l) {
        long l2 = SectionPos.getZeroNode(l);
        int n = ((SkyDataLayerStorageMap)this.updatingSectionData).topSections.get(l2);
        return n == ((SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY || SectionPos.y(l) >= n;
    }

    protected boolean lightOnInSection(long l) {
        long l2 = SectionPos.getZeroNode(l);
        return this.columnsWithSkySources.contains(l2);
    }

    public static final class SkyDataLayerStorageMap
    extends DataLayerStorageMap<SkyDataLayerStorageMap> {
        private int currentLowestY;
        private final Long2IntOpenHashMap topSections;

        public SkyDataLayerStorageMap(Long2ObjectOpenHashMap<DataLayer> long2ObjectOpenHashMap, Long2IntOpenHashMap long2IntOpenHashMap, int n) {
            super(long2ObjectOpenHashMap);
            this.topSections = long2IntOpenHashMap;
            long2IntOpenHashMap.defaultReturnValue(n);
            this.currentLowestY = n;
        }

        @Override
        public SkyDataLayerStorageMap copy() {
            return new SkyDataLayerStorageMap((Long2ObjectOpenHashMap<DataLayer>)this.map.clone(), this.topSections.clone(), this.currentLowestY);
        }

        @Override
        public /* synthetic */ DataLayerStorageMap copy() {
            return this.copy();
        }
    }

}

