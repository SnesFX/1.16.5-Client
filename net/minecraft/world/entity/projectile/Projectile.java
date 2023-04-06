/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.projectile;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public abstract class Projectile
extends Entity {
    private UUID ownerUUID;
    private int ownerNetworkId;
    private boolean leftOwner;

    Projectile(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    public void setOwner(@Nullable Entity entity) {
        if (entity != null) {
            this.ownerUUID = entity.getUUID();
            this.ownerNetworkId = entity.getId();
        }
    }

    @Nullable
    public Entity getOwner() {
        if (this.ownerUUID != null && this.level instanceof ServerLevel) {
            return ((ServerLevel)this.level).getEntity(this.ownerUUID);
        }
        if (this.ownerNetworkId != 0) {
            return this.level.getEntity(this.ownerNetworkId);
        }
        return null;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        if (this.ownerUUID != null) {
            compoundTag.putUUID("Owner", this.ownerUUID);
        }
        if (this.leftOwner) {
            compoundTag.putBoolean("LeftOwner", true);
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        if (compoundTag.hasUUID("Owner")) {
            this.ownerUUID = compoundTag.getUUID("Owner");
        }
        this.leftOwner = compoundTag.getBoolean("LeftOwner");
    }

    @Override
    public void tick() {
        if (!this.leftOwner) {
            this.leftOwner = this.checkLeftOwner();
        }
        super.tick();
    }

    private boolean checkLeftOwner() {
        Entity entity2 = this.getOwner();
        if (entity2 != null) {
            for (Entity entity3 : this.level.getEntities(this, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0), entity -> !entity.isSpectator() && entity.isPickable())) {
                if (entity3.getRootVehicle() != entity2.getRootVehicle()) continue;
                return false;
            }
        }
        return true;
    }

    public void shoot(double d, double d2, double d3, float f, float f2) {
        Vec3 vec3 = new Vec3(d, d2, d3).normalize().add(this.random.nextGaussian() * 0.007499999832361937 * (double)f2, this.random.nextGaussian() * 0.007499999832361937 * (double)f2, this.random.nextGaussian() * 0.007499999832361937 * (double)f2).scale(f);
        this.setDeltaMovement(vec3);
        float f3 = Mth.sqrt(Projectile.getHorizontalDistanceSqr(vec3));
        this.yRot = (float)(Mth.atan2(vec3.x, vec3.z) * 57.2957763671875);
        this.xRot = (float)(Mth.atan2(vec3.y, f3) * 57.2957763671875);
        this.yRotO = this.yRot;
        this.xRotO = this.xRot;
    }

    public void shootFromRotation(Entity entity, float f, float f2, float f3, float f4, float f5) {
        float f6 = -Mth.sin(f2 * 0.017453292f) * Mth.cos(f * 0.017453292f);
        float f7 = -Mth.sin((f + f3) * 0.017453292f);
        float f8 = Mth.cos(f2 * 0.017453292f) * Mth.cos(f * 0.017453292f);
        this.shoot(f6, f7, f8, f4, f5);
        Vec3 vec3 = entity.getDeltaMovement();
        this.setDeltaMovement(this.getDeltaMovement().add(vec3.x, entity.isOnGround() ? 0.0 : vec3.y, vec3.z));
    }

    protected void onHit(HitResult hitResult) {
        HitResult.Type type = hitResult.getType();
        if (type == HitResult.Type.ENTITY) {
            this.onHitEntity((EntityHitResult)hitResult);
        } else if (type == HitResult.Type.BLOCK) {
            this.onHitBlock((BlockHitResult)hitResult);
        }
    }

    protected void onHitEntity(EntityHitResult entityHitResult) {
    }

    protected void onHitBlock(BlockHitResult blockHitResult) {
        BlockHitResult blockHitResult2 = blockHitResult;
        BlockState blockState = this.level.getBlockState(blockHitResult2.getBlockPos());
        blockState.onProjectileHit(this.level, blockState, blockHitResult2, this);
    }

    @Override
    public void lerpMotion(double d, double d2, double d3) {
        this.setDeltaMovement(d, d2, d3);
        if (this.xRotO == 0.0f && this.yRotO == 0.0f) {
            float f = Mth.sqrt(d * d + d3 * d3);
            this.xRot = (float)(Mth.atan2(d2, f) * 57.2957763671875);
            this.yRot = (float)(Mth.atan2(d, d3) * 57.2957763671875);
            this.xRotO = this.xRot;
            this.yRotO = this.yRot;
            this.moveTo(this.getX(), this.getY(), this.getZ(), this.yRot, this.xRot);
        }
    }

    protected boolean canHitEntity(Entity entity) {
        if (entity.isSpectator() || !entity.isAlive() || !entity.isPickable()) {
            return false;
        }
        Entity entity2 = this.getOwner();
        return entity2 == null || this.leftOwner || !entity2.isPassengerOfSameVehicle(entity);
    }

    protected void updateRotation() {
        Vec3 vec3 = this.getDeltaMovement();
        float f = Mth.sqrt(Projectile.getHorizontalDistanceSqr(vec3));
        this.xRot = Projectile.lerpRotation(this.xRotO, (float)(Mth.atan2(vec3.y, f) * 57.2957763671875));
        this.yRot = Projectile.lerpRotation(this.yRotO, (float)(Mth.atan2(vec3.x, vec3.z) * 57.2957763671875));
    }

    protected static float lerpRotation(float f, float f2) {
        while (f2 - f < -180.0f) {
            f -= 360.0f;
        }
        while (f2 - f >= 180.0f) {
            f += 360.0f;
        }
        return Mth.lerp(0.2f, f, f2);
    }
}

