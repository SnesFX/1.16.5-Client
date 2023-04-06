/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.hash.HashCode
 *  com.google.common.hash.Hashing
 *  com.google.common.util.concurrent.ThreadFactoryBuilder
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.Validate
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.gui.screens.multiplayer;

import com.google.common.collect.Lists;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.ServerStatusPinger;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.server.LanServer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerSelectionList
extends ObjectSelectionList<Entry> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ThreadPoolExecutor THREAD_POOL = new ScheduledThreadPoolExecutor(5, new ThreadFactoryBuilder().setNameFormat("Server Pinger #%d").setDaemon(true).setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler)new DefaultUncaughtExceptionHandler(LOGGER)).build());
    private static final ResourceLocation ICON_MISSING = new ResourceLocation("textures/misc/unknown_server.png");
    private static final ResourceLocation ICON_OVERLAY_LOCATION = new ResourceLocation("textures/gui/server_selection.png");
    private static final Component SCANNING_LABEL = new TranslatableComponent("lanServer.scanning");
    private static final Component CANT_RESOLVE_TEXT = new TranslatableComponent("multiplayer.status.cannot_resolve").withStyle(ChatFormatting.DARK_RED);
    private static final Component CANT_CONNECT_TEXT = new TranslatableComponent("multiplayer.status.cannot_connect").withStyle(ChatFormatting.DARK_RED);
    private static final Component INCOMPATIBLE_TOOLTIP = new TranslatableComponent("multiplayer.status.incompatible");
    private static final Component NO_CONNECTION_TOOLTIP = new TranslatableComponent("multiplayer.status.no_connection");
    private static final Component PINGING_TOOLTIP = new TranslatableComponent("multiplayer.status.pinging");
    private final JoinMultiplayerScreen screen;
    private final List<OnlineServerEntry> onlineServers = Lists.newArrayList();
    private final Entry lanHeader = new LANHeader();
    private final List<NetworkServerEntry> networkServers = Lists.newArrayList();

    public ServerSelectionList(JoinMultiplayerScreen joinMultiplayerScreen, Minecraft minecraft, int n, int n2, int n3, int n4, int n5) {
        super(minecraft, n, n2, n3, n4, n5);
        this.screen = joinMultiplayerScreen;
    }

    private void refreshEntries() {
        this.clearEntries();
        this.onlineServers.forEach(this::addEntry);
        this.addEntry(this.lanHeader);
        this.networkServers.forEach(this::addEntry);
    }

    @Override
    public void setSelected(@Nullable Entry entry) {
        super.setSelected(entry);
        if (this.getSelected() instanceof OnlineServerEntry) {
            NarratorChatListener.INSTANCE.sayNow(new TranslatableComponent("narrator.select", OnlineServerEntry.access$000((OnlineServerEntry)((OnlineServerEntry)this.getSelected())).name).getString());
        }
        this.screen.onSelectedChange();
    }

    @Override
    public boolean keyPressed(int n, int n2, int n3) {
        Entry entry = (Entry)this.getSelected();
        return entry != null && entry.keyPressed(n, n2, n3) || super.keyPressed(n, n2, n3);
    }

    @Override
    protected void moveSelection(AbstractSelectionList.SelectionDirection selectionDirection) {
        this.moveSelection(selectionDirection, entry -> !(entry instanceof LANHeader));
    }

    public void updateOnlineServers(ServerList serverList) {
        this.onlineServers.clear();
        for (int i = 0; i < serverList.size(); ++i) {
            this.onlineServers.add(new OnlineServerEntry(this.screen, serverList.get(i)));
        }
        this.refreshEntries();
    }

    public void updateNetworkServers(List<LanServer> list) {
        this.networkServers.clear();
        for (LanServer lanServer : list) {
            this.networkServers.add(new NetworkServerEntry(this.screen, lanServer));
        }
        this.refreshEntries();
    }

    @Override
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 30;
    }

    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 85;
    }

    @Override
    protected boolean isFocused() {
        return this.screen.getFocused() == this;
    }

    public class OnlineServerEntry
    extends Entry {
        private final JoinMultiplayerScreen screen;
        private final Minecraft minecraft;
        private final ServerData serverData;
        private final ResourceLocation iconLocation;
        private String lastIconB64;
        private DynamicTexture icon;
        private long lastClickTime;

        protected OnlineServerEntry(JoinMultiplayerScreen joinMultiplayerScreen, ServerData serverData) {
            this.screen = joinMultiplayerScreen;
            this.serverData = serverData;
            this.minecraft = Minecraft.getInstance();
            this.iconLocation = new ResourceLocation("servers/" + (Object)Hashing.sha1().hashUnencodedChars((CharSequence)serverData.ip) + "/icon");
            this.icon = (DynamicTexture)this.minecraft.getTextureManager().getTexture(this.iconLocation);
        }

        @Override
        public void render(PoseStack poseStack, int n, int n2, int n3, int n4, int n5, int n6, int n7, boolean bl, float f) {
            int n8;
            List<Component> list;
            Component component;
            if (!this.serverData.pinged) {
                this.serverData.pinged = true;
                this.serverData.ping = -2L;
                this.serverData.motd = TextComponent.EMPTY;
                this.serverData.status = TextComponent.EMPTY;
                THREAD_POOL.submit(() -> {
                    try {
                        this.screen.getPinger().pingServer(this.serverData, () -> this.minecraft.execute(this::updateServerList));
                    }
                    catch (UnknownHostException unknownHostException) {
                        this.serverData.ping = -1L;
                        this.serverData.motd = CANT_RESOLVE_TEXT;
                    }
                    catch (Exception exception) {
                        this.serverData.ping = -1L;
                        this.serverData.motd = CANT_CONNECT_TEXT;
                    }
                });
            }
            boolean bl2 = this.serverData.protocol != SharedConstants.getCurrentVersion().getProtocolVersion();
            this.minecraft.font.draw(poseStack, this.serverData.name, (float)(n3 + 32 + 3), (float)(n2 + 1), 16777215);
            List<FormattedCharSequence> list2 = this.minecraft.font.split(this.serverData.motd, n4 - 32 - 2);
            for (int i = 0; i < Math.min(list2.size(), 2); ++i) {
                this.minecraft.font.getClass();
                this.minecraft.font.draw(poseStack, list2.get(i), (float)(n3 + 32 + 3), (float)(n2 + 12 + 9 * i), 8421504);
            }
            Component component2 = bl2 ? this.serverData.version.copy().withStyle(ChatFormatting.RED) : this.serverData.status;
            int n9 = this.minecraft.font.width(component2);
            this.minecraft.font.draw(poseStack, component2, (float)(n3 + n4 - n9 - 15 - 2), (float)(n2 + 1), 8421504);
            int n10 = 0;
            if (bl2) {
                n8 = 5;
                component = INCOMPATIBLE_TOOLTIP;
                list = this.serverData.playerList;
            } else if (this.serverData.pinged && this.serverData.ping != -2L) {
                n8 = this.serverData.ping < 0L ? 5 : (this.serverData.ping < 150L ? 0 : (this.serverData.ping < 300L ? 1 : (this.serverData.ping < 600L ? 2 : (this.serverData.ping < 1000L ? 3 : 4))));
                if (this.serverData.ping < 0L) {
                    component = NO_CONNECTION_TOOLTIP;
                    list = Collections.emptyList();
                } else {
                    component = new TranslatableComponent("multiplayer.status.ping", this.serverData.ping);
                    list = this.serverData.playerList;
                }
            } else {
                n10 = 1;
                n8 = (int)(Util.getMillis() / 100L + (long)(n * 2) & 7L);
                if (n8 > 4) {
                    n8 = 8 - n8;
                }
                component = PINGING_TOOLTIP;
                list = Collections.emptyList();
            }
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.minecraft.getTextureManager().bind(GuiComponent.GUI_ICONS_LOCATION);
            GuiComponent.blit(poseStack, n3 + n4 - 15, n2, n10 * 10, 176 + n8 * 8, 10, 8, 256, 256);
            String string = this.serverData.getIconB64();
            if (!Objects.equals(string, this.lastIconB64)) {
                if (this.uploadServerIcon(string)) {
                    this.lastIconB64 = string;
                } else {
                    this.serverData.setIconB64(null);
                    this.updateServerList();
                }
            }
            if (this.icon != null) {
                this.drawIcon(poseStack, n3, n2, this.iconLocation);
            } else {
                this.drawIcon(poseStack, n3, n2, ICON_MISSING);
            }
            int n11 = n6 - n3;
            int n12 = n7 - n2;
            if (n11 >= n4 - 15 && n11 <= n4 - 5 && n12 >= 0 && n12 <= 8) {
                this.screen.setToolTip(Collections.singletonList(component));
            } else if (n11 >= n4 - n9 - 15 - 2 && n11 <= n4 - 15 - 2 && n12 >= 0 && n12 <= 8) {
                this.screen.setToolTip(list);
            }
            if (this.minecraft.options.touchscreen || bl) {
                this.minecraft.getTextureManager().bind(ICON_OVERLAY_LOCATION);
                GuiComponent.fill(poseStack, n3, n2, n3 + 32, n2 + 32, -1601138544);
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                int n13 = n6 - n3;
                int n14 = n7 - n2;
                if (this.canJoin()) {
                    if (n13 < 32 && n13 > 16) {
                        GuiComponent.blit(poseStack, n3, n2, 0.0f, 32.0f, 32, 32, 256, 256);
                    } else {
                        GuiComponent.blit(poseStack, n3, n2, 0.0f, 0.0f, 32, 32, 256, 256);
                    }
                }
                if (n > 0) {
                    if (n13 < 16 && n14 < 16) {
                        GuiComponent.blit(poseStack, n3, n2, 96.0f, 32.0f, 32, 32, 256, 256);
                    } else {
                        GuiComponent.blit(poseStack, n3, n2, 96.0f, 0.0f, 32, 32, 256, 256);
                    }
                }
                if (n < this.screen.getServers().size() - 1) {
                    if (n13 < 16 && n14 > 16) {
                        GuiComponent.blit(poseStack, n3, n2, 64.0f, 32.0f, 32, 32, 256, 256);
                    } else {
                        GuiComponent.blit(poseStack, n3, n2, 64.0f, 0.0f, 32, 32, 256, 256);
                    }
                }
            }
        }

        public void updateServerList() {
            this.screen.getServers().save();
        }

        protected void drawIcon(PoseStack poseStack, int n, int n2, ResourceLocation resourceLocation) {
            this.minecraft.getTextureManager().bind(resourceLocation);
            RenderSystem.enableBlend();
            GuiComponent.blit(poseStack, n, n2, 0.0f, 0.0f, 32, 32, 32, 32);
            RenderSystem.disableBlend();
        }

        private boolean canJoin() {
            return true;
        }

        private boolean uploadServerIcon(@Nullable String string) {
            if (string == null) {
                this.minecraft.getTextureManager().release(this.iconLocation);
                if (this.icon != null && this.icon.getPixels() != null) {
                    this.icon.getPixels().close();
                }
                this.icon = null;
            } else {
                try {
                    NativeImage nativeImage = NativeImage.fromBase64(string);
                    Validate.validState((boolean)(nativeImage.getWidth() == 64), (String)"Must be 64 pixels wide", (Object[])new Object[0]);
                    Validate.validState((boolean)(nativeImage.getHeight() == 64), (String)"Must be 64 pixels high", (Object[])new Object[0]);
                    if (this.icon == null) {
                        this.icon = new DynamicTexture(nativeImage);
                    } else {
                        this.icon.setPixels(nativeImage);
                        this.icon.upload();
                    }
                    this.minecraft.getTextureManager().register(this.iconLocation, (AbstractTexture)this.icon);
                }
                catch (Throwable throwable) {
                    LOGGER.error("Invalid icon for server {} ({})", (Object)this.serverData.name, (Object)this.serverData.ip, (Object)throwable);
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean keyPressed(int n, int n2, int n3) {
            if (Screen.hasShiftDown()) {
                ServerSelectionList serverSelectionList = this.screen.serverSelectionList;
                int n4 = serverSelectionList.children().indexOf(this);
                if (n == 264 && n4 < this.screen.getServers().size() - 1 || n == 265 && n4 > 0) {
                    this.swap(n4, n == 264 ? n4 + 1 : n4 - 1);
                    return true;
                }
            }
            return super.keyPressed(n, n2, n3);
        }

        private void swap(int n, int n2) {
            this.screen.getServers().swap(n, n2);
            this.screen.serverSelectionList.updateOnlineServers(this.screen.getServers());
            Entry entry = (Entry)this.screen.serverSelectionList.children().get(n2);
            this.screen.serverSelectionList.setSelected(entry);
            ServerSelectionList.this.ensureVisible(entry);
        }

        @Override
        public boolean mouseClicked(double d, double d2, int n) {
            double d3 = d - (double)ServerSelectionList.this.getRowLeft();
            double d4 = d2 - (double)ServerSelectionList.this.getRowTop(ServerSelectionList.this.children().indexOf(this));
            if (d3 <= 32.0) {
                if (d3 < 32.0 && d3 > 16.0 && this.canJoin()) {
                    this.screen.setSelected(this);
                    this.screen.joinSelectedServer();
                    return true;
                }
                int n2 = this.screen.serverSelectionList.children().indexOf(this);
                if (d3 < 16.0 && d4 < 16.0 && n2 > 0) {
                    this.swap(n2, n2 - 1);
                    return true;
                }
                if (d3 < 16.0 && d4 > 16.0 && n2 < this.screen.getServers().size() - 1) {
                    this.swap(n2, n2 + 1);
                    return true;
                }
            }
            this.screen.setSelected(this);
            if (Util.getMillis() - this.lastClickTime < 250L) {
                this.screen.joinSelectedServer();
            }
            this.lastClickTime = Util.getMillis();
            return false;
        }

        public ServerData getServerData() {
            return this.serverData;
        }

        static /* synthetic */ ServerData access$000(OnlineServerEntry onlineServerEntry) {
            return onlineServerEntry.serverData;
        }
    }

    public static class NetworkServerEntry
    extends Entry {
        private static final Component LAN_SERVER_HEADER = new TranslatableComponent("lanServer.title");
        private static final Component HIDDEN_ADDRESS_TEXT = new TranslatableComponent("selectServer.hiddenAddress");
        private final JoinMultiplayerScreen screen;
        protected final Minecraft minecraft;
        protected final LanServer serverData;
        private long lastClickTime;

        protected NetworkServerEntry(JoinMultiplayerScreen joinMultiplayerScreen, LanServer lanServer) {
            this.screen = joinMultiplayerScreen;
            this.serverData = lanServer;
            this.minecraft = Minecraft.getInstance();
        }

        @Override
        public void render(PoseStack poseStack, int n, int n2, int n3, int n4, int n5, int n6, int n7, boolean bl, float f) {
            this.minecraft.font.draw(poseStack, LAN_SERVER_HEADER, (float)(n3 + 32 + 3), (float)(n2 + 1), 16777215);
            this.minecraft.font.draw(poseStack, this.serverData.getMotd(), (float)(n3 + 32 + 3), (float)(n2 + 12), 8421504);
            if (this.minecraft.options.hideServerAddress) {
                this.minecraft.font.draw(poseStack, HIDDEN_ADDRESS_TEXT, (float)(n3 + 32 + 3), (float)(n2 + 12 + 11), 3158064);
            } else {
                this.minecraft.font.draw(poseStack, this.serverData.getAddress(), (float)(n3 + 32 + 3), (float)(n2 + 12 + 11), 3158064);
            }
        }

        @Override
        public boolean mouseClicked(double d, double d2, int n) {
            this.screen.setSelected(this);
            if (Util.getMillis() - this.lastClickTime < 250L) {
                this.screen.joinSelectedServer();
            }
            this.lastClickTime = Util.getMillis();
            return false;
        }

        public LanServer getServerData() {
            return this.serverData;
        }
    }

    public static class LANHeader
    extends Entry {
        private final Minecraft minecraft = Minecraft.getInstance();

        @Override
        public void render(PoseStack poseStack, int n, int n2, int n3, int n4, int n5, int n6, int n7, boolean bl, float f) {
            String string;
            this.minecraft.font.getClass();
            int n8 = n2 + n5 / 2 - 9 / 2;
            this.minecraft.font.draw(poseStack, SCANNING_LABEL, (float)(this.minecraft.screen.width / 2 - this.minecraft.font.width(SCANNING_LABEL) / 2), (float)n8, 16777215);
            switch ((int)(Util.getMillis() / 300L % 4L)) {
                default: {
                    string = "O o o";
                    break;
                }
                case 1: 
                case 3: {
                    string = "o O o";
                    break;
                }
                case 2: {
                    string = "o o O";
                }
            }
            this.minecraft.font.getClass();
            this.minecraft.font.draw(poseStack, string, (float)(this.minecraft.screen.width / 2 - this.minecraft.font.width(string) / 2), (float)(n8 + 9), 8421504);
        }
    }

    public static abstract class Entry
    extends ObjectSelectionList.Entry<Entry> {
    }

}

