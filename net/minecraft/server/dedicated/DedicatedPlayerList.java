/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.dedicated;

import com.mojang.authlib.GameProfile;
import java.io.File;
import java.io.IOException;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.server.dedicated.Settings;
import net.minecraft.server.players.IpBanList;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.ServerOpList;
import net.minecraft.server.players.UserBanList;
import net.minecraft.server.players.UserWhiteList;
import net.minecraft.world.level.storage.PlayerDataStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DedicatedPlayerList
extends PlayerList {
    private static final Logger LOGGER = LogManager.getLogger();

    public DedicatedPlayerList(DedicatedServer dedicatedServer, RegistryAccess.RegistryHolder registryHolder, PlayerDataStorage playerDataStorage) {
        super(dedicatedServer, registryHolder, playerDataStorage, dedicatedServer.getProperties().maxPlayers);
        DedicatedServerProperties dedicatedServerProperties = dedicatedServer.getProperties();
        this.setViewDistance(dedicatedServerProperties.viewDistance);
        super.setUsingWhiteList((Boolean)((Settings.MutableValue)((Object)dedicatedServerProperties.whiteList)).get());
        this.loadUserBanList();
        this.saveUserBanList();
        this.loadIpBanList();
        this.saveIpBanList();
        this.loadOps();
        this.loadWhiteList();
        this.saveOps();
        if (!this.getWhiteList().getFile().exists()) {
            this.saveWhiteList();
        }
    }

    @Override
    public void setUsingWhiteList(boolean bl) {
        super.setUsingWhiteList(bl);
        this.getServer().storeUsingWhiteList(bl);
    }

    @Override
    public void op(GameProfile gameProfile) {
        super.op(gameProfile);
        this.saveOps();
    }

    @Override
    public void deop(GameProfile gameProfile) {
        super.deop(gameProfile);
        this.saveOps();
    }

    @Override
    public void reloadWhiteList() {
        this.loadWhiteList();
    }

    private void saveIpBanList() {
        try {
            this.getIpBans().save();
        }
        catch (IOException iOException) {
            LOGGER.warn("Failed to save ip banlist: ", (Throwable)iOException);
        }
    }

    private void saveUserBanList() {
        try {
            this.getBans().save();
        }
        catch (IOException iOException) {
            LOGGER.warn("Failed to save user banlist: ", (Throwable)iOException);
        }
    }

    private void loadIpBanList() {
        try {
            this.getIpBans().load();
        }
        catch (IOException iOException) {
            LOGGER.warn("Failed to load ip banlist: ", (Throwable)iOException);
        }
    }

    private void loadUserBanList() {
        try {
            this.getBans().load();
        }
        catch (IOException iOException) {
            LOGGER.warn("Failed to load user banlist: ", (Throwable)iOException);
        }
    }

    private void loadOps() {
        try {
            this.getOps().load();
        }
        catch (Exception exception) {
            LOGGER.warn("Failed to load operators list: ", (Throwable)exception);
        }
    }

    private void saveOps() {
        try {
            this.getOps().save();
        }
        catch (Exception exception) {
            LOGGER.warn("Failed to save operators list: ", (Throwable)exception);
        }
    }

    private void loadWhiteList() {
        try {
            this.getWhiteList().load();
        }
        catch (Exception exception) {
            LOGGER.warn("Failed to load white-list: ", (Throwable)exception);
        }
    }

    private void saveWhiteList() {
        try {
            this.getWhiteList().save();
        }
        catch (Exception exception) {
            LOGGER.warn("Failed to save white-list: ", (Throwable)exception);
        }
    }

    @Override
    public boolean isWhiteListed(GameProfile gameProfile) {
        return !this.isUsingWhitelist() || this.isOp(gameProfile) || this.getWhiteList().isWhiteListed(gameProfile);
    }

    @Override
    public DedicatedServer getServer() {
        return (DedicatedServer)super.getServer();
    }

    @Override
    public boolean canBypassPlayerLimit(GameProfile gameProfile) {
        return this.getOps().canBypassPlayerLimit(gameProfile);
    }

    @Override
    public /* synthetic */ MinecraftServer getServer() {
        return this.getServer();
    }
}

