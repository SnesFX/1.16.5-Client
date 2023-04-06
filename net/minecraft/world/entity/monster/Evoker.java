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
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.SpellcasterIllager;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.scores.Team;

public class Evoker
extends SpellcasterIllager {
    private Sheep wololoTarget;

    public Evoker(EntityType<? extends Evoker> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 10;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new EvokerCastingSpellGoal());
        this.goalSelector.addGoal(2, new AvoidEntityGoal<Player>(this, Player.class, 8.0f, 0.6, 1.0));
        this.goalSelector.addGoal(4, new EvokerSummonSpellGoal());
        this.goalSelector.addGoal(5, new EvokerAttackSpellGoal());
        this.goalSelector.addGoal(6, new EvokerWololoSpellGoal());
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0f, 1.0f));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0f));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Raider.class).setAlertOthers(new Class[0]));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<Player>(this, Player.class, true).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<AbstractVillager>(this, AbstractVillager.class, false).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<IronGolem>(this, IronGolem.class, false));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.5).add(Attributes.FOLLOW_RANGE, 12.0).add(Attributes.MAX_HEALTH, 24.0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.EVOKER_CELEBRATE;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        if (entity == null) {
            return false;
        }
        if (entity == this) {
            return true;
        }
        if (super.isAlliedTo(entity)) {
            return true;
        }
        if (entity instanceof Vex) {
            return this.isAlliedTo(((Vex)entity).getOwner());
        }
        if (entity instanceof LivingEntity && ((LivingEntity)entity).getMobType() == MobType.ILLAGER) {
            return this.getTeam() == null && entity.getTeam() == null;
        }
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.EVOKER_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.EVOKER_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.EVOKER_HURT;
    }

    private void setWololoTarget(@Nullable Sheep sheep) {
        this.wololoTarget = sheep;
    }

    @Nullable
    private Sheep getWololoTarget() {
        return this.wololoTarget;
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return SoundEvents.EVOKER_CAST_SPELL;
    }

    @Override
    public void applyRaidBuffs(int n, boolean bl) {
    }

    public class EvokerWololoSpellGoal
    extends SpellcasterIllager.SpellcasterUseSpellGoal {
        private final TargetingConditions wololoTargeting;

        public EvokerWololoSpellGoal() {
            super(Evoker.this);
            this.wololoTargeting = new TargetingConditions().range(16.0).allowInvulnerable().selector(livingEntity -> ((Sheep)livingEntity).getColor() == DyeColor.BLUE);
        }

        @Override
        public boolean canUse() {
            if (Evoker.this.getTarget() != null) {
                return false;
            }
            if (Evoker.this.isCastingSpell()) {
                return false;
            }
            if (Evoker.this.tickCount < this.nextAttackTickCount) {
                return false;
            }
            if (!Evoker.this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                return false;
            }
            List<Sheep> list = Evoker.this.level.getNearbyEntities(Sheep.class, this.wololoTargeting, Evoker.this, Evoker.this.getBoundingBox().inflate(16.0, 4.0, 16.0));
            if (list.isEmpty()) {
                return false;
            }
            Evoker.this.setWololoTarget(list.get(Evoker.this.random.nextInt(list.size())));
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            return Evoker.this.getWololoTarget() != null && this.attackWarmupDelay > 0;
        }

        @Override
        public void stop() {
            super.stop();
            Evoker.this.setWololoTarget(null);
        }

        @Override
        protected void performSpellCasting() {
            Sheep sheep = Evoker.this.getWololoTarget();
            if (sheep != null && sheep.isAlive()) {
                sheep.setColor(DyeColor.RED);
            }
        }

        @Override
        protected int getCastWarmupTime() {
            return 40;
        }

        @Override
        protected int getCastingTime() {
            return 60;
        }

        @Override
        protected int getCastingInterval() {
            return 140;
        }

        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_WOLOLO;
        }

        @Override
        protected SpellcasterIllager.IllagerSpell getSpell() {
            return SpellcasterIllager.IllagerSpell.WOLOLO;
        }
    }

    class EvokerSummonSpellGoal
    extends SpellcasterIllager.SpellcasterUseSpellGoal {
        private final TargetingConditions vexCountTargeting;

        private EvokerSummonSpellGoal() {
            super(Evoker.this);
            this.vexCountTargeting = new TargetingConditions().range(16.0).allowUnseeable().ignoreInvisibilityTesting().allowInvulnerable().allowSameTeam();
        }

        @Override
        public boolean canUse() {
            if (!super.canUse()) {
                return false;
            }
            int n = Evoker.this.level.getNearbyEntities(Vex.class, this.vexCountTargeting, Evoker.this, Evoker.this.getBoundingBox().inflate(16.0)).size();
            return Evoker.this.random.nextInt(8) + 1 > n;
        }

        @Override
        protected int getCastingTime() {
            return 100;
        }

        @Override
        protected int getCastingInterval() {
            return 340;
        }

        @Override
        protected void performSpellCasting() {
            ServerLevel serverLevel = (ServerLevel)Evoker.this.level;
            for (int i = 0; i < 3; ++i) {
                BlockPos blockPos = Evoker.this.blockPosition().offset(-2 + Evoker.this.random.nextInt(5), 1, -2 + Evoker.this.random.nextInt(5));
                Vex vex = EntityType.VEX.create(Evoker.this.level);
                vex.moveTo(blockPos, 0.0f, 0.0f);
                vex.finalizeSpawn(serverLevel, Evoker.this.level.getCurrentDifficultyAt(blockPos), MobSpawnType.MOB_SUMMONED, null, null);
                vex.setOwner(Evoker.this);
                vex.setBoundOrigin(blockPos);
                vex.setLimitedLife(20 * (30 + Evoker.this.random.nextInt(90)));
                serverLevel.addFreshEntityWithPassengers(vex);
            }
        }

        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_SUMMON;
        }

        @Override
        protected SpellcasterIllager.IllagerSpell getSpell() {
            return SpellcasterIllager.IllagerSpell.SUMMON_VEX;
        }
    }

    class EvokerAttackSpellGoal
    extends SpellcasterIllager.SpellcasterUseSpellGoal {
        private EvokerAttackSpellGoal() {
            super(Evoker.this);
        }

        @Override
        protected int getCastingTime() {
            return 40;
        }

        @Override
        protected int getCastingInterval() {
            return 100;
        }

        @Override
        protected void performSpellCasting() {
            LivingEntity livingEntity = Evoker.this.getTarget();
            double d = Math.min(livingEntity.getY(), Evoker.this.getY());
            double d2 = Math.max(livingEntity.getY(), Evoker.this.getY()) + 1.0;
            float f = (float)Mth.atan2(livingEntity.getZ() - Evoker.this.getZ(), livingEntity.getX() - Evoker.this.getX());
            if (Evoker.this.distanceToSqr(livingEntity) < 9.0) {
                int n;
                float f2;
                for (n = 0; n < 5; ++n) {
                    f2 = f + (float)n * 3.1415927f * 0.4f;
                    this.createSpellEntity(Evoker.this.getX() + (double)Mth.cos(f2) * 1.5, Evoker.this.getZ() + (double)Mth.sin(f2) * 1.5, d, d2, f2, 0);
                }
                for (n = 0; n < 8; ++n) {
                    f2 = f + (float)n * 3.1415927f * 2.0f / 8.0f + 1.2566371f;
                    this.createSpellEntity(Evoker.this.getX() + (double)Mth.cos(f2) * 2.5, Evoker.this.getZ() + (double)Mth.sin(f2) * 2.5, d, d2, f2, 3);
                }
            } else {
                for (int i = 0; i < 16; ++i) {
                    double d3 = 1.25 * (double)(i + 1);
                    int n = 1 * i;
                    this.createSpellEntity(Evoker.this.getX() + (double)Mth.cos(f) * d3, Evoker.this.getZ() + (double)Mth.sin(f) * d3, d, d2, f, n);
                }
            }
        }

        private void createSpellEntity(double d, double d2, double d3, double d4, float f, int n) {
            BlockPos blockPos = new BlockPos(d, d4, d2);
            boolean bl = false;
            double d5 = 0.0;
            do {
                BlockState blockState;
                BlockState blockState2;
                VoxelShape voxelShape;
                BlockPos blockPos2;
                if (!(blockState2 = Evoker.this.level.getBlockState(blockPos2 = blockPos.below())).isFaceSturdy(Evoker.this.level, blockPos2, Direction.UP)) continue;
                if (!Evoker.this.level.isEmptyBlock(blockPos) && !(voxelShape = (blockState = Evoker.this.level.getBlockState(blockPos)).getCollisionShape(Evoker.this.level, blockPos)).isEmpty()) {
                    d5 = voxelShape.max(Direction.Axis.Y);
                }
                bl = true;
                break;
            } while ((blockPos = blockPos.below()).getY() >= Mth.floor(d3) - 1);
            if (bl) {
                Evoker.this.level.addFreshEntity(new EvokerFangs(Evoker.this.level, d, (double)blockPos.getY() + d5, d2, f, n, Evoker.this));
            }
        }

        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_ATTACK;
        }

        @Override
        protected SpellcasterIllager.IllagerSpell getSpell() {
            return SpellcasterIllager.IllagerSpell.FANGS;
        }
    }

    class EvokerCastingSpellGoal
    extends SpellcasterIllager.SpellcasterCastingSpellGoal {
        private EvokerCastingSpellGoal() {
            super(Evoker.this);
        }

        @Override
        public void tick() {
            if (Evoker.this.getTarget() != null) {
                Evoker.this.getLookControl().setLookAt(Evoker.this.getTarget(), Evoker.this.getMaxHeadYRot(), Evoker.this.getMaxHeadXRot());
            } else if (Evoker.this.getWololoTarget() != null) {
                Evoker.this.getLookControl().setLookAt(Evoker.this.getWololoTarget(), Evoker.this.getMaxHeadYRot(), Evoker.this.getMaxHeadXRot());
            }
        }
    }

}

