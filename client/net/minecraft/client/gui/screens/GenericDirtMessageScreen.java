/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class GenericDirtMessageScreen
extends Screen {
    public GenericDirtMessageScreen(Component component) {
        super(component);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderDirtBackground(0);
        GenericDirtMessageScreen.drawCenteredString(poseStack, this.font, this.title, this.width / 2, 70, 16777215);
        super.render(poseStack, n, n2, f);
    }
}

