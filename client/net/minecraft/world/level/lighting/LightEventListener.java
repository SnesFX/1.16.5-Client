/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.lighting;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;

public interface LightEventListener {
    default public void updateSectionStatus(BlockPos blockPos, boolean bl) {
        this.updateSectionStatus(SectionPos.of(blockPos), bl);
    }

    public void updateSectionStatus(SectionPos var1, boolean var2);
}

