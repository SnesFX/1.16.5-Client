/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class WitherSkull
extends AbstractHurtingProjectile {
    private static final EntityDataAccessor<Boolean> DATA_DANGEROUS = SynchedEntityData.defineId(WitherSkull.class, EntityDataSerializers.BOOLEAN);

    public WitherSkull(EntityType<? extends WitherSkull> entityType, Level level) {
        super(entityType, level);
    }

    public WitherSkull(Level level, LivingEntity livingEntity, double d, double d2, double d3) {
        super(EntityType.WITHER_SKULL, livingEntity, d, d2, d3, level);
    }

    public WitherSkull(Level level, double d, double d2, double d3, double d4, double d5, double d6) {
        super(EntityType.WITHER_SKULL, d, d2, d3, d4, d5, d6, level);
    }

    @Override
    protected float getInertia() {
        return this.isDangerous() ? 0.73f : super.getInertia();
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public float getBlockExplosionResistance(Explosion explosion, BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, FluidState fluidState, float f) {
        if (this.isDangerous() && WitherBoss.canDestroy(blockState)) {
            return Math.min(0.8f, f);
        }
        return f;
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        boolean bl;
        super.onHitEntity(entityHitResult);
        if (this.level.isClientSide) {
            return;
        }
        Entity entity = entityHitResult.getEntity();
        Entity entity2 = this.getOwner();
        if (entity2 instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity2;
            bl = entity.hurt(DamageSource.witherSkull(this, livingEntity), 8.0f);
            if (bl) {
                if (entity.isAlive()) {
                    this.doEnchantDamageEffects(livingEntity, entity);
                } else {
                    livingEntity.heal(5.0f);
                }
            }
        } else {
            bl = entity.hurt(DamageSource.MAGIC, 5.0f);
        }
        if (bl && entity instanceof LivingEntity) {
            int n = 0;
            if (this.level.getDifficulty() == Difficulty.NORMAL) {
                n = 10;
            } else if (this.level.getDifficulty() == Difficulty.HARD) {
                n = 40;
            }
            if (n > 0) {
                ((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.WITHER, 20 * n, 1));
            }
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level.isClientSide) {
            Explosion.BlockInteraction blockInteraction = this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE;
            this.level.explode(this, this.getX(), this.getY(), this.getZ(), 1.0f, false, blockInteraction);
            this.remove();
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        return false;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_DANGEROUS, false);
    }

    public boolean isDangerous() {
        return this.entityData.get(DATA_DANGEROUS);
    }

    public void setDangerous(boolean bl) {
        this.entityData.set(DATA_DANGEROUS, bl);
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }
}

