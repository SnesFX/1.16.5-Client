/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.mojang.authlib.properties.PropertyMap
 *  com.mojang.authlib.properties.PropertyMap$Serializer
 *  javax.annotation.Nullable
 *  joptsimple.ArgumentAcceptingOptionSpec
 *  joptsimple.NonOptionArgumentSpec
 *  joptsimple.OptionParser
 *  joptsimple.OptionSet
 *  joptsimple.OptionSpec
 *  joptsimple.OptionSpecBuilder
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Type;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.SocketAddress;
import java.util.List;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.OptionSpecBuilder;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.main.SilentInitException;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void main(String[] arrstring) {
        Thread thread;
        Minecraft minecraft;
        OptionParser optionParser = new OptionParser();
        optionParser.allowsUnrecognizedOptions();
        optionParser.accepts("demo");
        optionParser.accepts("disableMultiplayer");
        optionParser.accepts("disableChat");
        optionParser.accepts("fullscreen");
        optionParser.accepts("checkGlErrors");
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec = optionParser.accepts("server").withRequiredArg();
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec2 = optionParser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo((Object)25565, (Object[])new Integer[0]);
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec3 = optionParser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo((Object)new File("."), (Object[])new File[0]);
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec4 = optionParser.accepts("assetsDir").withRequiredArg().ofType(File.class);
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec5 = optionParser.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec6 = optionParser.accepts("dataPackDir").withRequiredArg().ofType(File.class);
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec7 = optionParser.accepts("proxyHost").withRequiredArg();
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec8 = optionParser.accepts("proxyPort").withRequiredArg().defaultsTo((Object)"8080", (Object[])new String[0]).ofType(Integer.class);
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec9 = optionParser.accepts("proxyUser").withRequiredArg();
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec10 = optionParser.accepts("proxyPass").withRequiredArg();
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec11 = optionParser.accepts("username").withRequiredArg().defaultsTo((Object)("Player" + Util.getMillis() % 1000L), (Object[])new String[0]);
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec12 = optionParser.accepts("uuid").withRequiredArg();
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec13 = optionParser.accepts("accessToken").withRequiredArg().required();
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec14 = optionParser.accepts("version").withRequiredArg().required();
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec15 = optionParser.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo((Object)854, (Object[])new Integer[0]);
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec16 = optionParser.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo((Object)480, (Object[])new Integer[0]);
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec17 = optionParser.accepts("fullscreenWidth").withRequiredArg().ofType(Integer.class);
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec18 = optionParser.accepts("fullscreenHeight").withRequiredArg().ofType(Integer.class);
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec19 = optionParser.accepts("userProperties").withRequiredArg().defaultsTo((Object)"{}", (Object[])new String[0]);
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec20 = optionParser.accepts("profileProperties").withRequiredArg().defaultsTo((Object)"{}", (Object[])new String[0]);
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec21 = optionParser.accepts("assetIndex").withRequiredArg();
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec22 = optionParser.accepts("userType").withRequiredArg().defaultsTo((Object)"legacy", (Object[])new String[0]);
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec23 = optionParser.accepts("versionType").withRequiredArg().defaultsTo((Object)"release", (Object[])new String[0]);
        NonOptionArgumentSpec nonOptionArgumentSpec = optionParser.nonOptions();
        OptionSet optionSet = optionParser.parse(arrstring);
        List list = optionSet.valuesOf((OptionSpec)nonOptionArgumentSpec);
        if (!list.isEmpty()) {
            System.out.println("Completely ignored arguments: " + list);
        }
        String string = (String)Main.parseArgument(optionSet, argumentAcceptingOptionSpec7);
        Proxy proxy = Proxy.NO_PROXY;
        if (string != null) {
            try {
                proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(string, (int)((Integer)Main.parseArgument(optionSet, argumentAcceptingOptionSpec8))));
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        final String string2 = (String)Main.parseArgument(optionSet, argumentAcceptingOptionSpec9);
        final String string3 = (String)Main.parseArgument(optionSet, argumentAcceptingOptionSpec10);
        if (!proxy.equals(Proxy.NO_PROXY) && Main.stringHasValue(string2) && Main.stringHasValue(string3)) {
            Authenticator.setDefault(new Authenticator(){

                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(string2, string3.toCharArray());
                }
            });
        }
        int n = (Integer)Main.parseArgument(optionSet, argumentAcceptingOptionSpec15);
        int n2 = (Integer)Main.parseArgument(optionSet, argumentAcceptingOptionSpec16);
        OptionalInt optionalInt = Main.ofNullable((Integer)Main.parseArgument(optionSet, argumentAcceptingOptionSpec17));
        OptionalInt optionalInt2 = Main.ofNullable((Integer)Main.parseArgument(optionSet, argumentAcceptingOptionSpec18));
        boolean bl = optionSet.has("fullscreen");
        boolean bl2 = optionSet.has("demo");
        boolean bl3 = optionSet.has("disableMultiplayer");
        boolean bl4 = optionSet.has("disableChat");
        String string4 = (String)Main.parseArgument(optionSet, argumentAcceptingOptionSpec14);
        Gson gson = new GsonBuilder().registerTypeAdapter(PropertyMap.class, (Object)new PropertyMap.Serializer()).create();
        PropertyMap propertyMap = GsonHelper.fromJson(gson, (String)Main.parseArgument(optionSet, argumentAcceptingOptionSpec19), PropertyMap.class);
        PropertyMap propertyMap2 = GsonHelper.fromJson(gson, (String)Main.parseArgument(optionSet, argumentAcceptingOptionSpec20), PropertyMap.class);
        String string5 = (String)Main.parseArgument(optionSet, argumentAcceptingOptionSpec23);
        File file = (File)Main.parseArgument(optionSet, argumentAcceptingOptionSpec3);
        File file2 = optionSet.has((OptionSpec)argumentAcceptingOptionSpec4) ? (File)Main.parseArgument(optionSet, argumentAcceptingOptionSpec4) : new File(file, "assets/");
        File file3 = optionSet.has((OptionSpec)argumentAcceptingOptionSpec5) ? (File)Main.parseArgument(optionSet, argumentAcceptingOptionSpec5) : new File(file, "resourcepacks/");
        String string6 = optionSet.has((OptionSpec)argumentAcceptingOptionSpec12) ? (String)argumentAcceptingOptionSpec12.value(optionSet) : Player.createPlayerUUID((String)argumentAcceptingOptionSpec11.value(optionSet)).toString();
        String string7 = optionSet.has((OptionSpec)argumentAcceptingOptionSpec21) ? (String)argumentAcceptingOptionSpec21.value(optionSet) : null;
        String string8 = (String)Main.parseArgument(optionSet, argumentAcceptingOptionSpec);
        Integer n3 = (Integer)Main.parseArgument(optionSet, argumentAcceptingOptionSpec2);
        CrashReport.preload();
        Bootstrap.bootStrap();
        Bootstrap.validate();
        Util.startTimerHackThread();
        User user = new User((String)argumentAcceptingOptionSpec11.value(optionSet), string6, (String)argumentAcceptingOptionSpec13.value(optionSet), (String)argumentAcceptingOptionSpec22.value(optionSet));
        GameConfig gameConfig = new GameConfig(new GameConfig.UserData(user, propertyMap, propertyMap2, proxy), new DisplayData(n, n2, optionalInt, optionalInt2, bl), new GameConfig.FolderData(file, file3, file2, string7), new GameConfig.GameData(bl2, string4, string5, bl3, bl4), new GameConfig.ServerData(string8, n3));
        Thread thread2 = new Thread("Client Shutdown Thread"){

            @Override
            public void run() {
                Minecraft minecraft = Minecraft.getInstance();
                if (minecraft == null) {
                    return;
                }
                IntegratedServer integratedServer = minecraft.getSingleplayerServer();
                if (integratedServer != null) {
                    integratedServer.halt(true);
                }
            }
        };
        thread2.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
        Runtime.getRuntime().addShutdownHook(thread2);
        RenderPipeline renderPipeline = new RenderPipeline();
        try {
            Thread.currentThread().setName("Render thread");
            RenderSystem.initRenderThread();
            RenderSystem.beginInitialization();
            minecraft = new Minecraft(gameConfig);
            RenderSystem.finishInitialization();
        }
        catch (SilentInitException silentInitException) {
            LOGGER.warn("Failed to create window: ", (Throwable)silentInitException);
            return;
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.forThrowable(throwable, "Initializing game");
            crashReport.addCategory("Initialization");
            Minecraft.fillReport(null, gameConfig.game.launchVersion, null, crashReport);
            Minecraft.crash(crashReport);
            return;
        }
        if (minecraft.renderOnThread()) {
            thread = new Thread("Game thread"){

                @Override
                public void run() {
                    try {
                        RenderSystem.initGameThread(true);
                        minecraft.run();
                    }
                    catch (Throwable throwable) {
                        LOGGER.error("Exception in client thread", throwable);
                    }
                }
            };
            thread.start();
            while (minecraft.isRunning()) {
            }
        } else {
            thread = null;
            try {
                RenderSystem.initGameThread(false);
                minecraft.run();
            }
            catch (Throwable throwable) {
                LOGGER.error("Unhandled game exception", throwable);
            }
        }
        try {
            minecraft.stop();
            if (thread != null) {
                thread.join();
            }
        }
        catch (InterruptedException interruptedException) {
            LOGGER.error("Exception during client thread shutdown", (Throwable)interruptedException);
        }
        finally {
            minecraft.destroy();
        }
    }

    private static OptionalInt ofNullable(@Nullable Integer n) {
        return n != null ? OptionalInt.of(n) : OptionalInt.empty();
    }

    @Nullable
    private static <T> T parseArgument(OptionSet optionSet, OptionSpec<T> optionSpec) {
        try {
            return (T)optionSet.valueOf(optionSpec);
        }
        catch (Throwable throwable) {
            ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec;
            List list;
            if (optionSpec instanceof ArgumentAcceptingOptionSpec && !(list = (argumentAcceptingOptionSpec = (ArgumentAcceptingOptionSpec)optionSpec).defaultValues()).isEmpty()) {
                return (T)list.get(0);
            }
            throw throwable;
        }
    }

    private static boolean stringHasValue(@Nullable String string) {
        return string != null && !string.isEmpty();
    }

    static {
        System.setProperty("java.awt.headless", "true");
    }

}

