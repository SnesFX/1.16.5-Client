/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.dto.WorldTemplatePaginatedList;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsResetNormalWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsScreenWithCallback;
import com.mojang.realmsclient.gui.screens.RealmsSelectFileToUploadScreen;
import com.mojang.realmsclient.gui.screens.RealmsSelectWorldTemplateScreen;
import com.mojang.realmsclient.util.task.LongRunningTask;
import com.mojang.realmsclient.util.task.ResettingWorldTask;
import com.mojang.realmsclient.util.task.SwitchSlotTask;
import javax.annotation.Nullable;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsResetWorldScreen
extends RealmsScreenWithCallback {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Screen lastScreen;
    private final RealmsServer serverData;
    private RealmsLabel titleLabel;
    private RealmsLabel subtitleLabel;
    private Component title = new TranslatableComponent("mco.reset.world.title");
    private Component subtitle = new TranslatableComponent("mco.reset.world.warning");
    private Component buttonTitle = CommonComponents.GUI_CANCEL;
    private int subtitleColor = 16711680;
    private static final ResourceLocation SLOT_FRAME_LOCATION = new ResourceLocation("realms", "textures/gui/realms/slot_frame.png");
    private static final ResourceLocation UPLOAD_LOCATION = new ResourceLocation("realms", "textures/gui/realms/upload.png");
    private static final ResourceLocation ADVENTURE_MAP_LOCATION = new ResourceLocation("realms", "textures/gui/realms/adventure.png");
    private static final ResourceLocation SURVIVAL_SPAWN_LOCATION = new ResourceLocation("realms", "textures/gui/realms/survival_spawn.png");
    private static final ResourceLocation NEW_WORLD_LOCATION = new ResourceLocation("realms", "textures/gui/realms/new_world.png");
    private static final ResourceLocation EXPERIENCE_LOCATION = new ResourceLocation("realms", "textures/gui/realms/experience.png");
    private static final ResourceLocation INSPIRATION_LOCATION = new ResourceLocation("realms", "textures/gui/realms/inspiration.png");
    private WorldTemplatePaginatedList templates;
    private WorldTemplatePaginatedList adventuremaps;
    private WorldTemplatePaginatedList experiences;
    private WorldTemplatePaginatedList inspirations;
    public int slot = -1;
    private ResetType typeToReset = ResetType.NONE;
    private ResetWorldInfo worldInfoToReset;
    private WorldTemplate worldTemplateToReset;
    @Nullable
    private Component resetTitle;
    private final Runnable resetWorldRunnable;
    private final Runnable callback;

    public RealmsResetWorldScreen(Screen screen, RealmsServer realmsServer, Runnable runnable, Runnable runnable2) {
        this.lastScreen = screen;
        this.serverData = realmsServer;
        this.resetWorldRunnable = runnable;
        this.callback = runnable2;
    }

    public RealmsResetWorldScreen(Screen screen, RealmsServer realmsServer, Component component, Component component2, int n, Component component3, Runnable runnable, Runnable runnable2) {
        this(screen, realmsServer, runnable, runnable2);
        this.title = component;
        this.subtitle = component2;
        this.subtitleColor = n;
        this.buttonTitle = component3;
    }

    public void setSlot(int n) {
        this.slot = n;
    }

    public void setResetTitle(Component component) {
        this.resetTitle = component;
    }

    @Override
    public void init() {
        this.addButton(new Button(this.width / 2 - 40, RealmsResetWorldScreen.row(14) - 10, 80, 20, this.buttonTitle, button -> this.minecraft.setScreen(this.lastScreen)));
        new Thread("Realms-reset-world-fetcher"){

            @Override
            public void run() {
                RealmsClient realmsClient = RealmsClient.create();
                try {
                    WorldTemplatePaginatedList worldTemplatePaginatedList = realmsClient.fetchWorldTemplates(1, 10, RealmsServer.WorldType.NORMAL);
                    WorldTemplatePaginatedList worldTemplatePaginatedList2 = realmsClient.fetchWorldTemplates(1, 10, RealmsServer.WorldType.ADVENTUREMAP);
                    WorldTemplatePaginatedList worldTemplatePaginatedList3 = realmsClient.fetchWorldTemplates(1, 10, RealmsServer.WorldType.EXPERIENCE);
                    WorldTemplatePaginatedList worldTemplatePaginatedList4 = realmsClient.fetchWorldTemplates(1, 10, RealmsServer.WorldType.INSPIRATION);
                    RealmsResetWorldScreen.this.minecraft.execute(() -> {
                        RealmsResetWorldScreen.this.templates = worldTemplatePaginatedList;
                        RealmsResetWorldScreen.this.adventuremaps = worldTemplatePaginatedList2;
                        RealmsResetWorldScreen.this.experiences = worldTemplatePaginatedList3;
                        RealmsResetWorldScreen.this.inspirations = worldTemplatePaginatedList4;
                    });
                }
                catch (RealmsServiceException realmsServiceException) {
                    LOGGER.error("Couldn't fetch templates in reset world", (Throwable)realmsServiceException);
                }
            }
        }.start();
        this.titleLabel = this.addWidget(new RealmsLabel(this.title, this.width / 2, 7, 16777215));
        this.subtitleLabel = this.addWidget(new RealmsLabel(this.subtitle, this.width / 2, 22, this.subtitleColor));
        this.addButton(new FrameButton(this.frame(1), RealmsResetWorldScreen.row(0) + 10, new TranslatableComponent("mco.reset.world.generate"), NEW_WORLD_LOCATION, button -> this.minecraft.setScreen(new RealmsResetNormalWorldScreen(this, this.title))));
        this.addButton(new FrameButton(this.frame(2), RealmsResetWorldScreen.row(0) + 10, new TranslatableComponent("mco.reset.world.upload"), UPLOAD_LOCATION, button -> {
            RealmsSelectFileToUploadScreen realmsSelectFileToUploadScreen = new RealmsSelectFileToUploadScreen(this.serverData.id, this.slot != -1 ? this.slot : this.serverData.activeSlot, this, this.callback);
            this.minecraft.setScreen(realmsSelectFileToUploadScreen);
        }));
        this.addButton(new FrameButton(this.frame(3), RealmsResetWorldScreen.row(0) + 10, new TranslatableComponent("mco.reset.world.template"), SURVIVAL_SPAWN_LOCATION, button -> {
            RealmsSelectWorldTemplateScreen realmsSelectWorldTemplateScreen = new RealmsSelectWorldTemplateScreen(this, RealmsServer.WorldType.NORMAL, this.templates);
            realmsSelectWorldTemplateScreen.setTitle(new TranslatableComponent("mco.reset.world.template"));
            this.minecraft.setScreen(realmsSelectWorldTemplateScreen);
        }));
        this.addButton(new FrameButton(this.frame(1), RealmsResetWorldScreen.row(6) + 20, new TranslatableComponent("mco.reset.world.adventure"), ADVENTURE_MAP_LOCATION, button -> {
            RealmsSelectWorldTemplateScreen realmsSelectWorldTemplateScreen = new RealmsSelectWorldTemplateScreen(this, RealmsServer.WorldType.ADVENTUREMAP, this.adventuremaps);
            realmsSelectWorldTemplateScreen.setTitle(new TranslatableComponent("mco.reset.world.adventure"));
            this.minecraft.setScreen(realmsSelectWorldTemplateScreen);
        }));
        this.addButton(new FrameButton(this.frame(2), RealmsResetWorldScreen.row(6) + 20, new TranslatableComponent("mco.reset.world.experience"), EXPERIENCE_LOCATION, button -> {
            RealmsSelectWorldTemplateScreen realmsSelectWorldTemplateScreen = new RealmsSelectWorldTemplateScreen(this, RealmsServer.WorldType.EXPERIENCE, this.experiences);
            realmsSelectWorldTemplateScreen.setTitle(new TranslatableComponent("mco.reset.world.experience"));
            this.minecraft.setScreen(realmsSelectWorldTemplateScreen);
        }));
        this.addButton(new FrameButton(this.frame(3), RealmsResetWorldScreen.row(6) + 20, new TranslatableComponent("mco.reset.world.inspiration"), INSPIRATION_LOCATION, button -> {
            RealmsSelectWorldTemplateScreen realmsSelectWorldTemplateScreen = new RealmsSelectWorldTemplateScreen(this, RealmsServer.WorldType.INSPIRATION, this.inspirations);
            realmsSelectWorldTemplateScreen.setTitle(new TranslatableComponent("mco.reset.world.inspiration"));
            this.minecraft.setScreen(realmsSelectWorldTemplateScreen);
        }));
        this.narrateLabels();
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

    private int frame(int n) {
        return this.width / 2 - 130 + (n - 1) * 100;
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderBackground(poseStack);
        this.titleLabel.render(this, poseStack);
        this.subtitleLabel.render(this, poseStack);
        super.render(poseStack, n, n2, f);
    }

    private void drawFrame(PoseStack poseStack, int n, int n2, Component component, ResourceLocation resourceLocation, boolean bl, boolean bl2) {
        this.minecraft.getTextureManager().bind(resourceLocation);
        if (bl) {
            RenderSystem.color4f(0.56f, 0.56f, 0.56f, 1.0f);
        } else {
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        }
        GuiComponent.blit(poseStack, n + 2, n2 + 14, 0.0f, 0.0f, 56, 56, 56, 56);
        this.minecraft.getTextureManager().bind(SLOT_FRAME_LOCATION);
        if (bl) {
            RenderSystem.color4f(0.56f, 0.56f, 0.56f, 1.0f);
        } else {
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        }
        GuiComponent.blit(poseStack, n, n2 + 12, 0.0f, 0.0f, 60, 60, 60, 60);
        int n3 = bl ? 10526880 : 16777215;
        RealmsResetWorldScreen.drawCenteredString(poseStack, this.font, component, n + 30, n2, n3);
    }

    @Override
    protected void callback(@Nullable WorldTemplate worldTemplate) {
        if (worldTemplate == null) {
            return;
        }
        if (this.slot == -1) {
            this.resetWorldWithTemplate(worldTemplate);
        } else {
            switch (worldTemplate.type) {
                case WORLD_TEMPLATE: {
                    this.typeToReset = ResetType.SURVIVAL_SPAWN;
                    break;
                }
                case ADVENTUREMAP: {
                    this.typeToReset = ResetType.ADVENTURE;
                    break;
                }
                case EXPERIENCE: {
                    this.typeToReset = ResetType.EXPERIENCE;
                    break;
                }
                case INSPIRATION: {
                    this.typeToReset = ResetType.INSPIRATION;
                }
            }
            this.worldTemplateToReset = worldTemplate;
            this.switchSlot();
        }
    }

    private void switchSlot() {
        this.switchSlot(() -> {
            switch (this.typeToReset) {
                case ADVENTURE: 
                case SURVIVAL_SPAWN: 
                case EXPERIENCE: 
                case INSPIRATION: {
                    if (this.worldTemplateToReset == null) break;
                    this.resetWorldWithTemplate(this.worldTemplateToReset);
                    break;
                }
                case GENERATE: {
                    if (this.worldInfoToReset == null) break;
                    this.triggerResetWorld(this.worldInfoToReset);
                    break;
                }
            }
        });
    }

    public void switchSlot(Runnable runnable) {
        this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new SwitchSlotTask(this.serverData.id, this.slot, runnable)));
    }

    public void resetWorldWithTemplate(WorldTemplate worldTemplate) {
        this.resetWorld(null, worldTemplate, -1, true);
    }

    private void triggerResetWorld(ResetWorldInfo resetWorldInfo) {
        this.resetWorld(resetWorldInfo.seed, null, resetWorldInfo.levelType, resetWorldInfo.generateStructures);
    }

    private void resetWorld(@Nullable String string, @Nullable WorldTemplate worldTemplate, int n, boolean bl) {
        this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new ResettingWorldTask(string, worldTemplate, n, bl, this.serverData.id, this.resetTitle, this.resetWorldRunnable)));
    }

    public void resetWorld(ResetWorldInfo resetWorldInfo) {
        if (this.slot == -1) {
            this.triggerResetWorld(resetWorldInfo);
        } else {
            this.typeToReset = ResetType.GENERATE;
            this.worldInfoToReset = resetWorldInfo;
            this.switchSlot();
        }
    }

    class FrameButton
    extends Button {
        private final ResourceLocation image;

        public FrameButton(int n, int n2, Component component, ResourceLocation resourceLocation, Button.OnPress onPress) {
            super(n, n2, 60, 72, component, onPress);
            this.image = resourceLocation;
        }

        @Override
        public void renderButton(PoseStack poseStack, int n, int n2, float f) {
            RealmsResetWorldScreen.this.drawFrame(poseStack, this.x, this.y, this.getMessage(), this.image, this.isHovered(), this.isMouseOver(n, n2));
        }
    }

    public static class ResetWorldInfo {
        private final String seed;
        private final int levelType;
        private final boolean generateStructures;

        public ResetWorldInfo(String string, int n, boolean bl) {
            this.seed = string;
            this.levelType = n;
            this.generateStructures = bl;
        }
    }

    static enum ResetType {
        NONE,
        GENERATE,
        UPLOAD,
        ADVENTURE,
        SURVIVAL_SPAWN,
        EXPERIENCE,
        INSPIRATION;
        
    }

}

