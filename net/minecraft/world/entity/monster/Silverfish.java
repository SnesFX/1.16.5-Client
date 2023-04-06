/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.InfestedBlock;
import net.minecraft.world.level.block.state.BlockState;

public class Silverfish
extends Monster {
    private SilverfishWakeUpFriendsGoal friendsGoal;

    public Silverfish(EntityType<? extends Silverfish> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        this.friendsGoal = new SilverfishWakeUpFriendsGoal(this);
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(3, this.friendsGoal);
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(5, new SilverfishMergeWithStoneGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]).setAlertOthers(new Class[0]));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<Player>(this, Player.class, true));
    }

    @Override
    public double getMyRidingOffset() {
        return 0.1;
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
        return 0.13f;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 8.0).add(Attributes.MOVEMENT_SPEED, 0.25).add(Attributes.ATTACK_DAMAGE, 1.0);
    }

    @Override
    protected boolean isMovementNoisy() {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SILVERFISH_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.SILVERFISH_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SILVERFISH_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos blockPos, BlockState blockState) {
        this.playSound(SoundEvents.SILVERFISH_STEP, 0.15f, 1.0f);
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        if (this.isInvulnerableTo(damageSource)) {
            return false;
        }
        if ((damageSource instanceof EntityDamageSource || damageSource == DamageSource.MAGIC) && this.friendsGoal != null) {
            this.friendsGoal.notifyHurt();
        }
        return super.hurt(damageSource, f);
    }

    @Override
    public void tick() {
        this.yBodyRot = this.yRot;
        super.tick();
    }

    @Override
    public void setYBodyRot(float f) {
        this.yRot = f;
        super.setYBodyRot(f);
    }

    @Override
    public float getWalkTargetValue(BlockPos blockPos, LevelReader levelReader) {
        if (InfestedBlock.isCompatibleHostBlock(levelReader.getBlockState(blockPos.below()))) {
            return 10.0f;
        }
        return super.getWalkTargetValue(blockPos, levelReader);
    }

    public static boolean checkSliverfishSpawnRules(EntityType<Silverfish> entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, Random random) {
        if (Silverfish.checkAnyLightMonsterSpawnRules(entityType, levelAccessor, mobSpawnType, blockPos, random)) {
            Player player = levelAccessor.getNearestPlayer((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, 5.0, true);
            return player == null;
        }
        return false;
    }

    @Override
    public MobType getMobType() {
        return MobType.ARTHROPOD;
    }

    static class SilverfishMergeWithStoneGoal
    extends RandomStrollGoal {
        private Direction selectedDirection;
        private boolean doMerge;

        public SilverfishMergeWithStoneGoal(Silverfish silverfish) {
            super(silverfish, 1.0, 10);
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (this.mob.getTarget() != null) {
                return false;
            }
            if (!this.mob.getNavigation().isDone()) {
                return false;
            }
            Random random = this.mob.getRandom();
            if (this.mob.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && random.nextInt(10) == 0) {
                this.selectedDirection = Direction.getRandom(random);
                BlockPos blockPos = new BlockPos(this.mob.getX(), this.mob.getY() + 0.5, this.mob.getZ()).relative(this.selectedDirection);
                BlockState blockState = this.mob.level.getBlockState(blockPos);
                if (InfestedBlock.isCompatibleHostBlock(blockState)) {
                    this.doMerge = true;
                    return true;
                }
            }
            this.doMerge = false;
            return super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            if (this.doMerge) {
                return false;
            }
            return super.canContinueToUse();
        }

        @Override
        public void start() {
            if (!this.doMerge) {
                super.start();
                return;
            }
            Level level = this.mob.level;
            BlockPos blockPos = new BlockPos(this.mob.getX(), this.mob.getY() + 0.5, this.mob.getZ()).relative(this.selectedDirection);
            BlockState blockState = level.getBlockState(blockPos);
            if (InfestedBlock.isCompatibleHostBlock(blockState)) {
                level.setBlock(blockPos, InfestedBlock.stateByHostBlock(blockState.getBlock()), 3);
                this.mob.spawnAnim();
                this.mob.remove();
            }
        }
    }

    static class SilverfishWakeUpFriendsGoal
    extends Goal {
        private final Silverfish silverfish;
        private int lookForFriends;

        public SilverfishWakeUpFriendsGoal(Silverfish silverfish) {
            this.silverfish = silverfish;
        }

        public void notifyHurt() {
            if (this.lookForFriends == 0) {
                this.lookForFriends = 20;
            }
        }

        @Override
        public boolean canUse() {
            return this.lookForFriends > 0;
        }

        @Override
        public void tick() {
            --this.lookForFriends;
            if (this.lookForFriends <= 0) {
                Level level = this.silverfish.level;
                Random random = this.silverfish.getRandom();
                BlockPos blockPos = this.silverfish.blockPosition();
                int n = 0;
                block0 : while (n <= 5 && n >= -5) {
                    int n2 = 0;
                    while (n2 <= 10 && n2 >= -10) {
                        int n3 = 0;
                        while (n3 <= 10 && n3 >= -10) {
                            BlockPos blockPos2 = blockPos.offset(n2, n, n3);
                            BlockState blockState = level.getBlockState(blockPos2);
                            Block block = blockState.getBlock();
                            if (block instanceof InfestedBlock) {
                                if (level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                                    level.destroyBlock(blockPos2, true, this.silverfish);
                                } else {
                                    level.setBlock(blockPos2, ((InfestedBlock)block).getHostBlock().defaultBlockState(), 3);
                                }
                                if (random.nextBoolean()) break block0;
                            }
                            n3 = (n3 <= 0 ? 1 : 0) - n3;
                        }
                        n2 = (n2 <= 0 ? 1 : 0) - n2;
                    }
                    n = (n <= 0 ? 1 : 0) - n;
                }
            }
        }
    }

}

