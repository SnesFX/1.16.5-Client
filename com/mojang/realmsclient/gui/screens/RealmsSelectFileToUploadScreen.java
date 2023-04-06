/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsResetWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsUploadScreen;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.NarrationHelper;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsSelectFileToUploadScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Component WORLD_TEXT = new TranslatableComponent("selectWorld.world");
    private static final Component REQUIRES_CONVERSION_TEXT = new TranslatableComponent("selectWorld.conversion");
    private static final Component HARDCORE_TEXT = new TranslatableComponent("mco.upload.hardcore").withStyle(ChatFormatting.DARK_RED);
    private static final Component CHEATS_TEXT = new TranslatableComponent("selectWorld.cheats");
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat();
    private final RealmsResetWorldScreen lastScreen;
    private final long worldId;
    private final int slotId;
    private Button uploadButton;
    private List<LevelSummary> levelList = Lists.newArrayList();
    private int selectedWorld = -1;
    private WorldSelectionList worldSelectionList;
    private RealmsLabel titleLabel;
    private RealmsLabel subtitleLabel;
    private RealmsLabel noWorldsLabel;
    private final Runnable callback;

    public RealmsSelectFileToUploadScreen(long l, int n, RealmsResetWorldScreen realmsResetWorldScreen, Runnable runnable) {
        this.lastScreen = realmsResetWorldScreen;
        this.worldId = l;
        this.slotId = n;
        this.callback = runnable;
    }

    private void loadLevelList() throws Exception {
        this.levelList = this.minecraft.getLevelSource().getLevelList().stream().sorted((levelSummary, levelSummary2) -> {
            if (levelSummary.getLastPlayed() < levelSummary2.getLastPlayed()) {
                return 1;
            }
            if (levelSummary.getLastPlayed() > levelSummary2.getLastPlayed()) {
                return -1;
            }
            return levelSummary.getLevelId().compareTo(levelSummary2.getLevelId());
        }).collect(Collectors.toList());
        for (LevelSummary levelSummary3 : this.levelList) {
            this.worldSelectionList.addEntry(levelSummary3);
        }
    }

    @Override
    public void init() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.worldSelectionList = new WorldSelectionList();
        try {
            this.loadLevelList();
        }
        catch (Exception exception) {
            LOGGER.error("Couldn't load level list", (Throwable)exception);
            this.minecraft.setScreen(new RealmsGenericErrorScreen(new TextComponent("Unable to load worlds"), Component.nullToEmpty(exception.getMessage()), this.lastScreen));
            return;
        }
        this.addWidget(this.worldSelectionList);
        this.uploadButton = this.addButton(new Button(this.width / 2 - 154, this.height - 32, 153, 20, new TranslatableComponent("mco.upload.button.name"), button -> this.upload()));
        this.uploadButton.active = this.selectedWorld >= 0 && this.selectedWorld < this.levelList.size();
        this.addButton(new Button(this.width / 2 + 6, this.height - 32, 153, 20, CommonComponents.GUI_BACK, button -> this.minecraft.setScreen(this.lastScreen)));
        this.titleLabel = this.addWidget(new RealmsLabel(new TranslatableComponent("mco.upload.select.world.title"), this.width / 2, 13, 16777215));
        this.subtitleLabel = this.addWidget(new RealmsLabel(new TranslatableComponent("mco.upload.select.world.subtitle"), this.width / 2, RealmsSelectFileToUploadScreen.row(-1), 10526880));
        this.noWorldsLabel = this.levelList.isEmpty() ? this.addWidget(new RealmsLabel(new TranslatableComponent("mco.upload.select.world.none"), this.width / 2, this.height / 2 - 20, 16777215)) : null;
        this.narrateLabels();
    }

    @Override
    public void removed() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }

    private void upload() {
        if (this.selectedWorld != -1 && !this.levelList.get(this.selectedWorld).isHardcore()) {
            LevelSummary levelSummary = this.levelList.get(this.selectedWorld);
            this.minecraft.setScreen(new RealmsUploadScreen(this.worldId, this.slotId, this.lastScreen, levelSummary, this.callback));
        }
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderBackground(poseStack);
        this.worldSelectionList.render(poseStack, n, n2, f);
        this.titleLabel.render(this, poseStack);
        this.subtitleLabel.render(this, poseStack);
        if (this.noWorldsLabel != null) {
            this.noWorldsLabel.render(this, poseStack);
        }
        super.render(poseStack, n, n2, f);
    }

    @Override
    public boolean keyPressed(int n, int n2, int n3) {
        if (n == 256) {
            this.minecraft.setScreen(this.lastScreen);
            return true;
        }
        return super.keyPressed(n, n2, n3);
    }

    private static Component gameModeName(LevelSummary levelSummary) {
        return levelSummary.getGameMode().getDisplayName();
    }

    private static String formatLastPlayed(LevelSummary levelSummary) {
        return DATE_FORMAT.format(new Date(levelSummary.getLastPlayed()));
    }

    static /* synthetic */ Button access$500(RealmsSelectFileToUploadScreen realmsSelectFileToUploadScreen) {
        return realmsSelectFileToUploadScreen.uploadButton;
    }

    class Entry
    extends ObjectSelectionList.Entry<Entry> {
        private final LevelSummary levelSummary;
        private final String name;
        private final String id;
        private final Component info;

        public Entry(LevelSummary levelSummary) {
            this.levelSummary = levelSummary;
            this.name = levelSummary.getLevelName();
            this.id = levelSummary.getLevelId() + " (" + RealmsSelectFileToUploadScreen.formatLastPlayed(levelSummary) + ")";
            if (levelSummary.isRequiresConversion()) {
                this.info = REQUIRES_CONVERSION_TEXT;
            } else {
                Component component = levelSummary.isHardcore() ? HARDCORE_TEXT : RealmsSelectFileToUploadScreen.gameModeName(levelSummary);
                if (levelSummary.hasCheats()) {
                    component = component.copy().append(", ").append(CHEATS_TEXT);
                }
                this.info = component;
            }
        }

        @Override
        public void render(PoseStack poseStack, int n, int n2, int n3, int n4, int n5, int n6, int n7, boolean bl, float f) {
            this.renderItem(poseStack, this.levelSummary, n, n3, n2);
        }

        @Override
        public boolean mouseClicked(double d, double d2, int n) {
            RealmsSelectFileToUploadScreen.this.worldSelectionList.selectItem(RealmsSelectFileToUploadScreen.this.levelList.indexOf(this.levelSummary));
            return true;
        }

        protected void renderItem(PoseStack poseStack, LevelSummary levelSummary, int n, int n2, int n3) {
            String string = this.name.isEmpty() ? WORLD_TEXT + " " + (n + 1) : this.name;
            RealmsSelectFileToUploadScreen.this.font.draw(poseStack, string, (float)(n2 + 2), (float)(n3 + 1), 16777215);
            RealmsSelectFileToUploadScreen.this.font.draw(poseStack, this.id, (float)(n2 + 2), (float)(n3 + 12), 8421504);
            RealmsSelectFileToUploadScreen.this.font.draw(poseStack, this.info, (float)(n2 + 2), (float)(n3 + 12 + 10), 8421504);
        }
    }

    class WorldSelectionList
    extends RealmsObjectSelectionList<Entry> {
        public WorldSelectionList() {
            super(RealmsSelectFileToUploadScreen.this.width, RealmsSelectFileToUploadScreen.this.height, RealmsSelectFileToUploadScreen.row(0), RealmsSelectFileToUploadScreen.this.height - 40, 36);
        }

        public void addEntry(LevelSummary levelSummary) {
            this.addEntry(new Entry(levelSummary));
        }

        @Override
        public int getMaxPosition() {
            return RealmsSelectFileToUploadScreen.this.levelList.size() * 36;
        }

        @Override
        public boolean isFocused() {
            return RealmsSelectFileToUploadScreen.this.getFocused() == this;
        }

        @Override
        public void renderBackground(PoseStack poseStack) {
            RealmsSelectFileToUploadScreen.this.renderBackground(poseStack);
        }

        @Override
        public void selectItem(int n) {
            this.setSelectedItem(n);
            if (n != -1) {
                LevelSummary levelSummary = (LevelSummary)RealmsSelectFileToUploadScreen.this.levelList.get(n);
                String string = I18n.get("narrator.select.list.position", n + 1, RealmsSelectFileToUploadScreen.this.levelList.size());
                String string2 = NarrationHelper.join(Arrays.asList(levelSummary.getLevelName(), RealmsSelectFileToUploadScreen.formatLastPlayed(levelSummary), RealmsSelectFileToUploadScreen.gameModeName(levelSummary).getString(), string));
                NarrationHelper.now(I18n.get("narrator.select", string2));
            }
        }

        @Override
        public void setSelected(@Nullable Entry entry) {
            super.setSelected(entry);
            RealmsSelectFileToUploadScreen.this.selectedWorld = this.children().indexOf(entry);
            RealmsSelectFileToUploadScreen.access$500((RealmsSelectFileToUploadScreen)RealmsSelectFileToUploadScreen.this).active = RealmsSelectFileToUploadScreen.this.selectedWorld >= 0 && RealmsSelectFileToUploadScreen.this.selectedWorld < this.getItemCount() && !((LevelSummary)RealmsSelectFileToUploadScreen.this.levelList.get(RealmsSelectFileToUploadScreen.this.selectedWorld)).isHardcore();
        }
    }

}

