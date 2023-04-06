/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsWorldSlotButton;
import com.mojang.realmsclient.gui.screens.RealmsDownloadLatestWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsResetWorldScreen;
import com.mojang.realmsclient.util.RealmsTextureManager;
import com.mojang.realmsclient.util.task.LongRunningTask;
import com.mojang.realmsclient.util.task.OpenServerTask;
import com.mojang.realmsclient.util.task.SwitchSlotTask;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.NarrationHelper;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsBrokenWorldScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Screen lastScreen;
    private final RealmsMainScreen mainScreen;
    private RealmsServer serverData;
    private final long serverId;
    private final Component header;
    private final Component[] message = new Component[]{new TranslatableComponent("mco.brokenworld.message.line1"), new TranslatableComponent("mco.brokenworld.message.line2")};
    private int leftX;
    private int rightX;
    private final List<Integer> slotsThatHasBeenDownloaded = Lists.newArrayList();
    private int animTick;

    public RealmsBrokenWorldScreen(Screen screen, RealmsMainScreen realmsMainScreen, long l, boolean bl) {
        this.lastScreen = screen;
        this.mainScreen = realmsMainScreen;
        this.serverId = l;
        this.header = bl ? new TranslatableComponent("mco.brokenworld.minigame.title") : new TranslatableComponent("mco.brokenworld.title");
    }

    @Override
    public void init() {
        this.leftX = this.width / 2 - 150;
        this.rightX = this.width / 2 + 190;
        this.addButton(new Button(this.rightX - 80 + 8, RealmsBrokenWorldScreen.row(13) - 5, 70, 20, CommonComponents.GUI_BACK, button -> this.backButtonClicked()));
        if (this.serverData == null) {
            this.fetchServerData(this.serverId);
        } else {
            this.addButtons();
        }
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        NarrationHelper.now(Stream.concat(Stream.of(this.header), Stream.of(this.message)).map(Component::getString).collect(Collectors.joining(" ")));
    }

    private void addButtons() {
        for (Map.Entry<Integer, RealmsWorldOptions> entry : this.serverData.slots.entrySet()) {
            int n = entry.getKey();
            boolean bl = n != this.serverData.activeSlot || this.serverData.worldType == RealmsServer.WorldType.MINIGAME;
            Button button2 = bl ? new Button(this.getFramePositionX(n), RealmsBrokenWorldScreen.row(8), 80, 20, new TranslatableComponent("mco.brokenworld.play"), button -> {
                if (this.serverData.slots.get((Object)Integer.valueOf((int)n)).empty) {
                    RealmsResetWorldScreen realmsResetWorldScreen = new RealmsResetWorldScreen(this, this.serverData, new TranslatableComponent("mco.configure.world.switch.slot"), new TranslatableComponent("mco.configure.world.switch.slot.subtitle"), 10526880, CommonComponents.GUI_CANCEL, this::doSwitchOrReset, () -> {
                        this.minecraft.setScreen(this);
                        this.doSwitchOrReset();
                    });
                    realmsResetWorldScreen.setSlot(n);
                    realmsResetWorldScreen.setResetTitle(new TranslatableComponent("mco.create.world.reset.title"));
                    this.minecraft.setScreen(realmsResetWorldScreen);
                } else {
                    this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new SwitchSlotTask(this.serverData.id, n, this::doSwitchOrReset)));
                }
            }) : new Button(this.getFramePositionX(n), RealmsBrokenWorldScreen.row(8), 80, 20, new TranslatableComponent("mco.brokenworld.download"), button -> {
                TranslatableComponent translatableComponent = new TranslatableComponent("mco.configure.world.restore.download.question.line1");
                TranslatableComponent translatableComponent2 = new TranslatableComponent("mco.configure.world.restore.download.question.line2");
                this.minecraft.setScreen(new RealmsLongConfirmationScreen(bl -> {
                    if (bl) {
                        this.downloadWorld(n);
                    } else {
                        this.minecraft.setScreen(this);
                    }
                }, RealmsLongConfirmationScreen.Type.Info, translatableComponent, translatableComponent2, true));
            });
            if (this.slotsThatHasBeenDownloaded.contains(n)) {
                button2.active = false;
                button2.setMessage(new TranslatableComponent("mco.brokenworld.downloaded"));
            }
            this.addButton(button2);
            this.addButton(new Button(this.getFramePositionX(n), RealmsBrokenWorldScreen.row(10), 80, 20, new TranslatableComponent("mco.brokenworld.reset"), button -> {
                RealmsResetWorldScreen realmsResetWorldScreen = new RealmsResetWorldScreen(this, this.serverData, this::doSwitchOrReset, () -> {
                    this.minecraft.setScreen(this);
                    this.doSwitchOrReset();
                });
                if (n != this.serverData.activeSlot || this.serverData.worldType == RealmsServer.WorldType.MINIGAME) {
                    realmsResetWorldScreen.setSlot(n);
                }
                this.minecraft.setScreen(realmsResetWorldScreen);
            }));
        }
    }

    @Override
    public void tick() {
        ++this.animTick;
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderBackground(poseStack);
        super.render(poseStack, n, n2, f);
        RealmsBrokenWorldScreen.drawCenteredString(poseStack, this.font, this.header, this.width / 2, 17, 16777215);
        for (int i = 0; i < this.message.length; ++i) {
            RealmsBrokenWorldScreen.drawCenteredString(poseStack, this.font, this.message[i], this.width / 2, RealmsBrokenWorldScreen.row(-1) + 3 + i * 12, 10526880);
        }
        if (this.serverData == null) {
            return;
        }
        for (Map.Entry<Integer, RealmsWorldOptions> entry : this.serverData.slots.entrySet()) {
            if (entry.getValue().templateImage != null && entry.getValue().templateId != -1L) {
                this.drawSlotFrame(poseStack, this.getFramePositionX(entry.getKey()), RealmsBrokenWorldScreen.row(1) + 5, n, n2, this.serverData.activeSlot == entry.getKey() && !this.isMinigame(), entry.getValue().getSlotName(entry.getKey()), entry.getKey(), entry.getValue().templateId, entry.getValue().templateImage, entry.getValue().empty);
                continue;
            }
            this.drawSlotFrame(poseStack, this.getFramePositionX(entry.getKey()), RealmsBrokenWorldScreen.row(1) + 5, n, n2, this.serverData.activeSlot == entry.getKey() && !this.isMinigame(), entry.getValue().getSlotName(entry.getKey()), entry.getKey(), -1L, null, entry.getValue().empty);
        }
    }

    private int getFramePositionX(int n) {
        return this.leftX + (n - 1) * 110;
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
        this.minecraft.setScreen(this.lastScreen);
    }

    private void fetchServerData(long l) {
        new Thread(() -> {
            RealmsClient realmsClient = RealmsClient.create();
            try {
                this.serverData = realmsClient.getOwnWorld(l);
                this.addButtons();
            }
            catch (RealmsServiceException realmsServiceException) {
                LOGGER.error("Couldn't get own world");
                this.minecraft.setScreen(new RealmsGenericErrorScreen(Component.nullToEmpty(realmsServiceException.getMessage()), this.lastScreen));
            }
        }).start();
    }

    public void doSwitchOrReset() {
        new Thread(() -> {
            RealmsClient realmsClient = RealmsClient.create();
            if (this.serverData.state == RealmsServer.State.CLOSED) {
                this.minecraft.execute(() -> this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this, new OpenServerTask(this.serverData, this, this.mainScreen, true))));
            } else {
                try {
                    this.mainScreen.newScreen().play(realmsClient.getOwnWorld(this.serverId), this);
                }
                catch (RealmsServiceException realmsServiceException) {
                    LOGGER.error("Couldn't get own world");
                    this.minecraft.execute(() -> this.minecraft.setScreen(this.lastScreen));
                }
            }
        }).start();
    }

    private void downloadWorld(int n) {
        RealmsClient realmsClient = RealmsClient.create();
        try {
            WorldDownload worldDownload = realmsClient.requestDownloadInfo(this.serverData.id, n);
            RealmsDownloadLatestWorldScreen realmsDownloadLatestWorldScreen = new RealmsDownloadLatestWorldScreen(this, worldDownload, this.serverData.getWorldName(n), bl -> {
                if (bl) {
                    this.slotsThatHasBeenDownloaded.add(n);
                    this.children.clear();
                    this.addButtons();
                } else {
                    this.minecraft.setScreen(this);
                }
            });
            this.minecraft.setScreen(realmsDownloadLatestWorldScreen);
        }
        catch (RealmsServiceException realmsServiceException) {
            LOGGER.error("Couldn't download world data");
            this.minecraft.setScreen(new RealmsGenericErrorScreen(realmsServiceException, (Screen)this));
        }
    }

    private boolean isMinigame() {
        return this.serverData != null && this.serverData.worldType == RealmsServer.WorldType.MINIGAME;
    }

    private void drawSlotFrame(PoseStack poseStack, int n, int n2, int n3, int n4, boolean bl, String string, int n5, long l, String string2, boolean bl2) {
        if (bl2) {
            this.minecraft.getTextureManager().bind(RealmsWorldSlotButton.EMPTY_SLOT_LOCATION);
        } else if (string2 != null && l != -1L) {
            RealmsTextureManager.bindWorldTemplate(String.valueOf(l), string2);
        } else if (n5 == 1) {
            this.minecraft.getTextureManager().bind(RealmsWorldSlotButton.DEFAULT_WORLD_SLOT_1);
        } else if (n5 == 2) {
            this.minecraft.getTextureManager().bind(RealmsWorldSlotButton.DEFAULT_WORLD_SLOT_2);
        } else if (n5 == 3) {
            this.minecraft.getTextureManager().bind(RealmsWorldSlotButton.DEFAULT_WORLD_SLOT_3);
        } else {
            RealmsTextureManager.bindWorldTemplate(String.valueOf(this.serverData.minigameId), this.serverData.minigameImage);
        }
        if (!bl) {
            RenderSystem.color4f(0.56f, 0.56f, 0.56f, 1.0f);
        } else if (bl) {
            float f = 0.9f + 0.1f * Mth.cos((float)this.animTick * 0.2f);
            RenderSystem.color4f(f, f, f, 1.0f);
        }
        GuiComponent.blit(poseStack, n + 3, n2 + 3, 0.0f, 0.0f, 74, 74, 74, 74);
        this.minecraft.getTextureManager().bind(RealmsWorldSlotButton.SLOT_FRAME_LOCATION);
        if (bl) {
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        } else {
            RenderSystem.color4f(0.56f, 0.56f, 0.56f, 1.0f);
        }
        GuiComponent.blit(poseStack, n, n2, 0.0f, 0.0f, 80, 80, 80, 80);
        RealmsBrokenWorldScreen.drawCenteredString(poseStack, this.font, string, n + 40, n2 + 66, 16777215);
    }
}

