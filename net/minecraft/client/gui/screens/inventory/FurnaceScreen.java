/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.gui.screens.recipebook.AbstractFurnaceRecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.SmeltingRecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.FurnaceMenu;

public class FurnaceScreen
extends AbstractFurnaceScreen<FurnaceMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/furnace.png");

    public FurnaceScreen(FurnaceMenu furnaceMenu, Inventory inventory, Component component) {
        super(furnaceMenu, new SmeltingRecipeBookComponent(), inventory, component, TEXTURE);
    }
}

