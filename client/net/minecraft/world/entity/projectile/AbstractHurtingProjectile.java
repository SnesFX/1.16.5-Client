/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.projectile;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractHurtingProjectile
extends Projectile {
    public double xPower;
    public double yPower;
    public double zPower;

    protected AbstractHurtingProjectile(EntityType<? extends AbstractHurtingProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public AbstractHurtingProjectile(EntityType<? extends AbstractHurtingProjectile> entityType, double d, double d2, double d3, double d4, double d5, double d6, Level level) {
        this(entityType, level);
        this.moveTo(d, d2, d3, this.yRot, this.xRot);
        this.reapplyPosition();
        double d7 = Mth.sqrt(d4 * d4 + d5 * d5 + d6 * d6);
        if (d7 != 0.0) {
            this.xPower = d4 / d7 * 0.1;
            this.yPower = d5 / d7 * 0.1;
            this.zPower = d6 / d7 * 0.1;
        }
    }

    public AbstractHurtingProjectile(EntityType<? extends AbstractHurtingProjectile> entityType, LivingEntity livingEntity, double d, double d2, double d3, Level level) {
        this(entityType, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), d, d2, d3, level);
        this.setOwner(livingEntity);
        this.setRot(livingEntity.yRot, livingEntity.xRot);
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double d) {
        double d2 = this.getBoundingBox().getSize() * 4.0;
        if (Double.isNaN(d2)) {
            d2 = 4.0;
        }
        return d < (d2 *= 64.0) * d2;
    }

    @Override
    public void tick() {
        HitResult hitResult;
        Entity entity = this.getOwner();
        if (!this.level.isClientSide && (entity != null && entity.removed || !this.level.hasChunkAt(this.blockPosition()))) {
            this.remove();
            return;
        }
        super.tick();
        if (this.shouldBurn()) {
            this.setSecondsOnFire(1);
        }
        if ((hitResult = ProjectileUtil.getHitResult(this, this::canHitEntity)).getType() != HitResult.Type.MISS) {
            this.onHit(hitResult);
        }
        this.checkInsideBlocks();
        Vec3 vec3 = this.getDeltaMovement();
        double d = this.getX() + vec3.x;
        double d2 = this.getY() + vec3.y;
        double d3 = this.getZ() + vec3.z;
        ProjectileUtil.rotateTowardsMovement(this, 0.2f);
        float f = this.getInertia();
        if (this.isInWater()) {
            for (int i = 0; i < 4; ++i) {
                float f2 = 0.25f;
                this.level.addParticle(ParticleTypes.BUBBLE, d - vec3.x * 0.25, d2 - vec3.y * 0.25, d3 - vec3.z * 0.25, vec3.x, vec3.y, vec3.z);
            }
            f = 0.8f;
        }
        this.setDeltaMovement(vec3.add(this.xPower, this.yPower, this.zPower).scale(f));
        this.level.addParticle(this.getTrailParticle(), d, d2 + 0.5, d3, 0.0, 0.0, 0.0);
        this.setPos(d, d2, d3);
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return super.canHitEntity(entity) && !entity.noPhysics;
    }

    protected boolean shouldBurn() {
        return true;
    }

    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.SMOKE;
    }

    protected float getInertia() {
        return 0.95f;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.put("power", this.newDoubleList(this.xPower, this.yPower, this.zPower));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        ListTag listTag;
        super.readAdditionalSaveData(compoundTag);
        if (compoundTag.contains("power", 9) && (listTag = compoundTag.getList("power", 6)).size() == 3) {
            this.xPower = listTag.getDouble(0);
            this.yPower = listTag.getDouble(1);
            this.zPower = listTag.getDouble(2);
        }
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public float getPickRadius() {
        return 1.0f;
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        if (this.isInvulnerableTo(damageSource)) {
            return false;
        }
        this.markHurt();
        Entity entity = damageSource.getEntity();
        if (entity != null) {
            Vec3 vec3 = entity.getLookAngle();
            this.setDeltaMovement(vec3);
            this.xPower = vec3.x * 0.1;
            this.yPower = vec3.y * 0.1;
            this.zPower = vec3.z * 0.1;
            this.setOwner(entity);
            return true;
        }
        return false;
    }

    @Override
    public float getBrightness() {
        return 1.0f;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        Entity entity = this.getOwner();
        int n = entity == null ? 0 : entity.getId();
        return new ClientboundAddEntityPacket(this.getId(), this.getUUID(), this.getX(), this.getY(), this.getZ(), this.xRot, this.yRot, this.getType(), n, new Vec3(this.xPower, this.yPower, this.zPower));
    }
}

