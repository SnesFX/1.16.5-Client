/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block.entity;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;

public class ShulkerBoxBlockEntity
extends RandomizableContainerBlockEntity
implements WorldlyContainer,
TickableBlockEntity {
    private static final int[] SLOTS = IntStream.range(0, 27).toArray();
    private NonNullList<ItemStack> itemStacks = NonNullList.withSize(27, ItemStack.EMPTY);
    private int openCount;
    private AnimationStatus animationStatus = AnimationStatus.CLOSED;
    private float progress;
    private float progressOld;
    @Nullable
    private DyeColor color;
    private boolean loadColorFromBlock;

    public ShulkerBoxBlockEntity(@Nullable DyeColor dyeColor) {
        super(BlockEntityType.SHULKER_BOX);
        this.color = dyeColor;
    }

    public ShulkerBoxBlockEntity() {
        this(null);
        this.loadColorFromBlock = true;
    }

    @Override
    public void tick() {
        this.updateAnimation();
        if (this.animationStatus == AnimationStatus.OPENING || this.animationStatus == AnimationStatus.CLOSING) {
            this.moveCollidedEntities();
        }
    }

    protected void updateAnimation() {
        this.progressOld = this.progress;
        switch (this.animationStatus) {
            case CLOSED: {
                this.progress = 0.0f;
                break;
            }
            case OPENING: {
                this.progress += 0.1f;
                if (!(this.progress >= 1.0f)) break;
                this.moveCollidedEntities();
                this.animationStatus = AnimationStatus.OPENED;
                this.progress = 1.0f;
                this.doNeighborUpdates();
                break;
            }
            case CLOSING: {
                this.progress -= 0.1f;
                if (!(this.progress <= 0.0f)) break;
                this.animationStatus = AnimationStatus.CLOSED;
                this.progress = 0.0f;
                this.doNeighborUpdates();
                break;
            }
            case OPENED: {
                this.progress = 1.0f;
            }
        }
    }

    public AnimationStatus getAnimationStatus() {
        return this.animationStatus;
    }

    public AABB getBoundingBox(BlockState blockState) {
        return this.getBoundingBox(blockState.getValue(ShulkerBoxBlock.FACING));
    }

    public AABB getBoundingBox(Direction direction) {
        float f = this.getProgress(1.0f);
        return Shapes.block().bounds().expandTowards(0.5f * f * (float)direction.getStepX(), 0.5f * f * (float)direction.getStepY(), 0.5f * f * (float)direction.getStepZ());
    }

    private AABB getTopBoundingBox(Direction direction) {
        Direction direction2 = direction.getOpposite();
        return this.getBoundingBox(direction).contract(direction2.getStepX(), direction2.getStepY(), direction2.getStepZ());
    }

    private void moveCollidedEntities() {
        BlockState blockState = this.level.getBlockState(this.getBlockPos());
        if (!(blockState.getBlock() instanceof ShulkerBoxBlock)) {
            return;
        }
        Direction direction = blockState.getValue(ShulkerBoxBlock.FACING);
        AABB aABB = this.getTopBoundingBox(direction).move(this.worldPosition);
        List<Entity> list = this.level.getEntities(null, aABB);
        if (list.isEmpty()) {
            return;
        }
        for (int i = 0; i < list.size(); ++i) {
            Entity entity = list.get(i);
            if (entity.getPistonPushReaction() == PushReaction.IGNORE) continue;
            double d = 0.0;
            double d2 = 0.0;
            double d3 = 0.0;
            AABB aABB2 = entity.getBoundingBox();
            switch (direction.getAxis()) {
                case X: {
                    d = direction.getAxisDirection() == Direction.AxisDirection.POSITIVE ? aABB.maxX - aABB2.minX : aABB2.maxX - aABB.minX;
                    d += 0.01;
                    break;
                }
                case Y: {
                    d2 = direction.getAxisDirection() == Direction.AxisDirection.POSITIVE ? aABB.maxY - aABB2.minY : aABB2.maxY - aABB.minY;
                    d2 += 0.01;
                    break;
                }
                case Z: {
                    d3 = direction.getAxisDirection() == Direction.AxisDirection.POSITIVE ? aABB.maxZ - aABB2.minZ : aABB2.maxZ - aABB.minZ;
                    d3 += 0.01;
                }
            }
            entity.move(MoverType.SHULKER_BOX, new Vec3(d * (double)direction.getStepX(), d2 * (double)direction.getStepY(), d3 * (double)direction.getStepZ()));
        }
    }

    @Override
    public int getContainerSize() {
        return this.itemStacks.size();
    }

    @Override
    public boolean triggerEvent(int n, int n2) {
        if (n == 1) {
            this.openCount = n2;
            if (n2 == 0) {
                this.animationStatus = AnimationStatus.CLOSING;
                this.doNeighborUpdates();
            }
            if (n2 == 1) {
                this.animationStatus = AnimationStatus.OPENING;
                this.doNeighborUpdates();
            }
            return true;
        }
        return super.triggerEvent(n, n2);
    }

    private void doNeighborUpdates() {
        this.getBlockState().updateNeighbourShapes(this.getLevel(), this.getBlockPos(), 3);
    }

    @Override
    public void startOpen(Player player) {
        if (!player.isSpectator()) {
            if (this.openCount < 0) {
                this.openCount = 0;
            }
            ++this.openCount;
            this.level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), 1, this.openCount);
            if (this.openCount == 1) {
                this.level.playSound(null, this.worldPosition, SoundEvents.SHULKER_BOX_OPEN, SoundSource.BLOCKS, 0.5f, this.level.random.nextFloat() * 0.1f + 0.9f);
            }
        }
    }

    @Override
    public void stopOpen(Player player) {
        if (!player.isSpectator()) {
            --this.openCount;
            this.level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), 1, this.openCount);
            if (this.openCount <= 0) {
                this.level.playSound(null, this.worldPosition, SoundEvents.SHULKER_BOX_CLOSE, SoundSource.BLOCKS, 0.5f, this.level.random.nextFloat() * 0.1f + 0.9f);
            }
        }
    }

    @Override
    protected Component getDefaultName() {
        return new TranslatableComponent("container.shulkerBox");
    }

    @Override
    public void load(BlockState blockState, CompoundTag compoundTag) {
        super.load(blockState, compoundTag);
        this.loadFromTag(compoundTag);
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        super.save(compoundTag);
        return this.saveToTag(compoundTag);
    }

    public void loadFromTag(CompoundTag compoundTag) {
        this.itemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(compoundTag) && compoundTag.contains("Items", 9)) {
            ContainerHelper.loadAllItems(compoundTag, this.itemStacks);
        }
    }

    public CompoundTag saveToTag(CompoundTag compoundTag) {
        if (!this.trySaveLootTable(compoundTag)) {
            ContainerHelper.saveAllItems(compoundTag, this.itemStacks, false);
        }
        return compoundTag;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.itemStacks;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> nonNullList) {
        this.itemStacks = nonNullList;
    }

    @Override
    public int[] getSlotsForFace(Direction direction) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int n, ItemStack itemStack, @Nullable Direction direction) {
        return !(Block.byItem(itemStack.getItem()) instanceof ShulkerBoxBlock);
    }

    @Override
    public boolean canTakeItemThroughFace(int n, ItemStack itemStack, Direction direction) {
        return true;
    }

    public float getProgress(float f) {
        return Mth.lerp(f, this.progressOld, this.progress);
    }

    @Nullable
    public DyeColor getColor() {
        if (this.loadColorFromBlock) {
            this.color = ShulkerBoxBlock.getColorFromBlock(this.getBlockState().getBlock());
            this.loadColorFromBlock = false;
        }
        return this.color;
    }

    @Override
    protected AbstractContainerMenu createMenu(int n, Inventory inventory) {
        return new ShulkerBoxMenu(n, inventory, this);
    }

    public boolean isClosed() {
        return this.animationStatus == AnimationStatus.CLOSED;
    }

    public static enum AnimationStatus {
        CLOSED,
        OPENING,
        OPENED,
        CLOSING;
        
    }

}

