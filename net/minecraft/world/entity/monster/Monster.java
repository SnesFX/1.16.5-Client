/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.monster;

import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;

public abstract class Monster
extends PathfinderMob
implements Enemy {
    protected Monster(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 5;
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    @Override
    public void aiStep() {
        this.updateSwingTime();
        this.updateNoActionTime();
        super.aiStep();
    }

    protected void updateNoActionTime() {
        float f = this.getBrightness();
        if (f > 0.5f) {
            this.noActionTime += 2;
        }
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return true;
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.HOSTILE_SWIM;
    }

    @Override
    protected SoundEvent getSwimSplashSound() {
        return SoundEvents.HOSTILE_SPLASH;
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        if (this.isInvulnerableTo(damageSource)) {
            return false;
        }
        return super.hurt(damageSource, f);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.HOSTILE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.HOSTILE_DEATH;
    }

    @Override
    protected SoundEvent getFallDamageSound(int n) {
        if (n > 4) {
            return SoundEvents.HOSTILE_BIG_FALL;
        }
        return SoundEvents.HOSTILE_SMALL_FALL;
    }

    @Override
    public float getWalkTargetValue(BlockPos blockPos, LevelReader levelReader) {
        return 0.5f - levelReader.getBrightness(blockPos);
    }

    public static boolean isDarkEnoughToSpawn(ServerLevelAccessor serverLevelAccessor, BlockPos blockPos, Random random) {
        if (serverLevelAccessor.getBrightness(LightLayer.SKY, blockPos) > random.nextInt(32)) {
            return false;
        }
        int n = serverLevelAccessor.getLevel().isThundering() ? serverLevelAccessor.getMaxLocalRawBrightness(blockPos, 10) : serverLevelAccessor.getMaxLocalRawBrightness(blockPos);
        return n <= random.nextInt(8);
    }

    public static boolean checkMonsterSpawnRules(EntityType<? extends Monster> entityType, ServerLevelAccessor serverLevelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, Random random) {
        return serverLevelAccessor.getDifficulty() != Difficulty.PEACEFUL && Monster.isDarkEnoughToSpawn(serverLevelAccessor, blockPos, random) && Monster.checkMobSpawnRules(entityType, serverLevelAccessor, mobSpawnType, blockPos, random);
    }

    public static boolean checkAnyLightMonsterSpawnRules(EntityType<? extends Monster> entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, Random random) {
        return levelAccessor.getDifficulty() != Difficulty.PEACEFUL && Monster.checkMobSpawnRules(entityType, levelAccessor, mobSpawnType, blockPos, random);
    }

    public static AttributeSupplier.Builder createMonsterAttributes() {
        return Mob.createMobAttributes().add(Attributes.ATTACK_DAMAGE);
    }

    @Override
    protected boolean shouldDropExperience() {
        return true;
    }

    @Override
    protected boolean shouldDropLoot() {
        return true;
    }

    public boolean isPreventingPlayerRest(Player player) {
        return true;
    }

    @Override
    public ItemStack getProjectile(ItemStack itemStack) {
        if (itemStack.getItem() instanceof ProjectileWeaponItem) {
            Predicate<ItemStack> predicate = ((ProjectileWeaponItem)itemStack.getItem()).getSupportedHeldProjectiles();
            ItemStack itemStack2 = ProjectileWeaponItem.getHeldProjectile(this, predicate);
            return itemStack2.isEmpty() ? new ItemStack(Items.ARROW) : itemStack2;
        }
        return ItemStack.EMPTY;
    }
}

