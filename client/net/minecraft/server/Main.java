/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.mojang.authlib.GameProfileRepository
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 *  com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.Lifecycle
 *  joptsimple.AbstractOptionSpec
 *  joptsimple.ArgumentAcceptingOptionSpec
 *  joptsimple.NonOptionArgumentSpec
 *  joptsimple.OptionParser
 *  joptsimple.OptionSet
 *  joptsimple.OptionSpec
 *  joptsimple.OptionSpecBuilder
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server;

import com.google.common.collect.ImmutableSet;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Proxy;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import joptsimple.AbstractOptionSpec;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.OptionSpecBuilder;
import net.minecraft.CrashReport;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.Util;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryReadOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.Eula;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerResources;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.server.dedicated.DedicatedServerSettings;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.level.progress.LoggerChunkProgressListener;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.worldupdate.WorldUpgrader;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.WorldData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] arrstring) {
        OptionParser optionParser = new OptionParser();
        OptionSpecBuilder optionSpecBuilder = optionParser.accepts("nogui");
        OptionSpecBuilder optionSpecBuilder2 = optionParser.accepts("initSettings", "Initializes 'server.properties' and 'eula.txt', then quits");
        OptionSpecBuilder optionSpecBuilder3 = optionParser.accepts("demo");
        OptionSpecBuilder optionSpecBuilder4 = optionParser.accepts("bonusChest");
        OptionSpecBuilder optionSpecBuilder5 = optionParser.accepts("forceUpgrade");
        OptionSpecBuilder optionSpecBuilder6 = optionParser.accepts("eraseCache");
        OptionSpecBuilder optionSpecBuilder7 = optionParser.accepts("safeMode", "Loads level with vanilla datapack only");
        AbstractOptionSpec abstractOptionSpec = optionParser.accepts("help").forHelp();
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec = optionParser.accepts("singleplayer").withRequiredArg();
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec2 = optionParser.accepts("universe").withRequiredArg().defaultsTo((Object)".", (Object[])new String[0]);
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec3 = optionParser.accepts("world").withRequiredArg();
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec4 = optionParser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo((Object)-1, (Object[])new Integer[0]);
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec5 = optionParser.accepts("serverId").withRequiredArg();
        NonOptionArgumentSpec nonOptionArgumentSpec = optionParser.nonOptions();
        try {
            Object object;
            Object object2;
            Object object3;
            ServerResources serverResources;
            OptionSet optionSet = optionParser.parse(arrstring);
            if (optionSet.has((OptionSpec)abstractOptionSpec)) {
                optionParser.printHelpOn((OutputStream)System.err);
                return;
            }
            CrashReport.preload();
            Bootstrap.bootStrap();
            Bootstrap.validate();
            Util.startTimerHackThread();
            RegistryAccess.RegistryHolder registryHolder = RegistryAccess.builtin();
            Path path = Paths.get("server.properties", new String[0]);
            DedicatedServerSettings dedicatedServerSettings = new DedicatedServerSettings(registryHolder, path);
            dedicatedServerSettings.forceSave();
            Path path2 = Paths.get("eula.txt", new String[0]);
            Eula eula = new Eula(path2);
            if (optionSet.has((OptionSpec)optionSpecBuilder2)) {
                LOGGER.info("Initialized '{}' and '{}'", (Object)path.toAbsolutePath(), (Object)path2.toAbsolutePath());
                return;
            }
            if (!eula.hasAgreedToEULA()) {
                LOGGER.info("You need to agree to the EULA in order to run the server. Go to eula.txt for more info.");
                return;
            }
            File file = new File((String)optionSet.valueOf((OptionSpec)argumentAcceptingOptionSpec2));
            YggdrasilAuthenticationService yggdrasilAuthenticationService = new YggdrasilAuthenticationService(Proxy.NO_PROXY);
            MinecraftSessionService minecraftSessionService = yggdrasilAuthenticationService.createMinecraftSessionService();
            GameProfileRepository gameProfileRepository = yggdrasilAuthenticationService.createProfileRepository();
            GameProfileCache gameProfileCache = new GameProfileCache(gameProfileRepository, new File(file, MinecraftServer.USERID_CACHE_FILE.getName()));
            String string = (String)Optional.ofNullable(optionSet.valueOf((OptionSpec)argumentAcceptingOptionSpec3)).orElse(dedicatedServerSettings.getProperties().levelName);
            LevelStorageSource levelStorageSource = LevelStorageSource.createDefault(file.toPath());
            LevelStorageSource.LevelStorageAccess levelStorageAccess = levelStorageSource.createAccess(string);
            MinecraftServer.convertFromRegionFormatIfNeeded(levelStorageAccess);
            DataPackConfig dataPackConfig = levelStorageAccess.getDataPacks();
            boolean bl = optionSet.has((OptionSpec)optionSpecBuilder7);
            if (bl) {
                LOGGER.warn("Safe mode active, only vanilla datapack will be loaded");
            }
            PackRepository packRepository = new PackRepository(new ServerPacksSource(), new FolderRepositorySource(levelStorageAccess.getLevelPath(LevelResource.DATAPACK_DIR).toFile(), PackSource.WORLD));
            DataPackConfig dataPackConfig2 = MinecraftServer.configurePackRepository(packRepository, dataPackConfig == null ? DataPackConfig.DEFAULT : dataPackConfig, bl);
            CompletableFuture<ServerResources> completableFuture = ServerResources.loadResources(packRepository.openAllSelected(), Commands.CommandSelection.DEDICATED, dedicatedServerSettings.getProperties().functionPermissionLevel, Util.backgroundExecutor(), Runnable::run);
            try {
                serverResources = completableFuture.get();
            }
            catch (Exception exception) {
                LOGGER.warn("Failed to load datapacks, can't proceed with server load. You can either fix your datapacks or reset to vanilla with --safeMode", (Throwable)exception);
                packRepository.close();
                return;
            }
            serverResources.updateGlobals();
            RegistryReadOps<Tag> registryReadOps = RegistryReadOps.create(NbtOps.INSTANCE, serverResources.getResourceManager(), registryHolder);
            WorldData worldData = levelStorageAccess.getDataTag(registryReadOps, dataPackConfig2);
            if (worldData == null) {
                if (optionSet.has((OptionSpec)optionSpecBuilder3)) {
                    object3 = MinecraftServer.DEMO_SETTINGS;
                    object = WorldGenSettings.demoSettings(registryHolder);
                } else {
                    object2 = dedicatedServerSettings.getProperties();
                    object3 = new LevelSettings(((DedicatedServerProperties)object2).levelName, ((DedicatedServerProperties)object2).gamemode, ((DedicatedServerProperties)object2).hardcore, ((DedicatedServerProperties)object2).difficulty, false, new GameRules(), dataPackConfig2);
                    object = optionSet.has((OptionSpec)optionSpecBuilder4) ? ((DedicatedServerProperties)object2).worldGenSettings.withBonusChest() : ((DedicatedServerProperties)object2).worldGenSettings;
                }
                worldData = new PrimaryLevelData((LevelSettings)object3, (WorldGenSettings)object, Lifecycle.stable());
            }
            if (optionSet.has((OptionSpec)optionSpecBuilder5)) {
                Main.forceUpgrade(levelStorageAccess, DataFixers.getDataFixer(), optionSet.has((OptionSpec)optionSpecBuilder6), () -> true, worldData.worldGenSettings().levels());
            }
            levelStorageAccess.saveDataTag(registryHolder, worldData);
            object3 = worldData;
            object = MinecraftServer.spin(arg_0 -> Main.lambda$main$1(registryHolder, levelStorageAccess, packRepository, serverResources, (WorldData)object3, dedicatedServerSettings, minecraftSessionService, gameProfileRepository, gameProfileCache, optionSet, (OptionSpec)argumentAcceptingOptionSpec, (OptionSpec)argumentAcceptingOptionSpec4, (OptionSpec)optionSpecBuilder3, (OptionSpec)argumentAcceptingOptionSpec5, (OptionSpec)optionSpecBuilder, (OptionSpec)nonOptionArgumentSpec, arg_0));
            object2 = new Thread("Server Shutdown Thread", (DedicatedServer)object){
                final /* synthetic */ DedicatedServer val$dedicatedServer;
                {
                    this.val$dedicatedServer = dedicatedServer;
                    super(string);
                }

                @Override
                public void run() {
                    this.val$dedicatedServer.halt(true);
                }
            };
            ((Thread)object2).setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
            Runtime.getRuntime().addShutdownHook((Thread)object2);
        }
        catch (Exception exception) {
            LOGGER.fatal("Failed to start the minecraft server", (Throwable)exception);
        }
    }

    private static void forceUpgrade(LevelStorageSource.LevelStorageAccess levelStorageAccess, DataFixer dataFixer, boolean bl, BooleanSupplier booleanSupplier, ImmutableSet<ResourceKey<Level>> immutableSet) {
        LOGGER.info("Forcing world upgrade!");
        WorldUpgrader worldUpgrader = new WorldUpgrader(levelStorageAccess, dataFixer, immutableSet, bl);
        Component component = null;
        while (!worldUpgrader.isFinished()) {
            int n;
            Component component2 = worldUpgrader.getStatus();
            if (component != component2) {
                component = component2;
                LOGGER.info(worldUpgrader.getStatus().getString());
            }
            if ((n = worldUpgrader.getTotalChunks()) > 0) {
                int n2 = worldUpgrader.getConverted() + worldUpgrader.getSkipped();
                LOGGER.info("{}% completed ({} / {} chunks)...", (Object)Mth.floor((float)n2 / (float)n * 100.0f), (Object)n2, (Object)n);
            }
            if (!booleanSupplier.getAsBoolean()) {
                worldUpgrader.cancel();
                continue;
            }
            try {
                Thread.sleep(1000L);
            }
            catch (InterruptedException interruptedException) {}
        }
    }

    private static /* synthetic */ DedicatedServer lambda$main$1(RegistryAccess.RegistryHolder registryHolder, LevelStorageSource.LevelStorageAccess levelStorageAccess, PackRepository packRepository, ServerResources serverResources, WorldData worldData, DedicatedServerSettings dedicatedServerSettings, MinecraftSessionService minecraftSessionService, GameProfileRepository gameProfileRepository, GameProfileCache gameProfileCache, OptionSet optionSet, OptionSpec optionSpec, OptionSpec optionSpec2, OptionSpec optionSpec3, OptionSpec optionSpec4, OptionSpec optionSpec5, OptionSpec optionSpec6, Thread thread) {
        boolean bl;
        DedicatedServer dedicatedServer = new DedicatedServer(thread, registryHolder, levelStorageAccess, packRepository, serverResources, worldData, dedicatedServerSettings, DataFixers.getDataFixer(), minecraftSessionService, gameProfileRepository, gameProfileCache, LoggerChunkProgressListener::new);
        dedicatedServer.setSingleplayerName((String)optionSet.valueOf(optionSpec));
        dedicatedServer.setPort((Integer)optionSet.valueOf(optionSpec2));
        dedicatedServer.setDemo(optionSet.has(optionSpec3));
        dedicatedServer.setId((String)optionSet.valueOf(optionSpec4));
        boolean bl2 = bl = !optionSet.has(optionSpec5) && !optionSet.valuesOf(optionSpec6).contains("nogui");
        if (bl && !GraphicsEnvironment.isHeadless()) {
            dedicatedServer.showGui();
        }
        return dedicatedServer;
    }

}

