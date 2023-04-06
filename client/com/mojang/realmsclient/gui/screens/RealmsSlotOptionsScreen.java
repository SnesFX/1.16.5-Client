/*
 * Decompiled with CFR 0.146.
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.Mth;

public class RealmsSlotOptionsScreen
extends RealmsScreen {
    public static final Component[] DIFFICULTIES = new Component[]{new TranslatableComponent("options.difficulty.peaceful"), new TranslatableComponent("options.difficulty.easy"), new TranslatableComponent("options.difficulty.normal"), new TranslatableComponent("options.difficulty.hard")};
    public static final Component[] GAME_MODES = new Component[]{new TranslatableComponent("selectWorld.gameMode.survival"), new TranslatableComponent("selectWorld.gameMode.creative"), new TranslatableComponent("selectWorld.gameMode.adventure")};
    private static final Component TEXT_ON = new TranslatableComponent("mco.configure.world.on");
    private static final Component TEXT_OFF = new TranslatableComponent("mco.configure.world.off");
    private static final Component GAME_MODE_LABEL = new TranslatableComponent("selectWorld.gameMode");
    private static final Component NAME_LABEL = new TranslatableComponent("mco.configure.world.edit.slot.name");
    private EditBox nameEdit;
    protected final RealmsConfigureWorldScreen parent;
    private int column1X;
    private int columnWidth;
    private int column2X;
    private final RealmsWorldOptions options;
    private final RealmsServer.WorldType worldType;
    private final int activeSlot;
    private int difficulty;
    private int gameMode;
    private Boolean pvp;
    private Boolean spawnNPCs;
    private Boolean spawnAnimals;
    private Boolean spawnMonsters;
    private Integer spawnProtection;
    private Boolean commandBlocks;
    private Boolean forceGameMode;
    private Button pvpButton;
    private Button spawnAnimalsButton;
    private Button spawnMonstersButton;
    private Button spawnNPCsButton;
    private SettingsSlider spawnProtectionButton;
    private Button commandBlocksButton;
    private Button forceGameModeButton;
    private RealmsLabel titleLabel;
    private RealmsLabel warningLabel;

    public RealmsSlotOptionsScreen(RealmsConfigureWorldScreen realmsConfigureWorldScreen, RealmsWorldOptions realmsWorldOptions, RealmsServer.WorldType worldType, int n) {
        this.parent = realmsConfigureWorldScreen;
        this.options = realmsWorldOptions;
        this.worldType = worldType;
        this.activeSlot = n;
    }

    @Override
    public void removed() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }

    @Override
    public void tick() {
        this.nameEdit.tick();
    }

    @Override
    public boolean keyPressed(int n, int n2, int n3) {
        if (n == 256) {
            this.minecraft.setScreen(this.parent);
            return true;
        }
        return super.keyPressed(n, n2, n3);
    }

    @Override
    public void init() {
        this.columnWidth = 170;
        this.column1X = this.width / 2 - this.columnWidth;
        this.column2X = this.width / 2 + 10;
        this.difficulty = this.options.difficulty;
        this.gameMode = this.options.gameMode;
        if (this.worldType == RealmsServer.WorldType.NORMAL) {
            this.pvp = this.options.pvp;
            this.spawnProtection = this.options.spawnProtection;
            this.forceGameMode = this.options.forceGameMode;
            this.spawnAnimals = this.options.spawnAnimals;
            this.spawnMonsters = this.options.spawnMonsters;
            this.spawnNPCs = this.options.spawnNPCs;
            this.commandBlocks = this.options.commandBlocks;
        } else {
            TranslatableComponent translatableComponent = this.worldType == RealmsServer.WorldType.ADVENTUREMAP ? new TranslatableComponent("mco.configure.world.edit.subscreen.adventuremap") : (this.worldType == RealmsServer.WorldType.INSPIRATION ? new TranslatableComponent("mco.configure.world.edit.subscreen.inspiration") : new TranslatableComponent("mco.configure.world.edit.subscreen.experience"));
            this.warningLabel = new RealmsLabel(translatableComponent, this.width / 2, 26, 16711680);
            this.pvp = true;
            this.spawnProtection = 0;
            this.forceGameMode = false;
            this.spawnAnimals = true;
            this.spawnMonsters = true;
            this.spawnNPCs = true;
            this.commandBlocks = true;
        }
        this.nameEdit = new EditBox(this.minecraft.font, this.column1X + 2, RealmsSlotOptionsScreen.row(1), this.columnWidth - 4, 20, null, new TranslatableComponent("mco.configure.world.edit.slot.name"));
        this.nameEdit.setMaxLength(10);
        this.nameEdit.setValue(this.options.getSlotName(this.activeSlot));
        this.magicalSpecialHackyFocus(this.nameEdit);
        this.pvpButton = this.addButton(new Button(this.column2X, RealmsSlotOptionsScreen.row(1), this.columnWidth, 20, this.pvpTitle(), button -> {
            this.pvp = this.pvp == false;
            button.setMessage(this.pvpTitle());
        }));
        this.addButton(new Button(this.column1X, RealmsSlotOptionsScreen.row(3), this.columnWidth, 20, this.gameModeTitle(), button -> {
            this.gameMode = (this.gameMode + 1) % GAME_MODES.length;
            button.setMessage(this.gameModeTitle());
        }));
        this.spawnAnimalsButton = this.addButton(new Button(this.column2X, RealmsSlotOptionsScreen.row(3), this.columnWidth, 20, this.spawnAnimalsTitle(), button -> {
            this.spawnAnimals = this.spawnAnimals == false;
            button.setMessage(this.spawnAnimalsTitle());
        }));
        this.addButton(new Button(this.column1X, RealmsSlotOptionsScreen.row(5), this.columnWidth, 20, this.difficultyTitle(), button -> {
            this.difficulty = (this.difficulty + 1) % DIFFICULTIES.length;
            button.setMessage(this.difficultyTitle());
            if (this.worldType == RealmsServer.WorldType.NORMAL) {
                this.spawnMonstersButton.active = this.difficulty != 0;
                this.spawnMonstersButton.setMessage(this.spawnMonstersTitle());
            }
        }));
        this.spawnMonstersButton = this.addButton(new Button(this.column2X, RealmsSlotOptionsScreen.row(5), this.columnWidth, 20, this.spawnMonstersTitle(), button -> {
            this.spawnMonsters = this.spawnMonsters == false;
            button.setMessage(this.spawnMonstersTitle());
        }));
        this.spawnProtectionButton = this.addButton(new SettingsSlider(this.column1X, RealmsSlotOptionsScreen.row(7), this.columnWidth, this.spawnProtection, 0.0f, 16.0f));
        this.spawnNPCsButton = this.addButton(new Button(this.column2X, RealmsSlotOptionsScreen.row(7), this.columnWidth, 20, this.spawnNPCsTitle(), button -> {
            this.spawnNPCs = this.spawnNPCs == false;
            button.setMessage(this.spawnNPCsTitle());
        }));
        this.forceGameModeButton = this.addButton(new Button(this.column1X, RealmsSlotOptionsScreen.row(9), this.columnWidth, 20, this.forceGameModeTitle(), button -> {
            this.forceGameMode = this.forceGameMode == false;
            button.setMessage(this.forceGameModeTitle());
        }));
        this.commandBlocksButton = this.addButton(new Button(this.column2X, RealmsSlotOptionsScreen.row(9), this.columnWidth, 20, this.commandBlocksTitle(), button -> {
            this.commandBlocks = this.commandBlocks == false;
            button.setMessage(this.commandBlocksTitle());
        }));
        if (this.worldType != RealmsServer.WorldType.NORMAL) {
            this.pvpButton.active = false;
            this.spawnAnimalsButton.active = false;
            this.spawnNPCsButton.active = false;
            this.spawnMonstersButton.active = false;
            this.spawnProtectionButton.active = false;
            this.commandBlocksButton.active = false;
            this.forceGameModeButton.active = false;
        }
        if (this.difficulty == 0) {
            this.spawnMonstersButton.active = false;
        }
        this.addButton(new Button(this.column1X, RealmsSlotOptionsScreen.row(13), this.columnWidth, 20, new TranslatableComponent("mco.configure.world.buttons.done"), button -> this.saveSettings()));
        this.addButton(new Button(this.column2X, RealmsSlotOptionsScreen.row(13), this.columnWidth, 20, CommonComponents.GUI_CANCEL, button -> this.minecraft.setScreen(this.parent)));
        this.addWidget(this.nameEdit);
        this.titleLabel = this.addWidget(new RealmsLabel(new TranslatableComponent("mco.configure.world.buttons.options"), this.width / 2, 17, 16777215));
        if (this.warningLabel != null) {
            this.addWidget(this.warningLabel);
        }
        this.narrateLabels();
    }

    private Component difficultyTitle() {
        return new TranslatableComponent("options.difficulty").append(": ").append(DIFFICULTIES[this.difficulty]);
    }

    private Component gameModeTitle() {
        return new TranslatableComponent("options.generic_value", GAME_MODE_LABEL, GAME_MODES[this.gameMode]);
    }

    private Component pvpTitle() {
        return new TranslatableComponent("mco.configure.world.pvp").append(": ").append(RealmsSlotOptionsScreen.getOnOff(this.pvp));
    }

    private Component spawnAnimalsTitle() {
        return new TranslatableComponent("mco.configure.world.spawnAnimals").append(": ").append(RealmsSlotOptionsScreen.getOnOff(this.spawnAnimals));
    }

    private Component spawnMonstersTitle() {
        if (this.difficulty == 0) {
            return new TranslatableComponent("mco.configure.world.spawnMonsters").append(": ").append(new TranslatableComponent("mco.configure.world.off"));
        }
        return new TranslatableComponent("mco.configure.world.spawnMonsters").append(": ").append(RealmsSlotOptionsScreen.getOnOff(this.spawnMonsters));
    }

    private Component spawnNPCsTitle() {
        return new TranslatableComponent("mco.configure.world.spawnNPCs").append(": ").append(RealmsSlotOptionsScreen.getOnOff(this.spawnNPCs));
    }

    private Component commandBlocksTitle() {
        return new TranslatableComponent("mco.configure.world.commandBlocks").append(": ").append(RealmsSlotOptionsScreen.getOnOff(this.commandBlocks));
    }

    private Component forceGameModeTitle() {
        return new TranslatableComponent("mco.configure.world.forceGameMode").append(": ").append(RealmsSlotOptionsScreen.getOnOff(this.forceGameMode));
    }

    private static Component getOnOff(boolean bl) {
        return bl ? TEXT_ON : TEXT_OFF;
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderBackground(poseStack);
        this.font.draw(poseStack, NAME_LABEL, (float)(this.column1X + this.columnWidth / 2 - this.font.width(NAME_LABEL) / 2), (float)(RealmsSlotOptionsScreen.row(0) - 5), 16777215);
        this.titleLabel.render(this, poseStack);
        if (this.warningLabel != null) {
            this.warningLabel.render(this, poseStack);
        }
        this.nameEdit.render(poseStack, n, n2, f);
        super.render(poseStack, n, n2, f);
    }

    private String getSlotName() {
        if (this.nameEdit.getValue().equals(this.options.getDefaultSlotName(this.activeSlot))) {
            return "";
        }
        return this.nameEdit.getValue();
    }

    private void saveSettings() {
        if (this.worldType == RealmsServer.WorldType.ADVENTUREMAP || this.worldType == RealmsServer.WorldType.EXPERIENCE || this.worldType == RealmsServer.WorldType.INSPIRATION) {
            this.parent.saveSlotSettings(new RealmsWorldOptions(this.options.pvp, this.options.spawnAnimals, this.options.spawnMonsters, this.options.spawnNPCs, this.options.spawnProtection, this.options.commandBlocks, this.difficulty, this.gameMode, this.options.forceGameMode, this.getSlotName()));
        } else {
            this.parent.saveSlotSettings(new RealmsWorldOptions(this.pvp, this.spawnAnimals, this.spawnMonsters, this.spawnNPCs, this.spawnProtection, this.commandBlocks, this.difficulty, this.gameMode, this.forceGameMode, this.getSlotName()));
        }
    }

    static /* synthetic */ SettingsSlider access$000(RealmsSlotOptionsScreen realmsSlotOptionsScreen) {
        return realmsSlotOptionsScreen.spawnProtectionButton;
    }

    class SettingsSlider
    extends AbstractSliderButton {
        private final double minValue;
        private final double maxValue;

        public SettingsSlider(int n, int n2, int n3, int n4, float f, float f2) {
            super(n, n2, n3, 20, TextComponent.EMPTY, 0.0);
            this.minValue = f;
            this.maxValue = f2;
            this.value = (Mth.clamp((float)n4, f, f2) - f) / (f2 - f);
            this.updateMessage();
        }

        @Override
        public void applyValue() {
            if (!RealmsSlotOptionsScreen.access$000((RealmsSlotOptionsScreen)RealmsSlotOptionsScreen.this).active) {
                return;
            }
            RealmsSlotOptionsScreen.this.spawnProtection = (int)Mth.lerp(Mth.clamp(this.value, 0.0, 1.0), this.minValue, this.maxValue);
        }

        @Override
        protected void updateMessage() {
            this.setMessage(new TranslatableComponent("mco.configure.world.spawnProtection").append(": ").append(RealmsSlotOptionsScreen.this.spawnProtection == 0 ? new TranslatableComponent("mco.configure.world.off") : new TextComponent(String.valueOf(RealmsSlotOptionsScreen.this.spawnProtection))));
        }

        @Override
        public void onClick(double d, double d2) {
        }

        @Override
        public void onRelease(double d, double d2) {
        }
    }

}

