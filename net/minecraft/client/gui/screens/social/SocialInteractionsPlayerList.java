/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.common.collect.Lists
 *  com.mojang.authlib.GameProfile
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens.social;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.screens.social.PlayerEntry;
import net.minecraft.client.gui.screens.social.PlayerSocialManager;
import net.minecraft.client.gui.screens.social.SocialInteractionsScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;

public class SocialInteractionsPlayerList
extends ContainerObjectSelectionList<PlayerEntry> {
    private final SocialInteractionsScreen socialInteractionsScreen;
    private final Minecraft minecraft;
    private final List<PlayerEntry> players = Lists.newArrayList();
    @Nullable
    private String filter;

    public SocialInteractionsPlayerList(SocialInteractionsScreen socialInteractionsScreen, Minecraft minecraft, int n, int n2, int n3, int n4, int n5) {
        super(minecraft, n, n2, n3, n4, n5);
        this.socialInteractionsScreen = socialInteractionsScreen;
        this.minecraft = minecraft;
        this.setRenderBackground(false);
        this.setRenderTopAndBottom(false);
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        double d = this.minecraft.getWindow().getGuiScale();
        RenderSystem.enableScissor((int)((double)this.getRowLeft() * d), (int)((double)(this.height - this.y1) * d), (int)((double)(this.getScrollbarPosition() + 6) * d), (int)((double)(this.height - (this.height - this.y1) - this.y0 - 4) * d));
        super.render(poseStack, n, n2, f);
        RenderSystem.disableScissor();
    }

    public void updatePlayerList(Collection<UUID> collection, double d) {
        this.players.clear();
        for (UUID uUID : collection) {
            PlayerInfo playerInfo = this.minecraft.player.connection.getPlayerInfo(uUID);
            if (playerInfo == null) continue;
            this.players.add(new PlayerEntry(this.minecraft, this.socialInteractionsScreen, playerInfo.getProfile().getId(), playerInfo.getProfile().getName(), playerInfo::getSkinLocation));
        }
        this.updateFilteredPlayers();
        this.players.sort((playerEntry, playerEntry2) -> playerEntry.getPlayerName().compareToIgnoreCase(playerEntry2.getPlayerName()));
        this.replaceEntries(this.players);
        this.setScrollAmount(d);
    }

    private void updateFilteredPlayers() {
        if (this.filter != null) {
            this.players.removeIf(playerEntry -> !playerEntry.getPlayerName().toLowerCase(Locale.ROOT).contains(this.filter));
            this.replaceEntries(this.players);
        }
    }

    public void setFilter(String string) {
        this.filter = string;
    }

    public boolean isEmpty() {
        return this.players.isEmpty();
    }

    public void addPlayer(PlayerInfo playerInfo, SocialInteractionsScreen.Page page) {
        UUID uUID = playerInfo.getProfile().getId();
        for (PlayerEntry playerEntry : this.players) {
            if (!playerEntry.getPlayerId().equals(uUID)) continue;
            playerEntry.setRemoved(false);
            return;
        }
        if ((page == SocialInteractionsScreen.Page.ALL || this.minecraft.getPlayerSocialManager().shouldHideMessageFrom(uUID)) && (Strings.isNullOrEmpty((String)this.filter) || playerInfo.getProfile().getName().toLowerCase(Locale.ROOT).contains(this.filter))) {
            PlayerEntry playerEntry = new PlayerEntry(this.minecraft, this.socialInteractionsScreen, playerInfo.getProfile().getId(), playerInfo.getProfile().getName(), playerInfo::getSkinLocation);
            this.addEntry(playerEntry);
            this.players.add(playerEntry);
        }
    }

    public void removePlayer(UUID uUID) {
        for (PlayerEntry playerEntry : this.players) {
            if (!playerEntry.getPlayerId().equals(uUID)) continue;
            playerEntry.setRemoved(true);
            return;
        }
    }
}

