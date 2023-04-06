/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CrashReport {
    private static final Logger LOGGER = LogManager.getLogger();
    private final String title;
    private final Throwable exception;
    private final CrashReportCategory systemDetails = new CrashReportCategory(this, "System Details");
    private final List<CrashReportCategory> details = Lists.newArrayList();
    private File saveFile;
    private boolean trackingStackTrace = true;
    private StackTraceElement[] uncategorizedStackTrace = new StackTraceElement[0];

    public CrashReport(String string, Throwable throwable) {
        this.title = string;
        this.exception = throwable;
        this.initDetails();
    }

    private void initDetails() {
        this.systemDetails.setDetail("Minecraft Version", () -> SharedConstants.getCurrentVersion().getName());
        this.systemDetails.setDetail("Minecraft Version ID", () -> SharedConstants.getCurrentVersion().getId());
        this.systemDetails.setDetail("Operating System", () -> System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version"));
        this.systemDetails.setDetail("Java Version", () -> System.getProperty("java.version") + ", " + System.getProperty("java.vendor"));
        this.systemDetails.setDetail("Java VM Version", () -> System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor"));
        this.systemDetails.setDetail("Memory", () -> {
            Runtime runtime = Runtime.getRuntime();
            long l = runtime.maxMemory();
            long l2 = runtime.totalMemory();
            long l3 = runtime.freeMemory();
            long l4 = l / 1024L / 1024L;
            long l5 = l2 / 1024L / 1024L;
            long l6 = l3 / 1024L / 1024L;
            return l3 + " bytes (" + l6 + " MB) / " + l2 + " bytes (" + l5 + " MB) up to " + l + " bytes (" + l4 + " MB)";
        });
        this.systemDetails.setDetail("CPUs", Runtime.getRuntime().availableProcessors());
        this.systemDetails.setDetail("JVM Flags", () -> {
            List list = Util.getVmArguments().collect(Collectors.toList());
            return String.format("%d total; %s", list.size(), list.stream().collect(Collectors.joining(" ")));
        });
    }

    public String getTitle() {
        return this.title;
    }

    public Throwable getException() {
        return this.exception;
    }

    public void getDetails(StringBuilder stringBuilder) {
        if (!(this.uncategorizedStackTrace != null && this.uncategorizedStackTrace.length > 0 || this.details.isEmpty())) {
            this.uncategorizedStackTrace = (StackTraceElement[])ArrayUtils.subarray((Object[])this.details.get(0).getStacktrace(), (int)0, (int)1);
        }
        if (this.uncategorizedStackTrace != null && this.uncategorizedStackTrace.length > 0) {
            stringBuilder.append("-- Head --\n");
            stringBuilder.append("Thread: ").append(Thread.currentThread().getName()).append("\n");
            stringBuilder.append("Stacktrace:\n");
            for (StackTraceElement stackTraceElement : this.uncategorizedStackTrace) {
                stringBuilder.append("\t").append("at ").append(stackTraceElement);
                stringBuilder.append("\n");
            }
            stringBuilder.append("\n");
        }
        for (CrashReportCategory crashReportCategory : this.details) {
            crashReportCategory.getDetails(stringBuilder);
            stringBuilder.append("\n\n");
        }
        this.systemDetails.getDetails(stringBuilder);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String getExceptionMessage() {
        String string;
        StringWriter stringWriter = null;
        PrintWriter printWriter = null;
        Throwable throwable = this.exception;
        if (throwable.getMessage() == null) {
            if (throwable instanceof NullPointerException) {
                throwable = new NullPointerException(this.title);
            } else if (throwable instanceof StackOverflowError) {
                throwable = new StackOverflowError(this.title);
            } else if (throwable instanceof OutOfMemoryError) {
                throwable = new OutOfMemoryError(this.title);
            }
            throwable.setStackTrace(this.exception.getStackTrace());
        }
        try {
            stringWriter = new StringWriter();
            printWriter = new PrintWriter(stringWriter);
            throwable.printStackTrace(printWriter);
            string = stringWriter.toString();
        }
        catch (Throwable throwable2) {
            IOUtils.closeQuietly((Writer)stringWriter);
            IOUtils.closeQuietly(printWriter);
            throw throwable2;
        }
        IOUtils.closeQuietly((Writer)stringWriter);
        IOUtils.closeQuietly((Writer)printWriter);
        return string;
    }

    public String getFriendlyReport() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("---- Minecraft Crash Report ----\n");
        stringBuilder.append("// ");
        stringBuilder.append(CrashReport.getErrorComment());
        stringBuilder.append("\n\n");
        stringBuilder.append("Time: ");
        stringBuilder.append(new SimpleDateFormat().format(new Date()));
        stringBuilder.append("\n");
        stringBuilder.append("Description: ");
        stringBuilder.append(this.title);
        stringBuilder.append("\n\n");
        stringBuilder.append(this.getExceptionMessage());
        stringBuilder.append("\n\nA detailed walkthrough of the error, its code path and all known details is as follows:\n");
        for (int i = 0; i < 87; ++i) {
            stringBuilder.append("-");
        }
        stringBuilder.append("\n\n");
        this.getDetails(stringBuilder);
        return stringBuilder.toString();
    }

    public File getSaveFile() {
        return this.saveFile;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean saveToFile(File file) {
        boolean bl;
        if (this.saveFile != null) {
            return false;
        }
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }
        OutputStreamWriter outputStreamWriter = null;
        try {
            outputStreamWriter = new OutputStreamWriter((OutputStream)new FileOutputStream(file), StandardCharsets.UTF_8);
            outputStreamWriter.write(this.getFriendlyReport());
            this.saveFile = file;
            bl = true;
        }
        catch (Throwable throwable) {
            boolean bl2;
            try {
                LOGGER.error("Could not save crash report to {}", (Object)file, (Object)throwable);
                bl2 = false;
            }
            catch (Throwable throwable2) {
                IOUtils.closeQuietly(outputStreamWriter);
                throw throwable2;
            }
            IOUtils.closeQuietly((Writer)outputStreamWriter);
            return bl2;
        }
        IOUtils.closeQuietly((Writer)outputStreamWriter);
        return bl;
    }

    public CrashReportCategory getSystemDetails() {
        return this.systemDetails;
    }

    public CrashReportCategory addCategory(String string) {
        return this.addCategory(string, 1);
    }

    public CrashReportCategory addCategory(String string, int n) {
        CrashReportCategory crashReportCategory = new CrashReportCategory(this, string);
        if (this.trackingStackTrace) {
            int n2 = crashReportCategory.fillInStackTrace(n);
            StackTraceElement[] arrstackTraceElement = this.exception.getStackTrace();
            StackTraceElement stackTraceElement = null;
            StackTraceElement stackTraceElement2 = null;
            int n3 = arrstackTraceElement.length - n2;
            if (n3 < 0) {
                System.out.println("Negative index in crash report handler (" + arrstackTraceElement.length + "/" + n2 + ")");
            }
            if (arrstackTraceElement != null && 0 <= n3 && n3 < arrstackTraceElement.length) {
                stackTraceElement = arrstackTraceElement[n3];
                if (arrstackTraceElement.length + 1 - n2 < arrstackTraceElement.length) {
                    stackTraceElement2 = arrstackTraceElement[arrstackTraceElement.length + 1 - n2];
                }
            }
            this.trackingStackTrace = crashReportCategory.validateStackTrace(stackTraceElement, stackTraceElement2);
            if (n2 > 0 && !this.details.isEmpty()) {
                CrashReportCategory crashReportCategory2 = this.details.get(this.details.size() - 1);
                crashReportCategory2.trimStacktrace(n2);
            } else if (arrstackTraceElement != null && arrstackTraceElement.length >= n2 && 0 <= n3 && n3 < arrstackTraceElement.length) {
                this.uncategorizedStackTrace = new StackTraceElement[n3];
                System.arraycopy(arrstackTraceElement, 0, this.uncategorizedStackTrace, 0, this.uncategorizedStackTrace.length);
            } else {
                this.trackingStackTrace = false;
            }
        }
        this.details.add(crashReportCategory);
        return crashReportCategory;
    }

    private static String getErrorComment() {
        String[] arrstring = new String[]{"Who set us up the TNT?", "Everything's going to plan. No, really, that was supposed to happen.", "Uh... Did I do that?", "Oops.", "Why did you do that?", "I feel sad now :(", "My bad.", "I'm sorry, Dave.", "I let you down. Sorry :(", "On the bright side, I bought you a teddy bear!", "Daisy, daisy...", "Oh - I know what I did wrong!", "Hey, that tickles! Hehehe!", "I blame Dinnerbone.", "You should try our sister game, Minceraft!", "Don't be sad. I'll do better next time, I promise!", "Don't be sad, have a hug! <3", "I just don't know what went wrong :(", "Shall we play a game?", "Quite honestly, I wouldn't worry myself about that.", "I bet Cylons wouldn't have this problem.", "Sorry :(", "Surprise! Haha. Well, this is awkward.", "Would you like a cupcake?", "Hi. I'm Minecraft, and I'm a crashaholic.", "Ooh. Shiny.", "This doesn't make any sense!", "Why is it breaking :(", "Don't do that.", "Ouch. That hurt :(", "You're mean.", "This is a token for 1 free hug. Redeem at your nearest Mojangsta: [~~HUG~~]", "There are four lights!", "But it works on my machine."};
        try {
            return arrstring[(int)(Util.getNanos() % (long)arrstring.length)];
        }
        catch (Throwable throwable) {
            return "Witty comment unavailable :(";
        }
    }

    public static CrashReport forThrowable(Throwable throwable, String string) {
        while (throwable instanceof CompletionException && throwable.getCause() != null) {
            throwable = throwable.getCause();
        }
        CrashReport crashReport = throwable instanceof ReportedException ? ((ReportedException)throwable).getReport() : new CrashReport(string, throwable);
        return crashReport;
    }

    public static void preload() {
        new CrashReport("Don't panic!", new Throwable()).getFriendlyReport();
    }
}

