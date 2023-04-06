/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public abstract class ThrowableProjectile
extends Projectile {
    protected ThrowableProjectile(EntityType<? extends ThrowableProjectile> entityType, Level level) {
        super(entityType, level);
    }

    protected ThrowableProjectile(EntityType<? extends ThrowableProjectile> entityType, double d, double d2, double d3, Level level) {
        this(entityType, level);
        this.setPos(d, d2, d3);
    }

    protected ThrowableProjectile(EntityType<? extends ThrowableProjectile> entityType, LivingEntity livingEntity, Level level) {
        this(entityType, livingEntity.getX(), livingEntity.getEyeY() - 0.10000000149011612, livingEntity.getZ(), level);
        this.setOwner(livingEntity);
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
        Object object;
        float f;
        super.tick();
        HitResult hitResult = ProjectileUtil.getHitResult(this, this::canHitEntity);
        boolean bl = false;
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            object = ((BlockHitResult)hitResult).getBlockPos();
            BlockState blockState = this.level.getBlockState((BlockPos)object);
            if (blockState.is(Blocks.NETHER_PORTAL)) {
                this.handleInsidePortal((BlockPos)object);
                bl = true;
            } else if (blockState.is(Blocks.END_GATEWAY)) {
                BlockEntity blockEntity = this.level.getBlockEntity((BlockPos)object);
                if (blockEntity instanceof TheEndGatewayBlockEntity && TheEndGatewayBlockEntity.canEntityTeleport(this)) {
                    ((TheEndGatewayBlockEntity)blockEntity).teleportEntity(this);
                }
                bl = true;
            }
        }
        if (hitResult.getType() != HitResult.Type.MISS && !bl) {
            this.onHit(hitResult);
        }
        this.checkInsideBlocks();
        object = this.getDeltaMovement();
        double d = this.getX() + ((Vec3)object).x;
        double d2 = this.getY() + ((Vec3)object).y;
        double d3 = this.getZ() + ((Vec3)object).z;
        this.updateRotation();
        if (this.isInWater()) {
            for (int i = 0; i < 4; ++i) {
                float f2 = 0.25f;
                this.level.addParticle(ParticleTypes.BUBBLE, d - ((Vec3)object).x * 0.25, d2 - ((Vec3)object).y * 0.25, d3 - ((Vec3)object).z * 0.25, ((Vec3)object).x, ((Vec3)object).y, ((Vec3)object).z);
            }
            f = 0.8f;
        } else {
            f = 0.99f;
        }
        this.setDeltaMovement(((Vec3)object).scale(f));
        if (!this.isNoGravity()) {
            Vec3 vec3 = this.getDeltaMovement();
            this.setDeltaMovement(vec3.x, vec3.y - (double)this.getGravity(), vec3.z);
        }
        this.setPos(d, d2, d3);
    }

    protected float getGravity() {
        return 0.03f;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}

