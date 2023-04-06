/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.google.common.collect.UnmodifiableIterator
 *  com.mojang.datafixers.util.Pair
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.vehicle;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.datafixers.util.Pair;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.BlockUtil;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import net.minecraft.world.entity.vehicle.MinecartFurnace;
import net.minecraft.world.entity.vehicle.MinecartHopper;
import net.minecraft.world.entity.vehicle.MinecartSpawner;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class AbstractMinecart
extends Entity {
    private static final EntityDataAccessor<Integer> DATA_ID_HURT = SynchedEntityData.defineId(AbstractMinecart.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ID_HURTDIR = SynchedEntityData.defineId(AbstractMinecart.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_ID_DAMAGE = SynchedEntityData.defineId(AbstractMinecart.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_ID_DISPLAY_BLOCK = SynchedEntityData.defineId(AbstractMinecart.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ID_DISPLAY_OFFSET = SynchedEntityData.defineId(AbstractMinecart.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_ID_CUSTOM_DISPLAY = SynchedEntityData.defineId(AbstractMinecart.class, EntityDataSerializers.BOOLEAN);
    private static final ImmutableMap<Pose, ImmutableList<Integer>> POSE_DISMOUNT_HEIGHTS = ImmutableMap.of((Object)((Object)Pose.STANDING), (Object)ImmutableList.of((Object)0, (Object)1, (Object)-1), (Object)((Object)Pose.CROUCHING), (Object)ImmutableList.of((Object)0, (Object)1, (Object)-1), (Object)((Object)Pose.SWIMMING), (Object)ImmutableList.of((Object)0, (Object)1));
    private boolean flipped;
    private static final Map<RailShape, Pair<Vec3i, Vec3i>> EXITS = Util.make(Maps.newEnumMap(RailShape.class), enumMap -> {
        Vec3i vec3i = Direction.WEST.getNormal();
        Vec3i vec3i2 = Direction.EAST.getNormal();
        Vec3i vec3i3 = Direction.NORTH.getNormal();
        Vec3i vec3i4 = Direction.SOUTH.getNormal();
        Vec3i vec3i5 = vec3i.below();
        Vec3i vec3i6 = vec3i2.below();
        Vec3i vec3i7 = vec3i3.below();
        Vec3i vec3i8 = vec3i4.below();
        enumMap.put(RailShape.NORTH_SOUTH, Pair.of((Object)vec3i3, (Object)vec3i4));
        enumMap.put(RailShape.EAST_WEST, Pair.of((Object)vec3i, (Object)vec3i2));
        enumMap.put(RailShape.ASCENDING_EAST, Pair.of((Object)vec3i5, (Object)vec3i2));
        enumMap.put(RailShape.ASCENDING_WEST, Pair.of((Object)vec3i, (Object)vec3i6));
        enumMap.put(RailShape.ASCENDING_NORTH, Pair.of((Object)vec3i3, (Object)vec3i8));
        enumMap.put(RailShape.ASCENDING_SOUTH, Pair.of((Object)vec3i7, (Object)vec3i4));
        enumMap.put(RailShape.SOUTH_EAST, Pair.of((Object)vec3i4, (Object)vec3i2));
        enumMap.put(RailShape.SOUTH_WEST, Pair.of((Object)vec3i4, (Object)vec3i));
        enumMap.put(RailShape.NORTH_WEST, Pair.of((Object)vec3i3, (Object)vec3i));
        enumMap.put(RailShape.NORTH_EAST, Pair.of((Object)vec3i3, (Object)vec3i2));
    });
    private int lSteps;
    private double lx;
    private double ly;
    private double lz;
    private double lyr;
    private double lxr;
    private double lxd;
    private double lyd;
    private double lzd;

    protected AbstractMinecart(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.blocksBuilding = true;
    }

    protected AbstractMinecart(EntityType<?> entityType, Level level, double d, double d2, double d3) {
        this(entityType, level);
        this.setPos(d, d2, d3);
        this.setDeltaMovement(Vec3.ZERO);
        this.xo = d;
        this.yo = d2;
        this.zo = d3;
    }

    public static AbstractMinecart createMinecart(Level level, double d, double d2, double d3, Type type) {
        if (type == Type.CHEST) {
            return new MinecartChest(level, d, d2, d3);
        }
        if (type == Type.FURNACE) {
            return new MinecartFurnace(level, d, d2, d3);
        }
        if (type == Type.TNT) {
            return new MinecartTNT(level, d, d2, d3);
        }
        if (type == Type.SPAWNER) {
            return new MinecartSpawner(level, d, d2, d3);
        }
        if (type == Type.HOPPER) {
            return new MinecartHopper(level, d, d2, d3);
        }
        if (type == Type.COMMAND_BLOCK) {
            return new MinecartCommandBlock(level, d, d2, d3);
        }
        return new Minecart(level, d, d2, d3);
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
        this.entityData.define(DATA_ID_DISPLAY_BLOCK, Block.getId(Blocks.AIR.defaultBlockState()));
        this.entityData.define(DATA_ID_DISPLAY_OFFSET, 6);
        this.entityData.define(DATA_ID_CUSTOM_DISPLAY, false);
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return Boat.canVehicleCollide(this, entity);
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
        return 0.0;
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity livingEntity) {
        Direction direction = this.getMotionDirection();
        if (direction.getAxis() == Direction.Axis.Y) {
            return super.getDismountLocationForPassenger(livingEntity);
        }
        int[][] arrn = DismountHelper.offsetsForDirection(direction);
        BlockPos blockPos2 = this.blockPosition();
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        ImmutableList<Pose> immutableList = livingEntity.getDismountPoses();
        for (Pose pose : immutableList) {
            EntityDimensions entityDimensions = livingEntity.getDimensions(pose);
            float f = Math.min(entityDimensions.width, 1.0f) / 2.0f;
            UnmodifiableIterator unmodifiableIterator = ((ImmutableList)POSE_DISMOUNT_HEIGHTS.get((Object)pose)).iterator();
            while (unmodifiableIterator.hasNext()) {
                int n = (Integer)unmodifiableIterator.next();
                for (int[] arrn2 : arrn) {
                    AABB aABB;
                    Vec3 vec3;
                    mutableBlockPos.set(blockPos2.getX() + arrn2[0], blockPos2.getY() + n, blockPos2.getZ() + arrn2[1]);
                    double d = this.level.getBlockFloorHeight(DismountHelper.nonClimbableShape(this.level, mutableBlockPos), () -> DismountHelper.nonClimbableShape(this.level, (BlockPos)mutableBlockPos.below()));
                    if (!DismountHelper.isBlockFloorValid(d) || !DismountHelper.canDismountTo(this.level, livingEntity, (aABB = new AABB(-f, 0.0, -f, f, entityDimensions.height, f)).move(vec3 = Vec3.upFromBottomCenterOf(mutableBlockPos, d)))) continue;
                    livingEntity.setPose(pose);
                    return vec3;
                }
            }
        }
        double d = this.getBoundingBox().maxY;
        mutableBlockPos.set((double)blockPos2.getX(), d, (double)blockPos2.getZ());
        for (Pose pose : immutableList) {
            double d2 = livingEntity.getDimensions((Pose)pose).height;
            Object object = Mth.ceil(d - (double)mutableBlockPos.getY() + d2);
            int n = (int)DismountHelper.findCeilingFrom(mutableBlockPos, (int)object, blockPos -> this.level.getBlockState((BlockPos)blockPos).getCollisionShape(this.level, (BlockPos)blockPos));
            if (!(d + d2 <= n)) continue;
            livingEntity.setPose(pose);
            break;
        }
        return super.getDismountLocationForPassenger(livingEntity);
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        boolean bl;
        if (this.level.isClientSide || this.removed) {
            return true;
        }
        if (this.isInvulnerableTo(damageSource)) {
            return false;
        }
        this.setHurtDir(-this.getHurtDir());
        this.setHurtTime(10);
        this.markHurt();
        this.setDamage(this.getDamage() + f * 10.0f);
        boolean bl2 = bl = damageSource.getEntity() instanceof Player && ((Player)damageSource.getEntity()).abilities.instabuild;
        if (bl || this.getDamage() > 40.0f) {
            this.ejectPassengers();
            if (!bl || this.hasCustomName()) {
                this.destroy(damageSource);
            } else {
                this.remove();
            }
        }
        return true;
    }

    @Override
    protected float getBlockSpeedFactor() {
        BlockState blockState = this.level.getBlockState(this.blockPosition());
        if (blockState.is(BlockTags.RAILS)) {
            return 1.0f;
        }
        return super.getBlockSpeedFactor();
    }

    public void destroy(DamageSource damageSource) {
        this.remove();
        if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            ItemStack itemStack = new ItemStack(Items.MINECART);
            if (this.hasCustomName()) {
                itemStack.setHoverName(this.getCustomName());
            }
            this.spawnAtLocation(itemStack);
        }
    }

    @Override
    public void animateHurt() {
        this.setHurtDir(-this.getHurtDir());
        this.setHurtTime(10);
        this.setDamage(this.getDamage() + this.getDamage() * 10.0f);
    }

    @Override
    public boolean isPickable() {
        return !this.removed;
    }

    private static Pair<Vec3i, Vec3i> exits(RailShape railShape) {
        return EXITS.get(railShape);
    }

    @Override
    public Direction getMotionDirection() {
        return this.flipped ? this.getDirection().getOpposite().getClockWise() : this.getDirection().getClockWise();
    }

    @Override
    public void tick() {
        double d;
        BlockPos blockPos;
        int n;
        int n2;
        int n3;
        BlockState blockState;
        if (this.getHurtTime() > 0) {
            this.setHurtTime(this.getHurtTime() - 1);
        }
        if (this.getDamage() > 0.0f) {
            this.setDamage(this.getDamage() - 1.0f);
        }
        if (this.getY() < -64.0) {
            this.outOfWorld();
        }
        this.handleNetherPortal();
        if (this.level.isClientSide) {
            if (this.lSteps > 0) {
                double d2 = this.getX() + (this.lx - this.getX()) / (double)this.lSteps;
                double d3 = this.getY() + (this.ly - this.getY()) / (double)this.lSteps;
                double d4 = this.getZ() + (this.lz - this.getZ()) / (double)this.lSteps;
                double d5 = Mth.wrapDegrees(this.lyr - (double)this.yRot);
                this.yRot = (float)((double)this.yRot + d5 / (double)this.lSteps);
                this.xRot = (float)((double)this.xRot + (this.lxr - (double)this.xRot) / (double)this.lSteps);
                --this.lSteps;
                this.setPos(d2, d3, d4);
                this.setRot(this.yRot, this.xRot);
            } else {
                this.reapplyPosition();
                this.setRot(this.yRot, this.xRot);
            }
            return;
        }
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.04, 0.0));
        }
        if (this.level.getBlockState(new BlockPos(n3 = Mth.floor(this.getX()), (n2 = Mth.floor(this.getY())) - 1, n = Mth.floor(this.getZ()))).is(BlockTags.RAILS)) {
            --n2;
        }
        if (BaseRailBlock.isRail(blockState = this.level.getBlockState(blockPos = new BlockPos(n3, n2, n)))) {
            this.moveAlongTrack(blockPos, blockState);
            if (blockState.is(Blocks.ACTIVATOR_RAIL)) {
                this.activateMinecart(n3, n2, n, blockState.getValue(PoweredRailBlock.POWERED));
            }
        } else {
            this.comeOffTrack();
        }
        this.checkInsideBlocks();
        this.xRot = 0.0f;
        double d6 = this.xo - this.getX();
        double d7 = this.zo - this.getZ();
        if (d6 * d6 + d7 * d7 > 0.001) {
            this.yRot = (float)(Mth.atan2(d7, d6) * 180.0 / 3.141592653589793);
            if (this.flipped) {
                this.yRot += 180.0f;
            }
        }
        if ((d = (double)Mth.wrapDegrees(this.yRot - this.yRotO)) < -170.0 || d >= 170.0) {
            this.yRot += 180.0f;
            this.flipped = !this.flipped;
        }
        this.setRot(this.yRot, this.xRot);
        if (this.getMinecartType() == Type.RIDEABLE && AbstractMinecart.getHorizontalDistanceSqr(this.getDeltaMovement()) > 0.01) {
            List<Entity> list = this.level.getEntities(this, this.getBoundingBox().inflate(0.20000000298023224, 0.0, 0.20000000298023224), EntitySelector.pushableBy(this));
            if (!list.isEmpty()) {
                for (int i = 0; i < list.size(); ++i) {
                    Entity entity = list.get(i);
                    if (entity instanceof Player || entity instanceof IronGolem || entity instanceof AbstractMinecart || this.isVehicle() || entity.isPassenger()) {
                        entity.push(this);
                        continue;
                    }
                    entity.startRiding(this);
                }
            }
        } else {
            for (Entity entity : this.level.getEntities(this, this.getBoundingBox().inflate(0.20000000298023224, 0.0, 0.20000000298023224))) {
                if (this.hasPassenger(entity) || !entity.isPushable() || !(entity instanceof AbstractMinecart)) continue;
                entity.push(this);
            }
        }
        this.updateInWaterStateAndDoFluidPushing();
        if (this.isInLava()) {
            this.lavaHurt();
            this.fallDistance *= 0.5f;
        }
        this.firstTick = false;
    }

    protected double getMaxSpeed() {
        return 0.4;
    }

    public void activateMinecart(int n, int n2, int n3, boolean bl) {
    }

    protected void comeOffTrack() {
        double d = this.getMaxSpeed();
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(Mth.clamp(vec3.x, -d, d), vec3.y, Mth.clamp(vec3.z, -d, d));
        if (this.onGround) {
            this.setDeltaMovement(this.getDeltaMovement().scale(0.5));
        }
        this.move(MoverType.SELF, this.getDeltaMovement());
        if (!this.onGround) {
            this.setDeltaMovement(this.getDeltaMovement().scale(0.95));
        }
    }

    protected void moveAlongTrack(BlockPos blockPos, BlockState blockState) {
        Entity entity;
        double d;
        Vec3 vec3;
        double d2;
        double d3;
        double d4;
        this.fallDistance = 0.0f;
        double d5 = this.getX();
        double d6 = this.getY();
        double d7 = this.getZ();
        Vec3 vec32 = this.getPos(d5, d6, d7);
        d6 = blockPos.getY();
        boolean bl = false;
        boolean bl2 = false;
        BaseRailBlock baseRailBlock = (BaseRailBlock)blockState.getBlock();
        if (baseRailBlock == Blocks.POWERED_RAIL) {
            bl = blockState.getValue(PoweredRailBlock.POWERED);
            bl2 = !bl;
        }
        double d8 = 0.0078125;
        Vec3 vec33 = this.getDeltaMovement();
        RailShape railShape = blockState.getValue(baseRailBlock.getShapeProperty());
        switch (railShape) {
            case ASCENDING_EAST: {
                this.setDeltaMovement(vec33.add(-0.0078125, 0.0, 0.0));
                d6 += 1.0;
                break;
            }
            case ASCENDING_WEST: {
                this.setDeltaMovement(vec33.add(0.0078125, 0.0, 0.0));
                d6 += 1.0;
                break;
            }
            case ASCENDING_NORTH: {
                this.setDeltaMovement(vec33.add(0.0, 0.0, 0.0078125));
                d6 += 1.0;
                break;
            }
            case ASCENDING_SOUTH: {
                this.setDeltaMovement(vec33.add(0.0, 0.0, -0.0078125));
                d6 += 1.0;
            }
        }
        vec33 = this.getDeltaMovement();
        Pair<Vec3i, Vec3i> pair = AbstractMinecart.exits(railShape);
        Vec3i vec3i = (Vec3i)pair.getFirst();
        Vec3i vec3i2 = (Vec3i)pair.getSecond();
        double d9 = vec3i2.getX() - vec3i.getX();
        double d10 = vec3i2.getZ() - vec3i.getZ();
        double d11 = Math.sqrt(d9 * d9 + d10 * d10);
        double d12 = vec33.x * d9 + vec33.z * d10;
        if (d12 < 0.0) {
            d9 = -d9;
            d10 = -d10;
        }
        double d13 = Math.min(2.0, Math.sqrt(AbstractMinecart.getHorizontalDistanceSqr(vec33)));
        vec33 = new Vec3(d13 * d9 / d11, vec33.y, d13 * d10 / d11);
        this.setDeltaMovement(vec33);
        Entity entity2 = entity = this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
        if (entity instanceof Player) {
            Vec3 vec34 = entity.getDeltaMovement();
            double d14 = AbstractMinecart.getHorizontalDistanceSqr(vec34);
            double d15 = AbstractMinecart.getHorizontalDistanceSqr(this.getDeltaMovement());
            if (d14 > 1.0E-4 && d15 < 0.01) {
                this.setDeltaMovement(this.getDeltaMovement().add(vec34.x * 0.1, 0.0, vec34.z * 0.1));
                bl2 = false;
            }
        }
        if (bl2) {
            double d16 = Math.sqrt(AbstractMinecart.getHorizontalDistanceSqr(this.getDeltaMovement()));
            if (d16 < 0.03) {
                this.setDeltaMovement(Vec3.ZERO);
            } else {
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.5, 0.0, 0.5));
            }
        }
        double d17 = (double)blockPos.getX() + 0.5 + (double)vec3i.getX() * 0.5;
        double d18 = (double)blockPos.getZ() + 0.5 + (double)vec3i.getZ() * 0.5;
        double d19 = (double)blockPos.getX() + 0.5 + (double)vec3i2.getX() * 0.5;
        double d20 = (double)blockPos.getZ() + 0.5 + (double)vec3i2.getZ() * 0.5;
        d9 = d19 - d17;
        d10 = d20 - d18;
        if (d9 == 0.0) {
            d4 = d7 - (double)blockPos.getZ();
        } else if (d10 == 0.0) {
            d4 = d5 - (double)blockPos.getX();
        } else {
            d = d5 - d17;
            d2 = d7 - d18;
            d4 = (d * d9 + d2 * d10) * 2.0;
        }
        d5 = d17 + d9 * d4;
        d7 = d18 + d10 * d4;
        this.setPos(d5, d6, d7);
        d = this.isVehicle() ? 0.75 : 1.0;
        d2 = this.getMaxSpeed();
        vec33 = this.getDeltaMovement();
        this.move(MoverType.SELF, new Vec3(Mth.clamp(d * vec33.x, -d2, d2), 0.0, Mth.clamp(d * vec33.z, -d2, d2)));
        if (vec3i.getY() != 0 && Mth.floor(this.getX()) - blockPos.getX() == vec3i.getX() && Mth.floor(this.getZ()) - blockPos.getZ() == vec3i.getZ()) {
            this.setPos(this.getX(), this.getY() + (double)vec3i.getY(), this.getZ());
        } else if (vec3i2.getY() != 0 && Mth.floor(this.getX()) - blockPos.getX() == vec3i2.getX() && Mth.floor(this.getZ()) - blockPos.getZ() == vec3i2.getZ()) {
            this.setPos(this.getX(), this.getY() + (double)vec3i2.getY(), this.getZ());
        }
        this.applyNaturalSlowdown();
        Vec3 vec35 = this.getPos(this.getX(), this.getY(), this.getZ());
        if (vec35 != null && vec32 != null) {
            double d21 = (vec32.y - vec35.y) * 0.05;
            vec3 = this.getDeltaMovement();
            d3 = Math.sqrt(AbstractMinecart.getHorizontalDistanceSqr(vec3));
            if (d3 > 0.0) {
                this.setDeltaMovement(vec3.multiply((d3 + d21) / d3, 1.0, (d3 + d21) / d3));
            }
            this.setPos(this.getX(), vec35.y, this.getZ());
        }
        int n = Mth.floor(this.getX());
        int n2 = Mth.floor(this.getZ());
        if (n != blockPos.getX() || n2 != blockPos.getZ()) {
            vec3 = this.getDeltaMovement();
            d3 = Math.sqrt(AbstractMinecart.getHorizontalDistanceSqr(vec3));
            this.setDeltaMovement(d3 * (double)(n - blockPos.getX()), vec3.y, d3 * (double)(n2 - blockPos.getZ()));
        }
        if (bl) {
            vec3 = this.getDeltaMovement();
            d3 = Math.sqrt(AbstractMinecart.getHorizontalDistanceSqr(vec3));
            if (d3 > 0.01) {
                double d22 = 0.06;
                this.setDeltaMovement(vec3.add(vec3.x / d3 * 0.06, 0.0, vec3.z / d3 * 0.06));
            } else {
                Vec3 vec36 = this.getDeltaMovement();
                double d23 = vec36.x;
                double d24 = vec36.z;
                if (railShape == RailShape.EAST_WEST) {
                    if (this.isRedstoneConductor(blockPos.west())) {
                        d23 = 0.02;
                    } else if (this.isRedstoneConductor(blockPos.east())) {
                        d23 = -0.02;
                    }
                } else if (railShape == RailShape.NORTH_SOUTH) {
                    if (this.isRedstoneConductor(blockPos.north())) {
                        d24 = 0.02;
                    } else if (this.isRedstoneConductor(blockPos.south())) {
                        d24 = -0.02;
                    }
                } else {
                    return;
                }
                this.setDeltaMovement(d23, vec36.y, d24);
            }
        }
    }

    private boolean isRedstoneConductor(BlockPos blockPos) {
        return this.level.getBlockState(blockPos).isRedstoneConductor(this.level, blockPos);
    }

    protected void applyNaturalSlowdown() {
        double d = this.isVehicle() ? 0.997 : 0.96;
        this.setDeltaMovement(this.getDeltaMovement().multiply(d, 0.0, d));
    }

    @Nullable
    public Vec3 getPosOffs(double d, double d2, double d3, double d4) {
        int n;
        BlockState blockState;
        int n2;
        int n3 = Mth.floor(d);
        if (this.level.getBlockState(new BlockPos(n3, (n = Mth.floor(d2)) - 1, n2 = Mth.floor(d3))).is(BlockTags.RAILS)) {
            --n;
        }
        if (BaseRailBlock.isRail(blockState = this.level.getBlockState(new BlockPos(n3, n, n2)))) {
            RailShape railShape = blockState.getValue(((BaseRailBlock)blockState.getBlock()).getShapeProperty());
            d2 = n;
            if (railShape.isAscending()) {
                d2 = n + 1;
            }
            Pair<Vec3i, Vec3i> pair = AbstractMinecart.exits(railShape);
            Vec3i vec3i = (Vec3i)pair.getFirst();
            Vec3i vec3i2 = (Vec3i)pair.getSecond();
            double d5 = vec3i2.getX() - vec3i.getX();
            double d6 = vec3i2.getZ() - vec3i.getZ();
            double d7 = Math.sqrt(d5 * d5 + d6 * d6);
            if (vec3i.getY() != 0 && Mth.floor(d += (d5 /= d7) * d4) - n3 == vec3i.getX() && Mth.floor(d3 += (d6 /= d7) * d4) - n2 == vec3i.getZ()) {
                d2 += (double)vec3i.getY();
            } else if (vec3i2.getY() != 0 && Mth.floor(d) - n3 == vec3i2.getX() && Mth.floor(d3) - n2 == vec3i2.getZ()) {
                d2 += (double)vec3i2.getY();
            }
            return this.getPos(d, d2, d3);
        }
        return null;
    }

    @Nullable
    public Vec3 getPos(double d, double d2, double d3) {
        int n;
        BlockState blockState;
        int n2;
        int n3 = Mth.floor(d);
        if (this.level.getBlockState(new BlockPos(n3, (n2 = Mth.floor(d2)) - 1, n = Mth.floor(d3))).is(BlockTags.RAILS)) {
            --n2;
        }
        if (BaseRailBlock.isRail(blockState = this.level.getBlockState(new BlockPos(n3, n2, n)))) {
            double d4;
            RailShape railShape = blockState.getValue(((BaseRailBlock)blockState.getBlock()).getShapeProperty());
            Pair<Vec3i, Vec3i> pair = AbstractMinecart.exits(railShape);
            Vec3i vec3i = (Vec3i)pair.getFirst();
            Vec3i vec3i2 = (Vec3i)pair.getSecond();
            double d5 = (double)n3 + 0.5 + (double)vec3i.getX() * 0.5;
            double d6 = (double)n2 + 0.0625 + (double)vec3i.getY() * 0.5;
            double d7 = (double)n + 0.5 + (double)vec3i.getZ() * 0.5;
            double d8 = (double)n3 + 0.5 + (double)vec3i2.getX() * 0.5;
            double d9 = (double)n2 + 0.0625 + (double)vec3i2.getY() * 0.5;
            double d10 = (double)n + 0.5 + (double)vec3i2.getZ() * 0.5;
            double d11 = d8 - d5;
            double d12 = (d9 - d6) * 2.0;
            double d13 = d10 - d7;
            if (d11 == 0.0) {
                d4 = d3 - (double)n;
            } else if (d13 == 0.0) {
                d4 = d - (double)n3;
            } else {
                double d14 = d - d5;
                double d15 = d3 - d7;
                d4 = (d14 * d11 + d15 * d13) * 2.0;
            }
            d = d5 + d11 * d4;
            d2 = d6 + d12 * d4;
            d3 = d7 + d13 * d4;
            if (d12 < 0.0) {
                d2 += 1.0;
            } else if (d12 > 0.0) {
                d2 += 0.5;
            }
            return new Vec3(d, d2, d3);
        }
        return null;
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        AABB aABB = this.getBoundingBox();
        if (this.hasCustomDisplay()) {
            return aABB.inflate((double)Math.abs(this.getDisplayOffset()) / 16.0);
        }
        return aABB;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        if (compoundTag.getBoolean("CustomDisplayTile")) {
            this.setDisplayBlockState(NbtUtils.readBlockState(compoundTag.getCompound("DisplayState")));
            this.setDisplayOffset(compoundTag.getInt("DisplayOffset"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        if (this.hasCustomDisplay()) {
            compoundTag.putBoolean("CustomDisplayTile", true);
            compoundTag.put("DisplayState", NbtUtils.writeBlockState(this.getDisplayBlockState()));
            compoundTag.putInt("DisplayOffset", this.getDisplayOffset());
        }
    }

    @Override
    public void push(Entity entity) {
        double d;
        if (this.level.isClientSide) {
            return;
        }
        if (entity.noPhysics || this.noPhysics) {
            return;
        }
        if (this.hasPassenger(entity)) {
            return;
        }
        double d2 = entity.getX() - this.getX();
        double d3 = d2 * d2 + (d = entity.getZ() - this.getZ()) * d;
        if (d3 >= 9.999999747378752E-5) {
            d3 = Mth.sqrt(d3);
            d2 /= d3;
            d /= d3;
            double d4 = 1.0 / d3;
            if (d4 > 1.0) {
                d4 = 1.0;
            }
            d2 *= d4;
            d *= d4;
            d2 *= 0.10000000149011612;
            d *= 0.10000000149011612;
            d2 *= (double)(1.0f - this.pushthrough);
            d *= (double)(1.0f - this.pushthrough);
            d2 *= 0.5;
            d *= 0.5;
            if (entity instanceof AbstractMinecart) {
                double d5;
                Vec3 vec3;
                double d6 = entity.getX() - this.getX();
                Vec3 vec32 = new Vec3(d6, 0.0, d5 = entity.getZ() - this.getZ()).normalize();
                double d7 = Math.abs(vec32.dot(vec3 = new Vec3(Mth.cos(this.yRot * 0.017453292f), 0.0, Mth.sin(this.yRot * 0.017453292f)).normalize()));
                if (d7 < 0.800000011920929) {
                    return;
                }
                Vec3 vec33 = this.getDeltaMovement();
                Vec3 vec34 = entity.getDeltaMovement();
                if (((AbstractMinecart)entity).getMinecartType() == Type.FURNACE && this.getMinecartType() != Type.FURNACE) {
                    this.setDeltaMovement(vec33.multiply(0.2, 1.0, 0.2));
                    this.push(vec34.x - d2, 0.0, vec34.z - d);
                    entity.setDeltaMovement(vec34.multiply(0.95, 1.0, 0.95));
                } else if (((AbstractMinecart)entity).getMinecartType() != Type.FURNACE && this.getMinecartType() == Type.FURNACE) {
                    entity.setDeltaMovement(vec34.multiply(0.2, 1.0, 0.2));
                    entity.push(vec33.x + d2, 0.0, vec33.z + d);
                    this.setDeltaMovement(vec33.multiply(0.95, 1.0, 0.95));
                } else {
                    double d8 = (vec34.x + vec33.x) / 2.0;
                    double d9 = (vec34.z + vec33.z) / 2.0;
                    this.setDeltaMovement(vec33.multiply(0.2, 1.0, 0.2));
                    this.push(d8 - d2, 0.0, d9 - d);
                    entity.setDeltaMovement(vec34.multiply(0.2, 1.0, 0.2));
                    entity.push(d8 + d2, 0.0, d9 + d);
                }
            } else {
                this.push(-d2, 0.0, -d);
                entity.push(d2 / 4.0, 0.0, d / 4.0);
            }
        }
    }

    @Override
    public void lerpTo(double d, double d2, double d3, float f, float f2, int n, boolean bl) {
        this.lx = d;
        this.ly = d2;
        this.lz = d3;
        this.lyr = f;
        this.lxr = f2;
        this.lSteps = n + 2;
        this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
    }

    @Override
    public void lerpMotion(double d, double d2, double d3) {
        this.lxd = d;
        this.lyd = d2;
        this.lzd = d3;
        this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
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

    public void setHurtDir(int n) {
        this.entityData.set(DATA_ID_HURTDIR, n);
    }

    public int getHurtDir() {
        return this.entityData.get(DATA_ID_HURTDIR);
    }

    public abstract Type getMinecartType();

    public BlockState getDisplayBlockState() {
        if (!this.hasCustomDisplay()) {
            return this.getDefaultDisplayBlockState();
        }
        return Block.stateById(this.getEntityData().get(DATA_ID_DISPLAY_BLOCK));
    }

    public BlockState getDefaultDisplayBlockState() {
        return Blocks.AIR.defaultBlockState();
    }

    public int getDisplayOffset() {
        if (!this.hasCustomDisplay()) {
            return this.getDefaultDisplayOffset();
        }
        return this.getEntityData().get(DATA_ID_DISPLAY_OFFSET);
    }

    public int getDefaultDisplayOffset() {
        return 6;
    }

    public void setDisplayBlockState(BlockState blockState) {
        this.getEntityData().set(DATA_ID_DISPLAY_BLOCK, Block.getId(blockState));
        this.setCustomDisplay(true);
    }

    public void setDisplayOffset(int n) {
        this.getEntityData().set(DATA_ID_DISPLAY_OFFSET, n);
        this.setCustomDisplay(true);
    }

    public boolean hasCustomDisplay() {
        return this.getEntityData().get(DATA_ID_CUSTOM_DISPLAY);
    }

    public void setCustomDisplay(boolean bl) {
        this.getEntityData().set(DATA_ID_CUSTOM_DISPLAY, bl);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    public static enum Type {
        RIDEABLE,
        CHEST,
        FURNACE,
        TNT,
        SPAWNER,
        HOPPER,
        COMMAND_BLOCK;
        
    }

}

