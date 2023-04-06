/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.HttpUtil;
import net.minecraft.world.level.GameType;

public class ShareToLanScreen
extends Screen {
    private static final Component ALLOW_COMMANDS_LABEL = new TranslatableComponent("selectWorld.allowCommands");
    private static final Component GAME_MODE_LABEL = new TranslatableComponent("selectWorld.gameMode");
    private static final Component INFO_TEXT = new TranslatableComponent("lanServer.otherPlayers");
    private final Screen lastScreen;
    private Button commandsButton;
    private Button modeButton;
    private String gameModeName = "survival";
    private boolean commands;

    public ShareToLanScreen(Screen screen) {
        super(new TranslatableComponent("lanServer.title"));
        this.lastScreen = screen;
    }

    @Override
    protected void init() {
        this.addButton(new Button(this.width / 2 - 155, this.height - 28, 150, 20, new TranslatableComponent("lanServer.start"), button -> {
            this.minecraft.setScreen(null);
            int n = HttpUtil.getAvailablePort();
            TranslatableComponent translatableComponent = this.minecraft.getSingleplayerServer().publishServer(GameType.byName(this.gameModeName), this.commands, n) ? new TranslatableComponent("commands.publish.started", n) : new TranslatableComponent("commands.publish.failed");
            this.minecraft.gui.getChat().addMessage(translatableComponent);
            this.minecraft.updateTitle();
        }));
        this.addButton(new Button(this.width / 2 + 5, this.height - 28, 150, 20, CommonComponents.GUI_CANCEL, button -> this.minecraft.setScreen(this.lastScreen)));
        this.modeButton = this.addButton(new Button(this.width / 2 - 155, 100, 150, 20, TextComponent.EMPTY, button -> {
            this.gameModeName = "spectator".equals(this.gameModeName) ? "creative" : ("creative".equals(this.gameModeName) ? "adventure" : ("adventure".equals(this.gameModeName) ? "survival" : "spectator"));
            this.updateSelectionStrings();
        }));
        this.commandsButton = this.addButton(new Button(this.width / 2 + 5, 100, 150, 20, ALLOW_COMMANDS_LABEL, button -> {
            this.commands = !this.commands;
            this.updateSelectionStrings();
        }));
        this.updateSelectionStrings();
    }

    private void updateSelectionStrings() {
        this.modeButton.setMessage(new TranslatableComponent("options.generic_value", GAME_MODE_LABEL, new TranslatableComponent("selectWorld.gameMode." + this.gameModeName)));
        this.commandsButton.setMessage(CommonComponents.optionStatus(ALLOW_COMMANDS_LABEL, this.commands));
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderBackground(poseStack);
        ShareToLanScreen.drawCenteredString(poseStack, this.font, this.title, this.width / 2, 50, 16777215);
        ShareToLanScreen.drawCenteredString(poseStack, this.font, INFO_TEXT, this.width / 2, 82, 16777215);
        super.render(poseStack, n, n2, f);
    }
}

