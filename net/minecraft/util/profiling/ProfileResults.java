/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.util.profiling;

import java.io.File;
import java.util.List;
import net.minecraft.util.profiling.ResultField;

public interface ProfileResults {
    public List<ResultField> getTimes(String var1);

    public boolean saveResults(File var1);

    public long getStartTimeNano();

    public int getStartTimeTicks();

    public long getEndTimeNano();

    public int getEndTimeTicks();

    default public long getNanoDuration() {
        return this.getEndTimeNano() - this.getStartTimeNano();
    }

    default public int getTickDuration() {
        return this.getEndTimeTicks() - this.getStartTimeTicks();
    }

    public static String demanglePath(String string) {
        return string.replace('\u001e', '.');
    }
}

