/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ComparisonChain
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Ordering
 *  com.mojang.authlib.GameProfile
 */
package net.minecraft.client.gui.spectator.categories;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.spectator.SpectatorGui;
import net.minecraft.client.gui.spectator.PlayerMenuItem;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.gui.spectator.SpectatorMenuCategory;
import net.minecraft.client.gui.spectator.SpectatorMenuItem;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameType;

public class TeleportToPlayerMenuCategory
implements SpectatorMenuCategory,
SpectatorMenuItem {
    private static final Ordering<PlayerInfo> PROFILE_ORDER = Ordering.from((playerInfo, playerInfo2) -> ComparisonChain.start().compare((Comparable)playerInfo.getProfile().getId(), (Comparable)playerInfo2.getProfile().getId()).result());
    private static final Component TELEPORT_TEXT = new TranslatableComponent("spectatorMenu.teleport");
    private static final Component TELEPORT_PROMPT = new TranslatableComponent("spectatorMenu.teleport.prompt");
    private final List<SpectatorMenuItem> items = Lists.newArrayList();

    public TeleportToPlayerMenuCategory() {
        this(PROFILE_ORDER.sortedCopy(Minecraft.getInstance().getConnection().getOnlinePlayers()));
    }

    public TeleportToPlayerMenuCategory(Collection<PlayerInfo> collection) {
        for (PlayerInfo playerInfo : PROFILE_ORDER.sortedCopy(collection)) {
            if (playerInfo.getGameMode() == GameType.SPECTATOR) continue;
            this.items.add(new PlayerMenuItem(playerInfo.getProfile()));
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
        GuiComponent.blit(poseStack, 0, 0, 0.0f, 0.0f, 16, 16, 256, 256);
    }

    @Override
    public boolean isEnabled() {
        return !this.items.isEmpty();
    }
}

