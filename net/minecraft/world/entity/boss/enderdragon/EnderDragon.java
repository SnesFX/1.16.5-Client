/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.entity.boss.enderdragon;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonDeathPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonHoverPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonLandingPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonTakeoffPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhaseManager;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.BinaryHeap;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EnderDragon
extends Mob
implements Enemy {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final EntityDataAccessor<Integer> DATA_PHASE = SynchedEntityData.defineId(EnderDragon.class, EntityDataSerializers.INT);
    private static final TargetingConditions CRYSTAL_DESTROY_TARGETING = new TargetingConditions().range(64.0);
    public final double[][] positions = new double[64][3];
    public int posPointer = -1;
    private final EnderDragonPart[] subEntities;
    public final EnderDragonPart head;
    private final EnderDragonPart neck;
    private final EnderDragonPart body;
    private final EnderDragonPart tail1;
    private final EnderDragonPart tail2;
    private final EnderDragonPart tail3;
    private final EnderDragonPart wing1;
    private final EnderDragonPart wing2;
    public float oFlapTime;
    public float flapTime;
    public boolean inWall;
    public int dragonDeathTime;
    public float yRotA;
    @Nullable
    public EndCrystal nearestCrystal;
    @Nullable
    private final EndDragonFight dragonFight;
    private final EnderDragonPhaseManager phaseManager;
    private int growlTime = 100;
    private int sittingDamageReceived;
    private final Node[] nodes = new Node[24];
    private final int[] nodeAdjacency = new int[24];
    private final BinaryHeap openSet = new BinaryHeap();

    public EnderDragon(EntityType<? extends EnderDragon> entityType, Level level) {
        super(EntityType.ENDER_DRAGON, level);
        this.head = new EnderDragonPart(this, "head", 1.0f, 1.0f);
        this.neck = new EnderDragonPart(this, "neck", 3.0f, 3.0f);
        this.body = new EnderDragonPart(this, "body", 5.0f, 3.0f);
        this.tail1 = new EnderDragonPart(this, "tail", 2.0f, 2.0f);
        this.tail2 = new EnderDragonPart(this, "tail", 2.0f, 2.0f);
        this.tail3 = new EnderDragonPart(this, "tail", 2.0f, 2.0f);
        this.wing1 = new EnderDragonPart(this, "wing", 4.0f, 2.0f);
        this.wing2 = new EnderDragonPart(this, "wing", 4.0f, 2.0f);
        this.subEntities = new EnderDragonPart[]{this.head, this.neck, this.body, this.tail1, this.tail2, this.tail3, this.wing1, this.wing2};
        this.setHealth(this.getMaxHealth());
        this.noPhysics = true;
        this.noCulling = true;
        this.dragonFight = level instanceof ServerLevel ? ((ServerLevel)level).dragonFight() : null;
        this.phaseManager = new EnderDragonPhaseManager(this);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 200.0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(DATA_PHASE, EnderDragonPhase.HOVERING.getId());
    }

    public double[] getLatencyPos(int n, float f) {
        if (this.isDeadOrDying()) {
            f = 0.0f;
        }
        f = 1.0f - f;
        int n2 = this.posPointer - n & 0x3F;
        int n3 = this.posPointer - n - 1 & 0x3F;
        double[] arrd = new double[3];
        double d = this.positions[n2][0];
        double d2 = Mth.wrapDegrees(this.positions[n3][0] - d);
        arrd[0] = d + d2 * (double)f;
        d = this.positions[n2][1];
        d2 = this.positions[n3][1] - d;
        arrd[1] = d + d2 * (double)f;
        arrd[2] = Mth.lerp((double)f, this.positions[n2][2], this.positions[n3][2]);
        return arrd;
    }

    @Override
    public void aiStep() {
        float f;
        float f2;
        double d;
        double d2;
        double d3;
        float f3;
        int n;
        float f4;
        if (this.level.isClientSide) {
            this.setHealth(this.getHealth());
            if (!this.isSilent()) {
                f = Mth.cos(this.flapTime * 6.2831855f);
                f4 = Mth.cos(this.oFlapTime * 6.2831855f);
                if (f4 <= -0.3f && f >= -0.3f) {
                    this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENDER_DRAGON_FLAP, this.getSoundSource(), 5.0f, 0.8f + this.random.nextFloat() * 0.3f, false);
                }
                if (!this.phaseManager.getCurrentPhase().isSitting() && --this.growlTime < 0) {
                    this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENDER_DRAGON_GROWL, this.getSoundSource(), 2.5f, 0.8f + this.random.nextFloat() * 0.3f, false);
                    this.growlTime = 200 + this.random.nextInt(200);
                }
            }
        }
        this.oFlapTime = this.flapTime;
        if (this.isDeadOrDying()) {
            f = (this.random.nextFloat() - 0.5f) * 8.0f;
            f4 = (this.random.nextFloat() - 0.5f) * 4.0f;
            float f5 = (this.random.nextFloat() - 0.5f) * 8.0f;
            this.level.addParticle(ParticleTypes.EXPLOSION, this.getX() + (double)f, this.getY() + 2.0 + (double)f4, this.getZ() + (double)f5, 0.0, 0.0, 0.0);
            return;
        }
        this.checkCrystals();
        Vec3 vec3 = this.getDeltaMovement();
        f4 = 0.2f / (Mth.sqrt(EnderDragon.getHorizontalDistanceSqr(vec3)) * 10.0f + 1.0f);
        this.flapTime = this.phaseManager.getCurrentPhase().isSitting() ? (this.flapTime += 0.1f) : (this.inWall ? (this.flapTime += f4 * 0.5f) : (this.flapTime += (f4 *= (float)Math.pow(2.0, vec3.y))));
        this.yRot = Mth.wrapDegrees(this.yRot);
        if (this.isNoAi()) {
            this.flapTime = 0.5f;
            return;
        }
        if (this.posPointer < 0) {
            for (int i = 0; i < this.positions.length; ++i) {
                this.positions[i][0] = this.yRot;
                this.positions[i][1] = this.getY();
            }
        }
        if (++this.posPointer == this.positions.length) {
            this.posPointer = 0;
        }
        this.positions[this.posPointer][0] = this.yRot;
        this.positions[this.posPointer][1] = this.getY();
        if (this.level.isClientSide) {
            if (this.lerpSteps > 0) {
                double d4 = this.getX() + (this.lerpX - this.getX()) / (double)this.lerpSteps;
                d2 = this.getY() + (this.lerpY - this.getY()) / (double)this.lerpSteps;
                d = this.getZ() + (this.lerpZ - this.getZ()) / (double)this.lerpSteps;
                d3 = Mth.wrapDegrees(this.lerpYRot - (double)this.yRot);
                this.yRot = (float)((double)this.yRot + d3 / (double)this.lerpSteps);
                this.xRot = (float)((double)this.xRot + (this.lerpXRot - (double)this.xRot) / (double)this.lerpSteps);
                --this.lerpSteps;
                this.setPos(d4, d2, d);
                this.setRot(this.yRot, this.xRot);
            }
            this.phaseManager.getCurrentPhase().doClientTick();
        } else {
            Vec3 vec32;
            DragonPhaseInstance dragonPhaseInstance = this.phaseManager.getCurrentPhase();
            dragonPhaseInstance.doServerTick();
            if (this.phaseManager.getCurrentPhase() != dragonPhaseInstance) {
                dragonPhaseInstance = this.phaseManager.getCurrentPhase();
                dragonPhaseInstance.doServerTick();
            }
            if ((vec32 = dragonPhaseInstance.getFlyTargetLocation()) != null) {
                d2 = vec32.x - this.getX();
                d = vec32.y - this.getY();
                d3 = vec32.z - this.getZ();
                double d5 = d2 * d2 + d * d + d3 * d3;
                float f6 = dragonPhaseInstance.getFlySpeed();
                double d6 = Mth.sqrt(d2 * d2 + d3 * d3);
                if (d6 > 0.0) {
                    d = Mth.clamp(d / d6, (double)(-f6), (double)f6);
                }
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, d * 0.01, 0.0));
                this.yRot = Mth.wrapDegrees(this.yRot);
                double d7 = Mth.clamp(Mth.wrapDegrees(180.0 - Mth.atan2(d2, d3) * 57.2957763671875 - (double)this.yRot), -50.0, 50.0);
                Vec3 vec33 = vec32.subtract(this.getX(), this.getY(), this.getZ()).normalize();
                Vec3 vec34 = new Vec3(Mth.sin(this.yRot * 0.017453292f), this.getDeltaMovement().y, -Mth.cos(this.yRot * 0.017453292f)).normalize();
                f2 = Math.max(((float)vec34.dot(vec33) + 0.5f) / 1.5f, 0.0f);
                this.yRotA *= 0.8f;
                this.yRotA = (float)((double)this.yRotA + d7 * (double)dragonPhaseInstance.getTurnSpeed());
                this.yRot += this.yRotA * 0.1f;
                f3 = (float)(2.0 / (d5 + 1.0));
                float f7 = 0.06f;
                this.moveRelative(0.06f * (f2 * f3 + (1.0f - f3)), new Vec3(0.0, 0.0, -1.0));
                if (this.inWall) {
                    this.move(MoverType.SELF, this.getDeltaMovement().scale(0.800000011920929));
                } else {
                    this.move(MoverType.SELF, this.getDeltaMovement());
                }
                Vec3 vec35 = this.getDeltaMovement().normalize();
                double d8 = 0.8 + 0.15 * (vec35.dot(vec34) + 1.0) / 2.0;
                this.setDeltaMovement(this.getDeltaMovement().multiply(d8, 0.9100000262260437, d8));
            }
        }
        this.yBodyRot = this.yRot;
        Vec3[] arrvec3 = new Vec3[this.subEntities.length];
        for (int i = 0; i < this.subEntities.length; ++i) {
            arrvec3[i] = new Vec3(this.subEntities[i].getX(), this.subEntities[i].getY(), this.subEntities[i].getZ());
        }
        float f8 = (float)(this.getLatencyPos(5, 1.0f)[1] - this.getLatencyPos(10, 1.0f)[1]) * 10.0f * 0.017453292f;
        float f9 = Mth.cos(f8);
        float f10 = Mth.sin(f8);
        float f11 = this.yRot * 0.017453292f;
        float f12 = Mth.sin(f11);
        float f13 = Mth.cos(f11);
        this.tickPart(this.body, f12 * 0.5f, 0.0, -f13 * 0.5f);
        this.tickPart(this.wing1, f13 * 4.5f, 2.0, f12 * 4.5f);
        this.tickPart(this.wing2, f13 * -4.5f, 2.0, f12 * -4.5f);
        if (!this.level.isClientSide && this.hurtTime == 0) {
            this.knockBack(this.level.getEntities(this, this.wing1.getBoundingBox().inflate(4.0, 2.0, 4.0).move(0.0, -2.0, 0.0), EntitySelector.NO_CREATIVE_OR_SPECTATOR));
            this.knockBack(this.level.getEntities(this, this.wing2.getBoundingBox().inflate(4.0, 2.0, 4.0).move(0.0, -2.0, 0.0), EntitySelector.NO_CREATIVE_OR_SPECTATOR));
            this.hurt(this.level.getEntities(this, this.head.getBoundingBox().inflate(1.0), EntitySelector.NO_CREATIVE_OR_SPECTATOR));
            this.hurt(this.level.getEntities(this, this.neck.getBoundingBox().inflate(1.0), EntitySelector.NO_CREATIVE_OR_SPECTATOR));
        }
        float f14 = Mth.sin(this.yRot * 0.017453292f - this.yRotA * 0.01f);
        float f15 = Mth.cos(this.yRot * 0.017453292f - this.yRotA * 0.01f);
        float f16 = this.getHeadYOffset();
        this.tickPart(this.head, f14 * 6.5f * f9, f16 + f10 * 6.5f, -f15 * 6.5f * f9);
        this.tickPart(this.neck, f14 * 5.5f * f9, f16 + f10 * 5.5f, -f15 * 5.5f * f9);
        double[] arrd = this.getLatencyPos(5, 1.0f);
        for (n = 0; n < 3; ++n) {
            EnderDragonPart enderDragonPart = null;
            if (n == 0) {
                enderDragonPart = this.tail1;
            }
            if (n == 1) {
                enderDragonPart = this.tail2;
            }
            if (n == 2) {
                enderDragonPart = this.tail3;
            }
            double[] arrd2 = this.getLatencyPos(12 + n * 2, 1.0f);
            float f17 = this.yRot * 0.017453292f + this.rotWrap(arrd2[0] - arrd[0]) * 0.017453292f;
            float f18 = Mth.sin(f17);
            float f19 = Mth.cos(f17);
            f2 = 1.5f;
            f3 = (float)(n + 1) * 2.0f;
            this.tickPart(enderDragonPart, -(f12 * 1.5f + f18 * f3) * f9, arrd2[1] - arrd[1] - (double)((f3 + 1.5f) * f10) + 1.5, (f13 * 1.5f + f19 * f3) * f9);
        }
        if (!this.level.isClientSide) {
            this.inWall = this.checkWalls(this.head.getBoundingBox()) | this.checkWalls(this.neck.getBoundingBox()) | this.checkWalls(this.body.getBoundingBox());
            if (this.dragonFight != null) {
                this.dragonFight.updateDragon(this);
            }
        }
        for (n = 0; n < this.subEntities.length; ++n) {
            this.subEntities[n].xo = arrvec3[n].x;
            this.subEntities[n].yo = arrvec3[n].y;
            this.subEntities[n].zo = arrvec3[n].z;
            this.subEntities[n].xOld = arrvec3[n].x;
            this.subEntities[n].yOld = arrvec3[n].y;
            this.subEntities[n].zOld = arrvec3[n].z;
        }
    }

    private void tickPart(EnderDragonPart enderDragonPart, double d, double d2, double d3) {
        enderDragonPart.setPos(this.getX() + d, this.getY() + d2, this.getZ() + d3);
    }

    private float getHeadYOffset() {
        if (this.phaseManager.getCurrentPhase().isSitting()) {
            return -1.0f;
        }
        double[] arrd = this.getLatencyPos(5, 1.0f);
        double[] arrd2 = this.getLatencyPos(0, 1.0f);
        return (float)(arrd[1] - arrd2[1]);
    }

    private void checkCrystals() {
        if (this.nearestCrystal != null) {
            if (this.nearestCrystal.removed) {
                this.nearestCrystal = null;
            } else if (this.tickCount % 10 == 0 && this.getHealth() < this.getMaxHealth()) {
                this.setHealth(this.getHealth() + 1.0f);
            }
        }
        if (this.random.nextInt(10) == 0) {
            List<EndCrystal> list = this.level.getEntitiesOfClass(EndCrystal.class, this.getBoundingBox().inflate(32.0));
            EndCrystal endCrystal = null;
            double d = Double.MAX_VALUE;
            for (EndCrystal endCrystal2 : list) {
                double d2 = endCrystal2.distanceToSqr(this);
                if (!(d2 < d)) continue;
                d = d2;
                endCrystal = endCrystal2;
            }
            this.nearestCrystal = endCrystal;
        }
    }

    private void knockBack(List<Entity> list) {
        double d = (this.body.getBoundingBox().minX + this.body.getBoundingBox().maxX) / 2.0;
        double d2 = (this.body.getBoundingBox().minZ + this.body.getBoundingBox().maxZ) / 2.0;
        for (Entity entity : list) {
            if (!(entity instanceof LivingEntity)) continue;
            double d3 = entity.getX() - d;
            double d4 = entity.getZ() - d2;
            double d5 = Math.max(d3 * d3 + d4 * d4, 0.1);
            entity.push(d3 / d5 * 4.0, 0.20000000298023224, d4 / d5 * 4.0);
            if (this.phaseManager.getCurrentPhase().isSitting() || ((LivingEntity)entity).getLastHurtByMobTimestamp() >= entity.tickCount - 2) continue;
            entity.hurt(DamageSource.mobAttack(this), 5.0f);
            this.doEnchantDamageEffects(this, entity);
        }
    }

    private void hurt(List<Entity> list) {
        for (Entity entity : list) {
            if (!(entity instanceof LivingEntity)) continue;
            entity.hurt(DamageSource.mobAttack(this), 10.0f);
            this.doEnchantDamageEffects(this, entity);
        }
    }

    private float rotWrap(double d) {
        return (float)Mth.wrapDegrees(d);
    }

    private boolean checkWalls(AABB aABB) {
        int n = Mth.floor(aABB.minX);
        int n2 = Mth.floor(aABB.minY);
        int n3 = Mth.floor(aABB.minZ);
        int n4 = Mth.floor(aABB.maxX);
        int n5 = Mth.floor(aABB.maxY);
        int n6 = Mth.floor(aABB.maxZ);
        boolean bl = false;
        boolean bl2 = false;
        for (int i = n; i <= n4; ++i) {
            for (int j = n2; j <= n5; ++j) {
                for (int k = n3; k <= n6; ++k) {
                    BlockPos blockPos = new BlockPos(i, j, k);
                    BlockState blockState = this.level.getBlockState(blockPos);
                    Block block = blockState.getBlock();
                    if (blockState.isAir() || blockState.getMaterial() == Material.FIRE) continue;
                    if (!this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) || BlockTags.DRAGON_IMMUNE.contains(block)) {
                        bl = true;
                        continue;
                    }
                    bl2 = this.level.removeBlock(blockPos, false) || bl2;
                }
            }
        }
        if (bl2) {
            BlockPos blockPos = new BlockPos(n + this.random.nextInt(n4 - n + 1), n2 + this.random.nextInt(n5 - n2 + 1), n3 + this.random.nextInt(n6 - n3 + 1));
            this.level.levelEvent(2008, blockPos, 0);
        }
        return bl;
    }

    public boolean hurt(EnderDragonPart enderDragonPart, DamageSource damageSource, float f) {
        if (this.phaseManager.getCurrentPhase().getPhase() == EnderDragonPhase.DYING) {
            return false;
        }
        f = this.phaseManager.getCurrentPhase().onHurt(damageSource, f);
        if (enderDragonPart != this.head) {
            f = f / 4.0f + Math.min(f, 1.0f);
        }
        if (f < 0.01f) {
            return false;
        }
        if (damageSource.getEntity() instanceof Player || damageSource.isExplosion()) {
            float f2 = this.getHealth();
            this.reallyHurt(damageSource, f);
            if (this.isDeadOrDying() && !this.phaseManager.getCurrentPhase().isSitting()) {
                this.setHealth(1.0f);
                this.phaseManager.setPhase(EnderDragonPhase.DYING);
            }
            if (this.phaseManager.getCurrentPhase().isSitting()) {
                this.sittingDamageReceived = (int)((float)this.sittingDamageReceived + (f2 - this.getHealth()));
                if ((float)this.sittingDamageReceived > 0.25f * this.getMaxHealth()) {
                    this.sittingDamageReceived = 0;
                    this.phaseManager.setPhase(EnderDragonPhase.TAKEOFF);
                }
            }
        }
        return true;
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        if (damageSource instanceof EntityDamageSource && ((EntityDamageSource)damageSource).isThorns()) {
            this.hurt(this.body, damageSource, f);
        }
        return false;
    }

    protected boolean reallyHurt(DamageSource damageSource, float f) {
        return super.hurt(damageSource, f);
    }

    @Override
    public void kill() {
        this.remove();
        if (this.dragonFight != null) {
            this.dragonFight.updateDragon(this);
            this.dragonFight.setDragonKilled(this);
        }
    }

    @Override
    protected void tickDeath() {
        if (this.dragonFight != null) {
            this.dragonFight.updateDragon(this);
        }
        ++this.dragonDeathTime;
        if (this.dragonDeathTime >= 180 && this.dragonDeathTime <= 200) {
            float f = (this.random.nextFloat() - 0.5f) * 8.0f;
            float f2 = (this.random.nextFloat() - 0.5f) * 4.0f;
            float f3 = (this.random.nextFloat() - 0.5f) * 8.0f;
            this.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.getX() + (double)f, this.getY() + 2.0 + (double)f2, this.getZ() + (double)f3, 0.0, 0.0, 0.0);
        }
        boolean bl = this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT);
        int n = 500;
        if (this.dragonFight != null && !this.dragonFight.hasPreviouslyKilledDragon()) {
            n = 12000;
        }
        if (!this.level.isClientSide) {
            if (this.dragonDeathTime > 150 && this.dragonDeathTime % 5 == 0 && bl) {
                this.dropExperience(Mth.floor((float)n * 0.08f));
            }
            if (this.dragonDeathTime == 1 && !this.isSilent()) {
                this.level.globalLevelEvent(1028, this.blockPosition(), 0);
            }
        }
        this.move(MoverType.SELF, new Vec3(0.0, 0.10000000149011612, 0.0));
        this.yRot += 20.0f;
        this.yBodyRot = this.yRot;
        if (this.dragonDeathTime == 200 && !this.level.isClientSide) {
            if (bl) {
                this.dropExperience(Mth.floor((float)n * 0.2f));
            }
            if (this.dragonFight != null) {
                this.dragonFight.setDragonKilled(this);
            }
            this.remove();
        }
    }

    private void dropExperience(int n) {
        while (n > 0) {
            int n2 = ExperienceOrb.getExperienceValue(n);
            n -= n2;
            this.level.addFreshEntity(new ExperienceOrb(this.level, this.getX(), this.getY(), this.getZ(), n2));
        }
    }

    public int findClosestNode() {
        if (this.nodes[0] == null) {
            for (int i = 0; i < 24; ++i) {
                int n;
                int n2;
                int n3 = 5;
                int n4 = i;
                if (i < 12) {
                    n2 = Mth.floor(60.0f * Mth.cos(2.0f * (-3.1415927f + 0.2617994f * (float)n4)));
                    n = Mth.floor(60.0f * Mth.sin(2.0f * (-3.1415927f + 0.2617994f * (float)n4)));
                } else if (i < 20) {
                    n2 = Mth.floor(40.0f * Mth.cos(2.0f * (-3.1415927f + 0.3926991f * (float)(n4 -= 12))));
                    n = Mth.floor(40.0f * Mth.sin(2.0f * (-3.1415927f + 0.3926991f * (float)n4)));
                    n3 += 10;
                } else {
                    n2 = Mth.floor(20.0f * Mth.cos(2.0f * (-3.1415927f + 0.7853982f * (float)(n4 -= 20))));
                    n = Mth.floor(20.0f * Mth.sin(2.0f * (-3.1415927f + 0.7853982f * (float)n4)));
                }
                int n5 = Math.max(this.level.getSeaLevel() + 10, this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(n2, 0, n)).getY() + n3);
                this.nodes[i] = new Node(n2, n5, n);
            }
            this.nodeAdjacency[0] = 6146;
            this.nodeAdjacency[1] = 8197;
            this.nodeAdjacency[2] = 8202;
            this.nodeAdjacency[3] = 16404;
            this.nodeAdjacency[4] = 32808;
            this.nodeAdjacency[5] = 32848;
            this.nodeAdjacency[6] = 65696;
            this.nodeAdjacency[7] = 131392;
            this.nodeAdjacency[8] = 131712;
            this.nodeAdjacency[9] = 263424;
            this.nodeAdjacency[10] = 526848;
            this.nodeAdjacency[11] = 525313;
            this.nodeAdjacency[12] = 1581057;
            this.nodeAdjacency[13] = 3166214;
            this.nodeAdjacency[14] = 2138120;
            this.nodeAdjacency[15] = 6373424;
            this.nodeAdjacency[16] = 4358208;
            this.nodeAdjacency[17] = 12910976;
            this.nodeAdjacency[18] = 9044480;
            this.nodeAdjacency[19] = 9706496;
            this.nodeAdjacency[20] = 15216640;
            this.nodeAdjacency[21] = 13688832;
            this.nodeAdjacency[22] = 11763712;
            this.nodeAdjacency[23] = 8257536;
        }
        return this.findClosestNode(this.getX(), this.getY(), this.getZ());
    }

    public int findClosestNode(double d, double d2, double d3) {
        float f = 10000.0f;
        int n = 0;
        Node node = new Node(Mth.floor(d), Mth.floor(d2), Mth.floor(d3));
        int n2 = 0;
        if (this.dragonFight == null || this.dragonFight.getCrystalsAlive() == 0) {
            n2 = 12;
        }
        for (int i = n2; i < 24; ++i) {
            float f2;
            if (this.nodes[i] == null || !((f2 = this.nodes[i].distanceToSqr(node)) < f)) continue;
            f = f2;
            n = i;
        }
        return n;
    }

    @Nullable
    public Path findPath(int n, int n2, @Nullable Node node) {
        Node node2;
        for (int i = 0; i < 24; ++i) {
            node2 = this.nodes[i];
            node2.closed = false;
            node2.f = 0.0f;
            node2.g = 0.0f;
            node2.h = 0.0f;
            node2.cameFrom = null;
            node2.heapIdx = -1;
        }
        Node node3 = this.nodes[n];
        node2 = this.nodes[n2];
        node3.g = 0.0f;
        node3.f = node3.h = node3.distanceTo(node2);
        this.openSet.clear();
        this.openSet.insert(node3);
        Node node4 = node3;
        int n3 = 0;
        if (this.dragonFight == null || this.dragonFight.getCrystalsAlive() == 0) {
            n3 = 12;
        }
        while (!this.openSet.isEmpty()) {
            int n4;
            Node node5 = this.openSet.pop();
            if (node5.equals(node2)) {
                if (node != null) {
                    node.cameFrom = node2;
                    node2 = node;
                }
                return this.reconstructPath(node3, node2);
            }
            if (node5.distanceTo(node2) < node4.distanceTo(node2)) {
                node4 = node5;
            }
            node5.closed = true;
            int n5 = 0;
            for (n4 = 0; n4 < 24; ++n4) {
                if (this.nodes[n4] != node5) continue;
                n5 = n4;
                break;
            }
            for (n4 = n3; n4 < 24; ++n4) {
                if ((this.nodeAdjacency[n5] & 1 << n4) <= 0) continue;
                Node node6 = this.nodes[n4];
                if (node6.closed) continue;
                float f = node5.g + node5.distanceTo(node6);
                if (node6.inOpenSet() && !(f < node6.g)) continue;
                node6.cameFrom = node5;
                node6.g = f;
                node6.h = node6.distanceTo(node2);
                if (node6.inOpenSet()) {
                    this.openSet.changeCost(node6, node6.g + node6.h);
                    continue;
                }
                node6.f = node6.g + node6.h;
                this.openSet.insert(node6);
            }
        }
        if (node4 == node3) {
            return null;
        }
        LOGGER.debug("Failed to find path from {} to {}", (Object)n, (Object)n2);
        if (node != null) {
            node.cameFrom = node4;
            node4 = node;
        }
        return this.reconstructPath(node3, node4);
    }

    private Path reconstructPath(Node node, Node node2) {
        ArrayList arrayList = Lists.newArrayList();
        Node node3 = node2;
        arrayList.add(0, node3);
        while (node3.cameFrom != null) {
            node3 = node3.cameFrom;
            arrayList.add(0, node3);
        }
        return new Path(arrayList, new BlockPos(node2.x, node2.y, node2.z), true);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("DragonPhase", this.phaseManager.getCurrentPhase().getPhase().getId());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        if (compoundTag.contains("DragonPhase")) {
            this.phaseManager.setPhase(EnderDragonPhase.getById(compoundTag.getInt("DragonPhase")));
        }
    }

    @Override
    public void checkDespawn() {
    }

    public EnderDragonPart[] getSubEntities() {
        return this.subEntities;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENDER_DRAGON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.ENDER_DRAGON_HURT;
    }

    @Override
    protected float getSoundVolume() {
        return 5.0f;
    }

    public float getHeadPartYOffset(int n, double[] arrd, double[] arrd2) {
        double d;
        DragonPhaseInstance dragonPhaseInstance = this.phaseManager.getCurrentPhase();
        EnderDragonPhase<? extends DragonPhaseInstance> enderDragonPhase = dragonPhaseInstance.getPhase();
        if (enderDragonPhase == EnderDragonPhase.LANDING || enderDragonPhase == EnderDragonPhase.TAKEOFF) {
            BlockPos blockPos = this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.END_PODIUM_LOCATION);
            float f = Math.max(Mth.sqrt(blockPos.distSqr(this.position(), true)) / 4.0f, 1.0f);
            d = (float)n / f;
        } else {
            d = dragonPhaseInstance.isSitting() ? (double)n : (n == 6 ? 0.0 : arrd2[1] - arrd[1]);
        }
        return (float)d;
    }

    public Vec3 getHeadLookVector(float f) {
        Vec3 vec3;
        DragonPhaseInstance dragonPhaseInstance = this.phaseManager.getCurrentPhase();
        EnderDragonPhase<? extends DragonPhaseInstance> enderDragonPhase = dragonPhaseInstance.getPhase();
        if (enderDragonPhase == EnderDragonPhase.LANDING || enderDragonPhase == EnderDragonPhase.TAKEOFF) {
            BlockPos blockPos = this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.END_PODIUM_LOCATION);
            float f2 = Math.max(Mth.sqrt(blockPos.distSqr(this.position(), true)) / 4.0f, 1.0f);
            float f3 = 6.0f / f2;
            float f4 = this.xRot;
            float f5 = 1.5f;
            this.xRot = -f3 * 1.5f * 5.0f;
            vec3 = this.getViewVector(f);
            this.xRot = f4;
        } else if (dragonPhaseInstance.isSitting()) {
            float f6 = this.xRot;
            float f7 = 1.5f;
            this.xRot = -45.0f;
            vec3 = this.getViewVector(f);
            this.xRot = f6;
        } else {
            vec3 = this.getViewVector(f);
        }
        return vec3;
    }

    public void onCrystalDestroyed(EndCrystal endCrystal, BlockPos blockPos, DamageSource damageSource) {
        Player player = damageSource.getEntity() instanceof Player ? (Player)damageSource.getEntity() : this.level.getNearestPlayer(CRYSTAL_DESTROY_TARGETING, blockPos.getX(), blockPos.getY(), blockPos.getZ());
        if (endCrystal == this.nearestCrystal) {
            this.hurt(this.head, DamageSource.explosion(player), 10.0f);
        }
        this.phaseManager.getCurrentPhase().onCrystalDestroyed(endCrystal, blockPos, damageSource, player);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
        if (DATA_PHASE.equals(entityDataAccessor) && this.level.isClientSide) {
            this.phaseManager.setPhase(EnderDragonPhase.getById(this.getEntityData().get(DATA_PHASE)));
        }
        super.onSyncedDataUpdated(entityDataAccessor);
    }

    public EnderDragonPhaseManager getPhaseManager() {
        return this.phaseManager;
    }

    @Nullable
    public EndDragonFight getDragonFight() {
        return this.dragonFight;
    }

    @Override
    public boolean addEffect(MobEffectInstance mobEffectInstance) {
        return false;
    }

    @Override
    protected boolean canRide(Entity entity) {
        return false;
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }
}

