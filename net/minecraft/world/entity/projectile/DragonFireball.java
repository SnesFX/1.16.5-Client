/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.projectile;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class DragonFireball
extends AbstractHurtingProjectile {
    public DragonFireball(EntityType<? extends DragonFireball> entityType, Level level) {
        super(entityType, level);
    }

    public DragonFireball(Level level, double d, double d2, double d3, double d4, double d5, double d6) {
        super(EntityType.DRAGON_FIREBALL, d, d2, d3, d4, d5, d6, level);
    }

    public DragonFireball(Level level, LivingEntity livingEntity, double d, double d2, double d3) {
        super(EntityType.DRAGON_FIREBALL, livingEntity, d, d2, d3, level);
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        Entity entity = this.getOwner();
        if (hitResult.getType() == HitResult.Type.ENTITY && ((EntityHitResult)hitResult).getEntity().is(entity)) {
            return;
        }
        if (!this.level.isClientSide) {
            List<LivingEntity> list = this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(4.0, 2.0, 4.0));
            AreaEffectCloud areaEffectCloud = new AreaEffectCloud(this.level, this.getX(), this.getY(), this.getZ());
            if (entity instanceof LivingEntity) {
                areaEffectCloud.setOwner((LivingEntity)entity);
            }
            areaEffectCloud.setParticle(ParticleTypes.DRAGON_BREATH);
            areaEffectCloud.setRadius(3.0f);
            areaEffectCloud.setDuration(600);
            areaEffectCloud.setRadiusPerTick((7.0f - areaEffectCloud.getRadius()) / (float)areaEffectCloud.getDuration());
            areaEffectCloud.addEffect(new MobEffectInstance(MobEffects.HARM, 1, 1));
            if (!list.isEmpty()) {
                for (LivingEntity livingEntity : list) {
                    double d = this.distanceToSqr(livingEntity);
                    if (!(d < 16.0)) continue;
                    areaEffectCloud.setPos(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
                    break;
                }
            }
            this.level.levelEvent(2006, this.blockPosition(), this.isSilent() ? -1 : 1);
            this.level.addFreshEntity(areaEffectCloud);
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
    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.DRAGON_BREATH;
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }
}

