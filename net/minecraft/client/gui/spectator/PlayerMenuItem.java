/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture$Type
 */
package net.minecraft.client.gui.spectator;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.gui.spectator.SpectatorMenuItem;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundTeleportToEntityPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class PlayerMenuItem
implements SpectatorMenuItem {
    private final GameProfile profile;
    private final ResourceLocation location;
    private final TextComponent name;

    public PlayerMenuItem(GameProfile gameProfile) {
        this.profile = gameProfile;
        Minecraft minecraft = Minecraft.getInstance();
        Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = minecraft.getSkinManager().getInsecureSkinInformation(gameProfile);
        this.location = map.containsKey((Object)MinecraftProfileTexture.Type.SKIN) ? minecraft.getSkinManager().registerTexture(map.get((Object)MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN) : DefaultPlayerSkin.getDefaultSkin(Player.createPlayerUUID(gameProfile));
        this.name = new TextComponent(gameProfile.getName());
    }

    @Override
    public void selectItem(SpectatorMenu spectatorMenu) {
        Minecraft.getInstance().getConnection().send(new ServerboundTeleportToEntityPacket(this.profile.getId()));
    }

    @Override
    public Component getName() {
        return this.name;
    }

    @Override
    public void renderIcon(PoseStack poseStack, float f, int n) {
        Minecraft.getInstance().getTextureManager().bind(this.location);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, (float)n / 255.0f);
        GuiComponent.blit(poseStack, 2, 2, 12, 12, 8.0f, 8.0f, 8, 8, 64, 64);
        GuiComponent.blit(poseStack, 2, 2, 12, 12, 40.0f, 8.0f, 8, 8, 64, 64);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

