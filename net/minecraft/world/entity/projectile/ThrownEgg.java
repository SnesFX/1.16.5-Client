/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.projectile;

import java.util.Random;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class ThrownEgg
extends ThrowableItemProjectile {
    public ThrownEgg(EntityType<? extends ThrownEgg> entityType, Level level) {
        super(entityType, level);
    }

    public ThrownEgg(Level level, LivingEntity livingEntity) {
        super(EntityType.EGG, livingEntity, level);
    }

    public ThrownEgg(Level level, double d, double d2, double d3) {
        super(EntityType.EGG, d, d2, d3, level);
    }

    @Override
    public void handleEntityEvent(byte by) {
        if (by == 3) {
            double d = 0.08;
            for (int i = 0; i < 8; ++i) {
                this.level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.getItem()), this.getX(), this.getY(), this.getZ(), ((double)this.random.nextFloat() - 0.5) * 0.08, ((double)this.random.nextFloat() - 0.5) * 0.08, ((double)this.random.nextFloat() - 0.5) * 0.08);
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        entityHitResult.getEntity().hurt(DamageSource.thrown(this, this.getOwner()), 0.0f);
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level.isClientSide) {
            if (this.random.nextInt(8) == 0) {
                int n = 1;
                if (this.random.nextInt(32) == 0) {
                    n = 4;
                }
                for (int i = 0; i < n; ++i) {
                    Chicken chicken = EntityType.CHICKEN.create(this.level);
                    chicken.setAge(-24000);
                    chicken.moveTo(this.getX(), this.getY(), this.getZ(), this.yRot, 0.0f);
                    this.level.addFreshEntity(chicken);
                }
            }
            this.level.broadcastEntityEvent(this, (byte)3);
            this.remove();
        }
    }

    @Override
    protected Item getDefaultItem() {
        return Items.EGG;
    }
}

