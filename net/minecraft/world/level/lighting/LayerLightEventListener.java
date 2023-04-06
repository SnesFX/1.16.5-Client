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
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.lighting.LightEventListener;

public interface LayerLightEventListener
extends LightEventListener {
    @Nullable
    public DataLayer getDataLayerData(SectionPos var1);

    public int getLightValue(BlockPos var1);

    public static enum DummyLightLayerEventListener implements LayerLightEventListener
    {
        INSTANCE;
        

        @Nullable
        @Override
        public DataLayer getDataLayerData(SectionPos sectionPos) {
            return null;
        }

        @Override
        public int getLightValue(BlockPos blockPos) {
            return 0;
        }

        @Override
        public void updateSectionStatus(SectionPos sectionPos, boolean bl) {
        }
    }

}

