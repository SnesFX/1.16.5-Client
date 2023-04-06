/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.properties.PropertyMap
 *  javax.annotation.Nullable
 */
package net.minecraft.client.main;

import com.mojang.authlib.properties.PropertyMap;
import com.mojang.blaze3d.platform.DisplayData;
import java.io.File;
import java.net.Proxy;
import javax.annotation.Nullable;
import net.minecraft.client.User;
import net.minecraft.client.resources.AssetIndex;
import net.minecraft.client.resources.DirectAssetIndex;

public class GameConfig {
    public final UserData user;
    public final DisplayData display;
    public final FolderData location;
    public final GameData game;
    public final ServerData server;

    public GameConfig(UserData userData, DisplayData displayData, FolderData folderData, GameData gameData, ServerData serverData) {
        this.user = userData;
        this.display = displayData;
        this.location = folderData;
        this.game = gameData;
        this.server = serverData;
    }

    public static class ServerData {
        @Nullable
        public final String hostname;
        public final int port;

        public ServerData(@Nullable String string, int n) {
            this.hostname = string;
            this.port = n;
        }
    }

    public static class FolderData {
        public final File gameDirectory;
        public final File resourcePackDirectory;
        public final File assetDirectory;
        @Nullable
        public final String assetIndex;

        public FolderData(File file, File file2, File file3, @Nullable String string) {
            this.gameDirectory = file;
            this.resourcePackDirectory = file2;
            this.assetDirectory = file3;
            this.assetIndex = string;
        }

        public AssetIndex getAssetIndex() {
            return this.assetIndex == null ? new DirectAssetIndex(this.assetDirectory) : new AssetIndex(this.assetDirectory, this.assetIndex);
        }
    }

    public static class UserData {
        public final User user;
        public final PropertyMap userProperties;
        public final PropertyMap profileProperties;
        public final Proxy proxy;

        public UserData(User user, PropertyMap propertyMap, PropertyMap propertyMap2, Proxy proxy) {
            this.user = user;
            this.userProperties = propertyMap;
            this.profileProperties = propertyMap2;
            this.proxy = proxy;
        }
    }

    public static class GameData {
        public final boolean demo;
        public final String launchVersion;
        public final String versionType;
        public final boolean disableMultiplayer;
        public final boolean disableChat;

        public GameData(boolean bl, String string, String string2, boolean bl2, boolean bl3) {
            this.demo = bl;
            this.launchVersion = string;
            this.versionType = string2;
            this.disableMultiplayer = bl2;
            this.disableChat = bl3;
        }
    }

}

