/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.levelgen.surfacebuilders;

import net.minecraft.world.level.block.state.BlockState;

public interface SurfaceBuilderConfiguration {
    public BlockState getTopMaterial();

    public BlockState getUnderMaterial();
}

