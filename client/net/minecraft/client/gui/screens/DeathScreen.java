/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.User;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class DeathScreen
extends Screen {
    private int delayTicker;
    private final Component causeOfDeath;
    private final boolean hardcore;
    private Component deathScore;

    public DeathScreen(@Nullable Component component, boolean bl) {
        super(new TranslatableComponent(bl ? "deathScreen.title.hardcore" : "deathScreen.title"));
        this.causeOfDeath = component;
        this.hardcore = bl;
    }

    @Override
    protected void init() {
        this.delayTicker = 0;
        this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 72, 200, 20, this.hardcore ? new TranslatableComponent("deathScreen.spectate") : new TranslatableComponent("deathScreen.respawn"), button -> {
            this.minecraft.player.respawn();
            this.minecraft.setScreen(null);
        }));
        Button button2 = this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 96, 200, 20, new TranslatableComponent("deathScreen.titleScreen"), button -> {
            if (this.hardcore) {
                this.exitToTitleScreen();
                return;
            }
            ConfirmScreen confirmScreen = new ConfirmScreen(this::confirmResult, new TranslatableComponent("deathScreen.quit.confirm"), TextComponent.EMPTY, new TranslatableComponent("deathScreen.titleScreen"), new TranslatableComponent("deathScreen.respawn"));
            this.minecraft.setScreen(confirmScreen);
            confirmScreen.setDelay(20);
        }));
        if (!this.hardcore && this.minecraft.getUser() == null) {
            button2.active = false;
        }
        for (AbstractWidget abstractWidget : this.buttons) {
            abstractWidget.active = false;
        }
        this.deathScore = new TranslatableComponent("deathScreen.score").append(": ").append(new TextComponent(Integer.toString(this.minecraft.player.getScore())).withStyle(ChatFormatting.YELLOW));
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    private void confirmResult(boolean bl) {
        if (bl) {
            this.exitToTitleScreen();
        } else {
            this.minecraft.player.respawn();
            this.minecraft.setScreen(null);
        }
    }

    private void exitToTitleScreen() {
        if (this.minecraft.level != null) {
            this.minecraft.level.disconnect();
        }
        this.minecraft.clearLevel(new GenericDirtMessageScreen(new TranslatableComponent("menu.savingLevel")));
        this.minecraft.setScreen(new TitleScreen());
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.fillGradient(poseStack, 0, 0, this.width, this.height, 1615855616, -1602211792);
        RenderSystem.pushMatrix();
        RenderSystem.scalef(2.0f, 2.0f, 2.0f);
        DeathScreen.drawCenteredString(poseStack, this.font, this.title, this.width / 2 / 2, 30, 16777215);
        RenderSystem.popMatrix();
        if (this.causeOfDeath != null) {
            DeathScreen.drawCenteredString(poseStack, this.font, this.causeOfDeath, this.width / 2, 85, 16777215);
        }
        DeathScreen.drawCenteredString(poseStack, this.font, this.deathScore, this.width / 2, 100, 16777215);
        if (this.causeOfDeath != null && n2 > 85) {
            this.font.getClass();
            if (n2 < 85 + 9) {
                Style style = this.getClickedComponentStyleAt(n);
                this.renderComponentHoverEffect(poseStack, style, n, n2);
            }
        }
        super.render(poseStack, n, n2, f);
    }

    @Nullable
    private Style getClickedComponentStyleAt(int n) {
        if (this.causeOfDeath == null) {
            return null;
        }
        int n2 = this.minecraft.font.width(this.causeOfDeath);
        int n3 = this.width / 2 - n2 / 2;
        int n4 = this.width / 2 + n2 / 2;
        if (n < n3 || n > n4) {
            return null;
        }
        return this.minecraft.font.getSplitter().componentStyleAtWidth(this.causeOfDeath, n - n3);
    }

    @Override
    public boolean mouseClicked(double d, double d2, int n) {
        if (this.causeOfDeath != null && d2 > 85.0) {
            Style style;
            this.font.getClass();
            if (d2 < (double)(85 + 9) && (style = this.getClickedComponentStyleAt((int)d)) != null && style.getClickEvent() != null && style.getClickEvent().getAction() == ClickEvent.Action.OPEN_URL) {
                this.handleComponentClicked(style);
                return false;
            }
        }
        return super.mouseClicked(d, d2, n);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        ++this.delayTicker;
        if (this.delayTicker == 20) {
            for (AbstractWidget abstractWidget : this.buttons) {
                abstractWidget.active = true;
            }
        }
    }
}

