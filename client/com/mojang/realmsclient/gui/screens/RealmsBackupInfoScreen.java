/*
 * Decompiled with CFR 0.146.
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.dto.Backup;
import com.mojang.realmsclient.gui.screens.RealmsSlotOptionsScreen;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.realms.RealmsScreen;

public class RealmsBackupInfoScreen
extends RealmsScreen {
    private final Screen lastScreen;
    private final Backup backup;
    private BackupInfoList backupInfoList;

    public RealmsBackupInfoScreen(Screen screen, Backup backup) {
        this.lastScreen = screen;
        this.backup = backup;
    }

    @Override
    public void tick() {
    }

    @Override
    public void init() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 120 + 24, 200, 20, CommonComponents.GUI_BACK, button -> this.minecraft.setScreen(this.lastScreen)));
        this.backupInfoList = new BackupInfoList(this.minecraft);
        this.addWidget(this.backupInfoList);
        this.magicalSpecialHackyFocus(this.backupInfoList);
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
        RealmsBackupInfoScreen.drawCenteredString(poseStack, this.font, "Changes from last backup", this.width / 2, 10, 16777215);
        this.backupInfoList.render(poseStack, n, n2, f);
        super.render(poseStack, n, n2, f);
    }

    private Component checkForSpecificMetadata(String string, String string2) {
        String string3 = string.toLowerCase(Locale.ROOT);
        if (string3.contains("game") && string3.contains("mode")) {
            return this.gameModeMetadata(string2);
        }
        if (string3.contains("game") && string3.contains("difficulty")) {
            return this.gameDifficultyMetadata(string2);
        }
        return new TextComponent(string2);
    }

    private Component gameDifficultyMetadata(String string) {
        try {
            return RealmsSlotOptionsScreen.DIFFICULTIES[Integer.parseInt(string)];
        }
        catch (Exception exception) {
            return new TextComponent("UNKNOWN");
        }
    }

    private Component gameModeMetadata(String string) {
        try {
            return RealmsSlotOptionsScreen.GAME_MODES[Integer.parseInt(string)];
        }
        catch (Exception exception) {
            return new TextComponent("UNKNOWN");
        }
    }

    static /* synthetic */ Minecraft access$000(RealmsBackupInfoScreen realmsBackupInfoScreen) {
        return realmsBackupInfoScreen.minecraft;
    }

    static /* synthetic */ Backup access$200(RealmsBackupInfoScreen realmsBackupInfoScreen) {
        return realmsBackupInfoScreen.backup;
    }

    class BackupInfoList
    extends ObjectSelectionList<BackupInfoListEntry> {
        public BackupInfoList(Minecraft minecraft) {
            super(minecraft, RealmsBackupInfoScreen.this.width, RealmsBackupInfoScreen.this.height, 32, RealmsBackupInfoScreen.this.height - 64, 36);
            this.setRenderSelection(false);
            if (RealmsBackupInfoScreen.access$200((RealmsBackupInfoScreen)RealmsBackupInfoScreen.this).changeList != null) {
                RealmsBackupInfoScreen.access$200((RealmsBackupInfoScreen)RealmsBackupInfoScreen.this).changeList.forEach((string, string2) -> this.addEntry(new BackupInfoListEntry((String)string, (String)string2)));
            }
        }
    }

    class BackupInfoListEntry
    extends ObjectSelectionList.Entry<BackupInfoListEntry> {
        private final String key;
        private final String value;

        public BackupInfoListEntry(String string, String string2) {
            this.key = string;
            this.value = string2;
        }

        @Override
        public void render(PoseStack poseStack, int n, int n2, int n3, int n4, int n5, int n6, int n7, boolean bl, float f) {
            Font font = RealmsBackupInfoScreen.access$000((RealmsBackupInfoScreen)RealmsBackupInfoScreen.this).font;
            GuiComponent.drawString(poseStack, font, this.key, n3, n2, 10526880);
            GuiComponent.drawString(poseStack, font, RealmsBackupInfoScreen.this.checkForSpecificMetadata(this.key, this.value), n3, n2 + 12, 16777215);
        }
    }

}

