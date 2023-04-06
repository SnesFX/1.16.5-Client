/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.vehicle;

import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.LootTableTrigger;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractMinecartContainer
extends AbstractMinecart
implements Container,
MenuProvider {
    private NonNullList<ItemStack> itemStacks = NonNullList.withSize(36, ItemStack.EMPTY);
    private boolean dropEquipment = true;
    @Nullable
    private ResourceLocation lootTable;
    private long lootTableSeed;

    protected AbstractMinecartContainer(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    protected AbstractMinecartContainer(EntityType<?> entityType, double d, double d2, double d3, Level level) {
        super(entityType, level, d, d2, d3);
    }

    @Override
    public void destroy(DamageSource damageSource) {
        super.destroy(damageSource);
        if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            Entity entity;
            Containers.dropContents(this.level, this, (Container)this);
            if (!this.level.isClientSide && (entity = damageSource.getDirectEntity()) != null && entity.getType() == EntityType.PLAYER) {
                PiglinAi.angerNearbyPiglins((Player)entity, true);
            }
        }
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : this.itemStacks) {
            if (itemStack.isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int n) {
        this.unpackLootTable(null);
        return this.itemStacks.get(n);
    }

    @Override
    public ItemStack removeItem(int n, int n2) {
        this.unpackLootTable(null);
        return ContainerHelper.removeItem(this.itemStacks, n, n2);
    }

    @Override
    public ItemStack removeItemNoUpdate(int n) {
        this.unpackLootTable(null);
        ItemStack itemStack = this.itemStacks.get(n);
        if (itemStack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        this.itemStacks.set(n, ItemStack.EMPTY);
        return itemStack;
    }

    @Override
    public void setItem(int n, ItemStack itemStack) {
        this.unpackLootTable(null);
        this.itemStacks.set(n, itemStack);
        if (!itemStack.isEmpty() && itemStack.getCount() > this.getMaxStackSize()) {
            itemStack.setCount(this.getMaxStackSize());
        }
    }

    @Override
    public boolean setSlot(int n, ItemStack itemStack) {
        if (n >= 0 && n < this.getContainerSize()) {
            this.setItem(n, itemStack);
            return true;
        }
        return false;
    }

    @Override
    public void setChanged() {
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.removed) {
            return false;
        }
        return !(player.distanceToSqr(this) > 64.0);
    }

    @Nullable
    @Override
    public Entity changeDimension(ServerLevel serverLevel) {
        this.dropEquipment = false;
        return super.changeDimension(serverLevel);
    }

    @Override
    public void remove() {
        if (!this.level.isClientSide && this.dropEquipment) {
            Containers.dropContents(this.level, this, (Container)this);
        }
        super.remove();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        if (this.lootTable != null) {
            compoundTag.putString("LootTable", this.lootTable.toString());
            if (this.lootTableSeed != 0L) {
                compoundTag.putLong("LootTableSeed", this.lootTableSeed);
            }
        } else {
            ContainerHelper.saveAllItems(compoundTag, this.itemStacks);
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.itemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (compoundTag.contains("LootTable", 8)) {
            this.lootTable = new ResourceLocation(compoundTag.getString("LootTable"));
            this.lootTableSeed = compoundTag.getLong("LootTableSeed");
        } else {
            ContainerHelper.loadAllItems(compoundTag, this.itemStacks);
        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand interactionHand) {
        player.openMenu(this);
        if (!player.level.isClientSide) {
            PiglinAi.angerNearbyPiglins(player, true);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void applyNaturalSlowdown() {
        float f = 0.98f;
        if (this.lootTable == null) {
            int n = 15 - AbstractContainerMenu.getRedstoneSignalFromContainer(this);
            f += (float)n * 0.001f;
        }
        this.setDeltaMovement(this.getDeltaMovement().multiply(f, 0.0, f));
    }

    public void unpackLootTable(@Nullable Player player) {
        if (this.lootTable != null && this.level.getServer() != null) {
            LootTable lootTable = this.level.getServer().getLootTables().get(this.lootTable);
            if (player instanceof ServerPlayer) {
                CriteriaTriggers.GENERATE_LOOT.trigger((ServerPlayer)player, this.lootTable);
            }
            this.lootTable = null;
            LootContext.Builder builder = new LootContext.Builder((ServerLevel)this.level).withParameter(LootContextParams.ORIGIN, this.position()).withOptionalRandomSeed(this.lootTableSeed);
            if (player != null) {
                builder.withLuck(player.getLuck()).withParameter(LootContextParams.THIS_ENTITY, player);
            }
            lootTable.fill(this, builder.create(LootContextParamSets.CHEST));
        }
    }

    @Override
    public void clearContent() {
        this.unpackLootTable(null);
        this.itemStacks.clear();
    }

    public void setLootTable(ResourceLocation resourceLocation, long l) {
        this.lootTable = resourceLocation;
        this.lootTableSeed = l;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int n, Inventory inventory, Player player) {
        if (this.lootTable == null || !player.isSpectator()) {
            this.unpackLootTable(inventory.player);
            return this.createMenu(n, inventory);
        }
        return null;
    }

    protected abstract AbstractContainerMenu createMenu(int var1, Inventory var2);
}

