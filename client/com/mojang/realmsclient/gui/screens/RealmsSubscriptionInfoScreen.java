/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.Subscription;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.text.DateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import net.minecraft.Util;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.NarrationHelper;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsSubscriptionInfoScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Component SUBSCRIPTION_TITLE = new TranslatableComponent("mco.configure.world.subscription.title");
    private static final Component SUBSCRIPTION_START_LABEL = new TranslatableComponent("mco.configure.world.subscription.start");
    private static final Component TIME_LEFT_LABEL = new TranslatableComponent("mco.configure.world.subscription.timeleft");
    private static final Component DAYS_LEFT_LABEL = new TranslatableComponent("mco.configure.world.subscription.recurring.daysleft");
    private static final Component SUBSCRIPTION_EXPIRED_TEXT = new TranslatableComponent("mco.configure.world.subscription.expired");
    private static final Component SUBSCRIPTION_LESS_THAN_A_DAY_TEXT = new TranslatableComponent("mco.configure.world.subscription.less_than_a_day");
    private static final Component MONTH_SUFFIX = new TranslatableComponent("mco.configure.world.subscription.month");
    private static final Component MONTHS_SUFFIX = new TranslatableComponent("mco.configure.world.subscription.months");
    private static final Component DAY_SUFFIX = new TranslatableComponent("mco.configure.world.subscription.day");
    private static final Component DAYS_SUFFIX = new TranslatableComponent("mco.configure.world.subscription.days");
    private final Screen lastScreen;
    private final RealmsServer serverData;
    private final Screen mainScreen;
    private Component daysLeft;
    private String startDate;
    private Subscription.SubscriptionType type;

    public RealmsSubscriptionInfoScreen(Screen screen, RealmsServer realmsServer, Screen screen2) {
        this.lastScreen = screen;
        this.serverData = realmsServer;
        this.mainScreen = screen2;
    }

    @Override
    public void init() {
        this.getSubscription(this.serverData.id);
        NarrationHelper.now(SUBSCRIPTION_TITLE.getString(), SUBSCRIPTION_START_LABEL.getString(), this.startDate, TIME_LEFT_LABEL.getString(), this.daysLeft.getString());
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.addButton(new Button(this.width / 2 - 100, RealmsSubscriptionInfoScreen.row(6), 200, 20, new TranslatableComponent("mco.configure.world.subscription.extend"), button -> {
            String string = "https://aka.ms/ExtendJavaRealms?subscriptionId=" + this.serverData.remoteSubscriptionId + "&profileId=" + this.minecraft.getUser().getUuid();
            this.minecraft.keyboardHandler.setClipboard(string);
            Util.getPlatform().openUri(string);
        }));
        this.addButton(new Button(this.width / 2 - 100, RealmsSubscriptionInfoScreen.row(12), 200, 20, CommonComponents.GUI_BACK, button -> this.minecraft.setScreen(this.lastScreen)));
        if (this.serverData.expired) {
            this.addButton(new Button(this.width / 2 - 100, RealmsSubscriptionInfoScreen.row(10), 200, 20, new TranslatableComponent("mco.configure.world.delete.button"), button -> {
                TranslatableComponent translatableComponent = new TranslatableComponent("mco.configure.world.delete.question.line1");
                TranslatableComponent translatableComponent2 = new TranslatableComponent("mco.configure.world.delete.question.line2");
                this.minecraft.setScreen(new RealmsLongConfirmationScreen(this::deleteRealm, RealmsLongConfirmationScreen.Type.Warning, translatableComponent, translatableComponent2, true));
            }));
        }
    }

    private void deleteRealm(boolean bl) {
        if (bl) {
            new Thread("Realms-delete-realm"){

                @Override
                public void run() {
                    try {
                        RealmsClient realmsClient = RealmsClient.create();
                        realmsClient.deleteWorld(RealmsSubscriptionInfoScreen.access$000((RealmsSubscriptionInfoScreen)RealmsSubscriptionInfoScreen.this).id);
                    }
                    catch (RealmsServiceException realmsServiceException) {
                        LOGGER.error("Couldn't delete world");
                        LOGGER.error((Object)realmsServiceException);
                    }
                    RealmsSubscriptionInfoScreen.this.minecraft.execute(() -> RealmsSubscriptionInfoScreen.this.minecraft.setScreen(RealmsSubscriptionInfoScreen.this.mainScreen));
                }
            }.start();
        }
        this.minecraft.setScreen(this);
    }

    private void getSubscription(long l) {
        RealmsClient realmsClient = RealmsClient.create();
        try {
            Subscription subscription = realmsClient.subscriptionFor(l);
            this.daysLeft = this.daysLeftPresentation(subscription.daysLeft);
            this.startDate = RealmsSubscriptionInfoScreen.localPresentation(subscription.startDate);
            this.type = subscription.type;
        }
        catch (RealmsServiceException realmsServiceException) {
            LOGGER.error("Couldn't get subscription");
            this.minecraft.setScreen(new RealmsGenericErrorScreen(realmsServiceException, this.lastScreen));
        }
    }

    private static String localPresentation(long l) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar(TimeZone.getDefault());
        gregorianCalendar.setTimeInMillis(l);
        return DateFormat.getDateTimeInstance().format(gregorianCalendar.getTime());
    }

    @Override
    public void removed() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }

    @Override
    public boolean keyPressed(int n, int n2, int n3) {
        if (n == 256) {
            this.minecraft.setScreen(this.lastScreen);
            return true;
        }
        return super.keyPressed(n, n2, n3);
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderBackground(poseStack);
        int n3 = this.width / 2 - 100;
        RealmsSubscriptionInfoScreen.drawCenteredString(poseStack, this.font, SUBSCRIPTION_TITLE, this.width / 2, 17, 16777215);
        this.font.draw(poseStack, SUBSCRIPTION_START_LABEL, (float)n3, (float)RealmsSubscriptionInfoScreen.row(0), 10526880);
        this.font.draw(poseStack, this.startDate, (float)n3, (float)RealmsSubscriptionInfoScreen.row(1), 16777215);
        if (this.type == Subscription.SubscriptionType.NORMAL) {
            this.font.draw(poseStack, TIME_LEFT_LABEL, (float)n3, (float)RealmsSubscriptionInfoScreen.row(3), 10526880);
        } else if (this.type == Subscription.SubscriptionType.RECURRING) {
            this.font.draw(poseStack, DAYS_LEFT_LABEL, (float)n3, (float)RealmsSubscriptionInfoScreen.row(3), 10526880);
        }
        this.font.draw(poseStack, this.daysLeft, (float)n3, (float)RealmsSubscriptionInfoScreen.row(4), 16777215);
        super.render(poseStack, n, n2, f);
    }

    private Component daysLeftPresentation(int n) {
        if (n < 0 && this.serverData.expired) {
            return SUBSCRIPTION_EXPIRED_TEXT;
        }
        if (n <= 1) {
            return SUBSCRIPTION_LESS_THAN_A_DAY_TEXT;
        }
        int n2 = n / 30;
        int n3 = n % 30;
        TextComponent textComponent = new TextComponent("");
        if (n2 > 0) {
            textComponent.append(Integer.toString(n2)).append(" ");
            if (n2 == 1) {
                textComponent.append(MONTH_SUFFIX);
            } else {
                textComponent.append(MONTHS_SUFFIX);
            }
        }
        if (n3 > 0) {
            if (n2 > 0) {
                textComponent.append(", ");
            }
            textComponent.append(Integer.toString(n3)).append(" ");
            if (n3 == 1) {
                textComponent.append(DAY_SUFFIX);
            } else {
                textComponent.append(DAYS_SUFFIX);
            }
        }
        return textComponent;
    }

    static /* synthetic */ RealmsServer access$000(RealmsSubscriptionInfoScreen realmsSubscriptionInfoScreen) {
        return realmsSubscriptionInfoScreen.serverData;
    }

}

