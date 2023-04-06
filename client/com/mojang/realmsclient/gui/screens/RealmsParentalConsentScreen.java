/*
 * Decompiled with CFR 0.146.
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.NarrationHelper;
import net.minecraft.realms.RealmsScreen;

public class RealmsParentalConsentScreen
extends RealmsScreen {
    private static final Component MESSAGE = new TranslatableComponent("mco.account.privacyinfo");
    private final Screen nextScreen;
    private MultiLineLabel messageLines = MultiLineLabel.EMPTY;

    public RealmsParentalConsentScreen(Screen screen) {
        this.nextScreen = screen;
    }

    @Override
    public void init() {
        NarrationHelper.now(MESSAGE.getString());
        TranslatableComponent translatableComponent = new TranslatableComponent("mco.account.update");
        Component component = CommonComponents.GUI_BACK;
        int n = Math.max(this.font.width(translatableComponent), this.font.width(component)) + 30;
        TranslatableComponent translatableComponent2 = new TranslatableComponent("mco.account.privacy.info");
        int n2 = (int)((double)this.font.width(translatableComponent2) * 1.2);
        this.addButton(new Button(this.width / 2 - n2 / 2, RealmsParentalConsentScreen.row(11), n2, 20, translatableComponent2, button -> Util.getPlatform().openUri("https://aka.ms/MinecraftGDPR")));
        this.addButton(new Button(this.width / 2 - (n + 5), RealmsParentalConsentScreen.row(13), n, 20, translatableComponent, button -> Util.getPlatform().openUri("https://aka.ms/UpdateMojangAccount")));
        this.addButton(new Button(this.width / 2 + 5, RealmsParentalConsentScreen.row(13), n, 20, component, button -> this.minecraft.setScreen(this.nextScreen)));
        this.messageLines = MultiLineLabel.create(this.font, (FormattedText)MESSAGE, (int)Math.round((double)this.width * 0.9));
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderBackground(poseStack);
        this.messageLines.renderCentered(poseStack, this.width / 2, 15, 15, 16777215);
        super.render(poseStack, n, n2, f);
    }
}

