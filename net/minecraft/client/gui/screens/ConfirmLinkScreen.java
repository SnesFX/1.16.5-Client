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
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class ConfirmLinkScreen
extends ConfirmScreen {
    private final Component warning;
    private final Component copyButton;
    private final String url;
    private final boolean showWarning;

    public ConfirmLinkScreen(BooleanConsumer booleanConsumer, String string, boolean bl) {
        super(booleanConsumer, new TranslatableComponent(bl ? "chat.link.confirmTrusted" : "chat.link.confirm"), new TextComponent(string));
        this.yesButton = bl ? new TranslatableComponent("chat.link.open") : CommonComponents.GUI_YES;
        this.noButton = bl ? CommonComponents.GUI_CANCEL : CommonComponents.GUI_NO;
        this.copyButton = new TranslatableComponent("chat.copy");
        this.warning = new TranslatableComponent("chat.link.warning");
        this.showWarning = !bl;
        this.url = string;
    }

    @Override
    protected void init() {
        super.init();
        this.buttons.clear();
        this.children.clear();
        this.addButton(new Button(this.width / 2 - 50 - 105, this.height / 6 + 96, 100, 20, this.yesButton, button -> this.callback.accept(true)));
        this.addButton(new Button(this.width / 2 - 50, this.height / 6 + 96, 100, 20, this.copyButton, button -> {
            this.copyToClipboard();
            this.callback.accept(false);
        }));
        this.addButton(new Button(this.width / 2 - 50 + 105, this.height / 6 + 96, 100, 20, this.noButton, button -> this.callback.accept(false)));
    }

    public void copyToClipboard() {
        this.minecraft.keyboardHandler.setClipboard(this.url);
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        super.render(poseStack, n, n2, f);
        if (this.showWarning) {
            ConfirmLinkScreen.drawCenteredString(poseStack, this.font, this.warning, this.width / 2, 110, 16764108);
        }
    }
}

