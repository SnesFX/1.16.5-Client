/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.block.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class FurnaceBlockEntity
extends AbstractFurnaceBlockEntity {
    public FurnaceBlockEntity() {
        super(BlockEntityType.FURNACE, RecipeType.SMELTING);
    }

    @Override
    protected Component getDefaultName() {
        return new TranslatableComponent("container.furnace");
    }

    @Override
    protected AbstractContainerMenu createMenu(int n, Inventory inventory) {
        return new FurnaceMenu(n, inventory, this, this.dataAccess);
    }
}

