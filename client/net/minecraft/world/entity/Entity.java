/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap
 *  it.unimi.dsi.fastutil.objects.Object2DoubleMap
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.entity;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.BlockUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.KilledTrigger;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.util.RewindableStream;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.Nameable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.HoneyBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.portal.PortalForcer;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Entity
implements Nameable,
CommandSource {
    protected static final Logger LOGGER = LogManager.getLogger();
    private static final AtomicInteger ENTITY_COUNTER = new AtomicInteger();
    private static final List<ItemStack> EMPTY_LIST = Collections.emptyList();
    private static final AABB INITIAL_AABB = new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    private static double viewScale = 1.0;
    private final EntityType<?> type;
    private int id = ENTITY_COUNTER.incrementAndGet();
    public boolean blocksBuilding;
    private final List<Entity> passengers = Lists.newArrayList();
    protected int boardingCooldown;
    @Nullable
    private Entity vehicle;
    public boolean forcedLoading;
    public Level level;
    public double xo;
    public double yo;
    public double zo;
    private Vec3 position;
    private BlockPos blockPosition;
    private Vec3 deltaMovement = Vec3.ZERO;
    public float yRot;
    public float xRot;
    public float yRotO;
    public float xRotO;
    private AABB bb = INITIAL_AABB;
    protected boolean onGround;
    public boolean horizontalCollision;
    public boolean verticalCollision;
    public boolean hurtMarked;
    protected Vec3 stuckSpeedMultiplier = Vec3.ZERO;
    public boolean removed;
    public float walkDistO;
    public float walkDist;
    public float moveDist;
    public float fallDistance;
    private float nextStep = 1.0f;
    private float nextFlap = 1.0f;
    public double xOld;
    public double yOld;
    public double zOld;
    public float maxUpStep;
    public boolean noPhysics;
    public float pushthrough;
    protected final Random random = new Random();
    public int tickCount;
    private int remainingFireTicks = -this.getFireImmuneTicks();
    protected boolean wasTouchingWater;
    protected Object2DoubleMap<Tag<Fluid>> fluidHeight = new Object2DoubleArrayMap(2);
    protected boolean wasEyeInWater;
    @Nullable
    protected Tag<Fluid> fluidOnEyes;
    public int invulnerableTime;
    protected boolean firstTick = true;
    protected final SynchedEntityData entityData;
    protected static final EntityDataAccessor<Byte> DATA_SHARED_FLAGS_ID = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> DATA_AIR_SUPPLY_ID = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<Component>> DATA_CUSTOM_NAME = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.OPTIONAL_COMPONENT);
    private static final EntityDataAccessor<Boolean> DATA_CUSTOM_NAME_VISIBLE = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_SILENT = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_NO_GRAVITY = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Pose> DATA_POSE = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.POSE);
    public boolean inChunk;
    public int xChunk;
    public int yChunk;
    public int zChunk;
    private boolean movedSinceLastChunkCheck;
    private Vec3 packetCoordinates;
    public boolean noCulling;
    public boolean hasImpulse;
    private int portalCooldown;
    protected boolean isInsidePortal;
    protected int portalTime;
    protected BlockPos portalEntrancePos;
    private boolean invulnerable;
    protected UUID uuid = Mth.createInsecureUUID(this.random);
    protected String stringUUID = this.uuid.toString();
    protected boolean glowing;
    private final Set<String> tags = Sets.newHashSet();
    private boolean forceChunkAddition;
    private final double[] pistonDeltas = new double[]{0.0, 0.0, 0.0};
    private long pistonDeltasGameTime;
    private EntityDimensions dimensions;
    private float eyeHeight;

    public Entity(EntityType<?> entityType, Level level) {
        this.type = entityType;
        this.level = level;
        this.dimensions = entityType.getDimensions();
        this.position = Vec3.ZERO;
        this.blockPosition = BlockPos.ZERO;
        this.packetCoordinates = Vec3.ZERO;
        this.setPos(0.0, 0.0, 0.0);
        this.entityData = new SynchedEntityData(this);
        this.entityData.define(DATA_SHARED_FLAGS_ID, (byte)0);
        this.entityData.define(DATA_AIR_SUPPLY_ID, this.getMaxAirSupply());
        this.entityData.define(DATA_CUSTOM_NAME_VISIBLE, false);
        this.entityData.define(DATA_CUSTOM_NAME, Optional.empty());
        this.entityData.define(DATA_SILENT, false);
        this.entityData.define(DATA_NO_GRAVITY, false);
        this.entityData.define(DATA_POSE, Pose.STANDING);
        this.defineSynchedData();
        this.eyeHeight = this.getEyeHeight(Pose.STANDING, this.dimensions);
    }

    public boolean isColliding(BlockPos blockPos, BlockState blockState) {
        VoxelShape voxelShape = blockState.getCollisionShape(this.level, blockPos, CollisionContext.of(this));
        VoxelShape voxelShape2 = voxelShape.move(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        return Shapes.joinIsNotEmpty(voxelShape2, Shapes.create(this.getBoundingBox()), BooleanOp.AND);
    }

    public int getTeamColor() {
        Team team = this.getTeam();
        if (team != null && team.getColor().getColor() != null) {
            return team.getColor().getColor();
        }
        return 16777215;
    }

    public boolean isSpectator() {
        return false;
    }

    public final void unRide() {
        if (this.isVehicle()) {
            this.ejectPassengers();
        }
        if (this.isPassenger()) {
            this.stopRiding();
        }
    }

    public void setPacketCoordinates(double d, double d2, double d3) {
        this.setPacketCoordinates(new Vec3(d, d2, d3));
    }

    public void setPacketCoordinates(Vec3 vec3) {
        this.packetCoordinates = vec3;
    }

    public Vec3 getPacketCoordinates() {
        return this.packetCoordinates;
    }

    public EntityType<?> getType() {
        return this.type;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int n) {
        this.id = n;
    }

    public Set<String> getTags() {
        return this.tags;
    }

    public boolean addTag(String string) {
        if (this.tags.size() >= 1024) {
            return false;
        }
        return this.tags.add(string);
    }

    public boolean removeTag(String string) {
        return this.tags.remove(string);
    }

    public void kill() {
        this.remove();
    }

    protected abstract void defineSynchedData();

    public SynchedEntityData getEntityData() {
        return this.entityData;
    }

    public boolean equals(Object object) {
        if (object instanceof Entity) {
            return ((Entity)object).id == this.id;
        }
        return false;
    }

    public int hashCode() {
        return this.id;
    }

    protected void resetPos() {
        if (this.level == null) {
            return;
        }
        for (double d = this.getY(); d > 0.0 && d < 256.0; d += 1.0) {
            this.setPos(this.getX(), d, this.getZ());
            if (this.level.noCollision(this)) break;
        }
        this.setDeltaMovement(Vec3.ZERO);
        this.xRot = 0.0f;
    }

    public void remove() {
        this.removed = true;
    }

    public void setPose(Pose pose) {
        this.entityData.set(DATA_POSE, pose);
    }

    public Pose getPose() {
        return this.entityData.get(DATA_POSE);
    }

    public boolean closerThan(Entity entity, double d) {
        double d2 = entity.position.x - this.position.x;
        double d3 = entity.position.y - this.position.y;
        double d4 = entity.position.z - this.position.z;
        return d2 * d2 + d3 * d3 + d4 * d4 < d * d;
    }

    protected void setRot(float f, float f2) {
        this.yRot = f % 360.0f;
        this.xRot = f2 % 360.0f;
    }

    public void setPos(double d, double d2, double d3) {
        this.setPosRaw(d, d2, d3);
        this.setBoundingBox(this.dimensions.makeBoundingBox(d, d2, d3));
    }

    protected void reapplyPosition() {
        this.setPos(this.position.x, this.position.y, this.position.z);
    }

    public void turn(double d, double d2) {
        double d3 = d2 * 0.15;
        double d4 = d * 0.15;
        this.xRot = (float)((double)this.xRot + d3);
        this.yRot = (float)((double)this.yRot + d4);
        this.xRot = Mth.clamp(this.xRot, -90.0f, 90.0f);
        this.xRotO = (float)((double)this.xRotO + d3);
        this.yRotO = (float)((double)this.yRotO + d4);
        this.xRotO = Mth.clamp(this.xRotO, -90.0f, 90.0f);
        if (this.vehicle != null) {
            this.vehicle.onPassengerTurned(this);
        }
    }

    public void tick() {
        if (!this.level.isClientSide) {
            this.setSharedFlag(6, this.isGlowing());
        }
        this.baseTick();
    }

    public void baseTick() {
        this.level.getProfiler().push("entityBaseTick");
        if (this.isPassenger() && this.getVehicle().removed) {
            this.stopRiding();
        }
        if (this.boardingCooldown > 0) {
            --this.boardingCooldown;
        }
        this.walkDistO = this.walkDist;
        this.xRotO = this.xRot;
        this.yRotO = this.yRot;
        this.handleNetherPortal();
        if (this.canSpawnSprintParticle()) {
            this.spawnSprintParticle();
        }
        this.updateInWaterStateAndDoFluidPushing();
        this.updateFluidOnEyes();
        this.updateSwimming();
        if (this.level.isClientSide) {
            this.clearFire();
        } else if (this.remainingFireTicks > 0) {
            if (this.fireImmune()) {
                this.setRemainingFireTicks(this.remainingFireTicks - 4);
                if (this.remainingFireTicks < 0) {
                    this.clearFire();
                }
            } else {
                if (this.remainingFireTicks % 20 == 0 && !this.isInLava()) {
                    this.hurt(DamageSource.ON_FIRE, 1.0f);
                }
                this.setRemainingFireTicks(this.remainingFireTicks - 1);
            }
        }
        if (this.isInLava()) {
            this.lavaHurt();
            this.fallDistance *= 0.5f;
        }
        if (this.getY() < -64.0) {
            this.outOfWorld();
        }
        if (!this.level.isClientSide) {
            this.setSharedFlag(0, this.remainingFireTicks > 0);
        }
        this.firstTick = false;
        this.level.getProfiler().pop();
    }

    public void setPortalCooldown() {
        this.portalCooldown = this.getDimensionChangingDelay();
    }

    public boolean isOnPortalCooldown() {
        return this.portalCooldown > 0;
    }

    protected void processPortalCooldown() {
        if (this.isOnPortalCooldown()) {
            --this.portalCooldown;
        }
    }

    public int getPortalWaitTime() {
        return 0;
    }

    protected void lavaHurt() {
        if (this.fireImmune()) {
            return;
        }
        this.setSecondsOnFire(15);
        this.hurt(DamageSource.LAVA, 4.0f);
    }

    public void setSecondsOnFire(int n) {
        int n2 = n * 20;
        if (this instanceof LivingEntity) {
            n2 = ProtectionEnchantment.getFireAfterDampener((LivingEntity)this, n2);
        }
        if (this.remainingFireTicks < n2) {
            this.setRemainingFireTicks(n2);
        }
    }

    public void setRemainingFireTicks(int n) {
        this.remainingFireTicks = n;
    }

    public int getRemainingFireTicks() {
        return this.remainingFireTicks;
    }

    public void clearFire() {
        this.setRemainingFireTicks(0);
    }

    protected void outOfWorld() {
        this.remove();
    }

    public boolean isFree(double d, double d2, double d3) {
        return this.isFree(this.getBoundingBox().move(d, d2, d3));
    }

    private boolean isFree(AABB aABB) {
        return this.level.noCollision(this, aABB) && !this.level.containsAnyLiquid(aABB);
    }

    public void setOnGround(boolean bl) {
        this.onGround = bl;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public void move(MoverType moverType, Vec3 vec3) {
        Vec3 vec32;
        if (this.noPhysics) {
            this.setBoundingBox(this.getBoundingBox().move(vec3));
            this.setLocationFromBoundingbox();
            return;
        }
        if (moverType == MoverType.PISTON && (vec3 = this.limitPistonMovement(vec3)).equals(Vec3.ZERO)) {
            return;
        }
        this.level.getProfiler().push("move");
        if (this.stuckSpeedMultiplier.lengthSqr() > 1.0E-7) {
            vec3 = vec3.multiply(this.stuckSpeedMultiplier);
            this.stuckSpeedMultiplier = Vec3.ZERO;
            this.setDeltaMovement(Vec3.ZERO);
        }
        if ((vec32 = this.collide(vec3 = this.maybeBackOffFromEdge(vec3, moverType))).lengthSqr() > 1.0E-7) {
            this.setBoundingBox(this.getBoundingBox().move(vec32));
            this.setLocationFromBoundingbox();
        }
        this.level.getProfiler().pop();
        this.level.getProfiler().push("rest");
        this.horizontalCollision = !Mth.equal(vec3.x, vec32.x) || !Mth.equal(vec3.z, vec32.z);
        this.verticalCollision = vec3.y != vec32.y;
        this.onGround = this.verticalCollision && vec3.y < 0.0;
        BlockPos blockPos = this.getOnPos();
        BlockState blockState2 = this.level.getBlockState(blockPos);
        this.checkFallDamage(vec32.y, this.onGround, blockState2, blockPos);
        Vec3 vec33 = this.getDeltaMovement();
        if (vec3.x != vec32.x) {
            this.setDeltaMovement(0.0, vec33.y, vec33.z);
        }
        if (vec3.z != vec32.z) {
            this.setDeltaMovement(vec33.x, vec33.y, 0.0);
        }
        Block block = blockState2.getBlock();
        if (vec3.y != vec32.y) {
            block.updateEntityAfterFallOn(this.level, this);
        }
        if (this.onGround && !this.isSteppingCarefully()) {
            block.stepOn(this.level, blockPos, this);
        }
        if (this.isMovementNoisy() && !this.isPassenger()) {
            double d = vec32.x;
            double d2 = vec32.y;
            double d3 = vec32.z;
            if (!block.is(BlockTags.CLIMBABLE)) {
                d2 = 0.0;
            }
            this.walkDist = (float)((double)this.walkDist + (double)Mth.sqrt(Entity.getHorizontalDistanceSqr(vec32)) * 0.6);
            this.moveDist = (float)((double)this.moveDist + (double)Mth.sqrt(d * d + d2 * d2 + d3 * d3) * 0.6);
            if (this.moveDist > this.nextStep && !blockState2.isAir()) {
                this.nextStep = this.nextStep();
                if (this.isInWater()) {
                    Entity entity = this.isVehicle() && this.getControllingPassenger() != null ? this.getControllingPassenger() : this;
                    float f = entity == this ? 0.35f : 0.4f;
                    Vec3 vec34 = entity.getDeltaMovement();
                    float f2 = Mth.sqrt(vec34.x * vec34.x * 0.20000000298023224 + vec34.y * vec34.y + vec34.z * vec34.z * 0.20000000298023224) * f;
                    if (f2 > 1.0f) {
                        f2 = 1.0f;
                    }
                    this.playSwimSound(f2);
                } else {
                    this.playStepSound(blockPos, blockState2);
                }
            } else if (this.moveDist > this.nextFlap && this.makeFlySound() && blockState2.isAir()) {
                this.nextFlap = this.playFlySound(this.moveDist);
            }
        }
        try {
            this.checkInsideBlocks();
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.forThrowable(throwable, "Checking entity block collision");
            CrashReportCategory crashReportCategory = crashReport.addCategory("Entity being checked for collision");
            this.fillCrashReportCategory(crashReportCategory);
            throw new ReportedException(crashReport);
        }
        float f = this.getBlockSpeedFactor();
        this.setDeltaMovement(this.getDeltaMovement().multiply(f, 1.0, f));
        if (this.level.getBlockStatesIfLoaded(this.getBoundingBox().deflate(0.001)).noneMatch(blockState -> blockState.is(BlockTags.FIRE) || blockState.is(Blocks.LAVA)) && this.remainingFireTicks <= 0) {
            this.setRemainingFireTicks(-this.getFireImmuneTicks());
        }
        if (this.isInWaterRainOrBubble() && this.isOnFire()) {
            this.playSound(SoundEvents.GENERIC_EXTINGUISH_FIRE, 0.7f, 1.6f + (this.random.nextFloat() - this.random.nextFloat()) * 0.4f);
            this.setRemainingFireTicks(-this.getFireImmuneTicks());
        }
        this.level.getProfiler().pop();
    }

    protected BlockPos getOnPos() {
        int n;
        BlockPos blockPos;
        int n2;
        BlockState blockState;
        Block block;
        int n3 = Mth.floor(this.position.x);
        BlockPos blockPos2 = new BlockPos(n3, n = Mth.floor(this.position.y - 0.20000000298023224), n2 = Mth.floor(this.position.z));
        if (this.level.getBlockState(blockPos2).isAir() && ((block = (blockState = this.level.getBlockState(blockPos = blockPos2.below())).getBlock()).is(BlockTags.FENCES) || block.is(BlockTags.WALLS) || block instanceof FenceGateBlock)) {
            return blockPos;
        }
        return blockPos2;
    }

    protected float getBlockJumpFactor() {
        float f = this.level.getBlockState(this.blockPosition()).getBlock().getJumpFactor();
        float f2 = this.level.getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).getBlock().getJumpFactor();
        return (double)f == 1.0 ? f2 : f;
    }

    protected float getBlockSpeedFactor() {
        Block block = this.level.getBlockState(this.blockPosition()).getBlock();
        float f = block.getSpeedFactor();
        if (block == Blocks.WATER || block == Blocks.BUBBLE_COLUMN) {
            return f;
        }
        return (double)f == 1.0 ? this.level.getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).getBlock().getSpeedFactor() : f;
    }

    protected BlockPos getBlockPosBelowThatAffectsMyMovement() {
        return new BlockPos(this.position.x, this.getBoundingBox().minY - 0.5000001, this.position.z);
    }

    protected Vec3 maybeBackOffFromEdge(Vec3 vec3, MoverType moverType) {
        return vec3;
    }

    protected Vec3 limitPistonMovement(Vec3 vec3) {
        if (vec3.lengthSqr() <= 1.0E-7) {
            return vec3;
        }
        long l = this.level.getGameTime();
        if (l != this.pistonDeltasGameTime) {
            Arrays.fill(this.pistonDeltas, 0.0);
            this.pistonDeltasGameTime = l;
        }
        if (vec3.x != 0.0) {
            double d = this.applyPistonMovementRestriction(Direction.Axis.X, vec3.x);
            return Math.abs(d) <= 9.999999747378752E-6 ? Vec3.ZERO : new Vec3(d, 0.0, 0.0);
        }
        if (vec3.y != 0.0) {
            double d = this.applyPistonMovementRestriction(Direction.Axis.Y, vec3.y);
            return Math.abs(d) <= 9.999999747378752E-6 ? Vec3.ZERO : new Vec3(0.0, d, 0.0);
        }
        if (vec3.z != 0.0) {
            double d = this.applyPistonMovementRestriction(Direction.Axis.Z, vec3.z);
            return Math.abs(d) <= 9.999999747378752E-6 ? Vec3.ZERO : new Vec3(0.0, 0.0, d);
        }
        return Vec3.ZERO;
    }

    private double applyPistonMovementRestriction(Direction.Axis axis, double d) {
        int n = axis.ordinal();
        double d2 = Mth.clamp(d + this.pistonDeltas[n], -0.51, 0.51);
        d = d2 - this.pistonDeltas[n];
        this.pistonDeltas[n] = d2;
        return d;
    }

    private Vec3 collide(Vec3 vec3) {
        boolean bl;
        AABB aABB = this.getBoundingBox();
        CollisionContext collisionContext = CollisionContext.of(this);
        VoxelShape voxelShape = this.level.getWorldBorder().getCollisionShape();
        Stream<Object> stream = Shapes.joinIsNotEmpty(voxelShape, Shapes.create(aABB.deflate(1.0E-7)), BooleanOp.AND) ? Stream.empty() : Stream.of(voxelShape);
        Stream<VoxelShape> stream2 = this.level.getEntityCollisions(this, aABB.expandTowards(vec3), entity -> true);
        RewindableStream<VoxelShape> rewindableStream = new RewindableStream<VoxelShape>(Stream.concat(stream2, stream));
        Vec3 vec32 = vec3.lengthSqr() == 0.0 ? vec3 : Entity.collideBoundingBoxHeuristically(this, vec3, aABB, this.level, collisionContext, rewindableStream);
        boolean bl2 = vec3.x != vec32.x;
        boolean bl3 = vec3.y != vec32.y;
        boolean bl4 = vec3.z != vec32.z;
        boolean bl5 = bl = this.onGround || bl3 && vec3.y < 0.0;
        if (this.maxUpStep > 0.0f && bl && (bl2 || bl4)) {
            Vec3 vec33;
            Vec3 vec34 = Entity.collideBoundingBoxHeuristically(this, new Vec3(vec3.x, this.maxUpStep, vec3.z), aABB, this.level, collisionContext, rewindableStream);
            Vec3 vec35 = Entity.collideBoundingBoxHeuristically(this, new Vec3(0.0, this.maxUpStep, 0.0), aABB.expandTowards(vec3.x, 0.0, vec3.z), this.level, collisionContext, rewindableStream);
            if (vec35.y < (double)this.maxUpStep && Entity.getHorizontalDistanceSqr(vec33 = Entity.collideBoundingBoxHeuristically(this, new Vec3(vec3.x, 0.0, vec3.z), aABB.move(vec35), this.level, collisionContext, rewindableStream).add(vec35)) > Entity.getHorizontalDistanceSqr(vec34)) {
                vec34 = vec33;
            }
            if (Entity.getHorizontalDistanceSqr(vec34) > Entity.getHorizontalDistanceSqr(vec32)) {
                return vec34.add(Entity.collideBoundingBoxHeuristically(this, new Vec3(0.0, -vec34.y + vec3.y, 0.0), aABB.move(vec34), this.level, collisionContext, rewindableStream));
            }
        }
        return vec32;
    }

    public static double getHorizontalDistanceSqr(Vec3 vec3) {
        return vec3.x * vec3.x + vec3.z * vec3.z;
    }

    public static Vec3 collideBoundingBoxHeuristically(@Nullable Entity entity, Vec3 vec3, AABB aABB, Level level, CollisionContext collisionContext, RewindableStream<VoxelShape> rewindableStream) {
        boolean bl;
        boolean bl2 = vec3.x == 0.0;
        boolean bl3 = vec3.y == 0.0;
        boolean bl4 = bl = vec3.z == 0.0;
        if (bl2 && bl3 || bl2 && bl || bl3 && bl) {
            return Entity.collideBoundingBox(vec3, aABB, level, collisionContext, rewindableStream);
        }
        RewindableStream<VoxelShape> rewindableStream2 = new RewindableStream<VoxelShape>(Stream.concat(rewindableStream.getStream(), level.getBlockCollisions(entity, aABB.expandTowards(vec3))));
        return Entity.collideBoundingBoxLegacy(vec3, aABB, rewindableStream2);
    }

    public static Vec3 collideBoundingBoxLegacy(Vec3 vec3, AABB aABB, RewindableStream<VoxelShape> rewindableStream) {
        boolean bl;
        double d = vec3.x;
        double d2 = vec3.y;
        double d3 = vec3.z;
        if (d2 != 0.0 && (d2 = Shapes.collide(Direction.Axis.Y, aABB, rewindableStream.getStream(), d2)) != 0.0) {
            aABB = aABB.move(0.0, d2, 0.0);
        }
        boolean bl2 = bl = Math.abs(d) < Math.abs(d3);
        if (bl && d3 != 0.0 && (d3 = Shapes.collide(Direction.Axis.Z, aABB, rewindableStream.getStream(), d3)) != 0.0) {
            aABB = aABB.move(0.0, 0.0, d3);
        }
        if (d != 0.0) {
            d = Shapes.collide(Direction.Axis.X, aABB, rewindableStream.getStream(), d);
            if (!bl && d != 0.0) {
                aABB = aABB.move(d, 0.0, 0.0);
            }
        }
        if (!bl && d3 != 0.0) {
            d3 = Shapes.collide(Direction.Axis.Z, aABB, rewindableStream.getStream(), d3);
        }
        return new Vec3(d, d2, d3);
    }

    public static Vec3 collideBoundingBox(Vec3 vec3, AABB aABB, LevelReader levelReader, CollisionContext collisionContext, RewindableStream<VoxelShape> rewindableStream) {
        boolean bl;
        double d = vec3.x;
        double d2 = vec3.y;
        double d3 = vec3.z;
        if (d2 != 0.0 && (d2 = Shapes.collide(Direction.Axis.Y, aABB, levelReader, d2, collisionContext, rewindableStream.getStream())) != 0.0) {
            aABB = aABB.move(0.0, d2, 0.0);
        }
        boolean bl2 = bl = Math.abs(d) < Math.abs(d3);
        if (bl && d3 != 0.0 && (d3 = Shapes.collide(Direction.Axis.Z, aABB, levelReader, d3, collisionContext, rewindableStream.getStream())) != 0.0) {
            aABB = aABB.move(0.0, 0.0, d3);
        }
        if (d != 0.0) {
            d = Shapes.collide(Direction.Axis.X, aABB, levelReader, d, collisionContext, rewindableStream.getStream());
            if (!bl && d != 0.0) {
                aABB = aABB.move(d, 0.0, 0.0);
            }
        }
        if (!bl && d3 != 0.0) {
            d3 = Shapes.collide(Direction.Axis.Z, aABB, levelReader, d3, collisionContext, rewindableStream.getStream());
        }
        return new Vec3(d, d2, d3);
    }

    protected float nextStep() {
        return (int)this.moveDist + 1;
    }

    public void setLocationFromBoundingbox() {
        AABB aABB = this.getBoundingBox();
        this.setPosRaw((aABB.minX + aABB.maxX) / 2.0, aABB.minY, (aABB.minZ + aABB.maxZ) / 2.0);
    }

    protected SoundEvent getSwimSound() {
        return SoundEvents.GENERIC_SWIM;
    }

    protected SoundEvent getSwimSplashSound() {
        return SoundEvents.GENERIC_SPLASH;
    }

    protected SoundEvent getSwimHighSpeedSplashSound() {
        return SoundEvents.GENERIC_SPLASH;
    }

    protected void checkInsideBlocks() {
        AABB aABB = this.getBoundingBox();
        BlockPos blockPos = new BlockPos(aABB.minX + 0.001, aABB.minY + 0.001, aABB.minZ + 0.001);
        BlockPos blockPos2 = new BlockPos(aABB.maxX - 0.001, aABB.maxY - 0.001, aABB.maxZ - 0.001);
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        if (this.level.hasChunksAt(blockPos, blockPos2)) {
            for (int i = blockPos.getX(); i <= blockPos2.getX(); ++i) {
                for (int j = blockPos.getY(); j <= blockPos2.getY(); ++j) {
                    for (int k = blockPos.getZ(); k <= blockPos2.getZ(); ++k) {
                        mutableBlockPos.set(i, j, k);
                        BlockState blockState = this.level.getBlockState(mutableBlockPos);
                        try {
                            blockState.entityInside(this.level, mutableBlockPos, this);
                            this.onInsideBlock(blockState);
                            continue;
                        }
                        catch (Throwable throwable) {
                            CrashReport crashReport = CrashReport.forThrowable(throwable, "Colliding entity with block");
                            CrashReportCategory crashReportCategory = crashReport.addCategory("Block being collided with");
                            CrashReportCategory.populateBlockDetails(crashReportCategory, mutableBlockPos, blockState);
                            throw new ReportedException(crashReport);
                        }
                    }
                }
            }
        }
    }

    protected void onInsideBlock(BlockState blockState) {
    }

    protected void playStepSound(BlockPos blockPos, BlockState blockState) {
        if (blockState.getMaterial().isLiquid()) {
            return;
        }
        BlockState blockState2 = this.level.getBlockState(blockPos.above());
        SoundType soundType = blockState2.is(Blocks.SNOW) ? blockState2.getSoundType() : blockState.getSoundType();
        this.playSound(soundType.getStepSound(), soundType.getVolume() * 0.15f, soundType.getPitch());
    }

    protected void playSwimSound(float f) {
        this.playSound(this.getSwimSound(), f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.4f);
    }

    protected float playFlySound(float f) {
        return 0.0f;
    }

    protected boolean makeFlySound() {
        return false;
    }

    public void playSound(SoundEvent soundEvent, float f, float f2) {
        if (!this.isSilent()) {
            this.level.playSound(null, this.getX(), this.getY(), this.getZ(), soundEvent, this.getSoundSource(), f, f2);
        }
    }

    public boolean isSilent() {
        return this.entityData.get(DATA_SILENT);
    }

    public void setSilent(boolean bl) {
        this.entityData.set(DATA_SILENT, bl);
    }

    public boolean isNoGravity() {
        return this.entityData.get(DATA_NO_GRAVITY);
    }

    public void setNoGravity(boolean bl) {
        this.entityData.set(DATA_NO_GRAVITY, bl);
    }

    protected boolean isMovementNoisy() {
        return true;
    }

    protected void checkFallDamage(double d, boolean bl, BlockState blockState, BlockPos blockPos) {
        if (bl) {
            if (this.fallDistance > 0.0f) {
                blockState.getBlock().fallOn(this.level, blockPos, this, this.fallDistance);
            }
            this.fallDistance = 0.0f;
        } else if (d < 0.0) {
            this.fallDistance = (float)((double)this.fallDistance - d);
        }
    }

    public boolean fireImmune() {
        return this.getType().fireImmune();
    }

    public boolean causeFallDamage(float f, float f2) {
        if (this.isVehicle()) {
            for (Entity entity : this.getPassengers()) {
                entity.causeFallDamage(f, f2);
            }
        }
        return false;
    }

    public boolean isInWater() {
        return this.wasTouchingWater;
    }

    private boolean isInRain() {
        BlockPos blockPos = this.blockPosition();
        return this.level.isRainingAt(blockPos) || this.level.isRainingAt(new BlockPos((double)blockPos.getX(), this.getBoundingBox().maxY, (double)blockPos.getZ()));
    }

    private boolean isInBubbleColumn() {
        return this.level.getBlockState(this.blockPosition()).is(Blocks.BUBBLE_COLUMN);
    }

    public boolean isInWaterOrRain() {
        return this.isInWater() || this.isInRain();
    }

    public boolean isInWaterRainOrBubble() {
        return this.isInWater() || this.isInRain() || this.isInBubbleColumn();
    }

    public boolean isInWaterOrBubble() {
        return this.isInWater() || this.isInBubbleColumn();
    }

    public boolean isUnderWater() {
        return this.wasEyeInWater && this.isInWater();
    }

    public void updateSwimming() {
        if (this.isSwimming()) {
            this.setSwimming(this.isSprinting() && this.isInWater() && !this.isPassenger());
        } else {
            this.setSwimming(this.isSprinting() && this.isUnderWater() && !this.isPassenger());
        }
    }

    protected boolean updateInWaterStateAndDoFluidPushing() {
        this.fluidHeight.clear();
        this.updateInWaterStateAndDoWaterCurrentPushing();
        double d = this.level.dimensionType().ultraWarm() ? 0.007 : 0.0023333333333333335;
        boolean bl = this.updateFluidHeightAndDoFluidPushing(FluidTags.LAVA, d);
        return this.isInWater() || bl;
    }

    void updateInWaterStateAndDoWaterCurrentPushing() {
        if (this.getVehicle() instanceof Boat) {
            this.wasTouchingWater = false;
        } else if (this.updateFluidHeightAndDoFluidPushing(FluidTags.WATER, 0.014)) {
            if (!this.wasTouchingWater && !this.firstTick) {
                this.doWaterSplashEffect();
            }
            this.fallDistance = 0.0f;
            this.wasTouchingWater = true;
            this.clearFire();
        } else {
            this.wasTouchingWater = false;
        }
    }

    private void updateFluidOnEyes() {
        Object object;
        this.wasEyeInWater = this.isEyeInFluid(FluidTags.WATER);
        this.fluidOnEyes = null;
        double d = this.getEyeY() - 0.1111111119389534;
        Entity entity = this.getVehicle();
        if (entity instanceof Boat && !((Boat)(object = (Boat)entity)).isUnderWater() && object.getBoundingBox().maxY >= d && object.getBoundingBox().minY <= d) {
            return;
        }
        object = new BlockPos(this.getX(), d, this.getZ());
        FluidState fluidState = this.level.getFluidState((BlockPos)object);
        for (Tag tag : FluidTags.getWrappers()) {
            if (!fluidState.is(tag)) continue;
            double d2 = (float)((Vec3i)object).getY() + fluidState.getHeight(this.level, (BlockPos)object);
            if (d2 > d) {
                this.fluidOnEyes = tag;
            }
            return;
        }
    }

    protected void doWaterSplashEffect() {
        double d;
        double d2;
        Entity entity = this.isVehicle() && this.getControllingPassenger() != null ? this.getControllingPassenger() : this;
        float f = entity == this ? 0.2f : 0.9f;
        Vec3 vec3 = entity.getDeltaMovement();
        float f2 = Mth.sqrt(vec3.x * vec3.x * 0.20000000298023224 + vec3.y * vec3.y + vec3.z * vec3.z * 0.20000000298023224) * f;
        if (f2 > 1.0f) {
            f2 = 1.0f;
        }
        if ((double)f2 < 0.25) {
            this.playSound(this.getSwimSplashSound(), f2, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.4f);
        } else {
            this.playSound(this.getSwimHighSpeedSplashSound(), f2, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.4f);
        }
        float f3 = Mth.floor(this.getY());
        int n = 0;
        while ((float)n < 1.0f + this.dimensions.width * 20.0f) {
            d = (this.random.nextDouble() * 2.0 - 1.0) * (double)this.dimensions.width;
            d2 = (this.random.nextDouble() * 2.0 - 1.0) * (double)this.dimensions.width;
            this.level.addParticle(ParticleTypes.BUBBLE, this.getX() + d, f3 + 1.0f, this.getZ() + d2, vec3.x, vec3.y - this.random.nextDouble() * 0.20000000298023224, vec3.z);
            ++n;
        }
        n = 0;
        while ((float)n < 1.0f + this.dimensions.width * 20.0f) {
            d = (this.random.nextDouble() * 2.0 - 1.0) * (double)this.dimensions.width;
            d2 = (this.random.nextDouble() * 2.0 - 1.0) * (double)this.dimensions.width;
            this.level.addParticle(ParticleTypes.SPLASH, this.getX() + d, f3 + 1.0f, this.getZ() + d2, vec3.x, vec3.y, vec3.z);
            ++n;
        }
    }

    protected BlockState getBlockStateOn() {
        return this.level.getBlockState(this.getOnPos());
    }

    public boolean canSpawnSprintParticle() {
        return this.isSprinting() && !this.isInWater() && !this.isSpectator() && !this.isCrouching() && !this.isInLava() && this.isAlive();
    }

    protected void spawnSprintParticle() {
        int n;
        int n2;
        int n3 = Mth.floor(this.getX());
        BlockPos blockPos = new BlockPos(n3, n = Mth.floor(this.getY() - 0.20000000298023224), n2 = Mth.floor(this.getZ()));
        BlockState blockState = this.level.getBlockState(blockPos);
        if (blockState.getRenderShape() != RenderShape.INVISIBLE) {
            Vec3 vec3 = this.getDeltaMovement();
            this.level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockState), this.getX() + (this.random.nextDouble() - 0.5) * (double)this.dimensions.width, this.getY() + 0.1, this.getZ() + (this.random.nextDouble() - 0.5) * (double)this.dimensions.width, vec3.x * -4.0, 1.5, vec3.z * -4.0);
        }
    }

    public boolean isEyeInFluid(Tag<Fluid> tag) {
        return this.fluidOnEyes == tag;
    }

    public boolean isInLava() {
        return !this.firstTick && this.fluidHeight.getDouble(FluidTags.LAVA) > 0.0;
    }

    public void moveRelative(float f, Vec3 vec3) {
        Vec3 vec32 = Entity.getInputVector(vec3, f, this.yRot);
        this.setDeltaMovement(this.getDeltaMovement().add(vec32));
    }

    private static Vec3 getInputVector(Vec3 vec3, float f, float f2) {
        double d = vec3.lengthSqr();
        if (d < 1.0E-7) {
            return Vec3.ZERO;
        }
        Vec3 vec32 = (d > 1.0 ? vec3.normalize() : vec3).scale(f);
        float f3 = Mth.sin(f2 * 0.017453292f);
        float f4 = Mth.cos(f2 * 0.017453292f);
        return new Vec3(vec32.x * (double)f4 - vec32.z * (double)f3, vec32.y, vec32.z * (double)f4 + vec32.x * (double)f3);
    }

    public float getBrightness() {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos(this.getX(), 0.0, this.getZ());
        if (this.level.hasChunkAt(mutableBlockPos)) {
            mutableBlockPos.setY(Mth.floor(this.getEyeY()));
            return this.level.getBrightness(mutableBlockPos);
        }
        return 0.0f;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public void absMoveTo(double d, double d2, double d3, float f, float f2) {
        this.absMoveTo(d, d2, d3);
        this.yRot = f % 360.0f;
        this.xRot = Mth.clamp(f2, -90.0f, 90.0f) % 360.0f;
        this.yRotO = this.yRot;
        this.xRotO = this.xRot;
    }

    public void absMoveTo(double d, double d2, double d3) {
        double d4 = Mth.clamp(d, -3.0E7, 3.0E7);
        double d5 = Mth.clamp(d3, -3.0E7, 3.0E7);
        this.xo = d4;
        this.yo = d2;
        this.zo = d5;
        this.setPos(d4, d2, d5);
    }

    public void moveTo(Vec3 vec3) {
        this.moveTo(vec3.x, vec3.y, vec3.z);
    }

    public void moveTo(double d, double d2, double d3) {
        this.moveTo(d, d2, d3, this.yRot, this.xRot);
    }

    public void moveTo(BlockPos blockPos, float f, float f2) {
        this.moveTo((double)blockPos.getX() + 0.5, blockPos.getY(), (double)blockPos.getZ() + 0.5, f, f2);
    }

    public void moveTo(double d, double d2, double d3, float f, float f2) {
        this.setPosAndOldPos(d, d2, d3);
        this.yRot = f;
        this.xRot = f2;
        this.reapplyPosition();
    }

    public void setPosAndOldPos(double d, double d2, double d3) {
        this.setPosRaw(d, d2, d3);
        this.xo = d;
        this.yo = d2;
        this.zo = d3;
        this.xOld = d;
        this.yOld = d2;
        this.zOld = d3;
    }

    public float distanceTo(Entity entity) {
        float f = (float)(this.getX() - entity.getX());
        float f2 = (float)(this.getY() - entity.getY());
        float f3 = (float)(this.getZ() - entity.getZ());
        return Mth.sqrt(f * f + f2 * f2 + f3 * f3);
    }

    public double distanceToSqr(double d, double d2, double d3) {
        double d4 = this.getX() - d;
        double d5 = this.getY() - d2;
        double d6 = this.getZ() - d3;
        return d4 * d4 + d5 * d5 + d6 * d6;
    }

    public double distanceToSqr(Entity entity) {
        return this.distanceToSqr(entity.position());
    }

    public double distanceToSqr(Vec3 vec3) {
        double d = this.getX() - vec3.x;
        double d2 = this.getY() - vec3.y;
        double d3 = this.getZ() - vec3.z;
        return d * d + d2 * d2 + d3 * d3;
    }

    public void playerTouch(Player player) {
    }

    public void push(Entity entity) {
        double d;
        if (this.isPassengerOfSameVehicle(entity)) {
            return;
        }
        if (entity.noPhysics || this.noPhysics) {
            return;
        }
        double d2 = entity.getX() - this.getX();
        double d3 = Mth.absMax(d2, d = entity.getZ() - this.getZ());
        if (d3 >= 0.009999999776482582) {
            d3 = Mth.sqrt(d3);
            d2 /= d3;
            d /= d3;
            double d4 = 1.0 / d3;
            if (d4 > 1.0) {
                d4 = 1.0;
            }
            d2 *= d4;
            d *= d4;
            d2 *= 0.05000000074505806;
            d *= 0.05000000074505806;
            d2 *= (double)(1.0f - this.pushthrough);
            d *= (double)(1.0f - this.pushthrough);
            if (!this.isVehicle()) {
                this.push(-d2, 0.0, -d);
            }
            if (!entity.isVehicle()) {
                entity.push(d2, 0.0, d);
            }
        }
    }

    public void push(double d, double d2, double d3) {
        this.setDeltaMovement(this.getDeltaMovement().add(d, d2, d3));
        this.hasImpulse = true;
    }

    protected void markHurt() {
        this.hurtMarked = true;
    }

    public boolean hurt(DamageSource damageSource, float f) {
        if (this.isInvulnerableTo(damageSource)) {
            return false;
        }
        this.markHurt();
        return false;
    }

    public final Vec3 getViewVector(float f) {
        return this.calculateViewVector(this.getViewXRot(f), this.getViewYRot(f));
    }

    public float getViewXRot(float f) {
        if (f == 1.0f) {
            return this.xRot;
        }
        return Mth.lerp(f, this.xRotO, this.xRot);
    }

    public float getViewYRot(float f) {
        if (f == 1.0f) {
            return this.yRot;
        }
        return Mth.lerp(f, this.yRotO, this.yRot);
    }

    protected final Vec3 calculateViewVector(float f, float f2) {
        float f3 = f * 0.017453292f;
        float f4 = -f2 * 0.017453292f;
        float f5 = Mth.cos(f4);
        float f6 = Mth.sin(f4);
        float f7 = Mth.cos(f3);
        float f8 = Mth.sin(f3);
        return new Vec3(f6 * f7, -f8, f5 * f7);
    }

    public final Vec3 getUpVector(float f) {
        return this.calculateUpVector(this.getViewXRot(f), this.getViewYRot(f));
    }

    protected final Vec3 calculateUpVector(float f, float f2) {
        return this.calculateViewVector(f - 90.0f, f2);
    }

    public final Vec3 getEyePosition(float f) {
        if (f == 1.0f) {
            return new Vec3(this.getX(), this.getEyeY(), this.getZ());
        }
        double d = Mth.lerp((double)f, this.xo, this.getX());
        double d2 = Mth.lerp((double)f, this.yo, this.getY()) + (double)this.getEyeHeight();
        double d3 = Mth.lerp((double)f, this.zo, this.getZ());
        return new Vec3(d, d2, d3);
    }

    public Vec3 getLightProbePosition(float f) {
        return this.getEyePosition(f);
    }

    public final Vec3 getPosition(float f) {
        double d = Mth.lerp((double)f, this.xo, this.getX());
        double d2 = Mth.lerp((double)f, this.yo, this.getY());
        double d3 = Mth.lerp((double)f, this.zo, this.getZ());
        return new Vec3(d, d2, d3);
    }

    public HitResult pick(double d, float f, boolean bl) {
        Vec3 vec3 = this.getEyePosition(f);
        Vec3 vec32 = this.getViewVector(f);
        Vec3 vec33 = vec3.add(vec32.x * d, vec32.y * d, vec32.z * d);
        return this.level.clip(new ClipContext(vec3, vec33, ClipContext.Block.OUTLINE, bl ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, this));
    }

    public boolean isPickable() {
        return false;
    }

    public boolean isPushable() {
        return false;
    }

    public void awardKillScore(Entity entity, int n, DamageSource damageSource) {
        if (entity instanceof ServerPlayer) {
            CriteriaTriggers.ENTITY_KILLED_PLAYER.trigger((ServerPlayer)entity, this, damageSource);
        }
    }

    public boolean shouldRender(double d, double d2, double d3) {
        double d4 = this.getX() - d;
        double d5 = this.getY() - d2;
        double d6 = this.getZ() - d3;
        double d7 = d4 * d4 + d5 * d5 + d6 * d6;
        return this.shouldRenderAtSqrDistance(d7);
    }

    public boolean shouldRenderAtSqrDistance(double d) {
        double d2 = this.getBoundingBox().getSize();
        if (Double.isNaN(d2)) {
            d2 = 1.0;
        }
        return d < (d2 *= 64.0 * viewScale) * d2;
    }

    public boolean saveAsPassenger(CompoundTag compoundTag) {
        String string = this.getEncodeId();
        if (this.removed || string == null) {
            return false;
        }
        compoundTag.putString("id", string);
        this.saveWithoutId(compoundTag);
        return true;
    }

    public boolean save(CompoundTag compoundTag) {
        if (this.isPassenger()) {
            return false;
        }
        return this.saveAsPassenger(compoundTag);
    }

    public CompoundTag saveWithoutId(CompoundTag compoundTag) {
        try {
            ListTag listTag;
            if (this.vehicle != null) {
                compoundTag.put("Pos", this.newDoubleList(this.vehicle.getX(), this.getY(), this.vehicle.getZ()));
            } else {
                compoundTag.put("Pos", this.newDoubleList(this.getX(), this.getY(), this.getZ()));
            }
            Vec3 vec3 = this.getDeltaMovement();
            compoundTag.put("Motion", this.newDoubleList(vec3.x, vec3.y, vec3.z));
            compoundTag.put("Rotation", this.newFloatList(this.yRot, this.xRot));
            compoundTag.putFloat("FallDistance", this.fallDistance);
            compoundTag.putShort("Fire", (short)this.remainingFireTicks);
            compoundTag.putShort("Air", (short)this.getAirSupply());
            compoundTag.putBoolean("OnGround", this.onGround);
            compoundTag.putBoolean("Invulnerable", this.invulnerable);
            compoundTag.putInt("PortalCooldown", this.portalCooldown);
            compoundTag.putUUID("UUID", this.getUUID());
            Component component = this.getCustomName();
            if (component != null) {
                compoundTag.putString("CustomName", Component.Serializer.toJson(component));
            }
            if (this.isCustomNameVisible()) {
                compoundTag.putBoolean("CustomNameVisible", this.isCustomNameVisible());
            }
            if (this.isSilent()) {
                compoundTag.putBoolean("Silent", this.isSilent());
            }
            if (this.isNoGravity()) {
                compoundTag.putBoolean("NoGravity", this.isNoGravity());
            }
            if (this.glowing) {
                compoundTag.putBoolean("Glowing", this.glowing);
            }
            if (!this.tags.isEmpty()) {
                listTag = new ListTag();
                for (String object : this.tags) {
                    listTag.add(StringTag.valueOf(object));
                }
                compoundTag.put("Tags", listTag);
            }
            this.addAdditionalSaveData(compoundTag);
            if (this.isVehicle()) {
                listTag = new ListTag();
                for (Entity entity : this.getPassengers()) {
                    CompoundTag compoundTag2;
                    if (!entity.saveAsPassenger(compoundTag2 = new CompoundTag())) continue;
                    listTag.add(compoundTag2);
                }
                if (!listTag.isEmpty()) {
                    compoundTag.put("Passengers", listTag);
                }
            }
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.forThrowable(throwable, "Saving entity NBT");
            CrashReportCategory crashReportCategory = crashReport.addCategory("Entity being saved");
            this.fillCrashReportCategory(crashReportCategory);
            throw new ReportedException(crashReport);
        }
        return compoundTag;
    }

    public void load(CompoundTag compoundTag) {
        try {
            Object object;
            ListTag listTag = compoundTag.getList("Pos", 6);
            ListTag listTag2 = compoundTag.getList("Motion", 6);
            ListTag listTag3 = compoundTag.getList("Rotation", 5);
            double d = listTag2.getDouble(0);
            double d2 = listTag2.getDouble(1);
            double d3 = listTag2.getDouble(2);
            this.setDeltaMovement(Math.abs(d) > 10.0 ? 0.0 : d, Math.abs(d2) > 10.0 ? 0.0 : d2, Math.abs(d3) > 10.0 ? 0.0 : d3);
            this.setPosAndOldPos(listTag.getDouble(0), listTag.getDouble(1), listTag.getDouble(2));
            this.yRot = listTag3.getFloat(0);
            this.xRot = listTag3.getFloat(1);
            this.yRotO = this.yRot;
            this.xRotO = this.xRot;
            this.setYHeadRot(this.yRot);
            this.setYBodyRot(this.yRot);
            this.fallDistance = compoundTag.getFloat("FallDistance");
            this.remainingFireTicks = compoundTag.getShort("Fire");
            this.setAirSupply(compoundTag.getShort("Air"));
            this.onGround = compoundTag.getBoolean("OnGround");
            this.invulnerable = compoundTag.getBoolean("Invulnerable");
            this.portalCooldown = compoundTag.getInt("PortalCooldown");
            if (compoundTag.hasUUID("UUID")) {
                this.uuid = compoundTag.getUUID("UUID");
                this.stringUUID = this.uuid.toString();
            }
            if (!(Double.isFinite(this.getX()) && Double.isFinite(this.getY()) && Double.isFinite(this.getZ()))) {
                throw new IllegalStateException("Entity has invalid position");
            }
            if (!Double.isFinite(this.yRot) || !Double.isFinite(this.xRot)) {
                throw new IllegalStateException("Entity has invalid rotation");
            }
            this.reapplyPosition();
            this.setRot(this.yRot, this.xRot);
            if (compoundTag.contains("CustomName", 8)) {
                object = compoundTag.getString("CustomName");
                try {
                    this.setCustomName(Component.Serializer.fromJson((String)object));
                }
                catch (Exception exception) {
                    LOGGER.warn("Failed to parse entity custom name {}", object, (Object)exception);
                }
            }
            this.setCustomNameVisible(compoundTag.getBoolean("CustomNameVisible"));
            this.setSilent(compoundTag.getBoolean("Silent"));
            this.setNoGravity(compoundTag.getBoolean("NoGravity"));
            this.setGlowing(compoundTag.getBoolean("Glowing"));
            if (compoundTag.contains("Tags", 9)) {
                this.tags.clear();
                object = compoundTag.getList("Tags", 8);
                int n = Math.min(((ListTag)object).size(), 1024);
                for (int i = 0; i < n; ++i) {
                    this.tags.add(((ListTag)object).getString(i));
                }
            }
            this.readAdditionalSaveData(compoundTag);
            if (this.repositionEntityAfterLoad()) {
                this.reapplyPosition();
            }
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.forThrowable(throwable, "Loading entity NBT");
            CrashReportCategory crashReportCategory = crashReport.addCategory("Entity being loaded");
            this.fillCrashReportCategory(crashReportCategory);
            throw new ReportedException(crashReport);
        }
    }

    protected boolean repositionEntityAfterLoad() {
        return true;
    }

    @Nullable
    protected final String getEncodeId() {
        EntityType<?> entityType = this.getType();
        ResourceLocation resourceLocation = EntityType.getKey(entityType);
        return !entityType.canSerialize() || resourceLocation == null ? null : resourceLocation.toString();
    }

    protected abstract void readAdditionalSaveData(CompoundTag var1);

    protected abstract void addAdditionalSaveData(CompoundTag var1);

    protected ListTag newDoubleList(double ... arrd) {
        ListTag listTag = new ListTag();
        for (double d : arrd) {
            listTag.add(DoubleTag.valueOf(d));
        }
        return listTag;
    }

    protected ListTag newFloatList(float ... arrf) {
        ListTag listTag = new ListTag();
        for (float f : arrf) {
            listTag.add(FloatTag.valueOf(f));
        }
        return listTag;
    }

    @Nullable
    public ItemEntity spawnAtLocation(ItemLike itemLike) {
        return this.spawnAtLocation(itemLike, 0);
    }

    @Nullable
    public ItemEntity spawnAtLocation(ItemLike itemLike, int n) {
        return this.spawnAtLocation(new ItemStack(itemLike), (float)n);
    }

    @Nullable
    public ItemEntity spawnAtLocation(ItemStack itemStack) {
        return this.spawnAtLocation(itemStack, 0.0f);
    }

    @Nullable
    public ItemEntity spawnAtLocation(ItemStack itemStack, float f) {
        if (itemStack.isEmpty()) {
            return null;
        }
        if (this.level.isClientSide) {
            return null;
        }
        ItemEntity itemEntity = new ItemEntity(this.level, this.getX(), this.getY() + (double)f, this.getZ(), itemStack);
        itemEntity.setDefaultPickUpDelay();
        this.level.addFreshEntity(itemEntity);
        return itemEntity;
    }

    public boolean isAlive() {
        return !this.removed;
    }

    public boolean isInWall() {
        if (this.noPhysics) {
            return false;
        }
        float f = 0.1f;
        float f2 = this.dimensions.width * 0.8f;
        AABB aABB = AABB.ofSize(f2, 0.10000000149011612, f2).move(this.getX(), this.getEyeY(), this.getZ());
        return this.level.getBlockCollisions(this, aABB, (blockState, blockPos) -> blockState.isSuffocating(this.level, (BlockPos)blockPos)).findAny().isPresent();
    }

    public InteractionResult interact(Player player, InteractionHand interactionHand) {
        return InteractionResult.PASS;
    }

    public boolean canCollideWith(Entity entity) {
        return entity.canBeCollidedWith() && !this.isPassengerOfSameVehicle(entity);
    }

    public boolean canBeCollidedWith() {
        return false;
    }

    public void rideTick() {
        this.setDeltaMovement(Vec3.ZERO);
        this.tick();
        if (!this.isPassenger()) {
            return;
        }
        this.getVehicle().positionRider(this);
    }

    public void positionRider(Entity entity) {
        this.positionRider(entity, Entity::setPos);
    }

    private void positionRider(Entity entity, MoveFunction moveFunction) {
        if (!this.hasPassenger(entity)) {
            return;
        }
        double d = this.getY() + this.getPassengersRidingOffset() + entity.getMyRidingOffset();
        moveFunction.accept(entity, this.getX(), d, this.getZ());
    }

    public void onPassengerTurned(Entity entity) {
    }

    public double getMyRidingOffset() {
        return 0.0;
    }

    public double getPassengersRidingOffset() {
        return (double)this.dimensions.height * 0.75;
    }

    public boolean startRiding(Entity entity) {
        return this.startRiding(entity, false);
    }

    public boolean showVehicleHealth() {
        return this instanceof LivingEntity;
    }

    public boolean startRiding(Entity entity, boolean bl) {
        Entity entity2 = entity;
        while (entity2.vehicle != null) {
            if (entity2.vehicle == this) {
                return false;
            }
            entity2 = entity2.vehicle;
        }
        if (!(bl || this.canRide(entity) && entity.canAddPassenger(this))) {
            return false;
        }
        if (this.isPassenger()) {
            this.stopRiding();
        }
        this.setPose(Pose.STANDING);
        this.vehicle = entity;
        this.vehicle.addPassenger(this);
        return true;
    }

    protected boolean canRide(Entity entity) {
        return !this.isShiftKeyDown() && this.boardingCooldown <= 0;
    }

    protected boolean canEnterPose(Pose pose) {
        return this.level.noCollision(this, this.getBoundingBoxForPose(pose).deflate(1.0E-7));
    }

    public void ejectPassengers() {
        for (int i = this.passengers.size() - 1; i >= 0; --i) {
            this.passengers.get(i).stopRiding();
        }
    }

    public void removeVehicle() {
        if (this.vehicle != null) {
            Entity entity = this.vehicle;
            this.vehicle = null;
            entity.removePassenger(this);
        }
    }

    public void stopRiding() {
        this.removeVehicle();
    }

    protected void addPassenger(Entity entity) {
        if (entity.getVehicle() != this) {
            throw new IllegalStateException("Use x.startRiding(y), not y.addPassenger(x)");
        }
        if (!this.level.isClientSide && entity instanceof Player && !(this.getControllingPassenger() instanceof Player)) {
            this.passengers.add(0, entity);
        } else {
            this.passengers.add(entity);
        }
    }

    protected void removePassenger(Entity entity) {
        if (entity.getVehicle() == this) {
            throw new IllegalStateException("Use x.stopRiding(y), not y.removePassenger(x)");
        }
        this.passengers.remove(entity);
        entity.boardingCooldown = 60;
    }

    protected boolean canAddPassenger(Entity entity) {
        return this.getPassengers().size() < 1;
    }

    public void lerpTo(double d, double d2, double d3, float f, float f2, int n, boolean bl) {
        this.setPos(d, d2, d3);
        this.setRot(f, f2);
    }

    public void lerpHeadTo(float f, int n) {
        this.setYHeadRot(f);
    }

    public float getPickRadius() {
        return 0.0f;
    }

    public Vec3 getLookAngle() {
        return this.calculateViewVector(this.xRot, this.yRot);
    }

    public Vec2 getRotationVector() {
        return new Vec2(this.xRot, this.yRot);
    }

    public Vec3 getForward() {
        return Vec3.directionFromRotation(this.getRotationVector());
    }

    public void handleInsidePortal(BlockPos blockPos) {
        if (this.isOnPortalCooldown()) {
            this.setPortalCooldown();
            return;
        }
        if (!this.level.isClientSide && !blockPos.equals(this.portalEntrancePos)) {
            this.portalEntrancePos = blockPos.immutable();
        }
        this.isInsidePortal = true;
    }

    protected void handleNetherPortal() {
        if (!(this.level instanceof ServerLevel)) {
            return;
        }
        int n = this.getPortalWaitTime();
        ServerLevel serverLevel = (ServerLevel)this.level;
        if (this.isInsidePortal) {
            ResourceKey<Level> resourceKey;
            MinecraftServer minecraftServer = serverLevel.getServer();
            ServerLevel serverLevel2 = minecraftServer.getLevel(resourceKey = this.level.dimension() == Level.NETHER ? Level.OVERWORLD : Level.NETHER);
            if (serverLevel2 != null && minecraftServer.isNetherEnabled() && !this.isPassenger() && this.portalTime++ >= n) {
                this.level.getProfiler().push("portal");
                this.portalTime = n;
                this.setPortalCooldown();
                this.changeDimension(serverLevel2);
                this.level.getProfiler().pop();
            }
            this.isInsidePortal = false;
        } else {
            if (this.portalTime > 0) {
                this.portalTime -= 4;
            }
            if (this.portalTime < 0) {
                this.portalTime = 0;
            }
        }
        this.processPortalCooldown();
    }

    public int getDimensionChangingDelay() {
        return 300;
    }

    public void lerpMotion(double d, double d2, double d3) {
        this.setDeltaMovement(d, d2, d3);
    }

    public void handleEntityEvent(byte by) {
        switch (by) {
            case 53: {
                HoneyBlock.showSlideParticles(this);
            }
        }
    }

    public void animateHurt() {
    }

    public Iterable<ItemStack> getHandSlots() {
        return EMPTY_LIST;
    }

    public Iterable<ItemStack> getArmorSlots() {
        return EMPTY_LIST;
    }

    public Iterable<ItemStack> getAllSlots() {
        return Iterables.concat(this.getHandSlots(), this.getArmorSlots());
    }

    public void setItemSlot(EquipmentSlot equipmentSlot, ItemStack itemStack) {
    }

    public boolean isOnFire() {
        boolean bl = this.level != null && this.level.isClientSide;
        return !this.fireImmune() && (this.remainingFireTicks > 0 || bl && this.getSharedFlag(0));
    }

    public boolean isPassenger() {
        return this.getVehicle() != null;
    }

    public boolean isVehicle() {
        return !this.getPassengers().isEmpty();
    }

    public boolean rideableUnderWater() {
        return true;
    }

    public void setShiftKeyDown(boolean bl) {
        this.setSharedFlag(1, bl);
    }

    public boolean isShiftKeyDown() {
        return this.getSharedFlag(1);
    }

    public boolean isSteppingCarefully() {
        return this.isShiftKeyDown();
    }

    public boolean isSuppressingBounce() {
        return this.isShiftKeyDown();
    }

    public boolean isDiscrete() {
        return this.isShiftKeyDown();
    }

    public boolean isDescending() {
        return this.isShiftKeyDown();
    }

    public boolean isCrouching() {
        return this.getPose() == Pose.CROUCHING;
    }

    public boolean isSprinting() {
        return this.getSharedFlag(3);
    }

    public void setSprinting(boolean bl) {
        this.setSharedFlag(3, bl);
    }

    public boolean isSwimming() {
        return this.getSharedFlag(4);
    }

    public boolean isVisuallySwimming() {
        return this.getPose() == Pose.SWIMMING;
    }

    public boolean isVisuallyCrawling() {
        return this.isVisuallySwimming() && !this.isInWater();
    }

    public void setSwimming(boolean bl) {
        this.setSharedFlag(4, bl);
    }

    public boolean isGlowing() {
        return this.glowing || this.level.isClientSide && this.getSharedFlag(6);
    }

    public void setGlowing(boolean bl) {
        this.glowing = bl;
        if (!this.level.isClientSide) {
            this.setSharedFlag(6, this.glowing);
        }
    }

    public boolean isInvisible() {
        return this.getSharedFlag(5);
    }

    public boolean isInvisibleTo(Player player) {
        if (player.isSpectator()) {
            return false;
        }
        Team team = this.getTeam();
        if (team != null && player != null && player.getTeam() == team && team.canSeeFriendlyInvisibles()) {
            return false;
        }
        return this.isInvisible();
    }

    @Nullable
    public Team getTeam() {
        return this.level.getScoreboard().getPlayersTeam(this.getScoreboardName());
    }

    public boolean isAlliedTo(Entity entity) {
        return this.isAlliedTo(entity.getTeam());
    }

    public boolean isAlliedTo(Team team) {
        if (this.getTeam() != null) {
            return this.getTeam().isAlliedTo(team);
        }
        return false;
    }

    public void setInvisible(boolean bl) {
        this.setSharedFlag(5, bl);
    }

    protected boolean getSharedFlag(int n) {
        return (this.entityData.get(DATA_SHARED_FLAGS_ID) & 1 << n) != 0;
    }

    protected void setSharedFlag(int n, boolean bl) {
        byte by = this.entityData.get(DATA_SHARED_FLAGS_ID);
        if (bl) {
            this.entityData.set(DATA_SHARED_FLAGS_ID, (byte)(by | 1 << n));
        } else {
            this.entityData.set(DATA_SHARED_FLAGS_ID, (byte)(by & ~(1 << n)));
        }
    }

    public int getMaxAirSupply() {
        return 300;
    }

    public int getAirSupply() {
        return this.entityData.get(DATA_AIR_SUPPLY_ID);
    }

    public void setAirSupply(int n) {
        this.entityData.set(DATA_AIR_SUPPLY_ID, n);
    }

    public void thunderHit(ServerLevel serverLevel, LightningBolt lightningBolt) {
        this.setRemainingFireTicks(this.remainingFireTicks + 1);
        if (this.remainingFireTicks == 0) {
            this.setSecondsOnFire(8);
        }
        this.hurt(DamageSource.LIGHTNING_BOLT, 5.0f);
    }

    public void onAboveBubbleCol(boolean bl) {
        Vec3 vec3 = this.getDeltaMovement();
        double d = bl ? Math.max(-0.9, vec3.y - 0.03) : Math.min(1.8, vec3.y + 0.1);
        this.setDeltaMovement(vec3.x, d, vec3.z);
    }

    public void onInsideBubbleColumn(boolean bl) {
        Vec3 vec3 = this.getDeltaMovement();
        double d = bl ? Math.max(-0.3, vec3.y - 0.03) : Math.min(0.7, vec3.y + 0.06);
        this.setDeltaMovement(vec3.x, d, vec3.z);
        this.fallDistance = 0.0f;
    }

    public void killed(ServerLevel serverLevel, LivingEntity livingEntity) {
    }

    protected void moveTowardsClosestSpace(double d, double d2, double d3) {
        BlockPos blockPos = new BlockPos(d, d2, d3);
        Vec3 vec3 = new Vec3(d - (double)blockPos.getX(), d2 - (double)blockPos.getY(), d3 - (double)blockPos.getZ());
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        Direction direction = Direction.UP;
        double d4 = Double.MAX_VALUE;
        for (Direction direction2 : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.UP}) {
            double d5;
            mutableBlockPos.setWithOffset(blockPos, direction2);
            if (this.level.getBlockState(mutableBlockPos).isCollisionShapeFullBlock(this.level, mutableBlockPos)) continue;
            double d6 = vec3.get(direction2.getAxis());
            double d7 = d5 = direction2.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1.0 - d6 : d6;
            if (!(d5 < d4)) continue;
            d4 = d5;
            direction = direction2;
        }
        float f = this.random.nextFloat() * 0.2f + 0.1f;
        float f2 = direction.getAxisDirection().getStep();
        Vec3 vec32 = this.getDeltaMovement().scale(0.75);
        if (direction.getAxis() == Direction.Axis.X) {
            this.setDeltaMovement(f2 * f, vec32.y, vec32.z);
        } else if (direction.getAxis() == Direction.Axis.Y) {
            this.setDeltaMovement(vec32.x, f2 * f, vec32.z);
        } else if (direction.getAxis() == Direction.Axis.Z) {
            this.setDeltaMovement(vec32.x, vec32.y, f2 * f);
        }
    }

    public void makeStuckInBlock(BlockState blockState, Vec3 vec3) {
        this.fallDistance = 0.0f;
        this.stuckSpeedMultiplier = vec3;
    }

    private static Component removeAction(Component component) {
        MutableComponent mutableComponent = component.plainCopy().setStyle(component.getStyle().withClickEvent(null));
        for (Component component2 : component.getSiblings()) {
            mutableComponent.append(Entity.removeAction(component2));
        }
        return mutableComponent;
    }

    @Override
    public Component getName() {
        Component component = this.getCustomName();
        if (component != null) {
            return Entity.removeAction(component);
        }
        return this.getTypeName();
    }

    protected Component getTypeName() {
        return this.type.getDescription();
    }

    public boolean is(Entity entity) {
        return this == entity;
    }

    public float getYHeadRot() {
        return 0.0f;
    }

    public void setYHeadRot(float f) {
    }

    public void setYBodyRot(float f) {
    }

    public boolean isAttackable() {
        return true;
    }

    public boolean skipAttackInteraction(Entity entity) {
        return false;
    }

    public String toString() {
        return String.format(Locale.ROOT, "%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f]", this.getClass().getSimpleName(), this.getName().getString(), this.id, this.level == null ? "~NULL~" : this.level.toString(), this.getX(), this.getY(), this.getZ());
    }

    public boolean isInvulnerableTo(DamageSource damageSource) {
        return this.invulnerable && damageSource != DamageSource.OUT_OF_WORLD && !damageSource.isCreativePlayer();
    }

    public boolean isInvulnerable() {
        return this.invulnerable;
    }

    public void setInvulnerable(boolean bl) {
        this.invulnerable = bl;
    }

    public void copyPosition(Entity entity) {
        this.moveTo(entity.getX(), entity.getY(), entity.getZ(), entity.yRot, entity.xRot);
    }

    public void restoreFrom(Entity entity) {
        CompoundTag compoundTag = entity.saveWithoutId(new CompoundTag());
        compoundTag.remove("Dimension");
        this.load(compoundTag);
        this.portalCooldown = entity.portalCooldown;
        this.portalEntrancePos = entity.portalEntrancePos;
    }

    @Nullable
    public Entity changeDimension(ServerLevel serverLevel) {
        if (!(this.level instanceof ServerLevel) || this.removed) {
            return null;
        }
        this.level.getProfiler().push("changeDimension");
        this.unRide();
        this.level.getProfiler().push("reposition");
        PortalInfo portalInfo = this.findDimensionEntryPoint(serverLevel);
        if (portalInfo == null) {
            return null;
        }
        this.level.getProfiler().popPush("reloading");
        ? obj = this.getType().create(serverLevel);
        if (obj != null) {
            ((Entity)obj).restoreFrom(this);
            ((Entity)obj).moveTo(portalInfo.pos.x, portalInfo.pos.y, portalInfo.pos.z, portalInfo.yRot, ((Entity)obj).xRot);
            ((Entity)obj).setDeltaMovement(portalInfo.speed);
            serverLevel.addFromAnotherDimension((Entity)obj);
            if (serverLevel.dimension() == Level.END) {
                ServerLevel.makeObsidianPlatform(serverLevel);
            }
        }
        this.removeAfterChangingDimensions();
        this.level.getProfiler().pop();
        ((ServerLevel)this.level).resetEmptyTime();
        serverLevel.resetEmptyTime();
        this.level.getProfiler().pop();
        return obj;
    }

    protected void removeAfterChangingDimensions() {
        this.removed = true;
    }

    @Nullable
    protected PortalInfo findDimensionEntryPoint(ServerLevel serverLevel) {
        boolean bl;
        boolean bl2;
        boolean bl3 = this.level.dimension() == Level.END && serverLevel.dimension() == Level.OVERWORLD;
        boolean bl4 = bl2 = serverLevel.dimension() == Level.END;
        if (bl3 || bl2) {
            BlockPos blockPos = bl2 ? ServerLevel.END_SPAWN_POINT : serverLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, serverLevel.getSharedSpawnPos());
            return new PortalInfo(new Vec3((double)blockPos.getX() + 0.5, blockPos.getY(), (double)blockPos.getZ() + 0.5), this.getDeltaMovement(), this.yRot, this.xRot);
        }
        boolean bl5 = bl = serverLevel.dimension() == Level.NETHER;
        if (this.level.dimension() != Level.NETHER && !bl) {
            return null;
        }
        WorldBorder worldBorder = serverLevel.getWorldBorder();
        double d = Math.max(-2.9999872E7, worldBorder.getMinX() + 16.0);
        double d2 = Math.max(-2.9999872E7, worldBorder.getMinZ() + 16.0);
        double d3 = Math.min(2.9999872E7, worldBorder.getMaxX() - 16.0);
        double d4 = Math.min(2.9999872E7, worldBorder.getMaxZ() - 16.0);
        double d5 = DimensionType.getTeleportationScale(this.level.dimensionType(), serverLevel.dimensionType());
        BlockPos blockPos = new BlockPos(Mth.clamp(this.getX() * d5, d, d3), this.getY(), Mth.clamp(this.getZ() * d5, d2, d4));
        return this.getExitPortal(serverLevel, blockPos, bl).map(foundRectangle -> {
            Direction.Axis axis;
            Vec3 vec3;
            BlockState blockState = this.level.getBlockState(this.portalEntrancePos);
            if (blockState.hasProperty(BlockStateProperties.HORIZONTAL_AXIS)) {
                axis = blockState.getValue(BlockStateProperties.HORIZONTAL_AXIS);
                BlockUtil.FoundRectangle foundRectangle2 = BlockUtil.getLargestRectangleAround(this.portalEntrancePos, axis, 21, Direction.Axis.Y, 21, blockPos -> this.level.getBlockState((BlockPos)blockPos) == blockState);
                vec3 = this.getRelativePortalPosition(axis, foundRectangle2);
            } else {
                axis = Direction.Axis.X;
                vec3 = new Vec3(0.5, 0.0, 0.0);
            }
            return PortalShape.createPortalInfo(serverLevel, foundRectangle, axis, vec3, this.getDimensions(this.getPose()), this.getDeltaMovement(), this.yRot, this.xRot);
        }).orElse(null);
    }

    protected Vec3 getRelativePortalPosition(Direction.Axis axis, BlockUtil.FoundRectangle foundRectangle) {
        return PortalShape.getRelativePosition(foundRectangle, axis, this.position(), this.getDimensions(this.getPose()));
    }

    protected Optional<BlockUtil.FoundRectangle> getExitPortal(ServerLevel serverLevel, BlockPos blockPos, boolean bl) {
        return serverLevel.getPortalForcer().findPortalAround(blockPos, bl);
    }

    public boolean canChangeDimensions() {
        return true;
    }

    public float getBlockExplosionResistance(Explosion explosion, BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, FluidState fluidState, float f) {
        return f;
    }

    public boolean shouldBlockExplode(Explosion explosion, BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, float f) {
        return true;
    }

    public int getMaxFallDistance() {
        return 3;
    }

    public boolean isIgnoringBlockTriggers() {
        return false;
    }

    public void fillCrashReportCategory(CrashReportCategory crashReportCategory) {
        crashReportCategory.setDetail("Entity Type", () -> EntityType.getKey(this.getType()) + " (" + this.getClass().getCanonicalName() + ")");
        crashReportCategory.setDetail("Entity ID", this.id);
        crashReportCategory.setDetail("Entity Name", () -> this.getName().getString());
        crashReportCategory.setDetail("Entity's Exact location", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", this.getX(), this.getY(), this.getZ()));
        crashReportCategory.setDetail("Entity's Block location", CrashReportCategory.formatLocation(Mth.floor(this.getX()), Mth.floor(this.getY()), Mth.floor(this.getZ())));
        Vec3 vec3 = this.getDeltaMovement();
        crashReportCategory.setDetail("Entity's Momentum", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", vec3.x, vec3.y, vec3.z));
        crashReportCategory.setDetail("Entity's Passengers", () -> this.getPassengers().toString());
        crashReportCategory.setDetail("Entity's Vehicle", () -> this.getVehicle().toString());
    }

    public boolean displayFireAnimation() {
        return this.isOnFire() && !this.isSpectator();
    }

    public void setUUID(UUID uUID) {
        this.uuid = uUID;
        this.stringUUID = this.uuid.toString();
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public String getStringUUID() {
        return this.stringUUID;
    }

    public String getScoreboardName() {
        return this.stringUUID;
    }

    public boolean isPushedByFluid() {
        return true;
    }

    public static double getViewScale() {
        return viewScale;
    }

    public static void setViewScale(double d) {
        viewScale = d;
    }

    @Override
    public Component getDisplayName() {
        return PlayerTeam.formatNameForTeam(this.getTeam(), this.getName()).withStyle(style -> style.withHoverEvent(this.createHoverEvent()).withInsertion(this.getStringUUID()));
    }

    public void setCustomName(@Nullable Component component) {
        this.entityData.set(DATA_CUSTOM_NAME, Optional.ofNullable(component));
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return this.entityData.get(DATA_CUSTOM_NAME).orElse(null);
    }

    @Override
    public boolean hasCustomName() {
        return this.entityData.get(DATA_CUSTOM_NAME).isPresent();
    }

    public void setCustomNameVisible(boolean bl) {
        this.entityData.set(DATA_CUSTOM_NAME_VISIBLE, bl);
    }

    public boolean isCustomNameVisible() {
        return this.entityData.get(DATA_CUSTOM_NAME_VISIBLE);
    }

    public final void teleportToWithTicket(double d, double d2, double d3) {
        if (!(this.level instanceof ServerLevel)) {
            return;
        }
        ChunkPos chunkPos = new ChunkPos(new BlockPos(d, d2, d3));
        ((ServerLevel)this.level).getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkPos, 0, this.getId());
        this.level.getChunk(chunkPos.x, chunkPos.z);
        this.teleportTo(d, d2, d3);
    }

    public void teleportTo(double d, double d2, double d3) {
        if (!(this.level instanceof ServerLevel)) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel)this.level;
        this.moveTo(d, d2, d3, this.yRot, this.xRot);
        this.getSelfAndPassengers().forEach(entity -> {
            serverLevel.updateChunkPos((Entity)entity);
            entity.forceChunkAddition = true;
            for (Entity entity2 : entity.passengers) {
                entity.positionRider(entity2, Entity::moveTo);
            }
        });
    }

    public boolean shouldShowName() {
        return this.isCustomNameVisible();
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
        if (DATA_POSE.equals(entityDataAccessor)) {
            this.refreshDimensions();
        }
    }

    public void refreshDimensions() {
        EntityDimensions entityDimensions;
        EntityDimensions entityDimensions2 = this.dimensions;
        Pose pose = this.getPose();
        this.dimensions = entityDimensions = this.getDimensions(pose);
        this.eyeHeight = this.getEyeHeight(pose, entityDimensions);
        if (entityDimensions.width < entityDimensions2.width) {
            double d = (double)entityDimensions.width / 2.0;
            this.setBoundingBox(new AABB(this.getX() - d, this.getY(), this.getZ() - d, this.getX() + d, this.getY() + (double)entityDimensions.height, this.getZ() + d));
            return;
        }
        AABB aABB = this.getBoundingBox();
        this.setBoundingBox(new AABB(aABB.minX, aABB.minY, aABB.minZ, aABB.minX + (double)entityDimensions.width, aABB.minY + (double)entityDimensions.height, aABB.minZ + (double)entityDimensions.width));
        if (entityDimensions.width > entityDimensions2.width && !this.firstTick && !this.level.isClientSide) {
            float f = entityDimensions2.width - entityDimensions.width;
            this.move(MoverType.SELF, new Vec3(f, 0.0, f));
        }
    }

    public Direction getDirection() {
        return Direction.fromYRot(this.yRot);
    }

    public Direction getMotionDirection() {
        return this.getDirection();
    }

    protected HoverEvent createHoverEvent() {
        return new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new HoverEvent.EntityTooltipInfo(this.getType(), this.getUUID(), this.getName()));
    }

    public boolean broadcastToPlayer(ServerPlayer serverPlayer) {
        return true;
    }

    public AABB getBoundingBox() {
        return this.bb;
    }

    public AABB getBoundingBoxForCulling() {
        return this.getBoundingBox();
    }

    protected AABB getBoundingBoxForPose(Pose pose) {
        EntityDimensions entityDimensions = this.getDimensions(pose);
        float f = entityDimensions.width / 2.0f;
        Vec3 vec3 = new Vec3(this.getX() - (double)f, this.getY(), this.getZ() - (double)f);
        Vec3 vec32 = new Vec3(this.getX() + (double)f, this.getY() + (double)entityDimensions.height, this.getZ() + (double)f);
        return new AABB(vec3, vec32);
    }

    public void setBoundingBox(AABB aABB) {
        this.bb = aABB;
    }

    protected float getEyeHeight(Pose pose, EntityDimensions entityDimensions) {
        return entityDimensions.height * 0.85f;
    }

    public float getEyeHeight(Pose pose) {
        return this.getEyeHeight(pose, this.getDimensions(pose));
    }

    public final float getEyeHeight() {
        return this.eyeHeight;
    }

    public Vec3 getLeashOffset() {
        return new Vec3(0.0, this.getEyeHeight(), this.getBbWidth() * 0.4f);
    }

    public boolean setSlot(int n, ItemStack itemStack) {
        return false;
    }

    @Override
    public void sendMessage(Component component, UUID uUID) {
    }

    public Level getCommandSenderWorld() {
        return this.level;
    }

    @Nullable
    public MinecraftServer getServer() {
        return this.level.getServer();
    }

    public InteractionResult interactAt(Player player, Vec3 vec3, InteractionHand interactionHand) {
        return InteractionResult.PASS;
    }

    public boolean ignoreExplosion() {
        return false;
    }

    public void doEnchantDamageEffects(LivingEntity livingEntity, Entity entity) {
        if (entity instanceof LivingEntity) {
            EnchantmentHelper.doPostHurtEffects((LivingEntity)entity, livingEntity);
        }
        EnchantmentHelper.doPostDamageEffects(livingEntity, entity);
    }

    public void startSeenByPlayer(ServerPlayer serverPlayer) {
    }

    public void stopSeenByPlayer(ServerPlayer serverPlayer) {
    }

    public float rotate(Rotation rotation) {
        float f = Mth.wrapDegrees(this.yRot);
        switch (rotation) {
            case CLOCKWISE_180: {
                return f + 180.0f;
            }
            case COUNTERCLOCKWISE_90: {
                return f + 270.0f;
            }
            case CLOCKWISE_90: {
                return f + 90.0f;
            }
        }
        return f;
    }

    public float mirror(Mirror mirror) {
        float f = Mth.wrapDegrees(this.yRot);
        switch (mirror) {
            case LEFT_RIGHT: {
                return -f;
            }
            case FRONT_BACK: {
                return 180.0f - f;
            }
        }
        return f;
    }

    public boolean onlyOpCanSetNbt() {
        return false;
    }

    public boolean checkAndResetForcedChunkAdditionFlag() {
        boolean bl = this.forceChunkAddition;
        this.forceChunkAddition = false;
        return bl;
    }

    public boolean checkAndResetUpdateChunkPos() {
        boolean bl = this.movedSinceLastChunkCheck;
        this.movedSinceLastChunkCheck = false;
        return bl;
    }

    @Nullable
    public Entity getControllingPassenger() {
        return null;
    }

    public List<Entity> getPassengers() {
        if (this.passengers.isEmpty()) {
            return Collections.emptyList();
        }
        return Lists.newArrayList(this.passengers);
    }

    public boolean hasPassenger(Entity entity) {
        for (Entity entity2 : this.getPassengers()) {
            if (!entity2.equals(entity)) continue;
            return true;
        }
        return false;
    }

    public boolean hasPassenger(Class<? extends Entity> class_) {
        for (Entity entity : this.getPassengers()) {
            if (!class_.isAssignableFrom(entity.getClass())) continue;
            return true;
        }
        return false;
    }

    public Collection<Entity> getIndirectPassengers() {
        HashSet hashSet = Sets.newHashSet();
        for (Entity entity : this.getPassengers()) {
            hashSet.add(entity);
            entity.fillIndirectPassengers(false, hashSet);
        }
        return hashSet;
    }

    public Stream<Entity> getSelfAndPassengers() {
        return Stream.concat(Stream.of(this), this.passengers.stream().flatMap(Entity::getSelfAndPassengers));
    }

    public boolean hasOnePlayerPassenger() {
        HashSet hashSet = Sets.newHashSet();
        this.fillIndirectPassengers(true, hashSet);
        return hashSet.size() == 1;
    }

    private void fillIndirectPassengers(boolean bl, Set<Entity> set) {
        for (Entity entity : this.getPassengers()) {
            if (!bl || ServerPlayer.class.isAssignableFrom(entity.getClass())) {
                set.add(entity);
            }
            entity.fillIndirectPassengers(bl, set);
        }
    }

    public Entity getRootVehicle() {
        Entity entity = this;
        while (entity.isPassenger()) {
            entity = entity.getVehicle();
        }
        return entity;
    }

    public boolean isPassengerOfSameVehicle(Entity entity) {
        return this.getRootVehicle() == entity.getRootVehicle();
    }

    public boolean hasIndirectPassenger(Entity entity) {
        for (Entity entity2 : this.getPassengers()) {
            if (entity2.equals(entity)) {
                return true;
            }
            if (!entity2.hasIndirectPassenger(entity)) continue;
            return true;
        }
        return false;
    }

    public boolean isControlledByLocalInstance() {
        Entity entity = this.getControllingPassenger();
        if (entity instanceof Player) {
            return ((Player)entity).isLocalPlayer();
        }
        return !this.level.isClientSide;
    }

    protected static Vec3 getCollisionHorizontalEscapeVector(double d, double d2, float f) {
        double d3 = (d + d2 + 9.999999747378752E-6) / 2.0;
        float f2 = -Mth.sin(f * 0.017453292f);
        float f3 = Mth.cos(f * 0.017453292f);
        float f4 = Math.max(Math.abs(f2), Math.abs(f3));
        return new Vec3((double)f2 * d3 / (double)f4, 0.0, (double)f3 * d3 / (double)f4);
    }

    public Vec3 getDismountLocationForPassenger(LivingEntity livingEntity) {
        return new Vec3(this.getX(), this.getBoundingBox().maxY, this.getZ());
    }

    @Nullable
    public Entity getVehicle() {
        return this.vehicle;
    }

    public PushReaction getPistonPushReaction() {
        return PushReaction.NORMAL;
    }

    public SoundSource getSoundSource() {
        return SoundSource.NEUTRAL;
    }

    protected int getFireImmuneTicks() {
        return 1;
    }

    public CommandSourceStack createCommandSourceStack() {
        return new CommandSourceStack(this, this.position(), this.getRotationVector(), this.level instanceof ServerLevel ? (ServerLevel)this.level : null, this.getPermissionLevel(), this.getName().getString(), this.getDisplayName(), this.level.getServer(), this);
    }

    protected int getPermissionLevel() {
        return 0;
    }

    public boolean hasPermissions(int n) {
        return this.getPermissionLevel() >= n;
    }

    @Override
    public boolean acceptsSuccess() {
        return this.level.getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK);
    }

    @Override
    public boolean acceptsFailure() {
        return true;
    }

    @Override
    public boolean shouldInformAdmins() {
        return true;
    }

    public void lookAt(EntityAnchorArgument.Anchor anchor, Vec3 vec3) {
        Vec3 vec32 = anchor.apply(this);
        double d = vec3.x - vec32.x;
        double d2 = vec3.y - vec32.y;
        double d3 = vec3.z - vec32.z;
        double d4 = Mth.sqrt(d * d + d3 * d3);
        this.xRot = Mth.wrapDegrees((float)(-(Mth.atan2(d2, d4) * 57.2957763671875)));
        this.yRot = Mth.wrapDegrees((float)(Mth.atan2(d3, d) * 57.2957763671875) - 90.0f);
        this.setYHeadRot(this.yRot);
        this.xRotO = this.xRot;
        this.yRotO = this.yRot;
    }

    public boolean updateFluidHeightAndDoFluidPushing(Tag<Fluid> tag, double d) {
        int n;
        AABB aABB = this.getBoundingBox().deflate(0.001);
        int n2 = Mth.floor(aABB.minX);
        int n3 = Mth.ceil(aABB.maxX);
        int n4 = Mth.floor(aABB.minY);
        int n5 = Mth.ceil(aABB.maxY);
        int n6 = Mth.floor(aABB.minZ);
        if (!this.level.hasChunksAt(n2, n4, n6, n3, n5, n = Mth.ceil(aABB.maxZ))) {
            return false;
        }
        double d2 = 0.0;
        boolean bl = this.isPushedByFluid();
        boolean bl2 = false;
        Vec3 vec3 = Vec3.ZERO;
        int n7 = 0;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int i = n2; i < n3; ++i) {
            for (int j = n4; j < n5; ++j) {
                for (int k = n6; k < n; ++k) {
                    double d3;
                    mutableBlockPos.set(i, j, k);
                    FluidState fluidState = this.level.getFluidState(mutableBlockPos);
                    if (!fluidState.is(tag) || !((d3 = (double)((float)j + fluidState.getHeight(this.level, mutableBlockPos))) >= aABB.minY)) continue;
                    bl2 = true;
                    d2 = Math.max(d3 - aABB.minY, d2);
                    if (!bl) continue;
                    Vec3 vec32 = fluidState.getFlow(this.level, mutableBlockPos);
                    if (d2 < 0.4) {
                        vec32 = vec32.scale(d2);
                    }
                    vec3 = vec3.add(vec32);
                    ++n7;
                }
            }
        }
        if (vec3.length() > 0.0) {
            if (n7 > 0) {
                vec3 = vec3.scale(1.0 / (double)n7);
            }
            if (!(this instanceof Player)) {
                vec3 = vec3.normalize();
            }
            Vec3 vec33 = this.getDeltaMovement();
            vec3 = vec3.scale(d * 1.0);
            double d4 = 0.003;
            if (Math.abs(vec33.x) < 0.003 && Math.abs(vec33.z) < 0.003 && vec3.length() < 0.0045000000000000005) {
                vec3 = vec3.normalize().scale(0.0045000000000000005);
            }
            this.setDeltaMovement(this.getDeltaMovement().add(vec3));
        }
        this.fluidHeight.put(tag, d2);
        return bl2;
    }

    public double getFluidHeight(Tag<Fluid> tag) {
        return this.fluidHeight.getDouble(tag);
    }

    public double getFluidJumpThreshold() {
        return (double)this.getEyeHeight() < 0.4 ? 0.0 : 0.4;
    }

    public final float getBbWidth() {
        return this.dimensions.width;
    }

    public final float getBbHeight() {
        return this.dimensions.height;
    }

    public abstract Packet<?> getAddEntityPacket();

    public EntityDimensions getDimensions(Pose pose) {
        return this.type.getDimensions();
    }

    public Vec3 position() {
        return this.position;
    }

    public BlockPos blockPosition() {
        return this.blockPosition;
    }

    public Vec3 getDeltaMovement() {
        return this.deltaMovement;
    }

    public void setDeltaMovement(Vec3 vec3) {
        this.deltaMovement = vec3;
    }

    public void setDeltaMovement(double d, double d2, double d3) {
        this.setDeltaMovement(new Vec3(d, d2, d3));
    }

    public final double getX() {
        return this.position.x;
    }

    public double getX(double d) {
        return this.position.x + (double)this.getBbWidth() * d;
    }

    public double getRandomX(double d) {
        return this.getX((2.0 * this.random.nextDouble() - 1.0) * d);
    }

    public final double getY() {
        return this.position.y;
    }

    public double getY(double d) {
        return this.position.y + (double)this.getBbHeight() * d;
    }

    public double getRandomY() {
        return this.getY(this.random.nextDouble());
    }

    public double getEyeY() {
        return this.position.y + (double)this.eyeHeight;
    }

    public final double getZ() {
        return this.position.z;
    }

    public double getZ(double d) {
        return this.position.z + (double)this.getBbWidth() * d;
    }

    public double getRandomZ(double d) {
        return this.getZ((2.0 * this.random.nextDouble() - 1.0) * d);
    }

    public void setPosRaw(double d, double d2, double d3) {
        if (this.position.x != d || this.position.y != d2 || this.position.z != d3) {
            this.position = new Vec3(d, d2, d3);
            int n = Mth.floor(d);
            int n2 = Mth.floor(d2);
            int n3 = Mth.floor(d3);
            if (n != this.blockPosition.getX() || n2 != this.blockPosition.getY() || n3 != this.blockPosition.getZ()) {
                this.blockPosition = new BlockPos(n, n2, n3);
            }
            this.movedSinceLastChunkCheck = true;
        }
    }

    public void checkDespawn() {
    }

    public Vec3 getRopeHoldPosition(float f) {
        return this.getPosition(f).add(0.0, (double)this.eyeHeight * 0.7, 0.0);
    }

    @FunctionalInterface
    public static interface MoveFunction {
        public void accept(Entity var1, double var2, double var4, double var6);
    }

}

