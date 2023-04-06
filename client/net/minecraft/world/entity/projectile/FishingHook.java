/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.projectile;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.FishingRodHookedTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FishingHook
extends Projectile {
    private final Random syncronizedRandom = new Random();
    private boolean biting;
    private int outOfWaterTime;
    private static final EntityDataAccessor<Integer> DATA_HOOKED_ENTITY = SynchedEntityData.defineId(FishingHook.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_BITING = SynchedEntityData.defineId(FishingHook.class, EntityDataSerializers.BOOLEAN);
    private int life;
    private int nibble;
    private int timeUntilLured;
    private int timeUntilHooked;
    private float fishAngle;
    private boolean openWater = true;
    private Entity hookedIn;
    private FishHookState currentState = FishHookState.FLYING;
    private final int luck;
    private final int lureSpeed;

    private FishingHook(Level level, Player player, int n, int n2) {
        super(EntityType.FISHING_BOBBER, level);
        this.noCulling = true;
        this.setOwner(player);
        player.fishing = this;
        this.luck = Math.max(0, n);
        this.lureSpeed = Math.max(0, n2);
    }

    public FishingHook(Level level, Player player, double d, double d2, double d3) {
        this(level, player, 0, 0);
        this.setPos(d, d2, d3);
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();
    }

    public FishingHook(Player player, Level level, int n, int n2) {
        this(level, player, n, n2);
        float f = player.xRot;
        float f2 = player.yRot;
        float f3 = Mth.cos(-f2 * 0.017453292f - 3.1415927f);
        float f4 = Mth.sin(-f2 * 0.017453292f - 3.1415927f);
        float f5 = -Mth.cos(-f * 0.017453292f);
        float f6 = Mth.sin(-f * 0.017453292f);
        double d = player.getX() - (double)f4 * 0.3;
        double d2 = player.getEyeY();
        double d3 = player.getZ() - (double)f3 * 0.3;
        this.moveTo(d, d2, d3, f2, f);
        Vec3 vec3 = new Vec3(-f4, Mth.clamp(-(f6 / f5), -5.0f, 5.0f), -f3);
        double d4 = vec3.length();
        vec3 = vec3.multiply(0.6 / d4 + 0.5 + this.random.nextGaussian() * 0.0045, 0.6 / d4 + 0.5 + this.random.nextGaussian() * 0.0045, 0.6 / d4 + 0.5 + this.random.nextGaussian() * 0.0045);
        this.setDeltaMovement(vec3);
        this.yRot = (float)(Mth.atan2(vec3.x, vec3.z) * 57.2957763671875);
        this.xRot = (float)(Mth.atan2(vec3.y, Mth.sqrt(FishingHook.getHorizontalDistanceSqr(vec3))) * 57.2957763671875);
        this.yRotO = this.yRot;
        this.xRotO = this.xRot;
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(DATA_HOOKED_ENTITY, 0);
        this.getEntityData().define(DATA_BITING, false);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
        if (DATA_HOOKED_ENTITY.equals(entityDataAccessor)) {
            int n = this.getEntityData().get(DATA_HOOKED_ENTITY);
            Entity entity = this.hookedIn = n > 0 ? this.level.getEntity(n - 1) : null;
        }
        if (DATA_BITING.equals(entityDataAccessor)) {
            this.biting = this.getEntityData().get(DATA_BITING);
            if (this.biting) {
                this.setDeltaMovement(this.getDeltaMovement().x, -0.4f * Mth.nextFloat(this.syncronizedRandom, 0.6f, 1.0f), this.getDeltaMovement().z);
            }
        }
        super.onSyncedDataUpdated(entityDataAccessor);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double d) {
        double d2 = 64.0;
        return d < 4096.0;
    }

    @Override
    public void lerpTo(double d, double d2, double d3, float f, float f2, int n, boolean bl) {
    }

    @Override
    public void tick() {
        boolean bl;
        this.syncronizedRandom.setSeed(this.getUUID().getLeastSignificantBits() ^ this.level.getGameTime());
        super.tick();
        Player player = this.getPlayerOwner();
        if (player == null) {
            this.remove();
            return;
        }
        if (!this.level.isClientSide && this.shouldStopFishing(player)) {
            return;
        }
        if (this.onGround) {
            ++this.life;
            if (this.life >= 1200) {
                this.remove();
                return;
            }
        } else {
            this.life = 0;
        }
        float f = 0.0f;
        BlockPos blockPos = this.blockPosition();
        FluidState fluidState = this.level.getFluidState(blockPos);
        if (fluidState.is(FluidTags.WATER)) {
            f = fluidState.getHeight(this.level, blockPos);
        }
        boolean bl2 = bl = f > 0.0f;
        if (this.currentState == FishHookState.FLYING) {
            if (this.hookedIn != null) {
                this.setDeltaMovement(Vec3.ZERO);
                this.currentState = FishHookState.HOOKED_IN_ENTITY;
                return;
            }
            if (bl) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.3, 0.2, 0.3));
                this.currentState = FishHookState.BOBBING;
                return;
            }
            this.checkCollision();
        } else {
            if (this.currentState == FishHookState.HOOKED_IN_ENTITY) {
                if (this.hookedIn != null) {
                    if (this.hookedIn.removed) {
                        this.hookedIn = null;
                        this.currentState = FishHookState.FLYING;
                    } else {
                        this.setPos(this.hookedIn.getX(), this.hookedIn.getY(0.8), this.hookedIn.getZ());
                    }
                }
                return;
            }
            if (this.currentState == FishHookState.BOBBING) {
                Vec3 vec3 = this.getDeltaMovement();
                double d = this.getY() + vec3.y - (double)blockPos.getY() - (double)f;
                if (Math.abs(d) < 0.01) {
                    d += Math.signum(d) * 0.1;
                }
                this.setDeltaMovement(vec3.x * 0.9, vec3.y - d * (double)this.random.nextFloat() * 0.2, vec3.z * 0.9);
                this.openWater = this.nibble > 0 || this.timeUntilHooked > 0 ? this.openWater && this.outOfWaterTime < 10 && this.calculateOpenWater(blockPos) : true;
                if (bl) {
                    this.outOfWaterTime = Math.max(0, this.outOfWaterTime - 1);
                    if (this.biting) {
                        this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.1 * (double)this.syncronizedRandom.nextFloat() * (double)this.syncronizedRandom.nextFloat(), 0.0));
                    }
                    if (!this.level.isClientSide) {
                        this.catchingFish(blockPos);
                    }
                } else {
                    this.outOfWaterTime = Math.min(10, this.outOfWaterTime + 1);
                }
            }
        }
        if (!fluidState.is(FluidTags.WATER)) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.03, 0.0));
        }
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.updateRotation();
        if (this.currentState == FishHookState.FLYING && (this.onGround || this.horizontalCollision)) {
            this.setDeltaMovement(Vec3.ZERO);
        }
        double d = 0.92;
        this.setDeltaMovement(this.getDeltaMovement().scale(0.92));
        this.reapplyPosition();
    }

    private boolean shouldStopFishing(Player player) {
        boolean bl;
        ItemStack itemStack = player.getMainHandItem();
        ItemStack itemStack2 = player.getOffhandItem();
        boolean bl2 = itemStack.getItem() == Items.FISHING_ROD;
        boolean bl3 = bl = itemStack2.getItem() == Items.FISHING_ROD;
        if (player.removed || !player.isAlive() || !bl2 && !bl || this.distanceToSqr(player) > 1024.0) {
            this.remove();
            return true;
        }
        return false;
    }

    private void checkCollision() {
        HitResult hitResult = ProjectileUtil.getHitResult(this, this::canHitEntity);
        this.onHit(hitResult);
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return super.canHitEntity(entity) || entity.isAlive() && entity instanceof ItemEntity;
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        if (!this.level.isClientSide) {
            this.hookedIn = entityHitResult.getEntity();
            this.setHookedEntity();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        this.setDeltaMovement(this.getDeltaMovement().normalize().scale(blockHitResult.distanceTo(this)));
    }

    private void setHookedEntity() {
        this.getEntityData().set(DATA_HOOKED_ENTITY, this.hookedIn.getId() + 1);
    }

    private void catchingFish(BlockPos blockPos) {
        ServerLevel serverLevel = (ServerLevel)this.level;
        int n = 1;
        BlockPos blockPos2 = blockPos.above();
        if (this.random.nextFloat() < 0.25f && this.level.isRainingAt(blockPos2)) {
            ++n;
        }
        if (this.random.nextFloat() < 0.5f && !this.level.canSeeSky(blockPos2)) {
            --n;
        }
        if (this.nibble > 0) {
            --this.nibble;
            if (this.nibble <= 0) {
                this.timeUntilLured = 0;
                this.timeUntilHooked = 0;
                this.getEntityData().set(DATA_BITING, false);
            }
        } else if (this.timeUntilHooked > 0) {
            this.timeUntilHooked -= n;
            if (this.timeUntilHooked > 0) {
                double d;
                double d2;
                this.fishAngle = (float)((double)this.fishAngle + this.random.nextGaussian() * 4.0);
                float f = this.fishAngle * 0.017453292f;
                float f2 = Mth.sin(f);
                float f3 = Mth.cos(f);
                double d3 = this.getX() + (double)(f2 * (float)this.timeUntilHooked * 0.1f);
                BlockState blockState = serverLevel.getBlockState(new BlockPos(d3, (d = (double)((float)Mth.floor(this.getY()) + 1.0f)) - 1.0, d2 = this.getZ() + (double)(f3 * (float)this.timeUntilHooked * 0.1f)));
                if (blockState.is(Blocks.WATER)) {
                    if (this.random.nextFloat() < 0.15f) {
                        serverLevel.sendParticles(ParticleTypes.BUBBLE, d3, d - 0.10000000149011612, d2, 1, f2, 0.1, f3, 0.0);
                    }
                    float f4 = f2 * 0.04f;
                    float f5 = f3 * 0.04f;
                    serverLevel.sendParticles(ParticleTypes.FISHING, d3, d, d2, 0, f5, 0.01, -f4, 1.0);
                    serverLevel.sendParticles(ParticleTypes.FISHING, d3, d, d2, 0, -f5, 0.01, f4, 1.0);
                }
            } else {
                this.playSound(SoundEvents.FISHING_BOBBER_SPLASH, 0.25f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.4f);
                double d = this.getY() + 0.5;
                serverLevel.sendParticles(ParticleTypes.BUBBLE, this.getX(), d, this.getZ(), (int)(1.0f + this.getBbWidth() * 20.0f), this.getBbWidth(), 0.0, this.getBbWidth(), 0.20000000298023224);
                serverLevel.sendParticles(ParticleTypes.FISHING, this.getX(), d, this.getZ(), (int)(1.0f + this.getBbWidth() * 20.0f), this.getBbWidth(), 0.0, this.getBbWidth(), 0.20000000298023224);
                this.nibble = Mth.nextInt(this.random, 20, 40);
                this.getEntityData().set(DATA_BITING, true);
            }
        } else if (this.timeUntilLured > 0) {
            this.timeUntilLured -= n;
            float f = 0.15f;
            if (this.timeUntilLured < 20) {
                f = (float)((double)f + (double)(20 - this.timeUntilLured) * 0.05);
            } else if (this.timeUntilLured < 40) {
                f = (float)((double)f + (double)(40 - this.timeUntilLured) * 0.02);
            } else if (this.timeUntilLured < 60) {
                f = (float)((double)f + (double)(60 - this.timeUntilLured) * 0.01);
            }
            if (this.random.nextFloat() < f) {
                double d;
                double d4;
                float f6 = Mth.nextFloat(this.random, 0.0f, 360.0f) * 0.017453292f;
                float f7 = Mth.nextFloat(this.random, 25.0f, 60.0f);
                double d5 = this.getX() + (double)(Mth.sin(f6) * f7 * 0.1f);
                BlockState blockState = serverLevel.getBlockState(new BlockPos(d5, (d = (double)((float)Mth.floor(this.getY()) + 1.0f)) - 1.0, d4 = this.getZ() + (double)(Mth.cos(f6) * f7 * 0.1f)));
                if (blockState.is(Blocks.WATER)) {
                    serverLevel.sendParticles(ParticleTypes.SPLASH, d5, d, d4, 2 + this.random.nextInt(2), 0.10000000149011612, 0.0, 0.10000000149011612, 0.0);
                }
            }
            if (this.timeUntilLured <= 0) {
                this.fishAngle = Mth.nextFloat(this.random, 0.0f, 360.0f);
                this.timeUntilHooked = Mth.nextInt(this.random, 20, 80);
            }
        } else {
            this.timeUntilLured = Mth.nextInt(this.random, 100, 600);
            this.timeUntilLured -= this.lureSpeed * 20 * 5;
        }
    }

    private boolean calculateOpenWater(BlockPos blockPos) {
        OpenWaterType openWaterType = OpenWaterType.INVALID;
        for (int i = -1; i <= 2; ++i) {
            OpenWaterType openWaterType2 = this.getOpenWaterTypeForArea(blockPos.offset(-2, i, -2), blockPos.offset(2, i, 2));
            switch (openWaterType2) {
                case INVALID: {
                    return false;
                }
                case ABOVE_WATER: {
                    if (openWaterType != OpenWaterType.INVALID) break;
                    return false;
                }
                case INSIDE_WATER: {
                    if (openWaterType != OpenWaterType.ABOVE_WATER) break;
                    return false;
                }
            }
            openWaterType = openWaterType2;
        }
        return true;
    }

    private OpenWaterType getOpenWaterTypeForArea(BlockPos blockPos, BlockPos blockPos2) {
        return BlockPos.betweenClosedStream(blockPos, blockPos2).map(this::getOpenWaterTypeForBlock).reduce((openWaterType, openWaterType2) -> openWaterType == openWaterType2 ? openWaterType : OpenWaterType.INVALID).orElse(OpenWaterType.INVALID);
    }

    private OpenWaterType getOpenWaterTypeForBlock(BlockPos blockPos) {
        BlockState blockState = this.level.getBlockState(blockPos);
        if (blockState.isAir() || blockState.is(Blocks.LILY_PAD)) {
            return OpenWaterType.ABOVE_WATER;
        }
        FluidState fluidState = blockState.getFluidState();
        if (fluidState.is(FluidTags.WATER) && fluidState.isSource() && blockState.getCollisionShape(this.level, blockPos).isEmpty()) {
            return OpenWaterType.INSIDE_WATER;
        }
        return OpenWaterType.INVALID;
    }

    public boolean isOpenWaterFishing() {
        return this.openWater;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
    }

    public int retrieve(ItemStack itemStack) {
        Player player = this.getPlayerOwner();
        if (this.level.isClientSide || player == null) {
            return 0;
        }
        int n = 0;
        if (this.hookedIn != null) {
            this.bringInHookedEntity();
            CriteriaTriggers.FISHING_ROD_HOOKED.trigger((ServerPlayer)player, itemStack, this, Collections.emptyList());
            this.level.broadcastEntityEvent(this, (byte)31);
            n = this.hookedIn instanceof ItemEntity ? 3 : 5;
        } else if (this.nibble > 0) {
            LootContext.Builder builder = new LootContext.Builder((ServerLevel)this.level).withParameter(LootContextParams.ORIGIN, this.position()).withParameter(LootContextParams.TOOL, itemStack).withParameter(LootContextParams.THIS_ENTITY, this).withRandom(this.random).withLuck((float)this.luck + player.getLuck());
            LootTable lootTable = this.level.getServer().getLootTables().get(BuiltInLootTables.FISHING);
            List<ItemStack> list = lootTable.getRandomItems(builder.create(LootContextParamSets.FISHING));
            CriteriaTriggers.FISHING_ROD_HOOKED.trigger((ServerPlayer)player, itemStack, this, list);
            for (ItemStack itemStack2 : list) {
                ItemEntity itemEntity = new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(), itemStack2);
                double d = player.getX() - this.getX();
                double d2 = player.getY() - this.getY();
                double d3 = player.getZ() - this.getZ();
                double d4 = 0.1;
                itemEntity.setDeltaMovement(d * 0.1, d2 * 0.1 + Math.sqrt(Math.sqrt(d * d + d2 * d2 + d3 * d3)) * 0.08, d3 * 0.1);
                this.level.addFreshEntity(itemEntity);
                player.level.addFreshEntity(new ExperienceOrb(player.level, player.getX(), player.getY() + 0.5, player.getZ() + 0.5, this.random.nextInt(6) + 1));
                if (!itemStack2.getItem().is(ItemTags.FISHES)) continue;
                player.awardStat(Stats.FISH_CAUGHT, 1);
            }
            n = 1;
        }
        if (this.onGround) {
            n = 2;
        }
        this.remove();
        return n;
    }

    @Override
    public void handleEntityEvent(byte by) {
        if (by == 31 && this.level.isClientSide && this.hookedIn instanceof Player && ((Player)this.hookedIn).isLocalPlayer()) {
            this.bringInHookedEntity();
        }
        super.handleEntityEvent(by);
    }

    protected void bringInHookedEntity() {
        Entity entity = this.getOwner();
        if (entity == null) {
            return;
        }
        Vec3 vec3 = new Vec3(entity.getX() - this.getX(), entity.getY() - this.getY(), entity.getZ() - this.getZ()).scale(0.1);
        this.hookedIn.setDeltaMovement(this.hookedIn.getDeltaMovement().add(vec3));
    }

    @Override
    protected boolean isMovementNoisy() {
        return false;
    }

    @Override
    public void remove() {
        super.remove();
        Player player = this.getPlayerOwner();
        if (player != null) {
            player.fishing = null;
        }
    }

    @Nullable
    public Player getPlayerOwner() {
        Entity entity = this.getOwner();
        return entity instanceof Player ? (Player)entity : null;
    }

    @Nullable
    public Entity getHookedIn() {
        return this.hookedIn;
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        Entity entity = this.getOwner();
        return new ClientboundAddEntityPacket(this, entity == null ? this.getId() : entity.getId());
    }

    static enum OpenWaterType {
        ABOVE_WATER,
        INSIDE_WATER,
        INVALID;
        
    }

    static enum FishHookState {
        FLYING,
        HOOKED_IN_ENTITY,
        BOBBING;
        
    }

}

