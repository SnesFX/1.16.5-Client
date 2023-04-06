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
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.Ops;
import com.mojang.realmsclient.dto.PlayerInfo;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsConfirmScreen;
import com.mojang.realmsclient.gui.screens.RealmsInviteScreen;
import com.mojang.realmsclient.util.RealmsTextureManager;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.List;
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
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.NarrationHelper;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsPlayerScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation OP_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/op_icon.png");
    private static final ResourceLocation USER_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/user_icon.png");
    private static final ResourceLocation CROSS_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/cross_player_icon.png");
    private static final ResourceLocation OPTIONS_BACKGROUND = new ResourceLocation("minecraft", "textures/gui/options_background.png");
    private static final Component NORMAL_USER_TOOLTIP = new TranslatableComponent("mco.configure.world.invites.normal.tooltip");
    private static final Component OP_TOOLTIP = new TranslatableComponent("mco.configure.world.invites.ops.tooltip");
    private static final Component REMOVE_ENTRY_TOOLTIP = new TranslatableComponent("mco.configure.world.invites.remove.tooltip");
    private static final Component INVITED_LABEL = new TranslatableComponent("mco.configure.world.invited");
    private Component toolTip;
    private final RealmsConfigureWorldScreen lastScreen;
    private final RealmsServer serverData;
    private InvitedObjectSelectionList invitedObjectSelectionList;
    private int column1X;
    private int columnWidth;
    private int column2X;
    private Button removeButton;
    private Button opdeopButton;
    private int selectedInvitedIndex = -1;
    private String selectedInvited;
    private int player = -1;
    private boolean stateChanged;
    private RealmsLabel titleLabel;
    private UserAction hoveredUserAction = UserAction.NONE;

    public RealmsPlayerScreen(RealmsConfigureWorldScreen realmsConfigureWorldScreen, RealmsServer realmsServer) {
        this.lastScreen = realmsConfigureWorldScreen;
        this.serverData = realmsServer;
    }

    @Override
    public void init() {
        this.column1X = this.width / 2 - 160;
        this.columnWidth = 150;
        this.column2X = this.width / 2 + 12;
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.invitedObjectSelectionList = new InvitedObjectSelectionList();
        this.invitedObjectSelectionList.setLeftPos(this.column1X);
        this.addWidget(this.invitedObjectSelectionList);
        for (PlayerInfo playerInfo : this.serverData.players) {
            this.invitedObjectSelectionList.addEntry(playerInfo);
        }
        this.addButton(new Button(this.column2X, RealmsPlayerScreen.row(1), this.columnWidth + 10, 20, new TranslatableComponent("mco.configure.world.buttons.invite"), button -> this.minecraft.setScreen(new RealmsInviteScreen(this.lastScreen, this, this.serverData))));
        this.removeButton = this.addButton(new Button(this.column2X, RealmsPlayerScreen.row(7), this.columnWidth + 10, 20, new TranslatableComponent("mco.configure.world.invites.remove.tooltip"), button -> this.uninvite(this.player)));
        this.opdeopButton = this.addButton(new Button(this.column2X, RealmsPlayerScreen.row(9), this.columnWidth + 10, 20, new TranslatableComponent("mco.configure.world.invites.ops.tooltip"), button -> {
            if (this.serverData.players.get(this.player).isOperator()) {
                this.deop(this.player);
            } else {
                this.op(this.player);
            }
        }));
        this.addButton(new Button(this.column2X + this.columnWidth / 2 + 2, RealmsPlayerScreen.row(12), this.columnWidth / 2 + 10 - 2, 20, CommonComponents.GUI_BACK, button -> this.backButtonClicked()));
        this.titleLabel = this.addWidget(new RealmsLabel(new TranslatableComponent("mco.configure.world.players.title"), this.width / 2, 17, 16777215));
        this.narrateLabels();
        this.updateButtonStates();
    }

    private void updateButtonStates() {
        this.removeButton.visible = this.shouldRemoveAndOpdeopButtonBeVisible(this.player);
        this.opdeopButton.visible = this.shouldRemoveAndOpdeopButtonBeVisible(this.player);
    }

    private boolean shouldRemoveAndOpdeopButtonBeVisible(int n) {
        return n != -1;
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
            this.minecraft.setScreen(this.lastScreen.getNewScreen());
        } else {
            this.minecraft.setScreen(this.lastScreen);
        }
    }

    private void op(int n) {
        this.updateButtonStates();
        RealmsClient realmsClient = RealmsClient.create();
        String string = this.serverData.players.get(n).getUuid();
        try {
            this.updateOps(realmsClient.op(this.serverData.id, string));
        }
        catch (RealmsServiceException realmsServiceException) {
            LOGGER.error("Couldn't op the user");
        }
    }

    private void deop(int n) {
        this.updateButtonStates();
        RealmsClient realmsClient = RealmsClient.create();
        String string = this.serverData.players.get(n).getUuid();
        try {
            this.updateOps(realmsClient.deop(this.serverData.id, string));
        }
        catch (RealmsServiceException realmsServiceException) {
            LOGGER.error("Couldn't deop the user");
        }
    }

    private void updateOps(Ops ops) {
        for (PlayerInfo playerInfo : this.serverData.players) {
            playerInfo.setOperator(ops.ops.contains(playerInfo.getName()));
        }
    }

    private void uninvite(int n) {
        this.updateButtonStates();
        if (n >= 0 && n < this.serverData.players.size()) {
            PlayerInfo playerInfo = this.serverData.players.get(n);
            this.selectedInvited = playerInfo.getUuid();
            this.selectedInvitedIndex = n;
            RealmsConfirmScreen realmsConfirmScreen = new RealmsConfirmScreen(bl -> {
                if (bl) {
                    RealmsClient realmsClient = RealmsClient.create();
                    try {
                        realmsClient.uninvite(this.serverData.id, this.selectedInvited);
                    }
                    catch (RealmsServiceException realmsServiceException) {
                        LOGGER.error("Couldn't uninvite user");
                    }
                    this.deleteFromInvitedList(this.selectedInvitedIndex);
                    this.player = -1;
                    this.updateButtonStates();
                }
                this.stateChanged = true;
                this.minecraft.setScreen(this);
            }, new TextComponent("Question"), new TranslatableComponent("mco.configure.world.uninvite.question").append(" '").append(playerInfo.getName()).append("' ?"));
            this.minecraft.setScreen(realmsConfirmScreen);
        }
    }

    private void deleteFromInvitedList(int n) {
        this.serverData.players.remove(n);
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.toolTip = null;
        this.hoveredUserAction = UserAction.NONE;
        this.renderBackground(poseStack);
        if (this.invitedObjectSelectionList != null) {
            this.invitedObjectSelectionList.render(poseStack, n, n2, f);
        }
        int n3 = RealmsPlayerScreen.row(12) + 20;
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        this.minecraft.getTextureManager().bind(OPTIONS_BACKGROUND);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        float f2 = 32.0f;
        bufferBuilder.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferBuilder.vertex(0.0, this.height, 0.0).uv(0.0f, (float)(this.height - n3) / 32.0f + 0.0f).color(64, 64, 64, 255).endVertex();
        bufferBuilder.vertex(this.width, this.height, 0.0).uv((float)this.width / 32.0f, (float)(this.height - n3) / 32.0f + 0.0f).color(64, 64, 64, 255).endVertex();
        bufferBuilder.vertex(this.width, n3, 0.0).uv((float)this.width / 32.0f, 0.0f).color(64, 64, 64, 255).endVertex();
        bufferBuilder.vertex(0.0, n3, 0.0).uv(0.0f, 0.0f).color(64, 64, 64, 255).endVertex();
        tesselator.end();
        this.titleLabel.render(this, poseStack);
        if (this.serverData != null && this.serverData.players != null) {
            this.font.draw(poseStack, new TextComponent("").append(INVITED_LABEL).append(" (").append(Integer.toString(this.serverData.players.size())).append(")"), (float)this.column1X, (float)RealmsPlayerScreen.row(0), 10526880);
        } else {
            this.font.draw(poseStack, INVITED_LABEL, (float)this.column1X, (float)RealmsPlayerScreen.row(0), 10526880);
        }
        super.render(poseStack, n, n2, f);
        if (this.serverData == null) {
            return;
        }
        this.renderMousehoverTooltip(poseStack, this.toolTip, n, n2);
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

    private void drawRemoveIcon(PoseStack poseStack, int n, int n2, int n3, int n4) {
        boolean bl = n3 >= n && n3 <= n + 9 && n4 >= n2 && n4 <= n2 + 9 && n4 < RealmsPlayerScreen.row(12) + 20 && n4 > RealmsPlayerScreen.row(1);
        this.minecraft.getTextureManager().bind(CROSS_ICON_LOCATION);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        float f = bl ? 7.0f : 0.0f;
        GuiComponent.blit(poseStack, n, n2, 0.0f, f, 8, 7, 8, 14);
        if (bl) {
            this.toolTip = REMOVE_ENTRY_TOOLTIP;
            this.hoveredUserAction = UserAction.REMOVE;
        }
    }

    private void drawOpped(PoseStack poseStack, int n, int n2, int n3, int n4) {
        boolean bl = n3 >= n && n3 <= n + 9 && n4 >= n2 && n4 <= n2 + 9 && n4 < RealmsPlayerScreen.row(12) + 20 && n4 > RealmsPlayerScreen.row(1);
        this.minecraft.getTextureManager().bind(OP_ICON_LOCATION);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        float f = bl ? 8.0f : 0.0f;
        GuiComponent.blit(poseStack, n, n2, 0.0f, f, 8, 8, 8, 16);
        if (bl) {
            this.toolTip = OP_TOOLTIP;
            this.hoveredUserAction = UserAction.TOGGLE_OP;
        }
    }

    private void drawNormal(PoseStack poseStack, int n, int n2, int n3, int n4) {
        boolean bl = n3 >= n && n3 <= n + 9 && n4 >= n2 && n4 <= n2 + 9 && n4 < RealmsPlayerScreen.row(12) + 20 && n4 > RealmsPlayerScreen.row(1);
        this.minecraft.getTextureManager().bind(USER_ICON_LOCATION);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        float f = bl ? 8.0f : 0.0f;
        GuiComponent.blit(poseStack, n, n2, 0.0f, f, 8, 8, 8, 16);
        if (bl) {
            this.toolTip = NORMAL_USER_TOOLTIP;
            this.hoveredUserAction = UserAction.TOGGLE_OP;
        }
    }

    static /* synthetic */ RealmsServer access$500(RealmsPlayerScreen realmsPlayerScreen) {
        return realmsPlayerScreen.serverData;
    }

    class Entry
    extends ObjectSelectionList.Entry<Entry> {
        private final PlayerInfo playerInfo;

        public Entry(PlayerInfo playerInfo) {
            this.playerInfo = playerInfo;
        }

        @Override
        public void render(PoseStack poseStack, int n, int n2, int n3, int n4, int n5, int n6, int n7, boolean bl, float f) {
            this.renderInvitedItem(poseStack, this.playerInfo, n3, n2, n6, n7);
        }

        private void renderInvitedItem(PoseStack poseStack, PlayerInfo playerInfo, int n, int n2, int n3, int n4) {
            int n5 = !playerInfo.getAccepted() ? 10526880 : (playerInfo.getOnline() ? 8388479 : 16777215);
            RealmsPlayerScreen.this.font.draw(poseStack, playerInfo.getName(), (float)(RealmsPlayerScreen.this.column1X + 3 + 12), (float)(n2 + 1), n5);
            if (playerInfo.isOperator()) {
                RealmsPlayerScreen.this.drawOpped(poseStack, RealmsPlayerScreen.this.column1X + RealmsPlayerScreen.this.columnWidth - 10, n2 + 1, n3, n4);
            } else {
                RealmsPlayerScreen.this.drawNormal(poseStack, RealmsPlayerScreen.this.column1X + RealmsPlayerScreen.this.columnWidth - 10, n2 + 1, n3, n4);
            }
            RealmsPlayerScreen.this.drawRemoveIcon(poseStack, RealmsPlayerScreen.this.column1X + RealmsPlayerScreen.this.columnWidth - 22, n2 + 2, n3, n4);
            RealmsTextureManager.withBoundFace(playerInfo.getUuid(), () -> {
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                GuiComponent.blit(poseStack, RealmsPlayerScreen.this.column1X + 2 + 2, n2 + 1, 8, 8, 8.0f, 8.0f, 8, 8, 64, 64);
                GuiComponent.blit(poseStack, RealmsPlayerScreen.this.column1X + 2 + 2, n2 + 1, 8, 8, 40.0f, 8.0f, 8, 8, 64, 64);
            });
        }
    }

    class InvitedObjectSelectionList
    extends RealmsObjectSelectionList<Entry> {
        public InvitedObjectSelectionList() {
            super(RealmsPlayerScreen.this.columnWidth + 10, RealmsPlayerScreen.row(12) + 20, RealmsPlayerScreen.row(1), RealmsPlayerScreen.row(12) + 20, 13);
        }

        public void addEntry(PlayerInfo playerInfo) {
            this.addEntry(new Entry(playerInfo));
        }

        @Override
        public int getRowWidth() {
            return (int)((double)this.width * 1.0);
        }

        @Override
        public boolean isFocused() {
            return RealmsPlayerScreen.this.getFocused() == this;
        }

        @Override
        public boolean mouseClicked(double d, double d2, int n) {
            if (n == 0 && d < (double)this.getScrollbarPosition() && d2 >= (double)this.y0 && d2 <= (double)this.y1) {
                int n2 = RealmsPlayerScreen.this.column1X;
                int n3 = RealmsPlayerScreen.this.column1X + RealmsPlayerScreen.this.columnWidth;
                int n4 = (int)Math.floor(d2 - (double)this.y0) - this.headerHeight + (int)this.getScrollAmount() - 4;
                int n5 = n4 / this.itemHeight;
                if (d >= (double)n2 && d <= (double)n3 && n5 >= 0 && n4 >= 0 && n5 < this.getItemCount()) {
                    this.selectItem(n5);
                    this.itemClicked(n4, n5, d, d2, this.width);
                }
                return true;
            }
            return super.mouseClicked(d, d2, n);
        }

        @Override
        public void itemClicked(int n, int n2, double d, double d2, int n3) {
            if (n2 < 0 || n2 > RealmsPlayerScreen.access$500((RealmsPlayerScreen)RealmsPlayerScreen.this).players.size() || RealmsPlayerScreen.this.hoveredUserAction == UserAction.NONE) {
                return;
            }
            if (RealmsPlayerScreen.this.hoveredUserAction == UserAction.TOGGLE_OP) {
                if (RealmsPlayerScreen.access$500((RealmsPlayerScreen)RealmsPlayerScreen.this).players.get(n2).isOperator()) {
                    RealmsPlayerScreen.this.deop(n2);
                } else {
                    RealmsPlayerScreen.this.op(n2);
                }
            } else if (RealmsPlayerScreen.this.hoveredUserAction == UserAction.REMOVE) {
                RealmsPlayerScreen.this.uninvite(n2);
            }
        }

        @Override
        public void selectItem(int n) {
            this.setSelectedItem(n);
            if (n != -1) {
                NarrationHelper.now(I18n.get("narrator.select", RealmsPlayerScreen.access$500((RealmsPlayerScreen)RealmsPlayerScreen.this).players.get(n).getName()));
            }
            this.selectInviteListItem(n);
        }

        public void selectInviteListItem(int n) {
            RealmsPlayerScreen.this.player = n;
            RealmsPlayerScreen.this.updateButtonStates();
        }

        @Override
        public void setSelected(@Nullable Entry entry) {
            super.setSelected(entry);
            RealmsPlayerScreen.this.player = this.children().indexOf(entry);
            RealmsPlayerScreen.this.updateButtonStates();
        }

        @Override
        public void renderBackground(PoseStack poseStack) {
            RealmsPlayerScreen.this.renderBackground(poseStack);
        }

        @Override
        public int getScrollbarPosition() {
            return RealmsPlayerScreen.this.column1X + this.width - 5;
        }

        @Override
        public int getMaxPosition() {
            return this.getItemCount() * 13;
        }
    }

    static enum UserAction {
        TOGGLE_OP,
        REMOVE,
        NONE;
        
    }

}

