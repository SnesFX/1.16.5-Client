/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.projectile;

import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class LlamaSpit
extends Projectile {
    public LlamaSpit(EntityType<? extends LlamaSpit> entityType, Level level) {
        super(entityType, level);
    }

    public LlamaSpit(Level level, Llama llama) {
        this(EntityType.LLAMA_SPIT, level);
        super.setOwner(llama);
        this.setPos(llama.getX() - (double)(llama.getBbWidth() + 1.0f) * 0.5 * (double)Mth.sin(llama.yBodyRot * 0.017453292f), llama.getEyeY() - 0.10000000149011612, llama.getZ() + (double)(llama.getBbWidth() + 1.0f) * 0.5 * (double)Mth.cos(llama.yBodyRot * 0.017453292f));
    }

    public LlamaSpit(Level level, double d, double d2, double d3, double d4, double d5, double d6) {
        this(EntityType.LLAMA_SPIT, level);
        this.setPos(d, d2, d3);
        for (int i = 0; i < 7; ++i) {
            double d7 = 0.4 + 0.1 * (double)i;
            level.addParticle(ParticleTypes.SPIT, d, d2, d3, d4 * d7, d5, d6 * d7);
        }
        this.setDeltaMovement(d4, d5, d6);
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 vec3 = this.getDeltaMovement();
        HitResult hitResult = ProjectileUtil.getHitResult(this, this::canHitEntity);
        if (hitResult != null) {
            this.onHit(hitResult);
        }
        double d = this.getX() + vec3.x;
        double d2 = this.getY() + vec3.y;
        double d3 = this.getZ() + vec3.z;
        this.updateRotation();
        float f = 0.99f;
        float f2 = 0.06f;
        if (this.level.getBlockStates(this.getBoundingBox()).noneMatch(BlockBehaviour.BlockStateBase::isAir)) {
            this.remove();
            return;
        }
        if (this.isInWaterOrBubble()) {
            this.remove();
            return;
        }
        this.setDeltaMovement(vec3.scale(0.9900000095367432));
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.05999999865889549, 0.0));
        }
        this.setPos(d, d2, d3);
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        Entity entity = this.getOwner();
        if (entity instanceof LivingEntity) {
            entityHitResult.getEntity().hurt(DamageSource.indirectMobAttack(this, (LivingEntity)entity).setProjectile(), 1.0f);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        if (!this.level.isClientSide) {
            this.remove();
        }
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}

