/*
 * Decompiled with CFR 0.146.
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import net.minecraft.Util;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;

public class RealmsNotificationsScreen
extends RealmsScreen {
    private static final ResourceLocation INVITE_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/invite_icon.png");
    private static final ResourceLocation TRIAL_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/trial_icon.png");
    private static final ResourceLocation NEWS_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/news_notification_mainscreen.png");
    private static final RealmsDataFetcher REALMS_DATA_FETCHER = new RealmsDataFetcher();
    private volatile int numberOfPendingInvites;
    private static boolean checkedMcoAvailability;
    private static boolean trialAvailable;
    private static boolean validClient;
    private static boolean hasUnreadNews;

    @Override
    public void init() {
        this.checkIfMcoEnabled();
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
    }

    @Override
    public void tick() {
        if (!(this.getRealmsNotificationsEnabled() && this.inTitleScreen() && validClient || REALMS_DATA_FETCHER.isStopped())) {
            REALMS_DATA_FETCHER.stop();
            return;
        }
        if (!validClient || !this.getRealmsNotificationsEnabled()) {
            return;
        }
        REALMS_DATA_FETCHER.initWithSpecificTaskList();
        if (REALMS_DATA_FETCHER.isFetchedSinceLastTry(RealmsDataFetcher.Task.PENDING_INVITE)) {
            this.numberOfPendingInvites = REALMS_DATA_FETCHER.getPendingInvitesCount();
        }
        if (REALMS_DATA_FETCHER.isFetchedSinceLastTry(RealmsDataFetcher.Task.TRIAL_AVAILABLE)) {
            trialAvailable = REALMS_DATA_FETCHER.isTrialAvailable();
        }
        if (REALMS_DATA_FETCHER.isFetchedSinceLastTry(RealmsDataFetcher.Task.UNREAD_NEWS)) {
            hasUnreadNews = REALMS_DATA_FETCHER.hasUnreadNews();
        }
        REALMS_DATA_FETCHER.markClean();
    }

    private boolean getRealmsNotificationsEnabled() {
        return this.minecraft.options.realmsNotifications;
    }

    private boolean inTitleScreen() {
        return this.minecraft.screen instanceof TitleScreen;
    }

    private void checkIfMcoEnabled() {
        if (!checkedMcoAvailability) {
            checkedMcoAvailability = true;
            new Thread("Realms Notification Availability checker #1"){

                @Override
                public void run() {
                    RealmsClient realmsClient = RealmsClient.create();
                    try {
                        RealmsClient.CompatibleVersionResponse compatibleVersionResponse = realmsClient.clientCompatible();
                        if (compatibleVersionResponse != RealmsClient.CompatibleVersionResponse.COMPATIBLE) {
                            return;
                        }
                    }
                    catch (RealmsServiceException realmsServiceException) {
                        if (realmsServiceException.httpResultCode != 401) {
                            checkedMcoAvailability = false;
                        }
                        return;
                    }
                    validClient = true;
                }
            }.start();
        }
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        if (validClient) {
            this.drawIcons(poseStack, n, n2);
        }
        super.render(poseStack, n, n2, f);
    }

    private void drawIcons(PoseStack poseStack, int n, int n2) {
        int n3 = this.numberOfPendingInvites;
        int n4 = 24;
        int n5 = this.height / 4 + 48;
        int n6 = this.width / 2 + 80;
        int n7 = n5 + 48 + 2;
        int n8 = 0;
        if (hasUnreadNews) {
            this.minecraft.getTextureManager().bind(NEWS_ICON_LOCATION);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.pushMatrix();
            RenderSystem.scalef(0.4f, 0.4f, 0.4f);
            GuiComponent.blit(poseStack, (int)((double)(n6 + 2 - n8) * 2.5), (int)((double)n7 * 2.5), 0.0f, 0.0f, 40, 40, 40, 40);
            RenderSystem.popMatrix();
            n8 += 14;
        }
        if (n3 != 0) {
            this.minecraft.getTextureManager().bind(INVITE_ICON_LOCATION);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            GuiComponent.blit(poseStack, n6 - n8, n7 - 6, 0.0f, 0.0f, 15, 25, 31, 25);
            n8 += 16;
        }
        if (trialAvailable) {
            this.minecraft.getTextureManager().bind(TRIAL_ICON_LOCATION);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            int n9 = 0;
            if ((Util.getMillis() / 800L & 1L) == 1L) {
                n9 = 8;
            }
            GuiComponent.blit(poseStack, n6 + 4 - n8, n7 + 4, 0.0f, n9, 8, 8, 8, 16);
        }
    }

    @Override
    public void removed() {
        REALMS_DATA_FETCHER.stop();
    }

}

