/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.inventory;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.UpgradeRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SmithingMenu
extends ItemCombinerMenu {
    private final Level level;
    @Nullable
    private UpgradeRecipe selectedRecipe;
    private final List<UpgradeRecipe> recipes;

    public SmithingMenu(int n, Inventory inventory) {
        this(n, inventory, ContainerLevelAccess.NULL);
    }

    public SmithingMenu(int n, Inventory inventory, ContainerLevelAccess containerLevelAccess) {
        super(MenuType.SMITHING, n, inventory, containerLevelAccess);
        this.level = inventory.player.level;
        this.recipes = this.level.getRecipeManager().getAllRecipesFor(RecipeType.SMITHING);
    }

    @Override
    protected boolean isValidBlock(BlockState blockState) {
        return blockState.is(Blocks.SMITHING_TABLE);
    }

    @Override
    protected boolean mayPickup(Player player, boolean bl) {
        return this.selectedRecipe != null && this.selectedRecipe.matches(this.inputSlots, this.level);
    }

    @Override
    protected ItemStack onTake(Player player, ItemStack itemStack) {
        itemStack.onCraftedBy(player.level, player, itemStack.getCount());
        this.resultSlots.awardUsedRecipes(player);
        this.shrinkStackInSlot(0);
        this.shrinkStackInSlot(1);
        this.access.execute((level, blockPos) -> level.levelEvent(1044, (BlockPos)blockPos, 0));
        return itemStack;
    }

    private void shrinkStackInSlot(int n) {
        ItemStack itemStack = this.inputSlots.getItem(n);
        itemStack.shrink(1);
        this.inputSlots.setItem(n, itemStack);
    }

    @Override
    public void createResult() {
        List<UpgradeRecipe> list = this.level.getRecipeManager().getRecipesFor(RecipeType.SMITHING, this.inputSlots, this.level);
        if (list.isEmpty()) {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
        } else {
            this.selectedRecipe = list.get(0);
            ItemStack itemStack = this.selectedRecipe.assemble(this.inputSlots);
            this.resultSlots.setRecipeUsed(this.selectedRecipe);
            this.resultSlots.setItem(0, itemStack);
        }
    }

    @Override
    protected boolean shouldQuickMoveToAdditionalSlot(ItemStack itemStack) {
        return this.recipes.stream().anyMatch(upgradeRecipe -> upgradeRecipe.isAdditionIngredient(itemStack));
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack itemStack, Slot slot) {
        return slot.container != this.resultSlots && super.canTakeItemForPickAll(itemStack, slot);
    }
}

