/*
 * Decompiled with CFR 0.146.
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.gui.screens.RealmsResetWorldScreen;
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
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;

public class RealmsResetNormalWorldScreen
extends RealmsScreen {
    private static final Component SEED_LABEL = new TranslatableComponent("mco.reset.world.seed");
    private static final Component[] LEVEL_TYPES = new Component[]{new TranslatableComponent("generator.default"), new TranslatableComponent("generator.flat"), new TranslatableComponent("generator.large_biomes"), new TranslatableComponent("generator.amplified")};
    private final RealmsResetWorldScreen lastScreen;
    private RealmsLabel titleLabel;
    private EditBox seedEdit;
    private Boolean generateStructures = true;
    private Integer levelTypeIndex = 0;
    private Component buttonTitle;

    public RealmsResetNormalWorldScreen(RealmsResetWorldScreen realmsResetWorldScreen, Component component) {
        this.lastScreen = realmsResetWorldScreen;
        this.buttonTitle = component;
    }

    @Override
    public void tick() {
        this.seedEdit.tick();
        super.tick();
    }

    @Override
    public void init() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.titleLabel = new RealmsLabel(new TranslatableComponent("mco.reset.world.generate"), this.width / 2, 17, 16777215);
        this.addWidget(this.titleLabel);
        this.seedEdit = new EditBox(this.minecraft.font, this.width / 2 - 100, RealmsResetNormalWorldScreen.row(2), 200, 20, null, new TranslatableComponent("mco.reset.world.seed"));
        this.seedEdit.setMaxLength(32);
        this.addWidget(this.seedEdit);
        this.setInitialFocus(this.seedEdit);
        this.addButton(new Button(this.width / 2 - 102, RealmsResetNormalWorldScreen.row(4), 205, 20, this.levelTypeTitle(), button -> {
            this.levelTypeIndex = (this.levelTypeIndex + 1) % LEVEL_TYPES.length;
            button.setMessage(this.levelTypeTitle());
        }));
        this.addButton(new Button(this.width / 2 - 102, RealmsResetNormalWorldScreen.row(6) - 2, 205, 20, this.generateStructuresTitle(), button -> {
            this.generateStructures = this.generateStructures == false;
            button.setMessage(this.generateStructuresTitle());
        }));
        this.addButton(new Button(this.width / 2 - 102, RealmsResetNormalWorldScreen.row(12), 97, 20, this.buttonTitle, button -> this.lastScreen.resetWorld(new RealmsResetWorldScreen.ResetWorldInfo(this.seedEdit.getValue(), this.levelTypeIndex, this.generateStructures))));
        this.addButton(new Button(this.width / 2 + 8, RealmsResetNormalWorldScreen.row(12), 97, 20, CommonComponents.GUI_BACK, button -> this.minecraft.setScreen(this.lastScreen)));
        this.narrateLabels();
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

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderBackground(poseStack);
        this.titleLabel.render(this, poseStack);
        this.font.draw(poseStack, SEED_LABEL, (float)(this.width / 2 - 100), (float)RealmsResetNormalWorldScreen.row(1), 10526880);
        this.seedEdit.render(poseStack, n, n2, f);
        super.render(poseStack, n, n2, f);
    }

    private Component levelTypeTitle() {
        return new TranslatableComponent("selectWorld.mapType").append(" ").append(LEVEL_TYPES[this.levelTypeIndex]);
    }

    private Component generateStructuresTitle() {
        return CommonComponents.optionStatus(new TranslatableComponent("selectWorld.mapFeatures"), this.generateStructures);
    }
}

