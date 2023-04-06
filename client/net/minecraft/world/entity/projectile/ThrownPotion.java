/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.projectile;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class ThrownPotion
extends ThrowableItemProjectile
implements ItemSupplier {
    public static final Predicate<LivingEntity> WATER_SENSITIVE = LivingEntity::isSensitiveToWater;

    public ThrownPotion(EntityType<? extends ThrownPotion> entityType, Level level) {
        super(entityType, level);
    }

    public ThrownPotion(Level level, LivingEntity livingEntity) {
        super(EntityType.POTION, livingEntity, level);
    }

    public ThrownPotion(Level level, double d, double d2, double d3) {
        super(EntityType.POTION, d, d2, d3, level);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.SPLASH_POTION;
    }

    @Override
    protected float getGravity() {
        return 0.05f;
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        if (this.level.isClientSide) {
            return;
        }
        ItemStack itemStack = this.getItem();
        Potion potion = PotionUtils.getPotion(itemStack);
        List<MobEffectInstance> list = PotionUtils.getMobEffects(itemStack);
        boolean bl = potion == Potions.WATER && list.isEmpty();
        Direction direction = blockHitResult.getDirection();
        BlockPos blockPos = blockHitResult.getBlockPos();
        BlockPos blockPos2 = blockPos.relative(direction);
        if (bl) {
            this.dowseFire(blockPos2, direction);
            this.dowseFire(blockPos2.relative(direction.getOpposite()), direction);
            for (Direction direction2 : Direction.Plane.HORIZONTAL) {
                this.dowseFire(blockPos2.relative(direction2), direction2);
            }
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        boolean bl;
        super.onHit(hitResult);
        if (this.level.isClientSide) {
            return;
        }
        ItemStack itemStack = this.getItem();
        Potion potion = PotionUtils.getPotion(itemStack);
        List<MobEffectInstance> list = PotionUtils.getMobEffects(itemStack);
        boolean bl2 = bl = potion == Potions.WATER && list.isEmpty();
        if (bl) {
            this.applyWater();
        } else if (!list.isEmpty()) {
            if (this.isLingering()) {
                this.makeAreaOfEffectCloud(itemStack, potion);
            } else {
                this.applySplash(list, hitResult.getType() == HitResult.Type.ENTITY ? ((EntityHitResult)hitResult).getEntity() : null);
            }
        }
        int n = potion.hasInstantEffects() ? 2007 : 2002;
        this.level.levelEvent(n, this.blockPosition(), PotionUtils.getColor(itemStack));
        this.remove();
    }

    private void applyWater() {
        AABB aABB = this.getBoundingBox().inflate(4.0, 2.0, 4.0);
        List<LivingEntity> list = this.level.getEntitiesOfClass(LivingEntity.class, aABB, WATER_SENSITIVE);
        if (!list.isEmpty()) {
            for (LivingEntity livingEntity : list) {
                double d = this.distanceToSqr(livingEntity);
                if (!(d < 16.0) || !livingEntity.isSensitiveToWater()) continue;
                livingEntity.hurt(DamageSource.indirectMagic(livingEntity, this.getOwner()), 1.0f);
            }
        }
    }

    private void applySplash(List<MobEffectInstance> list, @Nullable Entity entity) {
        AABB aABB = this.getBoundingBox().inflate(4.0, 2.0, 4.0);
        List<LivingEntity> list2 = this.level.getEntitiesOfClass(LivingEntity.class, aABB);
        if (!list2.isEmpty()) {
            for (LivingEntity livingEntity : list2) {
                double d;
                if (!livingEntity.isAffectedByPotions() || !((d = this.distanceToSqr(livingEntity)) < 16.0)) continue;
                double d2 = 1.0 - Math.sqrt(d) / 4.0;
                if (livingEntity == entity) {
                    d2 = 1.0;
                }
                for (MobEffectInstance mobEffectInstance : list) {
                    MobEffect mobEffect = mobEffectInstance.getEffect();
                    if (mobEffect.isInstantenous()) {
                        mobEffect.applyInstantenousEffect(this, this.getOwner(), livingEntity, mobEffectInstance.getAmplifier(), d2);
                        continue;
                    }
                    int n = (int)(d2 * (double)mobEffectInstance.getDuration() + 0.5);
                    if (n <= 20) continue;
                    livingEntity.addEffect(new MobEffectInstance(mobEffect, n, mobEffectInstance.getAmplifier(), mobEffectInstance.isAmbient(), mobEffectInstance.isVisible()));
                }
            }
        }
    }

    private void makeAreaOfEffectCloud(ItemStack itemStack, Potion potion) {
        AreaEffectCloud areaEffectCloud = new AreaEffectCloud(this.level, this.getX(), this.getY(), this.getZ());
        Entity entity = this.getOwner();
        if (entity instanceof LivingEntity) {
            areaEffectCloud.setOwner((LivingEntity)entity);
        }
        areaEffectCloud.setRadius(3.0f);
        areaEffectCloud.setRadiusOnUse(-0.5f);
        areaEffectCloud.setWaitTime(10);
        areaEffectCloud.setRadiusPerTick(-areaEffectCloud.getRadius() / (float)areaEffectCloud.getDuration());
        areaEffectCloud.setPotion(potion);
        for (MobEffectInstance mobEffectInstance : PotionUtils.getCustomEffects(itemStack)) {
            areaEffectCloud.addEffect(new MobEffectInstance(mobEffectInstance));
        }
        CompoundTag compoundTag = itemStack.getTag();
        if (compoundTag != null && compoundTag.contains("CustomPotionColor", 99)) {
            areaEffectCloud.setFixedColor(compoundTag.getInt("CustomPotionColor"));
        }
        this.level.addFreshEntity(areaEffectCloud);
    }

    private boolean isLingering() {
        return this.getItem().getItem() == Items.LINGERING_POTION;
    }

    private void dowseFire(BlockPos blockPos, Direction direction) {
        BlockState blockState = this.level.getBlockState(blockPos);
        if (blockState.is(BlockTags.FIRE)) {
            this.level.removeBlock(blockPos, false);
        } else if (CampfireBlock.isLitCampfire(blockState)) {
            this.level.levelEvent(null, 1009, blockPos, 0);
            CampfireBlock.dowse(this.level, blockPos, blockState);
            this.level.setBlockAndUpdate(blockPos, (BlockState)blockState.setValue(CampfireBlock.LIT, false));
        }
    }
}

