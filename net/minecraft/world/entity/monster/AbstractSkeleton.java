/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.monster;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.ai.goal.RestrictSunGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractSkeleton
extends Monster
implements RangedAttackMob {
    private final RangedBowAttackGoal<AbstractSkeleton> bowGoal = new RangedBowAttackGoal<AbstractSkeleton>(this, 1.0, 20, 15.0f);
    private final MeleeAttackGoal meleeGoal = new MeleeAttackGoal(this, 1.2, false){

        @Override
        public void stop() {
            super.stop();
            AbstractSkeleton.this.setAggressive(false);
        }

        @Override
        public void start() {
            super.start();
            AbstractSkeleton.this.setAggressive(true);
        }
    };

    protected AbstractSkeleton(EntityType<? extends AbstractSkeleton> entityType, Level level) {
        super(entityType, level);
        this.reassessWeaponGoal();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new RestrictSunGoal(this));
        this.goalSelector.addGoal(3, new FleeSunGoal(this, 1.0));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<Wolf>(this, Wolf.class, 6.0f, 1.0, 1.2));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<Player>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<IronGolem>(this, IronGolem.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<Turtle>(this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    @Override
    protected void playStepSound(BlockPos blockPos, BlockState blockState) {
        this.playSound(this.getStepSound(), 0.15f, 1.0f);
    }

    abstract SoundEvent getStepSound();

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    public void aiStep() {
        boolean bl = this.isSunBurnTick();
        if (bl) {
            ItemStack itemStack = this.getItemBySlot(EquipmentSlot.HEAD);
            if (!itemStack.isEmpty()) {
                if (itemStack.isDamageableItem()) {
                    itemStack.setDamageValue(itemStack.getDamageValue() + this.random.nextInt(2));
                    if (itemStack.getDamageValue() >= itemStack.getMaxDamage()) {
                        this.broadcastBreakEvent(EquipmentSlot.HEAD);
                        this.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                    }
                }
                bl = false;
            }
            if (bl) {
                this.setSecondsOnFire(8);
            }
        }
        super.aiStep();
    }

    @Override
    public void rideTick() {
        super.rideTick();
        if (this.getVehicle() instanceof PathfinderMob) {
            PathfinderMob pathfinderMob = (PathfinderMob)this.getVehicle();
            this.yBodyRot = pathfinderMob.yBodyRot;
        }
    }

    @Override
    protected void populateDefaultEquipmentSlots(DifficultyInstance difficultyInstance) {
        super.populateDefaultEquipmentSlots(difficultyInstance);
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
        spawnGroupData = super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
        this.populateDefaultEquipmentSlots(difficultyInstance);
        this.populateDefaultEquipmentEnchantments(difficultyInstance);
        this.reassessWeaponGoal();
        this.setCanPickUpLoot(this.random.nextFloat() < 0.55f * difficultyInstance.getSpecialMultiplier());
        if (this.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
            LocalDate localDate = LocalDate.now();
            int n = localDate.get(ChronoField.DAY_OF_MONTH);
            int n2 = localDate.get(ChronoField.MONTH_OF_YEAR);
            if (n2 == 10 && n == 31 && this.random.nextFloat() < 0.25f) {
                this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(this.random.nextFloat() < 0.1f ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
                this.armorDropChances[EquipmentSlot.HEAD.getIndex()] = 0.0f;
            }
        }
        return spawnGroupData;
    }

    public void reassessWeaponGoal() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }
        this.goalSelector.removeGoal(this.meleeGoal);
        this.goalSelector.removeGoal(this.bowGoal);
        ItemStack itemStack = this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, Items.BOW));
        if (itemStack.getItem() == Items.BOW) {
            int n = 20;
            if (this.level.getDifficulty() != Difficulty.HARD) {
                n = 40;
            }
            this.bowGoal.setMinAttackInterval(n);
            this.goalSelector.addGoal(4, this.bowGoal);
        } else {
            this.goalSelector.addGoal(4, this.meleeGoal);
        }
    }

    @Override
    public void performRangedAttack(LivingEntity livingEntity, float f) {
        ItemStack itemStack = this.getProjectile(this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, Items.BOW)));
        AbstractArrow abstractArrow = this.getArrow(itemStack, f);
        double d = livingEntity.getX() - this.getX();
        double d2 = livingEntity.getY(0.3333333333333333) - abstractArrow.getY();
        double d3 = livingEntity.getZ() - this.getZ();
        double d4 = Mth.sqrt(d * d + d3 * d3);
        abstractArrow.shoot(d, d2 + d4 * 0.20000000298023224, d3, 1.6f, 14 - this.level.getDifficulty().getId() * 4);
        this.playSound(SoundEvents.SKELETON_SHOOT, 1.0f, 1.0f / (this.getRandom().nextFloat() * 0.4f + 0.8f));
        this.level.addFreshEntity(abstractArrow);
    }

    protected AbstractArrow getArrow(ItemStack itemStack, float f) {
        return ProjectileUtil.getMobArrow(this, itemStack, f);
    }

    @Override
    public boolean canFireProjectileWeapon(ProjectileWeaponItem projectileWeaponItem) {
        return projectileWeaponItem == Items.BOW;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.reassessWeaponGoal();
    }

    @Override
    public void setItemSlot(EquipmentSlot equipmentSlot, ItemStack itemStack) {
        super.setItemSlot(equipmentSlot, itemStack);
        if (!this.level.isClientSide) {
            this.reassessWeaponGoal();
        }
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
        return 1.74f;
    }

    @Override
    public double getMyRidingOffset() {
        return -0.6;
    }

}

