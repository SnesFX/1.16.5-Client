/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.storage;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.storage.LevelData;

public interface WritableLevelData
extends LevelData {
    public void setXSpawn(int var1);

    public void setYSpawn(int var1);

    public void setZSpawn(int var1);

    public void setSpawnAngle(float var1);

    default public void setSpawn(BlockPos blockPos, float f) {
        this.setXSpawn(blockPos.getX());
        this.setYSpawn(blockPos.getY());
        this.setZSpawn(blockPos.getZ());
        this.setSpawnAngle(f);
    }
}

