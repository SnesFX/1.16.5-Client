/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.vehicle;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ServerboundPaddleBoatPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WaterlilyBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Boat
extends Entity {
    private static final EntityDataAccessor<Integer> DATA_ID_HURT = SynchedEntityData.defineId(Boat.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ID_HURTDIR = SynchedEntityData.defineId(Boat.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_ID_DAMAGE = SynchedEntityData.defineId(Boat.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_ID_TYPE = SynchedEntityData.defineId(Boat.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_ID_PADDLE_LEFT = SynchedEntityData.defineId(Boat.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_ID_PADDLE_RIGHT = SynchedEntityData.defineId(Boat.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_ID_BUBBLE_TIME = SynchedEntityData.defineId(Boat.class, EntityDataSerializers.INT);
    private final float[] paddlePositions = new float[2];
    private float invFriction;
    private float outOfControlTicks;
    private float deltaRotation;
    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYRot;
    private double lerpXRot;
    private boolean inputLeft;
    private boolean inputRight;
    private boolean inputUp;
    private boolean inputDown;
    private double waterLevel;
    private float landFriction;
    private Status status;
    private Status oldStatus;
    private double lastYd;
    private boolean isAboveBubbleColumn;
    private boolean bubbleColumnDirectionIsDown;
    private float bubbleMultiplier;
    private float bubbleAngle;
    private float bubbleAngleO;

    public Boat(EntityType<? extends Boat> entityType, Level level) {
        super(entityType, level);
        this.blocksBuilding = true;
    }

    public Boat(Level level, double d, double d2, double d3) {
        this(EntityType.BOAT, level);
        this.setPos(d, d2, d3);
        this.setDeltaMovement(Vec3.ZERO);
        this.xo = d;
        this.yo = d2;
        this.zo = d3;
    }

    @Override
    protected float getEyeHeight(Pose pose, EntityDimensions entityDimensions) {
        return entityDimensions.height;
    }

    @Override
    protected boolean isMovementNoisy() {
        return false;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_ID_HURT, 0);
        this.entityData.define(DATA_ID_HURTDIR, 1);
        this.entityData.define(DATA_ID_DAMAGE, Float.valueOf(0.0f));
        this.entityData.define(DATA_ID_TYPE, Type.OAK.ordinal());
        this.entityData.define(DATA_ID_PADDLE_LEFT, false);
        this.entityData.define(DATA_ID_PADDLE_RIGHT, false);
        this.entityData.define(DATA_ID_BUBBLE_TIME, 0);
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return Boat.canVehicleCollide(this, entity);
    }

    public static boolean canVehicleCollide(Entity entity, Entity entity2) {
        return (entity2.canBeCollidedWith() || entity2.isPushable()) && !entity.isPassengerOfSameVehicle(entity2);
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    protected Vec3 getRelativePortalPosition(Direction.Axis axis, BlockUtil.FoundRectangle foundRectangle) {
        return LivingEntity.resetForwardDirectionOfRelativePortalPosition(super.getRelativePortalPosition(axis, foundRectangle));
    }

    @Override
    public double getPassengersRidingOffset() {
        return -0.1;
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        boolean bl;
        if (this.isInvulnerableTo(damageSource)) {
            return false;
        }
        if (this.level.isClientSide || this.removed) {
            return true;
        }
        this.setHurtDir(-this.getHurtDir());
        this.setHurtTime(10);
        this.setDamage(this.getDamage() + f * 10.0f);
        this.markHurt();
        boolean bl2 = bl = damageSource.getEntity() instanceof Player && ((Player)damageSource.getEntity()).abilities.instabuild;
        if (bl || this.getDamage() > 40.0f) {
            if (!bl && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                this.spawnAtLocation(this.getDropItem());
            }
            this.remove();
        }
        return true;
    }

    @Override
    public void onAboveBubbleCol(boolean bl) {
        if (!this.level.isClientSide) {
            this.isAboveBubbleColumn = true;
            this.bubbleColumnDirectionIsDown = bl;
            if (this.getBubbleTime() == 0) {
                this.setBubbleTime(60);
            }
        }
        this.level.addParticle(ParticleTypes.SPLASH, this.getX() + (double)this.random.nextFloat(), this.getY() + 0.7, this.getZ() + (double)this.random.nextFloat(), 0.0, 0.0, 0.0);
        if (this.random.nextInt(20) == 0) {
            this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), this.getSwimSplashSound(), this.getSoundSource(), 1.0f, 0.8f + 0.4f * this.random.nextFloat(), false);
        }
    }

    @Override
    public void push(Entity entity) {
        if (entity instanceof Boat) {
            if (entity.getBoundingBox().minY < this.getBoundingBox().maxY) {
                super.push(entity);
            }
        } else if (entity.getBoundingBox().minY <= this.getBoundingBox().minY) {
            super.push(entity);
        }
    }

    public Item getDropItem() {
        switch (this.getBoatType()) {
            default: {
                return Items.OAK_BOAT;
            }
            case SPRUCE: {
                return Items.SPRUCE_BOAT;
            }
            case BIRCH: {
                return Items.BIRCH_BOAT;
            }
            case JUNGLE: {
                return Items.JUNGLE_BOAT;
            }
            case ACACIA: {
                return Items.ACACIA_BOAT;
            }
            case DARK_OAK: 
        }
        return Items.DARK_OAK_BOAT;
    }

    @Override
    public void animateHurt() {
        this.setHurtDir(-this.getHurtDir());
        this.setHurtTime(10);
        this.setDamage(this.getDamage() * 11.0f);
    }

    @Override
    public boolean isPickable() {
        return !this.removed;
    }

    @Override
    public void lerpTo(double d, double d2, double d3, float f, float f2, int n, boolean bl) {
        this.lerpX = d;
        this.lerpY = d2;
        this.lerpZ = d3;
        this.lerpYRot = f;
        this.lerpXRot = f2;
        this.lerpSteps = 10;
    }

    @Override
    public Direction getMotionDirection() {
        return this.getDirection().getClockWise();
    }

    @Override
    public void tick() {
        this.oldStatus = this.status;
        this.status = this.getStatus();
        this.outOfControlTicks = this.status == Status.UNDER_WATER || this.status == Status.UNDER_FLOWING_WATER ? (this.outOfControlTicks += 1.0f) : 0.0f;
        if (!this.level.isClientSide && this.outOfControlTicks >= 60.0f) {
            this.ejectPassengers();
        }
        if (this.getHurtTime() > 0) {
            this.setHurtTime(this.getHurtTime() - 1);
        }
        if (this.getDamage() > 0.0f) {
            this.setDamage(this.getDamage() - 1.0f);
        }
        super.tick();
        this.tickLerp();
        if (this.isControlledByLocalInstance()) {
            if (this.getPassengers().isEmpty() || !(this.getPassengers().get(0) instanceof Player)) {
                this.setPaddleState(false, false);
            }
            this.floatBoat();
            if (this.level.isClientSide) {
                this.controlBoat();
                this.level.sendPacketToServer(new ServerboundPaddleBoatPacket(this.getPaddleState(0), this.getPaddleState(1)));
            }
            this.move(MoverType.SELF, this.getDeltaMovement());
        } else {
            this.setDeltaMovement(Vec3.ZERO);
        }
        this.tickBubbleColumn();
        for (int i = 0; i <= 1; ++i) {
            if (this.getPaddleState(i)) {
                SoundEvent soundEvent;
                if (!this.isSilent() && (double)(this.paddlePositions[i] % 6.2831855f) <= 0.7853981852531433 && ((double)this.paddlePositions[i] + 0.39269909262657166) % 6.2831854820251465 >= 0.7853981852531433 && (soundEvent = this.getPaddleSound()) != null) {
                    Vec3 vec3 = this.getViewVector(1.0f);
                    double d = i == 1 ? -vec3.z : vec3.z;
                    double d2 = i == 1 ? vec3.x : -vec3.x;
                    this.level.playSound(null, this.getX() + d, this.getY(), this.getZ() + d2, soundEvent, this.getSoundSource(), 1.0f, 0.8f + 0.4f * this.random.nextFloat());
                }
                float[] arrf = this.paddlePositions;
                int n = i;
                arrf[n] = (float)((double)arrf[n] + 0.39269909262657166);
                continue;
            }
            this.paddlePositions[i] = 0.0f;
        }
        this.checkInsideBlocks();
        List<Entity> list = this.level.getEntities(this, this.getBoundingBox().inflate(0.20000000298023224, -0.009999999776482582, 0.20000000298023224), EntitySelector.pushableBy(this));
        if (!list.isEmpty()) {
            boolean bl = !this.level.isClientSide && !(this.getControllingPassenger() instanceof Player);
            for (int i = 0; i < list.size(); ++i) {
                Entity entity = list.get(i);
                if (entity.hasPassenger(this)) continue;
                if (bl && this.getPassengers().size() < 2 && !entity.isPassenger() && entity.getBbWidth() < this.getBbWidth() && entity instanceof LivingEntity && !(entity instanceof WaterAnimal) && !(entity instanceof Player)) {
                    entity.startRiding(this);
                    continue;
                }
                this.push(entity);
            }
        }
    }

    private void tickBubbleColumn() {
        if (this.level.isClientSide) {
            int n = this.getBubbleTime();
            this.bubbleMultiplier = n > 0 ? (this.bubbleMultiplier += 0.05f) : (this.bubbleMultiplier -= 0.1f);
            this.bubbleMultiplier = Mth.clamp(this.bubbleMultiplier, 0.0f, 1.0f);
            this.bubbleAngleO = this.bubbleAngle;
            this.bubbleAngle = 10.0f * (float)Math.sin(0.5f * (float)this.level.getGameTime()) * this.bubbleMultiplier;
        } else {
            int n;
            if (!this.isAboveBubbleColumn) {
                this.setBubbleTime(0);
            }
            if ((n = this.getBubbleTime()) > 0) {
                this.setBubbleTime(--n);
                int n2 = 60 - n - 1;
                if (n2 > 0 && n == 0) {
                    this.setBubbleTime(0);
                    Vec3 vec3 = this.getDeltaMovement();
                    if (this.bubbleColumnDirectionIsDown) {
                        this.setDeltaMovement(vec3.add(0.0, -0.7, 0.0));
                        this.ejectPassengers();
                    } else {
                        this.setDeltaMovement(vec3.x, this.hasPassenger(Player.class) ? 2.7 : 0.6, vec3.z);
                    }
                }
                this.isAboveBubbleColumn = false;
            }
        }
    }

    @Nullable
    protected SoundEvent getPaddleSound() {
        switch (this.getStatus()) {
            case IN_WATER: 
            case UNDER_WATER: 
            case UNDER_FLOWING_WATER: {
                return SoundEvents.BOAT_PADDLE_WATER;
            }
            case ON_LAND: {
                return SoundEvents.BOAT_PADDLE_LAND;
            }
        }
        return null;
    }

    private void tickLerp() {
        if (this.isControlledByLocalInstance()) {
            this.lerpSteps = 0;
            this.setPacketCoordinates(this.getX(), this.getY(), this.getZ());
        }
        if (this.lerpSteps <= 0) {
            return;
        }
        double d = this.getX() + (this.lerpX - this.getX()) / (double)this.lerpSteps;
        double d2 = this.getY() + (this.lerpY - this.getY()) / (double)this.lerpSteps;
        double d3 = this.getZ() + (this.lerpZ - this.getZ()) / (double)this.lerpSteps;
        double d4 = Mth.wrapDegrees(this.lerpYRot - (double)this.yRot);
        this.yRot = (float)((double)this.yRot + d4 / (double)this.lerpSteps);
        this.xRot = (float)((double)this.xRot + (this.lerpXRot - (double)this.xRot) / (double)this.lerpSteps);
        --this.lerpSteps;
        this.setPos(d, d2, d3);
        this.setRot(this.yRot, this.xRot);
    }

    public void setPaddleState(boolean bl, boolean bl2) {
        this.entityData.set(DATA_ID_PADDLE_LEFT, bl);
        this.entityData.set(DATA_ID_PADDLE_RIGHT, bl2);
    }

    public float getRowingTime(int n, float f) {
        if (this.getPaddleState(n)) {
            return (float)Mth.clampedLerp((double)this.paddlePositions[n] - 0.39269909262657166, this.paddlePositions[n], f);
        }
        return 0.0f;
    }

    private Status getStatus() {
        Status status = this.isUnderwater();
        if (status != null) {
            this.waterLevel = this.getBoundingBox().maxY;
            return status;
        }
        if (this.checkInWater()) {
            return Status.IN_WATER;
        }
        float f = this.getGroundFriction();
        if (f > 0.0f) {
            this.landFriction = f;
            return Status.ON_LAND;
        }
        return Status.IN_AIR;
    }

    public float getWaterLevelAbove() {
        AABB aABB = this.getBoundingBox();
        int n = Mth.floor(aABB.minX);
        int n2 = Mth.ceil(aABB.maxX);
        int n3 = Mth.floor(aABB.maxY);
        int n4 = Mth.ceil(aABB.maxY - this.lastYd);
        int n5 = Mth.floor(aABB.minZ);
        int n6 = Mth.ceil(aABB.maxZ);
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        block0 : for (int i = n3; i < n4; ++i) {
            float f = 0.0f;
            for (int j = n; j < n2; ++j) {
                for (int k = n5; k < n6; ++k) {
                    mutableBlockPos.set(j, i, k);
                    FluidState fluidState = this.level.getFluidState(mutableBlockPos);
                    if (fluidState.is(FluidTags.WATER)) {
                        f = Math.max(f, fluidState.getHeight(this.level, mutableBlockPos));
                    }
                    if (f >= 1.0f) continue block0;
                }
            }
            if (!(f < 1.0f)) continue;
            return (float)mutableBlockPos.getY() + f;
        }
        return n4 + 1;
    }

    public float getGroundFriction() {
        AABB aABB = this.getBoundingBox();
        AABB aABB2 = new AABB(aABB.minX, aABB.minY - 0.001, aABB.minZ, aABB.maxX, aABB.minY, aABB.maxZ);
        int n = Mth.floor(aABB2.minX) - 1;
        int n2 = Mth.ceil(aABB2.maxX) + 1;
        int n3 = Mth.floor(aABB2.minY) - 1;
        int n4 = Mth.ceil(aABB2.maxY) + 1;
        int n5 = Mth.floor(aABB2.minZ) - 1;
        int n6 = Mth.ceil(aABB2.maxZ) + 1;
        VoxelShape voxelShape = Shapes.create(aABB2);
        float f = 0.0f;
        int n7 = 0;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int i = n; i < n2; ++i) {
            for (int j = n5; j < n6; ++j) {
                int n8 = (i == n || i == n2 - 1 ? 1 : 0) + (j == n5 || j == n6 - 1 ? 1 : 0);
                if (n8 == 2) continue;
                for (int k = n3; k < n4; ++k) {
                    if (n8 > 0 && (k == n3 || k == n4 - 1)) continue;
                    mutableBlockPos.set(i, k, j);
                    BlockState blockState = this.level.getBlockState(mutableBlockPos);
                    if (blockState.getBlock() instanceof WaterlilyBlock || !Shapes.joinIsNotEmpty(blockState.getCollisionShape(this.level, mutableBlockPos).move(i, k, j), voxelShape, BooleanOp.AND)) continue;
                    f += blockState.getBlock().getFriction();
                    ++n7;
                }
            }
        }
        return f / (float)n7;
    }

    private boolean checkInWater() {
        AABB aABB = this.getBoundingBox();
        int n = Mth.floor(aABB.minX);
        int n2 = Mth.ceil(aABB.maxX);
        int n3 = Mth.floor(aABB.minY);
        int n4 = Mth.ceil(aABB.minY + 0.001);
        int n5 = Mth.floor(aABB.minZ);
        int n6 = Mth.ceil(aABB.maxZ);
        boolean bl = false;
        this.waterLevel = Double.MIN_VALUE;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int i = n; i < n2; ++i) {
            for (int j = n3; j < n4; ++j) {
                for (int k = n5; k < n6; ++k) {
                    mutableBlockPos.set(i, j, k);
                    FluidState fluidState = this.level.getFluidState(mutableBlockPos);
                    if (!fluidState.is(FluidTags.WATER)) continue;
                    float f = (float)j + fluidState.getHeight(this.level, mutableBlockPos);
                    this.waterLevel = Math.max((double)f, this.waterLevel);
                    bl |= aABB.minY < (double)f;
                }
            }
        }
        return bl;
    }

    @Nullable
    private Status isUnderwater() {
        AABB aABB = this.getBoundingBox();
        double d = aABB.maxY + 0.001;
        int n = Mth.floor(aABB.minX);
        int n2 = Mth.ceil(aABB.maxX);
        int n3 = Mth.floor(aABB.maxY);
        int n4 = Mth.ceil(d);
        int n5 = Mth.floor(aABB.minZ);
        int n6 = Mth.ceil(aABB.maxZ);
        boolean bl = false;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int i = n; i < n2; ++i) {
            for (int j = n3; j < n4; ++j) {
                for (int k = n5; k < n6; ++k) {
                    mutableBlockPos.set(i, j, k);
                    FluidState fluidState = this.level.getFluidState(mutableBlockPos);
                    if (!fluidState.is(FluidTags.WATER) || !(d < (double)((float)mutableBlockPos.getY() + fluidState.getHeight(this.level, mutableBlockPos)))) continue;
                    if (fluidState.isSource()) {
                        bl = true;
                        continue;
                    }
                    return Status.UNDER_FLOWING_WATER;
                }
            }
        }
        return bl ? Status.UNDER_WATER : null;
    }

    private void floatBoat() {
        double d = -0.03999999910593033;
        double d2 = this.isNoGravity() ? 0.0 : -0.03999999910593033;
        double d3 = 0.0;
        this.invFriction = 0.05f;
        if (this.oldStatus == Status.IN_AIR && this.status != Status.IN_AIR && this.status != Status.ON_LAND) {
            this.waterLevel = this.getY(1.0);
            this.setPos(this.getX(), (double)(this.getWaterLevelAbove() - this.getBbHeight()) + 0.101, this.getZ());
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.0, 1.0));
            this.lastYd = 0.0;
            this.status = Status.IN_WATER;
        } else {
            if (this.status == Status.IN_WATER) {
                d3 = (this.waterLevel - this.getY()) / (double)this.getBbHeight();
                this.invFriction = 0.9f;
            } else if (this.status == Status.UNDER_FLOWING_WATER) {
                d2 = -7.0E-4;
                this.invFriction = 0.9f;
            } else if (this.status == Status.UNDER_WATER) {
                d3 = 0.009999999776482582;
                this.invFriction = 0.45f;
            } else if (this.status == Status.IN_AIR) {
                this.invFriction = 0.9f;
            } else if (this.status == Status.ON_LAND) {
                this.invFriction = this.landFriction;
                if (this.getControllingPassenger() instanceof Player) {
                    this.landFriction /= 2.0f;
                }
            }
            Vec3 vec3 = this.getDeltaMovement();
            this.setDeltaMovement(vec3.x * (double)this.invFriction, vec3.y + d2, vec3.z * (double)this.invFriction);
            this.deltaRotation *= this.invFriction;
            if (d3 > 0.0) {
                Vec3 vec32 = this.getDeltaMovement();
                this.setDeltaMovement(vec32.x, (vec32.y + d3 * 0.06153846016296973) * 0.75, vec32.z);
            }
        }
    }

    private void controlBoat() {
        if (!this.isVehicle()) {
            return;
        }
        float f = 0.0f;
        if (this.inputLeft) {
            this.deltaRotation -= 1.0f;
        }
        if (this.inputRight) {
            this.deltaRotation += 1.0f;
        }
        if (this.inputRight != this.inputLeft && !this.inputUp && !this.inputDown) {
            f += 0.005f;
        }
        this.yRot += this.deltaRotation;
        if (this.inputUp) {
            f += 0.04f;
        }
        if (this.inputDown) {
            f -= 0.005f;
        }
        this.setDeltaMovement(this.getDeltaMovement().add(Mth.sin(-this.yRot * 0.017453292f) * f, 0.0, Mth.cos(this.yRot * 0.017453292f) * f));
        this.setPaddleState(this.inputRight && !this.inputLeft || this.inputUp, this.inputLeft && !this.inputRight || this.inputUp);
    }

    @Override
    public void positionRider(Entity entity) {
        if (!this.hasPassenger(entity)) {
            return;
        }
        float f = 0.0f;
        float f2 = (float)((this.removed ? 0.009999999776482582 : this.getPassengersRidingOffset()) + entity.getMyRidingOffset());
        if (this.getPassengers().size() > 1) {
            int n = this.getPassengers().indexOf(entity);
            f = n == 0 ? 0.2f : -0.6f;
            if (entity instanceof Animal) {
                f = (float)((double)f + 0.2);
            }
        }
        Vec3 vec3 = new Vec3(f, 0.0, 0.0).yRot(-this.yRot * 0.017453292f - 1.5707964f);
        entity.setPos(this.getX() + vec3.x, this.getY() + (double)f2, this.getZ() + vec3.z);
        entity.yRot += this.deltaRotation;
        entity.setYHeadRot(entity.getYHeadRot() + this.deltaRotation);
        this.clampRotation(entity);
        if (entity instanceof Animal && this.getPassengers().size() > 1) {
            int n = entity.getId() % 2 == 0 ? 90 : 270;
            entity.setYBodyRot(((Animal)entity).yBodyRot + (float)n);
            entity.setYHeadRot(entity.getYHeadRot() + (float)n);
        }
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity livingEntity) {
        double d;
        Vec3 vec3 = Boat.getCollisionHorizontalEscapeVector(this.getBbWidth() * Mth.SQRT_OF_TWO, livingEntity.getBbWidth(), this.yRot);
        double d2 = this.getX() + vec3.x;
        BlockPos blockPos = new BlockPos(d2, this.getBoundingBox().maxY, d = this.getZ() + vec3.z);
        BlockPos blockPos2 = blockPos.below();
        if (!this.level.isWaterAt(blockPos2)) {
            double d3 = (double)blockPos.getY() + this.level.getBlockFloorHeight(blockPos);
            double d4 = (double)blockPos.getY() + this.level.getBlockFloorHeight(blockPos2);
            for (Pose pose : livingEntity.getDismountPoses()) {
                Vec3 vec32 = DismountHelper.findDismountLocation(this.level, d2, d3, d, livingEntity, pose);
                if (vec32 != null) {
                    livingEntity.setPose(pose);
                    return vec32;
                }
                Vec3 vec33 = DismountHelper.findDismountLocation(this.level, d2, d4, d, livingEntity, pose);
                if (vec33 == null) continue;
                livingEntity.setPose(pose);
                return vec33;
            }
        }
        return super.getDismountLocationForPassenger(livingEntity);
    }

    protected void clampRotation(Entity entity) {
        entity.setYBodyRot(this.yRot);
        float f = Mth.wrapDegrees(entity.yRot - this.yRot);
        float f2 = Mth.clamp(f, -105.0f, 105.0f);
        entity.yRotO += f2 - f;
        entity.yRot += f2 - f;
        entity.setYHeadRot(entity.yRot);
    }

    @Override
    public void onPassengerTurned(Entity entity) {
        this.clampRotation(entity);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putString("Type", this.getBoatType().getName());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        if (compoundTag.contains("Type", 8)) {
            this.setType(Type.byName(compoundTag.getString("Type")));
        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand interactionHand) {
        if (player.isSecondaryUseActive()) {
            return InteractionResult.PASS;
        }
        if (this.outOfControlTicks < 60.0f) {
            if (!this.level.isClientSide) {
                return player.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void checkFallDamage(double d, boolean bl, BlockState blockState, BlockPos blockPos) {
        this.lastYd = this.getDeltaMovement().y;
        if (this.isPassenger()) {
            return;
        }
        if (bl) {
            if (this.fallDistance > 3.0f) {
                if (this.status != Status.ON_LAND) {
                    this.fallDistance = 0.0f;
                    return;
                }
                this.causeFallDamage(this.fallDistance, 1.0f);
                if (!this.level.isClientSide && !this.removed) {
                    this.remove();
                    if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                        int n;
                        for (n = 0; n < 3; ++n) {
                            this.spawnAtLocation(this.getBoatType().getPlanks());
                        }
                        for (n = 0; n < 2; ++n) {
                            this.spawnAtLocation(Items.STICK);
                        }
                    }
                }
            }
            this.fallDistance = 0.0f;
        } else if (!this.level.getFluidState(this.blockPosition().below()).is(FluidTags.WATER) && d < 0.0) {
            this.fallDistance = (float)((double)this.fallDistance - d);
        }
    }

    public boolean getPaddleState(int n) {
        return this.entityData.get(n == 0 ? DATA_ID_PADDLE_LEFT : DATA_ID_PADDLE_RIGHT) != false && this.getControllingPassenger() != null;
    }

    public void setDamage(float f) {
        this.entityData.set(DATA_ID_DAMAGE, Float.valueOf(f));
    }

    public float getDamage() {
        return this.entityData.get(DATA_ID_DAMAGE).floatValue();
    }

    public void setHurtTime(int n) {
        this.entityData.set(DATA_ID_HURT, n);
    }

    public int getHurtTime() {
        return this.entityData.get(DATA_ID_HURT);
    }

    private void setBubbleTime(int n) {
        this.entityData.set(DATA_ID_BUBBLE_TIME, n);
    }

    private int getBubbleTime() {
        return this.entityData.get(DATA_ID_BUBBLE_TIME);
    }

    public float getBubbleAngle(float f) {
        return Mth.lerp(f, this.bubbleAngleO, this.bubbleAngle);
    }

    public void setHurtDir(int n) {
        this.entityData.set(DATA_ID_HURTDIR, n);
    }

    public int getHurtDir() {
        return this.entityData.get(DATA_ID_HURTDIR);
    }

    public void setType(Type type) {
        this.entityData.set(DATA_ID_TYPE, type.ordinal());
    }

    public Type getBoatType() {
        return Type.byId(this.entityData.get(DATA_ID_TYPE));
    }

    @Override
    protected boolean canAddPassenger(Entity entity) {
        return this.getPassengers().size() < 2 && !this.isEyeInFluid(FluidTags.WATER);
    }

    @Nullable
    @Override
    public Entity getControllingPassenger() {
        List<Entity> list = this.getPassengers();
        return list.isEmpty() ? null : list.get(0);
    }

    public void setInput(boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        this.inputLeft = bl;
        this.inputRight = bl2;
        this.inputUp = bl3;
        this.inputDown = bl4;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    @Override
    public boolean isUnderWater() {
        return this.status == Status.UNDER_WATER || this.status == Status.UNDER_FLOWING_WATER;
    }

    public static enum Type {
        OAK(Blocks.OAK_PLANKS, "oak"),
        SPRUCE(Blocks.SPRUCE_PLANKS, "spruce"),
        BIRCH(Blocks.BIRCH_PLANKS, "birch"),
        JUNGLE(Blocks.JUNGLE_PLANKS, "jungle"),
        ACACIA(Blocks.ACACIA_PLANKS, "acacia"),
        DARK_OAK(Blocks.DARK_OAK_PLANKS, "dark_oak");
        
        private final String name;
        private final Block planks;

        private Type(Block block, String string2) {
            this.name = string2;
            this.planks = block;
        }

        public String getName() {
            return this.name;
        }

        public Block getPlanks() {
            return this.planks;
        }

        public String toString() {
            return this.name;
        }

        public static Type byId(int n) {
            Type[] arrtype = Type.values();
            if (n < 0 || n >= arrtype.length) {
                n = 0;
            }
            return arrtype[n];
        }

        public static Type byName(String string) {
            Type[] arrtype = Type.values();
            for (int i = 0; i < arrtype.length; ++i) {
                if (!arrtype[i].getName().equals(string)) continue;
                return arrtype[i];
            }
            return arrtype[0];
        }
    }

    public static enum Status {
        IN_WATER,
        UNDER_WATER,
        UNDER_FLOWING_WATER,
        ON_LAND,
        IN_AIR;
        
    }

}

