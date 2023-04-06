/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.vehicle;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

public class MinecartTNT
extends AbstractMinecart {
    private int fuse = -1;

    public MinecartTNT(EntityType<? extends MinecartTNT> entityType, Level level) {
        super(entityType, level);
    }

    public MinecartTNT(Level level, double d, double d2, double d3) {
        super(EntityType.TNT_MINECART, level, d, d2, d3);
    }

    @Override
    public AbstractMinecart.Type getMinecartType() {
        return AbstractMinecart.Type.TNT;
    }

    @Override
    public BlockState getDefaultDisplayBlockState() {
        return Blocks.TNT.defaultBlockState();
    }

    @Override
    public void tick() {
        double d;
        super.tick();
        if (this.fuse > 0) {
            --this.fuse;
            this.level.addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 0.0, 0.0, 0.0);
        } else if (this.fuse == 0) {
            this.explode(MinecartTNT.getHorizontalDistanceSqr(this.getDeltaMovement()));
        }
        if (this.horizontalCollision && (d = MinecartTNT.getHorizontalDistanceSqr(this.getDeltaMovement())) >= 0.009999999776482582) {
            this.explode(d);
        }
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        AbstractArrow abstractArrow;
        Entity entity = damageSource.getDirectEntity();
        if (entity instanceof AbstractArrow && (abstractArrow = (AbstractArrow)entity).isOnFire()) {
            this.explode(abstractArrow.getDeltaMovement().lengthSqr());
        }
        return super.hurt(damageSource, f);
    }

    @Override
    public void destroy(DamageSource damageSource) {
        double d = MinecartTNT.getHorizontalDistanceSqr(this.getDeltaMovement());
        if (damageSource.isFire() || damageSource.isExplosion() || d >= 0.009999999776482582) {
            if (this.fuse < 0) {
                this.primeFuse();
                this.fuse = this.random.nextInt(20) + this.random.nextInt(20);
            }
            return;
        }
        super.destroy(damageSource);
        if (!damageSource.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            this.spawnAtLocation(Blocks.TNT);
        }
    }

    protected void explode(double d) {
        if (!this.level.isClientSide) {
            double d2 = Math.sqrt(d);
            if (d2 > 5.0) {
                d2 = 5.0;
            }
            this.level.explode(this, this.getX(), this.getY(), this.getZ(), (float)(4.0 + this.random.nextDouble() * 1.5 * d2), Explosion.BlockInteraction.BREAK);
            this.remove();
        }
    }

    @Override
    public boolean causeFallDamage(float f, float f2) {
        if (f >= 3.0f) {
            float f3 = f / 10.0f;
            this.explode(f3 * f3);
        }
        return super.causeFallDamage(f, f2);
    }

    @Override
    public void activateMinecart(int n, int n2, int n3, boolean bl) {
        if (bl && this.fuse < 0) {
            this.primeFuse();
        }
    }

    @Override
    public void handleEntityEvent(byte by) {
        if (by == 10) {
            this.primeFuse();
        } else {
            super.handleEntityEvent(by);
        }
    }

    public void primeFuse() {
        this.fuse = 80;
        if (!this.level.isClientSide) {
            this.level.broadcastEntityEvent(this, (byte)10);
            if (!this.isSilent()) {
                this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0f, 1.0f);
            }
        }
    }

    public int getFuse() {
        return this.fuse;
    }

    public boolean isPrimed() {
        return this.fuse > -1;
    }

    @Override
    public float getBlockExplosionResistance(Explosion explosion, BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, FluidState fluidState, float f) {
        if (this.isPrimed() && (blockState.is(BlockTags.RAILS) || blockGetter.getBlockState(blockPos.above()).is(BlockTags.RAILS))) {
            return 0.0f;
        }
        return super.getBlockExplosionResistance(explosion, blockGetter, blockPos, blockState, fluidState, f);
    }

    @Override
    public boolean shouldBlockExplode(Explosion explosion, BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, float f) {
        if (this.isPrimed() && (blockState.is(BlockTags.RAILS) || blockGetter.getBlockState(blockPos.above()).is(BlockTags.RAILS))) {
            return false;
        }
        return super.shouldBlockExplode(explosion, blockGetter, blockPos, blockState, f);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        if (compoundTag.contains("TNTFuse", 99)) {
            this.fuse = compoundTag.getInt("TNTFuse");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("TNTFuse", this.fuse);
    }
}

