/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.io.Files
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.mojang.authlib.Agent
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.GameProfileRepository
 *  com.mojang.authlib.ProfileLookupCallback
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.players;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.ProfileLookupCallback;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameProfileCache {
    private static final Logger LOGGER = LogManager.getLogger();
    private static boolean usesAuthentication;
    private final Map<String, GameProfileInfo> profilesByName = Maps.newConcurrentMap();
    private final Map<UUID, GameProfileInfo> profilesByUUID = Maps.newConcurrentMap();
    private final GameProfileRepository profileRepository;
    private final Gson gson = new GsonBuilder().create();
    private final File file;
    private final AtomicLong operationCount = new AtomicLong();

    public GameProfileCache(GameProfileRepository gameProfileRepository, File file) {
        this.profileRepository = gameProfileRepository;
        this.file = file;
        Lists.reverse(this.load()).forEach(this::safeAdd);
    }

    private void safeAdd(GameProfileInfo gameProfileInfo) {
        UUID uUID;
        GameProfile gameProfile = gameProfileInfo.getProfile();
        gameProfileInfo.setLastAccess(this.getNextOperation());
        String string = gameProfile.getName();
        if (string != null) {
            this.profilesByName.put(string.toLowerCase(Locale.ROOT), gameProfileInfo);
        }
        if ((uUID = gameProfile.getId()) != null) {
            this.profilesByUUID.put(uUID, gameProfileInfo);
        }
    }

    @Nullable
    private static GameProfile lookupGameProfile(GameProfileRepository gameProfileRepository, String string) {
        final AtomicReference atomicReference = new AtomicReference();
        ProfileLookupCallback profileLookupCallback = new ProfileLookupCallback(){

            public void onProfileLookupSucceeded(GameProfile gameProfile) {
                atomicReference.set(gameProfile);
            }

            public void onProfileLookupFailed(GameProfile gameProfile, Exception exception) {
                atomicReference.set(null);
            }
        };
        gameProfileRepository.findProfilesByNames(new String[]{string}, Agent.MINECRAFT, profileLookupCallback);
        GameProfile gameProfile = (GameProfile)atomicReference.get();
        if (!GameProfileCache.usesAuthentication() && gameProfile == null) {
            UUID uUID = Player.createPlayerUUID(new GameProfile(null, string));
            gameProfile = new GameProfile(uUID, string);
        }
        return gameProfile;
    }

    public static void setUsesAuthentication(boolean bl) {
        usesAuthentication = bl;
    }

    private static boolean usesAuthentication() {
        return usesAuthentication;
    }

    public void add(GameProfile gameProfile) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(2, 1);
        Date date = calendar.getTime();
        GameProfileInfo gameProfileInfo = new GameProfileInfo(gameProfile, date);
        this.safeAdd(gameProfileInfo);
        this.save();
    }

    private long getNextOperation() {
        return this.operationCount.incrementAndGet();
    }

    @Nullable
    public GameProfile get(String string) {
        GameProfile gameProfile;
        String string2 = string.toLowerCase(Locale.ROOT);
        GameProfileInfo gameProfileInfo = this.profilesByName.get(string2);
        boolean bl = false;
        if (gameProfileInfo != null && new Date().getTime() >= gameProfileInfo.expirationDate.getTime()) {
            this.profilesByUUID.remove(gameProfileInfo.getProfile().getId());
            this.profilesByName.remove(gameProfileInfo.getProfile().getName().toLowerCase(Locale.ROOT));
            bl = true;
            gameProfileInfo = null;
        }
        if (gameProfileInfo != null) {
            gameProfileInfo.setLastAccess(this.getNextOperation());
            gameProfile = gameProfileInfo.getProfile();
        } else {
            gameProfile = GameProfileCache.lookupGameProfile(this.profileRepository, string2);
            if (gameProfile != null) {
                this.add(gameProfile);
                bl = false;
            }
        }
        if (bl) {
            this.save();
        }
        return gameProfile;
    }

    @Nullable
    public GameProfile get(UUID uUID) {
        GameProfileInfo gameProfileInfo = this.profilesByUUID.get(uUID);
        if (gameProfileInfo == null) {
            return null;
        }
        gameProfileInfo.setLastAccess(this.getNextOperation());
        return gameProfileInfo.getProfile();
    }

    private static DateFormat createDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public List<GameProfileInfo> load() {
        ArrayList arrayList = Lists.newArrayList();
        try {
            try (BufferedReader bufferedReader = Files.newReader((File)this.file, (Charset)StandardCharsets.UTF_8);){
                JsonArray jsonArray = (JsonArray)this.gson.fromJson((Reader)bufferedReader, JsonArray.class);
                if (jsonArray == null) {
                    ArrayList arrayList2 = arrayList;
                    return arrayList2;
                }
                DateFormat dateFormat = GameProfileCache.createDateFormat();
                jsonArray.forEach(jsonElement -> {
                    GameProfileInfo gameProfileInfo = GameProfileCache.readGameProfile(jsonElement, dateFormat);
                    if (gameProfileInfo != null) {
                        arrayList.add(gameProfileInfo);
                    }
                });
                return arrayList;
            }
        }
        catch (FileNotFoundException fileNotFoundException) {
            return arrayList;
        }
        catch (JsonParseException | IOException throwable) {
            LOGGER.warn("Failed to load profile cache {}", (Object)this.file, (Object)throwable);
        }
        return arrayList;
    }

    public void save() {
        JsonArray jsonArray = new JsonArray();
        DateFormat dateFormat = GameProfileCache.createDateFormat();
        this.getTopMRUProfiles(1000).forEach(gameProfileInfo -> jsonArray.add(GameProfileCache.writeGameProfile(gameProfileInfo, dateFormat)));
        String string = this.gson.toJson((JsonElement)jsonArray);
        try {
            try (BufferedWriter bufferedWriter = Files.newWriter((File)this.file, (Charset)StandardCharsets.UTF_8);){
                bufferedWriter.write(string);
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    private Stream<GameProfileInfo> getTopMRUProfiles(int n) {
        return ImmutableList.copyOf(this.profilesByUUID.values()).stream().sorted(Comparator.comparing(GameProfileInfo::getLastAccess).reversed()).limit(n);
    }

    private static JsonElement writeGameProfile(GameProfileInfo gameProfileInfo, DateFormat dateFormat) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", gameProfileInfo.getProfile().getName());
        UUID uUID = gameProfileInfo.getProfile().getId();
        jsonObject.addProperty("uuid", uUID == null ? "" : uUID.toString());
        jsonObject.addProperty("expiresOn", dateFormat.format(gameProfileInfo.getExpirationDate()));
        return jsonObject;
    }

    @Nullable
    private static GameProfileInfo readGameProfile(JsonElement jsonElement, DateFormat dateFormat) {
        if (jsonElement.isJsonObject()) {
            UUID uUID;
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            JsonElement jsonElement2 = jsonObject.get("name");
            JsonElement jsonElement3 = jsonObject.get("uuid");
            JsonElement jsonElement4 = jsonObject.get("expiresOn");
            if (jsonElement2 == null || jsonElement3 == null) {
                return null;
            }
            String string = jsonElement3.getAsString();
            String string2 = jsonElement2.getAsString();
            Date date = null;
            if (jsonElement4 != null) {
                try {
                    date = dateFormat.parse(jsonElement4.getAsString());
                }
                catch (ParseException parseException) {
                    // empty catch block
                }
            }
            if (string2 == null || string == null || date == null) {
                return null;
            }
            try {
                uUID = UUID.fromString(string);
            }
            catch (Throwable throwable) {
                return null;
            }
            return new GameProfileInfo(new GameProfile(uUID, string2), date);
        }
        return null;
    }

    static class GameProfileInfo {
        private final GameProfile profile;
        private final Date expirationDate;
        private volatile long lastAccess;

        private GameProfileInfo(GameProfile gameProfile, Date date) {
            this.profile = gameProfile;
            this.expirationDate = date;
        }

        public GameProfile getProfile() {
            return this.profile;
        }

        public Date getExpirationDate() {
            return this.expirationDate;
        }

        public void setLastAccess(long l) {
            this.lastAccess = l;
        }

        public long getLastAccess() {
            return this.lastAccess;
        }
    }

}

