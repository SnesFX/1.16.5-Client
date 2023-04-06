/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.animal;

import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class SnowGolem
extends AbstractGolem
implements Shearable,
RangedAttackMob {
    private static final EntityDataAccessor<Byte> DATA_PUMPKIN_ID = SynchedEntityData.defineId(SnowGolem.class, EntityDataSerializers.BYTE);

    public SnowGolem(EntityType<? extends SnowGolem> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.25, 20, 10.0f));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0, 1.0000001E-5f));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 6.0f));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<Mob>(this, Mob.class, 10, true, false, livingEntity -> livingEntity instanceof Enemy));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 4.0).add(Attributes.MOVEMENT_SPEED, 0.20000000298023224);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_PUMPKIN_ID, (byte)16);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putBoolean("Pumpkin", this.hasPumpkin());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        if (compoundTag.contains("Pumpkin")) {
            this.setPumpkin(compoundTag.getBoolean("Pumpkin"));
        }
    }

    @Override
    public boolean isSensitiveToWater() {
        return true;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level.isClientSide) {
            int n = Mth.floor(this.getX());
            int n2 = Mth.floor(this.getY());
            int n3 = Mth.floor(this.getZ());
            BlockPos blockPos = new BlockPos(n, 0, n3);
            BlockPos blockPos2 = new BlockPos(n, n2, n3);
            if (this.level.getBiome(blockPos).getTemperature(blockPos2) > 1.0f) {
                this.hurt(DamageSource.ON_FIRE, 1.0f);
            }
            if (!this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                return;
            }
            BlockState blockState = Blocks.SNOW.defaultBlockState();
            for (int i = 0; i < 4; ++i) {
                n = Mth.floor(this.getX() + (double)((float)(i % 2 * 2 - 1) * 0.25f));
                BlockPos blockPos3 = new BlockPos(n, n2 = Mth.floor(this.getY()), n3 = Mth.floor(this.getZ() + (double)((float)(i / 2 % 2 * 2 - 1) * 0.25f)));
                if (!this.level.getBlockState(blockPos3).isAir() || !(this.level.getBiome(blockPos3).getTemperature(blockPos3) < 0.8f) || !blockState.canSurvive(this.level, blockPos3)) continue;
                this.level.setBlockAndUpdate(blockPos3, blockState);
            }
        }
    }

    @Override
    public void performRangedAttack(LivingEntity livingEntity, float f) {
        Snowball snowball = new Snowball(this.level, this);
        double d = livingEntity.getEyeY() - 1.100000023841858;
        double d2 = livingEntity.getX() - this.getX();
        double d3 = d - snowball.getY();
        double d4 = livingEntity.getZ() - this.getZ();
        float f2 = Mth.sqrt(d2 * d2 + d4 * d4) * 0.2f;
        snowball.shoot(d2, d3 + (double)f2, d4, 1.6f, 12.0f);
        this.playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1.0f, 0.4f / (this.getRandom().nextFloat() * 0.4f + 0.8f));
        this.level.addFreshEntity(snowball);
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
        return 1.7f;
    }

    @Override
    protected InteractionResult mobInteract(Player player2, InteractionHand interactionHand) {
        ItemStack itemStack = player2.getItemInHand(interactionHand);
        if (itemStack.getItem() == Items.SHEARS && this.readyForShearing()) {
            this.shear(SoundSource.PLAYERS);
            if (!this.level.isClientSide) {
                itemStack.hurtAndBreak(1, player2, player -> player.broadcastBreakEvent(interactionHand));
            }
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    public void shear(SoundSource soundSource) {
        this.level.playSound(null, this, SoundEvents.SNOW_GOLEM_SHEAR, soundSource, 1.0f, 1.0f);
        if (!this.level.isClientSide()) {
            this.setPumpkin(false);
            this.spawnAtLocation(new ItemStack(Items.CARVED_PUMPKIN), 1.7f);
        }
    }

    @Override
    public boolean readyForShearing() {
        return this.isAlive() && this.hasPumpkin();
    }

    public boolean hasPumpkin() {
        return (this.entityData.get(DATA_PUMPKIN_ID) & 0x10) != 0;
    }

    public void setPumpkin(boolean bl) {
        byte by = this.entityData.get(DATA_PUMPKIN_ID);
        if (bl) {
            this.entityData.set(DATA_PUMPKIN_ID, (byte)(by | 0x10));
        } else {
            this.entityData.set(DATA_PUMPKIN_ID, (byte)(by & 0xFFFFFFEF));
        }
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SNOW_GOLEM_AMBIENT;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.SNOW_GOLEM_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SNOW_GOLEM_DEATH;
    }

    @Override
    public Vec3 getLeashOffset() {
        return new Vec3(0.0, 0.75f * this.getEyeHeight(), this.getBbWidth() * 0.4f);
    }
}

