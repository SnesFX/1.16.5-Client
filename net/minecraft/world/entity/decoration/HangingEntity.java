/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.Validate
 */
package net.minecraft.world.entity.decoration;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.Validate;

public abstract class HangingEntity
extends Entity {
    protected static final Predicate<Entity> HANGING_ENTITY = entity -> entity instanceof HangingEntity;
    private int checkInterval;
    protected BlockPos pos;
    protected Direction direction = Direction.SOUTH;

    protected HangingEntity(EntityType<? extends HangingEntity> entityType, Level level) {
        super(entityType, level);
    }

    protected HangingEntity(EntityType<? extends HangingEntity> entityType, Level level, BlockPos blockPos) {
        this(entityType, level);
        this.pos = blockPos;
    }

    @Override
    protected void defineSynchedData() {
    }

    protected void setDirection(Direction direction) {
        Validate.notNull((Object)direction);
        Validate.isTrue((boolean)direction.getAxis().isHorizontal());
        this.direction = direction;
        this.yRotO = this.yRot = (float)(this.direction.get2DDataValue() * 90);
        this.recalculateBoundingBox();
    }

    protected void recalculateBoundingBox() {
        if (this.direction == null) {
            return;
        }
        double d = (double)this.pos.getX() + 0.5;
        double d2 = (double)this.pos.getY() + 0.5;
        double d3 = (double)this.pos.getZ() + 0.5;
        double d4 = 0.46875;
        double d5 = this.offs(this.getWidth());
        double d6 = this.offs(this.getHeight());
        d -= (double)this.direction.getStepX() * 0.46875;
        d3 -= (double)this.direction.getStepZ() * 0.46875;
        Direction direction = this.direction.getCounterClockWise();
        this.setPosRaw(d += d5 * (double)direction.getStepX(), d2 += d6, d3 += d5 * (double)direction.getStepZ());
        double d7 = this.getWidth();
        double d8 = this.getHeight();
        double d9 = this.getWidth();
        if (this.direction.getAxis() == Direction.Axis.Z) {
            d9 = 1.0;
        } else {
            d7 = 1.0;
        }
        this.setBoundingBox(new AABB(d - (d7 /= 32.0), d2 - (d8 /= 32.0), d3 - (d9 /= 32.0), d + d7, d2 + d8, d3 + d9));
    }

    private double offs(int n) {
        return n % 32 == 0 ? 0.5 : 0.0;
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide) {
            if (this.getY() < -64.0) {
                this.outOfWorld();
            }
            if (this.checkInterval++ == 100) {
                this.checkInterval = 0;
                if (!this.removed && !this.survives()) {
                    this.remove();
                    this.dropItem(null);
                }
            }
        }
    }

    public boolean survives() {
        if (!this.level.noCollision(this)) {
            return false;
        }
        int n = Math.max(1, this.getWidth() / 16);
        int n2 = Math.max(1, this.getHeight() / 16);
        BlockPos blockPos = this.pos.relative(this.direction.getOpposite());
        Direction direction = this.direction.getCounterClockWise();
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n2; ++j) {
                int n3 = (n - 1) / -2;
                int n4 = (n2 - 1) / -2;
                mutableBlockPos.set(blockPos).move(direction, i + n3).move(Direction.UP, j + n4);
                BlockState blockState = this.level.getBlockState(mutableBlockPos);
                if (blockState.getMaterial().isSolid() || DiodeBlock.isDiode(blockState)) continue;
                return false;
            }
        }
        return this.level.getEntities(this, this.getBoundingBox(), HANGING_ENTITY).isEmpty();
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean skipAttackInteraction(Entity entity) {
        if (entity instanceof Player) {
            Player player = (Player)entity;
            if (!this.level.mayInteract(player, this.pos)) {
                return true;
            }
            return this.hurt(DamageSource.playerAttack(player), 0.0f);
        }
        return false;
    }

    @Override
    public Direction getDirection() {
        return this.direction;
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        if (this.isInvulnerableTo(damageSource)) {
            return false;
        }
        if (!this.removed && !this.level.isClientSide) {
            this.remove();
            this.markHurt();
            this.dropItem(damageSource.getEntity());
        }
        return true;
    }

    @Override
    public void move(MoverType moverType, Vec3 vec3) {
        if (!this.level.isClientSide && !this.removed && vec3.lengthSqr() > 0.0) {
            this.remove();
            this.dropItem(null);
        }
    }

    @Override
    public void push(double d, double d2, double d3) {
        if (!this.level.isClientSide && !this.removed && d * d + d2 * d2 + d3 * d3 > 0.0) {
            this.remove();
            this.dropItem(null);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        BlockPos blockPos = this.getPos();
        compoundTag.putInt("TileX", blockPos.getX());
        compoundTag.putInt("TileY", blockPos.getY());
        compoundTag.putInt("TileZ", blockPos.getZ());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        this.pos = new BlockPos(compoundTag.getInt("TileX"), compoundTag.getInt("TileY"), compoundTag.getInt("TileZ"));
    }

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract void dropItem(@Nullable Entity var1);

    public abstract void playPlacementSound();

    @Override
    public ItemEntity spawnAtLocation(ItemStack itemStack, float f) {
        ItemEntity itemEntity = new ItemEntity(this.level, this.getX() + (double)((float)this.direction.getStepX() * 0.15f), this.getY() + (double)f, this.getZ() + (double)((float)this.direction.getStepZ() * 0.15f), itemStack);
        itemEntity.setDefaultPickUpDelay();
        this.level.addFreshEntity(itemEntity);
        return itemEntity;
    }

    @Override
    protected boolean repositionEntityAfterLoad() {
        return false;
    }

    @Override
    public void setPos(double d, double d2, double d3) {
        this.pos = new BlockPos(d, d2, d3);
        this.recalculateBoundingBox();
        this.hasImpulse = true;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    @Override
    public float rotate(Rotation rotation) {
        if (this.direction.getAxis() != Direction.Axis.Y) {
            switch (rotation) {
                case CLOCKWISE_180: {
                    this.direction = this.direction.getOpposite();
                    break;
                }
                case COUNTERCLOCKWISE_90: {
                    this.direction = this.direction.getCounterClockWise();
                    break;
                }
                case CLOCKWISE_90: {
                    this.direction = this.direction.getClockWise();
                    break;
                }
            }
        }
        float f = Mth.wrapDegrees(this.yRot);
        switch (rotation) {
            case CLOCKWISE_180: {
                return f + 180.0f;
            }
            case COUNTERCLOCKWISE_90: {
                return f + 90.0f;
            }
            case CLOCKWISE_90: {
                return f + 270.0f;
            }
        }
        return f;
    }

    @Override
    public float mirror(Mirror mirror) {
        return this.rotate(mirror.getRotation(this.direction));
    }

    @Override
    public void thunderHit(ServerLevel serverLevel, LightningBolt lightningBolt) {
    }

    @Override
    public void refreshDimensions() {
    }

}

