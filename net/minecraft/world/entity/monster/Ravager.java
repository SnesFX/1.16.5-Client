/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.monster;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Ravager
extends Raider {
    private static final Predicate<Entity> NO_RAVAGER_AND_ALIVE = entity -> entity.isAlive() && !(entity instanceof Ravager);
    private int attackTick;
    private int stunnedTick;
    private int roarTick;

    public Ravager(EntityType<? extends Ravager> entityType, Level level) {
        super(entityType, level);
        this.maxUpStep = 1.0f;
        this.xpReward = 20;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(4, new RavagerMeleeAttackGoal());
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.4));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0f));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0f));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this, Raider.class).setAlertOthers(new Class[0]));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<Player>(this, Player.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<AbstractVillager>(this, AbstractVillager.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<IronGolem>(this, IronGolem.class, true));
    }

    @Override
    protected void updateControlFlags() {
        boolean bl = !(this.getControllingPassenger() instanceof Mob) || this.getControllingPassenger().getType().is(EntityTypeTags.RAIDERS);
        boolean bl2 = !(this.getVehicle() instanceof Boat);
        this.goalSelector.setControlFlag(Goal.Flag.MOVE, bl);
        this.goalSelector.setControlFlag(Goal.Flag.JUMP, bl && bl2);
        this.goalSelector.setControlFlag(Goal.Flag.LOOK, bl);
        this.goalSelector.setControlFlag(Goal.Flag.TARGET, bl);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 100.0).add(Attributes.MOVEMENT_SPEED, 0.3).add(Attributes.KNOCKBACK_RESISTANCE, 0.75).add(Attributes.ATTACK_DAMAGE, 12.0).add(Attributes.ATTACK_KNOCKBACK, 1.5).add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("AttackTick", this.attackTick);
        compoundTag.putInt("StunTick", this.stunnedTick);
        compoundTag.putInt("RoarTick", this.roarTick);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.attackTick = compoundTag.getInt("AttackTick");
        this.stunnedTick = compoundTag.getInt("StunTick");
        this.roarTick = compoundTag.getInt("RoarTick");
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.RAVAGER_CELEBRATE;
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new RavagerNavigation(this, level);
    }

    @Override
    public int getMaxHeadYRot() {
        return 45;
    }

    @Override
    public double getPassengersRidingOffset() {
        return 2.1;
    }

    @Override
    public boolean canBeControlledByRider() {
        return !this.isNoAi() && this.getControllingPassenger() instanceof LivingEntity;
    }

    @Nullable
    @Override
    public Entity getControllingPassenger() {
        if (this.getPassengers().isEmpty()) {
            return null;
        }
        return this.getPassengers().get(0);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.isAlive()) {
            return;
        }
        if (this.isImmobile()) {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
        } else {
            double d = this.getTarget() != null ? 0.35 : 0.3;
            double d2 = this.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue();
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(Mth.lerp(0.1, d2, d));
        }
        if (this.horizontalCollision && this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            boolean bl = false;
            AABB aABB = this.getBoundingBox().inflate(0.2);
            for (BlockPos blockPos : BlockPos.betweenClosed(Mth.floor(aABB.minX), Mth.floor(aABB.minY), Mth.floor(aABB.minZ), Mth.floor(aABB.maxX), Mth.floor(aABB.maxY), Mth.floor(aABB.maxZ))) {
                BlockState blockState = this.level.getBlockState(blockPos);
                Block block = blockState.getBlock();
                if (!(block instanceof LeavesBlock)) continue;
                bl = this.level.destroyBlock(blockPos, true, this) || bl;
            }
            if (!bl && this.onGround) {
                this.jumpFromGround();
            }
        }
        if (this.roarTick > 0) {
            --this.roarTick;
            if (this.roarTick == 10) {
                this.roar();
            }
        }
        if (this.attackTick > 0) {
            --this.attackTick;
        }
        if (this.stunnedTick > 0) {
            --this.stunnedTick;
            this.stunEffect();
            if (this.stunnedTick == 0) {
                this.playSound(SoundEvents.RAVAGER_ROAR, 1.0f, 1.0f);
                this.roarTick = 20;
            }
        }
    }

    private void stunEffect() {
        if (this.random.nextInt(6) == 0) {
            double d = this.getX() - (double)this.getBbWidth() * Math.sin(this.yBodyRot * 0.017453292f) + (this.random.nextDouble() * 0.6 - 0.3);
            double d2 = this.getY() + (double)this.getBbHeight() - 0.3;
            double d3 = this.getZ() + (double)this.getBbWidth() * Math.cos(this.yBodyRot * 0.017453292f) + (this.random.nextDouble() * 0.6 - 0.3);
            this.level.addParticle(ParticleTypes.ENTITY_EFFECT, d, d2, d3, 0.4980392156862745, 0.5137254901960784, 0.5725490196078431);
        }
    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || this.attackTick > 0 || this.stunnedTick > 0 || this.roarTick > 0;
    }

    @Override
    public boolean canSee(Entity entity) {
        if (this.stunnedTick > 0 || this.roarTick > 0) {
            return false;
        }
        return super.canSee(entity);
    }

    @Override
    protected void blockedByShield(LivingEntity livingEntity) {
        if (this.roarTick == 0) {
            if (this.random.nextDouble() < 0.5) {
                this.stunnedTick = 40;
                this.playSound(SoundEvents.RAVAGER_STUNNED, 1.0f, 1.0f);
                this.level.broadcastEntityEvent(this, (byte)39);
                livingEntity.push(this);
            } else {
                this.strongKnockback(livingEntity);
            }
            livingEntity.hurtMarked = true;
        }
    }

    private void roar() {
        if (this.isAlive()) {
            List<Entity> list = this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(4.0), NO_RAVAGER_AND_ALIVE);
            for (Entity entity : list) {
                if (!(entity instanceof AbstractIllager)) {
                    entity.hurt(DamageSource.mobAttack(this), 6.0f);
                }
                this.strongKnockback(entity);
            }
            Vec3 vec3 = this.getBoundingBox().getCenter();
            for (int i = 0; i < 40; ++i) {
                double d = this.random.nextGaussian() * 0.2;
                double d2 = this.random.nextGaussian() * 0.2;
                double d3 = this.random.nextGaussian() * 0.2;
                this.level.addParticle(ParticleTypes.POOF, vec3.x, vec3.y, vec3.z, d, d2, d3);
            }
        }
    }

    private void strongKnockback(Entity entity) {
        double d = entity.getX() - this.getX();
        double d2 = entity.getZ() - this.getZ();
        double d3 = Math.max(d * d + d2 * d2, 0.001);
        entity.push(d / d3 * 4.0, 0.2, d2 / d3 * 4.0);
    }

    @Override
    public void handleEntityEvent(byte by) {
        if (by == 4) {
            this.attackTick = 10;
            this.playSound(SoundEvents.RAVAGER_ATTACK, 1.0f, 1.0f);
        } else if (by == 39) {
            this.stunnedTick = 40;
        }
        super.handleEntityEvent(by);
    }

    public int getAttackTick() {
        return this.attackTick;
    }

    public int getStunnedTick() {
        return this.stunnedTick;
    }

    public int getRoarTick() {
        return this.roarTick;
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        this.attackTick = 10;
        this.level.broadcastEntityEvent(this, (byte)4);
        this.playSound(SoundEvents.RAVAGER_ATTACK, 1.0f, 1.0f);
        return super.doHurtTarget(entity);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.RAVAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.RAVAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.RAVAGER_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos blockPos, BlockState blockState) {
        this.playSound(SoundEvents.RAVAGER_STEP, 0.15f, 1.0f);
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader levelReader) {
        return !levelReader.containsAnyLiquid(this.getBoundingBox());
    }

    @Override
    public void applyRaidBuffs(int n, boolean bl) {
    }

    @Override
    public boolean canBeLeader() {
        return false;
    }

    static class RavagerNodeEvaluator
    extends WalkNodeEvaluator {
        private RavagerNodeEvaluator() {
        }

        @Override
        protected BlockPathTypes evaluateBlockPathType(BlockGetter blockGetter, boolean bl, boolean bl2, BlockPos blockPos, BlockPathTypes blockPathTypes) {
            if (blockPathTypes == BlockPathTypes.LEAVES) {
                return BlockPathTypes.OPEN;
            }
            return super.evaluateBlockPathType(blockGetter, bl, bl2, blockPos, blockPathTypes);
        }
    }

    static class RavagerNavigation
    extends GroundPathNavigation {
        public RavagerNavigation(Mob mob, Level level) {
            super(mob, level);
        }

        @Override
        protected PathFinder createPathFinder(int n) {
            this.nodeEvaluator = new RavagerNodeEvaluator();
            return new PathFinder(this.nodeEvaluator, n);
        }
    }

    class RavagerMeleeAttackGoal
    extends MeleeAttackGoal {
        public RavagerMeleeAttackGoal() {
            super(Ravager.this, 1.0, true);
        }

        @Override
        protected double getAttackReachSqr(LivingEntity livingEntity) {
            float f = Ravager.this.getBbWidth() - 0.1f;
            return f * 2.0f * (f * 2.0f) + livingEntity.getBbWidth();
        }
    }

}

