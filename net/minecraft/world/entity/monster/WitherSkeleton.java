/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

public class WitherSkeleton
extends AbstractSkeleton {
    public WitherSkeleton(EntityType<? extends WitherSkeleton> entityType, Level level) {
        super(entityType, level);
        this.setPathfindingMalus(BlockPathTypes.LAVA, 8.0f);
    }

    @Override
    protected void registerGoals() {
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<AbstractPiglin>(this, AbstractPiglin.class, true));
        super.registerGoals();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.WITHER_SKELETON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.WITHER_SKELETON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WITHER_SKELETON_DEATH;
    }

    @Override
    SoundEvent getStepSound() {
        return SoundEvents.WITHER_SKELETON_STEP;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource damageSource, int n, boolean bl) {
        Creeper creeper;
        super.dropCustomDeathLoot(damageSource, n, bl);
        Entity entity = damageSource.getEntity();
        if (entity instanceof Creeper && (creeper = (Creeper)entity).canDropMobsSkull()) {
            creeper.increaseDroppedSkulls();
            this.spawnAtLocation(Items.WITHER_SKELETON_SKULL);
        }
    }

    @Override
    protected void populateDefaultEquipmentSlots(DifficultyInstance difficultyInstance) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
    }

    @Override
    protected void populateDefaultEquipmentEnchantments(DifficultyInstance difficultyInstance) {
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
        SpawnGroupData spawnGroupData2 = super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(4.0);
        this.reassessWeaponGoal();
        return spawnGroupData2;
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
        return 2.1f;
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        if (!super.doHurtTarget(entity)) {
            return false;
        }
        if (entity instanceof LivingEntity) {
            ((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.WITHER, 200));
        }
        return true;
    }

    @Override
    protected AbstractArrow getArrow(ItemStack itemStack, float f) {
        AbstractArrow abstractArrow = super.getArrow(itemStack, f);
        abstractArrow.setSecondsOnFire(100);
        return abstractArrow;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance mobEffectInstance) {
        if (mobEffectInstance.getEffect() == MobEffects.WITHER) {
            return false;
        }
        return super.canBeAffected(mobEffectInstance);
    }
}

