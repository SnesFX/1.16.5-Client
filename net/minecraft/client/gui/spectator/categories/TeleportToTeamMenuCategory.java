/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.authlib.GameProfile
 */
package net.minecraft.client.gui.spectator.categories;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.spectator.SpectatorGui;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.gui.spectator.SpectatorMenuCategory;
import net.minecraft.client.gui.spectator.SpectatorMenuItem;
import net.minecraft.client.gui.spectator.categories.TeleportToPlayerMenuCategory;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

public class TeleportToTeamMenuCategory
implements SpectatorMenuCategory,
SpectatorMenuItem {
    private static final Component TELEPORT_TEXT = new TranslatableComponent("spectatorMenu.team_teleport");
    private static final Component TELEPORT_PROMPT = new TranslatableComponent("spectatorMenu.team_teleport.prompt");
    private final List<SpectatorMenuItem> items = Lists.newArrayList();

    public TeleportToTeamMenuCategory() {
        Minecraft minecraft = Minecraft.getInstance();
        for (PlayerTeam playerTeam : minecraft.level.getScoreboard().getPlayerTeams()) {
            this.items.add(new TeamSelectionItem(playerTeam));
        }
    }

    @Override
    public List<SpectatorMenuItem> getItems() {
        return this.items;
    }

    @Override
    public Component getPrompt() {
        return TELEPORT_PROMPT;
    }

    @Override
    public void selectItem(SpectatorMenu spectatorMenu) {
        spectatorMenu.selectCategory(this);
    }

    @Override
    public Component getName() {
        return TELEPORT_TEXT;
    }

    @Override
    public void renderIcon(PoseStack poseStack, float f, int n) {
        Minecraft.getInstance().getTextureManager().bind(SpectatorGui.SPECTATOR_LOCATION);
        GuiComponent.blit(poseStack, 0, 0, 16.0f, 0.0f, 16, 16, 256, 256);
    }

    @Override
    public boolean isEnabled() {
        for (SpectatorMenuItem spectatorMenuItem : this.items) {
            if (!spectatorMenuItem.isEnabled()) continue;
            return true;
        }
        return false;
    }

    class TeamSelectionItem
    implements SpectatorMenuItem {
        private final PlayerTeam team;
        private final ResourceLocation location;
        private final List<PlayerInfo> players;

        public TeamSelectionItem(PlayerTeam playerTeam) {
            this.team = playerTeam;
            this.players = Lists.newArrayList();
            for (String string : playerTeam.getPlayers()) {
                PlayerInfo playerInfo = Minecraft.getInstance().getConnection().getPlayerInfo(string);
                if (playerInfo == null) continue;
                this.players.add(playerInfo);
            }
            if (this.players.isEmpty()) {
                this.location = DefaultPlayerSkin.getDefaultSkin();
            } else {
                String string = this.players.get(new Random().nextInt(this.players.size())).getProfile().getName();
                this.location = AbstractClientPlayer.getSkinLocation(string);
                AbstractClientPlayer.registerSkinTexture(this.location, string);
            }
        }

        @Override
        public void selectItem(SpectatorMenu spectatorMenu) {
            spectatorMenu.selectCategory(new TeleportToPlayerMenuCategory(this.players));
        }

        @Override
        public Component getName() {
            return this.team.getDisplayName();
        }

        @Override
        public void renderIcon(PoseStack poseStack, float f, int n) {
            Integer n2 = this.team.getColor().getColor();
            if (n2 != null) {
                float f2 = (float)(n2 >> 16 & 0xFF) / 255.0f;
                float f3 = (float)(n2 >> 8 & 0xFF) / 255.0f;
                float f4 = (float)(n2 & 0xFF) / 255.0f;
                GuiComponent.fill(poseStack, 1, 1, 15, 15, Mth.color(f2 * f, f3 * f, f4 * f) | n << 24);
            }
            Minecraft.getInstance().getTextureManager().bind(this.location);
            RenderSystem.color4f(f, f, f, (float)n / 255.0f);
            GuiComponent.blit(poseStack, 2, 2, 12, 12, 8.0f, 8.0f, 8, 8, 64, 64);
            GuiComponent.blit(poseStack, 2, 2, 12, 12, 40.0f, 8.0f, 8, 8, 64, 64);
        }

        @Override
        public boolean isEnabled() {
            return !this.players.isEmpty();
        }
    }

}

