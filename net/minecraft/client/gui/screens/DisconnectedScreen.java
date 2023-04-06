/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TranslatableComponent;

public class DisconnectedScreen
extends Screen {
    private final Component reason;
    private MultiLineLabel message = MultiLineLabel.EMPTY;
    private final Screen parent;
    private int textHeight;

    public DisconnectedScreen(Screen screen, Component component, Component component2) {
        super(component);
        this.parent = screen;
        this.reason = component2;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    protected void init() {
        this.message = MultiLineLabel.create(this.font, (FormattedText)this.reason, this.width - 50);
        this.font.getClass();
        this.textHeight = this.message.getLineCount() * 9;
        this.font.getClass();
        this.addButton(new Button(this.width / 2 - 100, Math.min(this.height / 2 + this.textHeight / 2 + 9, this.height - 30), 200, 20, new TranslatableComponent("gui.toMenu"), button -> this.minecraft.setScreen(this.parent)));
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderBackground(poseStack);
        this.font.getClass();
        DisconnectedScreen.drawCenteredString(poseStack, this.font, this.title, this.width / 2, this.height / 2 - this.textHeight / 2 - 9 * 2, 11184810);
        this.message.renderCentered(poseStack, this.width / 2, this.height / 2 - this.textHeight / 2);
        super.render(poseStack, n, n2, f);
    }
}

