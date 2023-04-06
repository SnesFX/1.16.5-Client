/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.hash.HashCode
 *  com.google.common.hash.Hashing
 *  com.mojang.authlib.GameProfile
 *  javax.annotation.Nullable
 */
package net.minecraft.client.player;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import java.io.File;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.HttpTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;

public abstract class AbstractClientPlayer
extends Player {
    private PlayerInfo playerInfo;
    public float elytraRotX;
    public float elytraRotY;
    public float elytraRotZ;
    public final ClientLevel clientLevel;

    public AbstractClientPlayer(ClientLevel clientLevel, GameProfile gameProfile) {
        super(clientLevel, clientLevel.getSharedSpawnPos(), clientLevel.getSharedSpawnAngle(), gameProfile);
        this.clientLevel = clientLevel;
    }

    @Override
    public boolean isSpectator() {
        PlayerInfo playerInfo = Minecraft.getInstance().getConnection().getPlayerInfo(this.getGameProfile().getId());
        return playerInfo != null && playerInfo.getGameMode() == GameType.SPECTATOR;
    }

    @Override
    public boolean isCreative() {
        PlayerInfo playerInfo = Minecraft.getInstance().getConnection().getPlayerInfo(this.getGameProfile().getId());
        return playerInfo != null && playerInfo.getGameMode() == GameType.CREATIVE;
    }

    public boolean isCapeLoaded() {
        return this.getPlayerInfo() != null;
    }

    @Nullable
    protected PlayerInfo getPlayerInfo() {
        if (this.playerInfo == null) {
            this.playerInfo = Minecraft.getInstance().getConnection().getPlayerInfo(this.getUUID());
        }
        return this.playerInfo;
    }

    public boolean isSkinLoaded() {
        PlayerInfo playerInfo = this.getPlayerInfo();
        return playerInfo != null && playerInfo.isSkinLoaded();
    }

    public ResourceLocation getSkinTextureLocation() {
        PlayerInfo playerInfo = this.getPlayerInfo();
        return playerInfo == null ? DefaultPlayerSkin.getDefaultSkin(this.getUUID()) : playerInfo.getSkinLocation();
    }

    @Nullable
    public ResourceLocation getCloakTextureLocation() {
        PlayerInfo playerInfo = this.getPlayerInfo();
        return playerInfo == null ? null : playerInfo.getCapeLocation();
    }

    public boolean isElytraLoaded() {
        return this.getPlayerInfo() != null;
    }

    @Nullable
    public ResourceLocation getElytraTextureLocation() {
        PlayerInfo playerInfo = this.getPlayerInfo();
        return playerInfo == null ? null : playerInfo.getElytraLocation();
    }

    public static HttpTexture registerSkinTexture(ResourceLocation resourceLocation, String string) {
        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        AbstractTexture abstractTexture = textureManager.getTexture(resourceLocation);
        if (abstractTexture == null) {
            abstractTexture = new HttpTexture(null, String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", StringUtil.stripColor(string)), DefaultPlayerSkin.getDefaultSkin(AbstractClientPlayer.createPlayerUUID(string)), true, null);
            textureManager.register(resourceLocation, abstractTexture);
        }
        return (HttpTexture)abstractTexture;
    }

    public static ResourceLocation getSkinLocation(String string) {
        return new ResourceLocation("skins/" + (Object)Hashing.sha1().hashUnencodedChars((CharSequence)StringUtil.stripColor(string)));
    }

    public String getModelName() {
        PlayerInfo playerInfo = this.getPlayerInfo();
        return playerInfo == null ? DefaultPlayerSkin.getSkinModelName(this.getUUID()) : playerInfo.getModelName();
    }

    public float getFieldOfViewModifier() {
        float f = 1.0f;
        if (this.abilities.flying) {
            f *= 1.1f;
        }
        f = (float)((double)f * ((this.getAttributeValue(Attributes.MOVEMENT_SPEED) / (double)this.abilities.getWalkingSpeed() + 1.0) / 2.0));
        if (this.abilities.getWalkingSpeed() == 0.0f || Float.isNaN(f) || Float.isInfinite(f)) {
            f = 1.0f;
        }
        if (this.isUsingItem() && this.getUseItem().getItem() == Items.BOW) {
            int n = this.getTicksUsingItem();
            float f2 = (float)n / 20.0f;
            f2 = f2 > 1.0f ? 1.0f : (f2 *= f2);
            f *= 1.0f - f2 * 0.15f;
        }
        return Mth.lerp(Minecraft.getInstance().options.fovEffectScale, 1.0f, f);
    }
}

