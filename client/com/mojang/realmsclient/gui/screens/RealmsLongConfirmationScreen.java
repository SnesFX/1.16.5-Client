/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.NarrationHelper;
import net.minecraft.realms.RealmsScreen;

public class RealmsLongConfirmationScreen
extends RealmsScreen {
    private final Type type;
    private final Component line2;
    private final Component line3;
    protected final BooleanConsumer callback;
    private final boolean yesNoQuestion;

    public RealmsLongConfirmationScreen(BooleanConsumer booleanConsumer, Type type, Component component, Component component2, boolean bl) {
        this.callback = booleanConsumer;
        this.type = type;
        this.line2 = component;
        this.line3 = component2;
        this.yesNoQuestion = bl;
    }

    @Override
    public void init() {
        NarrationHelper.now(this.type.text, this.line2.getString(), this.line3.getString());
        if (this.yesNoQuestion) {
            this.addButton(new Button(this.width / 2 - 105, RealmsLongConfirmationScreen.row(8), 100, 20, CommonComponents.GUI_YES, button -> this.callback.accept(true)));
            this.addButton(new Button(this.width / 2 + 5, RealmsLongConfirmationScreen.row(8), 100, 20, CommonComponents.GUI_NO, button -> this.callback.accept(false)));
        } else {
            this.addButton(new Button(this.width / 2 - 50, RealmsLongConfirmationScreen.row(8), 100, 20, new TranslatableComponent("mco.gui.ok"), button -> this.callback.accept(true)));
        }
    }

    @Override
    public boolean keyPressed(int n, int n2, int n3) {
        if (n == 256) {
            this.callback.accept(false);
            return true;
        }
        return super.keyPressed(n, n2, n3);
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderBackground(poseStack);
        RealmsLongConfirmationScreen.drawCenteredString(poseStack, this.font, this.type.text, this.width / 2, RealmsLongConfirmationScreen.row(2), this.type.colorCode);
        RealmsLongConfirmationScreen.drawCenteredString(poseStack, this.font, this.line2, this.width / 2, RealmsLongConfirmationScreen.row(4), 16777215);
        RealmsLongConfirmationScreen.drawCenteredString(poseStack, this.font, this.line3, this.width / 2, RealmsLongConfirmationScreen.row(6), 16777215);
        super.render(poseStack, n, n2, f);
    }

    public static enum Type {
        Warning("Warning!", 16711680),
        Info("Info!", 8226750);
        
        public final int colorCode;
        public final String text;

        private Type(String string2, int n2) {
            this.text = string2;
            this.colorCode = n2;
        }
    }

}

