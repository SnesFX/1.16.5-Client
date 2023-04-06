/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.PlayerInfo;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsPlayerScreen;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.NarrationHelper;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsInviteScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Component NAME_LABEL = new TranslatableComponent("mco.configure.world.invite.profile.name");
    private static final Component NO_SUCH_PLAYER_ERROR_TEXT = new TranslatableComponent("mco.configure.world.players.error");
    private EditBox profileName;
    private final RealmsServer serverData;
    private final RealmsConfigureWorldScreen configureScreen;
    private final Screen lastScreen;
    @Nullable
    private Component errorMsg;

    public RealmsInviteScreen(RealmsConfigureWorldScreen realmsConfigureWorldScreen, Screen screen, RealmsServer realmsServer) {
        this.configureScreen = realmsConfigureWorldScreen;
        this.lastScreen = screen;
        this.serverData = realmsServer;
    }

    @Override
    public void tick() {
        this.profileName.tick();
    }

    @Override
    public void init() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.profileName = new EditBox(this.minecraft.font, this.width / 2 - 100, RealmsInviteScreen.row(2), 200, 20, null, new TranslatableComponent("mco.configure.world.invite.profile.name"));
        this.addWidget(this.profileName);
        this.setInitialFocus(this.profileName);
        this.addButton(new Button(this.width / 2 - 100, RealmsInviteScreen.row(10), 200, 20, new TranslatableComponent("mco.configure.world.buttons.invite"), button -> this.onInvite()));
        this.addButton(new Button(this.width / 2 - 100, RealmsInviteScreen.row(12), 200, 20, CommonComponents.GUI_CANCEL, button -> this.minecraft.setScreen(this.lastScreen)));
    }

    @Override
    public void removed() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }

    private void onInvite() {
        RealmsClient realmsClient = RealmsClient.create();
        if (this.profileName.getValue() == null || this.profileName.getValue().isEmpty()) {
            this.showError(NO_SUCH_PLAYER_ERROR_TEXT);
            return;
        }
        try {
            RealmsServer realmsServer = realmsClient.invite(this.serverData.id, this.profileName.getValue().trim());
            if (realmsServer != null) {
                this.serverData.players = realmsServer.players;
                this.minecraft.setScreen(new RealmsPlayerScreen(this.configureScreen, this.serverData));
            } else {
                this.showError(NO_SUCH_PLAYER_ERROR_TEXT);
            }
        }
        catch (Exception exception) {
            LOGGER.error("Couldn't invite user");
            this.showError(NO_SUCH_PLAYER_ERROR_TEXT);
        }
    }

    private void showError(Component component) {
        this.errorMsg = component;
        NarrationHelper.now(component.getString());
    }

    @Override
    public boolean keyPressed(int n, int n2, int n3) {
        if (n == 256) {
            this.minecraft.setScreen(this.lastScreen);
            return true;
        }
        return super.keyPressed(n, n2, n3);
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderBackground(poseStack);
        this.font.draw(poseStack, NAME_LABEL, (float)(this.width / 2 - 100), (float)RealmsInviteScreen.row(1), 10526880);
        if (this.errorMsg != null) {
            RealmsInviteScreen.drawCenteredString(poseStack, this.font, this.errorMsg, this.width / 2, RealmsInviteScreen.row(5), 16711680);
        }
        this.profileName.render(poseStack, n, n2, f);
        super.render(poseStack, n, n2, f);
    }
}

