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
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.PendingInvite;
import com.mojang.realmsclient.dto.PendingInvitesList;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RowButton;
import com.mojang.realmsclient.util.RealmsTextureManager;
import com.mojang.realmsclient.util.RealmsUtil;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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

public class RealmsPendingInvitesScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation ACCEPT_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/accept_icon.png");
    private static final ResourceLocation REJECT_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/reject_icon.png");
    private static final Component NO_PENDING_INVITES_TEXT = new TranslatableComponent("mco.invites.nopending");
    private static final Component ACCEPT_INVITE_TOOLTIP = new TranslatableComponent("mco.invites.button.accept");
    private static final Component REJECT_INVITE_TOOLTIP = new TranslatableComponent("mco.invites.button.reject");
    private final Screen lastScreen;
    @Nullable
    private Component toolTip;
    private boolean loaded;
    private PendingInvitationSelectionList pendingInvitationSelectionList;
    private RealmsLabel titleLabel;
    private int selectedInvite = -1;
    private Button acceptButton;
    private Button rejectButton;

    public RealmsPendingInvitesScreen(Screen screen) {
        this.lastScreen = screen;
    }

    @Override
    public void init() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.pendingInvitationSelectionList = new PendingInvitationSelectionList();
        new Thread("Realms-pending-invitations-fetcher"){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void run() {
                RealmsClient realmsClient = RealmsClient.create();
                try {
                    List<PendingInvite> list = realmsClient.pendingInvites().pendingInvites;
                    List list2 = list.stream().map(pendingInvite -> new Entry((PendingInvite)pendingInvite)).collect(Collectors.toList());
                    RealmsPendingInvitesScreen.this.minecraft.execute(() -> RealmsPendingInvitesScreen.this.pendingInvitationSelectionList.replaceEntries(list2));
                }
                catch (RealmsServiceException realmsServiceException) {
                    LOGGER.error("Couldn't list invites");
                }
                finally {
                    RealmsPendingInvitesScreen.this.loaded = true;
                }
            }
        }.start();
        this.addWidget(this.pendingInvitationSelectionList);
        this.acceptButton = this.addButton(new Button(this.width / 2 - 174, this.height - 32, 100, 20, new TranslatableComponent("mco.invites.button.accept"), button -> {
            this.accept(this.selectedInvite);
            this.selectedInvite = -1;
            this.updateButtonStates();
        }));
        this.addButton(new Button(this.width / 2 - 50, this.height - 32, 100, 20, CommonComponents.GUI_DONE, button -> this.minecraft.setScreen(new RealmsMainScreen(this.lastScreen))));
        this.rejectButton = this.addButton(new Button(this.width / 2 + 74, this.height - 32, 100, 20, new TranslatableComponent("mco.invites.button.reject"), button -> {
            this.reject(this.selectedInvite);
            this.selectedInvite = -1;
            this.updateButtonStates();
        }));
        this.titleLabel = new RealmsLabel(new TranslatableComponent("mco.invites.title"), this.width / 2, 12, 16777215);
        this.addWidget(this.titleLabel);
        this.narrateLabels();
        this.updateButtonStates();
    }

    @Override
    public boolean keyPressed(int n, int n2, int n3) {
        if (n == 256) {
            this.minecraft.setScreen(new RealmsMainScreen(this.lastScreen));
            return true;
        }
        return super.keyPressed(n, n2, n3);
    }

    private void updateList(int n) {
        this.pendingInvitationSelectionList.removeAtIndex(n);
    }

    private void reject(final int n) {
        if (n < this.pendingInvitationSelectionList.getItemCount()) {
            new Thread("Realms-reject-invitation"){

                @Override
                public void run() {
                    try {
                        RealmsClient realmsClient = RealmsClient.create();
                        realmsClient.rejectInvitation(Entry.access$400((Entry)((Entry)RealmsPendingInvitesScreen.access$300((RealmsPendingInvitesScreen)RealmsPendingInvitesScreen.this).children().get((int)n))).invitationId);
                        RealmsPendingInvitesScreen.this.minecraft.execute(() -> RealmsPendingInvitesScreen.this.updateList(n));
                    }
                    catch (RealmsServiceException realmsServiceException) {
                        LOGGER.error("Couldn't reject invite");
                    }
                }
            }.start();
        }
    }

    private void accept(final int n) {
        if (n < this.pendingInvitationSelectionList.getItemCount()) {
            new Thread("Realms-accept-invitation"){

                @Override
                public void run() {
                    try {
                        RealmsClient realmsClient = RealmsClient.create();
                        realmsClient.acceptInvitation(Entry.access$400((Entry)((Entry)RealmsPendingInvitesScreen.access$300((RealmsPendingInvitesScreen)RealmsPendingInvitesScreen.this).children().get((int)n))).invitationId);
                        RealmsPendingInvitesScreen.this.minecraft.execute(() -> RealmsPendingInvitesScreen.this.updateList(n));
                    }
                    catch (RealmsServiceException realmsServiceException) {
                        LOGGER.error("Couldn't accept invite");
                    }
                }
            }.start();
        }
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.toolTip = null;
        this.renderBackground(poseStack);
        this.pendingInvitationSelectionList.render(poseStack, n, n2, f);
        this.titleLabel.render(this, poseStack);
        if (this.toolTip != null) {
            this.renderMousehoverTooltip(poseStack, this.toolTip, n, n2);
        }
        if (this.pendingInvitationSelectionList.getItemCount() == 0 && this.loaded) {
            RealmsPendingInvitesScreen.drawCenteredString(poseStack, this.font, NO_PENDING_INVITES_TEXT, this.width / 2, this.height / 2 - 20, 16777215);
        }
        super.render(poseStack, n, n2, f);
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

    private void updateButtonStates() {
        this.acceptButton.visible = this.shouldAcceptAndRejectButtonBeVisible(this.selectedInvite);
        this.rejectButton.visible = this.shouldAcceptAndRejectButtonBeVisible(this.selectedInvite);
    }

    private boolean shouldAcceptAndRejectButtonBeVisible(int n) {
        return n != -1;
    }

    class Entry
    extends ObjectSelectionList.Entry<Entry> {
        private final PendingInvite pendingInvite;
        private final List<RowButton> rowButtons;

        Entry(PendingInvite pendingInvite) {
            this.pendingInvite = pendingInvite;
            this.rowButtons = Arrays.asList(new AcceptRowButton(), new RejectRowButton());
        }

        @Override
        public void render(PoseStack poseStack, int n, int n2, int n3, int n4, int n5, int n6, int n7, boolean bl, float f) {
            this.renderPendingInvitationItem(poseStack, this.pendingInvite, n3, n2, n6, n7);
        }

        @Override
        public boolean mouseClicked(double d, double d2, int n) {
            RowButton.rowButtonMouseClicked(RealmsPendingInvitesScreen.this.pendingInvitationSelectionList, this, this.rowButtons, n, d, d2);
            return true;
        }

        private void renderPendingInvitationItem(PoseStack poseStack, PendingInvite pendingInvite, int n, int n2, int n3, int n4) {
            RealmsPendingInvitesScreen.this.font.draw(poseStack, pendingInvite.worldName, (float)(n + 38), (float)(n2 + 1), 16777215);
            RealmsPendingInvitesScreen.this.font.draw(poseStack, pendingInvite.worldOwnerName, (float)(n + 38), (float)(n2 + 12), 7105644);
            RealmsPendingInvitesScreen.this.font.draw(poseStack, RealmsUtil.convertToAgePresentationFromInstant(pendingInvite.date), (float)(n + 38), (float)(n2 + 24), 7105644);
            RowButton.drawButtonsInRow(poseStack, this.rowButtons, RealmsPendingInvitesScreen.this.pendingInvitationSelectionList, n, n2, n3, n4);
            RealmsTextureManager.withBoundFace(pendingInvite.worldOwnerUuid, () -> {
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                GuiComponent.blit(poseStack, n, n2, 32, 32, 8.0f, 8.0f, 8, 8, 64, 64);
                GuiComponent.blit(poseStack, n, n2, 32, 32, 40.0f, 8.0f, 8, 8, 64, 64);
            });
        }

        class RejectRowButton
        extends RowButton {
            RejectRowButton() {
                super(15, 15, 235, 5);
            }

            @Override
            protected void draw(PoseStack poseStack, int n, int n2, boolean bl) {
                RealmsPendingInvitesScreen.this.minecraft.getTextureManager().bind(REJECT_ICON_LOCATION);
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                float f = bl ? 19.0f : 0.0f;
                GuiComponent.blit(poseStack, n, n2, f, 0.0f, 18, 18, 37, 18);
                if (bl) {
                    RealmsPendingInvitesScreen.this.toolTip = REJECT_INVITE_TOOLTIP;
                }
            }

            @Override
            public void onClick(int n) {
                RealmsPendingInvitesScreen.this.reject(n);
            }
        }

        class AcceptRowButton
        extends RowButton {
            AcceptRowButton() {
                super(15, 15, 215, 5);
            }

            @Override
            protected void draw(PoseStack poseStack, int n, int n2, boolean bl) {
                RealmsPendingInvitesScreen.this.minecraft.getTextureManager().bind(ACCEPT_ICON_LOCATION);
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                float f = bl ? 19.0f : 0.0f;
                GuiComponent.blit(poseStack, n, n2, f, 0.0f, 18, 18, 37, 18);
                if (bl) {
                    RealmsPendingInvitesScreen.this.toolTip = ACCEPT_INVITE_TOOLTIP;
                }
            }

            @Override
            public void onClick(int n) {
                RealmsPendingInvitesScreen.this.accept(n);
            }
        }

    }

    class PendingInvitationSelectionList
    extends RealmsObjectSelectionList<Entry> {
        public PendingInvitationSelectionList() {
            super(RealmsPendingInvitesScreen.this.width, RealmsPendingInvitesScreen.this.height, 32, RealmsPendingInvitesScreen.this.height - 40, 36);
        }

        public void removeAtIndex(int n) {
            this.remove(n);
        }

        @Override
        public int getMaxPosition() {
            return this.getItemCount() * 36;
        }

        @Override
        public int getRowWidth() {
            return 260;
        }

        @Override
        public boolean isFocused() {
            return RealmsPendingInvitesScreen.this.getFocused() == this;
        }

        @Override
        public void renderBackground(PoseStack poseStack) {
            RealmsPendingInvitesScreen.this.renderBackground(poseStack);
        }

        @Override
        public void selectItem(int n) {
            this.setSelectedItem(n);
            if (n != -1) {
                List list = RealmsPendingInvitesScreen.this.pendingInvitationSelectionList.children();
                PendingInvite pendingInvite = ((Entry)list.get(n)).pendingInvite;
                String string = I18n.get("narrator.select.list.position", n + 1, list.size());
                String string2 = NarrationHelper.join(Arrays.asList(pendingInvite.worldName, pendingInvite.worldOwnerName, RealmsUtil.convertToAgePresentationFromInstant(pendingInvite.date), string));
                NarrationHelper.now(I18n.get("narrator.select", string2));
            }
            this.selectInviteListItem(n);
        }

        public void selectInviteListItem(int n) {
            RealmsPendingInvitesScreen.this.selectedInvite = n;
            RealmsPendingInvitesScreen.this.updateButtonStates();
        }

        @Override
        public void setSelected(@Nullable Entry entry) {
            super.setSelected(entry);
            RealmsPendingInvitesScreen.this.selectedInvite = this.children().indexOf(entry);
            RealmsPendingInvitesScreen.this.updateButtonStates();
        }
    }

}

