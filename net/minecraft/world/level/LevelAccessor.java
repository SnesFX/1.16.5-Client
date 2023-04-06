/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.CommonLevelAccessor;
import net.minecraft.world.level.LevelTimeAccess;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.LevelData;

public interface LevelAccessor
extends CommonLevelAccessor,
LevelTimeAccess {
    @Override
    default public long dayTime() {
        return this.getLevelData().getDayTime();
    }

    public TickList<Block> getBlockTicks();

    public TickList<Fluid> getLiquidTicks();

    public LevelData getLevelData();

    public DifficultyInstance getCurrentDifficultyAt(BlockPos var1);

    default public Difficulty getDifficulty() {
        return this.getLevelData().getDifficulty();
    }

    public ChunkSource getChunkSource();

    @Override
    default public boolean hasChunk(int n, int n2) {
        return this.getChunkSource().hasChunk(n, n2);
    }

    public Random getRandom();

    default public void blockUpdated(BlockPos blockPos, Block block) {
    }

    public void playSound(@Nullable Player var1, BlockPos var2, SoundEvent var3, SoundSource var4, float var5, float var6);

    public void addParticle(ParticleOptions var1, double var2, double var4, double var6, double var8, double var10, double var12);

    public void levelEvent(@Nullable Player var1, int var2, BlockPos var3, int var4);

    default public int getHeight() {
        return this.dimensionType().logicalHeight();
    }

    default public void levelEvent(int n, BlockPos blockPos, int n2) {
        this.levelEvent(null, n, blockPos, n2);
    }
}

