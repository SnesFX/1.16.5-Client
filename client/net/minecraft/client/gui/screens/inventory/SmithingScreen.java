/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.SmithingMenu;

public class SmithingScreen
extends ItemCombinerScreen<SmithingMenu> {
    private static final ResourceLocation SMITHING_LOCATION = new ResourceLocation("textures/gui/container/smithing.png");

    public SmithingScreen(SmithingMenu smithingMenu, Inventory inventory, Component component) {
        super(smithingMenu, inventory, component, SMITHING_LOCATION);
        this.titleLabelX = 60;
        this.titleLabelY = 18;
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int n, int n2) {
        RenderSystem.disableBlend();
        super.renderLabels(poseStack, n, n2);
    }
}

