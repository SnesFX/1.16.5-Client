/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.lighting;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.BlockLightEngine;
import net.minecraft.world.level.lighting.LayerLightEngine;
import net.minecraft.world.level.lighting.LayerLightEventListener;
import net.minecraft.world.level.lighting.LightEventListener;
import net.minecraft.world.level.lighting.SkyLightEngine;

public class LevelLightEngine
implements LightEventListener {
    @Nullable
    private final LayerLightEngine<?, ?> blockEngine;
    @Nullable
    private final LayerLightEngine<?, ?> skyEngine;

    public LevelLightEngine(LightChunkGetter lightChunkGetter, boolean bl, boolean bl2) {
        this.blockEngine = bl ? new BlockLightEngine(lightChunkGetter) : null;
        this.skyEngine = bl2 ? new SkyLightEngine(lightChunkGetter) : null;
    }

    public void checkBlock(BlockPos blockPos) {
        if (this.blockEngine != null) {
            this.blockEngine.checkBlock(blockPos);
        }
        if (this.skyEngine != null) {
            this.skyEngine.checkBlock(blockPos);
        }
    }

    public void onBlockEmissionIncrease(BlockPos blockPos, int n) {
        if (this.blockEngine != null) {
            this.blockEngine.onBlockEmissionIncrease(blockPos, n);
        }
    }

    public boolean hasLightWork() {
        if (this.skyEngine != null && this.skyEngine.hasLightWork()) {
            return true;
        }
        return this.blockEngine != null && this.blockEngine.hasLightWork();
    }

    public int runUpdates(int n, boolean bl, boolean bl2) {
        if (this.blockEngine != null && this.skyEngine != null) {
            int n2 = n / 2;
            int n3 = this.blockEngine.runUpdates(n2, bl, bl2);
            int n4 = n - n2 + n3;
            int n5 = this.skyEngine.runUpdates(n4, bl, bl2);
            if (n3 == 0 && n5 > 0) {
                return this.blockEngine.runUpdates(n5, bl, bl2);
            }
            return n5;
        }
        if (this.blockEngine != null) {
            return this.blockEngine.runUpdates(n, bl, bl2);
        }
        if (this.skyEngine != null) {
            return this.skyEngine.runUpdates(n, bl, bl2);
        }
        return n;
    }

    @Override
    public void updateSectionStatus(SectionPos sectionPos, boolean bl) {
        if (this.blockEngine != null) {
            this.blockEngine.updateSectionStatus(sectionPos, bl);
        }
        if (this.skyEngine != null) {
            this.skyEngine.updateSectionStatus(sectionPos, bl);
        }
    }

    public void enableLightSources(ChunkPos chunkPos, boolean bl) {
        if (this.blockEngine != null) {
            this.blockEngine.enableLightSources(chunkPos, bl);
        }
        if (this.skyEngine != null) {
            this.skyEngine.enableLightSources(chunkPos, bl);
        }
    }

    public LayerLightEventListener getLayerListener(LightLayer lightLayer) {
        if (lightLayer == LightLayer.BLOCK) {
            if (this.blockEngine == null) {
                return LayerLightEventListener.DummyLightLayerEventListener.INSTANCE;
            }
            return this.blockEngine;
        }
        if (this.skyEngine == null) {
            return LayerLightEventListener.DummyLightLayerEventListener.INSTANCE;
        }
        return this.skyEngine;
    }

    public String getDebugData(LightLayer lightLayer, SectionPos sectionPos) {
        if (lightLayer == LightLayer.BLOCK) {
            if (this.blockEngine != null) {
                return this.blockEngine.getDebugData(sectionPos.asLong());
            }
        } else if (this.skyEngine != null) {
            return this.skyEngine.getDebugData(sectionPos.asLong());
        }
        return "n/a";
    }

    public void queueSectionData(LightLayer lightLayer, SectionPos sectionPos, @Nullable DataLayer dataLayer, boolean bl) {
        if (lightLayer == LightLayer.BLOCK) {
            if (this.blockEngine != null) {
                this.blockEngine.queueSectionData(sectionPos.asLong(), dataLayer, bl);
            }
        } else if (this.skyEngine != null) {
            this.skyEngine.queueSectionData(sectionPos.asLong(), dataLayer, bl);
        }
    }

    public void retainData(ChunkPos chunkPos, boolean bl) {
        if (this.blockEngine != null) {
            this.blockEngine.retainData(chunkPos, bl);
        }
        if (this.skyEngine != null) {
            this.skyEngine.retainData(chunkPos, bl);
        }
    }

    public int getRawBrightness(BlockPos blockPos, int n) {
        int n2 = this.skyEngine == null ? 0 : this.skyEngine.getLightValue(blockPos) - n;
        int n3 = this.blockEngine == null ? 0 : this.blockEngine.getLightValue(blockPos);
        return Math.max(n3, n2);
    }
}

