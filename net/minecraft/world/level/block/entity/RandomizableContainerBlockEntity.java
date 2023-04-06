/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block.entity;

import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.LootTableTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public abstract class RandomizableContainerBlockEntity
extends BaseContainerBlockEntity {
    @Nullable
    protected ResourceLocation lootTable;
    protected long lootTableSeed;

    protected RandomizableContainerBlockEntity(BlockEntityType<?> blockEntityType) {
        super(blockEntityType);
    }

    public static void setLootTable(BlockGetter blockGetter, Random random, BlockPos blockPos, ResourceLocation resourceLocation) {
        BlockEntity blockEntity = blockGetter.getBlockEntity(blockPos);
        if (blockEntity instanceof RandomizableContainerBlockEntity) {
            ((RandomizableContainerBlockEntity)blockEntity).setLootTable(resourceLocation, random.nextLong());
        }
    }

    protected boolean tryLoadLootTable(CompoundTag compoundTag) {
        if (compoundTag.contains("LootTable", 8)) {
            this.lootTable = new ResourceLocation(compoundTag.getString("LootTable"));
            this.lootTableSeed = compoundTag.getLong("LootTableSeed");
            return true;
        }
        return false;
    }

    protected boolean trySaveLootTable(CompoundTag compoundTag) {
        if (this.lootTable == null) {
            return false;
        }
        compoundTag.putString("LootTable", this.lootTable.toString());
        if (this.lootTableSeed != 0L) {
            compoundTag.putLong("LootTableSeed", this.lootTableSeed);
        }
        return true;
    }

    public void unpackLootTable(@Nullable Player player) {
        if (this.lootTable != null && this.level.getServer() != null) {
            LootTable lootTable = this.level.getServer().getLootTables().get(this.lootTable);
            if (player instanceof ServerPlayer) {
                CriteriaTriggers.GENERATE_LOOT.trigger((ServerPlayer)player, this.lootTable);
            }
            this.lootTable = null;
            LootContext.Builder builder = new LootContext.Builder((ServerLevel)this.level).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(this.worldPosition)).withOptionalRandomSeed(this.lootTableSeed);
            if (player != null) {
                builder.withLuck(player.getLuck()).withParameter(LootContextParams.THIS_ENTITY, player);
            }
            lootTable.fill(this, builder.create(LootContextParamSets.CHEST));
        }
    }

    public void setLootTable(ResourceLocation resourceLocation, long l) {
        this.lootTable = resourceLocation;
        this.lootTableSeed = l;
    }

    @Override
    public boolean isEmpty() {
        this.unpackLootTable(null);
        return this.getItems().stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int n) {
        this.unpackLootTable(null);
        return this.getItems().get(n);
    }

    @Override
    public ItemStack removeItem(int n, int n2) {
        this.unpackLootTable(null);
        ItemStack itemStack = ContainerHelper.removeItem(this.getItems(), n, n2);
        if (!itemStack.isEmpty()) {
            this.setChanged();
        }
        return itemStack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int n) {
        this.unpackLootTable(null);
        return ContainerHelper.takeItem(this.getItems(), n);
    }

    @Override
    public void setItem(int n, ItemStack itemStack) {
        this.unpackLootTable(null);
        this.getItems().set(n, itemStack);
        if (itemStack.getCount() > this.getMaxStackSize()) {
            itemStack.setCount(this.getMaxStackSize());
        }
        this.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        }
        return !(player.distanceToSqr((double)this.worldPosition.getX() + 0.5, (double)this.worldPosition.getY() + 0.5, (double)this.worldPosition.getZ() + 0.5) > 64.0);
    }

    @Override
    public void clearContent() {
        this.getItems().clear();
    }

    protected abstract NonNullList<ItemStack> getItems();

    protected abstract void setItems(NonNullList<ItemStack> var1);

    @Override
    public boolean canOpen(Player player) {
        return super.canOpen(player) && (this.lootTable == null || !player.isSpectator());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int n, Inventory inventory, Player player) {
        if (this.canOpen(player)) {
            this.unpackLootTable(inventory.player);
            return this.createMenu(n, inventory);
        }
        return null;
    }
}

