/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block.entity;

import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HopperBlockEntity
extends RandomizableContainerBlockEntity
implements Hopper,
TickableBlockEntity {
    private NonNullList<ItemStack> items = NonNullList.withSize(5, ItemStack.EMPTY);
    private int cooldownTime = -1;
    private long tickedGameTime;

    public HopperBlockEntity() {
        super(BlockEntityType.HOPPER);
    }

    @Override
    public void load(BlockState blockState, CompoundTag compoundTag) {
        super.load(blockState, compoundTag);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(compoundTag)) {
            ContainerHelper.loadAllItems(compoundTag, this.items);
        }
        this.cooldownTime = compoundTag.getInt("TransferCooldown");
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        super.save(compoundTag);
        if (!this.trySaveLootTable(compoundTag)) {
            ContainerHelper.saveAllItems(compoundTag, this.items);
        }
        compoundTag.putInt("TransferCooldown", this.cooldownTime);
        return compoundTag;
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    public ItemStack removeItem(int n, int n2) {
        this.unpackLootTable(null);
        return ContainerHelper.removeItem(this.getItems(), n, n2);
    }

    @Override
    public void setItem(int n, ItemStack itemStack) {
        this.unpackLootTable(null);
        this.getItems().set(n, itemStack);
        if (itemStack.getCount() > this.getMaxStackSize()) {
            itemStack.setCount(this.getMaxStackSize());
        }
    }

    @Override
    protected Component getDefaultName() {
        return new TranslatableComponent("container.hopper");
    }

    @Override
    public void tick() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }
        --this.cooldownTime;
        this.tickedGameTime = this.level.getGameTime();
        if (!this.isOnCooldown()) {
            this.setCooldown(0);
            this.tryMoveItems(() -> HopperBlockEntity.suckInItems(this));
        }
    }

    private boolean tryMoveItems(Supplier<Boolean> supplier) {
        if (this.level == null || this.level.isClientSide) {
            return false;
        }
        if (!this.isOnCooldown() && this.getBlockState().getValue(HopperBlock.ENABLED).booleanValue()) {
            boolean bl = false;
            if (!this.isEmpty()) {
                bl = this.ejectItems();
            }
            if (!this.inventoryFull()) {
                bl |= supplier.get().booleanValue();
            }
            if (bl) {
                this.setCooldown(8);
                this.setChanged();
                return true;
            }
        }
        return false;
    }

    private boolean inventoryFull() {
        for (ItemStack itemStack : this.items) {
            if (!itemStack.isEmpty() && itemStack.getCount() == itemStack.getMaxStackSize()) continue;
            return false;
        }
        return true;
    }

    private boolean ejectItems() {
        Container container = this.getAttachedContainer();
        if (container == null) {
            return false;
        }
        Direction direction = this.getBlockState().getValue(HopperBlock.FACING).getOpposite();
        if (this.isFullContainer(container, direction)) {
            return false;
        }
        for (int i = 0; i < this.getContainerSize(); ++i) {
            if (this.getItem(i).isEmpty()) continue;
            ItemStack itemStack = this.getItem(i).copy();
            ItemStack itemStack2 = HopperBlockEntity.addItem(this, container, this.removeItem(i, 1), direction);
            if (itemStack2.isEmpty()) {
                container.setChanged();
                return true;
            }
            this.setItem(i, itemStack);
        }
        return false;
    }

    private static IntStream getSlots(Container container, Direction direction) {
        if (container instanceof WorldlyContainer) {
            return IntStream.of(((WorldlyContainer)container).getSlotsForFace(direction));
        }
        return IntStream.range(0, container.getContainerSize());
    }

    private boolean isFullContainer(Container container, Direction direction) {
        return HopperBlockEntity.getSlots(container, direction).allMatch(n -> {
            ItemStack itemStack = container.getItem(n);
            return itemStack.getCount() >= itemStack.getMaxStackSize();
        });
    }

    private static boolean isEmptyContainer(Container container, Direction direction) {
        return HopperBlockEntity.getSlots(container, direction).allMatch(n -> container.getItem(n).isEmpty());
    }

    public static boolean suckInItems(Hopper hopper) {
        Container container = HopperBlockEntity.getSourceContainer(hopper);
        if (container != null) {
            Direction direction = Direction.DOWN;
            if (HopperBlockEntity.isEmptyContainer(container, direction)) {
                return false;
            }
            return HopperBlockEntity.getSlots(container, direction).anyMatch(n -> HopperBlockEntity.tryTakeInItemFromSlot(hopper, container, n, direction));
        }
        for (ItemEntity itemEntity : HopperBlockEntity.getItemsAtAndAbove(hopper)) {
            if (!HopperBlockEntity.addItem(hopper, itemEntity)) continue;
            return true;
        }
        return false;
    }

    private static boolean tryTakeInItemFromSlot(Hopper hopper, Container container, int n, Direction direction) {
        ItemStack itemStack = container.getItem(n);
        if (!itemStack.isEmpty() && HopperBlockEntity.canTakeItemFromContainer(container, itemStack, n, direction)) {
            ItemStack itemStack2 = itemStack.copy();
            ItemStack itemStack3 = HopperBlockEntity.addItem(container, hopper, container.removeItem(n, 1), null);
            if (itemStack3.isEmpty()) {
                container.setChanged();
                return true;
            }
            container.setItem(n, itemStack2);
        }
        return false;
    }

    public static boolean addItem(Container container, ItemEntity itemEntity) {
        boolean bl = false;
        ItemStack itemStack = itemEntity.getItem().copy();
        ItemStack itemStack2 = HopperBlockEntity.addItem(null, container, itemStack, null);
        if (itemStack2.isEmpty()) {
            bl = true;
            itemEntity.remove();
        } else {
            itemEntity.setItem(itemStack2);
        }
        return bl;
    }

    public static ItemStack addItem(@Nullable Container container, Container container2, ItemStack itemStack, @Nullable Direction direction) {
        if (container2 instanceof WorldlyContainer && direction != null) {
            WorldlyContainer worldlyContainer = (WorldlyContainer)container2;
            int[] arrn = worldlyContainer.getSlotsForFace(direction);
            for (int i = 0; i < arrn.length && !itemStack.isEmpty(); ++i) {
                itemStack = HopperBlockEntity.tryMoveInItem(container, container2, itemStack, arrn[i], direction);
            }
        } else {
            int n = container2.getContainerSize();
            for (int i = 0; i < n && !itemStack.isEmpty(); ++i) {
                itemStack = HopperBlockEntity.tryMoveInItem(container, container2, itemStack, i, direction);
            }
        }
        return itemStack;
    }

    private static boolean canPlaceItemInContainer(Container container, ItemStack itemStack, int n, @Nullable Direction direction) {
        if (!container.canPlaceItem(n, itemStack)) {
            return false;
        }
        return !(container instanceof WorldlyContainer) || ((WorldlyContainer)container).canPlaceItemThroughFace(n, itemStack, direction);
    }

    private static boolean canTakeItemFromContainer(Container container, ItemStack itemStack, int n, Direction direction) {
        return !(container instanceof WorldlyContainer) || ((WorldlyContainer)container).canTakeItemThroughFace(n, itemStack, direction);
    }

    private static ItemStack tryMoveInItem(@Nullable Container container, Container container2, ItemStack itemStack, int n, @Nullable Direction direction) {
        ItemStack itemStack2 = container2.getItem(n);
        if (HopperBlockEntity.canPlaceItemInContainer(container2, itemStack, n, direction)) {
            int n2;
            boolean bl = false;
            boolean bl2 = container2.isEmpty();
            if (itemStack2.isEmpty()) {
                container2.setItem(n, itemStack);
                itemStack = ItemStack.EMPTY;
                bl = true;
            } else if (HopperBlockEntity.canMergeItems(itemStack2, itemStack)) {
                int n3 = itemStack.getMaxStackSize() - itemStack2.getCount();
                n2 = Math.min(itemStack.getCount(), n3);
                itemStack.shrink(n2);
                itemStack2.grow(n2);
                boolean bl3 = bl = n2 > 0;
            }
            if (bl) {
                HopperBlockEntity hopperBlockEntity;
                if (bl2 && container2 instanceof HopperBlockEntity && !(hopperBlockEntity = (HopperBlockEntity)container2).isOnCustomCooldown()) {
                    n2 = 0;
                    if (container instanceof HopperBlockEntity) {
                        HopperBlockEntity hopperBlockEntity2 = (HopperBlockEntity)container;
                        if (hopperBlockEntity.tickedGameTime >= hopperBlockEntity2.tickedGameTime) {
                            n2 = 1;
                        }
                    }
                    hopperBlockEntity.setCooldown(8 - n2);
                }
                container2.setChanged();
            }
        }
        return itemStack;
    }

    @Nullable
    private Container getAttachedContainer() {
        Direction direction = this.getBlockState().getValue(HopperBlock.FACING);
        return HopperBlockEntity.getContainerAt(this.getLevel(), this.worldPosition.relative(direction));
    }

    @Nullable
    public static Container getSourceContainer(Hopper hopper) {
        return HopperBlockEntity.getContainerAt(hopper.getLevel(), hopper.getLevelX(), hopper.getLevelY() + 1.0, hopper.getLevelZ());
    }

    public static List<ItemEntity> getItemsAtAndAbove(Hopper hopper) {
        return hopper.getSuckShape().toAabbs().stream().flatMap(aABB -> hopper.getLevel().getEntitiesOfClass(ItemEntity.class, aABB.move(hopper.getLevelX() - 0.5, hopper.getLevelY() - 0.5, hopper.getLevelZ() - 0.5), EntitySelector.ENTITY_STILL_ALIVE).stream()).collect(Collectors.toList());
    }

    @Nullable
    public static Container getContainerAt(Level level, BlockPos blockPos) {
        return HopperBlockEntity.getContainerAt(level, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5);
    }

    @Nullable
    public static Container getContainerAt(Level level, double d, double d2, double d3) {
        Object object;
        Container container = null;
        BlockPos blockPos = new BlockPos(d, d2, d3);
        BlockState blockState = level.getBlockState(blockPos);
        Block block = blockState.getBlock();
        if (block instanceof WorldlyContainerHolder) {
            container = ((WorldlyContainerHolder)((Object)block)).getContainer(blockState, level, blockPos);
        } else if (block.isEntityBlock() && (object = level.getBlockEntity(blockPos)) instanceof Container && (container = (Container)object) instanceof ChestBlockEntity && block instanceof ChestBlock) {
            container = ChestBlock.getContainer((ChestBlock)block, blockState, level, blockPos, true);
        }
        if (container == null && !(object = level.getEntities((Entity)null, new AABB(d - 0.5, d2 - 0.5, d3 - 0.5, d + 0.5, d2 + 0.5, d3 + 0.5), EntitySelector.CONTAINER_ENTITY_SELECTOR)).isEmpty()) {
            container = (Container)object.get(level.random.nextInt(object.size()));
        }
        return container;
    }

    private static boolean canMergeItems(ItemStack itemStack, ItemStack itemStack2) {
        if (itemStack.getItem() != itemStack2.getItem()) {
            return false;
        }
        if (itemStack.getDamageValue() != itemStack2.getDamageValue()) {
            return false;
        }
        if (itemStack.getCount() > itemStack.getMaxStackSize()) {
            return false;
        }
        return ItemStack.tagMatches(itemStack, itemStack2);
    }

    @Override
    public double getLevelX() {
        return (double)this.worldPosition.getX() + 0.5;
    }

    @Override
    public double getLevelY() {
        return (double)this.worldPosition.getY() + 0.5;
    }

    @Override
    public double getLevelZ() {
        return (double)this.worldPosition.getZ() + 0.5;
    }

    private void setCooldown(int n) {
        this.cooldownTime = n;
    }

    private boolean isOnCooldown() {
        return this.cooldownTime > 0;
    }

    private boolean isOnCustomCooldown() {
        return this.cooldownTime > 8;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> nonNullList) {
        this.items = nonNullList;
    }

    public void entityInside(Entity entity) {
        if (entity instanceof ItemEntity) {
            BlockPos blockPos = this.getBlockPos();
            if (Shapes.joinIsNotEmpty(Shapes.create(entity.getBoundingBox().move(-blockPos.getX(), -blockPos.getY(), -blockPos.getZ())), this.getSuckShape(), BooleanOp.AND)) {
                this.tryMoveItems(() -> HopperBlockEntity.addItem(this, (ItemEntity)entity));
            }
        }
    }

    @Override
    protected AbstractContainerMenu createMenu(int n, Inventory inventory) {
        return new HopperMenu(n, inventory, this);
    }
}

