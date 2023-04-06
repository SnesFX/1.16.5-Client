/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.boss.enderdragon;

import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.end.EndDragonFight;

public class EndCrystal
extends Entity {
    private static final EntityDataAccessor<Optional<BlockPos>> DATA_BEAM_TARGET = SynchedEntityData.defineId(EndCrystal.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final EntityDataAccessor<Boolean> DATA_SHOW_BOTTOM = SynchedEntityData.defineId(EndCrystal.class, EntityDataSerializers.BOOLEAN);
    public int time;

    public EndCrystal(EntityType<? extends EndCrystal> entityType, Level level) {
        super(entityType, level);
        this.blocksBuilding = true;
        this.time = this.random.nextInt(100000);
    }

    public EndCrystal(Level level, double d, double d2, double d3) {
        this(EntityType.END_CRYSTAL, level);
        this.setPos(d, d2, d3);
    }

    @Override
    protected boolean isMovementNoisy() {
        return false;
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(DATA_BEAM_TARGET, Optional.empty());
        this.getEntityData().define(DATA_SHOW_BOTTOM, true);
    }

    @Override
    public void tick() {
        ++this.time;
        if (this.level instanceof ServerLevel) {
            BlockPos blockPos = this.blockPosition();
            if (((ServerLevel)this.level).dragonFight() != null && this.level.getBlockState(blockPos).isAir()) {
                this.level.setBlockAndUpdate(blockPos, BaseFireBlock.getState(this.level, blockPos));
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        if (this.getBeamTarget() != null) {
            compoundTag.put("BeamTarget", NbtUtils.writeBlockPos(this.getBeamTarget()));
        }
        compoundTag.putBoolean("ShowBottom", this.showsBottom());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        if (compoundTag.contains("BeamTarget", 10)) {
            this.setBeamTarget(NbtUtils.readBlockPos(compoundTag.getCompound("BeamTarget")));
        }
        if (compoundTag.contains("ShowBottom", 1)) {
            this.setShowBottom(compoundTag.getBoolean("ShowBottom"));
        }
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        if (this.isInvulnerableTo(damageSource)) {
            return false;
        }
        if (damageSource.getEntity() instanceof EnderDragon) {
            return false;
        }
        if (!this.removed && !this.level.isClientSide) {
            this.remove();
            if (!damageSource.isExplosion()) {
                this.level.explode(null, this.getX(), this.getY(), this.getZ(), 6.0f, Explosion.BlockInteraction.DESTROY);
            }
            this.onDestroyedBy(damageSource);
        }
        return true;
    }

    @Override
    public void kill() {
        this.onDestroyedBy(DamageSource.GENERIC);
        super.kill();
    }

    private void onDestroyedBy(DamageSource damageSource) {
        EndDragonFight endDragonFight;
        if (this.level instanceof ServerLevel && (endDragonFight = ((ServerLevel)this.level).dragonFight()) != null) {
            endDragonFight.onCrystalDestroyed(this, damageSource);
        }
    }

    public void setBeamTarget(@Nullable BlockPos blockPos) {
        this.getEntityData().set(DATA_BEAM_TARGET, Optional.ofNullable(blockPos));
    }

    @Nullable
    public BlockPos getBeamTarget() {
        return this.getEntityData().get(DATA_BEAM_TARGET).orElse(null);
    }

    public void setShowBottom(boolean bl) {
        this.getEntityData().set(DATA_SHOW_BOTTOM, bl);
    }

    public boolean showsBottom() {
        return this.getEntityData().get(DATA_SHOW_BOTTOM);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double d) {
        return super.shouldRenderAtSqrDistance(d) || this.getBeamTarget() != null;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}

