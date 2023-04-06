/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.io.Files
 *  com.mojang.authlib.Agent
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.GameProfileRepository
 *  com.mojang.authlib.ProfileLookupCallback
 *  com.mojang.authlib.yggdrasil.ProfileNotFoundException
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.players;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.authlib.yggdrasil.ProfileNotFoundException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.players.BanListEntry;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.server.players.IpBanList;
import net.minecraft.server.players.IpBanListEntry;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.ServerOpList;
import net.minecraft.server.players.ServerOpListEntry;
import net.minecraft.server.players.UserBanList;
import net.minecraft.server.players.UserBanListEntry;
import net.minecraft.server.players.UserWhiteList;
import net.minecraft.server.players.UserWhiteListEntry;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.LevelResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OldUsersConverter {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final File OLD_IPBANLIST = new File("banned-ips.txt");
    public static final File OLD_USERBANLIST = new File("banned-players.txt");
    public static final File OLD_OPLIST = new File("ops.txt");
    public static final File OLD_WHITELIST = new File("white-list.txt");

    static List<String> readOldListFormat(File file, Map<String, String[]> map) throws IOException {
        List list = Files.readLines((File)file, (Charset)StandardCharsets.UTF_8);
        for (String string : list) {
            if ((string = string.trim()).startsWith("#") || string.length() < 1) continue;
            String[] arrstring = string.split("\\|");
            map.put(arrstring[0].toLowerCase(Locale.ROOT), arrstring);
        }
        return list;
    }

    private static void lookupPlayers(MinecraftServer minecraftServer, Collection<String> collection, ProfileLookupCallback profileLookupCallback) {
        String[] arrstring = (String[])collection.stream().filter(string -> !StringUtil.isNullOrEmpty(string)).toArray(n -> new String[n]);
        if (minecraftServer.usesAuthentication()) {
            minecraftServer.getProfileRepository().findProfilesByNames(arrstring, Agent.MINECRAFT, profileLookupCallback);
        } else {
            for (String string2 : arrstring) {
                UUID uUID = Player.createPlayerUUID(new GameProfile(null, string2));
                GameProfile gameProfile = new GameProfile(uUID, string2);
                profileLookupCallback.onProfileLookupSucceeded(gameProfile);
            }
        }
    }

    public static boolean convertUserBanlist(final MinecraftServer minecraftServer) {
        final UserBanList userBanList = new UserBanList(PlayerList.USERBANLIST_FILE);
        if (OLD_USERBANLIST.exists() && OLD_USERBANLIST.isFile()) {
            if (userBanList.getFile().exists()) {
                try {
                    userBanList.load();
                }
                catch (IOException iOException) {
                    LOGGER.warn("Could not load existing file {}", (Object)userBanList.getFile().getName(), (Object)iOException);
                }
            }
            try {
                final HashMap hashMap = Maps.newHashMap();
                OldUsersConverter.readOldListFormat(OLD_USERBANLIST, hashMap);
                ProfileLookupCallback profileLookupCallback = new ProfileLookupCallback(){

                    public void onProfileLookupSucceeded(GameProfile gameProfile) {
                        minecraftServer.getProfileCache().add(gameProfile);
                        String[] arrstring = (String[])hashMap.get(gameProfile.getName().toLowerCase(Locale.ROOT));
                        if (arrstring == null) {
                            LOGGER.warn("Could not convert user banlist entry for {}", (Object)gameProfile.getName());
                            throw new ConversionError("Profile not in the conversionlist");
                        }
                        Date date = arrstring.length > 1 ? OldUsersConverter.parseDate(arrstring[1], null) : null;
                        String string = arrstring.length > 2 ? arrstring[2] : null;
                        Date date2 = arrstring.length > 3 ? OldUsersConverter.parseDate(arrstring[3], null) : null;
                        String string2 = arrstring.length > 4 ? arrstring[4] : null;
                        userBanList.add(new UserBanListEntry(gameProfile, date, string, date2, string2));
                    }

                    public void onProfileLookupFailed(GameProfile gameProfile, Exception exception) {
                        LOGGER.warn("Could not lookup user banlist entry for {}", (Object)gameProfile.getName(), (Object)exception);
                        if (!(exception instanceof ProfileNotFoundException)) {
                            throw new ConversionError("Could not request user " + gameProfile.getName() + " from backend systems", exception);
                        }
                    }
                };
                OldUsersConverter.lookupPlayers(minecraftServer, hashMap.keySet(), profileLookupCallback);
                userBanList.save();
                OldUsersConverter.renameOldFile(OLD_USERBANLIST);
            }
            catch (IOException iOException) {
                LOGGER.warn("Could not read old user banlist to convert it!", (Throwable)iOException);
                return false;
            }
            catch (ConversionError conversionError) {
                LOGGER.error("Conversion failed, please try again later", (Throwable)conversionError);
                return false;
            }
            return true;
        }
        return true;
    }

    public static boolean convertIpBanlist(MinecraftServer minecraftServer) {
        IpBanList ipBanList = new IpBanList(PlayerList.IPBANLIST_FILE);
        if (OLD_IPBANLIST.exists() && OLD_IPBANLIST.isFile()) {
            if (ipBanList.getFile().exists()) {
                try {
                    ipBanList.load();
                }
                catch (IOException iOException) {
                    LOGGER.warn("Could not load existing file {}", (Object)ipBanList.getFile().getName(), (Object)iOException);
                }
            }
            try {
                HashMap hashMap = Maps.newHashMap();
                OldUsersConverter.readOldListFormat(OLD_IPBANLIST, hashMap);
                for (String string : hashMap.keySet()) {
                    String[] arrstring = (String[])hashMap.get(string);
                    Date date = arrstring.length > 1 ? OldUsersConverter.parseDate(arrstring[1], null) : null;
                    String string2 = arrstring.length > 2 ? arrstring[2] : null;
                    Date date2 = arrstring.length > 3 ? OldUsersConverter.parseDate(arrstring[3], null) : null;
                    String string3 = arrstring.length > 4 ? arrstring[4] : null;
                    ipBanList.add(new IpBanListEntry(string, date, string2, date2, string3));
                }
                ipBanList.save();
                OldUsersConverter.renameOldFile(OLD_IPBANLIST);
            }
            catch (IOException iOException) {
                LOGGER.warn("Could not parse old ip banlist to convert it!", (Throwable)iOException);
                return false;
            }
            return true;
        }
        return true;
    }

    public static boolean convertOpsList(final MinecraftServer minecraftServer) {
        final ServerOpList serverOpList = new ServerOpList(PlayerList.OPLIST_FILE);
        if (OLD_OPLIST.exists() && OLD_OPLIST.isFile()) {
            if (serverOpList.getFile().exists()) {
                try {
                    serverOpList.load();
                }
                catch (IOException iOException) {
                    LOGGER.warn("Could not load existing file {}", (Object)serverOpList.getFile().getName(), (Object)iOException);
                }
            }
            try {
                List list = Files.readLines((File)OLD_OPLIST, (Charset)StandardCharsets.UTF_8);
                ProfileLookupCallback profileLookupCallback = new ProfileLookupCallback(){

                    public void onProfileLookupSucceeded(GameProfile gameProfile) {
                        minecraftServer.getProfileCache().add(gameProfile);
                        serverOpList.add(new ServerOpListEntry(gameProfile, minecraftServer.getOperatorUserPermissionLevel(), false));
                    }

                    public void onProfileLookupFailed(GameProfile gameProfile, Exception exception) {
                        LOGGER.warn("Could not lookup oplist entry for {}", (Object)gameProfile.getName(), (Object)exception);
                        if (!(exception instanceof ProfileNotFoundException)) {
                            throw new ConversionError("Could not request user " + gameProfile.getName() + " from backend systems", exception);
                        }
                    }
                };
                OldUsersConverter.lookupPlayers(minecraftServer, list, profileLookupCallback);
                serverOpList.save();
                OldUsersConverter.renameOldFile(OLD_OPLIST);
            }
            catch (IOException iOException) {
                LOGGER.warn("Could not read old oplist to convert it!", (Throwable)iOException);
                return false;
            }
            catch (ConversionError conversionError) {
                LOGGER.error("Conversion failed, please try again later", (Throwable)conversionError);
                return false;
            }
            return true;
        }
        return true;
    }

    public static boolean convertWhiteList(final MinecraftServer minecraftServer) {
        final UserWhiteList userWhiteList = new UserWhiteList(PlayerList.WHITELIST_FILE);
        if (OLD_WHITELIST.exists() && OLD_WHITELIST.isFile()) {
            if (userWhiteList.getFile().exists()) {
                try {
                    userWhiteList.load();
                }
                catch (IOException iOException) {
                    LOGGER.warn("Could not load existing file {}", (Object)userWhiteList.getFile().getName(), (Object)iOException);
                }
            }
            try {
                List list = Files.readLines((File)OLD_WHITELIST, (Charset)StandardCharsets.UTF_8);
                ProfileLookupCallback profileLookupCallback = new ProfileLookupCallback(){

                    public void onProfileLookupSucceeded(GameProfile gameProfile) {
                        minecraftServer.getProfileCache().add(gameProfile);
                        userWhiteList.add(new UserWhiteListEntry(gameProfile));
                    }

                    public void onProfileLookupFailed(GameProfile gameProfile, Exception exception) {
                        LOGGER.warn("Could not lookup user whitelist entry for {}", (Object)gameProfile.getName(), (Object)exception);
                        if (!(exception instanceof ProfileNotFoundException)) {
                            throw new ConversionError("Could not request user " + gameProfile.getName() + " from backend systems", exception);
                        }
                    }
                };
                OldUsersConverter.lookupPlayers(minecraftServer, list, profileLookupCallback);
                userWhiteList.save();
                OldUsersConverter.renameOldFile(OLD_WHITELIST);
            }
            catch (IOException iOException) {
                LOGGER.warn("Could not read old whitelist to convert it!", (Throwable)iOException);
                return false;
            }
            catch (ConversionError conversionError) {
                LOGGER.error("Conversion failed, please try again later", (Throwable)conversionError);
                return false;
            }
            return true;
        }
        return true;
    }

    @Nullable
    public static UUID convertMobOwnerIfNecessary(final MinecraftServer minecraftServer, String string) {
        if (StringUtil.isNullOrEmpty(string) || string.length() > 16) {
            try {
                return UUID.fromString(string);
            }
            catch (IllegalArgumentException illegalArgumentException) {
                return null;
            }
        }
        GameProfile gameProfile = minecraftServer.getProfileCache().get(string);
        if (gameProfile != null && gameProfile.getId() != null) {
            return gameProfile.getId();
        }
        if (minecraftServer.isSingleplayer() || !minecraftServer.usesAuthentication()) {
            return Player.createPlayerUUID(new GameProfile(null, string));
        }
        final ArrayList arrayList = Lists.newArrayList();
        ProfileLookupCallback profileLookupCallback = new ProfileLookupCallback(){

            public void onProfileLookupSucceeded(GameProfile gameProfile) {
                minecraftServer.getProfileCache().add(gameProfile);
                arrayList.add(gameProfile);
            }

            public void onProfileLookupFailed(GameProfile gameProfile, Exception exception) {
                LOGGER.warn("Could not lookup user whitelist entry for {}", (Object)gameProfile.getName(), (Object)exception);
            }
        };
        OldUsersConverter.lookupPlayers(minecraftServer, Lists.newArrayList((Object[])new String[]{string}), profileLookupCallback);
        if (!arrayList.isEmpty() && ((GameProfile)arrayList.get(0)).getId() != null) {
            return ((GameProfile)arrayList.get(0)).getId();
        }
        return null;
    }

    public static boolean convertPlayers(final DedicatedServer dedicatedServer) {
        final File file = OldUsersConverter.getWorldPlayersDirectory(dedicatedServer);
        final File file2 = new File(file.getParentFile(), "playerdata");
        final File file3 = new File(file.getParentFile(), "unknownplayers");
        if (!file.exists() || !file.isDirectory()) {
            return true;
        }
        File[] arrfile = file.listFiles();
        ArrayList arrayList = Lists.newArrayList();
        for (File file4 : arrfile) {
            String string;
            String string2 = file4.getName();
            if (!string2.toLowerCase(Locale.ROOT).endsWith(".dat") || (string = string2.substring(0, string2.length() - ".dat".length())).isEmpty()) continue;
            arrayList.add(string);
        }
        try {
            Object[] arrobject = arrayList.toArray(new String[arrayList.size()]);
            ProfileLookupCallback profileLookupCallback = new ProfileLookupCallback((String[])arrobject){
                final /* synthetic */ String[] val$names;
                {
                    this.val$names = arrstring;
                }

                public void onProfileLookupSucceeded(GameProfile gameProfile) {
                    dedicatedServer.getProfileCache().add(gameProfile);
                    UUID uUID = gameProfile.getId();
                    if (uUID == null) {
                        throw new ConversionError("Missing UUID for user profile " + gameProfile.getName());
                    }
                    this.movePlayerFile(file2, this.getFileNameForProfile(gameProfile), uUID.toString());
                }

                public void onProfileLookupFailed(GameProfile gameProfile, Exception exception) {
                    LOGGER.warn("Could not lookup user uuid for {}", (Object)gameProfile.getName(), (Object)exception);
                    if (!(exception instanceof ProfileNotFoundException)) {
                        throw new ConversionError("Could not request user " + gameProfile.getName() + " from backend systems", exception);
                    }
                    String string = this.getFileNameForProfile(gameProfile);
                    this.movePlayerFile(file3, string, string);
                }

                private void movePlayerFile(File file4, String string, String string2) {
                    File file22 = new File(file, string + ".dat");
                    File file32 = new File(file4, string2 + ".dat");
                    OldUsersConverter.ensureDirectoryExists(file4);
                    if (!file22.renameTo(file32)) {
                        throw new ConversionError("Could not convert file for " + string);
                    }
                }

                private String getFileNameForProfile(GameProfile gameProfile) {
                    String string = null;
                    for (String string2 : this.val$names) {
                        if (string2 == null || !string2.equalsIgnoreCase(gameProfile.getName())) continue;
                        string = string2;
                        break;
                    }
                    if (string == null) {
                        throw new ConversionError("Could not find the filename for " + gameProfile.getName() + " anymore");
                    }
                    return string;
                }
            };
            OldUsersConverter.lookupPlayers(dedicatedServer, Lists.newArrayList((Object[])arrobject), profileLookupCallback);
        }
        catch (ConversionError conversionError) {
            LOGGER.error("Conversion failed, please try again later", (Throwable)conversionError);
            return false;
        }
        return true;
    }

    private static void ensureDirectoryExists(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                return;
            }
            throw new ConversionError("Can't create directory " + file.getName() + " in world save directory.");
        }
        if (!file.mkdirs()) {
            throw new ConversionError("Can't create directory " + file.getName() + " in world save directory.");
        }
    }

    public static boolean serverReadyAfterUserconversion(MinecraftServer minecraftServer) {
        boolean bl = OldUsersConverter.areOldUserlistsRemoved();
        bl = bl && OldUsersConverter.areOldPlayersConverted(minecraftServer);
        return bl;
    }

    private static boolean areOldUserlistsRemoved() {
        boolean bl = false;
        if (OLD_USERBANLIST.exists() && OLD_USERBANLIST.isFile()) {
            bl = true;
        }
        boolean bl2 = false;
        if (OLD_IPBANLIST.exists() && OLD_IPBANLIST.isFile()) {
            bl2 = true;
        }
        boolean bl3 = false;
        if (OLD_OPLIST.exists() && OLD_OPLIST.isFile()) {
            bl3 = true;
        }
        boolean bl4 = false;
        if (OLD_WHITELIST.exists() && OLD_WHITELIST.isFile()) {
            bl4 = true;
        }
        if (bl || bl2 || bl3 || bl4) {
            LOGGER.warn("**** FAILED TO START THE SERVER AFTER ACCOUNT CONVERSION!");
            LOGGER.warn("** please remove the following files and restart the server:");
            if (bl) {
                LOGGER.warn("* {}", (Object)OLD_USERBANLIST.getName());
            }
            if (bl2) {
                LOGGER.warn("* {}", (Object)OLD_IPBANLIST.getName());
            }
            if (bl3) {
                LOGGER.warn("* {}", (Object)OLD_OPLIST.getName());
            }
            if (bl4) {
                LOGGER.warn("* {}", (Object)OLD_WHITELIST.getName());
            }
            return false;
        }
        return true;
    }

    private static boolean areOldPlayersConverted(MinecraftServer minecraftServer) {
        File file = OldUsersConverter.getWorldPlayersDirectory(minecraftServer);
        if (file.exists() && file.isDirectory() && (file.list().length > 0 || !file.delete())) {
            LOGGER.warn("**** DETECTED OLD PLAYER DIRECTORY IN THE WORLD SAVE");
            LOGGER.warn("**** THIS USUALLY HAPPENS WHEN THE AUTOMATIC CONVERSION FAILED IN SOME WAY");
            LOGGER.warn("** please restart the server and if the problem persists, remove the directory '{}'", (Object)file.getPath());
            return false;
        }
        return true;
    }

    private static File getWorldPlayersDirectory(MinecraftServer minecraftServer) {
        return minecraftServer.getWorldPath(LevelResource.PLAYER_OLD_DATA_DIR).toFile();
    }

    private static void renameOldFile(File file) {
        File file2 = new File(file.getName() + ".converted");
        file.renameTo(file2);
    }

    private static Date parseDate(String string, Date date) {
        Date date2;
        try {
            date2 = BanListEntry.DATE_FORMAT.parse(string);
        }
        catch (ParseException parseException) {
            date2 = date;
        }
        return date2;
    }

    static class ConversionError
    extends RuntimeException {
        private ConversionError(String string, Throwable throwable) {
            super(string, throwable);
        }

        private ConversionError(String string) {
            super(string);
        }
    }

}

