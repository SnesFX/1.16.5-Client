/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.util.profiling;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import javax.annotation.Nullable;
import net.minecraft.util.profiling.ActiveProfiler;
import net.minecraft.util.profiling.InactiveProfiler;
import net.minecraft.util.profiling.ProfileCollector;
import net.minecraft.util.profiling.ProfileResults;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SingleTickProfiler {
    private static final Logger LOGGER = LogManager.getLogger();
    private final LongSupplier realTime;
    private final long saveThreshold;
    private int tick;
    private final File location;
    private ProfileCollector profiler;

    public ProfilerFiller startTick() {
        this.profiler = new ActiveProfiler(this.realTime, () -> this.tick, false);
        ++this.tick;
        return this.profiler;
    }

    public void endTick() {
        if (this.profiler == InactiveProfiler.INSTANCE) {
            return;
        }
        ProfileResults profileResults = this.profiler.getResults();
        this.profiler = InactiveProfiler.INSTANCE;
        if (profileResults.getNanoDuration() >= this.saveThreshold) {
            File file = new File(this.location, "tick-results-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + ".txt");
            profileResults.saveResults(file);
            LOGGER.info("Recorded long tick -- wrote info to: {}", (Object)file.getAbsolutePath());
        }
    }

    @Nullable
    public static SingleTickProfiler createTickProfiler(String string) {
        return null;
    }

    public static ProfilerFiller decorateFiller(ProfilerFiller profilerFiller, @Nullable SingleTickProfiler singleTickProfiler) {
        if (singleTickProfiler != null) {
            return ProfilerFiller.tee(singleTickProfiler.startTick(), profilerFiller);
        }
        return profilerFiller;
    }
}

