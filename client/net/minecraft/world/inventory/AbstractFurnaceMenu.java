/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.inventory;

import java.util.List;
import java.util.Optional;
import net.minecraft.recipebook.ServerPlaceSmeltingRecipe;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.FurnaceFuelSlot;
import net.minecraft.world.inventory.FurnaceResultSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

public abstract class AbstractFurnaceMenu
extends RecipeBookMenu<Container> {
    private final Container container;
    private final ContainerData data;
    protected final Level level;
    private final RecipeType<? extends AbstractCookingRecipe> recipeType;
    private final RecipeBookType recipeBookType;

    protected AbstractFurnaceMenu(MenuType<?> menuType, RecipeType<? extends AbstractCookingRecipe> recipeType, RecipeBookType recipeBookType, int n, Inventory inventory) {
        this(menuType, recipeType, recipeBookType, n, inventory, new SimpleContainer(3), new SimpleContainerData(4));
    }

    protected AbstractFurnaceMenu(MenuType<?> menuType, RecipeType<? extends AbstractCookingRecipe> recipeType, RecipeBookType recipeBookType, int n, Inventory inventory, Container container, ContainerData containerData) {
        super(menuType, n);
        int n2;
        this.recipeType = recipeType;
        this.recipeBookType = recipeBookType;
        AbstractFurnaceMenu.checkContainerSize(container, 3);
        AbstractFurnaceMenu.checkContainerDataCount(containerData, 4);
        this.container = container;
        this.data = containerData;
        this.level = inventory.player.level;
        this.addSlot(new Slot(container, 0, 56, 17));
        this.addSlot(new FurnaceFuelSlot(this, container, 1, 56, 53));
        this.addSlot(new FurnaceResultSlot(inventory.player, container, 2, 116, 35));
        for (n2 = 0; n2 < 3; ++n2) {
            for (int i = 0; i < 9; ++i) {
                this.addSlot(new Slot(inventory, i + n2 * 9 + 9, 8 + i * 18, 84 + n2 * 18));
            }
        }
        for (n2 = 0; n2 < 9; ++n2) {
            this.addSlot(new Slot(inventory, n2, 8 + n2 * 18, 142));
        }
        this.addDataSlots(containerData);
    }

    @Override
    public void fillCraftSlotsStackedContents(StackedContents stackedContents) {
        if (this.container instanceof StackedContentsCompatible) {
            ((StackedContentsCompatible)((Object)this.container)).fillStackedContents(stackedContents);
        }
    }

    @Override
    public void clearCraftingContent() {
        this.container.clearContent();
    }

    @Override
    public void handlePlacement(boolean bl, Recipe<?> recipe, ServerPlayer serverPlayer) {
        new ServerPlaceSmeltingRecipe<Container>(this).recipeClicked(serverPlayer, recipe, bl);
    }

    @Override
    public boolean recipeMatches(Recipe<? super Container> recipe) {
        return recipe.matches(this.container, this.level);
    }

    @Override
    public int getResultSlotIndex() {
        return 2;
    }

    @Override
    public int getGridWidth() {
        return 1;
    }

    @Override
    public int getGridHeight() {
        return 1;
    }

    @Override
    public int getSize() {
        return 3;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int n) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(n);
        if (slot != null && slot.hasItem()) {
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.copy();
            if (n == 2) {
                if (!this.moveItemStackTo(itemStack2, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemStack2, itemStack);
            } else if (n == 1 || n == 0 ? !this.moveItemStackTo(itemStack2, 3, 39, false) : (this.canSmelt(itemStack2) ? !this.moveItemStackTo(itemStack2, 0, 1, false) : (this.isFuel(itemStack2) ? !this.moveItemStackTo(itemStack2, 1, 2, false) : (n >= 3 && n < 30 ? !this.moveItemStackTo(itemStack2, 30, 39, false) : n >= 30 && n < 39 && !this.moveItemStackTo(itemStack2, 3, 30, false))))) {
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
            slot.onTake(player, itemStack2);
        }
        return itemStack;
    }

    protected boolean canSmelt(ItemStack itemStack) {
        return this.level.getRecipeManager().getRecipeFor(this.recipeType, new SimpleContainer(itemStack), this.level).isPresent();
    }

    protected boolean isFuel(ItemStack itemStack) {
        return AbstractFurnaceBlockEntity.isFuel(itemStack);
    }

    public int getBurnProgress() {
        int n = this.data.get(2);
        int n2 = this.data.get(3);
        if (n2 == 0 || n == 0) {
            return 0;
        }
        return n * 24 / n2;
    }

    public int getLitProgress() {
        int n = this.data.get(1);
        if (n == 0) {
            n = 200;
        }
        return this.data.get(0) * 13 / n;
    }

    public boolean isLit() {
        return this.data.get(0) > 0;
    }

    @Override
    public RecipeBookType getRecipeBookType() {
        return this.recipeBookType;
    }
}

