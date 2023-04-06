/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;

public class ChatScreen
extends Screen {
    private String historyBuffer = "";
    private int historyPos = -1;
    protected EditBox input;
    private String initial = "";
    private CommandSuggestions commandSuggestions;

    public ChatScreen(String string) {
        super(NarratorChatListener.NO_TITLE);
        this.initial = string;
    }

    @Override
    protected void init() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.historyPos = this.minecraft.gui.getChat().getRecentChat().size();
        this.input = new EditBox(this.font, 4, this.height - 12, this.width - 4, 12, new TranslatableComponent("chat.editBox")){

            @Override
            protected MutableComponent createNarrationMessage() {
                return super.createNarrationMessage().append(ChatScreen.this.commandSuggestions.getNarrationMessage());
            }
        };
        this.input.setMaxLength(256);
        this.input.setBordered(false);
        this.input.setValue(this.initial);
        this.input.setResponder(this::onEdited);
        this.children.add(this.input);
        this.commandSuggestions = new CommandSuggestions(this.minecraft, this, this.input, this.font, false, false, 1, 10, true, -805306368);
        this.commandSuggestions.updateCommandInfo();
        this.setInitialFocus(this.input);
    }

    @Override
    public void resize(Minecraft minecraft, int n, int n2) {
        String string = this.input.getValue();
        this.init(minecraft, n, n2);
        this.setChatLine(string);
        this.commandSuggestions.updateCommandInfo();
    }

    @Override
    public void removed() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
        this.minecraft.gui.getChat().resetChatScroll();
    }

    @Override
    public void tick() {
        this.input.tick();
    }

    private void onEdited(String string) {
        String string2 = this.input.getValue();
        this.commandSuggestions.setAllowSuggestions(!string2.equals(this.initial));
        this.commandSuggestions.updateCommandInfo();
    }

    @Override
    public boolean keyPressed(int n, int n2, int n3) {
        if (this.commandSuggestions.keyPressed(n, n2, n3)) {
            return true;
        }
        if (super.keyPressed(n, n2, n3)) {
            return true;
        }
        if (n == 256) {
            this.minecraft.setScreen(null);
            return true;
        }
        if (n == 257 || n == 335) {
            String string = this.input.getValue().trim();
            if (!string.isEmpty()) {
                this.sendMessage(string);
            }
            this.minecraft.setScreen(null);
            return true;
        }
        if (n == 265) {
            this.moveInHistory(-1);
            return true;
        }
        if (n == 264) {
            this.moveInHistory(1);
            return true;
        }
        if (n == 266) {
            this.minecraft.gui.getChat().scrollChat(this.minecraft.gui.getChat().getLinesPerPage() - 1);
            return true;
        }
        if (n == 267) {
            this.minecraft.gui.getChat().scrollChat(-this.minecraft.gui.getChat().getLinesPerPage() + 1);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double d, double d2, double d3) {
        if (d3 > 1.0) {
            d3 = 1.0;
        }
        if (d3 < -1.0) {
            d3 = -1.0;
        }
        if (this.commandSuggestions.mouseScrolled(d3)) {
            return true;
        }
        if (!ChatScreen.hasShiftDown()) {
            d3 *= 7.0;
        }
        this.minecraft.gui.getChat().scrollChat(d3);
        return true;
    }

    @Override
    public boolean mouseClicked(double d, double d2, int n) {
        if (this.commandSuggestions.mouseClicked((int)d, (int)d2, n)) {
            return true;
        }
        if (n == 0) {
            ChatComponent chatComponent = this.minecraft.gui.getChat();
            if (chatComponent.handleChatQueueClicked(d, d2)) {
                return true;
            }
            Style style = chatComponent.getClickedComponentStyleAt(d, d2);
            if (style != null && this.handleComponentClicked(style)) {
                return true;
            }
        }
        if (this.input.mouseClicked(d, d2, n)) {
            return true;
        }
        return super.mouseClicked(d, d2, n);
    }

    @Override
    protected void insertText(String string, boolean bl) {
        if (bl) {
            this.input.setValue(string);
        } else {
            this.input.insertText(string);
        }
    }

    public void moveInHistory(int n) {
        int n2 = this.historyPos + n;
        int n3 = this.minecraft.gui.getChat().getRecentChat().size();
        if ((n2 = Mth.clamp(n2, 0, n3)) == this.historyPos) {
            return;
        }
        if (n2 == n3) {
            this.historyPos = n3;
            this.input.setValue(this.historyBuffer);
            return;
        }
        if (this.historyPos == n3) {
            this.historyBuffer = this.input.getValue();
        }
        this.input.setValue(this.minecraft.gui.getChat().getRecentChat().get(n2));
        this.commandSuggestions.setAllowSuggestions(false);
        this.historyPos = n2;
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.setFocused(this.input);
        this.input.setFocus(true);
        ChatScreen.fill(poseStack, 2, this.height - 14, this.width - 2, this.height - 2, this.minecraft.options.getBackgroundColor(Integer.MIN_VALUE));
        this.input.render(poseStack, n, n2, f);
        this.commandSuggestions.render(poseStack, n, n2);
        Style style = this.minecraft.gui.getChat().getClickedComponentStyleAt(n, n2);
        if (style != null && style.getHoverEvent() != null) {
            this.renderComponentHoverEffect(poseStack, style, n, n2);
        }
        super.render(poseStack, n, n2, f);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void setChatLine(String string) {
        this.input.setValue(string);
    }

}

