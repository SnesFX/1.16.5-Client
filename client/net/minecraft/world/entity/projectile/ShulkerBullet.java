/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.projectile;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ShulkerBullet
extends Projectile {
    private Entity finalTarget;
    @Nullable
    private Direction currentMoveDirection;
    private int flightSteps;
    private double targetDeltaX;
    private double targetDeltaY;
    private double targetDeltaZ;
    @Nullable
    private UUID targetId;

    public ShulkerBullet(EntityType<? extends ShulkerBullet> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
    }

    public ShulkerBullet(Level level, double d, double d2, double d3, double d4, double d5, double d6) {
        this(EntityType.SHULKER_BULLET, level);
        this.moveTo(d, d2, d3, this.yRot, this.xRot);
        this.setDeltaMovement(d4, d5, d6);
    }

    public ShulkerBullet(Level level, LivingEntity livingEntity, Entity entity, Direction.Axis axis) {
        this(EntityType.SHULKER_BULLET, level);
        this.setOwner(livingEntity);
        BlockPos blockPos = livingEntity.blockPosition();
        double d = (double)blockPos.getX() + 0.5;
        double d2 = (double)blockPos.getY() + 0.5;
        double d3 = (double)blockPos.getZ() + 0.5;
        this.moveTo(d, d2, d3, this.yRot, this.xRot);
        this.finalTarget = entity;
        this.currentMoveDirection = Direction.UP;
        this.selectNextMoveDirection(axis);
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        if (this.finalTarget != null) {
            compoundTag.putUUID("Target", this.finalTarget.getUUID());
        }
        if (this.currentMoveDirection != null) {
            compoundTag.putInt("Dir", this.currentMoveDirection.get3DDataValue());
        }
        compoundTag.putInt("Steps", this.flightSteps);
        compoundTag.putDouble("TXD", this.targetDeltaX);
        compoundTag.putDouble("TYD", this.targetDeltaY);
        compoundTag.putDouble("TZD", this.targetDeltaZ);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.flightSteps = compoundTag.getInt("Steps");
        this.targetDeltaX = compoundTag.getDouble("TXD");
        this.targetDeltaY = compoundTag.getDouble("TYD");
        this.targetDeltaZ = compoundTag.getDouble("TZD");
        if (compoundTag.contains("Dir", 99)) {
            this.currentMoveDirection = Direction.from3DDataValue(compoundTag.getInt("Dir"));
        }
        if (compoundTag.hasUUID("Target")) {
            this.targetId = compoundTag.getUUID("Target");
        }
    }

    @Override
    protected void defineSynchedData() {
    }

    private void setMoveDirection(@Nullable Direction direction) {
        this.currentMoveDirection = direction;
    }

    private void selectNextMoveDirection(@Nullable Direction.Axis axis) {
        BlockPos blockPos;
        double d = 0.5;
        if (this.finalTarget == null) {
            blockPos = this.blockPosition().below();
        } else {
            d = (double)this.finalTarget.getBbHeight() * 0.5;
            blockPos = new BlockPos(this.finalTarget.getX(), this.finalTarget.getY() + d, this.finalTarget.getZ());
        }
        double d2 = (double)blockPos.getX() + 0.5;
        double d3 = (double)blockPos.getY() + d;
        double d4 = (double)blockPos.getZ() + 0.5;
        Direction direction = null;
        if (!blockPos.closerThan(this.position(), 2.0)) {
            BlockPos blockPos2 = this.blockPosition();
            ArrayList arrayList = Lists.newArrayList();
            if (axis != Direction.Axis.X) {
                if (blockPos2.getX() < blockPos.getX() && this.level.isEmptyBlock(blockPos2.east())) {
                    arrayList.add(Direction.EAST);
                } else if (blockPos2.getX() > blockPos.getX() && this.level.isEmptyBlock(blockPos2.west())) {
                    arrayList.add(Direction.WEST);
                }
            }
            if (axis != Direction.Axis.Y) {
                if (blockPos2.getY() < blockPos.getY() && this.level.isEmptyBlock(blockPos2.above())) {
                    arrayList.add(Direction.UP);
                } else if (blockPos2.getY() > blockPos.getY() && this.level.isEmptyBlock(blockPos2.below())) {
                    arrayList.add(Direction.DOWN);
                }
            }
            if (axis != Direction.Axis.Z) {
                if (blockPos2.getZ() < blockPos.getZ() && this.level.isEmptyBlock(blockPos2.south())) {
                    arrayList.add(Direction.SOUTH);
                } else if (blockPos2.getZ() > blockPos.getZ() && this.level.isEmptyBlock(blockPos2.north())) {
                    arrayList.add(Direction.NORTH);
                }
            }
            direction = Direction.getRandom(this.random);
            if (arrayList.isEmpty()) {
                for (int i = 5; !this.level.isEmptyBlock(blockPos2.relative(direction)) && i > 0; --i) {
                    direction = Direction.getRandom(this.random);
                }
            } else {
                direction = (Direction)arrayList.get(this.random.nextInt(arrayList.size()));
            }
            d2 = this.getX() + (double)direction.getStepX();
            d3 = this.getY() + (double)direction.getStepY();
            d4 = this.getZ() + (double)direction.getStepZ();
        }
        this.setMoveDirection(direction);
        double d5 = d2 - this.getX();
        double d6 = d3 - this.getY();
        double d7 = d4 - this.getZ();
        double d8 = Mth.sqrt(d5 * d5 + d6 * d6 + d7 * d7);
        if (d8 == 0.0) {
            this.targetDeltaX = 0.0;
            this.targetDeltaY = 0.0;
            this.targetDeltaZ = 0.0;
        } else {
            this.targetDeltaX = d5 / d8 * 0.15;
            this.targetDeltaY = d6 / d8 * 0.15;
            this.targetDeltaZ = d7 / d8 * 0.15;
        }
        this.hasImpulse = true;
        this.flightSteps = 10 + this.random.nextInt(5) * 10;
    }

    @Override
    public void checkDespawn() {
        if (this.level.getDifficulty() == Difficulty.PEACEFUL) {
            this.remove();
        }
    }

    @Override
    public void tick() {
        Object object;
        super.tick();
        if (!this.level.isClientSide) {
            if (this.finalTarget == null && this.targetId != null) {
                this.finalTarget = ((ServerLevel)this.level).getEntity(this.targetId);
                if (this.finalTarget == null) {
                    this.targetId = null;
                }
            }
            if (!(this.finalTarget == null || !this.finalTarget.isAlive() || this.finalTarget instanceof Player && ((Player)this.finalTarget).isSpectator())) {
                this.targetDeltaX = Mth.clamp(this.targetDeltaX * 1.025, -1.0, 1.0);
                this.targetDeltaY = Mth.clamp(this.targetDeltaY * 1.025, -1.0, 1.0);
                this.targetDeltaZ = Mth.clamp(this.targetDeltaZ * 1.025, -1.0, 1.0);
                object = this.getDeltaMovement();
                this.setDeltaMovement(((Vec3)object).add((this.targetDeltaX - ((Vec3)object).x) * 0.2, (this.targetDeltaY - ((Vec3)object).y) * 0.2, (this.targetDeltaZ - ((Vec3)object).z) * 0.2));
            } else if (!this.isNoGravity()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.04, 0.0));
            }
            object = ProjectileUtil.getHitResult(this, this::canHitEntity);
            if (((HitResult)object).getType() != HitResult.Type.MISS) {
                this.onHit((HitResult)object);
            }
        }
        this.checkInsideBlocks();
        object = this.getDeltaMovement();
        this.setPos(this.getX() + ((Vec3)object).x, this.getY() + ((Vec3)object).y, this.getZ() + ((Vec3)object).z);
        ProjectileUtil.rotateTowardsMovement(this, 0.5f);
        if (this.level.isClientSide) {
            this.level.addParticle(ParticleTypes.END_ROD, this.getX() - ((Vec3)object).x, this.getY() - ((Vec3)object).y + 0.15, this.getZ() - ((Vec3)object).z, 0.0, 0.0, 0.0);
        } else if (this.finalTarget != null && !this.finalTarget.removed) {
            if (this.flightSteps > 0) {
                --this.flightSteps;
                if (this.flightSteps == 0) {
                    this.selectNextMoveDirection(this.currentMoveDirection == null ? null : this.currentMoveDirection.getAxis());
                }
            }
            if (this.currentMoveDirection != null) {
                BlockPos blockPos = this.blockPosition();
                Direction.Axis axis = this.currentMoveDirection.getAxis();
                if (this.level.loadedAndEntityCanStandOn(blockPos.relative(this.currentMoveDirection), this)) {
                    this.selectNextMoveDirection(axis);
                } else {
                    BlockPos blockPos2 = this.finalTarget.blockPosition();
                    if (axis == Direction.Axis.X && blockPos.getX() == blockPos2.getX() || axis == Direction.Axis.Z && blockPos.getZ() == blockPos2.getZ() || axis == Direction.Axis.Y && blockPos.getY() == blockPos2.getY()) {
                        this.selectNextMoveDirection(axis);
                    }
                }
            }
        }
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return super.canHitEntity(entity) && !entity.noPhysics;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double d) {
        return d < 16384.0;
    }

    @Override
    public float getBrightness() {
        return 1.0f;
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        Entity entity2 = this.getOwner();
        LivingEntity livingEntity = entity2 instanceof LivingEntity ? (LivingEntity)entity2 : null;
        boolean bl = entity.hurt(DamageSource.indirectMobAttack(this, livingEntity).setProjectile(), 4.0f);
        if (bl) {
            this.doEnchantDamageEffects(livingEntity, entity);
            if (entity instanceof LivingEntity) {
                ((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.LEVITATION, 200));
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        ((ServerLevel)this.level).sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 2, 0.2, 0.2, 0.2, 0.0);
        this.playSound(SoundEvents.SHULKER_BULLET_HIT, 1.0f, 1.0f);
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        this.remove();
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        if (!this.level.isClientSide) {
            this.playSound(SoundEvents.SHULKER_BULLET_HURT, 1.0f, 1.0f);
            ((ServerLevel)this.level).sendParticles(ParticleTypes.CRIT, this.getX(), this.getY(), this.getZ(), 15, 0.2, 0.2, 0.2, 0.0);
            this.remove();
        }
        return true;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}

