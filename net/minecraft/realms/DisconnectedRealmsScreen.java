/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.realms;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.ClientPackSource;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.realms.NarrationHelper;
import net.minecraft.realms.RealmsScreen;

public class DisconnectedRealmsScreen
extends RealmsScreen {
    private final Component title;
    private final Component reason;
    private MultiLineLabel message = MultiLineLabel.EMPTY;
    private final Screen parent;
    private int textHeight;

    public DisconnectedRealmsScreen(Screen screen, Component component, Component component2) {
        this.parent = screen;
        this.title = component;
        this.reason = component2;
    }

    @Override
    public void init() {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.setConnectedToRealms(false);
        minecraft.getClientPackSource().clearServerPack();
        NarrationHelper.now(this.title.getString() + ": " + this.reason.getString());
        this.message = MultiLineLabel.create(this.font, (FormattedText)this.reason, this.width - 50);
        this.font.getClass();
        this.textHeight = this.message.getLineCount() * 9;
        this.font.getClass();
        this.addButton(new Button(this.width / 2 - 100, this.height / 2 + this.textHeight / 2 + 9, 200, 20, CommonComponents.GUI_BACK, button -> minecraft.setScreen(this.parent)));
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(this.parent);
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderBackground(poseStack);
        this.font.getClass();
        DisconnectedRealmsScreen.drawCenteredString(poseStack, this.font, this.title, this.width / 2, this.height / 2 - this.textHeight / 2 - 9 * 2, 11184810);
        this.message.renderCentered(poseStack, this.width / 2, this.height / 2 - this.textHeight / 2);
        super.render(poseStack, n, n2, f);
    }
}

