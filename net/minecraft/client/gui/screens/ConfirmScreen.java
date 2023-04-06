/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 */
package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;

public class ConfirmScreen
extends Screen {
    private final Component title2;
    private MultiLineLabel message = MultiLineLabel.EMPTY;
    protected Component yesButton;
    protected Component noButton;
    private int delayTicker;
    protected final BooleanConsumer callback;

    public ConfirmScreen(BooleanConsumer booleanConsumer, Component component, Component component2) {
        this(booleanConsumer, component, component2, CommonComponents.GUI_YES, CommonComponents.GUI_NO);
    }

    public ConfirmScreen(BooleanConsumer booleanConsumer, Component component, Component component2, Component component3, Component component4) {
        super(component);
        this.callback = booleanConsumer;
        this.title2 = component2;
        this.yesButton = component3;
        this.noButton = component4;
    }

    @Override
    public String getNarrationMessage() {
        return super.getNarrationMessage() + ". " + this.title2.getString();
    }

    @Override
    protected void init() {
        super.init();
        this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 96, 150, 20, this.yesButton, button -> this.callback.accept(true)));
        this.addButton(new Button(this.width / 2 - 155 + 160, this.height / 6 + 96, 150, 20, this.noButton, button -> this.callback.accept(false)));
        this.message = MultiLineLabel.create(this.font, (FormattedText)this.title2, this.width - 50);
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderBackground(poseStack);
        ConfirmScreen.drawCenteredString(poseStack, this.font, this.title, this.width / 2, 70, 16777215);
        this.message.renderCentered(poseStack, this.width / 2, 90);
        super.render(poseStack, n, n2, f);
    }

    public void setDelay(int n) {
        this.delayTicker = n;
        for (AbstractWidget abstractWidget : this.buttons) {
            abstractWidget.active = false;
        }
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

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean keyPressed(int n, int n2, int n3) {
        if (n == 256) {
            this.callback.accept(false);
            return true;
        }
        return super.keyPressed(n, n2, n3);
    }
}

