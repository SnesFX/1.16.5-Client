/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Streams
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.dedicated;

import com.google.common.collect.Streams;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.Util;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.WorldData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerWatchdog
implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger();
    private final DedicatedServer server;
    private final long maxTickTime;

    public ServerWatchdog(DedicatedServer dedicatedServer) {
        this.server = dedicatedServer;
        this.maxTickTime = dedicatedServer.getMaxTickLength();
    }

    @Override
    public void run() {
        while (this.server.isRunning()) {
            long l = this.server.getNextTickTime();
            long l2 = Util.getMillis();
            long l3 = l2 - l;
            if (l3 > this.maxTickTime) {
                LOGGER.fatal("A single server tick took {} seconds (should be max {})", (Object)String.format(Locale.ROOT, "%.2f", Float.valueOf((float)l3 / 1000.0f)), (Object)String.format(Locale.ROOT, "%.2f", Float.valueOf(0.05f)));
                LOGGER.fatal("Considering it to be crashed, server will forcibly shutdown.");
                ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
                ThreadInfo[] arrthreadInfo = threadMXBean.dumpAllThreads(true, true);
                StringBuilder stringBuilder = new StringBuilder();
                Error error = new Error("Watchdog");
                for (ThreadInfo threadInfo : arrthreadInfo) {
                    if (threadInfo.getThreadId() == this.server.getRunningThread().getId()) {
                        error.setStackTrace(threadInfo.getStackTrace());
                    }
                    stringBuilder.append(threadInfo);
                    stringBuilder.append("\n");
                }
                CrashReport crashReport = new CrashReport("Watching Server", error);
                this.server.fillReport(crashReport);
                CrashReportCategory crashReportCategory = crashReport.addCategory("Thread Dump");
                crashReportCategory.setDetail("Threads", stringBuilder);
                CrashReportCategory crashReportCategory2 = crashReport.addCategory("Performance stats");
                crashReportCategory2.setDetail("Random tick rate", () -> this.server.getWorldData().getGameRules().getRule(GameRules.RULE_RANDOMTICKING).toString());
                crashReportCategory2.setDetail("Level stats", () -> Streams.stream(this.server.getAllLevels()).map(serverLevel -> serverLevel.dimension() + ": " + serverLevel.getWatchdogStats()).collect(Collectors.joining(",\n")));
                Bootstrap.realStdoutPrintln("Crash report:\n" + crashReport.getFriendlyReport());
                File object2 = new File(new File(this.server.getServerDirectory(), "crash-reports"), "crash-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-server.txt");
                if (crashReport.saveToFile(object2)) {
                    LOGGER.error("This crash report has been saved to: {}", (Object)object2.getAbsolutePath());
                } else {
                    LOGGER.error("We were unable to save this crash report to disk.");
                }
                this.exit();
            }
            try {
                Thread.sleep(l + this.maxTickTime - l2);
            }
            catch (InterruptedException interruptedException) {}
        }
    }

    private void exit() {
        try {
            Timer timer = new Timer();
            timer.schedule(new TimerTask(){

                @Override
                public void run() {
                    Runtime.getRuntime().halt(1);
                }
            }, 10000L);
            System.exit(1);
        }
        catch (Throwable throwable) {
            Runtime.getRuntime().halt(1);
        }
    }

}

