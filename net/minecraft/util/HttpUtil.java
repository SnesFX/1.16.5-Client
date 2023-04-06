/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.util.concurrent.ListeningExecutorService
 *  com.google.common.util.concurrent.MoreExecutors
 *  com.google.common.util.concurrent.ThreadFactoryBuilder
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.util;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.net.ServerSocket;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import javax.annotation.Nullable;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.util.ProgressListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HttpUtil {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final ListeningExecutorService DOWNLOAD_EXECUTOR = MoreExecutors.listeningDecorator((ExecutorService)Executors.newCachedThreadPool(new ThreadFactoryBuilder().setDaemon(true).setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler)new DefaultUncaughtExceptionHandler(LOGGER)).setNameFormat("Downloader %d").build()));

    public static CompletableFuture<?> downloadTo(File file, String string, Map<String, String> map, int n, @Nullable ProgressListener progressListener, Proxy proxy) {
        return CompletableFuture.supplyAsync(() -> {
            // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
            // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [1[TRYBLOCK]], but top level block is 10[WHILELOOP]
            // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:427)
            // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:479)
            // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:619)
            // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:699)
            // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:188)
            // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:133)
            // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:96)
            // org.benf.cfr.reader.entities.Method.analyse(Method.java:397)
            // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:901)
            // org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:797)
            // org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:225)
            // org.benf.cfr.reader.Driver.doJar(Driver.java:109)
            // org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:65)
            // org.benf.cfr.reader.Main.main(Main.java:48)
            throw new IllegalStateException("Decompilation failed");
        }, (Executor)DOWNLOAD_EXECUTOR);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static int getAvailablePort() {
        try {
            try (ServerSocket serverSocket = new ServerSocket(0);){
                int n = serverSocket.getLocalPort();
                return n;
            }
        }
        catch (IOException iOException) {
            return 25564;
        }
    }
}

