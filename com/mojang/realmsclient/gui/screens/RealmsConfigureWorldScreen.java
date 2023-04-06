/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsWorldSlotButton;
import com.mojang.realmsclient.gui.screens.RealmsBackupScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsPlayerScreen;
import com.mojang.realmsclient.gui.screens.RealmsResetWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsScreenWithCallback;
import com.mojang.realmsclient.gui.screens.RealmsSelectWorldTemplateScreen;
import com.mojang.realmsclient.gui.screens.RealmsSettingsScreen;
import com.mojang.realmsclient.gui.screens.RealmsSlotOptionsScreen;
import com.mojang.realmsclient.gui.screens.RealmsSubscriptionInfoScreen;
import com.mojang.realmsclient.util.task.CloseServerTask;
import com.mojang.realmsclient.util.task.LongRunningTask;
import com.mojang.realmsclient.util.task.OpenServerTask;
import com.mojang.realmsclient.util.task.SwitchMinigameTask;
import com.mojang.realmsclient.util.task.SwitchSlotTask;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsConfigureWorldScreen
extends RealmsScreenWithCallback {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation ON_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/on_icon.png");
    private static final ResourceLocation OFF_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/off_icon.png");
    private static final ResourceLocation EXPIRED_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/expired_icon.png");
    private static final ResourceLocation EXPIRES_SOON_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/expires_soon_icon.png");
    private static final Component TITLE = new TranslatableComponent("mco.configure.worlds.title");
    private static final Component WORLD_TITLE = new TranslatableComponent("mco.configure.world.title");
    private static final Component MINIGAME_PREFIX = new TranslatableComponent("mco.configure.current.minigame").append(": ");
    private static final Component SERVER_EXPIRED_TOOLTIP = new TranslatableComponent("mco.selectServer.expired");
    private static final Component SERVER_EXPIRING_SOON_TOOLTIP = new TranslatableComponent("mco.selectServer.expires.soon");
    private static final Component SERVER_EXPIRING_IN_DAY_TOOLTIP = new TranslatableComponent("mco.selectServer.expires.day");
    private static final Component SERVER_OPEN_TOOLTIP = new TranslatableComponent("mco.selectServer.open");
    private static final Component SERVER_CLOSED_TOOLTIP = new TranslatableComponent("mco.selectServer.closed");
    @Nullable
    private Component toolTip;
    private final RealmsMainScreen lastScreen;
    @Nullable
    private RealmsServer serverData;
    private final long serverId;
    private int leftX;
    private int rightX;
    private Button playersButton;
    private Button settingsButton;
    private Button subscriptionButton;
    private Button optionsButton;
    private Button backupButton;
    private Button resetWorldButton;
    private Button switchMinigameButton;
    private boolean stateChanged;
    private int animTick;
    private int clicks;

    public RealmsConfigureWorldScreen(RealmsMainScreen realmsMainScreen, long l) {
        this.lastScreen = realmsMainScreen;
        this.serverId = l;
    }

    @Override
    public void init() {
        if (this.serverData == null) {
            this.fetchServerData(this.serverId);
        }
        this.leftX = this.width / 2 - 187;
        this.rightX = this.width / 2 + 190;
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.playersButton = this.addButton(new Button(this.centerButton(0, 3), RealmsConfigureWorldScreen.row(0), 100, 20, new TranslatableComponent("mco.configure.world.buttons.players"), button -> this.minecraft.setScreen(new RealmsPlayerScreen(this, this.serverData))));
        this.settingsButton = this.addButton(new Button(this.centerButton(1, 3), RealmsConfigureWorldScreen.row(0), 100, 20, new TranslatableComponent("mco.configure.world.buttons.settings"), button -> this.minecraft.setScreen(new RealmsSettingsScreen(this, this.serverData.clone()))));
        this.subscriptionButton = this.addButton(new Button(this.centerButton(2, 3), RealmsConfigureWorldScreen.row(0), 100, 20, new TranslatableComponent("mco.configure.world.buttons.subscription"), button -> this.minecraft.setScreen(new RealmsSubscriptionInfoScreen(this, this.serverData.clone(), this.lastScreen))));
        for (int i = 1; i < 5; ++i) {
            this.addSlotButton(i);
        }
        this.switchMinigameButton = this.addButton(new Button(this.leftButton(0), RealmsConfigureWorldScreen.row(13) - 5, 100, 20, new TranslatableComponent("mco.configure.world.buttons.switchminigame"), button -> {
            RealmsSelectWorldTemplateScreen realmsSelectWorldTemplateScreen = new RealmsSelectWorldTemplateScreen(this, RealmsServer.WorldType.MINIGAME);
            realmsSelectWorldTemplateScreen.setTitle(new TranslatableComponent("mco.template.title.minigame"));
            this.minecraft.setScreen(realmsSelectWorldTemplateScreen);
        }));
        this.optionsButton = this.addButton(new Button(this.leftButton(0), RealmsConfigureWorldScreen.row(13) - 5, 90, 20, new TranslatableComponent("mco.configure.world.buttons.options"), button -> this.minecraft.setScreen(new RealmsSlotOptionsScreen(this, this.serverData.slots.get(this.serverData.activeSlot).clone(), this.serverData.worldType, this.serverData.activeSlot))));
        this.backupButton = this.addButton(new Button(this.leftButton(1), RealmsConfigureWorldScreen.row(13) - 5, 90, 20, new TranslatableComponent("mco.configure.world.backup"), button -> this.minecraft.setScreen(new RealmsBackupScreen(this, this.serverData.clone(), this.serverData.activeSlot))));
        this.resetWorldButton = this.addButton(new Button(this.leftButton(2), RealmsConfigureWorldScreen.row(13) - 5, 90, 20, new TranslatableComponent("mco.configure.world.buttons.resetworld"), button -> this.minecraft.setScreen(new RealmsResetWorldScreen(this, this.serverData.clone(), () -> this.minecraft.setScreen(this.getNewScreen()), () -> this.minecraft.setScreen(this.getNewScreen())))));
        this.addButton(new Button(this.rightX - 80 + 8, RealmsConfigureWorldScreen.row(13) - 5, 70, 20, CommonComponents.GUI_BACK, button -> this.backButtonClicked()));
        this.backupButton.active = true;
        if (this.serverData == null) {
            this.hideMinigameButtons();
            this.hideRegularButtons();
            this.playersButton.active = false;
            this.settingsButton.active = false;
            this.subscriptionButton.active = false;
        } else {
            this.disableButtons();
            if (this.isMinigame()) {
                this.hideRegularButtons();
            } else {
                this.hideMinigameButtons();
            }
        }
    }

    private void addSlotButton(int n) {
        int n2 = this.frame(n);
        int n3 = RealmsConfigureWorldScreen.row(5) + 5;
        RealmsWorldSlotButton realmsWorldSlotButton = new RealmsWorldSlotButton(n2, n3, 80, 80, () -> this.serverData, component -> {
            this.toolTip = component;
        }, n, button -> {
            RealmsWorldSlotButton.State state = ((RealmsWorldSlotButton)button).getState();
            if (state != null) {
                switch (state.action) {
                    case NOTHING: {
                        break;
                    }
                    case JOIN: {
                        this.joinRealm(this.serverData);
                        break;
                    }
                    case SWITCH_SLOT: {
                        if (state.minigame) {
                            this.switchToMinigame();
                            break;
                        }
                        if (state.empty) {
                            this.switchToEmptySlot(n, this.serverData);
                            break;
                        }
                        this.switchToFullSlot(n, this.serverData);
                        break;
                    }
                    default: {
                        throw new IllegalStateException("Unknown action " + (Object)((Object)state.action));
                    }
                }
            }
        });
        this.addButton(realmsWorldSlotButton);
    }

    private int leftButton(int n) {
        return this.leftX + n * 95;
    }

    private int centerButton(int n, int n2) {
        return this.width / 2 - (n2 * 105 - 5) / 2 + n * 105;
    }

    @Override
    public void tick() {
        super.tick();
        ++this.animTick;
        --this.clicks;
        if (this.clicks < 0) {
            this.clicks = 0;
        }
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.toolTip = null;
        this.renderBackground(poseStack);
        RealmsConfigureWorldScreen.drawCenteredString(poseStack, this.font, TITLE, this.width / 2, RealmsConfigureWorldScreen.row(4), 16777215);
        super.render(poseStack, n, n2, f);
        if (this.serverData == null) {
            RealmsConfigureWorldScreen.drawCenteredString(poseStack, this.font, WORLD_TITLE, this.width / 2, 17, 16777215);
            return;
        }
        String string = this.serverData.getName();
        int n3 = this.font.width(string);
        int n4 = this.serverData.state == RealmsServer.State.CLOSED ? 10526880 : 8388479;
        int n5 = this.font.width(WORLD_TITLE);
        RealmsConfigureWorldScreen.drawCenteredString(poseStack, this.font, WORLD_TITLE, this.width / 2, 12, 16777215);
        RealmsConfigureWorldScreen.drawCenteredString(poseStack, this.font, string, this.width / 2, 24, n4);
        int n6 = Math.min(this.centerButton(2, 3) + 80 - 11, this.width / 2 + n3 / 2 + n5 / 2 + 10);
        this.drawServerStatus(poseStack, n6, 7, n, n2);
        if (this.isMinigame()) {
            this.font.draw(poseStack, MINIGAME_PREFIX.copy().append(this.serverData.getMinigameName()), (float)(this.leftX + 80 + 20 + 10), (float)RealmsConfigureWorldScreen.row(13), 16777215);
        }
        if (this.toolTip != null) {
            this.renderMousehoverTooltip(poseStack, this.toolTip, n, n2);
        }
    }

    private int frame(int n) {
        return this.leftX + (n - 1) * 98;
    }

    @Override
    public void removed() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }

    @Override
    public boolean keyPressed(int n, int n2, int n3) {
        if (n == 256) {
            this.backButtonClicked();
            return true;
        }
        return super.keyPressed(n, n2, n3);
    }

    private void backButtonClicked() {
        if (this.stateChanged) {
            this.lastScreen.removeSelection();
        }
        this.minecraft.setScreen(this.lastScreen);
    }

    private void fetchServerData(long l) {
        new Thread(() -> {
            RealmsClient realmsClient = RealmsClient.create();
            try {
                this.serverData = realmsClient.getOwnWorld(l);
                this.disableButtons();
                if (this.isMinigame()) {
                    this.show(this.switchMinigameButton);
                } else {
                    this.show(this.optionsButton);
                    this.show(this.backupButton);
                    this.show(this.resetWorldButton);
                }
            }
            catch (RealmsServiceException realmsServiceException) {
                LOGGER.error("Couldn't get own world");
                this.minecraft.execute(() -> this.minecraft.setScreen(new RealmsGenericErrorScreen(Component.nullToEmpty(realmsServiceException.getMessage()), (Screen)this.lastScreen)));
            }
        }).start();
    }

    private void disableButtons() {
        this.playersButton.active = !this.serverData.expired;
        this.settingsButton.active = !this.serverData.expired;
        this.subscriptionButton.active = true;
        this.switchMinigameButton.active = !this.serverData.expired;
        this.optionsButton.active = !this.serverData.expired;
        this.resetWorldButton.active = !this.serverData.expired;
    }

    private void joinRealm(RealmsServer realmsServer) {
        if (this.serverData.state == RealmsServer.State.OPEN) {
            this.lastScreen.play(realmsServer, new RealmsConfigureWorldScreen(this.lastScreen.newScreen(), this.serverId));
        } else {
            this.openTheWorld(true, new RealmsConfigureWorldScreen(this.lastScreen.newScreen(), this.serverId));
        }
    }

    private void switchToMinigame() {
        RealmsSelectWorldTemplateScreen realmsSelectWorldTemplateScreen = new RealmsSelectWorldTemplateScreen(this, RealmsServer.WorldType.MINIGAME);
        realmsSelectWorldTemplateScreen.setTitle(new TranslatableComponent("mco.template.title.minigame"));
        realmsSelectWorldTemplateScreen.setWarning(new TranslatableComponent("mco.minigame.world.info.line1"), new TranslatableComponent("mco.minigame.world.info.line2"));
        this.minecraft.setScreen(realmsSelectWorldTemplateScreen);
    }

    private void switchToFullSlot(int n, RealmsServer realmsServer) {
        TranslatableComponent translatableComponent = new TranslatableComponent("mco.configure.world.slot.switch.question.line1");
        TranslatableComponent translatableComponent2 = new TranslatableComponent("mco.configure.world.slot.switch.question.line2");
        this.minecraft.setScreen(new RealmsLongConfirmationScreen(bl -> {
            if (bl) {
                this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new SwitchSlotTask(realmsServer.id, n, () -> this.minecraft.setScreen(this.getNewScreen()))));
            } else {
                this.minecraft.setScreen(this);
            }
        }, RealmsLongConfirmationScreen.Type.Info, translatableComponent, translatableComponent2, true));
    }

    private void switchToEmptySlot(int n, RealmsServer realmsServer) {
        TranslatableComponent translatableComponent = new TranslatableComponent("mco.configure.world.slot.switch.question.line1");
        TranslatableComponent translatableComponent2 = new TranslatableComponent("mco.configure.world.slot.switch.question.line2");
        this.minecraft.setScreen(new RealmsLongConfirmationScreen(bl -> {
            if (bl) {
                RealmsResetWorldScreen realmsResetWorldScreen = new RealmsResetWorldScreen(this, realmsServer, new TranslatableComponent("mco.configure.world.switch.slot"), new TranslatableComponent("mco.configure.world.switch.slot.subtitle"), 10526880, CommonComponents.GUI_CANCEL, () -> this.minecraft.setScreen(this.getNewScreen()), () -> this.minecraft.setScreen(this.getNewScreen()));
                realmsResetWorldScreen.setSlot(n);
                realmsResetWorldScreen.setResetTitle(new TranslatableComponent("mco.create.world.reset.title"));
                this.minecraft.setScreen(realmsResetWorldScreen);
            } else {
                this.minecraft.setScreen(this);
            }
        }, RealmsLongConfirmationScreen.Type.Info, translatableComponent, translatableComponent2, true));
    }

    protected void renderMousehoverTooltip(PoseStack poseStack, @Nullable Component component, int n, int n2) {
        int n3 = n + 12;
        int n4 = n2 - 12;
        int n5 = this.font.width(component);
        if (n3 + n5 + 3 > this.rightX) {
            n3 = n3 - n5 - 20;
        }
        this.fillGradient(poseStack, n3 - 3, n4 - 3, n3 + n5 + 3, n4 + 8 + 3, -1073741824, -1073741824);
        this.font.drawShadow(poseStack, component, (float)n3, (float)n4, 16777215);
    }

    private void drawServerStatus(PoseStack poseStack, int n, int n2, int n3, int n4) {
        if (this.serverData.expired) {
            this.drawExpired(poseStack, n, n2, n3, n4);
        } else if (this.serverData.state == RealmsServer.State.CLOSED) {
            this.drawClose(poseStack, n, n2, n3, n4);
        } else if (this.serverData.state == RealmsServer.State.OPEN) {
            if (this.serverData.daysLeft < 7) {
                this.drawExpiring(poseStack, n, n2, n3, n4, this.serverData.daysLeft);
            } else {
                this.drawOpen(poseStack, n, n2, n3, n4);
            }
        }
    }

    private void drawExpired(PoseStack poseStack, int n, int n2, int n3, int n4) {
        this.minecraft.getTextureManager().bind(EXPIRED_ICON_LOCATION);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GuiComponent.blit(poseStack, n, n2, 0.0f, 0.0f, 10, 28, 10, 28);
        if (n3 >= n && n3 <= n + 9 && n4 >= n2 && n4 <= n2 + 27) {
            this.toolTip = SERVER_EXPIRED_TOOLTIP;
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
        if (n3 >= n && n3 <= n + 9 && n4 >= n2 && n4 <= n2 + 27) {
            this.toolTip = n5 <= 0 ? SERVER_EXPIRING_SOON_TOOLTIP : (n5 == 1 ? SERVER_EXPIRING_IN_DAY_TOOLTIP : new TranslatableComponent("mco.selectServer.expires.days", n5));
        }
    }

    private void drawOpen(PoseStack poseStack, int n, int n2, int n3, int n4) {
        this.minecraft.getTextureManager().bind(ON_ICON_LOCATION);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GuiComponent.blit(poseStack, n, n2, 0.0f, 0.0f, 10, 28, 10, 28);
        if (n3 >= n && n3 <= n + 9 && n4 >= n2 && n4 <= n2 + 27) {
            this.toolTip = SERVER_OPEN_TOOLTIP;
        }
    }

    private void drawClose(PoseStack poseStack, int n, int n2, int n3, int n4) {
        this.minecraft.getTextureManager().bind(OFF_ICON_LOCATION);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GuiComponent.blit(poseStack, n, n2, 0.0f, 0.0f, 10, 28, 10, 28);
        if (n3 >= n && n3 <= n + 9 && n4 >= n2 && n4 <= n2 + 27) {
            this.toolTip = SERVER_CLOSED_TOOLTIP;
        }
    }

    private boolean isMinigame() {
        return this.serverData != null && this.serverData.worldType == RealmsServer.WorldType.MINIGAME;
    }

    private void hideRegularButtons() {
        this.hide(this.optionsButton);
        this.hide(this.backupButton);
        this.hide(this.resetWorldButton);
    }

    private void hide(Button button) {
        button.visible = false;
        this.children.remove(button);
        this.buttons.remove(button);
    }

    private void show(Button button) {
        button.visible = true;
        this.addButton(button);
    }

    private void hideMinigameButtons() {
        this.hide(this.switchMinigameButton);
    }

    public void saveSlotSettings(RealmsWorldOptions realmsWorldOptions) {
        RealmsWorldOptions realmsWorldOptions2 = this.serverData.slots.get(this.serverData.activeSlot);
        realmsWorldOptions.templateId = realmsWorldOptions2.templateId;
        realmsWorldOptions.templateImage = realmsWorldOptions2.templateImage;
        RealmsClient realmsClient = RealmsClient.create();
        try {
            realmsClient.updateSlot(this.serverData.id, this.serverData.activeSlot, realmsWorldOptions);
            this.serverData.slots.put(this.serverData.activeSlot, realmsWorldOptions);
        }
        catch (RealmsServiceException realmsServiceException) {
            LOGGER.error("Couldn't save slot settings");
            this.minecraft.setScreen(new RealmsGenericErrorScreen(realmsServiceException, (Screen)this));
            return;
        }
        this.minecraft.setScreen(this);
    }

    public void saveSettings(String string, String string2) {
        String string3 = string2.trim().isEmpty() ? null : string2;
        RealmsClient realmsClient = RealmsClient.create();
        try {
            realmsClient.update(this.serverData.id, string, string3);
            this.serverData.setName(string);
            this.serverData.setDescription(string3);
        }
        catch (RealmsServiceException realmsServiceException) {
            LOGGER.error("Couldn't save settings");
            this.minecraft.setScreen(new RealmsGenericErrorScreen(realmsServiceException, (Screen)this));
            return;
        }
        this.minecraft.setScreen(this);
    }

    public void openTheWorld(boolean bl, Screen screen) {
        this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(screen, new OpenServerTask(this.serverData, this, this.lastScreen, bl)));
    }

    public void closeTheWorld(Screen screen) {
        this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(screen, new CloseServerTask(this.serverData, this)));
    }

    public void stateChanged() {
        this.stateChanged = true;
    }

    @Override
    protected void callback(@Nullable WorldTemplate worldTemplate) {
        if (worldTemplate == null) {
            return;
        }
        if (WorldTemplate.WorldTemplateType.MINIGAME == worldTemplate.type) {
            this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new SwitchMinigameTask(this.serverData.id, worldTemplate, this.getNewScreen())));
        }
    }

    public RealmsConfigureWorldScreen getNewScreen() {
        return new RealmsConfigureWorldScreen(this.lastScreen, this.serverId);
    }

}

