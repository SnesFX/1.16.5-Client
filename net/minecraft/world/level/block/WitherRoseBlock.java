/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WitherRoseBlock
extends FlowerBlock {
    public WitherRoseBlock(MobEffect mobEffect, BlockBehaviour.Properties properties) {
        super(mobEffect, 8, properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return super.mayPlaceOn(blockState, blockGetter, blockPos) || blockState.is(Blocks.NETHERRACK) || blockState.is(Blocks.SOUL_SAND) || blockState.is(Blocks.SOUL_SOIL);
    }

    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
        VoxelShape voxelShape = this.getShape(blockState, level, blockPos, CollisionContext.empty());
        Vec3 vec3 = voxelShape.bounds().getCenter();
        double d = (double)blockPos.getX() + vec3.x;
        double d2 = (double)blockPos.getZ() + vec3.z;
        for (int i = 0; i < 3; ++i) {
            if (!random.nextBoolean()) continue;
            level.addParticle(ParticleTypes.SMOKE, d + random.nextDouble() / 5.0, (double)blockPos.getY() + (0.5 - random.nextDouble()), d2 + random.nextDouble() / 5.0, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
        LivingEntity livingEntity;
        if (level.isClientSide || level.getDifficulty() == Difficulty.PEACEFUL) {
            return;
        }
        if (entity instanceof LivingEntity && !(livingEntity = (LivingEntity)entity).isInvulnerableTo(DamageSource.WITHER)) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.WITHER, 40));
        }
    }
}

