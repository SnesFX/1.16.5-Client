/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.common.collect.Lists
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.GameProfileRepository
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 *  com.mojang.datafixers.DataFixer
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.dedicated;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.InetAddress;
import java.net.Proxy;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.DefaultUncaughtExceptionHandlerWithName;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.ConsoleInput;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerInterface;
import net.minecraft.server.ServerResources;
import net.minecraft.server.dedicated.DedicatedPlayerList;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.server.dedicated.DedicatedServerSettings;
import net.minecraft.server.dedicated.ServerWatchdog;
import net.minecraft.server.dedicated.Settings;
import net.minecraft.server.gui.MinecraftServerGui;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.network.ServerConnectionListener;
import net.minecraft.server.network.TextFilter;
import net.minecraft.server.network.TextFilterClient;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.ServerOpList;
import net.minecraft.server.rcon.RconConsoleSource;
import net.minecraft.server.rcon.thread.QueryThreadGs4;
import net.minecraft.server.rcon.thread.RconThread;
import net.minecraft.util.Mth;
import net.minecraft.util.monitoring.jmx.MinecraftServerStatistics;
import net.minecraft.world.Difficulty;
import net.minecraft.world.Snooper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PlayerDataStorage;
import net.minecraft.world.level.storage.WorldData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DedicatedServer
extends MinecraftServer
implements ServerInterface {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Pattern SHA1 = Pattern.compile("^[a-fA-F0-9]{40}$");
    private final List<ConsoleInput> consoleInput = Collections.synchronizedList(Lists.newArrayList());
    private QueryThreadGs4 queryThreadGs4;
    private final RconConsoleSource rconConsoleSource;
    private RconThread rconThread;
    private final DedicatedServerSettings settings;
    @Nullable
    private MinecraftServerGui gui;
    @Nullable
    private final TextFilterClient textFilterClient;

    public DedicatedServer(Thread thread, RegistryAccess.RegistryHolder registryHolder, LevelStorageSource.LevelStorageAccess levelStorageAccess, PackRepository packRepository, ServerResources serverResources, WorldData worldData, DedicatedServerSettings dedicatedServerSettings, DataFixer dataFixer, MinecraftSessionService minecraftSessionService, GameProfileRepository gameProfileRepository, GameProfileCache gameProfileCache, ChunkProgressListenerFactory chunkProgressListenerFactory) {
        super(thread, registryHolder, levelStorageAccess, worldData, packRepository, Proxy.NO_PROXY, dataFixer, serverResources, minecraftSessionService, gameProfileRepository, gameProfileCache, chunkProgressListenerFactory);
        this.settings = dedicatedServerSettings;
        this.rconConsoleSource = new RconConsoleSource(this);
        this.textFilterClient = null;
    }

    @Override
    public boolean initServer() throws IOException {
        Thread thread = new Thread("Server console handler"){

            @Override
            public void run() {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
                try {
                    String string;
                    while (!DedicatedServer.this.isStopped() && DedicatedServer.this.isRunning() && (string = bufferedReader.readLine()) != null) {
                        DedicatedServer.this.handleConsoleInput(string, DedicatedServer.this.createCommandSourceStack());
                    }
                }
                catch (IOException iOException) {
                    LOGGER.error("Exception handling console input", (Throwable)iOException);
                }
            }
        };
        thread.setDaemon(true);
        thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
        thread.start();
        LOGGER.info("Starting minecraft server version " + SharedConstants.getCurrentVersion().getName());
        if (Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L) {
            LOGGER.warn("To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
        }
        LOGGER.info("Loading properties");
        DedicatedServerProperties dedicatedServerProperties = this.settings.getProperties();
        if (this.isSingleplayer()) {
            this.setLocalIp("127.0.0.1");
        } else {
            this.setUsesAuthentication(dedicatedServerProperties.onlineMode);
            this.setPreventProxyConnections(dedicatedServerProperties.preventProxyConnections);
            this.setLocalIp(dedicatedServerProperties.serverIp);
        }
        this.setPvpAllowed(dedicatedServerProperties.pvp);
        this.setFlightAllowed(dedicatedServerProperties.allowFlight);
        this.setResourcePack(dedicatedServerProperties.resourcePack, this.getPackHash());
        this.setMotd(dedicatedServerProperties.motd);
        this.setForceGameType(dedicatedServerProperties.forceGameMode);
        super.setPlayerIdleTimeout((Integer)((Settings.MutableValue)((Object)dedicatedServerProperties.playerIdleTimeout)).get());
        this.setEnforceWhitelist(dedicatedServerProperties.enforceWhitelist);
        this.worldData.setGameType(dedicatedServerProperties.gamemode);
        LOGGER.info("Default game type: {}", (Object)dedicatedServerProperties.gamemode);
        InetAddress inetAddress = null;
        if (!this.getLocalIp().isEmpty()) {
            inetAddress = InetAddress.getByName(this.getLocalIp());
        }
        if (this.getPort() < 0) {
            this.setPort(dedicatedServerProperties.serverPort);
        }
        this.initializeKeyPair();
        LOGGER.info("Starting Minecraft server on {}:{}", (Object)(this.getLocalIp().isEmpty() ? "*" : this.getLocalIp()), (Object)this.getPort());
        try {
            this.getConnection().startTcpServerListener(inetAddress, this.getPort());
        }
        catch (IOException iOException) {
            LOGGER.warn("**** FAILED TO BIND TO PORT!");
            LOGGER.warn("The exception was: {}", (Object)iOException.toString());
            LOGGER.warn("Perhaps a server is already running on that port?");
            return false;
        }
        if (!this.usesAuthentication()) {
            LOGGER.warn("**** SERVER IS RUNNING IN OFFLINE/INSECURE MODE!");
            LOGGER.warn("The server will make no attempt to authenticate usernames. Beware.");
            LOGGER.warn("While this makes the game possible to play without internet access, it also opens up the ability for hackers to connect with any username they choose.");
            LOGGER.warn("To change this, set \"online-mode\" to \"true\" in the server.properties file.");
        }
        if (this.convertOldUsers()) {
            this.getProfileCache().save();
        }
        if (!OldUsersConverter.serverReadyAfterUserconversion(this)) {
            return false;
        }
        this.setPlayerList(new DedicatedPlayerList(this, this.registryHolder, this.playerDataStorage));
        long l = Util.getNanos();
        this.setMaxBuildHeight(dedicatedServerProperties.maxBuildHeight);
        SkullBlockEntity.setProfileCache(this.getProfileCache());
        SkullBlockEntity.setSessionService(this.getSessionService());
        GameProfileCache.setUsesAuthentication(this.usesAuthentication());
        LOGGER.info("Preparing level \"{}\"", (Object)this.getLevelIdName());
        this.loadLevel();
        long l2 = Util.getNanos() - l;
        String string = String.format(Locale.ROOT, "%.3fs", (double)l2 / 1.0E9);
        LOGGER.info("Done ({})! For help, type \"help\"", (Object)string);
        if (dedicatedServerProperties.announcePlayerAchievements != null) {
            this.getGameRules().getRule(GameRules.RULE_ANNOUNCE_ADVANCEMENTS).set(dedicatedServerProperties.announcePlayerAchievements, this);
        }
        if (dedicatedServerProperties.enableQuery) {
            LOGGER.info("Starting GS4 status listener");
            this.queryThreadGs4 = QueryThreadGs4.create(this);
        }
        if (dedicatedServerProperties.enableRcon) {
            LOGGER.info("Starting remote control listener");
            this.rconThread = RconThread.create(this);
        }
        if (this.getMaxTickLength() > 0L) {
            Thread thread2 = new Thread(new ServerWatchdog(this));
            thread2.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandlerWithName(LOGGER));
            thread2.setName("Server Watchdog");
            thread2.setDaemon(true);
            thread2.start();
        }
        Items.AIR.fillItemCategory(CreativeModeTab.TAB_SEARCH, NonNullList.create());
        if (dedicatedServerProperties.enableJmxMonitoring) {
            MinecraftServerStatistics.registerJmxMonitoring(this);
        }
        return true;
    }

    @Override
    public boolean isSpawningAnimals() {
        return this.getProperties().spawnAnimals && super.isSpawningAnimals();
    }

    @Override
    public boolean isSpawningMonsters() {
        return this.settings.getProperties().spawnMonsters && super.isSpawningMonsters();
    }

    @Override
    public boolean areNpcsEnabled() {
        return this.settings.getProperties().spawnNpcs && super.areNpcsEnabled();
    }

    public String getPackHash() {
        String string;
        DedicatedServerProperties dedicatedServerProperties = this.settings.getProperties();
        if (!dedicatedServerProperties.resourcePackSha1.isEmpty()) {
            string = dedicatedServerProperties.resourcePackSha1;
            if (!Strings.isNullOrEmpty((String)dedicatedServerProperties.resourcePackHash)) {
                LOGGER.warn("resource-pack-hash is deprecated and found along side resource-pack-sha1. resource-pack-hash will be ignored.");
            }
        } else if (!Strings.isNullOrEmpty((String)dedicatedServerProperties.resourcePackHash)) {
            LOGGER.warn("resource-pack-hash is deprecated. Please use resource-pack-sha1 instead.");
            string = dedicatedServerProperties.resourcePackHash;
        } else {
            string = "";
        }
        if (!string.isEmpty() && !SHA1.matcher(string).matches()) {
            LOGGER.warn("Invalid sha1 for ressource-pack-sha1");
        }
        if (!dedicatedServerProperties.resourcePack.isEmpty() && string.isEmpty()) {
            LOGGER.warn("You specified a resource pack without providing a sha1 hash. Pack will be updated on the client only if you change the name of the pack.");
        }
        return string;
    }

    @Override
    public DedicatedServerProperties getProperties() {
        return this.settings.getProperties();
    }

    @Override
    public void forceDifficulty() {
        this.setDifficulty(this.getProperties().difficulty, true);
    }

    @Override
    public boolean isHardcore() {
        return this.getProperties().hardcore;
    }

    @Override
    public CrashReport fillReport(CrashReport crashReport) {
        crashReport = super.fillReport(crashReport);
        crashReport.getSystemDetails().setDetail("Is Modded", () -> this.getModdedStatus().orElse("Unknown (can't tell)"));
        crashReport.getSystemDetails().setDetail("Type", () -> "Dedicated Server (map_server.txt)");
        return crashReport;
    }

    @Override
    public Optional<String> getModdedStatus() {
        String string = this.getServerModName();
        if (!"vanilla".equals(string)) {
            return Optional.of("Definitely; Server brand changed to '" + string + "'");
        }
        return Optional.empty();
    }

    @Override
    public void onServerExit() {
        if (this.textFilterClient != null) {
            this.textFilterClient.close();
        }
        if (this.gui != null) {
            this.gui.close();
        }
        if (this.rconThread != null) {
            this.rconThread.stop();
        }
        if (this.queryThreadGs4 != null) {
            this.queryThreadGs4.stop();
        }
    }

    @Override
    public void tickChildren(BooleanSupplier booleanSupplier) {
        super.tickChildren(booleanSupplier);
        this.handleConsoleInputs();
    }

    @Override
    public boolean isNetherEnabled() {
        return this.getProperties().allowNether;
    }

    @Override
    public void populateSnooper(Snooper snooper) {
        snooper.setDynamicData("whitelist_enabled", this.getPlayerList().isUsingWhitelist());
        snooper.setDynamicData("whitelist_count", this.getPlayerList().getWhiteListNames().length);
        super.populateSnooper(snooper);
    }

    public void handleConsoleInput(String string, CommandSourceStack commandSourceStack) {
        this.consoleInput.add(new ConsoleInput(string, commandSourceStack));
    }

    public void handleConsoleInputs() {
        while (!this.consoleInput.isEmpty()) {
            ConsoleInput consoleInput = this.consoleInput.remove(0);
            this.getCommands().performCommand(consoleInput.source, consoleInput.msg);
        }
    }

    @Override
    public boolean isDedicatedServer() {
        return true;
    }

    @Override
    public int getRateLimitPacketsPerSecond() {
        return this.getProperties().rateLimitPacketsPerSecond;
    }

    @Override
    public boolean isEpollEnabled() {
        return this.getProperties().useNativeTransport;
    }

    @Override
    public DedicatedPlayerList getPlayerList() {
        return (DedicatedPlayerList)super.getPlayerList();
    }

    @Override
    public boolean isPublished() {
        return true;
    }

    @Override
    public String getServerIp() {
        return this.getLocalIp();
    }

    @Override
    public int getServerPort() {
        return this.getPort();
    }

    @Override
    public String getServerName() {
        return this.getMotd();
    }

    public void showGui() {
        if (this.gui == null) {
            this.gui = MinecraftServerGui.showFrameFor(this);
        }
    }

    @Override
    public boolean hasGui() {
        return this.gui != null;
    }

    @Override
    public boolean publishServer(GameType gameType, boolean bl, int n) {
        return false;
    }

    @Override
    public boolean isCommandBlockEnabled() {
        return this.getProperties().enableCommandBlock;
    }

    @Override
    public int getSpawnProtectionRadius() {
        return this.getProperties().spawnProtection;
    }

    @Override
    public boolean isUnderSpawnProtection(ServerLevel serverLevel, BlockPos blockPos, Player player) {
        int n;
        if (serverLevel.dimension() != Level.OVERWORLD) {
            return false;
        }
        if (this.getPlayerList().getOps().isEmpty()) {
            return false;
        }
        if (this.getPlayerList().isOp(player.getGameProfile())) {
            return false;
        }
        if (this.getSpawnProtectionRadius() <= 0) {
            return false;
        }
        BlockPos blockPos2 = serverLevel.getSharedSpawnPos();
        int n2 = Mth.abs(blockPos.getX() - blockPos2.getX());
        int n3 = Math.max(n2, n = Mth.abs(blockPos.getZ() - blockPos2.getZ()));
        return n3 <= this.getSpawnProtectionRadius();
    }

    @Override
    public boolean repliesToStatus() {
        return this.getProperties().enableStatus;
    }

    @Override
    public int getOperatorUserPermissionLevel() {
        return this.getProperties().opPermissionLevel;
    }

    @Override
    public int getFunctionCompilationLevel() {
        return this.getProperties().functionPermissionLevel;
    }

    @Override
    public void setPlayerIdleTimeout(int n) {
        super.setPlayerIdleTimeout(n);
        this.settings.update(dedicatedServerProperties -> (DedicatedServerProperties)((Settings.MutableValue)((Object)dedicatedServerProperties.playerIdleTimeout)).update(this.registryAccess(), (DedicatedServerProperties)((Object)Integer.valueOf(n))));
    }

    @Override
    public boolean shouldRconBroadcast() {
        return this.getProperties().broadcastRconToOps;
    }

    @Override
    public boolean shouldInformAdmins() {
        return this.getProperties().broadcastConsoleToOps;
    }

    @Override
    public int getAbsoluteMaxWorldSize() {
        return this.getProperties().maxWorldSize;
    }

    @Override
    public int getCompressionThreshold() {
        return this.getProperties().networkCompressionThreshold;
    }

    protected boolean convertOldUsers() {
        int n;
        boolean bl = false;
        for (n = 0; !bl && n <= 2; ++n) {
            if (n > 0) {
                LOGGER.warn("Encountered a problem while converting the user banlist, retrying in a few seconds");
                this.waitForRetry();
            }
            bl = OldUsersConverter.convertUserBanlist(this);
        }
        boolean bl2 = false;
        for (n = 0; !bl2 && n <= 2; ++n) {
            if (n > 0) {
                LOGGER.warn("Encountered a problem while converting the ip banlist, retrying in a few seconds");
                this.waitForRetry();
            }
            bl2 = OldUsersConverter.convertIpBanlist(this);
        }
        boolean bl3 = false;
        for (n = 0; !bl3 && n <= 2; ++n) {
            if (n > 0) {
                LOGGER.warn("Encountered a problem while converting the op list, retrying in a few seconds");
                this.waitForRetry();
            }
            bl3 = OldUsersConverter.convertOpsList(this);
        }
        boolean bl4 = false;
        for (n = 0; !bl4 && n <= 2; ++n) {
            if (n > 0) {
                LOGGER.warn("Encountered a problem while converting the whitelist, retrying in a few seconds");
                this.waitForRetry();
            }
            bl4 = OldUsersConverter.convertWhiteList(this);
        }
        boolean bl5 = false;
        for (n = 0; !bl5 && n <= 2; ++n) {
            if (n > 0) {
                LOGGER.warn("Encountered a problem while converting the player save files, retrying in a few seconds");
                this.waitForRetry();
            }
            bl5 = OldUsersConverter.convertPlayers(this);
        }
        return bl || bl2 || bl3 || bl4 || bl5;
    }

    private void waitForRetry() {
        try {
            Thread.sleep(5000L);
        }
        catch (InterruptedException interruptedException) {
            return;
        }
    }

    public long getMaxTickLength() {
        return this.getProperties().maxTickTime;
    }

    @Override
    public String getPluginNames() {
        return "";
    }

    @Override
    public String runCommand(String string) {
        this.rconConsoleSource.prepareForCommand();
        this.executeBlocking(() -> this.getCommands().performCommand(this.rconConsoleSource.createCommandSourceStack(), string));
        return this.rconConsoleSource.getCommandResponse();
    }

    public void storeUsingWhiteList(boolean bl) {
        this.settings.update(dedicatedServerProperties -> (DedicatedServerProperties)((Settings.MutableValue)((Object)dedicatedServerProperties.whiteList)).update(this.registryAccess(), (DedicatedServerProperties)((Object)Boolean.valueOf(bl))));
    }

    @Override
    public void stopServer() {
        super.stopServer();
        Util.shutdownExecutors();
    }

    @Override
    public boolean isSingleplayerOwner(GameProfile gameProfile) {
        return false;
    }

    @Override
    public int getScaledTrackingDistance(int n) {
        return this.getProperties().entityBroadcastRangePercentage * n / 100;
    }

    @Override
    public String getLevelIdName() {
        return this.storageSource.getLevelId();
    }

    @Override
    public boolean forceSynchronousWrites() {
        return this.settings.getProperties().syncChunkWrites;
    }

    @Nullable
    @Override
    public TextFilter createTextFilterForPlayer(ServerPlayer serverPlayer) {
        if (this.textFilterClient != null) {
            return this.textFilterClient.createContext(serverPlayer.getGameProfile());
        }
        return null;
    }

    @Override
    public /* synthetic */ PlayerList getPlayerList() {
        return this.getPlayerList();
    }

}

