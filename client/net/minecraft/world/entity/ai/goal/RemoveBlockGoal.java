/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.ai.goal;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.phys.Vec3;

public class RemoveBlockGoal
extends MoveToBlockGoal {
    private final Block blockToRemove;
    private final Mob removerMob;
    private int ticksSinceReachedGoal;

    public RemoveBlockGoal(Block block, PathfinderMob pathfinderMob, double d, int n) {
        super(pathfinderMob, d, 24, n);
        this.blockToRemove = block;
        this.removerMob = pathfinderMob;
    }

    @Override
    public boolean canUse() {
        if (!this.removerMob.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            return false;
        }
        if (this.nextStartTick > 0) {
            --this.nextStartTick;
            return false;
        }
        if (this.tryFindBlock()) {
            this.nextStartTick = 20;
            return true;
        }
        this.nextStartTick = this.nextStartTick(this.mob);
        return false;
    }

    private boolean tryFindBlock() {
        if (this.blockPos != null && this.isValidTarget(this.mob.level, this.blockPos)) {
            return true;
        }
        return this.findNearestBlock();
    }

    @Override
    public void stop() {
        super.stop();
        this.removerMob.fallDistance = 1.0f;
    }

    @Override
    public void start() {
        super.start();
        this.ticksSinceReachedGoal = 0;
    }

    public void playDestroyProgressSound(LevelAccessor levelAccessor, BlockPos blockPos) {
    }

    public void playBreakSound(Level level, BlockPos blockPos) {
    }

    @Override
    public void tick() {
        super.tick();
        Level level = this.removerMob.level;
        BlockPos blockPos = this.removerMob.blockPosition();
        BlockPos blockPos2 = this.getPosWithBlock(blockPos, level);
        Random random = this.removerMob.getRandom();
        if (this.isReachedTarget() && blockPos2 != null) {
            Vec3 vec3;
            double d;
            if (this.ticksSinceReachedGoal > 0) {
                vec3 = this.removerMob.getDeltaMovement();
                this.removerMob.setDeltaMovement(vec3.x, 0.3, vec3.z);
                if (!level.isClientSide) {
                    d = 0.08;
                    ((ServerLevel)level).sendParticles(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.EGG)), (double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.7, (double)blockPos2.getZ() + 0.5, 3, ((double)random.nextFloat() - 0.5) * 0.08, ((double)random.nextFloat() - 0.5) * 0.08, ((double)random.nextFloat() - 0.5) * 0.08, 0.15000000596046448);
                }
            }
            if (this.ticksSinceReachedGoal % 2 == 0) {
                vec3 = this.removerMob.getDeltaMovement();
                this.removerMob.setDeltaMovement(vec3.x, -0.3, vec3.z);
                if (this.ticksSinceReachedGoal % 6 == 0) {
                    this.playDestroyProgressSound(level, this.blockPos);
                }
            }
            if (this.ticksSinceReachedGoal > 60) {
                level.removeBlock(blockPos2, false);
                if (!level.isClientSide) {
                    for (int i = 0; i < 20; ++i) {
                        d = random.nextGaussian() * 0.02;
                        double d2 = random.nextGaussian() * 0.02;
                        double d3 = random.nextGaussian() * 0.02;
                        ((ServerLevel)level).sendParticles(ParticleTypes.POOF, (double)blockPos2.getX() + 0.5, blockPos2.getY(), (double)blockPos2.getZ() + 0.5, 1, d, d2, d3, 0.15000000596046448);
                    }
                    this.playBreakSound(level, blockPos2);
                }
            }
            ++this.ticksSinceReachedGoal;
        }
    }

    @Nullable
    private BlockPos getPosWithBlock(BlockPos blockPos, BlockGetter blockGetter) {
        BlockPos[] arrblockPos;
        if (blockGetter.getBlockState(blockPos).is(this.blockToRemove)) {
            return blockPos;
        }
        for (BlockPos blockPos2 : arrblockPos = new BlockPos[]{blockPos.below(), blockPos.west(), blockPos.east(), blockPos.north(), blockPos.south(), blockPos.below().below()}) {
            if (!blockGetter.getBlockState(blockPos2).is(this.blockToRemove)) continue;
            return blockPos2;
        }
        return null;
    }

    @Override
    protected boolean isValidTarget(LevelReader levelReader, BlockPos blockPos) {
        ChunkAccess chunkAccess = levelReader.getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4, ChunkStatus.FULL, false);
        if (chunkAccess != null) {
            return chunkAccess.getBlockState(blockPos).is(this.blockToRemove) && chunkAccess.getBlockState(blockPos.above()).isAir() && chunkAccess.getBlockState(blockPos.above(2)).isAir();
        }
        return false;
    }
}

