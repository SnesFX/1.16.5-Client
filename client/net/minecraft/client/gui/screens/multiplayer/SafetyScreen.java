/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.screens.multiplayer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class SafetyScreen
extends Screen {
    private final Screen previous;
    private static final Component TITLE = new TranslatableComponent("multiplayerWarning.header").withStyle(ChatFormatting.BOLD);
    private static final Component CONTENT = new TranslatableComponent("multiplayerWarning.message");
    private static final Component CHECK = new TranslatableComponent("multiplayerWarning.check");
    private static final Component NARRATION = TITLE.copy().append("\n").append(CONTENT);
    private Checkbox stopShowing;
    private MultiLineLabel message = MultiLineLabel.EMPTY;

    public SafetyScreen(Screen screen) {
        super(NarratorChatListener.NO_TITLE);
        this.previous = screen;
    }

    @Override
    protected void init() {
        super.init();
        this.message = MultiLineLabel.create(this.font, (FormattedText)CONTENT, this.width - 50);
        this.font.getClass();
        int n = (this.message.getLineCount() + 1) * 9 * 2;
        this.addButton(new Button(this.width / 2 - 155, 100 + n, 150, 20, CommonComponents.GUI_PROCEED, button -> {
            if (this.stopShowing.selected()) {
                this.minecraft.options.skipMultiplayerWarning = true;
                this.minecraft.options.save();
            }
            this.minecraft.setScreen(new JoinMultiplayerScreen(this.previous));
        }));
        this.addButton(new Button(this.width / 2 - 155 + 160, 100 + n, 150, 20, CommonComponents.GUI_BACK, button -> this.minecraft.setScreen(this.previous)));
        this.stopShowing = new Checkbox(this.width / 2 - 155 + 80, 76 + n, 150, 20, CHECK, false);
        this.addButton(this.stopShowing);
    }

    @Override
    public String getNarrationMessage() {
        return NARRATION.getString();
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderDirtBackground(0);
        SafetyScreen.drawString(poseStack, this.font, TITLE, 25, 30, 16777215);
        this.font.getClass();
        this.message.renderLeftAligned(poseStack, 25, 70, 9 * 2, 16777215);
        super.render(poseStack, n, n2, f);
    }
}

