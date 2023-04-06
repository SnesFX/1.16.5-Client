/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class SmallFireball
extends Fireball {
    public SmallFireball(EntityType<? extends SmallFireball> entityType, Level level) {
        super(entityType, level);
    }

    public SmallFireball(Level level, LivingEntity livingEntity, double d, double d2, double d3) {
        super(EntityType.SMALL_FIREBALL, livingEntity, d, d2, d3, level);
    }

    public SmallFireball(Level level, double d, double d2, double d3, double d4, double d5, double d6) {
        super(EntityType.SMALL_FIREBALL, d, d2, d3, d4, d5, d6, level);
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        if (this.level.isClientSide) {
            return;
        }
        Entity entity = entityHitResult.getEntity();
        if (!entity.fireImmune()) {
            Entity entity2 = this.getOwner();
            int n = entity.getRemainingFireTicks();
            entity.setSecondsOnFire(5);
            boolean bl = entity.hurt(DamageSource.fireball(this, entity2), 5.0f);
            if (!bl) {
                entity.setRemainingFireTicks(n);
            } else if (entity2 instanceof LivingEntity) {
                this.doEnchantDamageEffects((LivingEntity)entity2, entity);
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        BlockPos blockPos;
        BlockHitResult blockHitResult2;
        super.onHitBlock(blockHitResult);
        if (this.level.isClientSide) {
            return;
        }
        Entity entity = this.getOwner();
        if ((entity == null || !(entity instanceof Mob) || this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) && this.level.isEmptyBlock(blockPos = (blockHitResult2 = blockHitResult).getBlockPos().relative(blockHitResult2.getDirection()))) {
            this.level.setBlockAndUpdate(blockPos, BaseFireBlock.getState(this.level, blockPos));
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level.isClientSide) {
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
}

