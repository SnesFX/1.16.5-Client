/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.GameProfileRepository
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 *  com.mojang.datafixers.DataFixer
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.server;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.SharedConstants;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.User;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.server.IntegratedPlayerList;
import net.minecraft.client.server.LanServerPinger;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerResources;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.network.ServerConnectionListener;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.Snooper;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PlayerDataStorage;
import net.minecraft.world.level.storage.WorldData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IntegratedServer
extends MinecraftServer {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Minecraft minecraft;
    private boolean paused;
    private int publishedPort = -1;
    private LanServerPinger lanPinger;
    private UUID uuid;

    public IntegratedServer(Thread thread, Minecraft minecraft, RegistryAccess.RegistryHolder registryHolder, LevelStorageSource.LevelStorageAccess levelStorageAccess, PackRepository packRepository, ServerResources serverResources, WorldData worldData, MinecraftSessionService minecraftSessionService, GameProfileRepository gameProfileRepository, GameProfileCache gameProfileCache, ChunkProgressListenerFactory chunkProgressListenerFactory) {
        super(thread, registryHolder, levelStorageAccess, worldData, packRepository, minecraft.getProxy(), minecraft.getFixerUpper(), serverResources, minecraftSessionService, gameProfileRepository, gameProfileCache, chunkProgressListenerFactory);
        this.setSingleplayerName(minecraft.getUser().getName());
        this.setDemo(minecraft.isDemo());
        this.setMaxBuildHeight(256);
        this.setPlayerList(new IntegratedPlayerList(this, this.registryHolder, this.playerDataStorage));
        this.minecraft = minecraft;
    }

    @Override
    public boolean initServer() {
        LOGGER.info("Starting integrated minecraft server version " + SharedConstants.getCurrentVersion().getName());
        this.setUsesAuthentication(true);
        this.setPvpAllowed(true);
        this.setFlightAllowed(true);
        this.initializeKeyPair();
        this.loadLevel();
        this.setMotd(this.getSingleplayerName() + " - " + this.getWorldData().getLevelName());
        return true;
    }

    @Override
    public void tickServer(BooleanSupplier booleanSupplier) {
        boolean bl = this.paused;
        this.paused = Minecraft.getInstance().getConnection() != null && Minecraft.getInstance().isPaused();
        ProfilerFiller profilerFiller = this.getProfiler();
        if (!bl && this.paused) {
            profilerFiller.push("autoSave");
            LOGGER.info("Saving and pausing game...");
            this.getPlayerList().saveAll();
            this.saveAllChunks(false, false, false);
            profilerFiller.pop();
        }
        if (this.paused) {
            return;
        }
        super.tickServer(booleanSupplier);
        int n = Math.max(2, this.minecraft.options.renderDistance + -1);
        if (n != this.getPlayerList().getViewDistance()) {
            LOGGER.info("Changing view distance to {}, from {}", (Object)n, (Object)this.getPlayerList().getViewDistance());
            this.getPlayerList().setViewDistance(n);
        }
    }

    @Override
    public boolean shouldRconBroadcast() {
        return true;
    }

    @Override
    public boolean shouldInformAdmins() {
        return true;
    }

    @Override
    public File getServerDirectory() {
        return this.minecraft.gameDirectory;
    }

    @Override
    public boolean isDedicatedServer() {
        return false;
    }

    @Override
    public int getRateLimitPacketsPerSecond() {
        return 0;
    }

    @Override
    public boolean isEpollEnabled() {
        return false;
    }

    @Override
    public void onServerCrash(CrashReport crashReport) {
        this.minecraft.delayCrash(crashReport);
    }

    @Override
    public CrashReport fillReport(CrashReport crashReport) {
        crashReport = super.fillReport(crashReport);
        crashReport.getSystemDetails().setDetail("Type", "Integrated Server (map_client.txt)");
        crashReport.getSystemDetails().setDetail("Is Modded", () -> this.getModdedStatus().orElse("Probably not. Jar signature remains and both client + server brands are untouched."));
        return crashReport;
    }

    @Override
    public Optional<String> getModdedStatus() {
        String string = ClientBrandRetriever.getClientModName();
        if (!string.equals("vanilla")) {
            return Optional.of("Definitely; Client brand changed to '" + string + "'");
        }
        string = this.getServerModName();
        if (!"vanilla".equals(string)) {
            return Optional.of("Definitely; Server brand changed to '" + string + "'");
        }
        if (Minecraft.class.getSigners() == null) {
            return Optional.of("Very likely; Jar signature invalidated");
        }
        return Optional.empty();
    }

    @Override
    public void populateSnooper(Snooper snooper) {
        super.populateSnooper(snooper);
        snooper.setDynamicData("snooper_partner", this.minecraft.getSnooper().getToken());
    }

    @Override
    public boolean publishServer(GameType gameType, boolean bl, int n) {
        try {
            this.getConnection().startTcpServerListener(null, n);
            LOGGER.info("Started serving on {}", (Object)n);
            this.publishedPort = n;
            this.lanPinger = new LanServerPinger(this.getMotd(), n + "");
            this.lanPinger.start();
            this.getPlayerList().setOverrideGameMode(gameType);
            this.getPlayerList().setAllowCheatsForAllPlayers(bl);
            int n2 = this.getProfilePermissions(this.minecraft.player.getGameProfile());
            this.minecraft.player.setPermissionLevel(n2);
            for (ServerPlayer serverPlayer : this.getPlayerList().getPlayers()) {
                this.getCommands().sendCommands(serverPlayer);
            }
            return true;
        }
        catch (IOException iOException) {
            return false;
        }
    }

    @Override
    public void stopServer() {
        super.stopServer();
        if (this.lanPinger != null) {
            this.lanPinger.interrupt();
            this.lanPinger = null;
        }
    }

    @Override
    public void halt(boolean bl) {
        this.executeBlocking(() -> {
            ArrayList arrayList = Lists.newArrayList(this.getPlayerList().getPlayers());
            for (ServerPlayer serverPlayer : arrayList) {
                if (serverPlayer.getUUID().equals(this.uuid)) continue;
                this.getPlayerList().remove(serverPlayer);
            }
        });
        super.halt(bl);
        if (this.lanPinger != null) {
            this.lanPinger.interrupt();
            this.lanPinger = null;
        }
    }

    @Override
    public boolean isPublished() {
        return this.publishedPort > -1;
    }

    @Override
    public int getPort() {
        return this.publishedPort;
    }

    @Override
    public void setDefaultGameType(GameType gameType) {
        super.setDefaultGameType(gameType);
        this.getPlayerList().setOverrideGameMode(gameType);
    }

    @Override
    public boolean isCommandBlockEnabled() {
        return true;
    }

    @Override
    public int getOperatorUserPermissionLevel() {
        return 2;
    }

    @Override
    public int getFunctionCompilationLevel() {
        return 2;
    }

    public void setUUID(UUID uUID) {
        this.uuid = uUID;
    }

    @Override
    public boolean isSingleplayerOwner(GameProfile gameProfile) {
        return gameProfile.getName().equalsIgnoreCase(this.getSingleplayerName());
    }

    @Override
    public int getScaledTrackingDistance(int n) {
        return (int)(this.minecraft.options.entityDistanceScaling * (float)n);
    }

    @Override
    public boolean forceSynchronousWrites() {
        return this.minecraft.options.syncWrites;
    }
}

