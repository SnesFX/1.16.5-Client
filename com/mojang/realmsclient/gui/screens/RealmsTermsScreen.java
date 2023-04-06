/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.util.task.GetServerDetailsTask;
import com.mojang.realmsclient.util.task.LongRunningTask;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.Util;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsTermsScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Component TITLE = new TranslatableComponent("mco.terms.title");
    private static final Component TERMS_STATIC_TEXT = new TranslatableComponent("mco.terms.sentence.1");
    private static final Component TERMS_LINK_TEXT = new TextComponent(" ").append(new TranslatableComponent("mco.terms.sentence.2").withStyle(Style.EMPTY.withUnderlined(true)));
    private final Screen lastScreen;
    private final RealmsMainScreen mainScreen;
    private final RealmsServer realmsServer;
    private boolean onLink;
    private final String realmsToSUrl = "https://aka.ms/MinecraftRealmsTerms";

    public RealmsTermsScreen(Screen screen, RealmsMainScreen realmsMainScreen, RealmsServer realmsServer) {
        this.lastScreen = screen;
        this.mainScreen = realmsMainScreen;
        this.realmsServer = realmsServer;
    }

    @Override
    public void init() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        int n = this.width / 4 - 2;
        this.addButton(new Button(this.width / 4, RealmsTermsScreen.row(12), n, 20, new TranslatableComponent("mco.terms.buttons.agree"), button -> this.agreedToTos()));
        this.addButton(new Button(this.width / 2 + 4, RealmsTermsScreen.row(12), n, 20, new TranslatableComponent("mco.terms.buttons.disagree"), button -> this.minecraft.setScreen(this.lastScreen)));
    }

    @Override
    public void removed() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }

    @Override
    public boolean keyPressed(int n, int n2, int n3) {
        if (n == 256) {
            this.minecraft.setScreen(this.lastScreen);
            return true;
        }
        return super.keyPressed(n, n2, n3);
    }

    private void agreedToTos() {
        RealmsClient realmsClient = RealmsClient.create();
        try {
            realmsClient.agreeToTos();
            this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new GetServerDetailsTask(this.mainScreen, this.lastScreen, this.realmsServer, new ReentrantLock())));
        }
        catch (RealmsServiceException realmsServiceException) {
            LOGGER.error("Couldn't agree to TOS");
        }
    }

    @Override
    public boolean mouseClicked(double d, double d2, int n) {
        if (this.onLink) {
            this.minecraft.keyboardHandler.setClipboard("https://aka.ms/MinecraftRealmsTerms");
            Util.getPlatform().openUri("https://aka.ms/MinecraftRealmsTerms");
            return true;
        }
        return super.mouseClicked(d, d2, n);
    }

    @Override
    public String getNarrationMessage() {
        return super.getNarrationMessage() + ". " + TERMS_STATIC_TEXT.getString() + " " + TERMS_LINK_TEXT.getString();
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderBackground(poseStack);
        RealmsTermsScreen.drawCenteredString(poseStack, this.font, TITLE, this.width / 2, 17, 16777215);
        this.font.draw(poseStack, TERMS_STATIC_TEXT, (float)(this.width / 2 - 120), (float)RealmsTermsScreen.row(5), 16777215);
        int n3 = this.font.width(TERMS_STATIC_TEXT);
        int n4 = this.width / 2 - 121 + n3;
        int n5 = RealmsTermsScreen.row(5);
        int n6 = n4 + this.font.width(TERMS_LINK_TEXT) + 1;
        this.font.getClass();
        int n7 = n5 + 1 + 9;
        this.onLink = n4 <= n && n <= n6 && n5 <= n2 && n2 <= n7;
        this.font.draw(poseStack, TERMS_LINK_TEXT, (float)(this.width / 2 - 120 + n3), (float)RealmsTermsScreen.row(5), this.onLink ? 7107012 : 3368635);
        super.render(poseStack, n, n2, f);
    }
}

