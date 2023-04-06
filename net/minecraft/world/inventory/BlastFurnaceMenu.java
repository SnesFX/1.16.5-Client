/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.RecipeType;

public class BlastFurnaceMenu
extends AbstractFurnaceMenu {
    public BlastFurnaceMenu(int n, Inventory inventory) {
        super(MenuType.BLAST_FURNACE, RecipeType.BLASTING, RecipeBookType.BLAST_FURNACE, n, inventory);
    }

    public BlastFurnaceMenu(int n, Inventory inventory, Container container, ContainerData containerData) {
        super(MenuType.BLAST_FURNACE, RecipeType.BLASTING, RecipeBookType.BLAST_FURNACE, n, inventory, container, containerData);
    }
}

