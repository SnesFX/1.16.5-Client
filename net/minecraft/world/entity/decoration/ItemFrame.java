/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.Validate
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.entity.decoration;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ItemFrame
extends HangingEntity {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final EntityDataAccessor<ItemStack> DATA_ITEM = SynchedEntityData.defineId(ItemFrame.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Integer> DATA_ROTATION = SynchedEntityData.defineId(ItemFrame.class, EntityDataSerializers.INT);
    private float dropChance = 1.0f;
    private boolean fixed;

    public ItemFrame(EntityType<? extends ItemFrame> entityType, Level level) {
        super(entityType, level);
    }

    public ItemFrame(Level level, BlockPos blockPos, Direction direction) {
        super(EntityType.ITEM_FRAME, level, blockPos);
        this.setDirection(direction);
    }

    @Override
    protected float getEyeHeight(Pose pose, EntityDimensions entityDimensions) {
        return 0.0f;
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(DATA_ITEM, ItemStack.EMPTY);
        this.getEntityData().define(DATA_ROTATION, 0);
    }

    @Override
    protected void setDirection(Direction direction) {
        Validate.notNull((Object)direction);
        this.direction = direction;
        if (direction.getAxis().isHorizontal()) {
            this.xRot = 0.0f;
            this.yRot = this.direction.get2DDataValue() * 90;
        } else {
            this.xRot = -90 * direction.getAxisDirection().getStep();
            this.yRot = 0.0f;
        }
        this.xRotO = this.xRot;
        this.yRotO = this.yRot;
        this.recalculateBoundingBox();
    }

    @Override
    protected void recalculateBoundingBox() {
        if (this.direction == null) {
            return;
        }
        double d = 0.46875;
        double d2 = (double)this.pos.getX() + 0.5 - (double)this.direction.getStepX() * 0.46875;
        double d3 = (double)this.pos.getY() + 0.5 - (double)this.direction.getStepY() * 0.46875;
        double d4 = (double)this.pos.getZ() + 0.5 - (double)this.direction.getStepZ() * 0.46875;
        this.setPosRaw(d2, d3, d4);
        double d5 = this.getWidth();
        double d6 = this.getHeight();
        double d7 = this.getWidth();
        Direction.Axis axis = this.direction.getAxis();
        switch (axis) {
            case X: {
                d5 = 1.0;
                break;
            }
            case Y: {
                d6 = 1.0;
                break;
            }
            case Z: {
                d7 = 1.0;
            }
        }
        this.setBoundingBox(new AABB(d2 - (d5 /= 32.0), d3 - (d6 /= 32.0), d4 - (d7 /= 32.0), d2 + d5, d3 + d6, d4 + d7));
    }

    @Override
    public boolean survives() {
        if (this.fixed) {
            return true;
        }
        if (!this.level.noCollision(this)) {
            return false;
        }
        BlockState blockState = this.level.getBlockState(this.pos.relative(this.direction.getOpposite()));
        if (!(blockState.getMaterial().isSolid() || this.direction.getAxis().isHorizontal() && DiodeBlock.isDiode(blockState))) {
            return false;
        }
        return this.level.getEntities(this, this.getBoundingBox(), HANGING_ENTITY).isEmpty();
    }

    @Override
    public void move(MoverType moverType, Vec3 vec3) {
        if (!this.fixed) {
            super.move(moverType, vec3);
        }
    }

    @Override
    public void push(double d, double d2, double d3) {
        if (!this.fixed) {
            super.push(d, d2, d3);
        }
    }

    @Override
    public float getPickRadius() {
        return 0.0f;
    }

    @Override
    public void kill() {
        this.removeFramedMap(this.getItem());
        super.kill();
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        if (this.fixed) {
            if (damageSource == DamageSource.OUT_OF_WORLD || damageSource.isCreativePlayer()) {
                return super.hurt(damageSource, f);
            }
            return false;
        }
        if (this.isInvulnerableTo(damageSource)) {
            return false;
        }
        if (!damageSource.isExplosion() && !this.getItem().isEmpty()) {
            if (!this.level.isClientSide) {
                this.dropItem(damageSource.getEntity(), false);
                this.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM, 1.0f, 1.0f);
            }
            return true;
        }
        return super.hurt(damageSource, f);
    }

    @Override
    public int getWidth() {
        return 12;
    }

    @Override
    public int getHeight() {
        return 12;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double d) {
        double d2 = 16.0;
        return d < (d2 *= 64.0 * ItemFrame.getViewScale()) * d2;
    }

    @Override
    public void dropItem(@Nullable Entity entity) {
        this.playSound(SoundEvents.ITEM_FRAME_BREAK, 1.0f, 1.0f);
        this.dropItem(entity, true);
    }

    @Override
    public void playPlacementSound() {
        this.playSound(SoundEvents.ITEM_FRAME_PLACE, 1.0f, 1.0f);
    }

    private void dropItem(@Nullable Entity entity, boolean bl) {
        if (this.fixed) {
            return;
        }
        ItemStack itemStack = this.getItem();
        this.setItem(ItemStack.EMPTY);
        if (!this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            if (entity == null) {
                this.removeFramedMap(itemStack);
            }
            return;
        }
        if (entity instanceof Player) {
            Player player = (Player)entity;
            if (player.abilities.instabuild) {
                this.removeFramedMap(itemStack);
                return;
            }
        }
        if (bl) {
            this.spawnAtLocation(Items.ITEM_FRAME);
        }
        if (!itemStack.isEmpty()) {
            itemStack = itemStack.copy();
            this.removeFramedMap(itemStack);
            if (this.random.nextFloat() < this.dropChance) {
                this.spawnAtLocation(itemStack);
            }
        }
    }

    private void removeFramedMap(ItemStack itemStack) {
        if (itemStack.getItem() == Items.FILLED_MAP) {
            MapItemSavedData mapItemSavedData = MapItem.getOrCreateSavedData(itemStack, this.level);
            mapItemSavedData.removedFromFrame(this.pos, this.getId());
            mapItemSavedData.setDirty(true);
        }
        itemStack.setEntityRepresentation(null);
    }

    public ItemStack getItem() {
        return this.getEntityData().get(DATA_ITEM);
    }

    public void setItem(ItemStack itemStack) {
        this.setItem(itemStack, true);
    }

    public void setItem(ItemStack itemStack, boolean bl) {
        if (!itemStack.isEmpty()) {
            itemStack = itemStack.copy();
            itemStack.setCount(1);
            itemStack.setEntityRepresentation(this);
        }
        this.getEntityData().set(DATA_ITEM, itemStack);
        if (!itemStack.isEmpty()) {
            this.playSound(SoundEvents.ITEM_FRAME_ADD_ITEM, 1.0f, 1.0f);
        }
        if (bl && this.pos != null) {
            this.level.updateNeighbourForOutputSignal(this.pos, Blocks.AIR);
        }
    }

    @Override
    public boolean setSlot(int n, ItemStack itemStack) {
        if (n == 0) {
            this.setItem(itemStack);
            return true;
        }
        return false;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
        ItemStack itemStack;
        if (entityDataAccessor.equals(DATA_ITEM) && !(itemStack = this.getItem()).isEmpty() && itemStack.getFrame() != this) {
            itemStack.setEntityRepresentation(this);
        }
    }

    public int getRotation() {
        return this.getEntityData().get(DATA_ROTATION);
    }

    public void setRotation(int n) {
        this.setRotation(n, true);
    }

    private void setRotation(int n, boolean bl) {
        this.getEntityData().set(DATA_ROTATION, n % 8);
        if (bl && this.pos != null) {
            this.level.updateNeighbourForOutputSignal(this.pos, Blocks.AIR);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        if (!this.getItem().isEmpty()) {
            compoundTag.put("Item", this.getItem().save(new CompoundTag()));
            compoundTag.putByte("ItemRotation", (byte)this.getRotation());
            compoundTag.putFloat("ItemDropChance", this.dropChance);
        }
        compoundTag.putByte("Facing", (byte)this.direction.get3DDataValue());
        compoundTag.putBoolean("Invisible", this.isInvisible());
        compoundTag.putBoolean("Fixed", this.fixed);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        CompoundTag compoundTag2 = compoundTag.getCompound("Item");
        if (compoundTag2 != null && !compoundTag2.isEmpty()) {
            ItemStack itemStack;
            ItemStack itemStack2 = ItemStack.of(compoundTag2);
            if (itemStack2.isEmpty()) {
                LOGGER.warn("Unable to load item from: {}", (Object)compoundTag2);
            }
            if (!(itemStack = this.getItem()).isEmpty() && !ItemStack.matches(itemStack2, itemStack)) {
                this.removeFramedMap(itemStack);
            }
            this.setItem(itemStack2, false);
            this.setRotation(compoundTag.getByte("ItemRotation"), false);
            if (compoundTag.contains("ItemDropChance", 99)) {
                this.dropChance = compoundTag.getFloat("ItemDropChance");
            }
        }
        this.setDirection(Direction.from3DDataValue(compoundTag.getByte("Facing")));
        this.setInvisible(compoundTag.getBoolean("Invisible"));
        this.fixed = compoundTag.getBoolean("Fixed");
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand interactionHand) {
        boolean bl;
        ItemStack itemStack = player.getItemInHand(interactionHand);
        boolean bl2 = !this.getItem().isEmpty();
        boolean bl3 = bl = !itemStack.isEmpty();
        if (this.fixed) {
            return InteractionResult.PASS;
        }
        if (this.level.isClientSide) {
            return bl2 || bl ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }
        if (!bl2) {
            if (bl && !this.removed) {
                this.setItem(itemStack);
                if (!player.abilities.instabuild) {
                    itemStack.shrink(1);
                }
            }
        } else {
            this.playSound(SoundEvents.ITEM_FRAME_ROTATE_ITEM, 1.0f, 1.0f);
            this.setRotation(this.getRotation() + 1);
        }
        return InteractionResult.CONSUME;
    }

    public int getAnalogOutput() {
        if (this.getItem().isEmpty()) {
            return 0;
        }
        return this.getRotation() % 8 + 1;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this, this.getType(), this.direction.get3DDataValue(), this.getPos());
    }

}

