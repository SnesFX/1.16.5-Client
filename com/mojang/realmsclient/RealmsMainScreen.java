/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.util.concurrent.RateLimiter
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.mojang.realmsclient;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.KeyCombo;
import com.mojang.realmsclient.client.Ping;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.PingResult;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerPing;
import com.mojang.realmsclient.dto.RealmsServerPlayerList;
import com.mojang.realmsclient.dto.RealmsServerPlayerLists;
import com.mojang.realmsclient.dto.RegionPingResult;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import com.mojang.realmsclient.gui.screens.RealmsClientOutdatedScreen;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsCreateRealmScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsParentalConsentScreen;
import com.mojang.realmsclient.gui.screens.RealmsPendingInvitesScreen;
import com.mojang.realmsclient.util.RealmsPersistence;
import com.mojang.realmsclient.util.RealmsTextureManager;
import com.mojang.realmsclient.util.task.GetServerDetailsTask;
import com.mojang.realmsclient.util.task.LongRunningTask;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.TickableWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.NarrationHelper;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsMainScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation ON_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/on_icon.png");
    private static final ResourceLocation OFF_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/off_icon.png");
    private static final ResourceLocation EXPIRED_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/expired_icon.png");
    private static final ResourceLocation EXPIRES_SOON_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/expires_soon_icon.png");
    private static final ResourceLocation LEAVE_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/leave_icon.png");
    private static final ResourceLocation INVITATION_ICONS_LOCATION = new ResourceLocation("realms", "textures/gui/realms/invitation_icons.png");
    private static final ResourceLocation INVITE_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/invite_icon.png");
    private static final ResourceLocation WORLDICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/world_icon.png");
    private static final ResourceLocation LOGO_LOCATION = new ResourceLocation("realms", "textures/gui/title/realms.png");
    private static final ResourceLocation CONFIGURE_LOCATION = new ResourceLocation("realms", "textures/gui/realms/configure_icon.png");
    private static final ResourceLocation QUESTIONMARK_LOCATION = new ResourceLocation("realms", "textures/gui/realms/questionmark.png");
    private static final ResourceLocation NEWS_LOCATION = new ResourceLocation("realms", "textures/gui/realms/news_icon.png");
    private static final ResourceLocation POPUP_LOCATION = new ResourceLocation("realms", "textures/gui/realms/popup.png");
    private static final ResourceLocation DARKEN_LOCATION = new ResourceLocation("realms", "textures/gui/realms/darken.png");
    private static final ResourceLocation CROSS_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/cross_icon.png");
    private static final ResourceLocation TRIAL_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/trial_icon.png");
    private static final ResourceLocation BUTTON_LOCATION = new ResourceLocation("minecraft", "textures/gui/widgets.png");
    private static final Component NO_PENDING_INVITES_TEXT = new TranslatableComponent("mco.invites.nopending");
    private static final Component PENDING_INVITES_TEXT = new TranslatableComponent("mco.invites.pending");
    private static final List<Component> TRIAL_MESSAGE_LINES = ImmutableList.of((Object)new TranslatableComponent("mco.trial.message.line1"), (Object)new TranslatableComponent("mco.trial.message.line2"));
    private static final Component SERVER_UNITIALIZED_TEXT = new TranslatableComponent("mco.selectServer.uninitialized");
    private static final Component SUBSCRIPTION_EXPIRED_TEXT = new TranslatableComponent("mco.selectServer.expiredList");
    private static final Component SUBSCRIPTION_RENEW_TEXT = new TranslatableComponent("mco.selectServer.expiredRenew");
    private static final Component TRIAL_EXPIRED_TEXT = new TranslatableComponent("mco.selectServer.expiredTrial");
    private static final Component SUBSCRIPTION_CREATE_TEXT = new TranslatableComponent("mco.selectServer.expiredSubscribe");
    private static final Component SELECT_MINIGAME_PREFIX = new TranslatableComponent("mco.selectServer.minigame").append(" ");
    private static final Component POPUP_TEXT = new TranslatableComponent("mco.selectServer.popup");
    private static final Component SERVER_EXPIRED_TOOLTIP = new TranslatableComponent("mco.selectServer.expired");
    private static final Component SERVER_EXPIRES_SOON_TOOLTIP = new TranslatableComponent("mco.selectServer.expires.soon");
    private static final Component SERVER_EXPIRES_IN_DAY_TOOLTIP = new TranslatableComponent("mco.selectServer.expires.day");
    private static final Component SERVER_OPEN_TOOLTIP = new TranslatableComponent("mco.selectServer.open");
    private static final Component SERVER_CLOSED_TOOLTIP = new TranslatableComponent("mco.selectServer.closed");
    private static final Component LEAVE_SERVER_TOOLTIP = new TranslatableComponent("mco.selectServer.leave");
    private static final Component CONFIGURE_SERVER_TOOLTIP = new TranslatableComponent("mco.selectServer.configure");
    private static final Component SERVER_INFO_TOOLTIP = new TranslatableComponent("mco.selectServer.info");
    private static final Component NEWS_TOOLTIP = new TranslatableComponent("mco.news");
    private static List<ResourceLocation> teaserImages = ImmutableList.of();
    private static final RealmsDataFetcher REALMS_DATA_FETCHER = new RealmsDataFetcher();
    private static boolean overrideConfigure;
    private static int lastScrollYPosition;
    private static volatile boolean hasParentalConsent;
    private static volatile boolean checkedParentalConsent;
    private static volatile boolean checkedClientCompatability;
    private static Screen realmsGenericErrorScreen;
    private static boolean regionsPinged;
    private final RateLimiter inviteNarrationLimiter;
    private boolean dontSetConnectedToRealms;
    private final Screen lastScreen;
    private volatile RealmSelectionList realmSelectionList;
    private long selectedServerId = -1L;
    private Button playButton;
    private Button backButton;
    private Button renewButton;
    private Button configureButton;
    private Button leaveButton;
    private List<Component> toolTip;
    private List<RealmsServer> realmsServers = Lists.newArrayList();
    private volatile int numberOfPendingInvites;
    private int animTick;
    private boolean hasFetchedServers;
    private boolean popupOpenedByUser;
    private boolean justClosedPopup;
    private volatile boolean trialsAvailable;
    private volatile boolean createdTrial;
    private volatile boolean showingPopup;
    private volatile boolean hasUnreadNews;
    private volatile String newsLink;
    private int carouselIndex;
    private int carouselTick;
    private boolean hasSwitchedCarouselImage;
    private List<KeyCombo> keyCombos;
    private int clicks;
    private ReentrantLock connectLock = new ReentrantLock();
    private MultiLineLabel formattedPopup = MultiLineLabel.EMPTY;
    private HoveredElement hoveredElement;
    private Button showPopupButton;
    private Button pendingInvitesButton;
    private Button newsButton;
    private Button createTrialButton;
    private Button buyARealmButton;
    private Button closeButton;

    public RealmsMainScreen(Screen screen) {
        this.lastScreen = screen;
        this.inviteNarrationLimiter = RateLimiter.create((double)0.01666666753590107);
    }

    private boolean shouldShowMessageInList() {
        if (!RealmsMainScreen.hasParentalConsent() || !this.hasFetchedServers) {
            return false;
        }
        if (this.trialsAvailable && !this.createdTrial) {
            return true;
        }
        for (RealmsServer realmsServer : this.realmsServers) {
            if (!realmsServer.ownerUUID.equals(this.minecraft.getUser().getUuid())) continue;
            return false;
        }
        return true;
    }

    public boolean shouldShowPopup() {
        if (!RealmsMainScreen.hasParentalConsent() || !this.hasFetchedServers) {
            return false;
        }
        if (this.popupOpenedByUser) {
            return true;
        }
        if (this.trialsAvailable && !this.createdTrial && this.realmsServers.isEmpty()) {
            return true;
        }
        return this.realmsServers.isEmpty();
    }

    @Override
    public void init() {
        this.keyCombos = Lists.newArrayList((Object[])new KeyCombo[]{new KeyCombo(new char[]{'3', '2', '1', '4', '5', '6'}, () -> {
            overrideConfigure = !overrideConfigure;
        }), new KeyCombo(new char[]{'9', '8', '7', '1', '2', '3'}, () -> {
            if (RealmsClient.currentEnvironment == RealmsClient.Environment.STAGE) {
                this.switchToProd();
            } else {
                this.switchToStage();
            }
        }), new KeyCombo(new char[]{'9', '8', '7', '4', '5', '6'}, () -> {
            if (RealmsClient.currentEnvironment == RealmsClient.Environment.LOCAL) {
                this.switchToProd();
            } else {
                this.switchToLocal();
            }
        })});
        if (realmsGenericErrorScreen != null) {
            this.minecraft.setScreen(realmsGenericErrorScreen);
            return;
        }
        this.connectLock = new ReentrantLock();
        if (checkedClientCompatability && !RealmsMainScreen.hasParentalConsent()) {
            this.checkParentalConsent();
        }
        this.checkClientCompatability();
        this.checkUnreadNews();
        if (!this.dontSetConnectedToRealms) {
            this.minecraft.setConnectedToRealms(false);
        }
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        if (RealmsMainScreen.hasParentalConsent()) {
            REALMS_DATA_FETCHER.forceUpdate();
        }
        this.showingPopup = false;
        if (RealmsMainScreen.hasParentalConsent() && this.hasFetchedServers) {
            this.addButtons();
        }
        this.realmSelectionList = new RealmSelectionList();
        if (lastScrollYPosition != -1) {
            this.realmSelectionList.setScrollAmount(lastScrollYPosition);
        }
        this.addWidget(this.realmSelectionList);
        this.magicalSpecialHackyFocus(this.realmSelectionList);
        this.formattedPopup = MultiLineLabel.create(this.font, (FormattedText)POPUP_TEXT, 100);
    }

    private static boolean hasParentalConsent() {
        return checkedParentalConsent && hasParentalConsent;
    }

    public void addButtons() {
        this.leaveButton = this.addButton(new Button(this.width / 2 - 202, this.height - 32, 90, 20, new TranslatableComponent("mco.selectServer.leave"), button -> this.leaveClicked(this.findServer(this.selectedServerId))));
        this.configureButton = this.addButton(new Button(this.width / 2 - 190, this.height - 32, 90, 20, new TranslatableComponent("mco.selectServer.configure"), button -> this.configureClicked(this.findServer(this.selectedServerId))));
        this.playButton = this.addButton(new Button(this.width / 2 - 93, this.height - 32, 90, 20, new TranslatableComponent("mco.selectServer.play"), button -> {
            RealmsServer realmsServer = this.findServer(this.selectedServerId);
            if (realmsServer == null) {
                return;
            }
            this.play(realmsServer, this);
        }));
        this.backButton = this.addButton(new Button(this.width / 2 + 4, this.height - 32, 90, 20, CommonComponents.GUI_BACK, button -> {
            if (!this.justClosedPopup) {
                this.minecraft.setScreen(this.lastScreen);
            }
        }));
        this.renewButton = this.addButton(new Button(this.width / 2 + 100, this.height - 32, 90, 20, new TranslatableComponent("mco.selectServer.expiredRenew"), button -> this.onRenew()));
        this.pendingInvitesButton = this.addButton(new PendingInvitesButton());
        this.newsButton = this.addButton(new NewsButton());
        this.showPopupButton = this.addButton(new ShowPopupButton());
        this.closeButton = this.addButton(new CloseButton());
        this.createTrialButton = this.addButton(new Button(this.width / 2 + 52, this.popupY0() + 137 - 20, 98, 20, new TranslatableComponent("mco.selectServer.trial"), button -> {
            if (!this.trialsAvailable || this.createdTrial) {
                return;
            }
            Util.getPlatform().openUri("https://aka.ms/startjavarealmstrial");
            this.minecraft.setScreen(this.lastScreen);
        }));
        this.buyARealmButton = this.addButton(new Button(this.width / 2 + 52, this.popupY0() + 160 - 20, 98, 20, new TranslatableComponent("mco.selectServer.buy"), button -> Util.getPlatform().openUri("https://aka.ms/BuyJavaRealms")));
        RealmsServer realmsServer = this.findServer(this.selectedServerId);
        this.updateButtonStates(realmsServer);
    }

    private void updateButtonStates(@Nullable RealmsServer realmsServer) {
        boolean bl;
        this.playButton.active = this.shouldPlayButtonBeActive(realmsServer) && !this.shouldShowPopup();
        this.renewButton.visible = this.shouldRenewButtonBeActive(realmsServer);
        this.configureButton.visible = this.shouldConfigureButtonBeVisible(realmsServer);
        this.leaveButton.visible = this.shouldLeaveButtonBeVisible(realmsServer);
        this.createTrialButton.visible = bl = this.shouldShowPopup() && this.trialsAvailable && !this.createdTrial;
        this.createTrialButton.active = bl;
        this.buyARealmButton.visible = this.shouldShowPopup();
        this.closeButton.visible = this.shouldShowPopup() && this.popupOpenedByUser;
        this.renewButton.active = !this.shouldShowPopup();
        this.configureButton.active = !this.shouldShowPopup();
        this.leaveButton.active = !this.shouldShowPopup();
        this.newsButton.active = true;
        this.pendingInvitesButton.active = true;
        this.backButton.active = true;
        this.showPopupButton.active = !this.shouldShowPopup();
    }

    private boolean shouldShowPopupButton() {
        return (!this.shouldShowPopup() || this.popupOpenedByUser) && RealmsMainScreen.hasParentalConsent() && this.hasFetchedServers;
    }

    private boolean shouldPlayButtonBeActive(@Nullable RealmsServer realmsServer) {
        return realmsServer != null && !realmsServer.expired && realmsServer.state == RealmsServer.State.OPEN;
    }

    private boolean shouldRenewButtonBeActive(@Nullable RealmsServer realmsServer) {
        return realmsServer != null && realmsServer.expired && this.isSelfOwnedServer(realmsServer);
    }

    private boolean shouldConfigureButtonBeVisible(@Nullable RealmsServer realmsServer) {
        return realmsServer != null && this.isSelfOwnedServer(realmsServer);
    }

    private boolean shouldLeaveButtonBeVisible(@Nullable RealmsServer realmsServer) {
        return realmsServer != null && !this.isSelfOwnedServer(realmsServer);
    }

    @Override
    public void tick() {
        Object object;
        super.tick();
        this.justClosedPopup = false;
        ++this.animTick;
        --this.clicks;
        if (this.clicks < 0) {
            this.clicks = 0;
        }
        if (!RealmsMainScreen.hasParentalConsent()) {
            return;
        }
        REALMS_DATA_FETCHER.init();
        if (REALMS_DATA_FETCHER.isFetchedSinceLastTry(RealmsDataFetcher.Task.SERVER_LIST)) {
            boolean bl;
            object = REALMS_DATA_FETCHER.getServers();
            this.realmSelectionList.clear();
            boolean bl2 = bl = !this.hasFetchedServers;
            if (bl) {
                this.hasFetchedServers = true;
            }
            if (object != null) {
                boolean bl3 = false;
                Iterator<Object> iterator = object.iterator();
                while (iterator.hasNext()) {
                    RealmsServer realmsServer = (RealmsServer)iterator.next();
                    if (!this.isSelfOwnedNonExpiredServer(realmsServer)) continue;
                    bl3 = true;
                }
                this.realmsServers = object;
                if (this.shouldShowMessageInList()) {
                    this.realmSelectionList.addMessageEntry(new TrialEntry());
                }
                for (RealmsServer realmsServer : this.realmsServers) {
                    this.realmSelectionList.addEntry(new ServerEntry(realmsServer));
                }
                if (!regionsPinged && bl3) {
                    regionsPinged = true;
                    this.pingRegions();
                }
            }
            if (bl) {
                this.addButtons();
            }
        }
        if (REALMS_DATA_FETCHER.isFetchedSinceLastTry(RealmsDataFetcher.Task.PENDING_INVITE)) {
            this.numberOfPendingInvites = REALMS_DATA_FETCHER.getPendingInvitesCount();
            if (this.numberOfPendingInvites > 0 && this.inviteNarrationLimiter.tryAcquire(1)) {
                NarrationHelper.now(I18n.get("mco.configure.world.invite.narration", this.numberOfPendingInvites));
            }
        }
        if (REALMS_DATA_FETCHER.isFetchedSinceLastTry(RealmsDataFetcher.Task.TRIAL_AVAILABLE) && !this.createdTrial) {
            boolean bl = REALMS_DATA_FETCHER.isTrialAvailable();
            if (bl != this.trialsAvailable && this.shouldShowPopup()) {
                this.trialsAvailable = bl;
                this.showingPopup = false;
            } else {
                this.trialsAvailable = bl;
            }
        }
        if (REALMS_DATA_FETCHER.isFetchedSinceLastTry(RealmsDataFetcher.Task.LIVE_STATS)) {
            object = REALMS_DATA_FETCHER.getLivestats();
            block2 : for (RealmsServerPlayerList realmsServerPlayerList : ((RealmsServerPlayerLists)object).servers) {
                for (RealmsServer realmsServer : this.realmsServers) {
                    if (realmsServer.id != realmsServerPlayerList.serverId) continue;
                    realmsServer.updateServerPing(realmsServerPlayerList);
                    continue block2;
                }
            }
        }
        if (REALMS_DATA_FETCHER.isFetchedSinceLastTry(RealmsDataFetcher.Task.UNREAD_NEWS)) {
            this.hasUnreadNews = REALMS_DATA_FETCHER.hasUnreadNews();
            this.newsLink = REALMS_DATA_FETCHER.newsLink();
        }
        REALMS_DATA_FETCHER.markClean();
        if (this.shouldShowPopup()) {
            ++this.carouselTick;
        }
        if (this.showPopupButton != null) {
            this.showPopupButton.visible = this.shouldShowPopupButton();
        }
    }

    private void pingRegions() {
        new Thread(() -> {
            List<RegionPingResult> list = Ping.pingAllRegions();
            RealmsClient realmsClient = RealmsClient.create();
            PingResult pingResult = new PingResult();
            pingResult.pingResults = list;
            pingResult.worldIds = this.getOwnedNonExpiredWorldIds();
            try {
                realmsClient.sendPingResults(pingResult);
            }
            catch (Throwable throwable) {
                LOGGER.warn("Could not send ping result to Realms: ", throwable);
            }
        }).start();
    }

    private List<Long> getOwnedNonExpiredWorldIds() {
        ArrayList arrayList = Lists.newArrayList();
        for (RealmsServer realmsServer : this.realmsServers) {
            if (!this.isSelfOwnedNonExpiredServer(realmsServer)) continue;
            arrayList.add(realmsServer.id);
        }
        return arrayList;
    }

    @Override
    public void removed() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
        this.stopRealmsFetcher();
    }

    private void onRenew() {
        RealmsServer realmsServer = this.findServer(this.selectedServerId);
        if (realmsServer == null) {
            return;
        }
        String string = "https://aka.ms/ExtendJavaRealms?subscriptionId=" + realmsServer.remoteSubscriptionId + "&profileId=" + this.minecraft.getUser().getUuid() + "&ref=" + (realmsServer.expiredTrial ? "expiredTrial" : "expiredRealm");
        this.minecraft.keyboardHandler.setClipboard(string);
        Util.getPlatform().openUri(string);
    }

    private void checkClientCompatability() {
        if (!checkedClientCompatability) {
            checkedClientCompatability = true;
            new Thread("MCO Compatability Checker #1"){

                @Override
                public void run() {
                    RealmsClient realmsClient = RealmsClient.create();
                    try {
                        RealmsClient.CompatibleVersionResponse compatibleVersionResponse = realmsClient.clientCompatible();
                        if (compatibleVersionResponse == RealmsClient.CompatibleVersionResponse.OUTDATED) {
                            realmsGenericErrorScreen = new RealmsClientOutdatedScreen(RealmsMainScreen.this.lastScreen, true);
                            RealmsMainScreen.this.minecraft.execute(() -> RealmsMainScreen.this.minecraft.setScreen(realmsGenericErrorScreen));
                            return;
                        }
                        if (compatibleVersionResponse == RealmsClient.CompatibleVersionResponse.OTHER) {
                            realmsGenericErrorScreen = new RealmsClientOutdatedScreen(RealmsMainScreen.this.lastScreen, false);
                            RealmsMainScreen.this.minecraft.execute(() -> RealmsMainScreen.this.minecraft.setScreen(realmsGenericErrorScreen));
                            return;
                        }
                        RealmsMainScreen.this.checkParentalConsent();
                    }
                    catch (RealmsServiceException realmsServiceException) {
                        checkedClientCompatability = false;
                        LOGGER.error("Couldn't connect to realms", (Throwable)realmsServiceException);
                        if (realmsServiceException.httpResultCode == 401) {
                            realmsGenericErrorScreen = new RealmsGenericErrorScreen(new TranslatableComponent("mco.error.invalid.session.title"), new TranslatableComponent("mco.error.invalid.session.message"), RealmsMainScreen.this.lastScreen);
                            RealmsMainScreen.this.minecraft.execute(() -> RealmsMainScreen.this.minecraft.setScreen(realmsGenericErrorScreen));
                        }
                        RealmsMainScreen.this.minecraft.execute(() -> RealmsMainScreen.this.minecraft.setScreen(new RealmsGenericErrorScreen(realmsServiceException, RealmsMainScreen.this.lastScreen)));
                    }
                }
            }.start();
        }
    }

    private void checkUnreadNews() {
    }

    private void checkParentalConsent() {
        new Thread("MCO Compatability Checker #1"){

            @Override
            public void run() {
                RealmsClient realmsClient = RealmsClient.create();
                try {
                    Boolean bl = realmsClient.mcoEnabled();
                    if (bl.booleanValue()) {
                        LOGGER.info("Realms is available for this user");
                        hasParentalConsent = true;
                    } else {
                        LOGGER.info("Realms is not available for this user");
                        hasParentalConsent = false;
                        RealmsMainScreen.this.minecraft.execute(() -> RealmsMainScreen.this.minecraft.setScreen(new RealmsParentalConsentScreen(RealmsMainScreen.this.lastScreen)));
                    }
                    checkedParentalConsent = true;
                }
                catch (RealmsServiceException realmsServiceException) {
                    LOGGER.error("Couldn't connect to realms", (Throwable)realmsServiceException);
                    RealmsMainScreen.this.minecraft.execute(() -> RealmsMainScreen.this.minecraft.setScreen(new RealmsGenericErrorScreen(realmsServiceException, RealmsMainScreen.this.lastScreen)));
                }
            }
        }.start();
    }

    private void switchToStage() {
        if (RealmsClient.currentEnvironment != RealmsClient.Environment.STAGE) {
            new Thread("MCO Stage Availability Checker #1"){

                @Override
                public void run() {
                    RealmsClient realmsClient = RealmsClient.create();
                    try {
                        Boolean bl = realmsClient.stageAvailable();
                        if (bl.booleanValue()) {
                            RealmsClient.switchToStage();
                            LOGGER.info("Switched to stage");
                            REALMS_DATA_FETCHER.forceUpdate();
                        }
                    }
                    catch (RealmsServiceException realmsServiceException) {
                        LOGGER.error("Couldn't connect to Realms: " + realmsServiceException);
                    }
                }
            }.start();
        }
    }

    private void switchToLocal() {
        if (RealmsClient.currentEnvironment != RealmsClient.Environment.LOCAL) {
            new Thread("MCO Local Availability Checker #1"){

                @Override
                public void run() {
                    RealmsClient realmsClient = RealmsClient.create();
                    try {
                        Boolean bl = realmsClient.stageAvailable();
                        if (bl.booleanValue()) {
                            RealmsClient.switchToLocal();
                            LOGGER.info("Switched to local");
                            REALMS_DATA_FETCHER.forceUpdate();
                        }
                    }
                    catch (RealmsServiceException realmsServiceException) {
                        LOGGER.error("Couldn't connect to Realms: " + realmsServiceException);
                    }
                }
            }.start();
        }
    }

    private void switchToProd() {
        RealmsClient.switchToProd();
        REALMS_DATA_FETCHER.forceUpdate();
    }

    private void stopRealmsFetcher() {
        REALMS_DATA_FETCHER.stop();
    }

    private void configureClicked(RealmsServer realmsServer) {
        if (this.minecraft.getUser().getUuid().equals(realmsServer.ownerUUID) || overrideConfigure) {
            this.saveListScrollPosition();
            this.minecraft.setScreen(new RealmsConfigureWorldScreen(this, realmsServer.id));
        }
    }

    private void leaveClicked(@Nullable RealmsServer realmsServer) {
        if (realmsServer != null && !this.minecraft.getUser().getUuid().equals(realmsServer.ownerUUID)) {
            this.saveListScrollPosition();
            TranslatableComponent translatableComponent = new TranslatableComponent("mco.configure.world.leave.question.line1");
            TranslatableComponent translatableComponent2 = new TranslatableComponent("mco.configure.world.leave.question.line2");
            this.minecraft.setScreen(new RealmsLongConfirmationScreen(this::leaveServer, RealmsLongConfirmationScreen.Type.Info, translatableComponent, translatableComponent2, true));
        }
    }

    private void saveListScrollPosition() {
        lastScrollYPosition = (int)this.realmSelectionList.getScrollAmount();
    }

    @Nullable
    private RealmsServer findServer(long l) {
        for (RealmsServer realmsServer : this.realmsServers) {
            if (realmsServer.id != l) continue;
            return realmsServer;
        }
        return null;
    }

    private void leaveServer(boolean bl) {
        if (bl) {
            new Thread("Realms-leave-server"){

                @Override
                public void run() {
                    try {
                        RealmsServer realmsServer = RealmsMainScreen.this.findServer(RealmsMainScreen.this.selectedServerId);
                        if (realmsServer != null) {
                            RealmsClient realmsClient = RealmsClient.create();
                            realmsClient.uninviteMyselfFrom(realmsServer.id);
                            RealmsMainScreen.this.minecraft.execute(() -> RealmsMainScreen.this.removeServer(realmsServer));
                        }
                    }
                    catch (RealmsServiceException realmsServiceException) {
                        LOGGER.error("Couldn't configure world");
                        RealmsMainScreen.this.minecraft.execute(() -> RealmsMainScreen.this.minecraft.setScreen(new RealmsGenericErrorScreen(realmsServiceException, (Screen)RealmsMainScreen.this)));
                    }
                }
            }.start();
        }
        this.minecraft.setScreen(this);
    }

    private void removeServer(RealmsServer realmsServer) {
        REALMS_DATA_FETCHER.removeItem(realmsServer);
        this.realmsServers.remove(realmsServer);
        this.realmSelectionList.children().removeIf(entry -> entry instanceof ServerEntry && ServerEntry.access$8700((ServerEntry)((ServerEntry)entry)).id == this.selectedServerId);
        this.realmSelectionList.setSelected(null);
        this.updateButtonStates(null);
        this.selectedServerId = -1L;
        this.playButton.active = false;
    }

    public void removeSelection() {
        this.selectedServerId = -1L;
    }

    @Override
    public boolean keyPressed(int n, int n2, int n3) {
        if (n == 256) {
            this.keyCombos.forEach(KeyCombo::reset);
            this.onClosePopup();
            return true;
        }
        return super.keyPressed(n, n2, n3);
    }

    private void onClosePopup() {
        if (this.shouldShowPopup() && this.popupOpenedByUser) {
            this.popupOpenedByUser = false;
        } else {
            this.minecraft.setScreen(this.lastScreen);
        }
    }

    @Override
    public boolean charTyped(char c, int n) {
        this.keyCombos.forEach(keyCombo -> keyCombo.keyPressed(c));
        return true;
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.hoveredElement = HoveredElement.NONE;
        this.toolTip = null;
        this.renderBackground(poseStack);
        this.realmSelectionList.render(poseStack, n, n2, f);
        this.drawRealmsLogo(poseStack, this.width / 2 - 50, 7);
        if (RealmsClient.currentEnvironment == RealmsClient.Environment.STAGE) {
            this.renderStage(poseStack);
        }
        if (RealmsClient.currentEnvironment == RealmsClient.Environment.LOCAL) {
            this.renderLocal(poseStack);
        }
        if (this.shouldShowPopup()) {
            this.drawPopup(poseStack, n, n2);
        } else {
            if (this.showingPopup) {
                this.updateButtonStates(null);
                if (!this.children.contains(this.realmSelectionList)) {
                    this.children.add(this.realmSelectionList);
                }
                RealmsServer realmsServer = this.findServer(this.selectedServerId);
                this.playButton.active = this.shouldPlayButtonBeActive(realmsServer);
            }
            this.showingPopup = false;
        }
        super.render(poseStack, n, n2, f);
        if (this.toolTip != null) {
            this.renderMousehoverTooltip(poseStack, this.toolTip, n, n2);
        }
        if (this.trialsAvailable && !this.createdTrial && this.shouldShowPopup()) {
            this.minecraft.getTextureManager().bind(TRIAL_ICON_LOCATION);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            int n3 = 8;
            int n4 = 8;
            int n5 = 0;
            if ((Util.getMillis() / 800L & 1L) == 1L) {
                n5 = 8;
            }
            GuiComponent.blit(poseStack, this.createTrialButton.x + this.createTrialButton.getWidth() - 8 - 4, this.createTrialButton.y + this.createTrialButton.getHeight() / 2 - 4, 0.0f, n5, 8, 8, 8, 16);
        }
    }

    private void drawRealmsLogo(PoseStack poseStack, int n, int n2) {
        this.minecraft.getTextureManager().bind(LOGO_LOCATION);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.pushMatrix();
        RenderSystem.scalef(0.5f, 0.5f, 0.5f);
        GuiComponent.blit(poseStack, n * 2, n2 * 2 - 5, 0.0f, 0.0f, 200, 50, 200, 50);
        RenderSystem.popMatrix();
    }

    @Override
    public boolean mouseClicked(double d, double d2, int n) {
        if (this.isOutsidePopup(d, d2) && this.popupOpenedByUser) {
            this.popupOpenedByUser = false;
            this.justClosedPopup = true;
            return true;
        }
        return super.mouseClicked(d, d2, n);
    }

    private boolean isOutsidePopup(double d, double d2) {
        int n = this.popupX0();
        int n2 = this.popupY0();
        return d < (double)(n - 5) || d > (double)(n + 315) || d2 < (double)(n2 - 5) || d2 > (double)(n2 + 171);
    }

    private void drawPopup(PoseStack poseStack, int n, int n2) {
        int n3 = this.popupX0();
        int n4 = this.popupY0();
        if (!this.showingPopup) {
            RealmSelectionList realmSelectionList;
            this.carouselIndex = 0;
            this.carouselTick = 0;
            this.hasSwitchedCarouselImage = true;
            this.updateButtonStates(null);
            if (this.children.contains(this.realmSelectionList) && !this.children.remove(realmSelectionList = this.realmSelectionList)) {
                LOGGER.error("Unable to remove widget: " + realmSelectionList);
            }
            NarrationHelper.now(POPUP_TEXT.getString());
        }
        if (this.hasFetchedServers) {
            this.showingPopup = true;
        }
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 0.7f);
        RenderSystem.enableBlend();
        this.minecraft.getTextureManager().bind(DARKEN_LOCATION);
        boolean bl = false;
        int n5 = 32;
        GuiComponent.blit(poseStack, 0, 32, 0.0f, 0.0f, this.width, this.height - 40 - 32, 310, 166);
        RenderSystem.disableBlend();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bind(POPUP_LOCATION);
        GuiComponent.blit(poseStack, n3, n4, 0.0f, 0.0f, 310, 166, 310, 166);
        if (!teaserImages.isEmpty()) {
            this.minecraft.getTextureManager().bind(teaserImages.get(this.carouselIndex));
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            GuiComponent.blit(poseStack, n3 + 7, n4 + 7, 0.0f, 0.0f, 195, 152, 195, 152);
            if (this.carouselTick % 95 < 5) {
                if (!this.hasSwitchedCarouselImage) {
                    this.carouselIndex = (this.carouselIndex + 1) % teaserImages.size();
                    this.hasSwitchedCarouselImage = true;
                }
            } else {
                this.hasSwitchedCarouselImage = false;
            }
        }
        this.formattedPopup.renderLeftAlignedNoShadow(poseStack, this.width / 2 + 52, n4 + 7, 10, 5000268);
    }

    private int popupX0() {
        return (this.width - 310) / 2;
    }

    private int popupY0() {
        return this.height / 2 - 80;
    }

    private void drawInvitationPendingIcon(PoseStack poseStack, int n, int n2, int n3, int n4, boolean bl, boolean bl2) {
        int n5;
        boolean bl3;
        int n6;
        boolean bl4;
        boolean bl5;
        int n7 = this.numberOfPendingInvites;
        boolean bl6 = this.inPendingInvitationArea(n, n2);
        boolean bl7 = bl3 = bl2 && bl;
        if (bl3) {
            float f = 0.25f + (1.0f + Mth.sin((float)this.animTick * 0.5f)) * 0.25f;
            int n8 = 0xFF000000 | (int)(f * 64.0f) << 16 | (int)(f * 64.0f) << 8 | (int)(f * 64.0f) << 0;
            this.fillGradient(poseStack, n3 - 2, n4 - 2, n3 + 18, n4 + 18, n8, n8);
            n8 = 0xFF000000 | (int)(f * 255.0f) << 16 | (int)(f * 255.0f) << 8 | (int)(f * 255.0f) << 0;
            this.fillGradient(poseStack, n3 - 2, n4 - 2, n3 + 18, n4 - 1, n8, n8);
            this.fillGradient(poseStack, n3 - 2, n4 - 2, n3 - 1, n4 + 18, n8, n8);
            this.fillGradient(poseStack, n3 + 17, n4 - 2, n3 + 18, n4 + 18, n8, n8);
            this.fillGradient(poseStack, n3 - 2, n4 + 17, n3 + 18, n4 + 18, n8, n8);
        }
        this.minecraft.getTextureManager().bind(INVITE_ICON_LOCATION);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        boolean bl8 = bl2 && bl;
        float f = bl8 ? 16.0f : 0.0f;
        GuiComponent.blit(poseStack, n3, n4 - 6, f, 0.0f, 15, 25, 31, 25);
        boolean bl9 = bl5 = bl2 && n7 != 0;
        if (bl5) {
            n6 = (Math.min(n7, 6) - 1) * 8;
            n5 = (int)(Math.max(0.0f, Math.max(Mth.sin((float)(10 + this.animTick) * 0.57f), Mth.cos((float)this.animTick * 0.35f))) * -6.0f);
            this.minecraft.getTextureManager().bind(INVITATION_ICONS_LOCATION);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            float f2 = bl6 ? 8.0f : 0.0f;
            GuiComponent.blit(poseStack, n3 + 4, n4 + 4 + n5, n6, f2, 8, 8, 48, 16);
        }
        n6 = n + 12;
        n5 = n2;
        boolean bl10 = bl4 = bl2 && bl6;
        if (bl4) {
            Component component = n7 == 0 ? NO_PENDING_INVITES_TEXT : PENDING_INVITES_TEXT;
            int n9 = this.font.width(component);
            this.fillGradient(poseStack, n6 - 3, n5 - 3, n6 + n9 + 3, n5 + 8 + 3, -1073741824, -1073741824);
            this.font.drawShadow(poseStack, component, (float)n6, (float)n5, -1);
        }
    }

    private boolean inPendingInvitationArea(double d, double d2) {
        int n = this.width / 2 + 50;
        int n2 = this.width / 2 + 66;
        int n3 = 11;
        int n4 = 23;
        if (this.numberOfPendingInvites != 0) {
            n -= 3;
            n2 += 3;
            n3 -= 5;
            n4 += 5;
        }
        return (double)n <= d && d <= (double)n2 && (double)n3 <= d2 && d2 <= (double)n4;
    }

    public void play(RealmsServer realmsServer, Screen screen) {
        if (realmsServer != null) {
            try {
                if (!this.connectLock.tryLock(1L, TimeUnit.SECONDS)) {
                    return;
                }
                if (this.connectLock.getHoldCount() > 1) {
                    return;
                }
            }
            catch (InterruptedException interruptedException) {
                return;
            }
            this.dontSetConnectedToRealms = true;
            this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(screen, new GetServerDetailsTask(this, screen, realmsServer, this.connectLock)));
        }
    }

    private boolean isSelfOwnedServer(RealmsServer realmsServer) {
        return realmsServer.ownerUUID != null && realmsServer.ownerUUID.equals(this.minecraft.getUser().getUuid());
    }

    private boolean isSelfOwnedNonExpiredServer(RealmsServer realmsServer) {
        return this.isSelfOwnedServer(realmsServer) && !realmsServer.expired;
    }

    private void drawExpired(PoseStack poseStack, int n, int n2, int n3, int n4) {
        this.minecraft.getTextureManager().bind(EXPIRED_ICON_LOCATION);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GuiComponent.blit(poseStack, n, n2, 0.0f, 0.0f, 10, 28, 10, 28);
        if (n3 >= n && n3 <= n + 9 && n4 >= n2 && n4 <= n2 + 27 && n4 < this.height - 40 && n4 > 32 && !this.shouldShowPopup()) {
            this.setTooltip(SERVER_EXPIRED_TOOLTIP);
        }
    }

    private void drawExpiring(PoseStack poseStack, int n, int n2, int n3, int n4, int n5) {
        this.minecraft.getTextureManager().bind(EXPIRES_SOON_ICON_LOCATION);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        if (this.animTick % 20 < 10) {
            GuiComponent.blit(poseStack, n, n2, 0.0f, 0.0f, 10, 28, 20, 28);
        } else {
            GuiComponent.blit(poseStack, n, n2, 10.0f, 0.0f, 10, 28, 20, 28);
        }
        if (n3 >= n && n3 <= n + 9 && n4 >= n2 && n4 <= n2 + 27 && n4 < this.height - 40 && n4 > 32 && !this.shouldShowPopup()) {
            if (n5 <= 0) {
                this.setTooltip(SERVER_EXPIRES_SOON_TOOLTIP);
            } else if (n5 == 1) {
                this.setTooltip(SERVER_EXPIRES_IN_DAY_TOOLTIP);
            } else {
                this.setTooltip(new TranslatableComponent("mco.selectServer.expires.days", n5));
            }
        }
    }

    private void drawOpen(PoseStack poseStack, int n, int n2, int n3, int n4) {
        this.minecraft.getTextureManager().bind(ON_ICON_LOCATION);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GuiComponent.blit(poseStack, n, n2, 0.0f, 0.0f, 10, 28, 10, 28);
        if (n3 >= n && n3 <= n + 9 && n4 >= n2 && n4 <= n2 + 27 && n4 < this.height - 40 && n4 > 32 && !this.shouldShowPopup()) {
            this.setTooltip(SERVER_OPEN_TOOLTIP);
        }
    }

    private void drawClose(PoseStack poseStack, int n, int n2, int n3, int n4) {
        this.minecraft.getTextureManager().bind(OFF_ICON_LOCATION);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GuiComponent.blit(poseStack, n, n2, 0.0f, 0.0f, 10, 28, 10, 28);
        if (n3 >= n && n3 <= n + 9 && n4 >= n2 && n4 <= n2 + 27 && n4 < this.height - 40 && n4 > 32 && !this.shouldShowPopup()) {
            this.setTooltip(SERVER_CLOSED_TOOLTIP);
        }
    }

    private void drawLeave(PoseStack poseStack, int n, int n2, int n3, int n4) {
        boolean bl = false;
        if (n3 >= n && n3 <= n + 28 && n4 >= n2 && n4 <= n2 + 28 && n4 < this.height - 40 && n4 > 32 && !this.shouldShowPopup()) {
            bl = true;
        }
        this.minecraft.getTextureManager().bind(LEAVE_ICON_LOCATION);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        float f = bl ? 28.0f : 0.0f;
        GuiComponent.blit(poseStack, n, n2, f, 0.0f, 28, 28, 56, 28);
        if (bl) {
            this.setTooltip(LEAVE_SERVER_TOOLTIP);
            this.hoveredElement = HoveredElement.LEAVE;
        }
    }

    private void drawConfigure(PoseStack poseStack, int n, int n2, int n3, int n4) {
        boolean bl = false;
        if (n3 >= n && n3 <= n + 28 && n4 >= n2 && n4 <= n2 + 28 && n4 < this.height - 40 && n4 > 32 && !this.shouldShowPopup()) {
            bl = true;
        }
        this.minecraft.getTextureManager().bind(CONFIGURE_LOCATION);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        float f = bl ? 28.0f : 0.0f;
        GuiComponent.blit(poseStack, n, n2, f, 0.0f, 28, 28, 56, 28);
        if (bl) {
            this.setTooltip(CONFIGURE_SERVER_TOOLTIP);
            this.hoveredElement = HoveredElement.CONFIGURE;
        }
    }

    protected void renderMousehoverTooltip(PoseStack poseStack, List<Component> list, int n, int n2) {
        if (list.isEmpty()) {
            return;
        }
        int n3 = 0;
        int n4 = 0;
        for (Component component : list) {
            int n5 = this.font.width(component);
            if (n5 <= n4) continue;
            n4 = n5;
        }
        int n6 = n - n4 - 5;
        int n7 = n2;
        if (n6 < 0) {
            n6 = n + 12;
        }
        for (Component component : list) {
            int n8 = n7 - (n3 == 0 ? 3 : 0) + n3;
            this.fillGradient(poseStack, n6 - 3, n8, n6 + n4 + 3, n7 + 8 + 3 + n3, -1073741824, -1073741824);
            this.font.drawShadow(poseStack, component, (float)n6, (float)(n7 + n3), 16777215);
            n3 += 10;
        }
    }

    private void renderMoreInfo(PoseStack poseStack, int n, int n2, int n3, int n4, boolean bl) {
        boolean bl2 = false;
        if (n >= n3 && n <= n3 + 20 && n2 >= n4 && n2 <= n4 + 20) {
            bl2 = true;
        }
        this.minecraft.getTextureManager().bind(QUESTIONMARK_LOCATION);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        float f = bl ? 20.0f : 0.0f;
        GuiComponent.blit(poseStack, n3, n4, f, 0.0f, 20, 20, 40, 20);
        if (bl2) {
            this.setTooltip(SERVER_INFO_TOOLTIP);
        }
    }

    private void renderNews(PoseStack poseStack, int n, int n2, boolean bl, int n3, int n4, boolean bl2, boolean bl3) {
        boolean bl4 = false;
        if (n >= n3 && n <= n3 + 20 && n2 >= n4 && n2 <= n4 + 20) {
            bl4 = true;
        }
        this.minecraft.getTextureManager().bind(NEWS_LOCATION);
        if (bl3) {
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        } else {
            RenderSystem.color4f(0.5f, 0.5f, 0.5f, 1.0f);
        }
        boolean bl5 = bl3 && bl2;
        float f = bl5 ? 20.0f : 0.0f;
        GuiComponent.blit(poseStack, n3, n4, f, 0.0f, 20, 20, 40, 20);
        if (bl4 && bl3) {
            this.setTooltip(NEWS_TOOLTIP);
        }
        if (bl && bl3) {
            int n5 = bl4 ? 0 : (int)(Math.max(0.0f, Math.max(Mth.sin((float)(10 + this.animTick) * 0.57f), Mth.cos((float)this.animTick * 0.35f))) * -6.0f);
            this.minecraft.getTextureManager().bind(INVITATION_ICONS_LOCATION);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            GuiComponent.blit(poseStack, n3 + 10, n4 + 2 + n5, 40.0f, 0.0f, 8, 8, 48, 16);
        }
    }

    private void renderLocal(PoseStack poseStack) {
        String string = "LOCAL!";
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.pushMatrix();
        RenderSystem.translatef(this.width / 2 - 25, 20.0f, 0.0f);
        RenderSystem.rotatef(-20.0f, 0.0f, 0.0f, 1.0f);
        RenderSystem.scalef(1.5f, 1.5f, 1.5f);
        this.font.draw(poseStack, "LOCAL!", 0.0f, 0.0f, 8388479);
        RenderSystem.popMatrix();
    }

    private void renderStage(PoseStack poseStack) {
        String string = "STAGE!";
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.pushMatrix();
        RenderSystem.translatef(this.width / 2 - 25, 20.0f, 0.0f);
        RenderSystem.rotatef(-20.0f, 0.0f, 0.0f, 1.0f);
        RenderSystem.scalef(1.5f, 1.5f, 1.5f);
        this.font.draw(poseStack, "STAGE!", 0.0f, 0.0f, -256);
        RenderSystem.popMatrix();
    }

    public RealmsMainScreen newScreen() {
        RealmsMainScreen realmsMainScreen = new RealmsMainScreen(this.lastScreen);
        realmsMainScreen.init(this.minecraft, this.width, this.height);
        return realmsMainScreen;
    }

    public static void updateTeaserImages(ResourceManager resourceManager) {
        Collection<ResourceLocation> collection = resourceManager.listResources("textures/gui/images", string -> string.endsWith(".png"));
        teaserImages = (List)collection.stream().filter(resourceLocation -> resourceLocation.getNamespace().equals("realms")).collect(ImmutableList.toImmutableList());
    }

    private void setTooltip(Component ... arrcomponent) {
        this.toolTip = Arrays.asList(arrcomponent);
    }

    private void pendingButtonPress(Button button) {
        this.minecraft.setScreen(new RealmsPendingInvitesScreen(this.lastScreen));
    }

    static /* synthetic */ Button access$3100(RealmsMainScreen realmsMainScreen) {
        return realmsMainScreen.playButton;
    }

    static {
        lastScrollYPosition = -1;
    }

    class CloseButton
    extends Button {
        public CloseButton() {
            super(RealmsMainScreen.this.popupX0() + 4, RealmsMainScreen.this.popupY0() + 4, 12, 12, new TranslatableComponent("mco.selectServer.close"), button -> RealmsMainScreen.this.onClosePopup());
        }

        @Override
        public void renderButton(PoseStack poseStack, int n, int n2, float f) {
            RealmsMainScreen.this.minecraft.getTextureManager().bind(CROSS_ICON_LOCATION);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            float f2 = this.isHovered() ? 12.0f : 0.0f;
            CloseButton.blit(poseStack, this.x, this.y, 0.0f, f2, 12, 12, 12, 24);
            if (this.isMouseOver(n, n2)) {
                RealmsMainScreen.this.setTooltip(new Component[]{this.getMessage()});
            }
        }
    }

    class ShowPopupButton
    extends Button {
        public ShowPopupButton() {
            super(RealmsMainScreen.this.width - 37, 6, 20, 20, new TranslatableComponent("mco.selectServer.info"), button -> RealmsMainScreen.this.popupOpenedByUser = !RealmsMainScreen.this.popupOpenedByUser);
        }

        @Override
        public void renderButton(PoseStack poseStack, int n, int n2, float f) {
            RealmsMainScreen.this.renderMoreInfo(poseStack, n, n2, this.x, this.y, this.isHovered());
        }
    }

    class NewsButton
    extends Button {
        public NewsButton() {
            super(RealmsMainScreen.this.width - 62, 6, 20, 20, TextComponent.EMPTY, button -> {
                if (RealmsMainScreen.this.newsLink == null) {
                    return;
                }
                Util.getPlatform().openUri(RealmsMainScreen.this.newsLink);
                if (RealmsMainScreen.this.hasUnreadNews) {
                    RealmsPersistence.RealmsPersistenceData realmsPersistenceData = RealmsPersistence.readFile();
                    realmsPersistenceData.hasUnreadNews = false;
                    RealmsMainScreen.this.hasUnreadNews = false;
                    RealmsPersistence.writeFile(realmsPersistenceData);
                }
            });
            this.setMessage(new TranslatableComponent("mco.news"));
        }

        @Override
        public void renderButton(PoseStack poseStack, int n, int n2, float f) {
            RealmsMainScreen.this.renderNews(poseStack, n, n2, RealmsMainScreen.this.hasUnreadNews, this.x, this.y, this.isHovered(), this.active);
        }
    }

    class PendingInvitesButton
    extends Button
    implements TickableWidget {
        public PendingInvitesButton() {
            super(RealmsMainScreen.this.width / 2 + 47, 6, 22, 22, TextComponent.EMPTY, button -> RealmsMainScreen.this.pendingButtonPress(button));
        }

        @Override
        public void tick() {
            this.setMessage(new TranslatableComponent(RealmsMainScreen.this.numberOfPendingInvites == 0 ? "mco.invites.nopending" : "mco.invites.pending"));
        }

        @Override
        public void renderButton(PoseStack poseStack, int n, int n2, float f) {
            RealmsMainScreen.this.drawInvitationPendingIcon(poseStack, n, n2, this.x, this.y, this.isHovered(), this.active);
        }
    }

    class ServerEntry
    extends Entry {
        private final RealmsServer serverData;

        public ServerEntry(RealmsServer realmsServer) {
            this.serverData = realmsServer;
        }

        @Override
        public void render(PoseStack poseStack, int n, int n2, int n3, int n4, int n5, int n6, int n7, boolean bl, float f) {
            this.renderMcoServerItem(this.serverData, poseStack, n3, n2, n6, n7);
        }

        @Override
        public boolean mouseClicked(double d, double d2, int n) {
            if (this.serverData.state == RealmsServer.State.UNINITIALIZED) {
                RealmsMainScreen.this.selectedServerId = -1L;
                RealmsMainScreen.this.minecraft.setScreen(new RealmsCreateRealmScreen(this.serverData, RealmsMainScreen.this));
            } else {
                RealmsMainScreen.this.selectedServerId = this.serverData.id;
            }
            return true;
        }

        private void renderMcoServerItem(RealmsServer realmsServer, PoseStack poseStack, int n, int n2, int n3, int n4) {
            this.renderLegacy(realmsServer, poseStack, n + 36, n2, n3, n4);
        }

        private void renderLegacy(RealmsServer realmsServer, PoseStack poseStack, int n, int n2, int n3, int n4) {
            Object object;
            if (realmsServer.state == RealmsServer.State.UNINITIALIZED) {
                RealmsMainScreen.this.minecraft.getTextureManager().bind(WORLDICON_LOCATION);
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                RenderSystem.enableAlphaTest();
                GuiComponent.blit(poseStack, n + 10, n2 + 6, 0.0f, 0.0f, 40, 20, 40, 20);
                float f = 0.5f + (1.0f + Mth.sin((float)RealmsMainScreen.this.animTick * 0.25f)) * 0.25f;
                int n5 = 0xFF000000 | (int)(127.0f * f) << 16 | (int)(255.0f * f) << 8 | (int)(127.0f * f);
                GuiComponent.drawCenteredString(poseStack, RealmsMainScreen.this.font, SERVER_UNITIALIZED_TEXT, n + 10 + 40 + 75, n2 + 12, n5);
                return;
            }
            int n6 = 225;
            int n7 = 2;
            if (realmsServer.expired) {
                RealmsMainScreen.this.drawExpired(poseStack, n + 225 - 14, n2 + 2, n3, n4);
            } else if (realmsServer.state == RealmsServer.State.CLOSED) {
                RealmsMainScreen.this.drawClose(poseStack, n + 225 - 14, n2 + 2, n3, n4);
            } else if (RealmsMainScreen.this.isSelfOwnedServer(realmsServer) && realmsServer.daysLeft < 7) {
                RealmsMainScreen.this.drawExpiring(poseStack, n + 225 - 14, n2 + 2, n3, n4, realmsServer.daysLeft);
            } else if (realmsServer.state == RealmsServer.State.OPEN) {
                RealmsMainScreen.this.drawOpen(poseStack, n + 225 - 14, n2 + 2, n3, n4);
            }
            if (!RealmsMainScreen.this.isSelfOwnedServer(realmsServer) && !overrideConfigure) {
                RealmsMainScreen.this.drawLeave(poseStack, n + 225, n2 + 2, n3, n4);
            } else {
                RealmsMainScreen.this.drawConfigure(poseStack, n + 225, n2 + 2, n3, n4);
            }
            if (!"0".equals(realmsServer.serverPing.nrOfPlayers)) {
                object = (Object)((Object)ChatFormatting.GRAY) + "" + realmsServer.serverPing.nrOfPlayers;
                RealmsMainScreen.this.font.draw(poseStack, (String)object, (float)(n + 207 - RealmsMainScreen.this.font.width((String)object)), (float)(n2 + 3), 8421504);
                if (n3 >= n + 207 - RealmsMainScreen.this.font.width((String)object) && n3 <= n + 207 && n4 >= n2 + 1 && n4 <= n2 + 10 && n4 < RealmsMainScreen.this.height - 40 && n4 > 32 && !RealmsMainScreen.this.shouldShowPopup()) {
                    RealmsMainScreen.this.setTooltip(new Component[]{new TextComponent(realmsServer.serverPing.playerList)});
                }
            }
            if (RealmsMainScreen.this.isSelfOwnedServer(realmsServer) && realmsServer.expired) {
                Component component;
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                RenderSystem.enableBlend();
                RealmsMainScreen.this.minecraft.getTextureManager().bind(BUTTON_LOCATION);
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                if (realmsServer.expiredTrial) {
                    object = TRIAL_EXPIRED_TEXT;
                    component = SUBSCRIPTION_CREATE_TEXT;
                } else {
                    object = SUBSCRIPTION_EXPIRED_TEXT;
                    component = SUBSCRIPTION_RENEW_TEXT;
                }
                int n8 = RealmsMainScreen.this.font.width(component) + 17;
                int n9 = 16;
                int n10 = n + RealmsMainScreen.this.font.width((FormattedText)object) + 8;
                int n11 = n2 + 13;
                boolean bl = false;
                if (n3 >= n10 && n3 < n10 + n8 && n4 > n11 && n4 <= n11 + 16 & n4 < RealmsMainScreen.this.height - 40 && n4 > 32 && !RealmsMainScreen.this.shouldShowPopup()) {
                    bl = true;
                    RealmsMainScreen.this.hoveredElement = HoveredElement.EXPIRED;
                }
                int n12 = bl ? 2 : 1;
                GuiComponent.blit(poseStack, n10, n11, 0.0f, 46 + n12 * 20, n8 / 2, 8, 256, 256);
                GuiComponent.blit(poseStack, n10 + n8 / 2, n11, 200 - n8 / 2, 46 + n12 * 20, n8 / 2, 8, 256, 256);
                GuiComponent.blit(poseStack, n10, n11 + 8, 0.0f, 46 + n12 * 20 + 12, n8 / 2, 8, 256, 256);
                GuiComponent.blit(poseStack, n10 + n8 / 2, n11 + 8, 200 - n8 / 2, 46 + n12 * 20 + 12, n8 / 2, 8, 256, 256);
                RenderSystem.disableBlend();
                int n13 = n2 + 11 + 5;
                int n14 = bl ? 16777120 : 16777215;
                RealmsMainScreen.this.font.draw(poseStack, (Component)object, (float)(n + 2), (float)(n13 + 1), 15553363);
                GuiComponent.drawCenteredString(poseStack, RealmsMainScreen.this.font, component, n10 + n8 / 2, n13 + 1, n14);
            } else {
                if (realmsServer.worldType == RealmsServer.WorldType.MINIGAME) {
                    int n15 = 13413468;
                    int n16 = RealmsMainScreen.this.font.width(SELECT_MINIGAME_PREFIX);
                    RealmsMainScreen.this.font.draw(poseStack, SELECT_MINIGAME_PREFIX, (float)(n + 2), (float)(n2 + 12), 13413468);
                    RealmsMainScreen.this.font.draw(poseStack, realmsServer.getMinigameName(), (float)(n + 2 + n16), (float)(n2 + 12), 7105644);
                } else {
                    RealmsMainScreen.this.font.draw(poseStack, realmsServer.getDescription(), (float)(n + 2), (float)(n2 + 12), 7105644);
                }
                if (!RealmsMainScreen.this.isSelfOwnedServer(realmsServer)) {
                    RealmsMainScreen.this.font.draw(poseStack, realmsServer.owner, (float)(n + 2), (float)(n2 + 12 + 11), 5000268);
                }
            }
            RealmsMainScreen.this.font.draw(poseStack, realmsServer.getName(), (float)(n + 2), (float)(n2 + 1), 16777215);
            RealmsTextureManager.withBoundFace(realmsServer.ownerUUID, () -> {
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                GuiComponent.blit(poseStack, n - 36, n2, 32, 32, 8.0f, 8.0f, 8, 8, 64, 64);
                GuiComponent.blit(poseStack, n - 36, n2, 32, 32, 40.0f, 8.0f, 8, 8, 64, 64);
            });
        }

        static /* synthetic */ RealmsServer access$8700(ServerEntry serverEntry) {
            return serverEntry.serverData;
        }
    }

    class TrialEntry
    extends Entry {
        private TrialEntry() {
        }

        @Override
        public void render(PoseStack poseStack, int n, int n2, int n3, int n4, int n5, int n6, int n7, boolean bl, float f) {
            this.renderTrialItem(poseStack, n, n3, n2, n6, n7);
        }

        @Override
        public boolean mouseClicked(double d, double d2, int n) {
            RealmsMainScreen.this.popupOpenedByUser = true;
            return true;
        }

        private void renderTrialItem(PoseStack poseStack, int n, int n2, int n3, int n4, int n5) {
            int n6 = n3 + 8;
            int n7 = 0;
            boolean bl = false;
            if (n2 <= n4 && n4 <= (int)RealmsMainScreen.this.realmSelectionList.getScrollAmount() && n3 <= n5 && n5 <= n3 + 32) {
                bl = true;
            }
            int n8 = 8388479;
            if (bl && !RealmsMainScreen.this.shouldShowPopup()) {
                n8 = 6077788;
            }
            for (Component component : TRIAL_MESSAGE_LINES) {
                GuiComponent.drawCenteredString(poseStack, RealmsMainScreen.this.font, component, RealmsMainScreen.this.width / 2, n6 + n7, n8);
                n7 += 10;
            }
        }
    }

    abstract class Entry
    extends ObjectSelectionList.Entry<Entry> {
        private Entry() {
        }
    }

    class RealmSelectionList
    extends RealmsObjectSelectionList<Entry> {
        private boolean showingMessage;

        public RealmSelectionList() {
            super(RealmsMainScreen.this.width, RealmsMainScreen.this.height, 32, RealmsMainScreen.this.height - 40, 36);
        }

        @Override
        public void clear() {
            super.clear();
            this.showingMessage = false;
        }

        public int addMessageEntry(Entry entry) {
            this.showingMessage = true;
            return this.addEntry(entry);
        }

        @Override
        public boolean isFocused() {
            return RealmsMainScreen.this.getFocused() == this;
        }

        @Override
        public boolean keyPressed(int n, int n2, int n3) {
            if (n == 257 || n == 32 || n == 335) {
                ObjectSelectionList.Entry entry = (ObjectSelectionList.Entry)this.getSelected();
                if (entry == null) {
                    return super.keyPressed(n, n2, n3);
                }
                return entry.mouseClicked(0.0, 0.0, 0);
            }
            return super.keyPressed(n, n2, n3);
        }

        @Override
        public boolean mouseClicked(double d, double d2, int n) {
            if (n == 0 && d < (double)this.getScrollbarPosition() && d2 >= (double)this.y0 && d2 <= (double)this.y1) {
                int n2 = RealmsMainScreen.this.realmSelectionList.getRowLeft();
                int n3 = this.getScrollbarPosition();
                int n4 = (int)Math.floor(d2 - (double)this.y0) - this.headerHeight + (int)this.getScrollAmount() - 4;
                int n5 = n4 / this.itemHeight;
                if (d >= (double)n2 && d <= (double)n3 && n5 >= 0 && n4 >= 0 && n5 < this.getItemCount()) {
                    this.itemClicked(n4, n5, d, d2, this.width);
                    RealmsMainScreen.this.clicks = RealmsMainScreen.this.clicks + 7;
                    this.selectItem(n5);
                }
                return true;
            }
            return super.mouseClicked(d, d2, n);
        }

        @Override
        public void selectItem(int n) {
            RealmsServer realmsServer;
            this.setSelectedItem(n);
            if (n == -1) {
                return;
            }
            if (this.showingMessage) {
                if (n == 0) {
                    realmsServer = null;
                } else {
                    if (n - 1 >= RealmsMainScreen.this.realmsServers.size()) {
                        RealmsMainScreen.this.selectedServerId = -1L;
                        return;
                    }
                    realmsServer = (RealmsServer)RealmsMainScreen.this.realmsServers.get(n - 1);
                }
            } else {
                if (n >= RealmsMainScreen.this.realmsServers.size()) {
                    RealmsMainScreen.this.selectedServerId = -1L;
                    return;
                }
                realmsServer = (RealmsServer)RealmsMainScreen.this.realmsServers.get(n);
            }
            RealmsMainScreen.this.updateButtonStates(realmsServer);
            if (realmsServer == null) {
                RealmsMainScreen.this.selectedServerId = -1L;
                return;
            }
            if (realmsServer.state == RealmsServer.State.UNINITIALIZED) {
                RealmsMainScreen.this.selectedServerId = -1L;
                return;
            }
            RealmsMainScreen.this.selectedServerId = realmsServer.id;
            if (RealmsMainScreen.this.clicks >= 10 && RealmsMainScreen.access$3100((RealmsMainScreen)RealmsMainScreen.this).active) {
                RealmsMainScreen.this.play(RealmsMainScreen.this.findServer(RealmsMainScreen.this.selectedServerId), RealmsMainScreen.this);
            }
        }

        @Override
        public void setSelected(@Nullable Entry entry) {
            super.setSelected(entry);
            int n = this.children().indexOf(entry);
            if (this.showingMessage && n == 0) {
                NarrationHelper.now(I18n.get("mco.trial.message.line1", new Object[0]), I18n.get("mco.trial.message.line2", new Object[0]));
            } else if (!this.showingMessage || n > 0) {
                RealmsServer realmsServer = (RealmsServer)RealmsMainScreen.this.realmsServers.get(n - (this.showingMessage ? 1 : 0));
                RealmsMainScreen.this.selectedServerId = realmsServer.id;
                RealmsMainScreen.this.updateButtonStates(realmsServer);
                if (realmsServer.state == RealmsServer.State.UNINITIALIZED) {
                    NarrationHelper.now(I18n.get("mco.selectServer.uninitialized", new Object[0]) + I18n.get("mco.gui.button", new Object[0]));
                } else {
                    NarrationHelper.now(I18n.get("narrator.select", realmsServer.name));
                }
            }
        }

        @Override
        public void itemClicked(int n, int n2, double d, double d2, int n3) {
            if (this.showingMessage) {
                if (n2 == 0) {
                    RealmsMainScreen.this.popupOpenedByUser = true;
                    return;
                }
                --n2;
            }
            if (n2 >= RealmsMainScreen.this.realmsServers.size()) {
                return;
            }
            RealmsServer realmsServer = (RealmsServer)RealmsMainScreen.this.realmsServers.get(n2);
            if (realmsServer == null) {
                return;
            }
            if (realmsServer.state == RealmsServer.State.UNINITIALIZED) {
                RealmsMainScreen.this.selectedServerId = -1L;
                Minecraft.getInstance().setScreen(new RealmsCreateRealmScreen(realmsServer, RealmsMainScreen.this));
            } else {
                RealmsMainScreen.this.selectedServerId = realmsServer.id;
            }
            if (RealmsMainScreen.this.hoveredElement == HoveredElement.CONFIGURE) {
                RealmsMainScreen.this.selectedServerId = realmsServer.id;
                RealmsMainScreen.this.configureClicked(realmsServer);
            } else if (RealmsMainScreen.this.hoveredElement == HoveredElement.LEAVE) {
                RealmsMainScreen.this.selectedServerId = realmsServer.id;
                RealmsMainScreen.this.leaveClicked(realmsServer);
            } else if (RealmsMainScreen.this.hoveredElement == HoveredElement.EXPIRED) {
                RealmsMainScreen.this.onRenew();
            }
        }

        @Override
        public int getMaxPosition() {
            return this.getItemCount() * 36;
        }

        @Override
        public int getRowWidth() {
            return 300;
        }

        @Override
        public /* synthetic */ void setSelected(@Nullable AbstractSelectionList.Entry entry) {
            this.setSelected((Entry)entry);
        }
    }

    static enum HoveredElement {
        NONE,
        EXPIRED,
        LEAVE,
        CONFIGURE;
        
    }

}

