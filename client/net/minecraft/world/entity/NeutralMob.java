/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public interface NeutralMob {
    public int getRemainingPersistentAngerTime();

    public void setRemainingPersistentAngerTime(int var1);

    @Nullable
    public UUID getPersistentAngerTarget();

    public void setPersistentAngerTarget(@Nullable UUID var1);

    public void startPersistentAngerTimer();

    default public void addPersistentAngerSaveData(CompoundTag compoundTag) {
        compoundTag.putInt("AngerTime", this.getRemainingPersistentAngerTime());
        if (this.getPersistentAngerTarget() != null) {
            compoundTag.putUUID("AngryAt", this.getPersistentAngerTarget());
        }
    }

    default public void readPersistentAngerSaveData(ServerLevel serverLevel, CompoundTag compoundTag) {
        this.setRemainingPersistentAngerTime(compoundTag.getInt("AngerTime"));
        if (!compoundTag.hasUUID("AngryAt")) {
            this.setPersistentAngerTarget(null);
            return;
        }
        UUID uUID = compoundTag.getUUID("AngryAt");
        this.setPersistentAngerTarget(uUID);
        Entity entity = serverLevel.getEntity(uUID);
        if (entity == null) {
            return;
        }
        if (entity instanceof Mob) {
            this.setLastHurtByMob((Mob)entity);
        }
        if (entity.getType() == EntityType.PLAYER) {
            this.setLastHurtByPlayer((Player)entity);
        }
    }

    default public void updatePersistentAnger(ServerLevel serverLevel, boolean bl) {
        LivingEntity livingEntity = this.getTarget();
        UUID uUID = this.getPersistentAngerTarget();
        if ((livingEntity == null || livingEntity.isDeadOrDying()) && uUID != null && serverLevel.getEntity(uUID) instanceof Mob) {
            this.stopBeingAngry();
            return;
        }
        if (livingEntity != null && !Objects.equals(uUID, livingEntity.getUUID())) {
            this.setPersistentAngerTarget(livingEntity.getUUID());
            this.startPersistentAngerTimer();
        }
        if (!(this.getRemainingPersistentAngerTime() <= 0 || livingEntity != null && livingEntity.getType() == EntityType.PLAYER && bl)) {
            this.setRemainingPersistentAngerTime(this.getRemainingPersistentAngerTime() - 1);
            if (this.getRemainingPersistentAngerTime() == 0) {
                this.stopBeingAngry();
            }
        }
    }

    default public boolean isAngryAt(LivingEntity livingEntity) {
        if (!EntitySelector.ATTACK_ALLOWED.test(livingEntity)) {
            return false;
        }
        if (livingEntity.getType() == EntityType.PLAYER && this.isAngryAtAllPlayers(livingEntity.level)) {
            return true;
        }
        return livingEntity.getUUID().equals(this.getPersistentAngerTarget());
    }

    default public boolean isAngryAtAllPlayers(Level level) {
        return level.getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER) && this.isAngry() && this.getPersistentAngerTarget() == null;
    }

    default public boolean isAngry() {
        return this.getRemainingPersistentAngerTime() > 0;
    }

    default public void playerDied(Player player) {
        if (!player.level.getGameRules().getBoolean(GameRules.RULE_FORGIVE_DEAD_PLAYERS)) {
            return;
        }
        if (!player.getUUID().equals(this.getPersistentAngerTarget())) {
            return;
        }
        this.stopBeingAngry();
    }

    default public void forgetCurrentTargetAndRefreshUniversalAnger() {
        this.stopBeingAngry();
        this.startPersistentAngerTimer();
    }

    default public void stopBeingAngry() {
        this.setLastHurtByMob(null);
        this.setPersistentAngerTarget(null);
        this.setTarget(null);
        this.setRemainingPersistentAngerTime(0);
    }

    public void setLastHurtByMob(@Nullable LivingEntity var1);

    public void setLastHurtByPlayer(@Nullable Player var1);

    public void setTarget(@Nullable LivingEntity var1);

    @Nullable
    public LivingEntity getTarget();
}

