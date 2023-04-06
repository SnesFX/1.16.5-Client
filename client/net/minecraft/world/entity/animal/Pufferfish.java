/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.animal;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class Pufferfish
extends AbstractFish {
    private static final EntityDataAccessor<Integer> PUFF_STATE = SynchedEntityData.defineId(Pufferfish.class, EntityDataSerializers.INT);
    private int inflateCounter;
    private int deflateTimer;
    private static final Predicate<LivingEntity> NO_SPECTATORS_AND_NO_WATER_MOB = livingEntity -> {
        if (livingEntity == null) {
            return false;
        }
        if (livingEntity instanceof Player && (livingEntity.isSpectator() || ((Player)livingEntity).isCreative())) {
            return false;
        }
        return livingEntity.getMobType() != MobType.WATER;
    };

    public Pufferfish(EntityType<? extends Pufferfish> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PUFF_STATE, 0);
    }

    public int getPuffState() {
        return this.entityData.get(PUFF_STATE);
    }

    public void setPuffState(int n) {
        this.entityData.set(PUFF_STATE, n);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
        if (PUFF_STATE.equals(entityDataAccessor)) {
            this.refreshDimensions();
        }
        super.onSyncedDataUpdated(entityDataAccessor);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("PuffState", this.getPuffState());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setPuffState(compoundTag.getInt("PuffState"));
    }

    @Override
    protected ItemStack getBucketItemStack() {
        return new ItemStack(Items.PUFFERFISH_BUCKET);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new PufferfishPuffGoal(this));
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide && this.isAlive() && this.isEffectiveAi()) {
            if (this.inflateCounter > 0) {
                if (this.getPuffState() == 0) {
                    this.playSound(SoundEvents.PUFFER_FISH_BLOW_UP, this.getSoundVolume(), this.getVoicePitch());
                    this.setPuffState(1);
                } else if (this.inflateCounter > 40 && this.getPuffState() == 1) {
                    this.playSound(SoundEvents.PUFFER_FISH_BLOW_UP, this.getSoundVolume(), this.getVoicePitch());
                    this.setPuffState(2);
                }
                ++this.inflateCounter;
            } else if (this.getPuffState() != 0) {
                if (this.deflateTimer > 60 && this.getPuffState() == 2) {
                    this.playSound(SoundEvents.PUFFER_FISH_BLOW_OUT, this.getSoundVolume(), this.getVoicePitch());
                    this.setPuffState(1);
                } else if (this.deflateTimer > 100 && this.getPuffState() == 1) {
                    this.playSound(SoundEvents.PUFFER_FISH_BLOW_OUT, this.getSoundVolume(), this.getVoicePitch());
                    this.setPuffState(0);
                }
                ++this.deflateTimer;
            }
        }
        super.tick();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.isAlive() && this.getPuffState() > 0) {
            List<LivingEntity> list = this.level.getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(0.3), NO_SPECTATORS_AND_NO_WATER_MOB);
            for (Mob mob : list) {
                if (!mob.isAlive()) continue;
                this.touch(mob);
            }
        }
    }

    private void touch(Mob mob) {
        int n = this.getPuffState();
        if (mob.hurt(DamageSource.mobAttack(this), 1 + n)) {
            mob.addEffect(new MobEffectInstance(MobEffects.POISON, 60 * n, 0));
            this.playSound(SoundEvents.PUFFER_FISH_STING, 1.0f, 1.0f);
        }
    }

    @Override
    public void playerTouch(Player player) {
        int n = this.getPuffState();
        if (player instanceof ServerPlayer && n > 0 && player.hurt(DamageSource.mobAttack(this), 1 + n)) {
            if (!this.isSilent()) {
                ((ServerPlayer)player).connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.PUFFER_FISH_STING, 0.0f));
            }
            player.addEffect(new MobEffectInstance(MobEffects.POISON, 60 * n, 0));
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.PUFFER_FISH_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PUFFER_FISH_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.PUFFER_FISH_HURT;
    }

    @Override
    protected SoundEvent getFlopSound() {
        return SoundEvents.PUFFER_FISH_FLOP;
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return super.getDimensions(pose).scale(Pufferfish.getScale(this.getPuffState()));
    }

    private static float getScale(int n) {
        switch (n) {
            case 1: {
                return 0.7f;
            }
            case 0: {
                return 0.5f;
            }
        }
        return 1.0f;
    }

    static class PufferfishPuffGoal
    extends Goal {
        private final Pufferfish fish;

        public PufferfishPuffGoal(Pufferfish pufferfish) {
            this.fish = pufferfish;
        }

        @Override
        public boolean canUse() {
            List<LivingEntity> list = this.fish.level.getEntitiesOfClass(LivingEntity.class, this.fish.getBoundingBox().inflate(2.0), NO_SPECTATORS_AND_NO_WATER_MOB);
            return !list.isEmpty();
        }

        @Override
        public void start() {
            this.fish.inflateCounter = 1;
            this.fish.deflateTimer = 0;
        }

        @Override
        public void stop() {
            this.fish.inflateCounter = 0;
        }

        @Override
        public boolean canContinueToUse() {
            List<LivingEntity> list = this.fish.level.getEntitiesOfClass(LivingEntity.class, this.fish.getBoundingBox().inflate(2.0), NO_SPECTATORS_AND_NO_WATER_MOB);
            return !list.isEmpty();
        }
    }

}

