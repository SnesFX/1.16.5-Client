/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.SlideDownBlockTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HoneyBlock
extends HalfTransparentBlock {
    protected static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 15.0, 15.0);

    public HoneyBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    private static boolean doesEntityDoHoneyBlockSlideEffects(Entity entity) {
        return entity instanceof LivingEntity || entity instanceof AbstractMinecart || entity instanceof PrimedTnt || entity instanceof Boat;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return SHAPE;
    }

    @Override
    public void fallOn(Level level, BlockPos blockPos, Entity entity, float f) {
        entity.playSound(SoundEvents.HONEY_BLOCK_SLIDE, 1.0f, 1.0f);
        if (!level.isClientSide) {
            level.broadcastEntityEvent(entity, (byte)54);
        }
        if (entity.causeFallDamage(f, 0.2f)) {
            entity.playSound(this.soundType.getFallSound(), this.soundType.getVolume() * 0.5f, this.soundType.getPitch() * 0.75f);
        }
    }

    @Override
    public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
        if (this.isSlidingDown(blockPos, entity)) {
            this.maybeDoSlideAchievement(entity, blockPos);
            this.doSlideMovement(entity);
            this.maybeDoSlideEffects(level, entity);
        }
        super.entityInside(blockState, level, blockPos, entity);
    }

    private boolean isSlidingDown(BlockPos blockPos, Entity entity) {
        if (entity.isOnGround()) {
            return false;
        }
        if (entity.getY() > (double)blockPos.getY() + 0.9375 - 1.0E-7) {
            return false;
        }
        if (entity.getDeltaMovement().y >= -0.08) {
            return false;
        }
        double d = Math.abs((double)blockPos.getX() + 0.5 - entity.getX());
        double d2 = Math.abs((double)blockPos.getZ() + 0.5 - entity.getZ());
        double d3 = 0.4375 + (double)(entity.getBbWidth() / 2.0f);
        return d + 1.0E-7 > d3 || d2 + 1.0E-7 > d3;
    }

    private void maybeDoSlideAchievement(Entity entity, BlockPos blockPos) {
        if (entity instanceof ServerPlayer && entity.level.getGameTime() % 20L == 0L) {
            CriteriaTriggers.HONEY_BLOCK_SLIDE.trigger((ServerPlayer)entity, entity.level.getBlockState(blockPos));
        }
    }

    private void doSlideMovement(Entity entity) {
        Vec3 vec3 = entity.getDeltaMovement();
        if (vec3.y < -0.13) {
            double d = -0.05 / vec3.y;
            entity.setDeltaMovement(new Vec3(vec3.x * d, -0.05, vec3.z * d));
        } else {
            entity.setDeltaMovement(new Vec3(vec3.x, -0.05, vec3.z));
        }
        entity.fallDistance = 0.0f;
    }

    private void maybeDoSlideEffects(Level level, Entity entity) {
        if (HoneyBlock.doesEntityDoHoneyBlockSlideEffects(entity)) {
            if (level.random.nextInt(5) == 0) {
                entity.playSound(SoundEvents.HONEY_BLOCK_SLIDE, 1.0f, 1.0f);
            }
            if (!level.isClientSide && level.random.nextInt(5) == 0) {
                level.broadcastEntityEvent(entity, (byte)53);
            }
        }
    }

    public static void showSlideParticles(Entity entity) {
        HoneyBlock.showParticles(entity, 5);
    }

    public static void showJumpParticles(Entity entity) {
        HoneyBlock.showParticles(entity, 10);
    }

    private static void showParticles(Entity entity, int n) {
        if (!entity.level.isClientSide) {
            return;
        }
        BlockState blockState = Blocks.HONEY_BLOCK.defaultBlockState();
        for (int i = 0; i < n; ++i) {
            entity.level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockState), entity.getX(), entity.getY(), entity.getZ(), 0.0, 0.0, 0.0);
        }
    }
}

