/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens.social;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.social.PlayerSocialManager;
import net.minecraft.client.gui.screens.social.SocialInteractionsPlayerList;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class SocialInteractionsScreen
extends Screen {
    protected static final ResourceLocation SOCIAL_INTERACTIONS_LOCATION = new ResourceLocation("textures/gui/social_interactions.png");
    private static final Component TAB_ALL = new TranslatableComponent("gui.socialInteractions.tab_all");
    private static final Component TAB_HIDDEN = new TranslatableComponent("gui.socialInteractions.tab_hidden");
    private static final Component TAB_BLOCKED = new TranslatableComponent("gui.socialInteractions.tab_blocked");
    private static final Component TAB_ALL_SELECTED = TAB_ALL.plainCopy().withStyle(ChatFormatting.UNDERLINE);
    private static final Component TAB_HIDDEN_SELECTED = TAB_HIDDEN.plainCopy().withStyle(ChatFormatting.UNDERLINE);
    private static final Component TAB_BLOCKED_SELECTED = TAB_BLOCKED.plainCopy().withStyle(ChatFormatting.UNDERLINE);
    private static final Component SEARCH_HINT = new TranslatableComponent("gui.socialInteractions.search_hint").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY);
    private static final Component EMPTY_SEARCH = new TranslatableComponent("gui.socialInteractions.search_empty").withStyle(ChatFormatting.GRAY);
    private static final Component EMPTY_HIDDEN = new TranslatableComponent("gui.socialInteractions.empty_hidden").withStyle(ChatFormatting.GRAY);
    private static final Component EMPTY_BLOCKED = new TranslatableComponent("gui.socialInteractions.empty_blocked").withStyle(ChatFormatting.GRAY);
    private static final Component BLOCKING_HINT = new TranslatableComponent("gui.socialInteractions.blocking_hint");
    private SocialInteractionsPlayerList socialInteractionsPlayerList;
    private EditBox searchBox;
    private String lastSearch = "";
    private Page page = Page.ALL;
    private Button allButton;
    private Button hiddenButton;
    private Button blockedButton;
    private Button blockingHintButton;
    @Nullable
    private Component serverLabel;
    private int playerCount;
    private boolean initialized;
    @Nullable
    private Runnable postRenderRunnable;

    public SocialInteractionsScreen() {
        super(new TranslatableComponent("gui.socialInteractions.title"));
        this.updateServerLabel(Minecraft.getInstance());
    }

    private int windowHeight() {
        return Math.max(52, this.height - 128 - 16);
    }

    private int backgroundUnits() {
        return this.windowHeight() / 16;
    }

    private int listEnd() {
        return 80 + this.backgroundUnits() * 16 - 8;
    }

    private int marginX() {
        return (this.width - 238) / 2;
    }

    @Override
    public String getNarrationMessage() {
        return super.getNarrationMessage() + ". " + this.serverLabel.getString();
    }

    @Override
    public void tick() {
        super.tick();
        this.searchBox.tick();
    }

    @Override
    protected void init() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        if (this.initialized) {
            this.socialInteractionsPlayerList.updateSize(this.width, this.height, 88, this.listEnd());
        } else {
            this.socialInteractionsPlayerList = new SocialInteractionsPlayerList(this, this.minecraft, this.width, this.height, 88, this.listEnd(), 36);
        }
        int n = this.socialInteractionsPlayerList.getRowWidth() / 3;
        int n2 = this.socialInteractionsPlayerList.getRowLeft();
        int n3 = this.socialInteractionsPlayerList.getRowRight();
        int n4 = this.font.width(BLOCKING_HINT) + 40;
        int n5 = 64 + 16 * this.backgroundUnits();
        int n6 = (this.width - n4) / 2;
        this.allButton = this.addButton(new Button(n2, 45, n, 20, TAB_ALL, button -> this.showPage(Page.ALL)));
        this.hiddenButton = this.addButton(new Button((n2 + n3 - n) / 2 + 1, 45, n, 20, TAB_HIDDEN, button -> this.showPage(Page.HIDDEN)));
        this.blockedButton = this.addButton(new Button(n3 - n + 1, 45, n, 20, TAB_BLOCKED, button -> this.showPage(Page.BLOCKED)));
        this.blockingHintButton = this.addButton(new Button(n6, n5, n4, 20, BLOCKING_HINT, button -> this.minecraft.setScreen(new ConfirmLinkScreen(bl -> {
            if (bl) {
                Util.getPlatform().openUri("https://aka.ms/javablocking");
            }
            this.minecraft.setScreen(this);
        }, "https://aka.ms/javablocking", true))));
        String string = this.searchBox != null ? this.searchBox.getValue() : "";
        this.searchBox = new EditBox(this.font, this.marginX() + 28, 78, 196, 16, SEARCH_HINT){

            @Override
            protected MutableComponent createNarrationMessage() {
                if (!SocialInteractionsScreen.this.searchBox.getValue().isEmpty() && SocialInteractionsScreen.this.socialInteractionsPlayerList.isEmpty()) {
                    return super.createNarrationMessage().append(", ").append(EMPTY_SEARCH);
                }
                return super.createNarrationMessage();
            }
        };
        this.searchBox.setMaxLength(16);
        this.searchBox.setBordered(false);
        this.searchBox.setVisible(true);
        this.searchBox.setTextColor(16777215);
        this.searchBox.setValue(string);
        this.searchBox.setResponder(this::checkSearchStringUpdate);
        this.children.add(this.searchBox);
        this.children.add(this.socialInteractionsPlayerList);
        this.initialized = true;
        this.showPage(this.page);
    }

    private void showPage(Page page) {
        Object object;
        this.page = page;
        this.allButton.setMessage(TAB_ALL);
        this.hiddenButton.setMessage(TAB_HIDDEN);
        this.blockedButton.setMessage(TAB_BLOCKED);
        switch (page) {
            case ALL: {
                this.allButton.setMessage(TAB_ALL_SELECTED);
                object = this.minecraft.player.connection.getOnlinePlayerIds();
                break;
            }
            case HIDDEN: {
                this.hiddenButton.setMessage(TAB_HIDDEN_SELECTED);
                object = this.minecraft.getPlayerSocialManager().getHiddenPlayers();
                break;
            }
            case BLOCKED: {
                this.blockedButton.setMessage(TAB_BLOCKED_SELECTED);
                PlayerSocialManager playerSocialManager = this.minecraft.getPlayerSocialManager();
                object = this.minecraft.player.connection.getOnlinePlayerIds().stream().filter(playerSocialManager::isBlocked).collect(Collectors.toSet());
                break;
            }
            default: {
                object = ImmutableList.of();
            }
        }
        this.page = page;
        this.socialInteractionsPlayerList.updatePlayerList((Collection<UUID>)object, this.socialInteractionsPlayerList.getScrollAmount());
        if (!this.searchBox.getValue().isEmpty() && this.socialInteractionsPlayerList.isEmpty() && !this.searchBox.isFocused()) {
            NarratorChatListener.INSTANCE.sayNow(EMPTY_SEARCH.getString());
        } else if (object.isEmpty()) {
            if (page == Page.HIDDEN) {
                NarratorChatListener.INSTANCE.sayNow(EMPTY_HIDDEN.getString());
            } else if (page == Page.BLOCKED) {
                NarratorChatListener.INSTANCE.sayNow(EMPTY_BLOCKED.getString());
            }
        }
    }

    @Override
    public void removed() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }

    @Override
    public void renderBackground(PoseStack poseStack) {
        int n = this.marginX() + 3;
        super.renderBackground(poseStack);
        this.minecraft.getTextureManager().bind(SOCIAL_INTERACTIONS_LOCATION);
        this.blit(poseStack, n, 64, 1, 1, 236, 8);
        int n2 = this.backgroundUnits();
        for (int i = 0; i < n2; ++i) {
            this.blit(poseStack, n, 72 + 16 * i, 1, 10, 236, 16);
        }
        this.blit(poseStack, n, 72 + 16 * n2, 1, 27, 236, 8);
        this.blit(poseStack, n + 10, 76, 243, 1, 12, 12);
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.updateServerLabel(this.minecraft);
        this.renderBackground(poseStack);
        if (this.serverLabel != null) {
            SocialInteractionsScreen.drawString(poseStack, this.minecraft.font, this.serverLabel, this.marginX() + 8, 35, -1);
        }
        if (!this.socialInteractionsPlayerList.isEmpty()) {
            this.socialInteractionsPlayerList.render(poseStack, n, n2, f);
        } else if (!this.searchBox.getValue().isEmpty()) {
            SocialInteractionsScreen.drawCenteredString(poseStack, this.minecraft.font, EMPTY_SEARCH, this.width / 2, (78 + this.listEnd()) / 2, -1);
        } else {
            switch (this.page) {
                case HIDDEN: {
                    SocialInteractionsScreen.drawCenteredString(poseStack, this.minecraft.font, EMPTY_HIDDEN, this.width / 2, (78 + this.listEnd()) / 2, -1);
                    break;
                }
                case BLOCKED: {
                    SocialInteractionsScreen.drawCenteredString(poseStack, this.minecraft.font, EMPTY_BLOCKED, this.width / 2, (78 + this.listEnd()) / 2, -1);
                }
            }
        }
        if (!this.searchBox.isFocused() && this.searchBox.getValue().isEmpty()) {
            SocialInteractionsScreen.drawString(poseStack, this.minecraft.font, SEARCH_HINT, this.searchBox.x, this.searchBox.y, -1);
        } else {
            this.searchBox.render(poseStack, n, n2, f);
        }
        this.blockingHintButton.visible = this.page == Page.BLOCKED;
        super.render(poseStack, n, n2, f);
        if (this.postRenderRunnable != null) {
            this.postRenderRunnable.run();
        }
    }

    @Override
    public boolean mouseClicked(double d, double d2, int n) {
        if (this.searchBox.isFocused()) {
            this.searchBox.mouseClicked(d, d2, n);
        }
        return super.mouseClicked(d, d2, n) || this.socialInteractionsPlayerList.mouseClicked(d, d2, n);
    }

    @Override
    public boolean keyPressed(int n, int n2, int n3) {
        if (!this.searchBox.isFocused() && this.minecraft.options.keySocialInteractions.matches(n, n2)) {
            this.minecraft.setScreen(null);
            return true;
        }
        return super.keyPressed(n, n2, n3);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void checkSearchStringUpdate(String string) {
        if (!(string = string.toLowerCase(Locale.ROOT)).equals(this.lastSearch)) {
            this.socialInteractionsPlayerList.setFilter(string);
            this.lastSearch = string;
            this.showPage(this.page);
        }
    }

    private void updateServerLabel(Minecraft minecraft) {
        int n = minecraft.getConnection().getOnlinePlayers().size();
        if (this.playerCount != n) {
            String string = "";
            ServerData serverData = minecraft.getCurrentServer();
            if (minecraft.isLocalServer()) {
                string = minecraft.getSingleplayerServer().getMotd();
            } else if (serverData != null) {
                string = serverData.name;
            }
            this.serverLabel = n > 1 ? new TranslatableComponent("gui.socialInteractions.server_label.multiple", string, n) : new TranslatableComponent("gui.socialInteractions.server_label.single", string, n);
            this.playerCount = n;
        }
    }

    public void onAddPlayer(PlayerInfo playerInfo) {
        this.socialInteractionsPlayerList.addPlayer(playerInfo, this.page);
    }

    public void onRemovePlayer(UUID uUID) {
        this.socialInteractionsPlayerList.removePlayer(uUID);
    }

    public void setPostRenderRunnable(@Nullable Runnable runnable) {
        this.postRenderRunnable = runnable;
    }

    public static enum Page {
        ALL,
        HIDDEN,
        BLOCKED;
        
    }

}

