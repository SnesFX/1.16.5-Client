/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Queues
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.gui.components;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Deque;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.ChatVisiblity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChatComponent
extends GuiComponent {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Minecraft minecraft;
    private final List<String> recentChat = Lists.newArrayList();
    private final List<GuiMessage<Component>> allMessages = Lists.newArrayList();
    private final List<GuiMessage<FormattedCharSequence>> trimmedMessages = Lists.newArrayList();
    private final Deque<Component> chatQueue = Queues.newArrayDeque();
    private int chatScrollbarPos;
    private boolean newMessageSinceScroll;
    private long lastMessage = 0L;

    public ChatComponent(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    public void render(PoseStack poseStack, int n) {
        int n2;
        Object object;
        int n3;
        int n4;
        int n5;
        if (this.isChatHidden()) {
            return;
        }
        this.processPendingMessages();
        int n6 = this.getLinesPerPage();
        int n7 = this.trimmedMessages.size();
        if (n7 <= 0) {
            return;
        }
        boolean bl = false;
        if (this.isChatFocused()) {
            bl = true;
        }
        double d = this.getScale();
        int n8 = Mth.ceil((double)this.getWidth() / d);
        RenderSystem.pushMatrix();
        RenderSystem.translatef(2.0f, 8.0f, 0.0f);
        RenderSystem.scaled(d, d, 1.0);
        double d2 = this.minecraft.options.chatOpacity * 0.8999999761581421 + 0.10000000149011612;
        double d3 = this.minecraft.options.textBackgroundOpacity;
        double d4 = 9.0 * (this.minecraft.options.chatLineSpacing + 1.0);
        double d5 = -8.0 * (this.minecraft.options.chatLineSpacing + 1.0) + 4.0 * this.minecraft.options.chatLineSpacing;
        int n9 = 0;
        for (n3 = 0; n3 + this.chatScrollbarPos < this.trimmedMessages.size() && n3 < n6; ++n3) {
            object = this.trimmedMessages.get(n3 + this.chatScrollbarPos);
            if (object == null || (n2 = n - ((GuiMessage)object).getAddedTime()) >= 200 && !bl) continue;
            double d6 = bl ? 1.0 : ChatComponent.getTimeFactor(n2);
            n4 = (int)(255.0 * d6 * d2);
            n5 = (int)(255.0 * d6 * d3);
            ++n9;
            if (n4 <= 3) continue;
            boolean bl2 = false;
            double d7 = (double)(-n3) * d4;
            poseStack.pushPose();
            poseStack.translate(0.0, 0.0, 50.0);
            ChatComponent.fill(poseStack, -2, (int)(d7 - d4), 0 + n8 + 4, (int)d7, n5 << 24);
            RenderSystem.enableBlend();
            poseStack.translate(0.0, 0.0, 50.0);
            this.minecraft.font.drawShadow(poseStack, (FormattedCharSequence)((GuiMessage)object).getMessage(), 0.0f, (float)((int)(d7 + d5)), 16777215 + (n4 << 24));
            RenderSystem.disableAlphaTest();
            RenderSystem.disableBlend();
            poseStack.popPose();
        }
        if (!this.chatQueue.isEmpty()) {
            n3 = (int)(128.0 * d2);
            int n10 = (int)(255.0 * d3);
            poseStack.pushPose();
            poseStack.translate(0.0, 0.0, 50.0);
            ChatComponent.fill(poseStack, -2, 0, n8 + 4, 9, n10 << 24);
            RenderSystem.enableBlend();
            poseStack.translate(0.0, 0.0, 50.0);
            this.minecraft.font.drawShadow(poseStack, new TranslatableComponent("chat.queue", this.chatQueue.size()), 0.0f, 1.0f, 16777215 + (n3 << 24));
            poseStack.popPose();
            RenderSystem.disableAlphaTest();
            RenderSystem.disableBlend();
        }
        if (bl) {
            this.minecraft.font.getClass();
            n3 = 9;
            RenderSystem.translatef(-3.0f, 0.0f, 0.0f);
            object = n7 * n3 + n7;
            n2 = n9 * n3 + n9;
            int n11 = this.chatScrollbarPos * n2 / n7;
            int n12 = n2 * n2 / object;
            if (object != n2) {
                n4 = n11 > 0 ? 170 : 96;
                n5 = this.newMessageSinceScroll ? 13382451 : 3355562;
                ChatComponent.fill(poseStack, 0, -n11, 2, -n11 - n12, n5 + (n4 << 24));
                ChatComponent.fill(poseStack, 2, -n11, 1, -n11 - n12, 13421772 + (n4 << 24));
            }
        }
        RenderSystem.popMatrix();
    }

    private boolean isChatHidden() {
        return this.minecraft.options.chatVisibility == ChatVisiblity.HIDDEN;
    }

    private static double getTimeFactor(int n) {
        double d = (double)n / 200.0;
        d = 1.0 - d;
        d *= 10.0;
        d = Mth.clamp(d, 0.0, 1.0);
        d *= d;
        return d;
    }

    public void clearMessages(boolean bl) {
        this.chatQueue.clear();
        this.trimmedMessages.clear();
        this.allMessages.clear();
        if (bl) {
            this.recentChat.clear();
        }
    }

    public void addMessage(Component component) {
        this.addMessage(component, 0);
    }

    private void addMessage(Component component, int n) {
        this.addMessage(component, n, this.minecraft.gui.getGuiTicks(), false);
        LOGGER.info("[CHAT] {}", (Object)component.getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
    }

    private void addMessage(Component component, int n, int n2, boolean bl) {
        if (n != 0) {
            this.removeById(n);
        }
        int n3 = Mth.floor((double)this.getWidth() / this.getScale());
        List<FormattedCharSequence> list = ComponentRenderUtils.wrapComponents(component, n3, this.minecraft.font);
        boolean bl2 = this.isChatFocused();
        for (FormattedCharSequence formattedCharSequence : list) {
            if (bl2 && this.chatScrollbarPos > 0) {
                this.newMessageSinceScroll = true;
                this.scrollChat(1.0);
            }
            this.trimmedMessages.add(0, new GuiMessage<FormattedCharSequence>(n2, formattedCharSequence, n));
        }
        while (this.trimmedMessages.size() > 100) {
            this.trimmedMessages.remove(this.trimmedMessages.size() - 1);
        }
        if (!bl) {
            this.allMessages.add(0, new GuiMessage<Component>(n2, component, n));
            while (this.allMessages.size() > 100) {
                this.allMessages.remove(this.allMessages.size() - 1);
            }
        }
    }

    public void rescaleChat() {
        this.trimmedMessages.clear();
        this.resetChatScroll();
        for (int i = this.allMessages.size() - 1; i >= 0; --i) {
            GuiMessage<Component> guiMessage = this.allMessages.get(i);
            this.addMessage(guiMessage.getMessage(), guiMessage.getId(), guiMessage.getAddedTime(), true);
        }
    }

    public List<String> getRecentChat() {
        return this.recentChat;
    }

    public void addRecentChat(String string) {
        if (this.recentChat.isEmpty() || !this.recentChat.get(this.recentChat.size() - 1).equals(string)) {
            this.recentChat.add(string);
        }
    }

    public void resetChatScroll() {
        this.chatScrollbarPos = 0;
        this.newMessageSinceScroll = false;
    }

    public void scrollChat(double d) {
        this.chatScrollbarPos = (int)((double)this.chatScrollbarPos + d);
        int n = this.trimmedMessages.size();
        if (this.chatScrollbarPos > n - this.getLinesPerPage()) {
            this.chatScrollbarPos = n - this.getLinesPerPage();
        }
        if (this.chatScrollbarPos <= 0) {
            this.chatScrollbarPos = 0;
            this.newMessageSinceScroll = false;
        }
    }

    public boolean handleChatQueueClicked(double d, double d2) {
        if (!this.isChatFocused() || this.minecraft.options.hideGui || this.isChatHidden() || this.chatQueue.isEmpty()) {
            return false;
        }
        double d3 = d - 2.0;
        double d4 = (double)this.minecraft.getWindow().getGuiScaledHeight() - d2 - 40.0;
        if (d3 <= (double)Mth.floor((double)this.getWidth() / this.getScale()) && d4 < 0.0 && d4 > (double)Mth.floor(-9.0 * this.getScale())) {
            this.addMessage(this.chatQueue.remove());
            this.lastMessage = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    @Nullable
    public Style getClickedComponentStyleAt(double d, double d2) {
        if (!this.isChatFocused() || this.minecraft.options.hideGui || this.isChatHidden()) {
            return null;
        }
        double d3 = d - 2.0;
        double d4 = (double)this.minecraft.getWindow().getGuiScaledHeight() - d2 - 40.0;
        d3 = Mth.floor(d3 / this.getScale());
        d4 = Mth.floor(d4 / (this.getScale() * (this.minecraft.options.chatLineSpacing + 1.0)));
        if (d3 < 0.0 || d4 < 0.0) {
            return null;
        }
        int n = Math.min(this.getLinesPerPage(), this.trimmedMessages.size());
        if (d3 <= (double)Mth.floor((double)this.getWidth() / this.getScale())) {
            this.minecraft.font.getClass();
            if (d4 < (double)(9 * n + n)) {
                this.minecraft.font.getClass();
                int n2 = (int)(d4 / 9.0 + (double)this.chatScrollbarPos);
                if (n2 >= 0 && n2 < this.trimmedMessages.size()) {
                    GuiMessage<FormattedCharSequence> guiMessage = this.trimmedMessages.get(n2);
                    return this.minecraft.font.getSplitter().componentStyleAtWidth(guiMessage.getMessage(), (int)d3);
                }
            }
        }
        return null;
    }

    private boolean isChatFocused() {
        return this.minecraft.screen instanceof ChatScreen;
    }

    private void removeById(int n) {
        this.trimmedMessages.removeIf(guiMessage -> guiMessage.getId() == n);
        this.allMessages.removeIf(guiMessage -> guiMessage.getId() == n);
    }

    public int getWidth() {
        return ChatComponent.getWidth(this.minecraft.options.chatWidth);
    }

    public int getHeight() {
        return ChatComponent.getHeight((this.isChatFocused() ? this.minecraft.options.chatHeightFocused : this.minecraft.options.chatHeightUnfocused) / (this.minecraft.options.chatLineSpacing + 1.0));
    }

    public double getScale() {
        return this.minecraft.options.chatScale;
    }

    public static int getWidth(double d) {
        int n = 320;
        int n2 = 40;
        return Mth.floor(d * 280.0 + 40.0);
    }

    public static int getHeight(double d) {
        int n = 180;
        int n2 = 20;
        return Mth.floor(d * 160.0 + 20.0);
    }

    public int getLinesPerPage() {
        return this.getHeight() / 9;
    }

    private long getChatRateMillis() {
        return (long)(this.minecraft.options.chatDelay * 1000.0);
    }

    private void processPendingMessages() {
        if (this.chatQueue.isEmpty()) {
            return;
        }
        long l = System.currentTimeMillis();
        if (l - this.lastMessage >= this.getChatRateMillis()) {
            this.addMessage(this.chatQueue.remove());
            this.lastMessage = l;
        }
    }

    public void enqueueMessage(Component component) {
        if (this.minecraft.options.chatDelay <= 0.0) {
            this.addMessage(component);
        } else {
            long l = System.currentTimeMillis();
            if (l - this.lastMessage >= this.getChatRateMillis()) {
                this.addMessage(component);
                this.lastMessage = l;
            } else {
                this.chatQueue.add(component);
            }
        }
    }
}

