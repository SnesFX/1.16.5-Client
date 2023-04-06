/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 */
package net.minecraft.client.server;

import com.mojang.authlib.GameProfile;
import java.net.SocketAddress;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.storage.PlayerDataStorage;

public class IntegratedPlayerList
extends PlayerList {
    private CompoundTag playerData;

    public IntegratedPlayerList(IntegratedServer integratedServer, RegistryAccess.RegistryHolder registryHolder, PlayerDataStorage playerDataStorage) {
        super(integratedServer, registryHolder, playerDataStorage, 8);
        this.setViewDistance(10);
    }

    @Override
    protected void save(ServerPlayer serverPlayer) {
        if (serverPlayer.getName().getString().equals(this.getServer().getSingleplayerName())) {
            this.playerData = serverPlayer.saveWithoutId(new CompoundTag());
        }
        super.save(serverPlayer);
    }

    @Override
    public Component canPlayerLogin(SocketAddress socketAddress, GameProfile gameProfile) {
        if (gameProfile.getName().equalsIgnoreCase(this.getServer().getSingleplayerName()) && this.getPlayerByName(gameProfile.getName()) != null) {
            return new TranslatableComponent("multiplayer.disconnect.name_taken");
        }
        return super.canPlayerLogin(socketAddress, gameProfile);
    }

    @Override
    public IntegratedServer getServer() {
        return (IntegratedServer)super.getServer();
    }

    @Override
    public CompoundTag getSingleplayerData() {
        return this.playerData;
    }

    @Override
    public /* synthetic */ MinecraftServer getServer() {
        return this.getServer();
    }
}

