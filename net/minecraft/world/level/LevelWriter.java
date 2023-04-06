/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;

public interface LevelWriter {
    public boolean setBlock(BlockPos var1, BlockState var2, int var3, int var4);

    default public boolean setBlock(BlockPos blockPos, BlockState blockState, int n) {
        return this.setBlock(blockPos, blockState, n, 512);
    }

    public boolean removeBlock(BlockPos var1, boolean var2);

    default public boolean destroyBlock(BlockPos blockPos, boolean bl) {
        return this.destroyBlock(blockPos, bl, null);
    }

    default public boolean destroyBlock(BlockPos blockPos, boolean bl, @Nullable Entity entity) {
        return this.destroyBlock(blockPos, bl, entity, 512);
    }

    public boolean destroyBlock(BlockPos var1, boolean var2, @Nullable Entity var3, int var4);

    default public boolean addFreshEntity(Entity entity) {
        return false;
    }
}

