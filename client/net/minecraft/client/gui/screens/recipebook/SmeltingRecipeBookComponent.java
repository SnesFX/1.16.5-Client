/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.screens.recipebook;

import java.util.Set;
import net.minecraft.client.gui.screens.recipebook.AbstractFurnaceRecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

public class SmeltingRecipeBookComponent
extends AbstractFurnaceRecipeBookComponent {
    private static final Component FILTER_NAME = new TranslatableComponent("gui.recipebook.toggleRecipes.smeltable");

    @Override
    protected Component getRecipeFilterName() {
        return FILTER_NAME;
    }

    @Override
    protected Set<Item> getFuelItems() {
        return AbstractFurnaceBlockEntity.getFuel().keySet();
    }
}

