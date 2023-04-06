/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.ShulkerSharedHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;

public class Shulker
extends AbstractGolem
implements Enemy {
    private static final UUID COVERED_ARMOR_MODIFIER_UUID = UUID.fromString("7E0292F2-9434-48D5-A29F-9583AF7DF27F");
    private static final AttributeModifier COVERED_ARMOR_MODIFIER = new AttributeModifier(COVERED_ARMOR_MODIFIER_UUID, "Covered armor bonus", 20.0, AttributeModifier.Operation.ADDITION);
    protected static final EntityDataAccessor<Direction> DATA_ATTACH_FACE_ID = SynchedEntityData.defineId(Shulker.class, EntityDataSerializers.DIRECTION);
    protected static final EntityDataAccessor<Optional<BlockPos>> DATA_ATTACH_POS_ID = SynchedEntityData.defineId(Shulker.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    protected static final EntityDataAccessor<Byte> DATA_PEEK_ID = SynchedEntityData.defineId(Shulker.class, EntityDataSerializers.BYTE);
    protected static final EntityDataAccessor<Byte> DATA_COLOR_ID = SynchedEntityData.defineId(Shulker.class, EntityDataSerializers.BYTE);
    private float currentPeekAmountO;
    private float currentPeekAmount;
    private BlockPos oldAttachPosition = null;
    private int clientSideTeleportInterpolation;

    public Shulker(EntityType<? extends Shulker> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 5;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(4, new ShulkerAttackGoal());
        this.goalSelector.addGoal(7, new ShulkerPeekGoal());
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]).setAlertOthers(new Class[0]));
        this.targetSelector.addGoal(2, new ShulkerNearestAttackGoal(this));
        this.targetSelector.addGoal(3, new ShulkerDefenseAttackGoal(this));
    }

    @Override
    protected boolean isMovementNoisy() {
        return false;
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SHULKER_AMBIENT;
    }

    @Override
    public void playAmbientSound() {
        if (!this.isClosed()) {
            super.playAmbientSound();
        }
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SHULKER_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        if (this.isClosed()) {
            return SoundEvents.SHULKER_HURT_CLOSED;
        }
        return SoundEvents.SHULKER_HURT;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ATTACH_FACE_ID, Direction.DOWN);
        this.entityData.define(DATA_ATTACH_POS_ID, Optional.empty());
        this.entityData.define(DATA_PEEK_ID, (byte)0);
        this.entityData.define(DATA_COLOR_ID, (byte)16);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 30.0);
    }

    @Override
    protected BodyRotationControl createBodyControl() {
        return new ShulkerBodyRotationControl(this);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.entityData.set(DATA_ATTACH_FACE_ID, Direction.from3DDataValue(compoundTag.getByte("AttachFace")));
        this.entityData.set(DATA_PEEK_ID, compoundTag.getByte("Peek"));
        this.entityData.set(DATA_COLOR_ID, compoundTag.getByte("Color"));
        if (compoundTag.contains("APX")) {
            int n = compoundTag.getInt("APX");
            int n2 = compoundTag.getInt("APY");
            int n3 = compoundTag.getInt("APZ");
            this.entityData.set(DATA_ATTACH_POS_ID, Optional.of(new BlockPos(n, n2, n3)));
        } else {
            this.entityData.set(DATA_ATTACH_POS_ID, Optional.empty());
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putByte("AttachFace", (byte)this.entityData.get(DATA_ATTACH_FACE_ID).get3DDataValue());
        compoundTag.putByte("Peek", this.entityData.get(DATA_PEEK_ID));
        compoundTag.putByte("Color", this.entityData.get(DATA_COLOR_ID));
        BlockPos blockPos = this.getAttachPosition();
        if (blockPos != null) {
            compoundTag.putInt("APX", blockPos.getX());
            compoundTag.putInt("APY", blockPos.getY());
            compoundTag.putInt("APZ", blockPos.getZ());
        }
    }

    @Override
    public void tick() {
        float f;
        super.tick();
        BlockPos blockPos = this.entityData.get(DATA_ATTACH_POS_ID).orElse(null);
        if (blockPos == null && !this.level.isClientSide) {
            blockPos = this.blockPosition();
            this.entityData.set(DATA_ATTACH_POS_ID, Optional.of(blockPos));
        }
        if (this.isPassenger()) {
            blockPos = null;
            this.yRot = f = this.getVehicle().yRot;
            this.yBodyRot = f;
            this.yBodyRotO = f;
            this.clientSideTeleportInterpolation = 0;
        } else if (!this.level.isClientSide) {
            Direction direction;
            BlockState blockState = this.level.getBlockState(blockPos);
            if (!blockState.isAir()) {
                if (blockState.is(Blocks.MOVING_PISTON)) {
                    direction = blockState.getValue(PistonBaseBlock.FACING);
                    if (this.level.isEmptyBlock(blockPos.relative(direction))) {
                        blockPos = blockPos.relative(direction);
                        this.entityData.set(DATA_ATTACH_POS_ID, Optional.of(blockPos));
                    } else {
                        this.teleportSomewhere();
                    }
                } else if (blockState.is(Blocks.PISTON_HEAD)) {
                    direction = blockState.getValue(PistonHeadBlock.FACING);
                    if (this.level.isEmptyBlock(blockPos.relative(direction))) {
                        blockPos = blockPos.relative(direction);
                        this.entityData.set(DATA_ATTACH_POS_ID, Optional.of(blockPos));
                    } else {
                        this.teleportSomewhere();
                    }
                } else {
                    this.teleportSomewhere();
                }
            }
            if (!this.canAttachOnBlockFace(blockPos, direction = this.getAttachFace())) {
                Direction direction2 = this.findAttachableFace(blockPos);
                if (direction2 != null) {
                    this.entityData.set(DATA_ATTACH_FACE_ID, direction2);
                } else {
                    this.teleportSomewhere();
                }
            }
        }
        f = (float)this.getRawPeekAmount() * 0.01f;
        this.currentPeekAmountO = this.currentPeekAmount;
        if (this.currentPeekAmount > f) {
            this.currentPeekAmount = Mth.clamp(this.currentPeekAmount - 0.05f, f, 1.0f);
        } else if (this.currentPeekAmount < f) {
            this.currentPeekAmount = Mth.clamp(this.currentPeekAmount + 0.05f, 0.0f, f);
        }
        if (blockPos != null) {
            List<Entity> list;
            if (this.level.isClientSide) {
                if (this.clientSideTeleportInterpolation > 0 && this.oldAttachPosition != null) {
                    --this.clientSideTeleportInterpolation;
                } else {
                    this.oldAttachPosition = blockPos;
                }
            }
            this.setPosAndOldPos((double)blockPos.getX() + 0.5, blockPos.getY(), (double)blockPos.getZ() + 0.5);
            double d = 0.5 - (double)Mth.sin((0.5f + this.currentPeekAmount) * 3.1415927f) * 0.5;
            double d2 = 0.5 - (double)Mth.sin((0.5f + this.currentPeekAmountO) * 3.1415927f) * 0.5;
            Direction direction = this.getAttachFace().getOpposite();
            this.setBoundingBox(new AABB(this.getX() - 0.5, this.getY(), this.getZ() - 0.5, this.getX() + 0.5, this.getY() + 1.0, this.getZ() + 0.5).expandTowards((double)direction.getStepX() * d, (double)direction.getStepY() * d, (double)direction.getStepZ() * d));
            double d3 = d - d2;
            if (d3 > 0.0 && !(list = this.level.getEntities(this, this.getBoundingBox())).isEmpty()) {
                for (Entity entity : list) {
                    if (entity instanceof Shulker || entity.noPhysics) continue;
                    entity.move(MoverType.SHULKER, new Vec3(d3 * (double)direction.getStepX(), d3 * (double)direction.getStepY(), d3 * (double)direction.getStepZ()));
                }
            }
        }
    }

    @Override
    public void move(MoverType moverType, Vec3 vec3) {
        if (moverType == MoverType.SHULKER_BOX) {
            this.teleportSomewhere();
        } else {
            super.move(moverType, vec3);
        }
    }

    @Override
    public void setPos(double d, double d2, double d3) {
        super.setPos(d, d2, d3);
        if (this.entityData == null || this.tickCount == 0) {
            return;
        }
        Optional<BlockPos> optional = this.entityData.get(DATA_ATTACH_POS_ID);
        Optional<BlockPos> optional2 = Optional.of(new BlockPos(d, d2, d3));
        if (!optional2.equals(optional)) {
            this.entityData.set(DATA_ATTACH_POS_ID, optional2);
            this.entityData.set(DATA_PEEK_ID, (byte)0);
            this.hasImpulse = true;
        }
    }

    @Nullable
    protected Direction findAttachableFace(BlockPos blockPos) {
        for (Direction direction : Direction.values()) {
            if (!this.canAttachOnBlockFace(blockPos, direction)) continue;
            return direction;
        }
        return null;
    }

    private boolean canAttachOnBlockFace(BlockPos blockPos, Direction direction) {
        return this.level.loadedAndEntityCanStandOnFace(blockPos.relative(direction), this, direction.getOpposite()) && this.level.noCollision(this, ShulkerSharedHelper.openBoundingBox(blockPos, direction.getOpposite()));
    }

    protected boolean teleportSomewhere() {
        if (this.isNoAi() || !this.isAlive()) {
            return true;
        }
        BlockPos blockPos = this.blockPosition();
        for (int i = 0; i < 5; ++i) {
            Direction direction;
            BlockPos blockPos2 = blockPos.offset(8 - this.random.nextInt(17), 8 - this.random.nextInt(17), 8 - this.random.nextInt(17));
            if (blockPos2.getY() <= 0 || !this.level.isEmptyBlock(blockPos2) || !this.level.getWorldBorder().isWithinBounds(blockPos2) || !this.level.noCollision(this, new AABB(blockPos2)) || (direction = this.findAttachableFace(blockPos2)) == null) continue;
            this.entityData.set(DATA_ATTACH_FACE_ID, direction);
            this.playSound(SoundEvents.SHULKER_TELEPORT, 1.0f, 1.0f);
            this.entityData.set(DATA_ATTACH_POS_ID, Optional.of(blockPos2));
            this.entityData.set(DATA_PEEK_ID, (byte)0);
            this.setTarget(null);
            return true;
        }
        return false;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.setDeltaMovement(Vec3.ZERO);
        if (!this.isNoAi()) {
            this.yBodyRotO = 0.0f;
            this.yBodyRot = 0.0f;
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
        BlockPos blockPos;
        if (DATA_ATTACH_POS_ID.equals(entityDataAccessor) && this.level.isClientSide && !this.isPassenger() && (blockPos = this.getAttachPosition()) != null) {
            if (this.oldAttachPosition == null) {
                this.oldAttachPosition = blockPos;
            } else {
                this.clientSideTeleportInterpolation = 6;
            }
            this.setPosAndOldPos((double)blockPos.getX() + 0.5, blockPos.getY(), (double)blockPos.getZ() + 0.5);
        }
        super.onSyncedDataUpdated(entityDataAccessor);
    }

    @Override
    public void lerpTo(double d, double d2, double d3, float f, float f2, int n, boolean bl) {
        this.lerpSteps = 0;
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        Entity entity;
        if (this.isClosed() && (entity = damageSource.getDirectEntity()) instanceof AbstractArrow) {
            return false;
        }
        if (super.hurt(damageSource, f)) {
            if ((double)this.getHealth() < (double)this.getMaxHealth() * 0.5 && this.random.nextInt(4) == 0) {
                this.teleportSomewhere();
            }
            return true;
        }
        return false;
    }

    private boolean isClosed() {
        return this.getRawPeekAmount() == 0;
    }

    @Override
    public boolean canBeCollidedWith() {
        return this.isAlive();
    }

    public Direction getAttachFace() {
        return this.entityData.get(DATA_ATTACH_FACE_ID);
    }

    @Nullable
    public BlockPos getAttachPosition() {
        return this.entityData.get(DATA_ATTACH_POS_ID).orElse(null);
    }

    public void setAttachPosition(@Nullable BlockPos blockPos) {
        this.entityData.set(DATA_ATTACH_POS_ID, Optional.ofNullable(blockPos));
    }

    public int getRawPeekAmount() {
        return this.entityData.get(DATA_PEEK_ID).byteValue();
    }

    public void setRawPeekAmount(int n) {
        if (!this.level.isClientSide) {
            this.getAttribute(Attributes.ARMOR).removeModifier(COVERED_ARMOR_MODIFIER);
            if (n == 0) {
                this.getAttribute(Attributes.ARMOR).addPermanentModifier(COVERED_ARMOR_MODIFIER);
                this.playSound(SoundEvents.SHULKER_CLOSE, 1.0f, 1.0f);
            } else {
                this.playSound(SoundEvents.SHULKER_OPEN, 1.0f, 1.0f);
            }
        }
        this.entityData.set(DATA_PEEK_ID, (byte)n);
    }

    public float getClientPeekAmount(float f) {
        return Mth.lerp(f, this.currentPeekAmountO, this.currentPeekAmount);
    }

    public int getClientSideTeleportInterpolation() {
        return this.clientSideTeleportInterpolation;
    }

    public BlockPos getOldAttachPosition() {
        return this.oldAttachPosition;
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
        return 0.5f;
    }

    @Override
    public int getMaxHeadXRot() {
        return 180;
    }

    @Override
    public int getMaxHeadYRot() {
        return 180;
    }

    @Override
    public void push(Entity entity) {
    }

    @Override
    public float getPickRadius() {
        return 0.0f;
    }

    public boolean hasValidInterpolationPositions() {
        return this.oldAttachPosition != null && this.getAttachPosition() != null;
    }

    @Nullable
    public DyeColor getColor() {
        Byte by = this.entityData.get(DATA_COLOR_ID);
        if (by == 16 || by > 15) {
            return null;
        }
        return DyeColor.byId(by.byteValue());
    }

    static class ShulkerDefenseAttackGoal
    extends NearestAttackableTargetGoal<LivingEntity> {
        public ShulkerDefenseAttackGoal(Shulker shulker) {
            super(shulker, LivingEntity.class, 10, true, false, livingEntity -> livingEntity instanceof Enemy);
        }

        @Override
        public boolean canUse() {
            if (this.mob.getTeam() == null) {
                return false;
            }
            return super.canUse();
        }

        @Override
        protected AABB getTargetSearchArea(double d) {
            Direction direction = ((Shulker)this.mob).getAttachFace();
            if (direction.getAxis() == Direction.Axis.X) {
                return this.mob.getBoundingBox().inflate(4.0, d, d);
            }
            if (direction.getAxis() == Direction.Axis.Z) {
                return this.mob.getBoundingBox().inflate(d, d, 4.0);
            }
            return this.mob.getBoundingBox().inflate(d, 4.0, d);
        }
    }

    class ShulkerNearestAttackGoal
    extends NearestAttackableTargetGoal<Player> {
        public ShulkerNearestAttackGoal(Shulker shulker2) {
            super(shulker2, Player.class, true);
        }

        @Override
        public boolean canUse() {
            if (Shulker.this.level.getDifficulty() == Difficulty.PEACEFUL) {
                return false;
            }
            return super.canUse();
        }

        @Override
        protected AABB getTargetSearchArea(double d) {
            Direction direction = ((Shulker)this.mob).getAttachFace();
            if (direction.getAxis() == Direction.Axis.X) {
                return this.mob.getBoundingBox().inflate(4.0, d, d);
            }
            if (direction.getAxis() == Direction.Axis.Z) {
                return this.mob.getBoundingBox().inflate(d, d, 4.0);
            }
            return this.mob.getBoundingBox().inflate(d, 4.0, d);
        }
    }

    class ShulkerAttackGoal
    extends Goal {
        private int attackTime;

        public ShulkerAttackGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity livingEntity = Shulker.this.getTarget();
            if (livingEntity == null || !livingEntity.isAlive()) {
                return false;
            }
            return Shulker.this.level.getDifficulty() != Difficulty.PEACEFUL;
        }

        @Override
        public void start() {
            this.attackTime = 20;
            Shulker.this.setRawPeekAmount(100);
        }

        @Override
        public void stop() {
            Shulker.this.setRawPeekAmount(0);
        }

        @Override
        public void tick() {
            if (Shulker.this.level.getDifficulty() == Difficulty.PEACEFUL) {
                return;
            }
            --this.attackTime;
            LivingEntity livingEntity = Shulker.this.getTarget();
            Shulker.this.getLookControl().setLookAt(livingEntity, 180.0f, 180.0f);
            double d = Shulker.this.distanceToSqr(livingEntity);
            if (d < 400.0) {
                if (this.attackTime <= 0) {
                    this.attackTime = 20 + Shulker.this.random.nextInt(10) * 20 / 2;
                    Shulker.this.level.addFreshEntity(new ShulkerBullet(Shulker.this.level, Shulker.this, livingEntity, Shulker.this.getAttachFace().getAxis()));
                    Shulker.this.playSound(SoundEvents.SHULKER_SHOOT, 2.0f, (Shulker.this.random.nextFloat() - Shulker.this.random.nextFloat()) * 0.2f + 1.0f);
                }
            } else {
                Shulker.this.setTarget(null);
            }
            super.tick();
        }
    }

    class ShulkerPeekGoal
    extends Goal {
        private int peekTime;

        private ShulkerPeekGoal() {
        }

        @Override
        public boolean canUse() {
            return Shulker.this.getTarget() == null && Shulker.this.random.nextInt(40) == 0;
        }

        @Override
        public boolean canContinueToUse() {
            return Shulker.this.getTarget() == null && this.peekTime > 0;
        }

        @Override
        public void start() {
            this.peekTime = 20 * (1 + Shulker.this.random.nextInt(3));
            Shulker.this.setRawPeekAmount(30);
        }

        @Override
        public void stop() {
            if (Shulker.this.getTarget() == null) {
                Shulker.this.setRawPeekAmount(0);
            }
        }

        @Override
        public void tick() {
            --this.peekTime;
        }
    }

    class ShulkerBodyRotationControl
    extends BodyRotationControl {
        public ShulkerBodyRotationControl(Mob mob) {
            super(mob);
        }

        @Override
        public void clientTick() {
        }
    }

}

