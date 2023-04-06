/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.projectile;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class ThrownTrident
extends AbstractArrow {
    private static final EntityDataAccessor<Byte> ID_LOYALTY = SynchedEntityData.defineId(ThrownTrident.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> ID_FOIL = SynchedEntityData.defineId(ThrownTrident.class, EntityDataSerializers.BOOLEAN);
    private ItemStack tridentItem = new ItemStack(Items.TRIDENT);
    private boolean dealtDamage;
    public int clientSideReturnTridentTickCount;

    public ThrownTrident(EntityType<? extends ThrownTrident> entityType, Level level) {
        super(entityType, level);
    }

    public ThrownTrident(Level level, LivingEntity livingEntity, ItemStack itemStack) {
        super(EntityType.TRIDENT, livingEntity, level);
        this.tridentItem = itemStack.copy();
        this.entityData.set(ID_LOYALTY, (byte)EnchantmentHelper.getLoyalty(itemStack));
        this.entityData.set(ID_FOIL, itemStack.hasFoil());
    }

    public ThrownTrident(Level level, double d, double d2, double d3) {
        super(EntityType.TRIDENT, d, d2, d3, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ID_LOYALTY, (byte)0);
        this.entityData.define(ID_FOIL, false);
    }

    @Override
    public void tick() {
        if (this.inGroundTime > 4) {
            this.dealtDamage = true;
        }
        Entity entity = this.getOwner();
        if ((this.dealtDamage || this.isNoPhysics()) && entity != null) {
            byte by = this.entityData.get(ID_LOYALTY);
            if (by > 0 && !this.isAcceptibleReturnOwner()) {
                if (!this.level.isClientSide && this.pickup == AbstractArrow.Pickup.ALLOWED) {
                    this.spawnAtLocation(this.getPickupItem(), 0.1f);
                }
                this.remove();
            } else if (by > 0) {
                this.setNoPhysics(true);
                Vec3 vec3 = new Vec3(entity.getX() - this.getX(), entity.getEyeY() - this.getY(), entity.getZ() - this.getZ());
                this.setPosRaw(this.getX(), this.getY() + vec3.y * 0.015 * (double)by, this.getZ());
                if (this.level.isClientSide) {
                    this.yOld = this.getY();
                }
                double d = 0.05 * (double)by;
                this.setDeltaMovement(this.getDeltaMovement().scale(0.95).add(vec3.normalize().scale(d)));
                if (this.clientSideReturnTridentTickCount == 0) {
                    this.playSound(SoundEvents.TRIDENT_RETURN, 10.0f, 1.0f);
                }
                ++this.clientSideReturnTridentTickCount;
            }
        }
        super.tick();
    }

    private boolean isAcceptibleReturnOwner() {
        Entity entity = this.getOwner();
        if (entity == null || !entity.isAlive()) {
            return false;
        }
        return !(entity instanceof ServerPlayer) || !entity.isSpectator();
    }

    @Override
    protected ItemStack getPickupItem() {
        return this.tridentItem.copy();
    }

    public boolean isFoil() {
        return this.entityData.get(ID_FOIL);
    }

    @Nullable
    @Override
    protected EntityHitResult findHitEntity(Vec3 vec3, Vec3 vec32) {
        if (this.dealtDamage) {
            return null;
        }
        return super.findHitEntity(vec3, vec32);
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        BlockPos blockPos;
        Entity entity;
        Entity entity2 = entityHitResult.getEntity();
        float f = 8.0f;
        if (entity2 instanceof LivingEntity) {
            entity = (LivingEntity)entity2;
            f += EnchantmentHelper.getDamageBonus(this.tridentItem, ((LivingEntity)entity).getMobType());
        }
        DamageSource damageSource = DamageSource.trident(this, (entity = this.getOwner()) == null ? this : entity);
        this.dealtDamage = true;
        SoundEvent soundEvent = SoundEvents.TRIDENT_HIT;
        if (entity2.hurt(damageSource, f)) {
            if (entity2.getType() == EntityType.ENDERMAN) {
                return;
            }
            if (entity2 instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity)entity2;
                if (entity instanceof LivingEntity) {
                    EnchantmentHelper.doPostHurtEffects(livingEntity, entity);
                    EnchantmentHelper.doPostDamageEffects((LivingEntity)entity, livingEntity);
                }
                this.doPostHurtEffects(livingEntity);
            }
        }
        this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01, -0.1, -0.01));
        float f2 = 1.0f;
        if (this.level instanceof ServerLevel && this.level.isThundering() && EnchantmentHelper.hasChanneling(this.tridentItem) && this.level.canSeeSky(blockPos = entity2.blockPosition())) {
            LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(this.level);
            lightningBolt.moveTo(Vec3.atBottomCenterOf(blockPos));
            lightningBolt.setCause(entity instanceof ServerPlayer ? (ServerPlayer)entity : null);
            this.level.addFreshEntity(lightningBolt);
            soundEvent = SoundEvents.TRIDENT_THUNDER;
            f2 = 5.0f;
        }
        this.playSound(soundEvent, f2, 1.0f);
    }

    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.TRIDENT_HIT_GROUND;
    }

    @Override
    public void playerTouch(Player player) {
        Entity entity = this.getOwner();
        if (entity != null && entity.getUUID() != player.getUUID()) {
            return;
        }
        super.playerTouch(player);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        if (compoundTag.contains("Trident", 10)) {
            this.tridentItem = ItemStack.of(compoundTag.getCompound("Trident"));
        }
        this.dealtDamage = compoundTag.getBoolean("DealtDamage");
        this.entityData.set(ID_LOYALTY, (byte)EnchantmentHelper.getLoyalty(this.tridentItem));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.put("Trident", this.tridentItem.save(new CompoundTag()));
        compoundTag.putBoolean("DealtDamage", this.dealtDamage);
    }

    @Override
    public void tickDespawn() {
        byte by = this.entityData.get(ID_LOYALTY);
        if (this.pickup != AbstractArrow.Pickup.ALLOWED || by <= 0) {
            super.tickDespawn();
        }
    }

    @Override
    protected float getWaterInertia() {
        return 0.99f;
    }

    @Override
    public boolean shouldRender(double d, double d2, double d3) {
        return true;
    }
}

