/*
 * Decompiled with CFR 0.146.
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsResetWorldScreen;
import com.mojang.realmsclient.util.task.LongRunningTask;
import com.mojang.realmsclient.util.task.WorldCreationTask;
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
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;

public class RealmsCreateRealmScreen
extends RealmsScreen {
    private static final Component NAME_LABEL = new TranslatableComponent("mco.configure.world.name");
    private static final Component DESCRIPTION_LABEL = new TranslatableComponent("mco.configure.world.description");
    private final RealmsServer server;
    private final RealmsMainScreen lastScreen;
    private EditBox nameBox;
    private EditBox descriptionBox;
    private Button createButton;
    private RealmsLabel createRealmLabel;

    public RealmsCreateRealmScreen(RealmsServer realmsServer, RealmsMainScreen realmsMainScreen) {
        this.server = realmsServer;
        this.lastScreen = realmsMainScreen;
    }

    @Override
    public void tick() {
        if (this.nameBox != null) {
            this.nameBox.tick();
        }
        if (this.descriptionBox != null) {
            this.descriptionBox.tick();
        }
    }

    @Override
    public void init() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.createButton = this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 120 + 17, 97, 20, new TranslatableComponent("mco.create.world"), button -> this.createWorld()));
        this.addButton(new Button(this.width / 2 + 5, this.height / 4 + 120 + 17, 95, 20, CommonComponents.GUI_CANCEL, button -> this.minecraft.setScreen(this.lastScreen)));
        this.createButton.active = false;
        this.nameBox = new EditBox(this.minecraft.font, this.width / 2 - 100, 65, 200, 20, null, new TranslatableComponent("mco.configure.world.name"));
        this.addWidget(this.nameBox);
        this.setInitialFocus(this.nameBox);
        this.descriptionBox = new EditBox(this.minecraft.font, this.width / 2 - 100, 115, 200, 20, null, new TranslatableComponent("mco.configure.world.description"));
        this.addWidget(this.descriptionBox);
        this.createRealmLabel = new RealmsLabel(new TranslatableComponent("mco.selectServer.create"), this.width / 2, 11, 16777215);
        this.addWidget(this.createRealmLabel);
        this.narrateLabels();
    }

    @Override
    public void removed() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }

    @Override
    public boolean charTyped(char c, int n) {
        boolean bl = super.charTyped(c, n);
        this.createButton.active = this.valid();
        return bl;
    }

    @Override
    public boolean keyPressed(int n, int n2, int n3) {
        if (n == 256) {
            this.minecraft.setScreen(this.lastScreen);
            return true;
        }
        boolean bl = super.keyPressed(n, n2, n3);
        this.createButton.active = this.valid();
        return bl;
    }

    private void createWorld() {
        if (this.valid()) {
            RealmsResetWorldScreen realmsResetWorldScreen = new RealmsResetWorldScreen(this.lastScreen, this.server, new TranslatableComponent("mco.selectServer.create"), new TranslatableComponent("mco.create.world.subtitle"), 10526880, new TranslatableComponent("mco.create.world.skip"), () -> this.minecraft.setScreen(this.lastScreen.newScreen()), () -> this.minecraft.setScreen(this.lastScreen.newScreen()));
            realmsResetWorldScreen.setResetTitle(new TranslatableComponent("mco.create.world.reset.title"));
            this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new WorldCreationTask(this.server.id, this.nameBox.getValue(), this.descriptionBox.getValue(), realmsResetWorldScreen)));
        }
    }

    private boolean valid() {
        return !this.nameBox.getValue().trim().isEmpty();
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderBackground(poseStack);
        this.createRealmLabel.render(this, poseStack);
        this.font.draw(poseStack, NAME_LABEL, (float)(this.width / 2 - 100), 52.0f, 10526880);
        this.font.draw(poseStack, DESCRIPTION_LABEL, (float)(this.width / 2 - 100), 102.0f, 10526880);
        if (this.nameBox != null) {
            this.nameBox.render(poseStack, n, n2, f);
        }
        if (this.descriptionBox != null) {
            this.descriptionBox.render(poseStack, n, n2, f);
        }
        super.render(poseStack, n, n2, f);
    }
}

