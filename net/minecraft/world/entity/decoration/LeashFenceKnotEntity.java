/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.decoration;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class LeashFenceKnotEntity
extends HangingEntity {
    public LeashFenceKnotEntity(EntityType<? extends LeashFenceKnotEntity> entityType, Level level) {
        super(entityType, level);
    }

    public LeashFenceKnotEntity(Level level, BlockPos blockPos) {
        super(EntityType.LEASH_KNOT, level, blockPos);
        this.setPos((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5);
        float f = 0.125f;
        float f2 = 0.1875f;
        float f3 = 0.25f;
        this.setBoundingBox(new AABB(this.getX() - 0.1875, this.getY() - 0.25 + 0.125, this.getZ() - 0.1875, this.getX() + 0.1875, this.getY() + 0.25 + 0.125, this.getZ() + 0.1875));
        this.forcedLoading = true;
    }

    @Override
    public void setPos(double d, double d2, double d3) {
        super.setPos((double)Mth.floor(d) + 0.5, (double)Mth.floor(d2) + 0.5, (double)Mth.floor(d3) + 0.5);
    }

    @Override
    protected void recalculateBoundingBox() {
        this.setPosRaw((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5);
    }

    @Override
    public void setDirection(Direction direction) {
    }

    @Override
    public int getWidth() {
        return 9;
    }

    @Override
    public int getHeight() {
        return 9;
    }

    @Override
    protected float getEyeHeight(Pose pose, EntityDimensions entityDimensions) {
        return -0.0625f;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double d) {
        return d < 1024.0;
    }

    @Override
    public void dropItem(@Nullable Entity entity) {
        this.playSound(SoundEvents.LEASH_KNOT_BREAK, 1.0f, 1.0f);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand interactionHand) {
        if (this.level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        boolean bl = false;
        double d = 7.0;
        List<Mob> list = this.level.getEntitiesOfClass(Mob.class, new AABB(this.getX() - 7.0, this.getY() - 7.0, this.getZ() - 7.0, this.getX() + 7.0, this.getY() + 7.0, this.getZ() + 7.0));
        for (Mob mob : list) {
            if (mob.getLeashHolder() != player) continue;
            mob.setLeashedTo(this, true);
            bl = true;
        }
        if (!bl) {
            this.remove();
            if (player.abilities.instabuild) {
                for (Mob mob : list) {
                    if (!mob.isLeashed() || mob.getLeashHolder() != this) continue;
                    mob.dropLeash(true, false);
                }
            }
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public boolean survives() {
        return this.level.getBlockState(this.pos).getBlock().is(BlockTags.FENCES);
    }

    public static LeashFenceKnotEntity getOrCreateKnot(Level level, BlockPos blockPos) {
        int n = blockPos.getX();
        int n2 = blockPos.getY();
        int n3 = blockPos.getZ();
        List<LeashFenceKnotEntity> list = level.getEntitiesOfClass(LeashFenceKnotEntity.class, new AABB((double)n - 1.0, (double)n2 - 1.0, (double)n3 - 1.0, (double)n + 1.0, (double)n2 + 1.0, (double)n3 + 1.0));
        for (LeashFenceKnotEntity leashFenceKnotEntity : list) {
            if (!leashFenceKnotEntity.getPos().equals(blockPos)) continue;
            return leashFenceKnotEntity;
        }
        LeashFenceKnotEntity leashFenceKnotEntity = new LeashFenceKnotEntity(level, blockPos);
        level.addFreshEntity(leashFenceKnotEntity);
        leashFenceKnotEntity.playPlacementSound();
        return leashFenceKnotEntity;
    }

    @Override
    public void playPlacementSound() {
        this.playSound(SoundEvents.LEASH_KNOT_PLACE, 1.0f, 1.0f);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this, this.getType(), 0, this.getPos());
    }

    @Override
    public Vec3 getRopeHoldPosition(float f) {
        return this.getPosition(f).add(0.0, 0.2, 0.0);
    }
}

