/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.mojang.realmsclient.client;

import com.mojang.realmsclient.client.RealmsClientConfig;
import com.mojang.realmsclient.client.RealmsError;
import com.mojang.realmsclient.client.Request;
import com.mojang.realmsclient.dto.BackupList;
import com.mojang.realmsclient.dto.GuardedSerializer;
import com.mojang.realmsclient.dto.Ops;
import com.mojang.realmsclient.dto.PendingInvite;
import com.mojang.realmsclient.dto.PendingInvitesList;
import com.mojang.realmsclient.dto.PingResult;
import com.mojang.realmsclient.dto.PlayerInfo;
import com.mojang.realmsclient.dto.RealmsDescriptionDto;
import com.mojang.realmsclient.dto.RealmsNews;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerAddress;
import com.mojang.realmsclient.dto.RealmsServerList;
import com.mojang.realmsclient.dto.RealmsServerPlayerLists;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.dto.RealmsWorldResetDto;
import com.mojang.realmsclient.dto.ReflectionBasedSerialization;
import com.mojang.realmsclient.dto.Subscription;
import com.mojang.realmsclient.dto.UploadInfo;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.dto.WorldTemplatePaginatedList;
import com.mojang.realmsclient.exception.RealmsHttpException;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.gui.screens.social.PlayerSocialManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsClient {
    public static Environment currentEnvironment = Environment.PRODUCTION;
    private static boolean initialized;
    private static final Logger LOGGER;
    private final String sessionId;
    private final String username;
    private final Minecraft minecraft;
    private static final GuardedSerializer GSON;

    public static RealmsClient create() {
        Minecraft minecraft = Minecraft.getInstance();
        String string = minecraft.getUser().getName();
        String string2 = minecraft.getUser().getSessionId();
        if (!initialized) {
            initialized = true;
            String string3 = System.getenv("realms.environment");
            if (string3 == null) {
                string3 = System.getProperty("realms.environment");
            }
            if (string3 != null) {
                if ("LOCAL".equals(string3)) {
                    RealmsClient.switchToLocal();
                } else if ("STAGE".equals(string3)) {
                    RealmsClient.switchToStage();
                }
            }
        }
        return new RealmsClient(string2, string, minecraft);
    }

    public static void switchToStage() {
        currentEnvironment = Environment.STAGE;
    }

    public static void switchToProd() {
        currentEnvironment = Environment.PRODUCTION;
    }

    public static void switchToLocal() {
        currentEnvironment = Environment.LOCAL;
    }

    public RealmsClient(String string, String string2, Minecraft minecraft) {
        this.sessionId = string;
        this.username = string2;
        this.minecraft = minecraft;
        RealmsClientConfig.setProxy(minecraft.getProxy());
    }

    public RealmsServerList listWorlds() throws RealmsServiceException {
        String string = this.url("worlds");
        String string2 = this.execute(Request.get(string));
        return RealmsServerList.parse(string2);
    }

    public RealmsServer getOwnWorld(long l) throws RealmsServiceException {
        String string = this.url("worlds" + "/$ID".replace("$ID", String.valueOf(l)));
        String string2 = this.execute(Request.get(string));
        return RealmsServer.parse(string2);
    }

    public RealmsServerPlayerLists getLiveStats() throws RealmsServiceException {
        String string = this.url("activities/liveplayerlist");
        String string2 = this.execute(Request.get(string));
        return RealmsServerPlayerLists.parse(string2);
    }

    public RealmsServerAddress join(long l) throws RealmsServiceException {
        String string = this.url("worlds" + "/v1/$ID/join/pc".replace("$ID", "" + l));
        String string2 = this.execute(Request.get(string, 5000, 30000));
        return RealmsServerAddress.parse(string2);
    }

    public void initializeWorld(long l, String string, String string2) throws RealmsServiceException {
        RealmsDescriptionDto realmsDescriptionDto = new RealmsDescriptionDto(string, string2);
        String string3 = this.url("worlds" + "/$WORLD_ID/initialize".replace("$WORLD_ID", String.valueOf(l)));
        String string4 = GSON.toJson(realmsDescriptionDto);
        this.execute(Request.post(string3, string4, 5000, 10000));
    }

    public Boolean mcoEnabled() throws RealmsServiceException {
        String string = this.url("mco/available");
        String string2 = this.execute(Request.get(string));
        return Boolean.valueOf(string2);
    }

    public Boolean stageAvailable() throws RealmsServiceException {
        String string = this.url("mco/stageAvailable");
        String string2 = this.execute(Request.get(string));
        return Boolean.valueOf(string2);
    }

    public CompatibleVersionResponse clientCompatible() throws RealmsServiceException {
        CompatibleVersionResponse compatibleVersionResponse;
        String string = this.url("mco/client/compatible");
        String string2 = this.execute(Request.get(string));
        try {
            compatibleVersionResponse = CompatibleVersionResponse.valueOf(string2);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            throw new RealmsServiceException(500, "Could not check compatible version, got response: " + string2, -1, "");
        }
        return compatibleVersionResponse;
    }

    public void uninvite(long l, String string) throws RealmsServiceException {
        String string2 = this.url("invites" + "/$WORLD_ID/invite/$UUID".replace("$WORLD_ID", String.valueOf(l)).replace("$UUID", string));
        this.execute(Request.delete(string2));
    }

    public void uninviteMyselfFrom(long l) throws RealmsServiceException {
        String string = this.url("invites" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(l)));
        this.execute(Request.delete(string));
    }

    public RealmsServer invite(long l, String string) throws RealmsServiceException {
        PlayerInfo playerInfo = new PlayerInfo();
        playerInfo.setName(string);
        String string2 = this.url("invites" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(l)));
        String string3 = this.execute(Request.post(string2, GSON.toJson(playerInfo)));
        return RealmsServer.parse(string3);
    }

    public BackupList backupsFor(long l) throws RealmsServiceException {
        String string = this.url("worlds" + "/$WORLD_ID/backups".replace("$WORLD_ID", String.valueOf(l)));
        String string2 = this.execute(Request.get(string));
        return BackupList.parse(string2);
    }

    public void update(long l, String string, String string2) throws RealmsServiceException {
        RealmsDescriptionDto realmsDescriptionDto = new RealmsDescriptionDto(string, string2);
        String string3 = this.url("worlds" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(l)));
        this.execute(Request.post(string3, GSON.toJson(realmsDescriptionDto)));
    }

    public void updateSlot(long l, int n, RealmsWorldOptions realmsWorldOptions) throws RealmsServiceException {
        String string = this.url("worlds" + "/$WORLD_ID/slot/$SLOT_ID".replace("$WORLD_ID", String.valueOf(l)).replace("$SLOT_ID", String.valueOf(n)));
        String string2 = realmsWorldOptions.toJson();
        this.execute(Request.post(string, string2));
    }

    public boolean switchSlot(long l, int n) throws RealmsServiceException {
        String string = this.url("worlds" + "/$WORLD_ID/slot/$SLOT_ID".replace("$WORLD_ID", String.valueOf(l)).replace("$SLOT_ID", String.valueOf(n)));
        String string2 = this.execute(Request.put(string, ""));
        return Boolean.valueOf(string2);
    }

    public void restoreWorld(long l, String string) throws RealmsServiceException {
        String string2 = this.url("worlds" + "/$WORLD_ID/backups".replace("$WORLD_ID", String.valueOf(l)), "backupId=" + string);
        this.execute(Request.put(string2, "", 40000, 600000));
    }

    public WorldTemplatePaginatedList fetchWorldTemplates(int n, int n2, RealmsServer.WorldType worldType) throws RealmsServiceException {
        String string = this.url("worlds" + "/templates/$WORLD_TYPE".replace("$WORLD_TYPE", worldType.toString()), String.format("page=%d&pageSize=%d", n, n2));
        String string2 = this.execute(Request.get(string));
        return WorldTemplatePaginatedList.parse(string2);
    }

    public Boolean putIntoMinigameMode(long l, String string) throws RealmsServiceException {
        String string2 = "/minigames/$MINIGAME_ID/$WORLD_ID".replace("$MINIGAME_ID", string).replace("$WORLD_ID", String.valueOf(l));
        String string3 = this.url("worlds" + string2);
        return Boolean.valueOf(this.execute(Request.put(string3, "")));
    }

    public Ops op(long l, String string) throws RealmsServiceException {
        String string2 = "/$WORLD_ID/$PROFILE_UUID".replace("$WORLD_ID", String.valueOf(l)).replace("$PROFILE_UUID", string);
        String string3 = this.url("ops" + string2);
        return Ops.parse(this.execute(Request.post(string3, "")));
    }

    public Ops deop(long l, String string) throws RealmsServiceException {
        String string2 = "/$WORLD_ID/$PROFILE_UUID".replace("$WORLD_ID", String.valueOf(l)).replace("$PROFILE_UUID", string);
        String string3 = this.url("ops" + string2);
        return Ops.parse(this.execute(Request.delete(string3)));
    }

    public Boolean open(long l) throws RealmsServiceException {
        String string = this.url("worlds" + "/$WORLD_ID/open".replace("$WORLD_ID", String.valueOf(l)));
        String string2 = this.execute(Request.put(string, ""));
        return Boolean.valueOf(string2);
    }

    public Boolean close(long l) throws RealmsServiceException {
        String string = this.url("worlds" + "/$WORLD_ID/close".replace("$WORLD_ID", String.valueOf(l)));
        String string2 = this.execute(Request.put(string, ""));
        return Boolean.valueOf(string2);
    }

    public Boolean resetWorldWithSeed(long l, String string, Integer n, boolean bl) throws RealmsServiceException {
        RealmsWorldResetDto realmsWorldResetDto = new RealmsWorldResetDto(string, -1L, n, bl);
        String string2 = this.url("worlds" + "/$WORLD_ID/reset".replace("$WORLD_ID", String.valueOf(l)));
        String string3 = this.execute(Request.post(string2, GSON.toJson(realmsWorldResetDto), 30000, 80000));
        return Boolean.valueOf(string3);
    }

    public Boolean resetWorldWithTemplate(long l, String string) throws RealmsServiceException {
        RealmsWorldResetDto realmsWorldResetDto = new RealmsWorldResetDto(null, Long.valueOf(string), -1, false);
        String string2 = this.url("worlds" + "/$WORLD_ID/reset".replace("$WORLD_ID", String.valueOf(l)));
        String string3 = this.execute(Request.post(string2, GSON.toJson(realmsWorldResetDto), 30000, 80000));
        return Boolean.valueOf(string3);
    }

    public Subscription subscriptionFor(long l) throws RealmsServiceException {
        String string = this.url("subscriptions" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(l)));
        String string2 = this.execute(Request.get(string));
        return Subscription.parse(string2);
    }

    public int pendingInvitesCount() throws RealmsServiceException {
        return this.pendingInvites().pendingInvites.size();
    }

    public PendingInvitesList pendingInvites() throws RealmsServiceException {
        String string = this.url("invites/pending");
        String string2 = this.execute(Request.get(string));
        PendingInvitesList pendingInvitesList = PendingInvitesList.parse(string2);
        pendingInvitesList.pendingInvites.removeIf(this::isBlocked);
        return pendingInvitesList;
    }

    private boolean isBlocked(PendingInvite pendingInvite) {
        try {
            UUID uUID = UUID.fromString(pendingInvite.worldOwnerUuid);
            return this.minecraft.getPlayerSocialManager().isBlocked(uUID);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            return false;
        }
    }

    public void acceptInvitation(String string) throws RealmsServiceException {
        String string2 = this.url("invites" + "/accept/$INVITATION_ID".replace("$INVITATION_ID", string));
        this.execute(Request.put(string2, ""));
    }

    public WorldDownload requestDownloadInfo(long l, int n) throws RealmsServiceException {
        String string = this.url("worlds" + "/$WORLD_ID/slot/$SLOT_ID/download".replace("$WORLD_ID", String.valueOf(l)).replace("$SLOT_ID", String.valueOf(n)));
        String string2 = this.execute(Request.get(string));
        return WorldDownload.parse(string2);
    }

    @Nullable
    public UploadInfo requestUploadInfo(long l, @Nullable String string) throws RealmsServiceException {
        String string2 = this.url("worlds" + "/$WORLD_ID/backups/upload".replace("$WORLD_ID", String.valueOf(l)));
        return UploadInfo.parse(this.execute(Request.put(string2, UploadInfo.createRequest(string))));
    }

    public void rejectInvitation(String string) throws RealmsServiceException {
        String string2 = this.url("invites" + "/reject/$INVITATION_ID".replace("$INVITATION_ID", string));
        this.execute(Request.put(string2, ""));
    }

    public void agreeToTos() throws RealmsServiceException {
        String string = this.url("mco/tos/agreed");
        this.execute(Request.post(string, ""));
    }

    public RealmsNews getNews() throws RealmsServiceException {
        String string = this.url("mco/v1/news");
        String string2 = this.execute(Request.get(string, 5000, 10000));
        return RealmsNews.parse(string2);
    }

    public void sendPingResults(PingResult pingResult) throws RealmsServiceException {
        String string = this.url("regions/ping/stat");
        this.execute(Request.post(string, GSON.toJson(pingResult)));
    }

    public Boolean trialAvailable() throws RealmsServiceException {
        String string = this.url("trial");
        String string2 = this.execute(Request.get(string));
        return Boolean.valueOf(string2);
    }

    public void deleteWorld(long l) throws RealmsServiceException {
        String string = this.url("worlds" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(l)));
        this.execute(Request.delete(string));
    }

    @Nullable
    private String url(String string) {
        return this.url(string, null);
    }

    @Nullable
    private String url(String string, @Nullable String string2) {
        try {
            return new URI(RealmsClient.currentEnvironment.protocol, RealmsClient.currentEnvironment.baseUrl, "/" + string, string2, null).toASCIIString();
        }
        catch (URISyntaxException uRISyntaxException) {
            uRISyntaxException.printStackTrace();
            return null;
        }
    }

    private String execute(Request<?> request) throws RealmsServiceException {
        request.cookie("sid", this.sessionId);
        request.cookie("user", this.username);
        request.cookie("version", SharedConstants.getCurrentVersion().getName());
        try {
            int n = request.responseCode();
            if (n == 503 || n == 277) {
                int n2 = request.getRetryAfterHeader();
                throw new RetryCallException(n2, n);
            }
            String string = request.text();
            if (n < 200 || n >= 300) {
                if (n == 401) {
                    String string2 = request.getHeader("WWW-Authenticate");
                    LOGGER.info("Could not authorize you against Realms server: " + string2);
                    throw new RealmsServiceException(n, string2, -1, string2);
                }
                if (string == null || string.length() == 0) {
                    LOGGER.error("Realms error code: " + n + " message: " + string);
                    throw new RealmsServiceException(n, string, n, "");
                }
                RealmsError realmsError = RealmsError.create(string);
                LOGGER.error("Realms http code: " + n + " -  error code: " + realmsError.getErrorCode() + " -  message: " + realmsError.getErrorMessage() + " - raw body: " + string);
                throw new RealmsServiceException(n, string, realmsError);
            }
            return string;
        }
        catch (RealmsHttpException realmsHttpException) {
            throw new RealmsServiceException(500, "Could not connect to Realms: " + realmsHttpException.getMessage(), -1, "");
        }
    }

    static {
        LOGGER = LogManager.getLogger();
        GSON = new GuardedSerializer();
    }

    public static enum CompatibleVersionResponse {
        COMPATIBLE,
        OUTDATED,
        OTHER;
        
    }

    public static enum Environment {
        PRODUCTION("pc.realms.minecraft.net", "https"),
        STAGE("pc-stage.realms.minecraft.net", "https"),
        LOCAL("localhost:8080", "http");
        
        public String baseUrl;
        public String protocol;

        private Environment(String string2, String string3) {
            this.baseUrl = string2;
            this.protocol = string3;
        }
    }

}

