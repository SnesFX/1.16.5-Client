/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.gui.screens.recipebook.AbstractFurnaceRecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.BlastingRecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.BlastFurnaceMenu;

public class BlastFurnaceScreen
extends AbstractFurnaceScreen<BlastFurnaceMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/blast_furnace.png");

    public BlastFurnaceScreen(BlastFurnaceMenu blastFurnaceMenu, Inventory inventory, Component component) {
        super(blastFurnaceMenu, new BlastingRecipeBookComponent(), inventory, component, TEXTURE);
    }
}

