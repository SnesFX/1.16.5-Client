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
import net.minecraft.world.item.crafting.SmokingRecipe;

public class SmokerMenu
extends AbstractFurnaceMenu {
    public SmokerMenu(int n, Inventory inventory) {
        super(MenuType.SMOKER, RecipeType.SMOKING, RecipeBookType.SMOKER, n, inventory);
    }

    public SmokerMenu(int n, Inventory inventory, Container container, ContainerData containerData) {
        super(MenuType.SMOKER, RecipeType.SMOKING, RecipeBookType.SMOKER, n, inventory, container, containerData);
    }
}

