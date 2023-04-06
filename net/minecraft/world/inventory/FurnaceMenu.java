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
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;

public class FurnaceMenu
extends AbstractFurnaceMenu {
    public FurnaceMenu(int n, Inventory inventory) {
        super(MenuType.FURNACE, RecipeType.SMELTING, RecipeBookType.FURNACE, n, inventory);
    }

    public FurnaceMenu(int n, Inventory inventory, Container container, ContainerData containerData) {
        super(MenuType.FURNACE, RecipeType.SMELTING, RecipeBookType.FURNACE, n, inventory, container, containerData);
    }
}

