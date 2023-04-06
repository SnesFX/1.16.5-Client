/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.block.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BlastFurnaceMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BlastFurnaceBlockEntity
extends AbstractFurnaceBlockEntity {
    public BlastFurnaceBlockEntity() {
        super(BlockEntityType.BLAST_FURNACE, RecipeType.BLASTING);
    }

    @Override
    protected Component getDefaultName() {
        return new TranslatableComponent("container.blast_furnace");
    }

    @Override
    protected int getBurnDuration(ItemStack itemStack) {
        return super.getBurnDuration(itemStack) / 2;
    }

    @Override
    protected AbstractContainerMenu createMenu(int n, Inventory inventory) {
        return new BlastFurnaceMenu(n, inventory, this, this.dataAccess);
    }
}

