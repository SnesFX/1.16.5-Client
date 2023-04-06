/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;

public class RealmsConfirmScreen
extends RealmsScreen {
    protected BooleanConsumer callback;
    private final Component title1;
    private final Component title2;
    private int delayTicker;

    public RealmsConfirmScreen(BooleanConsumer booleanConsumer, Component component, Component component2) {
        this.callback = booleanConsumer;
        this.title1 = component;
        this.title2 = component2;
    }

    @Override
    public void init() {
        this.addButton(new Button(this.width / 2 - 105, RealmsConfirmScreen.row(9), 100, 20, CommonComponents.GUI_YES, button -> this.callback.accept(true)));
        this.addButton(new Button(this.width / 2 + 5, RealmsConfirmScreen.row(9), 100, 20, CommonComponents.GUI_NO, button -> this.callback.accept(false)));
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderBackground(poseStack);
        RealmsConfirmScreen.drawCenteredString(poseStack, this.font, this.title1, this.width / 2, RealmsConfirmScreen.row(3), 16777215);
        RealmsConfirmScreen.drawCenteredString(poseStack, this.font, this.title2, this.width / 2, RealmsConfirmScreen.row(5), 16777215);
        super.render(poseStack, n, n2, f);
    }

    @Override
    public void tick() {
        super.tick();
        if (--this.delayTicker == 0) {
            for (AbstractWidget abstractWidget : this.buttons) {
                abstractWidget.active = true;
            }
        }
    }
}

