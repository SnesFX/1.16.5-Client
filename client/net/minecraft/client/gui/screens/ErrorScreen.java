/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ErrorScreen
extends Screen {
    private final Component message;

    public ErrorScreen(Component component, Component component2) {
        super(component);
        this.message = component2;
    }

    @Override
    protected void init() {
        super.init();
        this.addButton(new Button(this.width / 2 - 100, 140, 200, 20, CommonComponents.GUI_CANCEL, button -> this.minecraft.setScreen(null)));
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.fillGradient(poseStack, 0, 0, this.width, this.height, -12574688, -11530224);
        ErrorScreen.drawCenteredString(poseStack, this.font, this.title, this.width / 2, 90, 16777215);
        ErrorScreen.drawCenteredString(poseStack, this.font, this.message, this.width / 2, 110, 16777215);
        super.render(poseStack, n, n2, f);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}

