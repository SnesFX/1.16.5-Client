/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.gui.screens.recipebook.AbstractFurnaceRecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.SmokingRecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.SmokerMenu;

public class SmokerScreen
extends AbstractFurnaceScreen<SmokerMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/smoker.png");

    public SmokerScreen(SmokerMenu smokerMenu, Inventory inventory, Component component) {
        super(smokerMenu, new SmokingRecipeBookComponent(), inventory, component, TEXTURE);
    }
}

