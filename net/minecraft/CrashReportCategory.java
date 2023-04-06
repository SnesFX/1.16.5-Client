/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportDetail;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;

public class CrashReportCategory {
    private final CrashReport report;
    private final String title;
    private final List<Entry> entries = Lists.newArrayList();
    private StackTraceElement[] stackTrace = new StackTraceElement[0];

    public CrashReportCategory(CrashReport crashReport, String string) {
        this.report = crashReport;
        this.title = string;
    }

    public static String formatLocation(double d, double d2, double d3) {
        return String.format(Locale.ROOT, "%.2f,%.2f,%.2f - %s", d, d2, d3, CrashReportCategory.formatLocation(new BlockPos(d, d2, d3)));
    }

    public static String formatLocation(BlockPos blockPos) {
        return CrashReportCategory.formatLocation(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public static String formatLocation(int n, int n2, int n3) {
        int n4;
        Object object;
        int n5;
        Object object2;
        Object object3;
        int n6;
        Object object4;
        int n7;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            stringBuilder.append(String.format("World: (%d,%d,%d)", n, n2, n3));
        }
        catch (Throwable object5) {
            stringBuilder.append("(Error finding world loc)");
        }
        stringBuilder.append(", ");
        try {
            int n8 = n >> 4;
            n5 = n3 >> 4;
            object4 = n & 0xF;
            n6 = n2 >> 4;
            object2 = n3 & 0xF;
            n7 = n8 << 4;
            object3 = n5 << 4;
            n4 = (n8 + 1 << 4) - 1;
            object = (n5 + 1 << 4) - 1;
            stringBuilder.append(String.format("Chunk: (at %d,%d,%d in %d,%d; contains blocks %d,0,%d to %d,255,%d)", object4, n6, object2, n8, n5, n7, object3, n4, object));
        }
        catch (Throwable throwable) {
            stringBuilder.append("(Error finding chunk loc)");
        }
        stringBuilder.append(", ");
        try {
            object5 = n >> 9;
            n5 = n3 >> 9;
            object4 = object5 << 5;
            n6 = n5 << 5;
            object2 = (object5 + true << 5) - true;
            n7 = (n5 + 1 << 5) - 1;
            object3 = object5 << 9;
            n4 = n5 << 9;
            object = (object5 + true << 9) - true;
            int n9 = (n5 + 1 << 9) - 1;
            stringBuilder.append(String.format("Region: (%d,%d; contains chunks %d,%d to %d,%d, blocks %d,0,%d to %d,255,%d)", (int)object5, n5, object4, n6, object2, n7, object3, n4, object, n9));
        }
        catch (Throwable throwable) {
            stringBuilder.append("(Error finding world loc)");
        }
        return stringBuilder.toString();
    }

    public CrashReportCategory setDetail(String string, CrashReportDetail<String> crashReportDetail) {
        try {
            this.setDetail(string, crashReportDetail.call());
        }
        catch (Throwable throwable) {
            this.setDetailError(string, throwable);
        }
        return this;
    }

    public CrashReportCategory setDetail(String string, Object object) {
        this.entries.add(new Entry(string, object));
        return this;
    }

    public void setDetailError(String string, Throwable throwable) {
        this.setDetail(string, throwable);
    }

    public int fillInStackTrace(int n) {
        StackTraceElement[] arrstackTraceElement = Thread.currentThread().getStackTrace();
        if (arrstackTraceElement.length <= 0) {
            return 0;
        }
        this.stackTrace = new StackTraceElement[arrstackTraceElement.length - 3 - n];
        System.arraycopy(arrstackTraceElement, 3 + n, this.stackTrace, 0, this.stackTrace.length);
        return this.stackTrace.length;
    }

    public boolean validateStackTrace(StackTraceElement stackTraceElement, StackTraceElement stackTraceElement2) {
        if (this.stackTrace.length == 0 || stackTraceElement == null) {
            return false;
        }
        StackTraceElement stackTraceElement3 = this.stackTrace[0];
        if (!(stackTraceElement3.isNativeMethod() == stackTraceElement.isNativeMethod() && stackTraceElement3.getClassName().equals(stackTraceElement.getClassName()) && stackTraceElement3.getFileName().equals(stackTraceElement.getFileName()) && stackTraceElement3.getMethodName().equals(stackTraceElement.getMethodName()))) {
            return false;
        }
        if (stackTraceElement2 != null != this.stackTrace.length > 1) {
            return false;
        }
        if (stackTraceElement2 != null && !this.stackTrace[1].equals(stackTraceElement2)) {
            return false;
        }
        this.stackTrace[0] = stackTraceElement;
        return true;
    }

    public void trimStacktrace(int n) {
        StackTraceElement[] arrstackTraceElement = new StackTraceElement[this.stackTrace.length - n];
        System.arraycopy(this.stackTrace, 0, arrstackTraceElement, 0, arrstackTraceElement.length);
        this.stackTrace = arrstackTraceElement;
    }

    public void getDetails(StringBuilder stringBuilder) {
        stringBuilder.append("-- ").append(this.title).append(" --\n");
        stringBuilder.append("Details:");
        for (Entry entry : this.entries) {
            stringBuilder.append("\n\t");
            stringBuilder.append(entry.getKey());
            stringBuilder.append(": ");
            stringBuilder.append(entry.getValue());
        }
        if (this.stackTrace != null && this.stackTrace.length > 0) {
            stringBuilder.append("\nStacktrace:");
            for (StackTraceElement stackTraceElement : this.stackTrace) {
                stringBuilder.append("\n\tat ");
                stringBuilder.append(stackTraceElement);
            }
        }
    }

    public StackTraceElement[] getStacktrace() {
        return this.stackTrace;
    }

    public static void populateBlockDetails(CrashReportCategory crashReportCategory, BlockPos blockPos, @Nullable BlockState blockState) {
        if (blockState != null) {
            crashReportCategory.setDetail("Block", blockState::toString);
        }
        crashReportCategory.setDetail("Block location", () -> CrashReportCategory.formatLocation(blockPos));
    }

    static class Entry {
        private final String key;
        private final String value;

        public Entry(String string, @Nullable Object object) {
            this.key = string;
            if (object == null) {
                this.value = "~~NULL~~";
            } else if (object instanceof Throwable) {
                Throwable throwable = (Throwable)object;
                this.value = "~~ERROR~~ " + throwable.getClass().getSimpleName() + ": " + throwable.getMessage();
            } else {
                this.value = object.toString();
            }
        }

        public String getKey() {
            return this.key;
        }

        public String getValue() {
            return this.value;
        }
    }

}

