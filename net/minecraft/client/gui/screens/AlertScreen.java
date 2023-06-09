/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;

public class AlertScreen
extends Screen {
    private final Runnable callback;
    protected final Component text;
    private MultiLineLabel message = MultiLineLabel.EMPTY;
    protected final Component okButton;
    private int delayTicker;

    public AlertScreen(Runnable runnable, Component component, Component component2) {
        this(runnable, component, component2, CommonComponents.GUI_BACK);
    }

    public AlertScreen(Runnable runnable, Component component, Component component2, Component component3) {
        super(component);
        this.callback = runnable;
        this.text = component2;
        this.okButton = component3;
    }

    @Override
    protected void init() {
        super.init();
        this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 168, 200, 20, this.okButton, button -> this.callback.run()));
        this.message = MultiLineLabel.create(this.font, (FormattedText)this.text, this.width - 50);
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderBackground(poseStack);
        AlertScreen.drawCenteredString(poseStack, this.font, this.title, this.width / 2, 70, 16777215);
        this.message.renderCentered(poseStack, this.width / 2, 90);
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

