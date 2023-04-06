/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.inventory;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class CraftingMenu
extends RecipeBookMenu<CraftingContainer> {
    private final CraftingContainer craftSlots = new CraftingContainer(this, 3, 3);
    private final ResultContainer resultSlots = new ResultContainer();
    private final ContainerLevelAccess access;
    private final Player player;

    public CraftingMenu(int n, Inventory inventory) {
        this(n, inventory, ContainerLevelAccess.NULL);
    }

    public CraftingMenu(int n, Inventory inventory, ContainerLevelAccess containerLevelAccess) {
        super(MenuType.CRAFTING, n);
        int n2;
        int n3;
        this.access = containerLevelAccess;
        this.player = inventory.player;
        this.addSlot(new ResultSlot(inventory.player, this.craftSlots, this.resultSlots, 0, 124, 35));
        for (n3 = 0; n3 < 3; ++n3) {
            for (n2 = 0; n2 < 3; ++n2) {
                this.addSlot(new Slot(this.craftSlots, n2 + n3 * 3, 30 + n2 * 18, 17 + n3 * 18));
            }
        }
        for (n3 = 0; n3 < 3; ++n3) {
            for (n2 = 0; n2 < 9; ++n2) {
                this.addSlot(new Slot(inventory, n2 + n3 * 9 + 9, 8 + n2 * 18, 84 + n3 * 18));
            }
        }
        for (n3 = 0; n3 < 9; ++n3) {
            this.addSlot(new Slot(inventory, n3, 8 + n3 * 18, 142));
        }
    }

    protected static void slotChangedCraftingGrid(int n, Level level, Player player, CraftingContainer craftingContainer, ResultContainer resultContainer) {
        CraftingRecipe craftingRecipe;
        if (level.isClientSide) {
            return;
        }
        ServerPlayer serverPlayer = (ServerPlayer)player;
        ItemStack itemStack = ItemStack.EMPTY;
        Optional<CraftingRecipe> optional = level.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftingContainer, level);
        if (optional.isPresent() && resultContainer.setRecipeUsed(level, serverPlayer, craftingRecipe = optional.get())) {
            itemStack = craftingRecipe.assemble(craftingContainer);
        }
        resultContainer.setItem(0, itemStack);
        serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(n, 0, itemStack));
    }

    @Override
    public void slotsChanged(Container container) {
        this.access.execute((level, blockPos) -> CraftingMenu.slotChangedCraftingGrid(this.containerId, level, this.player, this.craftSlots, this.resultSlots));
    }

    @Override
    public void fillCraftSlotsStackedContents(StackedContents stackedContents) {
        this.craftSlots.fillStackedContents(stackedContents);
    }

    @Override
    public void clearCraftingContent() {
        this.craftSlots.clearContent();
        this.resultSlots.clearContent();
    }

    @Override
    public boolean recipeMatches(Recipe<? super CraftingContainer> recipe) {
        return recipe.matches(this.craftSlots, this.player.level);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.access.execute((level, blockPos) -> this.clearContainer(player, (Level)level, this.craftSlots));
    }

    @Override
    public boolean stillValid(Player player) {
        return CraftingMenu.stillValid(this.access, player, Blocks.CRAFTING_TABLE);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int n) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(n);
        if (slot != null && slot.hasItem()) {
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.copy();
            if (n == 0) {
                this.access.execute((level, blockPos) -> itemStack2.getItem().onCraftedBy(itemStack2, (Level)level, player));
                if (!this.moveItemStackTo(itemStack2, 10, 46, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemStack2, itemStack);
            } else if (n >= 10 && n < 46 ? !this.moveItemStackTo(itemStack2, 1, 10, false) && (n < 37 ? !this.moveItemStackTo(itemStack2, 37, 46, false) : !this.moveItemStackTo(itemStack2, 10, 37, false)) : !this.moveItemStackTo(itemStack2, 10, 46, false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            ItemStack itemStack3 = slot.onTake(player, itemStack2);
            if (n == 0) {
                player.drop(itemStack3, false);
            }
        }
        return itemStack;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack itemStack, Slot slot) {
        return slot.container != this.resultSlots && super.canTakeItemForPickAll(itemStack, slot);
    }

    @Override
    public int getResultSlotIndex() {
        return 0;
    }

    @Override
    public int getGridWidth() {
        return this.craftSlots.getWidth();
    }

    @Override
    public int getGridHeight() {
        return this.craftSlots.getHeight();
    }

    @Override
    public int getSize() {
        return 10;
    }

    @Override
    public RecipeBookType getRecipeBookType() {
        return RecipeBookType.CRAFTING;
    }
}

