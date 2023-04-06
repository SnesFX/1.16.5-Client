/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
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

public class RealmsSettingsScreen
extends RealmsScreen {
    private static final Component NAME_LABEL = new TranslatableComponent("mco.configure.world.name");
    private static final Component DESCRIPTION_LABEL = new TranslatableComponent("mco.configure.world.description");
    private final RealmsConfigureWorldScreen configureWorldScreen;
    private final RealmsServer serverData;
    private Button doneButton;
    private EditBox descEdit;
    private EditBox nameEdit;
    private RealmsLabel titleLabel;

    public RealmsSettingsScreen(RealmsConfigureWorldScreen realmsConfigureWorldScreen, RealmsServer realmsServer) {
        this.configureWorldScreen = realmsConfigureWorldScreen;
        this.serverData = realmsServer;
    }

    @Override
    public void tick() {
        this.nameEdit.tick();
        this.descEdit.tick();
        this.doneButton.active = !this.nameEdit.getValue().trim().isEmpty();
    }

    @Override
    public void init() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        int n = this.width / 2 - 106;
        this.doneButton = this.addButton(new Button(n - 2, RealmsSettingsScreen.row(12), 106, 20, new TranslatableComponent("mco.configure.world.buttons.done"), button -> this.save()));
        this.addButton(new Button(this.width / 2 + 2, RealmsSettingsScreen.row(12), 106, 20, CommonComponents.GUI_CANCEL, button -> this.minecraft.setScreen(this.configureWorldScreen)));
        String string = this.serverData.state == RealmsServer.State.OPEN ? "mco.configure.world.buttons.close" : "mco.configure.world.buttons.open";
        Button button2 = new Button(this.width / 2 - 53, RealmsSettingsScreen.row(0), 106, 20, new TranslatableComponent(string), button -> {
            if (this.serverData.state == RealmsServer.State.OPEN) {
                TranslatableComponent translatableComponent = new TranslatableComponent("mco.configure.world.close.question.line1");
                TranslatableComponent translatableComponent2 = new TranslatableComponent("mco.configure.world.close.question.line2");
                this.minecraft.setScreen(new RealmsLongConfirmationScreen(bl -> {
                    if (bl) {
                        this.configureWorldScreen.closeTheWorld(this);
                    } else {
                        this.minecraft.setScreen(this);
                    }
                }, RealmsLongConfirmationScreen.Type.Info, translatableComponent, translatableComponent2, true));
            } else {
                this.configureWorldScreen.openTheWorld(false, this);
            }
        });
        this.addButton(button2);
        this.nameEdit = new EditBox(this.minecraft.font, n, RealmsSettingsScreen.row(4), 212, 20, null, new TranslatableComponent("mco.configure.world.name"));
        this.nameEdit.setMaxLength(32);
        this.nameEdit.setValue(this.serverData.getName());
        this.addWidget(this.nameEdit);
        this.magicalSpecialHackyFocus(this.nameEdit);
        this.descEdit = new EditBox(this.minecraft.font, n, RealmsSettingsScreen.row(8), 212, 20, null, new TranslatableComponent("mco.configure.world.description"));
        this.descEdit.setMaxLength(32);
        this.descEdit.setValue(this.serverData.getDescription());
        this.addWidget(this.descEdit);
        this.titleLabel = this.addWidget(new RealmsLabel(new TranslatableComponent("mco.configure.world.settings.title"), this.width / 2, 17, 16777215));
        this.narrateLabels();
    }

    @Override
    public void removed() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }

    @Override
    public boolean keyPressed(int n, int n2, int n3) {
        if (n == 256) {
            this.minecraft.setScreen(this.configureWorldScreen);
            return true;
        }
        return super.keyPressed(n, n2, n3);
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderBackground(poseStack);
        this.titleLabel.render(this, poseStack);
        this.font.draw(poseStack, NAME_LABEL, (float)(this.width / 2 - 106), (float)RealmsSettingsScreen.row(3), 10526880);
        this.font.draw(poseStack, DESCRIPTION_LABEL, (float)(this.width / 2 - 106), (float)RealmsSettingsScreen.row(7), 10526880);
        this.nameEdit.render(poseStack, n, n2, f);
        this.descEdit.render(poseStack, n, n2, f);
        super.render(poseStack, n, n2, f);
    }

    public void save() {
        this.configureWorldScreen.saveSettings(this.nameEdit.getValue(), this.descEdit.getValue());
    }
}

