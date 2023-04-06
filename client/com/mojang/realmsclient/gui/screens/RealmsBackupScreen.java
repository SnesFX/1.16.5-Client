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
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.Backup;
import com.mojang.realmsclient.dto.BackupList;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.RealmsBackupInfoScreen;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.util.RealmsUtil;
import com.mojang.realmsclient.util.task.DownloadTask;
import com.mojang.realmsclient.util.task.LongRunningTask;
import com.mojang.realmsclient.util.task.RestoreTask;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.NarrationHelper;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsBackupScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation PLUS_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/plus_icon.png");
    private static final ResourceLocation RESTORE_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/restore_icon.png");
    private static final Component RESTORE_TOOLTIP = new TranslatableComponent("mco.backup.button.restore");
    private static final Component HAS_CHANGES_TOOLTIP = new TranslatableComponent("mco.backup.changes.tooltip");
    private static final Component TITLE = new TranslatableComponent("mco.configure.world.backup");
    private static final Component NO_BACKUPS_LABEL = new TranslatableComponent("mco.backup.nobackups");
    private static int lastScrollPosition = -1;
    private final RealmsConfigureWorldScreen lastScreen;
    private List<Backup> backups = Collections.emptyList();
    @Nullable
    private Component toolTip;
    private BackupObjectSelectionList backupObjectSelectionList;
    private int selectedBackup = -1;
    private final int slotId;
    private Button downloadButton;
    private Button restoreButton;
    private Button changesButton;
    private Boolean noBackups = false;
    private final RealmsServer serverData;
    private RealmsLabel titleLabel;

    public RealmsBackupScreen(RealmsConfigureWorldScreen realmsConfigureWorldScreen, RealmsServer realmsServer, int n) {
        this.lastScreen = realmsConfigureWorldScreen;
        this.serverData = realmsServer;
        this.slotId = n;
    }

    @Override
    public void init() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.backupObjectSelectionList = new BackupObjectSelectionList();
        if (lastScrollPosition != -1) {
            this.backupObjectSelectionList.setScrollAmount(lastScrollPosition);
        }
        new Thread("Realms-fetch-backups"){

            @Override
            public void run() {
                RealmsClient realmsClient = RealmsClient.create();
                try {
                    List<Backup> list = realmsClient.backupsFor((long)RealmsBackupScreen.access$000((RealmsBackupScreen)RealmsBackupScreen.this).id).backups;
                    RealmsBackupScreen.this.minecraft.execute(() -> {
                        RealmsBackupScreen.this.backups = list;
                        RealmsBackupScreen.this.noBackups = RealmsBackupScreen.this.backups.isEmpty();
                        RealmsBackupScreen.this.backupObjectSelectionList.clear();
                        for (Backup backup : RealmsBackupScreen.this.backups) {
                            RealmsBackupScreen.this.backupObjectSelectionList.addEntry(backup);
                        }
                        RealmsBackupScreen.this.generateChangeList();
                    });
                }
                catch (RealmsServiceException realmsServiceException) {
                    LOGGER.error("Couldn't request backups", (Throwable)realmsServiceException);
                }
            }
        }.start();
        this.downloadButton = this.addButton(new Button(this.width - 135, RealmsBackupScreen.row(1), 120, 20, new TranslatableComponent("mco.backup.button.download"), button -> this.downloadClicked()));
        this.restoreButton = this.addButton(new Button(this.width - 135, RealmsBackupScreen.row(3), 120, 20, new TranslatableComponent("mco.backup.button.restore"), button -> this.restoreClicked(this.selectedBackup)));
        this.changesButton = this.addButton(new Button(this.width - 135, RealmsBackupScreen.row(5), 120, 20, new TranslatableComponent("mco.backup.changes.tooltip"), button -> {
            this.minecraft.setScreen(new RealmsBackupInfoScreen(this, this.backups.get(this.selectedBackup)));
            this.selectedBackup = -1;
        }));
        this.addButton(new Button(this.width - 100, this.height - 35, 85, 20, CommonComponents.GUI_BACK, button -> this.minecraft.setScreen(this.lastScreen)));
        this.addWidget(this.backupObjectSelectionList);
        this.titleLabel = this.addWidget(new RealmsLabel(new TranslatableComponent("mco.configure.world.backup"), this.width / 2, 12, 16777215));
        this.magicalSpecialHackyFocus(this.backupObjectSelectionList);
        this.updateButtonStates();
        this.narrateLabels();
    }

    private void generateChangeList() {
        if (this.backups.size() <= 1) {
            return;
        }
        for (int i = 0; i < this.backups.size() - 1; ++i) {
            Backup backup = this.backups.get(i);
            Backup backup2 = this.backups.get(i + 1);
            if (backup.metadata.isEmpty() || backup2.metadata.isEmpty()) continue;
            for (String string : backup.metadata.keySet()) {
                if (!string.contains("Uploaded") && backup2.metadata.containsKey(string)) {
                    if (backup.metadata.get(string).equals(backup2.metadata.get(string))) continue;
                    this.addToChangeList(backup, string);
                    continue;
                }
                this.addToChangeList(backup, string);
            }
        }
    }

    private void addToChangeList(Backup backup, String string) {
        if (string.contains("Uploaded")) {
            String string2 = DateFormat.getDateTimeInstance(3, 3).format(backup.lastModifiedDate);
            backup.changeList.put(string, string2);
            backup.setUploadedVersion(true);
        } else {
            backup.changeList.put(string, backup.metadata.get(string));
        }
    }

    private void updateButtonStates() {
        this.restoreButton.visible = this.shouldRestoreButtonBeVisible();
        this.changesButton.visible = this.shouldChangesButtonBeVisible();
    }

    private boolean shouldChangesButtonBeVisible() {
        if (this.selectedBackup == -1) {
            return false;
        }
        return !this.backups.get((int)this.selectedBackup).changeList.isEmpty();
    }

    private boolean shouldRestoreButtonBeVisible() {
        if (this.selectedBackup == -1) {
            return false;
        }
        return !this.serverData.expired;
    }

    @Override
    public boolean keyPressed(int n, int n2, int n3) {
        if (n == 256) {
            this.minecraft.setScreen(this.lastScreen);
            return true;
        }
        return super.keyPressed(n, n2, n3);
    }

    private void restoreClicked(int n) {
        if (n >= 0 && n < this.backups.size() && !this.serverData.expired) {
            this.selectedBackup = n;
            Date date = this.backups.get((int)n).lastModifiedDate;
            String string = DateFormat.getDateTimeInstance(3, 3).format(date);
            String string2 = RealmsUtil.convertToAgePresentationFromInstant(date);
            TranslatableComponent translatableComponent = new TranslatableComponent("mco.configure.world.restore.question.line1", string, string2);
            TranslatableComponent translatableComponent2 = new TranslatableComponent("mco.configure.world.restore.question.line2");
            this.minecraft.setScreen(new RealmsLongConfirmationScreen(bl -> {
                if (bl) {
                    this.restore();
                } else {
                    this.selectedBackup = -1;
                    this.minecraft.setScreen(this);
                }
            }, RealmsLongConfirmationScreen.Type.Warning, translatableComponent, translatableComponent2, true));
        }
    }

    private void downloadClicked() {
        TranslatableComponent translatableComponent = new TranslatableComponent("mco.configure.world.restore.download.question.line1");
        TranslatableComponent translatableComponent2 = new TranslatableComponent("mco.configure.world.restore.download.question.line2");
        this.minecraft.setScreen(new RealmsLongConfirmationScreen(bl -> {
            if (bl) {
                this.downloadWorldData();
            } else {
                this.minecraft.setScreen(this);
            }
        }, RealmsLongConfirmationScreen.Type.Info, translatableComponent, translatableComponent2, true));
    }

    private void downloadWorldData() {
        this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen.getNewScreen(), new DownloadTask(this.serverData.id, this.slotId, this.serverData.name + " (" + this.serverData.slots.get(this.serverData.activeSlot).getSlotName(this.serverData.activeSlot) + ")", this)));
    }

    private void restore() {
        Backup backup = this.backups.get(this.selectedBackup);
        this.selectedBackup = -1;
        this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen.getNewScreen(), new RestoreTask(backup, this.serverData.id, this.lastScreen)));
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.toolTip = null;
        this.renderBackground(poseStack);
        this.backupObjectSelectionList.render(poseStack, n, n2, f);
        this.titleLabel.render(this, poseStack);
        this.font.draw(poseStack, TITLE, (float)((this.width - 150) / 2 - 90), 20.0f, 10526880);
        if (this.noBackups.booleanValue()) {
            this.font.draw(poseStack, NO_BACKUPS_LABEL, 20.0f, (float)(this.height / 2 - 10), 16777215);
        }
        this.downloadButton.active = this.noBackups == false;
        super.render(poseStack, n, n2, f);
        if (this.toolTip != null) {
            this.renderMousehoverTooltip(poseStack, this.toolTip, n, n2);
        }
    }

    protected void renderMousehoverTooltip(PoseStack poseStack, @Nullable Component component, int n, int n2) {
        if (component == null) {
            return;
        }
        int n3 = n + 12;
        int n4 = n2 - 12;
        int n5 = this.font.width(component);
        this.fillGradient(poseStack, n3 - 3, n4 - 3, n3 + n5 + 3, n4 + 8 + 3, -1073741824, -1073741824);
        this.font.drawShadow(poseStack, component, (float)n3, (float)n4, 16777215);
    }

    static /* synthetic */ RealmsServer access$000(RealmsBackupScreen realmsBackupScreen) {
        return realmsBackupScreen.serverData;
    }

    class Entry
    extends ObjectSelectionList.Entry<Entry> {
        private final Backup backup;

        public Entry(Backup backup) {
            this.backup = backup;
        }

        @Override
        public void render(PoseStack poseStack, int n, int n2, int n3, int n4, int n5, int n6, int n7, boolean bl, float f) {
            this.renderBackupItem(poseStack, this.backup, n3 - 40, n2, n6, n7);
        }

        private void renderBackupItem(PoseStack poseStack, Backup backup, int n, int n2, int n3, int n4) {
            int n5 = backup.isUploadedVersion() ? -8388737 : 16777215;
            RealmsBackupScreen.this.font.draw(poseStack, "Backup (" + RealmsUtil.convertToAgePresentationFromInstant(backup.lastModifiedDate) + ")", (float)(n + 40), (float)(n2 + 1), n5);
            RealmsBackupScreen.this.font.draw(poseStack, this.getMediumDatePresentation(backup.lastModifiedDate), (float)(n + 40), (float)(n2 + 12), 5000268);
            int n6 = RealmsBackupScreen.this.width - 175;
            int n7 = -3;
            int n8 = n6 - 10;
            boolean bl = false;
            if (!RealmsBackupScreen.access$000((RealmsBackupScreen)RealmsBackupScreen.this).expired) {
                this.drawRestore(poseStack, n6, n2 + -3, n3, n4);
            }
            if (!backup.changeList.isEmpty()) {
                this.drawInfo(poseStack, n8, n2 + 0, n3, n4);
            }
        }

        private String getMediumDatePresentation(Date date) {
            return DateFormat.getDateTimeInstance(3, 3).format(date);
        }

        private void drawRestore(PoseStack poseStack, int n, int n2, int n3, int n4) {
            boolean bl = n3 >= n && n3 <= n + 12 && n4 >= n2 && n4 <= n2 + 14 && n4 < RealmsBackupScreen.this.height - 15 && n4 > 32;
            RealmsBackupScreen.this.minecraft.getTextureManager().bind(RESTORE_ICON_LOCATION);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.pushMatrix();
            RenderSystem.scalef(0.5f, 0.5f, 0.5f);
            float f = bl ? 28.0f : 0.0f;
            GuiComponent.blit(poseStack, n * 2, n2 * 2, 0.0f, f, 23, 28, 23, 56);
            RenderSystem.popMatrix();
            if (bl) {
                RealmsBackupScreen.this.toolTip = RESTORE_TOOLTIP;
            }
        }

        private void drawInfo(PoseStack poseStack, int n, int n2, int n3, int n4) {
            boolean bl = n3 >= n && n3 <= n + 8 && n4 >= n2 && n4 <= n2 + 8 && n4 < RealmsBackupScreen.this.height - 15 && n4 > 32;
            RealmsBackupScreen.this.minecraft.getTextureManager().bind(PLUS_ICON_LOCATION);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.pushMatrix();
            RenderSystem.scalef(0.5f, 0.5f, 0.5f);
            float f = bl ? 15.0f : 0.0f;
            GuiComponent.blit(poseStack, n * 2, n2 * 2, 0.0f, f, 15, 15, 15, 30);
            RenderSystem.popMatrix();
            if (bl) {
                RealmsBackupScreen.this.toolTip = HAS_CHANGES_TOOLTIP;
            }
        }
    }

    class BackupObjectSelectionList
    extends RealmsObjectSelectionList<Entry> {
        public BackupObjectSelectionList() {
            super(RealmsBackupScreen.this.width - 150, RealmsBackupScreen.this.height, 32, RealmsBackupScreen.this.height - 15, 36);
        }

        public void addEntry(Backup backup) {
            this.addEntry(new Entry(backup));
        }

        @Override
        public int getRowWidth() {
            return (int)((double)this.width * 0.93);
        }

        @Override
        public boolean isFocused() {
            return RealmsBackupScreen.this.getFocused() == this;
        }

        @Override
        public int getMaxPosition() {
            return this.getItemCount() * 36;
        }

        @Override
        public void renderBackground(PoseStack poseStack) {
            RealmsBackupScreen.this.renderBackground(poseStack);
        }

        @Override
        public boolean mouseClicked(double d, double d2, int n) {
            if (n != 0) {
                return false;
            }
            if (d < (double)this.getScrollbarPosition() && d2 >= (double)this.y0 && d2 <= (double)this.y1) {
                int n2 = this.width / 2 - 92;
                int n3 = this.width;
                int n4 = (int)Math.floor(d2 - (double)this.y0) - this.headerHeight + (int)this.getScrollAmount();
                int n5 = n4 / this.itemHeight;
                if (d >= (double)n2 && d <= (double)n3 && n5 >= 0 && n4 >= 0 && n5 < this.getItemCount()) {
                    this.selectItem(n5);
                    this.itemClicked(n4, n5, d, d2, this.width);
                }
                return true;
            }
            return false;
        }

        @Override
        public int getScrollbarPosition() {
            return this.width - 5;
        }

        @Override
        public void itemClicked(int n, int n2, double d, double d2, int n3) {
            int n4 = this.width - 35;
            int n5 = n2 * this.itemHeight + 36 - (int)this.getScrollAmount();
            int n6 = n4 + 10;
            int n7 = n5 - 3;
            if (d >= (double)n4 && d <= (double)(n4 + 9) && d2 >= (double)n5 && d2 <= (double)(n5 + 9)) {
                if (!((Backup)RealmsBackupScreen.access$300((RealmsBackupScreen)RealmsBackupScreen.this).get((int)n2)).changeList.isEmpty()) {
                    RealmsBackupScreen.this.selectedBackup = -1;
                    lastScrollPosition = (int)this.getScrollAmount();
                    this.minecraft.setScreen(new RealmsBackupInfoScreen(RealmsBackupScreen.this, (Backup)RealmsBackupScreen.this.backups.get(n2)));
                }
            } else if (d >= (double)n6 && d < (double)(n6 + 13) && d2 >= (double)n7 && d2 < (double)(n7 + 15)) {
                lastScrollPosition = (int)this.getScrollAmount();
                RealmsBackupScreen.this.restoreClicked(n2);
            }
        }

        @Override
        public void selectItem(int n) {
            this.setSelectedItem(n);
            if (n != -1) {
                NarrationHelper.now(I18n.get("narrator.select", ((Backup)RealmsBackupScreen.access$300((RealmsBackupScreen)RealmsBackupScreen.this).get((int)n)).lastModifiedDate.toString()));
            }
            this.selectInviteListItem(n);
        }

        public void selectInviteListItem(int n) {
            RealmsBackupScreen.this.selectedBackup = n;
            RealmsBackupScreen.this.updateButtonStates();
        }

        @Override
        public void setSelected(@Nullable Entry entry) {
            super.setSelected(entry);
            RealmsBackupScreen.this.selectedBackup = this.children().indexOf(entry);
            RealmsBackupScreen.this.updateButtonStates();
        }
    }

}

